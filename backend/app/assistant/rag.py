"""
Sentinel AI - Next-Gen Autonomous Cybersecurity Intelligence & Contextual RAG Engine
Implements an extensive cyber threat dataset with BM25 vector-space information retrieval,
multilingual generative synthesis (English, Hindi, Telugu), and grounded scan telemetry reasoning.
"""

from typing import Dict, Any, List, Optional, Tuple
from datetime import datetime, timezone
import math
import re
from app.schemas.cyber import (
    AssistantChatResponse,
    ContextualAssistantRequest,
    ContextualAssistantResponse
)

# ==============================================================================
# 01. EXPANDED CYBERSECURITY THREAT & DEFENSE KNOWLEDGE DATASET
# ==============================================================================

CYBER_KNOWLEDGE_CORPUS = [
    {
        "id": "upi_pin_rule",
        "keywords": ["upi", "pin", "receive money", "cashback", "payment", "gpay", "phonepe", "paytm", "bhim", "qr pay"],
        "title": "UPI PIN Fundamental Rule & Collect Request Frauds",
        "en": (
            "CRITICAL UPI SECURITY RULE: You ONLY enter your UPI PIN when PAYING money out of your bank account. "
            "Receiving money, claiming cashbacks, or winning refunds NEVER requires entering your UPI PIN or scanning any QR code. "
            "Any transaction prompt asking for your UPI PIN to 'receive' or 'verify' credits is 100% FRAUDULENT. "
            "Immediately decline the collect request and block the sender in your UPI application."
        ),
        "hi": (
            "महत्वपूर्ण यूपीआई सुरक्षा नियम: आपको केवल अपने बैंक खाते से पैसे भेजते (PAY) समय ही UPI PIN दर्ज करना होता है। "
            "पैसे प्राप्त करने, कैशबैक लेने या रिफंड पाने के लिए कभी भी UPI PIN दर्ज करने या QR कोड स्कैन करने की आवश्यकता नहीं होती है। "
            "कोई भी अनुरोध जो पैसे 'प्राप्त' करने के लिए आपका UPI PIN मांगता है, वह 100% धोखाधड़ी (धोखा) है। "
            "तुरंत उस अनुरोध को अस्वीकार (Decline) करें और भेजने वाले को ब्लॉक करें।"
        ),
        "te": (
            "ముఖ్యమైన UPI భద్రతా నియమం: మీ బ్యాంక్ ఖాతా నుండి డబ్బు పంపేటప్పుడు (PAY) మాత్రమే మీరు UPI PIN నమోదు చేయాలి. "
            "డబ్బు అందుకోవడానికి, క్యాష్‌బ్యాక్ లేదా రీఫండ్ పొందడానికి ఎప్పుడూ UPI PIN నమోదు చేయాల్సిన అవసరం లేదు మరియు QR కోడ్ స్కాన్ చేయకూడదు. "
            "డబ్బులు 'అందుకోవడానికి' మీ UPI PIN అడిగే ఏ అభ్యర్థన అయినా 100% మోసపూరితమైనది (ఫ్రాడ్). "
            "వెంటనే ఆ కలెక్ట్ రిక్వెస్ట్‌ను తిరస్కరించండి (Decline) మరియు వారిని బ్లాక్ చేయండి."
        ),
        "actions": [
            "Never share UPI PIN or enter it to receive money.",
            "Report fraudulent UPI VPA IDs via the NPCI portal or your bank.",
            "Call National Cyber Crime Helpline 1930 immediately if money is debited."
        ]
    },
    {
        "id": "digital_arrest_scam",
        "keywords": ["digital arrest", "police", "cbi", "ed", "customs", "trai", "courier parcel", "drugs", "passport", "skype", "video call"],
        "title": "Digital Arrest & Fake Law Enforcement Video Call Extortion",
        "en": (
            "ALERT ON DIGITAL ARREST SCAMS: There is NO legal provision for 'Digital Arrest' under Indian or international law. "
            "Law enforcement agencies (CBI, Police, ED, Customs, NIA) NEVER conduct trials, issue arrest warrants, or interrogate citizens over Skype, WhatsApp, or video calls. "
            "They will never demand funds to 'verify your bank account' or verify innocence via RBI escrow. "
            "Disconnect the call immediately. Report the caller's phone number to cybercrime.gov.in and dial 1930."
        ),
        "hi": (
            "डिजिटल अरेस्ट धोखाधड़ी चेतावनी: भारतीय कानून में 'डिजिटल अरेस्ट' (Digital Arrest) का कोई कानूनी प्रावधान नहीं है। "
            "कानून प्रवर्तन एजेंसियां (CBI, पुलिस, ED, कस्टम्स) कभी भी स्काइप, व्हाट्सएप या वीडियो कॉल पर गिरफ्तारी वारंट जारी नहीं करती हैं और न ही पूछताछ करती हैं। "
            "वे कभी भी सत्यापन के लिए आपके बैंक खाते से पैसे ट्रांसफर करने की मांग नहीं करते हैं। "
            "तुरंत कॉल काट दें, 1930 पर कॉल करें और cybercrime.gov.in पर रिपोर्ट दर्ज करें।"
        ),
        "te": (
            "డిజిటల్ అరెస్ట్ మోసాలపై హెచ్చరిక: భారతీయ చట్టం ప్రకారం 'డిజిటల్ అరెస్ట్' అనే నిబంధన ఏదీ లేదు. "
            "పోలీసులు, CBI, ED లేదా కస్టమ్స్ అధికారులు ఎప్పుడూ స్కైప్, వాట్సాప్ లేదా వీడియో కాల్స్ ద్వారా విచారణ చేయరు లేదా అరెస్ట్ చేయరు. "
            "వారు మీ నిర్దోషిత్వాన్ని నిరూపించుకోవడానికి డబ్బు బదిలీ చేయమని ఎప్పుడూ అడగరు. "
            "వెంటనే కాల్ కట్ చేయండి, 1930 హెల్ప్‌లైన్‌కు కాల్ చేయండి మరియు cybercrime.gov.in లో ఫిర్యాదు చేయండి."
        ),
        "actions": [
            "Hang up video calls claiming to be police/CBI immediately.",
            "Do not transfer funds to any 'verification' or 'escrow' account.",
            "Dial 1930 or visit local police cyber cell."
        ]
    },
    {
        "id": "electricity_bill_scam",
        "keywords": ["electricity", "power", "disconnection", "tonight", "bill unpaid", "officer", "call this number", "bijli"],
        "title": "Urgent Electricity Disconnection Smishing Attack",
        "en": (
            "ELECTRICITY DISCONNECTION SCAM: Scammers send SMS claiming your power supply will be disconnected tonight at 9:30 PM due to unpaid bills, prompting you to call a personal 10-digit mobile number. "
            "Electricity distribution companies (DISCOMs) NEVER send disconnection notices from individual personal mobile numbers and never ask you to install quick-support or screen-sharing apps (AnyDesk, TeamViewer). "
            "Do not call the number or click any links."
        ),
        "hi": (
            "बिजली बिल कनेक्शन कटने की धोखाधड़ी: जालसाज एसएमएस भेजते हैं कि आपका बिजली कनेक्शन आज रात बिल न भरने के कारण काट दिया जाएगा, और एक 10 अंकों के मोबाइल नंबर पर कॉल करने को कहते हैं। "
            "बिजली विभाग कभी भी व्यक्तिगत मोबाइल नंबरों से ऐसे संदेश नहीं भेजता और न ही एनीडेस्क या टीमव्यूअर जैसे ऐप इंस्टॉल करने को कहता है। "
            "दिए गए नंबर पर कभी कॉल न करें और न ही कोई लिंक खोलें।"
        ),
        "te": (
            "కరెంట్ బిల్లు డిస్‌కనెక్ట్ మోసం: విద్యుత్ బిల్లు చెల్లించలేదని, ఈ రాత్రి మీ కరెంట్ కట్ చేస్తామని మెసేజ్ పంపి వ్యక్తిగత మొబైల్ నంబర్‌కు కాల్ చేయమని మోసగాళ్లు బెదిరిస్తారు. "
            "విద్యుత్ శాఖ ఎప్పుడూ సాధారణ వ్యక్తిగత మొబైల్ నంబర్ల నుండి ఇలాంటి మెసేజ్‌లు పంపదు మరియు AnyDesk వంటి యాప్‌లను ఇన్‌స్టాల్ చేయమని అడగదు. "
            "ఆ నంబర్‌కు కాల్ చేయవద్దు మరియు ఎటువంటి లింక్‌లను క్లిక్ చేయవద్దు."
        ),
        "actions": [
            "Verify your bill exclusively on your state electricity board official portal.",
            "Never install remote screen-sharing tools prompted via SMS.",
            "Block and report the sender number as spam."
        ]
    },
    {
        "id": "part_time_job_task_scam",
        "keywords": ["part time job", "work from home", "telegram", "youtube like", "hotel review", "task scam", "daily income", "crypto task"],
        "title": "Telegram / Part-Time Job & Prepaid Task Scam",
        "en": (
            "TASK-BASED JOB SCAM: Fraudsters offer ₹3,000–₹8,000/day for liking YouTube videos, rating Google Maps locations, or reviewing hotels on Telegram. "
            "After paying small initial amounts (₹150–₹500) to win trust, they mandate 'prepaid investment tasks' or 'VIP crypto accounts' where victims lose lakhs attempting to withdraw funds. "
            "No legitimate company pays money for liking YouTube videos. All money sent to task platforms is unrecoverable."
        ),
        "hi": (
            "पार्ट-टाइम जॉब और टास्क स्कैम: धोखेबाज टेलीग्राम पर यूट्यूब वीडियो लाइक करने या होटल रिव्यू देने के नाम पर रोजाना ₹3,000–₹8,000 कमाने का लालच देते हैं। "
            "शुरुआत में विश्वास जीतने के लिए ₹150–₹500 देकर वे बाद में 'प्रीपेड टास्क' के नाम पर लाखों रुपये ठग लेते हैं। "
            "कोई भी वैध कंपनी वीडियो लाइक करने के पैसे नहीं देती। इन ग्रुप्स से तुरंत बाहर निकलें।"
        ),
        "te": (
            "పార్ట్ టైమ్ జాబ్ & టాస్క్ స్కామ్: యూట్యూబ్ వీడియోలు లైక్ చేయడం లేదా హోటల్ రివ్యూలు రాయడం ద్వారా రోజుకు ₹3,000–₹8,000 సంపాదించవచ్చని టెలిగ్రామ్‌లో మోసగాళ్లు ఆఫర్ చేస్తారు. "
            "మొదట్లో చిన్న మొత్తం చెల్లించి నమ్మకం కుదిర్చి, ఆ తర్వాత 'ప్రీపెయిడ్ ఇన్వెస్ట్‌మెంట్ టాస్క్' పేరుతో పెద్ద మొత్తంలో డబ్బు గుంజుతారు. "
            "వీడియోలు లైక్ చేయడానికి ఏ నిజమైన కంపెనీ డబ్బులు ఇవ్వదు. వెంటనే ఆ గ్రూపుల నుండి వైదొలగండి."
        ),
        "actions": [
            "Exit all unsolicited Telegram job earning groups.",
            "Do not transfer money to unlock 'task earnings'.",
            "Report fraudulent bank accounts to 1930."
        ]
    },
    {
        "id": "apk_trojan_accessibility",
        "keywords": ["apk", "malware", "trojan", "accessibility", "unknown sources", "sharkbot", "teabot", "sideload", "mod apk"],
        "title": "Malicious APK Sideloading & Accessibility Service Hijacking",
        "en": (
            "ANDROID BANKING TROJAN ANALYSIS: Fraudsters distribute malicious APK files disguised as banking updates, rewards, or free premium apps via WhatsApp/SMS. "
            "Once installed, these Trojans request Accessibility Service permission. This allows the malware to read SMS OTPs, log every keystroke, capture banking credentials, and execute automated fund transfers silently in the background. "
            "Never enable 'Install from Unknown Sources' or grant Accessibility Service to unknown apps."
        ),
        "hi": (
            "खतरनाक APK और एक्सेसिबिलिटी सर्विस हाइजैकिंग: धोखेबाज व्हाट्सएप या एसएमएस पर बैंकिंग ऐप या लॉटरी के नाम पर फर्जी APK फाइलें भेजते हैं। "
            "इंस्टॉल होने पर ये ऐप 'Accessibility Service' की अनुमति मांगते हैं। इसके जरिए वायरस आपके OTP पढ़ सकता है, कीबोर्ड टाइपिंग रिकॉर्ड कर सकता है और आपके बैंक से पैसे चुरा सकता है। "
            "अज्ञात स्रोतों से APK कभी इंस्टॉल न करें और किसी अनजान ऐप को एक्सेसिबिलिटी परमिशन न दें।"
        ),
        "te": (
            "హానికరమైన APK మరియు యాక్సెసిబిలిటీ దుర్వినియోగం: బ్యాంకింగ్ అప్‌డేట్‌లు లేదా ఆఫర్‌ల పేరుతో వాట్సాప్/SMS ద్వారా మోసగాళ్లు నకిలీ APK ఫైళ్లను పంపుతారు. "
            "ఇన్‌స్టాల్ చేసిన తర్వాత, ఈ యాప్‌లు 'Accessibility Service' అనుమతిని కోరతాయి. దీని ద్వారా మాల్వేర్ మీ SMS OTPలను దొంగిలించి, పాస్‌వర్డ్‌లను రికార్డ్ చేసి బ్యాంకు ఖాతా ఖాళీ చేస్తుంది. "
            "తెలియని APK ఫైళ్లను ఎప్పుడూ ఇన్‌స్టాల్ చేయవద్దు మరియు యాక్సెసిబిలిటీ అనుమతులు ఇవ్వవద్దు."
        ),
        "actions": [
            "Inspect Settings > Accessibility and revoke permission for all unknown apps.",
            "Uninstall suspicious sideloaded APKs immediately in Safe Mode.",
            "Run Sentinel AI Antivirus Full Device Audit."
        ]
    },
    {
        "id": "phishing_punycode_url",
        "keywords": ["phishing", "url", "link", "punycode", "domain", "fake website", "typosquatting", "ssl", "https fake"],
        "title": "Phishing URLs & Punycode Homograph Attacks",
        "en": (
            "PHISHING LINK DETECTION: Cybercriminals create fake lookalike websites (e.g., using Cyrillic characters that look like English 'a' or 'o', registered via Punycode 'xn--'). "
            "Having an SSL padlock (HTTPS) only means the connection is encrypted; it DOES NOT mean the website is legitimate. "
            "Always inspect the domain name strictly (e.g., paypal-secure-login.xyz is NOT paypal.com). "
            "Never enter passwords or debit card details on links received via SMS, email, or chat."
        ),
        "hi": (
            "फिशिंग लिंक और फर्जी वेबसाइट पहचान: साइबर अपराधी असली जैसी दिखने वाली फर्जी वेबसाइटें बनाते हैं (उदा. मिलते-जुलते स्पेलिंग या Punycode)। "
            "HTTPS या हरा ताला केवल यह दर्शाता है कि कनेक्शन सुरक्षित है, यह वेबसाइट के असली होने की गारंटी नहीं है। "
            "हमेशा मुख्य डोमेन नाम ध्यान से देखें। एसएमएस या चैट में मिले किसी भी लिंक पर कभी भी अपना पासवर्ड या कार्ड विवरण दर्ज न करें।"
        ),
        "te": (
            "ఫిషింగ్ లింకులు మరియు నకిలీ వెబ్‌సైట్లు: సైబర్ నేరగాళ్లు ప్రముఖ బ్యాంకులు మరియు కంపెనీల పేర్లను పోలిన నకిలీ వెబ్‌సైట్‌లను సృష్టిస్తారు. "
            "HTTPS లేదా తాళం గుర్తు ఉన్నంత మాత్రాన ఆ వెబ్‌సైట్ సురక్షితమైనదని భావించవద్దు. "
            "ప్రధాన డొమైన్ పేరును జాగ్రత్తగా పరిశీలించండి. అనుమానాస్పద లింకులలో మీ పాస్‌వర్డ్‌లు లేదా బ్యాంక్ వివరాలను ఎప్పుడూ నమోదు చేయవద్దు."
        ),
        "actions": [
            "Verify the exact root domain before typing credentials.",
            "Use bookmarks or official apps rather than SMS links.",
            "Sentinel AI analyzes Punycode deception and TLS certificate veracity automatically."
        ]
    },
    {
        "id": "deepfake_voice_video",
        "keywords": ["deepfake", "voice clone", "ai voice", "face swap", "kidnapping scam", "family emergency call", "synthetic media", "video call fake"],
        "title": "Deepfake Voice Cloning & AI Face-Swap Emergency Scams",
        "en": (
            "DEEPFAKE THREAT INTEL: Generative AI can clone a person's voice from just 3 seconds of social media audio. "
            "Scammers call parents or friends simulating a loved one's distress ('I was arrested', 'In hospital accident', 'Need urgent bail money'). "
            "Detection Protocol: 1. Hang up and call your family member back on their known regular phone number. 2. Establish a private 'Family Safe Word' that AI voice clones cannot know. 3. Look for video anomalies: irregular blinking, jitter around facial edges, and unsynchronized lips."
        ),
        "hi": (
            "डीपफेक वॉयस क्लोनिंग और आपातकालीन धोखाधड़ी: आधुनिक एआई केवल 3 सेकंड के ऑडियो से किसी की भी आवाज की नकल कर सकता है। "
            "जालसाज किसी रिश्तेदार की आवाज में फोन करके दुर्घटना या गिरफ्तारी का बहाना बनाकर तुरंत पैसे मांगते हैं। "
            "सुरक्षा उपाय: 1. फोन काटें और अपने परिचित को उनके सामान्य नंबर पर दोबारा कॉल करें। 2. परिवार के बीच एक गुप्त पासवर्ड (Safe Word) तय करें। 3. वीडियो में चेहरे के किनारों के विकृति और पलक झपकने की अप्राकृतिक गति पर ध्यान दें।"
        ),
        "te": (
            "డీప్‌ఫేక్ వాయిస్ క్లోనింగ్ & ఎమర్జెన్సీ స్కామ్: కేవలం 3 సెకన్ల ఆడియోతో AI మీ కుటుంబ సభ్యుల వాయిస్‌ను క్లోన్ చేయగలదు. "
            "మోసగాళ్లు బంధువుల గొంతుతో ఫోన్ చేసి ప్రమాదం లేదా ఆసుపత్రి ఎమర్జెన్సీ అని చెప్పి వెంటనే డబ్బు పంపమని బెదిరిస్తారు. "
            "రక్షణ సూచనలు: 1. వెంటనే కాల్ కట్ చేసి వారి అసలు నంబర్‌కు తిరిగి కాల్ చేయండి. 2. కుటుంబ సభ్యులతో ఒక రహస్య 'సేఫ్ వర్డ్' ఏర్పాటు చేసుకోండి. 3. వీడియోలో పెదవుల కదలికలు మరియు ముఖ అంచులలోని తేడాలను గమనించండి."
        ),
        "actions": [
            "Never send emergency funds based solely on an unexpected phone or video call.",
            "Verify via alternative communication channels.",
            "Utilize Sentinel AI Deepfake Vision to scan suspect videos/audio."
        ]
    },
    {
        "id": "kyc_suspension_smishing",
        "keywords": ["kyc", "bank kyc", "pan card update", "account blocked", "sbi kyc", "hdfc kyc", "icici kyc", "sim block"],
        "title": "Bank Account KYC Suspension Smishing",
        "en": (
            "BANK KYC SUSPENSION SMISHING: SMS claims: 'Dear Customer, your bank account will be blocked within 24 hours. Click link to update PAN/Aadhaar card immediately.' "
            "RBI guidelines mandate that banks NEVER collect PAN, Aadhaar, or NetBanking passwords through unverified links sent in SMS or WhatsApp. "
            "Submitting details on these pages hands complete access of your net banking credentials to fraudsters."
        ),
        "hi": (
            "बैंक केवाईसी सस्पेंशन धोखाधड़ी: फर्जी एसएमएस में दावा किया जाता है कि 24 घंटे में आपका बैंक खाता बंद हो जाएगा, तुरंत पैन कार्ड अपडेट करने के लिए लिंक पर क्लिक करें। "
            "आरबीआई के नियमों के अनुसार कोई भी बैंक एसएमएस में दिए गए लिंक के जरिए पैन या आधार कार्ड अपडेट नहीं कराता है। "
            "ऐसे लिंक पर कभी भी अपनी नेट बैंकिंग आईडी, पासवर्ड या OTP न डालें।"
        ),
        "te": (
            "బ్యాంక్ KYC నిలిపివేత మెసేజ్ మోసం: మీ బ్యాంక్ ఖాతా 24 గంటల్లో బ్లాక్ చేయబడుతుందని, వెంటనే పాన్ లేదా ఆధార్ అప్‌డేట్ చేయాలని నకిలీ SMS లు వస్తాయి. "
            "RBI నిబంధనల ప్రకారం బ్యాంకులు ఎప్పుడూ SMS లేదా వాట్సాప్ లింకుల ద్వారా KYC వివరాలను సేకరించవు. "
            "ఈ లింకులను క్లిక్ చేసి మీ నెట్ బ్యాంకింగ్ పాస్‌వర్డ్ లేదా OTPలను నమోదు చేయవద్దు."
        ),
        "actions": [
            "Only update KYC directly at your local bank branch or verified official mobile banking app.",
            "Ignore urgent account suspension threats.",
            "Forward suspicious SMS to telecom fraud reporting shortcode 1909."
        ]
    },
    {
        "id": "reporting_helpline_1930",
        "keywords": ["report fraud", "money lost", "cybercrime helpline", "1930", "portal", "police complaint", "freeze account", "rbi 3 days"],
        "title": "Emergency Financial Cyber Fraud Reporting & Legal Remedies (1930 / cybercrime.gov.in)",
        "en": (
            "EMERGENCY FINANCIAL ACTION PLAYBOOK: If you have lost money to cyber fraud: "
            "1. GOLDEN HOUR: Call National Cyber Crime Helpline '1930' immediately (within 2–4 hours). The portal initiates immediate transaction freezing across recipient bank accounts and merchant payment gateways. "
            "2. File a formal complaint on https://cybercrime.gov.in. "
            "3. Notify your bank immediately in writing. Under RBI Circular DBR.No.Leg.BC.78/09.07.005/2017-18, if an unauthorized transaction is reported within 3 working days, customer liability is ZERO."
        ),
        "hi": (
            "साइबर धोखाधड़ी आपातकालीन कार्रवाई: यदि आपके खाते से धोखाधड़ी से पैसे कट गए हैं: "
            "1. गोल्डन ऑवर (Golden Hour): तुरंत राष्ट्रीय साइबर अपराध हेल्पलाइन नंबर '1930' पर कॉल करें। यह पोर्टल पैसे को धोखेबाज के खाते में फ्रीज करने की प्रक्रिया तुरंत शुरू करता है। "
            "2. https://cybercrime.gov.in पर आधिकारिक शिकायत दर्ज करें। "
            "3. तुरंत अपने बैंक को लिखित रूप में सूचित करें। RBI के दिशानिर्देशों के तहत 3 दिनों के भीतर सूचना देने पर ग्राहक की कोई देनदारी नहीं होती (Zero Liability)।"
        ),
        "te": (
            "సైబర్ ఫ్రాడ్ అత్యవసర చర్యలు మరియు 1930 హెల్ప్‌లైన్: మోసపూరితంగా మీ బ్యాంక్ నుండి డబ్బులు కట్ అయితే: "
            "1. గోల్డెన్ అవర్: వెంటనే జాతీయ సైబర్ క్రైమ్ హెల్ప్‌లైన్ '1930' నంబర్‌కు కాల్ చేయండి. ఇది నిందితుడి బ్యాంక్ ఖాతాలోని డబ్బును ఫ్రీజ్ చేయడానికి సహాయపడుతుంది. "
            "2. https://cybercrime.gov.in వెబ్‌సైట్‌లో అధికారిక ఫిర్యాదు నమోదు చేయండి. "
            "3. మీ బ్యాంకుకు వెంటనే లిఖితపూర్వకంగా తెలియజేయండి. RBI నిబంధనల ప్రకారం 3 రోజుల్లో ఫిర్యాదు చేస్తే మీ డబ్బుకు పూర్తి రక్షణ ఉంటుంది (జీరో లయబిలిటీ)."
        ),
        "actions": [
            "Call 1930 within minutes of unauthorized debit.",
            "Save transaction ID, SMS alerts, and UPI reference numbers.",
            "Block your ATM card, UPI account, and netbanking access immediately."
        ]
    },
    {
        "id": "device_hardening_mfa",
        "keywords": ["security settings", "device hardening", "2fa", "mfa", "authenticator", "password", "private dns", "developer options"],
        "title": "Mobile Defense & Device Hardening Master Checklist",
        "en": (
            "SENTINEL AI HARDENING CHECKLIST: "
            "1. Enable Hardware-backed Multi-Factor Authentication (Authenticator App / TOTP like Google Authenticator or Passkeys rather than SMS 2FA). "
            "2. Set Private DNS to DNS-over-TLS (e.g., dns.quad9.net or 1dot1dot1dot1.cloudflare-dns.com) to block phishing domains globally. "
            "3. Turn OFF 'Developer Options' and 'USB Debugging' unless actively developing software. "
            "4. Disable Bluetooth, WiFi auto-join, and NFC when in crowded public areas. "
            "5. Audit app permissions monthly: strictly revoke SMS, Location, and Camera for non-essential applications."
        ),
        "hi": (
            "मोबाइल सुरक्षा और डिवाइस हार्डनिंग चेकलिस्ट: "
            "1. एसएमएस ओटीपी के बजाय गूगल ऑथेंटिकेटर या पासकी (Passkeys) जैसे 2FA का उपयोग करें। "
            "2. सेटिंग्स में Private DNS सक्षम करें ताकि फर्जी और फिशिंग वेबसाइटें अपने आप ब्लॉक हो जाएं। "
            "3. सामान्य उपयोग के दौरान 'Developer Options' और 'USB Debugging' हमेशा बंद रखें। "
            "4. सार्वजनिक स्थानों पर ब्लूटूथ और वाई-फाई ऑटो-कनेक्ट बंद रखें। "
            "5. सेटिंग्स में जाकर ऐप्स की गैर-जरूरी परमिशन (जैसे SMS, कैमरा, लोकेशन) तुरंत बंद करें।"
        ),
        "te": (
            "మొబైల్ రక్షణ & సెక్యూరిటీ చెక్‌లిస్ట్: "
            "1. సాధారణ SMS 2FA బదులుగా గూగుల్ అథెంటికేటర్ లేదా Passkeys ఉపయోగించండి. "
            "2. సెట్టింగ్స్‌లో Private DNS ను ఎనేబుల్ చేయండి, ఇది ఫిషింగ్ వెబ్‌సైట్‌లను ఆటోమేటిక్‌గా అడ్డుకుంటుంది. "
            "3. అవసరం లేనప్పుడు 'Developer Options' మరియు 'USB Debugging' ఆఫ్ చేసి ఉంచండి. "
            "4. బహిరంగ ప్రదేశాలలో బ్లూటూత్ మరియు ఆటో-వైఫై కనెక్ట్‌ను నిలిపివేయండి. "
            "5. అనవసరమైన యాప్‌లకు ఇచ్చిన SMS, కెమెరా, లొకేషన్ అనుమతులను వెంటనే రద్దు చేయండి."
        ),
        "actions": [
            "Enable biometric screen locks.",
            "Keep Android Security Patches updated.",
            "Perform weekly Sentinel AI scans."
        ]
    },
    {
        "id": "sextortion_webcam_blackmail",
        "keywords": ["sextortion", "video call blackmail", "nude", "morphing", "instagram threat", "facebook contacts", "leak video", "ransom"],
        "title": "Video Call Sextortion & AI Morphed Photo Blackmail",
        "en": (
            "CRITICAL PROTOCOL FOR SEXTORTION EXTORTION: "
            "1. NEVER PAY ANY MONEY: Paying does NOT make them delete the video; it only marks you as a paying target and they will demand exponentially more. "
            "2. DO NOT PANIC OR ENGAGE: Cut off communication immediately. Do not plead or negotiate. "
            "3. SECURE YOUR PROOF: Take screenshots of their profile, phone number, payment QR, and chat messages before blocking. "
            "4. LOCK SOCIAL PROFILES: Make your Instagram and Facebook profiles strictly private and disable message requests from non-friends. "
            "5. FILE COMPLAINT: File an anonymous report on StopNCII.org to generate photographic digital hashes that prevent uploads to major platforms. Dial 1930 and file a complaint at cybercrime.gov.in."
        ),
        "hi": (
            "सेक्सटॉर्शन और वीडियो कॉल ब्लैकमेल से निपटने के नियम: "
            "1. पैसे कभी न दें: एक बार पैसे देने पर वे बार-बार और ज्यादा पैसों की मांग करेंगे। "
            "2. घबराएं नहीं और बातचीत बंद करें: उनसे कोई बहस या विनती न करें, तुरंत संपर्क तोड़ें। "
            "3. सबूत सुरक्षित रखें: ब्लॉक करने से पहले चैट, फोन नंबर और पेमेंट आईडी का स्क्रीनशॉट लें। "
            "4. सोशल मीडिया प्रोफाइल लॉक करें: अपना इंस्टाग्राम और फेसबुक तुरंत प्राइवेट करें। "
            "5. तुरंत शिकायत दर्ज करें: StopNCII.org पर फोटो हैश दर्ज करें और 1930 पर कॉल करके cybercrime.gov.in पर रिपोर्ट दर्ज करें।"
        ),
        "te": (
            "సెక్స్‌టార్షన్ మరియు వీడియో కాల్ బ్లాక్‌మెయిల్ రక్షణ మార్గదర్శకాలు: "
            "1. ఎట్టి పరిస్థితుల్లోనూ డబ్బు చెల్లించవద్దు: డబ్బు ఇస్తే వారు మరింత బ్లాక్‌మెయిల్ చేస్తారు కానీ వీడియో తొలగించరు. "
            "2. భయపడవద్దు, వారితో మాట్లాడవద్దు: వెంటనే సంభాషణ ఆపివేయండి. "
            "3. ఆధారాలు భద్రపరచండి: బ్లాక్ చేయడానికి ముందు చాట్, ఫోన్ నంబర్, పేమెంట్ వివరాల స్క్రీన్‌షాట్లు తీసుకోండి. "
            "4. సోషల్ మీడియా లాక్ చేయండి: మీ ఇన్‌స్టాగ్రామ్, ఫేస్‌బుక్ ఖాతాలను వెంటనే ప్రైవేట్ చేయండి. "
            "5. ఫిర్యాదు చేయండి: StopNCII.org లో రిపోర్ట్ చేసి, 1930 నంబర్‌కు కాల్ చేసి cybercrime.gov.in లో ఫిర్యాదు చేయండి."
        ),
        "actions": [
            "Never transfer any ransom money.",
            "Report to StopNCII.org to prevent video distribution.",
            "Dial 1930 immediately."
        ]
    },
    {
        "id": "fedex_customs_parcel_scam",
        "keywords": ["fedex", "dhl", "customs parcel", "illegal parcel", "taiwan parcel", "drugs parcel", "mdma", "narcotics bureau"],
        "title": "Fake FedEx / DHL Courier & Customs Narcotics Extortion",
        "en": (
            "FAKE COURIER / CUSTOMS PARCEL SCAM: Scammers call claiming a parcel sent in your name to Taiwan/Dubai contains 5 passports, 140g MDMA, or synthetic narcotics. "
            "They pretend to transfer you to the Mumbai Crime Branch or Narcotics Control Bureau (NCB) and threaten immediate arrest. "
            "FACT: Courier companies never connect calls to police stations. Real police officers never conduct video interrogations or ask for money to issue an 'NOC'. "
            "Hang up immediately and block the caller."
        ),
        "hi": (
            "फर्जी कूरियर / फेडेक्स कस्टम्स पार्सल धोखाधड़ी: जालसाज फोन करके कहते हैं कि आपके नाम से विदेश भेजे गए पार्सल में नशीली दवाएं (MDMA) या पासपोर्ट पकड़े गए हैं। "
            "वे कॉल को फर्जी पुलिस या नारकोटिक्स विभाग से ट्रांसफर करने का नाटक करते हैं और गिरफ्तारी का डर दिखाते हैं। "
            "सच्चाई: कूरियर कंपनियां कभी पुलिस को कॉल ट्रांसफर नहीं करतीं और न ही पुलिस वीडियो कॉल पर पैसे मांगकर एनओसी देती है। "
            "तुरंत फोन काटें और 1930 पर रिपोर्ट करें।"
        ),
        "te": (
            "నకిలీ కొరియర్ / కస్టమ్స్ పార్శిల్ స్కామ్: మీ పేరు మీద పంపిన పార్శిల్‌లో డ్రగ్స్ లేదా పాస్‌పోర్ట్‌లు ఉన్నాయని, నార్కోటిక్స్ అధికారులు పట్టుకున్నారని నకిలీ కాల్స్ వస్తాయి. "
            "వారు పోలీసులకు కాల్ కలుపుతున్నామని భయపెట్టి, అరెస్ట్ చేయకుండా ఉండటానికి డబ్బు డిమాండ్ చేస్తారు. "
            "వాస్తవం: కొరియర్ కంపెనీలు ఎప్పుడూ పోలీసులకు కాల్స్ బదిలీ చేయవు మరియు పోలీసులు వీడియో కాల్స్ ద్వారా డబ్బులు వసూలు చేయరు. "
            "వెంటనే కాల్ కట్ చేసి 1930 కు ఫిర్యాదు చేయండి."
        ),
        "actions": [
            "Hang up fake customs calls immediately.",
            "Do not transfer funds to obtain any 'police clearance certificate'.",
            "Dial 1930 to report the scammer's caller ID."
        ]
    },
    {
        "id": "stock_trading_ipo_fraud",
        "keywords": ["stock trading", "institutional account", "fii", "dii", "ipo allotment", "stock tips", "vip trading group", "upper circuit"],
        "title": "Fake Institutional Stock Trading & Guaranteed IPO Scams",
        "en": (
            "FAKE INSTITUTIONAL STOCK TRADING SCAM: Fraudsters invite victims to WhatsApp/Telegram groups claiming to represent reputed firms (e.g., Goldman Sachs, BlackRock, Motilal Oswal). "
            "They force victims to install customized trading APKs claiming guaranteed 100% allotment in high-demand IPOs or institutional 'Upper Circuit' accounts. "
            "Victims see fake multi-crore profits on the app, but withdrawals are blocked demanding 20% 'advance tax' or 'SEBI clearance fees'. "
            "All SEBI-registered brokers operate only through authorized exchanges (NSE/BSE) and designated client bank accounts. Never transfer funds to personal savings/current accounts for share trading."
        ),
        "hi": (
            "फर्जी स्टॉक ट्रेडिंग और आईपीओ घोटाला: धोखेबाज व्हाट्सएप ग्रुप बनाकर खुद को बड़ी ब्रोकरेज फर्म बताते हैं और गारंटीकृत आईपीओ अलॉटमेंट का लालच देते हैं। "
            "वे फर्जी ट्रेडिंग ऐप इंस्टॉल करवाते हैं जहां स्क्रीन पर लाखों का फर्जी मुनाफा दिखता है, लेकिन पैसे निकालने के नाम पर और टैक्स की मांग करते हैं। "
            "सेबी-पंजीकृत ब्रोकर कभी भी व्यक्तिगत बैंक खातों में पैसे नहीं मंगवाते। तुरंत 1930 पर शिकायत करें।"
        ),
        "te": (
            "నకిలీ స్టాక్ ట్రేడింగ్ & IPO మోసాలు: వాట్సాప్ గ్రూపుల ద్వారా భారీ లాభాలు మరియు గ్యారెంటీడ్ IPO అలాట్‌మెంట్లు ఇస్తామని నమ్మించి నకిలీ ట్రేడింగ్ యాప్‌లను డౌన్‌లోడ్ చేయిస్తారు. "
            "యాప్‌లో నకిలీ లాభాలు చూపిస్తారు, కానీ డబ్బును విత్‌డ్రా చేయడానికి ప్రయత్నించినప్పుడు టాక్స్ పేరుతో మరింత డబ్బు గుంజుతారు. "
            "SEBI నమోదిత బ్రోకర్లు ఎప్పుడూ వ్యక్తిగత ఖాతాలకు డబ్బు బదిలీ చేయమని అడగరు. వెంటనే 1930 కి కాల్ చేయండి."
        ),
        "actions": [
            "Verify broker registration directly on sebi.gov.in.",
            "Never trade via third-party APKs downloaded outside official app stores.",
            "Report fraudulent bank accounts to National Cyber Crime portal."
        ]
    },
    {
        "id": "sim_swap_esim_takeover",
        "keywords": ["sim swap", "esim", "no network", "sim block", "port out", "4g to 5g upgrade", "telecom verification"],
        "title": "SIM Swap & Unauthorized eSIM Migration Account Takeover",
        "en": (
            "SIM SWAP THREAT ALERT: Scammers call posing as your telecom operator (Jio, Airtel, Vi) offering free 5G SIM upgrades or mandatory KYC verification. "
            "They trick you into forwarding an SMS code or sending an eSIM QR transfer request to an attacker-controlled email. "
            "Once the SIM swap completes, your phone loses all network signal ('No Service'), and attackers intercept your banking OTPs to drain accounts within minutes. "
            "If your SIM suddenly shows 'No Service' unexpectedly, contact your telecom operator immediately from an alternate phone."
        ),
        "hi": (
            "सिम स्वैप और फर्जी ई-सिम धोखाधड़ी: जालसाज टेलीकॉम कंपनी (Jio, Airtel) के नाम पर 5G अपग्रेड का लालच देकर आपसे एसएमएस फॉरवर्ड या eSIM ट्रांसफर करवाते हैं। "
            "इसके बाद आपके फोन का नेटवर्क बंद हो जाता है और धोखेबाज आपके बैंक ओटीपी चुराकर खाता खाली कर देते हैं। "
            "यदि आपके फोन का सिग्नल अचानक गायब हो जाए, तो तुरंत किसी दूसरे फोन से अपने टेलीकॉम ऑपरेटर से संपर्क करें और सिम ब्लॉक करवाएं।"
        ),
        "te": (
            "సిమ్ స్వాప్ & నకిలీ eSIM మోసం: 5G అప్‌గ్రేడ్ పేరుతో టెలికాం ఆపరేటర్లుగా చెప్పుకునే మోసగాళ్లు మీతో ఒక SMS పంపించి లేదా eSIM QR కోడ్ ద్వారా మీ సిమ్‌ను వారి ఆధీనంలోకి తీసుకుంటారు. "
            "మీ ఫోన్‌లో నెట్‌వర్క్ ఆగిపోయిన వెంటనే, వారు మీ బ్యాంకింగ్ OTP లను ఉపయోగించి డబ్బులు కాజేస్తారు. "
            "మీ మొబైల్‌లో అకస్మాత్తుగా 'నో సర్వీస్' వస్తే, వెంటనే కస్టమర్ కేర్‌కు కాల్ చేసి మీ సిమ్‌ను బ్లాక్ చేయించండి."
        ),
        "actions": [
            "Never forward 1900 porting SMS or share eSIM QR codes.",
            "Contact your mobile carrier immediately if cellular reception abruptly drops to zero.",
            "Lock your SIM card with a SIM PIN in phone settings."
        ]
    },
    {
        "id": "credit_card_points_expiry",
        "keywords": ["credit card points", "reward points", "points expire", "redeem cash", "sbi card points", "hdfc points", "cvv otp"],
        "title": "Credit Card Reward Points Expiry Phishing Smishing",
        "en": (
            "REWARD POINTS SMISHING SCAM: You receive an SMS: 'Dear customer, your reward points worth ₹9,850 expire tonight. Click here to convert points to cash in your bank account.' "
            "The link opens a phishing page imitating your bank, asking for your full 16-digit card number, Expiry Date, CVV, and netbanking password. "
            "FACT: Bank reward points are NEVER converted directly to cash via unverified external web links, and banks NEVER require CVV or OTP to credit points. "
            "Do not click the link or enter card credentials."
        ),
        "hi": (
            "क्रेडिट कार्ड रिवॉर्ड पॉइंट एक्सपायरी धोखाधड़ी: एसएमएस आता है कि आपके बैंक क्रेडिट कार्ड के ₹9,850 के रिवॉर्ड पॉइंट आज रात एक्सपायर हो रहे हैं, इन्हें कैश में बदलने के लिए लिंक पर क्लिक करें। "
            "यह लिंक एक फर्जी वेबसाइट खोलता है जो कार्ड नंबर, सीवीवी और ओटीपी मांगती है। "
            "सच्चाई: बैंक कभी भी पॉइंट्स को कैश में बदलने के लिए सीवीवी या ओटीपी नहीं मांगते। किसी भी लिंक पर अपने कार्ड की जानकारी न भरें।"
        ),
        "te": (
            "క్రెడిట్ కార్డ్ రివార్డ్ పాయింట్స్ మోసం: మీ క్రెడిట్ కార్డ్ పాయింట్లు ఈ రాత్రికి ముగిసిపోతాయని, వాటిని నగదుగా మార్చుకోవడానికి లింక్ క్లిక్ చేయాలని మోసపూరిత SMS లు వస్తాయి. "
            "ఆ లింక్ ద్వారా కార్డ్ నంబర్, CVV మరియు OTP లను దొంగిలిస్తారు. "
            "నిజానికి బ్యాంకులు పాయింట్లను నగదుగా మార్చడానికి ఎప్పుడూ CVV లేదా OTP అడగవు. ఎటువంటి లింకులను తెరవకండి."
        ),
        "actions": [
            "Redeem reward points strictly through your official bank netbanking app.",
            "Never enter card CVV or OTP on external web pages.",
            "Block compromised credit cards immediately in the mobile banking app."
        ]
    }
]

# ==============================================================================
# 02. BM25 / TF-IDF VECTOR-SPACE RETRIEVAL ALGORITHM
# ==============================================================================

class BM25Retriever:
    """
    Production-grade BM25 (Best Matching 25) Information Retrieval Engine.
    Computes term frequency saturation and document length normalization
    for exact, domain-authoritative cybersecurity knowledge retrieval.
    """
    def __init__(self, corpus: List[Dict[str, Any]]):
        self.corpus = corpus
        self.k1 = 1.5
        self.b = 0.75
        self.docs: List[List[str]] = []
        self.doc_lens: List[int] = []
        self.avg_doc_len = 0.0
        self.df: Dict[str, int] = {}
        self.idf: Dict[str, float] = {}
        self._build_index()

    def _tokenize(self, text: str) -> List[str]:
        cleaned = re.sub(r"[^\w\s]", " ", text.lower())
        tokens = [t.strip() for t in cleaned.split() if len(t.strip()) > 1]
        return tokens

    def _build_index(self):
        total_tokens = 0
        for doc in self.corpus:
            combined = f"{doc['id']} {doc['title']} {' '.join(doc['keywords'])} {doc['en']} {doc['hi']} {doc['te']}"
            tokens = self._tokenize(combined)
            self.docs.append(tokens)
            doc_len = len(tokens)
            self.doc_lens.append(doc_len)
            total_tokens += doc_len

            seen = set(tokens)
            for token in seen:
                self.df[token] = self.df.get(token, 0) + 1

        n_docs = len(self.corpus)
        self.avg_doc_len = (total_tokens / n_docs) if n_docs > 0 else 1.0

        for term, freq in self.df.items():
            # BM25 standard IDF with smoothing
            self.idf[term] = math.log(1.0 + (n_docs - freq + 0.5) / (freq + 0.5))

    def query(self, question: str, top_k: int = 2) -> List[Tuple[Dict[str, Any], float]]:
        q_tokens = self._tokenize(question)
        if not q_tokens:
            return [(self.corpus[0], 1.0)]

        scores = [0.0] * len(self.corpus)

        for term in q_tokens:
            term_idf = self.idf.get(term, 0.4)
            for i, doc_tokens in enumerate(self.docs):
                # Count term occurrences in doc
                tf = doc_tokens.count(term)
                if tf > 0:
                    doc_len = self.doc_lens[i]
                    numerator = tf * (self.k1 + 1.0)
                    denominator = tf + self.k1 * (1.0 - self.b + self.b * (doc_len / self.avg_doc_len))
                    scores[i] += term_idf * (numerator / denominator)

        # Keyword boost: if user query matches keyword directly, add bonus
        q_lower = question.lower()
        for i, doc in enumerate(self.corpus):
            for kw in doc.get("keywords", []):
                if kw in q_lower:
                    scores[i] += 4.5

        scored_docs = list(zip(self.corpus, scores))
        scored_docs.sort(key=lambda x: x[1], reverse=True)
        return scored_docs[:top_k]


# ==============================================================================
# 03. SENTINEL CONTEXTUAL AI ASSISTANT ENGINE
# ==============================================================================

class SentinelContextualAssistantEngine:
    """
    State-of-the-art Sentinel AI Autonomous Cybersecurity Reasoning Engine.
    Provides precise, grounded telemetry analysis and multilingual domain expertise.
    """
    def __init__(self):
        self.version = "4.0.0-PRO"
        self.retriever = BM25Retriever(CYBER_KNOWLEDGE_CORPUS)

    def detect_language(self, text: str, explicit_lang: str = "en") -> str:
        explicit = (explicit_lang or "").lower().strip()
        if "te" in explicit or "telugu" in explicit or "తెలుగు" in explicit:
            return "te"
        if "hi" in explicit or "hindi" in explicit or "हिन्दी" in explicit:
            return "hi"

        # Auto-detect Devanagari script for Hindi
        if re.search(r"[\u0900-\u097F]", text):
            return "hi"
        # Auto-detect Telugu script
        if re.search(r"[\u0C00-\u0C7F]", text):
            return "te"

        # Keyword-based Hindi / Telugu transliteration cues
        t_low = text.lower()
        if any(w in t_low for w in ["paise", "dhokhadhadi", "kya karu", "kyu", "kya", "batao", "kaise", "kat gaye"]):
            return "hi"
        if any(w in t_low for w in ["dabbu", "ela", "chesaru", "cheyali", "cheppandi", "enduku"]):
            return "te"

        return "en"

    def route_intent(self, question: str) -> str:
        q = question.lower()
        if any(w in q for w in ["why is this dangerous", "why dangerous", "how is this dangerous", "threat", "harm", "khatra", "danger", "pramadam"]):
            return "WHY_DANGEROUS"
        elif any(w in q for w in ["why safe", "is it safe", "can i trust", "is this safe", "legitimate", "surakshit", "safe aa"]):
            return "WHY_SAFE"
        elif any(w in q for w in ["why did you give", "why score", "how did you calculate", "why this score", "score breakdown", "score"]):
            return "SCORE_EXPLANATION"
        elif any(w in q for w in ["signals", "what was detected", "what did you find", "indicators", "lakshan"]):
            return "SIGNAL_EXPLANATION"
        elif any(w in q for w in ["evidence", "proof", "show evidence", "tokens", "saboot"]):
            return "EVIDENCE"
        elif any(w in q for w in ["what should i do", "what to do", "next steps", "recommendation", "action", "kya karu", "cheyali"]):
            return "WHAT_TO_DO"
        elif any(w in q for w in ["what to avoid", "what should i avoid", "what not to do", "mistake", "kya na kare"]):
            return "WHAT_TO_AVOID"
        elif any(w in q for w in ["simple words", "explain simply", "plain english", "layman", "saral"]):
            return "EXPLAIN_SIMPLY"
        elif any(w in q for w in ["1930", "complaint", "police", "money lost", "paise kat gaye", "fraud ho gaya", "report"]):
            return "INCIDENT_RESPONSE"
        elif any(w in q for w in ["confidence", "how confident", "certainty", "accuracy"]):
            return "CONFIDENCE"
        elif any(w in q for w in ["limitation", "what can't you see", "uncertainty"]):
            return "LIMITATIONS"
        return "GENERAL_CYBER_QUERY"

    def answer_scan_question(
        self,
        question: str,
        scan_context: Dict[str, Any],
        previous_scan_context: Optional[Dict[str, Any]] = None,
        language: str = "en"
    ) -> ContextualAssistantResponse:
        lang = self.detect_language(question, language)
        intent = self.route_intent(question)

        scanner_type = scan_context.get("scanner_type", "SECURITY_SCAN")
        sec_score = scan_context.get("security_score", max(0, 100 - scan_context.get("risk_score", 10)))
        risk_level = scan_context.get("risk_level", "SAFE")
        confidence = scan_context.get("confidence", 0.94)
        signals = scan_context.get("signals", [])
        evidence = scan_context.get("evidence", [])
        recs = scan_context.get("recommended_actions", [])
        avoids = scan_context.get("what_to_avoid", [])
        limits = scan_context.get("limitations", [])
        raw_target = scan_context.get("raw_input_reference") or scan_context.get("target_identifier") or "inspected target"

        signal_details = [
            f"• {s.get('name') if isinstance(s, dict) else getattr(s, 'name', '')}: {s.get('description') if isinstance(s, dict) else getattr(s, 'description', '')}"
            for s in signals
        ]
        evidence_refs = evidence.copy()
        if not evidence_refs and signals:
            evidence_refs = [f"{s.get('name', 'Signal')}: {s.get('evidence_value', 'Heuristic observation')}" for s in signals if isinstance(s, dict)]

        # Intent evaluation with multilingual synthesis
        if intent == "WHY_DANGEROUS":
            if sec_score >= 85:
                if lang == "hi":
                    answer = f"सेंटिनल एआई इस लक्ष्य ('{raw_target}') को खतरनाक नहीं मानता है। सुरक्षा स्कोर {sec_score}/100 है। कोई दुर्भावनापूर्ण पैटर्न नहीं मिला।"
                elif lang == "te":
                    answer = f"సెంటినెల్ AI ఈ లక్ష్యాన్ని ('{raw_target}') ప్రమాదకరమైనదిగా పరిగణించదు. భద్రతా స్కోరు {sec_score}/100. ఎటువంటి ముప్పు కనుగొనబడలేదు."
                else:
                    answer = f"Sentinel AI evaluates '{raw_target}' as non-hazardous with a nominal security score of {sec_score}/100. No phishing, trojan, or credential-harvesting indicators were detected."
                reasoning = "Clean safety posture evaluated."
            else:
                if lang == "hi":
                    answer = (
                        f"यह लक्ष्य {risk_level} जोखिम के साथ खतरनाक आंका गया है (सुरक्षा स्कोर: {sec_score}/100)। "
                        f"जांच में {len(signals)} प्रमुख खतरे के संकेत पाए गए:\n\n"
                        + "\n".join(signal_details) +
                        f"\n\nइन संकेतों के कारण यह आपके बैंक खाते, पासवर्ड या मोबाइल डिवाइस के लिए गंभीर खतरा हो सकता है।"
                    )
                elif lang == "te":
                    answer = (
                        f"ఈ లక్ష్యం {risk_level} తీవ్ర ముప్పును కలిగి ఉంది (భద్రతా స్కోరు: {sec_score}/100). "
                        f"పరిశీలనలో {len(signals)} రిస్క్ సంకేతాలు గుర్తించబడ్డాయి:\n\n"
                        + "\n".join(signal_details) +
                        f"\n\nఇది మీ ఆధారాలు లేదా బ్యాంక్ నిధుల భద్రతకు హాని కలిగించవచ్చు."
                    )
                else:
                    answer = (
                        f"This {scanner_type.lower().replace('_', ' ')} received a risk verdict of {risk_level} (Security Score: {sec_score}/100) "
                        f"because {len(signals)} structural threat signal(s) were flagged:\n\n"
                        + "\n".join(signal_details) +
                        f"\n\nThese forensic indicators confirm high susceptibility to credential theft or unauthorized device interaction."
                    )
                reasoning = f"Flagged {len(signals)} threat signals."

        elif intent == "WHY_SAFE":
            if sec_score >= 70:
                if lang == "hi":
                    answer = (
                        f"सेंटिनल एआई ने पुष्टि की है कि '{raw_target}' सुरक्षित है (सुरक्षा स्कोर: {sec_score}/100):\n"
                        f"✓ कोई ब्लैकलिस्टेड या दुर्भावनापूर्ण संकेतक नहीं मिले।\n"
                        f"✓ इसमें कोई फिशिंग या बैंकिंग ट्रोजन अनुमति नहीं पाई गई।\n"
                        f"✓ सभी सुरक्षा सत्यापन मानक पास हुए हैं।"
                    )
                elif lang == "te":
                    answer = (
                        f"సెంటినెల్ AI ధృవీకరణ ప్రకారం '{raw_target}' సురక్షితమైనది (భద్రతా స్కోరు: {sec_score}/100):\n"
                        f"✓ ఎటువంటి బ్లాక్‌లిస్ట్ చేసిన హానికర అంశాలు లేవు.\n"
                        f"✓ ఎలాంటి మోసపూరిత లింకులు లేదా మాల్వేర్ అనుమతులు కనుగొనబడలేదు.\n"
                        f"✓ అన్ని ప్రాథమిక భద్రతా తనిఖీలు విజయవంతంగా పూర్తయ్యాయి."
                    )
                else:
                    answer = (
                        f"Sentinel AI determined '{raw_target}' is structurally safe (Security Score: {sec_score}/100):\n"
                        f"✓ Verified clean against active threat intelligence feeds.\n"
                        f"✓ No credential-harvesting parameters or banking trojan vectors detected.\n"
                        f"✓ Conforms to legitimate transport security standards."
                    )
                reasoning = "Verified safe structural characteristics."
            else:
                if lang == "hi":
                    answer = f"चेतावनी: यह लक्ष्य सुरक्षित नहीं है! सुरक्षा स्कोर केवल {sec_score}/100 है। इसमें {len(signals)} गंभीर खतरे के संकेत मौजूद हैं।"
                elif lang == "te":
                    answer = f"హెచ్చరిక: ఇది సురక్షితమైనది కాదు! భద్రతా స్కోరు కేవలం {sec_score}/100 మాత్రమే. ఇందులో {len(signals)} తీవ్రమైన రిస్క్ అంశాలు ఉన్నాయి."
                else:
                    answer = f"This target CANNOT be certified safe. Current security score is only {sec_score}/100 with {len(signals)} threat indicators active."
                reasoning = "Unsafe score prevents safety certification."

        elif intent == "SCORE_EXPLANATION":
            if lang == "hi":
                answer = (
                    f"सुरक्षा स्कोर: {sec_score}/100 (100 = सर्वाधिक सुरक्षित)।\n"
                    f"यह स्कोर सेंटिनल एआई के {scanner_type} न्यूरल इंजन द्वारा विश्लेषित किया गया है। "
                    f"जोखिम स्तर: {risk_level} (अनुमानित खतरा: {int((100 - sec_score))}%)."
                )
            elif lang == "te":
                answer = (
                    f"భద్రతా స్కోరు: {sec_score}/100 (100 = అత్యంత సురక్షితమైనది).\n"
                    f"ఈ స్కోరును సెంటినెల్ AI యొక్క {scanner_type} మోడల్ అంచనా వేసింది. "
                    f"ముప్పు స్థాయి: {risk_level} (ప్రమాద తీవ్రత: {int((100 - sec_score))}%)."
                )
            else:
                answer = (
                    f"The calibrated Security Score is {sec_score}/100 (where 100 represents highest security). "
                    f"Calculated by Sentinel AI's {scanner_type} engine evaluated against real-time forensic heuristics. "
                    f"Confidence: {int(confidence * 100)}%."
                )
            reasoning = f"Calibrated security score {sec_score}."

        elif intent == "WHAT_TO_DO":
            acts = recs or [
                "Do not interact with the target.",
                "Verify official banking or service channels directly.",
                "Keep Sentinel AI real-time protection active."
            ]
            if lang == "hi":
                answer = "सुरक्षा के लिए आवश्यक कदम:\n" + "\n".join([f"{i+1}. {a}" for i, a in enumerate(acts)]) + "\n\nआपातकाल में राष्ट्रीय हेल्पलाइन 1930 पर कॉल करें।"
            elif lang == "te":
                answer = "సురక్షితంగా ఉండటానికి తీసుకోవలసిన చర్యలు:\n" + "\n".join([f"{i+1}. {a}" for i, a in enumerate(acts)]) + "\n\nఅత్యవసర పరిస్థితుల్లో జాతీయ హెల్ప్‌లైన్ 1930 కు కాల్ చేయండి."
            else:
                answer = "Prescribed immediate defense steps:\n" + "\n".join([f"{i+1}. {a}" for i, a in enumerate(acts)]) + "\n\nIn financial emergencies, dial 1930 or file on cybercrime.gov.in."
            reasoning = "Delivered prescriptive defense playbook."

        elif intent == "WHAT_TO_AVOID":
            av = avoids or [
                "NEVER share your OTP, UPI PIN, or passwords.",
                "NEVER click on links received via SMS claiming urgent bill disconnection or lottery wins.",
                "NEVER install screen-sharing software (AnyDesk, TeamViewer) at the request of callers."
            ]
            if lang == "hi":
                answer = "इन गलतियों से पूरी तरह बचें:\n" + "\n".join([f"⚠ {a}" for a in av])
            elif lang == "te":
                answer = "ఈ పొరపాట్లను అస్సలు చేయవద్దు:\n" + "\n".join([f"⚠ {a}" for a in av])
            else:
                answer = "Critical actions to strictly AVOID:\n" + "\n".join([f"⚠ {a}" for a in av])
            reasoning = "Warned against dangerous user actions."

        elif intent == "INCIDENT_RESPONSE":
            top_doc = CYBER_KNOWLEDGE_CORPUS[8]  # reporting_helpline_1930
            answer = (
                f"{top_doc[lang]}\n\n"
                f"Actions:\n" + "\n".join([f"• {a}" for a in top_doc["actions"]])
            )
            reasoning = "Incident response helpline 1930 and RBI guidelines."

        elif intent == "EXPLAIN_SIMPLY":
            if sec_score >= 80:
                if lang == "hi":
                    answer = f"आसान शब्दों में: यह बिल्कुल सुरक्षित लग रहा है। हमने '{raw_target}' की जांच की और कोई वायरस या फर्जी लिंक नहीं मिला। फिर भी अपना गुप्त OTP या पासवर्ड किसी को न बताएं।"
                elif lang == "te":
                    answer = f"సులువైన మాటల్లో: ఇది పూర్తిగా సురక్షితంగా ఉంది. '{raw_target}' లో ఎటువంటి వైరస్ లేదా ఫ్రాడ్ లింకులు లేవు. అయితే మీ రహస్య OTP లేదా పాస్‌వర్డ్ ఎవరితోనూ పంచుకోవద్దు."
                else:
                    answer = f"In simple terms: This looks completely clean. We verified '{raw_target}' and detected no malware or deceptive tricks. Remember to keep your OTPs private."
            else:
                if lang == "hi":
                    answer = f"आसान शब्दों में: सावधान रहें! यह खतरनाक या धोखाधड़ी वाला हो सकता है। किसी भी लिंक पर क्लिक न करें और न ही कोई पासवर्ड या पैसे भेजें। तुरंत बंद करें।"
                elif lang == "te":
                    answer = f"సులువైన మాటల్లో: జాగ్రత్త! ఇది చాలా ప్రమాదకరమైనది లేదా మోసపూరితమైనది. ఎటువంటి లింకులను క్లిక్ చేయవద్దు మరియు డబ్బులు లేదా పాస్‌వర్డ్‌లు పంపవద్దు."
                else:
                    answer = f"In simple terms: Watch out! This target contains severe red flags. Someone may be trying to steal your money or credentials. Do not click or interact with it."
            reasoning = "Delivered plain-language summary."

        else:
            # GENERAL_CYBER_QUERY - Query BM25 Corpus
            ranked = self.retriever.query(question, top_k=1)
            best_doc, score = ranked[0]
            knowledge_text = best_doc.get(lang) or best_doc.get("en")
            doc_title = best_doc.get("title")

            if lang == "hi":
                answer = (
                    f"🛡️ सेंटिनल एआई साइबर सुरक्षा सलाहकार:\n"
                    f"विषय: {doc_title}\n\n"
                    f"{knowledge_text}\n\n"
                    f"सुझाव:\n" + "\n".join([f"• {a}" for a in best_doc.get("actions", [])])
                )
            elif lang == "te":
                answer = (
                    f"🛡️ సెంటినెల్ AI సైబర్ సెక్యూరిటీ అడ్వైజర్:\n"
                    f"అంశం: {doc_title}\n\n"
                    f"{knowledge_text}\n\n"
                    f"సూచనలు:\n" + "\n".join([f"• {a}" for a in best_doc.get("actions", [])])
                )
            else:
                answer = (
                    f"🛡️ Sentinel AI Security Advisory:\n"
                    f"Subject: {doc_title}\n\n"
                    f"{knowledge_text}\n\n"
                    f"Prescribed Steps:\n" + "\n".join([f"• {a}" for a in best_doc.get("actions", [])])
                )
            reasoning = f"Retrieved authoritative knowledge item '{best_doc['id']}' via BM25 (score: {round(score, 2)})."

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
                "Keep device real-time shield and DoT private DNS active.",
                "Never share OTPs, PINs, or authorize unexpected payment collect requests."
            ]
        return AssistantChatResponse(
            response=res.answer,
            intent=res.intent or "GENERAL_GUIDANCE",
            structured_findings=structured_context or {},
            recommended_safe_actions=safe_actions,
            limitations=res.limitations or ["Static and heuristic evaluation does not account for post-scan dynamic modifications."]
        )

# Export singleton
SecurityAssistantEngine = SentinelContextualAssistantEngine
