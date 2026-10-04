import re
from typing import Dict, List, Any
from app.scanners.base import BaseScanner
from app.schemas.cyber import SecurityResult, ScannerSignal, MessageScanRequest

class MessageScamScanner(BaseScanner[MessageScanRequest]):
    def __init__(self):
        super().__init__(
            scanner_type="SMS_MESSAGE_SCAM",
            model_name="CyberShield-MultiLang-ScamNLP",
            model_version="2.1.0"
        )
        # Multilingual scam intent lexicons (English, Hindi, Telugu)
        self.intent_patterns = {
            "URGENCY": {
                "patterns": [
                    r"(?i)\b(urgent|immediately|within 24 hours|account suspended|deactivated|expire today|last warning|blocked today)\b",
                    r"(तुरंत|जल्दी|अकाउंट बंद|अंतिम चेतावनी|24 घंटे के अंदर)",  # Hindi
                    r"(వెంటనే|ఖాతా నిలిపివేయబడుతుంది|చివరి హెచ్చరిక|24 గంటల్లో)" # Telugu
                ],
                "weight": 20,
                "desc": "Creates false urgency and panic to coerce hasty compliance."
            },
            "OTP_CREDENTIALS": {
                "patterns": [
                    r"(?i)\b(otp|one time password|cvv|pin|password|share otp|verify identity|netbanking credentials)\b",
                    r"(ओटीपी|पिन|पासवर्ड|सीवीवी|शेयर करें)",  # Hindi
                    r"(ఓటీపీ|పిన్|పాస్‌వర్డ్|సీవీవీ|షేర్ చేయండి)" # Telugu
                ],
                "weight": 35,
                "desc": "Explicitly requests secret authentication codes (OTP, PIN, or CVV)."
            },
            "FINANCIAL_IMPERSONATION": {
                "patterns": [
                    r"(?i)\b(sbi|hdfc|icici|axis|paytm|phonepe|gpay|bank|income tax|refund|electricity bill|epfo)\b",
                    r"(बैंक|आयकर|रिफंड|बिजली बिल|खाता नंबर)", # Hindi
                    r"(బ్యాంక్|రీఫండ్|కరెంట్ బిల్లు|ఆదాయపు పన్ను)" # Telugu
                ],
                "weight": 20,
                "desc": "Impersonates regulated banking institutions or government utilities."
            },
            "LOTTERY_REWARD": {
                "patterns": [
                    r"(?i)\b(won|winner|lottery|crore|lakhs|free gift|cash prize|claim reward|bonus|congratulations)\b",
                    r"(बधाई|लॉटरी|इनाम|लाख|मुफ्त उपहार|जीत लिया)", # Hindi
                    r"(అభినందనలు|లాటరీ|బహుమతి|లక్షలు|గెలుచుకున్నారు)" # Telugu
                ],
                "weight": 25,
                "desc": "Promises unrealistic lottery or cashback rewards to bait interaction."
            },
            "SUSPICIOUS_LINKS": {
                "patterns": [
                    r"(https?://\S+|bit\.ly/\S+|tinyurl\.com/\S+|is\.gd/\S+|ngrok\S+)",
                ],
                "weight": 25,
                "desc": "Contains an unverified shortened link or direct redirection URL."
            },
            "CALL_INSTRUCTION": {
                "patterns": [
                    r"(?i)\b(call immediately|contact manager|call on \+?\d{10}|helpline)\b",
                    r"(कॉल करें|हेल्पलाइन|संपर्क करें)", # Hindi
                    r"(కాల్ చేయండి|హెల్ప్‌లైన్|సంప్రదించండి)" # Telugu
                ],
                "weight": 15,
                "desc": "Instructs recipient to dial an unverified third-party phone number."
            }
        }

    async def analyze(self, input_data: MessageScanRequest) -> SecurityResult:
        text = input_data.message_text
        signals: List[ScannerSignal] = []
        accumulated_score = 5
        detected_categories = []
        
        for intent_name, config in self.intent_patterns.items():
            matched_terms = []
            for pat in config["patterns"]:
                matches = re.findall(pat, text, flags=re.IGNORECASE)
                if matches:
                    matched_terms.extend([m if isinstance(m, str) else m[0] for m in matches])
            if matched_terms:
                detected_categories.append(intent_name)
                accumulated_score += config["weight"]
                signals.append(ScannerSignal(
                    name=intent_name.replace("_", " ").title(),
                    type="NLP_SEMANTIC_INTENT",
                    severity="HIGH" if config["weight"] >= 25 else "MEDIUM",
                    description=config["desc"],
                    evidence_value="; ".join(set(matched_terms))[:100],
                    weight=float(config["weight"])
                ))

        # Check for correlated high-risk combination (e.g. Urgency + OTP or Impersonation + Link)
        if "OTP_CREDENTIALS" in detected_categories and "URGENCY" in detected_categories:
            accumulated_score += 20
            signals.append(ScannerSignal(
                name="Urgent Credential Intercept Pattern",
                type="CORRELATED_INTENT",
                severity="CRITICAL",
                description="Combination of panic-inducing language and OTP/credential demand is hallmark fraud behavior.",
                evidence_value="URGENCY + OTP"
            ))

        if "FINANCIAL_IMPERSONATION" in detected_categories and "SUSPICIOUS_LINKS" in detected_categories:
            accumulated_score += 15
            signals.append(ScannerSignal(
                name="Bank Phishing Link Delivery",
                type="CORRELATED_INTENT",
                severity="HIGH",
                description="Utility or banking impersonation paired with an unverified external hyperlink.",
                evidence_value="IMPERSONATION + LINK"
            ))

        final_score = min(99, accumulated_score)
        confidence = 0.92 if len(signals) >= 2 else (0.85 if len(signals) == 1 else 0.95)

        if final_score < 25:
            explanation = "Message analysis found no coercive, financial, or credential-harvesting indicators."
            recommended_actions = ["No action required.", "Always remain cautious of unsolicited messages."]
        else:
            explanation = f"Message classified as potential social engineering scam ({len(signals)} risk signals). Exhibits manipulative psychological triggers or credential extraction attempts."
            recommended_actions = [
                "NEVER share OTP, CVV, passwords, or UPI PIN with anyone, including bank staff.",
                "Do NOT click any link contained in this message.",
                "Block and report the sender number as spam.",
                "Call your bank's official helpline printed on your debit card if in doubt."
            ]

        limitations = [
            "NLP heuristics evaluate linguistic patterns; legitimate emergency notifications from trusted banks may occasionally share vocabulary with scams.",
            "Always verify with official banking channels directly."
        ]

        specialized_report = {
            "detected_categories": list(detected_categories),
            "language": input_data.language,
            "has_otp_request": "OTP_CREDENTIALS" in detected_categories,
            "has_urgency": "URGENCY" in detected_categories,
            "has_link": "SUSPICIOUS_LINKS" in detected_categories,
            "has_impersonation": "FINANCIAL_IMPERSONATION" in detected_categories
        }

        return self.build_result(
            risk_score=final_score,
            confidence=confidence,
            signals=signals,
            explanation=explanation,
            recommended_actions=recommended_actions,
            limitations=limitations,
            specialized_report=specialized_report,
            raw_input_reference=text[:120]
        )
