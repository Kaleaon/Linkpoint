/**
 * LLSD Binary Parser - TypeScript Implementation
 * 
 * Based on Java implementation and Second Life viewer binary parsing
 * Copyright (C) 2024 Linden Lab
 */

import { LLSD, LLSDValue, LLSDMap, LLSDArray, LLSDException } from './types';

export class LLSDBinaryParser {
    private static readonly LLSD_BINARY_MAGIC = 0x6c6c7364; // 'llsd'
    private data: Uint8Array;
    private position: number;

    constructor() {
        this.data = new Uint8Array();
        this.position = 0;
    }

    /**
     * Parse LLSD from binary data
     */
    parse(data: Uint8Array): LLSD {
        this.data = data;
        this.position = 0;

        try {
            // Check magic number
            const magic = this.readUInt32();
            if (magic !== LLSDBinaryParser.LLSD_BINARY_MAGIC) {
                throw new LLSDException('Invalid LLSD binary format: magic number mismatch');
            }

            const content = this.parseValue();
            return new LLSD(content);
        } catch (error) {
            if (error instanceof LLSDException) {
                throw error;
            }
            throw new LLSDException(`Failed to parse LLSD binary: ${error}`, error as Error);
        }
    }

    /**
     * Parse a single value from binary data
     */
    private parseValue(): LLSDValue {
        if (this.position >= this.data.length) {
            throw new LLSDException('Unexpected end of binary data');
        }

        const type = this.readUInt8();

        switch (type) {
            case 0: // Undefined
                return null;

            case 1: // Boolean
                return this.readUInt8() !== 0;

            case 2: // Integer
                return this.readInt32();

            case 3: // Real
                return this.readDouble();

            case 4: // String
                return this.readString();

            case 5: // UUID
                return this.readUUID();

            case 6: // Date
                const timestamp = this.readDouble();
                return new Date(timestamp * 1000);

            case 7: // URI
                const uriString = this.readString();
                return new URL(uriString);

            case 8: // Binary
                const length = this.readUInt32();
                const binaryData = this.readBytes(length);
                return binaryData;

            case 9: // Array
                const arrayLength = this.readUInt32();
                const array: LLSDArray = [];
                for (let i = 0; i < arrayLength; i++) {
                    array.push(this.parseValue());
                }
                return array;

            case 10: // Map
                const mapLength = this.readUInt32();
                const map: LLSDMap = {};
                for (let i = 0; i < mapLength; i++) {
                    const key = this.readString();
                    const value = this.parseValue();
                    map[key] = value;
                }
                return map;

            default:
                throw new LLSDException(`Unknown binary type: ${type}`);
        }
    }

    /**
     * Read a single byte
     */
    private readUInt8(): number {
        if (this.position >= this.data.length) {
            throw new LLSDException('Unexpected end of binary data');
        }
        return this.data[this.position++];
    }

    /**
     * Read a 32-bit signed integer
     */
    private readInt32(): number {
        const bytes = this.readBytes(4);
        const view = new DataView(bytes.buffer);
        return view.getInt32(0, false); // Big endian
    }

    /**
     * Read a 32-bit unsigned integer
     */
    private readUInt32(): number {
        const bytes = this.readBytes(4);
        const view = new DataView(bytes.buffer);
        return view.getUint32(0, false); // Big endian
    }

    /**
     * Read a 64-bit double
     */
    private readDouble(): number {
        const bytes = this.readBytes(8);
        const view = new DataView(bytes.buffer);
        return view.getFloat64(0, false); // Big endian
    }

    /**
     * Read a string
     */
    private readString(): string {
        const length = this.readUInt32();
        if (length === 0) {
            return '';
        }
        const bytes = this.readBytes(length);
        return new TextDecoder('utf-8').decode(bytes);
    }

    /**
     * Read a UUID
     */
    private readUUID(): string {
        const bytes = this.readBytes(16);
        const hex = Array.from(bytes).map(b => b.toString(16).padStart(2, '0')).join('');
        return [
            hex.substring(0, 8),
            hex.substring(8, 12),
            hex.substring(12, 16),
            hex.substring(16, 20),
            hex.substring(20, 32)
        ].join('-');
    }

    /**
     * Read a specified number of bytes
     */
    private readBytes(length: number): Uint8Array {
        if (this.position + length > this.data.length) {
            throw new LLSDException(`Cannot read ${length} bytes: not enough data`);
        }
        
        const result = this.data.slice(this.position, this.position + length);
        this.position += length;
        return result;
    }
}