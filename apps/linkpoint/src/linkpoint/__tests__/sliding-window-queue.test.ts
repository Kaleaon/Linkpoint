import { describe, expect, it, beforeEach } from 'vitest';
import { SlidingWindowSyncQueue } from '../sliding-window-queue';

describe('SlidingWindowSyncQueue', () => {
  let queue: SlidingWindowSyncQueue;

  beforeEach(() => {
    queue = new SlidingWindowSyncQueue();
  });

  it('limits active concurrent network requests to 3 parallel workers by default', async () => {
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

    expect(maxObservedStreams).toBe(3);
    expect(queue.getActiveCount()).toBe(0);
    expect(queue.getQueuedCount()).toBe(0);
  });

  it('adjusts max concurrency dynamically based on grid kind and network connection type', () => {
    // Unconfigured defaults to DEFAULT_MAX_CONCURRENCY
    expect(queue.getMaxConcurrency()).toBe(3);

    // Set Second Life on Unmetered Wi-Fi
    queue.setGridKind('secondlife');
    queue.setNetworkType('unmetered');
    expect(queue.getMaxConcurrency()).toBe(64);

    // Switch to Second Life on Metered Cellular
    queue.setNetworkType('metered');
    expect(queue.getMaxConcurrency()).toBe(16);

    // Switch to OpenSim on Metered Cellular
    queue.setGridKind('opensim');
    expect(queue.getMaxConcurrency()).toBe(4);

    // Switch to OpenSim on Unmetered Wi-Fi
    queue.setNetworkType('unmetered');
    expect(queue.getMaxConcurrency()).toBe(10);
  });

  it('triggers dynamic backoff on HTTP 503 / timeouts and recovers on successful downloads', () => {
    queue.setGridKind('opensim');
    queue.setNetworkType('unmetered');
    expect(queue.getMaxConcurrency()).toBe(10);

    // Simulate HTTP 503 error
    queue.recordError(503);
    expect(queue.getMaxConcurrency()).toBe(5);

    // Simulate server timeout
    queue.recordTimeout();
    expect(queue.getMaxConcurrency()).toBe(2);

    // Simulate another timeout (floor is 1)
    queue.recordTimeout();
    expect(queue.getMaxConcurrency()).toBe(1);

    // Successful fast responses recover concurrency back to base limit
    for (let i = 0; i < 10; i++) {
      queue.recordSuccess();
    }
    expect(queue.getMaxConcurrency()).toBe(10);
  });

  it('prioritizes avatar textures ahead of frustum and background textures', async () => {
    const startOrder: string[] = [];

    // Saturate queue workers
    queue.setGridKind('opensim');
    const blockers = Array.from({ length: 10 }, (_, i) =>
      queue.enqueue(
        `blocker_${i}`,
        async () => {
          startOrder.push(`blocker_${i}`);
          await new Promise((res) => setTimeout(res, 30));
        },
        'HIGH'
      )
    );

    // Enqueue background texture
    const bg = queue.enqueue(
      'bg_texture',
      async () => { startOrder.push('bg_texture'); },
      'LOW',
      { inFrustum: false }
    );

    // Enqueue frustum texture
    const frustum = queue.enqueue(
      'frustum_texture',
      async () => { startOrder.push('frustum_texture'); },
      'HIGH',
      { inFrustum: true }
    );

    // Enqueue avatar texture (should execute before frustum and background textures)
    const avatar = queue.enqueue(
      'avatar_baked_head',
      async () => { startOrder.push('avatar_baked_head'); },
      'HIGH',
      { isAvatarTexture: true }
    );

    await Promise.all([...blockers, bg, frustum, avatar]);

    const avatarIndex = startOrder.indexOf('avatar_baked_head');
    const frustumIndex = startOrder.indexOf('frustum_texture');
    const bgIndex = startOrder.indexOf('bg_texture');

    expect(avatarIndex).toBeGreaterThan(-1);
    expect(avatarIndex).toBeLessThan(frustumIndex);
    expect(frustumIndex).toBeLessThan(bgIndex);
  });

  it('prioritizes viewport-visible folders ahead of background off-screen folders', async () => {
    const startOrder: string[] = [];

    // Create a slow blocker task to saturate 1 stream
    const blockerPromise = queue.enqueue(
      'blocker',
      async () => {
        startOrder.push('blocker');
        await new Promise((res) => setTimeout(res, 30));
      },
      'HIGH'
    );

    // Fill remaining concurrency slots
    const slot2 = queue.enqueue('slot2', async () => { startOrder.push('slot2'); await new Promise((res) => setTimeout(res, 30)); }, 'HIGH');
    const slot3 = queue.enqueue('slot3', async () => { startOrder.push('slot3'); await new Promise((res) => setTimeout(res, 30)); }, 'HIGH');

    // Enqueue background off-screen folders (LOW priority)
    const bgFolder1 = queue.enqueue('bg_folder_1', async () => { startOrder.push('bg_folder_1'); }, 'LOW');
    const bgFolder2 = queue.enqueue('bg_folder_2', async () => { startOrder.push('bg_folder_2'); }, 'LOW');

    // Enqueue a new folder that suddenly becomes viewport-visible (HIGH priority)
    const visibleFolder = queue.enqueue('visible_folder_now', async () => { startOrder.push('visible_folder_now'); }, 'HIGH');

    await Promise.all([blockerPromise, slot2, slot3, bgFolder1, bgFolder2, visibleFolder]);

    // Viewport-visible folder must execute BEFORE the background off-screen folders!
    const visibleIndex = startOrder.indexOf('visible_folder_now');
    const bg1Index = startOrder.indexOf('bg_folder_1');
    const bg2Index = startOrder.indexOf('bg_folder_2');

    expect(visibleIndex).toBeGreaterThan(-1);
    expect(visibleIndex).toBeLessThan(bg1Index);
    expect(visibleIndex).toBeLessThan(bg2Index);
  });

  it('promotes existing queued tasks when viewport folders change', async () => {
    const startOrder: string[] = [];

    // Occupy 3 active streams
    const s1 = queue.enqueue('active_1', async () => { startOrder.push('active_1'); await new Promise((res) => setTimeout(res, 40)); });
    const s2 = queue.enqueue('active_2', async () => { startOrder.push('active_2'); await new Promise((res) => setTimeout(res, 40)); });
    const s3 = queue.enqueue('active_3', async () => { startOrder.push('active_3'); await new Promise((res) => setTimeout(res, 40)); });

    // Enqueue off-screen background tasks
    const bg1 = queue.enqueue('folder_a', async () => { startOrder.push('folder_a'); }, 'LOW');
    const bg2 = queue.enqueue('folder_b', async () => { startOrder.push('folder_b'); }, 'LOW');

    // User scrolls to folder_b: promote folder_b to viewport visible
    queue.updateViewportFolders(['folder_b']);

    await Promise.all([s1, s2, s3, bg1, bg2]);

    const indexA = startOrder.indexOf('folder_a');
    const indexB = startOrder.indexOf('folder_b');

    expect(indexB).toBeLessThan(indexA);
  });

  it('throttles network requests when device battery falls below 15%', async () => {
    expect(queue.getMaxConcurrency()).toBe(3);

    // Simulate battery dropping to 10% discharging
    queue.setBatteryStatus(0.1, false);

    expect(queue.getIsThrottled()).toBe(true);
    expect(queue.getMaxConcurrency()).toBe(1);

    // Simulate battery plugging in / charging
    queue.setBatteryStatus(0.1, true);

    expect(queue.getIsThrottled()).toBe(false);
    expect(queue.getMaxConcurrency()).toBe(3);
  });
});
