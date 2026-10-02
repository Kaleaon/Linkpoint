/**
 * AnnouncerContext.jsx
 *
 * Centralized live region announcer primitive and context provider.
 * Implements offscreen persistent live regions for screen readers without shifting keyboard focus.
 *
 * Compliance References:
 * - WCAG 2.2 Criterion 4.1.3 Status Messages (Level AA): https://www.w3.org/TR/WCAG22/#status-messages
 * - WCAG 2.2 Criterion 1.3.1 Info and Relationships (Level A): https://www.w3.org/TR/WCAG22/#info-and-relationships
 * - W3C ARIA Technique ARIA22 (Using role=status): https://www.w3.org/WAI/WCAG22/Techniques/aria/ARIA22
 * - W3C ARIA Technique ARIA19 (Using ARIA role=alert): https://www.w3.org/WAI/WCAG22/Techniques/aria/ARIA19
 * - W3C Failure F103 (Status message failure): https://www.w3.org/WAI/WCAG22/Techniques/failures/F103
 */

import { createContext, useContext, useState, useRef, useCallback, useEffect } from "react";

export const AnnouncerContext = createContext(null);

// Standard CSS clipping style for visually hiding offscreen screen-reader live regions
const srOnlyStyle = {
  position: "absolute",
  width: "1px",
  height: "1px",
  padding: "0",
  margin: "-1px",
  overflow: "hidden",
  clip: "rect(0, 0, 0, 0)",
  whiteSpace: "nowrap",
  borderWidth: "0",
};

export function AnnouncerProvider({ children }) {
  const [politeMessage, setPoliteMessage] = useState("");
  const [assertiveMessage, setAssertiveMessage] = useState("");

  const politeQueueRef = useRef([]);
  const assertiveQueueRef = useRef([]);
  const politeTimerRef = useRef(null);
  const assertiveTimerRef = useRef(null);
  const processingPoliteRef = useRef(false);
  const processingAssertiveRef = useRef(false);

  // Process polite queue sequentially with debouncing to avoid speech truncation
  const processPoliteQueue = useCallback(() => {
    if (politeQueueRef.current.length === 0) {
      processingPoliteRef.current = false;
      return;
    }

    processingPoliteRef.current = true;
    const nextMsg = politeQueueRef.current.shift();

    // Briefly clear message to force DOM mutation if identical consecutive message
    setPoliteMessage("");
    setTimeout(() => {
      setPoliteMessage(nextMsg);
      politeTimerRef.current = setTimeout(() => {
        processPoliteQueue();
      }, 800);
    }, 50);
  }, []);

  // Process assertive queue sequentially
  const processAssertiveQueue = useCallback(() => {
    if (assertiveQueueRef.current.length === 0) {
      processingAssertiveRef.current = false;
      return;
    }

    processingAssertiveRef.current = true;
    const nextMsg = assertiveQueueRef.current.shift();

    setAssertiveMessage("");
    setTimeout(() => {
      setAssertiveMessage(nextMsg);
      assertiveTimerRef.current = setTimeout(() => {
        processAssertiveQueue();
      }, 800);
    }, 50);
  }, []);

  /**
   * Dispatch a status update to screen readers via persistent live regions.
   * @param {string} message - Text message to announce
   * @param {'polite' | 'assertive' | 'status' | 'alert'} [priority='polite'] - Announcement priority
   */
  const announce = useCallback(
    (message, priority = "polite") => {
      if (!message || typeof message !== "string") return;
      const cleanMessage = message.trim();
      if (!cleanMessage) return;

      const isAssertive = priority === "assertive" || priority === "alert";

      if (isAssertive) {
        assertiveQueueRef.current.push(cleanMessage);
        if (!processingAssertiveRef.current) {
          processAssertiveQueue();
        }
      } else {
        politeQueueRef.current.push(cleanMessage);
        if (!processingPoliteRef.current) {
          processPoliteQueue();
        }
      }
    },
    [processPoliteQueue, processAssertiveQueue]
  );

  useEffect(() => {
    return () => {
      clearTimeout(politeTimerRef.current);
      clearTimeout(assertiveTimerRef.current);
    };
  }, []);

  return (
    <AnnouncerContext.Provider value={{ announce }}>
      {children}
      {/* Offscreen persistent live region containers for assistive technology */}
      <div className="sr-only-announcer" style={srOnlyStyle}>
        <div role="status" aria-live="polite" aria-atomic="true">
          {politeMessage}
        </div>
        <div role="alert" aria-live="assertive" aria-atomic="true">
          {assertiveMessage}
        </div>
      </div>
    </AnnouncerContext.Provider>
  );
}

/**
 * Hook to access the announcer context.
 * Exposes announce(message, priority).
 */
export function useAnnouncer() {
  const context = useContext(AnnouncerContext);
  if (!context) {
    // Fallback gracefully if used outside provider, but log warning
    return {
      announce: (msg, priority) => {
        console.warn("useAnnouncer called outside AnnouncerProvider:", msg, priority);
      },
    };
  }
  return context;
}
