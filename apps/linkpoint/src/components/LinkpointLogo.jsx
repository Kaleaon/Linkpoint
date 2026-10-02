import React from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { CRYSTAL } from "./linkpointCrystal.js";

// Depth model for the mark.  The camera looks down on the scene at ~20 deg,
// so everything sorts into three layers:
//
//   far   - the half of the ground plane / orbit that runs behind the crystal
//   solid - the bottom pyramid first (it sits below the waist, so it is always
//           further from the camera), then the top pyramid
//   near  - the half of the ground plane / orbit that passes in front
//
// The orbit motes are drawn once per layer and cross-fade at the left and
// right extremes of the ellipse, where they are clear of the crystal.

const grad = (id) => `url(#r${id[0].toUpperCase()}${id.slice(1)})`;

function Face({ face, animated, ink }) {
  const common = {
    fill: grad(face.grad),
    stroke: ink,
    strokeWidth: 1.5,
    strokeLinejoin: "round",
    strokeLinecap: "round",
  };
  if (!animated) {
    return <polygon {...common} points={face.points} opacity={face.opacity} />;
  }
  return (
    <polygon {...common}>
      <animate
        attributeName="points"
        dur={CRYSTAL.dur}
        repeatCount="indefinite"
        calcMode="linear"
        values={face.points}
      />
      <animate
        attributeName="opacity"
        dur={CRYSTAL.dur}
        repeatCount="indefinite"
        calcMode="linear"
        values={face.opacity}
      />
    </polygon>
  );
}

function Pyramid({ className, data, staticData, animated, surf, rim, ink }) {
  const base = animated ? data.base : staticData.base;
  const faces = animated ? data.faces : staticData.faces;
  return (
    <g className={className}>
      {/* The base doubles as the solid body: for the top half it always faces
          away from the camera and simply backs the lit faces, for the bottom
          half it is the one face the camera looks straight down on. */}
      <polygon
        fill={surf}
        stroke={rim}
        strokeOpacity="0.6"
        strokeWidth="1.5"
        strokeLinejoin="round"
        points={animated ? undefined : base}
      >
        {animated && (
          <animate
            attributeName="points"
            dur={CRYSTAL.dur}
            repeatCount="indefinite"
            calcMode="linear"
            values={base}
          />
        )}
      </polygon>
      {faces.map((f, i) => (
        <Face key={i} face={f} animated={animated} ink={ink} />
      ))}
    </g>
  );
}

function Motes({ layer, animated, token }) {
  const values = layer === "near" ? CRYSTAL.nearOpacity : CRYSTAL.farOpacity;
  if (!animated) {
    // Resting placements: one mote parked on the far arc, two on the near arc.
    return (
      <>
        {CRYSTAL.staticMotes[layer].map((m, i) => (
          <circle key={i} cx={m.cx} cy={m.cy} r={m.r} fill={token(m.token)} filter="url(#rGlow)" />
        ))}
      </>
    );
  }
  return (
    <>
      {CRYSTAL.motes.map((m, i) => (
        <circle key={i} r={layer === "near" ? m.near : m.far} fill={token(m.token)} filter="url(#rGlow)">
          <animateMotion dur={CRYSTAL.dur} repeatCount="indefinite" begin={m.begin}>
            <mpath href="#rOrbitTrack" />
          </animateMotion>
          <animate
            attributeName="opacity"
            dur={CRYSTAL.dur}
            repeatCount="indefinite"
            begin={m.begin}
            calcMode="linear"
            keyTimes={CRYSTAL.keyTimes}
            values={values}
          />
        </circle>
      ))}
    </>
  );
}

// width/height are applied through `style` rather than as SVG attributes:
// callers pass CSS keywords such as "auto", which the presentation attributes
// reject ("Expected length").
export default function LinkpointLogo({ animated = true, showTitle = true, width = "100%", height = "auto" }) {
  const { V, t } = useTheme();
  const token = (name) => V[name];

  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      xmlnsXlink="http://www.w3.org/1999/xlink"
      id="linkpoint-logo-react"
      viewBox={showTitle ? "0 0 512 580" : "0 0 512 512"}
      style={{ width, height, filter: "drop-shadow(0 4px 16px rgba(0,0,0,0.3))" }}
    >
      <style>{`
        /* The halves close into a perfect octahedron: their base centres sit
           172px apart, so each travels exactly 86px to meet at the waist. */
        .top-crystal-r {
          animation: ${animated ? "topCloseSeq 6s cubic-bezier(0.4, 0, 0.2, 1) infinite" : "none"};
        }
        .bot-crystal-r {
          animation: ${animated ? "botCloseSeq 6s cubic-bezier(0.4, 0, 0.2, 1) infinite" : "none"};
        }
        .core-anim-r {
          transform-origin: 256px 256px;
          animation: ${animated ? "corePulseR 6s cubic-bezier(0.4, 0, 0.2, 1) infinite" : "none"};
        }

        @keyframes topCloseSeq {
          0%, 15% { transform: translateY(0); }
          35%, 65% { transform: translateY(86px); }
          85%, 100% { transform: translateY(0); }
        }

        @keyframes botCloseSeq {
          0%, 15% { transform: translateY(0); }
          35%, 65% { transform: translateY(-86px); }
          85%, 100% { transform: translateY(0); }
        }

        @keyframes corePulseR {
          0%, 25% { transform: scale(1); opacity: 0.8; }
          35%, 65% { transform: scale(0.6) rotate(180deg); opacity: 1; filter: drop-shadow(0 0 20px ${V.pri}); }
          75%, 100% { transform: scale(1) rotate(0deg); opacity: 0.8; }
        }

        .logo-title-r {
          font-family: ${t.dfont || 'sans-serif'};
          font-weight: 800;
          font-size: 38px;
          letter-spacing: 0.28em;
          fill: ${V.pri};
        }
        .logo-subtitle-r {
          font-family: ${t.font || 'sans-serif'};
          font-weight: 500;
          font-size: 13px;
          letter-spacing: 0.38em;
          fill: ${V.ink2};
        }
      `}</style>

      <defs>
        <linearGradient id="rFace1Grad" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stopColor={V.pri} stopOpacity="1" />
          <stop offset="100%" stopColor={V.priC} stopOpacity="1" />
        </linearGradient>
        <linearGradient id="rFace2Grad" x1="100%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" stopColor={V.sec2} stopOpacity="1" />
          <stop offset="100%" stopColor={V.sec} stopOpacity="1" />
        </linearGradient>
        <linearGradient id="rFace3Grad" x1="50%" y1="100%" x2="50%" y2="0%">
          <stop offset="0%" stopColor={V.sec} stopOpacity="1" />
          <stop offset="100%" stopColor={V.surf2} stopOpacity="1" />
        </linearGradient>
        <linearGradient id="rFace4Grad" x1="0%" y1="100%" x2="100%" y2="0%">
          <stop offset="0%" stopColor={V.pri} stopOpacity="1" />
          <stop offset="100%" stopColor={V.sec2} stopOpacity="1" />
        </linearGradient>

        <radialGradient id="rCoreGlow" cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor="#ffffff" stopOpacity="1" />
          <stop offset="35%" stopColor={V.pri} stopOpacity="0.9" />
          <stop offset="100%" stopColor={V.sec} stopOpacity="0" />
        </radialGradient>

        {/* Wide enough to clear 3 sigma of blur on every side of the mote. */}
        <filter id="rGlow" x="-200%" y="-200%" width="500%" height="500%">
          <feGaussianBlur stdDeviation="3" result="blur" />
          <feComposite in="SourceGraphic" in2="blur" operator="over" />
        </filter>

        {/* Path fraction 0 - 0.5 sweeps the near half, 0.5 - 1 the far half. */}
        <path id="rOrbitTrack" d="M 56 256 A 200 65 0 1 0 456 256 A 200 65 0 1 0 56 256" />
      </defs>

      {/* ---- far layer ---------------------------------------------------- */}
      <g stroke={V.outv} strokeWidth="1.5" fill="none" opacity="0.6">
        <polyline points="82,256 256,180 430,256" />
        <polyline points="22,256 256,120 490,256" />
        <line x1="256" y1="20" x2="256" y2="492" strokeDasharray="4 6" />
        <line x1="22" y1="256" x2="490" y2="256" strokeDasharray="4 6" />
      </g>
      <path
        d="M 56 256 A 200 65 0 0 1 456 256"
        fill="none"
        stroke={V.sec2}
        strokeOpacity="0.16"
        strokeWidth="2"
        strokeDasharray="4 12"
      />
      <path
        d="M 41 256 A 215 72 0 0 1 471 256"
        fill="none"
        stroke={V.outv}
        strokeWidth="1"
        strokeOpacity="0.55"
      />
      <g opacity="0.6">
        <Motes layer="far" animated={animated} token={token} />
      </g>

      {/* ---- the solid ---------------------------------------------------- */}
      <Pyramid
        className="bot-crystal-r"
        data={CRYSTAL.bot}
        staticData={CRYSTAL.botStatic}
        animated={animated}
        surf={V.surf}
        rim={V.sec}
        ink={V.ink}
      />
      <Pyramid
        className="top-crystal-r"
        data={CRYSTAL.top}
        staticData={CRYSTAL.topStatic}
        animated={animated}
        surf={V.surf}
        rim={V.pri}
        ink={V.ink}
      />

      {/* ---- near layer --------------------------------------------------- */}
      <path
        d="M 56 256 A 200 65 0 0 0 456 256"
        fill="none"
        stroke={V.sec2}
        strokeOpacity="0.38"
        strokeWidth="2"
        strokeDasharray="4 12"
      />
      <path d="M 41 256 A 215 72 0 0 0 471 256" fill="none" stroke={V.outv} strokeWidth="1" />
      <g>
        <Motes layer="near" animated={animated} token={token} />
      </g>
      <g stroke={V.outv} strokeWidth="1.5" fill="none" opacity="0.6">
        <polyline points="430,256 256,332 82,256" />
        <polyline points="490,256 256,392 22,256" />
      </g>

      {/* CORE NODE */}
      <g className="core-anim-r">
        <circle cx="256" cy="256" r="45" fill="url(#rCoreGlow)" />
        <polygon points="256,242 270,256 256,270 242,256" fill={V.ink} />
      </g>

      {showTitle && (
        <g transform="translate(0, 522)">
          <text x="256" y="0" textAnchor="middle" className="logo-title-r">
            LINKPOINT
          </text>
          <text x="256" y="28" textAnchor="middle" className="logo-subtitle-r">
            SECONDLIFE COMMUNICATOR
          </text>
        </g>
      )}
    </svg>
  );
}
