from pydantic import BaseModel, Field, EmailStr
from typing import List, Dict, Any, Optional
from datetime import datetime
from enum import Enum

class RiskLevelEnum(str, Enum):
    SAFE = "SAFE"
    SUSPICIOUS = "SUSPICIOUS"
    HIGH_RISK = "HIGH_RISK"
    CRITICAL = "CRITICAL"

class ScannerSignal(BaseModel):
    name: str
    type: str
    severity: str
    description: str
    evidence_value: Optional[str] = None
    weight: float = 1.0

class SecurityResult(BaseModel):
    scan_id: str
    scanner_type: str
    risk_level: RiskLevelEnum
    risk_score: int = Field(default=10, ge=0, le=100)  # Threat index (0-100)
    security_score: int = Field(default=90, ge=0, le=100)  # Sentinel Security score: higher = safer (0-100)
    threat_probability: float = Field(default=0.10, ge=0.0, le=1.0)
    confidence: float = Field(ge=0.0, le=1.0)
    signals: List[ScannerSignal] = []
    explanation: str
    recommended_actions: List[str] = []
    what_to_avoid: List[str] = []
    limitations: List[str] = []
    model_name: str
    model_version: str
    timestamp: datetime
    # Sentinel AI detailed report extensions
    quick_summary: Optional[str] = None
    why_this_score: List[str] = []
    evidence: List[str] = []
    specialized_report: Optional[Dict[str, Any]] = None
    raw_input_reference: Optional[str] = None

class ContextualAssistantRequest(BaseModel):
    message: str
    language: str = "en"
    structured_context: Optional[Dict[str, Any]] = None

class ContextualAssistantResponse(BaseModel):
    answer: str
    reasoning_summary: str
    evidence_references: List[str] = []
    recommended_actions: List[str] = []
    limitations: List[str] = []
    confidence: float = 0.92
    intent: Optional[str] = None

class ScanCompareRequest(BaseModel):
    compare_with_scan_id: Optional[str] = None

# Auth schemas
class UserRegisterRequest(BaseModel):
    email: EmailStr
    password: str
    full_name: Optional[str] = None
    installation_id: str
    device_name: str

class UserLoginRequest(BaseModel):
    email: EmailStr
    password: str
    installation_id: str
    device_name: Optional[str] = None

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user_id: str
    device_id: str
    email: str

class FirebaseTokenRequest(BaseModel):
    id_token: str
    installation_id: str
    device_name: str

# Device schemas
class DeviceRegisterRequest(BaseModel):
    installation_id: str
    device_name: str
    manufacturer: Optional[str] = None
    brand: Optional[str] = None
    model: Optional[str] = None
    android_version: Optional[str] = None
    sdk_level: Optional[int] = None
    security_patch: Optional[str] = None

class DevicePostureSyncRequest(BaseModel):
    is_screen_lock_enabled: bool
    is_storage_encrypted: bool
    is_developer_options_enabled: bool
    is_adb_enabled: bool
    is_root_detected: bool
    root_signals: List[str] = []
    play_integrity_verdict: str = "MEETS_DEVICE_INTEGRITY"
    battery_level: Optional[int] = None
    storage_used_percent: Optional[float] = None

# Scanner Request schemas
class UrlScanRequest(BaseModel):
    url: str

class MessageScanRequest(BaseModel):
    message_text: str
    sender: Optional[str] = None
    language: Optional[str] = "en"  # en, hi, te

class QrScanRequest(BaseModel):
    raw_payload: str
    image_base64: Optional[str] = None

class PaymentScanRequest(BaseModel):
    screenshot_base64: Optional[str] = None
    ocr_text: Optional[str] = None
    claimed_amount: Optional[float] = None
    claimed_upi_id: Optional[str] = None
    claimed_txn_id: Optional[str] = None

class ApkScanRequest(BaseModel):
    package_name: str
    version_name: Optional[str] = None
    version_code: Optional[int] = None
    target_sdk: Optional[int] = None
    installer_package: Optional[str] = None
    is_sideloaded: bool = False
    permissions: List[str] = []
    sha256: Optional[str] = None

class FileScanRequest(BaseModel):
    file_name: str
    mime_type: Optional[str] = None
    size_bytes: int
    file_hash_sha256: str
    entropy: Optional[float] = None

class NetworkAuditRequest(BaseModel):
    network_type: str  # WIFI, CELLULAR, VPN, NONE
    ssid: Optional[str] = None
    bssid_hash: Optional[str] = None
    dns_servers: List[str] = []
    is_vpn_active: bool = False
    security_type: Optional[str] = "WPA2_PSK"

class PermissionAuditRequest(BaseModel):
    granted_permissions: List[str] = []
    app_permissions_map: Dict[str, List[str]] = {}

class ScamCallRiskRequest(BaseModel):
    phone_number: str
    call_context: Optional[str] = None

class InvestmentScamRequest(BaseModel):
    text: str
    claimed_return_percent: Optional[float] = None
    company_name: Optional[str] = None

class DeepfakeScanRequest(BaseModel):
    media_type: str = "IMAGE"  # IMAGE, VIDEO, AUDIO
    media_hash_sha256: str
    face_count: Optional[int] = 1
    image_base64: Optional[str] = None
    features: Optional[Dict[str, Any]] = None


# Incident schemas
class IncidentCreateRequest(BaseModel):
    title: str
    threat_category: str
    description: Optional[str] = None
    priority: str = "HIGH"
    evidence_type: Optional[str] = None
    evidence_content: Optional[str] = None
    evidence_hash: Optional[str] = None

class IncidentStatusUpdateRequest(BaseModel):
    status: str
    notes: Optional[str] = None

# Assistant schemas
class AssistantChatRequest(BaseModel):
    message: str
    context_type: Optional[str] = None  # SCAN, APP, PERMISSION, GENERAL
    context_data: Optional[Dict[str, Any]] = None
    language: str = "en"

class AssistantChatResponse(BaseModel):
    response: str
    intent: str
    structured_findings: Optional[Dict[str, Any]] = None
    recommended_safe_actions: List[str] = []
    limitations: List[str] = []

# Score and Report schemas
class SecurityScoreResponse(BaseModel):
    overall_score: int
    previous_score: Optional[int] = None
    breakdown: Dict[str, int]
    reasons_for_change: List[str]
    recommendations: List[str]
    calculated_at: datetime
    scoring_version: str

class AlertResponse(BaseModel):
    id: str
    title: str
    description: str
    severity: str
    category: str
    recommended_action: Optional[str] = None
    is_read: bool
    created_at: datetime

class AccountActivityResponse(BaseModel):
    id: str
    user_id: str
    email: Optional[str] = None
    device_id: Optional[str] = None
    device_name: Optional[str] = None
    activity_type: str
    description: str
    severity: str = "INFO"
    ip_address: Optional[str] = None
    timestamp: Optional[Any] = None
    created_at: Optional[Any] = None
    metadata: Optional[Dict[str, Any]] = None
    metadata_json: Optional[Dict[str, Any]] = None

class AccountActivityCreateRequest(BaseModel):
    activity_type: str
    description: str
    severity: Optional[str] = "INFO"
    metadata: Optional[Dict[str, Any]] = None
