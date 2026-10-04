package com.cybershield.app.core.security

import android.app.KeyguardManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import com.cybershield.app.core.model.DeviceTelemetry
import java.io.File

class DeviceRepository(private val context: Context) {

    fun collectRealDeviceTelemetry(): DeviceTelemetry {
        // Battery info
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level >= 0 && scale > 0) ((level.toFloat() / scale.toFloat()) * 100).toInt() else 0
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        // Storage metrics via StatFs
        val stat = StatFs(Environment.getDataDirectory().path)
        val totalBytes = stat.totalBytes.coerceAtLeast(1L)
        val availableBytes = stat.availableBytes
        val usedBytes = totalBytes - availableBytes
        val storageUsedPct = (((usedBytes.toDouble() / totalBytes) * 100).toInt()).coerceIn(0, 100)

        // Screen lock & Keyguard security
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        val isScreenLockEnabled = keyguardManager?.isDeviceSecure ?: false

        // Storage encryption status
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
        val encryptionStatus = dpm?.storageEncryptionStatus ?: DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED
        val isStorageEncrypted = encryptionStatus == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE ||
                encryptionStatus == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER

        // Developer Options & ADB Debugging
        val isDevOptions = try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0
        } catch (_: Exception) {
            false
        }
        val isAdb = try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) != 0
        } catch (_: Exception) {
            false
        }

        // Multi-Signal Root & Compromise Detection
        val rootSignals = mutableListOf<String>()
        val suPaths = listOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/vendor/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/xbin/su",
            "/data/local/bin/su"
        )
        for (path in suPaths) {
            if (File(path).exists()) {
                rootSignals.add("Found binary: $path")
            }
        }
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            rootSignals.add("OS built with test-keys signature")
        }
        val isRoot = rootSignals.isNotEmpty()

        // Connectivity & VPN
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = cm?.activeNetwork
        val caps = cm?.getNetworkCapabilities(activeNetwork)
        val networkType = when {
            caps == null -> "NONE"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "CELLULAR"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ETHERNET"
            else -> "OTHER"
        }
        val isVpn = caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false

        // App count available through PackageManager
        val pm = context.packageManager
        val installedApps = try {
            pm.getInstalledApplications(PackageManager.GET_META_DATA).size
        } catch (_: Exception) {
            0
        }

        return DeviceTelemetry(
            manufacturer = Build.MANUFACTURER,
            brand = Build.BRAND,
            model = Build.MODEL,
            androidVersion = Build.VERSION.RELEASE,
            sdkLevel = Build.VERSION.SDK_INT,
            securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.SECURITY_PATCH else "Unavailable",
            supportedAbis = Build.SUPPORTED_ABIS.toList(),
            batteryPercent = batteryPct,
            isCharging = isCharging,
            totalStorageBytes = totalBytes,
            availableStorageBytes = availableBytes,
            storageUsedPercent = storageUsedPct,
            isScreenLockEnabled = isScreenLockEnabled,
            isStorageEncrypted = isStorageEncrypted,
            isDeveloperOptionsEnabled = isDevOptions,
            isAdbEnabled = isAdb,
            isRootDetected = isRoot,
            rootSignals = rootSignals,
            networkType = networkType,
            isVpnActive = isVpn,
            installedAppCount = installedApps
        )
    }

    fun getInstalledAppNames(): List<String> {
        val pm = context.packageManager
        return try {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
                .map { pm.getApplicationLabel(it).toString() }
                .sorted()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
