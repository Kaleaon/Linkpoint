/**
 * Optional Google sign-in for Contacts and Calendar.
 *
 * Off unless the user turns it on in Settings, and loaded only then (this file
 * pulls in Firebase, so it is imported dynamically through `./google`). Each
 * feature asks Google for only the access it needs, when the user first uses it,
 * rather than one broad request up front:
 *   - contacts: create and read contacts (people API)
 *   - calendar: create and read events (calendar API)
 * Access tokens are kept in memory only and are gone when the page closes.
 */

import { initializeApp, getApps, getApp } from "firebase/app";
import { getAuth, signInWithPopup, GoogleAuthProvider, signOut, type User } from "firebase/auth";
import firebaseConfig from "../../firebase-applet-config.json";

export type GoogleFeature = "contacts" | "calendar";

export const GOOGLE_SCOPES: Record<GoogleFeature, string> = {
  contacts: "https://www.googleapis.com/auth/contacts",
  calendar: "https://www.googleapis.com/auth/calendar.events",
};

const app = getApps().length ? getApp() : initializeApp(firebaseConfig);
export const auth = getAuth(app);

const tokens: Partial<Record<GoogleFeature, string>> = {};

export interface GoogleAccount {
  email: string;
  name: string;
}

export function getGoogleAccount(): GoogleAccount | null {
  const user = auth.currentUser;
  return user ? { email: user.email || "", name: user.displayName || user.email || "Google account" } : null;
}

/** The in-memory access token for a feature, or null if the user has not signed in for it this session. */
export function getGoogleToken(feature: GoogleFeature): string | null {
  return tokens[feature] || null;
}

/** Forget a feature's token, for example after Google reports it expired. */
export function dropGoogleToken(feature: GoogleFeature) {
  delete tokens[feature];
}

/** Ask the user to sign in and grant access for one feature. Must be called from a user action (it opens a popup). */
export async function signInWithGoogle(feature: GoogleFeature): Promise<GoogleAccount> {
  const provider = new GoogleAuthProvider();
  provider.addScope(GOOGLE_SCOPES[feature]);
  const result = await signInWithPopup(auth, provider);
  const credential = GoogleAuthProvider.credentialFromResult(result);
  if (!credential?.accessToken) throw new Error("Google did not grant access. Try again.");
  tokens[feature] = credential.accessToken;
  const user: User = result.user;
  return { email: user.email || "", name: user.displayName || user.email || "Google account" };
}

export async function signOutGoogle(): Promise<void> {
  delete tokens.contacts;
  delete tokens.calendar;
  await signOut(auth);
}
