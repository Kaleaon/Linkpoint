/**
 * Copy saved Linkpoint contacts to Google Contacts (optional, user-initiated).
 *
 * Only what the user chose to keep is sent: the resident's name, Second Life
 * UUID and web profile, the note, the photo the user set, and any Telegram,
 * Discord or web links. Contacts are tagged with the resident's UUID so a second
 * push never creates a duplicate.
 */

import { googleFetch } from "./googleApi";
import type { Contact } from "../linkpoint/contacts";

const PEOPLE = "https://people.googleapis.com/v1";
const PAGE_SIZE = 200;
const MAX_PAGES = 20;
const RESOURCE_NAME = /^people\/[A-Za-z0-9_-]+$/;

export interface GoogleSlContact {
  resourceName: string;
  slUuid: string;
  slName: string;
}

/** Contacts in the user's Google account that were created from Second Life, keyed by resident UUID. */
export async function fetchSlContacts(): Promise<Map<string, GoogleSlContact>> {
  const found = new Map<string, GoogleSlContact>();
  let pageToken = "";
  for (let page = 0; page < MAX_PAGES; page++) {
    const url = `${PEOPLE}/people/me/connections?personFields=names,userDefined&pageSize=${PAGE_SIZE}${pageToken ? `&pageToken=${encodeURIComponent(pageToken)}` : ""}`;
    const data = await (await googleFetch("contacts", url)).json();
    for (const person of data.connections || []) {
      const fields: any[] = person.userDefined || [];
      const uuid = fields.find((field) => field.key === "SL_UUID")?.value;
      if (uuid && typeof person.resourceName === "string" && RESOURCE_NAME.test(person.resourceName)) {
        found.set(String(uuid), { resourceName: person.resourceName, slUuid: String(uuid), slName: String(fields.find((field) => field.key === "SL_NAME")?.value || person.names?.[0]?.displayName || "") });
      }
    }
    pageToken = data.nextPageToken || "";
    if (!pageToken) break;
  }
  return found;
}

/** The resident's public web profile page, or null if the name is not a plausible username. */
export function profileUrl(name: string): string | null {
  const parts = String(name || "").trim().toLowerCase().split(/\s+/).filter(Boolean);
  if (parts.length > 1 && parts[parts.length - 1] === "resident") parts.pop();
  const username = parts.join(".");
  return /^[a-z0-9][a-z0-9._-]{0,62}$/.test(username) ? `https://my.secondlife.com/${username}` : null;
}

const SERVICE_LABEL: Record<string, string> = { telegram: "Telegram", discord: "Discord", web: "Website" };

/** The People API record for a contact. Nothing is added that the user did not provide. */
export function buildPerson(contact: Contact, grid: string): Record<string, unknown> {
  const parts = contact.name.trim().split(/\s+/);
  const person: Record<string, unknown> = {
    names: [{ givenName: parts[0], ...(parts.length > 1 ? { familyName: parts.slice(1).join(" ") } : {}) }],
    userDefined: [
      { key: "SL_UUID", value: contact.id },
      { key: "SL_NAME", value: contact.name },
      { key: "SL_GRID", value: grid },
    ],
  };
  if (contact.note.trim()) person.biographies = [{ value: contact.note.trim(), contentType: "TEXT_PLAIN" }];

  const urls: Array<{ value: string; type: string }> = [];
  const profile = profileUrl(contact.name);
  if (profile) urls.push({ value: profile, type: "profile" });
  for (const link of contact.links) {
    if (link.url) urls.push({ value: link.url, type: SERVICE_LABEL[link.service] || link.service });
    else (person.userDefined as any[]).push({ key: SERVICE_LABEL[link.service] || link.service, value: link.label });
  }
  if (urls.length) person.urls = urls;
  return person;
}

export interface PushResult {
  resourceName: string;
  /** False when the resident was already in Google Contacts and nothing was changed. */
  created: boolean;
  /** True/false once a photo upload was attempted; null when there was no photo to send. */
  photoUploaded: boolean | null;
  photoError?: string;
}

/**
 * Create a Google contact for `contact`, unless one with the same resident UUID
 * already exists (`existing`, from `fetchSlContacts`). A photo that fails to upload
 * is reported in the result; the contact itself is still created.
 */
export async function pushContact(contact: Contact, options: { grid: string; existing?: Map<string, GoogleSlContact> }): Promise<PushResult> {
  const already = options.existing?.get(contact.id);
  if (already) return { resourceName: already.resourceName, created: false, photoUploaded: null };

  const response = await googleFetch("contacts", `${PEOPLE}/people:createContact?personFields=names,userDefined`, { method: "POST", body: JSON.stringify(buildPerson(contact, options.grid)) });
  const created = await response.json();
  const resourceName = created?.resourceName;
  if (typeof resourceName !== "string" || !RESOURCE_NAME.test(resourceName)) throw new Error("Google returned an unexpected contact id.");

  if (!contact.photo) return { resourceName, created: true, photoUploaded: null };
  try {
    const bytes = contact.photo.replace(/^data:image\/[a-z]+;base64,/, "");
    await googleFetch("contacts", `${PEOPLE}/${resourceName}:updateContactPhoto`, { method: "POST", body: JSON.stringify({ photoBytes: bytes }) });
    return { resourceName, created: true, photoUploaded: true };
  } catch (error: any) {
    return { resourceName, created: true, photoUploaded: false, photoError: error?.message || "The photo could not be uploaded." };
  }
}

export async function deleteGoogleContact(resourceName: string): Promise<void> {
  if (!RESOURCE_NAME.test(resourceName)) throw new Error("That is not a Google contact id.");
  await googleFetch("contacts", `${PEOPLE}/${resourceName}:deleteContact`, { method: "DELETE" });
}
