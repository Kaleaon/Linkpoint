import { useRef, useCallback } from "react";

export function useRovingTabindex({ count, activeIndex, onSelect, orientation = "horizontal" }) {
  const itemRefs = useRef([]);

  const handleKeyDown = useCallback(
    (e, index) => {
      if (!count || count <= 0) return;

      const isHorizontal = orientation === "horizontal";
      const isNextKey = isHorizontal
        ? (e.key === "ArrowRight" || e.key === "ArrowDown")
        : (e.key === "ArrowDown" || e.key === "ArrowRight");
      const isPrevKey = isHorizontal
        ? (e.key === "ArrowLeft" || e.key === "ArrowUp")
        : (e.key === "ArrowUp" || e.key === "ArrowLeft");

      let nextIndex = -1;

      if (isNextKey) {
        e.preventDefault();
        nextIndex = (index + 1) % count;
      } else if (isPrevKey) {
        e.preventDefault();
        nextIndex = (index - 1 + count) % count;
      } else if (e.key === "Home") {
        e.preventDefault();
        nextIndex = 0;
      } else if (e.key === "End") {
        e.preventDefault();
        nextIndex = count - 1;
      } else if (e.key === "Enter" || e.key === " ") {
        e.preventDefault();
        onSelect(index);
        return;
      }

      if (nextIndex !== -1) {
        onSelect(nextIndex);
        itemRefs.current[nextIndex]?.focus();
        setTimeout(() => {
          itemRefs.current[nextIndex]?.focus();
        }, 0);
      }
    },
    [count, onSelect, orientation]
  );

  const getTabProps = (index, isActive) => ({
    ref: (el) => {
      itemRefs.current[index] = el;
    },
    tabIndex: isActive ? 0 : -1,
    onKeyDown: (e) => handleKeyDown(e, index),
  });

  return { getTabProps };
}
