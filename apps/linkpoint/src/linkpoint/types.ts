/**
 * LLSD Types - TypeScript Implementation
 *
 * Based on Second Life viewer XML serialization
 * Copyright (C) 2024 Linden Lab
 */

export enum LLSDType {
    UNKNOWN = 'unknown',
    BOOLEAN = 'boolean',
    INTEGER = 'integer',
    REAL = 'real',
    STRING = 'string',
    UUID = 'uuid',
    DATE = 'date',
    URI = 'uri',
    BINARY = 'binary',
    ARRAY = 'array',
    MAP = 'map'
}

export enum LLSDFormat {
    XML = 'xml',
    BINARY = 'binary',
    JSON = 'json'
}

export type LLSDMap = { [key: string]: LLSDValue };
export type LLSDArray = LLSDValue[];
export type LLSDValue =
    | null
    | boolean
    | number
    | string
    | Date
    | URL
    | Uint8Array
    | LLSDArray
    | LLSDMap;

export class LLSDException extends Error {
    public cause?: Error;

    constructor(message: string, cause?: Error) {
        super(message);
        this.name = 'LLSDException';
        this.cause = cause;
    }
}

export class LLSD {
    private content: LLSDValue;

    constructor(content: LLSDValue = null) {
        this.content = content;
    }

    getContent(): LLSDValue {
        return this.content;
    }

    setContent(content: LLSDValue): void {
        this.content = content;
    }

    /**
     * Create LLSD from JSON string
     */
    static fromJSON(jsonString: string): LLSD {
        try {
            const data = JSON.parse(jsonString, (key, value) => {
                // Basic heuristic for dates in JSON
                if (typeof value === 'string' && /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/.test(value)) {
                    const date = new Date(value);
                    if (!isNaN(date.getTime())) return date;
                }
                return value;
            });
            return new LLSD(data);
        } catch (e) {
            throw new LLSDException(`Failed to parse JSON: ${e instanceof Error ? e.message : String(e)}`);
        }
    }

    /**
     * Serialize to JSON string
     */
    toJSON(): string {
        return JSON.stringify(this.content, (key, value) => {
            if (value instanceof Uint8Array) {
                // Convert binary to base64 for JSON
                let binary = '';
                for (let i = 0; i < value.byteLength; i++) {
                    binary += String.fromCharCode(value[i]);
                }
                return btoa(binary);
            }
            return value;
        });
    }
}

export class LLSDUtils {
    /**
     * Determine the LLSD type of a value
     */
    static getType(value: LLSDValue): LLSDType {
        if (value === null || value === undefined) {
            return LLSDType.UNKNOWN;
        }

        if (typeof value === 'boolean') {
            return LLSDType.BOOLEAN;
        }

        if (typeof value === 'number') {
            if (Number.isInteger(value)) {
                return LLSDType.INTEGER;
            }
            return LLSDType.REAL;
        }

        if (typeof value === 'string') {
            // Check if it's a UUID (very basic check)
            if (this.isUUIDString(value)) {
                return LLSDType.UUID;
            }
            return LLSDType.STRING;
        }

        if (value instanceof Date) {
            return LLSDType.DATE;
        }

        if (value instanceof URL) {
            return LLSDType.URI;
        }

        if (value instanceof Uint8Array) {
            return LLSDType.BINARY;
        }

        if (Array.isArray(value)) {
            return LLSDType.ARRAY;
        }

        if (typeof value === 'object') {
            return LLSDType.MAP;
        }

        return LLSDType.UNKNOWN;
    }

    /**
     * Check if a string is a valid UUID
     */
    static isUUIDString(value: string): boolean {
        const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
        return uuidRegex.test(value);
    }
}
