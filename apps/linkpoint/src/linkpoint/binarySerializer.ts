/**
 * LLSD Binary Serializer - TypeScript Implementation
 *
 * Based on Java implementation and Second Life viewer binary serialization
 * Copyright (C) 2024 Linden Lab
 */

import { LLSD, LLSDValue, LLSDMap, LLSDArray, LLSDException, LLSDType, LLSDUtils } from './types';

export class LLSDBinarySerializer {
    /**
     * Serialize LLSD to binary data
     */
    serialize(llsd: LLSD): Uint8Array {
        const content = llsd.getContent();
        const buffer = this.serializeValue(content);
        return buffer;
    }

    private serializeValue(value: LLSDValue): Uint8Array {
        const type = LLSDUtils.getType(value);

        switch (type) {
            case LLSDType.UNKNOWN:
                return new Uint8Array([33]); // '!'

            case LLSDType.BOOLEAN:
                return new Uint8Array([value ? 49 : 48]); // '1' or '0'

            case LLSDType.INTEGER:
                const intVal = value as number;
                const intBuf = new Uint8Array(5);
                intBuf[0] = 105; // 'i'
                this.writeInt32(intBuf, 1, intVal);
                return intBuf;

            case LLSDType.REAL:
                const realVal = value as number;
                const realBuf = new Uint8Array(9);
                realBuf[0] = 114; // 'r'
                this.writeFloat64(realBuf, 1, realVal);
                return realBuf;

            case LLSDType.STRING:
                const strVal = value as string;
                const strBytes = new TextEncoder().encode(strVal);
                const strBuf = new Uint8Array(5 + strBytes.length);
                strBuf[0] = 115; // 's'
                this.writeInt32(strBuf, 1, strBytes.length);
                strBuf.set(strBytes, 5);
                return strBuf;

            case LLSDType.UUID:
                const uuidVal = value as string;
                const uuidBytes = this.uuidToBytes(uuidVal);
                const uuidBuf = new Uint8Array(1 + uuidBytes.length);
                uuidBuf[0] = 117; // 'u'
                uuidBuf.set(uuidBytes, 1);
                return uuidBuf;

            case LLSDType.DATE:
                const dateVal = value as Date;
                const dateBuf = new Uint8Array(9);
                dateBuf[0] = 100; // 'd'
                this.writeFloat64(dateBuf, 1, dateVal.getTime() / 1000);
                return dateBuf;

            case LLSDType.URI:
                const uriVal = value as URL;
                const uriBytes = new TextEncoder().encode(uriVal.href);
                const uriBuf = new Uint8Array(5 + uriBytes.length);
                uriBuf[0] = 108; // 'l'
                this.writeInt32(uriBuf, 1, uriBytes.length);
                uriBuf.set(uriBytes, 5);
                return uriBuf;

            case LLSDType.BINARY:
                const binVal = value as Uint8Array;
                const binBuf = new Uint8Array(5 + binVal.length);
                binBuf[0] = 98; // 'b'
                this.writeInt32(binBuf, 1, binVal.length);
                binBuf.set(binVal, 5);
                return binBuf;

            case LLSDType.ARRAY:
                const arrayVal = value as LLSDArray;
                const arrayHeader = new Uint8Array(5);
                arrayHeader[0] = 91; // '['
                this.writeInt32(arrayHeader, 1, arrayVal.length);
                const arrayParts: Uint8Array[] = [arrayHeader];
                for (const item of arrayVal) {
                    arrayParts.push(this.serializeValue(item));
                }
                arrayParts.push(new Uint8Array([93])); // ']'
                return this.concatBuffers(arrayParts);

            case LLSDType.MAP:
                const mapVal = value as LLSDMap;
                const keys = Object.keys(mapVal);
                const mapHeader = new Uint8Array(5);
                mapHeader[0] = 123; // '{'
                this.writeInt32(mapHeader, 1, keys.length);
                const mapParts: Uint8Array[] = [mapHeader];
                for (const key of keys) {
                    const keyBytes = new TextEncoder().encode(key);
                    const keyBuf = new Uint8Array(5 + keyBytes.length);
                    keyBuf[0] = 107; // 'k'
                    this.writeInt32(keyBuf, 1, keyBytes.length);
                    keyBuf.set(keyBytes, 5);
                    mapParts.push(keyBuf);
                    mapParts.push(this.serializeValue(mapVal[key]));
                }
                mapParts.push(new Uint8Array([125])); // '}'
                return this.concatBuffers(mapParts);

            default:
                throw new LLSDException(`Cannot serialize unknown type: ${type}`);
        }
    }

    private writeInt32(buffer: Uint8Array, offset: number, value: number): void {
        buffer[offset] = (value >> 24) & 0xFF;
        buffer[offset + 1] = (value >> 16) & 0xFF;
        buffer[offset + 2] = (value >> 8) & 0xFF;
        buffer[offset + 3] = value & 0xFF;
    }

    private writeFloat64(buffer: Uint8Array, offset: number, value: number): void {
        const tempBuf = new ArrayBuffer(8);
        const view = new DataView(tempBuf);
        view.setFloat64(0, value, false); // Big-endian
        for (let i = 0; i < 8; i++) {
            buffer[offset + i] = view.getUint8(i);
        }
    }

    private uuidToBytes(uuid: string): Uint8Array {
        const hex = uuid.replace(/-/g, '');
        const bytes = new Uint8Array(16);
        for (let i = 0; i < 16; i++) {
            bytes[i] = parseInt(hex.substr(i * 2, 2), 16);
        }
        return bytes;
    }

    private concatBuffers(buffers: Uint8Array[]): Uint8Array {
        let totalLength = 0;
        for (const buf of buffers) {
            totalLength += buf.length;
        }
        const result = new Uint8Array(totalLength);
        let offset = 0;
        for (const buf of buffers) {
            result.set(buf, offset);
            offset += buf.length;
        }
        return result;
    }
}
