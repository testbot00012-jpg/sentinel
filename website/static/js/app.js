/**
 * Sentinel AI - Core Application & Global Multi-Page Utilities
 */

class SentinelApp {
  constructor() {
    this.toastContainer = document.getElementById('toast-container');
    this.bindNavigation();
    this.bindModals();
    this.bindLanguageSwitcher();
  }

  init() {
    // Highlight active nav item based on current page filename
    const currentPath = window.location.pathname.split('/').pop() || 'index.html';
    document.querySelectorAll('.nav-link').forEach(link => {
      const href = link.getAttribute('href');
      if (href && (href === currentPath || (currentPath === '' && href === 'index.html'))) {
        link.classList.add('active');
      } else {
        link.classList.remove('active');
      }
    });

    // Apply active language across all DOM elements
    if (window.applyGlobalLanguage) {
      const savedLang = localStorage.getItem('sentinel_preferred_lang') || 'en';
      window.applyGlobalLanguage(savedLang);
    }

    // Initialize Auth state on all pages
    if (window.authController) {
      window.authController.init();
    }
  }

  bindNavigation() {
    // Quick launcher shortcut cards on index.html
    document.querySelectorAll('.quick-action-card').forEach(card => {
      card.addEventListener('click', (e) => {
        const href = card.getAttribute('data-href');
        if (href) {
          window.location.href = href;
        }
      });
    });
  }

  bindModals() {
    document.querySelectorAll('.modal-backdrop').forEach(m => {
      m.addEventListener('click', (e) => {
        if (e.target === m) {
          m.classList.remove('open');
        }
      });
    });

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

    // Restore saved language if any
    const savedLang = localStorage.getItem('sentinel_preferred_lang') || 'en';
    langSelect.value = savedLang;

    langSelect.addEventListener('change', (e) => {
      const lang = e.target.value;
      if (window.applyGlobalLanguage) {
        window.applyGlobalLanguage(lang);
      } else {
        localStorage.setItem('sentinel_preferred_lang', lang);
      }

      const langNames = { en: 'English', hi: 'हिन्दी (Hindi)', te: 'తెలుగు (Telugu)' };
      this.toast(`Language set to ${langNames[lang] || lang}`, 'info');
    });
  }

  toast(message, type = 'info') {
    if (!this.toastContainer) {
      this.toastContainer = document.getElementById('toast-container');
      if (!this.toastContainer) {
        this.toastContainer = document.createElement('div');
        this.toastContainer.id = 'toast-container';
        this.toastContainer.className = 'toast-container';
        document.body.appendChild(this.toastContainer);
      }
    }

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

  openScanModal() {
    const modal = document.getElementById('scan-options-modal');
    if (modal) {
      modal.classList.add('open');
    }
  }

  closeScanModal() {
    const modal = document.getElementById('scan-options-modal');
    if (modal) {
      modal.classList.remove('open');
    }
  }
}

window.app = new SentinelApp();
window.sentinelApp = window.app;
window.toast = (msg, type) => window.app.toast(msg, type);

document.addEventListener('DOMContentLoaded', () => {
  window.app.init();
});
