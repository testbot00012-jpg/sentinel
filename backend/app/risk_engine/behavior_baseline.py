import math
from typing import Dict, Any, List, Tuple
from datetime import datetime, timezone
from sklearn.ensemble import IsolationForest
import numpy as np

class AdaptiveBehavioralBaselineEngine:
    def __init__(self):
        self.version = "1.1.0"

    def evaluate_behavior(
        self,
        baseline_record: Dict[str, Any],
        current_event: Dict[str, Any]
    ) -> Tuple[float, bool, List[str]]:
        """
        Evaluates a device's current event against its isolated baseline.
        Returns: (anomaly_score: float 0.0-1.0, is_anomaly: bool, deviation_features: List[str])
        """
        deviation_features = []
        anomaly_points = 0.0

        typical_networks = set(baseline_record.get("typical_network_types", ["WIFI_HOME", "CELLULAR"]))
        current_network = current_event.get("network_type", "WIFI")
        current_ssid = current_event.get("ssid")

        # 1. Network transition anomaly
        if current_network not in typical_networks and current_network == "WIFI_OPEN":
            anomaly_points += 0.35
            deviation_features.append(f"Unusual network transition to unencrypted Wi-Fi '{current_ssid or 'Unknown'}'")

        # 2. Sudden surge in high-risk permissions
        typical_perm_count = baseline_record.get("typical_permission_count", 15)
        current_perm_count = current_event.get("dangerous_permission_count", 0)
        if current_perm_count > (typical_perm_count * 1.5) and current_perm_count > 20:
            anomaly_points += 0.30
            deviation_features.append(f"Dangerous permissions increased significantly ({current_perm_count} active vs baseline {typical_perm_count})")

        # 3. Sideload frequency deviation
        sideloaded_app = current_event.get("is_sideloaded_app", False)
        if sideloaded_app and not baseline_record.get("allows_sideloading", False):
            anomaly_points += 0.40
            deviation_features.append("Installation of sideloaded package deviates from device normal play store policy")

        # 4. Rapid threat event velocity
        recent_scans_failed = current_event.get("recent_threats_detected", 0)
        if recent_scans_failed >= 2:
            anomaly_points += 0.35
            deviation_features.append(f"Cluster of {recent_scans_failed} security threats detected within short temporal window")

        normalized_score = min(1.0, anomaly_points)
        is_anomaly = normalized_score >= 0.50

        return round(normalized_score, 2), is_anomaly, deviation_features

    def update_baseline(
        self,
        current_baseline: Dict[str, Any],
        new_event: Dict[str, Any]
    ) -> Dict[str, Any]:
        """
        Gradually adapts moving statistical averages without catastrophic forgetting.
        """
        events_count = current_baseline.get("baseline_events_count", 0) + 1
        alpha = 1.0 / min(events_count, 50)  # Exponential moving weight

        current_risk_avg = current_baseline.get("average_risk_score", 15.0)
        new_risk = float(new_event.get("risk_score", 15.0))
        updated_risk_avg = (1.0 - alpha) * current_risk_avg + alpha * new_risk

        updated_baseline = current_baseline.copy()
        updated_baseline["average_risk_score"] = round(updated_risk_avg, 2)
        updated_baseline["baseline_events_count"] = events_count
        updated_baseline["last_updated_at"] = datetime.now(timezone.utc).isoformat()
        
        return updated_baseline
