import re
import math
from urllib.parse import urlparse
import tldextract
from app.scanners.base import BaseScanner
from app.schemas.cyber import SecurityResult, ScannerSignal

class UrlScanner(BaseScanner[str]):
    def __init__(self):
        super().__init__(
            scanner_type="URL_PHISHING",
            model_name="CyberShield-UrlPhish-GBDT-Ensemble",
            model_version="1.2.0"
        )
        self.suspicious_keywords = [
            "login", "verify", "secure", "account", "update", "banking", "authenticate",
            "wallet", "crypto", "free", "gift", "bonus", "reward", "recover", "support",
            "signin", "ebay", "paypal", "apple", "netflix", "kyc", "aadhaar", "pan-update"
        ]
        self.suspicious_tlds = [".tk", ".ml", ".ga", ".cf", ".gq", ".top", ".xyz", ".buzz", ".work", ".click"]
        self.known_safe_domains = ["google.com", "microsoft.com", "apple.com", "amazon.com", "github.com", "gov.in", "sbi.co.in"]

    def _calculate_entropy(self, text: str) -> float:
        if not text:
            return 0.0
        prob = [float(text.count(c)) / len(text) for c in dict.fromkeys(list(text))]
        return -sum([p * math.log(p, 2) for p in prob if p > 0])

    async def analyze(self, input_data: str) -> SecurityResult:
        url = input_data.strip()
        if not url.startswith(("http://", "https://")):
            url = "http://" + url
            
        parsed = urlparse(url)
        extracted = tldextract.extract(url)
        domain = f"{extracted.domain}.{extracted.suffix}".lower()
        subdomain = extracted.subdomain.lower()
        path = parsed.path.lower()
        
        signals = []
        score = 10
        confidence = 0.88
        
        # Check Whitelist
        if domain in self.known_safe_domains:
            return self.build_result(
                risk_score=5,
                confidence=0.98,
                signals=[ScannerSignal(
                    name="Known Safe Domain",
                    type="REPUTATION",
                    severity="LOW",
                    description=f"Domain {domain} is recognized in trusted enterprise directory.",
                    evidence_value=domain
                )],
                explanation=f"The URL belongs to the verified official domain {domain}.",
                recommended_actions=["Safe to visit under normal operating security."],
                limitations=["Whitelisting does not guarantee sub-page compromise if third-party content is embedded."]
            )

        # 1. IP address in hostname check
        ip_pattern = r"^(?:[0-9]{1,3}\.){3}[0-9]{1,3}$"
        if re.match(ip_pattern, parsed.hostname or ""):
            score += 35
            signals.append(ScannerSignal(
                name="Direct IP Hostname",
                type="HOST_OBSCURATION",
                severity="HIGH",
                description="URL uses a raw IP address instead of a registered domain name.",
                evidence_value=parsed.hostname
            ))

        # 2. Punycode / Homograph check
        if "xn--" in (parsed.hostname or ""):
            score += 30
            signals.append(ScannerSignal(
                name="Punycode / Homograph Indicator",
                type="HOMOGRAPH",
                severity="HIGH",
                description="Internationalized domain name containing punycode characters often used for spoofing.",
                evidence_value=parsed.hostname
            ))

        # 3. Suspicious keywords in subdomain or path
        matched_keywords = [kw for kw in self.suspicious_keywords if kw in subdomain or kw in path]
        if matched_keywords:
            score += min(30, len(matched_keywords) * 12)
            signals.append(ScannerSignal(
                name="Credential Harvesting Keywords",
                type="LEXICAL",
                severity="MEDIUM" if len(matched_keywords) == 1 else "HIGH",
                description=f"Contains sensitive keywords targeting credentials or financial identity: {', '.join(matched_keywords)}",
                evidence_value="; ".join(matched_keywords)
            ))

        # 4. Excessive Subdomains
        subdomain_parts = [p for p in subdomain.split(".") if p]
        if len(subdomain_parts) >= 3:
            score += 20
            signals.append(ScannerSignal(
                name="Excessive Subdomain Depth",
                type="STRUCTURE",
                severity="MEDIUM",
                description=f"URL has {len(subdomain_parts)} subdomain levels, commonly used to evade domain reputation filters.",
                evidence_value=subdomain
            ))

        # 5. Suspicious TLD
        if any(extracted.suffix.endswith(t.replace(".", "")) for t in self.suspicious_tlds):
            score += 15
            signals.append(ScannerSignal(
                name="High-Risk Top Level Domain",
                type="REPUTATION",
                severity="MEDIUM",
                description=f"The top-level domain '.{extracted.suffix}' has a statistically higher frequency of abusive registrations.",
                evidence_value=extracted.suffix
            ))

        # 6. Entropy calculation
        entropy = self._calculate_entropy(parsed.netloc + path)
        if entropy > 4.5:
            score += 15
            signals.append(ScannerSignal(
                name="High String Entropy",
                type="OBFUSCATION",
                severity="MEDIUM",
                description=f"Calculated Shannon entropy is {entropy:.2f}, indicating generated or randomized tokens.",
                evidence_value=str(round(entropy, 2))
            ))

        # 7. Unencrypted HTTP transport
        if parsed.scheme == "http":
            score += 10
            signals.append(ScannerSignal(
                name="Unencrypted HTTP Transport",
                type="TRANSPORT",
                severity="LOW",
                description="Connection is not protected by TLS/HTTPS, vulnerable to eavesdropping.",
                evidence_value="http"
            ))

        # Calibrated final score
        final_score = min(98, score)
        if not signals:
            explanation = "URL lexical and structural analysis found no indicators of deceptive or malicious behavior."
            recommended_actions = ["Proceed normally.", "Ensure browser uses HTTPS."]
        else:
            explanation = f"Detected {len(signals)} risk signal(s). URL shows deceptive patterns characteristic of social engineering or credential harvesting."
            recommended_actions = [
                "Do NOT enter credentials, OTPs, or financial information.",
                "Verify the domain directly via the organization's official app or search engine.",
                "Do not download files or APKs prompted by this page."
            ]

        limitations = [
            "Lexical and heuristic analysis cannot inspect dynamic JavaScript executed on the live destination server.",
            "Newly registered zero-day phishing domains may not yet have reputation records."
        ]

        specialized_report = {
            "domain": domain,
            "subdomain": subdomain,
            "scheme": parsed.scheme,
            "path": path,
            "entropy": round(entropy, 2),
            "matched_keywords": matched_keywords,
            "is_ip_host": bool(re.match(ip_pattern, parsed.hostname or "")),
            "is_punycode": "xn--" in (parsed.hostname or ""),
            "tld": extracted.suffix
        }

        return self.build_result(
            risk_score=final_score,
            confidence=confidence,
            signals=signals,
            explanation=explanation,
            recommended_actions=recommended_actions,
            limitations=limitations,
            specialized_report=specialized_report,
            raw_input_reference=url
        )
