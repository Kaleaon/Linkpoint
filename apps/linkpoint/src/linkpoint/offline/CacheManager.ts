import { Utils } from '../utils';
import { gridConsole, GridConsole, LOG_COMPONENTS } from './GridConsole';

export interface CacheSettings {
  maxSizeBytes: number;
  autoClearOnLimit: boolean;
}

export const CACHE_SETTINGS_STORAGE_KEY = 'linkpoint_cache_settings';

export const CACHE_SIZE_MIN_BYTES = 256 * 1024 * 1024;
export const CACHE_SIZE_MAX_BYTES = 1024 * 1024 * 1024 * 1024;
export const CACHE_SIZE_DEFAULT_BYTES = 1024 * 1024 * 1024;

export class CacheManager extends Utils.EventEmitter {
  private settings: CacheSettings;
  private console: GridConsole;

  constructor(console: GridConsole = gridConsole) {
    super();
    this.console = console;
    this.settings = this.loadSettings();
  }

  public getConsole(): GridConsole {
    return this.console;
  }

  private loadSettings(): CacheSettings {
    const saved = Utils.storage.get(CACHE_SETTINGS_STORAGE_KEY, null);
    if (saved && saved.maxSizeBytes) {
      return saved;
    }
    return {
      maxSizeBytes: CACHE_SIZE_DEFAULT_BYTES,
      autoClearOnLimit: false
    };
  }

  public setMaxSizeBytes(bytes: number): CacheSettings {
    const requested = Number.isFinite(bytes) ? bytes : CACHE_SIZE_DEFAULT_BYTES;
    const clampedBytes = Utils.clamp(requested, CACHE_SIZE_MIN_BYTES, CACHE_SIZE_MAX_BYTES);
    if (clampedBytes !== requested) {
      this.console.warn(
        LOG_COMPONENTS.CACHE,
        `Requested cache limit ${Utils.formatFileSize(requested)} is out of range; clamped to ${Utils.formatFileSize(clampedBytes)}.`
      );
    }

    this.settings.maxSizeBytes = clampedBytes;
    const persisted = Utils.storage.set(CACHE_SETTINGS_STORAGE_KEY, this.settings);
    if (!persisted) {
      this.console.warn(LOG_COMPONENTS.CACHE, 'Cache settings could not be saved; the change applies to this session only.');
    }

    this.console.info(LOG_COMPONENTS.CACHE, `Cache limit set to ${Utils.formatFileSize(clampedBytes)}.`);
    this.emit('cacheSettingsUpdated', this.settings);
    return this.settings;
  }

  public setMaxSizeMB(megabytes: number): CacheSettings {
    return this.setMaxSizeBytes(megabytes * 1024 * 1024);
  }

  public setMaxSizeGB(gigabytes: number): CacheSettings {
    return this.setMaxSizeBytes(gigabytes * 1024 * 1024 * 1024);
  }

  public getSettings(): CacheSettings {
    return { ...this.settings };
  }

  public getEstimatedUsageBytes(): number {
    let totalChars = 0;
    try {
      if (typeof localStorage !== 'undefined') {
        for (let i = 0; i < localStorage.length; i++) {
          const key = localStorage.key(i);
          if (key) {
            const value = localStorage.getItem(key) || '';
            totalChars += key.length + value.length;
          }
        }
      }
    } catch (e) {
      this.console.captureError(LOG_COMPONENTS.CACHE, 'Could not measure cache usage.', e);
    }
    return totalChars * 2;
  }

  public getUsagePercentage(): number {
    const usage = this.getEstimatedUsageBytes();
    if (this.settings.maxSizeBytes <= 0) return 0;
    return Math.min(100, Math.round((usage / this.settings.maxSizeBytes) * 10000) / 100);
  }

  public clearCache(): number {
    const usageBefore = this.getEstimatedUsageBytes();
    try {
      if (typeof localStorage !== 'undefined') {
        const keysToRemove: string[] = [];
        for (let i = 0; i < localStorage.length; i++) {
          const key = localStorage.key(i);
          if (key && (key.startsWith('linkpoint_local_asset_') || key.startsWith('linkpoint_cache_'))) {
            keysToRemove.push(key);
          }
        }
        for (const key of keysToRemove) {
          localStorage.removeItem(key);
        }
        this.console.info(
          LOG_COMPONENTS.CACHE,
          `Cleared ${keysToRemove.length} cached item(s), freeing about ${Utils.formatFileSize(usageBefore)}.`
        );
      }
      this.emit('cacheCleared', { freedBytes: usageBefore });
    } catch (e) {
      this.console.captureError(LOG_COMPONENTS.CACHE, 'Error clearing cache.', e);
    }
    return usageBefore;
  }
}
