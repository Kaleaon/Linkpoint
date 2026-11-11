import { LLSDSerialize, LLSDValue as UnifiedLLSDValue } from '@unified-llsd/typescript';

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

/**
 * LLSD XML Parsing
 */
export function parseXML(xml: string): LLSDValue {
  try {
    const sd = LLSDSerialize.fromXML(xml);
    return LLSDSerialize.toJSObject(sd);
  } catch (err: any) {
    if (err?.message?.includes('missing <llsd> root element') || err?.message?.includes('Unknown LLSD element type')) {
      return null;
    }
    throw err;
  }
}

/**
 * LLSD XML Serialization
 */
export function serializeXML(value: LLSDValue): string {
  const sd = LLSDSerialize.fromJSObject(value);
  return LLSDSerialize.toXML(sd);
}

/**
 * LLSD Binary Parsing
 */
export function parseBinary(data: Uint8Array): LLSDValue {
  const sd = LLSDSerialize.fromBinary(data);
  return LLSDSerialize.toJSObject(sd);
}

/**
 * LLSD Binary Serialization
 */
export function serializeBinary(value: LLSDValue): Uint8Array {
  const sd = LLSDSerialize.fromJSObject(value);
  return LLSDSerialize.toBinary(sd);
}

/**
 * Universal LLSD Parser (Auto-Detect)
 */
export function parse(data: Uint8Array | string): LLSDValue {
  const sd = LLSDSerialize.parse(data);
  return LLSDSerialize.toJSObject(sd);
}

/**
 * LLSD Notation Parsing
 */
export function parseNotation(notation: string): LLSDValue {
  const sd = LLSDSerialize.fromNotation(notation);
  return LLSDSerialize.toJSObject(sd);
}

/**
 * LLSD Notation Serialization
 */
export function serializeNotation(value: LLSDValue): string {
  const sd = LLSDSerialize.fromJSObject(value);
  return LLSDSerialize.toNotation(sd);
}

/**
 * JSON Conversion
 */
export function toJSON(value: LLSDValue): string {
  const sd = LLSDSerialize.fromJSObject(value);
  return LLSDSerialize.toJSON(sd);
}

export function fromJSON(json: string): LLSDValue {
  const sd = LLSDSerialize.fromJSON(json);
  return LLSDSerialize.toJSObject(sd);
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
    } catch {
      return LLSDFormat.NOTATION;
    }
  }
  return LLSDFormat.NOTATION;
}
