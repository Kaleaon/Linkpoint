/**
 * Second Life domain types.
 *
 * Names and value ranges follow the viewer protocol and the conventions third
 * party viewers share (Firestorm, Kokua, Lumiya): chat ranges in metres, the
 * standard inventory asset and folder types, the five-bit permission mask, and
 * the region statistics the built-in statistics bar reports.
 */

/** Chat ranges as the simulator enforces them. */
export const CHAT_RANGE = {
  /** Whisper carries 10m. */
  whisper: 10,
  /** Normal say carries 20m. */
  say: 20,
  /** Shout carries 100m. */
  shout: 100,
} as const;

export type ChatVolume = keyof typeof CHAT_RANGE;

/** The band a distance falls in, used for radar tinting and range labels. */
export function chatBand(metres: number): 'WHISPER' | 'CHAT' | 'SHOUT' | 'OUT OF RANGE' {
  if (metres <= CHAT_RANGE.whisper) return 'WHISPER';
  if (metres <= CHAT_RANGE.say) return 'CHAT';
  if (metres <= CHAT_RANGE.shout) return 'SHOUT';
  return 'OUT OF RANGE';
}

/** Sixteen-point compass, for radar bearings. */
export const COMPASS = ['N', 'NNE', 'NE', 'ENE', 'E', 'ESE', 'SE', 'SSE', 'S', 'SSW', 'SW', 'WSW', 'W', 'WNW', 'NW', 'NNW'];

/** Compass point for a bearing in degrees. */
export function bearingToCompass(degrees: number): string {
  return COMPASS[Math.round(degrees / 22.5) % 16];
}

/** Inventory asset types, matching LLAssetType. */
export type AssetType =
  | 'texture'
  | 'sound'
  | 'callingcard'
  | 'landmark'
  | 'clothing'
  | 'object'
  | 'notecard'
  | 'category'
  | 'bodypart'
  | 'animation'
  | 'gesture'
  | 'mesh'
  | 'settings'
  | 'material'
  | 'script';

/**
 * The system folders every account has. The viewer creates these on first
 * login and they cannot be renamed or deleted, which is why they are modelled
 * separately from user folders.
 */
export const SYSTEM_FOLDERS = [
  'Animations',
  'Body Parts',
  'Calling Cards',
  'Clothing',
  'Current Outfit',
  'Environments',
  'Favorites',
  'Gestures',
  'Landmarks',
  'Lost And Found',
  'Materials',
  'Notecards',
  'Objects',
  'Photo Album',
  'Received Items',
  'Scripts',
  'Sounds',
  'Textures',
  'Trash',
] as const;

/** The permission bits a resident actually sees on an inventory item. */
export interface Permissions {
  copy: boolean;
  modify: boolean;
  transfer: boolean;
}

/** Render the permission mask the way the viewer's inventory panel does. */
export function permissionLabel(p: Permissions): string {
  return [p.copy ? 'copy' : 'no copy', p.modify ? 'modify' : 'no modify', p.transfer ? 'transfer' : 'no transfer'].join(' · ');
}

export interface InventoryNode {
  id: string;
  name: string;
  /** Folders carry "category"; everything else is a leaf item. */
  type: AssetType;
  /** Item count, for folders. */
  count?: number;
  permissions?: Permissions;
  /** Prim (land impact) cost, for objects. */
  landImpact?: number;
  acquired?: string;
  creator?: string;
  description?: string;
  children?: InventoryNode[];
  /** System folders are protected from rename and delete. */
  system?: boolean;
}

export interface Resident {
  id: string;
  /** Legacy first name. */
  firstName: string;
  /** Legacy last name; "Resident" for single-name accounts. */
  lastName: string;
  /** Display name, which residents may change weekly. */
  displayName: string;
  /** Login name, lowercase, used for search and SLURLs. */
  userName: string;
  online: boolean;
  /** Last seen, for offline friends. */
  lastSeen?: string;
}

/** Full legacy name, the form the simulator uses on the wire. */
export function legacyName(r: Resident): string {
  return `${r.firstName} ${r.lastName}`;
}

/** Friendship permission bits, as granted per friend. */
export interface FriendRights {
  /** They may see when you are online. */
  seeOnline: boolean;
  /** They may locate you on the world map. */
  mapLocate: boolean;
  /** They may modify your objects. */
  modifyObjects: boolean;
}

export interface Friend extends Resident {
  rights: FriendRights;
  /** Rights you have granted them, mirrored back. */
  theirRights: FriendRights;
  region?: string;
}

export interface RadarEntry {
  id: string;
  name: string;
  /** Metres from the agent. */
  distance: number;
  /** Bearing in degrees, 0 = north. */
  bearing: number;
  meta: string;
  kind: 'avatar' | 'object';
  /** Avatars only: account age and payment status, as radar reports them. */
  isFriend?: boolean;
  typing?: boolean;
  voice?: boolean;
  /** Objects only. */
  owner?: string;
  scripts?: number;
  landImpact?: number;
}

export interface RegionInfo {
  name: string;
  /** Grid coordinates, in region units. */
  gridX: number;
  gridY: number;
  /** Maturity rating. */
  rating: 'General' | 'Moderate' | 'Adult';
  avatars: number;
  /** Estate the region belongs to. */
  estate?: string;
  note?: string;
}

/** Build the standard secondlife:// URL for a position inside a region. */
export function slurl(region: string, x: number, y: number, z: number): string {
  return `secondlife://${encodeURIComponent(region).replace(/%20/g, '%20')}/${Math.round(x)}/${Math.round(y)}/${Math.round(z)}`;
}

/** Render a local position the way the viewer's location bar does. */
export function positionLabel(x: number, y: number, z: number): string {
  return `<${Math.round(x)}, ${Math.round(y)}, ${Math.round(z)}>`;
}

export interface Landmark {
  id: string;
  name: string;
  region: string;
  x: number;
  y: number;
  z: number;
  rating: RegionInfo['rating'];
  /** Recent destinations carry a visit time; saved landmarks do not. */
  visited?: string;
  favourite?: boolean;
}

export interface Group {
  id: string;
  name: string;
  members: number;
  /** The role you hold in this group. */
  role: string;
  /** Whether group notices reach you. */
  notices: boolean;
  /** Whether the group chat session is muted. */
  muted: boolean;
  unread?: number;
  /** Group charter fee, in L$. Zero means free to join. */
  joinFee?: number;
}

/** An account may hold forty-two group slots. */
export const MAX_GROUPS = 42;

export interface ChatMessage {
  id: string;
  /** Simulator timestamp, local time. */
  time: string;
  from: string;
  text: string;
  /** Sent by the agent. */
  mine?: boolean;
  /** A simulator or viewer message rather than a resident. */
  system?: boolean;
  /** Emote, sent with /me. */
  emote?: boolean;
  volume?: ChatVolume;
  /** A SLURL the message carried. */
  link?: { title: string; url: string };
}

export type NotificationKind = 'im' | 'inventory' | 'payment' | 'notice' | 'friendship' | 'system';

export interface Notification {
  id: string;
  kind: NotificationKind;
  title: string;
  body: string;
  time: string;
  unread?: boolean;
}

/**
 * The region and viewer statistics the viewer reports. Names and healthy
 * ranges follow the built-in statistics bar.
 */
export interface ViewerStats {
  /** Viewer frames per second. */
  fps: number;
  /** Round trip to the simulator, in milliseconds. */
  ping: number;
  /** Packet loss as a percentage. */
  packetLoss: number;
  /** Inbound bandwidth, kbit/s. */
  bandwidth: number;
  /** Simulator frame rate; 45 is nominal. */
  simFps: number;
  /** Physics frame rate; 45 is nominal. */
  physicsFps: number;
  /** Time dilation, 0–1. Below 1 means the region is running slow. */
  timeDilation: number;
  /** Script time, milliseconds per frame. */
  scriptTime: number;
  /** Scripts running in the region. */
  activeScripts: number;
  /** Agents in the region. */
  agents: number;
}

/** A dialog raised by the simulator or a scripted object. */
export type SystemDialogKind =
  | 'llDialog'
  | 'permissions'
  | 'inventoryOffer'
  | 'teleportLure'
  | 'payment'
  | 'regionRestart'
  | 'friendshipOffer'
  | 'groupInvite';

export interface SystemDialogButton {
  label: string;
  kind?: 'normal' | 'primary' | 'danger';
}

export interface SystemDialog {
  kind: SystemDialogKind;
  /** The category line above the title. */
  category: string;
  title: string;
  body: string;
  /** The object, owner and channel line the viewer must always disclose. */
  meta?: string;
  buttons: SystemDialogButton[];
}

/**
 * Script permissions a scripted object may request, as named in the protocol.
 * The viewer must list every requested permission before the resident grants.
 */
export const SCRIPT_PERMISSIONS = {
  debit: 'take Linden dollars from your account',
  takeControls: 'take your movement controls',
  triggerAnimation: 'animate your avatar',
  attach: 'attach to your avatar',
  changeLinks: 'link and unlink its own parts',
  trackCamera: 'track your camera',
  controlCamera: 'control your camera',
  teleport: 'teleport you',
} as const;
