import { describe, expect, it, vi } from 'vitest';
import { CameraControls } from '../camera-controls';
import { Camera3D } from '../camera-3d';

function createMockCanvas(): HTMLCanvasElement {
  const listeners: Record<string, Function[]> = {};
  return {
    style: {},
    tabIndex: 0,
    focus: vi.fn(),
    getBoundingClientRect: () => ({ left: 10, top: 20, width: 800, height: 600 }),
    addEventListener: (event: string, handler: Function) => {
      listeners[event] = listeners[event] || [];
      listeners[event].push(handler);
    },
    removeEventListener: (event: string, handler: Function) => {
      if (listeners[event]) {
        listeners[event] = listeners[event].filter((h) => h !== handler);
      }
    },
    dispatchEvent: (event: any) => {
      const handlers = listeners[event.type] || [];
      handlers.forEach((h) => h(event));
      return true;
    },
    setPointerCapture: vi.fn(),
    releasePointerCapture: vi.fn(),
  } as unknown as HTMLCanvasElement;
}

describe('CameraControls touch gestures and mode overlay integration', () => {
  it('updates camera orbit on slow single-finger dragging without velocity suppression', () => {
    const canvas = createMockCanvas();
    const camera = new Camera3D();
    const rotateSpy = vi.spyOn(camera, 'rotate');
    const controls = new CameraControls(canvas, camera);

    const downEvent = { type: 'pointerdown', pointerId: 1, clientX: 100, clientY: 100, timeStampOverride: 1000 } as any;
    canvas.dispatchEvent(downEvent);

    // Very slow move (e.g. 10px over 10,000ms = 0.001 px/ms velocity)
    const moveEvent = { type: 'pointermove', pointerId: 1, clientX: 110, clientY: 105, timeStampOverride: 11000 } as any;
    canvas.dispatchEvent(moveEvent);

    // Should NOT be suppressed by velocity threshold; camera rotate should execute smoothly
    expect(rotateSpy).toHaveBeenCalled();
    controls.destroy();
  });

  it('triggers object picking on tap when total displacement is under 6 pixels', () => {
    const canvas = createMockCanvas();
    const camera = new Camera3D();
    const pickedSpy = vi.fn();
    const rotateSpy = vi.spyOn(camera, 'rotate');
    const controls = new CameraControls(canvas, camera, () => {}, pickedSpy);

    const downEvent = { type: 'pointerdown', pointerId: 1, clientX: 100, clientY: 100 } as any;
    canvas.dispatchEvent(downEvent);

    // Small micro-displacement (2px < displacementThreshold)
    const moveEvent = { type: 'pointermove', pointerId: 1, clientX: 101, clientY: 101 } as any;
    canvas.dispatchEvent(moveEvent);

    // Camera rotation should not fire during tap displacement under threshold
    expect(rotateSpy).not.toHaveBeenCalled();

    const upEvent = { type: 'pointerup', pointerId: 1, clientX: 101, clientY: 101 } as any;
    canvas.dispatchEvent(upEvent);

    // Picked callback should fire with canvas relative coordinates
    expect(pickedSpy).toHaveBeenCalledWith(91, 81); // (101 - left(10), 101 - top(20))
    controls.destroy();
  });

  it('allows single-finger camera panning when Pan Mode is enabled', () => {
    const canvas = createMockCanvas();
    const camera = new Camera3D();
    const panSpy = vi.spyOn(camera, 'pan');
    const controls = new CameraControls(canvas, camera);

    controls.setPanMode(true);
    expect(controls.getPanMode()).toBe(true);

    const downEvent = { type: 'pointerdown', pointerId: 1, clientX: 100, clientY: 100 } as any;
    canvas.dispatchEvent(downEvent);

    const moveEvent = { type: 'pointermove', pointerId: 1, clientX: 120, clientY: 110 } as any;
    canvas.dispatchEvent(moveEvent);

    expect(panSpy).toHaveBeenCalled();
    controls.destroy();
  });

  it('executes pinch-to-zoom and two-finger panning for multi-touch pointers', () => {
    const canvas = createMockCanvas();
    const camera = new Camera3D();
    const zoomSpy = vi.spyOn(camera, 'zoom');
    const panSpy = vi.spyOn(camera, 'pan');
    const controls = new CameraControls(canvas, camera);

    // Pointer 1 down
    canvas.dispatchEvent({ type: 'pointerdown', pointerId: 1, clientX: 100, clientY: 100 } as any);
    // Pointer 2 down
    canvas.dispatchEvent({ type: 'pointerdown', pointerId: 2, clientX: 200, clientY: 100 } as any);

    // Move pointer 2 outwards (spreading fingers from 100px apart to 150px apart) and shifting midpoint
    canvas.dispatchEvent({ type: 'pointermove', pointerId: 2, clientX: 250, clientY: 120 } as any);

    expect(zoomSpy).toHaveBeenCalled();
    expect(panSpy).toHaveBeenCalled();

    controls.destroy();
  });

  it('prevents default touch actions to block window scrolling on touch viewports', () => {
    const canvas = createMockCanvas();
    const camera = new Camera3D();
    const controls = new CameraControls(canvas, camera);

    const touchStartEvent = { type: 'touchstart', cancelable: true, preventDefault: vi.fn(), touches: [{}] } as any;
    canvas.dispatchEvent(touchStartEvent);
    expect(touchStartEvent.preventDefault).toHaveBeenCalled();

    const touchMoveEvent = { type: 'touchmove', cancelable: true, preventDefault: vi.fn(), touches: [{}] } as any;
    canvas.dispatchEvent(touchMoveEvent);
    expect(touchMoveEvent.preventDefault).toHaveBeenCalled();

    controls.destroy();
  });
});
