/**
 * Sentinel AI - Interactive Cybersecurity Assistant Controller
 */

class AssistantController {
  constructor() {
    this.messagesContainer = document.getElementById('chat-messages-scroll');
    this.inputField = document.getElementById('chat-input-text');
    this.sendBtn = document.getElementById('chat-send-btn');
    this.langSelect = document.getElementById('chat-lang-select');
    this.activeScanContextId = null;

    this.bindEvents();
    
    // Check if opened with a specific scan context from another page
    const urlParams = new URLSearchParams(window.location.search);
    const scanId = urlParams.get('scan_id');
    if (scanId) {
      this.activeScanContextId = scanId;
      setTimeout(() => {
        this.appendMessage('assistant', `I have loaded scan record \`${scanId}\` from your recent analysis into active context. What would you like to understand about this threat assessment?`);
      }, 400);
    }
  }


  bindEvents() {
    if (this.sendBtn) {
      this.sendBtn.addEventListener('click', () => this.sendMessage());
    }

    if (this.inputField) {
      this.inputField.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
          e.preventDefault();
          this.sendMessage();
        }
      });
    }

    document.querySelectorAll('.prompt-chip').forEach(chip => {
      chip.addEventListener('click', () => {
        const text = chip.getAttribute('data-prompt') || chip.textContent.trim();
        this.inputField.value = text;
        this.sendMessage();
      });
    });
  }

  async sendMessage() {
    const text = this.inputField.value.trim();
    if (!text) return;

    this.inputField.value = '';
    this.appendMessage('user', text);

    const lang = localStorage.getItem('sentinel_preferred_lang') || (this.langSelect ? this.langSelect.value : 'en');
    const typingId = this.showTypingIndicator(lang);

    try {
      let resp;
      if (this.activeScanContextId) {
        resp = await window.api.askScanAssistant(this.activeScanContextId, text, lang);
      } else {
        resp = await window.api.askAssistant(text, null, lang);
      }

      this.removeTypingIndicator(typingId);
      const answer = resp.response || resp.answer || 'I evaluated your security query. Please ensure never to share OTPs or authorization credentials.';
      this.appendMessage('assistant', answer);

    } catch (err) {
      this.removeTypingIndicator(typingId);
      this.appendMessage('assistant', `⚠️ Could not connect to Sentinel AI intelligence node: ${err.message}`);
    }
  }

  appendMessage(role, text) {
    if (!this.messagesContainer) return;

    const row = document.createElement('div');
    row.className = `chat-bubble-row ${role}`;

    const icon = role === 'assistant' ? '🛡️' : '👤';
    const formatted = this.formatMarkdown(text);

    row.innerHTML = `
      <div class="chat-avatar ${role}">${icon}</div>
      <div class="chat-bubble">
        ${formatted}
        <div style="font-size:0.7rem;color:var(--text-sub);margin-top:0.4rem;text-align:right;">
          ${new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
        </div>
      </div>
    `;

    this.messagesContainer.appendChild(row);
    this.messagesContainer.scrollTop = this.messagesContainer.scrollHeight;
  }

  showTypingIndicator(lang = 'en') {
    const id = 'typing-' + Date.now();
    const row = document.createElement('div');
    row.className = 'chat-bubble-row assistant';
    row.id = id;

    let msg = "Sentinel Neural BM25 RAG analyzing query...";
    if (lang === 'hi') msg = "सेंटिनल न्यूरल बीएम25 आरएजी विश्लेषण कर रहा है...";
    else if (lang === 'te') msg = "సెంటినెల్ న్యూరల్ BM25 RAG విశ్లేషిస్తోంది...";

    row.innerHTML = `
      <div class="chat-avatar assistant">🛡️</div>
      <div class="chat-bubble" style="display:flex;align-items:center;gap:6px;padding:12px 18px;">
        <span class="sync-icon-spin" style="font-size:1rem;">⚡</span>
        <span style="font-size:0.85rem;color:var(--text-muted);">${msg}</span>
      </div>
    `;

    this.messagesContainer.appendChild(row);
    this.messagesContainer.scrollTop = this.messagesContainer.scrollHeight;
    return id;
  }

  removeTypingIndicator(id) {
    const el = document.getElementById(id);
    if (el) el.remove();
  }

  formatMarkdown(text) {
    if (!text) return '';
    // Format bold, list items, and line breaks
    let html = text
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      .replace(/`(.*?)`/g, '<code style="background:rgba(0,0,0,0.3);padding:2px 5px;border-radius:4px;color:var(--cyan-primary);">$1</code>')
      .replace(/\n\n/g, '<br><br>')
      .replace(/\n/g, '<br>');
    return html;
  }

  discussScan(scanId) {
    this.activeScanContextId = scanId;
    window.location.href = `assistant.html?scan_id=${encodeURIComponent(scanId)}`;
  }
}

window.assistantController = new AssistantController();
