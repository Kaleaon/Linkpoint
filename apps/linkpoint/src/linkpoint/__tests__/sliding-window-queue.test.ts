import { describe, expect, it, beforeEach } from 'vitest';
import { SlidingWindowSyncQueue } from '../sliding-window-queue';

describe('SlidingWindowSyncQueue', () => {
  let queue: SlidingWindowSyncQueue;

  beforeEach(() => {
    queue = new SlidingWindowSyncQueue();
  });

  it('limits active concurrent network requests to 3 parallel workers', async () => {
    let activeStreamCount = 0;
    let maxObservedStreams = 0;

    const createSlowTask = () => async () => {
      activeStreamCount++;
      if (activeStreamCount > maxObservedStreams) {
        maxObservedStreams = activeStreamCount;
      }
      await new Promise((resolve) => setTimeout(resolve, 50));
      activeStreamCount--;
    };

    // Enqueue 8 sync tasks simultaneously
    const promises = Array.from({ length: 8 }, (_, i) =>
      queue.enqueue(`folder_${i}`, createSlowTask(), 'LOW')
    );

    // Assert that immediately after enqueue, active streams do not exceed 3
    expect(queue.getActiveCount()).toBeLessThanOrEqual(3);

    await Promise.all(promises);

    expect(maxObservedStreams).toBe(3); // Criterion 2 assertion
    expect(queue.getActiveCount()).toBe(0);
    expect(queue.getQueuedCount()).toBe(0);
  });

  it('prioritizes viewport-visible folders ahead of background off-screen folders', async () => {
    const executionOrder: string[] = [];

    // Create a slow blocker task to saturate 1 stream
    const blockerPromise = queue.enqueue(
      'blocker',
      async () => {
        await new Promise((res) => setTimeout(res, 30));
        executionOrder.push('blocker');
      },
      'HIGH'
    );

    // Fill remaining concurrency slots
    const slot2 = queue.enqueue('slot2', async () => { await new Promise((res) => setTimeout(res, 30)); executionOrder.push('slot2'); }, 'HIGH');
    const slot3 = queue.enqueue('slot3', async () => { await new Promise((res) => setTimeout(res, 30)); executionOrder.push('slot3'); }, 'HIGH');

    // Enqueue background off-screen folders (LOW priority)
    const bgFolder1 = queue.enqueue('bg_folder_1', async () => { executionOrder.push('bg_folder_1'); }, 'LOW');
    const bgFolder2 = queue.enqueue('bg_folder_2', async () => { executionOrder.push('bg_folder_2'); }, 'LOW');

    // Enqueue a new folder that suddenly becomes viewport-visible (HIGH priority)
    const visibleFolder = queue.enqueue('visible_folder_now', async () => { executionOrder.push('visible_folder_now'); }, 'HIGH');

    await Promise.all([blockerPromise, slot2, slot3, bgFolder1, bgFolder2, visibleFolder]);

    // Viewport-visible folder must execute BEFORE the background off-screen folders!
    const visibleIndex = executionOrder.indexOf('visible_folder_now');
    const bg1Index = executionOrder.indexOf('bg_folder_1');
    const bg2Index = executionOrder.indexOf('bg_folder_2');

    expect(visibleIndex).toBeGreaterThan(-1);
    expect(visibleIndex).toBeLessThan(bg1Index); // Criterion 3 assertion
    expect(visibleIndex).toBeLessThan(bg2Index);
  });

  it('promotes existing queued tasks when viewport folders change', async () => {
    const executionOrder: string[] = [];

    // Occupy 3 active streams
    const s1 = queue.enqueue('active_1', async () => { await new Promise((res) => setTimeout(res, 40)); executionOrder.push('active_1'); });
    const s2 = queue.enqueue('active_2', async () => { await new Promise((res) => setTimeout(res, 40)); executionOrder.push('active_2'); });
    const s3 = queue.enqueue('active_3', async () => { await new Promise((res) => setTimeout(res, 40)); executionOrder.push('active_3'); });

    // Enqueue off-screen background tasks
    const bg1 = queue.enqueue('folder_a', async () => { executionOrder.push('folder_a'); }, 'LOW');
    const bg2 = queue.enqueue('folder_b', async () => { executionOrder.push('folder_b'); }, 'LOW');

    // User scrolls to folder_b: promote folder_b to viewport visible
    queue.updateViewportFolders(['folder_b']);

    await Promise.all([s1, s2, s3, bg1, bg2]);

    const indexA = executionOrder.indexOf('folder_a');
    const indexB = executionOrder.indexOf('folder_b');

    expect(indexB).toBeLessThan(indexA);
  });

  it('throttles network requests when device battery falls below 15%', async () => {
    expect(queue.getMaxConcurrency()).toBe(3);

    // Simulate battery dropping to 10% discharging
    queue.setBatteryStatus(0.1, false);

    expect(queue.getIsThrottled()).toBe(true);
    expect(queue.getMaxConcurrency()).toBe(1); // Throttled to 1 worker

    // Simulate battery plugging in / charging
    queue.setBatteryStatus(0.1, true);

    expect(queue.getIsThrottled()).toBe(false);
    expect(queue.getMaxConcurrency()).toBe(3);
  });

  it('rejects pending enqueued task promises when clearQueue is called', async () => {
    // Fill all 3 active streams with slow tasks
    const active1 = queue.enqueue('active_1', () => new Promise((res) => setTimeout(res, 100)));
    const active2 = queue.enqueue('active_2', () => new Promise((res) => setTimeout(res, 100)));
    const active3 = queue.enqueue('active_3', () => new Promise((res) => setTimeout(res, 100)));

    // Enqueue 3 pending tasks (these will wait in queue)
    const pending1 = queue.enqueue('pending_1', async () => 'result_1');
    const pending2 = queue.enqueue('pending_2', async () => 'result_2');
    const pending3 = queue.enqueue('pending_3', async () => 'result_3');

    expect(queue.getActiveCount()).toBe(3);
    expect(queue.getQueuedCount()).toBe(3);

    let clearedEventPayload: any = null;
    queue.on('queue_cleared', (payload) => {
      clearedEventPayload = payload;
    });

    // Clear queue with cancellation reason
    const reasonMessage = 'Region teleport in progress';
    queue.clearQueue(reasonMessage);

    expect(queue.getQueuedCount()).toBe(0);
    expect(clearedEventPayload).not.toBeNull();
    expect(clearedEventPayload.cancelledCount).toBe(3);
    expect(clearedEventPayload.reason).toBeInstanceOf(Error);
    expect(clearedEventPayload.reason.message).toBe(reasonMessage);

    // Pending promises must reject with cancellation error
    await expect(pending1).rejects.toThrow(reasonMessage);
    await expect(pending2).rejects.toThrow(reasonMessage);
    await expect(pending3).rejects.toThrow(reasonMessage);

    // Active tasks finish normally
    await Promise.all([active1, active2, active3]);
    expect(queue.getActiveCount()).toBe(0);
  });

  it('executes clearQueue safely on an empty queue', async () => {
    let clearedEventPayload: any = null;
    queue.on('queue_cleared', (payload) => {
      clearedEventPayload = payload;
    });

    queue.clearQueue();

    expect(queue.getQueuedCount()).toBe(0);
    expect(clearedEventPayload).not.toBeNull();
    expect(clearedEventPayload.cancelledCount).toBe(0);
    expect(clearedEventPayload.reason).toBeInstanceOf(Error);
    expect(clearedEventPayload.reason.message).toBe('Queue cleared');
  });

  it('supports passing Error instances or strings or no args to clearQueue', async () => {
    // Fill 3 active worker streams
    const a1 = queue.enqueue('a1', () => new Promise((res) => setTimeout(res, 50)));
    const a2 = queue.enqueue('a2', () => new Promise((res) => setTimeout(res, 50)));
    const a3 = queue.enqueue('a3', () => new Promise((res) => setTimeout(res, 50)));

    const customError = new Error('Session disconnected');
    const pending = queue.enqueue('pending', async () => 'data');

    queue.clearQueue(customError);

    await expect(pending).rejects.toThrow('Session disconnected');
    await Promise.all([a1, a2, a3]);
  });
});
