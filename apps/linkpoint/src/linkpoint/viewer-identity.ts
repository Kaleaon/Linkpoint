/**
 * Viewer identity.
 *
 * TPV_COMPLIANCE.md §1 requires that the viewer identify itself honestly in the
 * login handshake: the channel must be our own registered name — never a string
 * that could be mistaken for the official Second Life viewer — and the version
 * must reflect the actual build.
 *
 * This is the only place either value is defined. The login request and
 * anything that displays the viewer's identity both read from here, so they can
 * never drift apart.
 */

/** The login channel. Must match the name registered with Linden Lab. */
export const VIEWER_CHANNEL = 'Linkpoint Viewer';

/** The build version sent alongside the channel. */
export const VIEWER_VERSION = '2.0.0';

/** Channel and version as one string, for status lines and diagnostics. */
export const VIEWER_IDENTITY = `${VIEWER_CHANNEL} ${VIEWER_VERSION}`;
