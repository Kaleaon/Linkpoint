import { describe, expect, it, vi } from 'vitest';
import { WorldViewer } from '../world';
import { slBridge } from '../sl-bridge';

describe('WorldViewer simulator sun phase synchronization and fallback', () => {
  function makeViewer() {
    const listeners = new Map<string, Function>();
    const protocol: any = {
      connected: true,
      on: (event: string, fn: Function) => listeners.set(event, fn),
      emit: (event: string, data: any) => listeners.get(event)?.(data),
      agentId: 'agent-id',
    };
    const viewer = new WorldViewer(protocol);
    const mockScene3d: any = {
      setEnvironment: vi.fn(),
    };
    viewer.scene3d = mockScene3d;
    return { viewer, protocol, mockScene3d, listeners };
  }

  it('initializes simSunHour as null and falls back to estimated wall-clock hour', () => {
    const { viewer, mockScene3d } = makeViewer();
    expect(viewer.simSunHour).toBeNull();

    viewer['applyEnvironment'](1000);
    expect(mockScene3d.setEnvironment).toHaveBeenCalledTimes(1);
    expect(mockScene3d.setEnvironment.mock.calls[0][0].source).toBe('default-windlight');
  });

  it('updates simSunHour upon scene:sun-hour-update event and applies new environment', () => {
    const { viewer, protocol, mockScene3d } = makeViewer();

    protocol.emit('scene:sun-hour-update', { sunHour: 0.5, sunPhase: Math.PI / 2 });
    expect(viewer.simSunHour).toBeCloseTo(0.5, 6);
    expect(mockScene3d.setEnvironment).toHaveBeenCalled();
  });

  it('updates simSunHour upon direct sun-hour-update event', () => {
    const { viewer, protocol, mockScene3d } = makeViewer();

    protocol.emit('sun-hour-update', { sunHour: 0.75, sunPhase: Math.PI });
    expect(viewer.simSunHour).toBeCloseTo(0.75, 6);
    expect(mockScene3d.setEnvironment).toHaveBeenCalled();
  });

  it('updates simSunHour from slBridge when protocol is disconnected', () => {
    const { viewer, mockScene3d } = makeViewer();
    viewer.protocol.connected = false;

    slBridge.emit('sun-hour-update', { sunHour: 0.25, sunPhase: 0 });
    expect(viewer.simSunHour).toBeCloseTo(0.25, 6);
    expect(mockScene3d.setEnvironment).toHaveBeenCalled();
  });

  it('resets simSunHour to null upon disconnection and reverts to wall-clock fallback', () => {
    const { viewer, protocol, mockScene3d } = makeViewer();

    protocol.emit('sun-hour-update', { sunHour: 0.5 });
    expect(viewer.simSunHour).toBe(0.5);

    protocol.emit('disconnected', {});
    expect(viewer.simSunHour).toBeNull();

    viewer['applyEnvironment'](2000);
    expect(mockScene3d.setEnvironment).toHaveBeenCalled();
  });
});
