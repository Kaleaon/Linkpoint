// Screens 3: Avatar/Outfit, Friends, Search, Notifications

function AvatarScreen() {
  const wearing = [
    { slot: 'Shape', n: 'Aurora · v3' },
    { slot: 'Skin', n: 'Glow Tone 02' },
    { slot: 'Hair', n: 'Lyric (midnight)' },
    { slot: 'Eyes', n: 'Cyan Mesh' },
    { slot: 'Body', n: 'Maitreya Lara X' },
    { slot: 'Top', n: 'Aurora dress · midnight' },
    { slot: 'Shoes', n: 'Sera heels · onyx' },
    { slot: 'AO', n: 'Casual female · v2' },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="My avatar"
        leading={<IconBtn><IconBack /></IconBtn>}
        trailing={<IconBtn><IconMore /></IconBtn>}
      />

      <div style={{ padding: '0 16px' }}>
        <div className="glass" style={{ padding: 16, display: 'flex', alignItems: 'center', gap: 16 }}>
          <Avatar name="Lyra" size="xxl" online />
          <div style={{ flex: 1 }}>
            <div className="h-title" style={{ fontSize: 18 }}>Lyra Resident</div>
            <div className="coord" style={{ marginTop: 2 }}>display: Lyra ✦</div>
            <div style={{ fontSize: 12, color: 'var(--on-surface-dim)', marginTop: 8, lineHeight: 1.4 }}>
              "explorer · builder · sunset chaser"
            </div>
            <div style={{ display: 'flex', gap: 6, marginTop: 10 }}>
              <button className="btn tonal" style={{ height: 32, fontSize: 11, padding: '0 10px' }}>Edit shape</button>
              <button className="btn ghost" style={{ height: 32, fontSize: 11, padding: '0 10px' }}>Snapshot</button>
            </div>
          </div>
        </div>
      </div>

      <div style={{ padding: '12px 16px 4px', display: 'flex', gap: 6, overflow: 'auto' }}>
        {['Wearing','Outfits','Wardrobe','Transactions'].map((t,i)=>(
          <span key={t} className={`chip ${i===0?'primary':''}`} style={{ whiteSpace: 'nowrap' }}>{t}</span>
        ))}
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '4px 16px 16px' }}>
        <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'var(--on-surface-dim)', margin: '8px 0' }}>CURRENTLY WEARING — 8 items</div>
        <div className="card" style={{ overflow: 'hidden' }}>
          {wearing.map((w, i) => (
            <React.Fragment key={i}>
              <div className="row">
                <div style={{
                  width: 36, padding: '4px 0', textAlign: 'center',
                  fontSize: 9, fontWeight: 700, letterSpacing: 0.8,
                  color: 'var(--primary)',
                  background: 'var(--surface-3)', borderRadius: 6,
                }}>{w.slot.toUpperCase()}</div>
                <div className="grow"><div className="h1">{w.n}</div></div>
                <span className="chip success">●</span>
                <IconBtn><IconMore /></IconBtn>
              </div>
              {i < wearing.length - 1 && <div className="divider" style={{ marginLeft: 16 }} />}
            </React.Fragment>
          ))}
        </div>

        <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'var(--on-surface-dim)', margin: '20px 0 8px' }}>SAVED OUTFITS</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8 }}>
          {['Casual','Beach','Formal','Cyber','Winter','Studio'].map((o, i) => (
            <div key={o} className="card" style={{ aspectRatio: '3/4', position: 'relative', overflow: 'hidden' }}>
              <div style={{
                position: 'absolute', inset: 0,
                background: `linear-gradient(135deg, ${['#2A6F85','#7CFFD8','#856D34','#A73CFF','#39B6F0','#FF8FB1'][i]}55, transparent)`,
              }} />
              <Avatar name={o} size="lg" style={{ position: 'absolute', left: '50%', top: '40%', transform: 'translate(-50%,-50%)' }} />
              <div style={{ position: 'absolute', left: 8, right: 8, bottom: 8, fontSize: 11, fontWeight: 600 }}>{o}</div>
            </div>
          ))}
        </div>
      </div>

      <TabBar active="me" />
      <NavPill />
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function FriendsScreen() {
  const friends = [
    { n: 'Echo Nightshade', loc: 'Crystal Coast', online: true, online_h: '14m', perms: 'TP · See me' },
    { n: 'Mira Ashford', loc: 'Bay City — Plum', online: true, online_h: '2h', perms: 'See me' },
    { n: 'Voss Vale', loc: 'Tinkerers Lab', online: true, online_h: '5h', perms: 'TP · See me · Edit' },
    { n: 'Juno Vex', loc: 'Offline', online: false, perms: 'See me' },
    { n: 'Ren Volkov', loc: 'Offline · 2d', online: false, perms: 'See me' },
    { n: 'Solaris Mae', loc: 'Solaris Build', online: true, online_h: '19m' },
    { n: 'Aria Sinclair', loc: 'DJ booth · NeonRoom', online: true, online_h: '1h' },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="Friends"
        subtitle="5 of 47 online"
        leading={<IconBtn><IconBack /></IconBtn>}
        trailing={<><IconBtn><IconSearch /></IconBtn><IconBtn><IconPlus /></IconBtn></>}
      />

      <div style={{ padding: '0 16px 8px', display: 'flex', gap: 6 }}>
        <span className="chip primary">Friends</span>
        <span className="chip">Groups (8)</span>
        <span className="chip">Calling cards</span>
        <span className="chip">Blocked</span>
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '0 8px' }}>
        <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'var(--success)', padding: '8px 12px' }}>● ONLINE — 5</div>
        {friends.filter(f => f.online).map((f, i) => (
          <div key={i} className="row" style={{ borderRadius: 'var(--r-md)' }}>
            <Avatar name={f.n} online />
            <div className="grow">
              <div style={{ display: 'flex', alignItems: 'baseline', gap: 8 }}>
                <div className="h1" style={{ flex: 1 }}>{f.n}</div>
                <div style={{ fontSize: 10, color: 'var(--on-surface-dim)' }}>{f.online_h}</div>
              </div>
              <div className="h2">{f.loc}</div>
              {f.perms && <div className="coord" style={{ fontSize: 9, marginTop: 2 }}>{f.perms}</div>}
            </div>
            <IconBtn><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M21 12a8 8 0 11-3.5-6.6L21 5l-1 4 1 3z"/></svg></IconBtn>
            <IconBtn style={{ color: 'var(--primary)' }}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round"><path d="M5 12l4-4M5 12l4 4M5 12h14"/></svg>
            </IconBtn>
          </div>
        ))}
        <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'var(--on-surface-dim)', padding: '12px 12px 4px' }}>OFFLINE — 42</div>
        {friends.filter(f => !f.online).map((f, i) => (
          <div key={i} className="row" style={{ opacity: 0.65 }}>
            <Avatar name={f.n} />
            <div className="grow">
              <div className="h1">{f.n}</div>
              <div className="h2">{f.loc}</div>
            </div>
            <IconBtn><IconMore /></IconBtn>
          </div>
        ))}
      </div>

      <TabBar active="me" />
      <NavPill />
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function SearchScreen() {
  const events = [
    { n: 'Sunset DJ Set — Aria', t: 'Tonight 8PM SLT', tag: 'MUSIC', loc: 'NeonRoom' },
    { n: 'Build Workshop: Mesh basics', t: 'Wed 6PM SLT', tag: 'EDU', loc: 'Tinkerers Lab' },
    { n: 'Photo Walk — Bay City', t: 'Sat 2PM SLT', tag: 'SOCIAL', loc: 'Bay City' },
  ];
  const places = [
    { n: 'Crystal Coast', sub: 'Beach · PG · 14 here', img: '#1d4060' },
    { n: 'NeonRoom', sub: 'Club · M · 32 here', img: '#A73CFF' },
    { n: 'Tinkerers Lab', sub: 'Education · PG · 8 here', img: '#7CFFD8' },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar title="Search" leading={<IconBtn><IconBack /></IconBtn>} />

      <div style={{ padding: '0 16px 8px' }}>
        <div className="input"><IconSearch /><input placeholder="People, places, events, classifieds…" defaultValue="crystal" /></div>
      </div>

      <div style={{ padding: '4px 16px 8px', display: 'flex', gap: 6, overflow: 'auto' }}>
        {['All','People','Places','Events','Groups','Classifieds'].map((t,i)=>(
          <span key={t} className={`chip ${i===2?'primary':''}`} style={{ whiteSpace: 'nowrap' }}>{t}</span>
        ))}
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '8px 16px 16px' }}>
        <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'var(--on-surface-dim)', margin: '4px 0 8px' }}>UPCOMING EVENTS</div>
        {events.map((e,i) => (
          <div key={i} className="card" style={{ padding: 12, marginBottom: 8, display: 'flex', gap: 12 }}>
            <div style={{
              width: 48, padding: '8px 4px', textAlign: 'center',
              borderRadius: 10,
              background: 'var(--surface-3)',
              border: '1px solid var(--outline-2)',
            }}>
              <div style={{ fontSize: 9, color: 'var(--primary)', fontWeight: 700, letterSpacing: 1 }}>{e.tag}</div>
              <div style={{ fontSize: 16, fontWeight: 700, marginTop: 4 }}>{e.t.split(' ')[0].slice(0,3)}</div>
            </div>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div className="h1">{e.n}</div>
              <div className="h2">{e.t}</div>
              <div className="coord" style={{ fontSize: 10, marginTop: 2 }}>{e.loc}</div>
            </div>
            <button className="btn tonal" style={{ height: 32, alignSelf: 'center', fontSize: 11 }}>RSVP</button>
          </div>
        ))}

        <div style={{ fontSize: 11, fontWeight: 700, letterSpacing: 1, color: 'var(--on-surface-dim)', margin: '16px 0 8px' }}>POPULAR PLACES</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
          {places.map((p, i) => (
            <div key={i} className="card" style={{ overflow: 'hidden' }}>
              <div style={{ height: 72, background: `linear-gradient(135deg, ${p.img}, ${p.img}88)` }} />
              <div style={{ padding: 10 }}>
                <div className="h1" style={{ fontSize: 13 }}>{p.n}</div>
                <div className="h2" style={{ fontSize: 11 }}>{p.sub}</div>
              </div>
            </div>
          ))}
        </div>
      </div>

      <NavPill />
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function NotificationsScreen() {
  const notifs = [
    { kind: 'tp', who: 'Echo Nightshade', txt: 'is offering you a teleport', when: 'now', urgent: true },
    { kind: 'fr', who: 'Aria Sinclair', txt: 'wants to be friends', when: '2m' },
    { kind: 'group', who: 'Tinkerers Guild', txt: 'New notice: Mesh workshop Wed', when: '14m' },
    { kind: 'gift', who: 'Mira Ashford', txt: 'sent you "beach sunset HUD"', when: '1h' },
    { kind: 'sys', who: 'System', txt: 'Region restart in 2 hours · save your work', when: '1h' },
    { kind: 'pay', who: 'Bay City Boutique', txt: 'You paid L$1,200 for Aurora dress', when: '3h' },
  ];
  const iconFor = (k) => ({
    tp: <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M5 12l4-4M5 12l4 4M5 12h14"/></svg>,
    fr: <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4.4 3.6-8 8-8s8 3.6 8 8"/></svg>,
    group: <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="9" cy="7" r="3"/><circle cx="17" cy="9" r="2.5"/><path d="M2 21c0-3.9 3.1-7 7-7s7 3.1 7 7M22 21c0-2.8-1.7-5.3-4-6"/></svg>,
    gift: <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><rect x="3" y="8" width="18" height="13" rx="1.5"/><path d="M3 12h18M12 8v13M8 8a2.5 2.5 0 010-5c2 0 4 5 4 5M16 8a2.5 2.5 0 000-5c-2 0-4 5-4 5"/></svg>,
    sys: <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="12" cy="12" r="9"/><path d="M12 8v4M12 16h.01"/></svg>,
    pay: <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M3 7h18v10H3z"/><path d="M3 11h18M7 15h2"/></svg>,
  })[k];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="Notifications"
        subtitle="6 new"
        leading={<IconBtn><IconBack /></IconBtn>}
        trailing={<button className="btn ghost" style={{ height: 32, fontSize: 11 }}>Clear all</button>}
      />

      <div style={{ flex: 1, overflow: 'auto', padding: '0 12px 16px', display: 'flex', flexDirection: 'column', gap: 8 }}>
        {/* Pending TP - inline action card */}
        <div className="glass" style={{ padding: 14, borderRadius: 'var(--r-lg)', border: '1px solid var(--primary)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 10 }}>
            <Avatar name="Echo Nightshade" online />
            <div style={{ flex: 1 }}>
              <div className="h1">Echo Nightshade</div>
              <div className="h2">is offering you a teleport</div>
            </div>
            <span className="chip primary">NEW</span>
          </div>
          <div style={{ background: 'var(--surface-3)', padding: 10, borderRadius: 12, marginBottom: 10 }}>
            <div className="coord" style={{ fontSize: 10 }}>→ Crystal Coast</div>
            <div style={{ fontSize: 12, marginTop: 2 }}>"come watch the sunset with us!"</div>
          </div>
          <div style={{ display: 'flex', gap: 6 }}>
            <button className="btn" style={{ flex: 1, height: 38 }}>Accept</button>
            <button className="btn ghost" style={{ flex: 1, height: 38 }}>Decline</button>
          </div>
        </div>

        {notifs.slice(1).map((n, i) => (
          <div key={i} className="row" style={{ padding: '12px', background: 'var(--surface)', borderRadius: 'var(--r-md)', border: '1px solid var(--outline-2)' }}>
            <div style={{
              width: 36, height: 36, borderRadius: 10,
              background: 'var(--surface-2)',
              border: '1px solid var(--outline-2)',
              color: 'var(--primary)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              flexShrink: 0,
            }}>{iconFor(n.kind)}</div>
            <div className="grow">
              <div className="h1"><span style={{ fontWeight: 700 }}>{n.who}</span> <span style={{ fontWeight: 400, color: 'var(--on-surface-dim)' }}>{n.txt}</span></div>
              <div className="h2">{n.when}</div>
            </div>
          </div>
        ))}
      </div>

      <NavPill />
    </div>
  );
}

Object.assign(window, { AvatarScreen, FriendsScreen, SearchScreen, NotificationsScreen });
