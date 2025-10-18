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
    const text = typeof data === 'string' ? data : new TextDecoder().decode(data);
    const lines = text.split('\n');
    
    const gesture = {
      version: 1,
      trigger: '',
      replaceWith: '',
      shortcut: '',
      animations: [],
      sounds: [],
      chats: [],
      waits: [],
      steps: []
    };
    
    let currentSection = null;
    
    for (let i = 0; i < lines.length; i++) {
      const line = lines[i].trim();
      
      if (line.startsWith('version')) {
        gesture.version = parseInt(line.split(' ')[1]);
      } else if (line.startsWith('trigger')) {
        gesture.trigger = line.substring(8).trim();
      } else if (line.startsWith('replace_with')) {
        gesture.replaceWith = line.substring(13).trim();
      } else if (line.startsWith('shortcut')) {
        gesture.shortcut = line.substring(9).trim();
      } else if (line === 'animation') {
        currentSection = 'animation';
      } else if (line === 'sound') {
        currentSection = 'sound';
      } else if (line === 'chat') {
        currentSection = 'chat';
      } else if (line === 'wait') {
        currentSection = 'wait';
      } else if (currentSection) {
        const parts = line.split(' ');
        
        if (currentSection === 'animation' && parts.length >= 2) {
          gesture.animations.push({
            name: parts[0],
            uuid: parts[1],
            flags: parts[2] ? parseInt(parts[2]) : 0
          });
          gesture.steps.push({ type: 'animation', data: gesture.animations[gesture.animations.length - 1] });
        } else if (currentSection === 'sound' && parts.length >= 2) {
          gesture.sounds.push({
            name: parts[0],
            uuid: parts[1],
            flags: parts[2] ? parseInt(parts[2]) : 0
          });
          gesture.steps.push({ type: 'sound', data: gesture.sounds[gesture.sounds.length - 1] });
        } else if (currentSection === 'chat' && line) {
          gesture.chats.push(line);
          gesture.steps.push({ type: 'chat', text: line });
        } else if (currentSection === 'wait' && parts[0]) {
          const waitTime = parseFloat(parts[0]);
          gesture.waits.push(waitTime);
          gesture.steps.push({ type: 'wait', duration: waitTime });
        }
        
        currentSection = null;
      }
    }
    
    return gesture;
  }
  
  /**
   * Serialize gesture to asset format
   * @param {Object} gesture - Gesture object
   * @returns {string} Serialized gesture data
   */
  static serialize(gesture) {
    let output = `version ${gesture.version}\n`;
    output += `trigger ${gesture.trigger}\n`;
    output += `replace_with ${gesture.replaceWith}\n`;
    output += `shortcut ${gesture.shortcut}\n`;
    
    for (const step of gesture.steps) {
      if (step.type === 'animation') {
        output += `animation\n${step.data.name} ${step.data.uuid} ${step.data.flags}\n`;
      } else if (step.type === 'sound') {
        output += `sound\n${step.data.name} ${step.data.uuid} ${step.data.flags}\n`;
      } else if (step.type === 'chat') {
        output += `chat\n${step.text}\n`;
      } else if (step.type === 'wait') {
        output += `wait\n${step.duration}\n`;
      }
    }
    
    return output;
  }
  
  /**
   * Validate gesture data
   * @param {Object} gesture - Gesture object
   * @returns {Object} Validation result {valid, errors}
   */
  static validate(gesture) {
    const errors = [];
    
    if (!gesture.trigger || gesture.trigger.length === 0) {
      errors.push('Gesture must have a trigger');
    }
    
    if (gesture.trigger && gesture.trigger.length > 31) {
      errors.push('Trigger too long (max 31 characters)');
    }
    
    if (gesture.steps.length === 0) {
      errors.push('Gesture must have at least one step');
    }
    
    return {
      valid: errors.length === 0,
      errors
    };
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
    // Check if BVH format (starts with "HIERARCHY")
    const text = new TextDecoder().decode(data.slice(0, Math.min(100, data.length)));
    
    if (text.includes('HIERARCHY')) {
      return this.parseBVH(data);
    } else {
      return this.parseSLAnim(data);
    }
  }
  
  /**
   * Parse BVH format animation
   * @param {Uint8Array} data - BVH animation data
   * @returns {Object} Parsed animation
   */
  static parseBVH(data) {
    const text = new TextDecoder().decode(data);
    const lines = text.split('\n');
    
    const animation = {
      format: 'bvh',
      version: 1,
      duration: 0,
      frameTime: 0,
      frameCount: 0,
      joints: [],
      hierarchy: [],
      frames: []
    };
    
    let inHierarchy = false;
    let inMotion = false;
    let currentJoint = null;
    
    for (const line of lines) {
      const trimmed = line.trim();
      
      if (trimmed === 'HIERARCHY') {
        inHierarchy = true;
      } else if (trimmed === 'MOTION') {
        inHierarchy = false;
        inMotion = true;
      } else if (inHierarchy) {
        if (trimmed.startsWith('ROOT') || trimmed.startsWith('JOINT')) {
          const parts = trimmed.split(' ');
          currentJoint = {
            name: parts[1],
            type: trimmed.startsWith('ROOT') ? 'root' : 'joint',
            channels: []
          };
          animation.joints.push(currentJoint);
        } else if (trimmed.startsWith('CHANNELS')) {
          const parts = trimmed.split(' ');
          const channelCount = parseInt(parts[1]);
          currentJoint.channels = parts.slice(2, 2 + channelCount);
        }
      } else if (inMotion) {
        if (trimmed.startsWith('Frames:')) {
          animation.frameCount = parseInt(trimmed.split(' ')[1]);
        } else if (trimmed.startsWith('Frame Time:')) {
          animation.frameTime = parseFloat(trimmed.split(' ')[2]);
          animation.duration = animation.frameCount * animation.frameTime;
        } else if (trimmed.length > 0 && !isNaN(parseFloat(trimmed.split(' ')[0]))) {
          const frameData = trimmed.split(' ').map(v => parseFloat(v));
          animation.frames.push(frameData);
        }
      }
    }
    
    return animation;
  }
  
  /**
   * Parse Second Life binary animation format
   * @param {Uint8Array} data - SL animation data
   * @returns {Object} Parsed animation
   */
  static parseSLAnim(data) {
    // SL binary anim format (simplified parsing)
    const animation = {
      format: 'sl',
      version: data[0],
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
    
    if (data.length < 20) {
      return animation;
    }
    
    let offset = 2;
    
    // Read duration (4 bytes, float)
    const durationView = new DataView(data.buffer, offset, 4);
    animation.duration = durationView.getFloat32(0, true);
    offset += 4;
    
    // Read emote name (null-terminated string)
    let emote = '';
    while (offset < data.length && data[offset] !== 0) {
      emote += String.fromCharCode(data[offset]);
      offset++;
    }
    animation.emote = emote;
    offset++; // Skip null terminator
    
    // Read priority, loop, ease in/out (simplified)
    if (offset + 10 < data.length) {
      animation.priority = data[offset++];
      animation.loop = data[offset++] !== 0;
      const easeInView = new DataView(data.buffer, offset, 4);
      animation.easeIn = easeInView.getFloat32(0, true);
      offset += 4;
      const easeOutView = new DataView(data.buffer, offset, 4);
      animation.easeOut = easeOutView.getFloat32(0, true);
      offset += 4;
      animation.handPose = data[offset++];
    }
    
    return animation;
  }
  
  /**
   * Get animation metadata without full parse
   * @param {Uint8Array} data - Animation asset data
   * @returns {Object} Animation metadata
   */
  static getMetadata(data) {
    const animation = this.parse(data);
    return {
      format: animation.format,
      duration: animation.duration,
      loop: animation.loop,
      priority: animation.priority,
      frameCount: animation.frameCount || 0
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
    const functions = [];
    const states = [];
    const globals = [];
    const events = [];
    
    // Extract functions
    const funcPattern = /(\w+)\s+(\w+)\s*\([^)]*\)\s*{/g;
    let match;
    while ((match = funcPattern.exec(scriptText)) !== null) {
      functions.push({
        returnType: match[1],
        name: match[2],
        line: scriptText.substring(0, match.index).split('\n').length
      });
    }
    
    // Extract states
    const statePattern = /state\s+(\w+)\s*{/g;
    while ((match = statePattern.exec(scriptText)) !== null) {
      states.push({
        name: match[1],
        line: scriptText.substring(0, match.index).split('\n').length
      });
    }
    
    // Extract globals (simplified)
    const globalPattern = /(integer|float|string|key|vector|rotation|list)\s+(\w+)\s*=/g;
    while ((match = globalPattern.exec(scriptText)) !== null) {
      globals.push({
        type: match[1],
        name: match[2],
        line: scriptText.substring(0, match.index).split('\n').length
      });
    }
    
    // Extract events
    const eventPattern = /(\w+)\s*\([^)]*\)\s*{/g;
    const knownEvents = ['state_entry', 'touch_start', 'touch_end', 'timer', 'listen', 
                         'collision_start', 'collision', 'collision_end', 'changed', 'on_rez'];
    while ((match = eventPattern.exec(scriptText)) !== null) {
      if (knownEvents.includes(match[1])) {
        events.push({
          name: match[1],
          line: scriptText.substring(0, match.index).split('\n').length
        });
      }
    }
    
    return {
      functions,
      states,
      globals,
      events,
      lineCount: scriptText.split('\n').length,
      characterCount: scriptText.length
    };
  }
  
  /**
   * Validate LSL script syntax (basic)
   * @param {string} scriptText - LSL script source code
   * @returns {Object} Validation result
   */
  static validate(scriptText) {
    const errors = [];
    const warnings = [];
    
    // Basic checks
    const braceBalance = (scriptText.match(/{/g) || []).length - (scriptText.match(/}/g) || []).length;
    if (braceBalance !== 0) {
      errors.push({ line: -1, message: 'Unbalanced braces' });
    }
    
    const parenBalance = (scriptText.match(/\(/g) || []).length - (scriptText.match(/\)/g) || []).length;
    if (parenBalance !== 0) {
      errors.push({ line: -1, message: 'Unbalanced parentheses' });
    }
    
    // Check for default state
    if (!scriptText.includes('default')) {
      errors.push({ line: -1, message: 'Script must have a default state' });
    }
    
    // Check for unterminated strings (simplified)
    const lines = scriptText.split('\n');
    for (let i = 0; i < lines.length; i++) {
      const line = lines[i];
      const quoteCount = (line.match(/"/g) || []).length;
      if (quoteCount % 2 !== 0 && !line.includes('//')) {
        warnings.push({ line: i + 1, message: 'Possible unterminated string' });
      }
    }
    
    return {
      valid: errors.length === 0,
      errors,
      warnings
    };
  }
  
  /**
   * Extract LSL keywords from script
   * @param {string} scriptText - LSL script source
   * @returns {Object} Keyword statistics
   */
  static getKeywordStats(scriptText) {
    const keywords = {
      dataTypes: ['integer', 'float', 'string', 'key', 'vector', 'rotation', 'list'],
      flowControl: ['if', 'else', 'for', 'while', 'do', 'return', 'jump'],
      events: ['state_entry', 'touch_start', 'timer', 'listen', 'collision_start'],
    };
    
    const stats = {};
    
    for (const category in keywords) {
      stats[category] = {};
      for (const keyword of keywords[category]) {
        const pattern = new RegExp(`\\b${keyword}\\b`, 'g');
        const matches = scriptText.match(pattern);
        stats[category][keyword] = matches ? matches.length : 0;
      }
    }
    
    return stats;
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
    const format = this.detectFormat(data);
    
    switch (format) {
      case 'png':
        return this.getPNGDimensions(data);
      case 'jpeg':
        return this.getJPEGDimensions(data);
      case 'j2k':
        return this.getJ2KDimensions(data);
      default:
        return { width: 0, height: 0 };
    }
  }
  
  /**
   * Extract PNG dimensions
   * @param {Uint8Array} data - PNG data
   * @returns {Object} Width and height
   */
  static getPNGDimensions(data) {
    if (data.length < 24) return { width: 0, height: 0 };
    
    // PNG dimensions are at bytes 16-23 (after signature and IHDR chunk header)
    const view = new DataView(data.buffer, 16, 8);
    return {
      width: view.getUint32(0, false),
      height: view.getUint32(4, false)
    };
  }
  
  /**
   * Extract JPEG dimensions
   * @param {Uint8Array} data - JPEG data
   * @returns {Object} Width and height
   */
  static getJPEGDimensions(data) {
    let offset = 2; // Skip SOI marker
    
    while (offset < data.length) {
      if (data[offset] !== 0xFF) break;
      
      const marker = data[offset + 1];
      offset += 2;
      
      // SOF markers (Start of Frame)
      if ((marker >= 0xC0 && marker <= 0xC3) || 
          (marker >= 0xC5 && marker <= 0xC7) ||
          (marker >= 0xC9 && marker <= 0xCB) ||
          (marker >= 0xCD && marker <= 0xCF)) {
        if (offset + 5 < data.length) {
          const view = new DataView(data.buffer, offset + 3, 4);
          return {
            height: view.getUint16(0, false),
            width: view.getUint16(2, false)
          };
        }
      }
      
      // Skip to next marker
      const segmentLength = new DataView(data.buffer, offset, 2).getUint16(0, false);
      offset += segmentLength;
    }
    
    return { width: 0, height: 0 };
  }
  
  /**
   * Extract JPEG2000 dimensions (simplified)
   * @param {Uint8Array} data - J2K data
   * @returns {Object} Width and height
   */
  static getJ2KDimensions(data) {
    // JPEG2000 is complex, this is a simplified approach
    // Look for Image Header box (ihdr)
    let offset = 12; // Skip JP2 signature
    
    while (offset + 8 < data.length) {
      const boxLength = new DataView(data.buffer, offset, 4).getUint32(0, false);
      const boxType = String.fromCharCode(...data.slice(offset + 4, offset + 8));
      
      if (boxType === 'ihdr' && offset + 16 < data.length) {
        const view = new DataView(data.buffer, offset + 8, 8);
        return {
          height: view.getUint32(0, false),
          width: view.getUint32(4, false)
        };
      }
      
      if (boxLength === 0 || boxLength > data.length - offset) break;
      offset += boxLength;
    }
    
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
