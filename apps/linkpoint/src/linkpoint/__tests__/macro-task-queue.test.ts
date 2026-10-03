import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest';
import { MacroTaskQueue, MacroTaskItem } from '../macro-task-queue';

describe('MacroTaskQueue Engine', () => {
  let queue: MacroTaskQueue;

  beforeEach(() => {
    vi.useFakeTimers();
    queue = new MacroTaskQueue();
  });

  afterEach(() => {
    queue.reset();
    vi.useRealTimers();
  });

  it('initializes with default idle status', () => {
    const status = queue.getStatus();
    expect(status.status).toBe('idle');
    expect(status.totalTasks).toBe(0);
    expect(status.currentIndex).toBe(0);
    expect(status.currentItem).toBeNull();
  });

  it('runs tasks sequentially and emits progress updates', async () => {
    const items: MacroTaskItem[] = [
      { id: 'item-1', name: 'Jacket' },
      { id: 'item-2', name: 'Pants' },
      { id: 'item-3', name: 'Boots' },
    ];

    const wearCalls: Array<{ item: MacroTaskItem; append: boolean }> = [];
    queue.setWearHandler(async (item, opts) => {
      wearCalls.push({ item, append: opts.append });
    });

    const progressSnapshots: any[] = [];
    queue.on('progress', (snapshot) => progressSnapshots.push({ ...snapshot }));

    const startPromise = queue.start(items, { mode: 'replace', delayMs: 200 }, 'Summer Outfit');

    // First task should be called immediately
    expect(wearCalls).toHaveLength(1);
    expect(wearCalls[0]).toEqual({ item: items[0], append: false }); // First item in replace mode is not append

    // Advance time for second item
    await vi.advanceTimersByTimeAsync(200);
    expect(wearCalls).toHaveLength(2);
    expect(wearCalls[1]).toEqual({ item: items[1], append: true }); // Subsequent items append

    // Advance time for third item
    await vi.advanceTimersByTimeAsync(200);
    expect(wearCalls).toHaveLength(3);
    expect(wearCalls[2]).toEqual({ item: items[2], append: true });

    await startPromise;

    const finalStatus = queue.getStatus();
    expect(finalStatus.status).toBe('completed');
    expect(progressSnapshots.length).toBeGreaterThan(0);
  });

  it('passes append=true for all items when mode is append', async () => {
    const items: MacroTaskItem[] = [
      { id: 'item-1', name: 'Hat' },
      { id: 'item-2', name: 'Glasses' },
    ];

    const wearCalls: Array<{ item: MacroTaskItem; append: boolean }> = [];
    queue.setWearHandler(async (item, opts) => {
      wearCalls.push({ item, append: opts.append });
    });

    const startPromise = queue.start(items, { mode: 'append', delayMs: 100 }, 'Accessories');

    expect(wearCalls[0].append).toBe(true);

    await vi.advanceTimersByTimeAsync(100);
    expect(wearCalls[1].append).toBe(true);

    await startPromise;
    expect(queue.getStatus().status).toBe('completed');
  });

  it('immediately cancels remaining tasks when cancel() is called', async () => {
    const items: MacroTaskItem[] = [
      { id: 'item-1', name: 'Item 1' },
      { id: 'item-2', name: 'Item 2' },
      { id: 'item-3', name: 'Item 3' },
      { id: 'item-4', name: 'Item 4' },
    ];

    const wearCalls: Array<{ item: MacroTaskItem }> = [];
    queue.setWearHandler(async (item) => {
      wearCalls.push({ item });
    });

    let cancelEmitted = false;
    queue.on('cancel', () => {
      cancelEmitted = true;
    });

    const startPromise = queue.start(items, { delayMs: 300 }, 'Test Outfit');

    // Item 1 executed
    expect(wearCalls).toHaveLength(1);

    // Cancel while waiting for Item 2
    queue.cancel();
    expect(queue.getStatus().status).toBe('cancelled');
    expect(cancelEmitted).toBe(true);

    // Fast-forward time to verify no more wear calls happen
    await vi.advanceTimersByTimeAsync(1000);
    expect(wearCalls).toHaveLength(1); // Still only item 1 was executed

    await startPromise;
  });

  it('handles empty task lists gracefully', async () => {
    let completed = false;
    queue.on('complete', () => {
      completed = true;
    });

    await queue.start([], { mode: 'replace' }, 'Empty Outfit');
    expect(queue.getStatus().status).toBe('completed');
    expect(completed).toBe(true);
  });
});
