import { describe, it, expect } from 'vitest';
import { toJSON, fromJSON, parseXML, detectFormat, LLSDFormat, serializeXML } from './llsd.js';
import { parseISO } from 'date-fns';




describe('LLSD JSON Serialization (toJSON)', () => {
  it('serializes null', () => {
    expect(toJSON(null)).toBe('null');
  });

  it('serializes boolean values', () => {
    expect(toJSON(true)).toBe('true');
    expect(toJSON(false)).toBe('false');
  });

  it('serializes number values', () => {
    expect(toJSON(42)).toBe('42');
    expect(toJSON(3.14159)).toBe('3.14159');
  });

  it('serializes string values', () => {
    expect(toJSON('hello world')).toBe('"hello world"');
    expect(toJSON('')).toBe('""');
  });

  it('serializes Date objects to ISO strings', () => {
    const d = new Date('2023-01-01T12:00:00.000Z');
    expect(toJSON(d)).toBe('"2023-01-01T12:00:00.000Z"');
  });

  it('serializes Uint8Array to base64 strings', () => {
    // btoa(String.fromCharCode(...[72, 101, 108, 108, 111])) -> "SGVsbG8="
    const data = new Uint8Array([72, 101, 108, 108, 111]); // "Hello"
    expect(toJSON(data)).toBe('"SGVsbG8="');
  });

  it('serializes arrays', () => {
    const arr = [1, "two", true, null];
    // using spacing from `toJSON` which is 2 spaces
    expect(toJSON(arr)).toBe('[\n  1,\n  "two",\n  true,\n  null\n]');
  });

  it('serializes objects', () => {
    const obj = { a: 1, b: "two" };
    expect(toJSON(obj)).toBe('{\n  "a": 1,\n  "b": "two"\n}');
  });

  it('serializes complex nested structures with Date and Uint8Array', () => {
    const d = new Date('2023-01-01T12:00:00.000Z');
    const data = new Uint8Array([1, 2, 3]);
    const obj = {
      timestamp: d,
      payload: data,
      items: [
        { id: 1, valid: true },
        { id: 2, data: new Uint8Array([255, 0]) }
      ]
    };

    // The result should have the correct strings and formatting
    const jsonString = toJSON(obj);
    const parsed = JSON.parse(jsonString);

    expect(parsed.timestamp).toBe('2023-01-01T12:00:00.000Z');
    expect(parsed.payload).toBe(btoa(String.fromCharCode(1, 2, 3)));
    expect(parsed.items[0]).toEqual({ id: 1, valid: true });
    expect(parsed.items[1].data).toBe(btoa(String.fromCharCode(255, 0)));
  });
});


describe('LLSD parseXML', () => {
  it('should parse undef', () => {
    const xml = '<llsd><undef /></llsd>';
    expect(parseXML(xml)).toBeNull();
  });

  it.each([
    ['true', true],
    ['1', true],
    ['false', false],
    ['0', false],
  ])('should parse boolean from "%s" to %s', (value, expected) => {
    const xml = `<llsd><boolean>${value}</boolean></llsd>`;
    expect(parseXML(xml)).toBe(expected);
  });

  it('should parse integer', () => {
    const xml = '<llsd><integer>123</integer></llsd>';
    expect(parseXML(xml)).toBe(123);
  });

  it('should parse real', () => {
    const xml = '<llsd><real>456.78</real></llsd>';
    expect(parseXML(xml)).toBe(456.78);
  });

  it('should parse uuid', () => {
    const xml = '<llsd><uuid>550e8400-e29b-41d4-a716-446655440000</uuid></llsd>';
    expect(parseXML(xml)).toBe('550e8400-e29b-41d4-a716-446655440000');
  });

  it('should parse string', () => {
    const xml = '<llsd><string>hello world</string></llsd>';
    expect(parseXML(xml)).toBe('hello world');
  });

  it('should parse date', () => {
    const xml = '<llsd><date>2023-10-27T10:00:00Z</date></llsd>';
    expect(parseXML(xml)).toEqual(parseISO('2023-10-27T10:00:00Z'));
  });

  it('should parse uri', () => {
    const xml = '<llsd><uri>http://example.com</uri></llsd>';
    expect(parseXML(xml)).toBe('http://example.com');
  });

  it('should parse binary (base64)', () => {
    // "hello" in base64 is aGVsbG8=
    const xml = '<llsd><binary encoding="base64">aGVsbG8=</binary></llsd>';
    const result = parseXML(xml) as Uint8Array;
    expect(result).toBeInstanceOf(Uint8Array);
    expect(Array.from(result)).toEqual([104, 101, 108, 108, 111]); // ASCII for h e l l o
  });

  it('should parse array of primitives', () => {
    const xml = `
      <llsd>
        <array>
          <integer>1</integer>
          <string>two</string>
          <boolean>true</boolean>
        </array>
      </llsd>
    `;
    expect(parseXML(xml)).toEqual([1, 'two', true]);
  });

  it('should parse map of primitives', () => {
    const xml = `
      <llsd>
        <map>
          <key>foo</key>
          <integer>42</integer>
          <key>bar</key>
          <string>test</string>
        </map>
      </llsd>
    `;
    expect(parseXML(xml)).toEqual({
      foo: 42,
      bar: 'test',
    });
  });

  it('should parse nested array and map', () => {
    const xml = `
      <llsd>
        <map>
          <key>items</key>
          <array>
            <map>
              <key>id</key><integer>1</integer>
            </map>
            <map>
              <key>id</key><integer>2</integer>
            </map>
          </array>
        </map>
      </llsd>
    `;
    expect(parseXML(xml)).toEqual({
      items: [
        { id: 1 },
        { id: 2 },
      ]
    });
  });

  it('should throw an error for malformed XML', () => {
    const xml = '<llsd><unclosed_tag></llsd>';
    expect(() => parseXML(xml)).toThrow(/XML Parse Error/);
  });

  it('should return null if <llsd> is missing', () => {
    const xml = '<otherroot><string>hello</string></otherroot>';
    expect(parseXML(xml)).toBeNull();
  });

  it('should return null if <llsd> is empty', () => {
    const xml = '<llsd></llsd>';
    expect(parseXML(xml)).toBeNull();
  });

  it('should return null for unknown tag', () => {
    const xml = '<llsd><unknown>value</unknown></llsd>';
    expect(parseXML(xml)).toBeNull();
  });
});

describe('detectFormat', () => {
  describe('XML Format', () => {
    it('should detect standard XML declaration', () => {
      expect(detectFormat('<?xml version="1.0" encoding="UTF-8"?>\n<llsd>...</llsd>')).toBe(LLSDFormat.XML);
    });
    it('should detect LLSD root element directly', () => {
      expect(detectFormat('<llsd><map><key>foo</key><string>bar</string></map></llsd>')).toBe(LLSDFormat.XML);
    });
    it('should ignore leading whitespace for XML', () => {
      expect(detectFormat('   \n  <?xml version="1.0"?>')).toBe(LLSDFormat.XML);
    });
  });

  describe('JSON Format', () => {
    it('should detect valid JSON object', () => {
      expect(detectFormat('{"key": "value"}')).toBe(LLSDFormat.JSON);
    });
    it('should detect valid JSON array', () => {
      expect(detectFormat('["value1", "value2"]')).toBe(LLSDFormat.JSON);
    });
    it('should ignore leading whitespace for JSON', () => {
      expect(detectFormat('   \n  {"key": "value"}')).toBe(LLSDFormat.JSON);
    });
  });

  describe('LLSD Notation Format', () => {
    describe('Prefix match', () => {
      it('should detect undefined/null (!)', () => {
        expect(detectFormat('!')).toBe(LLSDFormat.NOTATION);
      });
      it('should detect integers (i)', () => {
        expect(detectFormat("i'42'")).toBe(LLSDFormat.NOTATION);
        expect(detectFormat('i"42"')).toBe(LLSDFormat.NOTATION);
      });
      it('should detect reals (r)', () => {
        expect(detectFormat("r'3.14'")).toBe(LLSDFormat.NOTATION);
        expect(detectFormat('r"3.14"')).toBe(LLSDFormat.NOTATION);
      });
      it('should detect UUIDs (u)', () => {
        expect(detectFormat("u'd7f4aeca-88f1-4680-bcac-6a7f05c48873'")).toBe(LLSDFormat.NOTATION);
      });
      it('should detect dates (d)', () => {
        expect(detectFormat("d'2023-01-01T12:00:00Z'")).toBe(LLSDFormat.NOTATION);
      });
      it('should detect URIs/links (l)', () => {
        expect(detectFormat("l'http://example.com'")).toBe(LLSDFormat.NOTATION);
      });
      it('should detect binary data (b)', () => {
        expect(detectFormat("b'base64data'")).toBe(LLSDFormat.NOTATION);
      });
    });

    describe('Fallback from JSON-like structures', () => {
      it('should fallback to NOTATION for invalid JSON object', () => {
        expect(detectFormat('{key: "value"}')).toBe(LLSDFormat.NOTATION); // Invalid JSON, unquoted key
      });
      it('should fallback to NOTATION for invalid JSON array', () => {
        expect(detectFormat('["value1", value2]')).toBe(LLSDFormat.NOTATION); // Invalid JSON, unquoted string
      });
    });

    describe('Other notation structures', () => {
      it('should detect unquoted string (fallback)', () => {
        expect(detectFormat('someRandomString')).toBe(LLSDFormat.NOTATION);
      });
      it('should detect booleans (true/false) as NOTATION since they do not match XML/JSON starts', () => {
        expect(detectFormat('true')).toBe(LLSDFormat.NOTATION);
        expect(detectFormat('false')).toBe(LLSDFormat.NOTATION);
      });
      it('should detect plain numbers as NOTATION since they do not match XML/JSON starts', () => {
        expect(detectFormat('42')).toBe(LLSDFormat.NOTATION);
        expect(detectFormat('3.14')).toBe(LLSDFormat.NOTATION);
      });
    });
  });
});


describe('serializeXML', () => {
  it('serializes null to undef', () => {
    const xml = serializeXML(null);
    expect(xml).toContain('<undef />');
  });

  it('serializes boolean to boolean', () => {
    expect(serializeXML(true)).toContain('<boolean>true</boolean>');
    expect(serializeXML(false)).toContain('<boolean>false</boolean>');
  });

  it('serializes integers to integer', () => {
    expect(serializeXML(42)).toContain('<integer>42</integer>');
    expect(serializeXML(0)).toContain('<integer>0</integer>');
    expect(serializeXML(-1)).toContain('<integer>-1</integer>');
  });

  it('serializes floats to real', () => {
    expect(serializeXML(3.14)).toContain('<real>3.14</real>');
    expect(serializeXML(-0.5)).toContain('<real>-0.5</real>');
  });

  it('serializes Date to date', () => {
    const date = new Date('2023-10-01T12:00:00Z');
    const xml = serializeXML(date);
    expect(xml).toMatch(/<date>.*2023.*<\/date>/);
  });

  it('serializes Uint8Array to binary', () => {
    const bytes = new Uint8Array([104, 101, 108, 108, 111]); // 'hello'
    const xml = serializeXML(bytes);
    expect(xml).toContain('<binary encoding="base64">aGVsbG8=</binary>');
  });

  it('serializes Arrays', () => {
    const xml = serializeXML([1, "test"]);
    expect(xml).toContain('<array>');
    expect(xml).toContain('<integer>1</integer>');
    expect(xml).toContain('<string>test</string>');
    expect(xml).toContain('</array>');
  });

  it('serializes Maps', () => {
    const xml = serializeXML({ foo: "bar", baz: 42 });
    expect(xml).toContain('<map>');
    expect(xml).toContain('<key>foo</key>');
    expect(xml).toContain('<string>bar</string>');
    expect(xml).toContain('<key>baz</key>');
    expect(xml).toContain('<integer>42</integer>');
    expect(xml).toContain('</map>');
  });

  it('serializes strings to string', () => {
    expect(serializeXML("hello world")).toContain('<string>hello world</string>');
    expect(serializeXML("")).toContain('<string></string>');
  });

  it('serializes valid UUIDs to uuid', () => {
    const uuid = "123e4567-e89b-12d3-a456-426614174000";
    expect(serializeXML(uuid)).toContain(`<uuid>${uuid}</uuid>`);
  });

  it('serializes URIs to uri', () => {
    expect(serializeXML("http://example.com")).toContain('<uri>http://example.com</uri>');
    expect(serializeXML("https://secure.example.com")).toContain('<uri>https://secure.example.com</uri>');
  });

  it('includes xml declaration and root llsd element', () => {
    const xml = serializeXML("test");
    expect(xml).toContain('<?xml version="1.0" encoding="UTF-8"?>');
    expect(xml).toContain('<llsd>');
    expect(xml).toMatch(/<\/llsd>$/);
  });
});
