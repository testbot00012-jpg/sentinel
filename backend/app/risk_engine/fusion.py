from typing import List, Dict, Any, Optional
from datetime import datetime, timezone
from app.schemas.cyber import RiskLevelEnum, ScannerSignal

class RiskFusionEngine:
    VERSION = "2.0.0"

    # Documented subsystem weights for overall device security score (Sum = 1.0)
    SCORING_WEIGHTS = {
        "device_posture": 0.20,
        "app_security": 0.15,
        "permission_privacy": 0.15,
        "malware_findings": 0.20,
        "network_posture": 0.10,
        "web_phishing": 0.10,
        "account_security": 0.10
    }

    @classmethod
    def calculate_overall_security_score(
        cls,
        device_posture_score: int,      # 0 to 100 (100 = completely secure)
        app_security_score: int,        # 0 to 100
        privacy_score: int,            # 0 to 100
        malware_score: int,            # 0 to 100
        network_score: int,            # 0 to 100
        web_score: int,                # 0 to 100
        account_score: int             # 0 to 100
    ) -> Dict[str, Any]:
        """
        Calculates normalized security health score (0-100, where 100 is best).
        Versioned formula:
        Overall = Sum(subsystem_score * weight)
        """
        breakdown = {
            "device_posture": max(0, min(100, device_posture_score)),
            "app_security": max(0, min(100, app_security_score)),
            "permission_privacy": max(0, min(100, privacy_score)),
            "malware_findings": max(0, min(100, malware_score)),
            "network_posture": max(0, min(100, network_score)),
            "web_phishing": max(0, min(100, web_score)),
            "account_security": max(0, min(100, account_score))
        }

        weighted_sum = sum(breakdown[k] * cls.SCORING_WEIGHTS[k] for k in cls.SCORING_WEIGHTS)
        final_score = int(round(weighted_sum))

        recommendations = []
        if breakdown["device_posture"] < 80:
            recommendations.append("Harden device settings: enable screen lock, check security updates, or disable developer options.")
        if breakdown["app_security"] < 80:
            recommendations.append("Review installed sideloaded apps and high-risk permission holders.")
        if breakdown["malware_findings"] < 80:
            recommendations.append("Run a full device malware sweep to quarantine suspicious binaries.")
        if breakdown["network_posture"] < 80:
            recommendations.append("Avoid unencrypted Wi-Fi networks or ensure VPN protection is activated.")
        if breakdown["account_security"] < 80:
            recommendations.append("Enable Multi-Factor Authentication (MFA) and check email breach monitoring.")

        if not recommendations:
            recommendations.append("All security subsystems report healthy status. Continue regular scans.")

        return {
            "overall_score": final_score,
            "breakdown": breakdown,
            "scoring_version": cls.VERSION,
            "recommendations": recommendations,
            "calculated_at": datetime.now(timezone.utc)
        }

    @classmethod
    def correlate_threat_event(
        cls,
        scanner_results: List[Dict[str, Any]],
        behavior_anomaly_score: float = 0.0,
        device_compromised: bool = False
    ) -> Dict[str, Any]:
        """
        Fuses disparate signals into a unified threat verdict.
        Example: Suspicious SMS + malicious URL + unknown APK + dangerous permissions + behavior anomaly
        """
        if not scanner_results:
            return {
                "final_risk_score": 0,
                "risk_level": RiskLevelEnum.SAFE,
                "confidence": 0.90,
                "contributing_signals": [],
                "recommended_actions": ["No active threats observed."]
            }

        # Multi-factor correlation
        max_scanner_score = max(r.get("risk_score", 0) for r in scanner_results)
        avg_confidence = sum(r.get("confidence", 0.8) for r in scanner_results) / len(scanner_results)
        
        correlated_score = float(max_scanner_score)
        contributing_signals = []

        for r in scanner_results:
            for s in r.get("signals", []):
                contributing_signals.append(s)

        # Correlation booster: multiple independent detections
        high_risk_scanners = [r for r in scanner_results if r.get("risk_score", 0) >= 50]
        if len(high_risk_scanners) >= 2:
            correlated_score += 15
            contributing_signals.append({
                "name": "Correlated Multi-Vector Threat",
                "type": "CORRELATION_FUSION",
                "severity": "CRITICAL",
                "description": f"Detected {len(high_risk_scanners)} concurrent suspicious channels pointing to coordinated fraud or infection."
            })

        # Behavioral anomaly modifier
        if behavior_anomaly_score >= 0.5:
            correlated_score += int(behavior_anomaly_score * 20)
            contributing_signals.append({
                "name": "Behavioral Anomaly Amplification",
                "type": "BEHAVIORAL_DEVIATION",
                "severity": "HIGH",
                "description": f"Device baseline shows simultaneous behavioral deviation (Anomaly score: {behavior_anomaly_score})."
            })

        # Root / Compromised OS modifier
        if device_compromised:
            correlated_score += 20
            contributing_signals.append({
                "name": "Compromised Platform Posture",
                "type": "DEVICE_POSTURE",
                "severity": "CRITICAL",
                "description": "Host device exhibits root or integrity compromise, reducing isolation safeguards."
            })

        final_risk_score = min(100, int(round(correlated_score)))
        
        if final_risk_score < 25:
            level = RiskLevelEnum.SAFE
            actions = ["Maintain standard cybersecurity vigilance."]
        elif final_risk_score < 60:
            level = RiskLevelEnum.SUSPICIOUS
            actions = ["Inspect suspicious items.", "Do not authorize elevated permissions or financial transactions."]
        elif final_risk_score < 85:
            level = RiskLevelEnum.HIGH_RISK
            actions = ["Quarantine or remove flagged applications/files immediately.", "Revoke permissions from suspicious applications."]
        else:
            level = RiskLevelEnum.CRITICAL
            actions = [
                "CRITICAL SECURITY EVENT: Immediately isolate device from sensitive networks.",
                "Revoke session tokens and change banking/email credentials from a trusted secondary device.",
                "Create an incident record in Incident Center."
            ]

        return {
            "final_risk_score": final_risk_score,
            "risk_level": level,
            "confidence": min(1.0, round(avg_confidence, 2)),
            "contributing_signals": contributing_signals,
            "recommended_actions": actions
        }
