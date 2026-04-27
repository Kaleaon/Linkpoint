// Shared primitives for Linkpoint 2.0

// ── Geometric CSS-only avatars ──
// Each avatar gets a deterministic color palette + body shape variation
const AV_PALETTES = [
  { bg: 'linear-gradient(135deg,#6DE8FF,#8C7CFF)', head: '#FFE9C9', body: '#2A6F85' },
  { bg: 'linear-gradient(135deg,#FF8FB1,#8C7CFF)', head: '#F5D0C5', body: '#7A2454' },
  { bg: 'linear-gradient(135deg,#7CFFD8,#39B6F0)', head: '#D7B591', body: '#0A6FA0' },
  { bg: 'linear-gradient(135deg,#FFD56D,#FF8FB1)', head: '#E8C39A', body: '#856D34' },
  { bg: 'linear-gradient(135deg,#A073FF,#39B6F0)', head: '#D9A574', body: '#3B1E66' },
  { bg: 'linear-gradient(135deg,#79D87E,#39B6F0)', head: '#FFE2C2', body: '#1F5D33' },
  { bg: 'linear-gradient(135deg,#FF6B6B,#FFD56D)', head: '#C9876B', body: '#931C29' },
  { bg: 'linear-gradient(135deg,#39B6F0,#0A6FA0)', head: '#F2C098', body: '#0A2E47' },
];

function hashCode(s) {
  let h = 0;
  for (let i = 0; i < s.length; i++) h = (h * 31 + s.charCodeAt(i)) | 0;
  return Math.abs(h);
}

function Avatar({ name = 'A', size = 'md', online = false, style }) {
  const p = AV_PALETTES[hashCode(name) % AV_PALETTES.length];
  const variant = hashCode(name + 'v') % 3;
  const cls = `avatar ${size === 'md' ? '' : size} ${online ? 'online' : ''}`;
  return (
    <div className={cls} style={style}>
      <div className="av-bg" style={{ background: p.bg }} />
      {/* head */}
      <div className="av-head" style={{ background: p.head, top: variant === 1 ? '18%' : '22%' }} />
      {/* body */}
      <div className="av-body" style={{ background: p.body, opacity: 0.9 }} />
      {/* hair / hat suggestion */}
      {variant === 0 && (
        <div style={{
          position: 'absolute', left: '50%', top: '14%', transform: 'translateX(-50%)',
          width: '46%', height: '20%', background: p.body,
          borderTopLeftRadius: '50%', borderTopRightRadius: '50%',
        }} />
      )}
      {variant === 2 && (
        <div style={{
          position: 'absolute', left: '50%', top: '12%', transform: 'translateX(-50%)',
          width: '52%', height: '14%', background: p.body,
          borderRadius: '6px',
        }} />
      )}
    </div>
  );
}

// ── Status bar ──
function StatusBar({ time = '14:32', dark = true }) {
  const c = 'currentColor';
  return (
    <div className="status-bar">
      <span>{time}</span>
      <div className="punch" />
      <div className="icons">
        <svg width="14" height="14" viewBox="0 0 16 16"><path d="M8 13.3L.67 5.97a10.37 10.37 0 0114.66 0L8 13.3z" fill={c}/></svg>
        <svg width="14" height="14" viewBox="0 0 16 16"><path d="M14.67 14.67V1.33L1.33 14.67h13.34z" fill={c}/></svg>
        <svg width="20" height="12" viewBox="0 0 24 12"><rect x="1" y="1" width="20" height="10" rx="3" fill="none" stroke={c} strokeWidth="1.2"/><rect x="3" y="3" width="14" height="6" rx="1.5" fill={c}/><rect x="22" y="4" width="1.5" height="4" rx="0.5" fill={c}/></svg>
      </div>
    </div>
  );
}

function NavPill() {
  return <div className="nav-pill"><div /></div>;
}

// ── Tab bar (5 tabs: Chat, World, Map, Inventory, Me) ──
function TabBar({ active = 'chat' }) {
  const tabs = [
    { id: 'chat', label: 'Chat', icon: <IconChat /> },
    { id: 'world', label: 'World', icon: <IconWorld /> },
    { id: 'map', label: 'Map', icon: <IconMap /> },
    { id: 'inv', label: 'Inventory', icon: <IconInv /> },
    { id: 'me', label: 'Me', icon: <IconMe /> },
  ];
  return (
    <div className="tabbar">
      {tabs.map(t => (
        <div key={t.id} className={`tab ${active === t.id ? 'active' : ''}`}>
          <div className="glyph">{t.icon}</div>
          <span>{t.label}</span>
        </div>
      ))}
    </div>
  );
}

// ── Icons (line) ──
const ICONSZ = 20;
function IconChat() { return <svg width={ICONSZ} height={ICONSZ} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><path d="M21 12a8 8 0 11-3.5-6.6L21 5l-1 4 1 3z"/><circle cx="9" cy="12" r="0.8" fill="currentColor"/><circle cx="13" cy="12" r="0.8" fill="currentColor"/><circle cx="17" cy="12" r="0.8" fill="currentColor"/></svg>; }
function IconWorld() { return <svg width={ICONSZ} height={ICONSZ} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"><circle cx="12" cy="12" r="9"/><ellipse cx="12" cy="12" rx="9" ry="3.5"/><path d="M12 3v18"/></svg>; }
function IconMap() { return <svg width={ICONSZ} height={ICONSZ} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinejoin="round" strokeLinecap="round"><path d="M9 4L3 6v14l6-2 6 2 6-2V4l-6 2-6-2z"/><path d="M9 4v14M15 6v14"/></svg>; }
function IconInv() { return <svg width={ICONSZ} height={ICONSZ} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinejoin="round" strokeLinecap="round"><path d="M3 7l9-4 9 4-9 4-9-4z"/><path d="M3 12l9 4 9-4M3 17l9 4 9-4"/></svg>; }
function IconMe() { return <svg width={ICONSZ} height={ICONSZ} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4.4 3.6-8 8-8s8 3.6 8 8"/></svg>; }

function IconBack() { return <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M15 18l-6-6 6-6"/></svg>; }
function IconSearch() { return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>; }
function IconMore() { return <svg width="22" height="22" viewBox="0 0 24 24" fill="currentColor"><circle cx="5" cy="12" r="1.6"/><circle cx="12" cy="12" r="1.6"/><circle cx="19" cy="12" r="1.6"/></svg>; }
function IconPlus() { return <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round"><path d="M12 5v14M5 12h14"/></svg>; }
function IconSend() { return <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M3 11.5L21 3l-8.5 18-2-8z"/></svg>; }
function IconMic() { return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round"><rect x="9" y="3" width="6" height="12" rx="3"/><path d="M5 11a7 7 0 0014 0M12 18v3"/></svg>; }
function IconCam() { return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><path d="M3 7h4l2-2h6l2 2h4v12H3z"/><circle cx="12" cy="13" r="4"/></svg>; }
function IconBell() { return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><path d="M6 8a6 6 0 1112 0c0 7 3 8 3 8H3s3-1 3-8M10 21a2 2 0 004 0"/></svg>; }
function IconHud() { return <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8"><rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/><rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/></svg>; }
function IconClose() { return <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round"><path d="M6 6l12 12M18 6L6 18"/></svg>; }

// ── Top app bar ──
function TopBar({ title, leading, trailing, subtitle }) {
  return (
    <div style={{
      padding: '8px 12px 12px',
      display: 'flex', alignItems: 'center', gap: 8, flexShrink: 0,
    }}>
      {leading}
      <div style={{ flex: 1, minWidth: 0 }}>
        <div className="h-title" style={{ fontSize: 18, lineHeight: '22px' }}>{title}</div>
        {subtitle && <div style={{ fontSize: 11, color: 'var(--on-surface-dim)' }}>{subtitle}</div>}
      </div>
      {trailing}
    </div>
  );
}

function IconBtn({ children, onClick, style }) {
  return (
    <button onClick={onClick} style={{
      width: 40, height: 40, borderRadius: 100,
      background: 'transparent', border: 'none',
      color: 'var(--on-surface)', cursor: 'pointer',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      ...style,
    }}>{children}</button>
  );
}

// ── World scene placeholder (CSS-drawn) ──
function WorldScene({ children }) {
  return (
    <div style={{
      position: 'absolute', inset: 0,
      background: `
        radial-gradient(120% 60% at 50% 100%, #1A3A5C 0%, transparent 60%),
        linear-gradient(180deg, #0a1830 0%, #1d4060 45%, #4a7ba8 75%, #c8b88a 90%, #b89968 100%)
      `,
      overflow: 'hidden',
    }}>
      {/* Sun */}
      <div style={{
        position: 'absolute', right: '12%', top: '18%',
        width: 80, height: 80, borderRadius: '50%',
        background: 'radial-gradient(circle, #FFE7B0 0%, #FFB958 50%, transparent 70%)',
        filter: 'blur(2px)',
      }} />
      {/* Distant mountains */}
      <svg width="100%" height="40%" viewBox="0 0 412 200" preserveAspectRatio="none" style={{ position: 'absolute', bottom: '32%', left: 0 }}>
        <path d="M0 200 L0 130 L60 80 L130 120 L210 60 L290 110 L360 70 L412 100 L412 200Z" fill="#1f3a52" opacity="0.7"/>
        <path d="M0 200 L0 160 L80 120 L180 150 L260 100 L340 140 L412 110 L412 200Z" fill="#2a4d6e" opacity="0.8"/>
      </svg>
      {/* Water reflection band */}
      <div style={{
        position: 'absolute', left: 0, right: 0, top: '60%', height: '20%',
        background: 'linear-gradient(180deg, rgba(150,180,220,0.4), rgba(80,120,160,0.6))',
        backdropFilter: 'blur(1px)',
      }} />
      {/* Foreground ground */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0, height: '20%',
        background: 'linear-gradient(180deg, #6b7d4a 0%, #3d4c2a 100%)',
      }} />
      {/* Avatar silhouette in scene */}
      <div style={{
        position: 'absolute', left: '50%', bottom: '15%',
        transform: 'translateX(-50%)',
        width: 60, height: 110,
      }}>
        <div style={{ width: 30, height: 30, borderRadius: '50%', background: '#FFE9C9', margin: '0 auto', boxShadow: '0 0 20px rgba(109,232,255,0.4)' }} />
        <div style={{ width: 50, height: 70, background: '#2A6F85', borderRadius: '20px 20px 12px 12px', margin: '4px auto 0', boxShadow: '0 4px 20px rgba(0,0,0,0.4)' }} />
      </div>
      {/* Other distant avatars (dots) */}
      <div style={{ position: 'absolute', left: '25%', bottom: '22%', width: 4, height: 14, background: '#FFE9C9', borderRadius: 2 }} />
      <div style={{ position: 'absolute', left: '72%', bottom: '21%', width: 4, height: 14, background: '#FFE9C9', borderRadius: 2 }} />
      <div style={{ position: 'absolute', left: '60%', bottom: '24%', width: 3, height: 11, background: '#FFE9C9', borderRadius: 2 }} />
      {children}
    </div>
  );
}

Object.assign(window, {
  Avatar, StatusBar, NavPill, TabBar, TopBar, IconBtn, WorldScene,
  IconChat, IconWorld, IconMap, IconInv, IconMe,
  IconBack, IconSearch, IconMore, IconPlus, IconSend, IconMic, IconCam, IconBell, IconHud, IconClose,
  hashCode,
});
