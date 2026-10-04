import os
import logging
import uuid
from datetime import datetime, timezone
from typing import Optional, Dict, Any, List
from supabase import create_client, Client
from app.config.settings import settings

logger = logging.getLogger(__name__)

class SupabaseService:
    def __init__(self):
        self.client: Optional[Client] = None
        self._init_client()
        # In-memory storage cache for local fallback or testing
        self._local_users: Dict[str, Dict[str, Any]] = {}
        self._local_activities: List[Dict[str, Any]] = []

    def _init_client(self):
        url = settings.SUPABASE_URL
        if url:
            url = url.rstrip("/")
            if url.endswith("/rest/v1"):
                url = url[:-len("/rest/v1")].rstrip("/")
        key = settings.RESOLVED_SUPABASE_SERVICE_ROLE_KEY or settings.SUPABASE_KEY
        if url and key and not url.startswith("http://placeholder") and "your-project-ref" not in url:
            try:
                self.client = create_client(url, key)
                logger.info(f"Supabase client initialized successfully with {url}")
            except Exception as e:
                logger.warning(f"Failed to initialize live Supabase client: {e}. Fallback enabled.")
                self.client = None
        else:
            logger.info("Supabase credentials not configured in environment. Local fallback active.")
            self.client = None

    @property
    def is_live(self) -> bool:
        return self.client is not None

    async def register_user(self, email: str, password: str, metadata: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        """
        Stores user login credentials in Supabase Auth.
        """
        if self.client:
            try:
                res = self.client.auth.sign_up({
                    "email": email,
                    "password": password,
                    "options": {
                        "data": metadata or {}
                    }
                })
                user = res.user
                return {
                    "status": "SUCCESS",
                    "source": "SUPABASE_AUTH",
                    "supabase_uid": user.id if user else str(uuid.uuid4()),
                    "email": email,
                    "created_at": datetime.now(timezone.utc).isoformat()
                }
            except Exception as e:
                logger.error(f"Supabase auth registration error: {e}")
                # If error is e.g. user already exists, raise or report
                raise ValueError(f"Supabase Registration Failed: {str(e)}")
        else:
            # Local fallback for tests/development
            if email in self._local_users:
                raise ValueError("User already registered in local cache")
            uid = str(uuid.uuid4())
            self._local_users[email] = {
                "id": uid,
                "email": email,
                "password": password,
                "metadata": metadata or {},
                "created_at": datetime.now(timezone.utc).isoformat()
            }
            return {
                "status": "SUCCESS",
                "source": "SUPABASE_FALLBACK",
                "supabase_uid": uid,
                "email": email,
                "created_at": datetime.now(timezone.utc).isoformat()
            }

    async def validate_login_credentials(self, email: str, password: str) -> Dict[str, Any]:
        """
        Validates user login credentials against Supabase Auth.
        """
        if self.client:
            try:
                res = self.client.auth.sign_in_with_password({
                    "email": email,
                    "password": password
                })
                user = res.user
                session = res.session
                return {
                    "is_valid": True,
                    "source": "SUPABASE_AUTH",
                    "supabase_uid": user.id if user else str(uuid.uuid4()),
                    "email": user.email if user else email,
                    "access_token": session.access_token if session else None
                }
            except Exception as e:
                logger.warning(f"Supabase invalid login credentials for {email}: {e}")
                return {
                    "is_valid": False,
                    "source": "SUPABASE_AUTH",
                    "error": str(e)
                }
        else:
            # Local fallback validation
            user = self._local_users.get(email)
            if user and user["password"] == password:
                return {
                    "is_valid": True,
                    "source": "SUPABASE_FALLBACK",
                    "supabase_uid": user["id"],
                    "email": email,
                    "access_token": None
                }
            return {
                "is_valid": False,
                "source": "SUPABASE_FALLBACK",
                "error": "Invalid email or password"
            }

    async def log_account_activity(
        self,
        user_id: str,
        email: str,
        activity_type: str,
        description: str,
        device_id: Optional[str] = None,
        device_name: Optional[str] = None,
        severity: str = "INFO",
        ip_address: Optional[str] = None,
        metadata: Optional[Dict[str, Any]] = None
    ) -> Dict[str, Any]:
        """
        Stores recent account activity in Supabase table 'account_activities'.
        """
        now_iso = datetime.now(timezone.utc).isoformat()
        activity_record = {
            "id": str(uuid.uuid4()),
            "user_id": user_id,
            "email": email,
            "device_id": device_id,
            "device_name": device_name or "Android Client",
            "activity_type": activity_type,
            "description": description,
            "severity": severity,
            "ip_address": ip_address or "127.0.0.1",
            "metadata": metadata or {},
            "timestamp": now_iso
        }

        if self.client:
            try:
                res = self.client.table("account_activities").insert(activity_record).execute()
                return {"status": "STORED", "source": "SUPABASE_REST", "data": activity_record}
            except Exception as e:
                logger.error(f"Error storing activity to Supabase: {e}")
                self._local_activities.insert(0, activity_record)
                return {"status": "STORED_LOCALLY", "source": "LOCAL_FALLBACK", "error": str(e)}
        else:
            self._local_activities.insert(0, activity_record)
            return {"status": "STORED", "source": "SUPABASE_LOCAL", "data": activity_record}

    async def get_recent_activities(self, user_id: str, limit: int = 50) -> List[Dict[str, Any]]:
        """
        Retrieves recent activity history for a particular account from Supabase.
        """
        if self.client:
            try:
                res = self.client.table("account_activities") \
                    .select("*") \
                    .eq("user_id", user_id) \
                    .order("timestamp", desc=True) \
                    .limit(limit) \
                    .execute()
                data = res.data or []
                for row in data:
                    if "timestamp" in row and "created_at" not in row:
                        row["created_at"] = row["timestamp"]
                    if "metadata" in row and "metadata_json" not in row:
                        row["metadata_json"] = row["metadata"]
                return data
            except Exception as e:
                logger.error(f"Error querying account activities from Supabase: {e}")
                return [a for a in self._local_activities if a["user_id"] == user_id][:limit]
        else:
            return [a for a in self._local_activities if a["user_id"] == user_id][:limit]

supabase_service = SupabaseService()
