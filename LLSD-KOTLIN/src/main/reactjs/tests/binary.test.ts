/**
 * LLSD Binary Parser and Serializer Tests
 */

import { LLSD, LLSDException } from '../src/types';
import { LLSDBinaryParser } from '../src/binaryParser';
import { LLSDBinarySerializer } from '../src/binarySerializer';

describe('LLSD Binary Parser', () => {
    let parser: LLSDBinaryParser;

    beforeEach(() => {
        parser = new LLSDBinaryParser();
    });

    describe('Binary Parsing', () => {
        test('should parse binary with correct magic number', () => {
            // Create mock binary data with magic number
            const magic = 0x6c6c7364; // 'llsd-'
            const data = new Uint8Array(9); // 4 bytes magic + 1 byte type + 4 bytes length
            const view = new DataView(data.buffer);
            
            // Write magic number (big endian)
            view.setUint32(0, magic, false);
            // Write undefined type
            view.setUint8(4, 0);
            
            const llsd = parser.parse(data);
            expect(llsd).toBeInstanceOf(LLSD);
            expect(llsd.getContent()).toBe(null);
        });

        test('should reject data with invalid magic number', () => {
            const data = new Uint8Array(8);
            const view = new DataView(data.buffer);
            view.setUint32(0, 0x12345678, false); // Invalid magic
            
            expect(() => {
                parser.parse(data);
            }).toThrow(LLSDException);
        });

        test('should handle empty data gracefully', () => {
            const data = new Uint8Array(0);
            
            expect(() => {
                parser.parse(data);
            }).toThrow(LLSDException);
        });

        test('should parse boolean values', () => {
            const magic = 0x6c6c7364;
            const data = new Uint8Array(6);
            const view = new DataView(data.buffer);
            
            // Magic + boolean type + value
            view.setUint32(0, magic, false);
            view.setUint8(4, 1); // Boolean type
            view.setUint8(5, 1); // True value
            
            const llsd = parser.parse(data);
            expect(llsd.getContent()).toBe(true);
        });

        test('should parse integer values', () => {
            const magic = 0x6c6c7364;
            const data = new Uint8Array(9);
            const view = new DataView(data.buffer);
            
            // Magic + integer type + value
            view.setUint32(0, magic, false);
            view.setUint8(4, 2); // Integer type
            view.setInt32(5, 42, false); // Integer value
            
            const llsd = parser.parse(data);
            expect(llsd.getContent()).toBe(42);
        });

        test('should parse real values', () => {
            const magic = 0x6c6c7364;
            const data = new Uint8Array(13);
            const view = new DataView(data.buffer);
            
            // Magic + real type + value
            view.setUint32(0, magic, false);
            view.setUint8(4, 3); // Real type
            view.setFloat64(5, 3.14159, false); // Real value
            
            const llsd = parser.parse(data);
            expect(llsd.getContent()).toBeCloseTo(3.14159, 5);
        });

        test('should parse string values', () => {
            const magic = 0x6c6c7364;
            const testString = 'Hello';
            const stringBytes = new TextEncoder().encode(testString);
            const data = new Uint8Array(9 + stringBytes.length);
            const view = new DataView(data.buffer);
            
            // Magic + string type + length + data
            view.setUint32(0, magic, false);
            view.setUint8(4, 4); // String type
            view.setUint32(5, stringBytes.length, false); // String length
            data.set(stringBytes, 9);
            
            const llsd = parser.parse(data);
            expect(llsd.getContent()).toBe('Hello');
        });

        test('should parse UUID values', () => {
            const magic = 0x6c6c7364;
            const data = new Uint8Array(21); // Magic + type + 16 bytes UUID
            const view = new DataView(data.buffer);
            
            view.setUint32(0, magic, false);
            view.setUint8(4, 5); // UUID type
            
            // Set UUID bytes for '550e8400-e29b-41d4-a716-446655440000'
            const uuidBytes = new Uint8Array([
                0x55, 0x0e, 0x84, 0x00, 0xe2, 0x9b, 0x41, 0xd4,
                0xa7, 0x16, 0x44, 0x66, 0x55, 0x44, 0x00, 0x00
            ]);
            data.set(uuidBytes, 5);
            
            const llsd = parser.parse(data);
            expect(llsd.getContent()).toBe('550e8400-e29b-41d4-a716-446655440000');
        });

        test('should handle truncated data', () => {
            const magic = 0x6c6c7364;
            const data = new Uint8Array(6); // Too short for integer
            const view = new DataView(data.buffer);
            
            view.setUint32(0, magic, false);
            view.setUint8(4, 2); // Integer type but no data
            
            expect(() => {
                parser.parse(data);
            }).toThrow(LLSDException);
        });
    });
});

describe('LLSD Binary Serializer', () => {
    let serializer: LLSDBinarySerializer;

    beforeEach(() => {
        serializer = new LLSDBinarySerializer();
    });

    describe('Binary Serialization', () => {
        test('should serialize null/undefined values', () => {
            const llsd = new LLSD(null);
            const data = serializer.serialize(llsd);
            
            expect(data.length).toBe(5); // Magic (4) + type (1)
            
            const view = new DataView(data.buffer);
            expect(view.getUint32(0, false)).toBe(0x6c6c7364); // Magic
            expect(view.getUint8(4)).toBe(0); // Undefined type
        });

        test('should serialize boolean values', () => {
            const llsd = new LLSD(true);
            const data = serializer.serialize(llsd);
            
            expect(data.length).toBe(6); // Magic (4) + type (1) + value (1)
            
            const view = new DataView(data.buffer);
            expect(view.getUint32(0, false)).toBe(0x6c6c7364); // Magic
            expect(view.getUint8(4)).toBe(1); // Boolean type
            expect(view.getUint8(5)).toBe(1); // True value
        });

        test('should serialize integer values', () => {
            const llsd = new LLSD(42);
            const data = serializer.serialize(llsd);
            
            expect(data.length).toBe(9); // Magic (4) + type (1) + value (4)
            
            const view = new DataView(data.buffer);
            expect(view.getUint32(0, false)).toBe(0x6c6c7364); // Magic
            expect(view.getUint8(4)).toBe(2); // Integer type
            expect(view.getInt32(5, false)).toBe(42); // Integer value
        });

        test('should serialize real values', () => {
            const llsd = new LLSD(3.14159);
            const data = serializer.serialize(llsd);
            
            expect(data.length).toBe(13); // Magic (4) + type (1) + value (8)
            
            const view = new DataView(data.buffer);
            expect(view.getUint32(0, false)).toBe(0x6c6c7364); // Magic
            expect(view.getUint8(4)).toBe(3); // Real type
            expect(view.getFloat64(5, false)).toBeCloseTo(3.14159, 5);
        });

        test('should serialize string values', () => {
            const testString = 'Hello';
            const llsd = new LLSD(testString);
            const data = serializer.serialize(llsd);
            
            const expectedLength = 4 + 1 + 4 + 5; // Magic + type + length + string
            expect(data.length).toBe(expectedLength);
            
            const view = new DataView(data.buffer);
            expect(view.getUint32(0, false)).toBe(0x6c6c7364); // Magic
            expect(view.getUint8(4)).toBe(4); // String type
            expect(view.getUint32(5, false)).toBe(5); // String length
            
            const stringBytes = data.slice(9);
            const decodedString = new TextDecoder().decode(stringBytes);
            expect(decodedString).toBe('Hello');
        });

        test('should serialize UUID values', () => {
            const uuid = '550e8400-e29b-41d4-a716-446655440000';
            const llsd = new LLSD(uuid);
            const data = serializer.serialize(llsd);
            
            expect(data.length).toBe(21); // Magic (4) + type (1) + UUID (16)
            
            const view = new DataView(data.buffer);
            expect(view.getUint32(0, false)).toBe(0x6c6c7364); // Magic
            expect(view.getUint8(4)).toBe(5); // UUID type
            
            // Check UUID bytes
            const uuidBytes = data.slice(5);
            const expectedBytes = new Uint8Array([
                0x55, 0x0e, 0x84, 0x00, 0xe2, 0x9b, 0x41, 0xd4,
                0xa7, 0x16, 0x44, 0x66, 0x55, 0x44, 0x00, 0x00
            ]);
            expect(uuidBytes).toEqual(expectedBytes);
        });

        test('should serialize date values', () => {
            const date = new Date('2023-01-01T00:00:00.000Z');
            const llsd = new LLSD(date);
            const data = serializer.serialize(llsd);
            
            expect(data.length).toBe(13); // Magic (4) + type (1) + timestamp (8)
            
            const view = new DataView(data.buffer);
            expect(view.getUint32(0, false)).toBe(0x6c6c7364); // Magic
            expect(view.getUint8(4)).toBe(6); // Date type
            
            const timestamp = view.getFloat64(5, false);
            expect(timestamp).toBe(date.getTime() / 1000);
        });

        test('should serialize binary values', () => {
            const binary = new Uint8Array([1, 2, 3, 4, 5]);
            const llsd = new LLSD(binary);
            const data = serializer.serialize(llsd);
            
            const expectedLength = 4 + 1 + 4 + 5; // Magic + type + length + data
            expect(data.length).toBe(expectedLength);
            
            const view = new DataView(data.buffer);
            expect(view.getUint32(0, false)).toBe(0x6c6c7364); // Magic
            expect(view.getUint8(4)).toBe(8); // Binary type
            expect(view.getUint32(5, false)).toBe(5); // Binary length
            
            const binaryData = data.slice(9);
            expect(binaryData).toEqual(binary);
        });

        test('should serialize array values', () => {
            const array = [1, 'hello'];
            const llsd = new LLSD(array);
            const data = serializer.serialize(llsd);
            
            const view = new DataView(data.buffer);
            expect(view.getUint32(0, false)).toBe(0x6c6c7364); // Magic
            expect(view.getUint8(4)).toBe(9); // Array type
            expect(view.getUint32(5, false)).toBe(2); // Array length
            
            // Should contain serialized elements
            expect(data.length).toBeGreaterThan(9);
        });

        test('should serialize map values', () => {
            const map = { name: 'Alice', age: 30 };
            const llsd = new LLSD(map);
            const data = serializer.serialize(llsd);
            
            const view = new DataView(data.buffer);
            expect(view.getUint32(0, false)).toBe(0x6c6c7364); // Magic
            expect(view.getUint8(4)).toBe(10); // Map type
            expect(view.getUint32(5, false)).toBe(2); // Map size
            
            // Should contain serialized key-value pairs
            expect(data.length).toBeGreaterThan(9);
        });

        test('should handle invalid UUID format', () => {
            const llsd = new LLSD('invalid-uuid');
            // Since this is detected as a string, not UUID, it should serialize as string
            const data = serializer.serialize(llsd);
            
            const view = new DataView(data.buffer);
            expect(view.getUint8(4)).toBe(4); // String type, not UUID type
        });
    });

    describe('Round-trip testing', () => {
        test('should maintain data integrity for primitive types', () => {
            const parser = new LLSDBinaryParser();
            
            // Test with integer
            const originalInt = 12345;
            const llsdInt = new LLSD(originalInt);
            const dataInt = serializer.serialize(llsdInt);
            const parsedInt = parser.parse(dataInt);
            expect(parsedInt.getContent()).toBe(originalInt);
            
            // Test with string
            const originalString = 'Test String';
            const llsdString = new LLSD(originalString);
            const dataString = serializer.serialize(llsdString);
            const parsedString = parser.parse(dataString);
            expect(parsedString.getContent()).toBe(originalString);
        });

        test('should maintain data integrity for complex structures', () => {
            const parser = new LLSDBinaryParser();
            
            const original = {
                name: 'Test User',
                age: 25,
                scores: [100, 95, 87],
                active: true
            };
            
            const llsd = new LLSD(original);
            const data = serializer.serialize(llsd);
            const parsed = parser.parse(data);
            
            expect(parsed.getContent()).toEqual(original);
        });
    });
});