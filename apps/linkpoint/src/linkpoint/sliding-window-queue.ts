/**
 * Linkpoint - Asynchronous Sliding-Window Synchronization Queue
 *
 * Limits concurrent network HTTP requests, supports grid-aware throttling
 * (Second Life vs OpenSim), metered cellular vs unmetered Wi-Fi limits,
 * dynamic backoff on 503/timeouts, frustum & avatar texture priority,
 * and battery throttling.
 */

import { Utils } from './utils';

export type TaskPriority = 'HIGH' | 'LOW';
export type TaskStatus = 'queued' | 'running' | 'completed' | 'failed';
export type GridKind = 'secondlife' | 'opensim';
export type NetworkType = 'unmetered' | 'metered';

export interface SyncTask {
  id: string;
  folderId: string;
  priority: TaskPriority;
  isAvatarTexture?: boolean;
  inFrustum?: boolean;
  distance?: number;
  execute: () => Promise<any>;
  addedAt: number;
  status: TaskStatus;
  retries: number;
}

export interface BatteryStatus {
  level: number; // 0.0 to 1.0
  isCharging: boolean;
}

export class SlidingWindowSyncQueue extends Utils.EventEmitter {
  public static readonly DEFAULT_MAX_CONCURRENCY = 3;
  public static readonly THROTTLED_MAX_CONCURRENCY = 1;
  public static readonly BATTERY_THROTTLE_THRESHOLD = 0.15; // 15%

  private queue: SyncTask[] = [];
  private activeTasks: Map<string, SyncTask> = new Map();
  private maxConcurrency: number = SlidingWindowSyncQueue.DEFAULT_MAX_CONCURRENCY;
  private gridKind: GridKind = 'secondlife';
  private networkType: NetworkType = 'unmetered';
  private isGridConfigured: boolean = false;

  private consecutiveErrors: number = 0;
  private dynamicLimitOffset: number = 0;
  private minConcurrency: number = 1;

  private batteryStatus: BatteryStatus = { level: 1.0, isCharging: false };
  private isThrottled: boolean = false;
  private visibleFolderIds: Set<string> = new Set();
  private isProcessing: boolean = false;

  constructor() {
    super();
    this.initBatteryMonitoring().catch(() => {});
  }

  /**
   * Initializes battery monitoring via Web Battery API if supported.
   */
  private async initBatteryMonitoring(): Promise<void> {
    if (typeof window === 'undefined' || !('getBattery' in navigator)) return;

    try {
      const battery: any = await (navigator as any).getBattery();
      this.updateBatteryStatus(battery.level, battery.charging);

      battery.addEventListener('levelchange', () => {
        this.updateBatteryStatus(battery.level, battery.charging);
      });
      battery.addEventListener('chargingchange', () => {
        this.updateBatteryStatus(battery.level, battery.charging);
      });
    } catch {
      // Battery API not permitted or available
    }
  }

  /**
   * Manually sets battery status (for testing or native mobile bridge).
   */
  public setBatteryStatus(level: number, isCharging = false): void {
    this.updateBatteryStatus(level, isCharging);
  }

  private updateBatteryStatus(level: number, isCharging: boolean): void {
    this.batteryStatus = { level, isCharging };
    const shouldThrottle = level <= SlidingWindowSyncQueue.BATTERY_THROTTLE_THRESHOLD && !isCharging;

    if (shouldThrottle !== this.isThrottled) {
      this.isThrottled = shouldThrottle;
      this.recalculateMaxConcurrency();

      console.warn(
        `[SlidingWindowQueue] Battery ${Math.round(level * 100)}% (${isCharging ? 'charging' : 'discharging'}). ` +
        `Background sync throttling ${shouldThrottle ? 'ACTIVATED (max 1 stream)' : 'DEACTIVATED'}.`
      );

      this.emit('battery_throttled', {
        throttled: shouldThrottle,
        batteryLevel: level,
        maxConcurrency: this.maxConcurrency,
      });

      this.processQueue();
    }
  }

  /**
   * Configure active grid kind ('secondlife' | 'opensim').
   */
  public setGridKind(grid: GridKind): void {
    this.gridKind = grid;
    this.isGridConfigured = true;
    this.recalculateMaxConcurrency();
  }

  public getGridKind(): GridKind {
    return this.gridKind;
  }

  /**
   * Configure network connection type ('unmetered' | 'metered').
   */
  public setNetworkType(network: NetworkType): void {
    this.networkType = network;
    this.recalculateMaxConcurrency();
  }

  public getNetworkType(): NetworkType {
    return this.networkType;
  }

  public getBaseConcurrencyLimit(): number {
    if (!this.isGridConfigured) {
      return SlidingWindowSyncQueue.DEFAULT_MAX_CONCURRENCY;
    }
    if (this.gridKind === 'opensim') {
      return this.networkType === 'metered' ? 4 : 10;
    } else {
      return this.networkType === 'metered' ? 16 : 64;
    }
  }

  public recalculateMaxConcurrency(): void {
    if (this.isThrottled) {
      this.maxConcurrency = SlidingWindowSyncQueue.THROTTLED_MAX_CONCURRENCY;
      return;
    }
    const base = this.getBaseConcurrencyLimit();
    this.maxConcurrency = Math.max(this.minConcurrency, base - this.dynamicLimitOffset);
  }

  /**
   * Record HTTP 503 error, server error, or timeout to trigger dynamic backoff.
   */
  public recordError(statusCode?: number): void {
    if (!statusCode || statusCode === 503 || statusCode === 504 || statusCode === 408) {
      this.applyBackoff();
    }
  }

  public recordTimeout(): void {
    this.applyBackoff();
  }

  public recordResponseTime(latencyMs: number): void {
    if (latencyMs > 2000) {
      this.applyBackoff();
    } else if (latencyMs < 500) {
      this.recordSuccess();
    }
  }

  public recordSuccess(): void {
    this.consecutiveErrors = 0;
    if (this.dynamicLimitOffset > 0) {
      this.dynamicLimitOffset = Math.max(0, this.dynamicLimitOffset - 1);
      this.recalculateMaxConcurrency();
    }
  }

  private applyBackoff(): void {
    this.consecutiveErrors++;
    const current = this.maxConcurrency;
    const next = Math.max(this.minConcurrency, Math.floor(current / 2));
    const base = this.getBaseConcurrencyLimit();
    this.dynamicLimitOffset = Math.max(0, base - next);
    this.recalculateMaxConcurrency();

    this.emit('backoff_triggered', {
      consecutiveErrors: this.consecutiveErrors,
      maxConcurrency: this.maxConcurrency,
    });
  }

  /**
   * Updates the set of currently visible viewport folder IDs.
   * Promotes queued tasks matching visible folders to HIGH priority.
   */
  public updateViewportFolders(folderIds: string[]): void {
    this.visibleFolderIds = new Set(folderIds);

    let promotedCount = 0;
    for (const task of this.queue) {
      if (this.visibleFolderIds.has(task.folderId) && task.priority !== 'HIGH') {
        task.priority = 'HIGH';
        promotedCount++;
      }
    }

    if (promotedCount > 0) {
      this.sortQueue();
      console.log(`[SlidingWindowQueue] Promoted ${promotedCount} viewport folder tasks to HIGH priority.`);
      this.emit('priority_promoted', { folderIds, promotedCount });
      this.processQueue();
    }
  }

  /**
   * Enqueues a folder sync or asset download task into the queue.
   */
  public enqueue(
    folderId: string,
    executeFn: () => Promise<any>,
    initialPriority?: TaskPriority,
    options?: { isAvatarTexture?: boolean; inFrustum?: boolean; distance?: number }
  ): Promise<any> {
    return new Promise((resolve, reject) => {
      // Check if task is already running or queued
      if (this.activeTasks.has(folderId)) {
        return resolve(null);
      }

      const existingInQueue = this.queue.find((t) => t.folderId === folderId);
      if (existingInQueue) {
        if (options?.isAvatarTexture) existingInQueue.isAvatarTexture = true;
        if (options?.inFrustum) existingInQueue.inFrustum = true;
        if (options?.distance !== undefined) existingInQueue.distance = Math.min(existingInQueue.distance ?? Infinity, options.distance);
        if (initialPriority === 'HIGH' || this.visibleFolderIds.has(folderId)) {
          existingInQueue.priority = 'HIGH';
        }
        this.sortQueue();
        return resolve(null);
      }

      const isVisible = options?.inFrustum !== undefined ? options.inFrustum : this.visibleFolderIds.has(folderId);
      const priority: TaskPriority = initialPriority || (isVisible || options?.isAvatarTexture ? 'HIGH' : 'LOW');

      const task: SyncTask = {
        id: `sync_${folderId}_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`,
        folderId,
        priority,
        isAvatarTexture: options?.isAvatarTexture ?? false,
        inFrustum: isVisible,
        distance: options?.distance ?? 0,
        execute: executeFn,
        addedAt: Date.now(),
        status: 'queued',
        retries: 0,
      };

      // Wrap task execution to settle outer Promise and track dynamic response times
      const originalExecute = task.execute;
      task.execute = async () => {
        const startTime = Date.now();
        try {
          const result = await originalExecute();
          this.recordResponseTime(Date.now() - startTime);
          resolve(result);
          return result;
        } catch (err: any) {
          const statusCode = err?.status || err?.statusCode || err?.response?.status;
          this.recordError(statusCode);
          reject(err);
          throw err;
        }
      };

      this.queue.push(task);
      this.sortQueue();
      this.emit('task_enqueued', { folderId, priority, queueLength: this.queue.length });

      this.processQueue();
    });
  }

  /**
   * Sorts the queue:
   * 1. Avatar textures / avatar assets first.
   * 2. Viewport-visible / HIGH priority tasks next.
   * 3. Distance (closer first).
   * 4. Earliest addedAt timestamp first (FIFO).
   */
  private sortQueue(): void {
    this.queue.sort((a, b) => {
      // 1. Avatar textures strictly prioritized first
      const aAv = Boolean(a.isAvatarTexture);
      const bAv = Boolean(b.isAvatarTexture);
      if (aAv !== bAv) {
        return aAv ? -1 : 1;
      }

      // 2. Frustum-visible / HIGH priority next
      const aVis = a.priority === 'HIGH' || Boolean(a.inFrustum) || this.visibleFolderIds.has(a.folderId);
      const bVis = b.priority === 'HIGH' || Boolean(b.inFrustum) || this.visibleFolderIds.has(b.folderId);
      if (aVis !== bVis) {
        return aVis ? -1 : 1;
      }

      // 3. Distance (closer first)
      const aDist = a.distance ?? 0;
      const bDist = b.distance ?? 0;
      if (Math.abs(aDist - bDist) > 0.001) {
        return aDist - bDist;
      }

      // 4. FIFO
      return a.addedAt - b.addedAt;
    });
  }

  /**
   * Sliding window task worker dispatcher.
   * Maintains strictly <= maxConcurrency active network streams.
   */
  private async processQueue(): Promise<void> {
    if (this.isProcessing) return;
    this.isProcessing = true;

    try {
      while (this.activeTasks.size < this.maxConcurrency && this.queue.length > 0) {
        const task = this.queue.shift();
        if (!task) break;

        task.status = 'running';
        this.activeTasks.set(task.folderId, task);

        this.emit('task_started', {
          folderId: task.folderId,
          priority: task.priority,
          activeCount: this.activeTasks.size,
          maxConcurrency: this.maxConcurrency,
        });

        // Run task asynchronously without blocking queue worker loop
        this.runTask(task);
      }
    } finally {
      this.isProcessing = false;
    }
  }

  private async runTask(task: SyncTask): Promise<void> {
    try {
      await task.execute();
      task.status = 'completed';
      this.emit('task_completed', { folderId: task.folderId, success: true });
    } catch (err) {
      task.status = 'failed';
      console.warn(`[SlidingWindowQueue] Task ${task.folderId} failed:`, err);
      this.emit('task_failed', { folderId: task.folderId, error: err });
    } finally {
      this.activeTasks.delete(task.folderId);
      this.emit('stream_freed', {
        folderId: task.folderId,
        activeCount: this.activeTasks.size,
        remainingInQueue: this.queue.length,
      });

      if (this.activeTasks.size === 0 && this.queue.length === 0) {
        this.emit('queue_drained');
      }

      // Slide window forward to process next queued task
      this.processQueue();
    }
  }

  public getActiveCount(): number {
    return this.activeTasks.size;
  }

  public getQueuedCount(): number {
    return this.queue.length;
  }

  public getMaxConcurrency(): number {
    return this.maxConcurrency;
  }

  public getIsThrottled(): boolean {
    return this.isThrottled;
  }

  public getBatteryStatus(): BatteryStatus {
    return { ...this.batteryStatus };
  }

  public clearQueue(): void {
    this.queue = [];
    this.emit('queue_cleared');
  }
}

export const slidingWindowQueue = new SlidingWindowSyncQueue();
