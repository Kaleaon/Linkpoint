import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import React, { act } from 'react';
import { mountScreen, unmount, click } from './ui-helpers';
import useSinglePointerRelocate from '../../hooks/useSinglePointerRelocate';
import FloatersDesktop from '../../components/FloatersDesktop';
import ViewportTile from '../../components/ViewportTile';
import OutfitCarouselDrawer from '../../components/OutfitCarouselDrawer';

describe('Single Pointer Relocation & Keyboard Shortuts (WCAG 2.5.7 & 2.1.1)', () => {
  let mounted: any = null;

  afterEach(async () => {
    await unmount(mounted);
    mounted = null;
  });

  describe('useSinglePointerRelocate custom hook', () => {
    it('moveStep updates rect coordinates step by step with bounds clamping', async () => {
      let latestRect: any = null;

      const TestComponent = ({ initialRect }: { initialRect: any }) => {
        const [rect, setRect] = React.useState(initialRect);
        latestRect = rect;

        const { moveStep, resizeStep } = useSinglePointerRelocate({
          rect,
          onUpdate: setRect,
          container: { minX: 0, minY: 0, maxX: 1000, maxY: 1000 },
          stepPixels: 32,
        });

        return (
          <div data-testid="relocate-box">
            <button data-testid="up-btn" onClick={() => moveStep('up')}>Up</button>
            <button data-testid="down-btn" onClick={() => moveStep('down')}>Down</button>
            <button data-testid="left-btn" onClick={() => moveStep('left')}>Left</button>
            <button data-testid="right-btn" onClick={() => moveStep('right')}>Right</button>
            <button data-testid="grow-btn" onClick={() => resizeStep(32, 32)}>Grow</button>
          </div>
        );
      };

      mounted = await mountScreen(() => <TestComponent initialRect={{ x: 50, y: 50, w: 320, h: 240 }} />);

      const upBtn = mounted.host.querySelector('[data-testid="up-btn"]');
      const downBtn = mounted.host.querySelector('[data-testid="down-btn"]');
      const leftBtn = mounted.host.querySelector('[data-testid="left-btn"]');
      const rightBtn = mounted.host.querySelector('[data-testid="right-btn"]');
      const growBtn = mounted.host.querySelector('[data-testid="grow-btn"]');

      await click(upBtn);
      expect(latestRect).toEqual({ x: 50, y: 18, w: 320, h: 240 });

      await click(downBtn);
      expect(latestRect).toEqual({ x: 50, y: 50, w: 320, h: 240 });

      await click(leftBtn);
      expect(latestRect).toEqual({ x: 18, y: 50, w: 320, h: 240 });

      await click(rightBtn);
      expect(latestRect).toEqual({ x: 50, y: 50, w: 320, h: 240 });

      await click(growBtn);
      expect(latestRect).toEqual({ x: 50, y: 50, w: 352, h: 272 });
    });

    it('handleKeyDown maps arrow keys to movement and Shift+arrow keys to resizing', async () => {
      let latestRect: any = null;

      const TestComponent = () => {
        const [rect, setRect] = React.useState({ x: 100, y: 100, w: 300, h: 200 });
        latestRect = rect;

        const { handleKeyDown } = useSinglePointerRelocate({
          rect,
          onUpdate: setRect,
          stepPixels: 32,
        });

        return (
          <div
            data-testid="key-box"
            tabIndex={0}
            onKeyDown={handleKeyDown}
          >
            Target Box
          </div>
        );
      };

      mounted = await mountScreen(TestComponent);
      const keyBox = mounted.host.querySelector('[data-testid="key-box"]');

      await act(async () => {
        keyBox.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowUp', bubbles: true }));
      });
      expect(latestRect).toEqual({ x: 100, y: 68, w: 300, h: 200 });

      await act(async () => {
        keyBox.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowDown', bubbles: true }));
      });
      expect(latestRect).toEqual({ x: 100, y: 100, w: 300, h: 200 });

      await act(async () => {
        keyBox.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowLeft', bubbles: true }));
      });
      expect(latestRect).toEqual({ x: 68, y: 100, w: 300, h: 200 });

      await act(async () => {
        keyBox.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowRight', bubbles: true }));
      });
      expect(latestRect).toEqual({ x: 100, y: 100, w: 300, h: 200 });

      await act(async () => {
        keyBox.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowUp', shiftKey: true, bubbles: true }));
      });
      expect(latestRect).toEqual({ x: 100, y: 100, w: 300, h: 168 });

      await act(async () => {
        keyBox.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowDown', shiftKey: true, bubbles: true }));
      });
      expect(latestRect).toEqual({ x: 100, y: 100, w: 300, h: 200 });
    });
  });

  describe('FloatersDesktop single pointer and keyboard controls', () => {
    it('provides single-pointer step move buttons and scale controls in FloatersDesktop', async () => {
      mounted = await mountScreen(FloatersDesktop);

      // Verify floater title bar has move buttons
      const moveUpBtn = mounted.host.querySelector('button[aria-label="Move panel up"]');
      const moveDownBtn = mounted.host.querySelector('button[aria-label="Move panel down"]');
      const moveLeftBtn = mounted.host.querySelector('button[aria-label="Move panel left"]');
      const moveRightBtn = mounted.host.querySelector('button[aria-label="Move panel right"]');

      expect(moveUpBtn).not.toBeNull();
      expect(moveDownBtn).not.toBeNull();
      expect(moveLeftBtn).not.toBeNull();
      expect(moveRightBtn).not.toBeNull();

      // Verify scale buttons
      const increaseSizeBtn = mounted.host.querySelector('button[aria-label="Increase panel size"]');
      const decreaseSizeBtn = mounted.host.querySelector('button[aria-label="Decrease panel size"]');

      expect(increaseSizeBtn).not.toBeNull();
      expect(decreaseSizeBtn).not.toBeNull();

      // Test single-pointer click step move
      const initialChatRect = mounted.ctx.current.actions.flR('Chat');
      await click(moveRightBtn);
      const nextChatRect = mounted.ctx.current.actions.flR('Chat');
      expect(nextChatRect.x).toBe(initialChatRect.x + 32);

      // Test single-pointer click scale button
      await click(increaseSizeBtn);
      const scaledChatRect = mounted.ctx.current.actions.flR('Chat');
      expect(scaledChatRect.w).toBe(nextChatRect.w + 32);
      expect(scaledChatRect.h).toBe(nextChatRect.h + 32);
    });

    it('supports arrow keys and Shift+arrow keys on focused floater header', async () => {
      mounted = await mountScreen(FloatersDesktop);

      const headerEl = mounted.host.querySelector('div[role="region"][aria-label*="header"]');
      expect(headerEl).not.toBeNull();

      const initialRect = mounted.ctx.current.actions.flR('Chat');

      await act(async () => {
        headerEl.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowDown', bubbles: true }));
      });
      const movedRect = mounted.ctx.current.actions.flR('Chat');
      expect(movedRect.y).toBe(initialRect.y + 32);

      await act(async () => {
        headerEl.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowRight', shiftKey: true, bubbles: true }));
      });
      const resizedRect = mounted.ctx.current.actions.flR('Chat');
      expect(resizedRect.w).toBe(movedRect.w + 32);
    });
  });

  describe('ViewportTile single-pointer move & scale controls', () => {
    it('renders move and scale controls when isFloating is true', async () => {
      let moveStepCalled = false;
      let moveDx = 0, moveDy = 0;
      let resizeStepCalled = false;
      let resizeDw = 0, resizeDh = 0;

      const TestTile = () => (
        <ViewportTile
          id="Map"
          title="World Map"
          isDocked={false}
          isFloating={true}
          active={true}
          onMoveStep={(dx, dy) => { moveStepCalled = true; moveDx = dx; moveDy = dy; }}
          onResizeStep={(dw, dh) => { resizeStepCalled = true; resizeDw = dw; resizeDh = dh; }}
        >
          <div>Map Tile</div>
        </ViewportTile>
      );

      mounted = await mountScreen(TestTile);

      const moveUpBtn = mounted.host.querySelector('button[aria-label="Move Up"]');
      const moveDownBtn = mounted.host.querySelector('button[aria-label="Move Down"]');
      const increaseTileBtn = mounted.host.querySelector('button[aria-label="Increase tile size"]');
      const decreaseTileBtn = mounted.host.querySelector('button[aria-label="Decrease tile size"]');

      expect(moveUpBtn).not.toBeNull();
      expect(moveDownBtn).not.toBeNull();
      expect(increaseTileBtn).not.toBeNull();
      expect(decreaseTileBtn).not.toBeNull();

      await click(moveUpBtn);
      expect(moveStepCalled).toBe(true);
      expect(moveDx).toBe(0);
      expect(moveDy).toBe(-32);

      await click(increaseTileBtn);
      expect(resizeStepCalled).toBe(true);
      expect(resizeDw).toBe(32);
      expect(resizeDh).toBe(32);
    });
  });

  describe('OutfitCarouselDrawer handle accessibility', () => {
    it('toggles open/closed state on click, Enter, and Space keypress', async () => {
      let closed = false;
      const TestDrawer = () => (
        <OutfitCarouselDrawer
          open={true}
          onClose={() => { closed = true; }}
        />
      );

      mounted = await mountScreen(TestDrawer);

      const handleBar = mounted.host.querySelector('div[role="button"][aria-label="Toggle outfit drawer"]');
      expect(handleBar).not.toBeNull();
      expect(handleBar.getAttribute('tabindex')).toBe('0');

      // Single click toggle
      await click(handleBar);
      expect(closed).toBe(true);

      // Keyboard Enter toggle
      closed = false;
      await act(async () => {
        handleBar.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', bubbles: true }));
      });
      expect(closed).toBe(true);

      // Keyboard Space toggle
      closed = false;
      await act(async () => {
        handleBar.dispatchEvent(new KeyboardEvent('keydown', { key: 'Space', bubbles: true }));
      });
      expect(closed).toBe(true);
    });
  });
});
