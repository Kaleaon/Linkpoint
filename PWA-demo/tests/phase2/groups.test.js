const fs = require('fs');

// Stub window object before loading groups.js
global.window = {
  app: {
    protocol: {
      sendMessage: jest.fn()
    }
  }
};

const code = fs.readFileSync('js/groups.js', 'utf8');

// Replace export default with something jest can handle simply via evaluation
const evalCode = code.replace(/export default groupsManager;/, '')
                     .replace(/export \{ GroupsManager \};/, '')
                     .replace(/export const GroupPowers/, 'const GroupPowers');

let GroupsManager;
const mockExports = {};
const evaluateCode = new Function('exports', 'window', evalCode + '\nreturn GroupsManager;');
GroupsManager = evaluateCode(mockExports, global.window);

describe('GroupsManager', () => {
  let groupsManager;

  beforeEach(() => {
    groupsManager = new GroupsManager();
    global.window.app.protocol.sendMessage.mockClear();
  });

  test('updateCharter sends UpdateGroupInfo message', () => {
    const uuid = 'test-uuid';
    groupsManager.setGroupInfo({
      uuid,
      name: 'Test Group',
      charter: 'Old Charter',
      showInList: true,
      membershipFee: 10,
      openEnrollment: false,
      allowPublish: true,
      maturePublish: false
    });

    groupsManager.updateCharter(uuid, 'New Charter');

    expect(global.window.app.protocol.sendMessage).toHaveBeenCalledWith('UpdateGroupInfo', {
      uuid,
      charter: 'New Charter',
      showInList: true,
      insigniaID: null,
      membershipFee: 10,
      openEnrollment: false,
      allowPublish: true,
      maturePublish: false
    });
  });

  test('setInsignia sends UpdateGroupInfo message', () => {
    const uuid = 'test-uuid';
    groupsManager.setGroupInfo({
      uuid,
      name: 'Test Group',
      charter: 'Test Charter',
      showInList: false,
      membershipFee: 0,
      openEnrollment: true,
      allowPublish: false,
      maturePublish: true
    });

    groupsManager.setInsignia(uuid, 'new-insignia-uuid');

    expect(global.window.app.protocol.sendMessage).toHaveBeenCalledWith('UpdateGroupInfo', {
      uuid,
      charter: 'Test Charter',
      showInList: false,
      insigniaID: 'new-insignia-uuid',
      membershipFee: 0,
      openEnrollment: true,
      allowPublish: false,
      maturePublish: true
    });
  });
});
