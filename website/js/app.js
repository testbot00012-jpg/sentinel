/**
 * Sentinel AI - Application Core, Navigation Router & Internationalization
 */

class SentinelApp {
  constructor() {
    this.currentTab = 'dashboard';
    this.toastContainer = document.getElementById('toast-container');
    this.bindNavigation();
    this.bindModals();
    this.bindLanguageSwitcher();
  }

  init() {
    // Check URL hash for direct routing
    const hash = window.location.hash.replace('#/', '').replace('#', '');
    if (hash && ['dashboard', 'url', 'sms', 'deepfake', 'qr', 'assistant', 'history'].includes(hash)) {
      this.switchTab(hash);
    } else {
      this.switchTab('dashboard');
    }

    // Init controllers
    if (window.authController) window.authController.init();
    if (window.dashboardController) window.dashboardController.loadDashboard();
    if (window.historyController) window.historyController.loadHistory();
  }

  bindNavigation() {
    // Nav links
    document.querySelectorAll('.nav-link').forEach(link => {
      link.addEventListener('click', (e) => {
        e.preventDefault();
        const tab = link.getAttribute('data-tab');
        if (tab) {
          this.switchTab(tab);
          window.location.hash = `#/${tab}`;
        }
      });
    });

    // Quick launcher shortcut cards
    document.querySelectorAll('.quick-action-card').forEach(card => {
      card.addEventListener('click', (e) => {
        e.preventDefault();
        const tab = card.getAttribute('data-target-tab');
        if (tab) {
          this.switchTab(tab);
          window.location.hash = `#/${tab}`;
        }
      });
    });

    window.addEventListener('hashchange', () => {
      const hash = window.location.hash.replace('#/', '').replace('#', '');
      if (hash && hash !== this.currentTab) {
        this.switchTab(hash);
      }
    });
  }

  switchTab(tabId) {
    this.currentTab = tabId;

    // Update nav links active state
    document.querySelectorAll('.nav-link').forEach(link => {
      if (link.getAttribute('data-tab') === tabId) {
        link.classList.add('active');
      } else {
        link.classList.remove('active');
      }
    });

    // Show active pane
    document.querySelectorAll('.tab-pane').forEach(pane => {
      if (pane.id === `tab-${tabId}`) {
        pane.classList.add('active');
      } else {
        pane.classList.remove('active');
      }
    });

    window.scrollTo({ top: 0, behavior: 'smooth' });

    // Refresh context if entering history or dashboard
    if (tabId === 'history' && window.historyController) {
      window.historyController.loadHistory();
    } else if (tabId === 'dashboard' && window.dashboardController) {
      window.dashboardController.loadDashboard();
    }
  }

  bindModals() {
    // Close modal on backdrop click
    document.querySelectorAll('.modal-backdrop').forEach(m => {
      m.addEventListener('click', (e) => {
        if (e.target === m) {
          m.classList.remove('open');
        }
      });
    });

    // Modal close buttons
    document.querySelectorAll('.modal-close-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        const modal = btn.closest('.modal-backdrop');
        if (modal) modal.classList.remove('open');
      });
    });
  }

  bindLanguageSwitcher() {
    const langSelect = document.getElementById('global-lang-select');
    if (!langSelect) return;

    langSelect.addEventListener('change', (e) => {
      const lang = e.target.value;
      const smsLang = document.getElementById('sms-lang-select');
      const chatLang = document.getElementById('chat-lang-select');
      if (smsLang) smsLang.value = lang;
      if (chatLang) chatLang.value = lang;

      const langNames = { en: 'English', hi: 'हिन्दी (Hindi)', te: 'తెలుగు (Telugu)' };
      window.toast(`Language switched to ${langNames[lang] || lang}`, 'info');
    });
  }

  toast(message, type = 'info') {
    if (!this.toastContainer) return;

    const el = document.createElement('div');
    el.className = `toast ${type}`;
    const icon = type === 'success' ? '✅' : (type === 'error' ? '❌' : 'ℹ️');

    el.innerHTML = `
      <span>${icon}</span>
      <span>${message}</span>
    `;

    this.toastContainer.appendChild(el);

    setTimeout(() => {
      el.style.opacity = '0';
      el.style.transform = 'translateY(10px)';
      el.style.transition = 'all 0.3s ease';
      setTimeout(() => el.remove(), 300);
    }, 3800);
  }
}

window.app = new SentinelApp();
window.toast = (msg, type) => window.app.toast(msg, type);

// Initialize on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  window.app.init();
});
