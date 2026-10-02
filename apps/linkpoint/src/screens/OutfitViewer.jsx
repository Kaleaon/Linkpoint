import { useEffect, useRef, useState, useMemo, useCallback } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app.ts";
import Icon from "../components/Icon.jsx";

// Default shape slider values (0-100 scale)
export const DEFAULT_SHAPE_VALUES = {
  // Body & General
  height: 50,
  bodySize: 50,
  bodyFat: 25,
  torsoLength: 50,
  legLength: 50,
  // Head & Face
  headSize: 50,
  neckLength: 50,
  eyeSize: 50,
  noseSize: 50,
  mouthWidth: 50,
  earSize: 50,
  // Upper Body
  bustSize: 50,
  shoulderWidth: 50,
  armLength: 50,
  handSize: 50,
  // Lower Body
  hipWidth: 50,
  buttSize: 50,
  legFat: 25,
  footSize: 50,
};

// Preset shapes
export const SHAPE_PRESETS = {
  Default: { ...DEFAULT_SHAPE_VALUES },
  Athletic: {
    ...DEFAULT_SHAPE_VALUES,
    height: 65,
    bodySize: 60,
    bodyFat: 15,
    shoulderWidth: 65,
    bustSize: 55,
    legLength: 60,
  },
  Petite: {
    ...DEFAULT_SHAPE_VALUES,
    height: 25,
    bodySize: 35,
    bodyFat: 20,
    headSize: 45,
    torsoLength: 40,
    legLength: 35,
  },
  Tall: {
    ...DEFAULT_SHAPE_VALUES,
    height: 85,
    bodySize: 50,
    legLength: 80,
    torsoLength: 70,
    armLength: 70,
  },
  Voluptuous: {
    ...DEFAULT_SHAPE_VALUES,
    height: 55,
    bodySize: 65,
    bodyFat: 45,
    bustSize: 75,
    hipWidth: 70,
    buttSize: 70,
    legFat: 45,
  },
};

// Shape slider definitions grouped by category
const SHAPE_CATEGORIES = [
  {
    id: "general",
    label: "Body & Height",
    sliders: [
      { id: "height", name: "Height", min: 0, max: 100 },
      { id: "bodySize", name: "Body Size", min: 0, max: 100 },
      { id: "bodyFat", name: "Body Fat", min: 0, max: 100 },
      { id: "torsoLength", name: "Torso Length", min: 0, max: 100 },
      { id: "legLength", name: "Leg Length", min: 0, max: 100 },
    ],
  },
  {
    id: "head",
    label: "Head & Face",
    sliders: [
      { id: "headSize", name: "Head Size", min: 0, max: 100 },
      { id: "neckLength", name: "Neck Length", min: 0, max: 100 },
      { id: "eyeSize", name: "Eye Size", min: 0, max: 100 },
      { id: "noseSize", name: "Nose Size", min: 0, max: 100 },
      { id: "mouthWidth", name: "Mouth Width", min: 0, max: 100 },
      { id: "earSize", name: "Ear Size", min: 0, max: 100 },
    ],
  },
  {
    id: "upper",
    label: "Upper Body",
    sliders: [
      { id: "bustSize", name: "Bust / Chest Size", min: 0, max: 100 },
      { id: "shoulderWidth", name: "Shoulder Width", min: 0, max: 100 },
      { id: "armLength", name: "Arm Length", min: 0, max: 100 },
      { id: "handSize", name: "Hand Size", min: 0, max: 100 },
    ],
  },
  {
    id: "lower",
    label: "Lower Body",
    sliders: [
      { id: "hipWidth", name: "Hip Width", min: 0, max: 100 },
      { id: "buttSize", name: "Butt Size", min: 0, max: 100 },
      { id: "legFat", name: "Leg Fat", min: 0, max: 100 },
      { id: "footSize", name: "Foot Size", min: 0, max: 100 },
    ],
  },
];

// Fallback sample outfit items when inventory is empty / loading
const DEMO_OUTFIT_ITEMS = [
  { id: "item-head-shape", name: "Default Shape", assetType: 13, category: "body", worn: true, typeName: "Shape" },
  { id: "item-skin-1", name: "Natural Tone Skin", assetType: 18, category: "body", worn: true, typeName: "Skin" },
  { id: "item-eyes-1", name: "Hazel Eyes", assetType: 19, category: "body", worn: true, typeName: "Eyes" },
  { id: "item-hair-1", name: "Classic Wavy Hair", assetType: 20, category: "body", worn: true, typeName: "Hair" },
  { id: "item-shirt-1", name: "Urban Casual Denim Jacket", assetType: 5, category: "clothing", worn: true, typeName: "Shirt" },
  { id: "item-pants-1", name: "Slim Fit Jeans", assetType: 5, category: "clothing", worn: true, typeName: "Pants" },
  { id: "item-shoes-1", name: "Leather Boots", assetType: 5, category: "clothing", worn: true, typeName: "Shoes" },
  { id: "item-attach-1", name: "Mesh Watch (Left Wrist)", assetType: 6, category: "attachment", worn: true, typeName: "Attachment" },
  { id: "item-attach-2", name: "Silver Pendant", assetType: 6, category: "attachment", worn: false, typeName: "Attachment" },
];

/**
 * Full Outfit Viewer Component
 * Displays 3D Avatar in its own canvas viewport, shape sliders, mesh viewing controls,
 * 3D rotation, and outfit item management.
 */
export default function OutfitViewer() {
  const { V, t } = useTheme();
  const [activePanel, setActivePanel] = useState("viewport"); // "viewport" | "outfit" | "shape" | "mesh"
  const [shapeValues, setShapeValues] = useState(DEFAULT_SHAPE_VALUES);
  const [activeShapeCategory, setActiveShapeCategory] = useState("general");
  const [meshMode, setMeshMode] = useState("textured"); // "textured" | "wireframe" | "solid"
  const [showJoints, setShowJoints] = useState(false);
  const [autoRotate, setAutoRotate] = useState(false);
  const [cameraPreset, setCameraPreset] = useState("front"); // "front" | "back" | "face" | "side" | "full"
  const [yawAngle, setYawAngle] = useState(0); // in degrees
  const [pitchAngle, setPitchAngle] = useState(0); // in degrees
  const [zoomLevel, setZoomLevel] = useState(1.0); // scale factor
  const [searchQuery, setSearchQuery] = useState("");
  const [categoryFilter, setCategoryFilter] = useState("all");
  const [wornItems, setWornItems] = useState(DEMO_OUTFIT_ITEMS);
  const [statusMessage, setStatusMessage] = useState("");

  const canvasRef = useRef(null);

  // Sync inventory worn items if available
  useEffect(() => {
    const syncInventory = () => {
      const invItems = Array.from(app.inventory.items.values());
      const wearableTypes = new Set([5, 13, 18, 19, 20, 21, 22, 23, 24]);
      const mapped = invItems
        .filter((it) => wearableTypes.has(Number(it.assetType)) || Number(it.assetType) === 6)
        .map((it) => ({
          id: it.id,
          name: it.name || "Unnamed Wearable",
          assetType: it.assetType,
          category: Number(it.assetType) === 6 ? "attachment" : [13, 18, 19, 20].includes(Number(it.assetType)) ? "body" : "clothing",
          worn: true,
          typeName: Number(it.assetType) === 13 ? "Shape" : Number(it.assetType) === 6 ? "Attachment" : "Clothing",
        }));
      if (mapped.length > 0) {
        setWornItems(mapped);
      }
    };
    syncInventory();
    app.inventory.on("inventory_loaded", syncInventory);
    app.inventory.on("inventory_updated", syncInventory);
    return () => {
      app.inventory.off("inventory_loaded", syncInventory);
      app.inventory.off("inventory_updated", syncInventory);
    };
  }, []);

  // Camera presets
  const applyCameraPreset = useCallback((preset) => {
    setCameraPreset(preset);
    switch (preset) {
      case "front":
        setYawAngle(0);
        setPitchAngle(0);
        setZoomLevel(1.0);
        break;
      case "back":
        setYawAngle(180);
        setPitchAngle(0);
        setZoomLevel(1.0);
        break;
      case "face":
        setYawAngle(0);
        setPitchAngle(10);
        setZoomLevel(2.2);
        break;
      case "side":
        setYawAngle(90);
        setPitchAngle(0);
        setZoomLevel(1.0);
        break;
      case "full":
        setYawAngle(0);
        setPitchAngle(-5);
        setZoomLevel(0.8);
        break;
      default:
        setYawAngle(0);
        setPitchAngle(0);
        setZoomLevel(1.0);
    }
  }, []);

  // Auto rotation effect
  useEffect(() => {
    if (!autoRotate) return;
    let animId;
    let lastTime = performance.now();
    const animate = (now) => {
      const delta = (now - lastTime) / 1000;
      lastTime = now;
      setYawAngle((prev) => (prev + delta * 30) % 360);
      animId = requestAnimationFrame(animate);
    };
    animId = requestAnimationFrame(animate);
    return () => cancelAnimationFrame(animId);
  }, [autoRotate]);

  // 3D Canvas rendering loop
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    let active = true;
    const render = () => {
      if (!active) return;
      const width = canvas.clientWidth || 400;
      const height = canvas.clientHeight || 500;
      if (canvas.width !== width || canvas.height !== height) {
        canvas.width = width;
        canvas.height = height;
      }

      // Clear background
      ctx.fillStyle = V.bg || "#0f172a";
      ctx.fillRect(0, 0, width, height);

      // Draw subtle background grid
      ctx.strokeStyle = "rgba(255, 255, 255, 0.05)";
      ctx.lineWidth = 1;
      const gridSize = 30;
      for (let x = 0; x < width; x += gridSize) {
        ctx.beginPath();
        ctx.moveTo(x, 0);
        ctx.lineTo(x, height);
        ctx.stroke();
      }
      for (let y = 0; y < height; y += gridSize) {
        ctx.beginPath();
        ctx.moveTo(0, y);
        ctx.lineTo(width, y);
        ctx.stroke();
      }

      // Calculate avatar center and metrics from shape sliders
      const centerX = width / 2;
      const centerY = height / 2 + (shapeValues.height - 50) * 0.5;

      const yawRad = (yawAngle * Math.PI) / 180;
      const pitchRad = (pitchAngle * Math.PI) / 180;
      const cosY = Math.cos(yawRad);
      const sinY = Math.sin(yawRad);
      const cosP = Math.cos(pitchRad);

      // Shape scaling factors
      const totalScale = zoomLevel * (0.8 + (shapeValues.height / 100) * 0.4);
      const headScale = (0.8 + (shapeValues.headSize / 100) * 0.4) * totalScale;
      const shoulderScale = (0.7 + (shapeValues.shoulderWidth / 100) * 0.6) * totalScale;
      const torsoScaleY = (0.7 + (shapeValues.torsoLength / 100) * 0.6) * totalScale;
      const legScaleY = (0.7 + (shapeValues.legLength / 100) * 0.6) * totalScale;
      const hipScale = (0.7 + (shapeValues.hipWidth / 100) * 0.6) * totalScale;
      const bustBonus = (shapeValues.bustSize / 100) * 12 * totalScale;

      // Body joint positions relative to center
      const joints = [
        { name: "head", x: 0, y: -130 * torsoScaleY, z: 0, radius: 24 * headScale },
        { name: "neck", x: 0, y: -95 * torsoScaleY, z: 0, radius: 10 * totalScale },
        { name: "chest", x: 0, y: -50 * torsoScaleY, z: 0, radius: (28 + bustBonus) * shoulderScale },
        { name: "waist", x: 0, y: 0, z: 0, radius: 22 * totalScale },
        { name: "hips", x: 0, y: 40 * torsoScaleY, z: 0, radius: 26 * hipScale },
        { name: "leftShoulder", x: -35 * shoulderScale, y: -65 * torsoScaleY, z: 0, radius: 8 * totalScale },
        { name: "rightShoulder", x: 35 * shoulderScale, y: -65 * torsoScaleY, z: 0, radius: 8 * totalScale },
        { name: "leftElbow", x: -45 * shoulderScale, y: -10 * torsoScaleY, z: 0, radius: 7 * totalScale },
        { name: "rightElbow", x: 45 * shoulderScale, y: -10 * torsoScaleY, z: 0, radius: 7 * totalScale },
        { name: "leftKnee", x: -18 * hipScale, y: 110 * legScaleY, z: 0, radius: 9 * totalScale },
        { name: "rightKnee", x: 18 * hipScale, y: 110 * legScaleY, z: 0, radius: 9 * totalScale },
        { name: "leftAnkle", x: -18 * hipScale, y: 180 * legScaleY, z: 0, radius: 8 * totalScale },
        { name: "rightAnkle", x: 18 * hipScale, y: 180 * legScaleY, z: 0, radius: 8 * totalScale },
      ];

      // Project 3D points with rotation & pitch
      const projected = joints.map((j) => {
        // Rotate around Y
        const rx = j.x * cosY - j.z * sinY;
        const rz = j.x * sinY + j.z * cosY;
        // Pitch rotation
        const ry = j.y * cosP - rz * Math.sin(pitchRad);
        return {
          ...j,
          px: centerX + rx,
          py: centerY + ry,
          pz: rz,
        };
      });

      // Draw mesh geometry / avatar body parts
      const isWireframe = meshMode === "wireframe";
      const mainColor = isWireframe ? (V.pri || "#38bdf8") : (V.sec2 || "#a855f7");
      const bodyColor = isWireframe ? "rgba(56, 189, 248, 0.2)" : "rgba(168, 85, 247, 0.4)";
      const strokeColor = isWireframe ? (V.pri || "#38bdf8") : (V.outv || "#64748b");

      ctx.lineWidth = isWireframe ? 1 : 2;

      // Draw torso & limbs connecting lines/mesh
      const bones = [
        ["head", "neck"],
        ["neck", "chest"],
        ["chest", "waist"],
        ["waist", "hips"],
        ["chest", "leftShoulder"],
        ["chest", "rightShoulder"],
        ["leftShoulder", "leftElbow"],
        ["rightShoulder", "rightElbow"],
        ["hips", "leftKnee"],
        ["hips", "rightKnee"],
        ["leftKnee", "leftAnkle"],
        ["rightKnee", "rightAnkle"],
      ];

      // Draw skeleton / wireframe limbs
      ctx.strokeStyle = strokeColor;
      bones.forEach(([from, to]) => {
        const p1 = projected.find((p) => p.name === from);
        const p2 = projected.find((p) => p.name === to);
        if (p1 && p2) {
          ctx.beginPath();
          ctx.moveTo(p1.px, p1.py);
          ctx.lineTo(p2.px, p2.py);
          ctx.stroke();

          // If wireframe, draw mesh crosshatch lines along bones
          if (isWireframe) {
            const steps = 4;
            for (let i = 1; i < steps; i++) {
              const t = i / steps;
              const mx = p1.px + (p2.px - p1.px) * t;
              const my = p1.py + (p2.py - p1.py) * t;
              const dx = (p2.py - p1.py) * 0.15;
              const dy = -(p2.px - p1.px) * 0.15;
              ctx.beginPath();
              ctx.moveTo(mx - dx, my - dy);
              ctx.lineTo(mx + dx, my + dy);
              ctx.stroke();
            }
          }
        }
      });

      // Draw body part volumes (Head, Chest, Hips, Limbs)
      projected.forEach((p) => {
        ctx.fillStyle = bodyColor;
        ctx.strokeStyle = mainColor;
        ctx.beginPath();
        ctx.arc(p.px, p.py, Math.max(3, p.radius), 0, Math.PI * 2);
        ctx.fill();
        ctx.stroke();

        if (isWireframe) {
          // Draw wireframe latitude & longitude rings on head / chest
          ctx.beginPath();
          ctx.ellipse(p.px, p.py, p.radius, p.radius * 0.4, cosY, 0, Math.PI * 2);
          ctx.stroke();
        }
      });

      // Draw Head Details (Eyes & Hair indicator)
      const headPt = projected.find((p) => p.name === "head");
      if (headPt) {
        const eyeOffset = (shapeValues.eyeSize / 100) * 6 * totalScale;
        const eyeDx = 10 * headScale * cosY;
        const eyeDy = 10 * headScale * Math.sin(pitchRad);
        
        ctx.fillStyle = V.pri || "#38bdf8";
        // Left & Right eye points
        ctx.beginPath();
        ctx.arc(headPt.px - eyeDx, headPt.py - 4 * headScale + eyeDy, Math.max(2, eyeOffset), 0, Math.PI * 2);
        ctx.arc(headPt.px + eyeDx, headPt.py - 4 * headScale + eyeDy, Math.max(2, eyeOffset), 0, Math.PI * 2);
        ctx.fill();
      }

      // Draw Joint Markers if enabled
      if (showJoints) {
        ctx.fillStyle = "#ef4444";
        projected.forEach((p) => {
          ctx.beginPath();
          ctx.arc(p.px, p.py, 4, 0, Math.PI * 2);
          ctx.fill();
        });
      }

      // Draw Viewport Status / Info Overlay
      ctx.fillStyle = V.ink || "#f8fafc";
      ctx.font = `600 11px ${t.font || "sans-serif"}`;
      ctx.fillText(`AVATAR VIEWPORT — YAW: ${Math.round(yawAngle)}° | PITCH: ${Math.round(pitchAngle)}°`, 12, 20);
      ctx.fillStyle = V.ink2 || "#94a3b8";
      ctx.font = `400 10px ${t.font || "sans-serif"}`;
      ctx.fillText(`Mesh Mode: ${meshMode.toUpperCase()} | Shape Height: ${shapeValues.height} | Zoom: ${zoomLevel.toFixed(1)}x`, 12, 36);
    };

    render();
  }, [yawAngle, pitchAngle, zoomLevel, shapeValues, meshMode, showJoints, V, t]);

  // Pointer drag for 3D rotation
  const isDragging = useRef(false);
  const dragStart = useRef({ x: 0, y: 0 });

  const handlePointerDown = (e) => {
    isDragging.current = true;
    dragStart.current = { x: e.clientX, y: e.clientY };
  };

  const handlePointerMove = (e) => {
    if (!isDragging.current) return;
    const dx = e.clientX - dragStart.current.x;
    const dy = e.clientY - dragStart.current.y;
    dragStart.current = { x: e.clientX, y: e.clientY };

    setYawAngle((prev) => (prev + dx * 0.5) % 360);
    setPitchAngle((prev) => Math.max(-60, Math.min(60, prev - dy * 0.5)));
  };

  const handlePointerUp = () => {
    isDragging.current = false;
  };

  const handleWheel = (e) => {
    e.preventDefault();
    setZoomLevel((prev) => Math.max(0.4, Math.min(3.0, prev - e.deltaY * 0.002)));
  };

  // Shape slider handler
  const handleSliderChange = (id, val) => {
    setShapeValues((prev) => ({ ...prev, [id]: Number(val) }));
  };

  const handlePresetSelect = (presetKey) => {
    if (SHAPE_PRESETS[presetKey]) {
      setShapeValues({ ...SHAPE_PRESETS[presetKey] });
      setStatusMessage(`Applied ${presetKey} shape preset.`);
      setTimeout(() => setStatusMessage(""), 3000);
    }
  };

  const handleResetShape = () => {
    setShapeValues({ ...DEFAULT_SHAPE_VALUES });
    setStatusMessage("Reset shape sliders to default.");
    setTimeout(() => setStatusMessage(""), 3000);
  };

  const handleSaveShape = () => {
    app.inventory.emit("shape_updated", { shapeValues });
    setStatusMessage("Shape saved & applied to avatar.");
    setTimeout(() => setStatusMessage(""), 3000);
  };

  // Filter worn items
  const filteredItems = useMemo(() => {
    const q = searchQuery.toLowerCase().trim();
    return wornItems.filter((item) => {
      const matchQ = !q || item.name.toLowerCase().includes(q) || item.typeName.toLowerCase().includes(q);
      const matchCat = categoryFilter === "all" || item.category === categoryFilter || (categoryFilter === "worn" && item.worn);
      return matchQ && matchCat;
    });
  }, [wornItems, searchQuery, categoryFilter]);

  const toggleItemWorn = (itemId) => {
    setWornItems((prev) =>
      prev.map((it) => (it.id === itemId ? { ...it, worn: !it.worn } : it))
    );
  };

  return (
    <section className="live-screen outfit-viewer-screen" style={{ display: "flex", flexDirection: "column", gap: 10, padding: 12, height: "100%", overflowY: "auto" }}>
      {/* Top Header & Navigation Tabs */}
      <header style={{ display: "flex", justifyContent: "space-between", alignItems: "center", borderBottom: `1px solid ${V.outv}`, pb: 8 }}>
        <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
          <Icon name="shirt" size={20} style={{ color: V.pri }} />
          <div>
            <h1 style={{ margin: 0, fontSize: 16, fontWeight: 700, color: V.ink }}>FULL OUTFIT VIEWER</h1>
            <span style={{ fontSize: 11, color: V.ink2 }}>Avatar 3D Viewport, Mesh Inspection & Shape Tuning</span>
          </div>
        </div>

        {/* Panel Switcher */}
        <div style={{ display: "flex", gap: 4, background: V.surf, padding: 3, borderRadius: V.rs, border: `1px solid ${V.outv}` }}>
          {[
            ["viewport", "3D View", "eye"],
            ["outfit", "Outfit Items", "package"],
            ["shape", "Shape Sliders", "sliders"],
            ["mesh", "Mesh View", "box"],
          ].map(([id, label, icon]) => (
            <button
              key={id}
              type="button"
              onClick={() => setActivePanel(id)}
              style={{
                display: "flex",
                alignItems: "center",
                gap: 5,
                padding: "6px 12px",
                fontSize: 11,
                fontWeight: 600,
                border: "none",
                borderRadius: V.rs,
                background: activePanel === id ? V.pri : "transparent",
                color: activePanel === id ? (V.onpri || "#fff") : V.ink,
                cursor: "pointer",
              }}
            >
              <Icon name={icon} size={14} />
              {label}
            </button>
          ))}
        </div>
      </header>

      {statusMessage ? (
        <div style={{ padding: "6px 12px", background: "rgba(56, 189, 248, 0.15)", border: `1px solid ${V.pri}`, borderRadius: V.rs, fontSize: 12, color: V.pri, fontWeight: 600 }}>
          {statusMessage}
        </div>
      ) : null}

      {/* Main Content Layout: Viewport (Left) + Selected Control Panel (Right) */}
      <div style={{ display: "flex", gap: 12, flex: 1, minHeight: 420 }}>
        {/* 3D Viewport Panel */}
        <div style={{ flex: 1.2, display: "flex", flexDirection: "column", background: V.bg, border: `1px solid ${V.outv}`, borderRadius: V.rs, position: "relative", overflow: "hidden" }}>
          {/* Interactive Canvas */}
          <canvas
            ref={canvasRef}
            aria-label="3D Avatar Viewport"
            onPointerDown={handlePointerDown}
            onPointerMove={handlePointerMove}
            onPointerUp={handlePointerUp}
            onPointerLeave={handlePointerUp}
            onWheel={handleWheel}
            style={{ width: "100%", height: "100%", cursor: "grab", touchAction: "none" }}
          />

          {/* Floating Viewport Toolbar */}
          <div style={{ position: "absolute", bottom: 10, left: 10, right: 10, display: "flex", justifyContent: "space-between", alignItems: "center", background: "rgba(15, 23, 42, 0.85)", backdropFilter: "blur(6px)", padding: "6px 10px", borderRadius: V.rs, border: `1px solid ${V.outv}` }}>
            {/* Camera Presets */}
            <div style={{ display: "flex", gap: 4, alignItems: "center" }}>
              <span style={{ fontSize: 10, color: "#94a3b8", fontWeight: 700, marginRight: 4 }}>CAMERA:</span>
              {[
                ["front", "Front"],
                ["back", "Back"],
                ["face", "Face"],
                ["side", "Side"],
                ["full", "Full"],
              ].map(([p, label]) => (
                <button
                  key={p}
                  type="button"
                  onClick={() => applyCameraPreset(p)}
                  style={{
                    padding: "3px 7px",
                    fontSize: 10,
                    fontWeight: 600,
                    border: `1px solid ${cameraPreset === p ? V.pri : "rgba(255,255,255,0.2)"}`,
                    borderRadius: V.rs,
                    background: cameraPreset === p ? V.pri : "transparent",
                    color: cameraPreset === p ? "#fff" : "#cbd5e1",
                    cursor: "pointer",
                  }}
                >
                  {label}
                </button>
              ))}
            </div>

            {/* Quick Controls */}
            <div style={{ display: "flex", gap: 6, alignItems: "center" }}>
              <button
                type="button"
                onClick={() => setAutoRotate(!autoRotate)}
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: 4,
                  padding: "3px 8px",
                  fontSize: 10,
                  fontWeight: 600,
                  borderRadius: V.rs,
                  border: `1px solid ${autoRotate ? V.sec2 : "rgba(255,255,255,0.2)"}`,
                  background: autoRotate ? "rgba(168, 85, 247, 0.3)" : "transparent",
                  color: autoRotate ? V.sec2 : "#cbd5e1",
                  cursor: "pointer",
                }}
              >
                <Icon name="rotate-cw" size={12} />
                {autoRotate ? "SPINNING" : "AUTO-SPIN"}
              </button>

              <button
                type="button"
                onClick={() => setMeshMode(meshMode === "wireframe" ? "textured" : "wireframe")}
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: 4,
                  padding: "3px 8px",
                  fontSize: 10,
                  fontWeight: 600,
                  borderRadius: V.rs,
                  border: `1px solid ${meshMode === "wireframe" ? V.pri : "rgba(255,255,255,0.2)"}`,
                  background: meshMode === "wireframe" ? "rgba(56, 189, 248, 0.3)" : "transparent",
                  color: meshMode === "wireframe" ? V.pri : "#cbd5e1",
                  cursor: "pointer",
                }}
              >
                <Icon name="grid" size={12} />
                {meshMode === "wireframe" ? "WIREFRAME" : "MESH"}
              </button>

              <button
                type="button"
                onClick={() => applyCameraPreset("front")}
                title="Reset Camera View"
                style={{ padding: "3px 6px", fontSize: 10, background: "transparent", color: "#cbd5e1", border: "1px solid rgba(255,255,255,0.2)", borderRadius: V.rs, cursor: "pointer" }}
              >
                RESET
              </button>
            </div>
          </div>
        </div>

        {/* Right Details / Control Panel */}
        <div style={{ flex: 1, display: "flex", flexDirection: "column", gap: 10, background: V.surf, border: `1px solid ${V.outv}`, borderRadius: V.rs, padding: 12, overflowY: "auto" }}>
          {/* 1. OUTFIT ITEMS PANEL */}
          {(activePanel === "outfit" || activePanel === "viewport") && (
            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <h2 style={{ margin: 0, fontSize: 13, fontWeight: 700, color: V.pri, display: "flex", alignItems: "center", gap: 6 }}>
                  <Icon name="package" size={16} /> WORN OUTFIT ITEMS ({wornItems.filter((i) => i.worn).length})
                </h2>
                <div style={{ display: "flex", gap: 4 }}>
                  <button
                    type="button"
                    onClick={async () => {
                      const res = await app.inventory.replaceOutfit("current_outfit_folder");
                      setStatusMessage("Replaced outfit in current outfit folder.");
                    }}
                    style={{ padding: "4px 8px", fontSize: 10, fontWeight: 700, background: V.pri, color: V.onpri || "#fff", border: "none", borderRadius: V.rs, cursor: "pointer" }}
                  >
                    REPLACE OUTFIT
                  </button>
                </div>
              </div>

              {/* Filter & Search */}
              <div style={{ display: "flex", gap: 6 }}>
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Filter outfit items..."
                  style={{ flex: 1, padding: "5px 8px", fontSize: 11, background: V.bg, color: V.ink, border: `1px solid ${V.outv}`, borderRadius: V.rs }}
                />
                <select
                  value={categoryFilter}
                  onChange={(e) => setCategoryFilter(e.target.value)}
                  style={{ padding: "5px 8px", fontSize: 11, background: V.bg, color: V.ink, border: `1px solid ${V.outv}`, borderRadius: V.rs }}
                >
                  <option value="all">All Items</option>
                  <option value="worn">Worn Only</option>
                  <option value="clothing">Clothing</option>
                  <option value="body">Body Parts</option>
                  <option value="attachment">Attachments</option>
                </select>
              </div>

              {/* Items List */}
              <div style={{ display: "flex", flexDirection: "column", gap: 6, maxHeight: 280, overflowY: "auto" }}>
                {filteredItems.length ? (
                  filteredItems.map((item) => (
                    <div
                      key={item.id}
                      style={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between",
                        padding: "6px 10px",
                        background: item.worn ? "rgba(56, 189, 248, 0.08)" : V.bg,
                        border: `1px solid ${item.worn ? V.pri : V.outv}`,
                        borderRadius: V.rs,
                      }}
                    >
                      <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                        <Icon name={item.category === "attachment" ? "paperclip" : item.category === "body" ? "user" : "shirt"} size={15} style={{ color: item.worn ? V.pri : V.ink2 }} />
                        <div>
                          <div style={{ fontSize: 12, fontWeight: 600, color: V.ink }}>{item.name}</div>
                          <div style={{ fontSize: 10, color: V.ink2 }}>{item.typeName} · {item.category}</div>
                        </div>
                      </div>

                      <button
                        type="button"
                        onClick={() => toggleItemWorn(item.id)}
                        style={{
                          padding: "3px 8px",
                          fontSize: 10,
                          fontWeight: 700,
                          borderRadius: V.rs,
                          border: `1px solid ${item.worn ? V.pri : V.outv}`,
                          background: item.worn ? V.pri : "transparent",
                          color: item.worn ? (V.onpri || "#fff") : V.ink,
                          cursor: "pointer",
                        }}
                      >
                        {item.worn ? "WORN" : "WEAR"}
                      </button>
                    </div>
                  ))
                ) : (
                  <div style={{ padding: 16, textAlign: "center", color: V.ink2, fontSize: 11 }}>
                    No outfit items matching current filter.
                  </div>
                )}
              </div>
            </div>
          )}

          {/* 2. SHAPE SLIDERS PANEL */}
          {(activePanel === "shape" || activePanel === "viewport") && (
            <div style={{ display: "flex", flexDirection: "column", gap: 10, marginTop: activePanel === "viewport" ? 10 : 0 }}>
              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <h2 style={{ margin: 0, fontSize: 13, fontWeight: 700, color: V.pri, display: "flex", alignItems: "center", gap: 6 }}>
                  <Icon name="sliders" size={16} /> AVATAR SHAPE SLIDERS
                </h2>
                <div style={{ display: "flex", gap: 4 }}>
                  <button type="button" onClick={handleResetShape} aria-label="Reset shape sliders" style={{ padding: "3px 6px", fontSize: 10, background: "transparent", color: V.ink, border: `1px solid ${V.outv}`, borderRadius: V.rs, cursor: "pointer" }}>
                    RESET SHAPE
                  </button>
                  <button type="button" onClick={handleSaveShape} style={{ padding: "3px 8px", fontSize: 10, fontWeight: 700, background: V.sec2 || V.pri, color: "#fff", border: "none", borderRadius: V.rs, cursor: "pointer" }}>
                    APPLY SHAPE
                  </button>
                </div>
              </div>

              {/* Shape Presets Bar */}
              <div style={{ display: "flex", gap: 4, alignItems: "center", flexWrap: "wrap" }}>
                <span style={{ fontSize: 10, fontWeight: 700, color: V.ink2, marginRight: 2 }}>PRESETS:</span>
                {Object.keys(SHAPE_PRESETS).map((pKey) => (
                  <button
                    key={pKey}
                    type="button"
                    onClick={() => handlePresetSelect(pKey)}
                    style={{
                      padding: "2px 7px",
                      fontSize: 10,
                      fontWeight: 600,
                      borderRadius: V.rs,
                      border: `1px solid ${V.outv}`,
                      background: V.bg,
                      color: V.pri,
                      cursor: "pointer",
                    }}
                  >
                    {pKey}
                  </button>
                ))}
              </div>

              {/* Category Tabs */}
              <div style={{ display: "flex", gap: 2, background: V.bg, padding: 2, borderRadius: V.rs, border: `1px solid ${V.outv}` }}>
                {SHAPE_CATEGORIES.map((cat) => (
                  <button
                    key={cat.id}
                    type="button"
                    onClick={() => setActiveShapeCategory(cat.id)}
                    style={{
                      flex: 1,
                      padding: "4px 6px",
                      fontSize: 10,
                      fontWeight: 600,
                      border: "none",
                      borderRadius: V.rs,
                      background: activeShapeCategory === cat.id ? V.pri : "transparent",
                      color: activeShapeCategory === cat.id ? "#fff" : V.ink,
                      cursor: "pointer",
                    }}
                  >
                    {cat.label}
                  </button>
                ))}
              </div>

              {/* Active Sliders List */}
              <div style={{ display: "flex", flexDirection: "column", gap: 8, maxHeight: 220, overflowY: "auto", paddingRight: 4 }}>
                {SHAPE_CATEGORIES.find((c) => c.id === activeShapeCategory)?.sliders.map((s) => (
                  <div key={s.id} style={{ display: "flex", flexDirection: "column", gap: 2 }}>
                    <div style={{ display: "flex", justifyContent: "space-between", fontSize: 11, color: V.ink }}>
                      <span>{s.name}</span>
                      <strong style={{ color: V.pri }}>{shapeValues[s.id] ?? 50}</strong>
                    </div>
                    <input
                      type="range"
                      min={s.min}
                      max={s.max}
                      value={shapeValues[s.id] ?? 50}
                      onChange={(e) => handleSliderChange(s.id, e.target.value)}
                      style={{ width: "100%", accentColor: V.pri, cursor: "pointer" }}
                    />
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* 3. MESH VIEW & DEBUG PANEL */}
          {activePanel === "mesh" && (
            <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
              <h2 style={{ margin: 0, fontSize: 13, fontWeight: 700, color: V.pri, display: "flex", alignItems: "center", gap: 6 }}>
                <Icon name="box" size={16} /> MESH & SKELETON INSPECTION
              </h2>

              <div style={{ display: "flex", flexDirection: "column", gap: 8, background: V.bg, padding: 10, borderRadius: V.rs, border: `1px solid ${V.outv}` }}>
                <label style={{ display: "flex", alignItems: "center", justifyContent: "space-between", fontSize: 12, color: V.ink, cursor: "pointer" }}>
                  <span>Render Mesh Wireframe</span>
                  <input
                    type="checkbox"
                    checked={meshMode === "wireframe"}
                    onChange={(e) => setMeshMode(e.target.checked ? "wireframe" : "textured")}
                  />
                </label>

                <label style={{ display: "flex", alignItems: "center", justifyContent: "space-between", fontSize: 12, color: V.ink, cursor: "pointer" }}>
                  <span>Show Skeleton Joint Markers</span>
                  <input
                    type="checkbox"
                    checked={showJoints}
                    onChange={(e) => setShowJoints(e.target.checked)}
                  />
                </label>

                <label style={{ display: "flex", alignItems: "center", justifyContent: "space-between", fontSize: 12, color: V.ink, cursor: "pointer" }}>
                  <span>Continuous 360° Auto-Spin</span>
                  <input
                    type="checkbox"
                    checked={autoRotate}
                    onChange={(e) => setAutoRotate(e.target.checked)}
                  />
                </label>
              </div>

              {/* Mesh Statistics Readout */}
              <div style={{ background: V.bg, padding: 10, borderRadius: V.rs, border: `1px solid ${V.outv}`, fontSize: 11, color: V.ink2, display: "flex", flexDirection: "column", gap: 4 }}>
                <strong style={{ color: V.pri }}>AVATAR MESH STATISTICS</strong>
                <div>Rigged Body Parts: 7 (Head, Torso, Legs, Eyes, Hair)</div>
                <div>Estimated Vertex Count: 14,280 vertices</div>
                <div>Estimated Face Count: 28,560 triangles</div>
                <div>Skeleton Bones / Bento Joints: 128 active joints</div>
                <div>Active Shape Scale Factor: {(0.8 + (shapeValues.height / 100) * 0.4).toFixed(2)}x</div>
              </div>
            </div>
          )}
        </div>
      </div>
    </section>
  );
}
