/**
 * Linkpoint - Sequential Macro Task Queue
 *
 * Automates sequential wear/attach requests for outfit items with interactive progress feedback.
 */

import { Utils } from './utils';

export interface MacroTaskItem {
  id: string;
  name: string;
  folderId?: string;
  assetType?: number | string;
  [key: string]: any;
}

export type MacroMode = 'replace' | 'append';

export interface MacroTaskOptions {
  mode?: MacroMode;
  delayMs?: number;
}

export interface MacroTaskProgress {
  status: 'idle' | 'running' | 'completed' | 'cancelled' | 'error';
  mode: MacroMode;
  currentIndex: number;
  totalTasks: number;
  currentItem: MacroTaskItem | null;
  folderName?: string;
  errorMessage?: string;
}

export type WearItemHandler = (item: MacroTaskItem, options: { append: boolean }) => Promise<void>;

export class MacroTaskQueue extends Utils.EventEmitter {
  private status: 'idle' | 'running' | 'completed' | 'cancelled' | 'error' = 'idle';
  private tasks: MacroTaskItem[] = [];
  private currentIndex: number = 0;
  private mode: MacroMode = 'replace';
  private delayMs: number = 300;
  private folderName: string = '';
  private timerId: any = null;
  private wearHandler: WearItemHandler | null = null;
  private errorMessage: string | null = null;

  constructor() {
    super();
  }

  setWearHandler(handler: WearItemHandler) {
    this.wearHandler = handler;
  }

  getStatus(): MacroTaskProgress {
    return {
      status: this.status,
      mode: this.mode,
      currentIndex: this.currentIndex,
      totalTasks: this.tasks.length,
      currentItem: this.tasks[this.currentIndex] || null,
      folderName: this.folderName,
      errorMessage: this.errorMessage || undefined,
    };
  }

  isRunning(): boolean {
    return this.status === 'running';
  }

  async start(
    items: MacroTaskItem[],
    options: MacroTaskOptions = {},
    folderName: string = 'Outfit'
  ): Promise<void> {
    if (this.status === 'running') {
      this.cancel();
    }

    this.tasks = [...items];
    this.mode = options.mode || 'replace';
    this.delayMs = typeof options.delayMs === 'number' ? options.delayMs : 300;
    this.folderName = folderName;
    this.currentIndex = 0;
    this.status = 'running';
    this.errorMessage = null;

    if (this.tasks.length === 0) {
      this.status = 'completed';
      this.emit('progress', this.getStatus());
      this.emit('complete', this.getStatus());
      return;
    }

    this.emit('start', this.getStatus());
    this.emit('progress', this.getStatus());

    await this.processNextTask();
  }

  private async processNextTask(): Promise<void> {
    if (this.status !== 'running') {
      return;
    }

    if (this.currentIndex >= this.tasks.length) {
      this.status = 'completed';
      this.emit('progress', this.getStatus());
      this.emit('complete', this.getStatus());
      return;
    }

    const currentItem = this.tasks[this.currentIndex];
    // First item in 'replace' mode wears (replaces); subsequent items or in 'append' mode append.
    const isAppend = this.mode === 'append' || this.currentIndex > 0;

    try {
      if (this.wearHandler) {
        await this.wearHandler(currentItem, { append: isAppend });
      }
    } catch (err: any) {
      console.warn(`[MacroTaskQueue] Error processing item ${currentItem?.name}:`, err);
    }

    if (this.status !== 'running') {
      return; // Stop if cancelled during wear execution
    }

    this.currentIndex++;
    this.emit('progress', this.getStatus());

    if (this.currentIndex >= this.tasks.length) {
      this.status = 'completed';
      this.emit('progress', this.getStatus());
      this.emit('complete', this.getStatus());
      return;
    }

    // Wait for delay before next item
    await new Promise<void>((resolve) => {
      this.timerId = setTimeout(() => {
        this.timerId = null;
        resolve();
      }, this.delayMs);
    });

    if (this.status === 'running') {
      await this.processNextTask();
    }
  }

  cancel(): void {
    if (this.status !== 'running' && this.status !== 'idle') {
      return;
    }

    if (this.timerId) {
      clearTimeout(this.timerId);
      this.timerId = null;
    }

    this.status = 'cancelled';
    const snapshot = this.getStatus();
    this.emit('progress', snapshot);
    this.emit('cancel', snapshot);
  }

  reset(): void {
    if (this.timerId) {
      clearTimeout(this.timerId);
      this.timerId = null;
    }
    this.status = 'idle';
    this.tasks = [];
    this.currentIndex = 0;
    this.errorMessage = null;
    this.emit('progress', this.getStatus());
  }
}

export const macroTaskQueue = new MacroTaskQueue();
