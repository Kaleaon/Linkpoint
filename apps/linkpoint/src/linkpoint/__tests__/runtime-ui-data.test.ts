import { describe, expect, it, vi } from 'vitest';
import { Utils } from '../utils';
import { FriendsExtended } from '../phase2/friends-extended';
import { GroupsManager } from '../phase2/groups';
import { ObjectManagerExtended } from '../phase2/objects-extended';
import { NotificationsManager } from '../notifications';
import { WorldViewer } from '../world';
import { LAYOUTS } from '../../theme/layouts.js';
import { PALETTES } from '../../theme/palettes.js';
import { NAV_ALL } from '../../data/content.js';

class ProtocolStub extends Utils.EventEmitter {
  sendChat = vi.fn();
}

describe('runtime UI manager snapshots', () => {
  it('keeps every design layout and palette while adding Lumiya surfaces', () => {
    expect(Object.keys(LAYOUTS)).toEqual(expect.arrayContaining(['terminal', 'sweep', 'tiles', 'glass', 'rules', 'press']));
    expect(Object.keys(PALETTES).length).toBeGreaterThanOrEqual(20);
    expect(NAV_ALL.map((item: any) => item.id)).toEqual(expect.arrayContaining(['Notecards', 'Media', 'Accounts', 'Grids']));
  });
  it('exposes friend, group and object records without exposing backing maps', () => {
    const friends = new FriendsExtended();
    friends.addFriend('friend-id', { name: 'Live Resident', onlineStatus: 'online' });
    const friendSnapshot = friends.getFriends();
    friendSnapshot[0].name = 'changed';
    expect(friends.getFriends()[0].name).toBe('Live Resident');

    const groups = new GroupsManager();
    groups.setGroupInfo('group-id', { name: 'Live Group' });
    expect(groups.getGroups()).toMatchObject([{ id: 'group-id', name: 'Live Group' }]);

    const objects = new ObjectManagerExtended();
    objects.setPrimParams('object-id', { shape: 'sphere' });
    expect(objects.getObjects()[0]).toMatchObject({ id: 'object-id', primParams: { shape: 'sphere' } });
  });

  it('emits friend list mutations and dispatches real friend requests', async () => {
    const sendFriendRequest = vi.fn().mockResolvedValue(undefined);
    const friends = new FriendsExtended({ sendFriendRequest });
    const added = vi.fn();
    const removed = vi.fn();
    friends.on('friend_added', added);
    friends.on('friend_removed', removed);

    friends.replaceFriends([{ id: 'one', name: 'One Resident' }, { id: 'two', name: 'Two Resident' }]);
    friends.replaceFriends([{ id: 'two', name: 'Two Renamed' }]);
    await friends.sendFriendRequest('three', 'Hello');

    expect(added).toHaveBeenCalledTimes(2);
    expect(removed).toHaveBeenCalledWith(expect.objectContaining({ id: 'one' }));
    expect(friends.getFriends()).toEqual([expect.objectContaining({ id: 'two', name: 'Two Renamed' })]);
    expect(sendFriendRequest).toHaveBeenCalledWith('three', 'Hello');
  });

  it('normalizes live presence values and retains status events received before the buddy snapshot', () => {
    const friends = new FriendsExtended();
    const updated = vi.fn();
    friends.on('friend_updated', updated);

    friends.updateFriendStatus('{ABC-123}', 'online', { name: 'Early Resident' });
    expect(friends.getFriends()).toEqual([
      expect.objectContaining({ id: 'abc-123', name: 'Early Resident', onlineStatus: 'online' }),
    ]);

    friends.replaceFriends([{ id: 'ABC-123', name: 'Early Resident', onlineStatus: 'false' }]);
    expect(friends.getFriends()).toEqual([
      expect.objectContaining({ id: 'abc-123', onlineStatus: 'online' }),
    ]);
    expect(updated).toHaveBeenCalled();
  });

  it('retains and clears live notifications', () => {
    const notifications = new NotificationsManager(new ProtocolStub() as any);
    notifications.handleNotification({ title: 'Grid notice', message: 'Live payload' });
    expect(notifications.items).toHaveLength(1);
    notifications.clear();
    expect(notifications.items).toEqual([]);
    expect(notifications.unreadCount).toBe(0);
  });

  it('turns region and object protocol messages into world events', () => {
    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);
    const regionListener = vi.fn();
    const objectListener = vi.fn();
    world.on('region_changed', regionListener);
    world.on('objects_changed', objectListener);

    protocol.emit('RegionHandshake', { regionID: 'region-id', regionName: 'Live Region', regionX: 10, regionY: 20 });
    protocol.emit('ObjectUpdate', { id: 'object-id', name: 'Live Object' });

    expect(world.region).toMatchObject({ id: 'region-id', name: 'Live Region', x: 10, y: 20 });
    expect(world.objects).toMatchObject([{ id: 'object-id', name: 'Live Object' }]);
    expect(regionListener).toHaveBeenCalledOnce();
    expect(objectListener).toHaveBeenCalledOnce();
  });

  it('uses login grid coordinates without inventing a region and clears world data on disconnect', () => {
    const protocol = new ProtocolStub() as any;
    protocol.connected = true;
    protocol.agentId = 'self';
    const world = new WorldViewer(protocol);

    expect(world.region).toBeNull();
    protocol.emit('connected', { sim_name: 'Grid Region', region_x: 256000, region_y: 256256 });
    expect(world.region).toMatchObject({ name: 'Grid Region', x: 1000, y: 1001 });

    protocol.emit('CoarseAvatarUpdate', { id: 'nearby', name: 'Live Resident', position: [12, 14, 20] });
    expect(world.nearbyUsers).toMatchObject([{ id: 'nearby', name: 'Live Resident', position: [12, 14, 20] }]);

    protocol.connected = false;
    protocol.emit('disconnected');
    expect(world.region).toBeNull();
    expect(world.nearbyUsers).toEqual([]);
    expect(world.objects).toEqual([]);
  });

  it('tracks coarse avatar locations and parcel properties for radar and map', () => {
    const protocol = new ProtocolStub();
    const world = new WorldViewer(protocol);
    const nearbyListener = vi.fn();
    const parcelListener = vi.fn();
    world.on('nearby_changed', nearbyListener);
    world.on('parcel_changed', parcelListener);

    protocol.emit('CoarseLocationUpdate', {
      Index_Field: { You: 0 },
      Location_Fields: [{ X: 10, Y: 10, Z: 5 }, { X: 13, Y: 14, Z: 5 }],
      AgentData_Fields: [{ AgentID: 'self' }, { AgentID: 'nearby-agent' }],
    });
    protocol.emit('ParcelProperties', { ParcelData: [{ LocalID: 4, Name: 'Live parcel' }] });

    expect(world.avatarPosition).toEqual([10, 10, 20]);
    expect(world.nearbyUsers[0]).toMatchObject({ id: 'nearby-agent', position: [13, 14, 20], distance: 5 });
    expect(world.region.parcel).toMatchObject({ LocalID: 4, Name: 'Live parcel' });
    expect(nearbyListener).toHaveBeenCalledOnce();
    expect(parcelListener).toHaveBeenCalledOnce();
  });
});
