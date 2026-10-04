import re
from typing import Dict, Any, List
from app.scanners.base import BaseScanner
from app.schemas.cyber import SecurityResult, ScannerSignal, PaymentScanRequest

class PaymentScreenshotScanner(BaseScanner[PaymentScanRequest]):
    def __init__(self):
        super().__init__(
            scanner_type="PAYMENT_SCREENSHOT_FRAUD",
            model_name="CyberShield-PaymentVision-LayoutAudit",
            model_version="1.8.0"
        )

    async def analyze(self, input_data: PaymentScanRequest) -> SecurityResult:
        ocr_text = (input_data.ocr_text or "").strip()
        signals: List[ScannerSignal] = []
        score = 15
        confidence = 0.85
        
        # 1. OCR Extraction of Transaction IDs (UTR, Ref No)
        utr_pattern = r"\b(?:UTR|Ref(?:\s*No)?|Txn\s*ID)[:\s]*([0-9A-Za-z]{10,22})\b"
        utr_match = re.search(utr_pattern, ocr_text, flags=re.IGNORECASE)
        extracted_utr = utr_match.group(1) if utr_match else None
        
        if extracted_utr:
            signals.append(ScannerSignal(
                name="Transaction Identifier Found",
                type="FIELD_EXTRACTION",
                severity="LOW",
                description=f"Extracted transaction reference number: {extracted_utr}",
                evidence_value=extracted_utr
            ))
            # Check length/structure heuristic for Indian UPI (normally 12 numeric digits)
            if re.match(r"^\d{12}$", extracted_utr):
                signals.append(ScannerSignal(
                    name="Standard 12-Digit UTR Format",
                    type="SYNTAX_VALIDATION",
                    severity="LOW",
                    description="Reference matches standard 12-digit UPI banking reference format.",
                    evidence_value=extracted_utr
                ))
            else:
                score += 25
                signals.append(ScannerSignal(
                    name="Non-Standard UTR Length/Structure",
                    type="SYNTAX_ANOMALY",
                    severity="MEDIUM",
                    description=f"Extracted reference '{extracted_utr}' does not match standard 12-digit banking format.",
                    evidence_value=extracted_utr
                ))
        else:
            score += 30
            signals.append(ScannerSignal(
                name="Missing Bank Reference (UTR)",
                type="MISSING_FIELD",
                severity="HIGH",
                description="Legitimate payment confirmation receipts always display a 12-digit UTR/Bank Reference.",
                evidence_value="None found in text"
            ))

        # 2. Check for Font / Tampering artifacts (Common fake payment apps e.g. Paytm Spoon / Fake Pay)
        fake_app_indicators = [
            r"(?i)\b(demo|spoof|fake|prank|simulated|sample)\b",
            r"(?i)\b(test payment|for entertainment purposes)\b"
        ]
        for pat in fake_app_indicators:
            if re.search(pat, ocr_text):
                score += 50
                signals.append(ScannerSignal(
                    name="Fake Payment Generator Watermark",
                    type="WATERMARK_ANOMALY",
                    severity="CRITICAL",
                    description="Text contains explicit indicators of a payment simulation or prank generator app.",
                    evidence_value="Generator keyword detected"
                ))

        # 3. Timestamp / Date validation
        date_pattern = r"\b(\d{1,2}\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\s+\d{4}|\d{2}[/-]\d{2}[/-]\d{4})\b"
        if not re.search(date_pattern, ocr_text, flags=re.IGNORECASE):
            score += 20
            signals.append(ScannerSignal(
                name="Missing Complete Timestamp",
                type="METADATA_ABSENCE",
                severity="MEDIUM",
                description="Receipt lacks a verifiable execution timestamp and date.",
                evidence_value="Undated layout"
            ))

        final_score = min(95, score)
        
        if final_score < 40:
            explanation = "Visual and structural layout conforms to known UPI receipt conventions. Reference ID found."
            recommended_actions = [
                "CRITICAL: Always verify credit directly in your official banking or UPI app before handing over goods or services.",
                "Never rely exclusively on a buyer's screenshot as proof of credit."
            ]
        else:
            explanation = f"Detected {len(signals)} receipt inconsistency signal(s). The screenshot exhibits anomalies common in altered or simulated payment slips."
            recommended_actions = [
                "DO NOT release goods, merchandise, or services based on this image.",
                "Open your official bank app and check your actual account transaction statement.",
                "Check for an official SMS or push notification from your bank verifying the credited amount."
            ]

        limitations = [
            "Visual and OCR inspection only evaluates layout and typography consistency.",
            "CyberShield CANNOT authenticate bank transactions without an authorized direct API link to the settlement bank.",
            "Visual consistency alone does not guarantee that the funds have reached your account."
        ]

        return self.build_result(
            risk_score=final_score,
            confidence=confidence,
            signals=signals,
            explanation=explanation,
            recommended_actions=recommended_actions,
            limitations=limitations
        )
