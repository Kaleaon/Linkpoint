// Screens 2: World view (with HUDs), Map, Inventory

function WorldScreen({ hudVisible = true }) {
  return (
    <div className="screen" style={{ background: '#000' }}>
      <div style={{ position: 'absolute', inset: 0 }}>
        <WorldScene />
      </div>
      <div style={{ position: 'relative', zIndex: 2, color: '#fff' }}>
        <StatusBar />
      </div>

      {/* Region tag */}
      <div style={{ position: 'absolute', top: 48, left: 16, zIndex: 3, display: 'flex', alignItems: 'center', gap: 6 }}>
        <div className="hud-tag" style={{ background: 'rgba(0,0,0,0.5)', color: '#fff' }}>
          <strong style={{ color: 'var(--primary)' }}>BAY CITY — PLUM</strong> · 128, 64, 32
        </div>
      </div>
      <div style={{ position: 'absolute', top: 48, right: 16, zIndex: 3, display: 'flex', gap: 6 }}>
        <div className="hud-tag" style={{ background: 'rgba(0,0,0,0.5)', color: '#fff' }}>FPS 58</div>
        <div className="hud-tag" style={{ background: 'rgba(0,0,0,0.5)', color: '#7CFFD8' }}>● 240ms</div>
      </div>

      {/* Avatar nameplate */}
      <div style={{ position: 'absolute', left: '50%', bottom: '32%', transform: 'translateX(-50%)', zIndex: 3 }}>
        <div className="hud-tag" style={{ background: 'rgba(0,0,0,0.55)', color: '#fff', textAlign: 'center', whiteSpace: 'nowrap' }}>
          Lyra Resident
        </div>
      </div>
      {/* Other nameplates */}
      <div style={{ position: 'absolute', left: '20%', bottom: '38%', zIndex: 3 }}>
        <div className="hud-tag" style={{ fontSize: 10, padding: '3px 6px' }}>Mira A.</div>
      </div>
      <div style={{ position: 'absolute', right: '22%', bottom: '37%', zIndex: 3 }}>
        <div className="hud-tag" style={{ fontSize: 10, padding: '3px 6px' }}>Voss</div>
      </div>

      {/* HUDs - dismissable floating glass windows */}
      {hudVisible && (
        <>
          {/* HUD: Outfit/dance */}
          <div className="glass" style={{
            position: 'absolute', top: 110, right: 12, zIndex: 4,
            width: 140, padding: 10,
            borderRadius: 14,
            background: 'rgba(10,18,36,0.65)',
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
              <span style={{ fontSize: 10, fontWeight: 700, letterSpacing: 1, color: 'var(--primary)' }}>DANCE HUD</span>
              <span style={{ fontSize: 10, color: 'var(--on-surface-dim)' }}>v3.2</span>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 4 }}>
              {['idle','sway','bounce','spin','wave','sit'].map(a => (
                <div key={a} style={{
                  padding: '5px 0', textAlign: 'center', fontSize: 10,
                  background: a === 'sway' ? 'var(--primary)' : 'rgba(255,255,255,0.08)',
                  color: a === 'sway' ? 'var(--on-primary)' : '#fff',
                  borderRadius: 6,
                }}>{a}</div>
              ))}
            </div>
          </div>

          {/* HUD: AO */}
          <div className="glass" style={{
            position: 'absolute', top: 280, right: 12, zIndex: 4,
            width: 110, padding: 8, borderRadius: 14,
            background: 'rgba(10,18,36,0.65)',
          }}>
            <div style={{ fontSize: 10, fontWeight: 700, letterSpacing: 1, color: 'var(--tertiary, #7CFFD8)', marginBottom: 6 }}>AO · ON</div>
            <div style={{ height: 4, background: 'rgba(255,255,255,0.1)', borderRadius: 2, overflow: 'hidden' }}>
              <div style={{ width: '60%', height: '100%', background: 'var(--tertiary, #7CFFD8)' }} />
            </div>
            <div className="coord" style={{ fontSize: 9, marginTop: 6 }}>female · casual</div>
          </div>

          {/* Local chat ticker */}
          <div style={{
            position: 'absolute', left: 12, bottom: 220, zIndex: 4,
            maxWidth: 280, display: 'flex', flexDirection: 'column', gap: 4,
          }}>
            {[
              { n: 'Mira A.', m: 'haha okay one sec', c: '#FFD56D' },
              { n: 'Voss', m: 'try the new physics setting', c: '#7CFFD8' },
              { n: 'You', m: 'amazing, the sunset is wild', c: 'var(--primary)' },
            ].map((m, i) => (
              <div key={i} className="hud-tag" style={{ background: 'rgba(0,0,0,0.5)', maxWidth: 'fit-content' }}>
                <span style={{ color: m.c, fontWeight: 700 }}>{m.n}:</span>
                <span style={{ color: '#fff', marginLeft: 6 }}>{m.m}</span>
              </div>
            ))}
          </div>
        </>
      )}

      {/* Bottom action dock */}
      <div style={{
        position: 'absolute', left: 12, right: 12, bottom: 70, zIndex: 5,
        display: 'flex', gap: 6, justifyContent: 'space-between',
      }}>
        <div className="glass" style={{ display: 'flex', padding: 6, gap: 2, borderRadius: 100, background: 'rgba(10,18,36,0.65)' }}>
          <button style={{ width: 40, height: 40, borderRadius: 100, border: 'none', background: 'transparent', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 01-2.83 2.83l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09a1.65 1.65 0 00-1-1.51 1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83-2.83l.06-.06a1.65 1.65 0 00.33-1.82 1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06a1.65 1.65 0 001.82.33H9a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z"/></svg>
          </button>
          <button style={{ width: 40, height: 40, borderRadius: 100, border: 'none', background: 'transparent', color: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><IconHud /></button>
          <button style={{ width: 40, height: 40, borderRadius: 100, border: 'none', background: 'transparent', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><IconCam /></button>
        </div>

        {/* Joystick */}
        <div style={{
          width: 100, height: 100, borderRadius: '50%',
          background: 'rgba(255,255,255,0.06)',
          border: '1px solid rgba(255,255,255,0.18)',
          backdropFilter: 'blur(10px)',
          position: 'relative',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          <div style={{
            width: 44, height: 44, borderRadius: '50%',
            background: 'rgba(255,255,255,0.18)',
            border: '1px solid rgba(255,255,255,0.4)',
            transform: 'translate(8px, -4px)',
          }} />
          <div style={{ position: 'absolute', top: 6, color: '#fff', fontSize: 9, fontWeight: 700, letterSpacing: 1 }}>WALK</div>
        </div>

        <div className="glass" style={{ display: 'flex', flexDirection: 'column', padding: 6, gap: 2, borderRadius: 100, background: 'rgba(10,18,36,0.65)' }}>
          <button style={{ width: 40, height: 40, borderRadius: 100, border: 'none', background: 'transparent', color: '#fff' }}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round"><path d="M5 9l7-7 7 7"/></svg>
          </button>
          <button style={{ width: 40, height: 40, borderRadius: 100, border: 'none', background: 'var(--primary)', color: 'var(--on-primary)' }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2l4 8h-3v12h-2V10H8z"/></svg>
          </button>
          <button style={{ width: 40, height: 40, borderRadius: 100, border: 'none', background: 'transparent', color: '#fff' }}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round"><path d="M19 15l-7 7-7-7"/></svg>
          </button>
        </div>
      </div>

      {/* HUD pull-tab */}
      <div style={{
        position: 'absolute', right: 0, top: '50%', transform: 'translateY(-50%)', zIndex: 6,
        background: 'var(--primary)', color: 'var(--on-primary)',
        width: 22, height: 60,
        borderRadius: '14px 0 0 14px',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        boxShadow: 'var(--shadow-2)',
      }}>
        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3"><path d="M15 6l-6 6 6 6"/></svg>
      </div>

      <div style={{ position: 'relative', zIndex: 2, marginTop: 'auto', color: '#fff' }}>
        <NavPill />
      </div>
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function MapScreen() {
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="Map"
        subtitle="Bay City — Plum (128, 64)"
        leading={<IconBtn><IconBack /></IconBtn>}
        trailing={<><IconBtn><IconSearch /></IconBtn><IconBtn><IconMore /></IconBtn></>}
      />

      <div style={{ position: 'relative', flex: 1, overflow: 'hidden', margin: '0 12px', borderRadius: 'var(--r-lg)', border: '1px solid var(--outline-2)' }}>
        {/* Map base */}
        <div style={{
          position: 'absolute', inset: 0,
          background: `
            linear-gradient(135deg, #0a1830 0%, #1d4060 100%)
          `,
        }}>
          {/* Grid */}
          <svg width="100%" height="100%" style={{ position: 'absolute', inset: 0 }}>
            <defs>
              <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
                <path d="M 40 0 L 0 0 0 40" fill="none" stroke="rgba(109,232,255,0.12)" strokeWidth="0.5"/>
              </pattern>
            </defs>
            <rect width="100%" height="100%" fill="url(#grid)" />
          </svg>

          {/* Continents */}
          <div style={{ position: 'absolute', left: '15%', top: '20%', width: '40%', height: '35%', background: 'rgba(124,255,216,0.12)', borderRadius: '40% 60% 30% 70%', filter: 'blur(2px)' }} />
          <div style={{ position: 'absolute', right: '10%', bottom: '25%', width: '35%', height: '30%', background: 'rgba(140,124,255,0.18)', borderRadius: '60% 40% 70% 30%', filter: 'blur(2px)' }} />
          <div style={{ position: 'absolute', left: '5%', bottom: '12%', width: '25%', height: '20%', background: 'rgba(109,232,255,0.18)', borderRadius: '50% 50% 30% 70%', filter: 'blur(2px)' }} />

          {/* Pins */}
          <Pin x="35%" y="32%" label="You" primary />
          <Pin x="58%" y="48%" label="Echo" />
          <Pin x="22%" y="62%" label="Mira" />
          <Pin x="72%" y="22%" label="Crystal Coast" event />
          <Pin x="48%" y="70%" label="Tinkerers" />
        </div>

        {/* Search bar overlay */}
        <div style={{ position: 'absolute', top: 12, left: 12, right: 12 }}>
          <div className="input glass" style={{ background: 'var(--glass-bg)' }}>
            <IconSearch />
            <input placeholder="Find a region or person…" />
          </div>
        </div>

        {/* Selected region card */}
        <div className="glass" style={{
          position: 'absolute', left: 12, right: 12, bottom: 12,
          padding: 14, borderRadius: 'var(--r-lg)',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <div style={{
              width: 44, height: 44, borderRadius: 12,
              background: 'linear-gradient(135deg,#1d4060,#c8b88a)',
              flexShrink: 0,
            }} />
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 14, fontWeight: 700 }}>Crystal Coast</div>
              <div className="coord">186, 92, 24 · PG · 14 here</div>
            </div>
            <span className="chip success">Open</span>
          </div>
          <div style={{ display: 'flex', gap: 6, marginTop: 12 }}>
            <button className="btn" style={{ flex: 1, height: 40 }}>
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.4"><path d="M5 12l4-4M5 12l4 4M5 12h14"/></svg>
              Teleport
            </button>
            <button className="btn ghost" style={{ flex: 1, height: 40 }}>Copy SLURL</button>
          </div>
        </div>
      </div>

      {/* Map / Mini toggle */}
      <div style={{ display: 'flex', gap: 6, padding: '12px 12px 8px', justifyContent: 'center' }}>
        <span className="chip primary">World map</span>
        <span className="chip">Minimap</span>
        <span className="chip">My picks</span>
      </div>

      <TabBar active="map" />
      <NavPill />
    </div>
  );
}

function Pin({ x, y, label, primary, event }) {
  const color = primary ? 'var(--primary)' : event ? '#FFD56D' : '#fff';
  return (
    <div style={{ position: 'absolute', left: x, top: y, transform: 'translate(-50%,-100%)', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2 }}>
      <div className="hud-tag" style={{ fontSize: 9, padding: '2px 6px', background: 'rgba(0,0,0,0.55)' }}>{label}</div>
      <div style={{
        width: primary ? 14 : 10, height: primary ? 14 : 10, borderRadius: '50%',
        background: color, border: '2px solid rgba(0,0,0,0.4)',
        boxShadow: `0 0 12px ${primary ? 'var(--primary)' : 'transparent'}`,
      }} />
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function InventoryScreen() {
  const folders = [
    { n: 'Outfits', count: 47, color: '#8C7CFF', icon: '👗' },
    { n: 'Body parts', count: 12, color: '#7CFFD8', icon: '🧍' },
    { n: 'Objects', count: 384, color: '#6DE8FF', icon: '📦' },
    { n: 'Textures', count: 1224, color: '#FFD56D', icon: '🎨' },
    { n: 'Notecards', count: 88, color: '#FF8FB1', icon: '📝' },
    { n: 'Scripts', count: 56, color: '#79D87E', icon: '⚙' },
  ];
  const recent = [
    { n: 'Aurora dress · midnight', tag: 'CLOTHING', sub: 'Crystal Coast Boutique' },
    { n: 'beach sunset HUD', tag: 'OBJECT', sub: 'from Echo Nightshade' },
    { n: 'hair_recolor.dds', tag: 'TEXTURE', sub: '512×512 · 84 KB' },
    { n: 'tip jar v2', tag: 'SCRIPT', sub: 'Voss · LSL' },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="Inventory"
        subtitle="2,318 items · 412 MB"
        trailing={<><IconBtn><IconSearch /></IconBtn><IconBtn><IconPlus /></IconBtn></>}
      />

      <div style={{ padding: '0 16px 8px' }}>
        <div className="input"><IconSearch /><input placeholder="Search inventory…" /></div>
      </div>

      <div style={{ padding: '4px 16px 8px', display: 'flex', gap: 6, overflow: 'auto' }}>
        {['All','Worn','Recent','Calling cards','Trash'].map((t,i)=>(
          <span key={t} className={`chip ${i===0?'primary':''}`} style={{ whiteSpace: 'nowrap' }}>{t}</span>
        ))}
      </div>

      <div style={{ padding: '8px 16px' }}>
        <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'var(--on-surface-dim)', marginBottom: 8 }}>FOLDERS</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
          {folders.map(f => (
            <div key={f.n} className="card" style={{ padding: 12, position: 'relative', overflow: 'hidden' }}>
              <div style={{
                width: 32, height: 32, borderRadius: 10,
                background: `${f.color}22`,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                fontSize: 18,
                border: `1px solid ${f.color}44`,
              }}>{f.icon}</div>
              <div style={{ fontSize: 13, fontWeight: 600, marginTop: 8 }}>{f.n}</div>
              <div style={{ fontSize: 11, color: 'var(--on-surface-dim)' }}>{f.count} items</div>
            </div>
          ))}
        </div>
      </div>

      <div style={{ padding: '8px 16px', flex: 1, overflow: 'auto' }}>
        <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'var(--on-surface-dim)', marginBottom: 4 }}>RECENT</div>
        {recent.map((r, i) => (
          <div key={i} className="row" style={{ paddingLeft: 0, paddingRight: 0 }}>
            <div style={{
              width: 40, height: 40, borderRadius: 10,
              background: 'var(--surface-2)', border: '1px solid var(--outline-2)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontSize: 9, fontWeight: 700, letterSpacing: 0.5,
              color: 'var(--primary)',
              flexShrink: 0,
            }}>{r.tag.slice(0,3)}</div>
            <div className="grow">
              <div className="h1">{r.n}</div>
              <div className="h2">{r.sub}</div>
            </div>
            <IconBtn><IconMore /></IconBtn>
          </div>
        ))}
      </div>

      <TabBar active="inv" />
      <NavPill />
    </div>
  );
}

Object.assign(window, { WorldScreen, MapScreen, InventoryScreen });
