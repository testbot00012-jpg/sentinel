/**
 * Sentinel AI - Unified Web API Client
 * Connects directly to FastAPI backend and Supabase session synchronization.
 */

class SentinelApiClient {
  constructor() {
    // Connect directly to Sentinel AI FastAPI backend API
    this.baseUrl = window.SENTINEL_API_URL || 'http://localhost:8000/api/v1';
    this.tokenKey = 'sentinel_auth_token';
    this.userKey = 'sentinel_user_profile';
    this.deviceIdKey = 'sentinel_device_id';
  }



  getToken() {
    return localStorage.getItem(this.tokenKey) || null;
  }

  setToken(token) {
    if (token) {
      localStorage.setItem(this.tokenKey, token);
    } else {
      localStorage.removeItem(this.tokenKey);
    }
  }

  getUser() {
    const raw = localStorage.getItem(this.userKey);
    try {
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }

  setUser(user) {
    if (user) {
      localStorage.setItem(this.userKey, JSON.stringify(user));
    } else {
      localStorage.removeItem(this.userKey);
    }
  }

  getDeviceId() {
    let dId = localStorage.getItem(this.deviceIdKey);
    if (!dId) {
      dId = 'web-client-' + Math.random().toString(36).substring(2, 10);
      localStorage.setItem(this.deviceIdKey, dId);
    }
    return dId;
  }

  async request(endpoint, options = {}) {
    const url = `${this.baseUrl}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    };

    const token = this.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    try {
      const resp = await fetch(url, {
        ...options,
        headers
      });

      if (resp.status === 401 && !endpoint.includes('/auth/login') && !endpoint.includes('/auth/register')) {
        // Fallback to guest token if session invalidated
        await this.ensureGuestSession();
      }

      if (!resp.ok) {
        const errorData = await resp.json().catch(() => ({ detail: resp.statusText }));
        throw new Error(errorData.detail || `Request failed with status ${resp.status}`);
      }

      return await resp.json();
    } catch (err) {
      console.warn(`[API] Error on ${endpoint}:`, err.message);
      throw err;
    }
  }

  // ==========================================
  // AUTHENTICATION & SESSIONS
  // ==========================================

  async ensureGuestSession() {
    if (this.getToken()) {
      return;
    }
    try {
      const res = await this.request('/auth/guest', { method: 'POST' });
      if (res && res.access_token) {
        this.setToken(res.access_token);
        this.setUser({
          id: res.user_id,
          email: res.email,
          full_name: 'Guest Explorer',
          is_guest: true
        });
      }
    } catch (e) {
      console.warn('Guest session initialization deferred:', e);
    }
  }

  async register(fullName, email, password) {
    const payload = {
      full_name: fullName,
      email: email.trim().toLowerCase(),
      password: password,
      installation_id: this.getDeviceId(),
      device_name: 'Sentinel Web Dashboard'
    };
    const res = await this.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify(payload)
    });
    this.setToken(res.access_token);
    this.setUser({
      id: res.user_id,
      email: res.email,
      full_name: fullName,
      is_guest: false
    });
    return res;
  }

  async login(email, password) {
    const payload = {
      email: email.trim().toLowerCase(),
      password: password,
      installation_id: this.getDeviceId(),
      device_name: 'Sentinel Web Dashboard'
    };
    const res = await this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(payload)
    });
    this.setToken(res.access_token);
    
    // Fetch profile
    try {
      const prof = await this.getProfile();
      this.setUser({ ...prof, is_guest: false });
    } catch {
      this.setUser({ id: res.user_id, email: res.email, full_name: email.split('@')[0], is_guest: false });
    }
    return res;
  }

  logout() {
    this.setToken(null);
    this.setUser(null);
    return this.ensureGuestSession();
  }

  async getProfile() {
    return await this.request('/auth/me', { method: 'GET' });
  }

  async getAccountDevices() {
    return await this.request('/account/devices', { method: 'GET' });
  }

  async getAccountActivity() {
    try {
      return await this.request('/account/activity?limit=30', { method: 'GET' });
    } catch {
      return [];
    }
  }

  // ==========================================
  // DASHBOARD & TELEMETRY
  // ==========================================

  async getSecurityScore() {
    return await this.request('/security/score', { method: 'GET' });
  }

  async getAlerts() {
    return await this.request('/alerts/', { method: 'GET' });
  }

  async dismissAlert(alertId) {
    return await this.request(`/alerts/${alertId}/dismiss`, { method: 'POST' });
  }

  async getScanHistory() {
    return await this.request('/scans/history', { method: 'GET' });
  }

  async getLatestScan() {
    return await this.request('/scans/latest', { method: 'GET' });
  }

  async getScanById(scanId) {
    return await this.request(`/scans/${scanId}`, { method: 'GET' });
  }

  // ==========================================
  // SCANNERS (URL, SMS, DEEPFAKE, QR)
  // ==========================================

  async scanUrl(url) {
    return await this.request('/scans/url', {
      method: 'POST',
      body: JSON.stringify({ url: url.trim() })
    });
  }

  async scanMessage(messageText, language = 'en') {
    return await this.request('/scans/sms', {
      method: 'POST',
      body: JSON.stringify({
        message_text: messageText.trim(),
        language
      })
    });
  }

  async scanQr(rawPayload, imageBase64 = null) {
    return await this.request('/scans/qr', {
      method: 'POST',
      body: JSON.stringify({
        raw_payload: rawPayload.trim(),
        image_base64: imageBase64
      })
    });
  }

  async scanDeepfake(mediaHash, imageBase64 = null, features = null) {
    return await this.request('/scans/deepfake', {
      method: 'POST',
      body: JSON.stringify({
        media_type: 'IMAGE',
        media_hash_sha256: mediaHash,
        image_base64: imageBase64,
        features: features || {}
      })
    });
  }

  // ==========================================
  // AI SECURITY ASSISTANT
  // ==========================================

  async askAssistant(message, contextData = null, language = 'en') {
    return await this.request('/assistant/chat', {
      method: 'POST',
      body: JSON.stringify({
        message: message.trim(),
        context_data: contextData,
        language
      })
    });
  }

  async askScanAssistant(scanId, message, language = 'en') {
    return await this.request(`/scans/${scanId}/assistant`, {
      method: 'POST',
      body: JSON.stringify({
        message: message.trim(),
        language
      })
    });
  }
}

// Global API instance
window.api = new SentinelApiClient();
