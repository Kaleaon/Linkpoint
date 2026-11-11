// Screens 4: Object list, Voice, Settings, Notecard, Grid management

function ObjectListScreen() {
  const objs = [
    { n: 'Aurora dress · midnight', owner: 'Lyra Resident', kind: 'WORN', dist: '0m', perms: 'C M T' },
    { n: 'beach sunset HUD', owner: 'Lyra Resident', kind: 'HUD', dist: '0m', perms: 'C — T' },
    { n: 'Sunrise Lounger', owner: 'Bay City Boutique', kind: 'STATIC', dist: '4m', perms: '— — T' },
    { n: 'tip jar v2', owner: 'Voss Vale', kind: 'SCRIPTED', dist: '6m', perms: '— — —', script: true },
    { n: 'Floating Lantern', owner: 'Echo Nightshade', kind: 'PHYSICAL', dist: '8m', perms: 'C M T' },
    { n: 'Beach Towel', owner: 'Bay City Boutique', kind: 'STATIC', dist: '12m' },
    { n: 'Particle Emitter', owner: 'Aria Sinclair', kind: 'SCRIPTED', dist: '14m', script: true },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="Nearby objects"
        subtitle="38 objects · 25m radius"
        leading={<IconBtn><IconBack /></IconBtn>}
        trailing={<><IconBtn><IconSearch /></IconBtn><IconBtn><IconMore /></IconBtn></>}
      />

      <div style={{ padding: '0 16px 8px', display: 'flex', gap: 6, overflow: 'auto' }}>
        {['All','Mine','Scripted','Physical','HUDs'].map((t,i)=>(
          <span key={t} className={`chip ${i===0?'primary':''}`} style={{ whiteSpace: 'nowrap' }}>{t}</span>
        ))}
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '0 12px 16px' }}>
        {objs.map((o, i) => (
          <div key={i} className="card" style={{ padding: 12, marginBottom: 6, display: 'flex', gap: 10, alignItems: 'center' }}>
            <div style={{
              width: 8, alignSelf: 'stretch', borderRadius: 4,
              background: o.script ? 'var(--tertiary, #7CFFD8)' : o.kind === 'HUD' ? 'var(--primary)' : 'var(--outline)',
            }} />
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ display: 'flex', gap: 6, alignItems: 'center' }}>
                <div className="h1" style={{ flex: 1 }}>{o.n}</div>
                <span className="coord" style={{ fontSize: 10 }}>{o.dist}</span>
              </div>
              <div className="h2">by {o.owner}</div>
              <div style={{ display: 'flex', gap: 6, marginTop: 4 }}>
                <span className="chip" style={{ fontSize: 9, padding: '2px 6px' }}>{o.kind}</span>
                {o.perms && <span className="coord" style={{ fontSize: 9 }}>{o.perms}</span>}
                {o.script && <span className="chip success" style={{ fontSize: 9, padding: '2px 6px' }}>SCRIPT</span>}
              </div>
            </div>
            <IconBtn><IconMore /></IconBtn>
          </div>
        ))}
      </div>
      <NavPill />
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function VoiceScreen() {
  const speakers = [
    { n: 'Echo Nightshade', level: 0.85, talking: true },
    { n: 'Mira Ashford', level: 0.0, muted: false },
    { n: 'Voss Vale', level: 0.45, talking: true },
    { n: 'Aria Sinclair', level: 0.0, muted: true },
    { n: 'You', level: 0.65, you: true, talking: true },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="Voice"
        subtitle="Spatial · Bay City — Plum"
        leading={<IconBtn><IconBack /></IconBtn>}
      />

      <div style={{ padding: '0 16px' }}>
        <div className="glass" style={{ padding: 16, textAlign: 'center' }}>
          <div className="coord" style={{ marginBottom: 4 }}>CHANNEL</div>
          <div className="h-title" style={{ fontSize: 22 }}>Local Spatial</div>
          <div style={{ fontSize: 12, color: 'var(--on-surface-dim)', marginTop: 4 }}>5 voices nearby · 20m</div>

          {/* visualizer */}
          <div style={{ display: 'flex', justifyContent: 'center', gap: 3, marginTop: 16, height: 40, alignItems: 'center' }}>
            {Array.from({length:24}).map((_,i)=>{
              const h = 6 + Math.abs(Math.sin(i*0.7))*30 + (i%3===0?10:0);
              return <div key={i} style={{ width: 4, height: h, background: i<14 ? 'var(--primary)' : 'var(--outline)', borderRadius: 2 }} />;
            })}
          </div>
        </div>
      </div>

      <div style={{ padding: '16px 16px 8px' }}>
        <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'var(--on-surface-dim)', marginBottom: 6 }}>SPEAKERS</div>
      </div>
      <div style={{ flex: 1, overflow: 'auto', padding: '0 12px' }}>
        {speakers.map((s, i) => (
          <div key={i} className="row" style={{
            background: s.talking ? 'color-mix(in oklab, var(--primary) 12%, var(--surface))' : 'var(--surface)',
            border: `1px solid ${s.talking ? 'var(--primary)' : 'var(--outline-2)'}`,
            borderRadius: 'var(--r-md)', marginBottom: 6,
          }}>
            <Avatar name={s.n} online={!s.muted} />
            <div className="grow">
              <div className="h1">{s.n} {s.you && <span className="chip primary" style={{ fontSize: 9, padding: '2px 6px', marginLeft: 6 }}>YOU</span>}</div>
              <div style={{ height: 4, marginTop: 4, background: 'var(--surface-3)', borderRadius: 2, overflow: 'hidden' }}>
                <div style={{ width: `${s.level*100}%`, height: '100%', background: s.muted ? 'var(--error)' : 'var(--primary)', transition: 'width 0.2s' }} />
              </div>
            </div>
            {s.muted ? (
              <div style={{ color: 'var(--error)' }}>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><line x1="1" y1="1" x2="23" y2="23"/><path d="M9 9v3a3 3 0 005.12 2.12M15 9.34V4a3 3 0 00-5.94-.6M17 16.95A7 7 0 015 12v-2m14 0v2a7 7 0 01-.11 1.23M12 19v4M8 23h8"/></svg>
              </div>
            ) : (
              <div style={{ color: s.talking ? 'var(--primary)' : 'var(--on-surface-dim)' }}>
                <IconMic />
              </div>
            )}
          </div>
        ))}
      </div>

      {/* call bar */}
      <div style={{ padding: 16, display: 'flex', justifyContent: 'space-around', alignItems: 'center', borderTop: '1px solid var(--outline-2)' }}>
        <button style={{ width: 52, height: 52, borderRadius: 26, background: 'var(--surface-2)', border: '1px solid var(--outline-2)', color: 'var(--on-surface)' }}>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M11 5L6 9H2v6h4l5 4V5z"/></svg>
        </button>
        <button style={{ width: 64, height: 64, borderRadius: 32, background: 'var(--primary)', color: 'var(--on-primary)', border: 'none', boxShadow: 'var(--shadow-2)' }}>
          <IconMic />
        </button>
        <button style={{ width: 52, height: 52, borderRadius: 26, background: 'var(--error)', border: 'none', color: '#fff' }}>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M21 15.46l-5.27-.61c-.61-.07-1.21.14-1.64.57l-3.81 3.82a15.49 15.49 0 01-6.59-6.59l3.83-3.84c.43-.43.64-1.03.57-1.64L7.49 2C7.39 1 6.6.5 5.7.5H3.04c-.92 0-1.66.74-1.62 1.66.51 8.28 7.16 14.93 15.43 15.43.92.04 1.66-.7 1.66-1.62v-2.66c.01-.9-.5-1.69-1.51-1.85z" transform="rotate(135 12 12)"/></svg>
        </button>
      </div>
      <NavPill />
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function SettingsScreen() {
  const sections = [
    { title: 'Account', items: [
      { n: 'Lyra Resident', sub: 'Second Life · Agni', kind: 'header' },
      { n: 'Display name', val: 'Lyra ✦' },
      { n: 'Profile picks', val: '7' },
      { n: 'Manage accounts', val: '3 saved' },
      { n: 'Manage grids', val: '2 grids' },
    ]},
    { title: 'Graphics', items: [
      { n: 'Quality preset', val: 'Balanced' },
      { n: 'Draw distance', val: '128 m', slider: 0.55 },
      { n: 'Particles', val: 'Medium' },
      { n: 'Shadows', toggle: true },
      { n: 'Mesh detail', val: 'High' },
    ]},
    { title: 'Network', items: [
      { n: 'Bandwidth limit', val: '1500 kbps' },
      { n: 'Cellular fallback', toggle: true },
      { n: 'Asset cache', val: '412 / 2 GB' },
    ]},
    { title: 'Privacy & safety', items: [
      { n: 'Online status visible', toggle: true },
      { n: 'Block list', val: '12 residents' },
      { n: 'Auto-decline TPs from strangers', toggle: false },
    ]},
    { title: 'Background service', items: [
      { n: 'Stay connected on lock', toggle: true },
      { n: 'Push: IMs', toggle: true },
      { n: 'Push: friends online', toggle: false },
    ]},
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="Settings"
        leading={<IconBtn><IconBack /></IconBtn>}
        trailing={<IconBtn><IconSearch /></IconBtn>}
      />

      <div style={{ flex: 1, overflow: 'auto', padding: '0 16px 16px' }}>
        {sections.map((sec, si) => (
          <div key={si} style={{ marginBottom: 18 }}>
            <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'var(--on-surface-dim)', margin: '6px 4px 8px' }}>{sec.title.toUpperCase()}</div>
            <div className="card" style={{ overflow: 'hidden' }}>
              {sec.items.map((it, i) => (
                <React.Fragment key={i}>
                  {it.kind === 'header' ? (
                    <div className="row" style={{ background: 'var(--surface-3)' }}>
                      <Avatar name={it.n} />
                      <div className="grow">
                        <div className="h1">{it.n}</div>
                        <div className="h2">{it.sub}</div>
                      </div>
                      <button className="btn ghost" style={{ height: 30, fontSize: 11 }}>Switch</button>
                    </div>
                  ) : (
                    <div className="row">
                      <div className="grow"><div className="h1" style={{ fontWeight: 500 }}>{it.n}</div></div>
                      {it.toggle !== undefined && <Toggle on={it.toggle} />}
                      {it.val && <div style={{ fontSize: 12, color: 'var(--on-surface-dim)' }}>{it.val} ›</div>}
                      {it.slider !== undefined && (
                        <div style={{ width: 80, height: 4, background: 'var(--surface-3)', borderRadius: 2, position: 'relative' }}>
                          <div style={{ width: `${it.slider*100}%`, height: '100%', background: 'var(--primary)', borderRadius: 2 }} />
                          <div style={{ position: 'absolute', left: `${it.slider*100}%`, top: -6, width: 16, height: 16, borderRadius: 8, background: 'var(--primary)', transform: 'translateX(-8px)' }} />
                        </div>
                      )}
                    </div>
                  )}
                  {i < sec.items.length - 1 && <div className="divider" style={{ marginLeft: 16 }} />}
                </React.Fragment>
              ))}
            </div>
          </div>
        ))}

        <div style={{ fontSize: 10, color: 'var(--on-surface-dim)', textAlign: 'center', padding: 8 }}>
          Linkpoint v2.0 · build 4831 · Apache 2.0
        </div>
      </div>
      <NavPill />
    </div>
  );
}

function Toggle({ on }) {
  return (
    <div style={{
      width: 38, height: 22, borderRadius: 11,
      background: on ? 'var(--primary)' : 'var(--surface-3)',
      border: `1px solid ${on ? 'var(--primary)' : 'var(--outline-2)'}`,
      position: 'relative', transition: 'all 0.2s',
    }}>
      <div style={{
        position: 'absolute', top: 2, left: on ? 18 : 2,
        width: 16, height: 16, borderRadius: 8,
        background: on ? 'var(--on-primary)' : 'var(--on-surface-dim)',
        transition: 'left 0.2s',
      }} />
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function NotecardScreen() {
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="Crystal Coast Welcome"
        subtitle="Notecard · last edit 2d"
        leading={<IconBtn><IconBack /></IconBtn>}
        trailing={<><IconBtn><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M12 2v6m0 8v6M5 5l4 4m6 6l4 4M2 12h6m8 0h6M5 19l4-4m6-6l4-4"/></svg></IconBtn><IconBtn><IconMore /></IconBtn></>}
      />

      <div style={{ flex: 1, overflow: 'auto', padding: '0 20px 16px' }}>
        <div className="glass" style={{ padding: 14, marginBottom: 16, display: 'flex', alignItems: 'center', gap: 10 }}>
          <div style={{
            width: 36, height: 36, borderRadius: 8, flexShrink: 0,
            background: 'var(--surface-2)', display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: 'var(--primary)', fontSize: 9, fontWeight: 700, letterSpacing: 1,
          }}>NTC</div>
          <div style={{ flex: 1 }}>
            <div className="h1" style={{ fontSize: 12 }}>by Echo Nightshade</div>
            <div className="coord" style={{ fontSize: 10 }}>permissions: copy · transfer</div>
          </div>
          <span className="chip success">RECEIVED</span>
        </div>

        <div className="h-title" style={{ fontSize: 24, marginBottom: 10 }}>Welcome to Crystal Coast 🌊</div>
        <div style={{ fontSize: 14, lineHeight: 1.6, color: 'var(--on-surface-dim)' }}>
          A small beach community founded in 2019. We host weekly DJ sets, photo walks, and meditation circles.
          <br/><br/>
          <strong style={{ color: 'var(--on-surface)' }}>Rules</strong><br/>
          • PG zone — keep it friendly<br/>
          • No flying inside the lighthouse<br/>
          • Tip jars support our region tier<br/>
          <br/>
          <strong style={{ color: 'var(--on-surface)' }}>Useful SLURLs</strong>
        </div>

        <div className="card" style={{ padding: 12, marginTop: 10, fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--primary)', textDecoration: 'underline' }}>
          maps.secondlife.com/secondlife/Crystal Coast/186/92/24
        </div>
        <div className="card" style={{ padding: 12, marginTop: 6, fontFamily: 'var(--font-mono)', fontSize: 11, color: 'var(--primary)', textDecoration: 'underline' }}>
          maps.secondlife.com/secondlife/Crystal Coast/210/180/28
        </div>

        <div style={{ display: 'flex', gap: 6, marginTop: 16 }}>
          <button className="btn" style={{ flex: 1 }}>Save to inventory</button>
          <button className="btn ghost" style={{ flex: 1 }}>Share</button>
        </div>
      </div>
      <NavPill />
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function GridManagementScreen() {
  const grids = [
    { n: 'Second Life · Agni', host: 'login.agni.lindenlab.com', status: 'connected', primary: true },
    { n: 'Second Life · Aditi (beta)', host: 'login.aditi.lindenlab.com', status: 'available' },
    { n: 'OSGrid', host: 'login.osgrid.org', status: 'available' },
    { n: 'Wolf Territories', host: 'wolf.grid.io', status: 'offline' },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="Manage grids"
        subtitle="4 grids configured"
        leading={<IconBtn><IconBack /></IconBtn>}
        trailing={<IconBtn><IconPlus /></IconBtn>}
      />

      <div style={{ flex: 1, overflow: 'auto', padding: '0 16px 16px' }}>
        {grids.map((g, i) => (
          <div key={i} className="card" style={{ padding: 14, marginBottom: 8 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <div style={{
                width: 40, height: 40, borderRadius: 10,
                background: g.primary ? 'var(--primary)' : 'var(--surface-2)',
                color: g.primary ? 'var(--on-primary)' : 'var(--primary)',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                fontWeight: 700,
              }}>{g.n.charAt(0)}</div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div className="h1">{g.n}</div>
                <div className="coord" style={{ fontSize: 10 }}>{g.host}</div>
              </div>
              {g.status === 'connected' && <span className="chip success">● LIVE</span>}
              {g.status === 'available' && <span className="chip">●</span>}
              {g.status === 'offline' && <span className="chip warn">offline</span>}
            </div>
            {g.primary && (
              <div style={{ marginTop: 10, padding: 10, background: 'var(--surface-3)', borderRadius: 10, fontSize: 11, color: 'var(--on-surface-dim)' }}>
                3 accounts · 2 currently with saved password
              </div>
            )}
          </div>
        ))}
      </div>
      <NavPill />
    </div>
  );
}

Object.assign(window, { ObjectListScreen, VoiceScreen, SettingsScreen, NotecardScreen, GridManagementScreen });
