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
            else -> key
        }
    }

    fun t(key: String, language: String): String {
        val lang = language.lowercase()
        val isTelugu = lang.contains("telugu") || lang.contains("తెలుగు")
        val isHindi = lang.contains("hindi") || lang.contains("हिन्दी")

        return when (key) {
            "History" -> if (isTelugu) "చరిత్ర" else if (isHindi) "इतिहास" else "History"
            "Security logs" -> if (isTelugu) "భద్రతా లాగ్‌లు" else if (isHindi) "सुरक्षा लॉग्स" else "Security logs"
            "Trusted devices" -> if (isTelugu) "విశ్వసనీయ పరికరాలు" else if (isHindi) "विश्वसनीय उपकरण" else "Trusted devices"
            "Data & storage" -> if (isTelugu) "డేటా & స్టోరేజ్" else if (isHindi) "डेटा और स्टोरेज" else "Data & storage"
            "Language" -> if (isTelugu) "భాష" else if (isHindi) "भाषा" else "Language"
            "Apps Manager" -> if (isTelugu) "యాప్స్ మేనేజర్" else if (isHindi) "ऐप्स मैनेजर" else "Apps Manager"
            "Privacy center" -> if (isTelugu) "గోప్యతా కేంద్రం" else if (isHindi) "गोपनीयता केंद्र" else "Privacy center"
            "Scan Now" -> if (isTelugu) "ఇప్పుడే స్కాన్ చేయండి" else if (isHindi) "अभी स्कैन करें" else "Scan Now"
            "Log out" -> if (isTelugu) "లాగ్ అవుట్" else if (isHindi) "लॉग आउट" else "Log out"
            "Audit trail of security actions" -> if (isTelugu) "భద్రతా చర్యల ఆడిట్ రికార్డులు" else if (isHindi) "सुरक्षा कार्यों का ऑडिट रिकॉर्ड" else "Audit trail of security actions"
            "Manage active sessions and emergency access" -> if (isTelugu) "యాక్టివ్ సెషన్‌లు మరియు పరికర యాక్సెస్ నిర్వహించండి" else if (isHindi) "सक्रिय सत्र और डिवाइस एक्सेस प्रबंधित करें" else "Manage active sessions and emergency access"
            "Manage scan records and encrypted evidence" -> if (isTelugu) "స్కాన్ రికార్డులు మరియు నిల్వ నిర్వహించండి" else if (isHindi) "स्कैन रिकॉर्ड और स्टोरेज प्रबंधित करें" else "Manage scan records and encrypted evidence"
            "Choose your preferred security language" -> if (isTelugu) "మీ ప్రాధాన్య భద్రతా భాషను ఎంచుకోండి" else if (isHindi) "अपनी पसंदीदा सुरक्षा भाषा चुनें" else "Choose your preferred security language"
            "Storage Analyzer" -> if (isTelugu) "స్టోరేజ్ ఎనలైజర్" else if (isHindi) "स्टोरेज विश्लेषक" else "Storage Analyzer"
            "Suspicious Files" -> if (isTelugu) "అనుమానాస్పద ఫైళ్లు" else if (isHindi) "संदिग्ध फाइलें" else "Suspicious Files"
            "Duplicate Files" -> if (isTelugu) "డూప్లికేట్ ఫైళ్లు" else if (isHindi) "डुप्लिकेट फाइलें" else "Duplicate Files"
            "Dangerous Documents" -> if (isTelugu) "ప్రమాదకర పత్రాలు" else if (isHindi) "खतरनाक दस्तावेज़" else "Dangerous Documents"
            "Download Scanner" -> if (isTelugu) "డౌన్‌లోడ్ స్కానర్" else if (isHindi) "डाउनलोड स्कैनर" else "Download Scanner"
            "AI Media & Deepfake Scan" -> if (isTelugu) "AI మీడియా & డీప్‌ఫేక్ స్కాన్" else if (isHindi) "एआई मीडिया और डीपफेक स्कैन" else "AI Media & Deepfake Scan"
            "Priority alerts" -> if (isTelugu) "ముఖ్యమైన హెచ్చరికలు" else if (isHindi) "प्राथमिकता अलर्ट" else "Priority alerts"
            "All Systems Protected" -> if (isTelugu) "అన్ని వ్యవస్థలు సురక్షితంగా ఉన్నాయి" else if (isHindi) "सभी सिस्टम सुरक्षित हैं" else "All Systems Protected"
            "PROTECTED" -> if (isTelugu) "రక్షించబడింది" else if (isHindi) "सुरक्षित" else "PROTECTED"
            "SAFE" -> if (isTelugu) "సురక్షితం" else if (isHindi) "सुरक्षित" else "SAFE"
            "DANGER" -> if (isTelugu) "ప్రమాదం" else if (isHindi) "खतरा" else "DANGER"
            else -> key
        }
    }

    fun getString(key: String, language: String): String {
        val lang = language.lowercase()
        val isTelugu = lang.contains("telugu") || lang.contains("తెలుగు")
        val isHindi = lang.contains("hindi") || lang.contains("हिन्दी")
        return when (key.lowercase()) {
            "protected" -> if (isTelugu) "రక్షించబడింది" else if (isHindi) "सुरक्षित" else "PROTECTED"
            "attention" -> if (isTelugu) "శ్రద్ధ అవసరం" else if (isHindi) "ध्यान दें" else "ATTENTION"
            "at_risk" -> if (isTelugu) "ప్రమాదంలో ఉంది" else if (isHindi) "जोखिम में" else "AT RISK"
            "safe" -> if (isTelugu) "సురక్షితం" else if (isHindi) "सुरक्षित" else "SAFE"
            "danger" -> if (isTelugu) "ప్రమాదం" else if (isHindi) "खतरा" else "DANGER"
            else -> t(key, language)
        }
    }
}
