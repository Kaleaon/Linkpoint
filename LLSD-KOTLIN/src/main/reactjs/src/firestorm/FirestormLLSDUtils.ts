/**
 * Firestorm LLSD Extensions - TypeScript Implementation
 * 
 * Based on Java implementation and Firestorm viewer functionality
 * Copyright (C) 2024 Linden Lab
 */

import { LLSDValue, LLSDMap, LLSDArray, LLSDException, LLSDUtils } from '../types';
import { SecondLifeLLSDUtils, SLValidationRules, SLValidationResult } from '../secondlife/SecondLifeLLSDUtils';

/**
 * Firestorm-specific validation rules
 */
export class FSValidationRules extends SLValidationRules {
    requiresFSVersion: boolean = false;
    minFSVersion: string = '';
    requiresRLV: boolean = false;
    requiresBridge: boolean = false;

    requireFSVersion(minVersion: string): this {
        this.requiresFSVersion = true;
        this.minFSVersion = minVersion;
        return this;
    }

    requireRLV(): this {
        this.requiresRLV = true;
        return this;
    }

    requireBridge(): this {
        this.requiresBridge = true;
        return this;
    }
}

/**
 * Firestorm-specific validation result
 */
export class FSValidationResult extends SLValidationResult {
    private firestormVersion: string = '';
    private rlvEnabled: boolean = false;
    private bridgeEnabled: boolean = false;

    setFirestormVersion(version: string): void {
        this.firestormVersion = version;
    }

    setRLVEnabled(enabled: boolean): void {
        this.rlvEnabled = enabled;
    }

    setBridgeEnabled(enabled: boolean): void {
        this.bridgeEnabled = enabled;
    }

    getFirestormVersion(): string {
        return this.firestormVersion;
    }

    isRLVEnabled(): boolean {
        return this.rlvEnabled;
    }

    isBridgeEnabled(): boolean {
        return this.bridgeEnabled;
    }
}

export class FirestormLLSDUtils {
    /**
     * RLV (Restrained Life Viewer) command structure
     */
    static RLVCommand = class {
        private behaviour: string;
        private option: string;
        private param: string;
        private sourceId: string;

        constructor(behaviour: string, option: string, param: string, sourceId: string) {
            this.behaviour = behaviour;
            this.option = option;
            this.param = param;
            this.sourceId = sourceId;
        }

        toLLSD(): LLSDMap {
            return {
                behaviour: this.behaviour,
                option: this.option,
                param: this.param,
                source_id: this.sourceId,
                timestamp: Date.now() / 1000
            };
        }

        toString(): string {
            return `${this.behaviour}${this.option ? ':' + this.option : ''}${this.param}`;
        }
    };

    /**
     * Create enhanced radar data structure
     */
    static createRadarData(
        agentId: string,
        displayName: string,
        userName: string,
        position: LLSDArray,
        distance: number,
        isTyping: boolean,
        attachments: LLSDArray = []
    ): LLSDMap {
        return {
            agent_id: agentId,
            display_name: displayName,
            user_name: userName,
            position: position,
            distance: distance,
            is_typing: isTyping,
            attachments: attachments,
            last_seen: Date.now() / 1000,
            radar_version: '6.0.0'
        };
    }

    /**
     * Create bridge communication message
     */
    static createBridgeMessage(
        command: string,
        parameters: LLSDMap,
        requestId: string,
        priority: number = 1
    ): LLSDMap {
        return {
            command: command,
            parameters: parameters,
            request_id: requestId,
            priority: priority,
            bridge_version: '6.0.0',
            timestamp: Date.now() / 1000
        };
    }

    /**
     * Create performance statistics structure
     */
    static createPerformanceStats(
        fps: number,
        bandwidth: number,
        memoryUsage: number,
        renderTime: number,
        scriptTime: number,
        triangles: number
    ): LLSDMap {
        return {
            fps: fps,
            bandwidth: bandwidth,
            memory_usage: memoryUsage,
            render_time: renderTime,
            script_time: scriptTime,
            triangles: triangles,
            firestorm_version: '6.0.0',
            timestamp: Date.now() / 1000
        };
    }

    /**
     * Create enhanced particle system data
     */
    static createEnhancedParticleSystem(
        sourceId: string,
        ownerKey: string,
        pattern: number,
        maxAge: number,
        startAge: number,
        innerAngle: number,
        outerAngle: number,
        burstRate: number,
        burstPartCount: number,
        burstSpeedMin: number,
        burstSpeedMax: number,
        burstRadius: number,
        accelX: number,
        accelY: number,
        accelZ: number,
        textureUuid: string,
        targetUuid: string,
        particleFlags: number,
        startColorR: number,
        startColorG: number,
        startColorB: number,
        startColorA: number,
        endColorR: number,
        endColorG: number,
        endColorB: number,
        endColorA: number,
        startScaleX: number,
        startScaleY: number,
        endScaleX: number,
        endScaleY: number
    ): LLSDMap {
        return {
            source_id: sourceId,
            owner_key: ownerKey,
            pattern: pattern,
            max_age: maxAge,
            start_age: startAge,
            inner_angle: innerAngle,
            outer_angle: outerAngle,
            burst_rate: burstRate,
            burst_part_count: burstPartCount,
            burst_speed_min: burstSpeedMin,
            burst_speed_max: burstSpeedMax,
            burst_radius: burstRadius,
            accel: [accelX, accelY, accelZ],
            texture_uuid: textureUuid,
            target_uuid: targetUuid,
            particle_flags: particleFlags,
            start_color: [startColorR, startColorG, startColorB, startColorA],
            end_color: [endColorR, endColorG, endColorB, endColorA],
            start_scale: [startScaleX, startScaleY],
            end_scale: [endScaleX, endScaleY],
            firestorm_enhanced: true
        };
    }

    /**
     * Validate Firestorm-specific LLSD structure
     */
    static validateFSStructure(llsdData: LLSDValue, rules: FSValidationRules): FSValidationResult {
        const result = new FSValidationResult();

        // Use base SL validation first
        const baseResult = SecondLifeLLSDUtils.validateSLStructure(llsdData, rules);
        for (const error of baseResult.getErrors()) {
            result.addError(error);
        }
        for (const warning of baseResult.getWarnings()) {
            result.addWarning(warning);
        }

        if (!baseResult.isValid()) {
            return result; // Don't continue if base validation failed
        }

        // Firestorm-specific validations
        if (typeof llsdData === 'object' && !Array.isArray(llsdData) && llsdData !== null) {
            const map = llsdData as LLSDMap;

            // Check Firestorm version if required
            if (rules.requiresFSVersion) {
                const version = map['firestorm_version'] || map['viewer_version'] || map['ViewerVersion'];
                if (!version || typeof version !== 'string') {
                    result.addError('Missing Firestorm version information');
                } else if (!this.isCompatibleVersion(version, rules.minFSVersion)) {
                    result.addError(`Incompatible Firestorm version: ${version}, required: ${rules.minFSVersion}`);
                }
            }

            // Check RLV support if required
            if (rules.requiresRLV) {
                const rlvEnabled = map['rlv_enabled'] || map['RLVEnabled'];
                if (!rlvEnabled) {
                    result.addWarning('RLV support is required but not enabled');
                }
            }

            // Check bridge connection if required
            if (rules.requiresBridge) {
                const bridgeConnected = map['bridge_connected'] || map['BridgeConnected'];
                if (!bridgeConnected) {
                    result.addWarning('Bridge connection is required but not established');
                }
            }
        }

        return result;
    }

    /**
     * Check if a Firestorm version is compatible with the minimum required version
     */
    private static isCompatibleVersion(version: string, minVersion: string): boolean {
        if (!version || !minVersion) {
            return false;
        }

        const parseVersion = (v: string): number[] => {
            return v.split('.').map(part => {
                const num = parseInt(part.replace(/\D/g, ''), 10);
                return isNaN(num) ? 0 : num;
            });
        };

        const versionParts = parseVersion(version);
        const minParts = parseVersion(minVersion);
        const maxLength = Math.max(versionParts.length, minParts.length);

        for (let i = 0; i < maxLength; i++) {
            const v = i < versionParts.length ? versionParts[i] : 0;
            const m = i < minParts.length ? minParts[i] : 0;
            
            if (v > m) return true;
            if (v < m) return false;
        }

        return true; // Equal versions are compatible
    }

    /**
     * Thread-safe caching for performance (simplified for TypeScript)
     */
    static FSLLSDCache = class {
        private cache: Map<string, { data: LLSDValue, timestamp: number }> = new Map();
        private ttl: number;

        constructor(ttlMs: number = 300000) { // 5 minutes default
            this.ttl = ttlMs;
        }

        put(key: string, data: LLSDValue): void {
            this.cache.set(key, {
                data: LLSDUtils.deepCopy(data),
                timestamp: Date.now()
            });
        }

        get(key: string): LLSDValue | null {
            const entry = this.cache.get(key);
            if (!entry) {
                return null;
            }

            if (Date.now() - entry.timestamp > this.ttl) {
                this.cache.delete(key);
                return null;
            }

            return LLSDUtils.deepCopy(entry.data);
        }

        clear(): void {
            this.cache.clear();
        }

        size(): number {
            return this.cache.size;
        }
    };

    /**
     * Deep copy LLSD structures (alias to LLSDUtils.deepCopy for consistency with Java API)
     */
    static deepCopy(data: LLSDValue): LLSDValue {
        return LLSDUtils.deepCopy(data);
    }
}