/**
 * Secure Storage API for Linkpoint PWA
 * Bridges Java plugin with browser encrypted storage
 */

class SecureStorage {
  constructor() {
    this.isCapacitor = typeof window.Capacitor !== 'undefined';
    this.plugin = this.isCapacitor ? window.Capacitor.Plugins.SecureStorage : null;
    this.encryptionKey = null;
    this.storagePrefix = 'linkpoint_secure_';
  }

  /**
   * Initialize secure storage
   */
  async init() {
    if (!this.plugin) {
      // Initialize encryption key for browser
      await this.initBrowserEncryption();
    }
  }

  /**
   * Store a value securely
   */
  async set(key, value) {
    if (this.plugin) {
      try {
        await this.plugin.set({ key, value: JSON.stringify(value) });
        return true;
      } catch (error) {
        throw new Error(`Failed to store value: ${error.message}`);
      }
    }

    // Browser fallback with basic encryption
    try {
      const encrypted = await this.encrypt(JSON.stringify(value));
      localStorage.setItem(this.storagePrefix + key, encrypted);
      return true;
    } catch (error) {
      throw new Error(`Failed to store value: ${error.message}`);
    }
  }

  /**
   * Retrieve a value securely
   */
  async get(key) {
    if (this.plugin) {
      try {
        const result = await this.plugin.get({ key });
        return JSON.parse(result.value);
      } catch (error) {
        if (error.message && error.message.includes('Key not found')) {
          return null;
        }
        throw new Error(`Failed to retrieve value: ${error.message}`);
      }
    }

    // Browser fallback
    try {
      const encrypted = localStorage.getItem(this.storagePrefix + key);
      if (encrypted === null) {
        return null;
      }
      
      const decrypted = await this.decrypt(encrypted);
      return JSON.parse(decrypted);
    } catch (error) {
      throw new Error(`Failed to retrieve value: ${error.message}`);
    }
  }

  /**
   * Remove a value
   */
  async remove(key) {
    if (this.plugin) {
      try {
        await this.plugin.remove({ key });
        return true;
      } catch (error) {
        throw new Error(`Failed to remove value: ${error.message}`);
      }
    }

    // Browser fallback
    localStorage.removeItem(this.storagePrefix + key);
    return true;
  }

  /**
   * Clear all stored values
   */
  async clear() {
    if (this.plugin) {
      try {
        await this.plugin.clear();
        return true;
      } catch (error) {
        throw new Error(`Failed to clear storage: ${error.message}`);
      }
    }

    // Browser fallback - remove all keys with prefix
    const keysToRemove = [];
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (key && key.startsWith(this.storagePrefix)) {
        keysToRemove.push(key);
      }
    }
    
    keysToRemove.forEach(key => localStorage.removeItem(key));
    return true;
  }

  /**
   * Get all keys
   */
  async keys() {
    if (this.plugin) {
      try {
        const result = await this.plugin.keys();
        return result.keys;
      } catch (error) {
        throw new Error(`Failed to get keys: ${error.message}`);
      }
    }

    // Browser fallback
    const keys = [];
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (key && key.startsWith(this.storagePrefix)) {
        keys.push(key.substring(this.storagePrefix.length));
      }
    }
    
    return keys;
  }

  /**
   * Check if a key exists
   */
  async has(key) {
    if (this.plugin) {
      try {
        const result = await this.plugin.has({ key });
        return result.exists;
      } catch (error) {
        return false;
      }
    }

    // Browser fallback
    return localStorage.getItem(this.storagePrefix + key) !== null;
  }

  // Browser encryption methods (basic XOR cipher for demonstration)
  // In production, use Web Crypto API with proper key derivation

  async initBrowserEncryption() {
    // Get or create encryption key
    let keyData = localStorage.getItem('linkpoint_encryption_key');
    
    if (!keyData) {
      // Generate random key
      const keyArray = new Uint8Array(32);
      crypto.getRandomValues(keyArray);
      keyData = Array.from(keyArray).map(b => b.toString(16).padStart(2, '0')).join('');
      localStorage.setItem('linkpoint_encryption_key', keyData);
    }
    
    this.encryptionKey = keyData;

    // Try to use Web Crypto API if available
    if (window.crypto && window.crypto.subtle) {
      try {
        const keyBuffer = this.hexToArrayBuffer(keyData);
        this.cryptoKey = await window.crypto.subtle.importKey(
          'raw',
          keyBuffer,
          { name: 'AES-GCM' },
          false,
          ['encrypt', 'decrypt']
        );
      } catch (error) {
        console.warn('Web Crypto API not available, using fallback encryption');
      }
    }
  }

  async encrypt(plaintext) {
    // Use Web Crypto API if available
    if (this.cryptoKey) {
      try {
        const encoder = new TextEncoder();
        const data = encoder.encode(plaintext);
        const iv = window.crypto.getRandomValues(new Uint8Array(12));
        
        const encrypted = await window.crypto.subtle.encrypt(
          { name: 'AES-GCM', iv },
          this.cryptoKey,
          data
        );

        // Combine IV and encrypted data
        const combined = new Uint8Array(iv.length + encrypted.byteLength);
        combined.set(iv, 0);
        combined.set(new Uint8Array(encrypted), iv.length);

        return this.arrayBufferToBase64(combined.buffer);
      } catch (error) {
        console.warn('Web Crypto encryption failed, using fallback');
      }
    }

    // Fallback: Simple XOR cipher (not cryptographically secure!)
    return this.xorEncrypt(plaintext);
  }

  async decrypt(encrypted) {
    // Use Web Crypto API if available
    if (this.cryptoKey && encrypted.length > 16) {
      try {
        const combined = this.base64ToArrayBuffer(encrypted);
        const iv = combined.slice(0, 12);
        const data = combined.slice(12);

        const decrypted = await window.crypto.subtle.decrypt(
          { name: 'AES-GCM', iv },
          this.cryptoKey,
          data
        );

        const decoder = new TextDecoder();
        return decoder.decode(decrypted);
      } catch (error) {
        console.warn('Web Crypto decryption failed, trying fallback');
      }
    }

    // Fallback: Simple XOR cipher
    return this.xorDecrypt(encrypted);
  }

  // Simple XOR encryption (NOT secure, just obfuscation)
  xorEncrypt(plaintext) {
    const key = this.encryptionKey || 'default_key';
    let result = '';
    
    for (let i = 0; i < plaintext.length; i++) {
      const charCode = plaintext.charCodeAt(i) ^ key.charCodeAt(i % key.length);
      result += String.fromCharCode(charCode);
    }
    
    return btoa(result);
  }

  xorDecrypt(encrypted) {
    const decoded = atob(encrypted);
    const key = this.encryptionKey || 'default_key';
    let result = '';
    
    for (let i = 0; i < decoded.length; i++) {
      const charCode = decoded.charCodeAt(i) ^ key.charCodeAt(i % key.length);
      result += String.fromCharCode(charCode);
    }
    
    return result;
  }

  // Utility methods

  arrayBufferToBase64(buffer) {
    const bytes = new Uint8Array(buffer);
    let binary = '';
    for (let i = 0; i < bytes.byteLength; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    return btoa(binary);
  }

  base64ToArrayBuffer(base64) {
    const binary = atob(base64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) {
      bytes[i] = binary.charCodeAt(i);
    }
    return bytes.buffer;
  }

  hexToArrayBuffer(hex) {
    const bytes = new Uint8Array(hex.length / 2);
    for (let i = 0; i < hex.length; i += 2) {
      bytes[i / 2] = parseInt(hex.substr(i, 2), 16);
    }
    return bytes.buffer;
  }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = SecureStorage;
}

// Make available globally
window.SecureStorage = SecureStorage;
