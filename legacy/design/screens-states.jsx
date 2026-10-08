// Extra states: typing indicators, error toasts, low-bandwidth, login error, TP offers

// 20. Login — error state
function LoginErrorScreen() {
  return (
    <div className="screen">
      <StatusBar />
      <div style={{ position: 'absolute', inset: 0, overflow: 'hidden' }}>
        <div className="aurora-blob" style={{ width: 320, height: 320, right: -100, top: 200, background: 'var(--error)', opacity: 0.25 }} />
      </div>
      <div style={{ flex: 1, padding: '24px 24px 32px', position: 'relative', zIndex: 2, display: 'flex', flexDirection: 'column' }}>
        <div style={{ marginTop: 60 }}>
          <div className="h-title" style={{ fontSize: 32, marginBottom: 8 }}>Sign in</div>
          <div style={{ fontSize: 14, color: 'var(--on-surface-dim)' }}>Couldn't reach the grid.</div>
        </div>

        <div className="glass" style={{ marginTop: 28, padding: 18, border: '1px solid var(--error)' }}>
          <div style={{ display: 'flex', gap: 10, marginBottom: 14, padding: 12, background: 'color-mix(in oklab, var(--error) 18%, transparent)', borderRadius: 12 }}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--error)" strokeWidth="2"><circle cx="12" cy="12" r="10"/><path d="M12 8v4M12 16h.01"/></svg>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--error)' }}>Login failed · 401</div>
              <div style={{ fontSize: 12, color: 'var(--on-surface-dim)', marginTop: 2 }}>The username or password is incorrect. Check caps lock, then try again.</div>
            </div>
          </div>
          <div className="input" style={{ marginBottom: 10 }}>
            <input defaultValue="Lyra Resident" />
          </div>
          <div className="input" style={{ marginBottom: 14, borderColor: 'var(--error)' }}>
            <input type="password" defaultValue="●●●●●●●" />
            <span style={{ fontSize: 11, color: 'var(--error)', fontWeight: 600 }}>!</span>
          </div>
          <button className="btn" style={{ width: '100%', height: 48 }}>Try again</button>
          <button className="btn ghost" style={{ width: '100%', height: 40, marginTop: 8, fontSize: 12 }}>Reset password</button>
        </div>

        <div className="card" style={{ marginTop: 16, padding: 12, fontSize: 11, color: 'var(--on-surface-dim)' }}>
          <div style={{ fontWeight: 600, color: 'var(--on-surface)', marginBottom: 4 }}>Grid status — Agni</div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <span style={{ width: 6, height: 6, borderRadius: 3, background: 'var(--warn)' }} />
            <span>Login server degraded · 38% packet loss</span>
          </div>
        </div>

        <div style={{ flex: 1 }} />
      </div>
      <NavPill />
    </div>
  );
}

// 21. Chat — typing + delivery states
function ChatStatesScreen() {
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="Echo Nightshade"
        subtitle={<><span style={{ color: 'var(--success)' }}>●</span> typing…</>}
        leading={<><IconBtn><IconBack /></IconBtn><Avatar name="Echo" size="sm" online /></>}
        trailing={<><IconBtn><IconMore /></IconBtn></>}
      />
      <div style={{ flex: 1, overflow: 'auto', padding: '8px 16px', display: 'flex', flexDirection: 'column', gap: 8 }}>
        {/* sent — sending */}
        <div style={{ alignSelf: 'flex-end', maxWidth: '80%' }}>
          <div style={{ padding: '9px 14px', borderRadius: 18, borderBottomRightRadius: 6, background: 'color-mix(in oklab, var(--primary) 50%, transparent)', color: 'var(--on-primary)', fontSize: 14 }}>tping now 💜</div>
          <div style={{ fontSize: 10, color: 'var(--on-surface-dim)', textAlign: 'right', marginTop: 2, display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 4 }}>
            <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3"><circle cx="12" cy="12" r="10" strokeDasharray="20 40" strokeLinecap="round"><animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite"/></circle></svg>
            sending…
          </div>
        </div>

        {/* sent — failed */}
        <div style={{ alignSelf: 'flex-end', maxWidth: '80%' }}>
          <div style={{ padding: '9px 14px', borderRadius: 18, borderBottomRightRadius: 6, background: 'var(--surface-2)', color: 'var(--on-surface)', fontSize: 14, border: '1px solid var(--error)' }}>are you still there??</div>
          <div style={{ fontSize: 10, color: 'var(--error)', textAlign: 'right', marginTop: 2, fontWeight: 600 }}>! Couldn't send · tap to retry</div>
        </div>

        {/* received — new */}
        <div style={{ display: 'flex', alignItems: 'flex-end', gap: 8, alignSelf: 'flex-start', maxWidth: '82%' }}>
          <Avatar name="Echo" size="sm" />
          <div style={{ padding: '9px 14px', borderRadius: 18, borderBottomLeftRadius: 6, background: 'var(--surface-2)', fontSize: 14 }}>still here, my client crashed lol</div>
        </div>

        {/* typing bubble */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 4 }}>
          <Avatar name="Echo" size="sm" />
          <div style={{ background: 'var(--surface-2)', borderRadius: 18, padding: '12px 14px', display: 'flex', gap: 4 }}>
            {[0, 0.2, 0.4].map((d, i) => (
              <span key={i} style={{ width: 7, height: 7, borderRadius: 4, background: 'var(--on-surface-dim)', animation: `drift 1s ease-in-out ${d}s infinite` }} />
            ))}
          </div>
        </div>

        {/* delivered/read receipts */}
        <div style={{ alignSelf: 'flex-end', maxWidth: '80%', marginTop: 8 }}>
          <div style={{ padding: '9px 14px', borderRadius: 18, borderBottomRightRadius: 6, background: 'var(--primary)', color: 'var(--on-primary)', fontSize: 14 }}>oof. you ok?</div>
          <div style={{ fontSize: 10, color: 'var(--on-surface-dim)', textAlign: 'right', marginTop: 2, display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 4 }}>
            <svg width="14" height="10" viewBox="0 0 14 10" fill="none" stroke="var(--success)" strokeWidth="2" strokeLinecap="round"><path d="M1 5l3 3 5-7M6 8l3-3"/></svg>
            read 14:32
          </div>
        </div>
      </div>

      {/* Connection-degraded banner */}
      <div style={{ padding: '6px 12px', background: 'color-mix(in oklab, var(--warn) 16%, transparent)', borderTop: '1px solid color-mix(in oklab, var(--warn) 40%, transparent)', display: 'flex', alignItems: 'center', gap: 8, fontSize: 11, color: 'var(--warn)' }}>
          <span>⚠ Reconnecting to sim · 4s</span>
          <div style={{ flex: 1, height: 3, background: 'rgba(255,255,255,0.1)', borderRadius: 2, overflow: 'hidden' }}>
            <div style={{ width: '60%', height: '100%', background: 'var(--warn)' }} />
          </div>
      </div>

      <div style={{ padding: '8px 12px 10px', borderTop: '1px solid var(--outline-2)', display: 'flex', alignItems: 'center', gap: 8, background: 'var(--glass-bg)' }}>
        <IconBtn><IconPlus /></IconBtn>
        <div className="input" style={{ flex: 1 }}>
          <input placeholder="Send a message…" defaultValue="brb sim is laggy" />
        </div>
        <IconBtn style={{ background: 'var(--primary)', color: 'var(--on-primary)' }}><IconSend /></IconBtn>
      </div>
      <NavPill />
    </div>
  );
}

// 22. Teleport offer — modal over world
function TPOfferScreen() {
  return (
    <div className="screen" style={{ background: '#000' }}>
      <StatusBar />
      <div style={{ flex: 1, position: 'relative', overflow: 'hidden' }}>
        <WorldScene />
        {/* dim scrim */}
        <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.65)', backdropFilter: 'blur(4px)' }} />

        {/* TP offer card */}
        <div style={{ position: 'absolute', left: 16, right: 16, bottom: 24, zIndex: 5 }}>
          <div className="glass" style={{ padding: 16 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 12 }}>
              <Avatar name="Echo Nightshade" size="lg" online />
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 11, color: 'var(--primary)', fontWeight: 600, letterSpacing: 0.4, textTransform: 'uppercase' }}>Teleport offer</div>
                <div style={{ fontSize: 15, fontWeight: 600 }}>Echo Nightshade</div>
                <div style={{ fontSize: 11, color: 'var(--on-surface-dim)' }}>"come check this out!! 💖"</div>
              </div>
            </div>

            <div className="card" style={{ padding: 12, display: 'flex', alignItems: 'center', gap: 10, marginBottom: 12 }}>
              <div style={{
                width: 48, height: 48, borderRadius: 12, flexShrink: 0,
                background: 'linear-gradient(135deg,#1d4060,#4a7ba8 60%,#c8b88a)',
                position: 'relative', overflow: 'hidden',
              }}>
                <div style={{ position: 'absolute', right: 4, top: 4, width: 12, height: 12, borderRadius: '50%', background: 'radial-gradient(circle, #FFE7B0, #FFB958)' }} />
              </div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 13, fontWeight: 600 }}>Crystal Coast — Beach Pavilion</div>
                <div className="coord">186, 92, 24 · PG · 14 here</div>
              </div>
            </div>

            <div style={{ display: 'flex', gap: 8 }}>
              <button className="btn ghost" style={{ flex: 1 }}>Decline</button>
              <button className="btn" style={{ flex: 2 }}>
                Accept & teleport
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.4"><path d="M5 12h14M13 6l6 6-6 6"/></svg>
              </button>
            </div>
            <div style={{ textAlign: 'center', fontSize: 10, color: 'var(--on-surface-dim)', marginTop: 8 }}>Auto-decline in 58s</div>
          </div>
        </div>
      </div>
      <NavPill />
    </div>
  );
}

// 23. World — low bandwidth / loading
function LowBandwidthScreen() {
  return (
    <div className="screen" style={{ background: '#000' }}>
      <StatusBar />
      <div style={{ flex: 1, position: 'relative', overflow: 'hidden', background: '#0c1622' }}>
        {/* skeletal world — low LOD */}
        <div style={{
          position: 'absolute', inset: 0,
          background: `
            linear-gradient(180deg, #0a1830 0%, #1a2840 50%, #2a3850 100%)
          `,
        }} />
        {/* untextured prims */}
        <div style={{ position: 'absolute', left: '20%', bottom: '30%', width: 60, height: 90, background: '#444', border: '1px solid #666' }} />
        <div style={{ position: 'absolute', left: '50%', bottom: '28%', width: 80, height: 110, background: '#555', border: '1px solid #777' }} />
        <div style={{ position: 'absolute', left: '70%', bottom: '32%', width: 50, height: 70, background: '#444', border: '1px solid #666' }} />
        {/* generic avatar shape (ruth) */}
        <div style={{ position: 'absolute', left: '45%', bottom: '15%', width: 36, height: 90 }}>
          <div style={{ width: 24, height: 24, borderRadius: '50%', background: '#888', margin: '0 auto' }} />
          <div style={{ width: 36, height: 60, background: '#666', borderRadius: '12px 12px 6px 6px', marginTop: 4 }} />
        </div>

        {/* Top status — degraded */}
        <div style={{ position: 'absolute', top: 12, left: 12, right: 12, display: 'flex', gap: 6, zIndex: 3, flexWrap: 'wrap' }}>
          <div className="hud-tag" style={{ flex: 1, minWidth: 140 }}>
            <span style={{ color: 'var(--warn)' }}>●</span> Crystal Coast <span style={{ opacity: 0.6 }}>· 186, 92, 24</span>
          </div>
          <div className="hud-tag" style={{ color: 'var(--error)' }}>12fps</div>
          <div className="hud-tag" style={{ color: 'var(--warn)' }}>▼ 38kbps</div>
        </div>

        {/* Loading status panel */}
        <div style={{ position: 'absolute', left: 16, right: 16, top: 60, zIndex: 4 }}>
          <div className="glass" style={{ padding: 14 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 10 }}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="var(--warn)" strokeWidth="2"><path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83"><animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="2s" repeatCount="indefinite"/></path></svg>
              <div style={{ fontSize: 13, fontWeight: 600 }}>Slow connection</div>
              <div className="chip warn" style={{ marginLeft: 'auto', fontSize: 10 }}>Auto-low</div>
            </div>
            {[
              { l: 'Region', v: 92 },
              { l: 'Textures', v: 38 },
              { l: 'Mesh', v: 64 },
              { l: 'Avatars', v: 22 },
            ].map(r => (
              <div key={r.l} style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 4, fontSize: 11 }}>
                <span style={{ width: 60, color: 'var(--on-surface-dim)' }}>{r.l}</span>
                <div style={{ flex: 1, height: 4, background: 'rgba(255,255,255,0.08)', borderRadius: 2, overflow: 'hidden' }}>
                  <div style={{ width: `${r.v}%`, height: '100%', background: r.v > 80 ? 'var(--success)' : r.v > 50 ? 'var(--warn)' : 'var(--error)' }} />
                </div>
                <span className="coord" style={{ fontSize: 10, width: 28, textAlign: 'right' }}>{r.v}%</span>
              </div>
            ))}
            <div style={{ display: 'flex', gap: 6, marginTop: 12 }}>
              <button className="btn ghost" style={{ flex: 1, height: 32, fontSize: 11 }}>Pause stream</button>
              <button className="btn tonal" style={{ flex: 1, height: 32, fontSize: 11 }}>Text-only</button>
            </div>
          </div>
        </div>

        {/* Action stack greyed */}
        <div style={{ position: 'absolute', right: 16, bottom: 16, display: 'flex', flexDirection: 'column', gap: 8, zIndex: 4, opacity: 0.5 }}>
          {['fly','sit','cam','hud'].map(l => (
            <div key={l} style={{ width: 52, height: 52, borderRadius: 18, background: 'rgba(0,0,0,0.55)', border: '1px solid var(--glass-stroke)' }} />
          ))}
        </div>
      </div>
      <NavPill />
    </div>
  );
}

// 24. Toast / alert stack
function NotificationsToastScreen() {
  return (
    <div className="screen">
      <StatusBar />
      <div style={{ flex: 1, position: 'relative', overflow: 'hidden' }}>
        <WorldScene />
        {/* stack of incoming notifs */}
        <div style={{ position: 'absolute', left: 12, right: 12, top: 56, display: 'flex', flexDirection: 'column', gap: 8, zIndex: 5 }}>
          {/* IM toast */}
          <div className="glass" style={{ padding: 12, display: 'flex', alignItems: 'center', gap: 10 }}>
            <Avatar name="Mira Ashford" size="sm" online />
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 12, fontWeight: 600 }}>Mira Ashford</div>
              <div style={{ fontSize: 11, color: 'var(--on-surface-dim)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>sent you the mesh body fix script — should solve…</div>
            </div>
            <div className="chip primary" style={{ fontSize: 10 }}>IM</div>
          </div>

          {/* Friend request */}
          <div className="glass" style={{ padding: 12 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 8 }}>
              <Avatar name="Saffron Vale" size="sm" />
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 12, fontWeight: 600 }}>Saffron Vale</div>
                <div style={{ fontSize: 11, color: 'var(--on-surface-dim)' }}>wants to be friends</div>
              </div>
            </div>
            <div style={{ display: 'flex', gap: 6 }}>
              <button className="btn ghost" style={{ flex: 1, height: 30, fontSize: 11 }}>Decline</button>
              <button className="btn" style={{ flex: 1, height: 30, fontSize: 11 }}>Accept</button>
            </div>
          </div>

          {/* Money received */}
          <div className="glass" style={{ padding: 12, display: 'flex', alignItems: 'center', gap: 10, borderColor: 'color-mix(in oklab, var(--success) 35%, transparent)' }}>
            <div style={{ width: 32, height: 32, borderRadius: 10, background: 'color-mix(in oklab, var(--success) 25%, transparent)', color: 'var(--success)', display: 'grid', placeItems: 'center', fontWeight: 700 }}>L$</div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 12, fontWeight: 600 }}>You received <span style={{ color: 'var(--success)' }}>L$ 2,500</span></div>
              <div style={{ fontSize: 11, color: 'var(--on-surface-dim)' }}>from Pixel.Dust Designs · "thanks for the gig 💜"</div>
            </div>
          </div>

          {/* System / region restart */}
          <div className="glass" style={{ padding: 12, display: 'flex', alignItems: 'center', gap: 10, borderColor: 'color-mix(in oklab, var(--warn) 35%, transparent)' }}>
            <div style={{ width: 32, height: 32, borderRadius: 10, background: 'color-mix(in oklab, var(--warn) 25%, transparent)', color: 'var(--warn)', display: 'grid', placeItems: 'center', fontSize: 16 }}>⚠</div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 12, fontWeight: 600 }}>Region restart in 5:00</div>
              <div style={{ fontSize: 11, color: 'var(--on-surface-dim)' }}>Crystal Coast will go offline. Save your work.</div>
            </div>
          </div>

          {/* Group chat ping */}
          <div className="glass" style={{ padding: 12, display: 'flex', alignItems: 'center', gap: 10 }}>
            <div style={{ width: 32, height: 32, borderRadius: 8, background: 'var(--surface-2)', color: 'var(--primary)', display: 'grid', placeItems: 'center', fontWeight: 700, fontSize: 14 }}>T</div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 12, fontWeight: 600 }}>Tinkerers Guild</div>
              <div style={{ fontSize: 11, color: 'var(--on-surface-dim)' }}>3 new messages · @lyra mentioned</div>
            </div>
            <div style={{ minWidth: 22, height: 22, borderRadius: 11, background: 'var(--primary)', color: 'var(--on-primary)', fontSize: 11, fontWeight: 700, display: 'grid', placeItems: 'center', padding: '0 7px' }}>3</div>
          </div>
        </div>

        {/* Bottom dismiss bar */}
        <div style={{ position: 'absolute', left: 12, right: 12, bottom: 16, display: 'flex', justifyContent: 'center', zIndex: 5 }}>
          <button className="btn tonal" style={{ height: 36, fontSize: 12 }}>Clear all (5)</button>
        </div>
      </div>
      <NavPill />
    </div>
  );
}

Object.assign(window, {
  LoginErrorScreen, ChatStatesScreen, TPOfferScreen, LowBandwidthScreen, NotificationsToastScreen,
});
