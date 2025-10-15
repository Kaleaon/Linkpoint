/**
 * Linkpoint PWA - Windlight/Environment System
 * Ported from Android WindlightPreset.kt
 * Handles atmospheric rendering, lighting, and sky/water rendering
 */

class WindlightPreset {
  constructor(presetName = null) {
    this.WINDLIGHT_GAMMA = 2.2;
    
    // Default presets for time of day
    this.defaultPresets = [
      'A-12AM', 'A-3AM', 'A-6AM', 'A-9AM', 
      'A-12PM', 'A-3PM', 'A-6PM', 'A-9PM'
    ];
    
    this.hourTable = [0.0, 0.125, 0.25, 0.375, 0.5, 0.625, 0.75, 0.875];
    
    // Windlight parameters (RGBA arrays)
    this.ambient = new Float32Array(4);
    this.ambientBelowWater = new Float32Array(4);
    this.blue_density = new Float32Array(4);
    this.blue_horizon = new Float32Array(4);
    this.cloud_color = new Float32Array(4);
    this.cloud_pos_density1 = new Float32Array(4);
    this.cloud_pos_density2 = new Float32Array(4);
    this.cloud_shadow = new Float32Array(4);
    this.haze_density = new Float32Array(4);
    this.haze_horizon = new Float32Array(4);
    this.lightnorm = new Float32Array(4);
    this.sunlight_color = new Float32Array(4);
    this.sunlightBelowWater = new Float32Array(4);
    this.star_brightness = 0.0;
    
    this.reset();
    
    if (presetName) {
      this.loadPreset(presetName);
    }
  }
  
  /**
   * Reset to default values
   */
  reset() {
    // Ambient light
    this.setArray(this.ambient, [0.5, 0.5, 0.5, 1.0]);
    
    // Sky colors
    this.setArray(this.blue_density, [0.2447, 0.4487, 0.7599, 1.0]);
    this.setArray(this.blue_horizon, [0.4954, 0.4954, 0.64, 1.0]);
    this.setArray(this.haze_density, [0.7, 0.7, 0.7, 1.0]);
    this.setArray(this.haze_horizon, [0.19, 0.19, 0.19, 1.0]);
    
    // Sun/Moon
    this.setArray(this.sunlight_color, [0.7342, 0.7815, 0.8999, 1.0]);
    this.setArray(this.lightnorm, [0.0, 0.707, -0.707, 0.0]); // Sun at 45 degrees
    
    // Clouds
    this.setArray(this.cloud_color, [0.41, 0.41, 0.41, 1.0]);
    this.setArray(this.cloud_pos_density1, [1.0, 0.526, 1.0, 0.0]);
    this.setArray(this.cloud_pos_density2, [1.0, 0.526, 1.0, 0.0]);
    this.setArray(this.cloud_shadow, [0.27, 0.27, 0.27, 1.0]);
    
    // Stars
    this.star_brightness = 0.0;
    
    // Underwater
    this.darkenUnderWater(this.ambientBelowWater, this.ambient);
    this.darkenUnderWater(this.sunlightBelowWater, this.sunlight_color);
  }
  
  /**
   * Helper to set array values
   */
  setArray(arr, values) {
    for (let i = 0; i < Math.min(arr.length, values.length); i++) {
      arr[i] = values[i];
    }
  }
  
  /**
   * Darken colors for underwater rendering
   */
  darkenUnderWater(dest, src) {
    for (let i = 0; i < src.length; i++) {
      if (i === 2 || i === 3) { // Keep blue channel and alpha
        dest[i] = src[i];
      } else {
        dest[i] = src[i] / 2.0;
      }
    }
  }
  
  /**
   * Apply gamma correction to color array
   */
  gammaFloatArray(arr, gamma, multiplier) {
    for (let i = 0; i < arr.length; i++) {
      arr[i] = Math.pow(arr[i], 1.0 / gamma) * multiplier;
    }
  }
  
  /**
   * Linear interpolation between two float arrays
   */
  lerpFloatArray(dest, arr1, arr2, factor) {
    for (let i = 0; i < Math.min(dest.length, arr1.length, arr2.length); i++) {
      dest[i] = arr1[i] * (1.0 - factor) + arr2[i] * factor;
    }
  }
  
  /**
   * Load preset from data object
   * @param {Object} data - Windlight preset data
   */
  loadFromData(data) {
    if (data.ambient) this.getFloatArray(data.ambient, this.ambient, 3.0);
    if (data.sunlight_color) this.getFloatArray(data.sunlight_color, this.sunlight_color, 3.0);
    if (data.lightnorm) this.getFloatArray(data.lightnorm, this.lightnorm, 1.0);
    if (data.blue_density) this.getFloatArray(data.blue_density, this.blue_density, 2.0);
    if (data.blue_horizon) this.getFloatArray(data.blue_horizon, this.blue_horizon, 2.0);
    if (data.haze_density) this.getFloatArray(data.haze_density, this.haze_density, 5.0);
    if (data.haze_horizon) this.getFloatArray(data.haze_horizon, this.haze_horizon, 5.0);
    if (data.cloud_color) this.getFloatArray(data.cloud_color, this.cloud_color, 1.0);
    if (data.cloud_pos_density1) this.getFloatArray(data.cloud_pos_density1, this.cloud_pos_density1, 1.0);
    if (data.cloud_pos_density2) this.getFloatArray(data.cloud_pos_density2, this.cloud_pos_density2, 1.0);
    if (data.cloud_shadow) this.getFloatArray(data.cloud_shadow, this.cloud_shadow, 1.0);
    if (data.star_brightness !== undefined) this.star_brightness = data.star_brightness;
    
    // Apply gamma correction
    this.gammaFloatArray(this.ambient, this.WINDLIGHT_GAMMA, 3.0);
    this.gammaFloatArray(this.sunlight_color, this.WINDLIGHT_GAMMA, 3.0);
    this.gammaFloatArray(this.blue_density, this.WINDLIGHT_GAMMA, 2.0);
    this.gammaFloatArray(this.blue_horizon, this.WINDLIGHT_GAMMA, 2.0);
    this.gammaFloatArray(this.haze_density, this.WINDLIGHT_GAMMA, 2.0);
    this.gammaFloatArray(this.haze_horizon, this.WINDLIGHT_GAMMA, 2.0);
    
    // Update underwater colors
    this.darkenUnderWater(this.ambientBelowWater, this.ambient);
    this.darkenUnderWater(this.sunlightBelowWater, this.sunlight_color);
  }
  
  /**
   * Parse float array from data with divisor
   */
  getFloatArray(source, dest, divisor) {
    for (let i = 0; i < Math.min(dest.length, source.length); i++) {
      dest[i] = source[i] / divisor;
    }
  }
  
  /**
   * Load a preset by name
   * @param {string} presetName - Name of preset
   */
  loadPreset(presetName) {
    // For now, use default values
    // In a full implementation, this would load from a preset library
    console.log(`Loading windlight preset: ${presetName}`);
    this.reset();
  }
  
  /**
   * Get current sun direction
   * @returns {Object} - {x, y, z} normalized sun direction
   */
  getSunDirection() {
    return {
      x: this.lightnorm[0],
      y: this.lightnorm[1],
      z: this.lightnorm[2]
    };
  }
  
  /**
   * Set sun direction from time of day
   * @param {number} timeOfDay - Time in hours (0-24)
   */
  setSunDirectionFromTime(timeOfDay) {
    // Calculate sun angle based on time
    const hourAngle = (timeOfDay / 24.0) * Math.PI * 2.0;
    const sunAngle = hourAngle - Math.PI / 2.0; // Noon at 0
    
    this.lightnorm[0] = 0.0;
    this.lightnorm[1] = Math.sin(sunAngle);
    this.lightnorm[2] = -Math.cos(sunAngle);
    this.lightnorm[3] = 0.0;
    
    // Normalize
    const len = Math.sqrt(
      this.lightnorm[0] * this.lightnorm[0] +
      this.lightnorm[1] * this.lightnorm[1] +
      this.lightnorm[2] * this.lightnorm[2]
    );
    
    if (len > 0) {
      this.lightnorm[0] /= len;
      this.lightnorm[1] /= len;
      this.lightnorm[2] /= len;
    }
  }
}

/**
 * Windlight day cycle manager
 */
class WindlightDay {
  constructor() {
    this.presets = new Map();
    this.currentPreset = new WindlightPreset();
    this.timeOfDay = 12.0; // Noon
    this.dayLength = 14400.0; // 4 hours in seconds (SL default)
  }
  
  /**
   * Add a preset to the day cycle
   * @param {number} time - Time in day (0.0 - 1.0)
   * @param {WindlightPreset} preset - The preset
   */
  addPreset(time, preset) {
    this.presets.set(time, preset);
  }
  
  /**
   * Get interpolated preset for current time
   * @param {number} timeOfDay - Time in hours (0-24)
   * @returns {WindlightPreset} - Interpolated preset
   */
  getPresetAtTime(timeOfDay) {
    const normalizedTime = (timeOfDay % 24.0) / 24.0;
    
    // Find surrounding presets
    let prevTime = 0.0;
    let nextTime = 1.0;
    let prevPreset = null;
    let nextPreset = null;
    
    const times = Array.from(this.presets.keys()).sort((a, b) => a - b);
    
    for (let i = 0; i < times.length; i++) {
      if (times[i] <= normalizedTime) {
        prevTime = times[i];
        prevPreset = this.presets.get(times[i]);
      }
      if (times[i] > normalizedTime) {
        nextTime = times[i];
        nextPreset = this.presets.get(times[i]);
        break;
      }
    }
    
    // Wrap around if needed
    if (!nextPreset && times.length > 0) {
      nextTime = times[0] + 1.0;
      nextPreset = this.presets.get(times[0]);
    }
    
    if (!prevPreset) {
      prevPreset = nextPreset || new WindlightPreset();
    }
    if (!nextPreset) {
      nextPreset = prevPreset;
    }
    
    // Interpolate
    const factor = (normalizedTime - prevTime) / (nextTime - prevTime);
    const result = new WindlightPreset();
    
    result.lerpFloatArray(result.ambient, prevPreset.ambient, nextPreset.ambient, factor);
    result.lerpFloatArray(result.blue_density, prevPreset.blue_density, nextPreset.blue_density, factor);
    result.lerpFloatArray(result.blue_horizon, prevPreset.blue_horizon, nextPreset.blue_horizon, factor);
    result.lerpFloatArray(result.sunlight_color, prevPreset.sunlight_color, nextPreset.sunlight_color, factor);
    result.lerpFloatArray(result.haze_density, prevPreset.haze_density, nextPreset.haze_density, factor);
    result.lerpFloatArray(result.haze_horizon, prevPreset.haze_horizon, nextPreset.haze_horizon, factor);
    result.lerpFloatArray(result.cloud_color, prevPreset.cloud_color, nextPreset.cloud_color, factor);
    result.lerpFloatArray(result.lightnorm, prevPreset.lightnorm, nextPreset.lightnorm, factor);
    result.star_brightness = prevPreset.star_brightness * (1.0 - factor) + nextPreset.star_brightness * factor;
    
    return result;
  }
  
  /**
   * Update time of day
   * @param {number} deltaTime - Time elapsed in seconds
   */
  update(deltaTime) {
    this.timeOfDay += (deltaTime / this.dayLength) * 24.0;
    this.timeOfDay = this.timeOfDay % 24.0;
    
    this.currentPreset = this.getPresetAtTime(this.timeOfDay);
  }
  
  /**
   * Set time of day
   * @param {number} hours - Time in hours (0-24)
   */
  setTimeOfDay(hours) {
    this.timeOfDay = hours % 24.0;
    this.currentPreset = this.getPresetAtTime(this.timeOfDay);
  }
}

// Export classes
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { WindlightPreset, WindlightDay };
}
