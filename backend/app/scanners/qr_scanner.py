from urllib.parse import urlparse, parse_qs
from typing import Dict, Any, List
from app.scanners.base import BaseScanner
from app.scanners.url_scanner import UrlScanner
from app.schemas.cyber import SecurityResult, ScannerSignal, QrScanRequest

class QrFraudScanner(BaseScanner[QrScanRequest]):
    def __init__(self, url_scanner: UrlScanner):
        super().__init__(
            scanner_type="QR_FRAUD",
            model_name="CyberShield-QrPayload-Parser",
            model_version="1.5.0"
        )
        self.url_scanner = url_scanner

    async def analyze(self, input_data: QrScanRequest) -> SecurityResult:
        payload = input_data.raw_payload.strip()
        signals: List[ScannerSignal] = []
        
        # 1. UPI Payment URI analysis (e.g., upi://pay?pa=...&pn=...&am=...)
        if payload.lower().startswith("upi://pay"):
            parsed_upi = urlparse(payload)
            params = parse_qs(parsed_upi.query)
            
            pa = params.get("pa", [""])[0] # Payee VPA
            pn = params.get("pn", [""])[0] # Payee Name
            am = params.get("am", [""])[0] # Amount
            mc = params.get("mc", [""])[0] # Merchant code
            
            score = 15
            confidence = 0.90
            
            signals.append(ScannerSignal(
                name="UPI Payment Intent",
                type="PAYLOAD_CLASSIFICATION",
                severity="LOW",
                description=f"Payload is a UPI payment request to '{pa}' ({pn or 'Name not provided'}).",
                evidence_value=pa
            ))
            
            # Check for collect-request fraud (e.g. asking user to pay when user expects to receive money)
            if am:
                score += 25
                signals.append(ScannerSignal(
                    name="Fixed Amount Debit Request",
                    type="FINANCIAL_DEBIT",
                    severity="MEDIUM",
                    description=f"Request will immediately debit ₹{am} from your linked bank account upon entering UPI PIN.",
                    evidence_value=f"₹{am}"
                ))
            
            # Anonymous / Personal VPA check
            if not mc and ("@paytm" in pa or "@ybl" in pa or "@okaxis" in pa):
                score += 15
                signals.append(ScannerSignal(
                    name="Peer-to-Peer VPA Target",
                    type="RECIPIENT_TYPE",
                    severity="LOW",
                    description="VPA belongs to an individual account rather than an authorized verified merchant code.",
                    evidence_value=pa
                ))

            explanation = (
                f"QR Code decoded to a UPI payment transfer targeting '{pa}'. "
                "WARNING: Entering your UPI PIN will DEDUCT money from your account. You NEVER need to enter a UPI PIN to receive money."
            )
            recommended_actions = [
                "If you are expecting to RECEIVE money (e.g. on OLX, marketplace), DO NOT SCAN or enter your PIN.",
                "Verify the payee name and VPA before authorizing payment.",
                "Never share your screen while scanning payment QR codes."
            ]
            limitations = [
                "UPI protocol cannot verify the real-world intent of the recipient account.",
                "Only the user's bank can process and authenticate transfers."
            ]
            
            return self.build_result(
                risk_score=score,
                confidence=confidence,
                signals=signals,
                explanation=explanation,
                recommended_actions=recommended_actions,
                limitations=limitations
            )

        # 2. Hyperlink URL check
        if payload.lower().startswith(("http://", "https://", "www.")):
            url_res = await self.url_scanner.analyze(payload)
            signals.append(ScannerSignal(
                name="Embedded Hyperlink Payload",
                type="PAYLOAD_TYPE",
                severity="MEDIUM" if url_res.risk_score >= 60 else "LOW",
                description=f"QR contains a web URL redirecting to {payload[:60]}...",
                evidence_value=payload[:80]
            ))
            signals.extend(url_res.signals)
            
            return self.build_result(
                risk_score=max(url_res.risk_score, 20),
                confidence=url_res.confidence,
                signals=signals,
                explanation=f"QR contains a web link: {url_res.explanation}",
                recommended_actions=url_res.recommended_actions,
                limitations=url_res.limitations,
                raw_input_reference=payload[:100]
            )

        # 3. Wi-Fi Configuration QR (WIFI:T:WPA;S:MyNetwork;P:MyPassword;;)
        if payload.upper().startswith("WIFI:"):
            return self.build_result(
                risk_score=20,
                confidence=0.92,
                signals=[ScannerSignal(
                    name="Wi-Fi Auto-Configuration",
                    type="PAYLOAD_TYPE",
                    severity="LOW",
                    description="QR contains automated Wi-Fi network credentials.",
                    evidence_value=payload[:40]
                )],
                explanation="QR automatically joins a local Wi-Fi access point.",
                recommended_actions=["Only connect if you trust the physical premises providing the QR code."],
                limitations=["Cannot determine if the Wi-Fi network performs rogue packet capture."],
                raw_input_reference=payload[:100]
            )

        # 4. Plain Text or Other
        return self.build_result(
            risk_score=5,
            confidence=0.95,
            signals=[ScannerSignal(
                name="Plain Text Payload",
                type="PAYLOAD_TYPE",
                severity="LOW",
                description="Contains plain text without embedded payment or link commands.",
                evidence_value=payload[:50]
            )],
            explanation="The scanned QR code is plain static text with no automatic system commands.",
            recommended_actions=["Review the decoded text before using it in other applications."],
            limitations=["Static text does not execute code unless copied into an untrusted evaluator."],
            raw_input_reference=payload[:100]
        )
