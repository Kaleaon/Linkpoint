/**
 * Firestorm LLSD Utilities
 *
 * Specific LLSD patterns and helpers for Firestorm viewer
 * Copyright (C) 2024 Linden Lab
 */

import { LLSD, LLSDValue, LLSDMap } from '../types';

export class FirestormLLSDUtils {
    /**
     * Create a Firestorm-specific bridge command
     */
    static createBridgeCommand(command: string, params: LLSDMap = {}): LLSD {
        return new LLSD({
            command,
            params,
            viewer: 'Firestorm'
        });
    }
}
