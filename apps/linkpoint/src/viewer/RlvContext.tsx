import React, { createContext, useCallback, useContext, useMemo, useState } from 'react';
import type { ViewerClient } from '@linkpoint/viewer-client';

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
 * Commands are standard RLV behavior names, mapping directly to keys used here.
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
  /** @recvchat=n — cannot receive local chat. */
  | 'recvchat'
  /** @sendim=n — cannot send instant messages. */
  | 'sendim'
  /** @recvim=n — cannot receive instant messages. */
  | 'recvim'
  /** @tplm=n — cannot teleport via a landmark. */
  | 'tplm'
  /** @tploc=n — cannot teleport to an arbitrary location. */
  | 'tploc'
  /** @sittp=n — cannot sit teleport. */
  | 'sittp'
  /** @tplure=n — cannot accept teleport lures. */
  | 'tplure'
  /** @tpto=n — cannot teleport to location. */
  | 'tpto'
  /** @accepttp=n — cannot accept teleport offers. */
  | 'accepttp'
  /** @showinv=n — the inventory must not be browsable. */
  | 'showinv'
  /** @showworldmap=n — the world map must not be viewable. */
  | 'showworldmap'
  /** @showminimap=n — the radar and minimap must not be viewable. */
  | 'showminimap'
  /** @viewnote=n — cannot view notecards. */
  | 'viewnote'
  /** @edit=n — cannot edit objects. */
  | 'edit'
  /** @rez=n — cannot rez objects. */
  | 'rez'
  /** @unsit=n — cannot stand up / unsit. */
  | 'unsit'
  /** @sit=n — cannot sit. */
  | 'sit'
  /** @remoutfit=n — cannot remove outfit items. */
  | 'remoutfit'
  /** @addoutfit=n — cannot add outfit items. */
  | 'addoutfit'
  /** @sendchannel=n — cannot send chat on script channels. */
  | 'sendchannel'
  /** @redirchat=n — redirect local chat. */
  | 'redirchat';

/** What to tell the resident when a control is held by a restriction. */
export const RLV_REASONS: Record<RlvRestriction, string> = {
  detach: 'Locked by RLV — this item cannot be detached.',
  showloc: 'Hidden by RLV — your location is restricted.',
  shownames: 'Hidden by RLV — resident names are restricted.',
  sendchat: 'Blocked by RLV — you cannot send local chat.',
  recvchat: 'Blocked by RLV — receiving local chat is restricted.',
  sendim: 'Blocked by RLV — you cannot send instant messages.',
  recvim: 'Blocked by RLV — receiving instant messages is restricted.',
  tplm: 'Blocked by RLV — landmark teleports are restricted.',
  tploc: 'Blocked by RLV — teleporting is restricted.',
  sittp: 'Blocked by RLV — sit teleporting is restricted.',
  tplure: 'Blocked by RLV — teleport offers are restricted.',
  tpto: 'Blocked by RLV — location teleports are restricted.',
  accepttp: 'Blocked by RLV — accepting teleport offers is restricted.',
  showinv: 'Hidden by RLV — your inventory is restricted.',
  showworldmap: 'Hidden by RLV — the world map is restricted.',
  showminimap: 'Hidden by RLV — the radar is restricted.',
  viewnote: 'Blocked by RLV — viewing notecards is restricted.',
  edit: 'Blocked by RLV — editing objects is restricted.',
  rez: 'Blocked by RLV — rezzing objects is restricted.',
  unsit: 'Locked by RLV — standing up is restricted.',
  sit: 'Blocked by RLV — sitting is restricted.',
  remoutfit: 'Locked by RLV — removing outfit items is restricted.',
  addoutfit: 'Blocked by RLV — adding outfit items is restricted.',
  sendchannel: 'Blocked by RLV — sending on chat channels is restricted.',
  redirchat: 'Redirected by RLV — local chat is redirected.',
};

/** The placeholder shown wherever a name or location has been censored. */
export const RLV_REDACTED = '(hidden)';

export const DEFAULT_RLV_OBJECT_UUID = 'default';

export interface ParsedRlvCommand {
  name: string;
  option: string | null;
  value: string;
}

/**
 * Parses an RLV command string into single command components.
 * Supports single (@detach=n) and compound (@detach=n|sendchat=n or @detach=n,sendchat=n) command strings.
 */
export function parseRlvCommandString(rawCommand: string): ParsedRlvCommand[] {
  let str = rawCommand.trim();
  if (!str) return [];

  const parts = str.split(/[|,]/);
  const parsed: ParsedRlvCommand[] = [];

  for (const part of parts) {
    let trimmed = part.trim();
    if (trimmed.startsWith('@')) {
      trimmed = trimmed.substring(1).trim();
    }
    if (!trimmed) continue;

    const equalsIdx = trimmed.indexOf('=');
    if (equalsIdx < 0) continue;

    const cmdPart = trimmed.substring(0, equalsIdx).trim();
    const value = trimmed.substring(equalsIdx + 1).trim().toLowerCase();

    const colonIdx = cmdPart.indexOf(':');
    const name = (colonIdx >= 0 ? cmdPart.substring(0, colonIdx) : cmdPart).trim().toLowerCase();
    const option = colonIdx >= 0 ? cmdPart.substring(colonIdx + 1).trim() : null;

    parsed.push({ name, option, value });
  }

  return parsed;
}

export interface RlvContextValue {
  /** Master switch. With RLV off no restriction applies, whatever is set. */
  enabled: boolean;
  setEnabled: (on: boolean) => void;
  /** Active restrictions aggregated across all issuing objects. */
  active: Set<RlvRestriction>;
  /** Map of restrictions grouped by issuing object UUID. */
  objectRestrictions: Map<string, Set<RlvRestriction>>;
  /** True when this restriction is in force right now. */
  restricted: (r: RlvRestriction) => boolean;
  /** The reason string when restricted, otherwise null — handy for a title. */
  reasonFor: (r: RlvRestriction) => string | null;
  /** Apply or clear a restriction, as an in-world command would. */
  setRestriction: (r: RlvRestriction, on: boolean, objectUuid?: string) => void;
  /** Parse and process an incoming raw RLV command string from an object. */
  processCommand: (rawCommand: string, objectUuid?: string, isOwner?: boolean) => void;
  /** Clear all restrictions issued by a specific object UUID (e.g. when detached). */
  clearObjectRestrictions: (objectUuid: string) => void;
  /** Clear all restrictions across all objects. */
  clearAllRestrictions: () => void;
}

const RlvContext = createContext<RlvContextValue | null>(null);

export const RlvProvider: React.FC<{
  children?: React.ReactNode;
  /** Restore the consent flag, e.g. after a relog. */
  initialEnabled?: boolean;
  /** Restore restrictions still held by objects the resident is wearing. */
  initialRestrictions?: RlvRestriction[];
  /** Optional viewer client for sending query replies over script channels. */
  client?: ViewerClient;
  /** Optional callback for query command replies (useful for testing and script channels). */
  onQueryReply?: (channel: number, reply: string) => void;
}> = ({ children, initialEnabled = false, initialRestrictions, client, onQueryReply }) => {
  const [enabled, setEnabled] = useState(initialEnabled);

  // Map of objectUuid -> Set<RlvRestriction>
  const [objectRestrictions, setObjectRestrictions] = useState<Map<string, Set<RlvRestriction>>>(() => {
    const map = new Map<string, Set<RlvRestriction>>();
    if (initialRestrictions && initialRestrictions.length > 0) {
      map.set(DEFAULT_RLV_OBJECT_UUID, new Set(initialRestrictions));
    }
    return map;
  });

  // Compute union of active restrictions across all objects
  const active = useMemo(() => {
    const union = new Set<RlvRestriction>();
    for (const set of objectRestrictions.values()) {
      for (const r of set) {
        union.add(r);
      }
    }
    return union;
  }, [objectRestrictions]);

  const restricted = useCallback((r: RlvRestriction) => enabled && active.has(r), [enabled, active]);

  const reasonFor = useCallback(
    (r: RlvRestriction) => (enabled && active.has(r) ? RLV_REASONS[r] ?? 'Restricted by RLV.' : null),
    [enabled, active],
  );

  const setRestriction = useCallback((r: RlvRestriction, on: boolean, objectUuid: string = DEFAULT_RLV_OBJECT_UUID) => {
    setObjectRestrictions((prev) => {
      const next = new Map(prev);
      const currentSet = new Set(next.get(objectUuid) ?? []);
      if (on) {
        currentSet.add(r);
        next.set(objectUuid, currentSet);
      } else {
        currentSet.delete(r);
        if (currentSet.size === 0) {
          next.delete(objectUuid);
        } else {
          next.set(objectUuid, currentSet);
        }
      }
      return next;
    });
  }, []);

  const clearObjectRestrictions = useCallback((objectUuid: string) => {
    setObjectRestrictions((prev) => {
      if (!prev.has(objectUuid)) return prev;
      const next = new Map(prev);
      next.delete(objectUuid);
      return next;
    });
  }, []);

  const clearAllRestrictions = useCallback(() => {
    setObjectRestrictions(new Map());
  }, []);

  const sendReply = useCallback(
    (channel: number, reply: string) => {
      if (onQueryReply) {
        onQueryReply(channel, reply);
      }
      if (client) {
        void client.execute({
          type: 'chat.send',
          payload: { body: `/${channel} ${reply}` },
        });
      }
    },
    [client, onQueryReply],
  );

  const processCommand = useCallback(
    (rawCommand: string, objectUuid: string = DEFAULT_RLV_OBJECT_UUID, isOwner: boolean = true) => {
      if (!isOwner) {
        // TPV constraint: Unowned objects cannot issue restrictions unless authorized
        return;
      }

      const parsed = parseRlvCommandString(rawCommand);
      if (parsed.length === 0) return;

      // Group restriction state mutations into a single state update call per command string
      setObjectRestrictions((prev) => {
        let nextMap: Map<string, Set<RlvRestriction>> | null = null;
        const getMutableMap = () => {
          if (!nextMap) {
            nextMap = new Map(prev);
          }
          return nextMap;
        };

        for (const cmd of parsed) {
          const { name, option, value } = cmd;

          // 1. Query commands
          if (name === 'version' || name === 'versionnew' || name === 'versionnum') {
            const channel = parseInt(value, 10);
            if (!isNaN(channel)) {
              let reply = 'RestrainedLife viewer v2.8.0 (Linkpoint RLV v3.4.3)';
              if (name === 'versionnew') {
                reply = 'RestrainedLove viewer v2.8.0 (Linkpoint RLV v3.4.3)';
              } else if (name === 'versionnum') {
                reply = '3040300';
              }
              sendReply(channel, reply);
            }
            continue;
          }

          if (name === 'getstatus') {
            const channel = parseInt(value, 10);
            if (!isNaN(channel)) {
              const filter = (option ?? '').toLowerCase();
              const activeList = Array.from(active).filter((r) => !filter || r.includes(filter));
              const reply = activeList.length > 0 ? '/' + activeList.join('/') : '';
              sendReply(channel, reply);
            }
            continue;
          }

          if (name === 'getstatusall') {
            const channel = parseInt(value, 10);
            if (!isNaN(channel)) {
              const entries: string[] = [];
              const targetMap = nextMap ?? prev;
              targetMap.forEach((set, objId) => {
                set.forEach((r) => entries.push(`${r}:${objId}`));
              });
              const reply = entries.join('/');
              sendReply(channel, reply);
            }
            continue;
          }

          if (name === 'getoutfit') {
            const channel = parseInt(value, 10);
            if (!isNaN(channel)) {
              sendReply(channel, 'worn');
            }
            continue;
          }

          if (name === 'getattach') {
            const channel = parseInt(value, 10);
            if (!isNaN(channel)) {
              sendReply(channel, option ? `attached:${option}` : 'attached');
            }
            continue;
          }

          // 2. Clear command
          if (name === 'clear') {
            const filter = (option || value || '').toLowerCase();
            const map = getMutableMap();
            if (!filter || filter === 'y') {
              map.delete(objectUuid);
            } else {
              const currentSet = new Set(map.get(objectUuid) ?? []);
              for (const r of Array.from(currentSet)) {
                if (r.includes(filter)) {
                  currentSet.delete(r);
                }
              }
              if (currentSet.size === 0) {
                map.delete(objectUuid);
              } else {
                map.set(objectUuid, currentSet);
              }
            }
            continue;
          }

          // 3. Restriction commands (handles standard restriction command classes)
          if (name in RLV_REASONS || isRlvRestriction(name)) {
            const restriction = name as RlvRestriction;
            const map = getMutableMap();
            const currentSet = new Set(map.get(objectUuid) ?? []);
            if (value === 'n' || value === 'add' || value === 'force') {
              currentSet.add(restriction);
              map.set(objectUuid, currentSet);
            } else if (value === 'y' || value === 'rem') {
              currentSet.delete(restriction);
              if (currentSet.size === 0) {
                map.delete(objectUuid);
              } else {
                map.set(objectUuid, currentSet);
              }
            }
          }
        }

        return nextMap ?? prev;
      });
    },
    [active, sendReply],
  );

  const value = useMemo<RlvContextValue>(
    () => ({
      enabled,
      setEnabled,
      active,
      objectRestrictions,
      restricted,
      reasonFor,
      setRestriction,
      processCommand,
      clearObjectRestrictions,
      clearAllRestrictions,
    }),
    [
      enabled,
      active,
      objectRestrictions,
      restricted,
      reasonFor,
      setRestriction,
      processCommand,
      clearObjectRestrictions,
      clearAllRestrictions,
    ],
  );

  return <RlvContext.Provider value={value}>{children}</RlvContext.Provider>;
};

function isRlvRestriction(name: string): name is RlvRestriction {
  return [
    'detach',
    'showloc',
    'shownames',
    'sendchat',
    'recvchat',
    'sendim',
    'recvim',
    'tplm',
    'tploc',
    'sittp',
    'tplure',
    'tpto',
    'accepttp',
    'showinv',
    'showworldmap',
    'showminimap',
    'viewnote',
    'edit',
    'rez',
    'unsit',
    'sit',
    'remoutfit',
    'addoutfit',
    'sendchannel',
    'redirchat',
  ].includes(name);
}

export function useRlv(): RlvContextValue {
  const ctx = useContext(RlvContext);
  if (!ctx) throw new Error('useRlv must be used inside an RlvProvider');
  return ctx;
}
