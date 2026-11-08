// Screens 5: Profile, Streaming Media, Texture preview, IM list

function ProfileScreen() {
  return (
    <div className="screen">
      <StatusBar />
      <div style={{ position: 'relative' }}>
        <div style={{ height: 120, background: 'linear-gradient(135deg, var(--primary), var(--secondary, #8C7CFF))', position: 'relative' }}>
          <div style={{ position: 'absolute', top: 12, left: 12 }}><IconBtn style={{ background: 'rgba(0,0,0,0.4)', color: '#fff' }}><IconBack /></IconBtn></div>
          <div style={{ position: 'absolute', top: 12, right: 12 }}><IconBtn style={{ background: 'rgba(0,0,0,0.4)', color: '#fff' }}><IconMore /></IconBtn></div>
        </div>
        <div style={{ padding: '0 16px', marginTop: -48 }}>
          <Avatar name="Echo Nightshade" size="xxl" online style={{ border: '4px solid var(--bg)' }} />
        </div>
      </div>
      <div style={{ padding: '12px 16px', flex: 1, overflow: 'auto' }}>
        <div className="h-title" style={{ fontSize: 22 }}>Echo Nightshade</div>
        <div className="coord">display: Echo · joined 2019-04-12</div>
        <div style={{ display: 'flex', gap: 6, marginTop: 8 }}>
          <span className="chip success">● Online</span>
          <span className="chip">Crystal Coast</span>
          <span className="chip primary">Friend</span>
        </div>
        <div style={{ fontSize: 13, lineHeight: 1.5, marginTop: 12, color: 'var(--on-surface-dim)' }}>
          beach photographer · sunset chaser · co-founder of Crystal Coast. dm me for shoots ✨
        </div>
        <div style={{ display: 'flex', gap: 6, marginTop: 14 }}>
          <button className="btn" style={{ flex: 1 }}>Message</button>
          <button className="btn ghost" style={{ flex: 1 }}>Offer TP</button>
          <button className="btn ghost" style={{ width: 44 }}>···</button>
        </div>

        <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'var(--on-surface-dim)', margin: '20px 0 8px' }}>PICKS</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
          {['Crystal Coast','NeonRoom','Bay Sunset','Lighthouse'].map((p,i)=>(
            <div key={p} className="card" style={{ overflow: 'hidden' }}>
              <div style={{ height: 64, background: `linear-gradient(135deg, ${['#1d4060','#A73CFF','#FFB958','#7CFFD8'][i]}, transparent)` }} />
              <div style={{ padding: 8 }}><div className="h1" style={{ fontSize: 12 }}>{p}</div></div>
            </div>
          ))}
        </div>

        <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'var(--on-surface-dim)', margin: '20px 0 8px' }}>GROUPS — 12</div>
        <div style={{ display: 'flex', gap: -6 }}>
          {['Crystal Coast','Tinkerers','NeonRoom','Photo Walk','Bay City'].map((g,i)=>(
            <div key={g} style={{
              width: 36, height: 36, borderRadius: 10,
              background: 'var(--surface-2)', border: '2px solid var(--bg)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontSize: 13, fontWeight: 700, color: 'var(--primary)',
              marginLeft: i ? -6 : 0,
            }}>{g.charAt(0)}</div>
          ))}
        </div>
      </div>
      <NavPill />
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function MediaScreen() {
  return (
    <div className="screen">
      <StatusBar />
      <TopBar title="Parcel media" leading={<IconBtn><IconBack /></IconBtn>} trailing={<IconBtn><IconMore /></IconBtn>} />

      <div style={{ padding: '0 16px' }}>
        <div className="card" style={{ overflow: 'hidden' }}>
          <div style={{ height: 200, background: 'linear-gradient(135deg,#A73CFF,#FF3D9E,#FFB958)', position: 'relative' }}>
            <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <div style={{ width: 64, height: 64, borderRadius: 32, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <svg width="24" height="24" viewBox="0 0 24 24" fill="#fff"><path d="M8 5v14l11-7z"/></svg>
              </div>
            </div>
            <div className="hud-tag" style={{ position: 'absolute', top: 10, left: 10, background: 'rgba(0,0,0,0.55)', color: '#fff' }}>● LIVE · STREAM</div>
          </div>
          <div style={{ padding: 14 }}>
            <div className="h-title" style={{ fontSize: 16 }}>Aria Sinclair · Sunset DJ Set</div>
            <div className="coord" style={{ marginTop: 2 }}>shoutcast · 192 kbps · 32 listeners</div>
            <div style={{ display: 'flex', gap: 6, marginTop: 10 }}>
              <button className="btn tonal" style={{ flex: 1, height: 36, fontSize: 12 }}>⏮</button>
              <button className="btn" style={{ flex: 2, height: 36 }}>Pause</button>
              <button className="btn tonal" style={{ flex: 1, height: 36, fontSize: 12 }}>⏭</button>
            </div>
            <div style={{ marginTop: 12, height: 4, background: 'var(--surface-3)', borderRadius: 2 }}>
              <div style={{ width: '34%', height: '100%', background: 'var(--primary)', borderRadius: 2 }} />
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 4 }}>
              <span className="coord" style={{ fontSize: 10 }}>0:42:18</span>
              <span className="coord" style={{ fontSize: 10 }}>2:04:00</span>
            </div>
          </div>
        </div>
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '16px 16px' }}>
        <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'var(--on-surface-dim)', marginBottom: 8 }}>QUEUE</div>
        {['Crystal Coast Beach Radio','Bay City Lounge','NeonRoom DnB'].map((s,i) => (
          <div key={s} className="row" style={{ background: 'var(--surface)', borderRadius: 'var(--r-md)', marginBottom: 6, border: '1px solid var(--outline-2)' }}>
            <div style={{ width: 36, height: 36, borderRadius: 8, background: `linear-gradient(135deg, ${['#1d4060','#FFB958','#A73CFF'][i]}, transparent)` }} />
            <div className="grow"><div className="h1" style={{ fontSize: 13 }}>{s}</div><div className="h2">256 kbps · {[14,8,32][i]} listeners</div></div>
            <IconBtn><IconMore /></IconBtn>
          </div>
        ))}
      </div>
      <NavPill />
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function TextureScreen() {
  return (
    <div className="screen">
      <StatusBar />
      <TopBar title="Texture preview" subtitle="hair_recolor.dds" leading={<IconBtn><IconBack /></IconBtn>} trailing={<IconBtn><IconMore /></IconBtn>} />

      <div style={{ padding: '0 16px', flex: 1, display: 'flex', flexDirection: 'column' }}>
        <div className="card" style={{ aspectRatio: '1', position: 'relative', overflow: 'hidden', background: '#0a0c14' }}>
          {/* checker bg */}
          <div style={{ position: 'absolute', inset: 0, backgroundImage: 'linear-gradient(45deg, #1a1d24 25%, transparent 25%), linear-gradient(-45deg, #1a1d24 25%, transparent 25%), linear-gradient(45deg, transparent 75%, #1a1d24 75%), linear-gradient(-45deg, transparent 75%, #1a1d24 75%)', backgroundSize: '20px 20px', backgroundPosition: '0 0, 0 10px, 10px -10px, 10px 0' }} />
          {/* fake texture */}
          <div style={{ position: 'absolute', inset: 24, background: 'conic-gradient(from 90deg at 50% 50%, #FF8FB1, #FFD56D, #7CFFD8, #6DE8FF, #8C7CFF, #FF8FB1)', borderRadius: 16, opacity: 0.85 }} />
          <div style={{ position: 'absolute', top: 8, right: 8 }} className="hud-tag">512×512</div>
        </div>

        <div className="card" style={{ marginTop: 12, padding: 12 }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
            {[
              ['UUID', '8a3f…b21c'],
              ['Format', 'JPEG2000'],
              ['Size', '84.2 KB'],
              ['Owner', 'Lyra Resident'],
              ['Permissions', 'C M T'],
              ['Created', '2026-04-22'],
            ].map(([k,v])=>(
              <div key={k}>
                <div style={{ fontSize: 9, fontWeight: 700, letterSpacing: 1, color: 'var(--on-surface-dim)' }}>{k.toUpperCase()}</div>
                <div style={{ fontSize: 12, marginTop: 2, fontFamily: k==='UUID'?'var(--font-mono)':'inherit' }}>{v}</div>
              </div>
            ))}
          </div>
        </div>

        <div style={{ display: 'flex', gap: 6, marginTop: 12 }}>
          <button className="btn" style={{ flex: 1 }}>Apply to selection</button>
          <button className="btn ghost" style={{ flex: 1 }}>Save</button>
        </div>
      </div>
      <NavPill />
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function IMListScreen() {
  // a more compact "all conversations" sub-view; reuse home but minimal
  return null;
}

Object.assign(window, { ProfileScreen, MediaScreen, TextureScreen });
