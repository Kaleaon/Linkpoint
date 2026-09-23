import React, { useState } from "react";
import { useTheme } from "../context/ThemeContext.jsx";

export default function LinkpointLogo({ animated = true, showTitle = true, width = "100%", height = "auto" }) {
  const { V, t } = useTheme();

  // width/height go through style rather than the SVG presentation attributes:
  // callers pass CSS values ("100%", "auto") and an SVG attribute only accepts
  // a length, so height="auto" was rejected by the browser with
  // `<svg> attribute height: Expected length, "auto"` on every Login render.
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      xmlnsXlink="http://www.w3.org/1999/xlink"
      id="linkpoint-logo-react"
      viewBox={showTitle ? "0 0 512 580" : "0 0 512 512"}
      style={{ width, height, filter: "drop-shadow(0 4px 16px rgba(0,0,0,0.3))" }}
    >
      <style>{`
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
          35%, 65% { transform: translateY(65px); }
          85%, 100% { transform: translateY(0); }
        }

        @keyframes botCloseSeq {
          0%, 15% { transform: translateY(0); }
          35%, 65% { transform: translateY(-65px); }
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
          <stop offset="0%" stopColor={V.pri} stopOpacity="0.95" />
          <stop offset="100%" stopColor={V.priC} stopOpacity="0.8" />
        </linearGradient>
        <linearGradient id="rFace2Grad" x1="100%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" stopColor={V.sec2} stopOpacity="0.9" />
          <stop offset="100%" stopColor={V.sec} stopOpacity="0.8" />
        </linearGradient>
        <linearGradient id="rFace3Grad" x1="50%" y1="100%" x2="50%" y2="0%">
          <stop offset="0%" stopColor={V.sec} stopOpacity="0.9" />
          <stop offset="100%" stopColor={V.surf2} stopOpacity="0.8" />
        </linearGradient>
        <linearGradient id="rFace4Grad" x1="0%" y1="100%" x2="100%" y2="0%">
          <stop offset="0%" stopColor={V.pri} stopOpacity="0.9" />
          <stop offset="100%" stopColor={V.sec2} stopOpacity="0.8" />
        </linearGradient>

        <radialGradient id="rCoreGlow" cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor="#ffffff" stopOpacity="1" />
          <stop offset="35%" stopColor={V.pri} stopOpacity="0.9" />
          <stop offset="100%" stopColor={V.sec} stopOpacity="0" />
        </radialGradient>

        <filter id="rGlow" x="-50%" y="-50%" width="200%" height="200%">
          <feGaussianBlur stdDeviation="3" result="blur" />
          <feComposite in="SourceGraphic" in2="blur" operator="over" />
        </filter>

        <path id="rOrbitTrack" d="M 56 256 A 200 65 0 1 0 456 256 A 200 65 0 1 0 56 256" />
      </defs>

      {/* Isometric Space Grid */}
      <g stroke={V.outv} strokeWidth="1.5" fill="none" opacity="0.6">
        <polygon points="256,180 430,256 256,332 82,256" />
        <polygon points="256,120 490,256 256,392 22,256" />
        <line x1="256" y1="20" x2="256" y2="492" strokeDasharray="4 6" />
        <line x1="22" y1="256" x2="490" y2="256" strokeDasharray="4 6" />
      </g>

      {/* Static Orbital Ring Path */}
      <ellipse cx="256" cy="256" rx="200" ry="65" fill="none" stroke={V.sec2} strokeOpacity="0.3" strokeWidth="2" strokeDasharray="4 12" />
      <ellipse cx="256" cy="256" rx="215" ry="72" fill="none" stroke={V.outv} strokeWidth="1" />

      {/* Orbiting Particles */}
      <g>
        {animated ? (
          <>
            <circle r="4" fill={V.pri} filter="url(#rGlow)">
              <animateMotion dur="4s" repeatCount="indefinite" begin="0s">
                <mpath href="#rOrbitTrack" />
              </animateMotion>
            </circle>
            <circle r="5" fill={V.sec2} filter="url(#rGlow)">
              <animateMotion dur="4s" repeatCount="indefinite" begin="-1.33s">
                <mpath href="#rOrbitTrack" />
              </animateMotion>
            </circle>
            <circle r="3" fill={V.sec} filter="url(#rGlow)">
              <animateMotion dur="4s" repeatCount="indefinite" begin="-2.66s">
                <mpath href="#rOrbitTrack" />
              </animateMotion>
            </circle>
          </>
        ) : (
          <>
            <circle cx="56" cy="256" r="4" fill={V.pri} filter="url(#rGlow)" />
            <circle cx="280" cy="192" r="5" fill={V.sec2} filter="url(#rGlow)" />
            <circle cx="430" cy="280" r="3" fill={V.sec} filter="url(#rGlow)" />
          </>
        )}
      </g>

      {/* TOP PYRAMID */}
      <g className="top-crystal-r">
        <polygon fill={V.surf} stroke={V.pri} strokeOpacity="0.4" strokeWidth="1.5">
          {animated && (
            <animate
              attributeName="points"
              dur="4s"
              repeatCount="indefinite"
              calcMode="linear"
              values="392,170 256,220 120,170 256,120; 352,205 160,205 160,135 352,135; 256,220 120,170 256,120 392,170; 160,205 160,135 352,135 352,205; 120,170 256,120 392,170 256,220; 160,135 352,135 352,205 160,205; 256,120 392,170 256,220 120,170; 352,135 352,205 160,205 160,135; 392,170 256,220 120,170 256,120"
            />
          )}
          {!animated && <polygon points="392,170 256,220 120,170 256,120" />}
        </polygon>

        <polygon fill="url(#rFace1Grad)" stroke={V.ink} strokeWidth="1.5" strokeJoin="round" style={{ mixBlendMode: "screen" }}>
          {animated ? (
            <>
              <animate attributeName="points" dur="4s" repeatCount="indefinite" calcMode="linear" values="256,40 392,170 256,220; 256,40 352,205 160,205; 256,40 256,220 120,170; 256,40 160,205 160,135; 256,40 120,170 256,120; 256,40 160,135 352,135; 256,40 256,120 392,170; 256,40 352,135 352,205; 256,40 392,170 256,220" />
              <animate attributeName="opacity" dur="4s" repeatCount="indefinite" calcMode="linear" values="0.95; 0.95; 0.95; 0; 0; 0; 0; 0; 0.95" />
            </>
          ) : (
            <polygon points="256,40 392,170 256,220" />
          )}
        </polygon>

        <polygon fill="url(#rFace2Grad)" stroke={V.ink} strokeWidth="1.5" strokeJoin="round" style={{ mixBlendMode: "screen" }}>
          {animated ? (
            <>
              <animate attributeName="points" dur="4s" repeatCount="indefinite" calcMode="linear" values="256,40 256,220 120,170; 256,40 160,205 160,135; 256,40 120,170 256,120; 256,40 160,135 352,135; 256,40 256,120 392,170; 256,40 352,135 352,205; 256,40 392,170 256,220; 256,40 352,205 160,205; 256,40 256,220 120,170" />
              <animate attributeName="opacity" dur="4s" repeatCount="indefinite" calcMode="linear" values="0.95; 0; 0; 0; 0; 0; 0.95; 0.95; 0.95" />
            </>
          ) : (
            <polygon points="256,40 256,220 120,170" />
          )}
        </polygon>

        <polygon fill="url(#rFace3Grad)" stroke={V.ink2} strokeWidth="1" strokeJoin="round">
          {animated ? (
            <>
              <animate attributeName="points" dur="4s" repeatCount="indefinite" calcMode="linear" values="256,40 120,170 256,120; 256,40 160,135 352,135; 256,40 256,120 392,170; 256,40 352,135 352,205; 256,40 392,170 256,220; 256,40 352,205 160,205; 256,40 256,220 120,170; 256,40 160,205 160,135; 256,40 120,170 256,120" />
              <animate attributeName="opacity" dur="4s" repeatCount="indefinite" calcMode="linear" values="0; 0; 0; 0; 0.95; 0.95; 0.95; 0; 0" />
            </>
          ) : (
            <polygon points="256,40 120,170 256,120" />
          )}
        </polygon>

        {animated && (
          <polygon fill="url(#rFace4Grad)" stroke={V.ink2} strokeWidth="1" strokeJoin="round">
            <animate attributeName="points" dur="4s" repeatCount="indefinite" calcMode="linear" values="256,40 256,120 392,170; 256,40 352,135 352,205; 256,40 392,170 256,220; 256,40 352,205 160,205; 256,40 256,220 120,170; 256,40 160,205 160,135; 256,40 120,170 256,120; 256,40 160,135 352,135; 256,40 256,120 392,170" />
            <animate attributeName="opacity" dur="4s" repeatCount="indefinite" calcMode="linear" values="0; 0; 0.95; 0.95; 0.95; 0; 0; 0; 0" />
          </polygon>
        )}
      </g>

      {/* BOTTOM PYRAMID */}
      <g className="bot-crystal-r">
        <polygon fill={V.surf} stroke={V.sec} strokeOpacity="0.4" strokeWidth="1.5">
          {animated ? (
            <animate attributeName="points" dur="4s" repeatCount="indefinite" calcMode="linear" values="392,342 256,392 120,342 256,292; 352,377 160,377 160,307 352,307; 256,392 120,342 256,292 392,342; 160,377 160,307 352,307 352,377; 120,342 256,292 392,342 256,392; 160,307 352,307 352,377 160,377; 256,292 392,342 256,392 120,342; 352,307 352,377 160,377 160,307; 392,342 256,392 120,342 256,292" />
          ) : (
            <polygon points="392,342 256,392 120,342 256,292" />
          )}
        </polygon>

        <polygon fill="url(#rFace2Grad)" stroke={V.ink} strokeWidth="1.5" strokeJoin="round" style={{ mixBlendMode: "screen" }}>
          {animated ? (
            <>
              <animate attributeName="points" dur="4s" repeatCount="indefinite" calcMode="linear" values="256,472 392,342 256,392; 256,472 352,377 160,377; 256,472 256,392 120,342; 256,472 160,377 160,307; 256,472 120,342 256,292; 256,472 160,307 352,307; 256,472 256,292 392,342; 256,472 352,307 352,377; 256,472 392,342 256,392" />
              <animate attributeName="opacity" dur="4s" repeatCount="indefinite" calcMode="linear" values="0.95; 0.95; 0.95; 0; 0; 0; 0; 0; 0.95" />
            </>
          ) : (
            <polygon points="256,472 392,342 256,392" />
          )}
        </polygon>

        <polygon fill="url(#rFace3Grad)" stroke={V.ink} strokeWidth="1.5" strokeJoin="round" style={{ mixBlendMode: "screen" }}>
          {animated ? (
            <>
              <animate attributeName="points" dur="4s" repeatCount="indefinite" calcMode="linear" values="256,472 256,392 120,342; 256,472 160,377 160,307; 256,472 120,342 256,292; 256,472 160,307 352,307; 256,472 256,292 392,342; 256,472 352,307 352,377; 256,472 392,342 256,392; 256,472 352,377 160,377; 256,472 256,392 120,342" />
              <animate attributeName="opacity" dur="4s" repeatCount="indefinite" calcMode="linear" values="0.95; 0; 0; 0; 0; 0; 0.95; 0.95; 0.95" />
            </>
          ) : (
            <polygon points="256,472 256,392 120,342" />
          )}
        </polygon>

        <polygon fill="url(#rFace1Grad)" stroke={V.ink2} strokeWidth="1" strokeJoin="round">
          {animated ? (
            <>
              <animate attributeName="points" dur="4s" repeatCount="indefinite" calcMode="linear" values="256,472 120,342 256,292; 256,472 160,307 352,307; 256,472 256,292 392,342; 256,472 352,307 352,377; 256,472 392,342 256,392; 256,472 352,377 160,377; 256,472 256,392 120,342; 256,472 160,377 160,307; 256,472 120,342 256,292" />
              <animate attributeName="opacity" dur="4s" repeatCount="indefinite" calcMode="linear" values="0; 0; 0; 0; 0.95; 0.95; 0.95; 0; 0" />
            </>
          ) : (
            <polygon points="256,472 120,342 256,292" />
          )}
        </polygon>
      </g>

      {/* CORE NODE */}
      <g className="core-anim-r">
        <circle cx="256" cy="256" r="45" fill="url(#rCoreGlow)" />
        <polygon points="256,242 270,256 256,270 242,256" fill={V.ink} />
      </g>

      {/* TITLE TYPOGRAPHY */}
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
