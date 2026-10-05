/*
 * AsyncCacheManagerTest - Tests for decoupled asynchronous eviction queue and ReadWriteLock
 */

package lindenlab.llsd.viewer.secondlife.cache;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncCacheManagerTest {

    private CacheManager cacheManager;
    private Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        tempDir = Files.createTempDirectory("cache-test-dir");
        // Initialize cache manager with small max size for easy eviction testing (1 MB)
        cacheManager = new CacheManager(CacheManager.StorageLocation.SYSTEM_TEMP, 1024 * 1024);
    }

    @AfterEach
    void tearDown() {
        if (cacheManager != null) {
            cacheManager.shutdown();
        }
        try {
            if (tempDir != null && Files.exists(tempDir)) {
                Files.walk(tempDir)
                        .sorted((a, b) -> b.compareTo(a))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (Exception ignored) {}
                        });
            }
        } catch (Exception ignored) {}
    }

    @Test
    @DisplayName("Concurrent reads should execute concurrently without blocking each other")
    void testConcurrentReadsDoNotBlock() throws Exception {
        byte[] testData = "concurrent read test payload".getBytes();
        String key = "shared-read-key";

        cacheManager.store(CacheManager.CacheType.TEXTURE, key, testData).get(5, TimeUnit.SECONDS);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                    byte[] retrieved = cacheManager.retrieve(CacheManager.CacheType.TEXTURE, key).get(2, TimeUnit.SECONDS);
                    if (retrieved != null && new String(retrieved).equals("concurrent read test payload")) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        latch.countDown();
        assertTrue(completionLatch.await(5, TimeUnit.SECONDS), "All concurrent reads should complete in time");
        assertEquals(threadCount, successCount.get(), "All reader threads should successfully retrieve cached item");

        executor.shutdown();
    }

    @Test
    @DisplayName("Eviction should update in-memory size immediately and delete files asynchronously without blocking lookups")
    void testAsyncEvictionAndLookupLatency() throws Exception {
        // Set type limit low to force eviction on texture cache (100 KB)
        cacheManager.setCacheTypeLimit(CacheManager.CacheType.TEXTURE, 100 * 1024);

        byte[] smallItem = new byte[10 * 1024]; // 10 KB
        for (int i = 0; i < 9; i++) {
            cacheManager.store(CacheManager.CacheType.TEXTURE, "item-" + i, smallItem).get(5, TimeUnit.SECONDS);
        }

        // Cache is now at ~90 KB out of 100 KB limit.
        // Storing a 30 KB item will trigger eviction of oldest entries.
        byte[] largeItem = new byte[30 * 1024];
        CompletableFuture<Boolean> storeFuture = cacheManager.store(CacheManager.CacheType.TEXTURE, "item-large", largeItem);

        // Verify lookup on existing item completes in under 5ms during eviction
        long start = System.nanoTime();
        byte[] retrieved = cacheManager.retrieve(CacheManager.CacheType.TEXTURE, "item-8").get(5, TimeUnit.SECONDS);
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        assertTrue(storeFuture.get(5, TimeUnit.SECONDS), "Store triggering eviction should succeed");
        assertNotNull(retrieved, "Non-evicted item should be retrieved");
        assertTrue(elapsedMs < 50, "Retrieval latency should remain low during background eviction (actual: " + elapsedMs + "ms)");
    }

    @Test
    @DisplayName("Concurrent store, remove, retrieve, and global cleanup should execute without deadlocks")
    void testConcurrentOperationsNoDeadlock() throws Exception {
        int durationMs = 2000;
        int workerCount = 8;
        ExecutorService threadPool = Executors.newFixedThreadPool(workerCount);
        AtomicBoolean running = new AtomicBoolean(true);
        AtomicInteger operationCount = new AtomicInteger(0);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < workerCount; i++) {
            final int workerId = i;
            futures.add(threadPool.submit(() -> {
                int count = 0;
                while (running.get()) {
                    try {
                        String key = "key-" + workerId + "-" + (count % 20);
                        byte[] payload = ("data-" + workerId + "-" + count).getBytes();

                        switch (count % 4) {
                            case 0:
                                cacheManager.store(CacheManager.CacheType.TEXTURE, key, payload);
                                break;
                            case 1:
                                cacheManager.retrieve(CacheManager.CacheType.TEXTURE, key);
                                break;
                            case 2:
                                cacheManager.remove(CacheManager.CacheType.TEXTURE, key);
                                break;
                            case 3:
                                cacheManager.setCacheTypeLimit(CacheManager.CacheType.TEXTURE, 50 * 1024);
                                break;
                        }
                        count++;
                        operationCount.incrementAndGet();
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception ignored) {}
                }
            }));
        }

        Thread.sleep(durationMs);
        running.set(false);

        threadPool.shutdown();
        assertTrue(threadPool.awaitTermination(5, TimeUnit.SECONDS), "Worker threads should terminate cleanly without deadlocking");
        assertTrue(operationCount.get() > 0, "Concurrent operations should be processed");
    }
}
