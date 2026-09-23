import { parseISO, formatISO } from 'date-fns';
import { v4 as uuidv4, validate as validateUuid } from 'uuid';

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
  NOTATION = 'notation',
  JSON = 'json',
}

/**
 * LLSD XML Parsing
 */
export function parseXML(xml: string): LLSDValue {
  const parser = new DOMParser();
  const doc = parser.parseFromString(xml, 'text/xml');
  const parserError = doc.querySelector('parsererror');
  if (parserError) {
    throw new Error(`XML Parse Error: ${parserError.textContent}`);
  }
  const root = doc.querySelector('llsd');
  if (!root || !root.firstElementChild) return null;
  return parseXMLElement(root.firstElementChild);
}

function parseXMLElement(el: Element): LLSDValue {
  const tag = el.tagName.toLowerCase();
  switch (tag) {
    case 'undef': return null;
    case 'boolean': return el.textContent?.trim() === 'true' || el.textContent?.trim() === '1';
    case 'integer': return parseInt(el.textContent || '0', 10);
    case 'real': return parseFloat(el.textContent || '0');
    case 'uuid': return el.textContent?.trim() || '';
    case 'string': return el.textContent || '';
    case 'date': return parseISO(el.textContent?.trim() || '');
    case 'uri': return el.textContent?.trim() || '';
    case 'binary': {
      const base64 = el.textContent?.trim() || '';
      const binary = atob(base64);
      const bytes = new Uint8Array(binary.length);
      for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
      return bytes;
    }
    case 'map': {
      const map: { [key: string]: LLSDValue } = {};
      let currentKey: string | null = null;
      for (let i = 0; i < el.children.length; i++) {
        const child = el.children[i];
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
      for (let i = 0; i < el.children.length; i++) {
        array.push(parseXMLElement(el.children[i]));
      }
      return array;
    }
    default: return null;
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
    const children = value.map(v => serializeXMLElement(v, indent + 1)).join('\n');
    return `${pad}<array>\n${children}\n${pad}</array>`;
  }
  if (typeof value === 'object') {
    const children = Object.entries(value).map(([k, v]) => {
      return `${pad}  <key>${k}</key>\n${serializeXMLElement(v, indent + 1)}`;
    }).join('\n');
    return `${pad}<map>\n${children}\n${pad}</map>`;
  }
  if (typeof value === 'string') {
    if (validateUuid(value)) return `${pad}<uuid>${value}</uuid>`;
    // Simple URI check
    if (value.startsWith('http://') || value.startsWith('https://')) return `${pad}<uri>${value}</uri>`;
    return `${pad}<string>${value}</string>`;
  }
  return `${pad}<undef />`;
}

/**
 * LLSD Notation Parsing (Simplified)
 */
export function parseNotation(notation: string): LLSDValue {
  // This is a complex parser. For now, we'll use a simplified version.
  // Real LLSD notation uses specific prefixes for types.
  // We'll try to handle the most common ones.

  const trimmed = notation.trim();
  if (trimmed === '!') return null;
  if (trimmed === 'true') return true;
  if (trimmed === 'false') return false;

  // Handle strings
  if (trimmed.startsWith("'") && trimmed.endsWith("'")) return trimmed.slice(1, -1);
  if (trimmed.startsWith('"') && trimmed.endsWith('"')) return trimmed.slice(1, -1);

  // Handle numbers
  if (trimmed.startsWith('i')) return parseInt(trimmed.slice(1), 10);
  if (trimmed.startsWith('r')) return parseFloat(trimmed.slice(1));

  // Handle UUID
  if (trimmed.startsWith('u')) return trimmed.slice(1).replace(/^['"]|['"]$/g, '');

  // Handle Date
  if (trimmed.startsWith('d')) return parseISO(trimmed.slice(1).replace(/^['"]|['"]$/g, ''));

  // Handle URI
  if (trimmed.startsWith('l')) return trimmed.slice(1).replace(/^['"]|['"]$/g, '');

  // Handle Binary
  if (trimmed.startsWith('b')) {
    const base64 = trimmed.slice(1).replace(/^['"]|['"]$/g, '');
    const binary = atob(base64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
    return bytes;
  }

  // Handle Map and Array (this needs a proper tokenizer)
  // For now, let's try a very basic recursive approach or just JSON fallback if it looks like JSON
  try {
    // If it's valid JSON, it's often valid LLSD notation (mostly)
    // But LLSD notation uses single quotes and prefixes.
    // We'll implement a basic tokenizer/parser for Notation.
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

    // Fallback for unquoted strings or numbers without prefix (not strictly LLSD but common)
    const start = this.pos;
    while (this.pos < this.input.length && /[a-zA-Z0-9_\-\.]/.test(this.input[this.pos])) this.pos++;
    const val = this.input.slice(start, this.pos);
    if (!isNaN(Number(val))) return Number(val);
    return val;
  }

  private parseMap(): { [key: string]: LLSDValue } {
    this.pos++; // skip {
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
    this.pos++; // skip [
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
      // Unquoted string
      const start = this.pos;
      while (this.pos < this.input.length && /[a-zA-Z0-9_\-\.]/.test(this.input[this.pos])) this.pos++;
      return this.input.slice(start, this.pos);
    }
    this.pos++;
    let str = '';
    while (this.pos < this.input.length && this.input[this.pos] !== quote) {
      if (this.input[this.pos] === '\\') {
        this.pos++;
        // Handle escapes
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
    return `[ ${value.map(v => serializeNotation(v)).join(', ')} ]`;
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
  return JSON.stringify(value, (key, val) => {
    if (val instanceof Date) return val.toISOString();
    if (val instanceof Uint8Array) return btoa(String.fromCharCode(...val));
    return val;
  }, 2);
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
    // Check if it's valid JSON first
    try {
      JSON.parse(trimmed);
      return LLSDFormat.JSON;
    } catch (e) {
      return LLSDFormat.NOTATION;
    }
  }
  return LLSDFormat.NOTATION;
}
