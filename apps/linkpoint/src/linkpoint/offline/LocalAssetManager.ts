import { Utils } from '../utils';
import { gridConsole, GridConsole, LOG_COMPONENTS } from './GridConsole';

export type AssetType = 'texture' | 'sound' | 'script' | 'mesh' | 'animation' | 'bodypart' | 'clothing';

export interface LocalAsset {
  id: string;
  name: string;
  description: string;
  assetType: AssetType;
  creatorId: string;
  ownerId: string;
  data: string | Uint8Array;
  sizeBytes: number;
  uploadedAt: number;
  isUploadedToSL: boolean;
}

export const ASSET_STORAGE_PREFIX = 'linkpoint_local_asset_';

export class LocalAssetManager extends Utils.EventEmitter {
  private assets: Map<string, LocalAsset> = new Map();
  private console: GridConsole;

  constructor(console: GridConsole = gridConsole) {
    super();
    this.console = console;
    this.loadPersistedAssets();
  }

  public getConsole(): GridConsole {
    return this.console;
  }

  private loadPersistedAssets() {
    try {
      if (typeof localStorage !== 'undefined') {
        const keys = Object.keys(localStorage).filter(k => k.startsWith(ASSET_STORAGE_PREFIX));
        let skipped = 0;
        for (const key of keys) {
          const item = Utils.storage.get(key, null);
          if (item && item.id) {
            this.assets.set(item.id, item);
          } else {
            skipped++;
          }
        }
        if (keys.length > 0) {
          this.console.info(
            LOG_COMPONENTS.ASSET,
            `Loaded ${this.assets.size} local asset(s) from storage.` +
              (skipped > 0 ? ` Skipped ${skipped} unreadable entr(ies).` : '')
          );
        }
      }
    } catch (e) {
      this.console.captureError(LOG_COMPONENTS.ASSET, 'Failed to load local assets from storage.', e);
    }
  }

  public uploadAsset(
    name: string,
    assetType: AssetType,
    data: string | Uint8Array,
    description: string = '',
    ownerId: string = 'local_user'
  ): LocalAsset {
    const assetId = Utils.generateUUID();
    const sizeBytes = typeof data === 'string' ? data.length : data.byteLength;

    const asset: LocalAsset = {
      id: assetId,
      name,
      description,
      assetType,
      creatorId: ownerId,
      ownerId,
      data,
      sizeBytes,
      uploadedAt: Date.now(),
      isUploadedToSL: false
    };

    this.assets.set(assetId, asset);
    const persisted = Utils.storage.set(`${ASSET_STORAGE_PREFIX}${assetId}`, asset);
    if (persisted) {
      this.console.info(
        LOG_COMPONENTS.ASSET,
        `Stored ${assetType} asset "${name}" (${Utils.formatFileSize(sizeBytes)})`,
        `asset_id=${assetId}`
      );
    } else {
      this.console.error(
        LOG_COMPONENTS.ASSET,
        `Asset "${name}" was accepted but could not be written to storage; it will be lost on restart.`,
        `asset_id=${assetId} size=${sizeBytes} bytes. Local storage may be full or unavailable.`
      );
    }

    this.emit('assetUploaded', asset);
    return asset;
  }

  public getAsset(assetId: string): LocalAsset | undefined {
    return this.assets.get(assetId);
  }

  public getAllAssets(): LocalAsset[] {
    return Array.from(this.assets.values());
  }

  public deleteAsset(assetId: string): boolean {
    const asset = this.assets.get(assetId);
    const existed = this.assets.delete(assetId);
    if (existed) {
      Utils.storage.remove(`${ASSET_STORAGE_PREFIX}${assetId}`);
      this.console.info(LOG_COMPONENTS.ASSET, `Deleted asset "${asset?.name ?? assetId}"`, `asset_id=${assetId}`);
      this.emit('assetDeleted', assetId);
    } else {
      this.console.warn(LOG_COMPONENTS.ASSET, `Delete requested for unknown asset ${assetId}.`);
    }
    return existed;
  }

  /** Drop in-memory assets after their backing storage has been cleared. */
  public resetLoadedAssets() {
    this.assets.clear();
    this.emit('assetsReset', undefined);
  }

  public getTotalAssetsSizeBytes(): number {
    let total = 0;
    for (const asset of this.assets.values()) {
      total += asset.sizeBytes;
    }
    return total;
  }
}
