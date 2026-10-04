package com.cybershield.app.core.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

enum class PermissionStatus {
    GRANTED,
    DENIED,
    DENIED_PERMANENTLY,
    UNAVAILABLE,
    RESTRICTED_BY_ANDROID
}

data class PermissionExplanation(
    val permission: String,
    val featureName: String,
    val whyNeeded: String,
    val dataAccessed: String,
    val isProcessedLocally: Boolean,
    val revocationGuidance: String
)

object PermissionManager {

    private val EXPLANATIONS = mapOf(
        "android.permission.POST_NOTIFICATIONS" to PermissionExplanation(
            permission = "android.permission.POST_NOTIFICATIONS",
            featureName = "Alert Center & Threat Alerts",
            whyNeeded = "Required to immediately notify you if an active fraud attempt or rogue Wi-Fi is detected.",
            dataAccessed = "System notification banner and vibration.",
            isProcessedLocally = true,
            revocationGuidance = "Can be revoked at any time via Android Settings > Apps > SENTINEL AI > Notifications."
        ),
        "android.permission.READ_MEDIA_IMAGES" to PermissionExplanation(
            permission = "android.permission.READ_MEDIA_IMAGES",
            featureName = "Payment & QR Screenshot Inspection",
            whyNeeded = "Required only when you explicitly select a payment screenshot or QR image to analyze.",
            dataAccessed = "Only the single selected image file picked by the user.",
            isProcessedLocally = true,
            revocationGuidance = "Can be revoked via Android Settings > Apps > SENTINEL AI > Permissions."
        ),
        "android.permission.ACCESS_FINE_LOCATION" to PermissionExplanation(
            permission = "android.permission.ACCESS_FINE_LOCATION",
            featureName = "Wi-Fi SSID Identification",
            whyNeeded = "Android requires Location permission to inspect Wi-Fi SSID and detect rogue access points.",
            dataAccessed = "Current connected Wi-Fi Network Name.",
            isProcessedLocally = true,
            revocationGuidance = "Can be revoked via Android Settings > Location > SENTINEL AI."
        )
    )

    fun checkPermission(context: Context, permission: String): PermissionStatus {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return PermissionStatus.GRANTED
        }
        if (permission == "android.permission.POST_NOTIFICATIONS" && Build.VERSION.SDK_INT < 33) {
            return PermissionStatus.GRANTED
        }
        val result = ContextCompat.checkSelfPermission(context, permission)
        return if (result == PackageManager.PERMISSION_GRANTED) {
            PermissionStatus.GRANTED
        } else {
            PermissionStatus.DENIED
        }
    }

    fun getExplanation(permission: String): PermissionExplanation {
        return EXPLANATIONS[permission] ?: PermissionExplanation(
            permission = permission,
            featureName = "Security Capability",
            whyNeeded = "SENTINEL AI requires this capability to inspect security posture.",
            dataAccessed = "Selected sensor or metadata.",
            isProcessedLocally = true,
            revocationGuidance = "Manageable in Android Settings > Apps > SENTINEL AI."
        )
    }
}
