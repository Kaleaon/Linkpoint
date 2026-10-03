/**
 * LLSD Binary Serializer - TypeScript Implementation
 * 
 * Based on Java implementation and Second Life viewer binary serialization
 * Copyright (C) 2024 Linden Lab
 */

import { LLSD, LLSDValue, LLSDMap, LLSDArray, LLSDException, LLSDType, LLSDUtils } from './types';

export class LLSDBinarySerializer {
    private static readonly LLSD_BINARY_MAGIC = 0x6c6c7364; // 'llsd'
    private buffer: number[];

    constructor() {
        this.buffer = [];
    }

    /**
     * Serialize LLSD to binary data
     */
    serialize(llsd: LLSD): Uint8Array {
        this.buffer = [];

        try {
            // Write magic number
            this.writeUInt32(LLSDBinarySerializer.LLSD_BINARY_MAGIC);
            
            // Write content
            this.writeValue(llsd.getContent());
            
            return new Uint8Array(this.buffer);
        } catch (error) {
            if (error instanceof LLSDException) {
                throw error;
            }
            throw new LLSDException(`Failed to serialize LLSD to binary: ${error}`, error as Error);
        }
    }

    /**
     * Write a single value to binary data
     */
    private writeValue(value: LLSDValue): void {
        const type = LLSDUtils.getType(value);

        switch (type) {
            case LLSDType.UNKNOWN:
                this.writeUInt8(0); // Undefined
                break;

            case LLSDType.BOOLEAN:
                this.writeUInt8(1); // Boolean
                this.writeUInt8((value as boolean) ? 1 : 0);
                break;

            case LLSDType.INTEGER:
                this.writeUInt8(2); // Integer
                this.writeInt32(value as number);
                break;

            case LLSDType.REAL:
                this.writeUInt8(3); // Real
                this.writeDouble(value as number);
                break;

            case LLSDType.STRING:
                this.writeUInt8(4); // String
                this.writeString(value as string);
                break;

            case LLSDType.UUID:
                this.writeUInt8(5); // UUID
                this.writeUUID(value as string);
                break;

            case LLSDType.DATE:
                this.writeUInt8(6); // Date
                const timestamp = (value as Date).getTime() / 1000;
                this.writeDouble(timestamp);
                break;

            case LLSDType.URI:
                this.writeUInt8(7); // URI
                this.writeString((value as URL).href);
                break;

            case LLSDType.BINARY:
                this.writeUInt8(8); // Binary
                const binaryData = value as Uint8Array;
                this.writeUInt32(binaryData.length);
                this.writeBytes(binaryData);
                break;

            case LLSDType.ARRAY:
                this.writeUInt8(9); // Array
                const array = value as LLSDArray;
                this.writeUInt32(array.length);
                for (const item of array) {
                    this.writeValue(item);
                }
                break;

            case LLSDType.MAP:
                this.writeUInt8(10); // Map
                const map = value as LLSDMap;
                const entries = Object.entries(map);
                this.writeUInt32(entries.length);
                for (const [key, val] of entries) {
                    this.writeString(key);
                    this.writeValue(val);
                }
                break;

            default:
                throw new LLSDException(`Cannot serialize unknown type: ${type}`);
        }
    }

    /**
     * Write a single byte
     */
    private writeUInt8(value: number): void {
        this.buffer.push(value & 0xFF);
    }

    /**
     * Write a 32-bit signed integer
     */
    private writeInt32(value: number): void {
        const bytes = new ArrayBuffer(4);
        const view = new DataView(bytes);
        view.setInt32(0, value, false); // Big endian
        const array = new Uint8Array(bytes);
        this.buffer.push(...array);
    }

    /**
     * Write a 32-bit unsigned integer
     */
    private writeUInt32(value: number): void {
        const bytes = new ArrayBuffer(4);
        const view = new DataView(bytes);
        view.setUint32(0, value, false); // Big endian
        const array = new Uint8Array(bytes);
        this.buffer.push(...array);
    }

    /**
     * Write a 64-bit double
     */
    private writeDouble(value: number): void {
        const bytes = new ArrayBuffer(8);
        const view = new DataView(bytes);
        view.setFloat64(0, value, false); // Big endian
        const array = new Uint8Array(bytes);
        this.buffer.push(...array);
    }

    /**
     * Write a string
     */
    private writeString(value: string): void {
        const encoded = new TextEncoder().encode(value);
        this.writeUInt32(encoded.length);
        this.writeBytes(encoded);
    }

    /**
     * Write a UUID
     */
    private writeUUID(value: string): void {
        const hex = value.replace(/-/g, '');
        if (hex.length !== 32) {
            throw new LLSDException(`Invalid UUID format: ${value}`);
        }

        const bytes = new Uint8Array(16);
        for (let i = 0; i < 16; i++) {
            bytes[i] = parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        this.writeBytes(bytes);
    }

    /**
     * Write raw bytes
     */
    private writeBytes(bytes: Uint8Array): void {
        this.buffer.push(...bytes);
    }
}