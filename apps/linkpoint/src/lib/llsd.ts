import { parseISO, formatISO } from 'date-fns';
import { v4 as uuidv4, validate as validateUuid } from 'uuid';
import { DOMParser as XmlDomParser } from '@xmldom/xmldom';

export type LLSDValue =
  | null
  | boolean
  | number
  | string
  | Date
  | Uint8Array
  | { [key: string]: LLSDValue }
  | LLSDValue[];

export enum LLSDFormat {
  XML = 'xml',
  BINARY = 'binary',
  NOTATION = 'notation',
  JSON = 'json',
}

function getDOMParser(): DOMParser {
  if (typeof globalThis.DOMParser !== 'undefined') {
    return new globalThis.DOMParser();
  }
  return new XmlDomParser() as unknown as DOMParser;
}

function getElementChildren(el: Element): Element[] {
  const children: Element[] = [];
  if (el.children && el.children.length > 0) {
    for (let i = 0; i < el.children.length; i++) {
      children.push(el.children[i]);
    }
  } else if (el.childNodes) {
    for (let i = 0; i < el.childNodes.length; i++) {
      const node = el.childNodes[i];
      if (node.nodeType === 1) { // ELEMENT_NODE
        children.push(node as Element);
      }
    }
  }
  return children;
}

/**
 * LLSD XML Parsing
 */
export function parseXML(xml: string): LLSDValue {
  const parser = getDOMParser();
  try {
    const doc = parser.parseFromString(xml, 'text/xml');
    const parserErrors = doc.getElementsByTagName('parsererror');
    if (parserErrors && parserErrors.length > 0) {
      throw new Error(`XML Parse Error: ${parserErrors[0].textContent}`);
    }
    const roots = doc.getElementsByTagName('llsd');
    if (!roots || roots.length === 0) return null;
    const root = roots[0];
    const children = getElementChildren(root);
    if (children.length === 0) return null;
    return parseXMLElement(children[0]);
  } catch (err: any) {
    if (err && typeof err.message === 'string' && err.message.startsWith('XML Parse Error:')) {
      throw err;
    }
    throw new Error(`XML Parse Error: ${err?.message || err}`);
  }
}

function parseXMLElement(el: Element): LLSDValue {
  const tag = el.tagName.toLowerCase();
  switch (tag) {
    case 'undef':
      return null;
    case 'boolean': {
      const text = el.textContent?.trim();
      return text === 'true' || text === '1';
    }
    case 'integer':
      return parseInt(el.textContent?.trim() || '0', 10);
    case 'real':
      return parseFloat(el.textContent?.trim() || '0');
    case 'uuid':
      return el.textContent?.trim() || '00000000-0000-0000-0000-000000000000';
    case 'string':
      return el.textContent || '';
    case 'date': {
      let text = el.textContent?.trim() || '';
      if (text.endsWith('Z') && text.includes('+00:00')) {
        text = text.slice(0, -1);
      }
      return parseISO(text);
    }
    case 'uri':
      return el.textContent?.trim() || '';
    case 'binary': {
      const base64 = (el.textContent || '').replace(/\s+/g, '');
      if (!base64) return new Uint8Array(0);
      const binary = atob(base64);
      const bytes = new Uint8Array(binary.length);
      for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
      return bytes;
    }
    case 'map': {
      const map: { [key: string]: LLSDValue } = {};
      let currentKey: string | null = null;
      const children = getElementChildren(el);
      for (let i = 0; i < children.length; i++) {
        const child = children[i];
        if (child.tagName.toLowerCase() === 'key') {
          currentKey = child.textContent || '';
        } else if (currentKey !== null) {
          map[currentKey] = parseXMLElement(child);
          currentKey = null;
        }
      }
      return map;
    }
    case 'array': {
      const array: LLSDValue[] = [];
      const children = getElementChildren(el);
      for (let i = 0; i < children.length; i++) {
        array.push(parseXMLElement(children[i]));
      }
      return array;
    }
    default:
      return null;
  }
}

/**
 * LLSD XML Serialization
 */
export function serializeXML(value: LLSDValue): string {
  return `<?xml version="1.0" encoding="UTF-8"?>\n<llsd>\n${serializeXMLElement(value, 1)}\n</llsd>`;
}

function serializeXMLElement(value: LLSDValue, indent: number): string {
  const pad = '  '.repeat(indent);
  if (value === null) return `${pad}<undef />`;
  if (typeof value === 'boolean') return `${pad}<boolean>${value}</boolean>`;
  if (typeof value === 'number') {
    if (Number.isInteger(value)) return `${pad}<integer>${value}</integer>`;
    return `${pad}<real>${value}</real>`;
  }
  if (value instanceof Date) return `${pad}<date>${formatISO(value)}</date>`;
  if (value instanceof Uint8Array) {
    let binary = '';
    const len = value.byteLength;
    for (let i = 0; i < len; i++) {
      binary += String.fromCharCode(value[i]);
    }
    const base64 = btoa(binary);
    return `${pad}<binary encoding="base64">${base64}</binary>`;
  }
  if (Array.isArray(value)) {
    const children = value.map((v) => serializeXMLElement(v, indent + 1)).join('\n');
    return `${pad}<array>\n${children}\n${pad}</array>`;
  }
  if (typeof value === 'object') {
    const children = Object.entries(value)
      .map(([k, v]) => {
        return `${pad}  <key>${k}</key>\n${serializeXMLElement(v, indent + 1)}`;
      })
      .join('\n');
    return `${pad}<map>\n${children}\n${pad}</map>`;
  }
  if (typeof value === 'string') {
    if (validateUuid(value)) return `${pad}<uuid>${value}</uuid>`;
    if (value.startsWith('http://') || value.startsWith('https://')) return `${pad}<uri>${value}</uri>`;
    return `${pad}<string>${value}</string>`;
  }
  return `${pad}<undef />`;
}

/**
 * LLSD Binary Parsing
 */
export function parseBinary(data: Uint8Array): LLSDValue {
  let pos = 0;
  const magic = '<?llsd/binary?>';
  const textDecoder = new TextDecoder('utf-8');

  if (data.length >= magic.length) {
    const header = String.fromCharCode(...data.subarray(0, magic.length));
    if (header === magic) {
      pos = magic.length;
      if (pos < data.length && data[pos] === 13) pos++; // \r
      if (pos < data.length && data[pos] === 10) pos++; // \n
    }
  }

  const view = new DataView(data.buffer, data.byteOffset, data.byteLength);

  function readValue(): LLSDValue {
    if (pos >= data.length) return null;
    const marker = String.fromCharCode(data[pos++]);
    switch (marker) {
      case '!':
        return null;
      case '1':
      case 't':
      case 'T':
        return true;
      case '0':
      case 'f':
      case 'F':
        return false;
      case 'i': {
        const val = view.getInt32(pos, false);
        pos += 4;
        return val;
      }
      case 'r': {
        const val = view.getFloat64(pos, false);
        pos += 8;
        return val;
      }
      case 'u': {
        const uuidBytes = data.subarray(pos, pos + 16);
        pos += 16;
        let hex = '';
        for (let i = 0; i < 16; i++) {
          hex += uuidBytes[i].toString(16).padStart(2, '0');
        }
        return `${hex.slice(0, 8)}-${hex.slice(8, 12)}-${hex.slice(12, 16)}-${hex.slice(16, 20)}-${hex.slice(20, 32)}`;
      }
      case 's': {
        const len = view.getUint32(pos, false);
        pos += 4;
        const strBytes = data.subarray(pos, pos + len);
        pos += len;
        return textDecoder.decode(strBytes);
      }
      case 'b': {
        const len = view.getUint32(pos, false);
        pos += 4;
        const binBytes = data.slice(pos, pos + len);
        pos += len;
        return binBytes;
      }
      case 'd': {
        const seconds = view.getFloat64(pos, true);
        pos += 8;
        return new Date(Math.round(seconds * 1000));
      }
      case 'l': {
        const len = view.getUint32(pos, false);
        pos += 4;
        const uriBytes = data.subarray(pos, pos + len);
        pos += len;
        return textDecoder.decode(uriBytes);
      }
      case '{': {
        const count = view.getUint32(pos, false);
        pos += 4;
        const map: { [key: string]: LLSDValue } = {};
        while (pos < data.length) {
          if (data[pos] === 125) { // '}'
            pos++;
            break;
          }
          if (data[pos] === 107) { // 'k'
            pos++;
            const keyLen = view.getUint32(pos, false);
            pos += 4;
            const keyBytes = data.subarray(pos, pos + keyLen);
            pos += keyLen;
            const key = textDecoder.decode(keyBytes);
            map[key] = readValue();
          } else {
            break;
          }
        }
        return map;
      }
      case '[': {
        const count = view.getUint32(pos, false);
        pos += 4;
        const array: LLSDValue[] = [];
        while (pos < data.length) {
          if (data[pos] === 93) { // ']'
            pos++;
            break;
          }
          array.push(readValue());
        }
        return array;
      }
      default:
        return null;
    }
  }

  return readValue();
}

/**
 * LLSD Binary Serialization
 */
export function serializeBinary(value: LLSDValue): Uint8Array {
  const chunks: Uint8Array[] = [];
  const textEncoder = new TextEncoder();
  const headerBytes = textEncoder.encode('<?llsd/binary?>\n');
  chunks.push(headerBytes);

  function writeValue(val: LLSDValue) {
    if (val === null) {
      chunks.push(new Uint8Array([33])); // '!'
    } else if (typeof val === 'boolean') {
      chunks.push(new Uint8Array([val ? 49 : 48])); // '1' or '0'
    } else if (typeof val === 'number') {
      if (Number.isInteger(val)) {
        const buf = new Uint8Array(5);
        buf[0] = 105; // 'i'
        new DataView(buf.buffer).setInt32(1, val, false);
        chunks.push(buf);
      } else {
        const buf = new Uint8Array(9);
        buf[0] = 114; // 'r'
        new DataView(buf.buffer).setFloat64(1, val, false);
        chunks.push(buf);
      }
    } else if (val instanceof Date) {
      const buf = new Uint8Array(9);
      buf[0] = 100; // 'd'
      const seconds = val.getTime() / 1000;
      new DataView(buf.buffer).setFloat64(1, seconds, true);
      chunks.push(buf);
    } else if (val instanceof Uint8Array) {
      const buf = new Uint8Array(5);
      buf[0] = 98; // 'b'
      new DataView(buf.buffer).setUint32(1, val.length, false);
      chunks.push(buf);
      chunks.push(val);
    } else if (Array.isArray(val)) {
      const buf = new Uint8Array(5);
      buf[0] = 91; // '['
      new DataView(buf.buffer).setUint32(1, val.length, false);
      chunks.push(buf);
      for (const item of val) {
        writeValue(item);
      }
      chunks.push(new Uint8Array([93])); // ']'
    } else if (typeof val === 'object') {
      const entries = Object.entries(val);
      const buf = new Uint8Array(5);
      buf[0] = 123; // '{'
      new DataView(buf.buffer).setUint32(1, entries.length, false);
      chunks.push(buf);
      for (const [key, item] of entries) {
        const keyBytes = textEncoder.encode(key);
        const kBuf = new Uint8Array(5);
        kBuf[0] = 107; // 'k'
        new DataView(kBuf.buffer).setUint32(1, keyBytes.length, false);
        chunks.push(kBuf);
        chunks.push(keyBytes);
        writeValue(item);
      }
      chunks.push(new Uint8Array([125])); // '}'
    } else if (typeof val === 'string') {
      if (validateUuid(val)) {
        const buf = new Uint8Array(17);
        buf[0] = 117; // 'u'
        const hex = val.replace(/-/g, '');
        for (let i = 0; i < 16; i++) {
          buf[1 + i] = parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        chunks.push(buf);
      } else if (val.startsWith('http://') || val.startsWith('https://')) {
        const uriBytes = textEncoder.encode(val);
        const buf = new Uint8Array(5);
        buf[0] = 108; // 'l'
        new DataView(buf.buffer).setUint32(1, uriBytes.length, false);
        chunks.push(buf);
        chunks.push(uriBytes);
      } else {
        const strBytes = textEncoder.encode(val);
        const buf = new Uint8Array(5);
        buf[0] = 115; // 's'
        new DataView(buf.buffer).setUint32(1, strBytes.length, false);
        chunks.push(buf);
        chunks.push(strBytes);
      }
    } else {
      chunks.push(new Uint8Array([33])); // '!'
    }
  }

  writeValue(value);

  const totalLen = chunks.reduce((acc, c) => acc + c.length, 0);
  const result = new Uint8Array(totalLen);
  let offset = 0;
  for (const chunk of chunks) {
    result.set(chunk, offset);
    offset += chunk.length;
  }
  return result;
}

/**
 * Universal LLSD Parser (Auto-Detect)
 */
export function parse(data: Uint8Array | string): LLSDValue {
  if (typeof data === 'string') {
    const trimmed = data.trim();
    if (trimmed.startsWith('<?xml') || trimmed.startsWith('<llsd')) {
      return parseXML(data);
    }
    return parseNotation(data);
  } else {
    const magic = '<?llsd/binary?>';
    if (data.length >= magic.length) {
      const header = String.fromCharCode(...data.subarray(0, magic.length));
      if (header === magic) {
        return parseBinary(data);
      }
    }
    const str = new TextDecoder('utf-8').decode(data);
    if (str.trim().startsWith('<?xml') || str.trim().startsWith('<llsd')) {
      return parseXML(str);
    }
    return parseBinary(data);
  }
}

/**
 * LLSD Notation Parsing (Simplified)
 */
export function parseNotation(notation: string): LLSDValue {
  const trimmed = notation.trim();
  if (trimmed === '!') return null;
  if (trimmed === 'true') return true;
  if (trimmed === 'false') return false;

  if (trimmed.startsWith("'") && trimmed.endsWith("'")) return trimmed.slice(1, -1);
  if (trimmed.startsWith('"') && trimmed.endsWith('"')) return trimmed.slice(1, -1);

  if (trimmed.startsWith('i')) return parseInt(trimmed.slice(1), 10);
  if (trimmed.startsWith('r')) return parseFloat(trimmed.slice(1));
  if (trimmed.startsWith('u')) return trimmed.slice(1).replace(/^['"]|['"]$/g, '');
  if (trimmed.startsWith('d')) return parseISO(trimmed.slice(1).replace(/^['"]|['"]$/g, ''));
  if (trimmed.startsWith('l')) return trimmed.slice(1).replace(/^['"]|['"]$/g, '');

  if (trimmed.startsWith('b')) {
    const base64 = trimmed.slice(1).replace(/^['"]|['"]$/g, '');
    const binary = atob(base64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
    return bytes;
  }

  try {
    return new NotationParser(trimmed).parse();
  } catch (e) {
    console.error('Notation parse error:', e);
    return null;
  }
}

class NotationParser {
  private pos = 0;
  constructor(private input: string) {}

  parse(): LLSDValue {
    this.skipWhitespace();
    const char = this.input[this.pos];
    if (char === '{') return this.parseMap();
    if (char === '[') return this.parseArray();
    if (char === '!') { this.pos++; return null; }
    if (this.input.startsWith('true', this.pos)) { this.pos += 4; return true; }
    if (this.input.startsWith('false', this.pos)) { this.pos += 5; return false; }

    if (char === 'i') {
      this.pos++;
      const start = this.pos;
      while (this.pos < this.input.length && /[0-9\-]/.test(this.input[this.pos])) this.pos++;
      return parseInt(this.input.slice(start, this.pos), 10);
    }
    if (char === 'r') {
      this.pos++;
      const start = this.pos;
      while (this.pos < this.input.length && /[0-9\.\-eE]/.test(this.input[this.pos])) this.pos++;
      return parseFloat(this.input.slice(start, this.pos));
    }
    if (char === 'u') {
      this.pos++;
      return this.parseString();
    }
    if (char === 'd') {
      this.pos++;
      return parseISO(this.parseString());
    }
    if (char === 'l') {
      this.pos++;
      return this.parseString();
    }
    if (char === 'b') {
      this.pos++;
      const base64 = this.parseString();
      const binary = atob(base64);
      const bytes = new Uint8Array(binary.length);
      for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
      return bytes;
    }
    if (char === "'" || char === '"') return this.parseString();

    const start = this.pos;
    while (this.pos < this.input.length && /[a-zA-Z0-9_\-\.]/.test(this.input[this.pos])) this.pos++;
    const val = this.input.slice(start, this.pos);
    if (!isNaN(Number(val))) return Number(val);
    return val;
  }

  private parseMap(): { [key: string]: LLSDValue } {
    this.pos++;
    const map: { [key: string]: LLSDValue } = {};
    while (this.pos < this.input.length) {
      this.skipWhitespace();
      if (this.input[this.pos] === '}') { this.pos++; break; }
      const key = this.parseString();
      this.skipWhitespace();
      if (this.input[this.pos] === ':') this.pos++;
      this.skipWhitespace();
      map[key] = this.parse();
      this.skipWhitespace();
      if (this.input[this.pos] === ',') this.pos++;
    }
    return map;
  }

  private parseArray(): LLSDValue[] {
    this.pos++;
    const array: LLSDValue[] = [];
    while (this.pos < this.input.length) {
      this.skipWhitespace();
      if (this.input[this.pos] === ']') { this.pos++; break; }
      array.push(this.parse());
      this.skipWhitespace();
      if (this.input[this.pos] === ',') this.pos++;
    }
    return array;
  }

  private parseString(): string {
    this.skipWhitespace();
    const quote = this.input[this.pos];
    if (quote !== "'" && quote !== '"') {
      const start = this.pos;
      while (this.pos < this.input.length && /[a-zA-Z0-9_\-\.]/.test(this.input[this.pos])) this.pos++;
      return this.input.slice(start, this.pos);
    }
    this.pos++;
    let str = '';
    while (this.pos < this.input.length && this.input[this.pos] !== quote) {
      if (this.input[this.pos] === '\\') {
        this.pos++;
      }
      str += this.input[this.pos];
      this.pos++;
    }
    this.pos++;
    return str;
  }

  private skipWhitespace() {
    while (this.pos < this.input.length && /\s/.test(this.input[this.pos])) this.pos++;
  }
}

/**
 * LLSD Notation Serialization
 */
export function serializeNotation(value: LLSDValue): string {
  if (value === null) return '!';
  if (typeof value === 'boolean') return value ? 'true' : 'false';
  if (typeof value === 'number') {
    if (Number.isInteger(value)) return `i${value}`;
    return `r${value}`;
  }
  if (value instanceof Date) return `d'${formatISO(value)}'`;
  if (value instanceof Uint8Array) {
    const base64 = btoa(String.fromCharCode(...value));
    return `b'${base64}'`;
  }
  if (Array.isArray(value)) {
    return `[ ${value.map((v) => serializeNotation(v)).join(', ')} ]`;
  }
  if (typeof value === 'object') {
    return `{ ${Object.entries(value).map(([k, v]) => `'${k}': ${serializeNotation(v)}`).join(', ')} }`;
  }
  if (typeof value === 'string') {
    if (validateUuid(value)) return `u'${value}'`;
    if (value.startsWith('http://') || value.startsWith('https://')) return `l'${value}'`;
    return `'${value}'`;
  }
  return '!';
}

/**
 * JSON Conversion
 */
export function toJSON(value: LLSDValue): string {
  return JSON.stringify(
    value,
    (key, val) => {
      if (val instanceof Date) return val.toISOString();
      if (val instanceof Uint8Array) return btoa(String.fromCharCode(...val));
      return val;
    },
    2
  );
}

export function fromJSON(json: string): LLSDValue {
  return JSON.parse(json);
}

/**
 * Format Detection
 */
export function detectFormat(input: string): LLSDFormat {
  const trimmed = input.trim();
  if (trimmed.startsWith('<?xml') || trimmed.startsWith('<llsd')) return LLSDFormat.XML;
  if (trimmed.startsWith('{') || trimmed.startsWith('[') || trimmed.startsWith('!') || /^[irudlb]['"]/.test(trimmed)) {
    try {
      JSON.parse(trimmed);
      return LLSDFormat.JSON;
    } catch (e) {
      return LLSDFormat.NOTATION;
    }
  }
  return LLSDFormat.NOTATION;
}
