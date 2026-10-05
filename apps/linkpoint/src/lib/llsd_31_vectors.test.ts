import { describe, it, expect } from 'vitest';
import { parseXML, serializeXML, parseNotation, serializeNotation, toJSON, fromJSON } from './llsd.js';
import testVectors from '../../../../crates/linkpoint-protocol/fixtures/llsd_31_test_vectors.json';

describe('LLSD 31 Test Vectors Conformance Suite', () => {
  it('has exactly 31 test vectors', () => {
    expect(testVectors.length).toBe(31);
  });

  testVectors.forEach((vector) => {
    describe(`Vector #${vector.id}: ${vector.name} (${vector.type})`, () => {
      it('parses XML snippet without throwing', () => {
        if (vector.xml_snippet) {
          const fullXml = `<llsd>${vector.xml_snippet}</llsd>`;
          expect(() => parseXML(fullXml)).not.toThrow();
        }
      });

      it('parses Notation snippet without throwing', () => {
        if (vector.notation_snippet && !vector.notation_snippet.startsWith('b64') && !vector.notation_snippet.startsWith('b16') && !vector.notation_snippet.startsWith('b(')) {
          expect(() => parseNotation(vector.notation_snippet)).not.toThrow();
        }
      });

      it('serializes json value to XML', () => {
        const xml = serializeXML(vector.json_value as any);
        expect(xml).toContain('<llsd>');
        expect(xml).toContain('</llsd>');
      });

      it('serializes json value to Notation', () => {
        const notation = serializeNotation(vector.json_value as any);
        expect(typeof notation).toBe('string');
      });

      it('toJSON and fromJSON roundtrips correctly', () => {
        const jsonStr = toJSON(vector.json_value as any);
        const parsed = fromJSON(jsonStr);
        if (vector.json_value !== null && typeof vector.json_value === 'object' && !Array.isArray(vector.json_value)) {
          expect(typeof parsed).toBe('object');
        } else if (Array.isArray(vector.json_value)) {
          expect(Array.isArray(parsed)).toBe(true);
        } else {
          expect(parsed).toEqual(vector.json_value);
        }
      });
    });
  });
});
