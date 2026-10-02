// The Linkpoint crystal from the login screen, reduced to a loading indicator:
// two pyramid halves close into the octahedron and the core lights as they meet.
// Loading states used to spin the screen's lucide glyph inside its bordered box,
// which read as a generic spinner rather than as this product's mark.
//
// Colours come from the palette's CSS custom properties (the shell sets them on
// the device frame), so it re-skins with every colour pack like the login logo.
// Keyframes live in index.css next to `spin`.
export default function CrystalLoader({ size = 88 }) {
  return (
    <div style={{ width: size, height: size, flex: "none", display: "flex", alignItems: "center", justifyContent: "center" }} role="img" aria-label="Loading">
      <svg viewBox="0 0 120 120" aria-hidden="true" style={{ width: "100%", height: "100%", display: "block", overflow: "visible" }}>
        <defs>
          <linearGradient id="lpld-a" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stopColor="var(--pri,#6CFF9A)" />
            <stop offset="100%" stopColor="var(--priC,#123B27)" />
          </linearGradient>
          <linearGradient id="lpld-b" x1="100%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" stopColor="var(--sec2,#8AD0B0)" />
            <stop offset="100%" stopColor="var(--sec,#365047)" />
          </linearGradient>
          <radialGradient id="lpld-core" cx="50%" cy="50%" r="50%">
            <stop offset="0%" stopColor="#ffffff" stopOpacity=".95" />
            <stop offset="55%" stopColor="var(--pri,#6CFF9A)" stopOpacity=".8" />
            <stop offset="100%" stopColor="var(--pri,#6CFF9A)" stopOpacity="0" />
          </radialGradient>
        </defs>
        <circle className="lpld-ring" cx="60" cy="60" r="6" fill="none" stroke="var(--pri,#6CFF9A)" strokeWidth="1" />
        <g className="lpld-bot">
          <polygon points="60,114 12,60 60,76" fill="url(#lpld-b)" stroke="var(--outv,#365047)" strokeWidth="1" strokeLinejoin="round" />
          <polygon points="60,114 60,76 108,60" fill="url(#lpld-a)" stroke="var(--outv,#365047)" strokeWidth="1" strokeLinejoin="round" opacity=".72" />
        </g>
        <g className="lpld-top">
          <polygon points="60,6 12,60 60,76" fill="url(#lpld-a)" stroke="var(--outv,#365047)" strokeWidth="1" strokeLinejoin="round" />
          <polygon points="60,6 60,76 108,60" fill="url(#lpld-b)" stroke="var(--outv,#365047)" strokeWidth="1" strokeLinejoin="round" opacity=".72" />
          <polyline points="12,60 60,44 108,60" fill="none" stroke="var(--pri,#6CFF9A)" strokeOpacity=".45" strokeWidth="1" />
        </g>
        <circle className="lpld-core" cx="60" cy="60" r="15" fill="url(#lpld-core)" />
      </svg>
    </div>
  );
}
