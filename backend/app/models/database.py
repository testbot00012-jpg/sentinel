import uuid
from datetime import datetime, timezone
from sqlalchemy import Column, String, Integer, Float, Boolean, DateTime, ForeignKey, Text, JSON
from sqlalchemy.orm import relationship
from app.database.session import Base

def gen_uuid() -> str:
    return str(uuid.uuid4())

def utc_now() -> datetime:
    return datetime.now(timezone.utc)

class User(Base):
    __tablename__ = "users"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    email = Column(String(255), unique=True, index=True, nullable=False)
    firebase_uid = Column(String(128), unique=True, index=True, nullable=True)
    hashed_password = Column(String(255), nullable=True)
    full_name = Column(String(255), nullable=True)
    is_active = Column(Boolean, default=True)
    is_verified = Column(Boolean, default=False)
    created_at = Column(DateTime(timezone=True), default=utc_now)
    updated_at = Column(DateTime(timezone=True), default=utc_now, onupdate=utc_now)
    
    devices = relationship("Device", back_populates="user", cascade="all, delete-orphan")
    scans = relationship("Scan", back_populates="user")
    incidents = relationship("Incident", back_populates="user")
    alerts = relationship("Alert", back_populates="user")
    reports = relationship("Report", back_populates="user")

class Device(Base):
    __tablename__ = "devices"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    user_id = Column(String(36), ForeignKey("users.id"), index=True, nullable=False)
    installation_id = Column(String(128), unique=True, index=True, nullable=False)
    device_name = Column(String(255), nullable=False)
    manufacturer = Column(String(100), nullable=True)
    brand = Column(String(100), nullable=True)
    model = Column(String(100), nullable=True)
    android_version = Column(String(50), nullable=True)
    sdk_level = Column(Integer, nullable=True)
    security_patch = Column(String(50), nullable=True)
    is_trusted = Column(Boolean, default=True)
    is_compromised = Column(Boolean, default=False)
    last_seen_at = Column(DateTime(timezone=True), default=utc_now)
    created_at = Column(DateTime(timezone=True), default=utc_now)
    
    user = relationship("User", back_populates="devices")
    postures = relationship("DevicePosture", back_populates="device", cascade="all, delete-orphan")
    security_scores = relationship("SecurityScore", back_populates="device")
    scans = relationship("Scan", back_populates="device")
    behavior_baselines = relationship("BehaviorBaseline", back_populates="device")

class DevicePosture(Base):
    __tablename__ = "device_postures"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    device_id = Column(String(36), ForeignKey("devices.id"), index=True, nullable=False)
    is_screen_lock_enabled = Column(Boolean, default=True)
    is_storage_encrypted = Column(Boolean, default=True)
    is_developer_options_enabled = Column(Boolean, default=False)
    is_adb_enabled = Column(Boolean, default=False)
    is_root_detected = Column(Boolean, default=False)
    root_signals = Column(JSON, default=list)
    play_integrity_verdict = Column(String(100), default="MEETS_DEVICE_INTEGRITY")
    battery_level = Column(Integer, nullable=True)
    storage_used_percent = Column(Float, nullable=True)
    recorded_at = Column(DateTime(timezone=True), default=utc_now)
    
    device = relationship("Device", back_populates="postures")

class SecurityScore(Base):
    __tablename__ = "security_scores"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    device_id = Column(String(36), ForeignKey("devices.id"), index=True, nullable=False)
    overall_score = Column(Integer, nullable=False)  # 0 to 100
    previous_score = Column(Integer, nullable=True)
    device_score = Column(Integer, nullable=False)
    app_score = Column(Integer, nullable=False)
    permission_score = Column(Integer, nullable=False)
    malware_score = Column(Integer, nullable=False)
    network_score = Column(Integer, nullable=False)
    web_score = Column(Integer, nullable=False)
    account_score = Column(Integer, nullable=False)
    behavior_score = Column(Integer, nullable=False)
    scoring_version = Column(String(20), default="v1.0.0")
    reasons_for_change = Column(JSON, default=list)
    recommendations = Column(JSON, default=list)
    calculated_at = Column(DateTime(timezone=True), default=utc_now)
    
    device = relationship("Device", back_populates="security_scores")

class Scan(Base):
    __tablename__ = "scans"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    user_id = Column(String(36), ForeignKey("users.id"), index=True, nullable=False)
    device_id = Column(String(36), ForeignKey("devices.id"), index=True, nullable=False)
    scan_type = Column(String(50), nullable=False)  # url, sms, qr, payment, apk, malware, file, network
    status = Column(String(30), default="COMPLETED")  # PENDING, ANALYZING, COMPLETED, FAILED
    risk_level = Column(String(30), nullable=False)  # SAFE, SUSPICIOUS, HIGH_RISK, CRITICAL
    risk_score = Column(Integer, nullable=False)  # 0 to 100
    confidence = Column(Float, nullable=False)  # 0.0 to 1.0
    model_name = Column(String(100), nullable=True)
    model_version = Column(String(50), nullable=True)
    summary = Column(Text, nullable=True)
    target_identifier = Column(String(500), nullable=True)  # URL, file hash, phone number, etc.
    created_at = Column(DateTime(timezone=True), default=utc_now)
    
    user = relationship("User", back_populates="scans")
    device = relationship("Device", back_populates="scans")
    results = relationship("ScanResult", back_populates="scan", cascade="all, delete-orphan")

class ScanResult(Base):
    __tablename__ = "scan_results"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    scan_id = Column(String(36), ForeignKey("scans.id"), index=True, nullable=False)
    scanner_type = Column(String(50), nullable=False)
    risk_level = Column(String(30), nullable=False)
    risk_score = Column(Integer, nullable=False)
    confidence = Column(Float, nullable=False)
    signals = Column(JSON, default=list)
    explanation = Column(Text, nullable=True)
    recommended_actions = Column(JSON, default=list)
    limitations = Column(JSON, default=list)
    model_name = Column(String(100), nullable=True)
    model_version = Column(String(50), nullable=True)
    raw_telemetry = Column(JSON, default=dict)
    created_at = Column(DateTime(timezone=True), default=utc_now)
    
    scan = relationship("Scan", back_populates="results")
    scan_signals = relationship("ScanSignal", back_populates="scan_result", cascade="all, delete-orphan")

class ScanSignal(Base):
    __tablename__ = "scan_signals"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    scan_result_id = Column(String(36), ForeignKey("scan_results.id"), index=True, nullable=False)
    signal_name = Column(String(100), nullable=False)
    signal_type = Column(String(50), nullable=False)
    severity = Column(String(30), nullable=False)
    description = Column(Text, nullable=False)
    weight = Column(Float, default=1.0)
    
    scan_result = relationship("ScanResult", back_populates="scan_signals")

class Alert(Base):
    __tablename__ = "alerts"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    user_id = Column(String(36), ForeignKey("users.id"), index=True, nullable=False)
    device_id = Column(String(36), ForeignKey("devices.id"), index=True, nullable=False)
    title = Column(String(255), nullable=False)
    description = Column(Text, nullable=False)
    severity = Column(String(30), nullable=False)  # CRITICAL, HIGH, MEDIUM, LOW
    category = Column(String(50), nullable=False)  # MALWARE, PHISHING, NETWORK, PERMISSION, ACCOUNT
    recommended_action = Column(Text, nullable=True)
    is_read = Column(Boolean, default=False)
    is_dismissed = Column(Boolean, default=False)
    related_scan_id = Column(String(36), nullable=True)
    created_at = Column(DateTime(timezone=True), default=utc_now)
    
    user = relationship("User", back_populates="alerts")

class Incident(Base):
    __tablename__ = "incidents"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    user_id = Column(String(36), ForeignKey("users.id"), index=True, nullable=False)
    device_id = Column(String(36), ForeignKey("devices.id"), index=True, nullable=False)
    title = Column(String(255), nullable=False)
    description = Column(Text, nullable=True)
    threat_category = Column(String(50), nullable=False)
    status = Column(String(30), default="NEW")  # NEW, INVESTIGATING, ACTION_REQUIRED, RESOLVED, DISMISSED
    priority = Column(String(30), default="HIGH")  # CRITICAL, HIGH, MEDIUM, LOW
    resolution_notes = Column(Text, nullable=True)
    created_at = Column(DateTime(timezone=True), default=utc_now)
    updated_at = Column(DateTime(timezone=True), default=utc_now, onupdate=utc_now)
    
    user = relationship("User", back_populates="incidents")
    events = relationship("IncidentEvent", back_populates="incident", cascade="all, delete-orphan")
    evidence = relationship("Evidence", back_populates="incident", cascade="all, delete-orphan")

class IncidentEvent(Base):
    __tablename__ = "incident_events"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    incident_id = Column(String(36), ForeignKey("incidents.id"), index=True, nullable=False)
    event_type = Column(String(50), nullable=False)  # CREATED, STATUS_CHANGED, EVIDENCE_ADDED, RESOLVED
    actor = Column(String(100), default="SYSTEM")
    description = Column(Text, nullable=False)
    metadata_json = Column(JSON, default=dict)
    timestamp = Column(DateTime(timezone=True), default=utc_now)
    
    incident = relationship("Incident", back_populates="events")

class Evidence(Base):
    __tablename__ = "evidence"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    incident_id = Column(String(36), ForeignKey("incidents.id"), index=True, nullable=True)
    user_id = Column(String(36), ForeignKey("users.id"), index=True, nullable=False)
    device_id = Column(String(36), ForeignKey("devices.id"), index=True, nullable=False)
    evidence_type = Column(String(50), nullable=False)  # SCREENSHOT, URL, MESSAGE_TEXT, QR_PAYLOAD, APK_HASH, FILE_HASH
    content_text = Column(Text, nullable=True)
    file_path = Column(String(500), nullable=True)
    file_hash_sha256 = Column(String(64), nullable=False)
    mime_type = Column(String(100), nullable=True)
    size_bytes = Column(Integer, nullable=True)
    is_vault_secured = Column(Boolean, default=False)
    created_at = Column(DateTime(timezone=True), default=utc_now)
    
    incident = relationship("Incident", back_populates="evidence")

class Application(Base):
    __tablename__ = "applications"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    package_name = Column(String(255), unique=True, index=True, nullable=False)
    app_name = Column(String(255), nullable=True)
    category = Column(String(100), default="TOOLS")
    known_safe = Column(Boolean, default=False)
    known_malicious = Column(Boolean, default=False)
    created_at = Column(DateTime(timezone=True), default=utc_now)

class AppRiskSnapshot(Base):
    __tablename__ = "app_risk_snapshots"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    device_id = Column(String(36), ForeignKey("devices.id"), index=True, nullable=False)
    package_name = Column(String(255), index=True, nullable=False)
    version_name = Column(String(100), nullable=True)
    version_code = Column(Integer, nullable=True)
    target_sdk = Column(Integer, nullable=True)
    installer_package = Column(String(255), nullable=True)
    is_sideloaded = Column(Boolean, default=False)
    requested_permissions = Column(JSON, default=list)
    risk_level = Column(String(30), default="SAFE")
    risk_score = Column(Integer, default=10)
    risk_reasons = Column(JSON, default=list)
    analyzed_at = Column(DateTime(timezone=True), default=utc_now)

class PermissionSnapshot(Base):
    __tablename__ = "permission_snapshots"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    device_id = Column(String(36), ForeignKey("devices.id"), index=True, nullable=False)
    permission_name = Column(String(255), index=True, nullable=False)
    grant_state = Column(String(50), default="GRANTED")
    holding_packages = Column(JSON, default=list)
    privacy_risk_weight = Column(Float, default=1.0)
    recorded_at = Column(DateTime(timezone=True), default=utc_now)

class NetworkSnapshot(Base):
    __tablename__ = "network_snapshots"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    device_id = Column(String(36), ForeignKey("devices.id"), index=True, nullable=False)
    network_type = Column(String(50), nullable=False)  # WIFI, CELLULAR, VPN, NONE
    ssid = Column(String(100), nullable=True)
    bssid_hash = Column(String(64), nullable=True)
    is_captive_portal = Column(Boolean, default=False)
    is_vpn_active = Column(Boolean, default=False)
    dns_servers = Column(JSON, default=list)
    security_type = Column(String(50), default="WPA2_PSK")
    risk_level = Column(String(30), default="SAFE")
    captured_at = Column(DateTime(timezone=True), default=utc_now)

class BehaviorBaseline(Base):
    __tablename__ = "behavior_baselines"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    device_id = Column(String(36), ForeignKey("devices.id"), index=True, nullable=False)
    typical_network_types = Column(JSON, default=list)
    typical_app_count = Column(Integer, default=50)
    typical_permission_profile = Column(JSON, default=dict)
    average_risk_score = Column(Float, default=15.0)
    baseline_events_count = Column(Integer, default=0)
    baseline_age_days = Column(Float, default=1.0)
    model_state = Column(JSON, default=dict)
    last_updated_at = Column(DateTime(timezone=True), default=utc_now)
    
    device = relationship("Device", back_populates="behavior_baselines")

class BehaviorEvent(Base):
    __tablename__ = "behavior_events"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    device_id = Column(String(36), ForeignKey("devices.id"), index=True, nullable=False)
    event_type = Column(String(100), nullable=False)
    feature_vector = Column(JSON, default=dict)
    anomaly_score = Column(Float, default=0.0)
    is_anomaly = Column(Boolean, default=False)
    deviation_features = Column(JSON, default=list)
    timestamp = Column(DateTime(timezone=True), default=utc_now)

class RiskEvent(Base):
    __tablename__ = "risk_events"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    user_id = Column(String(36), index=True, nullable=False)
    device_id = Column(String(36), index=True, nullable=False)
    event_type = Column(String(100), nullable=False)
    risk_level = Column(String(30), nullable=False)
    risk_score = Column(Integer, nullable=False)
    confidence = Column(Float, nullable=False)
    source = Column(String(100), nullable=False)
    metadata_json = Column(JSON, default=dict)
    timestamp = Column(DateTime(timezone=True), default=utc_now)

class Report(Base):
    __tablename__ = "reports"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    user_id = Column(String(36), ForeignKey("users.id"), index=True, nullable=False)
    device_id = Column(String(36), ForeignKey("devices.id"), index=True, nullable=False)
    report_type = Column(String(50), nullable=False)  # DAILY, WEEKLY, THREAT, PRIVACY, NETWORK, APPS, DEVICE
    period_start = Column(DateTime(timezone=True), nullable=False)
    period_end = Column(DateTime(timezone=True), nullable=False)
    total_scans = Column(Integer, default=0)
    threats_detected = Column(Integer, default=0)
    average_score = Column(Float, default=100.0)
    summary_text = Column(Text, nullable=False)
    report_data = Column(JSON, default=dict)
    generated_at = Column(DateTime(timezone=True), default=utc_now)
    
    user = relationship("User", back_populates="reports")

class ModelRegistry(Base):
    __tablename__ = "model_registry"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    model_name = Column(String(100), unique=True, index=True, nullable=False)
    version = Column(String(50), nullable=False)
    algorithm = Column(String(100), nullable=False)
    dataset_versions = Column(JSON, default=list)
    training_date = Column(DateTime(timezone=True), default=utc_now)
    metrics = Column(JSON, default=dict)  # accuracy, precision, recall, f1, pr_auc, roc_auc
    decision_threshold = Column(Float, default=0.5)
    artifact_checksum = Column(String(64), nullable=False)
    deployment_status = Column(String(30), default="ACTIVE")  # ACTIVE, STAGED, DEPRECATED

class ModelPrediction(Base):
    __tablename__ = "model_predictions"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    model_name = Column(String(100), index=True, nullable=False)
    model_version = Column(String(50), nullable=False)
    input_type = Column(String(50), nullable=False)
    risk_level = Column(String(30), nullable=False)
    risk_score = Column(Integer, nullable=False)
    confidence = Column(Float, nullable=False)
    inference_latency_ms = Column(Float, nullable=True)
    timestamp = Column(DateTime(timezone=True), default=utc_now)

class AssistantSession(Base):
    __tablename__ = "assistant_sessions"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    user_id = Column(String(36), ForeignKey("users.id"), index=True, nullable=False)
    device_id = Column(String(36), nullable=False)
    conversation_history = Column(JSON, default=list)
    last_interaction = Column(DateTime(timezone=True), default=utc_now)

class NotificationPreference(Base):
    __tablename__ = "notification_preferences"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    user_id = Column(String(36), ForeignKey("users.id"), unique=True, index=True, nullable=False)
    notify_critical = Column(Boolean, default=True)
    notify_high = Column(Boolean, default=True)
    notify_medium = Column(Boolean, default=False)
    notify_daily_summary = Column(Boolean, default=True)
    language = Column(String(10), default="en")  # en, hi, te

class AccountActivity(Base):
    __tablename__ = "account_activities"
    
    id = Column(String(36), primary_key=True, default=gen_uuid)
    user_id = Column(String(36), ForeignKey("users.id"), index=True, nullable=False)
    email = Column(String(255), index=True, nullable=True)
    device_id = Column(String(36), nullable=True)
    device_name = Column(String(255), nullable=True)
    activity_type = Column(String(50), nullable=False)
    description = Column(Text, nullable=False)
    ip_address = Column(String(50), nullable=True)
    severity = Column(String(30), default="INFO")
    metadata_json = Column(JSON, default=dict)
    created_at = Column(DateTime(timezone=True), default=utc_now)

