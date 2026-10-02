import React, { useState, useEffect } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { useApp } from "../context/AppContext.jsx";
import { app } from "../linkpoint/app";
import Icon from "../components/Icon.jsx";
import { UNKNOWN, show, positiveOrNull, latencyBand, lossBand, pushLatency, latencyRange, packetAgeMs, describePing, eventQueueState } from "./diagnosticsView.js";

export default function DiagnosticsPanel() {
  const { V, t } = useTheme();
  const { state, actions } = useApp();

  const [diag, setDiag] = useState(() => app.protocol.getDiagnostics());
  const [latencyHistory, setLatencyHistory] = useState(() => pushLatency([], diag.latencyMs));
  const [pinging, setPinging] = useState(false);
  const [now, setNow] = useState(Date.now());

  useEffect(() => {
    // Keep a ticking clock for relative "last received packet" seconds
    const clock = setInterval(() => setNow(Date.now()), 250);

    const updateDiag = (newDiag) => {
      if (!newDiag) return;
      setDiag(newDiag);
      setLatencyHistory((prev) => pushLatency(prev, newDiag.latencyMs));
    };

    app.protocol.on("diagnostics_updated", updateDiag);
    // Initial fetch from connection
    app.protocol.fetchDiagnostics().then(updateDiag);

    // Periodic poll for circuit counters every 3s
    const poller = setInterval(() => {
      app.protocol.fetchDiagnostics().then(updateDiag);
    }, 3000);

    return () => {
      clearInterval(clock);
      clearInterval(poller);
      app.protocol.off("diagnostics_updated", updateDiag);
    };
  }, []);

  const handlePingNow = async () => {
    setPinging(true);
    try {
      const res = await app.protocol.fetchDiagnostics();
      if (res) {
        setDiag(res);
        setLatencyHistory((prev) => pushLatency(prev, res.latencyMs));
      }
      actions.notify(describePing(res));
    } catch {
      actions.notify("Ping probe error");
    } finally {
      setPinging(false);
    }
  };

  const isConnected = diag.connected || app.auth.isLoggedIn();
  const toneColor = (tone) => (tone === "ok" ? V.ok : tone === "warn" ? "#eab308" : tone === "err" ? V.err : V.ink2);
  const latency = latencyBand(diag.latencyMs);
  const latencyTone = toneColor(latency.tone);
  const loss = lossBand(diag.packetLossPct);
  const lossTone = toneColor(loss.tone);

  const lastTs = diag.lastPacketTimestamp;
  const ageMs = packetAgeMs(lastTs, now);
  const ageSec = ageMs === null ? UNKNOWN : (ageMs / 1000).toFixed(1);
  const ageTone = ageMs === null ? V.ink2 : ageMs < 3000 ? V.ok : ageMs < 8000 ? "#eab308" : V.err;

  const region = app.world?.region;
  const gridCoords = Number.isFinite(region?.x) && Number.isFinite(region?.y) ? `${region.x}, ${region.y}` : UNKNOWN;
  const capabilityCount = Object.keys(app.protocol?.capabilities || {}).length;
  const queue = eventQueueState(isConnected, app.protocol?.eventQueueRunning);

  const cardStyle = {
    background: V.surf,
    border: `1px solid ${V.outv}`,
    borderRadius: V.rs,
    padding: 16,
    display: "grid",
    gap: 12,
  };

  // Sparkline calculation
  const range = latencyRange(latencyHistory);
  const minL = range ? range.min : 0;
  const maxL = range ? range.max : 0;
  // Scale bars against a floor so one sample does not fill the whole graph.
  const barMax = Math.max(maxL, 120);
  const barMin = Math.min(minL, 20);
  const hRange = barMax - barMin || 1;

  return (
    <div style={{ flex: 1, minHeight: 0, overflowY: "auto", padding: "16px", display: "grid", gap: 16, maxWidth: 720, margin: "0 auto", width: "100%" }}>
      {/* Header Banner */}
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", borderBottom: `1px solid ${V.outv}`, paddingBottom: 12 }}>
        <div>
          <h2 style={{ margin: 0, color: V.pri, font: `700 18px/1.2 ${t.dfont}`, letterSpacing: ".08em", display: "flex", alignItems: "center", gap: 8 }}>
            <Icon name="activity" size={22} />
            SECOND LIFE CONNECTION DIAGNOSTICS
          </h2>
          <div style={{ fontSize: 12, color: V.ink2, marginTop: 4 }}>
            Real-time telemetry and network circuit monitoring from <code>SLConnectionFull</code>.
          </div>
        </div>
        <button
          type="button"
          onClick={handlePingNow}
          disabled={pinging || !isConnected}
          style={{
            minHeight: 36,
            padding: "0 14px",
            background: V.pri,
            color: V.onpri,
            border: 0,
            borderRadius: V.rs,
            fontWeight: 700,
            fontSize: 11,
            letterSpacing: ".1em",
            cursor: pinging ? "wait" : "pointer",
            display: "inline-flex",
            alignItems: "center",
            gap: 6,
            opacity: isConnected ? 1 : 0.6,
          }}
        >
          <Icon name="refresh-cw" size={13} />
          {pinging ? "PINGING…" : "PING PROBE"}
        </button>
      </div>

      {/* Primary KPI Row */}
      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))", gap: 12 }}>
        {/* Status Card */}
        <div style={cardStyle}>
          <div style={{ fontSize: 11, fontWeight: 700, color: V.ink2, letterSpacing: ".1em" }}>
            CIRCUIT STATE
          </div>
          <div style={{ display: "flex", alignItems: "baseline", gap: 8 }}>
            <span style={{ width: 10, height: 10, borderRadius: "50%", background: isConnected ? V.ok : V.err }} />
            <span style={{ fontSize: 20, fontWeight: 800, color: isConnected ? V.ok : V.err, fontFamily: t.dfont }}>
              {isConnected ? "CONNECTED" : "OFFLINE"}
            </span>
          </div>
          <div style={{ fontSize: 11, color: V.ink2 }}>
            Grid: <strong>{show(app.auth.user?.grid)}</strong> · {show(diag.simName)}
          </div>
        </div>

        {/* Latency Card */}
        <div style={cardStyle}>
          <div style={{ fontSize: 11, fontWeight: 700, color: V.ink2, letterSpacing: ".1em", display: "flex", justifyContent: "space-between" }}>
            <span>SIM LATENCY</span>
            <span style={{ color: latencyTone }}>{latency.label}</span>
          </div>
          <div style={{ display: "flex", alignItems: "baseline", gap: 4 }}>
            <span style={{ fontSize: 28, fontWeight: 800, color: latencyTone, fontFamily: t.dfont }}>
              {latency.text}
            </span>
            <span style={{ fontSize: 12, fontWeight: 700, color: V.ink2 }}>MS</span>
          </div>
          <div style={{ fontSize: 11, color: V.ink2 }}>
            Round-trip simulator circuit time
          </div>
        </div>

        {/* Packet Loss Card */}
        <div style={cardStyle}>
          <div style={{ fontSize: 11, fontWeight: 700, color: V.ink2, letterSpacing: ".1em" }}>
            PACKET LOSS
          </div>
          <div style={{ display: "flex", alignItems: "baseline", gap: 4 }}>
            <span style={{ fontSize: 28, fontWeight: 800, color: lossTone, fontFamily: t.dfont }}>
              {loss.text}
            </span>
            {loss.text !== UNKNOWN && <span style={{ fontSize: 14, fontWeight: 700, color: V.ink2 }}>%</span>}
          </div>
          <div style={{ fontSize: 11, color: V.ink2 }}>
            {loss.note}
          </div>
        </div>

        {/* Last Packet Received */}
        <div style={cardStyle}>
          <div style={{ fontSize: 11, fontWeight: 700, color: V.ink2, letterSpacing: ".1em" }}>
            LAST PACKET RECEIVED
          </div>
          <div style={{ display: "flex", alignItems: "baseline", gap: 4 }}>
            <span style={{ fontSize: 28, fontWeight: 800, color: ageTone, fontFamily: t.dfont }}>
              {ageSec}
            </span>
            <span style={{ fontSize: 12, fontWeight: 700, color: V.ink2 }}>SEC AGO</span>
          </div>
          <div style={{ fontSize: 11, color: V.ink2, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
            {ageMs === null ? "No simulator packets recorded" : new Date(lastTs).toLocaleTimeString()}
          </div>
        </div>
      </div>

      {/* Latency History Graph */}
      <div style={cardStyle}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <div style={{ fontSize: 12, fontWeight: 700, color: V.pri, letterSpacing: ".08em" }}>
            ROUND-TRIP LATENCY HISTORY (RECENT PINGS)
          </div>
          <div style={{ fontSize: 11, color: V.ink2 }}>
            {range ? `Min: ${Math.round(minL)} ms · Max: ${Math.round(maxL)} ms` : "No samples yet"}
          </div>
        </div>
        <div style={{ height: 64, display: "flex", alignItems: "flex-end", gap: 4, background: V.bg, padding: "8px 12px", borderRadius: V.rs, border: `1px solid ${V.outv}` }}>
          {latencyHistory.length === 0 && <span style={{ alignSelf: "center", fontSize: 11, color: V.ink2 }}>Waiting for the simulator to report a round-trip time.</span>}
          {latencyHistory.map((val, idx) => {
            const hPct = Math.round(((val - barMin) / hRange) * 80 + 10);
            const tone = val < 90 ? V.ok : val < 200 ? "#eab308" : V.err;
            return (
              <div
                key={idx}
                title={`${val} ms`}
                style={{
                  flex: 1,
                  height: `${hPct}%`,
                  background: tone,
                  borderRadius: "2px 2px 0 0",
                  opacity: 0.85,
                  minWidth: 4,
                  transition: "height 0.25s ease-out",
                }}
              />
            );
          })}
        </div>
      </div>

      {/* Simulator Circuit Telemetry Details */}
      <div style={cardStyle}>
        <div style={{ fontSize: 12, fontWeight: 700, color: V.pri, letterSpacing: ".08em" }}>
          CIRCUIT & REGION TELEMETRY DETAILS
        </div>
        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: 10, fontSize: 12 }}>
          <div style={{ display: "flex", justifyContent: "space-between", padding: "6px 8px", background: V.bg, borderRadius: V.rs }}>
            <span style={{ color: V.ink2 }}>Simulator Name</span>
            <strong>{show(diag.simName)}</strong>
          </div>
          <div style={{ display: "flex", justifyContent: "space-between", padding: "6px 8px", background: V.bg, borderRadius: V.rs }}>
            <span style={{ color: V.ink2 }}>Grid Coordinates</span>
            <strong>{gridCoords}</strong>
          </div>
          <div style={{ display: "flex", justifyContent: "space-between", padding: "6px 8px", background: V.bg, borderRadius: V.rs }}>
            <span style={{ color: V.ink2 }}>Simulator IP</span>
            <strong>{show(diag.simAddress)}</strong>
          </div>
          <div style={{ display: "flex", justifyContent: "space-between", padding: "6px 8px", background: V.bg, borderRadius: V.rs }}>
            <span style={{ color: V.ink2 }}>Simulator UDP Port</span>
            <strong>{show(positiveOrNull(diag.simPort))}</strong>
          </div>
          <div style={{ display: "flex", justifyContent: "space-between", padding: "6px 8px", background: V.bg, borderRadius: V.rs }}>
            <span style={{ color: V.ink2 }}>Circuit Code</span>
            <strong>{show(positiveOrNull(diag.circuitCode))}</strong>
          </div>
          <div style={{ display: "flex", justifyContent: "space-between", padding: "6px 8px", background: V.bg, borderRadius: V.rs }}>
            <span style={{ color: V.ink2 }}>Packets RX (In)</span>
            <strong style={{ color: V.ok }}>{show(diag.packetsIn)}</strong>
          </div>
          <div style={{ display: "flex", justifyContent: "space-between", padding: "6px 8px", background: V.bg, borderRadius: V.rs }}>
            <span style={{ color: V.ink2 }}>Packets TX (Out)</span>
            <strong style={{ color: V.pri }}>{show(diag.packetsOut)}</strong>
          </div>
          <div style={{ display: "flex", justifyContent: "space-between", padding: "6px 8px", background: V.bg, borderRadius: V.rs }}>
            <span style={{ color: V.ink2 }}>Agent ID</span>
            <strong style={{ fontSize: 10, maxWidth: 140, overflow: "hidden", textOverflow: "ellipsis" }}>
              {show(diag.agentId || app.auth.user?.id)}
            </strong>
          </div>
        </div>
      </div>

      {/* Capabilities & Event Queue Section */}
      <div style={cardStyle}>
        <div style={{ fontSize: 12, fontWeight: 700, color: V.pri, letterSpacing: ".08em" }}>
          CAPABILITIES & HTTP EVENT QUEUE STATUS
        </div>
        <div style={{ display: "grid", gap: 8, fontSize: 12 }}>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
            <span style={{ color: V.ink2 }}>Event Queue State</span>
            <span style={{ padding: "2px 8px", borderRadius: V.rs, background: V.surf2, color: toneColor(queue.tone), fontWeight: 700, fontSize: 11 }}>
              {queue.text}
            </span>
          </div>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
            <span style={{ color: V.ink2 }}>Active Capabilities</span>
            <span>{isConnected ? `${capabilityCount} loaded` : UNKNOWN}</span>
          </div>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
            <span style={{ color: V.ink2 }}>Seed Capability</span>
            <span style={{ fontSize: 10, maxWidth: 300, overflow: "hidden", textOverflow: "ellipsis", color: V.ink2 }}>
              {show(app.protocol?.seedCapability)}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
