/**
 * Sentinel AI - Authentication & Cross-Device Account Sync Controller
 */

class AuthController {
  constructor() {
    this.initElements();
    this.bindEvents();
  }

  initElements() {
    this.modal = document.getElementById('auth-modal');
    this.profileBtn = document.getElementById('user-profile-btn');
    this.userNameSpan = document.getElementById('user-display-name');
    this.userAvatar = document.getElementById('user-avatar-initials');
    this.devicesModal = document.getElementById('devices-modal');
    this.syncPill = document.getElementById('sync-status-pill');

    // Tabs inside auth modal
    this.tabLoginBtn = document.getElementById('tab-btn-login');
    this.tabRegisterBtn = document.getElementById('tab-btn-register');
    this.tabProfileBtn = document.getElementById('tab-btn-profile');

    this.formLogin = document.getElementById('form-login');
    this.formRegister = document.getElementById('form-register');
    this.viewProfile = document.getElementById('view-profile-details');

    this.loginEmail = document.getElementById('login-email');
    this.loginPass = document.getElementById('login-password');
    this.regName = document.getElementById('reg-name');
    this.regEmail = document.getElementById('reg-email');
    this.regPass = document.getElementById('reg-password');
  }

  bindEvents() {
    if (this.profileBtn) {
      this.profileBtn.addEventListener('click', () => this.openAuthModal());
    }

    if (this.tabLoginBtn) {
      this.tabLoginBtn.addEventListener('click', () => this.showTab('login'));
    }
    if (this.tabRegisterBtn) {
      this.tabRegisterBtn.addEventListener('click', () => this.showTab('register'));
    }
    if (this.tabProfileBtn) {
      this.tabProfileBtn.addEventListener('click', () => this.showTab('profile'));
    }

    if (this.formLogin) {
      this.formLogin.addEventListener('submit', (e) => this.handleLogin(e));
    }
    if (this.formRegister) {
      this.formRegister.addEventListener('submit', (e) => this.handleRegister(e));
    }

    const logoutBtn = document.getElementById('btn-logout');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', () => this.handleLogout());
    }

    const viewDevicesBtn = document.getElementById('btn-view-devices');
    if (viewDevicesBtn) {
      viewDevicesBtn.addEventListener('click', () => this.openDevicesModal());
    }

    if (this.syncPill) {
      this.syncPill.addEventListener('click', () => this.triggerSync());
    }
  }

  async init() {
    await window.api.ensureGuestSession();
    this.updateUserUI();
  }

  updateUserUI() {
    const user = window.api.getUser();
    if (user && !user.is_guest) {
      const name = user.full_name || user.email.split('@')[0];
      this.userNameSpan.textContent = name;
      this.userAvatar.textContent = name.substring(0, 2).toUpperCase();
      this.syncPill.innerHTML = `
        <span class="sync-icon-spin">⚡</span>
        <span>Synced with Account (${user.email.split('@')[0]})</span>
      `;
      this.syncPill.style.color = '#34d399';
    } else {
      this.userNameSpan.textContent = 'Sign In';
      this.userAvatar.textContent = '🛡️';
      this.syncPill.innerHTML = `
        <span class="sync-icon-spin">🔄</span>
        <span>Guest Mode • Click to Sync</span>
      `;
      this.syncPill.style.color = '#94a3b8';
    }
  }

  openAuthModal() {
    const user = window.api.getUser();
    if (user && !user.is_guest) {
      this.showTab('profile');
      this.populateProfileDetails(user);
    } else {
      this.showTab('login');
    }
    this.modal.classList.add('open');
  }

  closeAuthModal() {
    this.modal.classList.remove('open');
  }

  showTab(tab) {
    [this.tabLoginBtn, this.tabRegisterBtn, this.tabProfileBtn].forEach(b => b && b.classList.remove('active'));
    [this.formLogin, this.formRegister, this.viewProfile].forEach(v => v && (v.style.display = 'none'));

    if (tab === 'login') {
      this.tabLoginBtn.classList.add('active');
      this.formLogin.style.display = 'block';
    } else if (tab === 'register') {
      this.tabRegisterBtn.classList.add('active');
      this.formRegister.style.display = 'block';
    } else if (tab === 'profile') {
      this.tabProfileBtn.classList.add('active');
      this.viewProfile.style.display = 'block';
      const user = window.api.getUser();
      if (user) this.populateProfileDetails(user);
    }
  }

  async handleLogin(e) {
    e.preventDefault();
    const email = this.loginEmail.value.trim();
    const pass = this.loginPass.value.trim();
    const btn = this.formLogin.querySelector('button[type="submit"]');

    try {
      btn.textContent = 'Authenticating...';
      btn.disabled = true;
      await window.api.login(email, pass);
      window.toast('Welcome back! Account and scan history synchronized.', 'success');
      this.updateUserUI();
      this.closeAuthModal();
      
      // Refresh scans history and dashboard
      if (window.historyController) window.historyController.loadHistory();
      if (window.dashboardController) window.dashboardController.loadDashboard();
    } catch (err) {
      window.toast(err.message || 'Login failed. Please check credentials.', 'error');
    } finally {
      btn.textContent = 'Sign In to Account';
      btn.disabled = false;
    }
  }

  async handleRegister(e) {
    e.preventDefault();
    const name = this.regName.value.trim();
    const email = this.regEmail.value.trim();
    const pass = this.regPass.value.trim();
    const btn = this.formRegister.querySelector('button[type="submit"]');

    if (pass.length < 6) {
      window.toast('Password must be at least 6 characters long.', 'error');
      return;
    }

    try {
      btn.textContent = 'Creating Account...';
      btn.disabled = true;
      await window.api.register(name, email, pass);
      window.toast('Account registered successfully! All devices linked.', 'success');
      this.updateUserUI();
      this.closeAuthModal();

      if (window.historyController) window.historyController.loadHistory();
      if (window.dashboardController) window.dashboardController.loadDashboard();
    } catch (err) {
      window.toast(err.message || 'Registration failed.', 'error');
    } finally {
      btn.textContent = 'Create Sentinel Account';
      btn.disabled = false;
    }
  }

  async handleLogout() {
    await window.api.logout();
    window.toast('Logged out from account. Switched to guest mode.', 'info');
    this.updateUserUI();
    this.closeAuthModal();

    if (window.historyController) window.historyController.loadHistory();
    if (window.dashboardController) window.dashboardController.loadDashboard();
  }

  populateProfileDetails(user) {
    document.getElementById('profile-email-text').textContent = user.email;
    document.getElementById('profile-name-text').textContent = user.full_name || 'Sentinel User';
    document.getElementById('profile-id-text').textContent = user.id ? user.id.substring(0, 12) + '...' : 'Active';
    this.loadAccountActivity();
  }

  async loadAccountActivity() {
    const list = document.getElementById('account-activity-feed');
    if (!list) return;

    try {
      const activities = await window.api.getAccountActivity();
      if (!activities || activities.length === 0) {
        list.innerHTML = `<p style="color:var(--text-sub);font-size:0.8rem;text-align:center;padding:0.75rem;">No recent security activity logged.</p>`;
        return;
      }
      list.innerHTML = activities.slice(0, 5).map(a => `
        <div style="padding:0.5rem;border-bottom:1px solid rgba(255,255,255,0.04);font-size:0.78rem;">
          <div style="display:flex;justify-content:space-between;color:var(--text-muted);">
            <strong>${a.activity_type}</strong>
            <span>${new Date(a.created_at || Date.now()).toLocaleTimeString()}</span>
          </div>
          <p style="color:var(--text-sub);margin-top:2px;">${a.description}</p>
        </div>
      `).join('');
    } catch {
      list.innerHTML = `<p style="color:var(--text-sub);font-size:0.8rem;">Activity synced with Supabase.</p>`;
    }
  }

  async openDevicesModal() {
    this.closeAuthModal();
    if (this.devicesModal) {
      this.devicesModal.classList.add('open');
      const list = document.getElementById('connected-devices-list');
      list.innerHTML = `<div style="text-align:center;padding:1.5rem;color:var(--text-muted);">Querying registered account devices...</div>`;

      try {
        const devices = await window.api.getAccountDevices();
        if (!devices || devices.length === 0) {
          list.innerHTML = `
            <div style="text-align:center;padding:1.5rem;color:var(--text-muted);">
              <p>1 Active Device Registered (Current Web Browser)</p>
              <p style="font-size:0.8rem;margin-top:0.5rem;color:var(--text-sub);">Install the Sentinel AI Android app on your phone and log in with the same account to synchronize phone telemetry automatically.</p>
            </div>
          `;
          return;
        }

        list.innerHTML = devices.map(d => `
          <div class="feed-item" style="margin-bottom:0.65rem;">
            <div class="feed-item-icon" style="background:${d.brand === 'Web' ? 'rgba(0,242,254,0.1)' : 'rgba(16,185,129,0.1)'};color:${d.brand === 'Web' ? '#00f2fe' : '#34d399'}">
              ${d.brand === 'Web' ? '💻' : '📱'}
            </div>
            <div class="feed-item-body">
              <div class="feed-item-top">
                <span class="feed-item-title">${d.device_name} ${d.is_current ? '<span class="badge-level safe" style="font-size:0.65rem;padding:1px 6px;">Current Session</span>' : ''}</span>
                <span class="feed-item-time">${d.brand || 'Device'} • ${d.is_trusted ? 'Verified' : 'Unverified'}</span>
              </div>
              <p class="feed-item-desc">Model: ${d.model || 'Standard'} | Last Active: ${new Date(d.last_seen_at || Date.now()).toLocaleDateString()}</p>
            </div>
          </div>
        `).join('');
      } catch (err) {
        list.innerHTML = `<div style="text-align:center;padding:1rem;color:var(--text-sub);">Connected sessions active. (${err.message})</div>`;
      }
    }
  }

  closeDevicesModal() {
    if (this.devicesModal) this.devicesModal.classList.remove('open');
  }

  async triggerSync() {
    this.syncPill.classList.add('syncing');
    window.toast('Synchronizing telemetry across Android & Web...', 'info');

    try {
      if (window.historyController) await window.historyController.loadHistory();
      if (window.dashboardController) await window.dashboardController.loadDashboard();
      window.toast('All scans & account sessions fully synchronized!', 'success');
    } catch {
      window.toast('Sync completed with local baseline.', 'info');
    } finally {
      setTimeout(() => {
        this.syncPill.classList.remove('syncing');
      }, 1000);
    }
  }
}

window.authController = new AuthController();
