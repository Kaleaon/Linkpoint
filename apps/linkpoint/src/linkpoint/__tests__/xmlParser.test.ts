import { describe, it, expect, beforeEach } from 'vitest';
import { LLSDXMLParser } from '../xmlParser';
import { LLSDException, LLSDMap, LLSDArray } from '../types';

describe('LLSDXMLParser', () => {
    let parser: LLSDXMLParser;

    beforeEach(() => {
        parser = new LLSDXMLParser();
    });

    describe('parse() - Valid Cases', () => {
        it('should parse basic data types', () => {
            const xml = `
                <llsd>
                    <map>
                        <key>bool_true</key><boolean>true</boolean>
                        <key>bool_false</key><boolean>0</boolean>
                        <key>int_val</key><integer>42</integer>
                        <key>real_val</key><real>3.14</real>
                        <key>string_val</key><string>hello world</string>
                        <key>uuid_val</key><uuid>d7f4aeca-88f1-4f73-b712-1d44876b26f5</uuid>
                        <key>date_val</key><date>2024-01-01T12:00:00Z</date>
                        <key>uri_val</key><uri>https://example.com</uri>
                        <key>undef_val</key><undef/>
                        <key>binary_val</key><binary>SGVsbG8=</binary>
                    </map>
                </llsd>
            `;
            const result = parser.parse(xml).getContent() as LLSDMap;

            expect(result['bool_true']).toBe(true);
            expect(result['bool_false']).toBe(false);
            expect(result['int_val']).toBe(42);
            expect(result['real_val']).toBe(3.14);
            expect(result['string_val']).toBe('hello world');
            expect(result['uuid_val']).toBe('d7f4aeca-88f1-4f73-b712-1d44876b26f5');
            expect((result['date_val'] as Date).toISOString()).toBe('2024-01-01T12:00:00.000Z');
            expect((result['uri_val'] as URL).href).toBe('https://example.com/');
            expect(result['undef_val']).toBeNull();

            // Binary should decode 'SGVsbG8=' -> 'Hello'
            const binary = result['binary_val'] as Uint8Array;
            expect(binary).toBeInstanceOf(Uint8Array);
            expect(String.fromCharCode(...binary)).toBe('Hello');
        });

        it('should parse complex structures (nested map and array)', () => {
            const xml = `
                <llsd>
                    <map>
                        <key>my_array</key>
                        <array>
                            <integer>1</integer>
                            <integer>2</integer>
                            <map>
                                <key>nested_key</key>
                                <string>nested_value</string>
                            </map>
                        </array>
                    </map>
                </llsd>
            `;
            const result = parser.parse(xml).getContent() as LLSDMap;

            expect(Array.isArray(result['my_array'])).toBe(true);
            const array = result['my_array'] as LLSDArray;
            expect(array).toHaveLength(3);
            expect(array[0]).toBe(1);
            expect(array[1]).toBe(2);

            const nestedMap = array[2] as LLSDMap;
            expect(nestedMap['nested_key']).toBe('nested_value');
        });

        it('should return null content for empty <llsd> root', () => {
            const xml = `<llsd></llsd>`;
            const result = parser.parse(xml);
            expect(result.getContent()).toBeNull();
        });
    });

    describe('parse() - Empty/Malformed Check', () => {
        it('should throw LLSDException for empty or whitespace-only strings', () => {
            expect(() => parser.parse('')).toThrow(LLSDException);
            expect(() => parser.parse('   ')).toThrow(LLSDException);
            expect(() => parser.parse(null as unknown as string)).toThrow(LLSDException);
            expect(() => parser.parse(undefined as unknown as string)).toThrow(LLSDException);
        });

        it('should throw LLSDException for missing <llsd> root element', () => {
            const xml = `<notllsd><string>test</string></notllsd>`;
            expect(() => parser.parse(xml)).toThrow(LLSDException);
            expect(() => parser.parse(xml)).toThrow(/missing <llsd> root element/);
        });

        it('should throw LLSDException for malformed XML (unclosed tags)', () => {
             // Depending on the xmldom implementation, it may trigger an error in the errorHandler
            const xml = `<llsd>&invalid;</llsd>`;
            expect(() => parser.parse(xml)).toThrow(LLSDException);
        });
    });

    describe('parse() - Specific Type Errors', () => {
        it('should throw for invalid integer', () => {
            const xml = `<llsd><integer>not_an_int</integer></llsd>`;
            expect(() => parser.parse(xml)).toThrow(LLSDException);
            expect(() => parser.parse(xml)).toThrow(/Invalid integer value/);
        });

        it('should throw for invalid real', () => {
            const xml = `<llsd><real>not_a_real</real></llsd>`;
            expect(() => parser.parse(xml)).toThrow(LLSDException);
            expect(() => parser.parse(xml)).toThrow(/Invalid real value/);
        });

        it('should throw for invalid UUID', () => {
            const xml = `<llsd><uuid>invalid-uuid-format</uuid></llsd>`;
            expect(() => parser.parse(xml)).toThrow(LLSDException);
            expect(() => parser.parse(xml)).toThrow(/Invalid UUID value/);
        });

        it('should throw for invalid date', () => {
            const xml = `<llsd><date>invalid-date-format</date></llsd>`;
            expect(() => parser.parse(xml)).toThrow(LLSDException);
            expect(() => parser.parse(xml)).toThrow(/Invalid date value/);
        });

        it('should throw for invalid URI', () => {
            const xml = `<llsd><uri>://invalid-uri</uri></llsd>`;
            expect(() => parser.parse(xml)).toThrow(LLSDException);
            expect(() => parser.parse(xml)).toThrow(/Invalid URI value/);
        });

        it('should throw for invalid binary', () => {
            const xml = `<llsd><binary>invalid bas%%e64</binary></llsd>`;
            expect(() => parser.parse(xml)).toThrow(LLSDException);
            expect(() => parser.parse(xml)).toThrow(/Invalid binary data/);
        });

        it('should throw for unknown element types', () => {
            const xml = `<llsd><unknown_tag>data</unknown_tag></llsd>`;
            expect(() => parser.parse(xml)).toThrow(LLSDException);
            expect(() => parser.parse(xml)).toThrow(/Unknown LLSD element type/);
        });
    });
});
