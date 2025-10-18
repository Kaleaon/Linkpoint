/**
 * Linkpoint PWA - Inventory Types (Features 31-35)
 * Ported from Android slproto/inventory/
 * 
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/inventory/
 * See: PWA-demo/ANDROID_PORT_ROADMAP.md Phase 2, Priority 2 (Features 31-35)
 * 
 * Defines constants, enums, and type parsers for special inventory item types
 * including gestures, animations, scripts, sounds, and textures.
 */

/**
 * Asset Types - matches Second Life asset type enumeration
 */
export const AssetType = {
  TEXTURE: 0,
  SOUND: 1,
  CALLING_CARD: 2,
  LANDMARK: 3,
  SCRIPT: 4,
  CLOTHING: 5,
  OBJECT: 6,
  NOTECARD: 7,
  CATEGORY: 8,
  LSL_TEXT: 10,
  LSL_BYTECODE: 11,
  TEXTURE_TGA: 12,
  BODYPART: 13,
  SOUND_WAV: 17,
  IMAGE_TGA: 18,
  IMAGE_JPEG: 19,
  ANIMATION: 20,
  GESTURE: 21,
  SIMSTATE: 22,
  LINK: 24,
  LINK_FOLDER: 25,
  MESH: 49,
  SETTINGS: 56,
  MATERIAL: 57
};

/**
 * Inventory Types - matches inventory item types
 */
export const InventoryType = {
  TEXTURE: 0,
  SOUND: 1,
  CALLING_CARD: 2,
  LANDMARK: 3,
  OBJECT: 6,
  NOTECARD: 7,
  CATEGORY: 8,
  LSL: 10,
  SNAPSHOT: 15,
  ATTACHMENT: 17,
  WEARABLE: 18,
  ANIMATION: 19,
  GESTURE: 20,
  MESH: 22,
  SETTINGS: 25
};

/**
 * Wearable Types
 */
export const WearableType = {
  SHAPE: 0,
  SKIN: 1,
  HAIR: 2,
  EYES: 3,
  SHIRT: 4,
  PANTS: 5,
  SHOES: 6,
  SOCKS: 7,
  JACKET: 8,
  GLOVES: 9,
  UNDERSHIRT: 10,
  UNDERPANTS: 11,
  SKIRT: 12,
  ALPHA: 13,
  TATTOO: 14,
  PHYSICS: 15,
  UNIVERSAL: 16,
  
  /**
   * Get wearable type name
   * @param {number} type - Wearable type ID
   * @returns {string} Wearable type name
   */
  toString(type) {
    const names = Object.keys(this);
    for (const name of names) {
      if (this[name] === type && typeof this[name] === 'number') {
        return name;
      }
    }
    return 'UNKNOWN';
  }
};

/**
 * Feature 31: Gesture Types and Parser
 */
export class GestureParser {
  /**
   * Parse gesture asset data
   * @param {string|Uint8Array} data - Gesture asset data
   * @returns {Object} Parsed gesture data
   */
  static parse(data) {
    // TODO: Implement full gesture parsing
    // Gesture format includes trigger words, animations, sounds, chat text
    
    return {
      version: 1,
      trigger: '',
      replaceWith: '',
      shortcut: '',
      animations: [],
      sounds: [],
      steps: []
    };
  }
  
  /**
   * Serialize gesture to asset format
   * @param {Object} gesture - Gesture object
   * @returns {string} Serialized gesture data
   */
  static serialize(gesture) {
    // TODO: Implement gesture serialization
    return '';
  }
}

/**
 * Feature 32: Animation Types and Parser
 */
export class AnimationParser {
  /**
   * Parse animation asset data
   * @param {Uint8Array} data - Animation asset data (BVH or SL anim format)
   * @returns {Object} Parsed animation data
   */
  static parse(data) {
    // TODO: Implement animation parsing (BVH format or SL binary anim)
    
    return {
      version: 1,
      duration: 0,
      emote: '',
      priority: 0,
      loop: false,
      easeIn: 0,
      easeOut: 0,
      handPose: 0,
      joints: [],
      constraints: []
    };
  }
  
  /**
   * Get animation metadata without full parse
   * @param {Uint8Array} data - Animation asset data
   * @returns {Object} Animation metadata
   */
  static getMetadata(data) {
    // TODO: Extract just metadata (duration, loop, etc.)
    return {
      duration: 0,
      loop: false,
      priority: 0
    };
  }
}

/**
 * Feature 33: Script Types and Parser
 */
export class ScriptParser {
  /**
   * Parse LSL script text
   * @param {string} scriptText - LSL script source code
   * @returns {Object} Parsed script information
   */
  static parse(scriptText) {
    // TODO: Implement LSL syntax analysis
    
    // Extract basic info
    const functions = [];
    const states = [];
    const globals = [];
    
    // Simple regex patterns for now
    const funcPattern = /(\w+)\s+(\w+)\s*\([^)]*\)/g;
    const statePattern = /state\s+(\w+)/g;
    
    let match;
    while ((match = funcPattern.exec(scriptText)) !== null) {
      functions.push(match[2]);
    }
    
    while ((match = statePattern.exec(scriptText)) !== null) {
      states.push(match[1]);
    }
    
    return {
      functions,
      states,
      globals,
      lineCount: scriptText.split('\n').length
    };
  }
  
  /**
   * Validate LSL script syntax (basic)
   * @param {string} scriptText - LSL script source code
   * @returns {Object} Validation result
   */
  static validate(scriptText) {
    // TODO: Implement proper LSL syntax validation
    
    const errors = [];
    const warnings = [];
    
    // Basic checks
    const braceBalance = (scriptText.match(/{/g) || []).length - (scriptText.match(/}/g) || []).length;
    if (braceBalance !== 0) {
      errors.push('Unbalanced braces');
    }
    
    return {
      valid: errors.length === 0,
      errors,
      warnings
    };
  }
}

/**
 * Feature 34: Sound Types and Parser
 */
export class SoundParser {
  /**
   * Parse sound asset metadata
   * @param {Uint8Array} data - Sound asset data (WAV or OGG)
   * @returns {Object} Sound metadata
   */
  static parse(data) {
    // TODO: Implement sound format detection and parsing
    
    const format = this.detectFormat(data);
    
    return {
      format,
      duration: 0,
      sampleRate: 0,
      channels: 0,
      bitDepth: 0
    };
  }
  
  /**
   * Detect sound format
   * @param {Uint8Array} data - Sound asset data
   * @returns {string} Format (wav, ogg, unknown)
   */
  static detectFormat(data) {
    if (data.length < 4) return 'unknown';
    
    // Check for WAV header (RIFF)
    if (data[0] === 0x52 && data[1] === 0x49 && data[2] === 0x46 && data[3] === 0x46) {
      return 'wav';
    }
    
    // Check for OGG header
    if (data[0] === 0x4F && data[1] === 0x67 && data[2] === 0x67 && data[3] === 0x53) {
      return 'ogg';
    }
    
    return 'unknown';
  }
}

/**
 * Feature 35: Texture Types and Parser
 */
export class TextureParser {
  /**
   * Parse texture metadata
   * @param {Uint8Array} data - Texture asset data (JPEG2000, TGA, etc.)
   * @returns {Object} Texture metadata
   */
  static parse(data) {
    // TODO: Implement texture format detection and parsing
    
    const format = this.detectFormat(data);
    
    return {
      format,
      width: 0,
      height: 0,
      components: 0,
      hasAlpha: false
    };
  }
  
  /**
   * Detect texture format
   * @param {Uint8Array} data - Texture asset data
   * @returns {string} Format (j2k, tga, jpeg, png, unknown)
   */
  static detectFormat(data) {
    if (data.length < 4) return 'unknown';
    
    // JPEG2000 (starts with 0x00 0x00 0x00 0x0C 0x6A 0x50 0x20 0x20)
    if (data.length >= 8 && 
        data[0] === 0x00 && data[1] === 0x00 && 
        data[4] === 0x6A && data[5] === 0x50) {
      return 'j2k';
    }
    
    // TGA - no definitive header, check footer
    if (data.length >= 26) {
      const footerOffset = data.length - 26;
      const footer = String.fromCharCode(...data.slice(footerOffset + 8, footerOffset + 24));
      if (footer === 'TRUEVISION-XFILE') {
        return 'tga';
      }
    }
    
    // JPEG (starts with 0xFF 0xD8)
    if (data[0] === 0xFF && data[1] === 0xD8) {
      return 'jpeg';
    }
    
    // PNG (starts with 0x89 0x50 0x4E 0x47)
    if (data[0] === 0x89 && data[1] === 0x50 && data[2] === 0x4E && data[3] === 0x47) {
      return 'png';
    }
    
    return 'unknown';
  }
  
  /**
   * Get texture dimensions without full decode
   * @param {Uint8Array} data - Texture asset data
   * @returns {Object} Width and height
   */
  static getDimensions(data) {
    // TODO: Implement dimension extraction for each format
    return { width: 0, height: 0 };
  }
}

/**
 * Helper function to get asset type name
 * @param {number} type - Asset type ID
 * @returns {string} Asset type name
 */
export function getAssetTypeName(type) {
  const names = Object.keys(AssetType);
  for (const name of names) {
    if (AssetType[name] === type) {
      return name;
    }
  }
  return 'UNKNOWN';
}

/**
 * Helper function to get inventory type name
 * @param {number} type - Inventory type ID
 * @returns {string} Inventory type name
 */
export function getInventoryTypeName(type) {
  const names = Object.keys(InventoryType);
  for (const name of names) {
    if (InventoryType[name] === type) {
      return name;
    }
  }
  return 'UNKNOWN';
}

// Default export with all parsers and types
export default {
  AssetType,
  InventoryType,
  WearableType,
  GestureParser,
  AnimationParser,
  ScriptParser,
  SoundParser,
  TextureParser,
  getAssetTypeName,
  getInventoryTypeName
};
