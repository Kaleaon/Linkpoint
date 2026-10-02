/**
 * Linkpoint PWA - Main Application
 */

import { purgeFabricatedStorage } from './fabricated-data';
import { SLConnectionFull } from './sl-connection-full';
import { AuthManager } from './auth';
import { WorldViewer } from './world';
import { ChatManager } from './chat';
import { InventoryManager } from './inventory';
import { PreferencesManager } from './preferences';
import { NotificationsManager } from './notifications';
import { InteractionsManager } from './interactions';
import { ContactsStore } from './contacts';
import { NoticeStore } from './notices';
import { Utils } from './utils';
import { slBridge } from './sl-bridge';

// Phase 2 Modules
import { EventQueueManager } from './phase2/event-queue';
import { CapabilitiesManager } from './phase2/capabilities';
import { AvatarManager } from './phase2/avatar';
import { ObjectManagerExtended } from './phase2/objects-extended';
import { InventoryCore } from './phase2/inventory-core';
import { InventoryOperations } from './phase2/inventory-ops';
import { InventorySpecialTypes } from './phase2/inventory-types';
import { ChatExtended } from './phase2/chat-extended';
import { GroupsManager } from './phase2/groups';
import { FriendsExtended } from './phase2/friends-extended';

export class LinkpointApp {
  private balanceTimer: ReturnType<typeof setInterval> | null = null;
  private initialization: Promise<void> | null = null;
  public protocol: SLConnectionFull;
  public auth: AuthManager;
  public world: WorldViewer;
  public chat: ChatManager;
  public inventory: InventoryManager;
  public preferences: PreferencesManager;
  public notifications: NotificationsManager;
  public interactions: InteractionsManager;
  public contacts: ContactsStore;
  public notices: NoticeStore;

  // Phase 2 Managers
  public eventQueue: EventQueueManager;
  public capabilities: CapabilitiesManager;
  public avatar: AvatarManager;
  public objects: ObjectManagerExtended;
  public inventoryCore: InventoryCore;
  public inventoryOps: InventoryOperations;
  public inventoryTypes: InventorySpecialTypes;
  public chatExtended: ChatExtended;
  public groups: GroupsManager;
  public friends: FriendsExtended;

  constructor() {
    this.protocol = new SLConnectionFull();
    this.preferences = new PreferencesManager();
    this.auth = new AuthManager(this.protocol);
    this.world = new WorldViewer(this.protocol);
    this.chat = new ChatManager(this.protocol, this.auth);
    this.inventory = new InventoryManager(this.protocol, this.auth);
    this.notifications = new NotificationsManager(this.protocol);
    this.interactions = new InteractionsManager(this.protocol);
    this.contacts = new ContactsStore();
    this.notices = new NoticeStore(this.protocol);

    // Initialize Phase 2 Managers
    this.eventQueue = new EventQueueManager(this.protocol as any); // Type cast for now
    this.capabilities = new CapabilitiesManager();
    this.avatar = new AvatarManager();
    this.objects = new ObjectManagerExtended();
    this.inventoryCore = new InventoryCore();
    this.inventoryOps = new InventoryOperations(this.inventoryCore);
    this.inventoryTypes = new InventorySpecialTypes();
    this.chatExtended = new ChatExtended(this.protocol);
    this.groups = new GroupsManager(this.protocol);
    this.friends = new FriendsExtended(this.protocol);
  }

  async init() {
    if (this.initialization) return this.initialization;
    this.initialization = this.initialize();
    return this.initialization;
  }

  private async initialize() {
    console.log('🔗 Linkpoint PWA Starting...');

    // Remove invented data left in storage by earlier builds before anything reads it.
    purgeFabricatedStorage();
    this.preferences.init();
    this.auth.init();
    // Wired before anything that can fail or wait: a script dialog must never be dropped for want of a listener.
    this.interactions.init();
    this.notices.init();
    await this.world.init();
    this.chat.init();
    await this.inventory.init();
    this.notifications.init();

    this.setupEventListeners();

    console.log('✅ Linkpoint PWA Ready');
  }

  private setupEventListeners() {
    this.protocol.on('friends_loaded', (friends: any[]) => {
      console.log('Real friends loaded from Second Life:', friends.length);
      this.friends.replaceFriends(friends.map((f) => ({
        id: f.id,
        name: f.name,
        onlineStatus: f.onlineStatus ?? f.online,
        permissions: {
          canSeeOnline: typeof f.rightsHasMask === 'number' ? Boolean(f.rightsHasMask & 1) : Boolean(f.rightsHas),
          canSeeOnMap: typeof f.rightsHasMask === 'number' ? Boolean(f.rightsHasMask & 2) : Boolean(f.rightsHas),
          canModifyObjects: typeof f.rightsGivenMask === 'number' ? Boolean(f.rightsGivenMask & 4) : Boolean(f.rightsGiven),
        }
      })));
    });

    this.protocol.on('friend_status', (data: any) => {
      if (data?.id) {
        this.friends.updateFriendStatus(data.id, data.online ? 'online' : 'offline', data);
      }
    });

    this.protocol.on('friend_request', (data: any) => {
      this.notifications.handleNotification({
        id: data.requestId || String(Date.now()),
        title: 'Friend Request',
        message: `${data.fromName} offered friendship: "${data.message || ''}"`,
        type: 'friend_request',
        data,
      });
    });

    this.protocol.on('friend_remove', (data: any) => {
      const id = data?.id || data?.friendId;
      if (id) this.friends.removeFriend(String(id));
    });

    // The L$ balance is whatever the grid reports. It is requested after login and
    // refreshed periodically; until then it is null and shown as unknown.
    this.protocol.on('connected', () => {
      void this.protocol.refreshBalance();
      if (this.balanceTimer) clearInterval(this.balanceTimer);
      this.balanceTimer = setInterval(() => { void this.protocol.refreshBalance(); }, 60000);
    });
    this.protocol.on('disconnected', () => {
      if (this.balanceTimer) clearInterval(this.balanceTimer);
      this.balanceTimer = null;
      this.protocol.balance = null;
      this.protocol.emit('balance_updated', null);
    });

    this.auth.on('login_success', async (user: any) => {
      console.log('User logged in:', user);
      await this.inventory.load();
      await this.loadFriends();
      await this.loadGroups();
    });

    this.protocol.on('capabilities_ready', (caps: any) => {
      if (caps?.EventQueueGet && !this.eventQueue.isPolling) {
        this.eventQueue.startPolling(caps.EventQueueGet).catch(err => {
          console.warn('[LinkpointApp] EventQueue startPolling failed:', err);
        });
      }
    });

    this.auth.on('logout', () => {
      console.log('User logged out');
      this.eventQueue.stopPolling();
      this.chat.clearHistory();
      this.friends.clear();
    });
  }

  async loadGroups() {
    if (!this.auth.isLoggedIn()) return [];
    try {
      if (slBridge.connected) {
        const groups = await slBridge.fetchGroups();
        if (Array.isArray(groups) && groups.length > 0) {
          for (const g of groups) {
            this.groups.setGroupInfo(g.id, g);
          }
          return groups;
        }
      }
    } catch (err) {
      console.warn('[LinkpointApp] loadGroups warning:', err);
    }
    return this.groups.getGroups();
  }

  async loadFriends() {
    if (!this.auth.isLoggedIn()) return [];
    try {
      if (typeof this.protocol.fetchFriends === 'function') {
        const friends = await this.protocol.fetchFriends();
        if (Array.isArray(friends)) {
          this.protocol.emit('friends_loaded', friends);
          return friends;
        }
      }
    } catch (err) {
      console.warn('[LinkpointApp] loadFriends warning:', err);
    }
    return this.friends.getFriends();
  }
}

export const app = new LinkpointApp();
