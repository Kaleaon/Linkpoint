import { describe, expect, it, vi } from 'vitest';
import { CapabilityPermitService, extractSeedCapability } from '../proxy-permit';

const secret = 'a-production-secret-that-is-longer-than-thirty-two-characters';

describe('capability permits', () => {
  it('permits only HTTPS capability hosts returned by login', () => {
    const permits = new CapabilityPermitService(secret);
    const token = permits.issue(['https://caps.example-grid.test/seed']);

    expect(permits.permits(new URL('https://caps.example-grid.test/event-queue'), token)).toBe(true);
    expect(permits.permits(new URL('https://other.example-grid.test/'), token)).toBe(false);
  });

  it('rejects a tampered or expired permit', () => {
    const permits = new CapabilityPermitService(secret, 1);
    const token = permits.issue(['https://caps.example-grid.test/'])!;
    expect(permits.permits(new URL('https://caps.example-grid.test/'), `${token}x`)).toBe(false);
    vi.spyOn(Date, 'now').mockReturnValue(Date.now() + 2);
    expect(permits.permits(new URL('https://caps.example-grid.test/'), token)).toBe(false);
    vi.restoreAllMocks();
  });

  it('extracts a seed capability from a login response', () => {
    expect(extractSeedCapability('<name>seed_capability</name><value><string>https://caps.example.test/seed</string></value>'))
      .toEqual(['https://caps.example.test/seed']);
  });
});
