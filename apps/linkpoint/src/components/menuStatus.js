// Status readout for the desktop menu bar. Only values the viewer actually has
// are shown: an unknown balance, region or latency is omitted rather than
// replaced with a plausible-looking placeholder.

/** "123 ms" for a real, positive measurement; null when there is none. */
export function formatLatency(diagnostics) {
  const ms = diagnostics?.latencyMs;
  return typeof ms === "number" && Number.isFinite(ms) && ms > 0 ? `${Math.round(ms)} ms` : null;
}

/** Region name from the live session, or null. */
export function liveRegionName(app) {
  const name = app?.protocol?.authReply?.sim_name || app?.world?.region?.name;
  return typeof name === "string" && name.trim() ? name.trim() : null;
}

/** Second Life Time is US Pacific time. */
export function formatSlt(date = new Date()) {
  return date.toLocaleTimeString("en-US", { timeZone: "America/Los_Angeles", hour: "2-digit", minute: "2-digit" }) + " SLT";
}
