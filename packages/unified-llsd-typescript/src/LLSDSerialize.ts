/**
 * Multi-format LLSD Serializer and Parser for Notation, Binary, XML, and JSON.
 */

import {
    LLSDValue,
    LLSDType,
    LLUUID,
    LLSDDate,
    LLSDURI,
    LLSDBinary,
    LLSDException
} from './LLSD.js';

export class LLSDSerialize {
    public static readonly BINARY_HEADER = '<?llsd/binary?>';
    public static readonly NOTATION_HEADER = '<?llsd/notation?>';
    public static readonly XML_HEADER = '<?xml';

    public static parse(data: string | Uint8Array): LLSDValue {
        if (data instanceof Uint8Array) {
            const headerStr = new TextDecoder('ascii').decode(data.subarray(0, 15));
            if (headerStr.startsWith(LLSDSerialize.BINARY_HEADER)) {
                return LLSDSerialize.fromBinary(data);
            }
            const strData = new TextDecoder('utf-8').decode(data);
            return LLSDSerialize.parseString(strData);
        } else {
            return LLSDSerialize.parseString(data);
        }
    }

    private static parseString(str: string): LLSDValue {
        const trimmed = str.trim();
        if (trimmed.startsWith(LLSDSerialize.BINARY_HEADER)) {
            const encoder = new TextEncoder();
            return LLSDSerialize.fromBinary(encoder.encode(str));
        }
        if (trimmed.startsWith(LLSDSerialize.XML_HEADER) || trimmed.startsWith('<llsd')) {
            return LLSDSerialize.fromXML(trimmed);
        }
        if (trimmed.startsWith('{') || trimmed.startsWith('[')) {
            try {
                return LLSDSerialize.fromJSON(trimmed);
            } catch {
                return LLSDSerialize.fromNotation(trimmed);
            }
        }
        return LLSDSerialize.fromNotation(trimmed);
    }

    // --- JSON ---
    public static toJSON(value: LLSDValue): string {
        return JSON.stringify(LLSDSerialize.toJSONValue(value), null, 2);
    }

    public static toJSONValue(val: LLSDValue): any {
        switch (val.type) {
            case LLSDType.UNDEFINED: return null;
            case LLSDType.BOOLEAN: return val.asBoolean();
            case LLSDType.INTEGER: return val.asInteger();
            case LLSDType.REAL: return val.asReal();
            case LLSDType.STRING: return val.asString();
            case LLSDType.UUID: return val.asUUID().toString();
            case LLSDType.DATE: return val.asDate().toISOString();
            case LLSDType.URI: return val.asURI().toString();
            case LLSDType.BINARY: return val.asBinary().toBase64();
            case LLSDType.MAP: {
                const res: Record<string, any> = {};
                val.asMap.forEach((v, k) => {
                    res[k] = LLSDSerialize.toJSONValue(v);
                });
                return res;
            }
            case LLSDType.ARRAY: {
                return val.asArray.map(item => LLSDSerialize.toJSONValue(item));
            }
        }
    }

    public static fromJSON(jsonStr: string): LLSDValue {
        const obj = JSON.parse(jsonStr);
        return LLSDSerialize.fromJSObject(obj);
    }

    public static toJSObject(val: LLSDValue): any {
        switch (val.type) {
            case LLSDType.UNDEFINED: return null;
            case LLSDType.BOOLEAN: return val.asBoolean();
            case LLSDType.INTEGER: return val.asInteger();
            case LLSDType.REAL: return val.asReal();
            case LLSDType.STRING: return val.asString();
            case LLSDType.UUID: return val.asUUID().toString();
            case LLSDType.DATE: return val.asDate().date;
            case LLSDType.URI: return val.asURI().toString();
            case LLSDType.BINARY: return val.asBinary().bytes;
            case LLSDType.MAP: {
                const res: Record<string, any> = {};
                val.asMap.forEach((v, k) => {
                    res[k] = LLSDSerialize.toJSObject(v);
                });
                return res;
            }
            case LLSDType.ARRAY: {
                return val.asArray.map(item => LLSDSerialize.toJSObject(item));
            }
        }
    }

    public static fromJSObject(obj: any): LLSDValue {
        if (obj === null || obj === undefined) return LLSDValue.undefined();
        if (typeof obj === 'boolean') return LLSDValue.boolean(obj);
        if (typeof obj === 'number') {
            return Number.isInteger(obj) ? LLSDValue.integer(obj) : LLSDValue.real(obj);
        }
        if (obj instanceof Date) return LLSDValue.date(obj);
        if (obj instanceof Uint8Array) return LLSDValue.binary(obj);
        if (typeof obj === 'string') {
            if (LLUUID.validate(obj)) return LLSDValue.uuid(obj);
            if (obj.startsWith('http://') || obj.startsWith('https://')) return LLSDValue.uri(obj);
            return LLSDValue.string(obj);
        }
        if (Array.isArray(obj)) {
            return LLSDValue.array(obj.map(item => LLSDSerialize.fromJSObject(item)));
        }
        if (typeof obj === 'object') {
            const mapObj: Record<string, LLSDValue> = {};
            for (const [k, v] of Object.entries(obj)) {
                mapObj[k] = LLSDSerialize.fromJSObject(v);
            }
            return LLSDValue.map(mapObj);
        }
        return LLSDValue.undefined();
    }

    // --- NOTATION ---
    public static toNotation(val: LLSDValue): string {
        switch (val.type) {
            case LLSDType.UNDEFINED: return '!';
            case LLSDType.BOOLEAN: return val.asBoolean() ? 'true' : 'false';
            case LLSDType.INTEGER: return `i${val.asBigInt()}`;
            case LLSDType.REAL: return `r${val.asReal()}`;
            case LLSDType.STRING: return `'${val.asString().replace(/'/g, "\\'")}'`;
            case LLSDType.UUID: return `u${val.asUUID().toString()}`;
            case LLSDType.DATE: return `d"${val.asDate().toISOString()}"`;
            case LLSDType.URI: return `l"${val.asURI().toString()}"`;
            case LLSDType.BINARY: return `b64"${val.asBinary().toBase64()}"`;
            case LLSDType.MAP: {
                const parts: string[] = [];
                val.asMap.forEach((v, k) => {
                    parts.push(`'${k}':${LLSDSerialize.toNotation(v)}`);
                });
                return `{${parts.join(',')}}`;
            }
            case LLSDType.ARRAY: {
                const parts = val.asArray.map(v => LLSDSerialize.toNotation(v));
                return `[${parts.join(',')}]`;
            }
        }
    }

    public static fromNotation(notationStr: string): LLSDValue {
        let str = notationStr.trim();
        if (str.startsWith(LLSDSerialize.NOTATION_HEADER)) {
            str = str.substring(LLSDSerialize.NOTATION_HEADER.length).trim();
        }
        if (!str) return LLSDValue.undefined();

        let index = 0;

        function skipWhitespace() {
            while (index < str.length && /\s/.test(str[index])) {
                index++;
            }
        }

        function parseNext(): LLSDValue {
            skipWhitespace();
            if (index >= str.length) return LLSDValue.undefined();

            const ch = str[index];
            if (ch === '!') {
                index++;
                return LLSDValue.undefined();
            }
            if (ch === '1' || ch === 't') {
                if (str.startsWith('true', index)) index += 4;
                else index++;
                return LLSDValue.boolean(true);
            }
            if (ch === '0' || ch === 'f') {
                if (str.startsWith('false', index)) index += 5;
                else index++;
                return LLSDValue.boolean(false);
            }
            if (ch === 'i') {
                index++;
                let numStr = '';
                while (index < str.length && /[-0-9]/.test(str[index])) {
                    numStr += str[index++];
                }
                return LLSDValue.integer(BigInt(numStr || '0'));
            }
            if (ch === 'r') {
                index++;
                let numStr = '';
                while (index < str.length && /[-0-9\.eE]/.test(str[index])) {
                    numStr += str[index++];
                }
                return LLSDValue.real(parseFloat(numStr));
            }
            if (ch === 'u') {
                index++;
                let uuidStr = str.substring(index, index + 36);
                if (uuidStr.startsWith('"') || uuidStr.startsWith("'")) {
                    const quote = uuidStr[0];
                    const end = str.indexOf(quote, index + 1);
                    uuidStr = str.substring(index + 1, end);
                    index = end + 1;
                } else {
                    index += 36;
                }
                return LLSDValue.uuid(uuidStr);
            }
            if (ch === 'd') {
                index++;
                skipWhitespace();
                if (str[index] === '"' || str[index] === "'") {
                    const quote = str[index++];
                    const end = str.indexOf(quote, index);
                    const dateStr = str.substring(index, end);
                    index = end + 1;
                    return LLSDValue.date(dateStr);
                }
            }
            if (ch === 'l') {
                index++;
                skipWhitespace();
                if (str[index] === '"' || str[index] === "'") {
                    const quote = str[index++];
                    const end = str.indexOf(quote, index);
                    const uriStr = str.substring(index, end);
                    index = end + 1;
                    return LLSDValue.uri(uriStr);
                }
            }
            if (ch === 'b') {
                if (str.startsWith('b64', index)) {
                    index += 3;
                    skipWhitespace();
                    if (str[index] === '"' || str[index] === "'") {
                        const quote = str[index++];
                        const end = str.indexOf(quote, index);
                        const b64Str = str.substring(index, end);
                        index = end + 1;
                        return LLSDValue.binary(LLSDBinary.fromBase64(b64Str));
                    }
                }
            }
            if (ch === '"' || ch === "'") {
                const quote = str[index++];
                let valStr = '';
                while (index < str.length && str[index] !== quote) {
                    if (str[index] === '\\' && index + 1 < str.length) {
                        index++;
                    }
                    valStr += str[index++];
                }
                if (index < str.length && str[index] === quote) index++;
                return LLSDValue.string(valStr);
            }
            if (ch === '{') {
                index++; // skip '{'
                const mapObj: Record<string, LLSDValue> = {};
                while (index < str.length) {
                    skipWhitespace();
                    if (str[index] === '}') {
                        index++;
                        break;
                    }
                    const keyVal = parseNext();
                    const key = keyVal.asString();
                    skipWhitespace();
                    if (str[index] === ':') index++; // skip ':'
                    skipWhitespace();
                    const val = parseNext();
                    mapObj[key] = val;
                    skipWhitespace();
                    if (str[index] === ',') index++;
                }
                return LLSDValue.map(mapObj);
            }
            if (ch === '[') {
                index++; // skip '['
                const arr: LLSDValue[] = [];
                while (index < str.length) {
                    skipWhitespace();
                    if (str[index] === ']') {
                        index++;
                        break;
                    }
                    const val = parseNext();
                    arr.push(val);
                    skipWhitespace();
                    if (str[index] === ',') index++;
                }
                return LLSDValue.array(arr);
            }

            index++;
            return LLSDValue.undefined();
        }

        return parseNext();
    }

    // --- BINARY ---
    public static toBinary(val: LLSDValue): Uint8Array {
        const header = new TextEncoder().encode(LLSDSerialize.BINARY_HEADER + '\n');
        const body = LLSDSerialize.encodeBinaryValue(val);
        const result = new Uint8Array(header.length + body.length);
        result.set(header, 0);
        result.set(body, header.length);
        return result;
    }

    private static encodeBinaryValue(val: LLSDValue): Uint8Array {
        switch (val.type) {
            case LLSDType.UNDEFINED: return new Uint8Array([0x21]); // '!'
            case LLSDType.BOOLEAN: return new Uint8Array([val.asBoolean() ? 0x31 : 0x30]); // '1' / '0'
            case LLSDType.INTEGER: {
                const buf = new ArrayBuffer(5);
                const view = new DataView(buf);
                view.setUint8(0, 0x69); // 'i'
                view.setInt32(1, val.asInteger(), false); // Big endian
                return new Uint8Array(buf);
            }
            case LLSDType.REAL: {
                const buf = new ArrayBuffer(9);
                const view = new DataView(buf);
                view.setFloat64(1, val.asReal(), false); // Big endian double
                view.setUint8(0, 0x72); // 'r'
                return new Uint8Array(buf);
            }
            case LLSDType.UUID: {
                const buf = new Uint8Array(17);
                buf[0] = 0x75; // 'u'
                buf.set(val.asUUID().toBytes(), 1);
                return buf;
            }
            case LLSDType.STRING: {
                const strBytes = new TextEncoder().encode(val.asString());
                const buf = new Uint8Array(5 + strBytes.length);
                const view = new DataView(buf.buffer);
                view.setUint8(0, 0x73); // 's'
                view.setUint32(1, strBytes.length, false); // Length big endian
                buf.set(strBytes, 5);
                return buf;
            }
            case LLSDType.URI: {
                const uriBytes = new TextEncoder().encode(val.asURI().toString());
                const buf = new Uint8Array(5 + uriBytes.length);
                const view = new DataView(buf.buffer);
                view.setUint8(0, 0x6C); // 'l'
                view.setUint32(1, uriBytes.length, false); // Length big endian
                buf.set(uriBytes, 5);
                return buf;
            }
            case LLSDType.BINARY: {
                const bin = val.asBinary().bytes;
                const buf = new Uint8Array(5 + bin.length);
                const view = new DataView(buf.buffer);
                view.setUint8(0, 0x62); // 'b'
                view.setUint32(1, bin.length, false);
                buf.set(bin, 5);
                return buf;
            }
            case LLSDType.DATE: {
                const buf = new ArrayBuffer(9);
                const view = new DataView(buf);
                view.setUint8(0, 0x64); // 'd'
                view.setFloat64(1, val.asDate().getSecondsSinceEpoch(), true); // LE double
                return new Uint8Array(buf);
            }
            case LLSDType.MAP: {
                const chunks: Uint8Array[] = [new Uint8Array([0x7B]), new Uint8Array(4)];
                const mapObj = val.asMap;
                let totalLen = 5;
                let count = 0;

                mapObj.forEach((v, k) => {
                    const keyBytes = new TextEncoder().encode(k);
                    const keyBuf = new Uint8Array(5 + keyBytes.length);
                    const keyView = new DataView(keyBuf.buffer);
                    keyView.setUint8(0, 0x6B); // 'k'
                    keyView.setUint32(1, keyBytes.length, false);
                    keyBuf.set(keyBytes, 5);

                    const valBuf = LLSDSerialize.encodeBinaryValue(v);

                    chunks.push(keyBuf);
                    chunks.push(valBuf);
                    totalLen += keyBuf.length + valBuf.length;
                    count++;
                });

                chunks.push(new Uint8Array([0x7D])); // '}'
                totalLen += 1;

                const result = new Uint8Array(totalLen);
                let offset = 0;
                result[offset++] = 0x7B;

                const countView = new DataView(result.buffer, offset, 4);
                countView.setUint32(0, count, false);
                offset += 4;

                for (let i = 2; i < chunks.length; i++) {
                    result.set(chunks[i], offset);
                    offset += chunks[i].length;
                }
                return result;
            }
            case LLSDType.ARRAY: {
                const arr = val.asArray;
                const chunks: Uint8Array[] = [new Uint8Array([0x5B]), new Uint8Array(4)];
                let totalLen = 5;

                arr.forEach(v => {
                    const itemBuf = LLSDSerialize.encodeBinaryValue(v);
                    chunks.push(itemBuf);
                    totalLen += itemBuf.length;
                });

                chunks.push(new Uint8Array([0x5D])); // ']'
                totalLen += 1;

                const result = new Uint8Array(totalLen);
                let offset = 0;
                result[offset++] = 0x5B;

                const countView = new DataView(result.buffer, offset, 4);
                countView.setUint32(0, arr.length, false);
                offset += 4;

                for (let i = 2; i < chunks.length; i++) {
                    result.set(chunks[i], offset);
                    offset += chunks[i].length;
                }
                return result;
            }
            default:
                return new Uint8Array([0x21]);
        }
    }

    public static fromBinary(data: Uint8Array): LLSDValue {
        let offset = 0;
        const headerStr = new TextDecoder('ascii').decode(data.subarray(0, 16));
        if (headerStr.startsWith(LLSDSerialize.BINARY_HEADER)) {
            offset = LLSDSerialize.BINARY_HEADER.length;
            if (data[offset] === 0x0D) offset++; // skip \r
            if (data[offset] === 0x0A) offset++; // skip \n
        }

        const view = new DataView(data.buffer, data.byteOffset, data.byteLength);

        function readValue(): LLSDValue {
            if (offset >= data.length) return LLSDValue.undefined();
            const marker = data[offset++];

            switch (marker) {
                case 0x21: // '!'
                    return LLSDValue.undefined();
                case 0x30: // '0'
                case 0x66: // 'f'
                case 0x46: // 'F'
                    return LLSDValue.boolean(false);
                case 0x31: // '1'
                case 0x74: // 't'
                case 0x54: // 'T'
                    return LLSDValue.boolean(true);
                case 0x69: { // 'i'
                    const val = view.getInt32(offset, false);
                    offset += 4;
                    return LLSDValue.integer(val);
                }
                case 0x72: { // 'r'
                    const val = view.getFloat64(offset, false);
                    offset += 8;
                    return LLSDValue.real(val);
                }
                case 0x75: { // 'u'
                    const uuid = LLUUID.fromBytes(data, offset);
                    offset += 16;
                    return LLSDValue.uuid(uuid);
                }
                case 0x73: { // 's'
                    const len = view.getUint32(offset, false);
                    offset += 4;
                    const str = new TextDecoder('utf-8').decode(data.subarray(offset, offset + len));
                    offset += len;
                    return LLSDValue.string(str);
                }
                case 0x62: { // 'b'
                    const len = view.getUint32(offset, false);
                    offset += 4;
                    const bin = data.slice(offset, offset + len);
                    offset += len;
                    return LLSDValue.binary(bin);
                }
                case 0x64: { // 'd'
                    const sec = view.getFloat64(offset, true); // LE double
                    offset += 8;
                    return LLSDValue.date(sec);
                }
                case 0x6C: { // 'l'
                    const len = view.getUint32(offset, false);
                    offset += 4;
                    const uriStr = new TextDecoder('utf-8').decode(data.subarray(offset, offset + len));
                    offset += len;
                    return LLSDValue.uri(uriStr);
                }
                case 0x7B: { // '{'
                    const count = view.getUint32(offset, false);
                    offset += 4;
                    const mapObj: Record<string, LLSDValue> = {};
                    for (let i = 0; i < count; i++) {
                        if (data[offset] === 0x6B) { // 'k'
                            offset++;
                            const keyLen = view.getUint32(offset, false);
                            offset += 4;
                            const key = new TextDecoder('utf-8').decode(data.subarray(offset, offset + keyLen));
                            offset += keyLen;
                            mapObj[key] = readValue();
                        }
                    }
                    if (data[offset] === 0x7D) offset++; // '}'
                    return LLSDValue.map(mapObj);
                }
                case 0x5B: { // '['
                    const count = view.getUint32(offset, false);
                    offset += 4;
                    const arr: LLSDValue[] = [];
                    for (let i = 0; i < count; i++) {
                        arr.push(readValue());
                    }
                    if (data[offset] === 0x5D) offset++; // ']'
                    return LLSDValue.array(arr);
                }
                default:
                    return LLSDValue.undefined();
            }
        }

        return readValue();
    }

    // --- XML ---
    public static toXML(val: LLSDValue): string {
        return `<?xml version="1.0" encoding="UTF-8"?>\n<llsd>\n${LLSDSerialize.encodeXMLValue(val, 2)}\n</llsd>`;
    }

    private static encodeXMLValue(val: LLSDValue, indentLevel: number): string {
        const ind = ' '.repeat(indentLevel);
        switch (val.type) {
            case LLSDType.UNDEFINED: return `${ind}<undef />`;
            case LLSDType.BOOLEAN: return `${ind}<boolean>${val.asBoolean()}</boolean>`;
            case LLSDType.INTEGER: return `${ind}<integer>${val.asBigInt()}</integer>`;
            case LLSDType.REAL: {
                const r = val.asReal();
                if (isNaN(r)) return `${ind}<real>nan</real>`;
                if (!isFinite(r)) return `${ind}<real>${r > 0 ? 'inf' : '-inf'}</real>`;
                return `${ind}<real>${r}</real>`;
            }
            case LLSDType.STRING: return `${ind}<string>${LLSDSerialize.xmlEscape(val.asString())}</string>`;
            case LLSDType.UUID: return `${ind}<uuid>${val.asUUID().toString()}</uuid>`;
            case LLSDType.DATE: return `${ind}<date>${val.asDate().toISOString()}</date>`;
            case LLSDType.URI: return `${ind}<uri>${LLSDSerialize.xmlEscape(val.asURI().toString())}</uri>`;
            case LLSDType.BINARY: return `${ind}<binary encoding="base64">${val.asBinary().toBase64()}</binary>`;
            case LLSDType.MAP: {
                const lines: string[] = [`${ind}<map>`];
                val.asMap.forEach((v, k) => {
                    lines.push(`${ind}  <key>${LLSDSerialize.xmlEscape(k)}</key>`);
                    lines.push(LLSDSerialize.encodeXMLValue(v, indentLevel + 2));
                });
                lines.push(`${ind}</map>`);
                return lines.join('\n');
            }
            case LLSDType.ARRAY: {
                const lines: string[] = [`${ind}<array>`];
                val.asArray.forEach(v => {
                    lines.push(LLSDSerialize.encodeXMLValue(v, indentLevel + 2));
                });
                lines.push(`${ind}</array>`);
                return lines.join('\n');
            }
        }
    }

    private static xmlEscape(s: string): string {
        return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }

    private static xmlUnescape(s: string): string {
        return s
            .replace(/&quot;/g, '"')
            .replace(/&apos;/g, "'")
            .replace(/&gt;/g, '>')
            .replace(/&lt;/g, '<')
            .replace(/&amp;/g, '&');
    }

    public static fromXML(xmlStr: string): LLSDValue {
        if (!xmlStr || !xmlStr.trim()) {
            throw new LLSDException('Empty XML string');
        }
        const clean = xmlStr.trim();
        if (!clean.includes('<llsd') && !clean.includes('</llsd>')) {
            throw new LLSDException('Invalid LLSD XML: missing <llsd> root element');
        }

        const llsdMatch = /<llsd\b[^>]*>([\s\S]*?)<\/llsd>/i.exec(clean);
        let contentStr = llsdMatch ? llsdMatch[1].trim() : '';

        if (!contentStr) {
            return LLSDValue.undefined();
        }

        return LLSDSerialize.parseXmlNode(contentStr);
    }

    private static findMatchingEndTag(xml: string, tagName: string, startPos: number): number {
        const lowerXml = xml.toLowerCase();
        const tag = tagName.toLowerCase();
        const openPrefix = `<${tag}`;
        const closeTag = `</${tag}>`;

        let depth = 1;
        let pos = startPos;

        while (pos < xml.length) {
            const nextOpen = lowerXml.indexOf(openPrefix, pos);
            const nextClose = lowerXml.indexOf(closeTag, pos);

            if (nextClose === -1) return -1;

            if (nextOpen !== -1 && nextOpen < nextClose) {
                const charAfter = lowerXml[nextOpen + openPrefix.length];
                if (charAfter === '>' || charAfter === '/' || /\s/.test(charAfter)) {
                    const tagHeadEnd = lowerXml.indexOf('>', nextOpen);
                    if (tagHeadEnd !== -1) {
                        if (lowerXml[tagHeadEnd - 1] !== '/') {
                            depth++;
                        }
                        pos = tagHeadEnd + 1;
                        continue;
                    }
                }
                pos = nextOpen + openPrefix.length;
            } else {
                depth--;
                if (depth === 0) {
                    return nextClose + closeTag.length;
                }
                pos = nextClose + closeTag.length;
            }
        }
        return -1;
    }

    private static parseXmlNode(xmlSegment: string): LLSDValue {
        const seg = xmlSegment.trim();
        if (!seg) return LLSDValue.undefined();

        if (!seg.startsWith('<')) {
            throw new LLSDException(`XML Parse Error: Failed to parse LLSD XML element: ${seg}`);
        }

        const tagEnd = seg.indexOf('>');
        if (tagEnd === -1) {
            throw new LLSDException(`XML Parse Error: Failed to parse LLSD XML element: ${seg}`);
        }

        const tagHead = seg.substring(1, tagEnd).trim();
        const tagNameMatch = /^([a-zA-Z0-9_]+)/.exec(tagHead);
        if (!tagNameMatch) {
            throw new LLSDException(`XML Parse Error: Failed to parse LLSD XML element: ${seg}`);
        }

        const tagName = tagNameMatch[1].toLowerCase();
        const isSelfClosing = tagHead.endsWith('/');

        let innerContent = '';
        if (!isSelfClosing) {
            const closeTag = `</${tagName}>`;
            const matchEnd = LLSDSerialize.findMatchingEndTag(seg, tagName, tagEnd + 1);
            if (matchEnd === -1 || !seg.toLowerCase().endsWith(closeTag)) {
                throw new LLSDException(`XML Parse Error: Failed to parse LLSD XML element: ${seg}`);
            }
            innerContent = seg.substring(tagEnd + 1, matchEnd - closeTag.length);
        }

        switch (tagName) {
            case 'undef':
                return LLSDValue.undefined();

            case 'boolean': {
                if (isSelfClosing) return LLSDValue.boolean(false);
                const lower = innerContent.trim().toLowerCase();
                return LLSDValue.boolean(lower === 'true' || lower === '1');
            }

            case 'integer': {
                if (isSelfClosing) return LLSDValue.integer(0);
                const strVal = innerContent.trim();
                if (/[^0-9\-]/.test(strVal)) {
                    throw new LLSDException(`Invalid integer value: ${strVal}`);
                }
                const num = parseInt(strVal, 10);
                if (isNaN(num)) {
                    throw new LLSDException(`Invalid integer value: ${strVal}`);
                }
                return LLSDValue.integer(num);
            }

            case 'real': {
                if (isSelfClosing) return LLSDValue.real(0);
                const lower = innerContent.trim().toLowerCase();
                if (lower === 'nan') return LLSDValue.real(NaN);
                if (lower === 'inf' || lower === '+inf') return LLSDValue.real(Infinity);
                if (lower === '-inf') return LLSDValue.real(-Infinity);
                const num = parseFloat(lower);
                if (isNaN(num)) {
                    throw new LLSDException(`Invalid real value: ${lower}`);
                }
                return LLSDValue.real(num);
            }

            case 'string':
                return LLSDValue.string(LLSDSerialize.xmlUnescape(innerContent));

            case 'uuid': {
                const strVal = innerContent.trim();
                if (isSelfClosing || !strVal) return LLSDValue.uuid(LLUUID.nullUuid);
                if (!LLUUID.validate(strVal)) {
                    throw new LLSDException(`Invalid UUID value: ${strVal}`);
                }
                return LLSDValue.uuid(strVal);
            }

            case 'date': {
                const strVal = innerContent.trim();
                if (isSelfClosing || !strVal) return LLSDValue.date(new Date(0));
                let d = new Date(strVal);
                if (isNaN(d.getTime()) && strVal.endsWith('Z')) {
                    d = new Date(strVal.replace(/Z$/i, ''));
                }
                if (isNaN(d.getTime())) {
                    throw new LLSDException(`Invalid date value: ${strVal}`);
                }
                return LLSDValue.date(d);
            }

            case 'uri': {
                const strVal = innerContent.trim();
                if (isSelfClosing || !strVal) return LLSDValue.uri('');
                const unescapedUri = LLSDSerialize.xmlUnescape(strVal);
                try {
                    new URL(unescapedUri);
                } catch (e: any) {
                    throw new LLSDException(`Invalid URI value: ${unescapedUri}`, e);
                }
                return LLSDValue.uri(unescapedUri);
            }

            case 'binary': {
                if (isSelfClosing || !innerContent) return LLSDValue.binary(new Uint8Array(0));
                const cleanB64 = innerContent.replace(/\s+/g, '');
                if (/[^A-Za-z0-9+/=]/.test(cleanB64)) {
                    throw new LLSDException(`Invalid binary data: ${innerContent}`);
                }
                try {
                    return LLSDValue.binary(LLSDBinary.fromBase64(cleanB64));
                } catch (e: any) {
                    throw new LLSDException(`Invalid binary data: ${innerContent}`, e);
                }
            }

            case 'map': {
                const mapEntries: Record<string, LLSDValue> = {};
                let pos = 0;
                let currentKey: string | null = null;

                while (pos < innerContent.length) {
                    const nextTagStart = innerContent.indexOf('<', pos);
                    if (nextTagStart === -1) break;

                    const tagEnd = innerContent.indexOf('>', nextTagStart);
                    if (tagEnd === -1) break;

                    const tagHead = innerContent.substring(nextTagStart + 1, tagEnd).trim();
                    const tagNameMatch = /^([a-zA-Z0-9_]+)/.exec(tagHead);
                    if (!tagNameMatch) break;

                    const childTagName = tagNameMatch[1].toLowerCase();
                    const isSelfClose = tagHead.endsWith('/');

                    let nodeEnd = tagEnd + 1;
                    if (!isSelfClose) {
                        const matchEnd = LLSDSerialize.findMatchingEndTag(innerContent, childTagName, tagEnd + 1);
                        if (matchEnd !== -1) {
                            nodeEnd = matchEnd;
                        }
                    }

                    const elementXml = innerContent.substring(nextTagStart, nodeEnd);
                    pos = nodeEnd;

                    if (childTagName === 'key') {
                        currentKey = LLSDSerialize.xmlUnescape(elementXml.replace(/^<key[^>]*>/i, '').replace(/<\/key>$/i, '').trim());
                    } else if (currentKey !== null) {
                        mapEntries[currentKey] = LLSDSerialize.parseXmlNode(elementXml);
                        currentKey = null;
                    }
                }

                return LLSDValue.map(mapEntries);
            }

            case 'array': {
                const arrayItems: LLSDValue[] = [];
                let pos = 0;

                while (pos < innerContent.length) {
                    const nextTagStart = innerContent.indexOf('<', pos);
                    if (nextTagStart === -1) break;

                    const tagEnd = innerContent.indexOf('>', nextTagStart);
                    if (tagEnd === -1) break;

                    const tagHead = innerContent.substring(nextTagStart + 1, tagEnd).trim();
                    const tagNameMatch = /^([a-zA-Z0-9_]+)/.exec(tagHead);
                    if (!tagNameMatch) break;

                    const childTagName = tagNameMatch[1].toLowerCase();
                    const isSelfClose = tagHead.endsWith('/');

                    let nodeEnd = tagEnd + 1;
                    if (!isSelfClose) {
                        const matchEnd = LLSDSerialize.findMatchingEndTag(innerContent, childTagName, tagEnd + 1);
                        if (matchEnd !== -1) {
                            nodeEnd = matchEnd;
                        }
                    }

                    const elementXml = innerContent.substring(nextTagStart, nodeEnd);
                    pos = nodeEnd;

                    arrayItems.push(LLSDSerialize.parseXmlNode(elementXml));
                }

                return LLSDValue.array(arrayItems);
            }

            default:
                throw new LLSDException(`Unknown LLSD element type: ${tagName}`);
        }
    }
}
