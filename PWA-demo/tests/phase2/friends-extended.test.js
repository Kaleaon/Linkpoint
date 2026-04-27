const assert = require('assert');

// Mock browser environment
global.window = {};

// Load dependencies
const fs = require('fs');
const path = require('path');

const slMessagesCode = fs.readFileSync(path.join(__dirname, '../../js/sl-messages.js'), 'utf8');
eval(slMessagesCode);

const friendsExtendedCode = fs.readFileSync(path.join(__dirname, '../../js/friends-extended.js'), 'utf8');

// Simple mock for testing
let sentMessages = [];
window.app = {
  protocolManager: {
    agentId: '11111111-2222-3333-4444-555555555555',
    sessionId: '66666666-7777-8888-9999-000000000000',
    sendMessage: (type, data) => {
      sentMessages.push({ type, data });
    }
  }
};

// We need to slightly patch the module export for CJS testing since it uses ES6 export
const codeToRun = friendsExtendedCode.replace(/export /g, '')
                                     .replace('default friendsExtended;', 'global.friendsExtended = friendsExtended;')
                                     .replace('{ FriendsExtended, FriendRights, FriendStatus };', '');
eval(codeToRun);

function runTests() {
  console.log('Running FriendsExtended tests...');

  // Test grantRights
  sentMessages = [];
  friendsExtended.grantRights('12345678-1234-1234-1234-123456789012', 3);

  assert.strictEqual(sentMessages.length, 1, 'Should have sent one message');
  assert.strictEqual(sentMessages[0].type, 'GrantUserRights', 'Message type should be GrantUserRights');
  assert.strictEqual(sentMessages[0].data.friend_id, '12345678-1234-1234-1234-123456789012', 'Friend ID should match');
  assert.strictEqual(sentMessages[0].data.rights, 3, 'Rights should match');
  assert.ok(sentMessages[0].data.payload, 'Payload should be present');
  assert.strictEqual(sentMessages[0].data.payload.length, 53, 'Payload length should be 53 bytes');

  console.log('✅ All tests passed!');
}

runTests();
