from typing import List, Dict, Any
from app.scanners.base import BaseScanner
from app.schemas.cyber import SecurityResult, ScannerSignal, ApkScanRequest

class ApkSecurityScanner(BaseScanner[ApkScanRequest]):
    def __init__(self):
        super().__init__(
            scanner_type="APK_STATIC_ANALYSIS",
            model_name="CyberShield-ApkRisk-TreeForest",
            model_version="1.4.0"
        )
        self.dangerous_permissions = {
            "android.permission.RECEIVE_SMS": ("SMS Interception", 25, "Enables reading incoming verification SMS and OTPs."),
            "android.permission.READ_SMS": ("SMS Exfiltration", 20, "Enables reading archived private text messages."),
            "android.permission.SEND_SMS": ("Premium SMS / Spoofing", 20, "Allows sending background SMS without user confirmation."),
            "android.permission.READ_CONTACTS": ("Address Book Harvesting", 10, "Accesses complete phone contacts and personal details."),
            "android.permission.RECORD_AUDIO": ("Microphone Eavesdropping", 15, "Enables background voice and environmental audio recording."),
            "android.permission.CAMERA": ("Camera Capture", 15, "Enables capturing photos and videos."),
            "android.permission.ACCESS_FINE_LOCATION": ("Precise Geolocation", 10, "Tracks exact GPS coordinates of the device."),
            "android.permission.SYSTEM_ALERT_WINDOW": ("Overlay / Tapjacking", 25, "Enables drawing on top of other banking apps to steal credentials."),
            "android.permission.BIND_ACCESSIBILITY_SERVICE": ("Accessibility Abuse / RAT", 35, "Full device automation, screen scraping, and keylogging risk."),
            "android.permission.REQUEST_INSTALL_PACKAGES": ("Dropper Capability", 30, "Can download and install arbitrary secondary APK packages.")
        }
        self.official_installers = [
            "com.android.vending",  # Google Play Store
            "com.amazon.venezia",   # Amazon Appstore
            "com.sec.android.app.samsungapps" # Samsung Galaxy Store
        ]

    async def analyze(self, input_data: ApkScanRequest) -> SecurityResult:
        signals: List[ScannerSignal] = []
        score = 5
        confidence = 0.90
        perms = input_data.permissions

        # 1. Sideloading / Installer verification
        installer = input_data.installer_package
        if input_data.is_sideloaded or (installer and installer not in self.official_installers):
            score += 25
            signals.append(ScannerSignal(
                name="Sideloaded / Unknown Installation Source",
                type="ORIGIN_RISK",
                severity="MEDIUM",
                description=f"App was not installed from official Google Play Store (Source: {installer or 'Direct APK install'}).",
                evidence_value=installer or "Direct sideload"
            ))

        # 2. Target SDK Obsolete Check
        if input_data.target_sdk and input_data.target_sdk < 29:
            score += 20
            signals.append(ScannerSignal(
                name="Outdated Target SDK",
                type="PLATFORM_HARDENING",
                severity="HIGH",
                description=f"Targets Android SDK {input_data.target_sdk}, bypassing modern scoped storage and permission restrictions.",
                evidence_value=f"SDK {input_data.target_sdk}"
            ))

        # 3. Dangerous Permission Evaluation
        matched_dangerous = []
        for perm, (label, weight, desc) in self.dangerous_permissions.items():
            if perm in perms:
                matched_dangerous.append(perm)
                score += weight
                signals.append(ScannerSignal(
                    name=f"Dangerous Permission: {label}",
                    type="PERMISSION_SURFACE",
                    severity="HIGH" if weight >= 25 else "MEDIUM",
                    description=desc,
                    evidence_value=perm,
                    weight=float(weight)
                ))

        # 4. Critical Threat Triad: Accessibility + Overlay / SMS (Classic Banking Trojan profile)
        has_accessibility = "android.permission.BIND_ACCESSIBILITY_SERVICE" in perms
        has_overlay = "android.permission.SYSTEM_ALERT_WINDOW" in perms
        has_sms = any(p in perms for p in ["android.permission.RECEIVE_SMS", "android.permission.READ_SMS"])

        if has_accessibility and (has_overlay or has_sms):
            score += 35
            signals.append(ScannerSignal(
                name="Banking Trojan Permission Triad",
                type="CORRELATED_CAPABILITY",
                severity="CRITICAL",
                description="Application combines Accessibility service with Screen Overlay/SMS privileges, a signature profile of Android Banking Trojans (e.g., SharkBot/Cerberus).",
                evidence_value="Accessibility + Overlay/SMS"
            ))

        # 5. Dropper signature
        if "android.permission.REQUEST_INSTALL_PACKAGES" in perms and input_data.is_sideloaded:
            score += 20
            signals.append(ScannerSignal(
                name="Untrusted Dropper Profile",
                type="DROPPER_RISK",
                severity="HIGH",
                description="Sideloaded package requests permission to install secondary APKs.",
                evidence_value="REQUEST_INSTALL_PACKAGES"
            ))

        final_score = min(99, score)
        
        if final_score < 30:
            explanation = "Application declares reasonable permissions consistent with standard utility functionality."
            recommended_actions = ["No immediate action needed.", "Periodically review granted permissions in system settings."]
        else:
            explanation = f"High-risk application posture detected ({len(signals)} risk signals). Package requests privileged capabilities capable of exfiltration or device automation."
            recommended_actions = [
                "Unless this application is from a verified trusted enterprise vendor, consider uninstalling it.",
                "Ensure Accessibility permission is NOT enabled for this app in Android Settings.",
                "Do not open banking or finance apps while untrusted overlay applications are running."
            ]

        limitations = [
            "Static manifest inspection evaluates potential capabilities, not runtime execution logs.",
            "Normal accessibility tools for disabled users legitimately request AccessibilityService."
        ]

        return self.build_result(
            risk_score=final_score,
            confidence=confidence,
            signals=signals,
            explanation=explanation,
            recommended_actions=recommended_actions,
            limitations=limitations
        )
