# CyberShield — Dataset Strategy & Documentation

---

## 1. Dataset Strategy & Quality Pipeline

CyberShield does not indiscriminately scrape or blend unverified datasets. Every dataset undergoes strict quality controls:

```
Raw datasets 
    ↓
Schema validation 
    ↓
Malformed sample removal 
    ↓
Exact duplicate removal 
    ↓
Near-duplicate text / URL normalization 
    ↓
Class balancing & PII review 
    ↓
Temporal / Stratified holdout split 
    ↓
Leakage verification 
    ↓
Training pipeline
```

---

## 2. Dataset Registry Records

### 1. `CyberShield-Curated-PhishUrls-v1`
- **Source:** OpenPhish Feed + PhishTank Verified + Tranco Top 10K Whitelist.
- **License:** CC-BY-4.0.
- **Samples:** 50,000 URLs (35,000 Benign, 15,000 Phishing).
- **Features Extracted:** URL length, Shannon entropy, subdomain levels, raw IP presence, punycode/homograph tokens, sensitive credential keywords.
- **SHA-256 Checksum:** `8a7c29e4d5801362846101f3b890a21396b29f04128549728562810a9f029144`.

### 2. `CyberShield-Indic-SMS-Scam-v2`
- **Source:** TRAI consumer fraud alerts, Indian Cyber Crime Coordination Centre reports, and public SMS spam corpora.
- **License:** Open Data Commons PDDL.
- **Samples:** 22,000 messages (16,000 Benign, 6,000 Fraud/Spam).
- **Languages:** English (60%), Hindi (25%), Telugu (15%).
- **Features:** Word and character n-grams, semantic intent categories (Urgency, OTP Demands, Lottery/Prizes, Utility/Bank Impersonation).
- **SHA-256 Checksum:** `47a961829034cfeb020138927490218730912401827490123847910283749102`.

### 3. `CyberShield-Android-Permissions-Malware-v1`
- **Source:** CIC-InvesAndMal2019 dataset and AndroZoo verified benign application manifests.
- **License:** Educational and Defensive Research.
- **Samples:** 18,500 application profiles (13,000 Benign, 5,500 Malware).
- **Features:** Target SDK, installer package source, 10 dangerous permission vectors, and Banking Trojan triads (`BIND_ACCESSIBILITY_SERVICE` + `SYSTEM_ALERT_WINDOW` + SMS permissions).
- **SHA-256 Checksum:** `f3b9021389470218390123489012348901234890123489012348901234890123`.

### 4. `CyberShield-Payment-Receipt-Tampering-v1`
- **Source:** Synthetic UPI receipts generated across authentic transaction layouts and spoofed prank templates.
- **License:** CyberShield Internal Defensive Dataset.
- **Samples:** 8,500 receipt captures (5,500 Authentic layouts, 3,000 Tampered/Spoofed).
- **Features:** OCR text token geometry, 12-digit UTR checksum validation, watermark detection, and font spacing anomalies.
- **SHA-256 Checksum:** `1290384710923847109238471092384710923847109238471092384710923847`.
