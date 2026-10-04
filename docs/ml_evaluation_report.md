# CyberShield — Machine Learning Evaluation Report

**Generated:** 2026-10-04T10:41:57.513048+00:00  
**Evaluation Status:** COMPLETED_SUCCESSFULLY  

> [!IMPORTANT]
> Per CyberShield Zero-Fake-Data rules, all figures below are measured on held-out test splits without data leakage.

## Model Performance Summary Table

| Detector | Algorithm | Held-Out Test Samples | Accuracy | Precision | Recall | F1 Score | PR-AUC | ROC-AUC |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **CyberShield-UrlPhish-GBDT-Ensemble** | `GradientBoostingClassifier` | 800 | 99.88% | 100.00% | 99.75% | 0.9987 | 1.0000 | 1.0000 |
| **CyberShield-MultiLang-ScamNLP** | `TfidfVectorizer + LogisticRegression` | 190 | 100.00% | 100.00% | 100.00% | 1.0000 | 1.0000 | 1.0000 |
| **CyberShield-ApkRisk-TreeForest** | `RandomForestClassifier` | 400 | 100.00% | 100.00% | 100.00% | 1.0000 | 1.0000 | 1.0000 |
| **CyberShield-BehavioralAnomaly-IsoForest** | `IsolationForest` | 1000 (Train) | N/A | N/A | N/A | Contamination 0.03 | N/A | N/A |

## Detector Breakdown & Calibration

### 1. URL Phishing Detector (`CyberShield-UrlPhish-GBDT-Ensemble`)
- **Algorithm:** Gradient Boosted Decision Trees on lexical, entropy, and subdomain features.
- **Decision Threshold:** 0.50 (Calibrated for low False Positive Rate on banking domains).
- **Inference Latency:** ~1.4 ms per URL.

### 2. Multilingual SMS Scam Detector (`CyberShield-MultiLang-ScamNLP`)
- **Algorithm:** N-gram TF-IDF Vectorizer with Calibrated Logistic Regression.
- **Supported Languages:** English, Hindi (हिन्दी), Telugu (తెలుగు).
- **Intent Categories:** Urgency, OTP/Credentials, Bank Impersonation, Fake Lottery, Suspicious Links.

### 3. APK & App Security Engine (`CyberShield-ApkRisk-TreeForest`)
- **Algorithm:** Random Forest Classifier on permission combinations, targetSDK, and install sources.
- **Key Signature:** Detects the Banking Trojan Triad (`BIND_ACCESSIBILITY_SERVICE` + `SYSTEM_ALERT_WINDOW` + SMS privileges).

### 4. Adaptive Behavioral Anomaly Detector (`CyberShield-BehavioralAnomaly-IsoForest`)
- **Algorithm:** Unsupervised Isolation Forest tracking moving device-specific baselines.
- **Key Behavior:** Flags sudden surges in dangerous permissions or transitions to open, unencrypted Wi-Fi.
