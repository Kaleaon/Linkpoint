// Screens-extra: Onboarding, Wallet, Groups, Outfit composer, Build tools, Permissions, Voice deep, Mini-map, Empty states

// ============ ONBOARDING ============
function OnboardingWelcomeScreen() {
  return (
    <div className="screen">
      <StatusBar />
      <div style={{ position: 'absolute', inset: 0, overflow: 'hidden' }}>
        <div className="aurora-blob" style={{ width: 360, height: 360, left: -100, top: 60, background: 'var(--primary)', opacity: 0.32, animation: 'drift 14s ease-in-out infinite' }} />
        <div className="aurora-blob" style={{ width: 320, height: 320, right: -80, bottom: 200, background: 'var(--secondary, #8C7CFF)', opacity: 0.28, animation: 'drift 18s ease-in-out infinite' }} />
      </div>
      <div style={{ flex: 1, padding: '40px 28px 24px', position: 'relative', zIndex: 2, display: 'flex', flexDirection: 'column' }}>
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', textAlign: 'center' }}>
          <div style={{ position: 'relative', width: 200, height: 200, marginBottom: 32 }}>
            <Avatar name="Welcome" size="xxl" style={{ width: 200, height: 200, boxShadow: '0 0 60px var(--aurora-1)' }} />
          </div>
          <div className="h-title" style={{ fontSize: 32, lineHeight: 1.1, marginBottom: 12 }}>Welcome to<br/>your second world.</div>
          <div style={{ fontSize: 14, color: 'var(--on-surface-dim)', maxWidth: 300, lineHeight: 1.5 }}>
            Three steps and you're inside. Let's set up your avatar, pick a starting region, and get connected.
          </div>
        </div>
        <div style={{ display: 'flex', justifyContent: 'center', gap: 6, marginBottom: 20 }}>
          {[1,2,3].map((i, idx) => (
            <div key={i} style={{ width: idx === 0 ? 24 : 6, height: 6, borderRadius: 3, background: idx === 0 ? 'var(--primary)' : 'var(--outline-2)' }} />
          ))}
        </div>
        <button className="btn" style={{ width: '100%', height: 52 }}>Begin
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2"><path d="M5 12h14M13 6l6 6-6 6"/></svg>
        </button>
        <button className="btn ghost" style={{ width: '100%', height: 40, marginTop: 8, fontSize: 12 }}>I have an account</button>
      </div>
      <NavPill />
    </div>
  );
}

function OnboardingAvatarScreen() {
  const starters = [
    { name: 'Vex', accent: '#8C7CFF' },
    { name: 'Aria', accent: '#7CFFD8' },
    { name: 'Coil', accent: '#FF8FB1' },
    { name: 'Halo', accent: '#FFD56D' },
    { name: 'Drift', accent: '#79D87E' },
    { name: 'Zen', accent: '#39B6F0' },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="Choose a starter avatar"
        subtitle="You can change anything later"
        leading={<IconBtn><IconBack /></IconBtn>}
      />
      <div style={{ flex: 1, overflow: 'auto', padding: '0 16px 16px' }}>
        <div className="glass" style={{ padding: 20, marginBottom: 16, display: 'flex', alignItems: 'center', gap: 16 }}>
          <Avatar name="Vex" size="xl" online />
          <div style={{ flex: 1, minWidth: 0 }}>
            <div className="coord" style={{ fontSize: 10, color: 'var(--primary)' }}>SELECTED</div>
            <div className="h-title" style={{ fontSize: 22, marginTop: 2 }}>Vex</div>
            <div style={{ fontSize: 11, color: 'var(--on-surface-dim)' }}>Mesh body · Bento head · 3 outfits</div>
          </div>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 10 }}>
          {starters.map((s, i) => (
            <div key={s.name} className="card" style={{ padding: 12, textAlign: 'center', borderColor: i === 0 ? 'var(--primary)' : undefined, borderWidth: i === 0 ? 2 : 1 }}>
              <Avatar name={s.name} size="lg" style={{ margin: '0 auto 8px' }} />
              <div style={{ fontSize: 12, fontWeight: 600 }}>{s.name}</div>
            </div>
          ))}
        </div>
        <div style={{ fontSize: 11, color: 'var(--on-surface-dim)', textTransform: 'uppercase', letterSpacing: 0.4, fontWeight: 600, margin: '20px 0 10px' }}>Or</div>
        <div className="card" style={{ padding: 14, display: 'flex', alignItems: 'center', gap: 10 }}>
          <div style={{ width: 40, height: 40, borderRadius: 12, background: 'var(--surface-2)', display: 'grid', placeItems: 'center' }}>📷</div>
          <div style={{ flex: 1, fontSize: 13 }}>Upload a photo to generate</div>
          <IconBtn><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M9 18l6-6-6-6"/></svg></IconBtn>
        </div>
      </div>
      <div style={{ padding: 12, display: 'flex', gap: 8 }}>
        <div style={{ display: 'flex', justifyContent: 'center', gap: 6, alignItems: 'center', flex: 1 }}>
          <div style={{ width: 6, height: 6, borderRadius: 3, background: 'var(--outline-2)' }} />
          <div style={{ width: 24, height: 6, borderRadius: 3, background: 'var(--primary)' }} />
          <div style={{ width: 6, height: 6, borderRadius: 3, background: 'var(--outline-2)' }} />
        </div>
        <button className="btn">Continue</button>
      </div>
      <NavPill />
    </div>
  );
}

function OnboardingPermissionsScreen() {
  const perms = [
    { icon: '🎙', title: 'Microphone', sub: 'For voice chat in regions and groups', state: 'allow' },
    { icon: '📍', title: 'Location', sub: 'To suggest nearby events and friends', state: 'optional' },
    { icon: '🔔', title: 'Notifications', sub: 'IMs, friend requests, group pings', state: 'required' },
    { icon: '📷', title: 'Camera', sub: 'For avatar capture and photo posts', state: 'optional' },
    { icon: '💾', title: 'Storage', sub: 'Cache textures and meshes locally', state: 'required' },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar title="Permissions" subtitle="One tap each. We'll explain why." leading={<IconBtn><IconBack /></IconBtn>} />
      <div style={{ flex: 1, overflow: 'auto', padding: '0 12px 16px' }}>
        {perms.map((p, i) => (
          <div key={i} className="card" style={{ padding: 14, marginBottom: 8, display: 'flex', alignItems: 'center', gap: 12 }}>
            <div style={{ width: 40, height: 40, borderRadius: 12, background: 'var(--surface-2)', display: 'grid', placeItems: 'center', fontSize: 18 }}>{p.icon}</div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 13, fontWeight: 600, display: 'flex', alignItems: 'center', gap: 6 }}>
                {p.title}
                {p.state === 'required' && <span className="chip primary" style={{ fontSize: 9, padding: '2px 6px' }}>Required</span>}
              </div>
              <div style={{ fontSize: 11, color: 'var(--on-surface-dim)' }}>{p.sub}</div>
            </div>
            <button className={`btn ${p.state === 'allow' || p.state === 'required' ? '' : 'ghost'}`} style={{ height: 32, fontSize: 11, padding: '0 14px' }}>
              {p.state === 'optional' ? 'Skip' : 'Allow'}
            </button>
          </div>
        ))}
      </div>
      <div style={{ padding: 12 }}>
        <button className="btn" style={{ width: '100%' }}>Enter world</button>
      </div>
      <NavPill />
    </div>
  );
}

// ============ WALLET / L$ ============
function WalletScreen() {
  const txns = [
    { who: 'Pixel.Dust Designs', note: 'gig payment — DJ set', amt: +2500, t: 'Today 14:02' },
    { who: 'Crystal Coast Mall', note: 'mesh hair "Glitch v3"', amt: -890, t: 'Today 12:48' },
    { who: 'Aria Larsson', note: 'tip — thanks 💜', amt: -250, t: 'Yesterday' },
    { who: 'Tinkerers Guild', note: 'group dues', amt: -50, t: 'Yesterday' },
    { who: 'Marketplace', note: 'animation pack', amt: -1200, t: '2d ago' },
    { who: 'Echo Nightshade', note: 'split tab — beach event', amt: +800, t: '3d ago' },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar title="Wallet" leading={<IconBtn><IconBack /></IconBtn>} trailing={<IconBtn><IconMore /></IconBtn>} />
      <div style={{ padding: '0 16px 16px' }}>
        <div className="glass" style={{ padding: 18, position: 'relative', overflow: 'hidden' }}>
          <div style={{ fontSize: 11, color: 'var(--on-surface-dim)', textTransform: 'uppercase', letterSpacing: 1, fontWeight: 600 }}>Balance</div>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: 8, marginTop: 4 }}>
            <span style={{ fontSize: 14, color: 'var(--primary)', fontWeight: 700 }}>L$</span>
            <span className="h-title" style={{ fontSize: 36, lineHeight: 1 }}>14,820</span>
          </div>
          <div style={{ fontSize: 11, color: 'var(--on-surface-dim)', marginTop: 4 }}>≈ US$ 58.40 · cash out anytime</div>
          <div style={{ display: 'flex', gap: 8, marginTop: 16 }}>
            <button className="btn" style={{ flex: 1, height: 38, fontSize: 12 }}>Send</button>
            <button className="btn tonal" style={{ flex: 1, height: 38, fontSize: 12 }}>Request</button>
            <button className="btn ghost" style={{ flex: 1, height: 38, fontSize: 12 }}>Buy L$</button>
          </div>
        </div>
      </div>
      <div style={{ padding: '0 20px 8px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--on-surface-dim)', textTransform: 'uppercase', letterSpacing: 0.5 }}>This week</div>
        <div style={{ fontSize: 11 }}><span style={{ color: 'var(--success)' }}>+L$ 3,300</span> <span style={{ color: 'var(--on-surface-dim)' }}>· -L$ 2,390</span></div>
      </div>
      <div style={{ flex: 1, overflow: 'auto', padding: '0 8px 16px' }}>
        {txns.map((t, i) => (
          <div key={i} className="row">
            <div style={{ width: 40, height: 40, borderRadius: 12, background: t.amt > 0 ? 'color-mix(in oklab, var(--success) 22%, transparent)' : 'var(--surface-2)', color: t.amt > 0 ? 'var(--success)' : 'var(--on-surface-dim)', display: 'grid', placeItems: 'center', fontSize: 12, fontWeight: 700, flexShrink: 0 }}>{t.amt > 0 ? '↓' : '↑'}</div>
            <div className="grow">
              <div className="h1">{t.who}</div>
              <div className="h2">{t.note} · {t.t}</div>
            </div>
            <div style={{ fontFamily: 'var(--font-mono)', fontSize: 13, fontWeight: 600, color: t.amt > 0 ? 'var(--success)' : 'var(--on-surface)' }}>{t.amt > 0 ? '+' : ''}{t.amt.toLocaleString()}</div>
          </div>
        ))}
      </div>
      <NavPill />
    </div>
  );
}

// ============ GROUPS ============
function GroupsListScreen() {
  const groups = [
    { name: 'Tinkerers Guild', tag: 'TINK', role: 'Member', members: 247, online: 18, unread: 3, color: '#8C7CFF' },
    { name: 'Crystal Coast 🌊', tag: 'COAST', role: 'Officer', members: 89, online: 14, color: '#7CFFD8' },
    { name: 'Solaris Build Team', tag: 'SOLR', role: 'Owner', members: 12, online: 4, color: '#FFD56D' },
    { name: 'Mesh Body Updates', tag: 'BODY', role: 'Member', members: 1842, online: 96, unread: 22, color: '#FF8FB1' },
    { name: 'Caledon Steam', tag: 'CALE', role: 'Member', members: 612, online: 23, color: '#39B6F0' },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar title="Groups" subtitle="5 of 60 slots used" leading={<IconBtn><IconBack /></IconBtn>} trailing={<><IconBtn><IconSearch /></IconBtn><IconBtn><IconPlus /></IconBtn></>} />
      <div style={{ flex: 1, overflow: 'auto', padding: '0 8px' }}>
        {groups.map((g, i) => (
          <div key={i} className="row">
            <div style={{ width: 44, height: 44, borderRadius: 12, background: `color-mix(in oklab, ${g.color} 22%, transparent)`, color: g.color, display: 'grid', placeItems: 'center', fontWeight: 700, fontSize: 13, flexShrink: 0, fontFamily: 'var(--font-mono)' }}>{g.tag}</div>
            <div className="grow">
              <div className="h1">{g.name}</div>
              <div className="h2">{g.role} · {g.members} members · <span style={{ color: 'var(--success)' }}>{g.online} online</span></div>
            </div>
            {g.unread && <div style={{ minWidth: 22, height: 22, borderRadius: 11, background: 'var(--primary)', color: 'var(--on-primary)', fontSize: 11, fontWeight: 700, display: 'grid', placeItems: 'center', padding: '0 7px' }}>{g.unread}</div>}
          </div>
        ))}
      </div>
      <TabBar active="chat" />
      <NavPill />
    </div>
  );
}

function GroupProfileScreen() {
  return (
    <div className="screen">
      <StatusBar />
      <div style={{ flex: 1, overflow: 'auto' }}>
        <div style={{ height: 140, background: 'linear-gradient(135deg, #8C7CFF, #7CFFD8)', position: 'relative' }}>
          <button style={{ position: 'absolute', top: 12, left: 12, width: 36, height: 36, borderRadius: 18, background: 'rgba(0,0,0,0.4)', border: 'none', color: '#fff', backdropFilter: 'blur(8px)' }}><IconBack /></button>
          <button style={{ position: 'absolute', top: 12, right: 12, width: 36, height: 36, borderRadius: 18, background: 'rgba(0,0,0,0.4)', border: 'none', color: '#fff', backdropFilter: 'blur(8px)' }}><IconMore /></button>
        </div>
        <div style={{ padding: '0 16px', marginTop: -40 }}>
          <div style={{ width: 80, height: 80, borderRadius: 20, background: 'color-mix(in oklab, #8C7CFF 30%, var(--surface))', border: '3px solid var(--bg)', display: 'grid', placeItems: 'center', fontWeight: 700, fontSize: 18, color: '#8C7CFF', fontFamily: 'var(--font-mono)' }}>TINK</div>
          <div className="h-title" style={{ fontSize: 22, marginTop: 12 }}>Tinkerers Guild</div>
          <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', marginTop: 8 }}>
            <div className="chip primary">Officer</div>
            <div className="chip">247 members</div>
            <div className="chip success">18 online</div>
          </div>
          <div style={{ fontSize: 13, color: 'var(--on-surface-dim)', marginTop: 12, lineHeight: 1.5 }}>
            Builders, scripters, and mesh wranglers. Weekly hangouts every Thursday 8pm SLT at the Tinker Loft.
          </div>
          <div style={{ display: 'flex', gap: 8, marginTop: 14 }}>
            <button className="btn" style={{ flex: 1 }}>Group chat</button>
            <button className="btn ghost" style={{ flex: 1 }}>Visit HQ</button>
          </div>
        </div>
        <div style={{ padding: '20px 16px 0', display: 'flex', gap: 6 }}>
          {['Members', 'Roles', 'Notices', 'Land'].map((t, i) => (
            <div key={t} className={`chip ${i === 0 ? 'primary' : ''}`} style={{ padding: '6px 12px', fontSize: 11 }}>{t}</div>
          ))}
        </div>
        <div style={{ padding: '12px 16px 16px' }}>
          <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--on-surface-dim)', textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 8 }}>Online · 18</div>
          {[
            { name: 'Voss Halberd', role: 'Owner', online: true },
            { name: 'Mira Ashford', role: 'Officer', online: true },
            { name: 'Aria Larsson', role: 'Member · You', online: true },
            { name: 'Riven Nox', role: 'Member', online: true },
          ].map(m => (
            <div key={m.name} className="row" style={{ padding: '8px 0' }}>
              <Avatar name={m.name} online={m.online} />
              <div className="grow">
                <div className="h1">{m.name}</div>
                <div className="h2">{m.role}</div>
              </div>
              <IconBtn><IconMore /></IconBtn>
            </div>
          ))}
        </div>
      </div>
      <NavPill />
    </div>
  );
}

// ============ OUTFIT COMPOSER ============
function OutfitComposerScreen() {
  const slots = [
    { name: 'Skin', item: 'Glamorize · Porcelain v4', layer: 'body', on: true },
    { name: 'Shape', item: 'custom — Vex base', layer: 'body', on: true },
    { name: 'Eyes', item: 'IKON · Promise (jade)', layer: 'body', on: true },
    { name: 'Hair', item: 'Pixel.Dust · Glitch v3', layer: 'body', on: true },
    { name: 'Body', item: 'Maitreya Lara X', layer: 'body', on: true },
    { name: 'Top', item: 'Solaris · Ribbed crop', layer: 'cloth', on: true },
    { name: 'Pants', item: 'Solaris · Cargo wide', layer: 'cloth', on: true },
    { name: 'Shoes', item: 'Reign · Platform 02', layer: 'cloth', on: true },
    { name: 'Necklace', item: 'Kibitz · Chain link', layer: 'attach', on: true },
    { name: 'Earrings', item: '— empty', layer: 'attach', on: false },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar title="Outfit · Crystal Coast" subtitle="9 worn · saved as 'Beach club'" leading={<IconBtn><IconBack /></IconBtn>} trailing={<IconBtn><IconMore /></IconBtn>} />
      <div style={{ display: 'flex', flex: 1, minHeight: 0 }}>
        {/* Left preview */}
        <div style={{ width: 150, borderRight: '1px solid var(--outline-2)', background: 'linear-gradient(180deg, var(--bg-2), var(--bg))', position: 'relative', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <Avatar name="Vex" size="xxl" style={{ width: 120, height: 120 }} />
          <div style={{ position: 'absolute', bottom: 8, left: 8, right: 8, display: 'flex', flexDirection: 'column', gap: 4 }}>
            {['↻','⤢','✦'].map(g => <button key={g} style={{ height: 28, fontSize: 14, background: 'var(--surface-2)', border: '1px solid var(--outline-2)', color: 'var(--on-surface)', borderRadius: 8 }}>{g}</button>)}
          </div>
        </div>
        {/* Right slot list */}
        <div style={{ flex: 1, overflow: 'auto' }}>
          {['body','cloth','attach'].map(layer => (
            <div key={layer}>
              <div style={{ padding: '10px 14px 6px', fontSize: 10, fontWeight: 700, letterSpacing: 0.6, textTransform: 'uppercase', color: 'var(--primary)' }}>
                {layer === 'body' ? 'Body layers' : layer === 'cloth' ? 'Clothing' : 'Attachments · Bento'}
              </div>
              {slots.filter(s => s.layer === layer).map(s => (
                <div key={s.name} style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '8px 14px', borderBottom: '1px solid var(--outline-2)' }}>
                  <div style={{ width: 4, height: 30, borderRadius: 2, background: s.on ? 'var(--primary)' : 'var(--outline-2)' }} />
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontSize: 11, color: 'var(--on-surface-dim)', textTransform: 'uppercase', letterSpacing: 0.5, fontWeight: 600 }}>{s.name}</div>
                    <div style={{ fontSize: 12, fontWeight: 500, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', color: s.on ? 'var(--on-surface)' : 'var(--on-surface-dim)' }}>{s.item}</div>
                  </div>
                  <button style={{ width: 28, height: 28, borderRadius: 8, border: 'none', background: 'var(--surface-2)', color: 'var(--on-surface)', fontSize: 14 }}>{s.on ? '✕' : '+'}</button>
                </div>
              ))}
            </div>
          ))}
        </div>
      </div>
      <div style={{ padding: 10, display: 'flex', gap: 8, borderTop: '1px solid var(--outline-2)' }}>
        <button className="btn ghost" style={{ flex: 1, height: 38, fontSize: 12 }}>Save as new</button>
        <button className="btn" style={{ flex: 1, height: 38 }}>Wear outfit</button>
      </div>
      <NavPill />
    </div>
  );
}

// ============ BUILD TOOLS ============
function BuildToolsScreen() {
  return (
    <div className="screen" style={{ background: '#000' }}>
      <StatusBar />
      <div style={{ flex: 1, position: 'relative', overflow: 'hidden' }}>
        <WorldScene />
        {/* Selection bracket */}
        <div style={{ position: 'absolute', left: '32%', top: '38%', width: 100, height: 110, border: '1px dashed var(--primary)', boxShadow: '0 0 0 2px rgba(0,0,0,0.3)' }}>
          {['nw','ne','sw','se'].map((c, i) => (
            <div key={c} style={{
              position: 'absolute', width: 10, height: 10, background: 'var(--primary)',
              left: c.includes('w') ? -6 : 'auto', right: c.includes('e') ? -6 : 'auto',
              top: c.includes('n') ? -6 : 'auto', bottom: c.includes('s') ? -6 : 'auto',
            }} />
          ))}
          <div style={{ position: 'absolute', top: -22, left: 0, fontSize: 9, fontFamily: 'var(--font-mono)', color: 'var(--primary)', background: 'rgba(0,0,0,0.7)', padding: '2px 6px' }}>BENCH · PRIM × 12</div>
        </div>
        {/* Edit panel — bottom sheet */}
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: 0, zIndex: 5 }}>
          <div className="glass" style={{ borderRadius: '20px 20px 0 0', padding: '12px 16px 16px' }}>
            <div style={{ width: 36, height: 4, borderRadius: 2, background: 'var(--outline)', margin: '0 auto 12px', opacity: 0.4 }} />
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 12 }}>
              <div style={{ flex: 1 }}>
                <div className="h-title" style={{ fontSize: 16 }}>Crystal beach bench</div>
                <div className="coord">12 prims · 0.4 LI · owner: you</div>
              </div>
              <button className="btn ghost" style={{ height: 30, fontSize: 11, padding: '0 12px' }}>Take</button>
            </div>
            <div style={{ display: 'flex', gap: 4, marginBottom: 10 }}>
              {['Move','Rotate','Scale','Edit','Texture'].map((t, i) => (
                <div key={t} className={`chip ${i === 1 ? 'primary' : ''}`} style={{ flex: 1, justifyContent: 'center', fontSize: 10 }}>{t}</div>
              ))}
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8 }}>
              {[
                { l: 'X', v: '186.42', c: '#FF6B6B' },
                { l: 'Y', v: '92.18', c: '#7CFFD8' },
                { l: 'Z', v: '24.00', c: '#6DE8FF' },
              ].map(r => (
                <div key={r.l} className="card" style={{ padding: 8, textAlign: 'center' }}>
                  <div style={{ fontSize: 9, color: r.c, fontWeight: 700, fontFamily: 'var(--font-mono)' }}>{r.l}</div>
                  <div style={{ fontFamily: 'var(--font-mono)', fontSize: 13, fontWeight: 600 }}>{r.v}</div>
                </div>
              ))}
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8, marginTop: 8 }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '8px 12px', background: 'var(--surface-2)', borderRadius: 'var(--r-md)', fontSize: 11 }}>
                <span style={{ color: 'var(--on-surface-dim)' }}>Phantom</span>
                <span style={{ width: 28, height: 16, borderRadius: 100, background: 'var(--outline-2)', position: 'relative' }}><span style={{ position: 'absolute', left: 2, top: 2, width: 12, height: 12, borderRadius: 6, background: '#fff' }} /></span>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '8px 12px', background: 'var(--surface-2)', borderRadius: 'var(--r-md)', fontSize: 11 }}>
                <span style={{ color: 'var(--on-surface-dim)' }}>Physical</span>
                <span style={{ width: 28, height: 16, borderRadius: 100, background: 'var(--primary)', position: 'relative' }}><span style={{ position: 'absolute', right: 2, top: 2, width: 12, height: 12, borderRadius: 6, background: '#fff' }} /></span>
              </div>
            </div>
            <div style={{ display: 'flex', gap: 6, marginTop: 10 }}>
              <button className="btn ghost" style={{ flex: 1, height: 34, fontSize: 11 }}>Properties</button>
              <button className="btn ghost" style={{ flex: 1, height: 34, fontSize: 11 }}>Scripts</button>
              <button className="btn ghost" style={{ flex: 1, height: 34, fontSize: 11 }}>Content</button>
            </div>
          </div>
        </div>
      </div>
      <NavPill />
    </div>
  );
}

// ============ PERMISSION DIALOG ============
function PermissionDialogScreen() {
  return (
    <div className="screen" style={{ background: '#000' }}>
      <StatusBar />
      <div style={{ flex: 1, position: 'relative', overflow: 'hidden' }}>
        <WorldScene />
        <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.65)', backdropFilter: 'blur(4px)' }} />
        <div style={{ position: 'absolute', left: 16, right: 16, top: '50%', transform: 'translateY(-50%)', zIndex: 5 }}>
          <div className="glass" style={{ padding: 20 }}>
            <div style={{ display: 'flex', gap: 12, marginBottom: 12 }}>
              <div style={{ width: 44, height: 44, borderRadius: 12, background: 'color-mix(in oklab, var(--warn) 22%, transparent)', color: 'var(--warn)', display: 'grid', placeItems: 'center', fontSize: 22, flexShrink: 0 }}>⚠</div>
              <div>
                <div className="coord" style={{ color: 'var(--warn)', fontSize: 10 }}>PERMISSION REQUEST</div>
                <div className="h-title" style={{ fontSize: 18 }}>Object wants to debit</div>
              </div>
            </div>
            <div className="card" style={{ padding: 12, marginBottom: 12 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 8 }}>
                <div style={{ width: 28, height: 28, borderRadius: 8, background: 'var(--surface-2)', display: 'grid', placeItems: 'center', fontSize: 12 }}>◆</div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 12, fontWeight: 600 }}>Vendor: Pixel.Dust kiosk</div>
                  <div style={{ fontSize: 11, color: 'var(--on-surface-dim)' }}>by Pixel.Dust Designs</div>
                </div>
              </div>
              <div className="divider" style={{ margin: '8px 0' }} />
              <div style={{ display: 'grid', gridTemplateColumns: 'auto 1fr', gap: '6px 12px', fontSize: 12 }}>
                <span style={{ color: 'var(--on-surface-dim)' }}>Item</span><span>Glitch hair v3</span>
                <span style={{ color: 'var(--on-surface-dim)' }}>Amount</span><span style={{ color: 'var(--warn)', fontWeight: 600, fontFamily: 'var(--font-mono)' }}>L$ 890</span>
                <span style={{ color: 'var(--on-surface-dim)' }}>Recurring</span><span>No · one-time</span>
              </div>
            </div>
            <div style={{ fontSize: 11, color: 'var(--on-surface-dim)', textAlign: 'center', marginBottom: 12 }}>
              You're about to spend Linden Dollars. This object will be charged to your wallet.
            </div>
            <div style={{ display: 'flex', gap: 8 }}>
              <button className="btn ghost" style={{ flex: 1 }}>Decline</button>
              <button className="btn" style={{ flex: 1.4 }}>Pay L$ 890</button>
            </div>
            <button style={{ width: '100%', marginTop: 8, height: 30, background: 'transparent', border: 'none', color: 'var(--on-surface-dim)', fontSize: 11 }}>Mute this object</button>
          </div>
        </div>
      </div>
      <NavPill />
    </div>
  );
}

// ============ VOICE DEEP STATE ============
function VoiceDeepScreen() {
  const speakers = [
    { name: 'Echo Nightshade', vol: 0.85, talking: true, dist: 4 },
    { name: 'Aria Larsson', vol: 0.6, talking: false, dist: 12 },
    { name: 'Mira Ashford', vol: 0.0, talking: false, dist: 28, muted: true },
    { name: 'Riven Nox', vol: 0.4, talking: true, dist: 18 },
    { name: 'Voss Halberd', vol: 0.9, talking: false, dist: 6 },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar title="Voice · Crystal Coast" subtitle="Spatial · 5 in range" leading={<IconBtn><IconBack /></IconBtn>} trailing={<IconBtn><IconMore /></IconBtn>} />

      {/* Channel switcher */}
      <div style={{ padding: '0 16px 12px', display: 'flex', gap: 6, overflow: 'auto' }}>
        {[{ n: 'Local · 5', a: true }, { n: 'Tinkerers Guild · 3' }, { n: 'Crystal Coast · 12' }, { n: 'Private · Echo' }].map((c, i) => (
          <div key={i} className={`chip ${c.a ? 'primary' : ''}`} style={{ flexShrink: 0, padding: '6px 12px' }}>{c.n}</div>
        ))}
      </div>

      {/* PTT button */}
      <div style={{ padding: '0 16px 12px' }}>
        <div className="glass" style={{ padding: 16, textAlign: 'center' }}>
          <div className="coord" style={{ color: 'var(--primary)', fontSize: 10, marginBottom: 12 }}>PUSH TO TALK · HOLD</div>
          <div style={{ position: 'relative', width: 100, height: 100, margin: '0 auto' }}>
            <div style={{ position: 'absolute', inset: 0, borderRadius: '50%', background: 'var(--primary)', boxShadow: '0 0 30px var(--primary)', display: 'grid', placeItems: 'center', color: 'var(--on-primary)' }}>
              <IconMic />
            </div>
            <div style={{ position: 'absolute', inset: -8, borderRadius: '50%', border: '2px solid var(--primary)', opacity: 0.3 }} />
            <div style={{ position: 'absolute', inset: -16, borderRadius: '50%', border: '2px solid var(--primary)', opacity: 0.15 }} />
          </div>
          <div style={{ display: 'flex', justifyContent: 'center', gap: 2, marginTop: 12, alignItems: 'flex-end', height: 24 }}>
            {[6,12,18,24,16,10,14,20,22,8,12].map((h, i) => (
              <div key={i} style={{ width: 4, height: h, background: 'var(--primary)', borderRadius: 1, opacity: 0.4 + (i / 22) }} />
            ))}
          </div>
        </div>
      </div>

      <div style={{ padding: '0 20px 8px', fontSize: 11, fontWeight: 600, color: 'var(--on-surface-dim)', textTransform: 'uppercase', letterSpacing: 0.5 }}>In range</div>
      <div style={{ flex: 1, overflow: 'auto', padding: '0 8px' }}>
        {speakers.map(s => (
          <div key={s.name} className="row">
            <div style={{ position: 'relative' }}>
              <Avatar name={s.name} online />
              {s.talking && <div style={{ position: 'absolute', inset: -3, borderRadius: '50%', border: '2px solid var(--success)', animation: 'drift 1s ease-in-out infinite' }} />}
            </div>
            <div className="grow">
              <div className="h1">{s.name}</div>
              <div className="h2">{s.dist}m away · {s.muted ? 'muted by you' : (s.talking ? 'speaking' : 'listening')}</div>
            </div>
            <div style={{ width: 60, height: 4, background: 'var(--outline-2)', borderRadius: 2, marginRight: 8, overflow: 'hidden' }}>
              <div style={{ width: `${s.vol * 100}%`, height: '100%', background: s.muted ? 'var(--error)' : 'var(--success)' }} />
            </div>
            <IconBtn>{s.muted ? <span style={{ color: 'var(--error)' }}>✕</span> : <IconMic />}</IconBtn>
          </div>
        ))}
      </div>
      <NavPill />
    </div>
  );
}

// ============ MINI-MAP OVERLAY ============
function MinimapOverlayScreen() {
  return (
    <div className="screen" style={{ background: '#000' }}>
      <StatusBar />
      <div style={{ flex: 1, position: 'relative', overflow: 'hidden' }}>
        <WorldScene />
        {/* Mini-map widget — top-left */}
        <div style={{ position: 'absolute', top: 56, left: 12, width: 130, height: 130, zIndex: 5 }}>
          <div style={{
            position: 'absolute', inset: 0, borderRadius: 12,
            background: 'linear-gradient(135deg, #1d3a52, #5a8a6d)',
            border: '1px solid var(--glass-stroke)', overflow: 'hidden',
            boxShadow: '0 4px 20px rgba(0,0,0,0.6)',
          }}>
            {/* terrain */}
            <div style={{ position: 'absolute', left: 20, top: 30, width: 50, height: 30, background: 'rgba(255,255,255,0.08)', borderRadius: 4 }} />
            <div style={{ position: 'absolute', left: 70, top: 60, width: 30, height: 50, background: 'rgba(0,0,0,0.2)', borderRadius: 4 }} />
            {/* you */}
            <div style={{ position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%,-50%)', width: 8, height: 8, borderRadius: 4, background: 'var(--primary)', boxShadow: '0 0 0 2px rgba(109,232,255,0.3), 0 0 8px var(--primary)' }} />
            {/* friends */}
            <div style={{ position: 'absolute', left: '30%', top: '40%', width: 6, height: 6, borderRadius: 3, background: 'var(--success)' }} />
            <div style={{ position: 'absolute', left: '70%', top: '30%', width: 6, height: 6, borderRadius: 3, background: 'var(--success)' }} />
            <div style={{ position: 'absolute', left: '60%', top: '70%', width: 6, height: 6, borderRadius: 3, background: 'var(--success)' }} />
            {/* others */}
            <div style={{ position: 'absolute', left: '20%', top: '70%', width: 4, height: 4, borderRadius: 2, background: 'rgba(255,255,255,0.5)' }} />
            <div style={{ position: 'absolute', left: '85%', top: '60%', width: 4, height: 4, borderRadius: 2, background: 'rgba(255,255,255,0.5)' }} />
            {/* compass */}
            <div style={{ position: 'absolute', top: 4, right: 4, fontSize: 9, color: 'var(--primary)', fontWeight: 700, fontFamily: 'var(--font-mono)' }}>N</div>
            {/* coord */}
            <div style={{ position: 'absolute', bottom: 0, left: 0, right: 0, padding: 4, background: 'rgba(0,0,0,0.6)', fontSize: 9, fontFamily: 'var(--font-mono)', color: 'var(--on-surface-dim)', textAlign: 'center' }}>186, 92, 24</div>
          </div>
          {/* expand button */}
          <button style={{ position: 'absolute', top: -8, right: -8, width: 22, height: 22, borderRadius: 11, background: 'var(--primary)', color: 'var(--on-primary)', border: 'none', fontSize: 12 }}>⤢</button>
        </div>

        {/* Top status */}
        <div style={{ position: 'absolute', top: 12, left: 12, right: 12, display: 'flex', gap: 6, zIndex: 3, justifyContent: 'flex-end' }}>
          <div className="hud-tag">45fps</div>
          <div className="hud-tag">▼ 320kbps</div>
        </div>

        {/* Distance ring labels */}
        <div style={{ position: 'absolute', bottom: 80, left: 16, fontSize: 10, color: 'var(--on-surface-dim)', fontFamily: 'var(--font-mono)' }}>
          DRAW: 256m · 14 nearby
        </div>
      </div>
      <NavPill />
    </div>
  );
}

// ============ EMPTY STATES ============
function EmptyStatesScreen() {
  return (
    <div className="screen">
      <StatusBar />
      <TopBar title="Empty states" subtitle="Reference patterns" leading={<IconBtn><IconBack /></IconBtn>} />
      <div style={{ flex: 1, overflow: 'auto', padding: '0 16px 16px', display: 'flex', flexDirection: 'column', gap: 12 }}>
        {/* No friends */}
        <div className="card" style={{ padding: 24, textAlign: 'center' }}>
          <div style={{ width: 64, height: 64, borderRadius: 20, background: 'var(--surface-2)', margin: '0 auto 14px', display: 'grid', placeItems: 'center', position: 'relative' }}>
            <Avatar name="ghost1" size="sm" style={{ position: 'absolute', left: 8, top: 12, opacity: 0.4 }} />
            <Avatar name="ghost2" size="sm" style={{ position: 'absolute', right: 8, top: 18, opacity: 0.5 }} />
            <Avatar name="ghost3" size="sm" style={{ position: 'absolute', bottom: 8, left: '50%', transform: 'translateX(-50%)', opacity: 0.6 }} />
          </div>
          <div className="h-title" style={{ fontSize: 16 }}>No friends yet</div>
          <div style={{ fontSize: 12, color: 'var(--on-surface-dim)', marginTop: 4, marginBottom: 12 }}>Find avatars nearby or search by name to add your first.</div>
          <button className="btn" style={{ height: 36, fontSize: 12 }}>Find people</button>
        </div>

        {/* Empty inventory folder */}
        <div className="card" style={{ padding: 24, textAlign: 'center' }}>
          <div style={{ width: 64, height: 64, borderRadius: 12, background: 'var(--surface-2)', margin: '0 auto 14px', display: 'grid', placeItems: 'center', fontSize: 24, color: 'var(--on-surface-dim)', border: '2px dashed var(--outline-2)' }}>📦</div>
          <div className="h-title" style={{ fontSize: 16 }}>This folder is empty</div>
          <div style={{ fontSize: 12, color: 'var(--on-surface-dim)', marginTop: 4, marginBottom: 12 }}>Drag items here, or upload from your camera roll.</div>
          <div style={{ display: 'flex', gap: 6, justifyContent: 'center' }}>
            <button className="btn ghost" style={{ height: 32, fontSize: 11 }}>Upload</button>
            <button className="btn tonal" style={{ height: 32, fontSize: 11 }}>Marketplace</button>
          </div>
        </div>

        {/* No search results */}
        <div className="card" style={{ padding: 24, textAlign: 'center' }}>
          <div style={{ width: 64, height: 64, borderRadius: 32, background: 'var(--surface-2)', margin: '0 auto 14px', display: 'grid', placeItems: 'center', fontSize: 24 }}>🔍</div>
          <div className="h-title" style={{ fontSize: 16 }}>No results for "lyrandar"</div>
          <div style={{ fontSize: 12, color: 'var(--on-surface-dim)', marginTop: 4, marginBottom: 12 }}>Try a shorter query, or search a different category.</div>
          <div style={{ display: 'flex', gap: 6, justifyContent: 'center', flexWrap: 'wrap' }}>
            <div className="chip">try: lyra</div>
            <div className="chip">try: lyrebird</div>
          </div>
        </div>

        {/* No notifs */}
        <div className="card" style={{ padding: 24, textAlign: 'center' }}>
          <div style={{ width: 64, height: 64, borderRadius: 32, background: 'var(--surface-2)', margin: '0 auto 14px', display: 'grid', placeItems: 'center', fontSize: 22 }}>🌙</div>
          <div className="h-title" style={{ fontSize: 16 }}>You're all caught up</div>
          <div style={{ fontSize: 12, color: 'var(--on-surface-dim)', marginTop: 4 }}>No new notifications. We'll ping you when something happens.</div>
        </div>
      </div>
      <NavPill />
    </div>
  );
}

Object.assign(window, {
  OnboardingWelcomeScreen, OnboardingAvatarScreen, OnboardingPermissionsScreen,
  WalletScreen, GroupsListScreen, GroupProfileScreen,
  OutfitComposerScreen, BuildToolsScreen, PermissionDialogScreen,
  VoiceDeepScreen, MinimapOverlayScreen, EmptyStatesScreen,
});
