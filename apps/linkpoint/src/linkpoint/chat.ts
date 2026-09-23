/**
 * Linkpoint PWA - Chat Module
 */

import { Utils } from './utils';

export class ChatManager extends Utils.EventEmitter {
  public protocol: any;
  public auth: any;
  public messages: any[] = [];
  public maxMessages: number = 1000;

  constructor(protocolManager: any, authManager: any) {
    super();
    this.protocol = protocolManager;
    this.auth = authManager;
  }

  init() {
    this.protocol.on('ChatFromSimulator', (data: any) => this.handleIncomingMessage(data));
    this.loadChatHistory();
  }

  async sendMessage(message: string, channel: number = 0, type: number = 1) {
    if (!message.trim()) throw new Error('Message cannot be empty');
    if (!this.auth.isLoggedIn()) throw new Error('Not connected to a grid');

    try {
      await this.protocol.sendChat(message, channel, type);

      const messageData = {
        id: Utils.generateUUID(),
        sender: this.auth.getUserDisplayName(),
        senderId: this.auth.user?.id,
        text: message,
        timestamp: Date.now(),
        type: 'local'
      };

      this.addMessage(messageData);
      this.emit('message_sent', messageData);
    } catch (error) {
      console.error('Error sending message:', error);
      throw error;
    }
  }

  handleIncomingMessage(data: any) {
    const messageData = {
      id: data.id || Utils.generateUUID(),
      sender: data.fromName || 'Unknown',
      senderId: data.fromId,
      text: data.message || data.text,
      timestamp: data.timestamp || Date.now(),
      type: data.chatType || 'local'
    };

    this.addMessage(messageData);
    this.emit('message_received', messageData);
  }

  addMessage(messageData: any) {
    this.messages.push(messageData);
    if (this.messages.length > this.maxMessages) this.messages.shift();
    this.saveChatHistory();
  }

  saveChatHistory() {
    Utils.storage.set('linkpoint_chat_history', this.messages.slice(-100));
  }

  loadChatHistory() {
    this.messages = Utils.storage.get('linkpoint_chat_history', []);
  }

  clearHistory() {
    this.messages = [];
    this.saveChatHistory();
    this.emit('history_cleared');
  }
}
