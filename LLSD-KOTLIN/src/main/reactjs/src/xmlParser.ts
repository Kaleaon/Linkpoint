/**
 * LLSD XML Parser - TypeScript Implementation
 * 
 * Based on Java implementation and Second Life viewer XML parsing
 * Copyright (C) 2024 Linden Lab
 */

import { DOMParser } from 'xmldom';
import { LLSD, LLSDValue, LLSDMap, LLSDArray, LLSDException, LLSDType, LLSDUtils } from './types';

export class LLSDXMLParser {
    private parser: DOMParser;

    constructor() {
        this.parser = new DOMParser({
            errorHandler: {
                warning: () => {
                    // Just log warnings, don't fail on them
                }, 
                error: (error: string) => {
                    throw new LLSDException(`XML parsing error: ${error}`);
                },
                fatalError: (error: string) => {
                    throw new LLSDException(`XML fatal error: ${error}`);
                }
            }
        });
    }

    /**
     * Parse LLSD from XML string
     */
    parse(xmlString: string): LLSD {
        try {
            // Basic malformed XML check
            if (!xmlString || xmlString.trim() === '') {
                throw new LLSDException('Empty XML string');
            }
            
            const doc = this.parser.parseFromString(xmlString, 'text/xml');
            
            // Check for parsing errors
            const parseError = doc.getElementsByTagName('parsererror')[0];
            if (parseError) {
                throw new LLSDException(`XML parsing error: ${parseError.textContent}`);
            }
            
            const llsdElement = doc.getElementsByTagName('llsd')[0];
            
            if (!llsdElement) {
                throw new LLSDException('Invalid LLSD XML: missing <llsd> root element');
            }

            // Find the first child element (the actual data)
            let dataElement: Element | null = null;
            for (let i = 0; i < llsdElement.childNodes.length; i++) {
                const node = llsdElement.childNodes[i];
                if (node.nodeType === 1) { // Element node
                    dataElement = node as Element;
                    break;
                }
            }

            if (!dataElement) {
                return new LLSD(null);
            }

            const content = this.parseElement(dataElement);
            return new LLSD(content);
        } catch (error) {
            if (error instanceof LLSDException) {
                throw error;
            }
            throw new LLSDException(`Failed to parse LLSD XML: ${error}`, error as Error);
        }
    }

    /**
     * Parse an individual XML element into LLSD value
     */
    private parseElement(element: Element): LLSDValue {
        const tagName = element.tagName.toLowerCase();

        switch (tagName) {
            case 'undef':
                return null;

            case 'boolean':
                const boolText = this.getElementText(element).toLowerCase();
                return boolText === 'true' || boolText === '1';

            case 'integer':
                const intText = this.getElementText(element);
                const intValue = parseInt(intText, 10);
                if (isNaN(intValue)) {
                    throw new LLSDException(`Invalid integer value: ${intText}`);
                }
                return intValue;

            case 'real':
                const realText = this.getElementText(element);
                const realValue = parseFloat(realText);
                if (isNaN(realValue)) {
                    throw new LLSDException(`Invalid real value: ${realText}`);
                }
                return realValue;

            case 'string':
                return this.getElementText(element);

            case 'uuid':
                const uuidText = this.getElementText(element);
                if (!LLSDUtils.isUUIDString(uuidText) && uuidText !== '') {
                    throw new LLSDException(`Invalid UUID value: ${uuidText}`);
                }
                return uuidText || '00000000-0000-0000-0000-000000000000';

            case 'date':
                const dateText = this.getElementText(element);
                if (!dateText) {
                    return new Date(0); // Unix epoch
                }
                const date = new Date(dateText);
                if (isNaN(date.getTime())) {
                    throw new LLSDException(`Invalid date value: ${dateText}`);
                }
                return date;

            case 'uri':
                const uriText = this.getElementText(element);
                try {
                    return new URL(uriText);
                } catch (error) {
                    throw new LLSDException(`Invalid URI value: ${uriText}`, error as Error);
                }

            case 'binary':
                const binaryText = this.getElementText(element);
                if (!binaryText) {
                    return new Uint8Array(0);
                }
                try {
                    // Decode base64
                    const binaryString = atob(binaryText);
                    const bytes = new Uint8Array(binaryString.length);
                    for (let i = 0; i < binaryString.length; i++) {
                        bytes[i] = binaryString.charCodeAt(i);
                    }
                    return bytes;
                } catch (error) {
                    throw new LLSDException(`Invalid binary data: ${binaryText}`, error as Error);
                }

            case 'array':
                const array: LLSDArray = [];
                for (let i = 0; i < element.childNodes.length; i++) {
                    const node = element.childNodes[i];
                    if (node.nodeType === 1) { // Element node
                        const value = this.parseElement(node as Element);
                        array.push(value);
                    }
                }
                return array;

            case 'map':
                const map: LLSDMap = {};
                let currentKey: string | null = null;
                
                for (let i = 0; i < element.childNodes.length; i++) {
                    const node = element.childNodes[i];
                    if (node.nodeType === 1) { // Element node
                        const childElement = node as Element;
                        if (childElement.tagName.toLowerCase() === 'key') {
                            currentKey = this.getElementText(childElement);
                        } else if (currentKey !== null) {
                            const value = this.parseElement(childElement);
                            map[currentKey] = value;
                            currentKey = null;
                        }
                    }
                }
                return map;

            default:
                throw new LLSDException(`Unknown LLSD element type: ${tagName}`);
        }
    }

    /**
     * Get the text content of an element
     */
    private getElementText(element: Element): string {
        let text = '';
        for (let i = 0; i < element.childNodes.length; i++) {
            const node = element.childNodes[i];
            if (node.nodeType === 3 || node.nodeType === 4) { // Text node or CDATA
                text += node.nodeValue;
            }
        }
        return text;
    }
}