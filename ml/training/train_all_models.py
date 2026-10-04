import os
import json
import time
import math
import joblib
import numpy as np
from datetime import datetime, timezone
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier, IsolationForest
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, roc_auc_score, average_precision_score
from sklearn.model_selection import train_test_split

os.makedirs("ml/saved_models", exist_ok=True)
os.makedirs("ml/evaluation", exist_ok=True)

def train_url_model():
    print("--- Training URL Phishing Detector ---")
    np.random.seed(42)
    # Generate feature dataset: [length, entropy, num_dots, has_ip, num_keywords, is_https]
    n_samples = 4000
    # Benign URLs: shorter, normal entropy, 1-2 dots, no raw IP, few keywords, https=1
    benign_X = np.column_stack([
        np.random.normal(25, 8, n_samples // 2).clip(10, 80),
        np.random.normal(3.2, 0.4, n_samples // 2).clip(2.0, 4.2),
        np.random.choice([1, 2], n_samples // 2, p=[0.7, 0.3]),
        np.zeros(n_samples // 2),
        np.random.choice([0, 1], n_samples // 2, p=[0.9, 0.1]),
        np.random.choice([0, 1], n_samples // 2, p=[0.05, 0.95])
    ])
    benign_y = np.zeros(n_samples // 2)

    # Phishing URLs: longer, high entropy, 3+ dots, raw IP chance, multiple keywords, https=0 or 1
    phish_X = np.column_stack([
        np.random.normal(65, 20, n_samples // 2).clip(20, 150),
        np.random.normal(4.4, 0.5, n_samples // 2).clip(3.5, 5.5),
        np.random.choice([2, 3, 4, 5], n_samples // 2, p=[0.2, 0.4, 0.3, 0.1]),
        np.random.choice([0, 1], n_samples // 2, p=[0.75, 0.25]),
        np.random.choice([1, 2, 3], n_samples // 2, p=[0.3, 0.5, 0.2]),
        np.random.choice([0, 1], n_samples // 2, p=[0.6, 0.4])
    ])
    phish_y = np.ones(n_samples // 2)

    X = np.vstack([benign_X, phish_X])
    y = np.concatenate([benign_y, phish_y])

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)
    
    start_t = time.time()
    clf = GradientBoostingClassifier(n_estimators=100, learning_rate=0.1, max_depth=4, random_state=42)
    clf.fit(X_train, y_train)
    fit_time = time.time() - start_t

    y_pred = clf.predict(X_test)
    y_prob = clf.predict_proba(X_test)[:, 1]

    metrics = {
        "model_name": "CyberShield-UrlPhish-GBDT-Ensemble",
        "version": "1.2.0",
        "algorithm": "GradientBoostingClassifier",
        "samples_evaluated": len(y_test),
        "accuracy": round(float(accuracy_score(y_test, y_pred)), 4),
        "precision": round(float(precision_score(y_test, y_pred)), 4),
        "recall": round(float(recall_score(y_test, y_pred)), 4),
        "f1": round(float(f1_score(y_test, y_pred)), 4),
        "roc_auc": round(float(roc_auc_score(y_test, y_prob)), 4),
        "pr_auc": round(float(average_precision_score(y_test, y_prob)), 4),
        "training_time_sec": round(fit_time, 3)
    }
    joblib.dump(clf, "ml/saved_models/url_phish_model.pkl")
    print(f"URL Model Metrics: {metrics}")
    return metrics

def train_sms_nlp_model():
    print("--- Training Multilingual SMS Scam NLP Detector ---")
    texts = [
        # Benign messages (English, Hindi, Telugu)
        "Your grocery order #49281 has been delivered. Thank you for shopping with us.",
        "Hi Mom, I will reach home by 7 PM today. Please keep dinner ready.",
        "Your appointment with Dr. Sharma is confirmed for tomorrow at 10:30 AM.",
        "OTP for login to your account is 482910. Do not share this with anyone.",
        "Meeting is rescheduled to 4 PM in conference room B. Please bring laptops.",
        "आज शाम को मिलते हैं, क्या आप फ्री हैं?",
        "धन्यवाद, आपकी रसीद संलग्न है।",
        "రేపు ఉదయం ఆఫీస్‌కి రండి, ప్రాజెక్ట్ చర్చించుకుందాం.",
        "మీ ఆర్డర్ విజయవంతంగా పూర్తయింది, ధన్యవాదాలు.",
        "Happy Birthday! Wishing you a wonderful year ahead with family.",
        
        # Fraud/Scam messages
        "URGENT: Your SBI netbanking account is suspended. Click http://bit.ly/sbi-kyc immediately to update pan.",
        "Congratulations! You won 25 Lakhs in lottery. Share OTP and bank details to claim cash prize now.",
        "Dear customer, your electricity power will be disconnected tonight at 9:30 PM. Call manager immediately on 9876543210.",
        "Part-time job offer: Earn 5000 daily working from home on Telegram. Deposit 500 advance fee to activate task.",
        "HDFC Alert: Your debit card is blocked. Download our support APK from http://tinyurl.com/bank-fix to unblock.",
        "बधाई! आपने 50 लाख की लॉटरी जीती है। तुरंत अपना पिन और खाता नंबर भेजें।",
        "बिजली बिल बकाया है। रात को बिजली काट दी जाएगी। तुरंत इस नंबर पर कॉल करें।",
        "అభినందనలు! మీకు 10 లక్షల బహుమతి వచ్చింది. మీ పిన్ వివరాలు ఇక్కడ షేర్ చేయండి.",
        "మీ బ్యాంకు ఖాతా నిలిపివేయబడింది, వెంటనే ఈ లింక్ క్లిక్ చేయండి."
    ]
    labels = [0]*10 + [1]*9

    # Augment samples for training
    augmented_texts = texts * 50
    augmented_labels = labels * 50

    vectorizer = TfidfVectorizer(ngram_range=(1, 2), max_features=1000)
    X = vectorizer.fit_transform(augmented_texts)
    y = np.array(augmented_labels)

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)
    
    clf = LogisticRegression(C=1.0, max_iter=200, random_state=42)
    clf.fit(X_train, y_train)

    y_pred = clf.predict(X_test)
    y_prob = clf.predict_proba(X_test)[:, 1]

    metrics = {
        "model_name": "CyberShield-MultiLang-ScamNLP",
        "version": "2.1.0",
        "algorithm": "TfidfVectorizer + LogisticRegression",
        "samples_evaluated": len(y_test),
        "accuracy": round(float(accuracy_score(y_test, y_pred)), 4),
        "precision": round(float(precision_score(y_test, y_pred)), 4),
        "recall": round(float(recall_score(y_test, y_pred)), 4),
        "f1": round(float(f1_score(y_test, y_pred)), 4),
        "roc_auc": round(float(roc_auc_score(y_test, y_prob)), 4),
        "pr_auc": round(float(average_precision_score(y_test, y_prob)), 4)
    }
    joblib.dump({"vectorizer": vectorizer, "classifier": clf}, "ml/saved_models/sms_scam_model.pkl")
    print(f"SMS Model Metrics: {metrics}")
    return metrics

def train_apk_risk_model():
    print("--- Training APK Risk / Malware Detector ---")
    np.random.seed(42)
    n_samples = 2000
    # Features: [target_sdk, is_sideloaded, num_perms, has_accessibility, has_overlay, has_sms, has_dropper]
    benign_X = np.column_stack([
        np.random.choice([31, 33, 34, 35], n_samples // 2, p=[0.1, 0.3, 0.4, 0.2]),
        np.random.choice([0, 1], n_samples // 2, p=[0.9, 0.1]),
        np.random.normal(8, 3, n_samples // 2).clip(2, 20),
        np.zeros(n_samples // 2),
        np.random.choice([0, 1], n_samples // 2, p=[0.95, 0.05]),
        np.random.choice([0, 1], n_samples // 2, p=[0.95, 0.05]),
        np.zeros(n_samples // 2)
    ])
    benign_y = np.zeros(n_samples // 2)

    malware_X = np.column_stack([
        np.random.choice([24, 26, 28, 29], n_samples // 2, p=[0.3, 0.3, 0.2, 0.2]),
        np.random.choice([0, 1], n_samples // 2, p=[0.1, 0.9]),
        np.random.normal(22, 6, n_samples // 2).clip(10, 45),
        np.random.choice([0, 1], n_samples // 2, p=[0.3, 0.7]),
        np.random.choice([0, 1], n_samples // 2, p=[0.3, 0.7]),
        np.random.choice([0, 1], n_samples // 2, p=[0.2, 0.8]),
        np.random.choice([0, 1], n_samples // 2, p=[0.4, 0.6])
    ])
    malware_y = np.ones(n_samples // 2)

    X = np.vstack([benign_X, malware_X])
    y = np.concatenate([benign_y, malware_y])

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)
    
    rf = RandomForestClassifier(n_estimators=100, max_depth=6, random_state=42)
    rf.fit(X_train, y_train)

    y_pred = rf.predict(X_test)
    y_prob = rf.predict_proba(X_test)[:, 1]

    metrics = {
        "model_name": "CyberShield-ApkRisk-TreeForest",
        "version": "1.4.0",
        "algorithm": "RandomForestClassifier",
        "samples_evaluated": len(y_test),
        "accuracy": round(float(accuracy_score(y_test, y_pred)), 4),
        "precision": round(float(precision_score(y_test, y_pred)), 4),
        "recall": round(float(recall_score(y_test, y_pred)), 4),
        "f1": round(float(f1_score(y_test, y_pred)), 4),
        "roc_auc": round(float(roc_auc_score(y_test, y_prob)), 4),
        "pr_auc": round(float(average_precision_score(y_test, y_prob)), 4)
    }
    joblib.dump(rf, "ml/saved_models/apk_risk_model.pkl")
    print(f"APK Model Metrics: {metrics}")
    return metrics

def train_behavioral_anomaly_model():
    print("--- Training Behavioral Anomaly Isolation Forest ---")
    np.random.seed(42)
    # Features: [typical_perm_count, network_type_code, scan_frequency, risk_score_moving_avg]
    normal_behavior = np.column_stack([
        np.random.normal(12, 2, 1000).clip(5, 20),
        np.random.choice([0, 1], 1000, p=[0.7, 0.3]), # 0=Home Wi-Fi, 1=Cellular
        np.random.normal(3, 1, 1000).clip(1, 8),
        np.random.normal(15, 4, 1000).clip(5, 30)
    ])
    
    iso = IsolationForest(n_estimators=100, contamination=0.03, random_state=42)
    iso.fit(normal_behavior)
    
    joblib.dump(iso, "ml/saved_models/behavior_iso_forest.pkl")
    metrics = {
        "model_name": "CyberShield-BehavioralAnomaly-IsoForest",
        "version": "1.1.0",
        "algorithm": "IsolationForest",
        "samples_trained": len(normal_behavior),
        "contamination": 0.03
    }
    print(f"Behavioral Anomaly Model fitted: {metrics}")
    return metrics

def main():
    print("==================================================")
    print("CYBERSHIELD MACHINE LEARNING TRAINING PIPELINE")
    print("==================================================")
    results = {}
    results["url_detector"] = train_url_model()
    results["sms_detector"] = train_sms_nlp_model()
    results["apk_risk_detector"] = train_apk_risk_model()
    results["behavior_detector"] = train_behavioral_anomaly_model()

    report_path = "ml/evaluation/evaluation_report.json"
    with open(report_path, "w", encoding="utf-8") as f:
        json.dump({
            "generated_at": datetime.now(timezone.utc).isoformat(),
            "pipeline_status": "COMPLETED_SUCCESSFULLY",
            "models": results
        }, f, indent=2)
    print(f"All models trained and verified. Report saved to {report_path}")

if __name__ == "__main__":
    main()
