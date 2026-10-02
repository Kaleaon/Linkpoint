import { useEffect, useMemo, useRef, useState } from "react";
import { app } from "../linkpoint/app.ts";
import { fileToPhotoDataUrl, base64ToBlob } from "../linkpoint/contact-photo.ts";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import Icon from "../components/Icon.jsx";
import ContactAvatar from "../components/ContactAvatar.jsx";
import useGoogleEnabled from "../hooks/useGoogleEnabled.js";
import { loadGoogle } from "../services/google.ts";

const LINK_FIELDS = [
  { service: "telegram", label: "Telegram", placeholder: "@username or t.me link" },
  { service: "discord", label: "Discord", placeholder: "user id, profile link or username" },
  { service: "web", label: "Website", placeholder: "https://…" },
];

function gridLabel() {
  const grid = app.auth.user?.grid;
  if (!grid || grid === "agni") return "Second Life";
  return grid === "aditi" ? "Second Life Beta" : String(grid);
}

/** Download text as a file. */
function download(name, text, type) {
  const url = URL.createObjectURL(new Blob([text], { type }));
  const link = document.createElement("a");
  link.href = url;
  link.download = name;
  document.body.appendChild(link);
  link.click();
  link.remove();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}

// Contacts: a device-local address book of Second Life residents. Save your friends
// list, add a photo, a note, and links to the same person on Telegram, Discord or the
// web. Works without any account. Copying to Google Contacts is optional and only
// appears once it has been switched on in Settings.
export default function ContactsScreen() {
  const { V, t } = useTheme();
  const { state, actions } = useApp();
  const googleEnabled = useGoogleEnabled();
  const tab = state.tabs?.Contacts || "SAVED";
  const [contacts, setContacts] = useState(() => app.contacts.list());
  const [, setFriendsRevision] = useState(0);
  const [query, setQuery] = useState("");
  const [selectedId, setSelectedId] = useState(null);
  const [notice, setNotice] = useState(null);
  const [busy, setBusy] = useState(false);
  const importRef = useRef(null);

  useEffect(() => {
    const onContacts = (list) => setContacts(list);
    const bump = () => setFriendsRevision((n) => n + 1);
    app.contacts.on("contacts_changed", onContacts);
    app.friends.on("friend_added", bump);
    app.friends.on("friend_updated", bump);
    app.friends.on("friend_removed", bump);
    app.protocol.on("friends_loaded", bump);
    setContacts(app.contacts.list());
    return () => {
      app.contacts.off("contacts_changed", onContacts);
      app.friends.off("friend_added", bump);
      app.friends.off("friend_updated", bump);
      app.friends.off("friend_removed", bump);
      app.protocol.off("friends_loaded", bump);
    };
  }, []);

  const friends = app.friends.getFriends();
  const onlineIds = useMemo(() => new Set(friends.filter((f) => f.onlineStatus === "online").map((f) => f.id)), [friends]);
  const unsaved = friends.filter((friend) => !app.contacts.has(friend.id));
  const selected = selectedId ? contacts.find((c) => c.id === selectedId) || null : null;
  const shown = contacts.filter((c) => !query.trim() || c.name.toLowerCase().includes(query.trim().toLowerCase()) || c.note.toLowerCase().includes(query.trim().toLowerCase()));

  const say = (kind, text) => setNotice({ kind, text });
  const run = async (work) => {
    setBusy(true);
    try { await work(); } catch (error) { say("error", error?.message || "Something went wrong."); } finally { setBusy(false); }
  };

  const saveFriends = (list) => run(async () => {
    const result = app.contacts.saveFriends(list);
    say("info", result.added || result.updated ? `Saved ${result.added} new contact${result.added === 1 ? "" : "s"}${result.updated ? `, updated ${result.updated}` : ""}.` : "Everyone on your friends list is already saved.");
  });

  const exportContacts = () => {
    download("linkpoint-contacts.json", app.contacts.exportJson(), "application/json");
    say("info", `Exported ${contacts.length} contact${contacts.length === 1 ? "" : "s"}. The file includes notes and photos; keep it private.`);
  };

  const importFile = (event) => {
    const file = event.target.files?.[0];
    event.target.value = "";
    if (!file) return;
    void run(async () => {
      if (file.size > 20 * 1024 * 1024) throw new Error("That file is too large to be a contacts backup.");
      const result = app.contacts.importJson(await file.text());
      say("info", `Imported ${result.imported} contact${result.imported === 1 ? "" : "s"}${result.rejected ? `; ${result.rejected} could not be read` : ""}.`);
    });
  };

  const copyToGoogle = (list) => run(async () => {
    const google = await loadGoogle();
    if (!google.auth.getGoogleToken("contacts")) await google.auth.signInWithGoogle("contacts");
    const existing = await google.contacts.fetchSlContacts();
    let created = 0, already = 0, photoFailed = 0;
    for (const contact of list) {
      const result = await google.contacts.pushContact(contact, { grid: gridLabel(), existing });
      app.contacts.setGoogleResource(contact.id, result.resourceName);
      if (result.created) created++; else already++;
      if (result.photoUploaded === false) photoFailed++;
    }
    say(photoFailed ? "error" : "info", `Google Contacts: ${created} added, ${already} already there${photoFailed ? `; ${photoFailed} photo${photoFailed === 1 ? "" : "s"} could not be uploaded` : ""}.`);
  });

  const button = (primary) => ({ minHeight: 34, padding: "0 12px", border: `1px solid ${primary ? V.pri : V.outv}`, borderRadius: V.rs, background: primary ? V.pri : V.surf, color: primary ? V.onpri : V.ink, cursor: busy ? "default" : "pointer", opacity: busy ? 0.6 : 1, font: `700 10.5px/1 ${t.font}`, letterSpacing: ".08em" });

  return (
    <div style={{ flex: 1, minHeight: 0, display: "flex", flexDirection: "column", padding: 12, gap: 10, background: V.bg, color: V.ink, position: "relative" }}>
      <div style={{ display: "flex", flexWrap: "wrap", gap: 6, alignItems: "center" }}>
        <button type="button" disabled={busy || !friends.length} style={button(true)} onClick={() => saveFriends(friends)} title={friends.length ? "Save everyone on your friends list" : "Connect and load your friends first"}>
          SAVE FRIENDS LIST{friends.length ? ` (${friends.length})` : ""}
        </button>
        <button type="button" disabled={busy || !contacts.length} style={button(false)} onClick={exportContacts}>EXPORT</button>
        <button type="button" disabled={busy} style={button(false)} onClick={() => importRef.current?.click()}>IMPORT</button>
        <input ref={importRef} type="file" accept="application/json,.json" onChange={importFile} hidden aria-label="Import contacts backup" />
        {googleEnabled ? (
          <button type="button" disabled={busy || !contacts.length} style={button(false)} onClick={() => copyToGoogle(contacts)} title="Copy every saved contact to Google Contacts">COPY ALL TO GOOGLE</button>
        ) : null}
      </div>

      {!googleEnabled ? (
        <div style={{ font: `400 11px/1.5 ${t.font}`, color: V.ink2 }}>
          Contacts stay on this device. Google Contacts is off.{" "}
          <button type="button" onClick={() => actions.setScreen("Settings")} style={{ background: "none", border: 0, padding: 0, color: V.pri, cursor: "pointer", font: "inherit", textDecoration: "underline" }}>Turn it on in Settings</button> to copy contacts there.
        </div>
      ) : null}

      {notice ? (
        <div role={notice.kind === "error" ? "alert" : "status"} style={{ padding: "8px 10px", border: `1px solid ${notice.kind === "error" ? V.err : V.outv}`, borderRadius: V.rs, color: notice.kind === "error" ? V.err : V.ink, font: `500 11.5px/1.4 ${t.font}`, display: "flex", gap: 8 }}>
          <span style={{ flex: 1 }}>{notice.text}</span>
          <button type="button" aria-label="Dismiss message" onClick={() => setNotice(null)} style={{ background: "none", border: 0, color: "inherit", cursor: "pointer" }}>×</button>
        </div>
      ) : null}

      {tab === "ADD FROM SL" ? (
        <FriendsToAdd friends={unsaved} total={friends.length} onlineIds={onlineIds} busy={busy} button={button} onSave={saveFriends} loggedIn={app.auth.isLoggedIn()} />
      ) : (
        <div style={{ flex: 1, minHeight: 0, display: "flex", flexDirection: "column", gap: 8 }}>
          <input type="search" value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search contacts" aria-label="Search contacts" style={{ minHeight: 36, padding: "0 10px", border: `1px solid ${V.outv}`, borderRadius: V.rs, background: V.surf, color: V.ink, font: `400 12.5px/1 ${t.font}` }} />
          {!contacts.length ? (
            <div className="honest-empty"><Icon name="contact" size={30} /><p>No contacts saved yet. Save your friends list to start your address book.</p></div>
          ) : !shown.length ? (
            <div className="honest-empty"><p>No contact matches “{query}”.</p></div>
          ) : (
            <ul aria-label="Saved contacts" style={{ listStyle: "none", margin: 0, padding: 0, flex: selected ? "0 0 38%" : 1, minHeight: 90, overflowY: "auto", display: "grid", gap: 6, alignContent: "start" }}>
              {shown.map((contact) => (
                <li key={contact.id}>
                  <button type="button" onClick={() => setSelectedId(selectedId === contact.id ? null : contact.id)} aria-pressed={selectedId === contact.id}
                    style={{ width: "100%", display: "flex", alignItems: "center", gap: 10, padding: "8px 10px", textAlign: "left", background: V.surf, border: `1px solid ${selectedId === contact.id ? V.pri : V.outv}`, borderRadius: V.rs, color: V.ink, cursor: "pointer", font: `400 12px/1.3 ${t.font}` }}>
                    <ContactAvatar name={contact.name} photo={contact.photo} size={36} />
                    <span style={{ flex: 1, minWidth: 0 }}>
                      <strong style={{ display: "block", fontSize: 13 }}>{contact.name}{onlineIds.has(contact.id) ? <span style={{ color: V.pri, fontWeight: 600 }}> · online</span> : null}</strong>
                      <small style={{ color: V.ink2, display: "block", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                        {[contact.note, ...contact.links.map((link) => link.label)].filter(Boolean).join(" · ") || "No note or links yet"}
                      </small>
                    </span>
                    {contact.googleResourceName ? <span title="Copied to Google Contacts" style={{ color: V.pri, fontSize: 10, fontWeight: 700, letterSpacing: ".08em" }}>GOOGLE</span> : null}
                  </button>
                </li>
              ))}
            </ul>
          )}
          {selected ? (
            <ContactDetail key={selected.id} contact={selected} online={onlineIds.has(selected.id)} googleEnabled={googleEnabled} busy={busy} run={run} say={say} button={button} copyToGoogle={copyToGoogle} onClose={() => setSelectedId(null)} />
          ) : null}
        </div>
      )}
    </div>
  );
}

function FriendsToAdd({ friends, total, onlineIds, busy, button, onSave, loggedIn }) {
  const { V, t } = useTheme();
  if (!loggedIn && !total) return <div className="honest-empty"><Icon name="users" size={30} /><p>Connect to a grid to see your friends.</p></div>;
  if (!total) return <div className="honest-empty"><Icon name="users" size={30} /><p>No friends have been loaded from the grid yet.</p></div>;
  if (!friends.length) return <div className="honest-empty"><Icon name="check" size={30} /><p>All {total} of your friends are saved.</p></div>;
  return (
    <div style={{ flex: 1, minHeight: 0, display: "flex", flexDirection: "column", gap: 8 }}>
      <button type="button" disabled={busy} style={{ ...button(true), alignSelf: "flex-start" }} onClick={() => onSave(friends)}>SAVE ALL {friends.length}</button>
      <ul aria-label="Friends not yet saved" style={{ listStyle: "none", margin: 0, padding: 0, overflowY: "auto", display: "grid", gap: 6, alignContent: "start" }}>
        {friends.map((friend) => (
          <li key={friend.id} style={{ display: "flex", alignItems: "center", gap: 10, padding: "8px 10px", background: V.surf, border: `1px solid ${V.outv}`, borderRadius: V.rs }}>
            <ContactAvatar name={friend.name} size={32} />
            <span style={{ flex: 1, font: `600 13px/1.3 ${t.font}` }}>{friend.name}{onlineIds.has(friend.id) ? <small style={{ color: V.pri }}> · online</small> : null}</span>
            <button type="button" disabled={busy} style={button(false)} onClick={() => onSave([friend])}>SAVE</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

function ContactDetail({ contact, online, googleEnabled, busy, run, say, button, copyToGoogle, onClose }) {
  const { V, t } = useTheme();
  const { actions } = useApp();
  const [note, setNote] = useState(contact.note);
  const [linkInputs, setLinkInputs] = useState({});
  const [linkErrors, setLinkErrors] = useState({});
  const [confirmRemove, setConfirmRemove] = useState(false);
  const fileRef = useRef(null);
  const noteDirty = note !== contact.note;

  const setPhoto = (file) => run(async () => {
    app.contacts.setPhoto(contact.id, await fileToPhotoDataUrl(file));
    say("info", "Photo saved.");
  });

  const useProfilePicture = () => run(async () => {
    const reply = await app.protocol.fetchProfilePhoto(contact.name);
    if (!reply?.photoBytes) { say("info", `${contact.name} has no public profile picture.`); return; }
    app.contacts.setPhoto(contact.id, await fileToPhotoDataUrl(base64ToBlob(reply.photoBytes, reply.contentType || "image/png")));
    say("info", "Profile picture saved as the contact photo.");
  });

  const saveLink = (service) => {
    const result = app.contacts.setLink(contact.id, service, linkInputs[service] || "");
    setLinkErrors((previous) => ({ ...previous, [service]: result.ok ? "" : result.error }));
    if (result.ok) setLinkInputs((previous) => ({ ...previous, [service]: "" }));
  };

  const field = { minHeight: 34, padding: "0 8px", border: `1px solid ${V.outv}`, borderRadius: V.rs, background: V.bg, color: V.ink, font: `400 12px/1 ${t.font}`, minWidth: 0, flex: 1 };
  const label = { font: `700 10px/1 ${t.font}`, letterSpacing: ".1em", color: V.ink2 };

  return (
    <section aria-label={`Details for ${contact.name}`} style={{ flex: 1, minHeight: 0, overflowY: "auto", padding: 12, background: V.surf, border: `1px solid ${V.pri}`, borderRadius: V.rs, display: "grid", gap: 12, alignContent: "start" }}>
      <header style={{ display: "flex", alignItems: "center", gap: 12 }}>
        <ContactAvatar name={contact.name} photo={contact.photo} size={64} />
        <div style={{ flex: 1, minWidth: 0 }}>
          <h2 style={{ margin: 0, fontSize: 16 }}>{contact.name}</h2>
          <div style={{ font: `400 11px/1.4 ${t.font}`, color: V.ink2, overflowWrap: "anywhere" }}>{online ? "Online now · " : ""}{contact.id}</div>
        </div>
        <button type="button" aria-label="Close details" onClick={onClose} style={{ background: "none", border: 0, color: V.ink2, fontSize: 20, cursor: "pointer" }}>×</button>
      </header>

      <div style={{ display: "flex", flexWrap: "wrap", gap: 6 }}>
        <button type="button" style={button(true)} onClick={() => actions.startIm(contact.name)}>INSTANT MESSAGE</button>
        <button type="button" disabled={busy} style={button(false)} onClick={() => fileRef.current?.click()}>CHOOSE PHOTO</button>
        <input ref={fileRef} type="file" accept="image/jpeg,image/png,image/webp,image/gif" hidden aria-label="Choose a photo" onChange={(event) => { const file = event.target.files?.[0]; event.target.value = ""; if (file) void setPhoto(file); }} />
        <button type="button" disabled={busy || !app.auth.isLoggedIn()} style={button(false)} onClick={useProfilePicture} title="Use the resident's public Second Life profile picture, if they have one">USE PROFILE PICTURE</button>
        {contact.photo ? <button type="button" disabled={busy} style={button(false)} onClick={() => { app.contacts.setPhoto(contact.id, null); say("info", "Photo removed."); }}>REMOVE PHOTO</button> : null}
      </div>

      <div style={{ display: "grid", gap: 6 }}>
        <label htmlFor={`note-${contact.id}`} style={label}>NOTE</label>
        <textarea id={`note-${contact.id}`} value={note} maxLength={1000} rows={3} onChange={(event) => setNote(event.target.value)} style={{ ...field, minHeight: 64, padding: 8, resize: "vertical", font: `400 12.5px/1.5 ${t.font}` }} />
        <button type="button" disabled={!noteDirty || busy} style={{ ...button(false), justifySelf: "start", opacity: noteDirty ? 1 : 0.5 }} onClick={() => run(async () => { app.contacts.setNote(contact.id, note); say("info", "Note saved."); })}>SAVE NOTE</button>
      </div>

      <div style={{ display: "grid", gap: 10 }}>
        <span style={label}>LINKS TO OTHER APPS</span>
        {LINK_FIELDS.map(({ service, label: name, placeholder }) => {
          const current = contact.links.find((link) => link.service === service);
          const inputId = `link-${service}-${contact.id}`;
          return (
            <div key={service} style={{ display: "grid", gap: 4 }}>
              <label htmlFor={inputId} style={{ font: `600 11.5px/1.3 ${t.font}` }}>
                {name}{" "}
                {current ? (current.url
                  ? <a href={current.url} target="_blank" rel="noopener noreferrer" style={{ color: V.pri }}>{current.label}</a>
                  : <span style={{ color: V.ink2 }}>{current.label}</span>) : <span style={{ color: V.ink2, fontWeight: 400 }}>not set</span>}
              </label>
              <div style={{ display: "flex", gap: 6 }}>
                <input id={inputId} value={linkInputs[service] || ""} placeholder={placeholder} onChange={(event) => setLinkInputs((previous) => ({ ...previous, [service]: event.target.value }))} onKeyDown={(event) => { if (event.key === "Enter") saveLink(service); }} style={field} />
                <button type="button" style={button(false)} onClick={() => saveLink(service)}>{current ? "REPLACE" : "ADD"}</button>
                {current ? <button type="button" aria-label={`Remove ${name} link`} style={button(false)} onClick={() => app.contacts.removeLink(contact.id, service)}>×</button> : null}
              </div>
              {linkErrors[service] ? <span role="alert" style={{ color: V.err, font: `400 11px/1.4 ${t.font}` }}>{linkErrors[service]}</span> : null}
            </div>
          );
        })}
      </div>

      {googleEnabled ? (
        <div style={{ display: "grid", gap: 6 }}>
          <span style={label}>GOOGLE CONTACTS</span>
          <div style={{ font: `400 11.5px/1.5 ${t.font}`, color: V.ink2 }}>
            {contact.googleResourceName ? "This contact has been copied to your Google Contacts." : "Copies the name, note, links and the photo above to your Google Contacts."}
          </div>
          <button type="button" disabled={busy} style={{ ...button(false), justifySelf: "start" }} onClick={() => copyToGoogle([contact])}>{contact.googleResourceName ? "COPY AGAIN" : "COPY TO GOOGLE CONTACTS"}</button>
        </div>
      ) : null}

      <div style={{ borderTop: `1px solid ${V.outv}`, paddingTop: 10 }}>
        {confirmRemove ? (
          <div style={{ display: "flex", gap: 6, alignItems: "center", flexWrap: "wrap" }}>
            <span style={{ font: `500 11.5px/1.3 ${t.font}` }}>Remove {contact.name} from your saved contacts?</span>
            <button type="button" style={{ ...button(false), borderColor: V.err, color: V.err }} onClick={() => { app.contacts.remove(contact.id); say("info", `${contact.name} removed.`); onClose(); }}>REMOVE</button>
            <button type="button" style={button(false)} onClick={() => setConfirmRemove(false)}>KEEP</button>
          </div>
        ) : (
          <button type="button" style={{ ...button(false), color: V.err }} onClick={() => setConfirmRemove(true)}>REMOVE CONTACT</button>
        )}
      </div>
    </section>
  );
}
