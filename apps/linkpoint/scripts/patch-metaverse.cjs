const fs = require('fs');
const path = require('path');

/**
 * Read the viewer identity from its single source of truth so the login handshake
 * and the rest of the app can never disagree (TPV_COMPLIANCE.md section 1).
 */
function readViewerIdentity(root) {
  const candidates = [
    root ? path.join(root, 'src/linkpoint/viewer-identity.ts') : null,
    root ? path.join(root, 'apps/linkpoint/src/linkpoint/viewer-identity.ts') : null,
    path.join(__dirname, '../src/linkpoint/viewer-identity.ts'),
    path.join(__dirname, '../../src/linkpoint/viewer-identity.ts'),
    path.join(process.cwd(), 'src/linkpoint/viewer-identity.ts'),
    path.join(process.cwd(), 'apps/linkpoint/src/linkpoint/viewer-identity.ts')
  ].filter(Boolean);

  for (const candidate of candidates) {
    if (fs.existsSync(candidate)) {
      const text = fs.readFileSync(candidate, 'utf8');
      const channel = (text.match(/export const VIEWER_CHANNEL\s*=\s*'([^']+)'/) || [])[1];
      const version = (text.match(/export const VIEWER_VERSION\s*=\s*'([^']+)'/) || [])[1];
      if (channel && version) {
        return { channel, version };
      }
    }
  }
  return { channel: 'Linkpoint Viewer', version: '2.0.0' };
}

/**
 * Legacy string replacement helper kept for pure testing of text replacement.
 */
function patchLoginIdentity(content, channel, version) {
  const channelLiteral = "channel: 'libnmv'";
  const versionLine = 'const version = packageJson.version;';
  if (content.includes(`channel: ${JSON.stringify(channel)}`) && content.includes(`const version = ${JSON.stringify(version)};`)) return content;
  if (!content.includes(channelLiteral) || !content.includes(versionLine)) return null;
  return content.replace(channelLiteral, `channel: ${JSON.stringify(channel)}`).replace(versionLine, `const version = ${JSON.stringify(version)};`);
}

/**
 * Inject viewer identity parameters dynamically into Node.js memory at runtime.
 * This intercepts LoginHandler and XML-RPC calls to enforce official channel and version constants
 * without requiring file system modifications targeting node_modules.
 */
function injectRuntimeViewerIdentity(root) {
  const { channel, version } = readViewerIdentity(root);

  // Hook xmlrpc Client prototype to intercept 'login_to_simulator'
  try {
    const xmlrpc = require('xmlrpc');
    if (xmlrpc && (xmlrpc.createClient || xmlrpc.createSecureClient)) {
      const dummyClient = xmlrpc.createClient
        ? xmlrpc.createClient({ host: 'localhost', port: 80, path: '/' })
        : xmlrpc.createSecureClient({ host: 'localhost', port: 443, path: '/' });
      const clientProto = Object.getPrototypeOf(dummyClient);
      if (clientProto && clientProto.methodCall) {
        const originalMethodCall = clientProto.methodCall;
        if (!originalMethodCall.__identityInjected) {
          const wrappedMethodCall = function (method, params, callback) {
            if (method === 'login_to_simulator' && Array.isArray(params) && params[0] && typeof params[0] === 'object') {
              params[0].channel = channel;
              const versionParts = version.split('.');
              const major = versionParts[0] || '0';
              const minor = versionParts[1] || '0';
              const patch = versionParts[2] || '0';
              let build = major.padStart(2, '0') + minor.padStart(2, '0') + patch.padStart(2, '0');
              build = build.replace(/^0+/, '');

              params[0].major = major;
              params[0].minor = minor;
              params[0].patch = patch;
              params[0].build = build;
              params[0].version = `${version}.${build}`;
            }
            return originalMethodCall.call(this, method, params, callback);
          };
          wrappedMethodCall.__identityInjected = true;
          clientProto.methodCall = wrappedMethodCall;
        }
      }
    }
  } catch (_e) {
    /* ignore if xmlrpc is not present in environment */
  }

  // Intercept LoginHandler.prototype.Login
  try {
    const nmv = require('@caspertech/node-metaverse');
    if (nmv && nmv.LoginHandler && nmv.LoginHandler.prototype) {
      const originalLogin = nmv.LoginHandler.prototype.Login;
      if (originalLogin && !originalLogin.__identityInjected) {
        const wrappedLogin = function (loginParams) {
          if (loginParams && typeof loginParams === 'object') {
            loginParams.channel = channel;
            loginParams.version = version;
          }
          return originalLogin.call(this, loginParams);
        };
        wrappedLogin.__identityInjected = true;
        nmv.LoginHandler.prototype.Login = wrappedLogin;
      }
    }
  } catch (_e) {
    /* ignore if node-metaverse is not present or loaded yet */
  }

  return { channel, version };
}

function applyPatches(options = {}) {
  const { strict = require.main === module, root } = options;

  let nmvDir;
  try {
    nmvDir = path.dirname(require.resolve('@caspertech/node-metaverse/package.json'));
  } catch (_e) {
    const candidates = [
      path.join(__dirname, '../node_modules/@caspertech/node-metaverse'),
      path.join(__dirname, '../../node_modules/@caspertech/node-metaverse'),
      path.join(process.cwd(), 'node_modules/@caspertech/node-metaverse')
    ];
    nmvDir = candidates.find(c => fs.existsSync(c)) || candidates[0];
  }

  const safeWrite = (filePath, content, label) => {
    try {
      fs.writeFileSync(filePath, content, 'utf8');
      console.log(`[patch-metaverse] ${label}`);
    } catch (err) {
      if (strict && err.code !== 'EACCES' && err.code !== 'EROFS') {
        throw err;
      }
      console.warn(`[patch-metaverse] Could not patch ${path.basename(filePath)} (read-only filesystem or permissions): ${err.message}`);
    }
  };

  const packetPath = path.join(nmvDir, 'dist/lib/classes/Packet.js');
  if (fs.existsSync(packetPath)) {
    try {
      let content = fs.readFileSync(packetPath, 'utf8');
      const target = "console.error('WARNING: Finished reading ' + (0, MessageClasses_1.nameFromID)(messageID) + ' but we\\'re not at the end of the packet (' + pos + ' < ' + buf.length + ', seq ' + this.sequenceNumber + ')');";
      if (content.includes(target)) {
        content = content.replace(target, "// Second Life simulator packets frequently contain extra padding or newer unparsed fields; ignore gracefully");
        safeWrite(packetPath, content, 'Patched Packet.js successfully.');
      }
    } catch (_e) {
      /* read error on read-only */
    }
  }

  const friendCommandsPath = path.join(nmvDir, 'dist/lib/classes/commands/FriendCommands.js');
  if (fs.existsSync(friendCommandsPath)) {
    try {
      let fcContent = fs.readFileSync(friendCommandsPath, 'utf8');
      let modified = false;

      const onlineTarget = "if (this.friendsList.has(uuidStr) === undefined)";
      if (fcContent.includes(onlineTarget)) {
        fcContent = fcContent.replaceAll(onlineTarget, "if (!this.friendsList.has(uuidStr))");
        modified = true;
      }

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
        safeWrite(friendCommandsPath, fcContent, 'Patched FriendCommands.js successfully for online status.');
      }
    } catch (_e) {
      /* read error on read-only */
    }
  }

  const capsPath = path.join(nmvDir, 'dist/lib/classes/Caps.js');
  if (fs.existsSync(capsPath)) {
    let capsContent = fs.readFileSync(capsPath, 'utf8');
    if (!capsContent.includes("req.push('AgentInventoryService');")) {
      const eol = capsContent.includes('\r\n') ? '\r\n' : '\n';
      capsContent = capsContent.replace("req.push('AgentPreferences');", `req.push('AgentPreferences');${eol}        req.push('AgentInventoryService');${eol}        req.push('AgentInventoryService3');`);
      fs.writeFileSync(capsPath, capsContent, 'utf8');
      console.log('[patch-metaverse] Patched Caps.js successfully for AgentInventoryService.');
    }
  }

  // Programmatic runtime viewer identity injection (in-memory)
  const identity = injectRuntimeViewerIdentity(root);
  console.log(`[patch-metaverse] Programmatic identity injection active for ${identity.channel} ${identity.version}.`);
}

module.exports = { readViewerIdentity, patchLoginIdentity, injectRuntimeViewerIdentity, applyPatches };

if (require.main === module) applyPatches();

