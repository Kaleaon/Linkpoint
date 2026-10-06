'use strict';
// Viewer actions shared by the web server (src/server/sl-session.ts) and the
// desktop app (core/viewer-session.cjs), so the two backends cannot drift.
//
// Every function takes the connected bot and validated, plain-data arguments,
// and returns plain data. Inputs are checked here, because they arrive from the
// renderer process / browser and are used to drive a live account. Nothing here
// invents a result: a call either reports what the grid answered or throws.
//
// Protocol facts below come from Lumiya's recovered code, checked against its
// smali: SLObjectInfo.attachmentIDFromState and SLAttachmentPoint.

const UUID_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
const ZERO_UUID = '00000000-0000-0000-0000-000000000000';

// Attachment point names, indexed by id. 31-38 are the HUD points.
const ATTACHMENT_NAMES = {
  1: 'Chest', 2: 'Skull', 3: 'Left Shoulder', 4: 'Right Shoulder', 5: 'Left Hand', 6: 'Right Hand',
  7: 'Left Foot', 8: 'Right Foot', 9: 'Spine', 10: 'Pelvis', 11: 'Mouth', 12: 'Chin', 13: 'Left Ear',
  14: 'Right Ear', 15: 'Left Eyeball', 16: 'Right Eyeball', 17: 'Nose', 18: 'R Upper Arm', 19: 'R Forearm',
  20: 'L Upper Arm', 21: 'L Forearm', 22: 'Right Hip', 23: 'R Upper Leg', 24: 'R Lower Leg', 25: 'Left Hip',
  26: 'L Upper Leg', 27: 'L Lower Leg', 28: 'Stomach', 29: 'Left Pec', 30: 'Right Pec',
  31: 'Center 2', 32: 'Top Right', 33: 'Top', 34: 'Top Left', 35: 'Center', 36: 'Bottom Left', 37: 'Bottom', 38: 'Bottom Right',
  39: 'Neck', 40: 'Avatar Center', 41: 'Left Ring Finger', 42: 'Right Ring Finger', 43: 'Tail Base', 44: 'Tail Tip',
  45: 'Left Wing', 46: 'Right Wing', 47: 'Jaw', 48: 'Alt Left Ear', 49: 'Alt Right Ear', 50: 'Alt Left Eye',
  51: 'Alt Right Eye', 52: 'Tongue', 53: 'Groin', 54: 'Left Hind Foot', 55: 'Right Hind Foot',
};
const isHudPoint = (id) => id >= 31 && id <= 38;

/**
 * Attachment point from the ObjectUpdate "State" byte: the two nibbles are
 * swapped. (Lumiya: ((s & 0xF0) >> 4) | ((s & 0x0F) << 4).)
 */
function attachmentIdFromState(state) {
  const s = Number(state) & 0xff;
  return ((s & 0xf0) >> 4) | ((s & 0x0f) << 4);
}

/**
 * Attachment details for a node-metaverse GameObject. node-metaverse keeps the
 * raw wire byte in `attachmentPoint` and the already-swapped value in `State`
 * for attachments, so both are accepted and must name a known point. Objects
 * that are not attachments report point 0.
 */
function attachmentInfo(object) {
  if (!object || !object.IsAttachment) return { attachmentPoint: 0, attachmentName: null, isHud: false };
  const candidates = [
    attachmentIdFromState(object.attachmentPoint),
    Number(object.attachmentPoint),
    Number(object.State),
    attachmentIdFromState(object.State),
  ];
  const id = candidates.find((value) => Number.isInteger(value) && ATTACHMENT_NAMES[value]) || 0;
  return { attachmentPoint: id, attachmentName: ATTACHMENT_NAMES[id] || null, isHud: isHudPoint(id) };
}

const finite = (value, label) => {
  const number = Number(value);
  if (!Number.isFinite(number)) throw new Error(`${label} must be a number`);
  return number;
};

function requireUuid(value, label = 'id') {
  const text = String(value || '');
  if (!UUID_PATTERN.test(text) || text === ZERO_UUID) throw new Error(`${label} must be a valid UUID`);
  return text;
}

function loadLibrary(lib) {
  return lib || require('@caspertech/node-metaverse');
}

function commands(bot) {
  const c = bot && bot.clientCommands;
  if (!c) throw new Error('Not connected to Second Life');
  return c;
}

/** Parse a secondlife:// or maps.secondlife.com URL, "Region/x/y/z", or Hypergrid URI (e.g. hg.osgrid.org:8002:RegionName). */
function parseDestination(text) {
  const raw = String(text || '').trim();
  if (!raw) throw new Error('Enter a destination');

  let path = raw;
  let scheme = '';
  let queryStr = '';

  const queryIdx = raw.indexOf('?');
  if (queryIdx !== -1) {
    queryStr = raw.substring(queryIdx + 1);
    path = raw.substring(0, queryIdx);
  }

  const urlMatch = path.match(/^(secondlife:\/\/(?:\/app\/teleport\/)?|https?:\/\/maps\.secondlife\.com\/secondlife\/|https?:\/\/)(.+)$/i);
  if (urlMatch) {
    scheme = urlMatch[1].toLowerCase();
    path = urlMatch[2];
  }

  const parseQueryParams = (qs) => {
    if (!qs) return null;
    const params = new URLSearchParams(qs);
    const frameId = params.get('manifold_frame_id') || params.get('frame_id');
    if (!frameId && !params.get('topology_type')) return null;
    return {
      frameId: frameId || 'flat-2d',
      topologyType: (params.get('topology_type') || 'FLAT_2D').toUpperCase(),
      radius: Number(params.get('radius')) || 0,
      curvature: Number(params.get('curvature')) || 0,
      originX: Number(params.get('origin_x')) || 0,
      originY: Number(params.get('origin_y')) || 0,
      originZ: Number(params.get('origin_z')) || 0,
      sizeX: Number(params.get('size_x') || params.get('circumference')) || 262144,
      sizeY: Number(params.get('size_y')) || 262144,
    };
  };

  const manifoldFrame = parseQueryParams(queryStr);

  const hgColonMatch = path.match(/^([a-zA-Z0-9.-]+:\d+):([^/]+)(?:\/(.*))?$/);
  const hgSlashMatch = path.match(/^([a-zA-Z0-9.-]+:\d+)\/?(.*)$/);

  const parseCoords = (coordParts) => {
    const [x, y, z] = [coordParts[0], coordParts[1], coordParts[2]].map((p) => (p === undefined || p === '' ? undefined : Number(p)));
    const coordinates = { x: x ?? 128, y: y ?? 128, z: z ?? 30 };
    for (const [axis, value] of Object.entries(coordinates)) {
      if (!Number.isFinite(value)) throw new Error(`Coordinate ${axis} is not a number`);
    }
    if (coordinates.x < 0 || coordinates.x > 256 || coordinates.y < 0 || coordinates.y > 256) throw new Error('Coordinates must be within the region (0-256)');
    if (coordinates.z < -100 || coordinates.z > 4096) throw new Error('Altitude is out of range');
    return coordinates;
  };

  if (hgColonMatch) {
    const hostPort = hgColonMatch[1];
    const regionName = decodeURIComponent(hgColonMatch[2].trim());
    const coordParts = hgColonMatch[3] ? hgColonMatch[3].split('/').map((p) => decodeURIComponent(p.trim())).filter(Boolean) : [];
    const proto = scheme.startsWith('https') ? 'https' : 'http';
    const gridUri = `${proto}://${hostPort}`;
    const gatekeeperUrl = `${gridUri}/gatekeeper`;
    const coords = parseCoords(coordParts);

    return {
      isHypergrid: true,
      gridUri,
      gatekeeperUrl,
      region: regionName || 'Home',
      ...coords,
      ...(manifoldFrame ? { manifoldFrame } : {}),
    };
  } else if (hgSlashMatch && hgSlashMatch[1].includes(':')) {
    const hostPort = hgSlashMatch[1];
    const subpath = hgSlashMatch[2];
    const parts = subpath ? subpath.split('/').map((p) => decodeURIComponent(p.trim())).filter(Boolean) : [];
    const proto = scheme.startsWith('https') ? 'https' : 'http';
    const gridUri = `${proto}://${hostPort}`;
    const gatekeeperUrl = `${gridUri}/gatekeeper`;

    let regionName = parts[0] || 'Home';
    let coordParts = parts.slice(1);
    if (parts[0] && !isNaN(Number(parts[0]))) {
      regionName = 'Home';
      coordParts = parts;
    }

    const coords = parseCoords(coordParts);

    return {
      isHypergrid: true,
      gridUri,
      gatekeeperUrl,
      region: regionName,
      ...coords,
      ...(manifoldFrame ? { manifoldFrame } : {}),
    };
  }

  const parts = path.split('/').map((part) => decodeURIComponent(part.trim())).filter((part, i) => part || i > 0);
  const region = parts[0];
  if (!region) throw new Error('Destination has no region name');
  const [x, y, z] = [parts[1], parts[2], parts[3]].map((part) => (part === undefined || part === '' ? undefined : Number(part)));
  const coordinates = { x: x ?? 128, y: y ?? 128, z: z ?? 30 };
  for (const [axis, value] of Object.entries(coordinates)) {
    if (!Number.isFinite(value)) throw new Error(`Coordinate ${axis} is not a number`);
  }
  if (coordinates.x < 0 || coordinates.x > 256 || coordinates.y < 0 || coordinates.y > 256) throw new Error('Coordinates must be within the region (0-256)');
  if (coordinates.z < -100 || coordinates.z > 4096) throw new Error('Altitude is out of range');
  return { region, ...coordinates };
}

async function teleport(bot, params, lib) {
  const { Vector3 } = loadLibrary(lib);
  const target = params.region
    ? { isHypergrid: Boolean(params.isHypergrid), gridUri: params.gridUri, gatekeeperUrl: params.gatekeeperUrl, region: String(params.region), x: finite(params.x, 'x'), y: finite(params.y, 'y'), z: finite(params.z, 'z') }
    : parseDestination(params.destination);

  if (target.isHypergrid) {
    let result = null;
    const c = commands(bot);
    if (typeof c.teleport?.performHypergridTeleport === 'function') {
      result = await c.teleport.performHypergridTeleport(target);
    } else if (typeof bot.performGatekeeperTeleport === 'function') {
      result = await bot.performGatekeeperTeleport(target);
    } else if (typeof c.teleport?.teleportTo === 'function') {
      result = await c.teleport.teleportTo(target.region, new Vector3([target.x, target.y, target.z]), new Vector3([0, 1, 0]));
    }
    return { requested: target, isHypergrid: true, message: result && result.message ? String(result.message) : 'Hypergrid teleport completed' };
  }

  const result = await commands(bot).teleport.teleportTo(target.region, new Vector3([target.x, target.y, target.z]), new Vector3([0, 1, 0]));
  return { requested: target, message: result && result.message ? String(result.message) : '' };
}

/**
 * Touch an object. `face`, `uv` and `st` are optional; when the viewer could not
 * work out which face was hit they are left out and the library's defaults apply,
 * which scripts then see as face 0 / zero coordinates.
 */
async function touchObject(bot, params, lib) {
  const { UUID, Vector3 } = loadLibrary(lib);
  const hasId = params.id !== undefined && params.id !== null && params.id !== '';
  const hasLocal = Number.isInteger(params.localId) && params.localId > 0;
  if (!hasId && !hasLocal) throw new Error('A target object is required');
  const target = hasLocal ? params.localId : new UUID(requireUuid(params.id, 'object id'));
  const face = params.face === undefined ? undefined : Math.floor(finite(params.face, 'face'));
  if (face !== undefined && (face < 0 || face > 255)) throw new Error('face is out of range');
  const vec = (value, label) => (value === undefined ? undefined : new Vector3((Array.isArray(value) ? value : [value.x, value.y, value.z]).slice(0, 3).map((n) => finite(n, label))));
  await commands(bot).region.touchObject(target, undefined, vec(params.uv, 'uv'), vec(params.st, 'st'), face, vec(params.position, 'position'));
  return { touched: hasLocal ? params.localId : String(params.id) };
}

async function sit(bot, params, lib) {
  const { UUID, Vector3 } = loadLibrary(lib);
  if (!params || !params.id) { commands(bot).movement.sitOnGround(); return { sitting: 'ground' }; }
  await commands(bot).movement.sitOnObject(new UUID(requireUuid(params.id, 'object id')), new Vector3([0, 0, 0]));
  return { sitting: String(params.id) };
}

function stand(bot) {
  commands(bot).movement.stand();
  return { standing: true };
}

/** The account's currency balance as reported by the grid. */
async function getBalance(bot) {
  const balance = await commands(bot).grid.getBalance();
  const currencySymbol = bot?.gridInfo?.currencySymbol || bot?.gridInfo?.currency_symbol || params_currency_symbol(bot) || 'L$';
  const isZeroCurrency = Boolean(bot?.gridInfo?.isZeroCurrency || bot?.gridInfo?.is_zero_currency || bot?.isZeroCurrency);
  if (!Number.isFinite(Number(balance))) throw new Error('The grid returned no balance');
  return { balance: Number(balance), currencySymbol, isZeroCurrency };
}

function params_currency_symbol(bot) {
  return bot?.options?.currencySymbol || bot?.options?.currency_symbol;
}

// ---- login -----------------------------------------------------------------

/**
 * Split a login name the way Lumiya did (SLAuth.SendLoginRequest, recovered from
 * the original smali): the first of ' ', '.' or '_' ends the first name, the rest
 * is the last name, and a missing last name means "Resident".
 */
function parseLoginName(text) {
  const name = String(text || '').trim();
  if (!name) throw new Error('Enter your avatar name');
  let split = name.length;
  for (const separator of ' ._') {
    const at = name.indexOf(separator);
    if (at !== -1 && at < split) split = at;
  }
  const firstName = name.slice(0, split).trim();
  const lastName = (split < name.length ? name.slice(split + 1) : '').trim() || 'Resident';
  if (!firstName) throw new Error('Enter your avatar name');
  if (/[\u0000-\u001f<>&"']/.test(firstName + lastName)) throw new Error('The avatar name contains characters that are not allowed');
  return { firstName, lastName };
}

/**
 * Start location, as Lumiya sent it: "first" means the home location, "uri:..."
 * (a region and coordinates) is passed through, and anything else is "last".
 * "home" and "last" are accepted directly. A uri must name a region and may only
 * carry three numeric coordinates, so a caller cannot smuggle other login fields.
 */
function normalizeStart(start) {
  const value = String(start || 'last').trim();
  const lower = value.toLowerCase();
  if (!value || lower === 'last' || lower === 'last location') return 'last';
  if (lower === 'first' || lower === 'home') return 'home';

  let region = '';
  let x = 128;
  let y = 128;
  let z = 30;

  if (/^uri:/i.test(value)) {
    const parts = value.slice(4).split('&');
    region = parts[0];
    const coordinates = parts.slice(1);
    if (!region || !/^[\w .'-]{1,64}$/.test(region) || (coordinates.length !== 0 && coordinates.length !== 3) || coordinates.some((c) => !/^-?\d{1,4}$/.test(c))) {
      throw new Error('The start location is not a valid region and position');
    }
    if (coordinates.length === 3) {
      x = Number(coordinates[0]);
      y = Number(coordinates[1]);
      z = Number(coordinates[2]);
    }
  } else {
    try {
      const dest = parseDestination(value);
      if (!dest || !dest.region) {
        throw new Error('The start location is not a valid region and position');
      }
      region = dest.region;
      x = dest.x ?? 128;
      y = dest.y ?? 128;
      z = dest.z ?? 30;
    } catch {
      throw new Error('The start location is not a valid region and position');
    }
  }

  if (!region || !/^[\w .'-]{1,64}$/.test(region) || !Number.isFinite(x) || !Number.isFinite(y) || !Number.isFinite(z)) {
    throw new Error('The start location is not a valid region and position');
  }

  return `uri:${region}&${x}&${y}&${z}`;
}

const { readViewerIdentity } = require('../scripts/patch-metaverse.cjs');

function getViewerIdentity() {
  try {
    return readViewerIdentity();
  } catch (_e) {
    return { channel: 'Linkpoint Viewer', version: '2.0.0' };
  }
}

/** node-metaverse LoginParameters for a request from the browser or renderer. */
function buildLoginParams(request, lib) {
  const { LoginParameters } = loadLibrary(lib);
  const { firstName, lastName } = parseLoginName(request.username);
  const password = String(request.password || '');
  if (!password) throw new Error('Enter your password');
  let url;
  try { url = new URL(String(request.loginUrl || '')); } catch { throw new Error('The login address is not valid'); }
  if (url.protocol !== 'https:' && url.protocol !== 'http:') throw new Error('The login address must be http(s)');
  const params = new LoginParameters();
  params.firstName = firstName;
  params.lastName = lastName;
  params.password = password;
  params.start = normalizeStart(request.start);
  params.url = url.toString();
  // Multi-factor authentication: the code the resident typed after a challenge,
  // and the hash a previous successful MFA login returned for this device.
  if (request.mfaToken) params.token = String(request.mfaToken).replace(/\s+/g, '').slice(0, 32);
  if (request.mfaHash) params.mfa_hash = String(request.mfaHash).slice(0, 256);
  const identity = getViewerIdentity();
  params.channel = identity.channel;
  params.version = identity.version;
  return params;
}

const LOGIN_REASONS = {
  mfa_challenge: { code: 'mfa_required', message: 'This account uses multi-factor authentication. Enter the code from your authenticator app.' },
  key: { code: 'bad_credentials', message: 'The name or password is incorrect.' },
  presence: { code: 'already_logged_in', message: 'This account is already logged in. Wait a minute and try again, or log out of the other session.' },
  tos: { code: 'terms', message: 'The grid requires you to accept its Terms of Service before logging in.' },
  update: { code: 'update_required', message: 'The grid requires a newer viewer version.' },
  critical: { code: 'critical_message', message: 'The grid has a critical message that must be read before logging in.' },
  disabled: { code: 'account_disabled', message: 'This account has been disabled.' },
  mfa_failure: { code: 'mfa_failed', message: 'The multi-factor code was not accepted. Try the next code.' },
};

/** Error that carries the structured result of describeLoginError across process boundaries. */
const LOGIN_FAILURE_PREFIX = 'LOGIN_FAILURE:';
function loginFailure(error) {
  const details = describeLoginError(error);
  // Electron only preserves an error's message across IPC, so the details travel in it.
  const failure = new Error(LOGIN_FAILURE_PREFIX + JSON.stringify(details));
  failure.details = details;
  return failure;
}

/**
 * Turn a node-metaverse LoginError (or anything thrown while logging in) into
 * plain data the interface can act on. The grid's own message is kept: it is
 * what the grid said, and more specific than our summary.
 */
function describeLoginError(error) {
  const reason = error && typeof error.reason === 'string' ? error.reason : '';
  const known = LOGIN_REASONS[reason];
  const gridMessage = error && typeof error.message === 'string' ? error.message : '';
  return {
    reason,
    code: known ? known.code : 'login_failed',
    mfaRequired: reason === 'mfa_challenge' || reason === 'mfa_failure',
    message: known ? known.message : (gridMessage || 'Login failed'),
    gridMessage,
  };
}

async function payObject(bot, params = {}, lib) {
  const isZeroCurrency = Boolean(params.isZeroCurrency || bot?.gridInfo?.isZeroCurrency || bot?.isZeroCurrency);
  if (isZeroCurrency) throw new Error('Payments are disabled on zero-currency grids');
  const { UUID } = loadLibrary(lib);
  const targetId = params.targetId || params.id || params.objectId;
  if (!targetId) throw new Error('A target object is required');
  const amount = Math.floor(finite(params.amount || params.price, 'amount'));
  if (amount <= 0) throw new Error('Payment amount must be greater than 0');
  const description = String(params.description || params.targetName || 'Object payment');

  const grid = commands(bot).grid;
  if (typeof grid.payObject === 'function') {
    const targetUUID = new UUID(requireUuid(targetId, 'target object id'));
    const object = bot.currentRegion?.objects?.getObjectByUUID?.(targetUUID);
    if (object) {
      await grid.payObject(object, amount);
    } else {
      await grid.pay(targetUUID, amount, description, 5001);
    }
  } else if (typeof grid.pay === 'function') {
    const targetUUID = new UUID(requireUuid(targetId, 'target object id'));
    await grid.pay(targetUUID, amount, description, 5001);
  } else {
    throw new Error('Payment not supported by this connection');
  }
  const balanceResult = await getBalance(bot).catch(() => null);
  return { paid: true, targetId: String(targetId), amount, description, balance: balanceResult ? balanceResult.balance : null };
}

async function payAvatar(bot, params = {}, lib) {
  const isZeroCurrency = Boolean(params.isZeroCurrency || bot?.gridInfo?.isZeroCurrency || bot?.isZeroCurrency);
  if (isZeroCurrency) throw new Error('Payments are disabled on zero-currency grids');
  const { UUID } = loadLibrary(lib);
  const targetId = requireUuid(params.targetId || params.id || params.avatarId, 'target avatar id');
  const amount = Math.floor(finite(params.amount || params.price, 'amount'));
  if (amount <= 0) throw new Error('Payment amount must be greater than 0');
  const description = String(params.description || params.targetName || 'Resident gift/tip');

  const grid = commands(bot).grid;
  if (typeof grid.payAvatar === 'function') {
    await grid.payAvatar(new UUID(targetId), amount, description);
  } else if (typeof grid.pay === 'function') {
    await grid.pay(new UUID(targetId), amount, description, 5000);
  } else {
    throw new Error('Payment not supported by this connection');
  }
  const balanceResult = await getBalance(bot).catch(() => null);
  return { paid: true, targetId: String(targetId), amount, description, balance: balanceResult ? balanceResult.balance : null };
}

async function getTransactionHistory(bot, params) {
  const balance = await getBalance(bot);
  return {
    balance: balance.balance,
    currencySymbol: balance.currencySymbol,
    isZeroCurrency: balance.isZeroCurrency,
    transactions: []
  };
}

module.exports = {
  parseLoginName, normalizeStart, buildLoginParams, describeLoginError, loginFailure, LOGIN_FAILURE_PREFIX, LOGIN_REASONS,
  ATTACHMENT_NAMES, isHudPoint, attachmentIdFromState, attachmentInfo,
  parseDestination, teleport, touchObject, sit, stand, getBalance, requireUuid,
  payObject, payAvatar, getTransactionHistory,
};
