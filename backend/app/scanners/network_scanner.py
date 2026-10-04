from typing import List, Dict, Any
from app.scanners.base import BaseScanner
from app.schemas.cyber import SecurityResult, ScannerSignal, NetworkAuditRequest

class NetworkSecurityScanner(BaseScanner[NetworkAuditRequest]):
    def __init__(self):
        super().__init__(
            scanner_type="NETWORK_SECURITY_AUDIT",
            model_name="CyberShield-NetworkPosture-Audit",
            model_version="1.2.0"
        )
        self.known_secure_dns = ["1.1.1.1", "1.0.0.1", "8.8.8.8", "8.8.4.4", "9.9.9.9", "149.112.112.112"]

    async def analyze(self, input_data: NetworkAuditRequest) -> SecurityResult:
        signals: List[ScannerSignal] = []
        score = 5
        confidence = 0.90

        # 1. Open / Unencrypted Wi-Fi Check
        sec_type = (input_data.security_type or "").upper()
        if input_data.network_type == "WIFI" and ("OPEN" in sec_type or "NONE" in sec_type or "WEP" in sec_type):
            score += 50
            signals.append(ScannerSignal(
                name="Insecure Open Wi-Fi Network",
                type="TRANSPORT_EXPOSURE",
                severity="HIGH",
                description="Network lacks WPA2/WPA3 encryption. Anyone within physical radio range can eavesdrop unencrypted traffic.",
                evidence_value=f"Security: {sec_type}"
            ))

        # 2. VPN Status Evaluation
        if not input_data.is_vpn_active and input_data.network_type == "WIFI":
            score += 15
            signals.append(ScannerSignal(
                name="VPN Inactive on Wi-Fi",
                type="POSTURE_ADVISORY",
                severity="LOW",
                description="Device is connected to local Wi-Fi without an active encrypted VPN tunnel.",
                evidence_value="VPN: Inactive"
            ))
        elif input_data.is_vpn_active:
            score = max(0, score - 10)
            signals.append(ScannerSignal(
                name="Encrypted VPN Tunnel Active",
                type="SECURITY_CONTROL",
                severity="LOW",
                description="Active VPN encrypts outgoing traffic, mitigating local network eavesdropping.",
                evidence_value="VPN: Active"
            ))

        # 3. DNS Configuration Check
        rogue_dns = []
        for dns in input_data.dns_servers:
            # Check for non-standard local gateway DNS or ISP redirection
            if dns not in self.known_secure_dns and not dns.startswith(("192.168.", "10.", "172.")):
                rogue_dns.append(dns)
                
        if rogue_dns:
            score += 20
            signals.append(ScannerSignal(
                name="Untrusted DNS Resolver Configured",
                type="DNS_INTEGRITY",
                severity="MEDIUM",
                description=f"DNS requests are routed to third-party resolvers: {', '.join(rogue_dns)}",
                evidence_value=", ".join(rogue_dns)
            ))

        final_score = min(95, score)

        if final_score < 25:
            explanation = "Network connection is secured by modern encryption standards."
            recommended_actions = ["Network is secure for standard activities."]
        else:
            explanation = f"Network exposure detected ({len(signals)} risk signals). Public or unencrypted access points introduce risk of adversary-in-the-middle attacks."
            recommended_actions = [
                "Enable CyberShield VPN or avoid logging into sensitive banking portals on this network.",
                "Ensure Private DNS (DNS-over-TLS) is enabled in Android Settings.",
                "Disable auto-connect to open Wi-Fi networks in device settings."
            ]

        limitations = [
            "Android platform security rules prevent inspecting packet payloads of other applications.",
            "Assessment is based solely on observable network interface capabilities and DNS configurations."
        ]

        return self.build_result(
            risk_score=final_score,
            confidence=confidence,
            signals=signals,
            explanation=explanation,
            recommended_actions=recommended_actions,
            limitations=limitations
        )
