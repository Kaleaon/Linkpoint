/**
 * LLSD XML Serializer - TypeScript Implementation
 * 
 * Based on Java implementation and Second Life viewer XML serialization
 * Copyright (C) 2024 Linden Lab
 */

import { LLSD, LLSDValue, LLSDMap, LLSDArray, LLSDException, LLSDType, LLSDUtils } from './types';

export class LLSDXMLSerializer {
    private indentSize: number;

    constructor(indentSize: number = 2) {
        this.indentSize = indentSize;
    }

    /**
     * Serialize LLSD to XML string
     */
    serialize(llsd: LLSD): string {
        const content = llsd.getContent();
        let xml = '<?xml version="1.0" encoding="UTF-8"?>\n';
        xml += '<llsd>\n';
        xml += this.serializeValue(content, 1);
        xml += '</llsd>\n';
        return xml;
    }

    /**
     * Serialize a value to XML
     */
    private serializeValue(value: LLSDValue, depth: number): string {
        const indent = ' '.repeat(depth * this.indentSize);
        const type = LLSDUtils.getType(value);

        switch (type) {
            case LLSDType.UNKNOWN:
                return indent + '<undef />\n';

            case LLSDType.BOOLEAN:
                const boolValue = value as boolean;
                return indent + `<boolean>${boolValue ? '1' : '0'}</boolean>\n`;

            case LLSDType.INTEGER:
                const intValue = value as number;
                return indent + `<integer>${intValue}</integer>\n`;

            case LLSDType.REAL:
                const realValue = value as number;
                return indent + `<real>${realValue}</real>\n`;

            case LLSDType.STRING:
                const strValue = value as string;
                const escapedString = this.escapeXML(strValue);
                return indent + `<string>${escapedString}</string>\n`;

            case LLSDType.UUID:
                const uuidValue = value as string;
                return indent + `<uuid>${uuidValue}</uuid>\n`;

            case LLSDType.DATE:
                const dateValue = value as Date;
                const isoString = dateValue.toISOString();
                return indent + `<date>${isoString}</date>\n`;

            case LLSDType.URI:
                const uriValue = value as URL;
                const escapedUri = this.escapeXML(uriValue.href);
                return indent + `<uri>${escapedUri}</uri>\n`;

            case LLSDType.BINARY:
                const binaryValue = value as Uint8Array;
                const base64 = this.arrayBufferToBase64(binaryValue);
                return indent + `<binary>${base64}</binary>\n`;

            case LLSDType.ARRAY:
                const arrayValue = value as LLSDArray;
                let arrayXml = indent + '<array>\n';
                for (const item of arrayValue) {
                    arrayXml += this.serializeValue(item, depth + 1);
                }
                arrayXml += indent + '</array>\n';
                return arrayXml;

            case LLSDType.MAP:
                const mapValue = value as LLSDMap;
                let mapXml = indent + '<map>\n';
                for (const [key, val] of Object.entries(mapValue)) {
                    const escapedKey = this.escapeXML(key);
                    mapXml += ' '.repeat((depth + 1) * this.indentSize) + `<key>${escapedKey}</key>\n`;
                    mapXml += this.serializeValue(val, depth + 1);
                }
                mapXml += indent + '</map>\n';
                return mapXml;

            default:
                throw new LLSDException(`Cannot serialize unknown type: ${type}`);
        }
    }

    /**
     * Escape special XML characters
     */
    private escapeXML(str: string): string {
        return str
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&apos;');
    }

    /**
     * Convert Uint8Array to base64 string
     */
    private arrayBufferToBase64(buffer: Uint8Array): string {
        let binary = '';
        for (let i = 0; i < buffer.byteLength; i++) {
            binary += String.fromCharCode(buffer[i]);
        }
        return btoa(binary);
    }
}