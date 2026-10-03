import { useEffect, useRef } from "react";
import { liveRegionAnnouncer } from "../services/LiveRegionAnnouncer";
import { app } from "../linkpoint/app";

/**
 * LiveRegionAnnouncerComponent
 *
 * Mounts an off-screen role="log" live region element in the DOM tree (WCAG 2.2 SC 4.1.3).
 * Stays present in the DOM tree for screen readers while hidden visually using standard CSS (.sr-only).
 */
export default function LiveRegionAnnouncerComponent() {
  const liveRegionRef = useRef(null);

  useEffect(() => {
    if (liveRegionRef.current) {
      liveRegionAnnouncer.registerDOMElement(liveRegionRef.current);
    }
    liveRegionAnnouncer.attachChatBus(app.chat);

    return () => {
      // Don't fully destroy singleton on hot reload, but unregister DOM node
      liveRegionAnnouncer.registerDOMElement(null);
    };
  }, []);

  return (
    <div
      ref={liveRegionRef}
      id="live-region-announcer"
      role="log"
      aria-live="polite"
      aria-atomic="false"
      aria-label="Live Chat Announcements"
      className="sr-only live-region-announcer"
    />
  );
}
