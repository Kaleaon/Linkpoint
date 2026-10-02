import { describe, expect, it } from 'vitest';
import { deskKind, deskGeometry, elbowPath, floaterStyle, chipStyle, sweepSegmentFills } from '../../theme/deskStyle.js';
import { LAYOUTS } from '../../theme/layouts.js';
import { PALETTES } from '../../theme/palettes.js';

const V = { ...PALETTES.lcars.c, ...LAYOUTS.sweep.s };
const ink = (_bg: string, candidates: string[]) => candidates[0];

describe('desktop layout families', () => {
  it('maps layout navigation models to desktop families', () => {
    expect(deskKind(LAYOUTS.sweep)).toBe('sweep');
    expect(deskKind(LAYOUTS.tiles)).toBe('metro');
    for (const key of ['terminal', 'glass', 'rules', 'press']) expect(deskKind(LAYOUTS[key])).toBe('default');
  });

  it('keeps the established geometry for the default family', () => {
    expect(deskGeometry('default')).toMatchObject({ top: 38, rail: 42, bar: 26, dock: 40 });
    expect(deskGeometry('nonsense')).toEqual(deskGeometry('default'));
  });

  it('gives Sweep a framed elbow and Metro none', () => {
    const sweep = deskGeometry('sweep');
    expect(sweep.frame).toBeGreaterThan(0);
    expect(sweep.elbow).toBeGreaterThan(0);
    expect(sweep.top).toBeGreaterThan(sweep.frame + 20); // room for the location bar under the frame
    expect(deskGeometry('metro')).toMatchObject({ frame: 0, elbow: 0 });
  });

  it('draws a closed elbow whose inner curve joins the bar edge to the rail edge', () => {
    const g = deskGeometry('sweep');
    const path = elbowPath(g);
    expect(path.startsWith('M0,44')).toBe(true);
    expect(path.endsWith('Z')).toBe(true);
    expect(path).toContain(`V${g.frame}`);
    expect(path).toContain(`${g.rail},${g.frame + g.elbow}`);
    expect(path).toContain(`H${g.rail + g.elbow}`);
  });

  it('never uses the active fill for inactive sweep rail segments', () => {
    expect(sweepSegmentFills(V).slice(1)).not.toContain(V.pri);
  });

  it('styles floaters per family: Sweep caps, Metro flat/borderless, default bordered', () => {
    const args = { V, t: { dfont: 'x', font: 'x' }, act: true, ink };
    expect(floaterStyle('sweep', args).frame).toMatchObject({ borderLeftStyle: 'solid', borderLeftWidth: '10px', borderTopStyle: 'none' });
    const metro = floaterStyle('metro', args);
    expect(metro.frame).toMatchObject({ borderLeftStyle: 'none', borderRightStyle: 'none', borderRadius: 0, boxShadow: 'none' });
    expect(metro.frame).not.toHaveProperty('border');
    expect(metro.bar.textTransform).toBe('lowercase');
    expect(metro.bar.font).toContain('300');
    expect(floaterStyle('default', args).frame.border).toContain('1px solid');
    expect((floaterStyle('metro', { ...args, act: false }).frame as Record<string, unknown>).borderTopColor).toBe('transparent');
  });

  it('styles dock chips per family', () => {
    const args = { V, t: { dfont: 'x', font: 'x' }, on: false, ink, dim: false };
    expect(chipStyle('sweep', args).borderRadius).toBe('999px');
    expect(chipStyle('metro', args).borderRadius).toBe(0);
    expect(chipStyle('metro', args).textTransform).toBe('lowercase');
    expect(chipStyle('default', { ...args, dim: true }).background).toBe('transparent');
  });
});
