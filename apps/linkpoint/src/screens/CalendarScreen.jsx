import { useEffect, useMemo, useState } from "react";
import { app } from "../linkpoint/app.ts";
import { SLT_ZONE, fromLocalInputValue, suggestEventTime, toLocalInputValue } from "../linkpoint/calendar-time.ts";
import { buildIcs, icsFileName } from "../linkpoint/ics.ts";
import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import Icon from "../components/Icon.jsx";
import useGoogleEnabled from "../hooks/useGoogleEnabled.js";
import { loadGoogle } from "../services/google.ts";

const deviceZone = () => { try { return Intl.DateTimeFormat().resolvedOptions().timeZone || "UTC"; } catch { return "UTC"; } };

function zoneChoices() {
  const choices = [
    { id: SLT_ZONE, label: "Second Life Time (US Pacific)" },
    { id: "UTC", label: "UTC" },
    { id: "America/New_York", label: "US Eastern" },
    { id: "America/Chicago", label: "US Central" },
  ];
  const device = deviceZone();
  if (!choices.some((choice) => choice.id === device)) choices.push({ id: device, label: `This device (${device})` });
  return choices;
}

function downloadFile(name, text, type) {
  const url = URL.createObjectURL(new Blob([text], { type }));
  const link = document.createElement("a");
  link.href = url;
  link.download = name;
  document.body.appendChild(link);
  link.click();
  link.remove();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}

// Calendar: turn group notices into calendar events. A notice can always be saved as an
// .ics file for any calendar app. Adding it straight to Google Calendar is optional and
// appears only once Google has been switched on in Settings.
export default function CalendarScreen() {
  const { state } = useApp();
  const tab = state.tabs?.Calendar || "NOTICES";
  return tab === "GOOGLE CALENDAR" ? <GoogleEvents /> : <NoticeCalendar />;
}

function NoticeCalendar() {
  const { V, t } = useTheme();
  const [notices, setNotices] = useState(() => app.notices.list());
  const [selectedId, setSelectedId] = useState(() => app.notices.takeFocus());

  useEffect(() => {
    const onNotices = (list) => setNotices(list);
    const onFocus = () => { const id = app.notices.takeFocus(); if (id) setSelectedId(id); };
    app.notices.on("notices_changed", onNotices);
    app.notices.on("focus_changed", onFocus);
    setNotices(app.notices.list());
    return () => { app.notices.off("notices_changed", onNotices); app.notices.off("focus_changed", onFocus); };
  }, []);

  const selected = selectedId ? notices.find((notice) => notice.id === selectedId) || null : null;
  const groupName = (id) => (id ? app.groups.getGroups().find((group) => group.id === id || group.groupId === id)?.name : null) || "";

  if (!notices.length) {
    return <div className="honest-empty"><Icon name="calendar" size={30} /><p>No group notices yet. When a group you belong to sends one, it appears here so you can add it to a calendar.</p></div>;
  }

  return (
    <div style={{ flex: 1, minHeight: 0, display: "flex", flexDirection: "column", gap: 10, padding: 12, background: V.bg, color: V.ink }}>
      <ul aria-label="Group notices" style={{ listStyle: "none", margin: 0, padding: 0, flex: selected ? "0 0 32%" : 1, minHeight: 90, overflowY: "auto", display: "grid", gap: 6, alignContent: "start" }}>
        {notices.map((notice) => (
          <li key={notice.id}>
            <button type="button" aria-pressed={selectedId === notice.id} onClick={() => setSelectedId(selectedId === notice.id ? null : notice.id)}
              style={{ width: "100%", textAlign: "left", padding: "8px 10px", background: V.surf, border: `1px solid ${selectedId === notice.id ? V.pri : V.outv}`, borderRadius: V.rs, color: V.ink, cursor: "pointer", font: `400 12px/1.3 ${t.font}` }}>
              <strong style={{ display: "block", fontSize: 13 }}>{notice.subject}</strong>
              <small style={{ color: V.ink2 }}>{[groupName(notice.groupId), new Date(notice.timestamp).toLocaleString()].filter(Boolean).join(" · ")}</small>
              {notice.calendar ? <small style={{ color: V.pri, marginLeft: 6, fontWeight: 700 }}>{notice.calendar.eventId ? "IN GOOGLE CALENDAR" : "SAVED AS FILE"}</small> : null}
            </button>
          </li>
        ))}
      </ul>
      {selected ? <NoticeEditor key={selected.id} notice={selected} groupName={groupName(selected.groupId)} /> : null}
    </div>
  );
}

function NoticeEditor({ notice, groupName }) {
  const { V, t } = useTheme();
  const { actions } = useApp();
  const googleEnabled = useGoogleEnabled();
  const suggestion = useMemo(() => suggestEventTime(`${notice.subject}\n${notice.message}`, notice.timestamp), [notice.id]);
  const zones = useMemo(zoneChoices, []);
  const [zone, setZone] = useState(() => (zones.some((choice) => choice.id === suggestion.timeZone) ? suggestion.timeZone : SLT_ZONE));
  const [title, setTitle] = useState(notice.subject);
  const [start, setStart] = useState(() => toLocalInputValue(suggestion.start, zones.some((choice) => choice.id === suggestion.timeZone) ? suggestion.timeZone : SLT_ZONE));
  const [end, setEnd] = useState(() => toLocalInputValue(suggestion.end, zones.some((choice) => choice.id === suggestion.timeZone) ? suggestion.timeZone : SLT_ZONE));
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState(null);

  const startAt = fromLocalInputValue(start, zone);
  const endAt = fromLocalInputValue(end, zone);
  const problem = !startAt || !endAt ? "Enter a valid start and end time." : endAt <= startAt ? "The event must end after it starts." : null;

  // Changing the zone keeps the same moment and re-reads it on the new clock.
  const changeZone = (next) => {
    if (startAt && endAt) { setStart(toLocalInputValue(startAt, next)); setEnd(toLocalInputValue(endAt, next)); }
    setZone(next);
  };

  const description = [groupName ? `Group: ${groupName}` : null, notice.from ? `Sent by: ${notice.from}` : null, "", notice.message].filter((line) => line !== null).join("\n");
  const location = `Second Life${groupName ? ` (${groupName})` : ""}`;

  const saveFile = () => {
    if (problem) return;
    try {
      const text = buildIcs({ uid: `${notice.id}@linkpoint`, summary: title || notice.subject, description, location, start: startAt, end: endAt, reminders: [30, 10] });
      downloadFile(icsFileName(title || notice.subject), text, "text/calendar");
      app.notices.markAdded(notice.id, { eventId: null, link: null });
      setMessage({ kind: "info", text: "Calendar file saved. Open it to add the event to your calendar app." });
    } catch (error) {
      setMessage({ kind: "error", text: error?.message || "The calendar file could not be made." });
    }
  };

  const addToGoogle = async () => {
    if (problem) return;
    setBusy(true);
    setMessage(null);
    try {
      const google = await loadGoogle();
      if (!google.auth.getGoogleToken("calendar")) await google.auth.signInWithGoogle("calendar");
      const { event, existing } = await google.calendar.addNoticeToCalendar(
        { id: notice.id, subject: notice.subject, message: notice.message, from: notice.from, groupName },
        { start: startAt, end: endAt, timeZone: zone, summary: title || notice.subject, location },
      );
      app.notices.markAdded(notice.id, { eventId: event.id, link: event.htmlLink });
      setMessage({ kind: "info", text: existing ? "This notice was already in your Google Calendar." : "Added to your Google Calendar." });
      actions.notify(existing ? "Already in Google Calendar" : "Added to Google Calendar");
    } catch (error) {
      setMessage({ kind: "error", text: error?.message || "Google Calendar did not accept the event." });
    } finally {
      setBusy(false);
    }
  };

  const field = { minHeight: 36, padding: "0 8px", border: `1px solid ${V.outv}`, borderRadius: V.rs, background: V.bg, color: V.ink, font: `400 12.5px/1 ${t.font}`, minWidth: 0 };
  const label = { font: `700 10px/1 ${t.font}`, letterSpacing: ".1em", color: V.ink2 };
  const button = (primary) => ({ minHeight: 36, padding: "0 14px", border: `1px solid ${primary ? V.pri : V.outv}`, borderRadius: V.rs, background: primary ? V.pri : V.surf, color: primary ? V.onpri : V.ink, cursor: busy || problem ? "default" : "pointer", opacity: busy || problem ? 0.55 : 1, font: `700 10.5px/1 ${t.font}`, letterSpacing: ".08em" });

  return (
    <section aria-label="Add notice to calendar" style={{ flex: 1, minHeight: 0, overflowY: "auto", padding: 12, background: V.surf, border: `1px solid ${V.pri}`, borderRadius: V.rs, display: "grid", gap: 12, alignContent: "start" }}>
      <div style={{ whiteSpace: "pre-wrap", overflowWrap: "anywhere", font: `400 12.5px/1.6 ${t.font}`, color: V.ink2, maxHeight: 120, overflowY: "auto" }}>{notice.message || "This notice has no message."}</div>

      <div role="status" style={{ font: `500 11.5px/1.5 ${t.font}`, color: suggestion.found ? V.ink : V.err }}>
        {suggestion.found
          ? `Found a time in the notice${suggestion.assumedZone ? "; it names no time zone, so Second Life Time is assumed" : ""}. Check it below.`
          : "No time was found in this notice. Choose when the event happens."}
      </div>

      <div style={{ display: "grid", gap: 6 }}>
        <label htmlFor="cal-title" style={label}>TITLE</label>
        <input id="cal-title" value={title} maxLength={300} onChange={(event) => setTitle(event.target.value)} style={field} />
      </div>
      <div style={{ display: "grid", gap: 6 }}>
        <label htmlFor="cal-zone" style={label}>TIME ZONE</label>
        <select id="cal-zone" value={zone} onChange={(event) => changeZone(event.target.value)} style={field}>
          {zones.map((choice) => <option key={choice.id} value={choice.id}>{choice.label}</option>)}
        </select>
      </div>
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
        <div style={{ display: "grid", gap: 6 }}>
          <label htmlFor="cal-start" style={label}>STARTS</label>
          <input id="cal-start" type="datetime-local" value={start} onChange={(event) => setStart(event.target.value)} style={field} />
        </div>
        <div style={{ display: "grid", gap: 6 }}>
          <label htmlFor="cal-end" style={label}>ENDS</label>
          <input id="cal-end" type="datetime-local" value={end} onChange={(event) => setEnd(event.target.value)} style={field} />
        </div>
      </div>
      {problem ? <div role="alert" style={{ color: V.err, font: `500 11.5px/1.4 ${t.font}` }}>{problem}</div> : null}

      <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
        <button type="button" disabled={Boolean(problem)} style={button(!googleEnabled)} onClick={saveFile}>SAVE CALENDAR FILE (.ICS)</button>
        {googleEnabled ? <button type="button" disabled={busy || Boolean(problem)} style={button(true)} onClick={addToGoogle}>{busy ? "ADDING…" : "ADD TO GOOGLE CALENDAR"}</button> : null}
      </div>
      {!googleEnabled ? (
        <div style={{ font: `400 11px/1.5 ${t.font}`, color: V.ink2 }}>
          Google Calendar is off.{" "}
          <button type="button" onClick={() => actions.setScreen("Settings")} style={{ background: "none", border: 0, padding: 0, color: V.pri, cursor: "pointer", font: "inherit", textDecoration: "underline" }}>Turn it on in Settings</button> to add notices directly.
        </div>
      ) : null}

      {message ? <div role={message.kind === "error" ? "alert" : "status"} style={{ color: message.kind === "error" ? V.err : V.ink, font: `500 11.5px/1.5 ${t.font}` }}>{message.text}</div> : null}
      {notice.calendar?.link ? <a href={notice.calendar.link} target="_blank" rel="noopener noreferrer" style={{ color: V.pri, font: `600 12px/1 ${t.font}` }}>Open in Google Calendar</a> : null}
    </section>
  );
}

function GoogleEvents() {
  const { V, t } = useTheme();
  const { actions } = useApp();
  const googleEnabled = useGoogleEnabled();
  const [events, setEvents] = useState(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");

  const refresh = async () => {
    setBusy(true);
    setError("");
    try {
      const google = await loadGoogle();
      if (!google.auth.getGoogleToken("calendar")) await google.auth.signInWithGoogle("calendar");
      setEvents(await google.calendar.fetchLinkpointEvents());
    } catch (failure) {
      setError(failure?.message || "Could not load your Google Calendar.");
    } finally {
      setBusy(false);
    }
  };

  const remove = async (event) => {
    setBusy(true);
    setError("");
    try {
      const google = await loadGoogle();
      await google.calendar.deleteCalendarEvent(event.id);
      for (const notice of app.notices.list()) if (notice.calendar?.eventId === event.id) app.notices.clearAdded(notice.id);
      setEvents((list) => (list || []).filter((item) => item.id !== event.id));
    } catch (failure) {
      setError(failure?.message || "Could not delete the event.");
    } finally {
      setBusy(false);
    }
  };

  const button = { minHeight: 34, padding: "0 12px", border: `1px solid ${V.outv}`, borderRadius: V.rs, background: V.surf, color: V.pri, cursor: busy ? "default" : "pointer", opacity: busy ? 0.6 : 1, font: `700 10.5px/1 ${t.font}`, letterSpacing: ".08em" };

  if (!googleEnabled) {
    return (
      <div className="honest-empty">
        <Icon name="calendar" size={30} />
        <p>Google Calendar is off. Notices can still be saved as calendar files.</p>
        <button type="button" className="screen-action" onClick={() => actions.setScreen("Settings")}>Open Settings</button>
      </div>
    );
  }

  return (
    <div style={{ flex: 1, minHeight: 0, overflowY: "auto", padding: 12, display: "grid", gap: 10, alignContent: "start", background: V.bg, color: V.ink }}>
      <div style={{ font: `400 11.5px/1.5 ${t.font}`, color: V.ink2 }}>Upcoming events Linkpoint added to your Google Calendar.</div>
      <button type="button" disabled={busy} style={{ ...button, justifySelf: "start" }} onClick={refresh}>{busy ? "LOADING…" : events ? "REFRESH" : "SIGN IN AND LOAD"}</button>
      {error ? <div role="alert" style={{ color: V.err, font: `500 11.5px/1.4 ${t.font}` }}>{error}</div> : null}
      {events && !events.length ? <div className="honest-empty"><p>No upcoming events were added by Linkpoint.</p></div> : null}
      {events?.length ? (
        <ul aria-label="Upcoming Linkpoint events" style={{ listStyle: "none", margin: 0, padding: 0, display: "grid", gap: 6 }}>
          {events.map((event) => (
            <li key={event.id} style={{ display: "flex", gap: 8, alignItems: "center", padding: "8px 10px", background: V.surf, border: `1px solid ${V.outv}`, borderRadius: V.rs }}>
              <span style={{ flex: 1, minWidth: 0 }}>
                <strong style={{ display: "block", fontSize: 13 }}>{event.summary}</strong>
                <small style={{ color: V.ink2 }}>{event.start ? new Date(event.start).toLocaleString() : "No start time"}</small>
              </span>
              {event.htmlLink ? <a href={event.htmlLink} target="_blank" rel="noopener noreferrer" style={{ color: V.pri, font: `600 11px/1 ${t.font}` }}>OPEN</a> : null}
              <button type="button" disabled={busy} style={{ ...button, color: V.err }} aria-label={`Delete ${event.summary}`} onClick={() => remove(event)}>DELETE</button>
            </li>
          ))}
        </ul>
      ) : null}
    </div>
  );
}
