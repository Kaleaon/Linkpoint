export interface TextureCacheOptions<T = any> {
  capacity?: number;
  onEvict?: (item: T, key: string) => void;
}

export class TextureCache<T = any> {
  private capacity: number;
  private onEvict?: (item: T, key: string) => void;

  // Primary store: canonicalKey -> item
  private cache: Map<string, T> = new Map();

  // Alias lookup: key (or lowercase key) -> canonicalKey
  private keyToCanonical: Map<string, string> = new Map();

  // Reverse alias lookup: canonicalKey -> Set of alias keys
  private canonicalToKeys: Map<string, Set<string>> = new Map();

  // Array of canonical keys ordered from oldest (LRU, index 0) to newest (MRU, index end)
  private lruOrder: string[] = [];

  constructor(options: TextureCacheOptions<T> | number = 100) {
    if (typeof options === 'number') {
      this.capacity = options;
    } else {
      this.capacity = options.capacity ?? 100;
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
    const index = this.lruOrder.indexOf(canonical);
    if (index !== -1) {
      this.lruOrder.splice(index, 1);
    }
    this.lruOrder.push(canonical);
  }

  get(key: string): T | undefined {
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

  set(key: string, value: T): this {
    if (key == null) return this;
    const rawKey = String(key);
    const lowerKey = rawKey.toLowerCase();

    // Determine canonical ID from value or raw key
    const assetId = (value && (value as any).assetId != null) ? String((value as any).assetId) : rawKey;
    const existingCanonical = this.resolveCanonical(rawKey) || this.resolveCanonical(assetId) || (this.cache.has(assetId) ? assetId : undefined);
    const canonical = existingCanonical || assetId;

    this.cache.set(canonical, value);

    const aliases = this.canonicalToKeys.get(canonical) || new Set<string>();
    aliases.add(rawKey);
    aliases.add(lowerKey);
    aliases.add(canonical);
    aliases.add(canonical.toLowerCase());
    this.canonicalToKeys.set(canonical, aliases);

    for (const alias of aliases) {
      this.keyToCanonical.set(alias, canonical);
    }

    this.markMRU(canonical);
    this.evictIfOverCapacity();

    return this;
  }

  delete(key: string): boolean {
    const canonical = this.resolveCanonical(key);
    if (!canonical) return false;
    return this.removeCanonical(canonical);
  }

  private removeCanonical(canonical: string): boolean {
    const item = this.cache.get(canonical);
    if (!item && !this.cache.has(canonical)) {
      return false;
    }

    const aliases = this.canonicalToKeys.get(canonical);
    if (aliases) {
      for (const alias of aliases) {
        this.keyToCanonical.delete(alias);
      }
      this.canonicalToKeys.delete(canonical);
    }

    this.cache.delete(canonical);

    const lruIdx = this.lruOrder.indexOf(canonical);
    if (lruIdx !== -1) {
      this.lruOrder.splice(lruIdx, 1);
    }

    if (item !== undefined && this.onEvict) {
      this.onEvict(item, canonical);
    }

    return true;
  }

  private evictIfOverCapacity() {
    while (this.cache.size > this.capacity && this.lruOrder.length > 0) {
      const lruCanonical = this.lruOrder.shift();
      if (lruCanonical) {
        this.removeCanonical(lruCanonical);
      }
    }
  }

  clear(): void {
    this.cache.clear();
    this.keyToCanonical.clear();
    this.canonicalToKeys.clear();
    this.lruOrder = [];
  }

  values(): IterableIterator<T> {
    const items: T[] = [];
    for (const canonical of this.lruOrder) {
      const item = this.cache.get(canonical);
      if (item !== undefined) {
        items.push(item);
      }
    }
    return items[Symbol.iterator]();
  }

  keys(): IterableIterator<string> {
    return this.keyToCanonical.keys();
  }

  entries(): IterableIterator<[string, T]> {
    const result: [string, T][] = [];
    for (const [key, canonical] of this.keyToCanonical.entries()) {
      const item = this.cache.get(canonical);
      if (item !== undefined) {
        result.push([key, item]);
      }
    }
    return result[Symbol.iterator]();
  }

  forEach(callback: (value: T, key: string, map: TextureCache<T>) => void, thisArg?: any): void {
    for (const [key, canonical] of this.keyToCanonical.entries()) {
      const item = this.cache.get(canonical);
      if (item !== undefined) {
        callback.call(thisArg, item, key, this);
      }
    }
  }

  [Symbol.iterator](): IterableIterator<[string, T]> {
    return this.entries();
  }
}
