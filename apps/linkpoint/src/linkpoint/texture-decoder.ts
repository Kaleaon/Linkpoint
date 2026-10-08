import { JpxImage } from 'jpeg2000';

export interface DecodedTextureResult {
  width: number;
  height: number;
  rgba: string;
  data: Uint8Array;
  assetId?: string;
}

export interface TextureCacheOptions {
  capacity?: number;
  onEvict?: (item: DecodedTextureResult, key: string) => void;
}

/**
 * In-memory LRU Texture Cache enforcing capacity limit (default 128).
 */
export class LRUTextureCache {
  private capacity: number;
  private cache = new Map<string, DecodedTextureResult>();
  private keyToCanonical = new Map<string, string>();
  private lruOrder: string[] = [];
  private onEvict?: (item: DecodedTextureResult, key: string) => void;

  constructor(options: TextureCacheOptions | number = 128) {
    if (typeof options === 'number') {
      this.capacity = options;
    } else {
      this.capacity = options.capacity ?? 128;
      this.onEvict = options.onEvict;
    }
  }

  get maxCapacity(): number {
    return this.capacity;
  }

  get size(): number {
    return this.cache.size;
  }

  setCapacity(newCapacity: number) {
    this.capacity = Math.max(1, newCapacity);
    this.evictIfOverCapacity();
  }

  private resolveCanonical(key: string): string | undefined {
    if (!key) return undefined;
    const raw = String(key);
    return this.keyToCanonical.get(raw) || this.keyToCanonical.get(raw.toLowerCase());
  }

  private markMRU(canonical: string) {
    const idx = this.lruOrder.indexOf(canonical);
    if (idx !== -1) {
      this.lruOrder.splice(idx, 1);
    }
    this.lruOrder.push(canonical);
  }

  get(key: string): DecodedTextureResult | undefined {
    const canonical = this.resolveCanonical(key);
    if (!canonical) return undefined;
    this.markMRU(canonical);
    return this.cache.get(canonical);
  }

  has(key: string): boolean {
    const canonical = this.resolveCanonical(key);
    if (!canonical) return false;
    this.markMRU(canonical);
    return this.cache.has(canonical);
  }

  set(key: string, value: DecodedTextureResult): this {
    if (!key) return this;
    const rawKey = String(key);
    const canonical = rawKey.toLowerCase();

    this.cache.set(canonical, value);
    this.keyToCanonical.set(rawKey, canonical);
    this.keyToCanonical.set(canonical, canonical);

    this.markMRU(canonical);
    this.evictIfOverCapacity();

    return this;
  }

  delete(key: string): boolean {
    const canonical = this.resolveCanonical(key);
    if (!canonical) return false;
    this.cache.delete(canonical);
    this.keyToCanonical.delete(canonical);
    const idx = this.lruOrder.indexOf(canonical);
    if (idx !== -1) {
      this.lruOrder.splice(idx, 1);
    }
    return true;
  }

  private evictIfOverCapacity() {
    while (this.cache.size > this.capacity && this.lruOrder.length > 0) {
      const lruKey = this.lruOrder.shift();
      if (lruKey) {
        const item = this.cache.get(lruKey);
        this.cache.delete(lruKey);
        this.keyToCanonical.delete(lruKey);
        if (item && this.onEvict) {
          this.onEvict(item, lruKey);
        }
      }
    }
  }

  clear(): void {
    this.cache.clear();
    this.keyToCanonical.clear();
    this.lruOrder = [];
  }
}

interface DecodeTask {
  id: number;
  buffer: ArrayBuffer;
  assetId?: string;
  resolve: (value: DecodedTextureResult) => void;
  reject: (reason?: any) => void;
}

interface ActiveWorker {
  worker: Worker;
  busy: boolean;
  currentTaskId?: number;
}

/**
 * Web Worker Decoder Pool for background JPEG2000 processing.
 */
export class TextureDecoderPool {
  private workers: ActiveWorker[] = [];
  private taskQueue: DecodeTask[] = [];
  private activeTasks = new Map<number, DecodeTask>();
  private nextTaskId = 1;
  private poolSize: number;
  private workerScriptUrl?: string;
  private isSupported: boolean;

  constructor(poolSize?: number, workerScriptUrl?: string) {
    const concurrency = typeof navigator !== 'undefined' && navigator.hardwareConcurrency
      ? navigator.hardwareConcurrency
      : 2;
    this.poolSize = poolSize ?? Math.min(4, Math.max(2, concurrency));
    this.workerScriptUrl = workerScriptUrl;
    this.isSupported = typeof Worker !== 'undefined';
  }

  public get size(): number {
    return this.workers.length;
  }

  public get pendingTasksCount(): number {
    return this.taskQueue.length;
  }

  public addWorker(worker: Worker) {
    const activeWorker: ActiveWorker = { worker, busy: false };
    worker.onmessage = (event: MessageEvent) => {
      this.handleWorkerMessage(activeWorker, event.data);
    };
    worker.onerror = (error: ErrorEvent) => {
      this.handleWorkerError(activeWorker, error);
    };
    this.workers.push(activeWorker);
  }

  public initializeWorkers() {
    if (!this.isSupported || this.workers.length > 0) return;

    for (let i = 0; i < this.poolSize; i++) {
      try {
        let worker: Worker;
        if (this.workerScriptUrl) {
          worker = new Worker(this.workerScriptUrl);
        } else {
          // Use inline Blob worker or URL import for Vite/WebWorker
          try {
            worker = new Worker(new URL('./texture-decoder.worker.ts', import.meta.url), { type: 'module' });
          } catch {
            // Fallback for environments where Module Workers aren't directly instantiated
            worker = new Worker('./texture-decoder.worker.js');
          }
        }

        this.addWorker(worker);
      } catch {
        // If worker creation fails, mark pool as unsupported so tasks fall back to synchronous
        this.isSupported = false;
        break;
      }
    }
  }

  private async handleWorkerMessage(activeWorker: ActiveWorker, data: any) {
    const { id, width, height, rgbaBuffer, error } = data;
    const task = this.activeTasks.get(id);

    activeWorker.busy = false;
    activeWorker.currentTaskId = undefined;

    if (task) {
      this.activeTasks.delete(id);
      if (error) {
        // Attempt async fallback if worker decode fails
        try {
          const fallbackResult = await decodePixelsAsync(task.buffer, task.assetId);
          task.resolve(fallbackResult);
        } catch (fallbackErr) {
          task.reject(new Error(`Worker decode failed (${error}) and fallback failed (${fallbackErr})`));
        }
      } else if (width && height && rgbaBuffer) {
        const dataArray = new Uint8Array(rgbaBuffer);
        const rgbaB64 = uint8ArrayToBase64(dataArray);
        const result: DecodedTextureResult = {
          width,
          height,
          rgba: rgbaB64,
          data: dataArray,
          assetId: task.assetId,
        };
        task.resolve(result);
      } else {
        task.reject(new Error('Invalid response payload from texture decoder worker'));
      }
    }

    this.processQueue();
  }

  private async handleWorkerError(activeWorker: ActiveWorker, errorEvent: ErrorEvent) {
    const taskId = activeWorker.currentTaskId;
    activeWorker.busy = false;
    activeWorker.currentTaskId = undefined;

    if (taskId) {
      const task = this.activeTasks.get(taskId);
      if (task) {
        this.activeTasks.delete(taskId);
        try {
          const fallbackResult = await decodePixelsAsync(task.buffer, task.assetId);
          task.resolve(fallbackResult);
        } catch {
          task.reject(new Error(`Worker error: ${errorEvent.message || 'Unknown worker error'}`));
        }
      }
    }

    // Replace or terminate worker if unrecoverable
    try {
      activeWorker.worker.terminate();
    } catch {}

    const index = this.workers.indexOf(activeWorker);
    if (index !== -1) {
      this.workers.splice(index, 1);
    }

    this.processQueue();
  }

  public decode(buffer: ArrayBuffer | Uint8Array | Buffer, assetId?: string): Promise<DecodedTextureResult> {
    const arrayBuffer = toArrayBuffer(buffer);

    // If workers are not supported or not available, use fallback decoding
    if (!this.isSupported) {
      return decodePixelsAsync(arrayBuffer, assetId);
    }

    if (this.workers.length === 0) {
      this.initializeWorkers();
    }

    if (!this.isSupported || this.workers.length === 0) {
      return decodePixelsAsync(arrayBuffer, assetId);
    }

    return new Promise<DecodedTextureResult>((resolve, reject) => {
      const id = this.nextTaskId++;
      const task: DecodeTask = {
        id,
        buffer: arrayBuffer,
        assetId,
        resolve,
        reject,
      };

      this.taskQueue.push(task);
      this.processQueue();
    });
  }

  private processQueue() {
    if (this.taskQueue.length === 0) return;

    const availableWorker = this.workers.find(w => !w.busy);
    if (!availableWorker) return;

    const task = this.taskQueue.shift();
    if (!task) return;

    availableWorker.busy = true;
    availableWorker.currentTaskId = task.id;
    this.activeTasks.set(task.id, task);

    // Copy array buffer slice for transfer so caller's original buffer is preserved
    const transferableBuffer = task.buffer.slice(0);

    try {
      availableWorker.worker.postMessage(
        { id: task.id, buffer: transferableBuffer },
        [transferableBuffer]
      );
    } catch {
      // If posting with transfer fails, post without transfer
      try {
        availableWorker.worker.postMessage({ id: task.id, buffer: task.buffer });
      } catch (postErr) {
        // If posting to worker fails, fall back to synchronous decode
        this.activeTasks.delete(task.id);
        availableWorker.busy = false;
        availableWorker.currentTaskId = undefined;
        try {
          const fallbackResult = decodeSync(task.buffer, task.assetId);
          task.resolve(fallbackResult);
        } catch (err) {
          task.reject(err);
        }
        this.processQueue();
      }
    }
  }

  public clearQueue() {
    for (const task of this.taskQueue) {
      try {
        const placeholder = createPlaceholderTexture(1, 1, task.assetId);
        task.resolve(placeholder);
      } catch {
        task.reject(new Error('Decode task cancelled due to queue reset'));
      }
    }
    this.taskQueue = [];
  }

  public terminate() {
    this.clearQueue();
    for (const activeWorker of this.workers) {
      try {
        activeWorker.worker.terminate();
      } catch {}
    }
    this.workers = [];
    this.activeTasks.clear();
  }
}

// Global LRU Cache and Worker Pool instances
const globalTextureLRUCache = new LRUTextureCache(128);
let globalDecoderPool: TextureDecoderPool | null = null;

export function getTextureLRUCache(): LRUTextureCache {
  return globalTextureLRUCache;
}

export function getWorkerPool(): TextureDecoderPool {
  if (!globalDecoderPool) {
    globalDecoderPool = new TextureDecoderPool();
  }
  return globalDecoderPool;
}

/**
 * Utility to convert buffer types to ArrayBuffer.
 */
function toArrayBuffer(buffer: ArrayBuffer | Uint8Array | Buffer): ArrayBuffer {
  if (buffer instanceof ArrayBuffer) {
    return buffer;
  }
  if (ArrayBuffer.isView(buffer)) {
    return buffer.buffer.slice(buffer.byteOffset, buffer.byteOffset + buffer.byteLength);
  }
  if (typeof Buffer !== 'undefined' && Buffer.isBuffer(buffer)) {
    const u8 = new Uint8Array(buffer);
    return u8.buffer.slice(u8.byteOffset, u8.byteOffset + u8.byteLength);
  }
  return new Uint8Array(buffer as any).buffer;
}

/**
 * Utility to convert Uint8Array to Base64 string.
 */
function uint8ArrayToBase64(bytes: Uint8Array): string {
  if (typeof Buffer !== 'undefined') {
    return Buffer.from(bytes.buffer, bytes.byteOffset, bytes.byteLength).toString('base64');
  }
  let binary = '';
  const len = bytes.byteLength;
  for (let i = 0; i < len; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return typeof btoa === 'function' ? btoa(binary) : '';
}

let sharpInstance: any = null;
function getSharp() {
  if (!sharpInstance) {
    try {
      sharpInstance = require('sharp');
    } catch {
      sharpInstance = null;
    }
  }
  return sharpInstance;
}

export async function decodePixelsAsync(
  buffer: ArrayBuffer | Uint8Array | Buffer,
  assetId?: string
): Promise<DecodedTextureResult> {
  const arrayBuf = toArrayBuffer(buffer);
  const nodeBuf = Buffer.from(arrayBuf);

  const sharp = getSharp();
  if (sharp) {
    try {
      const result = await sharp(nodeBuf, { failOn: 'error' }).ensureAlpha().raw().toBuffer({ resolveWithObject: true });
      const u8 = new Uint8Array(result.data);
      const rgbaB64 = uint8ArrayToBase64(u8);
      const res: DecodedTextureResult = {
        width: result.info.width,
        height: result.info.height,
        rgba: rgbaB64,
        data: u8,
        assetId,
      };
      if (assetId) {
        globalTextureLRUCache.set(assetId, res);
      }
      return res;
    } catch {
      // Fall through to decodeSync
    }
  }

  return decodeSync(buffer, assetId);
}

/**
 * Synchronous JPEG2000 parsing fallback using JpxImage.
 */
export function decodeSync(
  buffer: ArrayBuffer | Uint8Array | Buffer,
  assetId?: string
): DecodedTextureResult {
  const arrayBuf = toArrayBuffer(buffer);
  const nodeBuf = Buffer.from(arrayBuf);
  const image = new JpxImage();

  try {
    image.parse(nodeBuf);
  } catch {
    // If parsing fails, create 1x1 placeholder
    const placeholder = createPlaceholderTexture(1, 1, assetId);
    if (assetId) {
      globalTextureLRUCache.set(assetId, placeholder);
    }
    return placeholder;
  }

  const width = image.width || 1;
  const height = image.height || 1;
  const rgba = new Uint8Array(width * height * 4);
  rgba.fill(255);

  if (image.tiles) {
    for (const tile of image.tiles) {
      for (let y = 0; y < tile.height; y++) {
        for (let x = 0; x < tile.width; x++) {
          const source = (y * tile.width + x) * image.componentsCount;
          const target = ((tile.top + y) * width + tile.left + x) * 4;
          rgba[target] = tile.items[source];
          rgba[target + 1] = image.componentsCount > 1 ? tile.items[source + 1] : tile.items[source];
          rgba[target + 2] = image.componentsCount > 2 ? tile.items[source + 2] : tile.items[source];
          if (image.componentsCount > 3) rgba[target + 3] = tile.items[source + 3];
        }
      }
    }
  }

  const rgbaB64 = uint8ArrayToBase64(rgba);
  const result: DecodedTextureResult = {
    width,
    height,
    rgba: rgbaB64,
    data: rgba,
    assetId,
  };

  if (assetId) {
    globalTextureLRUCache.set(assetId, result);
  }

  return result;
}

/**
 * Creates a 1x1 RGBA placeholder texture.
 */
export function createPlaceholderTexture(
  width = 1,
  height = 1,
  assetId?: string
): DecodedTextureResult {
  const rgba = new Uint8Array(width * height * 4);
  rgba.fill(128); // Neutral grey
  const rgbaB64 = uint8ArrayToBase64(rgba);
  return {
    width,
    height,
    rgba: rgbaB64,
    data: rgba,
    assetId,
  };
}

/**
 * Primary entry point for JPEG2000 decoding with LRU texture cache.
 *
 * Checks LRU cache first. If present, immediately returns cached result.
 * Otherwise, offloads decoding to worker pool or falls back synchronously.
 */
export async function decodeJPEG2000(
  buffer: ArrayBuffer | Uint8Array | Buffer,
  assetId?: string
): Promise<DecodedTextureResult> {
  if (assetId) {
    const cached = globalTextureLRUCache.get(assetId);
    if (cached) {
      return cached;
    }
  }

  const pool = getWorkerPool();
  const result = await pool.decode(buffer, assetId);

  if (assetId) {
    globalTextureLRUCache.set(assetId, result);
  }

  return result;
}

export function clearTextureCache(): void {
  globalTextureLRUCache.clear();
}

export function terminateWorkerPool(): void {
  if (globalDecoderPool) {
    globalDecoderPool.terminate();
    globalDecoderPool = null;
  }
}
