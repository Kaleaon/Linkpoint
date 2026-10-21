/**
 * Linden Lab Structured Data (LLSD) Data Model in TypeScript.
 */

export class LLUUID {
    public static readonly nullUuid = new LLUUID('00000000-0000-0000-0000-000000000000');
    public static readonly NULL = LLUUID.nullUuid;
    private readonly value: string;

    constructor(uuidStr?: string) {
        if (!uuidStr) {
            this.value = '00000000-0000-0000-0000-000000000000';
        } else {
            const cleaned = uuidStr.trim().toLowerCase();
            if (!LLUUID.validate(cleaned)) {
                throw new Error(`Invalid UUID format: ${uuidStr}`);
            }
            this.value = cleaned;
        }
    }

    public static validate(str: string): boolean {
        return /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(str);
    }

    public toString(): string {
        return this.value;
    }

    public isNull(): boolean {
        return this.value === '00000000-0000-0000-0000-000000000000';
    }

    public toBytes(): Uint8Array {
        const hex = this.value.replace(/-/g, '');
        const bytes = new Uint8Array(16);
        for (let i = 0; i < 16; i++) {
            bytes[i] = parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    public static fromBytes(bytes: Uint8Array, offset: number = 0): LLUUID {
        if (bytes.length < offset + 16) {
            throw new Error('Buffer too small for UUID');
        }
        let hex = '';
        for (let i = 0; i < 16; i++) {
            const b = bytes[offset + i].toString(16).padStart(2, '0');
            hex += b;
        }
        const formatted = `${hex.substring(0, 8)}-${hex.substring(8, 12)}-${hex.substring(12, 16)}-${hex.substring(16, 20)}-${hex.substring(20, 32)}`;
        return new LLUUID(formatted);
    }
}

export class LLSDDate {
    public static readonly nullDate = new LLSDDate(0);
    public readonly date: Date;

    constructor(dateInput?: Date | string | number) {
        if (dateInput instanceof Date) {
            this.date = dateInput;
        } else if (typeof dateInput === 'number') {
            this.date = new Date(dateInput * 1000);
        } else if (typeof dateInput === 'string') {
            this.date = new Date(dateInput);
        } else {
            this.date = new Date();
        }
    }

    public toISOString(): string {
        return this.date.toISOString();
    }

    public get secondsSinceEpoch(): number {
        return this.date.getTime() / 1000.0;
    }

    public getSecondsSinceEpoch(): number {
        return this.secondsSinceEpoch;
    }
}

export class LLSDURI {
    public readonly uri: string;

    constructor(uri: string) {
        this.uri = uri;
    }

    public toString(): string {
        return this.uri;
    }

    public asString(): string {
        return this.uri;
    }
}

export class LLSDBinary {
    public readonly bytes: Uint8Array;

    constructor(data: Uint8Array | number[]) {
        this.bytes = data instanceof Uint8Array ? data : new Uint8Array(data);
    }

    public toBase64(): string {
        return Buffer.from(this.bytes).toString('base64');
    }

    public static fromBase64(b64: string): LLSDBinary {
        const buf = Buffer.from(b64, 'base64');
        return new LLSDBinary(new Uint8Array(buf.buffer, buf.byteOffset, buf.byteLength));
    }
}

export enum LLSDType {
    UNDEFINED = 'undefined',
    BOOLEAN = 'boolean',
    INTEGER = 'integer',
    REAL = 'real',
    STRING = 'string',
    UUID = 'uuid',
    DATE = 'date',
    URI = 'uri',
    BINARY = 'binary',
    MAP = 'map',
    ARRAY = 'array'
}

export class LLSDException extends Error {
    public readonly cause?: Error;
    constructor(message: string, cause?: Error) {
        super(message);
        this.name = 'LLSDException';
        this.cause = cause;
    }
}

export class LLSDValue {
    public readonly type: LLSDType;
    private readonly raw: any;

    constructor(type: LLSDType = LLSDType.UNDEFINED, rawValue: any = null) {
        this.type = type;
        this.raw = rawValue;
    }

    public static undefined(): LLSDValue {
        return new LLSDValue(LLSDType.UNDEFINED, null);
    }

    public static boolean(val: boolean): LLSDValue {
        return new LLSDValue(LLSDType.BOOLEAN, Boolean(val));
    }

    public static integer(val: number | bigint): LLSDValue {
        return new LLSDValue(LLSDType.INTEGER, typeof val === 'bigint' ? val : Math.floor(val));
    }

    public static real(val: number): LLSDValue {
        return new LLSDValue(LLSDType.REAL, Number(val));
    }

    public static string(val: string): LLSDValue {
        return new LLSDValue(LLSDType.STRING, String(val));
    }

    public static uuid(val: LLUUID | string): LLSDValue {
        const uuidObj = typeof val === 'string' ? new LLUUID(val) : val;
        return new LLSDValue(LLSDType.UUID, uuidObj);
    }

    public static date(val: LLSDDate | Date | string | number): LLSDValue {
        const dateObj = val instanceof LLSDDate ? val : new LLSDDate(val);
        return new LLSDValue(LLSDType.DATE, dateObj);
    }

    public static uri(val: LLSDURI | string): LLSDValue {
        const uriObj = typeof val === 'string' ? new LLSDURI(val) : val;
        return new LLSDValue(LLSDType.URI, uriObj);
    }

    public static binary(val: LLSDBinary | Uint8Array): LLSDValue {
        const binObj = val instanceof LLSDBinary ? val : new LLSDBinary(val);
        return new LLSDValue(LLSDType.BINARY, binObj);
    }

    public static map(entries?: Record<string, LLSDValue> | Map<string, LLSDValue>): LLSDValue {
        const mapObj = new Map<string, LLSDValue>();
        if (entries) {
            if (entries instanceof Map) {
                entries.forEach((v, k) => mapObj.set(k, v));
            } else {
                for (const [k, v] of Object.entries(entries)) {
                    mapObj.set(k, v);
                }
            }
        }
        return new LLSDValue(LLSDType.MAP, mapObj);
    }

    public static array(items?: LLSDValue[]): LLSDValue {
        return new LLSDValue(LLSDType.ARRAY, items ? [...items] : []);
    }

    public isUndefined(): boolean {
        return this.type === LLSDType.UNDEFINED;
    }

    public get isDefined(): boolean {
        return !this.isUndefined();
    }

    public asBoolean(): boolean {
        if (this.type === LLSDType.BOOLEAN) return this.raw as boolean;
        if (this.type === LLSDType.INTEGER) return this.raw !== 0 && this.raw !== 0n;
        if (this.type === LLSDType.REAL) return this.raw !== 0;
        if (this.type === LLSDType.STRING) {
            const lower = (this.raw as string).trim().toLowerCase();
            return lower === 'true' || lower === '1' || lower === 't';
        }
        return false;
    }

    public asInteger(): number {
        if (this.type === LLSDType.INTEGER) return Number(this.raw);
        if (this.type === LLSDType.REAL) return Math.floor(this.raw as number);
        if (this.type === LLSDType.BOOLEAN) return this.raw ? 1 : 0;
        if (this.type === LLSDType.STRING) {
            const parsed = parseInt(this.raw as string, 10);
            return isNaN(parsed) ? 0 : parsed;
        }
        return 0;
    }

    public asBigInt(): bigint {
        if (this.type === LLSDType.INTEGER) return BigInt(this.raw);
        if (this.type === LLSDType.BOOLEAN) return this.raw ? 1n : 0n;
        if (this.type === LLSDType.REAL) return BigInt(Math.trunc(this.raw as number));
        if (this.type === LLSDType.STRING) {
            try { return BigInt(this.raw as string); } catch { return 0n; }
        }
        return 0n;
    }

    public asReal(): number {
        if (this.type === LLSDType.REAL) return this.raw as number;
        if (this.type === LLSDType.INTEGER) return Number(this.raw);
        if (this.type === LLSDType.BOOLEAN) return this.raw ? 1.0 : 0.0;
        if (this.type === LLSDType.STRING) {
            const lower = (this.raw as string).trim().toLowerCase();
            if (lower === 'nan') return NaN;
            if (lower === 'inf' || lower === '+inf') return Infinity;
            if (lower === '-inf') return -Infinity;
            const parsed = parseFloat(this.raw as string);
            return isNaN(parsed) ? 0.0 : parsed;
        }
        return 0.0;
    }

    public asString(): string {
        if (this.type === LLSDType.STRING) return this.raw as string;
        if (this.type === LLSDType.UUID) return (this.raw as LLUUID).toString();
        if (this.type === LLSDType.URI) return (this.raw as LLSDURI).toString();
        if (this.type === LLSDType.DATE) return (this.raw as LLSDDate).toISOString();
        if (this.type === LLSDType.BINARY) return (this.raw as LLSDBinary).toBase64();
        if (this.type === LLSDType.BOOLEAN) return this.raw ? 'true' : 'false';
        if (this.type === LLSDType.INTEGER || this.type === LLSDType.REAL) return String(this.raw);
        if (this.type === LLSDType.UNDEFINED) return '';
        return JSON.stringify(this.raw);
    }

    public asUUID(): LLUUID {
        if (this.type === LLSDType.UUID) return this.raw as LLUUID;
        if (this.type === LLSDType.STRING) return new LLUUID(this.raw as string);
        return LLUUID.nullUuid;
    }

    public asDate(): LLSDDate {
        if (this.type === LLSDType.DATE) return this.raw as LLSDDate;
        if (this.type === LLSDType.STRING || this.type === LLSDType.REAL || this.type === LLSDType.INTEGER) {
            return new LLSDDate(this.raw);
        }
        return LLSDDate.nullDate;
    }

    public asURI(): LLSDURI {
        if (this.type === LLSDType.URI) return this.raw as LLSDURI;
        if (this.type === LLSDType.STRING) return new LLSDURI(this.raw as string);
        return new LLSDURI('');
    }

    public asBinary(): LLSDBinary {
        if (this.type === LLSDType.BINARY) return this.raw as LLSDBinary;
        if (this.type === LLSDType.STRING) return LLSDBinary.fromBase64(this.raw as string);
        return new LLSDBinary(new Uint8Array(0));
    }

    public get(key: string): LLSDValue {
        if (this.type === LLSDType.MAP) {
            const mapObj = this.raw as Map<string, LLSDValue>;
            return mapObj.get(key) || LLSDValue.undefined();
        }
        return LLSDValue.undefined();
    }

    public getAt(index: number): LLSDValue {
        if (this.type === LLSDType.ARRAY) {
            const arr = this.raw as LLSDValue[];
            return (index >= 0 && index < arr.length) ? arr[index] : LLSDValue.undefined();
        }
        return LLSDValue.undefined();
    }

    public set(keyOrIndex: string | number, val: LLSDValue): void {
        if (this.type === LLSDType.MAP && typeof keyOrIndex === 'string') {
            (this.raw as Map<string, LLSDValue>).set(keyOrIndex, val);
        } else if (this.type === LLSDType.ARRAY && typeof keyOrIndex === 'number') {
            const arr = this.raw as LLSDValue[];
            while (arr.length <= keyOrIndex) arr.push(LLSDValue.undefined());
            arr[keyOrIndex] = val;
        }
    }

    public get asMap(): Map<string, LLSDValue> {
        if (this.type === LLSDType.MAP) return this.raw as Map<string, LLSDValue>;
        return new Map();
    }

    public get asArray(): LLSDValue[] {
        if (this.type === LLSDType.ARRAY) return this.raw as LLSDValue[];
        return [];
    }

    public getContent(): any {
        switch (this.type) {
            case LLSDType.UNDEFINED: return null;
            case LLSDType.BOOLEAN: return this.asBoolean();
            case LLSDType.INTEGER: return this.asInteger();
            case LLSDType.REAL: return this.asReal();
            case LLSDType.STRING: return this.asString();
            case LLSDType.UUID: return this.asUUID().toString();
            case LLSDType.DATE: return this.asDate().date;
            case LLSDType.URI: return new URL(this.asURI().toString());
            case LLSDType.BINARY: return this.asBinary().bytes;
            case LLSDType.MAP: {
                const res: Record<string, any> = {};
                this.asMap.forEach((v, k) => {
                    res[k] = v.getContent();
                });
                return res;
            }
            case LLSDType.ARRAY: {
                return this.asArray.map(item => item.getContent());
            }
        }
    }
}

export class LLSD extends LLSDValue {
    constructor(val?: any, type?: LLSDType) {
        if (val === undefined) {
            super(LLSDType.UNDEFINED, null);
        } else if (val instanceof LLSDValue) {
            super(val.type, (val as any).raw);
        } else if (type !== undefined) {
            super(type, val);
        } else if (val === null) {
            super(LLSDType.UNDEFINED, null);
        } else if (typeof val === 'boolean') {
            super(LLSDType.BOOLEAN, val);
        } else if (typeof val === 'bigint') {
            super(LLSDType.INTEGER, val);
        } else if (typeof val === 'number') {
            super(Number.isInteger(val) ? LLSDType.INTEGER : LLSDType.REAL, val);
        } else if (typeof val === 'string') {
            super(LLSDType.STRING, val);
        } else if (val instanceof LLUUID) {
            super(LLSDType.UUID, val);
        } else if (val instanceof LLSDDate) {
            super(LLSDType.DATE, val);
        } else if (val instanceof Date) {
            super(LLSDType.DATE, new LLSDDate(val));
        } else if (val instanceof LLSDURI) {
            super(LLSDType.URI, val);
        } else if (val instanceof Uint8Array) {
            super(LLSDType.BINARY, new LLSDBinary(val));
        } else if (val instanceof Map) {
            super(LLSDType.MAP, val);
        } else if (Array.isArray(val)) {
            super(LLSDType.ARRAY, val);
        } else {
            super(LLSDType.UNDEFINED, null);
        }
    }

    public static emptyMap(): LLSD {
        return new LLSD(new Map<string, LLSDValue>(), LLSDType.MAP);
    }

    public static emptyArray(): LLSD {
        return new LLSD([], LLSDType.ARRAY);
    }
}

export type LLSDMap = { [key: string]: any };
export type LLSDArray = any[];
