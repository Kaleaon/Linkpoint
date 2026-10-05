/* tslint:disable */
/* eslint-disable */

export function wasm_parse_binary(bytes: Uint8Array): string;

export function wasm_parse_notation(input: string): string;

export function wasm_parse_xml(input: string): string;

export function wasm_serialize_binary(json_str: string): Uint8Array;

export function wasm_serialize_notation(json_str: string): string;

export function wasm_serialize_xml(json_str: string): string;
