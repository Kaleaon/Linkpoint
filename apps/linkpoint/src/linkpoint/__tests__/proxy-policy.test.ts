import { describe, expect, it } from 'vitest';
import { getAllowedProxyHosts, validateProxyTarget } from '../proxy-policy';

describe('proxy target policy', () => {
  it('allows configured HTTPS login endpoints', () => {
    const target = validateProxyTarget(
      'https://login.agni.lindenlab.com/cgi-bin/login.cgi',
      getAllowedProxyHosts(),
    );

    expect(target.hostname).toBe('login.agni.lindenlab.com');
  });

  it.each([
    'http://login.agni.lindenlab.com/cgi-bin/login.cgi',
    'https://example.com/',
    'https://169.254.169.254/latest/meta-data/',
    'https://user:password@login.agni.lindenlab.com/',
    'not a URL',
  ])('rejects unsafe target %s', (target) => {
    expect(() => validateProxyTarget(target)).toThrow();
  });

  it('uses an explicit deployment allowlist when one is supplied', () => {
    const hosts = getAllowedProxyHosts('login.example-grid.test');
    expect(() => validateProxyTarget('https://login.example-grid.test/', hosts)).not.toThrow();
    expect(() => validateProxyTarget('https://login.agni.lindenlab.com/', hosts)).toThrow('not allowed');
  });
});
