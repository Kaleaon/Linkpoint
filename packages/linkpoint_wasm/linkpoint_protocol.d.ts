/* tslint:disable */
/* eslint-disable */

export class WasmMatrix4 {
    free(): void;
    [Symbol.dispose](): void;
    static from_quaternion(q: WasmQuaternion): WasmMatrix4;
    constructor();
    transform_point(x: number, y: number, z: number): Float32Array;
}

export class WasmQuaternion {
    free(): void;
    [Symbol.dispose](): void;
    static identity(): WasmQuaternion;
    constructor(x: number, y: number, z: number, w: number);
    normalize(): number;
    rotate_vector3(x: number, y: number, z: number): Float32Array;
    slerp(other: WasmQuaternion, t: number): WasmQuaternion;
}

export function wasm_generate_volume(params_json: string, detail: number): string;

export function wasm_parse_binary(bytes: Uint8Array): string;

export function wasm_parse_j2k_header(data: Uint8Array): string | undefined;

export function wasm_parse_notation(input: string): string;

export function wasm_parse_xml(input: string): string;

export function wasm_serialize_binary(json_str: string): Uint8Array;

export function wasm_serialize_notation(json_str: string): string;

export function wasm_serialize_xml(json_str: string): string;
