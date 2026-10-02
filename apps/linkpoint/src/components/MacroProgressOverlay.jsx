import React, { useEffect, useState } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { macroTaskQueue } from "../linkpoint/macro-task-queue";
import Icon from "./Icon.jsx";

export default function MacroProgressOverlay() {
  const { V, t } = useTheme();
  const [progress, setProgress] = useState(() => macroTaskQueue.getStatus());

  useEffect(() => {
    const handleProgress = (data) => setProgress(data);
    macroTaskQueue.on("progress", handleProgress);
    macroTaskQueue.on("start", handleProgress);
    macroTaskQueue.on("complete", handleProgress);
    macroTaskQueue.on("cancel", handleProgress);

    return () => {
      macroTaskQueue.off("progress", handleProgress);
      macroTaskQueue.off("start", handleProgress);
      macroTaskQueue.off("complete", handleProgress);
      macroTaskQueue.off("cancel", handleProgress);
    };
  }, []);

  if (progress.status !== "running") {
    return null;
  }

  const stepNumber = Math.min(progress.currentIndex + 1, progress.totalTasks);
  const totalSteps = progress.totalTasks;
  const currentItemName = progress.currentItem?.name || "Item";
  const percentage = totalSteps > 0 ? Math.round((progress.currentIndex / totalSteps) * 100) : 0;
  const modeLabel = progress.mode === "append" ? "Append All Items" : "Wear All Items";

  const handleCancel = () => {
    macroTaskQueue.cancel();
  };

  return (
    <div
      className="macro-progress-overlay-backdrop"
      role="dialog"
      aria-modal="true"
      aria-label="Macro task progress"
      style={{
        position: "fixed",
        inset: 0,
        zIndex: 9999,
        background: "rgba(0, 0, 0, 0.75)",
        backdropFilter: "blur(2px)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        padding: "20px",
        pointerEvents: "auto",
      }}
      onClick={(e) => e.stopPropagation()}
    >
      <div
        className="macro-progress-modal"
        style={{
          width: "100%",
          maxWidth: "420px",
          background: V.surf,
          border: `1px solid ${V.pri}`,
          borderRadius: V.rl || "12px",
          padding: "20px 24px",
          boxShadow: "0 12px 32px rgba(0, 0, 0, 0.5)",
          display: "flex",
          flexDirection: "column",
          gap: "16px",
          color: V.ink,
          fontFamily: t.font,
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
          <Icon name="layers" size={20} style={{ color: V.pri }} />
          <div style={{ flex: 1, minWidth: 0 }}>
            <h3 style={{ margin: 0, fontSize: "15px", fontWeight: 700, color: V.ink, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
              {progress.folderName || "Outfit Macro"}
            </h3>
            <span style={{ fontSize: "11px", fontWeight: 600, color: V.pri, textTransform: "uppercase", letterSpacing: "0.08em" }}>
              {modeLabel}
            </span>
          </div>
        </div>

        <div style={{ fontSize: "13px", lineHeight: "1.4", color: V.ink2 }}>
          {totalSteps > 0 ? (
            <>
              Wearing item <strong>{stepNumber}</strong> of <strong>{totalSteps}</strong>:
              <div style={{ marginTop: "4px", color: V.ink, fontWeight: 600, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                "{currentItemName}"
              </div>
            </>
          ) : (
            "Processing macro tasks..."
          )}
        </div>

        {/* Progress Bar */}
        <div
          style={{
            width: "100%",
            height: "8px",
            background: V.bg || "#111",
            borderRadius: "4px",
            overflow: "hidden",
            border: `1px solid ${V.outv || "#333"}`,
          }}
        >
          <div
            style={{
              width: `${percentage}%`,
              height: "100%",
              background: V.pri,
              transition: "width 0.25s ease-out",
            }}
          />
        </div>

        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <span style={{ fontSize: "11px", color: V.ink2, fontWeight: 600 }}>
            {percentage}% Complete
          </span>
          <button
            type="button"
            className="macro-cancel-button"
            onClick={handleCancel}
            style={{
              padding: "8px 18px",
              background: V.bg,
              border: `1px solid ${V.outv}`,
              borderRadius: V.rs || "6px",
              color: V.ink,
              fontWeight: 700,
              fontSize: "12px",
              letterSpacing: "0.05em",
              cursor: "pointer",
              transition: "all 0.15s ease",
            }}
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
