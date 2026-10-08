// Screens 1: Login, Home (chat-first), Chat thread

function LoginScreen() {
  return (
    <div className="screen">
      <StatusBar />
      <div style={{ position: 'absolute', inset: 0, overflow: 'hidden' }}>
        <div className="aurora-blob" style={{ width: 280, height: 280, left: -60, top: 80, background: 'var(--primary)', opacity: 0.35, animation: 'drift 12s ease-in-out infinite' }} />
        <div className="aurora-blob" style={{ width: 320, height: 320, right: -100, top: 200, background: 'var(--secondary, #8C7CFF)', opacity: 0.28, animation: 'drift 16s ease-in-out infinite' }} />
        <div className="aurora-blob" style={{ width: 240, height: 240, left: 80, bottom: 120, background: 'var(--tertiary, #7CFFD8)', opacity: 0.22, animation: 'drift 14s ease-in-out infinite' }} />
      </div>
      <div style={{ flex: 1, position: 'relative', display: 'flex', flexDirection: 'column', padding: '24px 24px 32px', zIndex: 2 }}>
        <div style={{ marginTop: 60 }}>
          <div style={{
            display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
            width: 64, height: 64, borderRadius: 20,
            background: 'var(--glass-bg)', border: '1px solid var(--glass-stroke)',
            backdropFilter: 'blur(20px)',
            marginBottom: 24,
          }}>
            <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
              <circle cx="16" cy="16" r="13" stroke="var(--primary)" strokeWidth="1.5"/>
              <ellipse cx="16" cy="16" rx="13" ry="5" stroke="var(--primary)" strokeWidth="1.5"/>
              <circle cx="16" cy="16" r="3" fill="var(--primary)"/>
            </svg>
          </div>
          <div className="h-title" style={{ fontSize: 38, lineHeight: '42px', letterSpacing: '-0.02em', marginBottom: 10 }}>
            Linkpoint
          </div>
          <div style={{ fontSize: 14, color: 'var(--on-surface-dim)', maxWidth: 280, lineHeight: 1.5 }}>
            Step into your second world. Faster, lighter, beautifully on the go.
          </div>
        </div>

        <div className="glass" style={{ marginTop: 36, padding: 20 }}>
          <div style={{ fontSize: 12, fontWeight: 600, letterSpacing: 1, color: 'var(--on-surface-dim)', marginBottom: 12, textTransform: 'uppercase' }}>Sign in</div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            <div className="input">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4.4 3.6-8 8-8s8 3.6 8 8"/></svg>
              <input placeholder="First name" defaultValue="Lyra" />
              <input placeholder="Last" defaultValue="Resident" style={{ maxWidth: 90 }} />
            </div>
            <div className="input">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><rect x="4" y="11" width="16" height="10" rx="2"/><path d="M8 11V7a4 4 0 018 0v4"/></svg>
              <input placeholder="Password" type="password" defaultValue="●●●●●●●●●●" />
            </div>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: 4 }}>
              <div style={{ fontSize: 12, color: 'var(--on-surface-dim)' }}>Grid</div>
              <div className="chip primary">Second Life · Agni  ▾</div>
            </div>
          </div>

          <button className="btn" style={{ width: '100%', marginTop: 18, height: 48 }}>
            Enter world
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2"><path d="M5 12h14M13 6l6 6-6 6"/></svg>
          </button>

          <div style={{ display: 'flex', gap: 8, marginTop: 12 }}>
            <button className="btn ghost" style={{ flex: 1, height: 40, fontSize: 12 }}>Saved (3)</button>
            <button className="btn ghost" style={{ flex: 1, height: 40, fontSize: 12 }}>Add account</button>
          </div>
        </div>

        <div style={{ flex: 1 }} />
        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 11, color: 'var(--on-surface-dim)' }}>
          <span>v2.0 · build 4831</span>
          <span>Forgot password?</span>
        </div>
      </div>
      <NavPill />
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function HomeScreen() {
  const chats = [
    { name: 'Echo Nightshade', msg: 'omg the new sim is GORGEOUS, you have to come', t: '14:30', unread: 2, online: true, group: false },
    { name: 'Crystal Coast 🌊', msg: 'Aria: dj set starts at 8 SLT, see you there', t: '14:12', unread: 7, online: true, group: true },
    { name: 'Mira Ashford', msg: 'sent you the mesh body fix script', t: '13:48', online: true },
    { name: 'Tinkerers Guild', msg: 'Voss: anyone testing the new physics?', t: '12:30', unread: 1, group: true },
    { name: 'Juno Vex', msg: 'thanks for the teleport! 💜', t: 'Yesterday', online: false },
    { name: 'Solaris Build Team', msg: 'You: pushing the parcel updates now', t: 'Yesterday', group: true },
    { name: 'Ren Volkov', msg: 'meet at the floating gardens?', t: 'Mon', online: false },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="Hello, Lyra"
        subtitle={<span><span style={{ color: 'var(--success)' }}>●</span> Online · Bay City — Plum (128, 64, 32)</span>}
        leading={<Avatar name="Lyra" size="sm" online style={{ marginRight: 4 }} />}
        trailing={<>
          <IconBtn><IconSearch /></IconBtn>
          <IconBtn><IconBell /></IconBtn>
        </>}
      />

      {/* Now Playing — current world strip */}
      <div style={{ padding: '0 16px 12px' }}>
        <div className="glass" style={{ padding: 12, display: 'flex', alignItems: 'center', gap: 12 }}>
          <div style={{
            width: 56, height: 56, borderRadius: 14, flexShrink: 0,
            background: 'linear-gradient(135deg,#1d4060,#4a7ba8 60%,#c8b88a)',
            position: 'relative', overflow: 'hidden',
          }}>
            <div style={{ position: 'absolute', right: 4, top: 4, width: 14, height: 14, borderRadius: '50%', background: 'radial-gradient(circle, #FFE7B0, #FFB958)' }} />
            <div style={{ position: 'absolute', bottom: 0, left: 0, right: 0, height: 14, background: '#3d4c2a' }} />
          </div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontSize: 13, fontWeight: 600 }}>Bay City — Plum</div>
            <div className="coord">128, 64, 32 · 14 nearby</div>
          </div>
          <button className="btn tonal" style={{ height: 36, padding: '0 14px', fontSize: 12 }}>Open</button>
        </div>
      </div>

      <div style={{ padding: '0 20px 8px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--on-surface-dim)', letterSpacing: 0.4, textTransform: 'uppercase' }}>Conversations</div>
        <div style={{ fontSize: 11, color: 'var(--primary)', fontWeight: 600 }}>10 unread</div>
      </div>

      {/* Chat list */}
      <div style={{ flex: 1, overflow: 'auto', padding: '0 8px' }}>
        {chats.map((c, i) => (
          <div key={i} className="row" style={{ borderRadius: 'var(--r-md)', cursor: 'pointer' }}>
            {c.group ? (
              <div style={{
                width: 40, height: 40, borderRadius: 12,
                background: 'var(--surface-2)', border: '1px solid var(--outline-2)',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                flexShrink: 0, fontSize: 16, fontWeight: 700,
                color: 'var(--primary)',
              }}>{c.name.charAt(0)}</div>
            ) : <Avatar name={c.name} online={c.online} />}
            <div className="grow">
              <div style={{ display: 'flex', alignItems: 'baseline', gap: 8 }}>
                <div className="h1" style={{ flex: 1 }}>{c.name}</div>
                <div style={{ fontSize: 11, color: 'var(--on-surface-dim)' }}>{c.t}</div>
              </div>
              <div className="h2">{c.msg}</div>
            </div>
            {c.unread && (
              <div style={{
                minWidth: 22, height: 22, borderRadius: 11,
                background: 'var(--primary)', color: 'var(--on-primary)',
                fontSize: 11, fontWeight: 700,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                padding: '0 7px',
              }}>{c.unread}</div>
            )}
          </div>
        ))}
      </div>

      {/* FAB */}
      <div style={{ position: 'absolute', right: 18, bottom: 100, zIndex: 4 }}>
        <button style={{
          width: 56, height: 56, borderRadius: 18,
          background: 'var(--primary)', color: 'var(--on-primary)',
          border: 'none', boxShadow: 'var(--shadow-2)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M21 12a8 8 0 11-3.5-6.6L21 5l-1 4 1 3z"/><circle cx="9" cy="12" r="0.8" fill="currentColor"/><circle cx="13" cy="12" r="0.8" fill="currentColor"/><circle cx="17" cy="12" r="0.8" fill="currentColor"/></svg>
        </button>
      </div>

      <TabBar active="chat" />
      <NavPill />
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
function ChatThreadScreen() {
  const messages = [
    { who: 'them', name: 'Echo Nightshade', msg: 'so I just got back from the Crystal Coast sim', t: '14:18' },
    { who: 'them', name: 'Echo Nightshade', msg: 'omg the new sim is GORGEOUS, you have to come', t: '14:18' },
    { who: 'me', msg: 'wait send me the slurl??', t: '14:21' },
    { who: 'them', name: 'Echo Nightshade', msg: 'http://maps.secondlife.com/secondlife/Crystal Coast/186/92/24', kind: 'slurl', t: '14:22' },
    { who: 'them', name: 'Echo Nightshade', msg: '', kind: 'card', card: { title: 'Crystal Coast', sub: 'PG · 14 here · Beach', img: 'beach' }, t: '14:22' },
    { who: 'me', msg: 'tping now 💜', t: '14:30' },
    { who: 'me', msg: 'omw', t: '14:30', read: true },
    { who: 'them', name: 'Echo Nightshade', msg: 'is typing…', kind: 'typing', t: '' },
  ];
  return (
    <div className="screen">
      <StatusBar />
      <TopBar
        title="Echo Nightshade"
        subtitle={<><span style={{ color: 'var(--success)' }}>●</span> Online · Crystal Coast (186, 92, 24)</>}
        leading={<>
          <IconBtn><IconBack /></IconBtn>
          <Avatar name="Echo Nightshade" size="sm" online />
        </>}
        trailing={<>
          <IconBtn><svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M22 16.92v3a2 2 0 01-2.18 2 19.86 19.86 0 01-8.63-3.07 19.5 19.5 0 01-6-6 19.86 19.86 0 01-3.07-8.67A2 2 0 014.11 2h3a2 2 0 012 1.72 13 13 0 00.7 2.81 2 2 0 01-.45 2.11L8.09 9.91a16 16 0 006 6l1.27-1.27a2 2 0 012.11-.45 13 13 0 002.81.7A2 2 0 0122 16.92z"/></svg></IconBtn>
          <IconBtn><IconMore /></IconBtn>
        </>}
      />

      <div style={{ flex: 1, overflow: 'auto', padding: '8px 16px 16px', display: 'flex', flexDirection: 'column', gap: 8 }}>
        <div style={{ alignSelf: 'center', fontSize: 11, color: 'var(--on-surface-dim)', padding: '8px 12px', background: 'var(--surface-3)', borderRadius: 100 }}>
          Today · Local distance 18m
        </div>

        {messages.map((m, i) => {
          const mine = m.who === 'me';
          if (m.kind === 'typing') {
            return (
              <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 8, margin: '4px 0' }}>
                <Avatar name={m.name} size="sm" />
                <div style={{
                  background: 'var(--surface-2)', borderRadius: 18, padding: '10px 14px',
                  display: 'flex', gap: 4,
                }}>
                  <span style={{ width: 6, height: 6, borderRadius: 3, background: 'var(--on-surface-dim)' }} />
                  <span style={{ width: 6, height: 6, borderRadius: 3, background: 'var(--on-surface-dim)', opacity: 0.6 }} />
                  <span style={{ width: 6, height: 6, borderRadius: 3, background: 'var(--on-surface-dim)', opacity: 0.3 }} />
                </div>
              </div>
            );
          }
          if (m.kind === 'card') {
            return (
              <div key={i} className="card" style={{ alignSelf: mine ? 'flex-end' : 'flex-start', maxWidth: '78%', overflow: 'hidden', marginLeft: mine ? 0 : 36 }}>
                <div style={{ height: 96, background: 'linear-gradient(135deg,#1d4060,#4a7ba8 60%,#c8b88a 95%)', position: 'relative' }}>
                  <div style={{ position: 'absolute', right: 16, top: 16, width: 24, height: 24, borderRadius: '50%', background: 'radial-gradient(circle, #FFE7B0, #FFB958)' }} />
                  <div style={{ position: 'absolute', bottom: 0, left: 0, right: 0, height: 22, background: 'linear-gradient(180deg,#6b7d4a,#3d4c2a)' }} />
                </div>
                <div style={{ padding: 12 }}>
                  <div style={{ fontSize: 13, fontWeight: 600 }}>{m.card.title}</div>
                  <div className="coord" style={{ marginTop: 2 }}>186, 92, 24 · PG · 14 here</div>
                  <div style={{ display: 'flex', gap: 6, marginTop: 8 }}>
                    <button className="btn" style={{ height: 32, fontSize: 12, padding: '0 12px' }}>Teleport</button>
                    <button className="btn ghost" style={{ height: 32, fontSize: 12, padding: '0 12px' }}>Map</button>
                  </div>
                </div>
              </div>
            );
          }
          return (
            <div key={i} style={{ display: 'flex', alignItems: 'flex-end', gap: 8, alignSelf: mine ? 'flex-end' : 'flex-start', maxWidth: '82%' }}>
              {!mine && <Avatar name={m.name} size="sm" />}
              <div style={{
                padding: '9px 14px',
                borderRadius: 18,
                background: mine ? 'var(--primary)' : 'var(--surface-2)',
                color: mine ? 'var(--on-primary)' : 'var(--on-surface)',
                fontSize: 14, lineHeight: 1.4,
                borderBottomRightRadius: mine ? 6 : 18,
                borderBottomLeftRadius: mine ? 18 : 6,
                wordBreak: 'break-word',
                fontFamily: m.kind === 'slurl' ? 'var(--font-mono)' : 'inherit',
                fontSize: m.kind === 'slurl' ? 11 : 14,
                textDecoration: m.kind === 'slurl' ? 'underline' : 'none',
              }}>
                {m.msg}
              </div>
            </div>
          );
        })}

        <div style={{ alignSelf: 'flex-end', fontSize: 10, color: 'var(--on-surface-dim)', marginTop: -4 }}>delivered · read 14:30</div>
      </div>

      {/* Composer */}
      <div style={{ padding: '8px 12px 10px', borderTop: '1px solid var(--outline-2)', display: 'flex', alignItems: 'center', gap: 8, background: 'var(--glass-bg)' }}>
        <IconBtn><IconPlus /></IconBtn>
        <div className="input" style={{ flex: 1 }}>
          <input placeholder="Send a message…" />
          <IconCam />
        </div>
        <IconBtn style={{ background: 'var(--primary)', color: 'var(--on-primary)' }}><IconSend /></IconBtn>
      </div>
      <NavPill />
    </div>
  );
}

Object.assign(window, { LoginScreen, HomeScreen, ChatThreadScreen });
