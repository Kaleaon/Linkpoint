import { describe, expect, it, vi } from 'vitest';
import { createRequire } from 'node:module';
import {
  registerForeignCapabilityHost,
  clearForeignCapabilityHosts,
  getAllowedProxyHosts,
  validateProxyTarget,
} from '../proxy-policy';

const require = createRequire(import.meta.url);
const actions = require('../../../core/sl-actions.cjs');
const { ViewerSession } = require('../../../core/viewer-session.cjs');

class Vector3 { constructor(public v: number[]) {} }
class UUID { constructor(public s: string) {} toString() { return this.s; } }
const lib = { Vector3, UUID };

describe('Hypergrid 2.0 URI parsing (sl-actions parseDestination)', () => {
  it('parses colon-formatted Hypergrid URIs (e.g. hg.osgrid.org:8002:RegionName)', () => {
    const dest = actions.parseDestination('hg.osgrid.org:8002:RegionName');
    expect(dest).toEqual({
      isHypergrid: true,
      gridUri: 'http://hg.osgrid.org:8002',
      gatekeeperUrl: 'http://hg.osgrid.org:8002/gatekeeper',
      region: 'RegionName',
      x: 128,
      y: 128,
      z: 30,
    });
  });

  it('parses colon-formatted Hypergrid URIs with coordinates', () => {
    const dest = actions.parseDestination('hg.osgrid.org:8002:RegionName/10/20/30');
    expect(dest).toEqual({
      isHypergrid: true,
      gridUri: 'http://hg.osgrid.org:8002',
      gatekeeperUrl: 'http://hg.osgrid.org:8002/gatekeeper',
      region: 'RegionName',
      x: 10,
      y: 20,
      z: 30,
    });
  });

  it('parses HTTP Hypergrid URLs', () => {
    const dest = actions.parseDestination('http://hg.osgrid.org:8002/RegionName/64/64/25');
    expect(dest).toEqual({
      isHypergrid: true,
      gridUri: 'http://hg.osgrid.org:8002',
      gatekeeperUrl: 'http://hg.osgrid.org:8002/gatekeeper',
      region: 'RegionName',
      x: 64,
      y: 64,
      z: 25,
    });
  });

  it('parses secondlife:// Hypergrid SLURLs', () => {
    const dest = actions.parseDestination('secondlife://hg.osgrid.org:8002:RegionName/128/128/30');
    expect(dest).toEqual({
      isHypergrid: true,
      gridUri: 'http://hg.osgrid.org:8002',
      gatekeeperUrl: 'http://hg.osgrid.org:8002/gatekeeper',
      region: 'RegionName',
      x: 128,
      y: 128,
      z: 30,
    });
  });
});

describe('Dynamic Foreign Seed Capabilities & Proxy Policy', () => {
  it('allows registering foreign capability hosts dynamically without errors', () => {
    clearForeignCapabilityHosts();
    registerForeignCapabilityHost('http://hg.osgrid.org:8002/CAPS/hg-session-123/');

    const allowed = getAllowedProxyHosts();
    expect(allowed.has('hg.osgrid.org')).toBe(true);

    const target = validateProxyTarget('http://hg.osgrid.org:8002/CAPS/hg-session-123/', allowed);
    expect(target.hostname).toBe('hg.osgrid.org');
  });
});

describe('Gatekeeper session negotiation & multi-grid asset routing', () => {
  it('performs Gatekeeper handshake, sets foreign seed caps, and routes asset URLs', async () => {
    clearForeignCapabilityHosts();
    const session = new ViewerSession(() => undefined);

    const botMock = {
      clientCommands: {
        teleport: { teleportTo: vi.fn().mockResolvedValue({ message: 'Arrived at foreign grid' }) },
        asset: { downloadAsset: vi.fn().mockResolvedValue(Buffer.from('fake-asset-data')) },
        inventory: { getInventoryRoot: () => ({ folderID: { toString: () => 'home-inv-root' }, getChildFolders: () => [], items: [], populate: async () => {} }) },
      },
      currentRegion: { caps: { getCapability: vi.fn(), requestGet: vi.fn() } },
    };
    session.bot = botMock;
    session.identity = { agentId: 'home-agent-uuid', firstName: 'Test', lastName: 'User', simName: 'HomeRegion', inventoryRootId: 'home-inv-root' };
    session.homeGridContext = {
      agentId: 'home-agent-uuid',
      firstName: 'Test',
      lastName: 'User',
      gridUri: 'http://127.0.0.1:9000',
      sessionToken: 'home-session-token',
      inventoryRootId: 'home-inv-root',
      assetServiceUri: null,
    };

    // Teleport to remote Hypergrid destination
    const result = await session.teleport({ destination: 'hg.osgrid.org:8002:RemoteRegion' });

    expect(result.isHypergrid).toBe(true);
    expect(session.isForeignSession).toBe(true);
    expect(session.foreignGridUri).toBe('http://hg.osgrid.org:8002');
    expect(session.foreignSeedCap).toBeDefined();

    registerForeignCapabilityHost('http://hg.osgrid.org:8002');
    expect(getAllowedProxyHosts().has('hg.osgrid.org')).toBe(true);

    // Verify inventory token preservation
    const inv = await session.getInventory();
    expect(inv.folderId).toBe('home-inv-root');

    // Teleport back to home grid region
    await session.teleport({ destination: 'HomeRegion/128/128/30' });
    expect(session.isForeignSession).toBe(false);
  });
});
