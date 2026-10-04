from abc import ABC, abstractmethod
from typing import TypeVar, Generic, List
import uuid
from datetime import datetime, timezone
from app.schemas.cyber import SecurityResult, ScannerSignal, RiskLevelEnum

TInput = TypeVar("TInput")

class BaseScanner(ABC, Generic[TInput]):
    def __init__(self, scanner_type: str, model_name: str, model_version: str):
        self.scanner_type = scanner_type
        self.model_name = model_name
        self.model_version = model_version

    @abstractmethod
    async def analyze(self, input_data: TInput) -> SecurityResult:
        pass

    def build_result(
        self,
        risk_score: int,
        confidence: float,
        signals: List[ScannerSignal],
        explanation: str,
        recommended_actions: List[str],
        limitations: List[str],
        scan_id: str = None,
        security_score: int = None,
        what_to_avoid: List[str] = None,
        quick_summary: str = None,
        why_this_score: List[str] = None,
        evidence: List[str] = None,
        specialized_report: dict = None,
        raw_input_reference: str = None
    ) -> SecurityResult:
        threat_score = max(0, min(100, risk_score))
        # Sentinel AI Security Score: Higher is Safer (0 to 100)
        sec_score = security_score if security_score is not None else max(0, min(100, 100 - threat_score))
        threat_prob = round(threat_score / 100.0, 2)

        # Sentinel AI Visual Risk Classification (Section 8 of Master Prompt)
        # 90-100: SAFE / STRONG SECURITY (GREEN)
        # 75-89: LOW CONCERN / REVIEW (LIGHT ORANGE)
        # 60-74: MEDIUM CONCERN (BLACKISH YELLOW / DARK AMBER)
        # 0-59: HIGH RISK / CRITICAL (RED)
        if sec_score >= 90:
            level = RiskLevelEnum.SAFE
        elif sec_score >= 75:
            level = RiskLevelEnum.SAFE  # LOW CONCERN / REVIEW
        elif sec_score >= 60:
            level = RiskLevelEnum.SUSPICIOUS  # MEDIUM CONCERN
        elif sec_score >= 35:
            level = RiskLevelEnum.HIGH_RISK
        else:
            level = RiskLevelEnum.CRITICAL

        # Generate Why This Score contributors if not provided
        if not why_this_score:
            why_this_score = []
            if sec_score >= 90:
                why_this_score.append("✓ No known malicious indicators detected")
                why_this_score.append("✓ Structural inspection nominal")
            else:
                for sig in signals:
                    sign = "+" if sig.severity in ["HIGH", "CRITICAL"] else "•"
                    why_this_score.append(f"{sign} {sig.name} ({sig.severity}): {sig.description}")

        # Generate What To Avoid if not provided
        if not what_to_avoid:
            if sec_score >= 90:
                what_to_avoid = [
                    "Do not share account passwords or verification OTPs with third parties.",
                    "Do not disable security safeguards."
                ]
            else:
                what_to_avoid = [
                    "Do NOT click unverified links or authorize unsolicited redirects.",
                    "Do NOT enter your banking PIN, OTP, or account credentials.",
                    "Do NOT install unknown third-party APKs or grant Accessibility permissions.",
                    "Do NOT forward or authorize unconfirmed UPI collect requests."
                ]

        # Generate Evidence if not provided
        if not evidence:
            evidence = [s.evidence_value for s in signals if s.evidence_value]
            if not evidence and raw_input_reference:
                evidence = [f"Analyzed Reference: {raw_input_reference}"]

        # Generate Quick Summary if not provided
        if not quick_summary:
            if sec_score >= 90:
                quick_summary = "Sentinel AI analyzed this target and confirmed it satisfies standard security baselines without malicious indicators."
            elif sec_score >= 75:
                quick_summary = "The analyzed target does not currently exhibit high-severity malware behavior, but contains signals that warrant user caution."
            elif sec_score >= 60:
                quick_summary = "Sentinel AI flagged multiple suspicious characteristics. Verification with the official provider is recommended before interacting."
            else:
                quick_summary = "CRITICAL ALERT: Sentinel AI detected strong threat indicators associated with fraud, phishing, or malware compromise."

        return SecurityResult(
            scan_id=scan_id or str(uuid.uuid4()),
            scanner_type=self.scanner_type,
            risk_level=level,
            risk_score=threat_score,
            security_score=sec_score,
            threat_probability=threat_prob,
            confidence=max(0.0, min(1.0, round(confidence, 2))),
            signals=signals,
            explanation=explanation,
            recommended_actions=recommended_actions,
            what_to_avoid=what_to_avoid,
            limitations=limitations,
            model_name=self.model_name.replace("CyberShield", "Sentinel"),
            model_version=self.model_version,
            timestamp=datetime.now(timezone.utc),
            quick_summary=quick_summary,
            why_this_score=why_this_score,
            evidence=evidence,
            specialized_report=specialized_report or {},
            raw_input_reference=raw_input_reference
        )
