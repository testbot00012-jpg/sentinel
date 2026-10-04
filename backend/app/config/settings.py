from pydantic_settings import BaseSettings, SettingsConfigDict
from typing import Optional
import os

class Settings(BaseSettings):
    PROJECT_NAME: str = "Sentinel AI Security Platform"
    VERSION: str = "1.0.0"
    API_V1_STR: str = "/api/v1"
    
    # Environment & Database
    ENV: str = "development"
    DATABASE_URL: str = os.getenv("DATABASE_URL", "sqlite+aiosqlite:///./cybershield.db")
    REDIS_URL: str = os.getenv("REDIS_URL", "redis://localhost:6379/0")
    
    # Security & Tokens
    SECRET_KEY: str = os.getenv("SECRET_KEY", "cybershield-super-secret-production-grade-key-2026")
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 * 7  # 7 days
    
    # Firebase
    FIREBASE_CREDENTIALS_PATH: Optional[str] = os.getenv("FIREBASE_CREDENTIALS_PATH", None)
    FIREBASE_PROJECT_ID: str = os.getenv("FIREBASE_PROJECT_ID", "cybershield-security")

    # Supabase (Credentials validation and account activity storage)
    SUPABASE_URL: Optional[str] = os.getenv("SUPABASE_URL", None)
    SUPABASE_KEY: Optional[str] = os.getenv("SUPABASE_KEY", None)
    SUPABASE_SERVICE_ROLE_KEY: Optional[str] = os.getenv("SUPABASE_SERVICE_ROLE_KEY", None)

    @property
    def RESOLVED_SUPABASE_SERVICE_ROLE_KEY(self) -> Optional[str]:
        if not self.SUPABASE_SERVICE_ROLE_KEY:
            return None
        k = self.SUPABASE_SERVICE_ROLE_KEY.strip()
        if k.startswith("c2Jfc2VjcmV0"):
            import base64
            try:
                return base64.b64decode(k).decode()
            except Exception:
                return k
        return k
    
    # Storage
    STORAGE_DIR: str = os.getenv("STORAGE_DIR", "./storage")
    EVIDENCE_DIR: str = os.getenv("EVIDENCE_DIR", "./storage/evidence")
    MAX_UPLOAD_SIZE_BYTES: int = 50 * 1024 * 1024  # 50MB
    
    # Rate Limiting
    RATE_LIMIT_DEFAULT: str = "100/minute"
    RATE_LIMIT_SCAN: str = "30/minute"
    
    # ML Models directory
    MODELS_DIR: str = os.getenv("MODELS_DIR", "../ml/saved_models")

    model_config = SettingsConfigDict(env_file=".env", extra="allow")

settings = Settings()
