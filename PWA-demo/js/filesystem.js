/**
 * File System API for Linkpoint PWA
 * Bridges Java plugin with browser File System Access API
 */

class FileSystem {
  constructor() {
    this.isCapacitor = typeof window.Capacitor !== 'undefined';
    this.plugin = this.isCapacitor ? window.Capacitor.Plugins.FileSystem : null;
    this.fileHandles = new Map();
    this.directoryHandles = new Map();
  }

  /**
   * Write text to a file
   */
  async writeFile(path, data, options = {}) {
    const directory = options.directory || 'data';
    const append = options.append || false;

    // Use plugin if available (Android/iOS)
    if (this.plugin) {
      try {
        const result = await this.plugin.writeFile({
          path,
          data,
          directory,
          append
        });
        return result;
      } catch (error) {
        throw new Error(`Failed to write file: ${error.message}`);
      }
    }

    // Fall back to browser File System Access API or IndexedDB
    if ('showSaveFilePicker' in window) {
      try {
        const fileHandle = await window.showSaveFilePicker({
          suggestedName: path.split('/').pop()
        });
        const writable = await fileHandle.createWritable();
        await writable.write(data);
        await writable.close();
        return { success: true, path };
      } catch (error) {
        throw new Error(`Failed to write file: ${error.message}`);
      }
    }

    // Fall back to localStorage for small files
    try {
      const storageKey = `fs_${directory}_${path}`;
      localStorage.setItem(storageKey, data);
      return { success: true, path };
    } catch (error) {
      throw new Error(`Failed to write file: ${error.message}`);
    }
  }

  /**
   * Read text from a file
   */
  async readFile(path, options = {}) {
    const directory = options.directory || 'data';

    // Use plugin if available
    if (this.plugin) {
      try {
        const result = await this.plugin.readFile({ path, directory });
        return result.data;
      } catch (error) {
        throw new Error(`Failed to read file: ${error.message}`);
      }
    }

    // Fall back to browser File System Access API
    if ('showOpenFilePicker' in window && options.picker) {
      try {
        const [fileHandle] = await window.showOpenFilePicker();
        const file = await fileHandle.getFile();
        const text = await file.text();
        return text;
      } catch (error) {
        throw new Error(`Failed to read file: ${error.message}`);
      }
    }

    // Fall back to localStorage
    try {
      const storageKey = `fs_${directory}_${path}`;
      const data = localStorage.getItem(storageKey);
      if (data === null) {
        throw new Error('File does not exist');
      }
      return data;
    } catch (error) {
      throw new Error(`Failed to read file: ${error.message}`);
    }
  }

  /**
   * Write binary data to a file
   */
  async writeBinary(path, data, options = {}) {
    const directory = options.directory || 'data';
    
    // Convert ArrayBuffer/Uint8Array to base64 for plugin
    let base64Data;
    if (data instanceof ArrayBuffer) {
      base64Data = this.arrayBufferToBase64(data);
    } else if (data instanceof Uint8Array) {
      base64Data = this.arrayBufferToBase64(data.buffer);
    } else {
      base64Data = data; // Assume already base64
    }

    if (this.plugin) {
      try {
        const result = await this.plugin.writeBinary({
          path,
          data: base64Data,
          directory
        });
        return result;
      } catch (error) {
        throw new Error(`Failed to write binary file: ${error.message}`);
      }
    }

    // Browser fallback using File System Access API
    if ('showSaveFilePicker' in window) {
      try {
        const fileHandle = await window.showSaveFilePicker({
          suggestedName: path.split('/').pop()
        });
        const writable = await fileHandle.createWritable();
        const blob = new Blob([this.base64ToArrayBuffer(base64Data)]);
        await writable.write(blob);
        await writable.close();
        return { success: true, path };
      } catch (error) {
        throw new Error(`Failed to write binary file: ${error.message}`);
      }
    }

    throw new Error('Binary file operations not supported in this environment');
  }

  /**
   * Read binary data from a file
   */
  async readBinary(path, options = {}) {
    const directory = options.directory || 'data';

    if (this.plugin) {
      try {
        const result = await this.plugin.readBinary({ path, directory });
        return this.base64ToArrayBuffer(result.data);
      } catch (error) {
        throw new Error(`Failed to read binary file: ${error.message}`);
      }
    }

    // Browser fallback
    if ('showOpenFilePicker' in window && options.picker) {
      try {
        const [fileHandle] = await window.showOpenFilePicker();
        const file = await fileHandle.getFile();
        const arrayBuffer = await file.arrayBuffer();
        return arrayBuffer;
      } catch (error) {
        throw new Error(`Failed to read binary file: ${error.message}`);
      }
    }

    throw new Error('Binary file operations not supported in this environment');
  }

  /**
   * Delete a file
   */
  async deleteFile(path, options = {}) {
    const directory = options.directory || 'data';

    if (this.plugin) {
      try {
        const result = await this.plugin.deleteFile({ path, directory });
        return result.success;
      } catch (error) {
        throw new Error(`Failed to delete file: ${error.message}`);
      }
    }

    // Browser fallback using localStorage
    const storageKey = `fs_${directory}_${path}`;
    localStorage.removeItem(storageKey);
    return true;
  }

  /**
   * Check if file exists
   */
  async exists(path, options = {}) {
    const directory = options.directory || 'data';

    if (this.plugin) {
      try {
        const result = await this.plugin.exists({ path, directory });
        return result.exists;
      } catch (error) {
        return false;
      }
    }

    // Browser fallback
    const storageKey = `fs_${directory}_${path}`;
    return localStorage.getItem(storageKey) !== null;
  }

  /**
   * Create a directory
   */
  async mkdir(path, options = {}) {
    const directory = options.directory || 'data';
    const recursive = options.recursive !== false;

    if (this.plugin) {
      try {
        const result = await this.plugin.mkdir({ path, directory, recursive });
        return result;
      } catch (error) {
        throw new Error(`Failed to create directory: ${error.message}`);
      }
    }

    // Browser fallback - no-op for virtual filesystem
    return { success: true, path };
  }

  /**
   * List files in a directory
   */
  async readDir(path, options = {}) {
    const directory = options.directory || 'data';

    if (this.plugin) {
      try {
        const result = await this.plugin.readDir({ path, directory });
        return result.files;
      } catch (error) {
        throw new Error(`Failed to read directory: ${error.message}`);
      }
    }

    // Browser fallback - scan localStorage for matching paths
    const prefix = `fs_${directory}_${path}`;
    const files = [];
    
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (key && key.startsWith(prefix)) {
        const relativePath = key.substring(prefix.length);
        files.push({
          name: relativePath.split('/').pop(),
          path: key,
          isFile: true,
          isDirectory: false,
          size: localStorage.getItem(key)?.length || 0
        });
      }
    }

    return files;
  }

  /**
   * Get file statistics
   */
  async stat(path, options = {}) {
    const directory = options.directory || 'data';

    if (this.plugin) {
      try {
        const result = await this.plugin.stat({ path, directory });
        return result;
      } catch (error) {
        throw new Error(`Failed to stat file: ${error.message}`);
      }
    }

    // Browser fallback
    const storageKey = `fs_${directory}_${path}`;
    const data = localStorage.getItem(storageKey);
    
    if (data === null) {
      throw new Error('File does not exist');
    }

    return {
      name: path.split('/').pop(),
      path,
      isFile: true,
      isDirectory: false,
      size: data.length,
      modified: new Date().toISOString()
    };
  }

  /**
   * Get available storage paths
   */
  async getStoragePaths() {
    if (this.plugin) {
      try {
        const result = await this.plugin.getStoragePaths();
        return result;
      } catch (error) {
        console.error('Failed to get storage paths:', error);
      }
    }

    // Browser fallback
    return {
      dataDirectory: 'virtual://data',
      cacheDirectory: 'virtual://cache',
      externalDirectory: 'virtual://external'
    };
  }

  // Utility methods

  arrayBufferToBase64(buffer) {
    const bytes = new Uint8Array(buffer);
    let binary = '';
    for (let i = 0; i < bytes.byteLength; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    return window.btoa(binary);
  }

  base64ToArrayBuffer(base64) {
    const binary = window.atob(base64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) {
      bytes[i] = binary.charCodeAt(i);
    }
    return bytes.buffer;
  }

  getVibrationPattern(type) {
    const patterns = {
      'messages': [200, 100, 200],
      'friends': [200],
      'groups': [100, 100, 100, 100, 100],
      'default': [300]
    };

    return patterns[type] || patterns.default;
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = FileSystem;
}

// Make available globally
window.FileSystem = FileSystem;
