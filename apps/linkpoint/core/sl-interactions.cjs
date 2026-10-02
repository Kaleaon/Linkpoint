/**
 * Interactions the simulator asks the user to answer: script dialogs (llDialog,
 * llTextBox) and teleport lures. Shared by the web server and the Electron
 * session so both serialize events and answer them identically.
 *
 * node-metaverse hands us event objects that the library later needs back to
 * reply (`respondToScriptDialog(event, index)`, `acceptTeleport(lure)`). The
 * client only ever sees a plain description plus an opaque id; the original
 * events stay here in `PendingInteractions`.
 *
 * Behaviour follows Lumiya's HandleScriptDialog: a button labelled
 * `!!llTextBox!!` turns the dialog into a text box, and the typed text is sent
 * back as the label of that button.
 */

const { randomUUID } = require('node:crypto');

const TEXT_BOX_MARKER = '!!llTextBox!!';
/** A ScriptDialogReply label is a single-byte-length-prefixed field, so 255 UTF-8 bytes at most. */
const MAX_REPLY_BYTES = 255;
/** Unanswered interactions kept per session; the oldest are dropped past this. */
const MAX_PENDING = 50;

const idString = (value) => (value && typeof value.toString === 'function' ? value.toString() : null);
/** A finite number from a number or numeric string; anything else (null, '', booleans, objects) is treated as missing. */
const finiteOr = (value, fallback) => {
  const usable = typeof value === 'number' || (typeof value === 'string' && value.trim() !== '');
  return usable && Number.isFinite(Number(value)) ? Number(value) : fallback;
};

function vectorArray(value) {
  if (!value) return null;
  const get = (axis) => (typeof value[axis] === 'number' ? value[axis] : typeof value[`get${axis.toUpperCase()}`] === 'function' ? value[`get${axis.toUpperCase()}`]() : NaN);
  const out = [get('x'), get('y'), get('z')];
  return out.every(Number.isFinite) ? out : null;
}

/** Describe a ScriptDialogEvent for the client. */
function serializeScriptDialog(event) {
  const buttons = Array.isArray(event.Buttons) ? event.Buttons.map(String) : [];
  const textBoxIndex = buttons.indexOf(TEXT_BOX_MARKER);
  const owner = [event.FirstName, event.LastName].filter(Boolean).join(' ').trim();
  return {
    objectId: idString(event.ObjectID),
    objectName: String(event.ObjectName || ''),
    ownerName: owner,
    message: String(event.Message || ''),
    channel: finiteOr(event.ChatChannel, 0),
    imageId: idString(event.ImageID),
    buttons,
    textBox: textBoxIndex !== -1,
    textBoxIndex,
  };
}

/** Describe a LureEvent (teleport offer) for the client. */
function serializeLure(event) {
  return {
    fromId: idString(event.from),
    fromName: String(event.fromName || ''),
    message: String(event.lureMessage || ''),
    regionId: idString(event.regionID),
    position: vectorArray(event.position),
    gridX: finiteOr(event.gridX, null),
    gridY: finiteOr(event.gridY, null),
  };
}

/** Describe a GroupNoticeEvent. Notices need no answer, so nothing is kept for them. */
function serializeGroupNotice(event) {
  return {
    groupId: idString(event.groupID),
    fromId: idString(event.from),
    fromName: String(event.fromName || 'Resident'),
    subject: String(event.subject || 'Group Notice'),
    message: String(event.message || ''),
  };
}

/**
 * Original events awaiting an answer, keyed by an opaque id. Bounded, and
 * cleared when the session closes.
 */
class PendingInteractions {
  constructor(max = MAX_PENDING) {
    this.max = max;
    this.items = new Map();
  }

  /** Remember `event` and return the id the client uses to answer it. */
  add(kind, event) {
    const id = randomUUID();
    this.items.set(id, { kind, event });
    while (this.items.size > this.max) this.items.delete(this.items.keys().next().value);
    return id;
  }

  /** The stored event for `id`, which must be of `kind`. Throws if it is unknown or already answered. */
  get(kind, id) {
    const entry = typeof id === 'string' ? this.items.get(id) : undefined;
    if (!entry || entry.kind !== kind) throw new Error('That request is no longer pending');
    return entry.event;
  }

  /** Forget `id`. Returns whether it was pending. */
  remove(id) {
    return this.items.delete(id);
  }

  clear() {
    this.items.clear();
  }

  get size() {
    return this.items.size;
  }
}

function commands(bot) {
  if (!bot || !bot.clientCommands) throw new Error('Not connected to a simulator');
  return bot.clientCommands;
}

/**
 * Answer a script dialog. Pass `buttonIndex` for a button dialog, or `text` for a
 * text box. The dialog stays pending if the grid does not acknowledge, so the
 * user can retry.
 */
async function respondScriptDialog(bot, pending, params) {
  const event = pending.get('script-dialog', params && params.id);
  const buttons = Array.isArray(event.Buttons) ? event.Buttons.map(String) : [];
  const textBoxIndex = buttons.indexOf(TEXT_BOX_MARKER);
  let reply;
  let index;
  if (textBoxIndex !== -1) {
    if (typeof params.text !== 'string' || params.text.length === 0) throw new Error('Enter some text to send');
    if (Buffer.byteLength(params.text, 'utf8') > MAX_REPLY_BYTES) throw new Error(`The reply is too long (${MAX_REPLY_BYTES} bytes at most)`);
    index = textBoxIndex;
    reply = params.text;
  } else {
    index = params.buttonIndex;
    if (!Number.isInteger(index) || index < 0 || index >= buttons.length) throw new Error('That button does not exist');
    reply = buttons[index];
  }
  // The library sends `Buttons[index]` as the reply label, so give it a copy whose label is the answer.
  const labels = buttons.slice();
  labels[index] = reply;
  await commands(bot).comms.respondToScriptDialog({ ObjectID: event.ObjectID, ChatChannel: event.ChatChannel, Buttons: labels }, index);
  pending.remove(params.id);
  return { answered: true };
}

/** Accept a teleport lure and wait for the grid's teleport result. */
async function acceptLure(bot, pending, params) {
  const lure = pending.get('lure', params && params.id);
  const result = await commands(bot).teleport.acceptTeleport(lure);
  pending.remove(params.id);
  return { accepted: true, message: result && result.message ? String(result.message) : '' };
}

/**
 * Dismiss an interaction locally. This does not tell the grid anything: the
 * client library has no call for declining a lure, and an unanswered script
 * dialog simply times out in the script.
 */
function dismissInteraction(pending, params) {
  if (!params || typeof params.id !== 'string') throw new Error('An interaction id is required');
  return { dismissed: pending.remove(params.id) };
}

/**
 * Subscribe to the library's script dialog and lure events. `send(type, data)` is
 * the backend's event sink. Returns the subscriptions so the caller can drop them.
 */
function subscribeInteractions(events, pending, send) {
  const subscriptions = [];
  const watch = (subject, kind, serialize) => {
    if (!subject || typeof subject.subscribe !== 'function') return;
    subscriptions.push(subject.subscribe((event) => {
      const id = pending.add(kind, event);
      send(kind, { id, receivedAt: Date.now(), ...serialize(event) });
    }));
  };
  watch(events.onScriptDialog, 'script-dialog', serializeScriptDialog);
  watch(events.onLure, 'lure', serializeLure);
  if (events.onGroupNotice && typeof events.onGroupNotice.subscribe === 'function') {
    subscriptions.push(events.onGroupNotice.subscribe((event) => {
      send('group-notice', { id: randomUUID(), timestamp: Date.now(), ...serializeGroupNotice(event) });
    }));
  }
  return subscriptions;
}

module.exports = {
  TEXT_BOX_MARKER,
  MAX_REPLY_BYTES,
  MAX_PENDING,
  serializeScriptDialog,
  serializeLure,
  serializeGroupNotice,
  PendingInteractions,
  subscribeInteractions,
  respondScriptDialog,
  acceptLure,
  dismissInteraction,
};
