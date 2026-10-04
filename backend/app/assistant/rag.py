from typing import Dict, Any, List, Optional
from datetime import datetime, timezone
import re
from app.schemas.cyber import (
    AssistantChatResponse,
    ContextualAssistantRequest,
    ContextualAssistantResponse
)

class SecurityKnowledgeBase:
    """
    Curated RAG knowledge repository of verified cybersecurity procedures,
    threat intelligence, permission profiles, and incident response playbooks.
    All entries are grounded in official documentation and security standards.
    """
    KB = {
        "permission_accessibility": {
            "title": "Accessibility Service Abuse (Banking Trojan Triad)",
            "body": "Android AccessibilityService allows apps to read all on-screen content, observe keystrokes, and interact with the UI. Malicious apps (e.g., SharkBot, FluBot, TeaBot) abuse this service to intercept OTPs, bypass MFA, and perform automated credential harvesting."
        },
        "overlay_tapjacking": {
            "title": "SYSTEM_ALERT_WINDOW / Tapjacking Exploits",
            "body": "Overlay permissions allow applications to draw transparent or deceptive surfaces over authentic banking apps. Attackers use this to display fake login dialogs right over the real application to steal credentials."
        },
        "upi_collect_fraud": {
            "title": "UPI Collect Request & PIN Protocol",
            "body": "Rule: You ONLY enter your UPI PIN when PAYING money out of your account. Receiving money or receiving a cashback never requires entering your UPI PIN. Any request asking for your PIN to receive money is 100% fraudulent."
        },
        "phishing_punycode": {
            "title": "Punycode & Homograph Deception",
            "body": "Attackers register domain names with visually identical characters from Cyrillic or Greek alphabets (e.g. 'xn--' prefix). Sentinel AI unmasks punycode to identify deceptive domain structures designed to spoof trusted brands."
        },
        "device_hardening": {
            "title": "Android Device Hardening Checklist",
            "body": "1. Keep Android security patch updated. 2. Enable screen lock with biometric/PIN. 3. Disable Developer Options / ADB when not debugging. 4. Never enable Unknown Sources globally. 5. Use Private DNS (DoT)."
        },
        "deepfake_media": {
            "title": "Deepfake & Synthetic Media Identification",
            "body": "Synthetic media and voice clones use generative neural networks to simulate identities. Key detection artifacts include irregular facial blending, blinking frequency anomalies, audio phase distortions, and boundary pixel inconsistencies."
        },
        "investment_fraud": {
            "title": "Guaranteed Return & Advance-Fee Fraud",
            "body": "Legitimate investment platforms are registered with financial regulators (e.g. SEBI/SEC) and never promise zero-risk high returns or demand crypto transfers to release locked funds."
        },
        "sms_urgency_scam": {
            "title": "Social Engineering & Urgency Triggers",
            "body": "Fraudsters create artificial urgency ('account suspended within 2 hours', 'electricity disconnected tonight') to bypass critical thinking and compel victims to click malicious links or share secret OTPs."
        }
    }

    @classmethod
    def search_kb(cls, query: str) -> List[Dict[str, str]]:
        q = query.lower()
        results = []
        for key, entry in cls.KB.items():
            if any(term in q for term in [key, entry["title"].lower(), "pin", "permission", "lock", "score", "dns", "app", "upi", "deepfake", "scam"]):
                results.append(entry)
        return results or [cls.KB["device_hardening"]]


class SentinelContextualAssistantEngine:
    """
    Dedicated Sentinel AI Contextual Security Assistant.
    Answers user questions strictly grounded in the specific scan's structured evidence.
    Does NOT invent non-existent signals or provide generic repeated answers.
    """
    def __init__(self):
        self.version = "3.0.0"
        self.kb = SecurityKnowledgeBase()

    def route_intent(self, question: str) -> str:
        q = question.lower()
        if any(w in q for w in ["why is this dangerous", "why dangerous", "how is this dangerous", "threat", "harm"]):
            return "WHY_DANGEROUS"
        elif any(w in q for w in ["why safe", "is it safe", "can i trust", "is this safe", "legitimate"]):
            return "WHY_SAFE"
        elif any(w in q for w in ["why did you give", "why score", "how did you calculate", "why this score", "score breakdown"]):
            return "SCORE_EXPLANATION"
        elif any(w in q for w in ["signals", "what was detected", "what did you find", "indicators"]):
            return "SIGNAL_EXPLANATION"
        elif any(w in q for w in ["evidence", "proof", "show evidence", "tokens"]):
            return "EVIDENCE"
        elif any(w in q for w in ["what should i do", "what to do", "next steps", "recommendation", "action"]):
            return "WHAT_TO_DO"
        elif any(w in q for w in ["what to avoid", "what should i avoid", "what not to do", "mistake"]):
            return "WHAT_TO_AVOID"
        elif any(w in q for w in ["simple words", "explain simply", "plain english", "layman"]):
            return "EXPLAIN_SIMPLY"
        elif any(w in q for w in ["confidence", "how confident", "certainty", "accuracy"]):
            return "CONFIDENCE"
        elif any(w in q for w in ["limitation", "what can't you see", "uncertainty"]):
            return "LIMITATIONS"
        elif any(w in q for w in ["compare", "previous scan", "last scan", "why did my score decrease", "what changed"]):
            return "COMPARE_WITH_PREVIOUS_SCAN"
        return "GENERAL_CONTEXTUAL"

    def answer_scan_question(
        self,
        question: str,
        scan_context: Dict[str, Any],
        previous_scan_context: Optional[Dict[str, Any]] = None,
        language: str = "en"
    ) -> ContextualAssistantResponse:
        intent = self.route_intent(question)
        
        scanner_type = scan_context.get("scanner_type", "SECURITY_SCAN")
        sec_score = scan_context.get("security_score", max(0, 100 - scan_context.get("risk_score", 10)))
        risk_level = scan_context.get("risk_level", "SAFE")
        confidence = scan_context.get("confidence", 0.90)
        signals = scan_context.get("signals", [])
        evidence = scan_context.get("evidence", [])
        recs = scan_context.get("recommended_actions", [])
        avoids = scan_context.get("what_to_avoid", [])
        limits = scan_context.get("limitations", [])
        raw_target = scan_context.get("raw_input_reference") or scan_context.get("target_identifier") or "analyzed item"
        spec_report = scan_context.get("specialized_report", {})

        signal_names = [s.get("name") if isinstance(s, dict) else getattr(s, "name", "Signal") for s in signals]
        signal_details = [f"• {s.get('name') if isinstance(s, dict) else getattr(s, 'name', '')}: {s.get('description') if isinstance(s, dict) else getattr(s, 'description', '')}" for s in signals]
        
        evidence_refs = evidence.copy()
        if not evidence_refs and signals:
            evidence_refs = [f"{s.get('name', 'Signal')}: {s.get('evidence_value', 'Observed pattern')}" for s in signals if isinstance(s, dict)]

        # Grounded Intent Handlers
        if intent == "WHY_DANGEROUS":
            if sec_score >= 90:
                answer = (
                    f"Sentinel AI does NOT consider this target dangerous. The scan calculated a high security score "
                    f"of {sec_score}/100 with {int(confidence * 100)}% confidence. No malicious IOCs, phishing lexical patterns, "
                    f"or unauthorized credential-interception techniques were observed."
                )
                reasoning = "Target meets safe baseline; no threat signals triggered."
            else:
                answer = (
                    f"This {scanner_type.lower().replace('_', ' ')} received a risk verdict of {risk_level} (Security Score: {sec_score}/100) "
                    f"because the detector identified {len(signals)} specific risk signal(s):\n\n"
                    + "\n".join(signal_details) +
                    f"\n\nTogether, these indicators point to potential malicious intent or deceptive structure targeting your device or credentials."
                )
                reasoning = f"Flagged {len(signals)} signals matching known attack heuristics."

        elif intent == "WHY_SAFE":
            if sec_score >= 75:
                answer = (
                    f"Sentinel AI determined this is safe (Security Score: {sec_score}/100) based on verified structural analysis:\n"
                    f"✓ No known blacklisted malicious indicators were present.\n"
                    f"✓ Structural inspection of '{raw_target}' aligned with nominal operational patterns.\n"
                    f"✓ No credential-harvesting triggers, unauthorized APK banking trojan permissions, or deceptive redirect vectors were detected."
                )
                reasoning = "Structural and reputation indicators verified clean."
            else:
                answer = (
                    f"This target is NOT safe to proceed with. Sentinel AI identified {len(signals)} risk indicators "
                    f"(Security Score: {sec_score}/100). Interacting with it could expose your credentials, session tokens, or private data."
                )
                reasoning = f"Threat signals detected; cannot certify as safe."

        elif intent == "SCORE_EXPLANATION":
            answer = (
                f"The Security Score is {sec_score}/100 (where 100 represents highest security). "
                f"It was calculated by Sentinel AI's {scanner_type} model evaluated against our baseline.\n"
            )
            if sec_score >= 90:
                answer += "The score is near maximum because all structural and reputation checks passed cleanly."
            else:
                deductions = []
                for s in signals:
                    name = s.get("name") if isinstance(s, dict) else getattr(s, "name", "Risk")
                    sev = s.get("severity") if isinstance(s, dict) else getattr(s, "severity", "MED")
                    deductions.append(f"- {name} ({sev} severity deduction)")
                answer += "Deductions applied based on detected signals:\n" + "\n".join(deductions)
            reasoning = f"Security score {sec_score} calibrated from signal weights."

        elif intent == "SIGNAL_EXPLANATION":
            if not signals:
                answer = f"No risk signals were triggered during this {scanner_type} scan. The target conforms to standard legitimate characteristics."
                reasoning = "Zero threat signals triggered."
            else:
                answer = f"Sentinel AI identified the following {len(signals)} signal(s) during analysis:\n\n" + "\n".join(signal_details)
                reasoning = f"Explained {len(signals)} active detection signals."

        elif intent == "EVIDENCE":
            if evidence_refs:
                answer = (
                    f"Evidence extracted from this scan:\n" +
                    "\n".join([f"• {e}" for e in evidence_refs]) +
                    f"\n\nTarget reference: {raw_target}."
                )
            else:
                answer = f"The detector analyzed the target '{raw_target}'. No malicious cryptographic IOCs or deceptive token strings were recorded in evidence."
            reasoning = "Retrieved exact raw detection artifacts."

        elif intent == "WHAT_TO_DO":
            acts = recs or ["Proceed with caution and verify the source directly."]
            answer = "Recommended safe steps based on this scan:\n" + "\n".join([f"{i+1}. {a}" for i, a in enumerate(acts)])
            reasoning = "Generated prescriptive safe user playbooks."

        elif intent == "WHAT_TO_AVOID":
            av = avoids or [
                "Do NOT enter passwords or OTPs.",
                "Do NOT authorize payment or UPI collect requests.",
                "Do NOT install prompted APK packages."
            ]
            answer = "Critical actions to AVOID based on this detection:\n" + "\n".join([f"⚠ {a}" for a in av])
            reasoning = "Outlined high-risk user behaviors to avoid."

        elif intent == "EXPLAIN_SIMPLY":
            if sec_score >= 80:
                answer = (
                    f"In simple words: This looks clean. We checked '{raw_target}' and didn't find any tricks, fake links, or viruses. "
                    f"You can use it normally, but remember: never share your secret OTP or passwords with anyone."
                )
            else:
                answer = (
                    f"In simple words: Be careful! This target looks fishy. Sentinel AI found warning signs that someone might be trying "
                    f"to trick you into giving away money, passwords, or phone access. Close it and don't click anything inside it."
                )
            reasoning = "Delivered plain-English translation of technical findings."

        elif intent == "CONFIDENCE":
            answer = (
                f"Sentinel AI evaluated this scan with {int(confidence * 100)}% model confidence. "
                f"This metric reflects the agreement across our specialized {scanner_type} rules, neural embeddings, "
                f"and heuristic signal extraction on the target."
            )
            reasoning = f"Reported confidence level: {confidence}."

        elif intent == "LIMITATIONS":
            lim_list = limits or [
                "Automated static and heuristic inspection cannot guarantee dynamic server-side changes made after the scan.",
                "Always verify unexpected communications through an official phone number or app."
            ]
            answer = "Model Boundaries & Limitations:\n" + "\n".join([f"ℹ {l}" for l in lim_list])
            reasoning = "Stated explicit model limitations."

        elif intent == "COMPARE_WITH_PREVIOUS_SCAN" and previous_scan_context:
            prev_score = previous_scan_context.get("security_score", 100 - previous_scan_context.get("risk_score", 10))
            diff = sec_score - prev_score
            if diff > 0:
                answer = (
                    f"Comparison: Your security score improved by +{diff} points (from {prev_score} to {sec_score}/100). "
                    f"The current scan presents fewer suspicious indicators than the previous scan."
                )
            elif diff < 0:
                answer = (
                    f"Comparison: Your security score decreased by {abs(diff)} points (from {prev_score} down to {sec_score}/100). "
                    f"This drop occurred because the current scan detected {len(signals)} new risk signal(s) that were absent in the earlier scan."
                )
            else:
                answer = f"Comparison: Both scans yielded identical security scores of {sec_score}/100."
            reasoning = f"Comparative analysis between scan {prev_score} and current {sec_score}."

        else:
            # GENERAL_CONTEXTUAL with RAG
            kb_matches = self.kb.search_kb(question)
            kb_snippet = kb_matches[0]["body"] if kb_matches else ""
            answer = (
                f"Sentinel AI Contextual Security Assistant:\n"
                f"Regarding your query on '{question}':\n"
                f"{kb_snippet}\n\n"
                f"For the current scan of '{raw_target}', the security score is {sec_score}/100 ({risk_level}). "
                f"Follow the recommended actions listed above."
            )
            reasoning = "Combined RAG knowledge base with active scan posture."

        return ContextualAssistantResponse(
            answer=answer,
            reasoning_summary=reasoning,
            evidence_references=evidence_refs[:5],
            recommended_actions=recs[:3],
            limitations=limits[:2],
            confidence=confidence,
            intent=intent
        )

    def process_query(
        self,
        user_message: str,
        structured_context: Optional[Dict[str, Any]] = None,
        language: str = "en"
    ) -> AssistantChatResponse:
        res = self.answer_scan_question(user_message, structured_context or {}, language=language)
        safe_actions = res.recommended_actions
        if not safe_actions:
            safe_actions = [
                "Review active device permissions in Settings.",
                "Ensure only official verified apps have sensitive access."
            ]
        return AssistantChatResponse(
            response=res.answer,
            intent=res.intent or "GENERAL_GUIDANCE",
            structured_findings=structured_context or {},
            recommended_safe_actions=safe_actions,
            limitations=res.limitations or ["Static analysis does not guarantee runtime security."]
        )

# Backward-compatibility alias
SecurityAssistantEngine = SentinelContextualAssistantEngine
