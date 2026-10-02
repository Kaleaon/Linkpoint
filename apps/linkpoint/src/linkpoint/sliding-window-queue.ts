/**
 * Linkpoint - Asynchronous Sliding-Window Synchronization Queue
 *
 * Limits concurrent background network HTTP requests to 3 active streams,
 * prioritizes viewport-visible folder subtrees over off-screen folders,
 * and throttles sync requests when battery drops below 15%.
 */

import { Utils } from './utils';

export type TaskPriority = 'HIGH' | 'LOW';
export type TaskStatus = 'queued' | 'running' | 'completed' | 'failed';

export interface SyncTask {
  id: string;
  folderId: string;
  priority: TaskPriority;
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
      this.maxConcurrency = shouldThrottle
        ? SlidingWindowSyncQueue.THROTTLED_MAX_CONCURRENCY
        : SlidingWindowSyncQueue.DEFAULT_MAX_CONCURRENCY;

      console.warn(
        `[SlidingWindowQueue] Battery ${Math.round(level * 100)}% (${isCharging ? 'charging' : 'discharging'}). ` +
        `Background sync throttling ${shouldThrottle ? 'ACTIVATED (max 1 stream)' : 'DEACTIVATED (max 3 streams)'}.`
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
   * Enqueues a folder sync task into the sliding-window queue.
   */
  public enqueue(
    folderId: string,
    executeFn: () => Promise<any>,
    initialPriority?: TaskPriority
  ): Promise<any> {
    return new Promise((resolve, reject) => {
      // Check if task is already running or queued
      if (this.activeTasks.has(folderId)) {
        return resolve(null);
      }

      const existingInQueue = this.queue.find((t) => t.folderId === folderId);
      if (existingInQueue) {
        if (initialPriority === 'HIGH' || this.visibleFolderIds.has(folderId)) {
          existingInQueue.priority = 'HIGH';
          this.sortQueue();
        }
        return resolve(null);
      }

      const isVisible = this.visibleFolderIds.has(folderId);
      const priority: TaskPriority = initialPriority || (isVisible ? 'HIGH' : 'LOW');

      const task: SyncTask = {
        id: `sync_${folderId}_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`,
        folderId,
        priority,
        execute: executeFn,
        addedAt: Date.now(),
        status: 'queued',
        retries: 0,
      };

      // Wrap task execution to settle outer Promise
      const originalExecute = task.execute;
      task.execute = async () => {
        try {
          const result = await originalExecute();
          resolve(result);
          return result;
        } catch (err) {
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
   * 1. HIGH priority tasks first.
   * 2. Viewport-visible folders first.
   * 3. Earliest addedAt timestamp first (FIFO).
   */
  private sortQueue(): void {
    this.queue.sort((a, b) => {
      if (a.priority !== b.priority) {
        return a.priority === 'HIGH' ? -1 : 1;
      }
      const aVisible = this.visibleFolderIds.has(a.folderId);
      const bVisible = this.visibleFolderIds.has(b.folderId);
      if (aVisible !== bVisible) {
        return aVisible ? -1 : 1;
      }
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

  /**
   * Returns current active task count (must be <= 3).
   */
  public getActiveCount(): number {
    return this.activeTasks.size;
  }

  /**
   * Returns remaining queued task count.
   */
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

  /**
   * Clears all pending queued tasks.
   */
  public clearQueue(): void {
    this.queue = [];
    this.emit('queue_cleared');
  }
}

export const slidingWindowQueue = new SlidingWindowSyncQueue();
