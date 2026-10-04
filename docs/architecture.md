# CyberShield — System Architecture Documentation

CyberShield is an enterprise-grade, modular Android cybersecurity and personal fraud-protection platform powered by specialized machine learning detectors, real device hardware telemetry, adaptive behavioral baselining, and a RAG-backed security assistant.

---

## 1. High-Level System Architecture

```
                    CYBERSHIELD
                         │
                ┌────────▼────────┐
                │ Security Engine │
                └────────┬────────┘
                         │
 ┌───────────┬───────────┼───────────┬─────────────┐
 ▼           ▼           ▼           ▼             ▼
SMS AI     URL AI     Malware AI   Deepfake AI   Payment AI
 │           │           │           │             │
NLP       URL/ML      Static ML    Vision DL     Vision/OCR
 │           │           │           │             │
 └───────────┴───────────┼───────────┴─────────────┘
                         ▼
              Behavioral Pattern AI
                         │
               Isolation Forest /
                 Autoencoder
                         │
                         ▼
                Threat Correlation
                         │
                         ▼
                 Unified Risk Score
                         │
                         ▼
                  Explainability
                         │
                         ▼
                  CyberShield LLM
                         │
                         ▼
             Explanation + Safe Action
```

---

## 2. Android Clean Modular Architecture

The Android application is organized into layered modules:

- **Presentation Layer (Jetpack Compose & Material 3):**
  - Navigation Compose graph with 174 registered screens across 19 protection modules.
  - Reactive `StateFlow` bindings to `MainSecurityViewModel`.
  - MVI/MVVM design pattern with explicit UI states: `Loading`, `Success`, `Error`, `PermissionRequired`, `Offline`.
- **Domain Layer:**
  - Standard `SecurityResult` model: `scanId`, `scannerType`, `riskLevel`, `riskScore`, `confidence`, `signals`, `explanation`, `recommendedActions`, `limitations`, `modelName`, `modelVersion`.
- **Data & Security Layer:**
  - `DeviceRepository`: Collects live hardware parameters directly from Android APIs (`Build`, `BatteryManager`, `StatFs`, `KeyguardManager`, `DevicePolicyManager`, `PackageManager`, `ConnectivityManager`).
  - `PermissionManager`: Manages runtime permissions transparently with contextual rationale dialogs.
  - `CyberShieldApiClient`: Asynchronous networking client communicating with the FastAPI backend, featuring automated on-device heuristic fallback when offline.

---

## 3. Backend Architecture (FastAPI & SQLAlchemy)

- **Framework:** FastAPI with Python 3.13.
- **Database:** SQLAlchemy 2.0 async engine supporting PostgreSQL in production and async SQLite for local/offline testing.
- **Identity & Sessions:**
  - Privacy-preserving device installation identifiers (`installation_id`).
  - Multi-device hierarchy (`User` -> `Device A`, `Device B`, `Device C`).
  - Firebase Authentication + JWT Bearer access token validation.
- **Central Risk Fusion & Threat Correlation:**
  - Weighted versioned scoring formula across 7 subsystems: Device Posture (20%), Malware (20%), App Security (15%), Permissions/Privacy (15%), Network Posture (10%), Web/Phishing (10%), and Account Security (10%).
  - Multi-vector threat correlation amplifies confidence when concurrent suspicious channels (e.g. urgent SMS + malicious URL + unknown APK) are detected.
- **Adaptive Behavioral Baseline:**
  - Device-isolated anomaly detection using unsupervised Isolation Forest.
  - Learns normal network types, application set stability, and permission counts without mixing baselines between unrelated users.
- **RAG-Backed Security Assistant:**
  - Faithful, non-hallucinating cybersecurity assistant.
  - Consumes structured scanner outputs and retrieves verified mitigation procedures from the curated security knowledge base.
