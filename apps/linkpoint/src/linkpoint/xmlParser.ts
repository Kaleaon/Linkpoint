/**
 * LLSD XML Parser - Powered by @unified-llsd/typescript
 */

import { LLSDSerialize } from '@unified-llsd/typescript';
import { LLSD, LLSDException } from './types';

export class LLSDXMLParser {
    /**
     * Parse LLSD from XML string
     */
    parse(xmlString: string): LLSD {
        if (!xmlString || xmlString.trim() === '') {
            throw new LLSDException('Empty XML string');
        }
        try {
            const llsdValue = LLSDSerialize.fromXML(xmlString);
            const content = llsdValue.getContent();
            return new LLSD(content);
        } catch (error: any) {
            if (error instanceof LLSDException) {
                throw error;
            }
            throw new LLSDException(error?.message || String(error), error as Error);
        }
    }
}
