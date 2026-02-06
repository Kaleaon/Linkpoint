/*
 * Advanced Rendering System - TypeScript/React implementation with fine-grained controls
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * TypeScript implementation Copyright (C) 2024
 */

import { EventEmitter } from 'events';

/**
 * Advanced rendering system with fine-grained controls (TypeScript/React implementation).
 * 
 * Features:
 * - WebGL-based rendering with TypeScript type safety
 * - React-compatible event system for UI updates
 * - Browser-optimized performance monitoring
 * - Battery conservation with Page Visibility API
 */

export enum TextureQuality {
    VERY_LOW = 'VERY_LOW',
    LOW = 'LOW',
    MEDIUM = 'MEDIUM',
    HIGH = 'HIGH',
    ULTRA = 'ULTRA'
}

export enum ShadowQuality {
    DISABLED = 'DISABLED',
    LOW = 'LOW',
    MEDIUM = 'MEDIUM',
    HIGH = 'HIGH',
    ULTRA = 'ULTRA'
}

export interface QualitySettings {
    overallQuality: number;
    autoAdjustQuality: boolean;
    renderScale: number;
    maxDrawDistance: number;
}

export interface PerformanceSettings {
    targetFPS: number;
    vSync: boolean;
    adaptiveQualityEnabled: boolean;
    maxCPUUsage: number;
    maxMemoryUsage: number;
}

export interface EffectsSettings {
    effectsEnabled: boolean;
    effectsQuality: number;
    bloom: boolean;
    motionBlur: boolean;
    depthOfField: boolean;
    screenSpaceReflections: boolean;
}

export interface TextureSettings {
    textureQuality: TextureQuality;
    anisotropicFiltering: boolean;
    anisotropyLevel: number;
    mipmapping: boolean;
    textureCompression: boolean;
}

export interface ShadowSettings {
    shadowsEnabled: boolean;
    shadowQuality: ShadowQuality;
    shadowDistance: number;
    shadowBias: number;
}

export interface MeshSettings {
    lodBias: number;
    maxLodLevel: number;
    meshStreaming: boolean;
    meshBandwidth: number;
}

export interface AvatarSettings {
    maxVisibleAvatars: number;
    avatarLodBias: number;
    avatarImpostors: boolean;
    impostorDistance: number;
}

export interface ParticleSettings {
    maxParticles: number;
    particleQuality: number;
    particlePhysics: boolean;
}

export interface RenderStatistics {
    renderingEnabled: boolean;
    batteryConservationMode: boolean;
    currentFPS: number;
    frameTime: number;
    trianglesRendered: number;
    drawCalls: number;
    textureMemoryUsed: number;
    bufferMemoryUsed: number;
}

export interface PerformanceMetrics {
    currentFPS: number;
    averageFPS: number;
    frameTime: number;
    cpuUsage: number;
    memoryUsage: number;
    batteryLevel?: number;
}

export class AdvancedRenderingSystem extends EventEmitter {
    // Rendering state
    private renderingEnabled: boolean = true;
    private batteryConservationMode: boolean = false;
    private currentFPS: number = 60;

    // WebGL context and canvas
    private canvas: HTMLCanvasElement | null = null;
    private gl: WebGLRenderingContext | WebGL2RenderingContext | null = null;

    // Settings with TypeScript interfaces
    public qualitySettings: QualitySettings = {
        overallQuality: 0.6,
        autoAdjustQuality: true,
        renderScale: 1.0,
        maxDrawDistance: 256
    };

    public performanceSettings: PerformanceSettings = {
        targetFPS: 60,
        vSync: true,
        adaptiveQualityEnabled: false,
        maxCPUUsage: 80,
        maxMemoryUsage: 2 * 1024 * 1024 * 1024
    };

    public effectsSettings: EffectsSettings = {
        effectsEnabled: true,
        effectsQuality: 0.6,
        bloom: true,
        motionBlur: false,
        depthOfField: false,
        screenSpaceReflections: true
    };

    public textureSettings: TextureSettings = {
        textureQuality: TextureQuality.MEDIUM,
        anisotropicFiltering: true,
        anisotropyLevel: 16,
        mipmapping: true,
        textureCompression: true
    };

    public shadowSettings: ShadowSettings = {
        shadowsEnabled: true,
        shadowQuality: ShadowQuality.MEDIUM,
        shadowDistance: 128,
        shadowBias: 0.005
    };

    public meshSettings: MeshSettings = {
        lodBias: 0.0,
        maxLodLevel: 4,
        meshStreaming: true,
        meshBandwidth: 500
    };

    public avatarSettings: AvatarSettings = {
        maxVisibleAvatars: 30,
        avatarLodBias: 0,
        avatarImpostors: true,
        impostorDistance: 64
    };

    public particleSettings: ParticleSettings = {
        maxParticles: 2000,
        particleQuality: 0.6,
        particlePhysics: true
    };

    // Performance monitoring
    private performanceMetrics: PerformanceMetrics = {
        currentFPS: 60,
        averageFPS: 60,
        frameTime: 16.67,
        cpuUsage: 0,
        memoryUsage: 0
    };

    // Animation frame and timing
    private animationFrameId: number | null = null;
    private lastFrameTime: number = 0;
    private frameCount: number = 0;
    private fpsUpdateTime: number = 0;

    // Battery optimization
    private visibilityChangeHandler: () => void;
    private batteryManager: any = null;

    // Stored settings for battery mode
    private storedSettings: Partial<{
        targetFPS: number;
        vSync: boolean;
        effectsEnabled: boolean;
        shadowsEnabled: boolean;
        maxParticles: number;
        textureQuality: TextureQuality;
        lodBias: number;
        maxAvatars: number;
    }> = {};

    constructor(canvas?: HTMLCanvasElement) {
        super();
        
        if (canvas) {
            this.initializeWebGL(canvas);
        }

        this.setupBatteryOptimization();
        this.applyBalancedPreset();
        
        console.log('TypeScript Advanced rendering system initialized');
    }

    private initializeWebGL(canvas: HTMLCanvasElement): void {
        this.canvas = canvas;
        
        // Try to get WebGL2 context first, then fallback to WebGL1
        this.gl = canvas.getContext('webgl2') || canvas.getContext('webgl');
        
        if (!this.gl) {
            throw new Error('WebGL not supported');
        }

        console.log('WebGL context initialized');
        this.emit('webglInitialized', this.gl);
    }

    private setupBatteryOptimization(): void {
        // Page Visibility API for battery conservation
        this.visibilityChangeHandler = () => {
            if (document.hidden && this.batteryConservationMode) {
                this.pauseRendering();
            } else if (!document.hidden) {
                this.resumeRendering();
            }
        };

        document.addEventListener('visibilitychange', this.visibilityChangeHandler);

        // Battery Status API (if available)
        if ('getBattery' in navigator) {
            (navigator as any).getBattery().then((battery: any) => {
                this.batteryManager = battery;
                
                battery.addEventListener('levelchange', () => {
                    this.performanceMetrics.batteryLevel = battery.level;
                    this.emit('batteryLevelChanged', battery.level);
                    
                    if (battery.level < 0.2 && !this.batteryConservationMode) {
                        this.emit('lowBattery', battery.level);
                    }
                });
            });
        }
    }

    // Main rendering control
    public get isRenderingEnabled(): boolean {
        return this.renderingEnabled;
    }

    public set isRenderingEnabled(enabled: boolean) {
        if (this.renderingEnabled !== enabled) {
            this.renderingEnabled = enabled;
            
            if (enabled) {
                this.startRenderLoop();
            } else {
                this.stopRenderLoop();
                this.clearFrameBuffer();
            }

            console.log(`Rendering ${enabled ? 'enabled' : 'disabled'}`);
            this.emit('renderingStateChanged', enabled);
        }
    }

    public get isBatteryConservationMode(): boolean {
        return this.batteryConservationMode;
    }

    public set isBatteryConservationMode(enabled: boolean) {
        if (this.batteryConservationMode !== enabled) {
            this.batteryConservationMode = enabled;
            
            if (enabled) {
                this.applyPowerSavingSettings();
                this.isRenderingEnabled = false;
            } else {
                this.restorePreviousSettings();
                this.isRenderingEnabled = true;
            }

            console.log(`Battery conservation mode ${enabled ? 'enabled' : 'disabled'}`);
            this.emit('batteryModeChanged', enabled);
        }
    }

    private clearFrameBuffer(): void {
        if (this.gl) {
            this.gl.clearColor(0, 0, 0, 1);
            this.gl.clear(this.gl.COLOR_BUFFER_BIT);
        }
        console.debug('Clearing frame buffer to blank background');
    }

    // Quality presets with TypeScript type safety
    public applyUltraLowPreset(): void {
        console.log('Applying Ultra Low quality preset');
        
        this.qualitySettings.overallQuality = 0.1;
        this.performanceSettings.targetFPS = 30;
        this.effectsSettings.effectsEnabled = false;
        this.textureSettings.textureQuality = TextureQuality.VERY_LOW;
        this.shadowSettings.shadowsEnabled = false;
        this.meshSettings.lodBias = -2.0;
        this.avatarSettings.maxVisibleAvatars = 5;
        this.particleSettings.maxParticles = 100;
        
        this.emit('qualityPresetChanged', 'ULTRA_LOW');
    }

    public applyLowPreset(): void {
        console.log('Applying Low quality preset');
        
        this.qualitySettings.overallQuality = 0.3;
        this.performanceSettings.targetFPS = 45;
        this.effectsSettings.effectsEnabled = true;
        this.effectsSettings.effectsQuality = 0.3;
        this.textureSettings.textureQuality = TextureQuality.LOW;
        this.shadowSettings.shadowsEnabled = false;
        this.meshSettings.lodBias = -1.0;
        this.avatarSettings.maxVisibleAvatars = 15;
        this.particleSettings.maxParticles = 500;
        
        this.emit('qualityPresetChanged', 'LOW');
    }

    public applyBalancedPreset(): void {
        console.log('Applying Balanced quality preset');
        
        this.qualitySettings.overallQuality = 0.6;
        this.performanceSettings.targetFPS = 60;
        this.effectsSettings.effectsEnabled = true;
        this.effectsSettings.effectsQuality = 0.6;
        this.textureSettings.textureQuality = TextureQuality.MEDIUM;
        this.shadowSettings.shadowsEnabled = true;
        this.shadowSettings.shadowQuality = ShadowQuality.MEDIUM;
        this.meshSettings.lodBias = 0.0;
        this.avatarSettings.maxVisibleAvatars = 30;
        this.particleSettings.maxParticles = 2000;
        
        this.emit('qualityPresetChanged', 'BALANCED');
    }

    public applyHighPreset(): void {
        console.log('Applying High quality preset');
        
        this.qualitySettings.overallQuality = 0.8;
        this.performanceSettings.targetFPS = 60;
        this.effectsSettings.effectsEnabled = true;
        this.effectsSettings.effectsQuality = 0.8;
        this.textureSettings.textureQuality = TextureQuality.HIGH;
        this.shadowSettings.shadowsEnabled = true;
        this.shadowSettings.shadowQuality = ShadowQuality.HIGH;
        this.meshSettings.lodBias = 1.0;
        this.avatarSettings.maxVisibleAvatars = 50;
        this.particleSettings.maxParticles = 5000;
        
        this.emit('qualityPresetChanged', 'HIGH');
    }

    public applyUltraPreset(): void {
        console.log('Applying Ultra quality preset');
        
        this.qualitySettings.overallQuality = 1.0;
        this.performanceSettings.targetFPS = 60;
        this.effectsSettings.effectsEnabled = true;
        this.effectsSettings.effectsQuality = 1.0;
        this.textureSettings.textureQuality = TextureQuality.ULTRA;
        this.shadowSettings.shadowsEnabled = true;
        this.shadowSettings.shadowQuality = ShadowQuality.ULTRA;
        this.meshSettings.lodBias = 2.0;
        this.avatarSettings.maxVisibleAvatars = 100;
        this.particleSettings.maxParticles = 10000;
        
        this.emit('qualityPresetChanged', 'ULTRA');
    }

    // Battery optimization
    private applyPowerSavingSettings(): void {
        // Store current settings
        this.storedSettings = {
            targetFPS: this.performanceSettings.targetFPS,
            vSync: this.performanceSettings.vSync,
            effectsEnabled: this.effectsSettings.effectsEnabled,
            shadowsEnabled: this.shadowSettings.shadowsEnabled,
            maxParticles: this.particleSettings.maxParticles,
            textureQuality: this.textureSettings.textureQuality,
            lodBias: this.meshSettings.lodBias,
            maxAvatars: this.avatarSettings.maxVisibleAvatars
        };

        // Apply extreme power saving
        this.performanceSettings.targetFPS = 15;
        this.performanceSettings.vSync = false;
        this.effectsSettings.effectsEnabled = false;
        this.shadowSettings.shadowsEnabled = false;
        this.particleSettings.maxParticles = 0;
        this.textureSettings.textureQuality = TextureQuality.VERY_LOW;
        this.meshSettings.lodBias = -3.0;
        this.avatarSettings.maxVisibleAvatars = 1;

        console.log('Applied power saving settings');
        this.emit('powerSavingSettingsApplied');
    }

    private restorePreviousSettings(): void {
        if (Object.keys(this.storedSettings).length > 0) {
            this.performanceSettings.targetFPS = this.storedSettings.targetFPS!;
            this.performanceSettings.vSync = this.storedSettings.vSync!;
            this.effectsSettings.effectsEnabled = this.storedSettings.effectsEnabled!;
            this.shadowSettings.shadowsEnabled = this.storedSettings.shadowsEnabled!;
            this.particleSettings.maxParticles = this.storedSettings.maxParticles!;
            this.textureSettings.textureQuality = this.storedSettings.textureQuality!;
            this.meshSettings.lodBias = this.storedSettings.lodBias!;
            this.avatarSettings.maxVisibleAvatars = this.storedSettings.maxAvatars!;

            console.log('Restored previous settings');
            this.emit('previousSettingsRestored');
        }
    }

    // Render loop management
    private startRenderLoop(): void {
        if (this.animationFrameId === null) {
            this.lastFrameTime = performance.now();
            this.renderLoop();
        }
    }

    private stopRenderLoop(): void {
        if (this.animationFrameId !== null) {
            cancelAnimationFrame(this.animationFrameId);
            this.animationFrameId = null;
        }
    }

    private renderLoop = (currentTime: number = performance.now()): void => {
        if (!this.renderingEnabled) return;

        // Calculate frame time
        const frameTime = currentTime - this.lastFrameTime;
        this.performanceMetrics.frameTime = frameTime;

        // Update FPS calculation
        this.frameCount++;
        if (currentTime - this.fpsUpdateTime >= 1000) {
            this.performanceMetrics.currentFPS = this.frameCount;
            this.performanceMetrics.averageFPS = 
                (this.performanceMetrics.averageFPS + this.performanceMetrics.currentFPS) / 2;
            this.frameCount = 0;
            this.fpsUpdateTime = currentTime;
            
            this.emit('fpsUpdated', this.performanceMetrics.currentFPS);
        }

        // Perform rendering
        this.render(frameTime);

        // Update adaptive quality if enabled
        if (this.performanceSettings.adaptiveQualityEnabled) {
            this.updateAdaptiveQuality();
        }

        this.lastFrameTime = currentTime;
        this.animationFrameId = requestAnimationFrame(this.renderLoop);
    };

    private render(deltaTime: number): void {
        if (!this.gl) return;

        // Basic WebGL rendering (placeholder)
        this.gl.clearColor(0.1, 0.1, 0.2, 1.0);
        this.gl.clear(this.gl.COLOR_BUFFER_BIT | this.gl.DEPTH_BUFFER_BIT);

        // Emit render event for external systems
        this.emit('frameRendered', deltaTime);
    }

    private pauseRendering(): void {
        this.stopRenderLoop();
        this.emit('renderingPaused');
    }

    private resumeRendering(): void {
        if (this.renderingEnabled) {
            this.startRenderLoop();
            this.emit('renderingResumed');
        }
    }

    // Adaptive quality system
    public enableAdaptiveQuality(enabled: boolean): void {
        this.performanceSettings.adaptiveQualityEnabled = enabled;
        
        if (enabled) {
            console.log('Adaptive quality enabled');
        } else {
            console.log('Adaptive quality disabled');
        }
        
        this.emit('adaptiveQualityChanged', enabled);
    }

    public updateAdaptiveQuality(): void {
        if (!this.performanceSettings.adaptiveQualityEnabled) return;

        const currentFPS = this.performanceMetrics.currentFPS;
        const targetFPS = this.performanceSettings.targetFPS;
        const fpsRatio = currentFPS / targetFPS;

        if (fpsRatio < 0.8) {
            this.reduceQuality();
        } else if (fpsRatio > 1.2) {
            this.increaseQuality();
        }
    }

    private reduceQuality(): void {
        const currentQuality = this.qualitySettings.overallQuality;
        if (currentQuality > 0.1) {
            this.qualitySettings.overallQuality = Math.max(currentQuality - 0.1, 0.1);
            console.debug(`Reduced quality to ${this.qualitySettings.overallQuality}`);
            this.emit('qualityReduced', this.qualitySettings.overallQuality);
        }
    }

    private increaseQuality(): void {
        const currentQuality = this.qualitySettings.overallQuality;
        if (currentQuality < 1.0) {
            this.qualitySettings.overallQuality = Math.min(currentQuality + 0.05, 1.0);
            console.debug(`Increased quality to ${this.qualitySettings.overallQuality}`);
            this.emit('qualityIncreased', this.qualitySettings.overallQuality);
        }
    }

    // Statistics and monitoring
    public getPerformanceMetrics(): PerformanceMetrics {
        return { ...this.performanceMetrics };
    }

    public getRenderStatistics(): RenderStatistics {
        return {
            renderingEnabled: this.renderingEnabled,
            batteryConservationMode: this.batteryConservationMode,
            currentFPS: this.performanceMetrics.currentFPS,
            frameTime: this.performanceMetrics.frameTime,
            trianglesRendered: 0, // Placeholder
            drawCalls: 0, // Placeholder
            textureMemoryUsed: 0, // Placeholder
            bufferMemoryUsed: 0 // Placeholder
        };
    }

    // Configuration export/import
    public exportSettings(): Record<string, any> {
        return {
            quality: this.qualitySettings,
            performance: this.performanceSettings,
            effects: this.effectsSettings,
            textures: this.textureSettings,
            shadows: this.shadowSettings,
            meshes: this.meshSettings,
            avatars: this.avatarSettings,
            particles: this.particleSettings
        };
    }

    public importSettings(settings: Record<string, any>): void {
        if (settings.quality) Object.assign(this.qualitySettings, settings.quality);
        if (settings.performance) Object.assign(this.performanceSettings, settings.performance);
        if (settings.effects) Object.assign(this.effectsSettings, settings.effects);
        if (settings.textures) Object.assign(this.textureSettings, settings.textures);
        if (settings.shadows) Object.assign(this.shadowSettings, settings.shadows);
        if (settings.meshes) Object.assign(this.meshSettings, settings.meshes);
        if (settings.avatars) Object.assign(this.avatarSettings, settings.avatars);
        if (settings.particles) Object.assign(this.particleSettings, settings.particles);

        console.log('Imported rendering settings');
        this.emit('settingsImported', settings);
    }

    // Canvas management
    public setCanvas(canvas: HTMLCanvasElement): void {
        this.initializeWebGL(canvas);
    }

    public getCanvas(): HTMLCanvasElement | null {
        return this.canvas;
    }

    public getWebGLContext(): WebGLRenderingContext | WebGL2RenderingContext | null {
        return this.gl;
    }

    // Cleanup
    public shutdown(): void {
        console.log('Shutting down TypeScript Advanced rendering system');

        // Stop render loop
        this.stopRenderLoop();

        // Remove event listeners
        document.removeEventListener('visibilitychange', this.visibilityChangeHandler);

        // Clean up WebGL context
        if (this.gl) {
            // WebGL context cleanup would go here
        }

        // Remove all event listeners
        this.removeAllListeners();

        console.log('TypeScript Advanced rendering system shutdown complete');
    }
}