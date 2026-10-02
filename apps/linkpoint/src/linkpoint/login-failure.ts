/**
 * A login the grid refused, with the reason it gave. The interface uses `details`
 * to respond: ask for a multi-factor code, say the account is already logged in,
 * and so on, instead of showing a generic failure.
 */
export interface LoginFailureDetails {
  reason: string;
  code: string;
  mfaRequired: boolean;
  message: string;
  gridMessage?: string;
}

export class LoginFailure extends Error {
  constructor(public details: LoginFailureDetails) {
    super(details.message);
    this.name = 'LoginFailure';
  }
}

const PREFIX = 'LOGIN_FAILURE:';

/**
 * Recover structured details from whatever a login attempt threw. Electron only
 * carries an error's message across its IPC boundary, so the details arrive
 * embedded in it; the web server returns them as JSON fields.
 */
export function toLoginFailure(error: unknown): unknown {
  if (error instanceof LoginFailure) return error;
  const message = error instanceof Error ? error.message : typeof error === 'string' ? error : '';
  const at = message.indexOf(PREFIX);
  if (at === -1) return error;
  try {
    const details = JSON.parse(message.slice(at + PREFIX.length)) as LoginFailureDetails;
    if (details && typeof details.message === 'string') return new LoginFailure(details);
  } catch {
    // Fall through to the original error.
  }
  return error;
}

/** Build a LoginFailure from a server error body, if it carries a reason code. */
export function failureFromResponseBody(body: any): LoginFailure | null {
  if (!body || typeof body.code !== 'string') return null;
  return new LoginFailure({
    reason: String(body.reason || ''),
    code: body.code,
    mfaRequired: Boolean(body.mfaRequired),
    message: String(body.message || body.error || 'Login failed'),
    gridMessage: body.gridMessage,
  });
}
