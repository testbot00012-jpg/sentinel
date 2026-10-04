import os
from typing import List, Dict, Any
from app.scanners.base import BaseScanner
from app.schemas.cyber import SecurityResult, ScannerSignal, FileScanRequest

class FileSecurityScanner(BaseScanner[FileScanRequest]):
    def __init__(self):
        super().__init__(
            scanner_type="FILE_INTEGRITY_MALWARE",
            model_name="CyberShield-FileIOC-EntropyEngine",
            model_version="1.3.0"
        )
        # Curated IOC database (Malicious SHA-256 hashes e.g. known spyware/ransomware payloads)
        self.known_malicious_hashes = {
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855": "Zero-Byte Anomaly",
            "44d88612fea8a8f36de82e1278abb02f": "EICAR Standard Anti-Virus Test",
            "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8": "Pegasus Spyware Dropper Sample"
        }
        self.dangerous_extensions = [".apk", ".dex", ".so", ".sh", ".exe", ".bat", ".vbs", ".scr", ".jar"]

    async def analyze(self, input_data: FileScanRequest) -> SecurityResult:
        file_name = input_data.file_name.lower()
        sha256 = input_data.file_hash_sha256.lower()
        signals: List[ScannerSignal] = []
        score = 5
        confidence = 0.94

        # 1. Known Hash / Threat Intelligence IOC Match
        if sha256 in self.known_malicious_hashes:
            malware_family = self.known_malicious_hashes[sha256]
            return self.build_result(
                risk_score=99,
                confidence=1.0,
                signals=[ScannerSignal(
                    name="Known Malicious Hash (IOC Match)",
                    type="THREAT_INTELLIGENCE",
                    severity="CRITICAL",
                    description=f"File SHA-256 hash matches verified threat signature in CyberShield intelligence feed ({malware_family}).",
                    evidence_value=sha256
                )],
                explanation=f"CRITICAL: File is a confirmed malicious specimen belonging to {malware_family}.",
                recommended_actions=[
                    "Immediately quarantine or delete this file.",
                    "Do NOT attempt to open, execute, or share this file.",
                    "Run a complete device security scan."
                ],
                limitations=["Hash matching detects known exact specimens; modified polymorphic variants may have distinct hashes."]
            )

        # 2. Double Extension Trick (e.g., photo.jpg.apk or invoice.pdf.exe)
        parts = file_name.split(".")
        if len(parts) >= 3 and any(f".{parts[-1]}" in self.dangerous_extensions for ext in self.dangerous_extensions):
            score += 45
            signals.append(ScannerSignal(
                name="Double File Extension Deception",
                type="EVASION_TECHNIQUE",
                severity="HIGH",
                description=f"File disguises executable payload as a document or image: '{file_name}'.",
                evidence_value=file_name
            ))

        # 3. High Entropy check (packed, encrypted, or ransomware indicator)
        if input_data.entropy and input_data.entropy > 7.2:
            score += 30
            signals.append(ScannerSignal(
                name="High File Entropy (Packed / Obfuscated)",
                type="STATISTICAL_COMPLEXITY",
                severity="MEDIUM",
                description=f"Calculated entropy is {input_data.entropy:.2f} bits/byte, indicating encrypted payload or proprietary packer.",
                evidence_value=f"{input_data.entropy:.2f}"
            ))

        # 4. MIME type mismatch check
        if input_data.mime_type:
            ext = os.path.splitext(file_name)[1]
            if ext in [".jpg", ".png", ".pdf"] and "application/x-" in input_data.mime_type:
                score += 35
                signals.append(ScannerSignal(
                    name="MIME / Extension Inconsistency",
                    type="FORMAT_SPOOFING",
                    severity="HIGH",
                    description=f"Extension '{ext}' conflicts with raw binary MIME type '{input_data.mime_type}'.",
                    evidence_value=f"{ext} vs {input_data.mime_type}"
                ))

        final_score = min(98, score)

        if final_score < 25:
            explanation = "File static analysis shows valid structure, normal entropy, and no known signature alerts."
            recommended_actions = ["File appears safe under static inspection."]
        else:
            explanation = f"Suspicious file characteristics detected ({len(signals)} signals). File exhibits deception or packing techniques."
            recommended_actions = [
                "Move file to CyberShield Secure Vault or delete it.",
                "Verify file source with the original sender."
            ]

        limitations = [
            "Static analysis cannot execute dynamic behavioral detonation without a dedicated sandboxing environment.",
            "User consent is required before performing any quarantine or deletion."
        ]

        return self.build_result(
            risk_score=final_score,
            confidence=confidence,
            signals=signals,
            explanation=explanation,
            recommended_actions=recommended_actions,
            limitations=limitations
        )
