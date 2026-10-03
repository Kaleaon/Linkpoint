/**
 * LLSD TypeScript/JavaScript Library
 * 
 * Complete implementation of LLSD (Linden Lab Structured Data) format
 * Based on Java implementation and Second Life/Firestorm viewer code
 * 
 * Copyright (C) 2024 Linden Lab
 */

// Core types and utilities
export { 
    LLSD,
    LLSDValue,
    LLSDMap,
    LLSDArray,
    LLSDType,
    LLSDFormat,
    LLSDException,
    LLSDUtils
} from './types';

// Parsers
export { LLSDXMLParser } from './xmlParser';
export { LLSDBinaryParser } from './binaryParser';

// Serializers
export { LLSDXMLSerializer } from './xmlSerializer';
export { LLSDBinarySerializer } from './binarySerializer';

// Second Life extensions
export { SecondLifeLLSDUtils } from './secondlife/SecondLifeLLSDUtils';

// Firestorm extensions
export { FirestormLLSDUtils } from './firestorm/FirestormLLSDUtils';

// Convenience factory class
export class LLSDFactory {
    /**
     * Parse LLSD from XML string
     */
    static parseXML(xmlString: string): LLSD {
        const parser = new LLSDXMLParser();
        return parser.parse(xmlString);
    }

    /**
     * Parse LLSD from binary data
     */
    static parseBinary(data: Uint8Array): LLSD {
        const parser = new LLSDBinaryParser();
        return parser.parse(data);
    }

    /**
     * Parse LLSD from JSON string
     */
    static parseJSON(jsonString: string): LLSD {
        return LLSD.fromJSON(jsonString);
    }

    /**
     * Serialize LLSD to XML string
     */
    static serializeXML(llsd: LLSD, indent: number = 2): string {
        const serializer = new LLSDXMLSerializer(indent);
        return serializer.serialize(llsd);
    }

    /**
     * Serialize LLSD to binary data
     */
    static serializeBinary(llsd: LLSD): Uint8Array {
        const serializer = new LLSDBinarySerializer();
        return serializer.serialize(llsd);
    }

    /**
     * Serialize LLSD to JSON string
     */
    static serializeJSON(llsd: LLSD): string {
        return llsd.toJSON();
    }

    /**
     * Create an LLSD document with the given content
     */
    static create(content: LLSDValue = null): LLSD {
        return new LLSD(content);
    }

    /**
     * Create an LLSD map
     */
    static createMap(data: LLSDMap = {}): LLSD {
        return new LLSD(data);
    }

    /**
     * Create an LLSD array
     */
    static createArray(data: LLSDArray = []): LLSD {
        return new LLSD(data);
    }
}