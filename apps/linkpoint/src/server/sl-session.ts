/**
 * Web host for the shared viewer session (core/viewer-session.cjs). It owns only what is specific
 * to HTTP: a session id, the server-sent-event clients, and the callbacks that fan events out to
 * them. Every viewer behaviour lives in core/ and is reached through `callViewer`.
 */
import type { Response } from 'express';
import { createRequire } from 'node:module';
import { v4 as uuidv4 } from 'uuid';

const require = createRequire(import.meta.url);
const { ViewerSession } = require('../../core/viewer-session.cjs') as { ViewerSession: new (send: (type: string, data: any) => void, options?: { replay?: boolean }) => any };
const { callViewer } = require('../../core/viewer-api.cjs') as { callViewer: (session: any, method: string, params?: any) => Promise<any> };

// Filter out harmless SL packet padding and diagnostic warnings from node-metaverse
const _origConsoleError = console.error;
console.error = function (...args: any[]) {
  if (
    typeof args[0] === 'string' &&
    (args[0].startsWith('WARNING: Finished reading ') ||
     args[0].includes('not at the end of the packet') ||
     args[0].startsWith('WARNING: Bytes written does not match') ||
     args[0].startsWith('WARNING: BUFFER UNDERFLOW'))
  ) {
    return;
  }
  _origConsoleError.apply(console, args);
};

export interface SLSessionData {
  sessionId: string;
  viewer: any;
  eventClients: Response[];
  /** Name of the region logged in to, announced when an event stream opens. */
  simName: string;
}

const sessions = new Map<string, SLSessionData>();

function broadcast(session: SLSessionData, type: string, data: unknown) {
  const line = `data: ${JSON.stringify({ type, data })}\n\n`;
  for (const client of session.eventClients) {
    try { client.write(line); } catch { /* client went away; its close handler removes it */ }
  }
}

export async function createSLSession(request: Record<string, unknown>) {
  const sessionId = uuidv4();
  const record: SLSessionData = { sessionId, viewer: null, eventClients: [], simName: '' };
  record.viewer = new ViewerSession((type: string, data: unknown) => broadcast(record, type, data), { replay: true });
  const result = await record.viewer.connect(request);
  record.simName = result.sim_name || '';
  sessions.set(sessionId, record);
  return { sessionId, ...result };
}

export function getSLSession(sessionId: string): SLSessionData | undefined {
  return sessions.get(sessionId);
}

/** Run one allow-listed viewer call (see core/viewer-api.cjs) for a session. */
export function callSLSession(sessionId: string, method: string, params?: unknown) {
  const session = sessions.get(sessionId);
  if (!session) throw new Error('Not connected to Second Life');
  return callViewer(session.viewer, method, params);
}

export function closeSLSession(sessionId: string) {
  const session = sessions.get(sessionId);
  if (!session) return;
  sessions.delete(sessionId);
  for (const client of session.eventClients) {
    try { client.end(); } catch { /* already closed */ }
  }
  void session.viewer.close();
}
