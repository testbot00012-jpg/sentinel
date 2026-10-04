# CyberShield — REST API Documentation

Base URL: `/api/v1`

---

## 1. Authentication Endpoints

### `POST /api/v1/auth/register`
Registers a new user and associates the initial client device.
```json
{
  "email": "user@cybershield.security",
  "password": "StrongPassword#2026",
  "full_name": "Security User",
  "installation_id": "inst_uuid_12345",
  "device_name": "Pixel 9"
}
```
**Response (200 OK):**
```json
{
  "access_token": "eyJhbGciOi...",
  "token_type": "bearer",
  "user_id": "usr_9981",
  "device_id": "dev_0012",
  "email": "user@cybershield.security"
}
```

### `POST /api/v1/auth/login`
Authenticates user credentials and resolves device session.

### `POST /api/v1/auth/firebase`
Exchanges a verified Firebase ID token for a CyberShield session token.

---

## 2. Device & Posture Endpoints

### `POST /api/v1/device/register`
Registers or updates device hardware metadata.

### `POST /api/v1/device/posture-sync`
Synchronizes live Android security state (screen lock, encryption, developer options, root signals, battery).

### `GET /api/v1/device/posture`
Returns the latest verified device posture record.

---

## 3. Security Score & Risk Engine

### `GET /api/v1/security/score`
Calculates and returns the unified device security score (0-100) with subsystem breakdowns and safe action recommendations.

---

## 4. Threat Detection Scanners

All scanner endpoints conform to the **Standard Scanner Interface**:
- `POST /api/v1/url/analyze`: Lexical, entropy, and domain phishing classifier.
- `POST /api/v1/messages/analyze`: Multilingual NLP scam detection (English, Hindi, Telugu).
- `POST /api/v1/qr/analyze`: QR payload routing (UPI payment verification, URL security).
- `POST /api/v1/payment/analyze-screenshot`: OCR & layout tampering detection for payment slips.
- `POST /api/v1/apk/analyze`: Static manifest and dangerous permission combination auditor.
- `POST /api/v1/files/scan`: Cryptographic SHA-256 IOC and Shannon entropy scanner.
- `POST /api/v1/network/audit`: Wi-Fi cipher, captive portal, and rogue DNS auditor.

**Standard Scanner Response Schema (`SecurityResult`):**
```json
{
  "scan_id": "f83a04b1-8b38-406a-a434-2e6515c0e181",
  "scanner_type": "URL_PHISHING",
  "risk_level": "HIGH_RISK",
  "risk_score": 87,
  "confidence": 0.91,
  "signals": [
    {
      "name": "Direct IP Hostname",
      "type": "HOST_OBSCURATION",
      "severity": "HIGH",
      "description": "URL uses a raw IP address instead of a registered domain name."
    }
  ],
  "explanation": "Detected 3 risk signals. URL shows deceptive credential harvesting characteristics.",
  "recommended_actions": [
    "Do NOT enter credentials, OTPs, or financial information."
  ],
  "limitations": [
    "Lexical analysis cannot inspect dynamic client-side JavaScript execution."
  ],
  "model_name": "CyberShield-UrlPhish-GBDT-Ensemble",
  "model_version": "1.2.0",
  "timestamp": "2026-10-04T16:15:00Z"
}
```

---

## 5. Alerts & Incidents

- `GET /api/v1/alerts/`: Retrieve prioritized alert feeds (Critical, High, Medium, Low).
- `POST /api/v1/alerts/{id}/dismiss`: Dismiss/acknowledge an alert.
- `POST /api/v1/incidents/create`: Open a tracked security incident with attached evidence.
- `GET /api/v1/incidents/{id}`: Retrieve incident status, evidence, and audit timeline.

---

## 6. AI Security Assistant

### `POST /api/v1/assistant/chat`
RAG-backed assistant explaining real detector outputs and verified security practices.
```json
{
  "message": "Why is Accessibility permission dangerous for an unknown app?",
  "language": "en"
}
```
