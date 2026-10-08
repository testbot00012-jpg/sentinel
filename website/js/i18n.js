/**
 * Sentinel AI - Comprehensive Multilingual Localization Engine (i18n)
 * Full translation support for English (en), हिन्दी (hi), and తెలుగు (te)
 * Applies to All Navigation, Headers, Dashboard Cards, Scanners, Reports & AI Assistant.
 */

const SENTINEL_I18N = {
  en: {
    // Navigation
    "Dashboard": "Dashboard",
    "Scan Options": "Scan Options ▾",
    "AI Assistant": "AI Assistant",
    "Audit History": "Audit History",
    "Account": "Account",
    "Sign Out": "🚪 Sign Out",
    "NEURAL SHIELD ARMED • v2.4": "NEURAL SHIELD ARMED • v2.4",
    "Guest Mode • Click to Sync": "Guest Mode • Click to Sync",
    "Device Synced • Live": "Device Synced • Live",

    // Dashboard Hero & Metrics
    "Security Posture & Telemetry": "Security Posture & Telemetry",
    "Real-time threat fusion engine synchronizing live telemetry across linked Android and Web sessions via Railway Cloud.": "Real-time threat fusion engine synchronizing live telemetry across linked Android and Web sessions via Railway Cloud.",
    "Connected Devices": "📱 Connected Devices",
    "Sync Now": "⚡ Sync Now",
    "LIVE COMPOSITE SCORE": "LIVE COMPOSITE SCORE",
    "CLEAN SECURITY BASELINE": "CLEAN SECURITY BASELINE",
    "CRITICAL THREATS DETECTED": "CRITICAL THREATS DETECTED",
    "SUSPICIOUS ACTIVITY FLAGGED": "SUSPICIOUS ACTIVITY FLAGGED",
    "No active threats detected. Run security scans to inspect targets and verify endpoint health.": "No active threats detected. Run security scans to inspect targets and verify endpoint health.",
    "Web Phishing Shield": "Web Phishing Shield",
    "Scam Message NLP": "Scam Message NLP",
    "Vision PRNU Forensics": "Vision PRNU Forensics",
    "Account & Device Health": "Account & Device Health",
    "Total Account Scans": "Total Account Scans",
    "Threats Blocked": "Threats Blocked",
    "Verified Clean Items": "Verified Clean Items",
    "Synced Devices": "Synced Devices",
    "Synchronized cross-device history": "Synchronized cross-device history",
    "Phishing & synthetic frauds intercepted": "Phishing & synthetic frauds intercepted",
    "Conforming to safety baselines": "Conforming to safety baselines",
    "Active session linked with Railway": "Active session linked with Railway",

    // Unified Scan Launcher
    "UNIFIED SECURITY SCANNER": "UNIFIED SECURITY SCANNER",
    "Launch Security Scan": "Launch Security Scan",
    "Click to select from 4 specialized AI detection engines: URL Phishing, Scam Messages, Deepfake Vision Forensics, or QR & UPI Fraud.": "Click to select from 4 specialized AI detection engines: URL Phishing, Scam Messages, Deepfake Vision Forensics, or QR & UPI Fraud.",
    "Start Scan": "⚡ Start Scan",

    // Feeds
    "Active Security Alerts": "Active Security Alerts",
    "No Active Security Alerts": "No Active Security Alerts",
    "All endpoints and inspected media conform to baseline safety guidelines.": "All endpoints and inspected media conform to baseline safety guidelines.",
    "Recent Account Activity": "Recent Account Activity",
    "View All History": "View All History →",

    // Scan Options Modal
    "SELECT ENGINE": "SELECT ENGINE",
    "Choose Security Scanner": "Choose Security Scanner",
    "Select an AI detection engine to inspect targets with real-time cross-device sync:": "Select an AI detection engine to inspect targets with real-time cross-device sync:",
    "URL Phishing Shield": "URL Phishing Shield",
    "Pre-flight DNS checks, Shannon entropy, homograph punycode & GBDT phishing detection.": "Pre-flight DNS checks, Shannon entropy, homograph punycode & GBDT phishing detection.",
    "Launch URL Scanner": "Launch URL Scanner",
    "SMS & Message Scam NLP": "SMS & Message Scam NLP",
    "Multilingual semantic analyzer for urgency traps, OTP interception, and lottery fraud.": "Multilingual semantic analyzer for urgency traps, OTP interception, and lottery fraud.",
    "Launch SMS Scanner": "Launch SMS Scanner",
    "Deepfake Vision Forensics": "Deepfake Vision Forensics",
    "Pixel PRNU sensor noise residual, dermis smoothing, and generative diffusion artifacts.": "Pixel PRNU sensor noise residual, dermis smoothing, and generative diffusion artifacts.",
    "Launch Vision AI": "Launch Vision AI",
    "QR & Payment Fraud Shield": "QR & Payment Fraud Shield",
    "Inspect payment QR codes, malicious collect requests, and deceptive merchant VPAs.": "Inspect payment QR codes, malicious collect requests, and deceptive merchant VPAs.",
    "Launch QR Scanner": "Launch QR Scanner",

    // URL Scanner Page
    "Verify Suspicious Website Link": "Verify Suspicious Website Link",
    "Inspect domain names, homograph punycode, SSL certificate validity, and phishing indicators:": "Inspect domain names, homograph punycode, SSL certificate validity, and phishing indicators:",
    "Scan URL Link": "⚡ Scan URL Link",
    "Quick Test Presets": "Quick Test Presets",
    "Phishing Bank Demo": "Phishing Bank Demo",
    "Legitimate Domain": "Legitimate Domain",
    "Non-Existent Domain": "Non-Existent Domain",

    // Message Scanner Page
    "Analyze Suspicious SMS or Chat": "Analyze Suspicious SMS or Chat",
    "Detect deceptive urgency, lottery claims, bank impersonation, and fraudulent payment prompts:": "Detect deceptive urgency, lottery claims, bank impersonation, and fraudulent payment prompts:",
    "Inspect Message Safety": "⚡ Inspect Message Safety",
    "Language Model:": "Language Model:",
    "English": "English",
    "Electricity Bill Threat": "Electricity Bill Threat",
    "Part-Time Job Scam": "Part-Time Job Scam",
    "Legitimate Bank Alert": "Legitimate Bank Alert",

    // Deepfake Vision Page
    "AI Media & Deepfake Detection": "AI Media & Deepfake Detection",
    "Analyze images for generative diffusion artifacts, GAN fingerprints, and PRNU sensor noise residuals:": "Analyze images for generative diffusion artifacts, GAN fingerprints, and PRNU sensor noise residuals:",
    "Upload Photo or Drag & Drop": "Upload Photo or Drag & Drop",
    "Drop an image here or click to browse files (PNG, JPG, WEBP)": "Drop an image here or click to browse files (PNG, JPG, WEBP)",
    "Analyze Media Forensics": "⚡ Analyze Media Forensics",
    "Sample Synthetic Face": "Sample Synthetic Face",
    "Authentic Camera Photo": "Authentic Camera Photo",

    // QR Guard Page
    "QR & Payment Safety Inspector": "QR & Payment Safety Inspector",
    "Upload payment QR screenshot or scan payload to detect fraudulent collect requests:": "Upload payment QR screenshot or scan payload to detect fraudulent collect requests:",
    "Upload QR / Payment Screenshot": "Upload QR / Payment Screenshot",
    "Drop payment screenshot or QR photo here to inspect": "Drop payment screenshot or QR photo here to inspect",
    "Inspect QR Safety": "⚡ Inspect QR Safety",
    "Verify QR Safety": "⚡ Verify QR Safety",

    // Scan Results & Verdicts
    "SAFE & VERIFIED DOMAIN": "SAFE & VERIFIED DOMAIN",
    "HIGH RISK PHISHING THREAT": "HIGH RISK PHISHING THREAT",
    "URL DOES NOT EXIST / UNREACHABLE": "URL DOES NOT EXIST / UNREACHABLE",
    "VERIFIED LEGITIMATE MESSAGE": "VERIFIED LEGITIMATE MESSAGE",
    "MALICIOUS SCAM OR SOCIAL ENGINEERING": "MALICIOUS SCAM OR SOCIAL ENGINEERING",
    "GENUINE / CAMERA AUTHENTIC": "GENUINE / CAMERA AUTHENTIC",
    "SYNTHETIC AI GENERATED / HIGH RISK": "SYNTHETIC AI GENERATED / HIGH RISK",
    "SAFE": "SAFE",
    "NOT SAFE / SCAM": "NOT SAFE / SCAM",
    "NOT SAFE / INSPECTION FAILED": "NOT SAFE / INSPECTION FAILED",
    "Security Score": "Security Score",
    "Threat Level": "Threat Level",
    "DNS Resolution": "DNS Resolution",
    "Safety Index": "Safety Index",
    "Threat Classification": "Threat Classification",
    "Forensic Score": "Forensic Score",
    "AI Probability": "AI Probability",
    "Sensor PRNU": "Sensor PRNU",
    "Extracted Forensic Signals": "Extracted Forensic Signals",
    "Linguistic & Semantic Triggers": "Linguistic & Semantic Triggers",
    "Vision Forensics Radar": "Vision Forensics Radar",
    "Safety Advisory": "Safety Advisory",
    "Critical Precautions": "Critical Precautions",
    "Recommended Security Actions": "Recommended Security Actions",
    "Ask AI Assistant": "💬 Ask AI Assistant",

    // Audit History Page
    "Platform Scan History": "Platform Scan History",
    "Cross-device telemetry log synchronized with Android mobile endpoints and web scanners:": "Cross-device telemetry log synchronized with Android mobile endpoints and web scanners:",
    "Refresh History": "🔄 Refresh History",
    "ALL": "ALL",
    "URL": "URL PHISHING",
    "MESSAGE": "SMS SCAM",
    "DEEPFAKE": "DEEPFAKE VISION",
    "QR": "QR & PAYMENT",
    "No Scan Records Found": "No Scan Records Found",
    "Run scans from the dashboard or your linked Android phone to see synced logs here.": "Run scans from the dashboard or your linked Android phone to see synced logs here.",
    "Time": "Time",
    "Device Source": "Device Source",
    "Scanner Module": "Scanner Module",
    "Target / Payload": "Target / Payload",

    // AI Assistant Page
    "Sentinel AI Cyber Defense Assistant": "Sentinel AI Cyber Defense Assistant",
    "Ask anything about cyber threats, digital arrest scams, suspicious APKs, UPI fraud, deepfakes, or legal remedies:": "Ask anything about cyber threats, digital arrest scams, suspicious APKs, UPI fraud, deepfakes, or legal remedies:",
    "Ask anything about cyber threats, digital arrest, UPI fraud, legal remedies (1930)...": "Ask anything about cyber threats, digital arrest, UPI fraud, legal remedies (1930)...",
    "Send Query": "⚡ Send Query",
    "Ask about Digital Arrest Scams": "🚨 Ask about Digital Arrest Scams",
    "Explain UPI PIN Rules": "💳 Explain UPI PIN Rules",
    "Report Cybercrime (1930)": "⚖️ Report Cybercrime (1930)",
    "Detect Sideloaded APK Malware": "📱 Detect Sideloaded APK Malware"
  },

  hi: {
    // Navigation
    "Dashboard": "डैशबोर्ड",
    "Scan Options": "स्कैन विकल्प ▾",
    "AI Assistant": "एआई सहायक",
    "Audit History": "ऑडिट इतिहास",
    "Account": "प्रोफ़ाइल",
    "Sign Out": "🚪 लॉग आउट",
    "NEURAL SHIELD ARMED • v2.4": "न्यूरल शील्ड सक्रिय • v2.4",
    "Guest Mode • Click to Sync": "अतिथि मोड • सिंक करने के लिए क्लिक करें",
    "Device Synced • Live": "डिवाइस सिंक • लाइव",

    // Dashboard Hero & Metrics
    "Security Posture & Telemetry": "सुरक्षा स्थिति और टेलीमेट्री",
    "Real-time threat fusion engine synchronizing live telemetry across linked Android and Web sessions via Railway Cloud.": "रीयल-टाइम सुरक्षा इंजन जो एंड्रॉइड और वेब के बीच लाइव टेलीमेट्री को स्वचालित रूप से सिंक करता है।",
    "Connected Devices": "📱 जुड़े हुए उपकरण",
    "Sync Now": "⚡ अभी सिंक करें",
    "LIVE COMPOSITE SCORE": "लाइव समग्र सुरक्षा स्कोर",
    "CLEAN SECURITY BASELINE": "सुरक्षित सुरक्षा आधार",
    "CRITICAL THREATS DETECTED": "गंभीर खतरे पाए गए",
    "SUSPICIOUS ACTIVITY FLAGGED": "संदिग्ध गतिविधि चिन्हित",
    "No active threats detected. Run security scans to inspect targets and verify endpoint health.": "कोई सक्रिय खतरा नहीं मिला। लक्ष्यों की जांच के लिए सुरक्षा स्कैन चलाएं।",
    "Web Phishing Shield": "वेब फ़िशिंग शील्ड",
    "Scam Message NLP": "स्कैम मैसेज एनएलपी",
    "Vision PRNU Forensics": "विज़न पीआरएनयू फोरेंसिक",
    "Account & Device Health": "खाता और डिवाइस स्वास्थ्य",
    "Total Account Scans": "कुल खाता स्कैन",
    "Threats Blocked": "रोके गए खतरे",
    "Verified Clean Items": "सत्यापित सुरक्षित आइटम",
    "Synced Devices": "सिंक किए गए डिवाइस",
    "Synchronized cross-device history": "क्रॉस-डिवाइस इतिहास सिंक",
    "Phishing & synthetic frauds intercepted": "फ़िशिंग और धोखाधड़ी रोकी गई",
    "Conforming to safety baselines": "सुरक्षा मानकों के अनुरूप",
    "Active session linked with Railway": "सक्रिय सत्र रेलवे क्लाउड से जुड़ा है",

    // Unified Scan Launcher
    "UNIFIED SECURITY SCANNER": "एकीकृत सुरक्षा स्कैनर",
    "Launch Security Scan": "सुरक्षा स्कैन शुरू करें",
    "Click to select from 4 specialized AI detection engines: URL Phishing, Scam Messages, Deepfake Vision Forensics, or QR & UPI Fraud.": "4 विशेष एआई इंजनों में से चुनें: यूआरएल फ़िशिंग, फर्जी संदेश, डीपफेक विज़न फोरेंसिक, या क्यूआर धोखाधड़ी।",
    "Start Scan": "⚡ स्कैन शुरू करें",

    // Feeds
    "Active Security Alerts": "सक्रिय सुरक्षा चेतावनियां",
    "No Active Security Alerts": "कोई सक्रिय सुरक्षा चेतावनी नहीं",
    "All endpoints and inspected media conform to baseline safety guidelines.": "सभी उपकरण और जांची गई सामग्री सुरक्षा दिशानिर्देशों के अनुरूप है।",
    "Recent Account Activity": "हाल की खाता गतिविधि",
    "View All History": "पूरा इतिहास देखें →",

    // Scan Options Modal
    "SELECT ENGINE": "इंजन चुनें",
    "Choose Security Scanner": "सुरक्षा स्कैनर चुनें",
    "Select an AI detection engine to inspect targets with real-time cross-device sync:": "वास्तविक समय क्रॉस-डिवाइस सिंक के साथ लक्ष्यों का निरीक्षण करने के लिए एक एआई इंजन चुनें:",
    "URL Phishing Shield": "यूआरएल फ़िशिंग शील्ड",
    "Pre-flight DNS checks, Shannon entropy, homograph punycode & GBDT phishing detection.": "डीएनएस सत्यापन, होमोग्राफ यूनिकोड और फ़िशिंग पहचान।",
    "Launch URL Scanner": "यूआरएल स्कैनर खोलें",
    "SMS & Message Scam NLP": "एसएमएस व संदेश धोखाधड़ी स्कैनर",
    "Multilingual semantic analyzer for urgency traps, OTP interception, and lottery fraud.": "ओटीपी चोरी, आपातकाल के झांसे और लॉटरी धोखाधड़ी के लिए बहुभाषी विश्लेषक।",
    "Launch SMS Scanner": "एसएमएस स्कैनर खोलें",
    "Deepfake Vision Forensics": "डीपफेक विज़न फोरेंसिक",
    "Pixel PRNU sensor noise residual, dermis smoothing, and generative diffusion artifacts.": "सेंसर नॉइज़, फेस स्मूथिंग और एआई जनरेटेड पिक्सेल पहचान।",
    "Launch Vision AI": "विज़न एआई खोलें",
    "QR & Payment Fraud Shield": "क्यूआर व भुगतान धोखाधड़ी शील्ड",
    "Inspect payment QR codes, malicious collect requests, and deceptive merchant VPAs.": "भुगतान क्यूआर कोड, फर्जी कलेक्ट रिक्वेस्ट और धोखाधड़ी वाले वीपीए की जांच करें।",
    "Launch QR Scanner": "क्यूआर स्कैनर खोलें",

    // URL Scanner Page
    "Verify Suspicious Website Link": "संदिग्ध वेबसाइट लिंक की जांच करें",
    "Inspect domain names, homograph punycode, SSL certificate validity, and phishing indicators:": "डोमेन नाम, होमोग्राफ प्यूनिकोड, एसएसएल वैधता और फ़िशिंग संकेतों की जांच करें:",
    "Scan URL Link": "⚡ यूआरएल स्कैन करें",
    "Quick Test Presets": "त्वरित परीक्षण उदाहरण",
    "Phishing Bank Demo": "फ़िशिंग बैंक डेमो",
    "Legitimate Domain": "असली वैध डोमेन",
    "Non-Existent Domain": "अस्तित्वहीन डोमेन",

    // Message Scanner Page
    "Analyze Suspicious SMS or Chat": "संदिग्ध एसएमएस या चैट का विश्लेषण करें",
    "Detect deceptive urgency, lottery claims, bank impersonation, and fraudulent payment prompts:": "धोखाधड़ी वाली तात्कालिकता, लॉटरी के दावे और फर्जी बैंक अलर्ट पहचानें:",
    "Inspect Message Safety": "⚡ संदेश सुरक्षा जांचें",
    "Language Model:": "भाषा मॉडल:",
    "English": "अंग्रेज़ी",
    "Electricity Bill Threat": "बिजली बिल धमकी",
    "Part-Time Job Scam": "पार्ट-टाइम जॉब स्कैम",
    "Legitimate Bank Alert": "वैध बैंक संदेश",

    // Deepfake Vision Page
    "AI Media & Deepfake Detection": "एआई मीडिया और डीपफेक पहचान",
    "Analyze images for generative diffusion artifacts, GAN fingerprints, and PRNU sensor noise residuals:": "एआई जनरेटेड बनावटी पिक्सेल, चेहरे के बदलाव और कैमरा सेंसर नॉइज़ की जांच करें:",
    "Upload Photo or Drag & Drop": "फोटो अपलोड करें या यहां खींचें",
    "Drop an image here or click to browse files (PNG, JPG, WEBP)": "इमेज यहां डालें या फाइल चुनने के लिए क्लिक करें (PNG, JPG, WEBP)",
    "Analyze Media Forensics": "⚡ मीडिया फोरेंसिक का विश्लेषण करें",
    "Sample Synthetic Face": "एआई जनरेटेड नमूना चेहरा",
    "Authentic Camera Photo": "वास्तविक कैमरे की फोटो",

    // QR Guard Page
    "QR & Payment Safety Inspector": "क्यूआर और भुगतान सुरक्षा निरीक्षक",
    "Upload payment QR screenshot or scan payload to detect fraudulent collect requests:": "भुगतान क्यूआर स्क्रीनशॉट अपलोड करें और धोखाधड़ी वाली कलेक्ट रिक्वेस्ट की जांच करें:",
    "Upload QR / Payment Screenshot": "क्यूआर या भुगतान स्क्रीनशॉट अपलोड करें",
    "Drop payment screenshot or QR photo here to inspect": "जांचने के लिए स्क्रीनशॉट या क्यूआर फोटो यहां डालें",
    "Inspect QR Safety": "⚡ क्यूआर सुरक्षा जांचें",
    "Verify QR Safety": "⚡ क्यूआर सुरक्षा सत्यापित करें",

    // Scan Results & Verdicts
    "SAFE & VERIFIED DOMAIN": "सुरक्षित और सत्यापित डोमेन",
    "HIGH RISK PHISHING THREAT": "उच्च जोखिम वाला फ़िशिंग खतरा",
    "URL DOES NOT EXIST / UNREACHABLE": "यूआरएल मौजूद नहीं है / दुर्गम",
    "VERIFIED LEGITIMATE MESSAGE": "सत्यापित वैध संदेश",
    "MALICIOUS SCAM OR SOCIAL ENGINEERING": "दुर्भावनापूर्ण धोखाधड़ी या सोशल इंजीनियरिंग",
    "GENUINE / CAMERA AUTHENTIC": "असली / कैमरे से लिया गया वास्तविक",
    "SYNTHETIC AI GENERATED / HIGH RISK": "सिंथेटिक एआई जनरेटेड / उच्च जोखिम",
    "SAFE": "सुरक्षित",
    "NOT SAFE / SCAM": "असुरक्षित / धोखाधड़ी (स्कैम)",
    "NOT SAFE / INSPECTION FAILED": "असुरक्षित / जांच विफल",
    "Security Score": "सुरक्षा स्कोर",
    "Threat Level": "खतरा स्तर",
    "DNS Resolution": "डीएनएस रिज़ॉल्यूशन",
    "Safety Index": "सुरक्षा सूचकांक",
    "Threat Classification": "खतरा वर्गीकरण",
    "Forensic Score": "फोरेंसिक स्कोर",
    "AI Probability": "एआई संभावना",
    "Sensor PRNU": "सेंसर पीआरएनयू",
    "Extracted Forensic Signals": "निकाले गए फोरेंसिक संकेत",
    "Linguistic & Semantic Triggers": "भाषाई और अर्थ संबंधी संकेत",
    "Vision Forensics Radar": "विज़न फोरेंसिक रडार",
    "Safety Advisory": "सुरक्षा सलाह",
    "Critical Precautions": "महत्वपूर्ण सावधानियां",
    "Recommended Security Actions": "अनुशंसित सुरक्षा कदम",
    "Ask AI Assistant": "💬 एआई सहायक से पूछें",

    // Audit History Page
    "Platform Scan History": "प्लेटफ़ॉर्म स्कैन इतिहास",
    "Cross-device telemetry log synchronized with Android mobile endpoints and web scanners:": "एंड्रॉइड मोबाइल और वेब स्कैनर के साथ सिंक किया गया इतिहास लॉग:",
    "Refresh History": "🔄 इतिहास रीफ़्रेश करें",
    "ALL": "सभी",
    "URL": "यूआरएल फ़िशिंग",
    "MESSAGE": "एसएमएस धोखाधड़ी",
    "DEEPFAKE": "डीपफेक विज़न",
    "QR": "क्यूआर व भुगतान",
    "No Scan Records Found": "कोई स्कैन रिकॉर्ड नहीं मिला",
    "Run scans from the dashboard or your linked Android phone to see synced logs here.": "सिंक किए गए लॉग देखने के लिए डैशबोर्ड या अपने जुड़े हुए फोन से स्कैन करें।",
    "Time": "समय",
    "Device Source": "उपकरण",
    "Scanner Module": "स्कैनर प्रकार",
    "Target / Payload": "लक्ष्य",

    // AI Assistant Page
    "Sentinel AI Cyber Defense Assistant": "सेंटिनल एआई साइबर रक्षा सहायक",
    "Ask anything about cyber threats, digital arrest scams, suspicious APKs, UPI fraud, deepfakes, or legal remedies:": "साइबर अपराध, डिजिटल अरेस्ट, यूपीआई धोखाधड़ी, खतरनाक एपीके, या कानूनी सहायता (1930) के बारे में कुछ भी पूछें:",
    "Ask anything about cyber threats, digital arrest, UPI fraud, legal remedies (1930)...": "साइबर खतरों, डिजिटल अरेस्ट, यूपीआई धोखाधड़ी या हेल्पलाइन (1930) के बारे में कुछ भी पूछें...",
    "Send Query": "⚡ सवाल भेजें",
    "Ask about Digital Arrest Scams": "🚨 डिजिटल अरेस्ट स्कैम के बारे में पूछें",
    "Explain UPI PIN Rules": "💳 यूपीआई पिन के नियम समझें",
    "Report Cybercrime (1930)": "⚖️ साइबर अपराध की रिपोर्ट (1930)",
    "Detect Sideloaded APK Malware": "📱 फर्जी एपीके मैलवेयर पहचानें"
  },

  te: {
    // Navigation
    "Dashboard": "డాష్‌బోర్డ్",
    "Scan Options": "స్కాన్ ఎంపికలు ▾",
    "AI Assistant": "AI అసిస్టెంట్",
    "Audit History": "ఆడిట్ చరిత్ర",
    "Account": "ఖాతా",
    "Sign Out": "🚪 లాగ్ అవుట్",
    "NEURAL SHIELD ARMED • v2.4": "న్యూరల్ షీల్డ్ యాక్టివ్ • v2.4",
    "Guest Mode • Click to Sync": "గెస్ట్ మోడ్ • సింక్ చేయడానికి క్లిక్ చేయండి",
    "Device Synced • Live": "పరికరం సింక్ అయింది • లైవ్",

    // Dashboard Hero & Metrics
    "Security Posture & Telemetry": "భద్రతా స్థితి & టెలిమెట్రీ",
    "Real-time threat fusion engine synchronizing live telemetry across linked Android and Web sessions via Railway Cloud.": "ఆండ్రాయిడ్ మరియు వెబ్ మధ్య లైవ్ టెలిమెట్రీని స్వయంచాలకంగా సింక్ చేసే నిజ-సమయ భద్రతా వ్యవస్థ.",
    "Connected Devices": "📱 కనెక్ట్ చేసిన పరికరాలు",
    "Sync Now": "⚡ ఇప్పుడే సింక్ చేయండి",
    "LIVE COMPOSITE SCORE": "లైవ్ కాంపోజిట్ సెక్యూరిటీ స్కోర్",
    "CLEAN SECURITY BASELINE": "క్లీన్ సెక్యూరిటీ బేస్‌లైన్",
    "CRITICAL THREATS DETECTED": "తీవ్రమైన ముప్పులు గుర్తించబడ్డాయి",
    "SUSPICIOUS ACTIVITY FLAGGED": "అనుమానాస్పద కార్యాచరణ",
    "No active threats detected. Run security scans to inspect targets and verify endpoint health.": "ఎటువంటి ముప్పులు గుర్తించబడలేదు. లక్ష్యాలను పరీక్షించడానికి భద్రతా స్కాన్‌ను ప్రారంభించండి.",
    "Web Phishing Shield": "వెబ్ ఫిషింగ్ రక్షణ",
    "Scam Message NLP": "స్కామ్ మెసేజ్ NLP",
    "Vision PRNU Forensics": "విజన్ PRNU ఫోరెన్సిక్స్",
    "Account & Device Health": "ఖాతా & పరికర ఆరోగ్యం",
    "Total Account Scans": "మొత్తం ఖాతా స్కాన్‌లు",
    "Threats Blocked": "అడ్డుకున్న ముప్పులు",
    "Verified Clean Items": "ధృవీకరించిన సురక్షిత అంశాలు",
    "Synced Devices": "సింక్ చేసిన పరికరాలు",
    "Synchronized cross-device history": "పరికరాల మధ్య సింక్ చేసిన చరిత్ర",
    "Phishing & synthetic frauds intercepted": "ఫిషింగ్ & మోసాలు అడ్డుకోబడ్డాయి",
    "Conforming to safety baselines": "భద్రతా ప్రమాణాలకు అనుగుణంగా ఉంది",
    "Active session linked with Railway": "రైల్వే క్లౌడ్‌తో అనుసంధానించబడింది",

    // Unified Scan Launcher
    "UNIFIED SECURITY SCANNER": "ఏకీకృత సెక్యూరిటీ స్కానర్",
    "Launch Security Scan": "సెక్యూరిటీ స్కాన్ ప్రారంభించండి",
    "Click to select from 4 specialized AI detection engines: URL Phishing, Scam Messages, Deepfake Vision Forensics, or QR & UPI Fraud.": "4 ప్రత్యేక AI ఇంజిన్‌ల నుండి ఎంచుకోండి: URL ఫిషింగ్, స్కామ్ మెసేజ్‌లు, డీప్‌ఫేక్ విజన్, లేదా QR మోసాలు.",
    "Start Scan": "⚡ స్కాన్ ప్రారంభించండి",

    // Feeds
    "Active Security Alerts": "యాక్టివ్ సెక్యూరిటీ అలర్ట్‌లు",
    "No Active Security Alerts": "ఎటువంటి సెక్యూరిటీ అలర్ట్‌లు లేవు",
    "All endpoints and inspected media conform to baseline safety guidelines.": "పరికరాలు మరియు పరిశీలించిన మీడియా భద్రతా ప్రమాణాలకు అనుగుణంగా ఉన్నాయి.",
    "Recent Account Activity": "ఇటీవలి ఖాతా కార్యాచరణ",
    "View All History": "మొత్తం చరిత్రను చూడండి →",

    // Scan Options Modal
    "SELECT ENGINE": "ఇంజిన్‌ను ఎంచుకోండి",
    "Choose Security Scanner": "సెక్యూరిటీ స్కానర్‌ని ఎంచుకోండి",
    "Select an AI detection engine to inspect targets with real-time cross-device sync:": "రియల్ టైమ్ క్రాస్-డివైస్ సింక్‌తో లక్ష్యాలను పరిశీలించడానికి AI ఇంజిన్‌ను ఎంచుకోండి:",
    "URL Phishing Shield": "URL ఫిషింగ్ రక్షణ",
    "Pre-flight DNS checks, Shannon entropy, homograph punycode & GBDT phishing detection.": "DNS ధృవీకరణ, హోమోగ్రాఫ్ మరియు అధునాతన ఫిషింగ్ గుర్తింపు.",
    "Launch URL Scanner": "URL స్కానర్‌ని ప్రారంభించండి",
    "SMS & Message Scam NLP": "SMS & సందేశ మోసాల రక్షణ",
    "Multilingual semantic analyzer for urgency traps, OTP interception, and lottery fraud.": "OTP దొంగతనం, అత్యవసర బెదిరింపులు మరియు లాటరీ మోసాలను గుర్తించే విశ్లేషణ.",
    "Launch SMS Scanner": "SMS స్కానర్‌ని ప్రారంభించండి",
    "Deepfake Vision Forensics": "డీప్‌ఫేక్ విజన్ ఫోరెన్సిక్స్",
    "Pixel PRNU sensor noise residual, dermis smoothing, and generative diffusion artifacts.": "కెమెరా సెన్సార్ నాయిస్, ఫేస్ స్మూతింగ్ మరియు కృత్రిమ పిక్సెల్ గుర్తింపు.",
    "Launch Vision AI": "విజన్ AIని ప్రారంభించండి",
    "QR & Payment Fraud Shield": "QR & పేమెంట్ ఫ్రాడ్ రక్షణ",
    "Inspect payment QR codes, malicious collect requests, and deceptive merchant VPAs.": "చెల్లింపు QR కోడ్‌లు, మోసపూరిత కలెక్ట్ అభ్యర్థనలు మరియు నకిలీ VPAలను తనిఖీ చేయండి.",
    "Launch QR Scanner": "QR స్కానర్‌ని ప్రారంభించండి",

    // URL Scanner Page
    "Verify Suspicious Website Link": "అనుమానాస్పద వెబ్‌సైట్ లింక్‌ని ధృవీకరించండి",
    "Inspect domain names, homograph punycode, SSL certificate validity, and phishing indicators:": "డొమైన్ పేర్లు, హోమోగ్రాఫ్ ప్యూనికోడ్, SSL చెల్లుబాటు మరియు ఫిషింగ్ సంకేతాలను తనిఖీ చేయండి:",
    "Scan URL Link": "⚡ URL స్కాన్ చేయండి",
    "Quick Test Presets": "శీఘ్ర పరీక్ష ఉదాహరణలు",
    "Phishing Bank Demo": "ఫిషింగ్ బ్యాంక్ డెమో",
    "Legitimate Domain": "నిజమైన డొమైన్",
    "Non-Existent Domain": "ఉనికిలో లేని డొమైన్",

    // Message Scanner Page
    "Analyze Suspicious SMS or Chat": "అనుమానాస్పద SMS లేదా చాట్‌ను విశ్లేషించండి",
    "Detect deceptive urgency, lottery claims, bank impersonation, and fraudulent payment prompts:": "మోసపూరిత అత్యవసర బెదిరింపులు, లాటరీ క్లెయిమ్‌లు మరియు నకిలీ బ్యాంక్ అలర్ట్‌లను గుర్తించండి:",
    "Inspect Message Safety": "⚡ సందేశ భద్రతను తనిఖీ చేయండి",
    "Language Model:": "భాషా మోడల్:",
    "English": "ఇంగ్లీష్",
    "Electricity Bill Threat": "కరెంట్ బిల్లు బెదిరింపు",
    "Part-Time Job Scam": "పార్ట్-టైమ్ జాబ్ మోసం",
    "Legitimate Bank Alert": "నిజమైన బ్యాంక్ మెసేజ్",

    // Deepfake Vision Page
    "AI Media & Deepfake Detection": "AI మీడియా & డీప్‌ఫేక్ గుర్తింపు",
    "Analyze images for generative diffusion artifacts, GAN fingerprints, and PRNU sensor noise residuals:": "కృత్రిమ AI సృష్టించిన ఫోటోలు, ముఖ మార్పులు మరియు సెన్సార్ నాయిస్‌ను విశ్లేషించండి:",
    "Upload Photo or Drag & Drop": "ఫోటో అప్‌లోడ్ చేయండి లేదా డ్రాగ్ చేయండి",
    "Drop an image here or click to browse files (PNG, JPG, WEBP)": "చిత్రాన్ని ఇక్కడ వేయండి లేదా ఫైల్ ఎంచుకోవడానికి క్లిక్ చేయండి (PNG, JPG, WEBP)",
    "Analyze Media Forensics": "⚡ మీడియా ఫోరెన్సిక్స్‌ను విశ్లేషించండి",
    "Sample Synthetic Face": "AI సృష్టించిన నకిలీ ముఖం",
    "Authentic Camera Photo": "నిజమైన కెమెరా ఫోటో",

    // QR Guard Page
    "QR & Payment Safety Inspector": "QR & చెల్లింపు భద్రతా తనిఖీ",
    "Upload payment QR screenshot or scan payload to detect fraudulent collect requests:": "చెల్లింపు QR స్క్రీన్‌షాట్‌ను అప్‌లోడ్ చేసి మోసపూరిత కలెక్ట్ అభ్యర్థనలను తనిఖీ చేయండి:",
    "Upload QR / Payment Screenshot": "QR లేదా పేమెంట్ స్క్రీన్‌షాట్‌ను అప్‌లోడ్ చేయండి",
    "Drop payment screenshot or QR photo here to inspect": "పరిశీలించడానికి స్క్రీన్‌షాట్ లేదా QR ఫోటోను ఇక్కడ వేయండి",
    "Inspect QR Safety": "⚡ QR భద్రతను తనిఖీ చేయండి",
    "Verify QR Safety": "⚡ QR భద్రతను ధృవీకరించండి",

    // Scan Results & Verdicts
    "SAFE & VERIFIED DOMAIN": "సురక్షిత & ధృవీకరించిన డొమైన్",
    "HIGH RISK PHISHING THREAT": "అధిక ముప్పు గల ఫిషింగ్ ప్రమాదం",
    "URL DOES NOT EXIST / UNREACHABLE": "URL ఉనికిలో లేదు / చేరుకోలేము",
    "VERIFIED LEGITIMATE MESSAGE": "ధృవీకరించిన నిజమైన సందేశం",
    "MALICIOUS SCAM OR SOCIAL ENGINEERING": "హానికరమైన మోసం లేదా సోషల్ ఇంజనీరింగ్",
    "GENUINE / CAMERA AUTHENTIC": "నిజమైన / కెమెరా ద్వారా తీసిన ఫోటో",
    "SYNTHETIC AI GENERATED / HIGH RISK": "కృత్రిమ AI సృష్టించినది / అధిక ప్రమాదం",
    "SAFE": "సురక్షితం",
    "NOT SAFE / SCAM": "సురక్షితం కాదు / మోసం (స్కామ్)",
    "NOT SAFE / INSPECTION FAILED": "సురక్షితం కాదు / తనిఖీ విఫలమైంది",
    "Security Score": "భద్రతా స్కోరు",
    "Threat Level": "ముప్పు స్థాయి",
    "DNS Resolution": "DNS రిజల్యూషన్",
    "Safety Index": "భద్రతా సూచిక",
    "Threat Classification": "ముప్పు వర్గీకరణ",
    "Forensic Score": "ఫోరెన్సిక్ స్కోరు",
    "AI Probability": "AI సంభావ్యత",
    "Sensor PRNU": "సెన్సార్ PRNU",
    "Extracted Forensic Signals": "గుర్తించిన ఫోరెన్సిక్ సంకేతాలు",
    "Linguistic & Semantic Triggers": "భాషా & అర్థ సంబంధిత ముప్పులు",
    "Vision Forensics Radar": "విజన్ ఫోరెన్సిక్స్ రాడార్",
    "Safety Advisory": "భద్రతా సలహా",
    "Critical Precautions": "ముఖ్యమైన జాగ్రత్తలు",
    "Recommended Security Actions": "సిఫార్సు చేసిన భద్రతా చర్యలు",
    "Ask AI Assistant": "💬 AI అసిస్టెంట్‌ని అడగండి",

    // Audit History Page
    "Platform Scan History": "ప్లాట్‌ఫారమ్ స్కాన్ చరిత్ర",
    "Cross-device telemetry log synchronized with Android mobile endpoints and web scanners:": "ఆండ్రాయిడ్ మొబైల్ మరియు వెబ్ స్కానర్‌లతో సింక్ చేసిన చరిత్ర లాగ్‌లు:",
    "Refresh History": "🔄 చరిత్రను రీఫ్రెష్ చేయండి",
    "ALL": "అన్నీ",
    "URL": "URL ఫిషింగ్",
    "MESSAGE": "SMS మోసం",
    "DEEPFAKE": "డీప్‌ఫేక్ విజన్",
    "QR": "QR & చెల్లింపు",
    "No Scan Records Found": "స్కాన్ రికార్డులు కనుగొనబడలేదు",
    "Run scans from the dashboard or your linked Android phone to see synced logs here.": "సింక్ చేసిన లాగ్‌లను చూడటానికి డాష్‌బోర్డ్ లేదా మీ మొబైల్ నుండి స్కాన్‌లను ప్రారంభించండి.",
    "Time": "సమయం",
    "Device Source": "పరికరం",
    "Scanner Module": "స్కానర్ రకం",
    "Target / Payload": "లక్ష్యం",

    // AI Assistant Page
    "Sentinel AI Cyber Defense Assistant": "సెంటినెల్ AI సైబర్ రక్షణ అసిస్టెంట్",
    "Ask anything about cyber threats, digital arrest scams, suspicious APKs, UPI fraud, deepfakes, or legal remedies:": "సైబర్ నేరాలు, డిజిటల్ అరెస్ట్, UPI మోసాలు, ప్రమాదకరమైన APKలు, 1930 హెల్ప్‌లైన్ గురించి ఏదైనా అడగండి:",
    "Ask anything about cyber threats, digital arrest, UPI fraud, legal remedies (1930)...": "సైబర్ నేరాలు, డిజిటల్ అరెస్ట్, UPI మోసాలు, 1930 హెల్ప్‌లైన్ గురించి ఏదైనా అడగండి...",
    "Send Query": "⚡ ప్రశ్న పంపండి",
    "Ask about Digital Arrest Scams": "🚨 డిజిటల్ అరెస్ట్ మోసాల గురించి అడగండి",
    "Explain UPI PIN Rules": "💳 UPI PIN నియమాలను వివరించండి",
    "Report Cybercrime (1930)": "⚖️ సైబర్ నేరాన్ని నివేదించండి (1930)",
    "Detect Sideloaded APK Malware": "📱 నకిలీ APK మాల్వేర్‌ను గుర్తించండి"
  }
};

/**
 * Look up key in given language with fallback to English
 */
function t(key, lang = null) {
  if (!key) return '';
  const currentLang = lang || localStorage.getItem('sentinel_preferred_lang') || 'en';
  if (SENTINEL_I18N[currentLang] && SENTINEL_I18N[currentLang][key]) {
    return SENTINEL_I18N[currentLang][key];
  }
  return SENTINEL_I18N.en[key] || key;
}

/**
 * Intelligent dynamic report explanation translator for English -> Hindi & Telugu
 */
function translateReport(text, lang = null) {
  if (!text) return '';
  const currentLang = lang || localStorage.getItem('sentinel_preferred_lang') || 'en';
  if (currentLang === 'en') return text;

  let out = text;
  if (currentLang === 'hi') {
    out = out
      .replace(/This URL does not resolve via public DNS nameservers/gi, "यह यूआरएल सार्वजनिक डीएनएस सर्वर पर मौजूद नहीं है")
      .replace(/Domain destination verified against legitimate registry/gi, "डोमेन गंतव्य को आधिकारिक रजिस्ट्री के विरुद्ध सत्यापित किया गया है")
      .replace(/Entering your UPI PIN will DEDUCT money from your account/gi, "UPI PIN डालने पर आपके खाते से पैसे कट जाएंगे")
      .replace(/You NEVER need to enter a UPI PIN to receive money/gi, "पैसे प्राप्त करने के लिए आपको कभी भी UPI PIN दर्ज करने की आवश्यकता नहीं होती")
      .replace(/High risk phishing heuristics detected/gi, "उच्च जोखिम वाले फ़िशिंग संकेत पाए गए")
      .replace(/Synthetic generative diffusion artifacts detected/gi, "सिंथेटिक एआई जनरेटेड बनावटी पिक्सेल पाए गए")
      .replace(/Organic camera sensor noise present/gi, "कैमरे के वास्तविक सेंसर नॉइज़ मौजूद हैं")
      .replace(/Do not open this link/gi, "इस लिंक को न खोलें")
      .replace(/Destination verified safe/gi, "गंतव्य सुरक्षित सत्यापित किया गया")
      .replace(/CRITICAL/gi, "गंभीर (CRITICAL)")
      .replace(/HIGH RISK/gi, "उच्च जोखिम (HIGH RISK)")
      .replace(/SUSPICIOUS/gi, "संदिग्ध (SUSPICIOUS)")
      .replace(/SAFE/gi, "सुरक्षित (SAFE)");
  } else if (currentLang === 'te') {
    out = out
      .replace(/This URL does not resolve via public DNS nameservers/gi, "ఈ URL పబ్లిక్ DNS సర్వర్లలో ఉనికిలో లేదు")
      .replace(/Domain destination verified against legitimate registry/gi, "డొమైన్ గమ్యస్థానం అధికారిక రిజిస్ట్రీ ద్వారా ధృవీకరించబడింది")
      .replace(/Entering your UPI PIN will DEDUCT money from your account/gi, "మీ UPI PIN నమోదు చేస్తే మీ ఖాతా నుండి డబ్బు కట్ అవుతుంది")
      .replace(/You NEVER need to enter a UPI PIN to receive money/gi, "డబ్బు అందుకోవడానికి ఎప్పుడూ UPI PIN అవసరం లేదు")
      .replace(/High risk phishing heuristics detected/gi, "అధిక ముప్పు గల ఫిషింగ్ సంకేతాలు గుర్తించబడ్డాయి")
      .replace(/Synthetic generative diffusion artifacts detected/gi, "కృత్రిమ AI సృష్టించిన పిక్సెల్స్ గుర్తించబడ్డాయి")
      .replace(/Organic camera sensor noise present/gi, "నిజమైన కెమెరా సెన్సార్ నాయిస్ ఉంది")
      .replace(/Do not open this link/gi, "ఈ లింక్‌ని తెరవవద్దు")
      .replace(/Destination verified safe/gi, "గమ్యస్థానం సురక్షితమని ధృవీకరించబడింది")
      .replace(/CRITICAL/gi, "తీవ్రమైన ప్రమాదం (CRITICAL)")
      .replace(/HIGH RISK/gi, "అధిక ముప్పు (HIGH RISK)")
      .replace(/SUSPICIOUS/gi, "అనుమానాస్పదం (SUSPICIOUS)")
      .replace(/SAFE/gi, "సురక్షితం (SAFE)");
  }
  return out;
}

/**
 * Apply selected language globally across the entire DOM
 */
function applyGlobalLanguage(lang) {
  if (!lang) lang = localStorage.getItem('sentinel_preferred_lang') || 'en';
  localStorage.setItem('sentinel_preferred_lang', lang);

  document.documentElement.setAttribute('lang', lang);

  // Sync all language dropdowns on the page
  const globalSelect = document.getElementById('global-lang-select');
  const smsSelect = document.getElementById('sms-lang-select');
  const chatSelect = document.getElementById('chat-lang-select');
  if (globalSelect && globalSelect.value !== lang) globalSelect.value = lang;
  if (smsSelect && smsSelect.value !== lang) smsSelect.value = lang;
  if (chatSelect && chatSelect.value !== lang) chatSelect.value = lang;

  const dict = SENTINEL_I18N[lang] || SENTINEL_I18N.en;

  // 1. Elements with explicit data-i18n attribute
  document.querySelectorAll('[data-i18n]').forEach(el => {
    const key = el.getAttribute('data-i18n');
    if (dict[key]) {
      el.textContent = dict[key];
    }
  });

  // 2. Elements with data-i18n-placeholder attribute
  document.querySelectorAll('[data-i18n-placeholder]').forEach(el => {
    const key = el.getAttribute('data-i18n-placeholder');
    if (dict[key]) {
      el.setAttribute('placeholder', dict[key]);
    }
  });

  // 3. Smart DOM text walker for navigation, headers, and buttons
  const selectors = [
    '.nav-link', '.brand-text span', '.header-title-block h2', '.header-title-block p',
    '.score-posture-title', '.score-posture-desc', '.breakdown-label',
    '.metric-title', '.metric-foot span', '.scan-hub-badge', '.scan-hub-info h3',
    '.scan-hub-info p', '.feed-title span', '.scan-option-tile h4', '.scan-option-tile p',
    '.scan-option-btn-link span', '.modal-title', '.modal-header p', '.btn',
    '.filter-btn', '.prompt-chip', '.signals-title', 'th', '.card-step-badge'
  ];

  document.querySelectorAll(selectors.join(', ')).forEach(el => {
    if (el.children.length === 0) {
      const trimmed = el.textContent.trim();
      // Check if matches English or another language key
      for (const [enKey, val] of Object.entries(SENTINEL_I18N.en)) {
        if (trimmed === val || trimmed === (SENTINEL_I18N.hi[enKey] || '') || trimmed === (SENTINEL_I18N.te[enKey] || '')) {
          if (dict[enKey]) {
            el.textContent = dict[enKey];
          }
          break;
        }
      }
    }
  });

  // 4. Update input placeholders
  const searchInput = document.getElementById('history-search-input');
  if (searchInput) {
    searchInput.placeholder = dict["Search scans..."] || "Search scans by URL, keyword, target...";
  }

  const chatInput = document.getElementById('chat-input-text');
  if (chatInput) {
    chatInput.placeholder = dict["Ask anything about cyber threats, digital arrest, UPI fraud, legal remedies (1930)..."] || "Ask anything about cyber threats, digital arrest, UPI fraud, legal remedies (1930)...";
  }

  // 5. Fire notification event so active controllers update dynamic results
  document.dispatchEvent(new CustomEvent('sentinel-language-changed', { detail: { lang } }));
}

// Export to window
window.SENTINEL_I18N = SENTINEL_I18N;
window.t = t;
window.translateReport = translateReport;
window.applyGlobalLanguage = applyGlobalLanguage;
