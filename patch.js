const fs = require('fs');
const file = 'PWA-demo/js/groups.js';
let data = fs.readFileSync(file, 'utf8');

data = data.replace(
`  updateCharter(groupUUID, charter) {
    const group = this.groups.get(groupUUID);
    if (group) {
      group.charter = charter;
      this.notifyChange('group-updated', groupUUID);
    }

    // TODO: Send UpdateGroupInfo message
  }`,
`  updateCharter(groupUUID, charter) {
    const group = this.groups.get(groupUUID);
    if (group) {
      group.charter = charter;
      this.notifyChange('group-updated', groupUUID);

      if (window.app && window.app.protocol) {
        window.app.protocol.sendMessage('UpdateGroupInfo', {
          uuid: group.uuid,
          charter: group.charter,
          showInList: group.showInList,
          insigniaID: group.insigniaID,
          membershipFee: group.membershipFee,
          openEnrollment: group.openEnrollment,
          allowPublish: group.allowPublish,
          maturePublish: group.maturePublish
        });
      }
    }
  }`
);

data = data.replace(
`  setInsignia(groupUUID, insigniaID) {
    const group = this.groups.get(groupUUID);
    if (group) {
      group.insigniaID = insigniaID;
      this.notifyChange('group-updated', groupUUID);
    }

    // TODO: Send UpdateGroupInfo message
  }`,
`  setInsignia(groupUUID, insigniaID) {
    const group = this.groups.get(groupUUID);
    if (group) {
      group.insigniaID = insigniaID;
      this.notifyChange('group-updated', groupUUID);

      if (window.app && window.app.protocol) {
        window.app.protocol.sendMessage('UpdateGroupInfo', {
          uuid: group.uuid,
          charter: group.charter,
          showInList: group.showInList,
          insigniaID: group.insigniaID,
          membershipFee: group.membershipFee,
          openEnrollment: group.openEnrollment,
          allowPublish: group.allowPublish,
          maturePublish: group.maturePublish
        });
      }
    }
  }`
);

fs.writeFileSync(file, data, 'utf8');
