/**
 * Linkpoint PWA - Chat Module
 */

class ChatManager extends Utils.EventEmitter {
  constructor(protocolManager, authManager) {
    super();
    this.protocol = protocolManager;
    this.auth = authManager;
    this.messages = [];
    this.maxMessages = 1000;
    this.chatInput = null;
    this.chatMessages = null;
    this.sendBtn = null;
    this.currentChatType = 'local'; // local, group, im
    this.currentGroupId = null;
  }

  /**
   * Initialize chat
   */
  init() {
    this.chatInput = document.getElementById('chat-input');
    this.chatMessages = document.getElementById('chat-messages');
    const chatForm = document.getElementById('chat-form');

    if (!this.chatInput || !this.chatMessages) {
      console.error('Chat elements not found');
      return;
    }

    // Setup event listeners
    if (chatForm) {
      chatForm.addEventListener('submit', (e) => {
        e.preventDefault();
        this.sendMessage();
      });
    }
    
    this.chatInput.addEventListener('keypress', (e) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        this.sendMessage();
      }
    });

    // Setup chat type tabs
    const chatTabs = document.querySelectorAll('.chat-tab');
    chatTabs.forEach(tab => {
      tab.addEventListener('click', () => {
        const chatType = tab.dataset.chatType;
        this.switchChatType(chatType);
      });
    });

    // Setup protocol listeners
    this.protocol.on('chat_message', (data) => this.handleIncomingMessage(data));
    this.protocol.on('group_chat', (data) => this.handleGroupMessage(data));
    this.protocol.on('instant_message', (data) => this.handleInstantMessage(data));

    // Load chat history from storage
    this.loadChatHistory();
  }
  
  /**
   * Switch chat type (local, group, im)
   */
  switchChatType(chatType) {
    this.currentChatType = chatType;
    
    // Update tab UI
    document.querySelectorAll('.chat-tab').forEach(tab => {
      tab.classList.toggle('active', tab.dataset.chatType === chatType);
    });
    
    // Update input placeholder
    if (this.chatInput) {
      switch (chatType) {
        case 'local':
          this.chatInput.placeholder = 'Type a message...';
          break;
        case 'group':
          this.chatInput.placeholder = 'Send to group...';
          break;
        case 'im':
          this.chatInput.placeholder = 'Send instant message...';
          break;
      }
    }
    
    // Load messages for this type
    this.displayMessagesForType(chatType);
  }
  
  /**
   * Display messages for specific chat type
   */
  displayMessagesForType(chatType) {
    if (!this.chatMessages) return;
    
    // Clear current messages
    this.chatMessages.innerHTML = '';
    
    // Filter and display messages for this type
    const filteredMessages = this.messages.filter(msg => {
      if (chatType === 'local') return msg.type === 'local' || !msg.type;
      if (chatType === 'group') return msg.type === 'group';
      if (chatType === 'im') return msg.type === 'im';
      return false;
    });
    
    if (filteredMessages.length === 0) {
      this.addSystemMessage(`No ${chatType} messages`);
    } else {
      filteredMessages.forEach(msg => {
        const isSelf = msg.senderId === this.auth.getUser()?.id;
        this.displayMessage(msg, isSelf);
      });
    }
  }

  /**
   * Send chat message
   */
  async sendMessage() {
    if (!this.chatInput) return;

    const message = this.chatInput.value.trim();
    if (!message) return;

    // Check if user is logged in
    if (!this.auth.isLoggedIn()) {
      Utils.showToast('Please login to send messages', 'warning');
      return;
    }

    try {
      // Handle commands
      if (message.startsWith('/')) {
        this.handleCommand(message);
        this.chatInput.value = '';
        return;
      }

      // Create message object
      const messageData = {
        id: Utils.generateUUID(),
        sender: this.auth.getUserDisplayName(),
        senderId: this.auth.getUser()?.id,
        text: message,
        timestamp: Date.now(),
        type: 'local',
        channel: 'local'
      };

      // Send to protocol (real implementation)
      if (this.protocol.sendChat) {
        // Using SLConnectionFull
        await this.protocol.sendChat(message, 0, 1);
      } else if (this.protocol.isConnected && this.protocol.isConnected()) {
        // Using ProtocolManager
        this.protocol.sendMessage('ChatFromViewer', {
          message: message,
          channel: 0,
          type: 1
        });
      }

      // Add to local messages
      this.addMessage(messageData, true);

      // Clear input
      this.chatInput.value = '';

      // Emit sent event
      this.emit('message_sent', messageData);

    } catch (error) {
      console.error('Error sending message:', error);
      Utils.showToast('Failed to send message', 'error');
    }
  }

  /**
   * Handle incoming message from protocol
   */
  handleIncomingMessage(data) {
    const messageData = {
      id: data.id || Utils.generateUUID(),
      sender: data.fromName || 'Unknown',
      senderId: data.fromId,
      text: data.message || data.text,
      timestamp: data.timestamp || Date.now(),
      type: data.chatType || 'local',
      channel: data.channel || 'local'
    };

    this.addMessage(messageData, false);
    this.emit('message_received', messageData);
  }

  /**
   * Add message to chat
   */
  addMessage(messageData, isSelf = false) {
    // Add to messages array
    this.messages.push(messageData);

    // Limit message history
    if (this.messages.length > this.maxMessages) {
      this.messages.shift();
    }

    // Save to storage
    this.saveChatHistory();

    // Display message
    this.displayMessage(messageData, isSelf);
  }

  /**
   * Display message in UI
   */
  displayMessage(messageData, isSelf = false) {
    if (!this.chatMessages) return;

    const messageEl = document.createElement('div');
    messageEl.className = `chat-message ${isSelf ? 'self' : ''}`;
    messageEl.dataset.messageId = messageData.id;

    const time = Utils.formatTime(messageData.timestamp);

    messageEl.innerHTML = `
      <div class="message-header">
        <span class="message-sender">${this.escapeHtml(messageData.sender)}</span>
        <span class="message-time">${time}</span>
      </div>
      <div class="message-body">${this.formatMessage(messageData.text)}</div>
    `;

    this.chatMessages.appendChild(messageEl);

    // Auto-scroll to bottom
    this.scrollToBottom();

    // Apply fade-in animation
    setTimeout(() => {
      messageEl.style.opacity = '1';
    }, 10);
  }

  /**
   * Format message text (handle URLs, emotes, etc.)
   */
  formatMessage(text) {
    // Escape HTML
    let formatted = this.escapeHtml(text);

    // Convert URLs to links (with security attributes)
    const urlRegex = /(https?:\/\/[^\s]+)/g;
    formatted = formatted.replace(urlRegex, '<a href="$1" target="_blank" rel="noopener noreferrer nofollow">$1</a>');

    // Handle emotes (simple implementation)
    formatted = formatted.replace(/:\)/g, '🙂');
    formatted = formatted.replace(/:\(/g, '☹️');
    formatted = formatted.replace(/:D/g, '😃');
    formatted = formatted.replace(/<3/g, '❤️');

    return formatted;
  }

  /**
   * Escape HTML to prevent XSS
   */
  escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  /**
   * Handle chat commands
   */
  handleCommand(command) {
    const parts = command.split(' ');
    const cmd = parts[0].toLowerCase();
    const args = parts.slice(1);

    switch (cmd) {
      case '/help':
        this.showHelp();
        break;
      case '/clear':
        this.clearChat();
        break;
      case '/me':
        this.sendEmote(args.join(' '));
        break;
      case '/shout':
        this.sendShout(args.join(' '));
        break;
      case '/whisper':
        this.sendWhisper(args.join(' '));
        break;
      default:
        this.addSystemMessage(`Unknown command: ${cmd}. Type /help for available commands.`);
    }
  }

  /**
   * Show help message
   */
  showHelp() {
    const helpText = `
Available commands:
/help - Show this help message
/clear - Clear chat history
/me <action> - Send an emote
/shout <message> - Shout message (100m range)
/whisper <message> - Whisper message (10m range)
    `.trim();
    this.addSystemMessage(helpText);
  }

  /**
   * Clear chat
   */
  clearChat() {
    if (this.chatMessages) {
      // Keep system messages
      const systemMessages = Array.from(this.chatMessages.querySelectorAll('.system-message'));
      this.chatMessages.innerHTML = '';
      systemMessages.forEach(msg => this.chatMessages.appendChild(msg));
    }
    this.messages = [];
    this.saveChatHistory();
    this.addSystemMessage('Chat cleared');
  }

  /**
   * Send emote
   */
  sendEmote(action) {
    if (!action) return;
    const user = this.auth.getUserDisplayName();
    this.addSystemMessage(`${user} ${action}`, 'emote');
  }

  /**
   * Send shout
   */
  sendShout(message) {
    if (!message) return;
    // Implement shout logic
    this.addSystemMessage('Shout sent', 'info');
  }

  /**
   * Send whisper
   */
  sendWhisper(message) {
    if (!message) return;
    // Implement whisper logic
    this.addSystemMessage('Whisper sent', 'info');
  }

  /**
   * Add system message
   */
  addSystemMessage(text, type = 'system') {
    if (!this.chatMessages) return;

    const messageEl = document.createElement('div');
    messageEl.className = `system-message ${type}`;
    messageEl.textContent = text;

    this.chatMessages.appendChild(messageEl);
    this.scrollToBottom();
  }

  /**
   * Scroll chat to bottom
   */
  scrollToBottom() {
    if (this.chatMessages) {
      this.chatMessages.scrollTop = this.chatMessages.scrollHeight;
    }
  }

  /**
   * Save chat history to storage
   */
  saveChatHistory() {
    // Save last 100 messages
    const recentMessages = this.messages.slice(-100);
    Utils.storage.set('linkpoint_chat_history', recentMessages);
  }

  /**
   * Load chat history from storage
   */
  loadChatHistory() {
    const history = Utils.storage.get('linkpoint_chat_history', []);
    
    if (history.length > 0) {
      this.messages = history;
      
      // Display recent messages
      history.slice(-20).forEach(msg => {
        const isSelf = msg.senderId === this.auth.getUser()?.id;
        this.displayMessage(msg, isSelf);
      });

      this.addSystemMessage(`Loaded ${history.length} messages from history`);
    }
  }

  /**
   * Get chat statistics
   */
  getStats() {
    return {
      totalMessages: this.messages.length,
      messagesInView: this.chatMessages?.children.length || 0
    };
  }

  /**
   * Search messages
   */
  searchMessages(query) {
    if (!query) return [];
    
    const lowerQuery = query.toLowerCase();
    return this.messages.filter(msg => 
      msg.text.toLowerCase().includes(lowerQuery) ||
      msg.sender.toLowerCase().includes(lowerQuery)
    );
  }

  /**
   * Export chat log
   */
  exportChatLog() {
    const log = this.messages.map(msg => {
      const time = new Date(msg.timestamp).toISOString();
      return `[${time}] ${msg.sender}: ${msg.text}`;
    }).join('\n');

    // Create download
    const blob = new Blob([log], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `linkpoint-chat-${Date.now()}.txt`;
    a.click();
    URL.revokeObjectURL(url);

    Utils.showToast('Chat log exported', 'success');
  }

  /**
   * Clear chat history
   */
  clearHistory() {
    this.messages = [];
    this.saveChatHistory();
    if (this.chatMessages) {
      this.chatMessages.innerHTML = '<div class="system-message">Chat history cleared</div>';
    }
  }
}

// Make available globally
window.ChatManager = ChatManager;
