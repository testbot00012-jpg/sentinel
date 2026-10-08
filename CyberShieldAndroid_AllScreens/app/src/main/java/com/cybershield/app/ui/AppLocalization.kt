package com.cybershield.app.ui

object AppLocalization {

    fun getGreeting(language: String, hour: Int): String {
        val lang = language.lowercase()
        return when {
            lang.contains("telugu") || lang.contains("తెలుగు") -> {
                when (hour) {
                    in 5..11 -> "శుభోదయం"
                    in 12..16 -> "శుభ మధ్యాహ్నం"
                    in 17..20 -> "శుభ సాయంత్రం"
                    else -> "శుభ రాత్రి"
                }
            }
            lang.contains("hindi") || lang.contains("हिन्दी") -> {
                when (hour) {
                    in 5..11 -> "शुभ प्रभात"
                    in 12..16 -> "शुभ दोपहर"
                    in 17..20 -> "शुभ संध्या"
                    else -> "शुभ रात्रि"
                }
            }
            else -> {
                when (hour) {
                    in 5..11 -> "Good morning"
                    in 12..16 -> "Good afternoon"
                    in 17..20 -> "Good evening"
                    else -> "Good night"
                }
            }
        }
    }

    fun getProtectedSubtitle(language: String, deviceName: String): String {
        val lang = language.lowercase()
        return when {
            lang.contains("telugu") || lang.contains("తెలుగు") -> "$deviceName సురక్షితంగా రక్షించబడింది"
            lang.contains("hindi") || lang.contains("हिन्दी") -> "$deviceName सुरक्षित और संरक्षित है"
            else -> "$deviceName is protected"
        }
    }

    fun getNavLabel(key: String, language: String): String {
        val lang = language.lowercase()
        val isTelugu = lang.contains("telugu") || lang.contains("తెలుగు")
        val isHindi = lang.contains("hindi") || lang.contains("हिन्दी")

        return when (key.lowercase()) {
            "home" -> if (isTelugu) "హోమ్" else if (isHindi) "होम" else "Home"
            "alerts" -> if (isTelugu) "హెచ్చరికలు" else if (isHindi) "अलर्ट" else "Alerts"
            "scan" -> if (isTelugu) "స్కాన్" else if (isHindi) "स्कैन" else "Scan"
            "protect" -> if (isTelugu) "రక్షణ" else if (isHindi) "सुरक्षा" else "Protect"
            "settings" -> if (isTelugu) "సెట్టింగ్‌లు" else if (isHindi) "सेटिंग्स" else "Settings"
            "history" -> if (isTelugu) "చరిత్ర" else if (isHindi) "इतिहास" else "History"
            "privacy" -> if (isTelugu) "గోప్యత" else if (isHindi) "गोपनीयता" else "Privacy"
            "apps" -> if (isTelugu) "యాప్స్" else if (isHindi) "ऐप्स" else "Apps"
            "storage" -> if (isTelugu) "స్టోరేజ్" else if (isHindi) "स्टोरेज" else "Storage"
            "ai" -> if (isTelugu) "AI అసిస్టెంట్" else if (isHindi) "एआई सहायक" else "AI Assistant"
            else -> key
        }
    }

    fun t(key: String, language: String): String {
        val lang = language.lowercase()
        val isTelugu = lang.contains("telugu") || lang.contains("తెలుగు")
        val isHindi = lang.contains("hindi") || lang.contains("हिन्दी")
        if (!isTelugu && !isHindi) return key

        return when (key.trim()) {
            // Navigation & Header
            "Home" -> if (isTelugu) "హోమ్" else "होम"
            "History" -> if (isTelugu) "చరిత్ర" else "इतिहास"
            "Scans, incidents & evidence" -> if (isTelugu) "స్కాన్‌లు, సంఘటనలు & సాక్ష్యాలు" else "स्कैन, घटनाएं और साक्ष्य"
            "Recent activity" -> if (isTelugu) "ఇటీవలి కార్యాచరణ" else "हाल की गतिविधियां"
            "Recent Scans" -> if (isTelugu) "ఇటీవలి స్కాన్‌లు" else "हाल के स्कैन"
            "No Scans in History" -> if (isTelugu) "చరిత్రలో ఎటువంటి స్కాన్‌లు లేవు" else "इतिहास में कोई स्कैन नहीं है"
            "Search scans, URLs, incidents..." -> if (isTelugu) "స్కాన్‌లు, URLలు, సంఘటనలు వెతకండి..." else "स्कैन, URL, घटनाओं को खोजें..."
            "Evidence vault" -> if (isTelugu) "సాక్ష్యాల భద్రతా గది" else "साक्ष्य वॉल्ट"
            "14 encrypted items" -> if (isTelugu) "14 గుప్తీకరించిన అంశాలు" else "14 एन्क्रिप्टेड आइटम"
            "EXPORT" -> if (isTelugu) "ఎగుమతి చేయి" else "एक्सपोर्ट"

            // Filter Tabs
            "ALL" -> if (isTelugu) "అన్నీ" else "सभी"
            "HIGH RISK" -> if (isTelugu) "అధిక ముప్పు" else "उच्च जोखिम"
            "SAFE" -> if (isTelugu) "సురక్షితం" else "सुरक्षित"
            "URL PHISHING" -> if (isTelugu) "URL ఫిషింగ్" else "URL फ़िशिंग"
            "SMS SCAM" -> if (isTelugu) "SMS మోసం" else "SMS धोखाधड़ी"
            "QR & PAYMENT" -> if (isTelugu) "QR & చెల్లింపులు" else "QR और भुगतान"
            "DEEPFAKE VISION" -> if (isTelugu) "డీప్‌ఫేక్ AI మీడియా" else "डीपफेक एआई मीडिया"
            "APPS & AUDIT" -> if (isTelugu) "యాప్స్ & ఆడిట్" else "ऐप्स और ऑडिट"

            // URL Shield
            "URL protection" -> if (isTelugu) "URL రక్షణ కవచం" else "URL सुरक्षा"
            "Preview risky destinations" -> if (isTelugu) "ప్రమాదకర వెబ్‌సైట్‌ల ముందస్తు పరిశీలన" else "खतरनाक वेबसाइटों का पूर्वावलोकन"
            "Scan Website Link / URL" -> if (isTelugu) "వెబ్‌సైట్ లింక్ / URL స్కాన్ చేయండి" else "वेबसाइट लिंक / URL स्कैन करें"
            "Enter any suspicious website link to check SSL, domain age, and phishing threat:" ->
                if (isTelugu) "SSL, డొమైన్ వయస్సు మరియు ఫిషింగ్ ముప్పును తనిఖీ చేయడానికి అనుమానాస్పద లింక్‌ను నమోదు చేయండి:" else "SSL, डोमेन आयु और फ़िशिंग खतरे की जांच के लिए संदिग्ध लिंक दर्ज करें:"
            "Scan URL" -> if (isTelugu) "URL స్కాన్ చేయండి" else "URL स्कैन करें"
            "Scanning..." -> if (isTelugu) "స్కాన్ చేస్తోంది..." else "स्कैन किया जा रहा है..."
            "Close" -> if (isTelugu) "మూసివేయి" else "बंद करें"
            "Destination Verified Safe" -> if (isTelugu) "వెబ్‌సైట్ సురక్షితమని నిర్ధారించబడింది" else "वेबसाइट सुरक्षित सत्यापित की गई"
            "Do not open this link" -> if (isTelugu) "ఈ లింక్‌ను అస్సలు తెరవవద్దు" else "इस लिंक को न खोलें"
            "New URL" -> if (isTelugu) "కొత్త URL" else "नया URL"
            "No Link Scanned Yet" -> if (isTelugu) "ఇంకా ఏ లింక్ స్కాన్ చేయలేదు" else "अभी तक कोई लिंक स्कैन नहीं हुआ"
            "Enter URL to Scan" -> if (isTelugu) "స్కాన్ చేయడానికి URL ఇవ్వండి" else "स्कैन के लिए URL दर्ज करें"
            "Evidence" -> if (isTelugu) "సాక్ష్యాలు" else "साक्ष्य"
            "TLS Encryption" -> if (isTelugu) "TLS ఎన్‌క్రిప్షన్" else "TLS एन्क्रिप्शन"
            "Threat Probability" -> if (isTelugu) "ముప్పు సంభావ్యత" else "खतरे की संभावना"
            "Security Engine" -> if (isTelugu) "సెక్యూరిటీ ఇంజిన్" else "सुरक्षा इंजन"
            "Security Score" -> if (isTelugu) "భద్రతా స్కోరు" else "सुरक्षा स्कोर"
            "WHAT YOU SHOULD DO" -> if (isTelugu) "మీరు ఏమి చేయాలి" else "आपको क्या करना चाहिए"
            "WHAT TO AVOID" -> if (isTelugu) "చేయకూడని పనులు" else "क्या न करें"
            "WHY THIS SCORE" -> if (isTelugu) "ఈ స్కోరు ఎందుకు వచ్చింది" else "यह स्कोर क्यों दिया गया"
            "Recent URL Scans" -> if (isTelugu) "ఇటీవలి URL స్కాన్‌లు" else "हाल के URL स्कैन"
            "View Full History" -> if (isTelugu) "పూర్తి చరిత్రను చూడండి" else "पूरा इतिहास देखें"

            // Message Shield
            "Message analysis" -> if (isTelugu) "సందేశ విశ్లేషణ" else "संदेश विश्लेषण"
            "Inspect SMS for fraud" -> if (isTelugu) "మోసపూరిత SMS సందేశాల గుర్తింపు" else "धोखाधड़ी के लिए SMS की जांच"
            "Analyze Message Text" -> if (isTelugu) "సందేశాన్ని విశ్లేషించండి" else "संदेश का विश्लेषण करें"
            "Scan Message" -> if (isTelugu) "సందేశాన్ని స్కాన్ చేయండి" else "संदेश स्कैन करें"
            "Recent SMS / Message Scans" -> if (isTelugu) "ఇటీవలి SMS స్కాన్‌లు" else "हाल के SMS स्कैन"
            "Safe to proceed" -> if (isTelugu) "ముందుకు సాగడం సురక్షితం" else "आगे बढ़ना सुरक्षित है"
            "Do not reply or click links" -> if (isTelugu) "సమాధానం ఇవ్వవద్దు లేదా లింక్‌లపై క్లిక్ చేయవద్దు" else "उत्तर न दें या लिंक पर क्लिक न करें"

            // QR & Payment
            "QR & Payment Safety" -> if (isTelugu) "QR & చెల్లింపు భద్రత" else "QR और भुगतान सुरक्षा"
            "Inspect payment codes & screenshots" -> if (isTelugu) "చెల్లింపు కోడ్‌లు మరియు స్క్రీన్‌షాట్‌ల పరిశీలన" else "भुगतान कोड और स्क्रीनशॉट की जांच"
            "Scan QR Code" -> if (isTelugu) "QR కోడ్‌ను స్కాన్ చేయండి" else "QR कोड स्कैन करें"
            "Upload Payment Screenshot" -> if (isTelugu) "చెల్లింపు స్క్రీన్‌షాట్ అప్‌లోడ్ చేయండి" else "भुगतान स्क्रीनशॉट अपलोड करें"
            "Recent QR & Payment Scans" -> if (isTelugu) "ఇటీవలి QR & చెల్లింపు స్కాన్‌లు" else "हाल के QR और भुगतान स्कैन"
            "Verified Payment Destination" -> if (isTelugu) "ధృవీకరించబడిన చెల్లింపు గమ్యస్థానం" else "सत्यापित भुगतान गंतव्य"
            "Fraudulent Collect Request Detected" -> if (isTelugu) "మోసపూరిత కలెక్ట్ రిక్వెస్ట్ గుర్తించబడింది" else "धोखाधड़ी वाला कलेक्ट अनुरोध पाया गया"

            // AI Media & Deepfake
            "AI Media & Deepfake Scan" -> if (isTelugu) "AI మీడియా & డీప్‌ఫేక్ స్కాన్" else "एआई मीडिया और डीपफेक स्कैन"
            "Detect synthetic faces & cloned voices" -> if (isTelugu) "కృత్రిమ ముఖాలు మరియు క్లోన్ చేసిన వాయిస్‌ల గుర్తింపు" else "नकली चेहरे और क्लोन की गई आवाज की पहचान"
            "Upload Photo / Video / Audio" -> if (isTelugu) "ఫోటో / వీడియో / ఆడియో అప్‌లోడ్ చేయండి" else "फोटो / वीडियो / ऑडियो अपलोड करें"
            "Analyze Media" -> if (isTelugu) "మీడియాను విశ్లేషించండి" else "मीडिया का विश्लेषण करें"
            "Recent Deepfake Scans" -> if (isTelugu) "ఇటీవలి డీప్‌ఫేక్ స్కాన్‌లు" else "हाल के डीपफेक स्कैन"
            "Authentic Media Verified" -> if (isTelugu) "అసలైన మీడియాగా నిర్ధారించబడింది" else "प्रामाणिक मीडिया सत्यापित"
            "AI Deepfake Detected" -> if (isTelugu) "AI డీప్‌ఫేక్ గుర్తించబడింది" else "एआई डीपफेक पाया गया"

            // Apps Manager & Settings
            "Apps Manager" -> if (isTelugu) "యాప్స్ మేనేజర్" else "ऐप्स मैनेजर"
            "Inspect installed application risk" -> if (isTelugu) "ఇన్‌స్టాల్ చేసిన యాప్‌ల రిస్క్ పరిశీలన" else "इंस्टॉल किए गए ऐप्स के जोखिम की जांच"
            "Privacy center" -> if (isTelugu) "గోప్యతా కేంద్రం" else "गोपनीयता केंद्र"
            "Security logs" -> if (isTelugu) "భద్రతా లాగ్‌లు" else "सुरक्षा लॉग्स"
            "Trusted devices" -> if (isTelugu) "విశ్వసనీయ పరికరాలు" else "विश्वसनीय उपकरण"
            "Data & storage" -> if (isTelugu) "డేటా & స్టోరేజ్" else "डेटा और स्टोरेज"
            "Language" -> if (isTelugu) "భాష" else "भाषा"
            "Scan Now" -> if (isTelugu) "ఇప్పుడే స్కాన్ చేయండి" else "अभी स्कैन करें"
            "Log out" -> if (isTelugu) "లాగ్ అవుట్" else "लॉग आउट"
            "Storage Analyzer" -> if (isTelugu) "స్టోరేజ్ ఎనలైజర్" else "स्टोरेज विश्लेषक"
            "Suspicious Files" -> if (isTelugu) "అనుమానాస్పద ఫైళ్లు" else "संदिग्ध फाइलें"
            "Duplicate Files" -> if (isTelugu) "డూప్లికేట్ ఫైళ్లు" else "डुप्लिकेट फाइलें"
            "Dangerous Documents" -> if (isTelugu) "ప్రమాదకర పత్రాలు" else "खतरनाक दस्तावेज़"
            "Download Scanner" -> if (isTelugu) "డౌన్‌లోడ్ స్కానర్" else "डाउनलोड स्कैनर"
            "Priority alerts" -> if (isTelugu) "ముఖ్యమైన హెచ్చరికలు" else "प्राथमिकता अलर्ट"
            "All Systems Protected" -> if (isTelugu) "అన్ని వ్యవస్థలు సురక్షితంగా ఉన్నాయి" else "सभी सिस्टम सुरक्षित हैं"
            "Run Live Device Audit" -> if (isTelugu) "లైవ్ డివైజ్ ఆడిట్ నిర్వహించండి" else "लाइव डिवाइस ऑडिट चलाएं"
            "PROTECTED" -> if (isTelugu) "రక్షించబడింది" else "सुरक्षित"
            "SAFE" -> if (isTelugu) "సురక్షితం" else "सुरक्षित"
            "DANGER" -> if (isTelugu) "ప్రమాదం" else "खतरा"
            "CRITICAL" -> if (isTelugu) "తీవ్ర ప్రమాదం" else "अत्यधिक गंभीर"
            "HIGH RISK" -> if (isTelugu) "అధిక ముప్పు" else "उच्च जोखिम"
            "SUSPICIOUS" -> if (isTelugu) "అనుమానాస్పదం" else "संदिग्ध"
            "LOW CONCERN" -> if (isTelugu) "తక్కువ ముప్పు" else "कम चिंता"
            else -> key
        }
    }

    fun translateExplanation(explanation: String, language: String): String {
        val lang = language.lowercase()
        val isTelugu = lang.contains("telugu") || lang.contains("తెలుగు")
        val isHindi = lang.contains("hindi") || lang.contains("हिन्दी")
        if (!isTelugu && !isHindi || explanation.isBlank()) return explanation

        val expLower = explanation.lower()

        if (expLower.contains("does not exist") || expLower.contains("resolution failure")) {
            return if (isTelugu) "ఈ వెబ్‌సైట్ అందుబాటులో లేదు లేదా DNS రిజల్యూషన్ విఫలమైంది." else "यह वेबसाइट मौजूद नहीं है या DNS रिज़ॉल्यूशन विफल हो गया।"
        }
        if (expLower.contains("phishing") || expLower.contains("deceptive")) {
            return if (isTelugu) "హెచ్చరిక: ఇది మీ ఆధారాలు మరియు బ్యాంక్ వివరాలను దొంగిలించడానికి రూపొందించిన నకిలీ ఫిషింగ్ లింక్." else "चेतावनी: यह आपकी साख और बैंक विवरण चुराने के लिए बनाया गया फ़िशिंग लिंक है।"
        }
        if (expLower.contains("clean") || expLower.contains("verified") || expLower.contains("safe")) {
            return if (isTelugu) "సురక్షితం: ఎటువంటి హానికర సంకేతాలు కనుగొనబడలేదు. డొమైన్ ధృవీకరించబడింది." else "सुरक्षित: कोई दुर्भावनापूर्ण संकेत नहीं मिले। डोमेन सत्यापित है।"
        }
        if (expLower.contains("upi pin") || expLower.contains("collect")) {
            return if (isTelugu) "UPI నియమం: డబ్బు అందుకోవడానికి ఎప్పుడూ UPI PIN నమోదు చేయకూడదు. ఇది మోసం." else "UPI नियम: पैसे प्राप्त करने के लिए कभी भी UPI PIN न डालें। यह धोखाधड़ी है।"
        }
        if (expLower.contains("deepfake") || expLower.contains("synthetic")) {
            return if (isTelugu) "AI విశ్లేషణ: ఈ మీడియాలో కృత్రిమ ముఖం లేదా క్లోన్ చేసిన వాయిస్ లక్షణాలు ఉన్నాయి." else "एआई विश्लेषण: इस मीडिया में सिंथेटिक चेहरे या क्लोन आवाज के लक्षण हैं।"
        }

        return if (isTelugu) "సెంటినెల్ AI విశ్లేషణ పూర్తి చేయబడింది: $explanation" else "सेंटिनल एआई विश्लेषण पूरा हुआ: $explanation"
    }

    fun translateRecommendation(rec: String, language: String): String {
        val lang = language.lowercase()
        val isTelugu = lang.contains("telugu") || lang.contains("తెలుగు")
        val isHindi = lang.contains("hindi") || lang.contains("हिन्दी")
        if (!isTelugu && !isHindi || rec.isBlank()) return rec

        val rLow = rec.lower()
        if (rLow.contains("do not open") || rLow.contains("do not click")) {
            return if (isTelugu) "ఈ లింక్‌ను ఏ బ్రౌజర్‌లోనూ తెరవవద్దు మరియు తొలగించండి." else "इस लिंक को किसी भी ब्राउज़र में न खोलें और तुरंत हटाएं।"
        }
        if (rLow.contains("never share") || rLow.contains("otp") || rLow.contains("pin")) {
            return if (isTelugu) "మీ రహస్య OTP లేదా UPI PIN ఎవరితోనూ పంచుకోవద్దు." else "अपना गुप्त OTP या UPI PIN कभी किसी के साथ साझा न करें।"
        }
        if (rLow.contains("safe to proceed") || rLow.contains("proceed with caution")) {
            return if (isTelugu) "సురక్షితంగా కొనసాగవచ్చు. బ్రౌజర్ చిరునామాను ఒకసారి నిర్ధారించుకోండి." else "आगे बढ़ना सुरक्षित है। ब्राउज़र में डोमेन नाम की पुष्टि करें।"
        }
        if (rLow.contains("1930") || rLow.contains("report")) {
            return if (isTelugu) "డబ్బు పోయినట్లయితే వెంటనే 1930 హెల్ప్‌లైన్‌కు కాల్ చేయండి." else "पैसे कटने पर तुरंत राष्ट्रीय हेल्पलाइन 1930 पर कॉल करें।"
        }

        return if (isTelugu) "భద్రతా సూచన: $rec" else "सुरक्षा सिफारिश: $rec"
    }

    fun getString(key: String, language: String): String = t(key, language)

    private fun String.lower(): String = this.lowercase()
}
