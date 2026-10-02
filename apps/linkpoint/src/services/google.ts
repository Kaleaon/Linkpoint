/**
 * Loads the Google services on demand. Nothing from Firebase or Google is
 * imported until this is called, which only happens after the user has turned
 * the integration on in Settings.
 */
export async function loadGoogle() {
  const [auth, contacts, calendar] = await Promise.all([import("./googleAuth"), import("./googleContacts"), import("./googleCalendar")]);
  return { auth, contacts, calendar };
}

export type GoogleServices = Awaited<ReturnType<typeof loadGoogle>>;
