/**
 * Second Life LLSD Utilities
 *
 * Specific LLSD patterns and helpers for Second Life
 * Copyright (C) 2024 Linden Lab
 */

import { LLSD, LLSDValue, LLSDMap } from '../types';

export class SecondLifeLLSDUtils {
    /**
     * Create a standard SL capability request
     */
    static createCapRequest(method: string, params: LLSDMap = {}): LLSD {
        return new LLSD({
            method,
            params
        });
    }

    /**
     * Parse a standard SL capability response
     */
    static parseCapResponse(llsd: LLSD): LLSDValue {
        const content = llsd.getContent();
        if (content && typeof content === 'object' && 'response' in content) {
            return (content as any).response;
        }
        return content;
    }
}
