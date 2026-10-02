import { describe, expect, it } from 'vitest';
import { actionButtonStyle, cardAccentStyle, cardLooks, fullBorder, sideBorder } from '../../theme/look.js';

const V: any = { outv: '#444', surf: '#111', sec2: '#0af', pri: '#f0f', onpri: '#fff', ink: '#eee', err: '#f00', rp: '8px', rs: '4px' };

/** React warns when a style object mixes a shorthand with its longhands (they fight on rerender). */
function mixesShorthand(style: object) {
  const keys = Object.keys(style);
  const has = (re: RegExp) => keys.some((k) => re.test(k));
  return (keys.includes('border') && has(/^border(Top|Right|Bottom|Left|Color|Style|Width)/))
    || (keys.includes('borderColor') && has(/^border(Top|Right|Bottom|Left)Color$/))
    || (keys.includes('borderStyle') && has(/^border(Top|Right|Bottom|Left)Style$/))
    || (keys.includes('borderWidth') && has(/^border(Top|Right|Bottom|Left)Width$/))
    || ['Top', 'Right', 'Bottom', 'Left'].some((s) => keys.includes(`border${s}`) && has(new RegExp(`^border${s}(Color|Style|Width)$`)));
}

describe('border styles', () => {
  it('sideBorder / fullBorder emit only per-side longhands', () => {
    expect(mixesShorthand(sideBorder('Left', '4px', '#fff'))).toBe(false);
    expect(mixesShorthand(fullBorder('1px', '#fff'))).toBe(false);
    expect(sideBorder('Left', '4px', '#fff')).toMatchObject({ borderLeftStyle: 'solid', borderLeftWidth: '4px', borderTopStyle: 'none', borderTopWidth: 0 });
    expect(sideBorder(null)).toMatchObject({ borderLeftStyle: 'none', borderTopStyle: 'none' });
  });

  it('every card look and accent combination avoids shorthand/longhand mixes', () => {
    const looks = cardLooks(V, '8px') as Record<string, Record<string, unknown>>;
    for (const [kind, style] of Object.entries(looks)) {
      expect(mixesShorthand(style), kind).toBe(false);
      const accent = cardAccentStyle(kind, V, '#0f0');
      expect(mixesShorthand({ ...style, ...accent }), `${kind} + accent`).toBe(false);
    }
  });

  it('action buttons keep colour overrides as longhands over longhand bases', () => {
    for (const a of [{}, { primary: true }, { dim: true }]) expect(mixesShorthand(actionButtonStyle(V, 'x', a))).toBe(false);
  });
});
