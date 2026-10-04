import { useCallback } from "react";

/**
 * Reusable custom hook for single-pointer relocation and resizing with keyboard arrow key shortcuts.
 *
 * Provides single-click step calculations (moveStep, resizeStep) and keyboard event listeners (handleKeyDown)
 * that map arrow keys to position shifts and Shift+arrow keys to dimension adjustments.
 *
 * Complies with WCAG 2.2 SC 2.5.7 (Dragging Movements) and SC 2.1.1 (Keyboard).
 *
 * @param {Object} options
 * @param {Object} [options.rect] Current bounds { x, y, w, h }
 * @param {Function} [options.onUpdate] Callback when rect changes: (nextRect) => void
 * @param {Function} [options.onMove] Callback for relative movement: (dx, dy) => void
 * @param {Function} [options.onResize] Callback for relative resizing: (dw, dh) => void
 * @param {Object} [options.container] Boundary limits { minX, minY, maxX, maxY, minW, minH, maxW, maxH }
 * @param {number} [options.stepPixels=32] Step size in pixels
 */
export function useSinglePointerRelocate({
  rect = { x: 0, y: 0, w: 320, h: 240 },
  onUpdate,
  onMove,
  onResize,
  container = {},
  stepPixels = 32,
} = {}) {
  const minX = container.minX ?? 0;
  const minY = container.minY ?? 0;
  const maxX = container.maxX ?? Infinity;
  const maxY = container.maxY ?? Infinity;
  const minW = container.minW ?? 216;
  const minH = container.minH ?? 96;
  const maxW = container.maxW ?? Infinity;
  const maxH = container.maxH ?? Infinity;

  /**
   * Adjusts position by stepPixels or relative dx/dy while respecting boundary limits.
   * Accepts direction string ('up', 'down', 'left', 'right', 'n', 's', 'w', 'e') or (dx, dy) numbers.
   */
  const moveStep = useCallback(
    (dirOrDx, customDyOrStep = stepPixels) => {
      let dx = 0;
      let dy = 0;

      if (typeof dirOrDx === "string") {
        const dir = dirOrDx.toLowerCase();
        const step = typeof customDyOrStep === "number" ? customDyOrStep : stepPixels;
        if (dir === "n" || dir === "up") dy = -step;
        else if (dir === "s" || dir === "down") dy = step;
        else if (dir === "w" || dir === "left") dx = -step;
        else if (dir === "e" || dir === "right") dx = step;
      } else if (typeof dirOrDx === "number") {
        dx = dirOrDx;
        dy = typeof customDyOrStep === "number" ? customDyOrStep : 0;
      }

      if (onMove) {
        onMove(dx, dy);
        return;
      }

      const currentW = rect.w || 320;
      const currentH = rect.h || 240;
      const currentX = rect.x || 0;
      const currentY = rect.y || 0;

      const nextX = Math.max(minX, Math.min(maxX - currentW, currentX + dx));
      const nextY = Math.max(minY, Math.min(maxY - currentH, currentY + dy));

      if (onUpdate) {
        onUpdate({ ...rect, x: nextX, y: nextY });
      }
    },
    [rect, onUpdate, onMove, minX, minY, maxX, maxY, stepPixels]
  );

  /**
   * Adjusts width and height by deltaWidth and deltaHeight while respecting minimum/maximum dimensions.
   */
  const resizeStep = useCallback(
    (deltaW, deltaH, customMinW = minW, customMinH = minH) => {
      if (onResize) {
        onResize(deltaW, deltaH);
        return;
      }

      const currentW = rect.w || 320;
      const currentH = rect.h || 240;

      const nextW = Math.min(maxW, Math.max(customMinW, currentW + deltaW));
      const nextH = Math.min(maxH, Math.max(customMinH, currentH + deltaH));

      if (onUpdate) {
        onUpdate({ ...rect, w: nextW, h: nextH });
      }
    },
    [rect, onUpdate, onResize, minW, minH, maxW, maxH]
  );

  /**
   * Event handler for keyboard navigation on window/tile headers.
   * Maps Arrow keys to position moves and Shift+Arrow keys to dimension resizes.
   */
  const handleKeyDown = useCallback(
    (e) => {
      const arrowKeys = ["ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight"];
      if (!arrowKeys.includes(e.key)) return;

      e.preventDefault();
      e.stopPropagation();

      const step = stepPixels;

      if (e.shiftKey) {
        // Shift + Arrow = Resize
        if (e.key === "ArrowUp") resizeStep(0, -step);
        else if (e.key === "ArrowDown") resizeStep(0, step);
        else if (e.key === "ArrowLeft") resizeStep(-step, 0);
        else if (e.key === "ArrowRight") resizeStep(step, 0);
      } else {
        // Arrow = Move
        if (e.key === "ArrowUp") moveStep("up", step);
        else if (e.key === "ArrowDown") moveStep("down", step);
        else if (e.key === "ArrowLeft") moveStep("left", step);
        else if (e.key === "ArrowRight") moveStep("right", step);
      }
    },
    [moveStep, resizeStep, stepPixels]
  );

  return {
    moveStep,
    resizeStep,
    handleKeyDown,
  };
}

export default useSinglePointerRelocate;
