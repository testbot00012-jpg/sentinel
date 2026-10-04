# CyberShield — Complete Android Navigation Map

Total registered screens: **174**

## Primary document flow
`Splash → Login/Register/Forgot → Home → Scan Center → Results → Protection → Incidents → Account/Settings`

## Bottom navigation
`Home | Protect | Scan | Alerts | Settings`

## Protection module hierarchy
### 01 Security Center
1. Overall Security Score
2. Device Security Status
3. Threat Status
4. Privacy Status
5. Network Status
6. Account Security
7. Security Recommendations
8. Last Security Scan
### 02 Device Protection
1. Device Information
2. OS Security
3. Security Updates
4. Device Configuration
5. Screen Lock Status
6. Encryption Status
7. Developer Options Status
8. Root/Jailbreak Detection
9. Device Integrity
### 03 App Security
1. Installed Applications
2. Dangerous Apps
3. Suspicious Apps
4. App Risk Score
5. App Permissions
6. Unknown Sources
7. Sideloaded Apps
8. App Activity
9. App Security Report
### 04 Permission & Privacy
1. Camera
2. Microphone
3. Location
4. Contacts
5. SMS
6. Phone
7. Storage
8. Notifications
9. Accessibility
10. Background Activity
11. Privacy Risk Score
### 05 Malware Protection
1. Quick Scan
2. Full Device Scan
3. File Scanner
4. App Scanner
5. Malware Detection
6. Suspicious File Detection
7. Quarantine
8. Scan History
9. Malware Reports
### 06 Network Security
1. Wi-Fi Security
2. Current Network
3. Network Risk
4. DNS Security
5. Suspicious Connections
6. Unsafe Network Detection
7. Public Wi-Fi Warning
8. VPN Status
9. Network History
### 07 Web Protection
1. URL Scanner
2. Website Risk Analyzer
3. Phishing Detection
4. Malicious Website Detection
5. Fake Website Detection
6. QR Code Scanner
7. Safe Browsing
8. Browser Protection
### 08 Phishing & Scam Protection
1. SMS Analysis
2. Email Analysis
3. Message Analysis
4. URL Analysis
5. QR Analysis
6. Scam Detection
7. Social Engineering Detection
8. Scam Report
### 09 Account Security
1. Account Inventory
2. Login Activity
3. Suspicious Login Detection
4. Password Health
5. MFA Status
6. Breach Monitoring
7. Session Monitoring
8. Account Risk
### 10 File & Storage Security
1. Storage Analyzer
2. Suspicious Files
3. Duplicate Files
4. Dangerous Documents
5. Download Scanner
6. File Integrity
7. Secure Vault
8. Secure Delete
### 11 AI Security Engine
1. Behavioral Analysis
2. Anomaly Detection
3. Threat Classification
4. Risk Calculation
5. Threat Correlation
6. Security Recommendations
7. Continuous Risk Assessment
### 12 AI Security Assistant
1. Ask Security Question
2. Explain Threat
3. Analyze Screenshot
4. Analyze Message
5. Analyze URL
6. Explain App Permissions
7. Security Recommendations
8. Emergency Guidance
### 13 Security Monitor
1. Real-Time Protection
2. Background Monitoring
3. Threat Events
4. Security Timeline
5. Recent Activities
6. Detection History
### 14 Alert Center
1. Critical Alerts
2. High Risk
3. Medium Risk
4. Low Risk
5. Alert Details
6. Recommended Action
7. Alert History
### 15 Security Checkup
1. Device Check
2. App Check
3. Permission Check
4. Network Check
5. Account Check
6. Privacy Check
7. Complete Security Report
### 16 Incident Center
1. Report Threat
2. Incident Details
3. Evidence
4. Timeline
5. Threat Status
6. Recommended Actions
7. Incident History
### 17 Emergency Protection
1. Lock Security Session
2. Revoke Account Sessions
3. Mark Device Compromised
4. Disable Trusted Access
5. Emergency Scan
6. Security Contact
### 18 Security Reports
1. Daily Security Summary
2. Weekly Security Summary
3. Threat Report
4. Privacy Report
5. Network Report
6. App Risk Report
7. Device Security Report
### 19 Settings
1. Profile
2. Scan Settings
3. About

## Implementation model
All 174 screens are registered as independent navigation destinations through `ScreenRegistry`. Feature screens use one reusable Compose renderer (`FeatureScreen`) so the code is maintainable while every screen still has its own route and can be linked directly.

## Important integration boundary
This package implements the Android UI and navigation skeleton. Real malware/phishing/deepfake detection, SMS/Call/Accessibility/VPN monitoring, Firebase authentication, Firestore persistence, ML inference, complaint submission, and evidence upload require backend/API/model integration and Android permissions/roles. The UI does not silently perform those actions.