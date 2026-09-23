/**
 * Linkpoint PWA - Authentication Module
 */

import { Utils } from './utils';

const CREDENTIALS_KEY = 'linkpoint_credentials';
const SESSION_KEY = 'linkpoint_session';

type SavedSession = {
  mode: 'grid';
  grid: string;
  username: string;
  user: any;
  sessionId: string | null;
  agentId: string | null;
  lastLoginAt: string;
};

export class AuthManager extends Utils.EventEmitter {
  public protocol: any;
  public user: any = null;
  public credentials: any = null;
  public sessionSnapshot: SavedSession | null = null;

  constructor(protocolManager: any) {
    super();
    this.protocol = protocolManager;
  }

  init() {
    const savedCreds = Utils.storage.get(CREDENTIALS_KEY);
    if (savedCreds && savedCreds.rememberMe) {
      this.credentials = savedCreds;
      this.emit('credentials_loaded', savedCreds);
    }

    // Session and agent identifiers are valid only for the live protocol
    // connection.  Do not restore them from localStorage after a reload.
    // Remove snapshots written by older versions as a one-time migration.
    Utils.storage.remove(SESSION_KEY);
  }

  async login(grid: string, username: string, password: string, rememberMe: boolean, startLocation: string = 'last') {
    try {
      const response = await this.protocol.connect(grid, username, password, startLocation);

      if (rememberMe) {
        this.credentials = { username, grid, rememberMe: true };
        Utils.storage.set(CREDENTIALS_KEY, this.credentials);
      } else {
        this.credentials = null;
        Utils.storage.remove(CREDENTIALS_KEY);
      }

      this.user = {
        id: this.protocol.agentId,
        firstName: response.first_name || username.split(' ')[0],
        lastName: response.last_name || username.split(' ')[1] || 'Resident',
        fullName: `${response.first_name || username.split(' ')[0]} ${response.last_name || username.split(' ')[1] || 'Resident'}`,
        grid
      };

      this.sessionSnapshot = {
        mode: 'grid',
        grid,
        username,
        user: this.user,
        sessionId: this.protocol.sessionId,
        agentId: this.protocol.agentId,
        lastLoginAt: new Date().toISOString(),
      };
      this.emit('login_success', this.user);
      return this.user;
    } catch (error) {
      this.emit('login_failed', error);
      throw error;
    }
  }

  async logout() {
    try {
      await this.protocol.logout();
    } finally {
      this.user = null;
      this.sessionSnapshot = null;
      Utils.storage.remove(SESSION_KEY);
      this.credentials = Utils.storage.get(CREDENTIALS_KEY) || null;
      this.emit('logout');
    }
  }

  async reconnect(password: string, startLocation: string = 'last') {
    const savedCreds = this.credentials || Utils.storage.get(CREDENTIALS_KEY);
    if (!savedCreds?.username || !savedCreds?.grid) {
      throw new Error('No saved credentials to reconnect with');
    }
    return this.login(savedCreds.grid, savedCreds.username, password, savedCreds.rememberMe === true, startLocation);
  }

  hasSavedSession() {
    return this.sessionSnapshot !== null;
  }

  getSessionSnapshot() {
    return this.sessionSnapshot;
  }

  clearSavedSession() {
    this.sessionSnapshot = null;
    Utils.storage.remove(SESSION_KEY);
    this.emit('session_cleared');
  }

  isLoggedIn() {
    return this.user !== null && this.protocol.connected;
  }

  getUserDisplayName() {
    return this.user ? this.user.fullName : 'Guest';
  }
}
