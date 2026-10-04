# CyberShield — Security & Privacy Architecture

---

## 1. Zero Fake Data Rule

CyberShield adheres strictly to the **Zero Fake Data Rule**:
- Device status displays only verified telemetry harvested from Android platform APIs (`Build`, `BatteryManager`, `StatFs`, `KeyguardManager`, `DevicePolicyManager`).
- Subsystem scores derive strictly from the documented versioned scoring formula (`RiskFusionEngine`).
- Threats are only declared when a specialized ML or heuristic detector actually produces that verdict.
- No model claims 100% accuracy; all inferences expose explicit **Confidence**, **Signals**, and **Limitations**.

---

## 2. Data Minimization & Privacy Controls

1. **On-Device First Processing:**
   - Static APK inspection, hardware posture, file SHA-256 calculation, and local heuristic evaluations execute on-device without cloud transmission.
2. **Explicit Consent for Cloud Analysis:**
   - Files, screenshots, and URLs are only uploaded for deep inference when explicitly initiated by the user.
3. **No Invasive Hardware Tracking:**
   - CyberShield uses privacy-preserving installation UUIDs instead of invasive hardware identifiers (IMEI, MAC address).
4. **Zero Password Collection:**
   - CyberShield never collects or transmits raw passwords. Password health audits use local entropy calculations and k-anonymity hashing.

---

## 3. Platform Restrictions & Supported Alternatives Matrix

Per Android and Google Play policies, normal third-party applications are constrained by sandbox boundaries. The table below documents features where Android enforces platform restrictions, alongside the verified supported alternatives implemented in CyberShield:

| Requested Feature | Android Platform Restriction | Supported Alternative Implemented | User Permission / Role Required |
| :--- | :--- | :--- | :--- |
| **Silent Call Recording / Audio Interception** | Android 9+ explicitly blocks third-party call recording and background microphone access. | User-reported scam number lookup, phone pattern analysis, and manual scam call reporting. | `android.permission.READ_PHONE_STATE` (Optional) / None |
| **Arbitrary Network Packet Inspection of Other Apps** | Android isolates app sandboxes; applications cannot inspect raw IP packets of other apps without a VPN service. | Android `ConnectivityManager` & `LinkProperties` inspection (Wi-Fi encryption, DNS servers, VPN status, captive portal). | `android.permission.ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE` |
| **Arbitrary App Uninstallation / Blocking** | Only Device Owner / Device Admin or system apps can silently uninstall or block third-party packages. | Deep static APK risk analysis, clear danger alerts, and direct intent forwarding to Android Settings app uninstall screen. | `android.permission.REQUEST_DELETE_PACKAGES` |
| **Global Package Visibility (Android 11+)** | `QUERY_ALL_PACKAGES` is restricted to antivirus/device manager apps with Google Play policy declarations. | Package inspection through standard Android `PackageManager` with declared security tool role. | `<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />` |
| **System-wide Keystroke / Password Interception** | Prohibited by Android security architecture. | k-Anonymity breach lookup via email query; password health checked only via local user input without persistence. | None |
| **Automatic Cloud Upload of User Media** | Prohibited by data protection policies and Android scoped storage. | Explicit user selection via Android Storage Access Framework (SAF) Document Picker. | `android.permission.READ_MEDIA_IMAGES` (Scoped) |
