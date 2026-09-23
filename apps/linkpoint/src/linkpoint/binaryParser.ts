/**
 * LLSD Binary Parser - TypeScript Implementation
 *
 * Based on Java implementation and Second Life viewer binary parsing
 * Copyright (C) 2024 Linden Lab
 */

import { LLSD, LLSDValue, LLSDMap, LLSDArray, LLSDException } from './types';

export class LLSDBinaryParser {
    private data: Uint8Array = new Uint8Array(0);
    private offset: number = 0;

    /**
     * Parse LLSD from binary data
     */
    parse(data: Uint8Array): LLSD {
        this.data = data;
        this.offset = 0;

        // Check for binary header (if any)
        // LLSD binary usually starts with '<?llsd/binary?>' or just the binary data
        // For simplicity, we assume the data is the raw binary content

        return new LLSD(this.parseValue());
    }

    private parseValue(): LLSDValue {
        if (this.offset >= this.data.length) {
            return null;
        }

        const typeCode = String.fromCharCode(this.data[this.offset++]);

        switch (typeCode) {
            case '!': // undef
                return null;

            case '1': // boolean true
                return true;

            case '0': // boolean false
                return false;

            case 'i': // integer (4 bytes)
                const intVal = this.readInt32();
                return intVal;

            case 'r': // real (8 bytes)
                const realVal = this.readFloat64();
                return realVal;

            case 'u': // uuid (16 bytes)
                const uuidVal = this.readUUID();
                return uuidVal;

            case 's': // string
                const strLen = this.readInt32();
                const strVal = this.readString(strLen);
                return strVal;

            case 'd': // date (8 bytes)
                const dateVal = this.readFloat64(); // seconds since epoch
                return new Date(dateVal * 1000);

            case 'l': // uri
                const uriLen = this.readInt32();
                const uriVal = this.readString(uriLen);
                try {
                    return new URL(uriVal);
                } catch {
                    return uriVal;
                }

            case 'b': // binary
                const binLen = this.readInt32();
                const binVal = this.readBytes(binLen);
                return binVal;

            case '[': // array
                const arrayLen = this.readInt32();
                const array: LLSDArray = [];
                for (let i = 0; i < arrayLen; i++) {
                    array.push(this.parseValue());
                }
                // Check for closing bracket (optional in some implementations)
                if (this.offset < this.data.length && String.fromCharCode(this.data[this.offset]) === ']') {
                    this.offset++;
                }
                return array;

            case '{': // map
                const mapLen = this.readInt32();
                const map: LLSDMap = {};
                for (let i = 0; i < mapLen; i++) {
                    const keyType = String.fromCharCode(this.data[this.offset++]);
                    if (keyType !== 'k') {
                        throw new LLSDException(`Expected key type 'k', got '${keyType}'`);
                    }
                    const keyLen = this.readInt32();
                    const key = this.readString(keyLen);
                    map[key] = this.parseValue();
                }
                // Check for closing brace (optional in some implementations)
                if (this.offset < this.data.length && String.fromCharCode(this.data[this.offset]) === '}') {
                    this.offset++;
                }
                return map;

            default:
                throw new LLSDException(`Unknown LLSD binary type code: '${typeCode}' at offset ${this.offset - 1}`);
        }
    }

    private readInt32(): number {
        const value = (this.data[this.offset] << 24) |
                      (this.data[this.offset + 1] << 16) |
                      (this.data[this.offset + 2] << 8) |
                      (this.data[this.offset + 3]);
        this.offset += 4;
        return value;
    }

    private readFloat64(): number {
        const buffer = new ArrayBuffer(8);
        const view = new DataView(buffer);
        for (let i = 0; i < 8; i++) {
            view.setUint8(i, this.data[this.offset + i]);
        }
        this.offset += 8;
        return view.getFloat64(0, false); // Big-endian
    }

    private readUUID(): string {
        const bytes = this.readBytes(16);
        const hex = Array.from(bytes).map(b => b.toString(16).padStart(2, '0')).join('');
        return `${hex.substr(0, 8)}-${hex.substr(8, 4)}-${hex.substr(12, 4)}-${hex.substr(16, 4)}-${hex.substr(20, 12)}`;
    }

    private readString(length: number): string {
        const bytes = this.readBytes(length);
        return new TextDecoder().decode(bytes);
    }

    private readBytes(length: number): Uint8Array {
        const bytes = this.data.slice(this.offset, this.offset + length);
        this.offset += length;
        return bytes;
    }
}
