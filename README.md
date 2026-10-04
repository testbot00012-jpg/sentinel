# SENTINEL AI — AI-Powered Personal Cybersecurity & Fraud Protection Platform

SENTINEL AI is a modular, production-oriented cybersecurity and fraud-protection platform for Android devices. It unites specialized machine learning detection engines, real Android hardware and operating system telemetry, adaptive behavioral baselining, calibrated multi-vector risk fusion, and an explainable RAG security assistant embedded directly inside each individual scan report.

---

## 1. Project Organization

```
Ddd/
├── CyberShieldAndroid_AllScreens/    # Android Studio Kotlin + Jetpack Compose app (Label: SENTINEL AI)
│   ├── app/src/main/java/com/cybershield/app/
│   │   ├── core/model/              # SecurityResult, RiskLevel, SentinelRiskColors, Telemetry
│   │   ├── core/security/           # DeviceRepository (Zero fake data, real Android APIs)
│   │   ├── core/network/            # CyberShieldApiClient (FastAPI HTTP + async fallbacks)
│   │   ├── core/permission/         # PermissionManager (Runtime flows & transparency)
│   │   ├── model/ScreenRegistry.kt  # 174 Registered screen destinations across 19 modules
│   │   ├── ui/CyberShieldApp.kt     # Jetpack Compose UI (14-Section Scan Report + Inline Chat)
│   │   └── ui/viewmodel/            # MainSecurityViewModel (StateFlow & scan chat threads)
│   └── build.gradle.kts             # Gradle build configuration (JVM 17, Compose BOM)
│
├── backend/                         # FastAPI Python Backend
│   ├── app/
│   │   ├── main.py                  # Lifespan startup, schema migration, CORS
│   │   ├── config/settings.py       # Pydantic Settings configuration
│   │   ├── database/session.py      # Async SQLAlchemy session (Postgres/SQLite)
│   │   ├── models/database.py       # 22 Normalized relational database models
│   │   ├── schemas/cyber.py         # Standard SecurityResult & request/response schemas
│   │   ├── auth/                    # Native bcrypt password hashing & JWT bearer
│   │   ├── scanners/                # URL, SMS NLP, QR/UPI, Payment, APK, File, Network
│   │   ├── risk_engine/             # Adaptive Behavioral Baseline & Risk Fusion Engine
│   │   ├── assistant/               # RAG Cybersecurity Assistant & Explainability
│   │   └── api/v1/endpoints.py      # Full REST API router
│   ├── tests/test_backend.py        # Comprehensive async test suite (8 tests)
│   ├── Dockerfile                   # Production container definition
│   └── requirements.txt             # Python backend dependencies
│
├── ml/                              # Machine Learning & Detection Engines
│   ├── registry/dataset_registry.py # Provenance, licenses, sample counts, and hashes
│   ├── saved_models/                # Serialized model checkpoints (.pkl)
│   ├── training/train_all_models.py # End-to-end multi-detector training pipeline
│   ├── evaluation/                  # Metrics calculation and report generator
│   └── tests/test_ml.py             # Inference and threshold unit tests
│
├── docs/                            # Architectural & Engineering Specifications
│   ├── architecture.md              # System architecture and data flow diagrams
│   ├── api.md                       # Complete REST API reference
│   ├── ml_evaluation_report.md      # Accuracy, Precision, Recall, F1, PR-AUC, Latency
│   ├── security_and_privacy.md      # Zero fake data, privacy & Android restrictions matrix
│   ├── datasets.md                  # Dataset registry and quality assurance pipelines
│   ├── firebase_setup.md            # Firebase Authentication setup instructions
│   └── deployment.md                # Docker Compose and production instructions
│
└── docker-compose.yml               # Multi-container orchestration (Backend + Postgres + Redis)
```

---

## 2. Key Architecture Pillars

### 1. Zero Fake Data Rule
- Device hardware data (battery %, storage usage, screen lock, encryption status, developer options, root signals, Wi-Fi status, package counts) is queried directly from actual Android platform APIs on the host device.
- Subsystem scores derive strictly from the versioned scoring formula (`RiskFusionEngine`).

### 2. Standard Scanner Interface
Every scanner returns a standardized `SecurityResult`:
```kotlin
data class SecurityResult(
    val scanId: String,
    val scannerType: String,
    val riskLevel: RiskLevel, // SAFE, SUSPICIOUS, HIGH_RISK, CRITICAL
    val riskScore: Int,       // 0 to 100
    val confidence: Double,   // 0.0 to 1.0
    val signals: List<ScannerSignal>,
    val explanation: String,
    val recommendedActions: List<String>,
    val limitations: List<String>,
    val modelName: String,
    val modelVersion: String,
    val timestamp: Long
)
```

### 3. Specialized Detectors
- **URL / Phishing Engine:** Lexical analysis, Shannon entropy, homograph/punycode checks, direct IP detection, and GBDT ensemble.
- **SMS & Message Scam NLP:** Multilingual semantic intent analyzer supporting **English**, **Hindi (हिन्दी)**, and **Telugu (తెలుగు)** for urgency, OTP demands, lottery scams, and banking impersonation.
- **QR & Payment Fraud Engine:** UPI URI parsing (`upi://pay`), debit transfer risk warnings, and URL redirection validation.
- **Payment Screenshot Tampering:** OCR reference extraction, 12-digit UTR checksum validation, and fake payment simulator watermark detection.
- **APK & Application Security:** Manifest privilege auditor identifying Banking Trojan Triads (`BIND_ACCESSIBILITY_SERVICE` + `SYSTEM_ALERT_WINDOW` + SMS permissions), target SDK deprecation, and sideload origin.
- **File & Storage Security:** Cryptographic SHA-256 IOC matching, double extension trick detection (`.pdf.apk`), and file entropy scanning.
- **Network Security Guard:** Wi-Fi encryption assessment (Open/WEP vs WPA2/WPA3), rogue DNS detection, and VPN tunnel validation.

### 4. Adaptive Behavioral Baseline & Threat Correlation
- Maintains a local/device-specific baseline using an unsupervised Isolation Forest.
- Fuses disparate detector findings (e.g. suspicious SMS + malicious URL + unknown APK) into a unified cyber risk index.

### 5. RAG-Backed AI Security Assistant
- Explains real scanner findings and telemetry without hallucinating threats.
- Pulls from a curated cybersecurity knowledge base for safe action procedures.

---

## 3. Quick Start & Execution Commands

### A. Run Backend Tests
```bash
# In project root:
$env:PYTHONPATH = "backend"
python -m pytest backend/tests/test_backend.py -v
```
*Result: 8 passed tests covering Auth, Devices, Posture, URL Phishing, Multilingual SMS, QR Fraud, APK/File Security, Assistant, and Incidents.*

### B. Run Machine Learning Training & Evaluation
```bash
# Train all specialized models and save checkpoints:
python ml/training/train_all_models.py

# Run ML inference and threshold tests:
python -m pytest ml/tests/test_ml.py -v

# Generate Markdown evaluation report:
python ml/evaluation/evaluate_models.py
```

### C. Run FastAPI Backend Locally
```bash
# In backend/ directory:
$env:PYTHONPATH = "backend"
python -m uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```
Swagger UI available at: `http://localhost:8000/docs`

### D. Compile Android Application
```bash
# In CyberShieldAndroid_AllScreens/ directory:
$env:ANDROID_HOME = "C:\Users\ganes_pof59a1\AppData\Local\Android\Sdk"
.\gradlew.bat compileDebugSources --console=plain
```
*Result: BUILD SUCCESSFUL (Kotlin 2.0.21, AGP 8.7.3, JVM 17).*

### E. Run Full Stack via Docker Compose
```bash
docker compose up -d --build
```
Provisions FastAPI Backend on port 8000, PostgreSQL 16 on port 5432, and Redis 7 on port 6379.
