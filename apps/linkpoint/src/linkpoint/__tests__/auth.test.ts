import { beforeEach, describe, expect, it } from 'vitest';
import { AuthManager } from '../auth';

describe('AuthManager session persistence', () => {
  beforeEach(() => localStorage.clear());

  it('removes stale session identifiers instead of restoring a disconnected session', () => {
    localStorage.setItem('linkpoint_session', JSON.stringify({
      user: { fullName: 'Ruth Resident' },
      sessionId: 'stale-session-id',
      agentId: 'stale-agent-id',
    }));
    const auth = new AuthManager({ connected: false });

    auth.init();

    expect(auth.user).toBeNull();
    expect(auth.hasSavedSession()).toBe(false);
    expect(localStorage.getItem('linkpoint_session')).toBeNull();
  });
});
