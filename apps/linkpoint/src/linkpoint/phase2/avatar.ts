/**
 * Linkpoint PWA - Avatar Manager (Features 13-16)
 *
 * Phase 2: Core Protocol Extensions - Priority 1
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 35-40)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/users/
 *
 * Manages avatar appearance, attachments, and visual parameters.
 */

import { Utils } from '../utils';

export class AvatarManager extends Utils.EventEmitter {
  public avatarId: string | null = null;
  private appearanceParams: Map<string, any> = new Map();
  private attachments: Map<number, any> = new Map();
  private visualParams: Map<number, number> = new Map();
  private skeletonData: any = null;

  constructor() {
    super();
  }

  /**
   * Feature 13: Avatar appearance parameters
   * Set avatar appearance parameter
   */
  setAppearanceParam(paramName: string, value: any) {
    if (!paramName || typeof paramName !== 'string') {
      throw new Error('Valid parameter name required');
    }
    this.appearanceParams.set(paramName, value);
    console.log(`[Avatar] Set appearance: ${paramName}`);
    this.emit('appearance_changed', { paramName, value });
  }

  getAppearanceParam(paramName: string) {
    return this.appearanceParams.get(paramName);
  }

  /**
   * Feature 14: Attachment points
   * Attach an object to avatar attachment point
   */
  attachObject(attachmentPoint: number, objectData: any) {
    if (typeof attachmentPoint !== 'number') {
      throw new Error('Valid attachment point ID required');
    }
    if (!objectData || typeof objectData !== 'object') {
      throw new Error('Valid object data required');
    }
    this.attachments.set(attachmentPoint, objectData);
    console.log(`[Avatar] Attached object to point ${attachmentPoint}`);
  }

  detachObject(attachmentPoint: number) {
    if (this.attachments.delete(attachmentPoint)) {
      console.log(`[Avatar] Detached object from point ${attachmentPoint}`);
    }
  }

  getAttachments() {
    return new Map(this.attachments);
  }

  /**
   * Feature 15: Visual parameters (shape, skin settings)
   * Set visual parameter (body shape, skin, etc.)
   */
  setVisualParam(paramId: number, value: number) {
    if (typeof paramId !== 'number' || typeof value !== 'number') {
      throw new Error('Valid parameter ID and value required');
    }
    const clampedValue = Math.max(0, Math.min(1, value));
    this.visualParams.set(paramId, clampedValue);
    console.log(`[Avatar] Set visual param ${paramId}: ${clampedValue}`);
    this.emit('visual_param_changed', { paramId, value: clampedValue });
  }

  getVisualParam(paramId: number) {
    return this.visualParams.get(paramId) ?? null;
  }

  /**
   * Feature 16: Avatar skeleton basics
   * Initialize avatar skeleton structure
   */
  initializeSkeleton(skeletonConfig: any) {
    if (!skeletonConfig || typeof skeletonConfig !== 'object') {
      throw new Error('Valid skeleton configuration required');
    }
    this.skeletonData = {
      bones: skeletonConfig.bones || [],
      joints: skeletonConfig.joints || [],
      initialized: true,
      timestamp: Date.now()
    };
    console.log(`[Avatar] Skeleton initialized with ${this.skeletonData.bones.length} bones`);
  }

  getSkeleton() {
    return this.skeletonData;
  }

  exportConfig() {
    return {
      avatarId: this.avatarId,
      appearance: Object.fromEntries(this.appearanceParams),
      attachments: Object.fromEntries(this.attachments),
      visualParams: Object.fromEntries(this.visualParams),
      skeleton: this.skeletonData
    };
  }

  reset() {
    this.appearanceParams.clear();
    this.attachments.clear();
    this.visualParams.clear();
    this.skeletonData = null;
    console.log('[Avatar] Reset to defaults');
  }
}
