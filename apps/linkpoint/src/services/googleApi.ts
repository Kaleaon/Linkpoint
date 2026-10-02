/**
 * One place that talks to Google's REST APIs: attaches the feature's token,
 * turns failures into readable errors, and drops an expired token so the UI can
 * ask the user to sign in again.
 */

import { dropGoogleToken, getGoogleToken, type GoogleFeature } from "./googleAuth";

export class GoogleApiError extends Error {
  constructor(message: string, readonly status: number, readonly signInRequired = false) {
    super(message);
    this.name = "GoogleApiError";
  }
}

/** The message Google put in an error body, if it did. */
async function readError(response: Response): Promise<string> {
  try {
    const body = await response.json();
    const message = body?.error?.message || body?.error_description;
    if (typeof message === "string" && message) return message;
  } catch {
    // not JSON
  }
  return `Google answered ${response.status}`;
}

export async function googleFetch(feature: GoogleFeature, url: string, init: RequestInit = {}): Promise<Response> {
  const token = getGoogleToken(feature);
  if (!token) throw new GoogleApiError("Sign in with Google to continue.", 401, true);
  const response = await fetch(url, {
    ...init,
    headers: { Accept: "application/json", ...(init.body ? { "Content-Type": "application/json" } : {}), ...(init.headers || {}), Authorization: `Bearer ${token}` },
  });
  if (response.status === 401) {
    dropGoogleToken(feature);
    throw new GoogleApiError("Your Google sign-in expired. Sign in again.", 401, true);
  }
  if (response.status === 403) {
    const message = await readError(response);
    throw new GoogleApiError(`Google refused: ${message}`, 403);
  }
  if (!response.ok) throw new GoogleApiError(await readError(response), response.status);
  return response;
}
