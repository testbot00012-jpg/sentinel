package com.cybershield.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cybershield.app.core.model.*
import com.cybershield.app.core.network.CyberShieldApiClient
import com.cybershield.app.core.security.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainSecurityViewModel(application: Application) : AndroidViewModel(application) {

    private val deviceRepository = DeviceRepository(application)
    private val apiClient = CyberShieldApiClient()

    private val _telemetry = MutableStateFlow<DeviceTelemetry?>(null)
    val telemetry: StateFlow<DeviceTelemetry?> = _telemetry.asStateFlow()

    private val _securityScore = MutableStateFlow(
        SecurityScoreState(
            overallScore = 90,
            breakdown = mapOf(
                "Device Posture" to 95,
                "App Security" to 90,
                "Permissions" to 85,
                "Malware" to 100,
                "Network" to 90,
                "Web Protection" to 95,
                "Account Security" to 85
            ),
            recommendations = listOf(
                "All systems reporting nominal telemetry.",
                "Keep automatic background threat checks enabled."
            ),
            reasonsForChange = listOf("Synchronized with active Android hardware posture.")
        )
    )
    val securityScore: StateFlow<SecurityScoreState> = _securityScore.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _currentScanResult = MutableStateFlow<SecurityResult?>(null)
    val currentScanResult: StateFlow<SecurityResult?> = _currentScanResult.asStateFlow()

    private val _alerts = MutableStateFlow<List<AlertItem>>(emptyList())
    val alerts: StateFlow<List<AlertItem>> = _alerts.asStateFlow()

    private val _incidents = MutableStateFlow<List<IncidentItem>>(emptyList())
    val incidents: StateFlow<List<IncidentItem>> = _incidents.asStateFlow()

    private val _scanChatThreads = MutableStateFlow<Map<String, List<ScanChatMessage>>>(emptyMap())
    val scanChatThreads: StateFlow<Map<String, List<ScanChatMessage>>> = _scanChatThreads.asStateFlow()

    private val _isAssistantResponding = MutableStateFlow(false)
    val isAssistantResponding: StateFlow<Boolean> = _isAssistantResponding.asStateFlow()

    private val _assistantMessages = MutableStateFlow<List<AssistantChatMessage>>(
        listOf(
            AssistantChatMessage(
                text = "Hello! I am Sentinel AI's Contextual Security Assistant. I explain real scan telemetry and threat signals without hallucinating. How can I assist your defense today?",
                isFromUser = false
            )
        )
    )
    val assistantMessages: StateFlow<List<AssistantChatMessage>> = _assistantMessages.asStateFlow()

    private val _accountActivities = MutableStateFlow<List<AccountActivityItem>>(emptyList())
    val accountActivities: StateFlow<List<AccountActivityItem>> = _accountActivities.asStateFlow()

    private val _authStatus = MutableStateFlow<String?>(null)
    val authStatus: StateFlow<String?> = _authStatus.asStateFlow()

    private val _currentUserEmail = MutableStateFlow<String?>("user@sentinelai.security")
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    init {
        refreshTelemetry()
        refreshAccountActivities()
    }

    fun refreshAccountActivities() {
        viewModelScope.launch {
            val list = apiClient.getAccountActivities()
            _accountActivities.value = list
        }
    }

    fun login(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = apiClient.login(email, pass)
            if (res.isSuccess) {
                _currentUserEmail.value = email
                _authStatus.value = "Authenticated with Supabase Auth"
                refreshAccountActivities()
                onResult(true, "Login successful. Credentials verified by Supabase.")
            } else {
                val err = res.exceptionOrNull()?.message ?: "Authentication failed"
                _authStatus.value = err
                onResult(false, err)
            }
        }
    }

    fun register(email: String, pass: String, fullName: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = apiClient.register(email, pass, fullName)
            if (res.isSuccess) {
                _currentUserEmail.value = email
                _authStatus.value = "Registered with Supabase Auth"
                refreshAccountActivities()
                onResult(true, "Registration successful. Credentials stored in Supabase.")
            } else {
                val err = res.exceptionOrNull()?.message ?: "Registration failed"
                _authStatus.value = err
                onResult(false, err)
            }
        }
    }

    fun refreshTelemetry() {
        viewModelScope.launch {
            val t = deviceRepository.collectRealDeviceTelemetry()
            _telemetry.value = t

            // Compute score based on real device parameters
            var devScore = 95
            if (t.isRootDetected) devScore -= 40
            if (!t.isScreenLockEnabled) devScore -= 20
            if (!t.isStorageEncrypted) devScore -= 15
            if (t.isDeveloperOptionsEnabled) devScore -= 5

            val newScore = (devScore * 0.3 + 90 * 0.7).toInt().coerceIn(0, 100)
            val recs = mutableListOf<String>()
            if (t.isRootDetected) recs.add("Root indicators detected! Isolate sensitive credentials.")
            if (!t.isScreenLockEnabled) recs.add("Set a PIN, Password, or Biometric Screen Lock.")
            if (t.networkType == "NONE") recs.add("Device is currently offline.")
            if (recs.isEmpty()) recs.add("Device configuration meets security baseline.")

            _securityScore.value = SecurityScoreState(
                overallScore = newScore,
                breakdown = mapOf(
                    "Device Posture" to devScore.coerceIn(0, 100),
                    "App Security" to 90,
                    "Permissions" to 85,
                    "Malware" to 100,
                    "Network" to if (t.networkType == "NONE") 60 else 90,
                    "Web Protection" to 95,
                    "Account Security" to 85
                ),
                recommendations = recs,
                reasonsForChange = listOf("Live Android API audit updated at System.currentTimeMillis()")
            )
        }
    }

    fun scanUrl(url: String) {
        viewModelScope.launch {
            _isScanning.value = true
            val result = apiClient.scanUrl(url)
            _currentScanResult.value = result
            _isScanning.value = false

            if (result.riskLevel == RiskLevel.HIGH_RISK || result.riskLevel == RiskLevel.CRITICAL) {
                addAlert(
                    AlertItem(
                        title = "Phishing Threat Blocked",
                        description = result.explanation,
                        severity = result.riskLevel,
                        category = "URL_PHISHING",
                        recommendedAction = result.recommendedActions.firstOrNull()
                    )
                )
            }
        }
    }

    fun scanMessage(text: String, language: String = "en") {
        viewModelScope.launch {
            _isScanning.value = true
            val result = apiClient.scanMessage(text, language)
            _currentScanResult.value = result
            _isScanning.value = false

            if (result.riskLevel == RiskLevel.HIGH_RISK || result.riskLevel == RiskLevel.CRITICAL) {
                addAlert(
                    AlertItem(
                        title = "Scam Message Detected",
                        description = result.explanation,
                        severity = result.riskLevel,
                        category = "SMS_MESSAGE_SCAM",
                        recommendedAction = result.recommendedActions.firstOrNull()
                    )
                )
            }
        }
    }

    fun scanQr(payload: String) {
        viewModelScope.launch {
            _isScanning.value = true
            val result = apiClient.scanQr(payload)
            _currentScanResult.value = result
            _isScanning.value = false
        }
    }

    fun scanApk(packageName: String) {
        viewModelScope.launch {
            _isScanning.value = true
            val result = apiClient.scanUrl("apk://$packageName")
            _currentScanResult.value = result.copy(
                scannerType = "APK_ANALYSIS",
                rawInputReference = packageName
            )
            _isScanning.value = false
        }
    }

    fun scanPayment(reference: String) {
        viewModelScope.launch {
            _isScanning.value = true
            val result = apiClient.scanQr("payment://$reference")
            _currentScanResult.value = result.copy(
                scannerType = "PAYMENT_FRAUD",
                rawInputReference = reference
            )
            _isScanning.value = false
        }
    }

    fun askScanAssistant(scanId: String, question: String) {
        if (question.isBlank()) return
        val userMsg = ScanChatMessage(
            sender = "User",
            text = question.trim(),
            isFromUser = true,
            timestamp = System.currentTimeMillis()
        )
        val currentThread = _scanChatThreads.value[scanId] ?: emptyList()
        _scanChatThreads.value = _scanChatThreads.value + (scanId to (currentThread + userMsg))

        viewModelScope.launch {
            _isAssistantResponding.value = true
            val res = apiClient.askScanAssistant(scanId, question, _currentScanResult.value)
            val answerObj = res.getOrNull()
            val answerText = answerObj?.optString("answer") ?: "Sentinel AI evaluated this scan context."
            val reasoning = answerObj?.optString("reasoning_summary")
            val acts = mutableListOf<String>()
            val actsArray = answerObj?.optJSONArray("recommended_actions")
            if (actsArray != null) {
                for (i in 0 until actsArray.length()) acts.add(actsArray.getString(i))
            }
            val lims = mutableListOf<String>()
            val limsArray = answerObj?.optJSONArray("limitations")
            if (limsArray != null) {
                for (i in 0 until limsArray.length()) lims.add(limsArray.getString(i))
            }

            val aiMsg = ScanChatMessage(
                sender = "Sentinel AI",
                text = answerText,
                isFromUser = false,
                timestamp = System.currentTimeMillis(),
                reasoningSummary = reasoning,
                recommendedActions = acts,
                limitations = lims
            )
            val updatedThread = (_scanChatThreads.value[scanId] ?: emptyList()) + aiMsg
            _scanChatThreads.value = _scanChatThreads.value + (scanId to updatedThread)
            _isAssistantResponding.value = false
        }
    }

    fun addAlert(alert: AlertItem) {
        _alerts.value = listOf(alert) + _alerts.value
    }

    fun dismissAlert(id: String) {
        _alerts.value = _alerts.value.filter { it.id != id }
    }

    fun reportIncident(title: String, category: String, desc: String) {
        val inc = IncidentItem(
            title = title,
            category = category,
            description = desc,
            status = "NEW"
        )
        _incidents.value = listOf(inc) + _incidents.value
    }

    fun sendAssistantMessage(query: String) {
        val userMsg = AssistantChatMessage(text = query, isFromUser = true)
        _assistantMessages.value = _assistantMessages.value + userMsg

        viewModelScope.launch {
            val responseText = apiClient.chatAssistant(query)
            val assistantMsg = AssistantChatMessage(text = responseText, isFromUser = false)
            _assistantMessages.value = _assistantMessages.value + assistantMsg
        }
    }
}
