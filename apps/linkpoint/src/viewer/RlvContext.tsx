import React, { createContext, useCallback, useContext, useMemo, useState } from 'react';

/**
 * RLV (Restrained Life Viewer) restrictions.
 *
 * TPV_COMPLIANCE.md §4 requires that when a restriction is active the UI
 * strictly prohibits the action — not merely discourages it. So restrictions
 * are resolved here, at the top of the tree, and every affected control asks
 * this context before it renders as usable. A restricted control is disabled
 * and says which restriction is holding it, because a dead button with no
 * explanation reads as a bug.
 *
 * Commands are the standard RLV behaviour names, so a restriction issued by an
 * in-world object maps straight onto the key used here.
 */
export type RlvRestriction =
  /** @detach=n — attachments and HUDs cannot be removed. */
  | 'detach'
  /** @showloc=n — region name, coordinates and SLURLs must be hidden. */
  | 'showloc'
  /** @shownames=n — other residents' names must be hidden. */
  | 'shownames'
  /** @sendchat=n — cannot send to local chat. */
  | 'sendchat'
  /** @sendim=n — cannot send instant messages. */
  | 'sendim'
  /** @tplm=n — cannot teleport via a landmark. */
  | 'tplm'
  /** @tploc=n — cannot teleport to an arbitrary location. */
  | 'tploc'
  /** @showinv=n — the inventory must not be browsable. */
  | 'showinv'
  /** @showworldmap=n — the world map must not be viewable. */
  | 'showworldmap'
  /** @showminimap=n — the radar and minimap must not be viewable. */
  | 'showminimap';

/** What to tell the resident when a control is held by a restriction. */
export const RLV_REASONS: Record<RlvRestriction, string> = {
  detach: 'Locked by RLV — this item cannot be detached.',
  showloc: 'Hidden by RLV — your location is restricted.',
  shownames: 'Hidden by RLV — resident names are restricted.',
  sendchat: 'Blocked by RLV — you cannot send local chat.',
  sendim: 'Blocked by RLV — you cannot send instant messages.',
  tplm: 'Blocked by RLV — landmark teleports are restricted.',
  tploc: 'Blocked by RLV — teleporting is restricted.',
  showinv: 'Hidden by RLV — your inventory is restricted.',
  showworldmap: 'Hidden by RLV — the world map is restricted.',
  showminimap: 'Hidden by RLV — the radar is restricted.',
};

/** The placeholder shown wherever a name or location has been censored. */
export const RLV_REDACTED = '(hidden)';

interface RlvContextValue {
  /** Master switch. With RLV off no restriction applies, whatever is set. */
  enabled: boolean;
  setEnabled: (on: boolean) => void;
  /** The restrictions currently issued by in-world objects. */
  active: Set<RlvRestriction>;
  /** True when this restriction is in force right now. */
  restricted: (r: RlvRestriction) => boolean;
  /** The reason string when restricted, otherwise null — handy for a title. */
  reasonFor: (r: RlvRestriction) => string | null;
  /** Apply or clear a restriction, as an in-world command would. */
  setRestriction: (r: RlvRestriction, on: boolean) => void;
}

const RlvContext = createContext<RlvContextValue | null>(null);

export const RlvProvider: React.FC<{
  children: React.ReactNode;
  /** Restore the consent flag, e.g. after a relog. */
  initialEnabled?: boolean;
  /** Restore restrictions still held by objects the resident is wearing. */
  initialRestrictions?: RlvRestriction[];
}> = ({ children, initialEnabled = false, initialRestrictions }) => {
  // RLV is off until a resident consents to it: restrictions are consensual by
  // definition, so nothing is enforced by default.
  const [enabled, setEnabled] = useState(initialEnabled);
  const [active, setActive] = useState<Set<RlvRestriction>>(() => new Set(initialRestrictions ?? []));

  const restricted = useCallback((r: RlvRestriction) => enabled && active.has(r), [enabled, active]);

  const reasonFor = useCallback((r: RlvRestriction) => (enabled && active.has(r) ? RLV_REASONS[r] : null), [enabled, active]);

  const setRestriction = useCallback((r: RlvRestriction, on: boolean) => {
    setActive((prev) => {
      const next = new Set(prev);
      if (on) next.add(r);
      else next.delete(r);
      return next;
    });
  }, []);

  const value = useMemo<RlvContextValue>(
    () => ({ enabled, setEnabled, active, restricted, reasonFor, setRestriction }),
    [enabled, active, restricted, reasonFor, setRestriction],
  );

  return <RlvContext.Provider value={value}>{children}</RlvContext.Provider>;
};

export function useRlv(): RlvContextValue {
  const ctx = useContext(RlvContext);
  if (!ctx) throw new Error('useRlv must be used inside an RlvProvider');
  return ctx;
}
