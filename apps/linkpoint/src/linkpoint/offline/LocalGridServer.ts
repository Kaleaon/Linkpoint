/**
 * Linkpoint Local Grid Server
 * Simulates local grid services (Login XML-RPC, User, Inventory, Asset, and Region services)
 * for offline viewing and local hosting.
 */

import { Utils } from '../utils';
import { gridConsole, GridConsole, LOG_COMPONENTS } from './GridConsole';
import { PasswordRecord, hashPassword, verifyPassword, isPasswordRecord } from './password';

export interface LocalUser {
  id: string;
  firstName: string;
  lastName: string;
  /** Salted PBKDF2 digest. The plaintext password is never retained. */
  passwordRecord: PasswordRecord;
  homeRegionId: string;
  homePosition: { x: number; y: number; z: number };
  inventoryRootId: string;
}

export interface LocalRegion {
  id: string;
  name: string;
  locX: number;
  locY: number;
  serverURI: string;
  simIp: string;
  simPort: number;
  terrainHeightmap: Float32Array | number[];
  prims: LocalPrim[];
}

export interface LocalPrim {
  id: string;
  name: string;
  description: string;
  position: { x: number; y: number; z: number };
  rotation: { x: number; y: number; z: number; w: number };
  scale: { x: number; y: number; z: number };
  shape: string;
  textureAssets: string[];
}

export interface HypergridIdentity {
  gridURI: string;
  userId: string;
  displayName: string;
  homeGrid: string;
}

export class LocalGridServer {
  private isRunning: boolean = false;
  private users: Map<string, LocalUser> = new Map();
  private regions: Map<string, LocalRegion> = new Map();
  private hypergridUsers: Map<string, HypergridIdentity> = new Map();
  private console: GridConsole;

  constructor(console: GridConsole = gridConsole) {
    this.console = console;
    this.initDefaultRegion();
  }

  public getConsole(): GridConsole {
    return this.console;
  }

  private initDefaultRegion() {
    const defaultRegionId = Utils.generateUUID();
    const defaultRegion: LocalRegion = {
      id: defaultRegionId,
      name: 'Offline Region',
      locX: 1000,
      locY: 1000,
      serverURI: 'http://127.0.0.1:9000/',
      simIp: '127.0.0.1',
      simPort: 9000,
      terrainHeightmap: new Float32Array(256 * 256).fill(21),
      prims: []
    };
    this.regions.set(defaultRegionId, defaultRegion);
    this.console.info(
      LOG_COMPONENTS.REGION,
      `Initialised default region "${defaultRegion.name}" at ${defaultRegion.locX},${defaultRegion.locY}`
    );
  }

  public start(): boolean {
    if (this.isRunning) {
      this.console.warn(LOG_COMPONENTS.GRID, 'Start requested but the local grid is already running.');
      return true;
    }
    this.isRunning = true;
    this.console.info(
      LOG_COMPONENTS.GRID,
      `Local grid started on 127.0.0.1:9000 with ${this.regions.size} region(s) and ${this.users.size} account(s).`
    );
    return true;
  }

  public stop(): boolean {
    if (!this.isRunning) {
      this.console.warn(LOG_COMPONENTS.GRID, 'Shutdown requested but the local grid is already stopped.');
      return true;
    }
    this.isRunning = false;
    this.console.info(LOG_COMPONENTS.GRID, 'Local grid shut down.');
    return true;
  }

  public getIsRunning(): boolean {
    return this.isRunning;
  }

  private userKey(firstName: string, lastName: string): string {
    return `${firstName} ${lastName}`.trim().toLowerCase();
  }

  private buildUser(firstName: string, lastName: string, passwordRecord: PasswordRecord): LocalUser {
    const defaultRegion = Array.from(this.regions.values())[0];
    return {
      id: Utils.generateUUID(),
      firstName,
      lastName,
      passwordRecord,
      homeRegionId: defaultRegion ? defaultRegion.id : Utils.generateUUID(),
      homePosition: { x: 128, y: 128, z: 22 },
      inventoryRootId: Utils.generateUUID()
    };
  }

  /** Register an account from a plaintext password, which is hashed before storage. */
  public async registerUser(firstName: string, lastName: string, password: string): Promise<LocalUser> {
    const passwordRecord = await hashPassword(password);
    const user = this.buildUser(firstName, lastName, passwordRecord);
    this.users.set(this.userKey(firstName, lastName), user);
    this.console.info(
      LOG_COMPONENTS.USER,
      `Created local account "${user.firstName} ${user.lastName}" (${user.id})`,
      `Credentials stored as ${passwordRecord.algo} with ${passwordRecord.iterations} iterations.`
    );
    return user;
  }

  /** Rehydrate an account from an already-derived record, without re-hashing. */
  public restoreUser(firstName: string, lastName: string, passwordRecord: PasswordRecord): LocalUser {
    if (!isPasswordRecord(passwordRecord)) {
      throw new Error('Cannot restore a local account without a valid password record.');
    }
    const user = this.buildUser(firstName, lastName, passwordRecord);
    this.users.set(this.userKey(firstName, lastName), user);
    this.console.debug(
      LOG_COMPONENTS.USER,
      `Restored local account "${user.firstName} ${user.lastName}" from saved configuration.`
    );
    return user;
  }

  public getUser(firstName: string, lastName: string): LocalUser | undefined {
    return this.users.get(this.userKey(firstName, lastName));
  }

  public async authenticate(firstName: string, lastName: string, password: string): Promise<boolean> {
    if (!this.isRunning) return false;
    const user = this.getUser(firstName, lastName);
    if (!user) return false;
    return verifyPassword(password, user.passwordRecord);
  }

  public async processLogin(firstName: string, lastName: string, password: string): Promise<Record<string, any>> {
    const displayName = `${firstName} ${lastName}`.trim();
    this.console.info(LOG_COMPONENTS.LOGIN, `Login request for "${displayName}".`);

    if (!this.isRunning) {
      this.console.error(LOG_COMPONENTS.LOGIN, `Login denied for "${displayName}": local grid is offline.`);
      return {
        login: 'false',
        reason: 'Local Grid Server is offline/shutdown.'
      };
    }

    const user = this.getUser(firstName, lastName);
    const authenticated = user ? await verifyPassword(password, user.passwordRecord) : false;
    if (!user || !authenticated) {
      this.console.warn(
        LOG_COMPONENTS.LOGIN,
        `Login failed for "${displayName}": ${user ? 'incorrect password' : 'no such local account'}.`
      );
      return {
        login: 'false',
        reason: 'Invalid username or password for local grid.'
      };
    }

    const region = this.regions.get(user.homeRegionId) || Array.from(this.regions.values())[0];
    const sessionToken = Utils.generateUUID();
    const secureSessionId = Utils.generateUUID();

    this.console.info(
      LOG_COMPONENTS.LOGIN,
      `Login succeeded for "${displayName}" into region "${region.name}".`,
      `agent_id=${user.id} session_id=${sessionToken}`
    );

    return {
      login: 'true',
      session_id: sessionToken,
      secure_session_id: secureSessionId,
      agent_id: user.id,
      first_name: user.firstName,
      last_name: user.lastName,
      start_location: 'home',
      sim_ip: region.simIp,
      sim_port: region.simPort,
      region_x: region.locX * 256,
      region_y: region.locY * 256,
      seed_capability: `${region.serverURI}CAPS/${sessionToken}/`,
      look_at: '[r0,g0,b0]',
      inventory_root: [{ folder_id: user.inventoryRootId }],
      message: 'Welcome to your Offline Local OpenSim Grid!'
    };
  }

  public resolveHypergridId(identifier: string): HypergridIdentity {
    let gridURI = 'http://127.0.0.1:9000';
    let userId = Utils.generateUUID();
    let displayName = identifier;

    if (identifier.includes('@')) {
      const parts = identifier.split('@');
      displayName = parts[0];
      gridURI = parts[1].startsWith('http') ? parts[1] : `http://${parts[1]}`;
    } else if (identifier.startsWith('http')) {
      try {
        const url = new URL(identifier);
        gridURI = `${url.protocol}//${url.host}`;
        const pathParts = url.pathname.split('/');
        userId = pathParts[pathParts.length - 1] || userId;
      } catch (e) {
        this.console.captureError(
          LOG_COMPONENTS.HYPERGRID,
          `Could not parse Hypergrid URL "${identifier}"; falling back to the local grid URI.`,
          e
        );
      }
    }

    const hgId: HypergridIdentity = {
      gridURI,
      userId,
      displayName,
      homeGrid: gridURI
    };

    this.hypergridUsers.set(identifier, hgId);
    this.console.info(LOG_COMPONENTS.HYPERGRID, `Resolved "${identifier}" to ${displayName} @ ${gridURI}`);
    return hgId;
  }

  public getRegions(): LocalRegion[] {
    return Array.from(this.regions.values());
  }

  public addRegion(region: LocalRegion) {
    this.regions.set(region.id, region);
    this.console.info(
      LOG_COMPONENTS.REGION,
      `Added region "${region.name}" at ${region.locX},${region.locY} with ${region.prims.length} object(s).`
    );
  }
}
