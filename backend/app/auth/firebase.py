import logging
from typing import Optional, Dict, Any
import firebase_admin
from firebase_admin import auth as firebase_auth, credentials
from app.config.settings import settings

logger = logging.getLogger(__name__)

_firebase_initialized = False

def init_firebase():
    global _firebase_initialized
    if _firebase_initialized:
        return
    try:
        if settings.FIREBASE_CREDENTIALS_PATH and os.path.exists(settings.FIREBASE_CREDENTIALS_PATH):
            cred = credentials.Certificate(settings.FIREBASE_CREDENTIALS_PATH)
            firebase_admin.initialize_app(cred, {"projectId": settings.FIREBASE_PROJECT_ID})
            logger.info("Firebase Admin SDK initialized from service account")
            _firebase_initialized = True
        else:
            # Default app initialization or mock verification mode
            try:
                firebase_admin.initialize_app(options={"projectId": settings.FIREBASE_PROJECT_ID})
                _firebase_initialized = True
                logger.info("Firebase Admin SDK initialized with default options")
            except Exception as e:
                logger.warning(f"Firebase credentials not loaded, fallback token verification enabled: {e}")
    except Exception as e:
        logger.warning(f"Firebase initialization warning: {e}")

async def verify_firebase_id_token(id_token: str) -> Optional[Dict[str, Any]]:
    init_firebase()
    try:
        decoded_token = firebase_auth.verify_id_token(id_token)
        return decoded_token
    except Exception as e:
        logger.error(f"Error verifying Firebase ID token: {e}")
        # If in dev environment and token starts with mock_ or test_, allow for test suites
        if settings.ENV == "development" and (id_token.startswith("mock_") or id_token.startswith("test_")):
            return {
                "uid": f"user_{id_token[:10]}",
                "email": f"{id_token[:10]}@cybershield.local",
                "email_verified": True
            }
        return None
