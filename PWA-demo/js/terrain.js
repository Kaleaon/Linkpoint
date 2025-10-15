/**
 * Linkpoint PWA - Terrain System
 * Ported from Android TerrainPatch.kt and related classes
 * Handles Second Life terrain decompression and rendering
 */

class TerrainPatch {
  constructor() {
    this.END_OF_PATCHES = 97;
    this.OO_SQRT2 = 0.70710677;
    
    // Terrain patch data
    this.DCOffset = 0;
    this.PatchIDs = 0;
    this.QuantWBits = 0;
    this.Range = 0;
    this.WordBits = 0;
    this.heightMap = new Float32Array(256); // 16x16 terrain patch
    this.patches = new Int32Array(256);
    
    // Lookup tables
    this.CopyMatrix16 = new Int32Array(256);
    this.CopyMatrix32 = new Int32Array(256);
    this.CosineTable16 = new Float32Array(256);
    this.DequantizeTable16 = new Float32Array(256);
    this.DequantizeTable32 = new Float32Array(256);
    this.QuantizeTable16 = new Float32Array(256);
    
    // Initialize lookup tables
    this.buildDequantizeTable16();
    this.setupCosines16();
    this.buildCopyMatrix16();
    this.buildQuantizeTable16();
  }
  
  /**
   * Build dequantization lookup table
   */
  buildDequantizeTable16() {
    for (let i = 0; i < 16; i++) {
      for (let j = 0; j < 16; j++) {
        this.DequantizeTable16[i * 16 + j] = ((j + i) * 2.0) + 1.0;
      }
    }
  }
  
  /**
   * Build quantization lookup table
   */
  buildQuantizeTable16() {
    for (let i = 0; i < 16; i++) {
      for (let j = 0; j < 16; j++) {
        this.QuantizeTable16[i * 16 + j] = 1.0 / (((j + i) * 2.0) + 1.0);
      }
    }
  }
  
  /**
   * Setup cosine lookup table for DCT
   */
  setupCosines16() {
    for (let u = 0; u < 16; u++) {
      for (let n = 0; n < 16; n++) {
        this.CosineTable16[u * 16 + n] = Math.cos((Math.PI * (2 * n + 1) * u) / (2 * 16));
      }
    }
  }
  
  /**
   * Build copy matrix for zigzag ordering
   */
  buildCopyMatrix16() {
    let i = 0, x = 0, y = 0;
    let goingRight = true;
    let isInitial = false;
    
    while (y < 16 && x < 16) {
      this.CopyMatrix16[y * 16 + x] = i++;
      
      if (!isInitial) {
        if (goingRight) {
          x = (x < 15) ? x + 1 : x;
          if (x === 15) y++;
          goingRight = false;
          isInitial = true;
        } else {
          y = (y < 15) ? y + 1 : y;
          if (y === 15) x++;
          goingRight = true;
          isInitial = true;
        }
      } else {
        if (goingRight) {
          x++;
          y--;
          if (x === 15 || y === 0) isInitial = false;
        } else {
          x--;
          y++;
          if (y === 15 || x === 0) isInitial = false;
        }
      }
    }
  }
  
  /**
   * Decompress a terrain patch from bit stream
   * @param {BitBuffer} bitBuffer - The bit buffer to read from
   * @param {number} patchSize - Size of the patch
   * @returns {TerrainPatch|null} - Decompressed terrain patch or null if end marker
   */
  decompressPatch(bitBuffer, patchSize) {
    const header = bitBuffer.getBits(8);
    
    // Check for end of patches marker
    if (header === this.END_OF_PATCHES) {
      return null;
    }
    
    const patch = new TerrainPatch();
    patch.QuantWBits = header;
    patch.DCOffset = bitBuffer.getFloat();
    patch.Range = bitBuffer.getBits(16);
    patch.PatchIDs = bitBuffer.getBits(10);
    patch.WordBits = (header & 0x0F) + 2;
    
    // Decompress patch data
    const patchData = new Int32Array(patchSize * patchSize);
    
    // Read quantized DCT coefficients
    for (let i = 0; i < patchSize * patchSize; i++) {
      const copyIdx = this.CopyMatrix16[i];
      if (copyIdx !== 0) {
        patchData[copyIdx] = bitBuffer.getBits(patch.WordBits);
        // Sign extend
        if (patchData[copyIdx] & (1 << (patch.WordBits - 1))) {
          patchData[copyIdx] |= (~0) << patch.WordBits;
        }
      } else {
        patchData[0] = 0; // DC component
      }
    }
    
    // Dequantize
    for (let i = 0; i < patchSize * patchSize; i++) {
      patchData[i] *= this.DequantizeTable16[i];
    }
    
    // Inverse DCT
    patch.heightMap = this.inverseDCT16(patchData);
    
    // Apply DC offset and range
    for (let i = 0; i < patch.heightMap.length; i++) {
      patch.heightMap[i] = patch.heightMap[i] * patch.Range + patch.DCOffset;
    }
    
    return patch;
  }
  
  /**
   * Perform inverse discrete cosine transform (IDCT)
   * @param {Int32Array} data - Quantized DCT coefficients
   * @returns {Float32Array} - Height values
   */
  inverseDCT16(data) {
    const result = new Float32Array(256);
    
    for (let y = 0; y < 16; y++) {
      for (let x = 0; x < 16; x++) {
        let sum = 0.0;
        
        for (let v = 0; v < 16; v++) {
          for (let u = 0; u < 16; u++) {
            const cu = (u === 0) ? this.OO_SQRT2 : 1.0;
            const cv = (v === 0) ? this.OO_SQRT2 : 1.0;
            
            sum += cu * cv * data[v * 16 + u] * 
                   this.CosineTable16[u * 16 + x] * 
                   this.CosineTable16[v * 16 + y];
          }
        }
        
        result[y * 16 + x] = sum / 16.0;
      }
    }
    
    return result;
  }
}

/**
 * Terrain manager for handling multiple patches
 */
class TerrainManager {
  constructor() {
    this.patches = new Map(); // Map of patch coordinates to TerrainPatch objects
    this.regionSize = 256; // Standard SL region size
    this.patchSize = 16; // Standard patch size
    this.waterHeight = 20.0; // Default water height
  }
  
  /**
   * Add or update a terrain patch
   * @param {number} x - X coordinate of patch
   * @param {number} y - Y coordinate of patch
   * @param {TerrainPatch} patch - The terrain patch data
   */
  setPatch(x, y, patch) {
    const key = `${x},${y}`;
    this.patches.set(key, patch);
  }
  
  /**
   * Get a terrain patch
   * @param {number} x - X coordinate of patch
   * @param {number} y - Y coordinate of patch
   * @returns {TerrainPatch|null} - The terrain patch or null
   */
  getPatch(x, y) {
    const key = `${x},${y}`;
    return this.patches.get(key) || null;
  }
  
  /**
   * Get height at a specific position
   * @param {number} x - X position in region
   * @param {number} y - Y position in region
   * @returns {number} - Height at position
   */
  getHeightAt(x, y) {
    const patchX = Math.floor(x / this.patchSize);
    const patchY = Math.floor(y / this.patchSize);
    const patch = this.getPatch(patchX, patchY);
    
    if (!patch) {
      return this.waterHeight; // Return water height if no patch data
    }
    
    const localX = Math.floor(x % this.patchSize);
    const localY = Math.floor(y % this.patchSize);
    
    return patch.heightMap[localY * this.patchSize + localX];
  }
  
  /**
   * Get interpolated height (bilinear interpolation)
   * @param {number} x - X position in region
   * @param {number} y - Y position in region
   * @returns {number} - Interpolated height
   */
  getHeightInterpolated(x, y) {
    const x0 = Math.floor(x);
    const y0 = Math.floor(y);
    const x1 = x0 + 1;
    const y1 = y0 + 1;
    
    const fx = x - x0;
    const fy = y - y0;
    
    const h00 = this.getHeightAt(x0, y0);
    const h10 = this.getHeightAt(x1, y0);
    const h01 = this.getHeightAt(x0, y1);
    const h11 = this.getHeightAt(x1, y1);
    
    // Bilinear interpolation
    const h0 = h00 * (1 - fx) + h10 * fx;
    const h1 = h01 * (1 - fx) + h11 * fx;
    
    return h0 * (1 - fy) + h1 * fy;
  }
  
  /**
   * Clear all terrain data
   */
  clear() {
    this.patches.clear();
  }
  
  /**
   * Set water height for region
   * @param {number} height - Water height
   */
  setWaterHeight(height) {
    this.waterHeight = height;
  }
}

/**
 * Simple bit buffer for reading terrain data
 */
class BitBuffer {
  constructor(buffer) {
    this.buffer = new Uint8Array(buffer);
    this.bytePos = 0;
    this.bitPos = 0;
  }
  
  /**
   * Read specified number of bits
   * @param {number} numBits - Number of bits to read
   * @returns {number} - Value read
   */
  getBits(numBits) {
    let result = 0;
    let bitsRead = 0;
    
    while (bitsRead < numBits) {
      const bitsAvailable = 8 - this.bitPos;
      const bitsToRead = Math.min(bitsAvailable, numBits - bitsRead);
      
      const mask = (1 << bitsToRead) - 1;
      const shift = bitsAvailable - bitsToRead;
      const bits = (this.buffer[this.bytePos] >> shift) & mask;
      
      result = (result << bitsToRead) | bits;
      
      this.bitPos += bitsToRead;
      if (this.bitPos >= 8) {
        this.bytePos++;
        this.bitPos = 0;
      }
      
      bitsRead += bitsToRead;
    }
    
    return result;
  }
  
  /**
   * Read a 32-bit float
   * @returns {number} - Float value
   */
  getFloat() {
    const bytes = new Uint8Array(4);
    for (let i = 0; i < 4; i++) {
      bytes[i] = this.getBits(8);
    }
    return new DataView(bytes.buffer).getFloat32(0, false); // Big-endian
  }
}

// Export classes
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { TerrainPatch, TerrainManager, BitBuffer };
}
