import os
import joblib
import numpy as np
import pytest

def test_saved_models_exist():
    expected_models = [
        "ml/saved_models/url_phish_model.pkl",
        "ml/saved_models/sms_scam_model.pkl",
        "ml/saved_models/apk_risk_model.pkl",
        "ml/saved_models/behavior_iso_forest.pkl"
    ]
    for p in expected_models:
        assert os.path.exists(p), f"Missing model checkpoint: {p}"

def test_url_model_inference():
    clf = joblib.load("ml/saved_models/url_phish_model.pkl")
    # Benign sample: short, low entropy, 1 dot, no IP, 0 keywords, https=1
    benign_sample = np.array([[22.0, 3.1, 1, 0, 0, 1]])
    pred_benign = clf.predict(benign_sample)[0]
    assert pred_benign == 0

    # Phish sample: long, high entropy, 4 dots, has IP, 3 keywords, https=0
    phish_sample = np.array([[85.0, 4.8, 4, 1, 3, 0]])
    pred_phish = clf.predict(phish_sample)[0]
    assert pred_phish == 1

def test_sms_model_inference():
    bundle = joblib.load("ml/saved_models/sms_scam_model.pkl")
    vectorizer = bundle["vectorizer"]
    clf = bundle["classifier"]

    # Benign SMS
    safe_vec = vectorizer.transform(["Your food delivery order is confirmed for 8 PM."])
    assert clf.predict(safe_vec)[0] == 0

    # Phishing SMS
    fraud_vec = vectorizer.transform(["URGENT: Click http://bit.ly/sbi to update your account pan immediately."])
    assert clf.predict(fraud_vec)[0] == 1

def test_apk_model_inference():
    rf = joblib.load("ml/saved_models/apk_risk_model.pkl")
    # Safe app: Target SDK 34, official store, 5 perms, no accessibility, no overlay, no sms, no dropper
    safe_apk = np.array([[34, 0, 5, 0, 0, 0, 0]])
    assert rf.predict(safe_apk)[0] == 0

    # High-risk banking trojan: Target SDK 26, sideloaded, 25 perms, accessibility=1, overlay=1, sms=1, dropper=1
    trojan_apk = np.array([[26, 1, 25, 1, 1, 1, 1]])
    assert rf.predict(trojan_apk)[0] == 1

def test_behavioral_anomaly_inference():
    iso = joblib.load("ml/saved_models/behavior_iso_forest.pkl")
    # Normal behavior sample
    normal = np.array([[12.0, 0, 3.0, 15.0]])
    pred = iso.predict(normal)[0]
    assert pred == 1  # Inlier
