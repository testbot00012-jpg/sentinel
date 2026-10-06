/**
 * Sentinel AI - Security Posture & Dynamic Score Dashboard Controller
 */

class DashboardController {
  constructor() {
    this.scoreNumber = document.getElementById('dash-score-number');
    this.scoreCircle = document.getElementById('dash-score-circle');
    this.postureTitle = document.getElementById('dash-posture-title');
    this.postureDesc = document.getElementById('dash-posture-desc');

    this.statTotalScans = document.getElementById('stat-total-scans');
    this.statThreatsBlocked = document.getElementById('stat-threats-blocked');
    this.statCleanItems = document.getElementById('stat-clean-items');
    this.statActiveDevices = document.getElementById('stat-active-devices');

    this.alertsContainer = document.getElementById('dash-alerts-feed');
    this.activityContainer = document.getElementById('dash-recent-scans-feed');

    this.breakdownWeb = document.getElementById('bar-web');
    this.breakdownApp = document.getElementById('bar-app');
    this.breakdownDevice = document.getElementById('bar-device');
    this.breakdownAccount = document.getElementById('bar-account');
  }

  async loadDashboard() {
    try {
      const [scoreData, historyData, alertsData, devicesData] = await Promise.all([
        window.api.getSecurityScore().catch(() => null),
        window.api.getScanHistory().catch(() => []),
        window.api.getAlerts().catch(() => []),
        window.api.getAccountDevices().catch(() => [])
      ]);

      this.renderScore(scoreData, historyData);
      this.renderStats(historyData, alertsData, devicesData);
      this.renderAlerts(alertsData);
      this.renderRecentActivity(historyData);
    } catch (err) {
      console.warn('Dashboard load error:', err);
    }
  }

  renderScore(scoreData, historyData) {
    const hasHistory = Array.isArray(historyData) && historyData.length > 0;
    // Calculate or use real score from backend; if 0 scans, clean 100 baseline
    const score = (scoreData && scoreData.overall_score !== undefined) 
      ? scoreData.overall_score 
      : (hasHistory ? 85 : 100);
    
    // Animate score number
    this.animateNumber(this.scoreNumber, score);

    // Circle circumference is 2 * PI * r = 2 * 3.14159 * 90 ≈ 565
    const circumference = 565;
    const offset = circumference - (score / 100) * circumference;
    
    if (this.scoreCircle) {
      this.scoreCircle.style.strokeDashoffset = offset;
      
      if (!hasHistory) {
        this.scoreCircle.style.stroke = '#10b981'; // Green
        this.postureTitle.textContent = 'CLEAN SECURITY BASELINE';
        this.postureTitle.style.color = '#34d399';
        this.postureDesc.textContent = 'No threats recorded for this account. Run scans using the modules below to analyze posture.';
      } else if (score >= 85) {
        this.scoreCircle.style.stroke = '#10b981'; // Green
        this.postureTitle.textContent = 'EXCELLENT DEFENSE POSTURE';
        this.postureTitle.style.color = '#34d399';
        this.postureDesc.textContent = 'All analyzed targets and endpoints conform to verified safety baselines.';
      } else if (score >= 60) {
        this.scoreCircle.style.stroke = '#f59e0b'; // Amber
        this.postureTitle.textContent = 'MODERATE EXPOSURE DETECTED';
        this.postureTitle.style.color = '#fbbf24';
        this.postureDesc.textContent = 'Suspicious artifacts or recent alerts detected. Review flagged items in the history audit.';
      } else {
        this.scoreCircle.style.stroke = '#ef4444'; // Red
        this.postureTitle.textContent = 'CRITICAL RISKS DETECTED';
        this.postureTitle.style.color = '#f87171';
        this.postureDesc.textContent = 'Urgent high-risk threats detected across active devices. Immediate remediation required.';
      }
    }

    // Breakdown bars
    if (scoreData && scoreData.breakdown) {
      const b = scoreData.breakdown;
      this.updateBar('bar-web', 'val-web', b.web_phishing ?? 100, '#00f2fe');
      this.updateBar('bar-app', 'val-app', b.app_security ?? 100, '#6366f1');
      this.updateBar('bar-device', 'val-device', b.device_posture ?? 100, '#10b981');
      this.updateBar('bar-account', 'val-account', b.account_security ?? 100, '#f59e0b');
    }
  }

  updateBar(barId, valId, score, color) {

    const bar = document.getElementById(barId);
    const val = document.getElementById(valId);
    if (bar) {
      bar.style.width = `${score}%`;
      bar.style.backgroundColor = color;
    }
    if (val) {
      val.textContent = score;
    }
  }

  renderStats(scans, alerts, devices) {
    const total = Array.isArray(scans) ? scans.length : 0;
    const threats = Array.isArray(scans) ? scans.filter(s => s.risk_level === 'CRITICAL' || s.risk_level === 'HIGH_RISK').length : 0;
    const clean = total - threats;
    const deviceCount = Array.isArray(devices) && devices.length > 0 ? devices.length : 1;

    this.animateNumber(this.statTotalScans, total);
    this.animateNumber(this.statThreatsBlocked, threats);
    this.animateNumber(this.statCleanItems, clean);
    this.animateNumber(this.statActiveDevices, deviceCount);
  }

  renderAlerts(alerts) {
    if (!this.alertsContainer) return;

    if (!Array.isArray(alerts) || alerts.length === 0) {
      this.alertsContainer.innerHTML = `
        <div style="text-align:center;padding:2rem 1rem;color:var(--text-sub);">
          <div style="font-size:2rem;margin-bottom:0.5rem;">🛡️</div>
          <p style="font-weight:600;color:var(--text-main);">No Active Security Alerts</p>
          <p style="font-size:0.8rem;">All endpoints and inspected media conform to baseline safety guidelines.</p>
        </div>
      `;
      return;
    }

    this.alertsContainer.innerHTML = alerts.map(a => `
      <div class="feed-item">
        <div class="feed-item-icon" style="background:rgba(239,68,68,0.15);color:#f87171;">
          ⚠️
        </div>
        <div class="feed-item-body">
          <div class="feed-item-top">
            <span class="feed-item-title">${a.title}</span>
            <span class="badge-level critical" style="font-size:0.65rem;">${a.severity}</span>
          </div>
          <p class="feed-item-desc">${a.description}</p>
          <div style="margin-top:0.4rem;display:flex;justify-content:space-between;align-items:center;">
            <span style="font-size:0.72rem;color:var(--text-sub);">${new Date(a.created_at || Date.now()).toLocaleTimeString()}</span>
            <button class="btn btn-secondary btn-sm" onclick="window.dashboardController.dismissAlert('${a.id}')" style="padding:2px 8px;font-size:0.72rem;">Dismiss</button>
          </div>
        </div>
      </div>
    `).join('');
  }

  async dismissAlert(alertId) {
    try {
      await window.api.dismissAlert(alertId);
      window.toast('Alert dismissed.', 'info');
      this.loadDashboard();
    } catch (err) {
      window.toast('Could not dismiss alert.', 'error');
    }
  }

  renderRecentActivity(scans) {
    if (!this.activityContainer) return;

    if (!Array.isArray(scans) || scans.length === 0) {
      this.activityContainer.innerHTML = `
        <div style="text-align:center;padding:2rem 1rem;color:var(--text-sub);">
          <div style="font-size:2rem;margin-bottom:0.5rem;">🔍</div>
          <p style="font-weight:600;color:var(--text-main);">No Recent Scans Found</p>
          <p style="font-size:0.8rem;">Run your first URL, SMS, Deepfake, or QR test using the quick action launcher above.</p>
        </div>
      `;
      return;
    }

    this.activityContainer.innerHTML = scans.slice(0, 5).map(s => {
      const isThreat = s.risk_level === 'CRITICAL' || s.risk_level === 'HIGH_RISK';
      const badgeCls = isThreat ? 'critical' : (s.risk_level === 'SAFE' ? 'safe' : 'suspicious');
      const icon = s.scan_type.includes('URL') ? '🌐' : (s.scan_type.includes('MESSAGE') || s.scan_type.includes('SMS') ? '💬' : (s.scan_type.includes('DEEPFAKE') ? '👁️' : '📷'));

      return `
        <div class="feed-item" onclick="window.historyController.inspectScan('${s.id}')">
          <div class="feed-item-icon" style="background:${isThreat ? 'rgba(239,68,68,0.1)' : 'rgba(16,185,129,0.1)'};">
            ${icon}
          </div>
          <div class="feed-item-body">
            <div class="feed-item-top">
              <span class="feed-item-title">${s.scan_type.replace('_', ' ')}: <code style="font-size:0.8rem;color:var(--cyan-primary);">${(s.target_identifier || 'Target').substring(0, 24)}</code></span>
              <span class="badge-level ${badgeCls}">${s.risk_level}</span>
            </div>
            <p class="feed-item-desc">${s.summary || 'Security inspection completed.'}</p>
            <div style="margin-top:0.3rem;display:flex;justify-content:space-between;font-size:0.72rem;color:var(--text-sub);">
              <span>Score: ${s.security_score || (100 - (s.risk_score || 0))}/100</span>
              <span class="device-source-tag ${s.device_brand === 'Web' ? 'web' : 'android'}">${s.device_name || 'Device'}</span>
            </div>
          </div>
        </div>
      `;
    }).join('');
  }

  animateNumber(el, targetVal) {
    if (!el) return;
    const startVal = parseInt(el.textContent) || 0;
    if (startVal === targetVal) {
      el.textContent = targetVal;
      return;
    }
    const duration = 600;
    const startTime = performance.now();

    function update(now) {
      const progress = Math.min((now - startTime) / duration, 1);
      const curr = Math.round(startVal + (targetVal - startVal) * progress);
      el.textContent = curr;
      if (progress < 1) requestAnimationFrame(update);
    }
    requestAnimationFrame(update);
  }
}

window.dashboardController = new DashboardController();
