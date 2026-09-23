import { afterEach, describe, expect, it, vi } from 'vitest';
import { SLProtocol } from '../sl-protocol-real';

describe('SLProtocol custom grids', () => {
  afterEach(() => vi.unstubAllGlobals());

  it('accepts an HTTPS custom grid endpoint without embedded credentials', () => {
    const protocol = new SLProtocol() as any;
    expect(protocol.createCustomGrid('https://login.example-grid.test/cgi-bin/login.cgi')).toEqual({
      name: 'login.example-grid.test',
      loginUrl: 'https://login.example-grid.test/cgi-bin/login.cgi',
    });
  });

  it.each([
    'http://login.example-grid.test/',
    'https://user:password@login.example-grid.test/',
    'not a URL',
  ])('rejects unsafe custom endpoint %s', (endpoint) => {
    const protocol = new SLProtocol() as any;
    expect(protocol.createCustomGrid(endpoint)).toBeNull();
  });
});
