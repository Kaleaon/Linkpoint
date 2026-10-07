'use strict';
/**
 * The one list of calls a client may make on a viewer session. Both hosts (the Electron IPC handler
 * and the web server's RPC route) dispatch through `callViewer`, so a method added to
 * `ViewerSession` and listed here is available on every platform.
 */
const METHODS = new Set([
  'sendChat', 'sendChatBatch', 'sendInstantMessage', 'sendGroupMessage', 'sendFriendRequest',
  'teleport', 'touchObject', 'sit', 'stand', 'setMovement', 'getBalance', 'payObject',
  'respondScriptDialog', 'acceptLure', 'acceptInventoryOffer', 'declineInventoryOffer', 'acceptGroupInvite', 'declineGroupInvite', 'dismissInteraction',
  'fetchAnimation', 'getMapBlocks', 'getFriends', 'getGroups', 'getInventory', 'getDiagnostics',
  'getSceneObjects', 'getSceneSnapshot', 'searchDir',
  'wearItem', 'wearOutfit', 'requestMuteList', 'updateMuteListEntry', 'removeMuteListEntry',
  'getAvatarProfile', 'getAvatarPicks', 'getAvatarGroups',
]);

async function callViewer(session, method, params) {
  if (typeof method !== 'string' || !METHODS.has(method)) throw new Error(`Unknown viewer call: ${String(method).slice(0, 64)}`);
  const args = params && typeof params === 'object' ? params : undefined;
  return session[method](args);
}

module.exports = { METHODS, callViewer };
