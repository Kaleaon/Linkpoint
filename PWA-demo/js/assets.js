/**
 * Linkpoint PWA - Assets System
 * Ported from Android assets package (SLWearable, SLNotecard, SLLandmark)
 * Handles Second Life assets like wearables, notecards, and landmarks
 */

/**
 * Wearable types enum
 */
const WearableType = {
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
  
  // Helper methods
  fromString(str) {
    return this[str.toUpperCase()] || -1;
  },
  
  toString(type) {
    const entries = Object.entries(this);
    for (const [key, value] of entries) {
      if (value === type && typeof value === 'number') {
        return key.toLowerCase();
      }
    }
    return 'unknown';
  },
  
  isBodyPart(type) {
    return type >= 0 && type <= 3; // SHAPE, SKIN, HAIR, EYES
  },
  
  isClothing(type) {
    return type >= 4 && type <= 16 && type !== 13 && type !== 14 && type !== 15;
  }
};

/**
 * Wearable asset
 */
class SLWearable {
  constructor(assetData = null) {
    this.type = -1;
    this.name = '';
    this.description = '';
    this.permissions = {};
    this.saleInfo = {};
    this.parameters = new Map(); // Visual parameters
    this.textures = new Map(); // Texture entries
    this.rawData = '';
    
    if (assetData) {
      this.parse(assetData);
    }
  }
  
  /**
   * Parse wearable from asset data
   * @param {string|ArrayBuffer} data - Asset data
   */
  parse(data) {
    try {
      if (data instanceof ArrayBuffer) {
        const decoder = new TextDecoder('utf-8');
        data = decoder.decode(data);
      }
      
      this.rawData = data;
      const lines = data.split('\n');
      let lineIndex = 0;
      
      // Parse header
      const header = lines[lineIndex++];
      if (!header.startsWith('LLWearable')) {
        throw new Error('Invalid wearable format');
      }
      
      // Parse version
      const version = parseInt(lines[lineIndex++]);
      
      // Parse name
      this.name = this.parseField(lines, lineIndex++);
      
      // Parse description (optional)
      if (lineIndex < lines.length) {
        this.description = this.parseField(lines, lineIndex++);
      }
      
      // Parse sections
      while (lineIndex < lines.length) {
        const line = lines[lineIndex++].trim();
        
        if (line.startsWith('type ')) {
          this.type = parseInt(line.split(' ')[1]);
        } else if (line.startsWith('parameters ')) {
          const count = parseInt(line.split(' ')[1]);
          lineIndex = this.parseParameters(lines, lineIndex, count);
        } else if (line.startsWith('textures ')) {
          const count = parseInt(line.split(' ')[1]);
          lineIndex = this.parseTextures(lines, lineIndex, count);
        }
      }
    } catch (error) {
      console.error('Failed to parse wearable:', error);
      throw new Error(`Wearable parse error: ${error.message}`);
    }
  }
  
  /**
   * Parse a field value
   */
  parseField(lines, index) {
    const line = lines[index];
    if (!line) return '';
    
    // Handle quoted strings
    const match = line.match(/^[^"]*"([^"]*)"/);
    return match ? match[1] : line.trim();
  }
  
  /**
   * Parse parameters section
   */
  parseParameters(lines, startIndex, count) {
    for (let i = 0; i < count; i++) {
      const line = lines[startIndex++].trim();
      const parts = line.split(' ');
      
      if (parts.length >= 2) {
        const paramId = parseInt(parts[0]);
        const value = parseFloat(parts[1]);
        this.parameters.set(paramId, value);
      }
    }
    return startIndex;
  }
  
  /**
   * Parse textures section
   */
  parseTextures(lines, startIndex, count) {
    for (let i = 0; i < count; i++) {
      const line = lines[startIndex++].trim();
      const parts = line.split(' ');
      
      if (parts.length >= 2) {
        const textureIndex = parseInt(parts[0]);
        const textureId = parts[1];
        this.textures.set(textureIndex, textureId);
      }
    }
    return startIndex;
  }
  
  /**
   * Get parameter value
   * @param {number} paramId - Parameter ID
   * @returns {number} - Parameter value
   */
  getParameter(paramId) {
    return this.parameters.get(paramId) || 0.0;
  }
  
  /**
   * Set parameter value
   * @param {number} paramId - Parameter ID
   * @param {number} value - Parameter value
   */
  setParameter(paramId, value) {
    this.parameters.set(paramId, value);
  }
  
  /**
   * Get texture ID for index
   * @param {number} textureIndex - Texture index
   * @returns {string} - Texture UUID
   */
  getTexture(textureIndex) {
    return this.textures.get(textureIndex) || '00000000-0000-0000-0000-000000000000';
  }
  
  /**
   * Serialize wearable to string
   * @returns {string} - Serialized wearable data
   */
  serialize() {
    let output = 'LLWearable version 22\n';
    output += `${this.name}\n`;
    output += `${this.description}\n\n`;
    output += `\tpermissions 0\n`;
    output += `\t{\n`;
    output += `\t\tbase_mask\t7fffffff\n`;
    output += `\t\towner_mask\t7fffffff\n`;
    output += `\t\tgroup_mask\t00000000\n`;
    output += `\t\teveryone_mask\t00000000\n`;
    output += `\t\tnext_owner_mask\t00082000\n`;
    output += `\t\tcreator_id\t00000000-0000-0000-0000-000000000000\n`;
    output += `\t}\n`;
    output += `\tsale_info\t0\n`;
    output += `\t{\n`;
    output += `\t\tsale_type\tnot\n`;
    output += `\t\tsale_price\t10\n`;
    output += `\t}\n`;
    output += `type ${this.type}\n`;
    output += `parameters ${this.parameters.size}\n`;
    
    for (const [paramId, value] of this.parameters) {
      output += `${paramId} ${value}\n`;
    }
    
    output += `textures ${this.textures.size}\n`;
    for (const [index, textureId] of this.textures) {
      output += `${index} ${textureId}\n`;
    }
    
    return output;
  }
}

/**
 * Notecard asset
 */
class SLNotecard {
  constructor(assetData = null) {
    this.version = 2;
    this.text = '';
    this.embeddedItems = [];
    
    if (assetData) {
      this.parse(assetData);
    }
  }
  
  /**
   * Parse notecard from asset data
   * @param {string|ArrayBuffer} data - Asset data
   */
  parse(data) {
    try {
      if (data instanceof ArrayBuffer) {
        const decoder = new TextDecoder('utf-8');
        data = decoder.decode(data);
      }
      
      const lines = data.split('\n');
      let lineIndex = 0;
      
      // Parse header
      const header = lines[lineIndex++];
      if (!header.startsWith('Linden text version')) {
        throw new Error('Invalid notecard format');
      }
      
      this.version = parseInt(header.split(' ').pop());
      
      // Parse sections
      let inText = false;
      let inEmbedded = false;
      
      while (lineIndex < lines.length) {
        const line = lines[lineIndex++];
        
        if (line.startsWith('{')) {
          if (line.includes('Text length')) {
            inText = true;
          } else if (line.includes('count')) {
            inEmbedded = true;
          }
        } else if (line.startsWith('}')) {
          inText = false;
          inEmbedded = false;
        } else if (inText) {
          this.text += line + '\n';
        } else if (inEmbedded) {
          // Parse embedded item (simplified)
          if (line.includes('inv_item')) {
            const item = this.parseEmbeddedItem(lines, lineIndex);
            if (item) {
              this.embeddedItems.push(item);
            }
          }
        }
      }
      
      this.text = this.text.trim();
    } catch (error) {
      console.error('Failed to parse notecard:', error);
      this.text = 'Error parsing notecard';
    }
  }
  
  /**
   * Parse embedded inventory item
   */
  parseEmbeddedItem(lines, startIndex) {
    // Simplified parsing
    return {
      type: 'item',
      name: 'Embedded Item',
      uuid: '00000000-0000-0000-0000-000000000000'
    };
  }
  
  /**
   * Get notecard text
   * @returns {string} - Notecard text
   */
  getText() {
    return this.text;
  }
  
  /**
   * Set notecard text
   * @param {string} text - New text
   */
  setText(text) {
    this.text = text;
  }
}

/**
 * Landmark asset
 */
class SLLandmark {
  constructor(assetData = null) {
    this.version = 2;
    this.regionId = '00000000-0000-0000-0000-000000000000';
    this.position = { x: 128, y: 128, z: 25 };
    this.regionHandle = '0';
    
    if (assetData) {
      this.parse(assetData);
    }
  }
  
  /**
   * Parse landmark from asset data
   * @param {string|ArrayBuffer} data - Asset data
   */
  parse(data) {
    try {
      if (data instanceof ArrayBuffer) {
        const decoder = new TextDecoder('utf-8');
        data = decoder.decode(data);
      }
      
      const lines = data.split('\n');
      
      for (const line of lines) {
        if (line.startsWith('Landmark version')) {
          this.version = parseInt(line.split(' ').pop());
        } else if (line.startsWith('region_id')) {
          this.regionId = line.split(' ')[1];
        } else if (line.startsWith('local_pos')) {
          const parts = line.split(' ');
          this.position.x = parseFloat(parts[1]);
          this.position.y = parseFloat(parts[2]);
          this.position.z = parseFloat(parts[3]);
        }
      }
    } catch (error) {
      console.error('Failed to parse landmark:', error);
    }
  }
  
  /**
   * Get SLURL for landmark
   * @returns {string} - SLURL string
   */
  getSLURL() {
    return `secondlife://${this.regionId}/${Math.floor(this.position.x)}/${Math.floor(this.position.y)}/${Math.floor(this.position.z)}`;
  }
  
  /**
   * Get position
   * @returns {Object} - {x, y, z} position
   */
  getPosition() {
    return { ...this.position };
  }
}

/**
 * Asset manager for handling various asset types
 */
class AssetManager {
  constructor(protocol) {
    this.protocol = protocol;
    this.assetCache = new Map();
  }
  
  /**
   * Fetch asset data
   * @param {string} assetId - Asset UUID
   * @param {string} assetType - Asset type
   * @returns {Promise<any>} - Parsed asset object
   */
  async fetchAsset(assetId, assetType) {
    // Check cache
    const cached = this.assetCache.get(assetId);
    if (cached) {
      return cached;
    }
    
    try {
      // Fetch via capability
      const cap = this.protocol.getCapability('GetMesh') || this.protocol.getCapability('ViewerAsset');
      if (!cap) {
        throw new Error('Asset capability not available');
      }
      
      const response = await fetch(`${cap}?asset_id=${assetId}`);
      const data = await response.arrayBuffer();
      
      // Parse based on type
      let asset;
      switch (assetType) {
        case 'wearable':
          asset = new SLWearable(data);
          break;
        case 'notecard':
          asset = new SLNotecard(data);
          break;
        case 'landmark':
          asset = new SLLandmark(data);
          break;
        default:
          asset = data;
      }
      
      this.assetCache.set(assetId, asset);
      return asset;
    } catch (error) {
      console.error(`Failed to fetch asset ${assetId}:`, error);
      throw error;
    }
  }
  
  /**
   * Clear asset cache
   */
  clearCache() {
    this.assetCache.clear();
  }
}

// Export classes
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { 
    WearableType, 
    SLWearable, 
    SLNotecard, 
    SLLandmark, 
    AssetManager 
  };
}
