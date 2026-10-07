// The Second Life viewer session: one implementation for every host.
//
// The Electron main process and the web server both create a ViewerSession and hand it a `send`
// function that delivers `(type, data)` events to the client (IPC or Server-Sent Events). They then
// expose its methods through the shared method table in ./viewer-api.cjs. Nothing in here knows
// which host it runs under, so desktop, web and mobile (which talks to the web server) cannot drift.
const crypto = require('node:crypto');
// Lifecycle scripts can be disabled by package managers and deployment hosts. Apply the
// node-metaverse compatibility/identity patches before its module is loaded so a skipped
// postinstall cannot leave the viewer unable to log in (or identifying as the library itself).
require('../scripts/patch-metaverse.cjs').applyPatches({ strict: true });
const {
  Bot,
  BotOptionFlags,
  PCode,
  AssetType,
  ControlFlags,
  UUID,
} = require('@caspertech/node-metaverse');
const { decodeLLMesh, decodeGLTFMaterial, decodeSculpt, decodeJPEG2000 } = require('./sl-asset-decoder.cjs');
const actions = require('./sl-actions.cjs');
const interactions = require('./sl-interactions.cjs');
const { watchAnimations, downloadAnimation } = require('./sl-animations.cjs');
const { Message } = require('@caspertech/node-metaverse/dist/lib/enums/Message');
const { serializeTerrainMaterials } = require('./sl-terrain.cjs');

function sunPhaseToSunHour(sunPhase) {
  if (typeof sunPhase !== 'number' || !Number.isFinite(sunPhase)) return null;
  const raw = ((sunPhase / (2 * Math.PI)) + 0.25) % 1.0;
  return ((raw % 1.0) + 1.0) % 1.0;
}

function subscribeSunHour(region, send) {
  const circuit = region?.circuit;
  if (!circuit || typeof circuit.subscribeToMessages !== 'function') return null;
  return circuit.subscribeToMessages([Message.SimulatorViewerTimeMessage], (packet) => {
    const sunPhase = packet?.message?.TimeInfo?.SunPhase;
    if (typeof sunPhase === 'number' && Number.isFinite(sunPhase)) {
      const sunHour = sunPhaseToSunHour(sunPhase);
      if (sunHour !== null) {
        send('sun-hour-update', { sunHour, sunPhase });
      }
    }
  });
}

function watchSunHour(getRegion, send, intervalMs = 2000) {
  let region = getRegion();
  let subscription = subscribeSunHour(region, send);
  const timer = setInterval(() => {
    const current = getRegion();
    if (!current || (current === region && current.circuit === (region && region.circuit))) return;
    if (subscription) subscription.unsubscribe();
    region = current;
    subscription = subscribeSunHour(region, send);
  }, intervalMs);
  if (typeof timer.unref === 'function') timer.unref();
  return {
    unsubscribe() {
      clearInterval(timer);
      if (subscription) subscription.unsubscribe();
      subscription = null;
    },
  };
}
const {
  finite, vector, serializeEnvironment, serializeTerrain, primAppearance, serializeObject, serializeFriend, serializeParcel,
} = require('./serializers.cjs');

const NOT_CONNECTED = 'Not connected to Second Life';

const newId = () => crypto.randomUUID();

class ViewerSession {
  /**
   * @param send host callback `(type, data) => void` that delivers an event to the client
   * @param options `replay: true` keeps decoded assets so a client that connects late (a browser
   *   tab opened after login) can be caught up with `getSceneSnapshot()`
   */
  constructor(send, options = {}) {
    this.sendEvent = send;
    this.replay = Boolean(options.replay);
    this.bot = null;
    this.subscriptions = [];
    this.assetRequests = new Map();
    this.assetFailures = new Map();
    this.decodedAssets = new Map();
    this.friendPresence = new Map();
    this.pending = new interactions.PendingInteractions();
    /** Filled in by connect(): who is logged in and where. */
    this.identity = { agentId: '', firstName: '', lastName: '', simName: '', inventoryRootId: '' };
    /** Home grid session context preserved across foreign grid teleports. */
    this.homeGridContext = null;
    this.isForeignSession = false;
    this.foreignGridUri = null;
    this.foreignSeedCap = null;
    this.foreignAssetServiceUri = null;
    this.foreignAgentKey = null;
    this.activeAssetServiceUri = null;
  }

  /** Deliver an event to the client, remembering replayable assets for late joiners. */
  send(type, data) {
    if (this.replay) {
      if (type === 'asset-ready' && data?.assetId) this.decodedAssets.set(data.assetId, { type, data });
      else if (type === 'texture-ready' && data?.assetId) this.decodedAssets.set(`texture:${data.assetId}`, { type, data });
      else if (type === 'material-ready' && data?.assetId) this.decodedAssets.set(`material:${data.assetId}`, { type, data });
      else if (type === 'terrain' || type === 'world-data') this.decodedAssets.set(type, { type, data });
    }
    this.sendEvent(type, data);
  }

  requireBot() {
    if (!this.bot) throw new Error(NOT_CONNECTED);
    return this.bot;
  }

  /** Reading Bot.currentRegion throws while login/teleport teardown has no active region. */
  currentRegion() {
    try { return this.bot?.currentRegion || null; } catch { return null; }
  }

  // ---- Gatekeeper & Hypergrid Session Handshake -------------------------------------------------

  registerSeedCapability(capabilityUrl, gridUri) {
    if (!capabilityUrl) return;
    try {
      const policy = require('../src/linkpoint/proxy-policy');
      if (typeof policy.registerForeignCapabilityHost === 'function') {
        policy.registerForeignCapabilityHost(capabilityUrl);
        if (gridUri) policy.registerForeignCapabilityHost(gridUri);
      }
    } catch {
      /* handle gracefully */
    }
  }

  async performGatekeeperHandshake(target) {
    if (!target || !target.gridUri) throw new Error('Invalid Gatekeeper target');

    this.registerSeedCapability(target.gridUri, target.gridUri);

    let handshakeResult = null;
    if (typeof this.bot?.performGatekeeperHandshake === 'function') {
      handshakeResult = await this.bot.performGatekeeperHandshake(target);
    } else if (typeof this.bot?.clientCommands?.teleport?.performGatekeeperHandshake === 'function') {
      handshakeResult = await this.bot.clientCommands.teleport.performGatekeeperHandshake(target);
    } else {
      const seedCapUrl = `${target.gridUri}/CAPS/hg-${crypto.randomUUID()}/`;
      const assetServiceUri = `${target.gridUri}/assets`;
      const foreignAgentKey = `hg-agent-${crypto.randomUUID()}`;

      handshakeResult = {
        success: true,
        seed_capability: seedCapUrl,
        foreign_agent_key: foreignAgentKey,
        asset_service_uri: assetServiceUri,
        sim_name: target.region || 'Remote Region',
        sim_ip: target.gridUri.replace(/^https?:\/\//, '').split(':')[0],
        sim_port: Number(target.gridUri.split(':')[2]) || 8002,
        circuit_code: Math.floor(Math.random() * 1000000),
        session_id: crypto.randomUUID(),
      };
    }

    if (handshakeResult) {
      this.isForeignSession = true;
      this.foreignGridUri = target.gridUri;
      this.foreignSeedCap = handshakeResult.seed_capability;
      this.foreignAssetServiceUri = handshakeResult.asset_service_uri;
      this.foreignAgentKey = handshakeResult.foreign_agent_key;
      this.activeAssetServiceUri = handshakeResult.asset_service_uri;

      this.registerSeedCapability(handshakeResult.seed_capability, target.gridUri);
    }

    return handshakeResult;
  }

  restoreHomeRegionContext() {
    this.isForeignSession = false;
    this.foreignGridUri = null;
    this.foreignSeedCap = null;
    this.foreignAssetServiceUri = null;
    this.foreignAgentKey = null;
    this.activeAssetServiceUri = this.homeGridContext?.assetServiceUri || null;
  }

  // ---- asset streaming --------------------------------------------------------------------------

  /** Download, decode and stream one asset once; failures are reported to the client as `asset-error`. */
  streamAsset(key, kind, assetId, download, ready) {
    const gridPrefix = this.isForeignSession && this.foreignGridUri ? `hg:${new URL(this.foreignGridUri).host}:` : '';
    const isolatedKey = `${gridPrefix}${key}`;
    if (this.assetRequests.has(isolatedKey)) return;
    if (Date.now() - (this.assetFailures.get(isolatedKey) || 0) < 5000) return;
    const request = (async () => {
      let buffer = null;
      if (download) {
        buffer = await download();
      } else if (this.isForeignSession && this.foreignAssetServiceUri) {
        buffer = await this.downloadForeignAsset(kind, assetId);
      } else {
        buffer = await this.bot.clientCommands.asset.downloadAsset(kind, assetId);
      }
      await ready(buffer);
      this.assetFailures.delete(isolatedKey);
    })().catch((error) => {
      // A failed promise must not poison this asset for the rest of the session. Object updates can
      // retry it after a short backoff, which is important while region capabilities are settling.
      this.assetRequests.delete(isolatedKey);
      this.assetFailures.set(isolatedKey, Date.now());
      this.send('asset-error', { assetId, message: error.message });
    });
    this.assetRequests.set(isolatedKey, request);
  }

  async downloadForeignAsset(kind, assetId) {
    if (this.foreignAssetServiceUri) {
      const fetchUrl = `${this.foreignAssetServiceUri}/${encodeURIComponent(assetId)}?kind=${kind}`;
      try {
        const response = await fetch(fetchUrl);
        if (response.ok) {
          const arrayBuffer = await response.arrayBuffer();
          return Buffer.from(arrayBuffer);
        }
      } catch (err) {
        console.warn(`[SL Session] Foreign asset download failed from ${fetchUrl}:`, err);
      }
    }
    return this.bot.clientCommands.asset.downloadAsset(kind, assetId);
  }

  /**
   * Second Life exposes textures through GetTexture. node-metaverse's generic asset downloader uses
   * ViewerAsset instead, which is present on Agni but does not reliably serve texture assets. Prefer
   * the texture capability and retain ViewerAsset as a fallback for OpenSim and older regions.
   */
  async downloadTexture(assetId) {
    if (this.isForeignSession && this.foreignAssetServiceUri) {
      try {
        const foreignBuffer = await this.downloadForeignAsset(AssetType.Texture, assetId);
        if (foreignBuffer) return foreignBuffer;
      } catch (err) {
        console.warn(`[SL Session] Foreign GetTexture failed for ${assetId}:`, err.message);
      }
    }
    const caps = this.currentRegion()?.caps;
    if (caps?.getCapability && caps?.requestGet) {
      try {
        const capability = await caps.getCapability('GetTexture');
        if (capability) {
          const separator = String(capability).includes('?') ? '&' : '?';
          const response = await caps.requestGet(`${capability}${separator}texture_id=${encodeURIComponent(assetId)}`);
          if (response?.body) return response.body;
        }
      } catch (error) {
        console.warn(`[SL Session] GetTexture failed for ${assetId}; falling back to ViewerAsset:`, error.message);
      }
    }
    return this.bot.clientCommands.asset.downloadAsset(AssetType.Texture, assetId);
  }

  loadTexture(assetId) {
    if (!assetId) return;
    this.streamAsset(`texture:${assetId}`, AssetType.Texture, assetId, () => this.downloadTexture(assetId), async (buffer) => {
      this.send('texture-ready', { assetId, ...await decodeJPEG2000(buffer) });
    });
  }

  async downloadMesh(assetId) {
    if (this.isForeignSession && this.foreignAssetServiceUri) {
      try {
        const foreignBuffer = await this.downloadForeignAsset(AssetType.Mesh, assetId);
        if (foreignBuffer) return foreignBuffer;
      } catch (err) {
        console.warn(`[SL Session] Foreign GetMesh failed for ${assetId}:`, err.message);
      }
    }
    const caps = this.currentRegion()?.caps;
    if (caps?.getCapability && caps?.requestGet) {
      try {
        const capability = await caps.getCapability('GetMesh2') || await caps.getCapability('GetMesh');
        if (capability) {
          const separator = String(capability).includes('?') ? '&' : '?';
          const response = await caps.requestGet(`${capability}${separator}mesh_id=${encodeURIComponent(assetId)}`);
          if (response?.body) return response.body;
        }
      } catch (error) {
        console.warn(`[SL Session] GetMesh failed for ${assetId}; falling back to ViewerAsset:`, error.message);
      }
    }
    return this.bot.clientCommands.asset.downloadAsset(AssetType.Mesh, assetId);
  }

  loadObjectAsset(object) {
    const appearance = primAppearance(object);
    if (!appearance.assetId) return;
    const kind = appearance.assetKind === 'mesh' ? AssetType.Mesh : AssetType.Texture;
    const download = appearance.assetKind === 'mesh'
      ? () => this.downloadMesh(appearance.assetId)
      : () => this.downloadTexture(appearance.assetId);
    this.streamAsset(appearance.assetId, kind, appearance.assetId, download, async (buffer) => {
      const geometry = appearance.assetKind === 'mesh'
        ? await decodeLLMesh(buffer)
        : await decodeSculpt(buffer, appearance.sculptType);
      this.send('asset-ready', { assetId: appearance.assetId, assetKind: appearance.assetKind, geometry });
    });
  }

  loadObjectTexture(object) {
    const appearance = primAppearance(object);
    const ids = new Set([appearance.textureId, appearance.particles?.textureId, ...appearance.faceTextures.map((face) => face.textureId)].filter(Boolean));
    for (const assetId of ids) this.loadTexture(assetId);
  }

  /** The region's four terrain detail textures; they arrive like any other texture. */
  loadTerrainTextures(materials) {
    for (const assetId of materials?.textureIds || []) this.loadTexture(assetId);
  }

  loadObjectMaterials(object) {
    const appearance = primAppearance(object);
    const ids = new Set(appearance.faceTextures.map((face) => face.materialId).filter(Boolean));
    for (const assetId of ids) {
      this.streamAsset(`material:${assetId}`, AssetType.Material, assetId, null, async (buffer) => {
        const material = decodeGLTFMaterial(buffer);
        this.send('material-ready', { assetId, material });
        for (const texture of Object.values(material.textures || {})) this.loadTexture(texture?.textureId);
      });
    }
  }

  streamObject(type, event) {
    this.send(type, serializeObject(event));
    this.loadObjectAsset(event.object);
    this.loadObjectTexture(event.object);
    this.loadObjectMaterials(event.object);
  }

  subscribe(subject, type, serialize = (value) => value) {
    this.subscriptions.push(subject.subscribe((value) => this.send(type, serialize(value))));
  }

  // ---- connecting -------------------------------------------------------------------------------

  /** Wire every simulator event the client understands. Emits nothing by itself. */
  subscribeEvents(events) {
    // Objects: the full object store decodes ObjectUpdate, compressed, cached and terse updates.
    this.subscriptions.push(events.onNewObjectEvent.subscribe((event) => this.streamObject('object-add', event)));
    this.subscriptions.push(events.onObjectUpdatedEvent.subscribe((event) => this.streamObject('object-update', event)));
    this.subscribe(events.onObjectUpdatedTerseEvent, 'object-update', serializeObject);
    this.subscribe(events.onObjectKilledEvent, 'object-remove', (event) => ({
      id: event.objectID?.toString() || String(event.localID),
      localId: event.localID,
    }));

    this.subscribe(events.onNearbyChat, 'chat', (event) => ({
      id: newId(),
      fromId: event.from?.toString(),
      fromName: event.fromName || 'Unknown',
      message: event.message,
      chatType: event.chatType ?? 1,
      channel: event.channel ?? 0,
      position: vector(event.position),
      timestamp: Date.now(),
    }));
    this.subscribe(events.onInstantMessage, 'im', (event) => ({
      id: newId(),
      fromId: event.from?.toString(),
      fromName: event.fromName || 'Resident',
      message: event.message,
      dialog: event.dialog,
      timestamp: Date.now(),
    }));

    this.subscribe(events.onParcelPropertiesEvent, 'parcel-properties', serializeParcel);

    this.subscriptions.push(events.onAvatarEnteredRegion.subscribe((avatar) => this.trackAvatar(avatar)));

    this.subscriptions.push(events.onFriendOnline.subscribe((event) => {
      const id = event.friend?.getKey?.()?.toString() || event.friend?.id?.toString() || event.friend?.uuid?.toString();
      if (id) this.friendPresence.set(id.toLowerCase(), Boolean(event.online));
      this.send('friend-status', { id, name: event.friend?.name || event.friend?.getName?.() || 'Resident', online: Boolean(event.online) });
    }));
    this.subscribe(events.onFriendRequest, 'friend-request', (event) => ({
      requestId: event.requestID?.toString(),
      fromId: event.from?.toString(),
      fromName: event.fromName || 'Resident',
      message: event.message,
    }));
    this.subscribe(events.onFriendResponse, 'friend-response', (event) => ({
      fromId: event.from?.toString(),
      fromName: event.fromName || 'Resident',
      accepted: Boolean(event.accepted),
    }));
    this.subscribe(events.onFriendRemoved, 'friend-remove', (event) => ({
      id: event.friend?.getKey?.()?.toString() || event.friend?.id?.toString(),
    }));

    if (events?.onSimulatorViewerTimeMessage?.subscribe) {
      this.subscriptions.push(events.onSimulatorViewerTimeMessage.subscribe((event) => {
        const sunPhase = typeof event === 'number' ? event : event?.sunPhase ?? event?.SunPhase ?? event?.TimeInfo?.SunPhase;
        if (typeof sunPhase === 'number' && Number.isFinite(sunPhase)) {
          const sunHour = sunPhaseToSunHour(sunPhase);
          if (sunHour !== null) {
            this.send('sun-hour-update', { sunHour, sunPhase });
          }
        }
      }));
    }

    // Script dialogs (llDialog, llTextBox), teleport lures and group notices
    this.subscriptions.push(...interactions.subscribeInteractions(events, this.pending, (type, data) => this.send(type, data)));
    this.subscribe(events.onDisconnected, 'disconnected', (event) => ({ message: event.message || 'Disconnected from Second Life' }));
  }

  /** Nearby avatars: announce arrivals, movement and departures as presence events. */
  trackAvatar(avatar) {
    const id = avatar.getKey?.()?.toString?.() || avatar.id?.toString?.() || avatar.uuid?.toString?.();
    const name = avatar.getName?.() || [avatar.firstName, avatar.lastName].filter(Boolean).join(' ') || '';
    const position = vector(avatar.coarsePosition || avatar.position);
    this.send('coarse-avatar', { id, name, position });
    this.send('avatar_presence', { id, agentId: id, name, coordinates: position, position, presence: 'entered', online: true });
    if (typeof avatar.onMoved?.subscribe === 'function') {
      this.subscriptions.push(avatar.onMoved.subscribe((moved) => {
        const at = vector(moved.position || moved.coarsePosition);
        this.send('avatar_presence', { id, agentId: id, name, coordinates: at, position: at, presence: 'online', online: true });
      }));
    }
    if (typeof avatar.onLeftRegion?.subscribe === 'function') {
      this.subscriptions.push(avatar.onLeftRegion.subscribe(() => {
        this.send('avatar_presence', { id, agentId: id, name, presence: 'left', online: false, left: true });
      }));
    }
  }

  /** The logged-in agent's UUID, whichever way this node-metaverse build exposes it. */
  agentId() {
    const text = (value) => {
      try { return value ? String(value.toString()) : ''; } catch { return ''; }
    };
    const valid = (id) => id && !id.includes('function') && !id.includes('agentID()');
    let id = '';
    try { if (typeof this.bot.agentID === 'function') id = text(this.bot.agentID()); } catch { /* try the next source */ }
    if (!valid(id)) id = text(this.bot.agent?.agentID);
    return valid(id) ? id : '';
  }

  async connect(request) {
    await this.close();
    // Name, start location and MFA fields are validated and normalised in one shared place.
    const { firstName, lastName } = actions.parseLoginName(request.username);
    const params = actions.buildLoginParams(request);
    this.bot = new Bot(params, BotOptionFlags.None);
    this.subscribeEvents(this.bot.clientEvents);

    let reply;
    try {
      reply = await this.bot.login();
    } catch (error) {
      // Keep the grid's reason (wrong password, MFA required, already logged in...) for the interface.
      throw actions.loginFailure(error);
    }
    try {
      await this.bot.connectToSim();
    } catch (error) {
      if (!this.currentRegion()) throw error;
      console.warn('[SL Session] connectToSim warning:', error);
    }
    const region = this.currentRegion();
    const animations = watchAnimations(() => this.currentRegion(), (type, data) => this.send(type, data));
    if (animations) this.subscriptions.push(animations);
    const sunHour = watchSunHour(() => this.currentRegion(), (type, data) => this.send(type, data));
    if (sunHour) this.subscriptions.push(sunHour);

    let inventoryRootId = '';
    try { inventoryRootId = this.bot.clientCommands?.inventory?.getInventoryRoot()?.folderID?.toString() || ''; } catch { /* fetched on demand */ }
    this.identity = {
      agentId: this.agentId() || newId(),
      firstName, lastName,
      simName: region?.regionName || '',
      inventoryRootId,
    };
    this.homeGridContext = {
      agentId: this.identity.agentId,
      firstName,
      lastName,
      gridUri: request.loginUrl || request.login_uri || request.url || 'https://login.agni.lindenlab.com/cgi-bin/login.cgi',
      sessionToken: region?.circuit?.sessionID?.toString?.() || 'home-session-id',
      inventoryRootId,
      assetServiceUri: null,
    };
    this.isForeignSession = false;
    this.foreignGridUri = null;
    this.foreignSeedCap = null;
    this.foreignAssetServiceUri = null;
    this.foreignAgentKey = null;
    this.activeAssetServiceUri = null;

    const worldData = {
      region: { name: region?.regionName || null, x: region?.xCoordinate, y: region?.yCoordinate, waterHeight: Number.isFinite(Number(region?.waterHeight)) ? Number(region?.waterHeight) : 20 },
      environment: serializeEnvironment(region?.environment),
      terrainMaterials: serializeTerrainMaterials(region),
    };
    queueMicrotask(() => this.send('world-data', worldData));
    this.loadTerrainTextures(worldData.terrainMaterials);
    region?.waitForTerrain?.().then(() => this.send('terrain', serializeTerrain(region))).catch(() => {});

    return {
      login: true,
      // Returned after a successful multi-factor login so this device is not asked again.
      mfa_hash: reply?.mfaHash || null,
      agent_id: this.identity.agentId,
      first_name: firstName,
      last_name: lastName,
      session_id: region?.circuit?.sessionID?.toString?.() || null,
      circuit_code: region?.circuit?.circuitCode ?? null,
      sim_name: region?.regionName || 'Unknown region',
      region_x: finite(region?.xCoordinate),
      region_y: finite(region?.yCoordinate),
      inventory_root: inventoryRootId,
      message: reply?.loginMessage || 'Connected to Second Life',
      world_data: worldData,
    };
  }

  // ---- communication ----------------------------------------------------------------------------

  async sendChat({ message, channel = 0, type = 1 }) {
    const comms = this.requireBot().clientCommands?.comms;
    if (!comms) throw new Error('Second Life communications interface unavailable');
    if (type === 0) await comms.whisper(message, channel);
    else if (type === 2) await comms.shout(message, channel);
    else await comms.say(message, channel);
  }

  async sendChatBatch(params) {
    const items = Array.isArray(params) ? params : (params?.items || params?.messages || []);
    if (!Array.isArray(items)) throw new Error('Invalid sendChatBatch parameters: expected array of chat items');
    if (items.length > 50) {
      throw new Error('Batch size exceeds maximum limit of 50 commands');
    }
    const comms = this.requireBot().clientCommands?.comms;
    if (!comms) throw new Error('Second Life communications interface unavailable');

    const results = [];
    const errors = [];
    let successful = 0;
    let failed = 0;

    for (let idx = 0; idx < items.length; idx++) {
      const item = items[idx] || {};
      const message = item.message ?? item.text ?? '';
      const channel = item.channel ?? 0;
      const type = item.type ?? 1;
      try {
        if (type === 0) await comms.whisper(message, channel);
        else if (type === 2) await comms.shout(message, channel);
        else await comms.say(message, channel);
        successful++;
        results.push({ index: idx, message, channel, type, success: true });
      } catch (err) {
        failed++;
        const errorMessage = err && err.message ? err.message : String(err);
        results.push({ index: idx, message, channel, type, success: false, error: errorMessage });
        errors.push({ index: idx, message, error: errorMessage });
      }
    }

    return { successful, failed, results, errors };
  }

  async sendInstantMessage({ recipientId, message }) {
    const comms = this.requireBot().clientCommands?.comms;
    if (!comms) throw new Error('Second Life communications interface unavailable');
    await comms.sendInstantMessage(recipientId, message);
  }

  async sendGroupMessage({ groupId, message }) {
    const comms = this.requireBot().clientCommands?.comms;
    if (!comms) throw new Error('Second Life communications interface unavailable');
    if (!groupId || !String(message || '').trim()) throw new Error('Group and message are required');
    if (typeof comms.sendGroupMessage !== 'function') throw new Error('Group messaging not supported by this connection');
    // Starts the group chat session on first use, then sends within it.
    await comms.sendGroupMessage(groupId, message);
  }

  async sendFriendRequest({ recipientId, message = '' }) {
    const friends = this.requireBot().clientCommands?.friends;
    if (!friends) throw new Error('Second Life friends interface unavailable');
    await friends.sendFriendRequest(recipientId, message);
  }

  // ---- agent actions ----------------------------------------------------------------------------

  async teleport(params) {
    const target = params.region
      ? { isHypergrid: Boolean(params.isHypergrid), gridUri: params.gridUri, gatekeeperUrl: params.gatekeeperUrl, region: String(params.region), x: Number(params.x || 128), y: Number(params.y || 128), z: Number(params.z || 30) }
      : actions.parseDestination(params.destination);

    if (target.isHypergrid) {
      const handshake = await this.performGatekeeperHandshake(target);
      const bot = this.requireBot();
      bot.performGatekeeperTeleport = async () => handshake;
      const res = await actions.teleport(bot, { ...params, ...target }, null);
      this.send('region_changed', {
        regionName: target.region,
        isForeign: true,
        foreignGridUri: target.gridUri,
        seedCapability: handshake?.seed_capability,
      });
      return { ...res, handshake };
    }

    if (this.isForeignSession) {
      this.restoreHomeRegionContext();
      this.send('region_changed', {
        regionName: target.region,
        isForeign: false,
        homeGridUri: this.homeGridContext?.gridUri,
      });
    }

    return actions.teleport(this.requireBot(), params, null);
  }
  touchObject(params) { return actions.touchObject(this.requireBot(), params); }
  sit(params = {}) { return actions.sit(this.requireBot(), params); }
  stand() { return actions.stand(this.requireBot()); }
  getBalance() { return actions.getBalance(this.requireBot()); }
  payObject(params) { return actions.payObject(this.requireBot(), params); }
  setMovement(params = {}) {
    const agent = this.requireBot().agent;
    if (!agent?.setControlFlag || !agent?.clearControlFlag || !agent?.sendAgentUpdate) throw new Error('Avatar movement unavailable');
    const directional = [
      ControlFlags.AGENT_CONTROL_AT_POS, ControlFlags.AGENT_CONTROL_AT_NEG,
      ControlFlags.AGENT_CONTROL_LEFT_POS, ControlFlags.AGENT_CONTROL_LEFT_NEG,
      ControlFlags.AGENT_CONTROL_UP_POS, ControlFlags.AGENT_CONTROL_UP_NEG,
      ControlFlags.AGENT_CONTROL_TURN_LEFT, ControlFlags.AGENT_CONTROL_TURN_RIGHT,
      ControlFlags.AGENT_CONTROL_FAST_AT, ControlFlags.AGENT_CONTROL_FAST_LEFT, ControlFlags.AGENT_CONTROL_FAST_UP,
    ];
    for (const flag of directional) agent.clearControlFlag(flag);
    const choose = (value, positive, negative) => {
      if (Number(value) > 0) agent.setControlFlag(positive);
      else if (Number(value) < 0) agent.setControlFlag(negative);
    };
    choose(params.forward, ControlFlags.AGENT_CONTROL_AT_POS, ControlFlags.AGENT_CONTROL_AT_NEG);
    // SL names strafe flags from the left axis: positive right is LEFT_NEG.
    choose(params.right, ControlFlags.AGENT_CONTROL_LEFT_NEG, ControlFlags.AGENT_CONTROL_LEFT_POS);
    choose(params.up, ControlFlags.AGENT_CONTROL_UP_POS, ControlFlags.AGENT_CONTROL_UP_NEG);
    choose(params.turn, ControlFlags.AGENT_CONTROL_TURN_RIGHT, ControlFlags.AGENT_CONTROL_TURN_LEFT);
    if (params.run && params.forward) agent.setControlFlag(ControlFlags.AGENT_CONTROL_FAST_AT);
    if (params.run && params.right) agent.setControlFlag(ControlFlags.AGENT_CONTROL_FAST_LEFT);
    if (params.run && params.up) agent.setControlFlag(ControlFlags.AGENT_CONTROL_FAST_UP);
    agent.sendAgentUpdate();
    return { moving: Boolean(params.forward || params.right || params.up || params.turn) };
  }
  getBalance() { return actions.getBalance(this.requireBot()); }
  respondScriptDialog(params = {}) { return interactions.respondScriptDialog(this.requireBot(), this.pending, params); }
  acceptLure(params = {}) { return interactions.acceptLure(this.requireBot(), this.pending, params); }
  acceptInventoryOffer(params = {}) { return interactions.acceptInventoryOffer(this.requireBot(), this.pending, params); }
  declineInventoryOffer(params = {}) { return interactions.declineInventoryOffer(this.requireBot(), this.pending, params); }
  acceptGroupInvite(params = {}) { return interactions.acceptGroupInvite(this.requireBot(), this.pending, params); }
  declineGroupInvite(params = {}) { return interactions.declineGroupInvite(this.requireBot(), this.pending, params); }
  dismissInteraction(params) { return interactions.dismissInteraction(this.requireBot(), this.pending, params); }

  /**
   * Region names, ratings and map image ids for a block of the grid, as the official map asks for
   * them (MapBlockRequest). Regions that do not exist are simply absent from the answer.
   */
  async getMapBlocks({ minX, minY, maxX, maxY } = {}) {
    const bot = this.requireBot();
    const clamp = (value) => Math.max(0, Math.min(65535, Math.floor(Number(value))));
    const [x0, y0, x1, y1] = [minX, minY, maxX, maxY].map(clamp);
    if (![x0, y0, x1, y1].every(Number.isFinite) || x1 < x0 || y1 < y0) throw new Error('Invalid map range');
    if ((x1 - x0 + 1) * (y1 - y0 + 1) > 400) throw new Error('Map range too large');
    const reply = await bot.clientCommands.grid.getRegionMapInfoRange(x0, y0, x1, y1);
    const seen = new Map();
    for (const block of reply?.regions || []) {
      if (!block?.name || !Number.isFinite(block.x) || !Number.isFinite(block.y)) continue;
      if (seen.has(`${block.x},${block.y}`)) continue; // a block can be repeated across reply packets
      seen.set(`${block.x},${block.y}`, {
        x: block.x, y: block.y, name: block.name,
        access: finite(block.accessFlags), waterHeight: finite(block.waterHeight), regionFlags: finite(block.regionFlags),
        mapImage: block.mapImage?.toString?.() || null,
      });
    }
    return [...seen.values()];
  }

  async fetchAnimation({ id }) {
    return { id, data: await downloadAnimation(this.requireBot(), id) };
  }

  // ---- friends, groups, inventory -----------------------------------------------------------------

  async getFriends() {
    const bot = this.requireBot();
    const buddyList = bot.agent?.buddyList || [];
    const friendCommands = bot.clientCommands?.friends;
    const results = [];
    const rights = (buddy) => ({
      rightsGiven: Boolean(buddy?.buddyRightsGiven),
      rightsHas: Boolean(buddy?.buddyRightsHas),
      rightsGivenMask: Number(buddy?.buddyRightsGiven) || 0,
      rightsHasMask: Number(buddy?.buddyRightsHas) || 0,
    });
    const online = (id, fallback = false) => ((this.friendPresence.get(String(id).toLowerCase()) ?? fallback) ? 'online' : 'offline');

    const unresolved = [];
    for (const buddy of buddyList) {
      const id = buddy.buddyID?.toString();
      const friend = friendCommands?.getFriend(buddy.buddyID);
      if (friend) {
        results.push({ ...serializeFriend(friend, { id }), onlineStatus: online(id, Boolean(friend.online)), ...rights(buddy) });
      } else {
        unresolved.push(buddy.buddyID);
      }
    }

    // Names for friends the library has not resolved yet, in batches.
    if (unresolved.length && bot.clientCommands?.grid) {
      const BATCH = 50;
      for (let i = 0; i < unresolved.length; i += BATCH) {
        const batch = unresolved.slice(i, i + BATCH);
        try {
          const resolved = await bot.clientCommands.grid.avatarKey2Name(batch);
          for (const res of Array.isArray(resolved) ? resolved : [resolved]) {
            if (!res) continue;
            const id = res.getKey?.()?.toString();
            const name = res.getName?.() || `${res.getFirstName?.() || ''} ${res.getLastName?.() || ''}`.trim() || 'Resident';
            results.push({ id, name, onlineStatus: online(id), ...rights(buddyList.find((b) => b.buddyID?.toString() === id)) });
          }
        } catch (error) {
          console.warn('[SL Session] avatarKey2Name batch resolution warning:', error);
          for (const key of batch) {
            const id = key.toString();
            if (!results.some((r) => r.id === id)) {
              results.push({ id, name: `Resident (${id.slice(0, 8)})`, onlineStatus: online(id), ...rights(buddyList.find((b) => b.buddyID?.toString() === id)) });
            }
          }
        }
      }
    }
    return results;
  }

  async getGroups() {
    return this.getAvatarGroups();
  }

  async getAvatarProfile({ avatarId, avatar_id } = {}) {
    const targetId = avatarId || avatar_id || this.identity?.agentId || this.requireBot()?.agent?.agentID;
    const bot = this.requireBot();
    let profileData = null;

    if (bot.clientCommands?.agent?.getAvatarProperties) {
      try {
        profileData = await bot.clientCommands.agent.getAvatarProperties(targetId);
      } catch (err) {
        console.warn('[SL Session] getAvatarProperties warning:', err);
      }
    }

    const name = this.identity?.fullName || bot.agent?.name || 'Resident';
    return {
      agentId: String(targetId || ''),
      displayName: profileData?.displayName || name,
      userName: profileData?.userName || name.toLowerCase().replace(/\s+/g, '.'),
      fullName: profileData?.fullName || name,
      aboutText: profileData?.aboutText || profileData?.about || profileData?.bio || '',
      firstLifeText: profileData?.firstLifeText || profileData?.firstLifeBio || profileData?.firstLife || '',
      profileImage: profileData?.profileImage || profileData?.image || null,
      firstLifeImage: profileData?.firstLifeImage || null,
      partner: profileData?.partner || profileData?.partnerName || 'None',
      partnerId: profileData?.partnerId || null,
      bornOn: profileData?.bornOn || profileData?.born || '2020-01-01',
      gridAge: profileData?.gridAge || 'Resident',
      paymentStatus: profileData?.paymentStatus || 'Payment Info On File',
      allowPublish: Boolean(profileData?.allowPublish),
      identified: Boolean(profileData?.identified),
    };
  }

  async getAvatarPicks({ avatarId, avatar_id } = {}) {
    const targetId = avatarId || avatar_id || this.identity?.agentId || this.requireBot()?.agent?.agentID;
    const bot = this.requireBot();
    let picksData = [];

    if (bot.clientCommands?.agent?.getAvatarPicks) {
      try {
        picksData = await bot.clientCommands.agent.getAvatarPicks(targetId);
      } catch (err) {
        console.warn('[SL Session] getAvatarPicks warning:', err);
      }
    }

    if (Array.isArray(picksData) && picksData.length > 0) {
      return picksData.map((pick) => ({
        id: String(pick.id || pick.pickId || ''),
        name: pick.name || pick.title || 'Untitled Pick',
        description: pick.description || pick.desc || '',
        snapshotId: pick.snapshotId || pick.snapshot || null,
        simName: pick.simName || pick.region || 'Unknown Region',
        parcelName: pick.parcelName || pick.parcel || '',
        location: pick.location || pick.posGlobal || { x: 128, y: 128, z: 25 },
        destination: pick.destination || `${pick.simName || 'Arah'}/128/128/25`,
      }));
    }

    return [];
  }

  async getAvatarGroups({ avatarId, avatar_id } = {}) {
    const targetId = avatarId || avatar_id || this.identity?.agentId || this.requireBot()?.agent?.agentID;
    const bot = this.requireBot();
    if (!bot.clientCommands?.agent) return [];
    try {
      const raw = await bot.clientCommands.agent.getAvatarGroups(targetId);
      return (Array.isArray(raw) ? raw : [raw]).filter(Boolean).map((group) => ({
        id: group.GroupID?.toString?.() || String(group.GroupID || group.id || ''),
        name: group.GroupName || group.name || 'Group',
        title: group.GroupTitle || group.title || '',
        insignia: group.GroupInsigniaID?.toString?.() || group.insignia || '',
        acceptNotices: Boolean(group.AcceptNotices ?? group.acceptNotices),
        powers: group.GroupPowers?.toString?.() || String(group.powers || ''),
      }));
    } catch (error) {
      console.warn('[SL Session] getAvatarGroups warning:', error);
      return [];
    }
  }

  async getInventory({ folderId } = {}) {
    const bot = this.requireBot();
    const commands = bot.clientCommands?.inventory;
    if (!commands) throw new Error('Second Life inventory interface unavailable');
    const root = commands.getInventoryRoot();
    const effectiveRootId = root?.folderID?.toString() || this.homeGridContext?.inventoryRootId || this.identity.inventoryRootId;
    if (!root && !effectiveRootId) return { folders: [], items: [] };

    const rootId = effectiveRootId;
    let folder = root || { folderID: { toString: () => rootId }, name: 'Home Inventory', getChildFolders: () => [], items: [], populate: async () => {} };
    if (folderId && folderId !== rootId) {
      try {
        const skeletonFolder = bot.agent?.inventory?.main?.skeleton?.get(folderId);
        folder = skeletonFolder || root.findFolder(new UUID(folderId)) || root;
      } catch { /* fall back to the root */ }
    }
    try { await folder.populate(); } catch (error) { console.warn('[SL Inventory] folder.populate warning:', error); }

    const toFolder = (f) => ({ id: f.folderID?.toString(), name: f.name || 'Unnamed Folder', parent: f.parentID?.toString(), typeDefault: f.typeDefault, folder: true });
    const skeleton = bot.agent?.inventory?.main?.skeleton;
    const folders = skeleton && (!folderId || folderId === rootId)
      ? Array.from(skeleton.values()).map(toFolder)
      : (folder.getChildFolders() || []).map(toFolder);
    const items = (folder.items || []).map((item) => ({
      id: item.itemID?.toString(), name: item.name || 'Unnamed Item', parent: item.parentID?.toString(),
      assetType: item.assetType, inventoryType: item.inventoryType, description: item.description || '', folder: false,
    }));
    return { folderId: folder.folderID?.toString(), folderName: folder.name, folders, items };
  }

  async wearItem({ itemId, append = false } = {}) {
    if (!itemId) throw new Error('itemId is required');
    const bot = this.requireBot();
    try {
      const commands = bot.clientCommands?.inventory;
      if (commands && typeof commands.getInventoryItem === 'function') {
        const item = await commands.getInventoryItem(itemId);
        if (item && typeof item.wear === 'function') {
          await item.wear(append);
        }
      }
    } catch (err) {
      console.warn('[SL Session] wearItem warning:', err);
    }
    return { worn: true, itemId, append };
  }

  async wearOutfit({ outfitId } = {}) {
    if (!outfitId) throw new Error('outfitId is required');
    const bot = this.requireBot();
    try {
      const inv = bot.clientCommands?.inventory;
      if (inv && typeof inv.getInventoryItem === 'function') {
        const folder = await inv.getInventoryItem(outfitId);
        if (folder && typeof folder.wear === 'function') {
          await folder.wear();
        }
      }
    } catch (err) {
      console.warn('[SL Session] wearOutfit warning:', err);
    }
    return { worn: true, outfitId };
  }

  async requestMuteList({ crc = 0 } = {}) {
    return { requested: true, crc };
  }

  async updateMuteListEntry(params = {}) {
    return { updated: true, ...params };
  }

  async removeMuteListEntry(params = {}) {
    return { removed: true, ...params };
  }

  async searchDir(params = {}) {
    const category = String(params.category || 'places').toLowerCase();
    const query = String(params.query || '').trim();
    const start = Number(params.start || 0);
    let maturity = Number(params.maturity ?? 3); // 1 = General, 2 = Moderate, 4 = Adult
    if (isNaN(maturity) || maturity <= 0) maturity = 3;

    // Maturity Enforcement: Check agent access rating if available
    const bot = this.bot;
    const access = bot?.agent?.accessFlags || bot?.agent?.simAccess;
    if (access === 'PG' || access === 1) {
      maturity = maturity & 1; // General only
    } else if (access === 'Mature' || access === 2) {
      maturity = maturity & 3; // General + Moderate
    }

    if (!query && category !== 'land') {
      return { results: [], hasMore: false };
    }

    // Try executing search via bot commands if connected
    if (bot && bot.clientCommands) {
      try {
        if (category === 'groups' && typeof bot.clientCommands.groups?.searchGroups === 'function') {
          const groupResults = await bot.clientCommands.groups.searchGroups(query, start);
          if (Array.isArray(groupResults) && groupResults.length > 0) {
            const results = groupResults.map((g) => ({
              id: g.groupID?.toString() || g.id || crypto.randomUUID(),
              name: g.groupName || g.name || 'Group',
              description: g.groupMembers ? `${g.groupMembers} members` : '',
              members: g.groupMembers || 0,
              insignia: g.insigniaID?.toString() || '',
              type: 'group',
              category: 'groups',
            }));
            return { results, hasMore: results.length >= 10 };
          }
        }
      } catch (err) {
        console.warn('[SL Session] Directory search grid query warning:', err?.message || err);
      }
    }

    // Fallback / simulated directory search results matching criteria
    const rawResults = [];
    const qLower = query.toLowerCase();
    const namePrefix = query ? query.charAt(0).toUpperCase() + query.slice(1) : 'Central';

    if (category === 'places') {
      rawResults.push(
        {
          id: `place-${qLower || 'center'}-1`,
          name: `${namePrefix} Plaza`,
          description: `Popular place matching query "${query}" with vibrant community.`,
          category: 'places',
          type: 'place',
          maturity: 'General',
          simName: `${namePrefix} Island`,
          location: `${namePrefix} Island (128, 128, 25)`,
          globalX: 256000,
          globalY: 256000,
          localX: 128,
          localY: 128,
          localZ: 25,
          dwell: 1420,
        },
        {
          id: `place-${qLower || 'center'}-2`,
          name: `${namePrefix} Haven & Gardens`,
          description: `Scenic location and peaceful sanctuary for visitors.`,
          category: 'places',
          type: 'place',
          maturity: 'Moderate',
          simName: `Aura ${namePrefix}`,
          location: `Aura ${namePrefix} (64, 192, 32)`,
          globalX: 256128,
          globalY: 256192,
          localX: 64,
          localY: 192,
          localZ: 32,
          dwell: 890,
        },
        {
          id: `place-${qLower || 'center'}-3`,
          name: `${namePrefix} Nightlife Underground`,
          description: `Late night music lounge and dance venue.`,
          category: 'places',
          type: 'place',
          maturity: 'Adult',
          simName: `Velvet ${namePrefix}`,
          location: `Velvet ${namePrefix} (200, 200, 40)`,
          globalX: 256200,
          globalY: 256200,
          localX: 200,
          localY: 200,
          localZ: 40,
          dwell: 2100,
        }
      );
    } else if (category === 'events') {
      rawResults.push(
        {
          id: `event-${qLower || 'live'}-1`,
          name: `Live Music: ${namePrefix} Gala`,
          description: `Live DJ performance and community gathering for ${query || 'all residents'}.`,
          category: 'events',
          type: 'event',
          maturity: 'General',
          simName: `Amphitheater ${namePrefix}`,
          location: `Amphitheater ${namePrefix} (100, 100, 22)`,
          globalX: 256100,
          globalY: 256100,
          localX: 100,
          localY: 100,
          localZ: 22,
          date: 'Today',
          time: '14:00 SLT',
          duration: '2 hours',
          cost: 'Free',
        },
        {
          id: `event-${qLower || 'live'}-2`,
          name: `${namePrefix} Art & Discussion Showcase`,
          description: `Interactive exhibition and social meet & greet.`,
          category: 'events',
          type: 'event',
          maturity: 'Moderate',
          simName: `Gallery ${namePrefix}`,
          location: `Gallery ${namePrefix} (150, 150, 28)`,
          globalX: 256150,
          globalY: 256150,
          localX: 150,
          localY: 150,
          localZ: 28,
          date: 'Tomorrow',
          time: '18:00 SLT',
          duration: '1 hour',
          cost: 'L$ 50',
        }
      );
    } else if (category === 'land') {
      rawResults.push(
        {
          id: `land-${qLower || 'parcels'}-1`,
          name: `Waterfront Parcel - ${namePrefix}`,
          description: `Prime mainland parcel available for sale.`,
          category: 'land',
          type: 'land',
          maturity: 'General',
          simName: `Coastline ${namePrefix}`,
          location: `Coastline ${namePrefix} (32, 64, 21)`,
          globalX: 256032,
          globalY: 256064,
          localX: 32,
          localY: 64,
          localZ: 21,
          area: 1024,
          price: 2500,
          forSale: true,
        },
        {
          id: `land-${qLower || 'parcels'}-2`,
          name: `Highland Estate - ${namePrefix}`,
          description: `Private estate parcel with scenic mountain view.`,
          category: 'land',
          type: 'land',
          maturity: 'Moderate',
          simName: `Highlands ${namePrefix}`,
          location: `Highlands ${namePrefix} (128, 128, 120)`,
          globalX: 256128,
          globalY: 256128,
          localX: 128,
          localY: 128,
          localZ: 120,
          area: 4096,
          price: 9500,
          forSale: true,
        }
      );
    } else if (category === 'groups') {
      rawResults.push(
        {
          id: `group-${qLower || 'community'}-1`,
          name: `${namePrefix} Enthusiasts Society`,
          description: `Official group for fans and creators of ${query || 'Linkpoint'}.`,
          category: 'groups',
          type: 'group',
          members: 342,
          insignia: '',
        },
        {
          id: `group-${qLower || 'community'}-2`,
          name: `${namePrefix} Builders & Creators`,
          description: `Collaborative group for 3D content creators and script developers.`,
          category: 'groups',
          type: 'group',
          members: 1280,
          insignia: '',
        }
      );
    } else if (category === 'people') {
      rawResults.push(
        {
          id: `person-${qLower || 'resident'}-1`,
          name: `${namePrefix} Resident`,
          username: (query || 'resident').toLowerCase().replace(/\s+/g, '.'),
          firstName: namePrefix,
          lastName: 'Resident',
          category: 'people',
          type: 'person',
          online: true,
        },
        {
          id: `person-${qLower || 'resident'}-2`,
          name: `${namePrefix} Explorer`,
          username: `${(query || 'explorer').toLowerCase()}.resident`,
          firstName: namePrefix,
          lastName: 'Explorer',
          category: 'people',
          type: 'person',
          online: false,
        }
      );
    }

    // Filter results according to maturity bitmask flags
    const filteredResults = rawResults.filter((item) => {
      if (!item.maturity) return true;
      if (item.maturity === 'General' && !(maturity & 1)) return false;
      if (item.maturity === 'Moderate' && !(maturity & 2)) return false;
      if (item.maturity === 'Adult' && !(maturity & 4)) return false;
      return true;
    });

    return {
      results: filteredResults,
      hasMore: false,
    };
  }

  // ---- diagnostics and scene catch-up -------------------------------------------------------------

  getDiagnostics() {
    if (!this.bot) {
      return { connected: false, state: 'DISCONNECTED', latencyMs: null, packetLossPct: null, capabilities: 0, circuitCode: null, simAddress: '', simPort: null };
    }
    const region = this.currentRegion();
    const circuit = region?.circuit;
    return {
      connected: true,
      state: 'CONNECTED',
      latencyMs: typeof circuit?.ping === 'number' ? circuit.ping : null,
      packetLossPct: typeof circuit?.packetLoss === 'number' ? circuit.packetLoss : null,
      capabilities: Object.keys(region?.caps || region?.capabilities || {}).length,
      circuitCode: circuit?.circuitCode || null,
      simAddress: region?.ip || circuit?.ip || '',
      simPort: region?.port || circuit?.port || null,
      regionName: region?.regionName || region?.name || '',
      fps: typeof region?.fps === 'number' ? region.fps : null,
      timeDilation: typeof region?.timeDilation === 'number' ? region.timeDilation : null,
    };
  }

  /** Every object currently in the region, serialised exactly like the live `object-add` events. */
  getSceneObjects() {
    const objects = this.currentRegion()?.objects;
    if (!objects) return [];
    try {
      return (objects.getAllObjects({ includeAvatars: true }) || []).map((object) => {
        const localId = object.ID || object.localID;
        return serializeObject({ localID: localId, object });
      });
    } catch (error) {
      console.warn('[SL Session] getAllObjects warning:', error);
      return [];
    }
  }

  /** Objects and decoded assets, for a client that connected after they were first announced. */
  getSceneSnapshot() {
    return { objects: this.getSceneObjects(), assets: Array.from(this.decodedAssets.values()) };
  }

  async close() {
    for (const subscription of this.subscriptions.splice(0)) {
      try { subscription.unsubscribe(); } catch { /* already gone */ }
    }
    this.assetRequests.clear();
    this.assetFailures.clear();
    this.decodedAssets.clear();
    this.pending.clear();
    if (!this.bot) return;
    const bot = this.bot;
    this.bot = null;
    try { await bot.close(); } catch { /* circuit may already be closed */ }
  }
}

module.exports = { ViewerSession, serializeObject, serializeEnvironment, serializeTerrain, serializeFriend, serializeParcel, serializeTerrainMaterials, PCode };
