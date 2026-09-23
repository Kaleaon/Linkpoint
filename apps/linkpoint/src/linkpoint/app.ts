/**
 * Linkpoint PWA - Main Application
 */

import { SLConnectionFull } from './sl-connection-full';
import { AuthManager } from './auth';
import { WorldViewer } from './world';
import { ChatManager } from './chat';
import { InventoryManager } from './inventory';
import { PreferencesManager } from './preferences';
import { NotificationsManager } from './notifications';
import { Utils } from './utils';

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
  private initialization: Promise<void> | null = null;
  public protocol: SLConnectionFull;
  public auth: AuthManager;
  public world: WorldViewer;
  public chat: ChatManager;
  public inventory: InventoryManager;
  public preferences: PreferencesManager;
  public notifications: NotificationsManager;

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
    this.friends = new FriendsExtended();
  }

  async init() {
    if (this.initialization) return this.initialization;
    this.initialization = this.initialize();
    return this.initialization;
  }

  private async initialize() {
    console.log('🔗 Linkpoint PWA Starting...');

    this.preferences.init();
    this.auth.init();
    await this.world.init();
    this.chat.init();
    await this.inventory.init();
    this.notifications.init();

    this.setupEventListeners();

    console.log('✅ Linkpoint PWA Ready');
  }

  private setupEventListeners() {
    this.auth.on('login_success', (user: any) => {
      console.log('User logged in:', user);
      this.inventory.load();
    });

    this.auth.on('logout', () => {
      console.log('User logged out');
      this.chat.clearHistory();
    });
  }
}

export const app = new LinkpointApp();
