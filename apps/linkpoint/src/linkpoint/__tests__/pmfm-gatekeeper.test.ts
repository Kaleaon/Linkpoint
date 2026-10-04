import { describe, expect, it } from 'vitest';
import { createRequire } from 'node:module';

const require = createRequire(import.meta.url);
const actions = require('../../../core/sl-actions.cjs');

describe('PMFM Hypergrid URI Query Parsing', () => {
  it('extracts manifoldFrame metadata from hypergrid destination URIs', () => {
    const dest = actions.parseDestination(
      'hg.osgrid.org:8002:RingRegion/128/128/30?manifold_frame_id=ring-frame-1&topology_type=RINGWORLD_CYLINDER&radius=100000&size_x=262144&size_y=256'
    );

    expect(dest.isHypergrid).toBe(true);
    expect(dest.region).toBe('RingRegion');
    expect(dest.manifoldFrame).toBeDefined();
    expect(dest.manifoldFrame.frameId).toBe('ring-frame-1');
    expect(dest.manifoldFrame.topologyType).toBe('RINGWORLD_CYLINDER');
    expect(dest.manifoldFrame.radius).toBe(100000);
    expect(dest.manifoldFrame.sizeX).toBe(262144);
    expect(dest.manifoldFrame.sizeY).toBe(256);
  });

  it('defaults to undefined manifoldFrame when query params are missing', () => {
    const dest = actions.parseDestination('hg.osgrid.org:8002:FlatRegion');
    expect(dest.isHypergrid).toBe(true);
    expect(dest.region).toBe('FlatRegion');
    expect(dest.manifoldFrame).toBeUndefined();
  });
});
