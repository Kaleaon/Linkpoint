/**
 * Linkpoint PWA - Notifications Manager
 */

import { Utils } from './utils';
import { SLConnectionFull } from './sl-connection-full';

export interface NotificationData {
  title?: string;
  message?: string;
  [key: string]: any;
}

export class NotificationsManager extends Utils.EventEmitter {
  public protocol: SLConnectionFull;
  public unreadCount: number = 0;
  public items: NotificationData[] = [];
  private noticeCounter = 0;

  constructor(protocolManager: SLConnectionFull) {
    super();
    this.protocol = protocolManager;
  }

  init() {
    this.protocol.on('notification', (data: NotificationData) => this.handleNotification(data));
    this.protocol.on('group_notice', (data: any) => {
      this.handleNotification({
        id: data.id || `notice-${Date.now()}-${++this.noticeCounter}`,
        kind: 'notice',
        title: data.subject || 'Group Notice',
        subject: data.subject || 'Group Notice',
        message: data.message || '',
        from: data.fromName || data.from || 'Resident',
        groupId: data.groupId,
        timestamp: data.timestamp || Date.now(),
      });
    });
  }

  handleNotification(data: NotificationData) {
    this.items.push({ ...data });
    this.unreadCount++;
    this.emit('notification_received', data);
    Utils.showToast(data.title || 'Notification', 'info');
  }

  clear() {
    this.unreadCount = 0;
    this.items = [];
    this.emit('cleared');
  }
}
