import { describe, it, expect, beforeEach, vi } from 'vitest';
import { EventQueueManager } from '../phase2/event-queue';
import { WorldViewer } from '../world';
import { Utils } from '../utils';

describe('EventQueueManager', () => {
  let mockProtocol: any;
  let eventQueue: EventQueueManager;

  beforeEach(() => {
    mockProtocol = new Utils.EventEmitter();
    mockProtocol.agentId = 'my-agent-uuid';
    mockProtocol.authReply = { sim_name: 'TestRegion', first_name: 'Tester' };
    eventQueue = new EventQueueManager(mockProtocol);
  });

  it('initializes with default stats and handles registration', () => {
    const stats = eventQueue.getStats();
    expect(stats.isPolling).toBe(false);
    expect(stats.bufferedEvents).toBe(0);
    expect(stats.handlerCount).toBe(0);

    const handler = vi.fn();
    eventQueue.registerHandler('AvatarPresence', handler);
    expect(eventQueue.getStats().handlerCount).toBe(1);

    eventQueue.unregisterHandler('AvatarPresence', handler);
    expect(eventQueue.getStats().handlerCount).toBe(0);
  });

  it('processes and dispatches custom registered handlers', () => {
    const presenceHandler = vi.fn();
    const chatHandler = vi.fn();

    eventQueue.registerHandler('AvatarPresence', presenceHandler);
    eventQueue.registerHandler('ChatSessionRequest', chatHandler);

    eventQueue.enqueueEvent({
      message: 'AvatarPresence',
      body: {
        AgentData: [
          { AgentID: 'agent-123', Coordinates: [128, 128, 25], presence: 'online' }
        ]
      }
    });

    expect(presenceHandler).toHaveBeenCalledTimes(1);
    expect(chatHandler).not.toHaveBeenCalled();
  });

  it('consolidates and batches multiple avatar presence events', () => {
    const presenceSpy = vi.fn();
    mockProtocol.on('avatar_presence', presenceSpy);

    // Enqueue a batch with multiple avatar position updates
    eventQueue.enqueueEvents([
      {
        message: 'AvatarPresence',
        body: { AgentID: 'avatar-1', Coordinates: [100, 100, 20], presence: 'online' }
      },
      {
        message: 'AvatarPresence',
        body: { AgentID: 'avatar-2', Coordinates: [150, 150, 25], presence: 'online' }
      },
      {
        // Second update for avatar-1 in the same batch
        message: 'AvatarPresence',
        body: { AgentID: 'avatar-1', Coordinates: [102, 101, 20], presence: 'online' }
      }
    ]);

    // Should emit a single consolidated avatar_presence pass rather than 3 passes
    expect(presenceSpy).toHaveBeenCalledTimes(1);
    const payload = presenceSpy.mock.calls[0][0];
    expect(payload.AgentData).toBeDefined();
    // Avatar-1 should be coalesced to the latest coordinates [102, 101, 20]
    const av1 = payload.AgentData.find((a: any) => a.AgentID === 'avatar-1');
    expect(av1).toBeDefined();
    expect(av1.Coordinates).toEqual([102, 101, 20]);

    const av2 = payload.AgentData.find((a: any) => a.AgentID === 'avatar-2');
    expect(av2).toBeDefined();
    expect(av2.Coordinates).toEqual([150, 150, 25]);
  });

  it('dispatches CoarseLocationUpdate and AgentMovementComplete correctly', () => {
    const coarseSpy = vi.fn();
    const movementSpy = vi.fn();
    const objectSpy = vi.fn();

    mockProtocol.on('CoarseLocationUpdate', coarseSpy);
    mockProtocol.on('AgentMovementComplete', movementSpy);
    mockProtocol.on('ObjectUpdate', objectSpy);

    eventQueue.enqueueEvents([
      { message: 'CoarseLocationUpdate', body: { Location_Fields: [{ X: 128, Y: 128, Z: 6 }] } },
      { message: 'AgentMovementComplete', body: { Data: { Position: [128, 128, 25] } } },
      { message: 'ObjectUpdate', body: { id: 'obj-1', position: [10, 10, 5] } }
    ]);

    expect(coarseSpy).toHaveBeenCalledTimes(1);
    expect(movementSpy).toHaveBeenCalledTimes(1);
    expect(objectSpy).toHaveBeenCalledTimes(1);
  });

  it('correctly updates WorldViewer scene graph when avatar presence is dispatched', () => {
    const world = new WorldViewer(mockProtocol);
    const objectsChangedSpy = vi.fn();
    const nearbyChangedSpy = vi.fn();

    world.on('objects_changed', objectsChangedSpy);
    world.on('nearby_changed', nearbyChangedSpy);

    eventQueue.enqueueEvents([
      {
        message: 'AvatarPresence',
        body: {
          AgentData: [
            { AgentID: 'resident-uuid-1', name: 'Alice Resident', position: [130, 135, 26], presence: 'online' }
          ]
        }
      }
    ]);

    expect(nearbyChangedSpy).toHaveBeenCalled();
    expect(world.nearbyUsers.length).toBe(1);
    expect(world.nearbyUsers[0].id).toBe('resident-uuid-1');
    expect(world.nearbyUsers[0].name).toBe('Alice Resident');

    // Resident was added to scene graph objects
    const avatarObj = world.objects.find((o: any) => o.id === 'resident-uuid-1');
    expect(avatarObj).toBeDefined();
    expect(avatarObj.avatar).toBe(true);
    expect(avatarObj.position).toEqual([130, 135, 26]);

    // Test avatar departure
    eventQueue.enqueueEvents([
      {
        message: 'AvatarPresence',
        body: {
          AgentData: [
            { AgentID: 'resident-uuid-1', presence: 'left' }
          ]
        }
      }
    ]);

    expect(world.nearbyUsers.length).toBe(0);
    const departedObj = world.objects.find((o: any) => o.id === 'resident-uuid-1');
    expect(departedObj).toBeUndefined();
  });
});
