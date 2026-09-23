import { LocalGridServer, LocalUser } from './LocalGridServer';
import { Utils } from '../utils';
import { gridConsole, GridConsole, LOG_COMPONENTS } from './GridConsole';
import { PasswordRecord, hashPassword, isPasswordRecord } from './password';

export const LOCAL_GRID_STORAGE_KEYS = {
  USER_ACCOUNT: 'linkpoint_offline_user_account',
  SETTINGS: 'linkpoint_offline_grid_settings'
};

export interface StoredAccount {
  firstName: string;
  lastName: string;
  passwordRecord: PasswordRecord;
}

export class LocalGridManager extends Utils.EventEmitter {
  private server: LocalGridServer;
  private isConfigured: boolean = false;
  private currentUserAccount: StoredAccount | null = null;
  private console: GridConsole;

  constructor(console: GridConsole = gridConsole) {
    super();
    this.console = console;
    this.server = new LocalGridServer(console);
    this.loadPersistedConfig();
  }

  public getConsole(): GridConsole {
    return this.console;
  }

  private loadPersistedConfig() {
    const account = Utils.storage.get(LOCAL_GRID_STORAGE_KEYS.USER_ACCOUNT, null);
    if (!account || !account.firstName) {
      return;
    }

    // Builds before password hashing stored the password itself under
    // `passwordHash`. Such a record is not trustworthy and must not be used as
    // a credential, so drop it and make the user set the account up again.
    if (!isPasswordRecord(account.passwordRecord)) {
      Utils.storage.remove(LOCAL_GRID_STORAGE_KEYS.USER_ACCOUNT);
      this.console.warn(
        LOG_COMPONENTS.USER,
        'Discarded a saved offline account stored in a legacy format. Please set up the account again.'
      );
      return;
    }

    try {
      this.currentUserAccount = {
        firstName: account.firstName,
        lastName: account.lastName || 'Resident',
        passwordRecord: account.passwordRecord
      };
      this.server.restoreUser(
        this.currentUserAccount.firstName,
        this.currentUserAccount.lastName,
        this.currentUserAccount.passwordRecord
      );
      this.isConfigured = true;
    } catch (e) {
      this.currentUserAccount = null;
      this.isConfigured = false;
      this.console.captureError(LOG_COMPONENTS.USER, 'Failed to restore the saved offline account.', e);
    }
  }

  public isFirstTimeSetupNeeded(): boolean {
    return !this.isConfigured || !this.currentUserAccount;
  }

  /**
   * Create the local account. `password` is the plaintext the user typed; it is
   * hashed here and only the derived record is kept or persisted.
   */
  public async setupOfflineAccount(firstName: string, lastName: string, password: string): Promise<LocalUser> {
    const cleanedFirstName = firstName.trim();
    const cleanedLastName = lastName.trim() || 'Resident';

    if (!cleanedFirstName) {
      throw new Error('A first name is required to create a local grid account.');
    }

    const user = await this.server.registerUser(cleanedFirstName, cleanedLastName, password);

    this.currentUserAccount = {
      firstName: cleanedFirstName,
      lastName: cleanedLastName,
      passwordRecord: user.passwordRecord
    };
    this.isConfigured = true;

    const persisted = Utils.storage.set(LOCAL_GRID_STORAGE_KEYS.USER_ACCOUNT, this.currentUserAccount);
    if (!persisted) {
      this.console.warn(
        LOG_COMPONENTS.USER,
        'Local account created but could not be saved; it will not survive a restart.'
      );
    }

    this.emit('accountConfigured', user);
    return user;
  }

  /** Replace the stored password with a new one. */
  public async changePassword(password: string): Promise<void> {
    if (!this.currentUserAccount) {
      throw new Error('No local account is configured.');
    }
    const passwordRecord = await hashPassword(password);
    this.currentUserAccount = { ...this.currentUserAccount, passwordRecord };
    this.server.restoreUser(
      this.currentUserAccount.firstName,
      this.currentUserAccount.lastName,
      passwordRecord
    );
    Utils.storage.set(LOCAL_GRID_STORAGE_KEYS.USER_ACCOUNT, this.currentUserAccount);
    this.console.info(LOG_COMPONENTS.USER, `Password updated for "${this.currentUserAccount.firstName}".`);
  }

  public toggleGridState(enable: boolean): boolean {
    if (enable) {
      if (this.isFirstTimeSetupNeeded()) {
        const message = 'First time setup required before starting local grid.';
        this.console.error(LOG_COMPONENTS.GRID, message);
        throw new Error(message);
      }
      const started = this.server.start();
      this.emit('gridStateChanged', { isRunning: true });
      return started;
    } else {
      const stopped = this.server.stop();
      this.emit('gridStateChanged', { isRunning: false });
      return stopped;
    }
  }

  public isGridRunning(): boolean {
    return this.server.getIsRunning();
  }

  public getServer(): LocalGridServer {
    return this.server;
  }

  /** The stored account. The returned record contains a digest, not a password. */
  public getCurrentUserAccount(): StoredAccount | null {
    return this.currentUserAccount;
  }
}
