/**
 * Core LLSD Types Tests
 */

import { LLSD, LLSDUtils, LLSDType, LLSDException, LLSDValue } from '../src/types';

describe('LLSD Core Types', () => {
    describe('LLSDUtils.getType', () => {
        test('should correctly identify boolean type', () => {
            expect(LLSDUtils.getType(true)).toBe(LLSDType.BOOLEAN);
            expect(LLSDUtils.getType(false)).toBe(LLSDType.BOOLEAN);
        });

        test('should correctly identify integer type', () => {
            expect(LLSDUtils.getType(42)).toBe(LLSDType.INTEGER);
            expect(LLSDUtils.getType(0)).toBe(LLSDType.INTEGER);
            expect(LLSDUtils.getType(-123)).toBe(LLSDType.INTEGER);
        });

        test('should correctly identify real type', () => {
            expect(LLSDUtils.getType(3.14)).toBe(LLSDType.REAL);
            expect(LLSDUtils.getType(-2.5)).toBe(LLSDType.REAL);
            expect(LLSDUtils.getType(0.1)).toBe(LLSDType.REAL);
        });

        test('should correctly identify string type', () => {
            expect(LLSDUtils.getType('hello')).toBe(LLSDType.STRING);
            expect(LLSDUtils.getType('')).toBe(LLSDType.STRING);
        });

        test('should correctly identify UUID type', () => {
            const uuid = '550e8400-e29b-41d4-a716-446655440000';
            expect(LLSDUtils.getType(uuid)).toBe(LLSDType.UUID);
        });

        test('should correctly identify date type', () => {
            expect(LLSDUtils.getType(new Date())).toBe(LLSDType.DATE);
        });

        test('should correctly identify URI type', () => {
            expect(LLSDUtils.getType(new URL('https://example.com'))).toBe(LLSDType.URI);
        });

        test('should correctly identify binary type', () => {
            expect(LLSDUtils.getType(new Uint8Array([1, 2, 3]))).toBe(LLSDType.BINARY);
        });

        test('should correctly identify array type', () => {
            expect(LLSDUtils.getType([1, 2, 3])).toBe(LLSDType.ARRAY);
            expect(LLSDUtils.getType([])).toBe(LLSDType.ARRAY);
        });

        test('should correctly identify map type', () => {
            expect(LLSDUtils.getType({ key: 'value' })).toBe(LLSDType.MAP);
            expect(LLSDUtils.getType({})).toBe(LLSDType.MAP);
        });

        test('should correctly identify unknown type', () => {
            expect(LLSDUtils.getType(null)).toBe(LLSDType.UNKNOWN);
            expect(LLSDUtils.getType(undefined)).toBe(LLSDType.UNKNOWN);
        });
    });

    describe('LLSDUtils.isUUIDString', () => {
        test('should validate correct UUID formats', () => {
            expect(LLSDUtils.isUUIDString('550e8400-e29b-41d4-a716-446655440000')).toBe(true);
            expect(LLSDUtils.isUUIDString('00000000-0000-0000-0000-000000000000')).toBe(true);
            expect(LLSDUtils.isUUIDString('FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF')).toBe(true);
        });

        test('should reject invalid UUID formats', () => {
            expect(LLSDUtils.isUUIDString('invalid-uuid')).toBe(false);
            expect(LLSDUtils.isUUIDString('550e8400-e29b-41d4-a716')).toBe(false);
            expect(LLSDUtils.isUUIDString('550e8400-e29b-41d4-a716-446655440000-extra')).toBe(false);
            expect(LLSDUtils.isUUIDString('')).toBe(false);
        });
    });

    describe('LLSDUtils.deepCopy', () => {
        test('should deep copy primitive values', () => {
            expect(LLSDUtils.deepCopy(42)).toBe(42);
            expect(LLSDUtils.deepCopy('hello')).toBe('hello');
            expect(LLSDUtils.deepCopy(true)).toBe(true);
            expect(LLSDUtils.deepCopy(null)).toBe(null);
            expect(LLSDUtils.deepCopy(undefined)).toBe(undefined);
        });

        test('should deep copy dates', () => {
            const original = new Date('2023-01-01');
            const copy = LLSDUtils.deepCopy(original) as Date;
            expect(copy).toEqual(original);
            expect(copy).not.toBe(original);
        });

        test('should deep copy URLs', () => {
            const original = new URL('https://example.com');
            const copy = LLSDUtils.deepCopy(original) as URL;
            expect(copy.href).toBe(original.href);
            expect(copy).not.toBe(original);
        });

        test('should deep copy binary data', () => {
            const original = new Uint8Array([1, 2, 3, 4]);
            const copy = LLSDUtils.deepCopy(original) as Uint8Array;
            expect(copy).toEqual(original);
            expect(copy).not.toBe(original);
        });

        test('should deep copy arrays', () => {
            const original = [1, 'hello', true, [1, 2, 3]];
            const copy = LLSDUtils.deepCopy(original) as any[];
            expect(copy).toEqual(original);
            expect(copy).not.toBe(original);
            expect(copy[3]).not.toBe(original[3]);
        });

        test('should deep copy maps', () => {
            const original = {
                number: 42,
                string: 'hello',
                nested: {
                    value: 123
                }
            };
            const copy = LLSDUtils.deepCopy(original) as any;
            expect(copy).toEqual(original);
            expect(copy).not.toBe(original);
            expect(copy.nested).not.toBe(original.nested);
        });
    });

    describe('LLSDUtils navigation methods', () => {
        const testData = {
            user: {
                name: 'Alice',
                age: 30,
                active: true
            },
            scores: [95, 87, 92]
        };

        test('should safely get string values', () => {
            expect(LLSDUtils.getString(testData, 'user.name')).toBe('Alice');
            expect(LLSDUtils.getString(testData, 'user.missing', 'default')).toBe('default');
            expect(LLSDUtils.getString(testData, 'user.age', 'default')).toBe('default'); // wrong type
        });

        test('should safely get number values', () => {
            expect(LLSDUtils.getNumber(testData, 'user.age')).toBe(30);
            expect(LLSDUtils.getNumber(testData, 'user.missing', 999)).toBe(999);
            expect(LLSDUtils.getNumber(testData, 'user.name', 999)).toBe(999); // wrong type
        });

        test('should safely get boolean values', () => {
            expect(LLSDUtils.getBoolean(testData, 'user.active')).toBe(true);
            expect(LLSDUtils.getBoolean(testData, 'user.missing', false)).toBe(false);
            expect(LLSDUtils.getBoolean(testData, 'user.name', false)).toBe(false); // wrong type
        });

        test('should handle invalid paths gracefully', () => {
            expect(LLSDUtils.getValue(null, 'any.path')).toBe(null);
            expect(LLSDUtils.getValue(testData, '')).toBe(testData);
            expect(LLSDUtils.getValue(testData, 'nonexistent.path')).toBe(null);
        });
    });

    describe('LLSD document', () => {
        test('should create LLSD with content', () => {
            const content = { message: 'hello' };
            const llsd = new LLSD(content);
            expect(llsd.getContent()).toBe(content);
        });

        test('should create LLSD with null content', () => {
            const llsd = new LLSD();
            expect(llsd.getContent()).toBe(null);
        });

        test('should get correct type for content', () => {
            const llsd = new LLSD({ key: 'value' });
            expect(llsd.getType()).toBe(LLSDType.MAP);
        });

        test('should convert to JSON', () => {
            const content = { message: 'hello', number: 42 };
            const llsd = new LLSD(content);
            const json = llsd.toJSON();
            expect(JSON.parse(json)).toEqual(content);
        });

        test('should create from JSON', () => {
            const content = { message: 'hello', number: 42 };
            const json = JSON.stringify(content);
            const llsd = LLSD.fromJSON(json);
            expect(llsd.getContent()).toEqual(content);
        });

        test('should handle invalid JSON', () => {
            expect(() => {
                LLSD.fromJSON('invalid json');
            }).toThrow(LLSDException);
        });
    });

    describe('UUID generation', () => {
        test('should generate valid UUIDs', () => {
            const uuid1 = LLSDUtils.generateUUID();
            const uuid2 = LLSDUtils.generateUUID();
            
            expect(LLSDUtils.isUUIDString(uuid1)).toBe(true);
            expect(LLSDUtils.isUUIDString(uuid2)).toBe(true);
            expect(uuid1).not.toBe(uuid2);
        });
    });
});