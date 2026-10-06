package com.cybershield.app.core.model

import java.util.UUID

enum class RiskLevel(val label: String) {
    SAFE("SAFE"),
    LOW_CONCERN("LOW CONCERN"),
    SUSPICIOUS("MEDIUM CONCERN"),
    HIGH_RISK("HIGH RISK"),
    CRITICAL("CRITICAL")
}

data class ScannerSignal(
    val name: String,
    val type: String,
    val severity: String,
    val description: String,
    val evidenceValue: String? = null,
    val weight: Double = 1.0
)

data class SecurityResult(
    val scanId: String = UUID.randomUUID().toString(),
    val scannerType: String,
    val riskLevel: RiskLevel,
    val riskScore: Int,
    val securityScore: Int = 90,  // Higher is Safer (0 to 100)
    val threatProbability: Double = 0.10,
    val confidence: Double,
    val signals: List<ScannerSignal> = emptyList(),
    val explanation: String,
    val recommendedActions: List<String> = emptyList(),
    val whatToAvoid: List<String> = emptyList(),
    val limitations: List<String> = emptyList(),
    val modelName: String,
    val modelVersion: String,
    val timestamp: Long = System.currentTimeMillis(),
    val quickSummary: String? = null,
    val whyThisScore: List<String> = emptyList(),
    val evidence: List<String> = emptyList(),
    val rawInputReference: String? = null
)

data class ScanChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val scanId: String = "",
    val sender: String = "Sentinel AI",
    val text: String,
    val isFromUser: Boolean,
    val intent: String? = null,
    val reasoning: String? = null,
    val reasoningSummary: String? = null,
    val recommendedActions: List<String> = emptyList(),
    val limitations: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class SessionInfo(
    val id: String,
    val name: String,
    val lastActive: String,
    val details: String
)

object SentinelRiskColors {
    val SafeGreen = androidx.compose.ui.graphics.Color(0xFF00E676)      // 90+: Green
    val LightOrange = androidx.compose.ui.graphics.Color(0xFFFF9800)    // 75–90: Light orange shade
    val BlackishYellow = androidx.compose.ui.graphics.Color(0xFF8A7300) // 60–75: Blackish-yellow
    val DangerRed = androidx.compose.ui.graphics.Color(0xFFFF3B30)      // Below 60: Red

    val SAFE_GREEN = SafeGreen
    val LIGHT_ORANGE = LightOrange
    val BLACKISH_YELLOW = BlackishYellow
    val DARK_AMBER = BlackishYellow
    val DANGER_RED = DangerRed

    fun getColorForScore(score: Int): androidx.compose.ui.graphics.Color {
        return when {
            score >= 90 -> SafeGreen
            score >= 75 -> LightOrange
            score >= 60 -> BlackishYellow
            else -> DangerRed
        }
    }

    fun getStatusForScore(score: Int): String {
        return when {
            score >= 90 -> "SAFE"
            score >= 75 -> "LOW CONCERN"
            score >= 60 -> "MEDIUM CONCERN"
            score >= 35 -> "HIGH RISK"
            else -> "CRITICAL"
        }
    }
}

data class DeviceTelemetry(
    val manufacturer: String,
    val brand: String,
    val model: String,
    val androidVersion: String,
    val sdkLevel: Int,
    val securityPatch: String,
    val supportedAbis: List<String>,
    val batteryPercent: Int,
    val isCharging: Boolean,
    val totalStorageBytes: Long,
    val availableStorageBytes: Long,
    val storageUsedPercent: Int,
    val isScreenLockEnabled: Boolean,
    val isStorageEncrypted: Boolean,
    val isDeveloperOptionsEnabled: Boolean,
    val isAdbEnabled: Boolean,
    val isRootDetected: Boolean,
    val rootSignals: List<String>,
    val networkType: String,
    val isVpnActive: Boolean,
    val installedAppCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class SecurityScoreState(
    val overallScore: Int,
    val previousScore: Int? = null,
    val breakdown: Map<String, Int> = emptyMap(),
    val recommendations: List<String> = emptyList(),
    val reasonsForChange: List<String> = emptyList(),
    val scoringVersion: String = "v2.0.0"
)

data class AlertItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val severity: RiskLevel,
    val category: String,
    val recommendedAction: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val isDismissed: Boolean = false
)

data class IncidentItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val description: String,
    val status: String = "NEW",
    val priority: String = "HIGH",
    val timestamp: Long = System.currentTimeMillis()
)

data class AssistantChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val structuredFindings: Map<String, Any>? = null,
    val safeActions: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class AccountActivityItem(
    val id: String = UUID.randomUUID().toString(),
    val activityType: String,
    val description: String,
    val deviceName: String? = null,
    val severity: String = "INFO",
    val timestamp: Long = System.currentTimeMillis()
)
