const fs = require('fs');
const path = require('path');

/**
 * Read the viewer identity from its single source of truth so the login handshake
 * and the rest of the app can never disagree (TPV_COMPLIANCE.md section 1).
 */
function readViewerIdentity(root = path.join(__dirname, '..')) {
  const text = fs.readFileSync(path.join(root, 'src/linkpoint/viewer-identity.ts'), 'utf8');
  const channel = (text.match(/export const VIEWER_CHANNEL\s*=\s*'([^']+)'/) || [])[1];
  const version = (text.match(/export const VIEWER_VERSION\s*=\s*'([^']+)'/) || [])[1];
  if (!channel || !version) throw new Error('Could not read VIEWER_CHANNEL / VIEWER_VERSION from viewer-identity.ts');
  return { channel, version };
}

/**
 * node-metaverse logs in as channel "libnmv" with its own package version. Make it
 * identify as this viewer instead. Pure so it can be tested; returns the new text,
 * or null when the file does not have the expected shape (so a library update
 * that changes it is noticed rather than silently left unpatched).
 */
function patchLoginIdentity(content, channel, version) {
  const channelLiteral = "channel: 'libnmv'";
  const versionLine = 'const version = packageJson.version;';
  if (content.includes(`channel: ${JSON.stringify(channel)}`) && content.includes(`const version = ${JSON.stringify(version)};`)) return content; // already patched
  if (!content.includes(channelLiteral) || !content.includes(versionLine)) return null;
  return content.replace(channelLiteral, `channel: ${JSON.stringify(channel)}`).replace(versionLine, `const version = ${JSON.stringify(version)};`);
}

function applyPatches(options = {}) {
  const { strict = require.main === module } = options;

  const packetPath = path.join(__dirname, '../node_modules/@caspertech/node-metaverse/dist/lib/classes/Packet.js');
  if (fs.existsSync(packetPath)) {
    let content = fs.readFileSync(packetPath, 'utf8');
    const target = "console.error('WARNING: Finished reading ' + (0, MessageClasses_1.nameFromID)(messageID) + ' but we\\'re not at the end of the packet (' + pos + ' < ' + buf.length + ', seq ' + this.sequenceNumber + ')');";
    if (content.includes(target)) {
      content = content.replace(target, "// Second Life simulator packets frequently contain extra padding or newer unparsed fields; ignore gracefully");
      fs.writeFileSync(packetPath, content, 'utf8');
      console.log('[patch-metaverse] Patched Packet.js successfully.');
    }
  }
  
  const friendCommandsPath = path.join(__dirname, '../node_modules/@caspertech/node-metaverse/dist/lib/classes/commands/FriendCommands.js');
  if (fs.existsSync(friendCommandsPath)) {
    let fcContent = fs.readFileSync(friendCommandsPath, 'utf8');
    let modified = false;
  
    // Fix OnlineNotification has() === undefined bug
    const onlineTarget = "if (this.friendsList.has(uuidStr) === undefined)";
    if (fcContent.includes(onlineTarget)) {
      fcContent = fcContent.replaceAll(onlineTarget, "if (!this.friendsList.has(uuidStr))");
      modified = true;
    }
  
    // Ensure online event fires reliably even if friend object is already present or resolving
    const onlineCheckTarget = "if (friend && !friend.online) {";
    const onlineCheckReplacement = "if (friend) {";
    if (fcContent.includes(onlineCheckTarget)) {
      fcContent = fcContent.replace(onlineCheckTarget, onlineCheckReplacement);
      modified = true;
    }
  
    const offlineCheckTarget = "if (friend !== undefined && friend.online) {";
    const offlineCheckReplacement = "if (friend !== undefined) {";
    if (fcContent.includes(offlineCheckTarget)) {
      fcContent = fcContent.replace(offlineCheckTarget, offlineCheckReplacement);
      modified = true;
    }
  
    if (modified) {
      fs.writeFileSync(friendCommandsPath, fcContent, 'utf8');
      console.log('[patch-metaverse] Patched FriendCommands.js successfully for online status.');
    }
  }

  const loginPath = path.join(__dirname, '../node_modules/@caspertech/node-metaverse/dist/lib/LoginHandler.js');
  if (fs.existsSync(loginPath)) {
    const { channel, version } = readViewerIdentity();
    const original = fs.readFileSync(loginPath, 'utf8');
    const patched = patchLoginIdentity(original, channel, version);
    if (patched === null) {
      const message = '[patch-metaverse] LoginHandler.js has an unexpected shape; the login would not identify as ' + channel + '.';
      if (strict) throw new Error(message);
      console.error(message);
    } else if (patched !== original) {
      fs.writeFileSync(loginPath, patched, 'utf8');
      console.log('[patch-metaverse] Login now identifies as ' + channel + ' ' + version + '.');
    }
  }
}

module.exports = { readViewerIdentity, patchLoginIdentity, applyPatches };

if (require.main === module) applyPatches();
