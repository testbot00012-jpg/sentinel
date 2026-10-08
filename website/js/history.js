/**
 * Sentinel AI - Audit History & Cross-Device Forensic Logs Controller
 */

class HistoryController {
  constructor() {
    this.tableBody = document.getElementById('history-table-body');
    this.searchInput = document.getElementById('history-search-input');
    this.filterButtons = document.querySelectorAll('.history-filter-tabs .filter-btn');
    this.modal = document.getElementById('audit-modal');
    this.modalContent = document.getElementById('audit-modal-content');

    this.currentFilter = 'ALL';
    this.allScans = [];

    this.bindEvents();
  }

  bindEvents() {
    this.filterButtons.forEach(btn => {
      btn.addEventListener('click', () => {
        this.filterButtons.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        this.currentFilter = btn.getAttribute('data-filter') || 'ALL';
        this.renderTable();
      });
    });

    if (this.searchInput) {
      this.searchInput.addEventListener('input', () => this.renderTable());
    }

    const refreshBtn = document.getElementById('btn-refresh-history');
    if (refreshBtn) {
      refreshBtn.addEventListener('click', () => this.loadHistory(true));
    }

    document.addEventListener('sentinel-language-changed', () => {
      this.renderTable();
    });
  }

  async loadHistory(showToast = false) {
    if (!this.tableBody) return;
    if (showToast) window.toast('Refreshing cross-device audit history...', 'info');

    try {
      const scans = await window.api.getScanHistory();
      this.allScans = Array.isArray(scans) ? scans : [];
      this.renderTable();
      if (showToast) window.toast(`Loaded ${this.allScans.length} historical scans.`, 'success');
      this.startRealtimeHistorySync();
    } catch (err) {
      console.warn('History load failed:', err);
      this.tableBody.innerHTML = `<tr><td colspan="6" style="text-align:center;color:var(--text-sub);padding:2rem;">Could not load scan logs (${err.message})</td></tr>`;
    }
  }

  startRealtimeHistorySync() {
    if (this.syncTimer) clearInterval(this.syncTimer);
    this.syncTimer = setInterval(async () => {
      try {
        const scans = await window.api.getScanHistory();
        if (Array.isArray(scans) && scans.length !== this.allScans.length) {
          this.allScans = scans;
          this.renderTable();
          window.toast('📱 Real-Time Sync: New scan record received from Android', 'success');
        }
      } catch (e) {}
    }, 3000);
  }

  renderTable() {
    if (!this.tableBody) return;

    const searchTerm = (this.searchInput ? this.searchInput.value : '').toLowerCase().trim();

    let filtered = this.allScans.filter(s => {
      // Type filtering
      if (this.currentFilter !== 'ALL') {
        const type = (s.scan_type || '').toUpperCase();
        if (this.currentFilter === 'URL' && !type.includes('URL')) return false;
        if (this.currentFilter === 'MESSAGE' && !type.includes('MESSAGE') && !type.includes('SMS')) return false;
        if (this.currentFilter === 'DEEPFAKE' && !type.includes('DEEPFAKE') && !type.includes('MEDIA')) return false;
        if (this.currentFilter === 'QR' && !type.includes('QR') && !type.includes('PAYMENT')) return false;
      }

      // Search term
      if (searchTerm) {
        const target = (s.target_identifier || '').toLowerCase();
        const summary = (s.summary || '').toLowerCase();
        const type = (s.scan_type || '').toLowerCase();
        const dev = (s.device_name || '').toLowerCase();
        return target.includes(searchTerm) || summary.includes(searchTerm) || type.includes(searchTerm) || dev.includes(searchTerm);
      }

      return true;
    });

    if (filtered.length === 0) {
      const emptyTitle = window.t ? window.t("No Scan Records Found") : "No Scan Records Found";
      const emptyDesc = window.t ? window.t("Run scans from the dashboard or your linked Android phone to see synced logs here.") : "Run scans from the dashboard or your linked Android phone to see synced logs here.";
      this.tableBody.innerHTML = `
        <tr>
          <td colspan="6" style="text-align:center;padding:3rem;color:var(--text-sub);">
            <div style="font-size:2rem;margin-bottom:0.5rem;">📂</div>
            <p style="color:var(--text-main);font-weight:600;">${emptyTitle}</p>
            <p style="font-size:0.8rem;">${emptyDesc}</p>
          </td>
        </tr>
      `;
      return;
    }

    this.tableBody.innerHTML = filtered.map(s => {
      const isCritical = s.risk_level === 'CRITICAL' || s.risk_level === 'HIGH_RISK';
      const badgeCls = isCritical ? 'critical' : (s.risk_level === 'SAFE' ? 'safe' : 'suspicious');
      const isWeb = (s.device_brand || '').toUpperCase() === 'WEB' || (s.device_name || '').includes('Web');
      const devIcon = isWeb ? '💻' : '📱';
      const devTagCls = isWeb ? 'web' : 'android';
      const dateStr = s.created_at ? new Date(s.created_at).toLocaleString() : 'Recent';
      const localizedType = window.t ? (window.t(s.scan_type) || s.scan_type.replace('_', ' ')) : s.scan_type.replace('_', ' ');
      const localizedRisk = window.t ? (window.t(s.risk_level) || s.risk_level) : s.risk_level;

      return `
        <tr onclick="window.historyController.inspectScan('${s.id}')">
          <td style="color:var(--text-sub);font-size:0.8rem;white-space:nowrap;">${dateStr}</td>
          <td>
            <span class="device-source-tag ${devTagCls}">
              ${devIcon} ${s.device_name || (isWeb ? 'Web Console' : 'Android Phone')}
            </span>
          </td>
          <td>
            <span style="font-weight:600;font-size:0.85rem;color:var(--text-main);">
              ${localizedType}
            </span>
          </td>
          <td style="max-width:260px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">
            <code style="font-size:0.8rem;color:var(--cyan-primary);">${s.target_identifier || 'Payload Target'}</code>
          </td>
          <td>
            <span class="badge-level ${badgeCls}">${localizedRisk}</span>
          </td>
          <td>
            <span style="font-weight:700;color:${s.risk_level === 'SAFE' ? '#34d399' : '#f87171'};font-family:var(--font-mono);">
              ${s.security_score || (100 - (s.risk_score || 0))}/100
            </span>
          </td>
        </tr>
      `;
    }).join('');
  }

  async inspectScan(scanId) {
    if (!this.modal || !this.modalContent) return;
    this.modal.classList.add('open');
    this.modalContent.innerHTML = `<div style="text-align:center;padding:3rem;color:var(--text-muted);"><span class="sync-icon-spin" style="font-size:2rem;display:inline-block;margin-bottom:0.75rem;">🔍</span><br>Loading detailed forensic telemetry report...</div>`;

    try {
      const scan = await window.api.getScanById(scanId);
      const isSafe = scan.risk_level === 'SAFE';
      const badgeCls = isSafe ? 'safe' : (scan.risk_level === 'SUSPICIOUS' ? 'suspicious' : 'critical');
      const localizedType = window.t ? (window.t(scan.scanner_type) || scan.scanner_type.replace('_', ' ')) : scan.scanner_type.replace('_', ' ');
      const localizedRisk = window.t ? (window.t(scan.risk_level) || scan.risk_level) : scan.risk_level;
      const localizedExplanation = window.translateReport ? window.translateReport(scan.explanation || scan.summary) : (scan.explanation || scan.summary);

      this.modalContent.innerHTML = `
        <div class="verdict-banner ${badgeCls}" style="margin-bottom:1.5rem;">
          <div class="verdict-icon-box" style="font-size:1.5rem;">${isSafe ? '✅' : '⚠️'}</div>
          <div class="verdict-text-content">
            <h3>${localizedType}: ${localizedRisk}</h3>
            <p>${localizedExplanation}</p>
          </div>
        </div>

        <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:1rem;margin-bottom:1.5rem;">
          <div class="feed-item" style="text-align:center;flex-direction:column;padding:0.75rem;">
            <span style="font-size:0.72rem;color:var(--text-sub);text-transform:uppercase;">${window.t ? window.t("Security Score") : "Security Score"}</span>
            <span style="font-size:1.6rem;font-weight:800;color:${isSafe ? '#34d399' : '#f87171'}">${scan.security_score}/100</span>
          </div>
          <div class="feed-item" style="text-align:center;flex-direction:column;padding:0.75rem;">
            <span style="font-size:0.72rem;color:var(--text-sub);text-transform:uppercase;">${window.t ? window.t("Model Confidence") : "Model Confidence"}</span>
            <span style="font-size:1.4rem;font-weight:700;color:var(--cyan-primary);">${Math.round(scan.confidence * 100)}%</span>
          </div>
          <div class="feed-item" style="text-align:center;flex-direction:column;padding:0.75rem;">
            <span style="font-size:0.72rem;color:var(--text-sub);text-transform:uppercase;">${window.t ? window.t("Threat Classification") : "Classification"}</span>
            <span class="badge-level ${badgeCls}" style="margin-top:0.35rem;">${localizedRisk}</span>
          </div>
        </div>

        <div class="signals-container">
          <h4 class="signals-title">${window.t ? window.t("Extracted Forensic Signals") : "Observed Threat Signals"} (${(scan.signals || []).length})</h4>
          ${(scan.signals || []).length > 0 ? (scan.signals || []).map(s => `
            <div class="signal-chip ${s.severity.toLowerCase()}">
              <div class="signal-chip-header">
                <span class="signal-name">${s.name}</span>
                <span class="badge-level ${s.severity.toLowerCase()}">${window.t ? window.t(s.severity) : s.severity}</span>
              </div>
              <p class="signal-desc">${window.translateReport ? window.translateReport(s.description) : s.description}</p>
              ${s.evidence_value ? `<div class="signal-ev">Evidence: ${s.evidence_value}</div>` : ''}
            </div>
          `).join('') : `<p style="color:var(--text-sub);font-size:0.85rem;">${window.t ? window.t("No active threats detected. Run security scans to inspect targets and verify endpoint health.") : "No malicious indicators detected. Clean scan."}</p>`}
        </div>

        ${(scan.recommended_actions || []).length > 0 ? `
          <div style="background:rgba(255,255,255,0.025);border:1px solid var(--border-subtle);border-radius:var(--radius-sm);padding:1rem;margin:1.25rem 0;">
            <h4 style="font-size:0.85rem;color:var(--text-main);margin-bottom:0.4rem;text-transform:uppercase;letter-spacing:0.04em;">${window.t ? window.t("Recommended Security Actions") : "Recommended Actions"}</h4>
            <ul style="font-size:0.85rem;color:var(--text-muted);margin-left:1.2rem;line-height:1.5;">
              ${scan.recommended_actions.map(r => `<li>${window.translateReport ? window.translateReport(r) : r}</li>`).join('')}
            </ul>
          </div>
        ` : ''}

        <div style="display:flex;justify-content:space-between;align-items:center;padding-top:1.25rem;border-top:1px solid var(--border-subtle);margin-top:1.5rem;">
          <span style="font-size:0.75rem;color:var(--text-sub);font-family:var(--font-mono);">ID: ${scan.scan_id}</span>
          <button class="btn btn-outline-cyan btn-sm" onclick="window.historyController.closeModal(); window.assistantController.discussScan('${scan.scan_id}')">
            💬 Discuss with Sentinel AI Assistant
          </button>
        </div>
      `;
    } catch (err) {
      this.modalContent.innerHTML = `<div style="padding:2rem;color:#f87171;">Could not load report details: ${err.message}</div>`;
    }
  }

  closeModal() {
    if (this.modal) this.modal.classList.remove('open');
  }
}

window.historyController = new HistoryController();
