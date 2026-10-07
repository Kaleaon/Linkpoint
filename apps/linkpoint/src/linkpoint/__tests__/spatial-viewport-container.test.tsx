// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { act, createElement } from 'react';
import SpatialViewportContainer from '../../components/SpatialViewportContainer.jsx';
import ViewportTile from '../../components/ViewportTile.jsx';
import OutfitViewer from '../../screens/OutfitViewer.jsx';
import World3D from '../../screens/World3D.jsx';
import { buttonByText, click, mountScreen, unmount, type Mounted } from './ui-helpers';

let mounted: Mounted | null = null;

afterEach(async () => {
  await unmount(mounted);
  mounted = null;
  vi.restoreAllMocks();
});

describe('SpatialViewportContainer accessibility component', () => {
  it('renders ARIA live region, focusable canvas wrapper, and camera control toolbar', async () => {
    const onRotate = vi.fn();
    const onZoom = vi.fn();
    const onReset = vi.fn();

    const TestComp = () => (
      <SpatialViewportContainer
        ariaLabel="3D Test Viewport"
        yaw={0}
        pitch={0}
        zoom={1}
        onRotate={onRotate}
        onZoom={onZoom}
        onReset={onReset}
      >
        <canvas id="test-canvas" />
      </SpatialViewportContainer>
    );

    mounted = await mountScreen(TestComp);

    // Live region status
    const liveRegion = mounted.host.querySelector('[role="status"]');
    expect(liveRegion).not.toBeNull();
    expect(liveRegion?.getAttribute('aria-live')).toBe('polite');

    // Focusable spatial canvas region
    const canvasRegion = mounted.host.querySelector('[role="region"]');
    expect(canvasRegion).not.toBeNull();
    expect(canvasRegion?.getAttribute('aria-label')).toBe('3D Test Viewport');
    expect(canvasRegion?.getAttribute('tabIndex')).toBe('0');

    // Camera toolbar
    const toolbar = mounted.host.querySelector('[role="toolbar"]');
    expect(toolbar).not.toBeNull();
    expect(toolbar?.getAttribute('aria-label')).toBe('3D Viewport Camera Controls');

    // Check toolbar buttons
    expect(mounted.host.querySelector('button[aria-label="Rotate Left"]')).not.toBeNull();
    expect(mounted.host.querySelector('button[aria-label="Rotate Right"]')).not.toBeNull();
    expect(mounted.host.querySelector('button[aria-label="Zoom In"]')).not.toBeNull();
    expect(mounted.host.querySelector('button[aria-label="Zoom Out"]')).not.toBeNull();
    expect(mounted.host.querySelector('button[aria-label="Reset Camera"]')).not.toBeNull();
  });

  it('handles keyboard navigation and updates live region announcements', async () => {
    const onRotate = vi.fn();
    const onZoom = vi.fn();
    const onReset = vi.fn();

    const TestComp = () => (
      <SpatialViewportContainer
        ariaLabel="3D Keyboard Test"
        yaw={0}
        pitch={0}
        zoom={1}
        onRotate={onRotate}
        onZoom={onZoom}
        onReset={onReset}
      >
        <canvas id="test-canvas-keys" />
      </SpatialViewportContainer>
    );

    mounted = await mountScreen(TestComp);
    const canvasRegion = mounted.host.querySelector('[role="region"]') as HTMLElement;
    const liveRegion = mounted.host.querySelector('[role="status"]');

    // ArrowLeft / 'a'
    await act(async () => {
      canvasRegion.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowLeft', bubbles: true }));
    });
    expect(onRotate).toHaveBeenCalledWith({ yawDelta: -15, pitchDelta: 0 });
    expect(liveRegion?.textContent).toContain('Rotated camera left');

    // ArrowRight / 'd'
    await act(async () => {
      canvasRegion.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowRight', bubbles: true }));
    });
    expect(onRotate).toHaveBeenCalledWith({ yawDelta: 15, pitchDelta: 0 });
    expect(liveRegion?.textContent).toContain('Rotated camera right');

    // ArrowUp / 'w'
    await act(async () => {
      canvasRegion.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowUp', bubbles: true }));
    });
    expect(onRotate).toHaveBeenCalledWith({ yawDelta: 0, pitchDelta: 10 });
    expect(liveRegion?.textContent).toContain('Tilted camera up');

    // ArrowDown / 's'
    await act(async () => {
      canvasRegion.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowDown', bubbles: true }));
    });
    expect(onRotate).toHaveBeenCalledWith({ yawDelta: 0, pitchDelta: -10 });
    expect(liveRegion?.textContent).toContain('Tilted camera down');

    // '+' zoom in
    await act(async () => {
      canvasRegion.dispatchEvent(new KeyboardEvent('keydown', { key: '+', bubbles: true }));
    });
    expect(onZoom).toHaveBeenCalledWith(0.2);
    expect(liveRegion?.textContent).toContain('Zoomed in');

    // '-' zoom out
    await act(async () => {
      canvasRegion.dispatchEvent(new KeyboardEvent('keydown', { key: '-', bubbles: true }));
    });
    expect(onZoom).toHaveBeenCalledWith(-0.2);
    expect(liveRegion?.textContent).toContain('Zoomed out');

    // 'r' reset
    await act(async () => {
      canvasRegion.dispatchEvent(new KeyboardEvent('keydown', { key: 'r', bubbles: true }));
    });
    expect(onReset).toHaveBeenCalled();
    expect(liveRegion?.textContent).toContain('Reset camera');
  });

  it('triggers camera rotation, zoom, and reset via toolbar buttons', async () => {
    const onRotate = vi.fn();
    const onZoom = vi.fn();
    const onReset = vi.fn();

    const TestComp = () => (
      <SpatialViewportContainer
        ariaLabel="3D Toolbar Test"
        yaw={0}
        pitch={0}
        zoom={1}
        onRotate={onRotate}
        onZoom={onZoom}
        onReset={onReset}
      >
        <canvas id="test-canvas-tb" />
      </SpatialViewportContainer>
    );

    mounted = await mountScreen(TestComp);
    const liveRegion = mounted.host.querySelector('[role="status"]');

    const rotateLeftBtn = mounted.host.querySelector('button[aria-label="Rotate Left"]') as HTMLElement;
    await click(rotateLeftBtn);
    expect(onRotate).toHaveBeenCalledWith({ yawDelta: -15, pitchDelta: 0 });
    expect(liveRegion?.textContent).toContain('Rotated camera left');

    const rotateRightBtn = mounted.host.querySelector('button[aria-label="Rotate Right"]') as HTMLElement;
    await click(rotateRightBtn);
    expect(onRotate).toHaveBeenCalledWith({ yawDelta: 15, pitchDelta: 0 });
    expect(liveRegion?.textContent).toContain('Rotated camera right');

    const zoomInBtn = mounted.host.querySelector('button[aria-label="Zoom In"]') as HTMLElement;
    await click(zoomInBtn);
    expect(onZoom).toHaveBeenCalledWith(0.2);
    expect(liveRegion?.textContent).toContain('Zoomed in');

    const zoomOutBtn = mounted.host.querySelector('button[aria-label="Zoom Out"]') as HTMLElement;
    await click(zoomOutBtn);
    expect(onZoom).toHaveBeenCalledWith(-0.2);
    expect(liveRegion?.textContent).toContain('Zoomed out');

    const resetBtn = mounted.host.querySelector('button[aria-label="Reset Camera"]') as HTMLElement;
    await click(resetBtn);
    expect(onReset).toHaveBeenCalled();
    expect(liveRegion?.textContent).toContain('Reset camera');
  });

  it('integrates SpatialViewportContainer into OutfitViewer screen', async () => {
    mounted = await mountScreen(OutfitViewer);

    const liveRegion = mounted.host.querySelector('[role="status"]');
    expect(liveRegion).not.toBeNull();

    const toolbar = mounted.host.querySelector('[role="toolbar"]');
    expect(toolbar).not.toBeNull();
    expect(toolbar?.getAttribute('aria-label')).toBe('3D Viewport Camera Controls');
  });

  it('integrates SpatialViewportContainer into World3D screen', async () => {
    mounted = await mountScreen(() => <World3D desktopBackdrop={false} />);

    const liveRegion = mounted.host.querySelector('[role="status"]');
    expect(liveRegion).not.toBeNull();

    const canvasRegion = mounted.host.querySelector('[role="region"]');
    expect(canvasRegion).not.toBeNull();
    expect(canvasRegion?.getAttribute('tabIndex')).toBe('0');
  });

  it('integrates SpatialViewportContainer into ViewportTile component when spatial props are passed', async () => {
    const onRotate = vi.fn();

    const TileComp = () => (
      <ViewportTile
        id="tile-3d"
        title="3D World Viewport Tile"
        isSpatialViewport={true}
        onRotate={onRotate}
      >
        <div id="tile-child">Child viewport</div>
      </ViewportTile>
    );

    mounted = await mountScreen(TileComp);

    const tileBody = mounted.host.querySelector('.viewport-tile-body');
    expect(tileBody).not.toBeNull();
    expect(tileBody?.getAttribute('tabIndex')).toBe('0');

    const toolbar = mounted.host.querySelector('[role="toolbar"]');
    expect(toolbar).not.toBeNull();
  });
});
