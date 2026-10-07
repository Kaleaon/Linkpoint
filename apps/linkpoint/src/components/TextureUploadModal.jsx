import React, { useState, useEffect, useMemo } from "react";
import { useTheme } from "../context/ThemeContext.jsx";
import { useApp } from "../context/AppContext.jsx";
import { app } from "../linkpoint/app.ts";
import { economyManager } from "../linkpoint/economy-manager.ts";
import { uploadTexture } from "../linkpoint/texture-uploader.ts";
import { scaleToPowerOfTwo } from "../linkpoint/jpeg2000-encoder.ts";
import Icon from "./Icon.jsx";
import FocusTrap from "./FocusTrap.jsx";

export default function TextureUploadModal({ isOpen, onClose }) {
  const { V, t } = useTheme();
  const { actions } = useApp();

  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [originalDims, setOriginalDims] = useState({ width: 0, height: 0 });
  const [targetDims, setTargetDims] = useState({ width: 512, height: 512 });
  const [assetName, setAssetName] = useState("");
  const [assetDesc, setAssetDesc] = useState("Uploaded via Linkpoint Mobile");
  const [selectedFolderId, setSelectedFolderId] = useState("");
  const [balance, setBalance] = useState(economyManager.balance);

  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState({ percent: 0, message: "" });
  const [errorMsg, setErrorMsg] = useState(null);
  const [successMsg, setSuccessMsg] = useState(null);

  useEffect(() => {
    setBalance(economyManager.balance);
    const handleBalance = (data) => {
      if (typeof data === "number") setBalance(data);
      else if (data && typeof data.balance === "number") setBalance(data.balance);
    };
    economyManager.on("balance_updated", handleBalance);
    return () => {
      economyManager.off("balance_updated", handleBalance);
    };
  }, []);

  useEffect(() => {
    if (!isOpen) {
      setSelectedFile(null);
      setPreviewUrl(null);
      setOriginalDims({ width: 0, height: 0 });
      setTargetDims({ width: 512, height: 512 });
      setAssetName("");
      setErrorMsg(null);
      setSuccessMsg(null);
      setUploading(false);
      setProgress({ percent: 0, message: "" });
    }
  }, [isOpen]);

  const foldersList = useMemo(() => {
    const list = [];
    if (app?.inventory?.folders) {
      for (const [id, folder] of app.inventory.folders.entries()) {
        if (folder && folder.name) {
          list.push({ id, name: folder.name, isTextures: folder.name.toLowerCase() === "textures" });
        }
      }
    }
    list.sort((a, b) => Number(b.isTextures) - Number(a.isTextures) || a.name.localeCompare(b.name));
    return list;
  }, [isOpen]);

  useEffect(() => {
    if (foldersList.length > 0 && !selectedFolderId) {
      setSelectedFolderId(foldersList[0].id);
    }
  }, [foldersList, selectedFolderId]);

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setErrorMsg(null);
    setSuccessMsg(null);
    setSelectedFile(file);

    const nameWithoutExt = file.name.replace(/\.[^/.]+$/, "");
    setAssetName(nameWithoutExt);

    const url = URL.createObjectURL(file);
    setPreviewUrl(url);

    const img = new Image();
    img.onload = () => {
      const w = img.naturalWidth || img.width;
      const h = img.naturalHeight || img.height;
      setOriginalDims({ width: w, height: h });
      setTargetDims(scaleToPowerOfTwo(w, h));
    };
    img.src = url;
  };

  const handleUploadConfirm = async () => {
    if (!selectedFile) return;

    if (balance !== null && typeof balance === "number" && balance < 10) {
      setErrorMsg(`Insufficient funds: L$10 upload fee required, current balance is L$${balance}.`);
      return;
    }

    setUploading(true);
    setErrorMsg(null);
    setSuccessMsg(null);

    try {
      const result = await uploadTexture(
        {
          file: selectedFile,
          name: assetName || "New Texture",
          description: assetDesc,
          folderId: selectedFolderId,
        },
        (prog) => setProgress(prog)
      );

      setUploading(false);
      setSuccessMsg(`Texture "${result.item.name}" successfully uploaded (${result.dimensions.width}x${result.dimensions.height})! L$10 upload fee deducted.`);
      actions.notify(`Uploaded ${result.item.name} to inventory`);
    } catch (err) {
      setUploading(false);
      setErrorMsg(err.message || "Failed to upload texture to Second Life");
    }
  };

  if (!isOpen) return null;

  const isInsufficientFunds = balance !== null && typeof balance === "number" && balance < 10;

  return (
    <FocusTrap active={isOpen} onEscape={uploading ? undefined : onClose} style={{ position: "fixed", inset: 0, zIndex: 1100, background: "rgba(0,0,0,0.72)", display: "flex", alignItems: "center", justifyContent: "center", padding: "16px" }}>
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="texture-upload-title"
        style={{
          width: "100%",
          maxWidth: "520px",
          background: V.surf,
          border: `1px solid ${V.pri}`,
          borderRadius: V.rm || "8px",
          boxShadow: "0 12px 32px rgba(0,0,0,0.5)",
          padding: "20px",
          maxHeight: "90vh",
          overflowY: "auto",
        }}
      >
        {/* Header */}
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "16px", borderBottom: `1px solid ${V.outv}`, paddingBottom: "12px" }}>
          <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
            <Icon name="image" size={20} style={{ color: V.pri }} />
            <h2 id="texture-upload-title" style={{ fontSize: "16px", fontWeight: "700", color: V.ink, margin: 0 }}>
              Upload Texture (JPEG2000)
            </h2>
          </div>
          <button
            type="button"
            onClick={onClose}
            disabled={uploading}
            aria-label="Close texture upload modal"
            style={{ background: "none", border: "none", color: V.ink2, fontSize: "20px", cursor: uploading ? "not-allowed" : "pointer", padding: "0 4px" }}
          >
            ×
          </button>
        </div>

        {/* Error Alert Banner */}
        {errorMsg ? (
          <div style={{ background: "rgba(220, 38, 38, 0.15)", border: `1px solid ${V.err || "#ef4444"}`, borderRadius: V.rs || "4px", padding: "10px 12px", marginBottom: "14px", color: V.err || "#f87171", fontSize: "12px", display: "flex", alignItems: "flex-start", gap: "8px" }}>
            <Icon name="alert-triangle" size={16} style={{ flexShrink: 0, marginTop: "2px" }} />
            <span style={{ flex: 1 }}>{errorMsg}</span>
          </div>
        ) : null}

        {/* Success Banner */}
        {successMsg ? (
          <div style={{ background: "rgba(34, 197, 94, 0.15)", border: "1px solid #22c55e", borderRadius: V.rs || "4px", padding: "12px", marginBottom: "14px", color: "#4ade80", fontSize: "13px", textAlign: "center" }}>
            <Icon name="check-circle" size={20} style={{ marginBottom: "6px" }} />
            <div>{successMsg}</div>
            <button
              type="button"
              onClick={onClose}
              style={{ marginTop: "10px", padding: "6px 16px", background: V.pri, color: V.onpri || "#fff", border: "none", borderRadius: V.rs || "4px", fontWeight: "700", fontSize: "12px", cursor: "pointer" }}
            >
              DONE
            </button>
          </div>
        ) : null}

        {!successMsg ? (
          <>
            {/* Step 1: Image Picker */}
            {!selectedFile ? (
              <div style={{ border: `2px dashed ${V.outv}`, borderRadius: V.rm || "8px", padding: "32px 16px", textAlign: "center", background: V.bg }}>
                <Icon name="upload-cloud" size={36} style={{ color: V.pri, marginBottom: "10px" }} />
                <p style={{ fontSize: "14px", fontWeight: "600", color: V.ink, marginBottom: "6px" }}>
                  Select an image from device storage
                </p>
                <p style={{ fontSize: "12px", color: V.ink2, marginBottom: "16px" }}>
                  Supports JPEG, PNG, or WEBP (Max 1024x1024)
                </p>
                <label
                  style={{
                    display: "inline-block",
                    padding: "10px 20px",
                    background: V.pri,
                    color: V.onpri || "#fff",
                    borderRadius: V.rs || "4px",
                    fontWeight: "700",
                    fontSize: "12px",
                    cursor: "pointer",
                  }}
                >
                  CHOOSE IMAGE FILE
                  <input
                    type="file"
                    accept="image/png,image/jpeg,image/webp"
                    onChange={handleFileChange}
                    style={{ display: "none" }}
                  />
                </label>
              </div>
            ) : (
              /* Step 2: Image Preview & Details */
              <div style={{ display: "flex", flexDirection: "column", gap: "14px" }}>
                <div style={{ display: "flex", gap: "14px", alignItems: "flex-start", background: V.bg, padding: "12px", borderRadius: V.rs || "6px", border: `1px solid ${V.outv}` }}>
                  {previewUrl ? (
                    <img
                      src={previewUrl}
                      alt="Texture Preview"
                      style={{ width: "96px", height: "96px", objectFit: "contain", borderRadius: "4px", background: "#000", border: `1px solid ${V.outv}` }}
                    />
                  ) : null}
                  <div style={{ flex: 1, fontSize: "12px", color: V.ink2, display: "flex", flexDirection: "column", gap: "4px" }}>
                    <div style={{ fontWeight: "700", color: V.ink, fontSize: "13px" }}>{selectedFile.name}</div>
                    <div>Original: {originalDims.width} × {originalDims.height} px</div>
                    <div style={{ color: V.pri, fontWeight: "600" }}>
                      Target JPEG2000: {targetDims.width} × {targetDims.height} px (Power of 2)
                    </div>
                    <div>Size: {(selectedFile.size / 1024).toFixed(1)} KB</div>
                  </div>
                </div>

                {/* Texture Name Input */}
                <div>
                  <label style={{ display: "block", fontSize: "11px", fontWeight: "700", color: V.ink2, marginBottom: "4px" }}>
                    TEXTURE NAME
                  </label>
                  <input
                    type="text"
                    value={assetName}
                    onChange={(e) => setAssetName(e.target.value)}
                    placeholder="Enter texture name"
                    disabled={uploading}
                    style={{ width: "100%", height: "36px", padding: "0 10px", background: V.bg, border: `1px solid ${V.outv}`, borderRadius: V.rs || "4px", color: V.ink, fontSize: "13px" }}
                  />
                </div>

                {/* Destination Folder Selector */}
                <div>
                  <label style={{ display: "block", fontSize: "11px", fontWeight: "700", color: V.ink2, marginBottom: "4px" }}>
                    DESTINATION FOLDER
                  </label>
                  <select
                    value={selectedFolderId}
                    onChange={(e) => setSelectedFolderId(e.target.value)}
                    disabled={uploading}
                    style={{ width: "100%", height: "36px", padding: "0 10px", background: V.bg, border: `1px solid ${V.outv}`, borderRadius: V.rs || "4px", color: V.ink, fontSize: "12px" }}
                  >
                    {foldersList.map((f) => (
                      <option key={f.id} value={f.id}>
                        {f.name}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Fee & Balance Display */}
                <div style={{ padding: "10px 12px", background: isInsufficientFunds ? "rgba(220, 38, 38, 0.1)" : V.bg, border: `1px solid ${isInsufficientFunds ? V.err || "#ef4444" : V.outv}`, borderRadius: V.rs || "4px", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <div>
                    <span style={{ fontSize: "11px", fontWeight: "700", color: V.ink2 }}>UPLOAD FEE: </span>
                    <span style={{ fontSize: "13px", fontWeight: "800", color: V.pri }}>L$10</span>
                  </div>
                  <div>
                    <span style={{ fontSize: "11px", fontWeight: "700", color: V.ink2 }}>YOUR BALANCE: </span>
                    <span style={{ fontSize: "13px", fontWeight: "800", color: isInsufficientFunds ? V.err || "#ef4444" : V.ink }}>
                      L${balance ?? "..."}
                    </span>
                  </div>
                </div>

                {isInsufficientFunds ? (
                  <div style={{ color: V.err || "#ef4444", fontSize: "11px", fontWeight: "600", marginTop: "-6px" }}>
                    ⚠️ Account balance below L$10. Upload cannot be completed.
                  </div>
                ) : null}

                {/* Upload Progress Bar */}
                {uploading ? (
                  <div style={{ marginTop: "10px" }}>
                    <div style={{ display: "flex", justifyContent: "space-between", fontSize: "11px", color: V.ink2, marginBottom: "4px" }}>
                      <span>{progress.message || "Processing..."}</span>
                      <span>{progress.percent}%</span>
                    </div>
                    <div style={{ height: "8px", width: "100%", background: V.bg, borderRadius: "4px", overflow: "hidden", border: `1px solid ${V.outv}` }}>
                      <div style={{ height: "100%", width: `${progress.percent}%`, background: V.pri, transition: "width 0.3s ease" }} />
                    </div>
                  </div>
                ) : null}

                {/* Actions */}
                <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
                  <button
                    type="button"
                    onClick={() => {
                      setSelectedFile(null);
                      setPreviewUrl(null);
                    }}
                    disabled={uploading}
                    style={{ flex: 1, height: "40px", background: "transparent", border: `1px solid ${V.outv}`, borderRadius: V.rs || "4px", color: V.ink, fontWeight: "600", fontSize: "12px", cursor: uploading ? "not-allowed" : "pointer" }}
                  >
                    CHANGE FILE
                  </button>
                  <button
                    type="button"
                    onClick={handleUploadConfirm}
                    disabled={uploading || isInsufficientFunds || !assetName.trim()}
                    style={{
                      flex: 1.5,
                      height: "40px",
                      background: isInsufficientFunds ? V.outv : V.pri,
                      color: V.onpri || "#fff",
                      border: "none",
                      borderRadius: V.rs || "4px",
                      fontWeight: "700",
                      fontSize: "12px",
                      cursor: uploading || isInsufficientFunds || !assetName.trim() ? "not-allowed" : "pointer",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      gap: "6px",
                    }}
                  >
                    <Icon name="upload" size={16} />
                    {uploading ? "UPLOADING..." : "CONFIRM & UPLOAD (L$10)"}
                  </button>
                </div>
              </div>
            )}
          </>
        ) : null}
      </div>
    </FocusTrap>
  );
}
