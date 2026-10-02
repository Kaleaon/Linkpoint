import { useEffect, useRef } from "react";

/**
 * FocusTrap component wraps interactive dialogs/overlays to trap keyboard focus,
 * handle Escape dismissals, and restore focus to the triggering element upon unmount.
 */
export default function FocusTrap({
  active = true,
  onEscape = undefined,
  autoFocus = true,
  children = null,
  style = undefined,
  className = undefined,
  ...props
} = {}) {
  const containerRef = useRef(null);
  const previousFocusRef = useRef(null);

  const getFocusableElements = () => {
    if (!containerRef.current) return [];
    const selectors = [
      'a[href]',
      'button:not([disabled])',
      'input:not([disabled])',
      'select:not([disabled])',
      'textarea:not([disabled])',
      '[tabindex]:not([tabindex="-1"])',
    ].join(', ');

    const elements = Array.from(containerRef.current.querySelectorAll(selectors));
    return elements.filter((el) => {
      return (
        !el.disabled &&
        el.getAttribute('aria-disabled') !== 'true' &&
        el.getAttribute('tabindex') !== '-1' &&
        !el.hasAttribute('hidden') &&
        el.type !== 'hidden'
      );
    });
  };

  useEffect(() => {
    if (!active) return undefined;

    if (!previousFocusRef.current && document.activeElement) {
      previousFocusRef.current = document.activeElement;
    }

    if (autoFocus && containerRef.current) {
      const focusables = getFocusableElements();
      if (!containerRef.current.contains(document.activeElement)) {
        if (focusables.length > 0) {
          focusables[0].focus();
        } else {
          containerRef.current.focus();
        }
      }
    }

    const handleKeyDown = (event) => {
      if (!active || !containerRef.current) return;

      if (event.key === "Escape") {
        if (typeof onEscape === "function") {
          event.preventDefault();
          event.stopPropagation();
          onEscape(event);
        }
        return;
      }

      if (event.key === "Tab") {
        if (event.altKey || event.metaKey || event.ctrlKey) return;

        const focusables = getFocusableElements();
        if (focusables.length === 0) {
          event.preventDefault();
          return;
        }

        const firstEl = focusables[0];
        const lastEl = focusables[focusables.length - 1];
        const activeEl = document.activeElement;

        if (event.shiftKey) {
          if (activeEl === firstEl || !containerRef.current.contains(activeEl)) {
            event.preventDefault();
            lastEl.focus();
          }
        } else {
          if (activeEl === lastEl || !containerRef.current.contains(activeEl)) {
            event.preventDefault();
            firstEl.focus();
          }
        }
      }
    };

    window.addEventListener("keydown", handleKeyDown, true);

    return () => {
      window.removeEventListener("keydown", handleKeyDown, true);

      if (
        previousFocusRef.current &&
        typeof previousFocusRef.current.focus === "function" &&
        document.body.contains(previousFocusRef.current)
      ) {
        previousFocusRef.current.focus();
      }
    };
  }, [active, autoFocus, onEscape]);

  return (
    <div
      ref={containerRef}
      tabIndex={-1}
      style={{ outline: "none", ...style }}
      className={className}
      {...props}
    >
      {children}
    </div>
  );
}
