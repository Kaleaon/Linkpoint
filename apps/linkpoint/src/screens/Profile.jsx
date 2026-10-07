import { useEffect, useState, useMemo } from "react";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { app } from "../linkpoint/app.ts";
import { slBridge } from "../linkpoint/sl-bridge.ts";
import { subView, setSub } from "../theme/constants.js";
import Icon from "../components/Icon.jsx";
import GuidedEmptyState from "../components/GuidedEmptyState.jsx";

export function sanitizeBioText(text) {
  if (!text || typeof text !== "string") return "";
  let cleaned = text
    .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, "")
    .replace(/<style\b[^<]*(?:(?!<\/style>)<[^<]*)*<\/style>/gi, "")
    .replace(/<iframe\b[^<]*(?:(?!<\/iframe>)<[^<]*)*<\/iframe>/gi, "");
  cleaned = cleaned.replace(/<[^>]+>/g, "");
  cleaned = cleaned
    .replace(/&lt;/g, "<")
    .replace(/&gt;/g, ">")
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/&amp;/g, "&");
  return cleaned.trim();
}

export default function Profile() {
  const { V, t } = useTheme();
  const { state, actions } = useApp();
  const user = app.auth.user;

  const currentTab = useMemo(() => {
    const raw = subView(state, "Profile") || state.tabs?.Profile || "ABOUT";
    const norm = String(raw).toUpperCase().trim();
    if (norm.includes("ABOUT") || norm.includes("2ND")) return "ABOUT";
    if (norm.includes("FIRST")) return "FIRST LIFE";
    if (norm.includes("PICK")) return "PICKS";
    if (norm.includes("GROUP")) return "GROUPS";
    return "ABOUT";
  }, [state]);

  const [profile, setProfile] = useState(null);
  const [picks, setPicks] = useState([]);
  const [groups, setGroups] = useState([]);
  const [photoUrl, setPhotoUrl] = useState(null);
  const [loading, setLoading] = useState(true);
  const [teleporting, setTeleporting] = useState(null);

  useEffect(() => {
    let active = true;
    async function loadData() {
      setLoading(true);
      try {
        if (slBridge.connected) {
          const [profData, picksData, groupsData] = await Promise.all([
            slBridge.getAvatarProfile().catch(() => null),
            slBridge.getAvatarPicks().catch(() => []),
            slBridge.getAvatarGroups().catch(() => []),
          ]);
          if (active) {
            setProfile(profData);
            setPicks(Array.isArray(picksData) ? picksData : []);
            setGroups(Array.isArray(groupsData) ? groupsData : []);
          }
        } else {
          if (active && user) {
            setProfile({
              agentId: user.id || "",
              displayName: user.fullName || "Resident",
              userName: (user.fullName || "Resident").toLowerCase().replace(/\s+/g, "."),
              fullName: user.fullName || "Resident",
              aboutText: user.bio || "",
              firstLifeText: "",
              profileImage: null,
              firstLifeImage: null,
              partner: "None",
              bornOn: "Unknown",
              gridAge: user.grid || "Second Life",
              paymentStatus: "Payment Info On File",
            });
            setPicks([]);
            setGroups([]);
          }
        }
      } catch (err) {
        console.warn("[Profile] Load error:", err);
      } finally {
        if (active) setLoading(false);
      }
    }
    loadData();
    return () => { active = false; };
  }, [user]);

  useEffect(() => {
    let active = true;
    async function fetchPhoto() {
      const nameToQuery = profile?.fullName || profile?.displayName || user?.fullName;
      if (!nameToQuery) return;
      try {
        if (slBridge.connected) {
          const res = await slBridge.fetchProfilePhoto(nameToQuery, true).catch(() => null);
          if (active && res?.photoBytes) {
            const mime = res.contentType || "image/jpeg";
            setPhotoUrl(`data:${mime};base64,${res.photoBytes}`);
            return;
          }
        }
      } catch (e) {
        console.warn("[Profile] fetchProfilePhoto failed:", e);
      }
      if (active && profile?.profileImage) {
        setPhotoUrl(`/api/sl/asset/texture/${profile.profileImage}`);
      }
    }
    fetchPhoto();
    return () => { active = false; };
  }, [profile, user]);

  const handleTeleport = async (pick) => {
    const dest = pick.destination || `${pick.simName || "Arah"}/128/128/25`;
    setTeleporting(pick.id);
    try {
      if (slBridge.connected) {
        await slBridge.teleport({ destination: dest });
      } else {
        alert(`Teleporting to ${dest}`);
      }
    } catch (err) {
      console.warn("[Profile] Teleport error:", err);
    } finally {
      setTeleporting(null);
    }
  };

  if (!user && !profile) {
    return (
      <div className="honest-empty" style={{ padding: "32px", textAlign: "center" }}>
        <Icon name="user" size={30} style={{ color: V.pri, marginBottom: "12px" }} />
        <h2 style={{ font: `600 20px/1.3 ${t.dfont}`, color: V.ink }}>Profile</h2>
        <p style={{ color: V.ink2, fontSize: "14px" }}>Connect to a grid to view your resident profile.</p>
      </div>
    );
  }

  const sanitizedAbout = sanitizeBioText(profile?.aboutText);
  const sanitizedFirstLife = sanitizeBioText(profile?.firstLifeText);

  return (
    <div className="tool-page profile-screen" style={{ padding: "16px", maxWidth: "800px", margin: "0 auto" }}>
      <div
        className="profile-tab-strip"
        role="tablist"
        aria-label="Profile tabs"
        style={{
          display: "flex",
          gap: "8px",
          marginBottom: "16px",
          borderBottom: `1px solid ${V.outv}`,
          paddingBottom: "8px",
        }}
      >
        {[
          ["ABOUT", "About"],
          ["FIRST LIFE", "First Life"],
          ["PICKS", "Picks"],
          ["GROUPS", "Groups"],
        ].map(([tabKey, tabLabel]) => {
          const isActive = currentTab === tabKey;
          return (
            <button
              key={tabKey}
              role="tab"
              aria-selected={isActive}
              onClick={() => setSub(actions, "Profile", tabKey)}
              style={{
                padding: "6px 14px",
                borderRadius: V.rs || "4px",
                border: isActive ? `1px solid ${V.pri}` : `1px solid ${V.outv}`,
                background: isActive ? V.pri : V.surf,
                color: isActive ? V.onpri : V.ink,
                fontWeight: isActive ? 600 : 400,
                fontSize: "13px",
                cursor: "pointer",
                transition: "all 0.15s ease",
              }}
            >
              {tabLabel}
            </button>
          );
        })}
      </div>

      {currentTab === "ABOUT" && (
        <article
          className="runtime-card profile-card"
          style={{
            borderColor: V.outv,
            background: V.surf,
            borderRadius: "8px",
            padding: "20px",
            display: "flex",
            flexDirection: "column",
            gap: "16px",
          }}
        >
          <div style={{ display: "flex", gap: "16px", alignItems: "center" }}>
            <div
              style={{
                width: "80px",
                height: "80px",
                borderRadius: "50%",
                overflow: "hidden",
                border: `2px solid ${V.pri}`,
                background: V.surf2 || "#222",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                flexShrink: 0,
              }}
            >
              {photoUrl ? (
                <img
                  src={photoUrl}
                  alt={profile?.displayName || user?.fullName || "Profile"}
                  style={{ width: "100%", height: "100%", objectFit: "cover" }}
                />
              ) : (
                <Icon name="user" size={40} style={{ color: V.pri }} />
              )}
            </div>
            <div>
              <h2 style={{ font: `600 22px/1.3 ${t.dfont}`, color: V.ink, margin: "0 0 4px 0" }}>
                {profile?.displayName || user?.fullName || "Resident"}
              </h2>
              <div style={{ fontSize: "13px", color: V.ink2 }}>
                @{profile?.userName || (user?.fullName || "").toLowerCase().replace(/\s+/g, ".")}
              </div>
            </div>
          </div>

          <dl
            style={{
              display: "grid",
              gridTemplateColumns: "120px 1fr",
              gap: "8px 16px",
              fontSize: "13px",
              margin: 0,
            }}
          >
            <dt style={{ color: V.ink2, fontWeight: 600 }}>Grid</dt>
            <dd style={{ margin: 0, color: V.ink }}>{user?.grid || "Second Life"}</dd>

            <dt style={{ color: V.ink2, fontWeight: 600 }}>Agent ID</dt>
            <dd style={{ margin: 0, color: V.ink, wordBreak: "break-all" }}>
              {profile?.agentId || user?.id || "Not supplied"}
            </dd>

            <dt style={{ color: V.ink2, fontWeight: 600 }}>Grid Age</dt>
            <dd style={{ margin: 0, color: V.ink }}>{profile?.bornOn || "Born On Unknown"}</dd>

            <dt style={{ color: V.ink2, fontWeight: 600 }}>Partner</dt>
            <dd style={{ margin: 0, color: V.ink }}>{profile?.partner || "None"}</dd>

            <dt style={{ color: V.ink2, fontWeight: 600 }}>Payment Status</dt>
            <dd style={{ margin: 0, color: V.ink }}>{profile?.paymentStatus || "Payment Info On File"}</dd>
          </dl>

          <div style={{ borderTop: `1px solid ${V.outv}`, paddingTop: "12px" }}>
            <h3 style={{ fontSize: "14px", fontWeight: 600, color: V.ink, marginBottom: "8px" }}>Biography</h3>
            {sanitizedAbout ? (
              <p style={{ fontSize: "13px", color: V.ink, lineHeight: 1.5, margin: 0, whiteSpace: "pre-wrap" }}>
                {sanitizedAbout}
              </p>
            ) : (
              <GuidedEmptyState
                icon="user"
                title="No Biography"
                description="This resident has not added a Second Life biography text yet."
              />
            )}
          </div>
        </article>
      )}

      {currentTab === "FIRST LIFE" && (
        <article
          className="runtime-card profile-card"
          style={{
            borderColor: V.outv,
            background: V.surf,
            borderRadius: "8px",
            padding: "20px",
            display: "flex",
            flexDirection: "column",
            gap: "16px",
          }}
        >
          <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
            <Icon name="globe" size={20} style={{ color: V.pri }} />
            <h2 style={{ font: `600 18px/1.3 ${t.dfont}`, color: V.ink, margin: 0 }}>First Life Details</h2>
          </div>

          {profile?.firstLifeImage && (
            <div style={{ maxWidth: "200px", borderRadius: "8px", overflow: "hidden", border: `1px solid ${V.outv}` }}>
              <img
                src={`/api/sl/asset/texture/${profile.firstLifeImage}`}
                alt="First Life Photo"
                style={{ width: "100%", height: "auto", display: "block" }}
              />
            </div>
          )}

          <div>
            {sanitizedFirstLife ? (
              <p style={{ fontSize: "13px", color: V.ink, lineHeight: 1.5, margin: 0, whiteSpace: "pre-wrap" }}>
                {sanitizedFirstLife}
              </p>
            ) : (
              <GuidedEmptyState
                icon="globe"
                title="No First Life Details"
                description="This resident has not published any First Life details or image."
              />
            )}
          </div>
        </article>
      )}

      {currentTab === "PICKS" && (
        <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
          {picks.length > 0 ? (
            picks.map((pick) => (
              <article
                key={pick.id || pick.name}
                className="runtime-card pick-card"
                style={{
                  borderColor: V.outv,
                  background: V.surf,
                  borderRadius: "8px",
                  padding: "16px",
                  display: "flex",
                  flexDirection: "column",
                  gap: "12px",
                }}
              >
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", gap: "12px" }}>
                  <div>
                    <h3 style={{ margin: "0 0 4px 0", fontSize: "16px", fontWeight: 600, color: V.ink }}>
                      {pick.name}
                    </h3>
                    <div style={{ fontSize: "12px", color: V.pri, display: "flex", alignItems: "center", gap: "4px" }}>
                      <Icon name="map-pin" size={14} />
                      {pick.simName} ({pick.parcelName || "Parcel"})
                    </div>
                  </div>
                  <button
                    type="button"
                    className="teleport-btn"
                    onClick={() => handleTeleport(pick)}
                    disabled={teleporting === pick.id}
                    style={{
                      background: V.pri,
                      color: V.onpri,
                      border: "none",
                      padding: "6px 14px",
                      borderRadius: V.rs || "4px",
                      fontWeight: 600,
                      fontSize: "12px",
                      cursor: "pointer",
                      display: "flex",
                      alignItems: "center",
                      gap: "6px",
                      flexShrink: 0,
                    }}
                  >
                    <Icon name="navigation" size={14} />
                    {teleporting === pick.id ? "Teleporting…" : "Teleport"}
                  </button>
                </div>

                {pick.snapshotId && (
                  <div style={{ height: "140px", borderRadius: "6px", overflow: "hidden", background: V.surf2 || "#111" }}>
                    <img
                      src={`/api/sl/asset/texture/${pick.snapshotId}`}
                      alt={pick.name}
                      style={{ width: "100%", height: "100%", objectFit: "cover" }}
                    />
                  </div>
                )}

                {pick.description && (
                  <p style={{ margin: 0, fontSize: "13px", color: V.ink2, lineHeight: 1.4 }}>
                    {sanitizeBioText(pick.description)}
                  </p>
                )}
              </article>
            ))
          ) : (
            <GuidedEmptyState
              icon="heart"
              title="No Picks Found"
              description="This resident has no favorite location Picks listed on their profile."
            />
          )}
        </div>
      )}

      {currentTab === "GROUPS" && (
        <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>
          {groups.length > 0 ? (
            groups.map((group) => (
              <article
                key={group.id || group.name}
                className="runtime-card group-card"
                style={{
                  borderColor: V.outv,
                  background: V.surf,
                  borderRadius: "6px",
                  padding: "12px 16px",
                  display: "flex",
                  alignItems: "center",
                  gap: "12px",
                }}
              >
                <div
                  style={{
                    width: "40px",
                    height: "40px",
                    borderRadius: "4px",
                    background: V.surf2 || "rgba(255,255,255,0.05)",
                    border: `1px solid ${V.outv}`,
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    flexShrink: 0,
                    overflow: "hidden",
                  }}
                >
                  {group.insignia ? (
                    <img
                      src={`/api/sl/asset/texture/${group.insignia}`}
                      alt={group.name}
                      style={{ width: "100%", height: "100%", objectFit: "cover" }}
                    />
                  ) : (
                    <Icon name="users" size={22} style={{ color: V.pri }} />
                  )}
                </div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <h4 style={{ margin: "0 0 2px 0", fontSize: "14px", fontWeight: 600, color: V.ink }}>
                    {group.name}
                  </h4>
                  {group.title && (
                    <div style={{ fontSize: "12px", color: V.ink2 }}>
                      {group.title}
                    </div>
                  )}
                </div>
              </article>
            ))
          ) : (
            <GuidedEmptyState
              icon="users"
              title="No Groups Found"
              description="This resident is not currently displaying any public group memberships."
            />
          )}
        </div>
      )}
    </div>
  );
}
