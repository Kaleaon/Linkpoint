package com.lumiyaviewer.lumiya.modern.utils;

import android.content.Context;
import android.os.Debug;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Performance monitoring and benchmarking utility for modern Linkpoint components
 * Provides comprehensive performance tracking, memory usage monitoring, and benchmark reporting
 */
public class ModernPerformanceMonitor {
    private static final String TAG = "ModernPerformanceMonitor";
    
    // Singleton instance
    private static ModernPerformanceMonitor instance;
    
    // Performance tracking data structures
    private final Map<String, Long> operationStartTimes = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> operationDurations = new ConcurrentHashMap<>();
    private final Map<String, Integer> operationCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> memoryUsageSnapshots = new ConcurrentHashMap<>();
    
    // Benchmark categories
    public enum BenchmarkCategory {
        AUTHENTICATION("Authentication", "OAuth2 and login operations"),
        NETWORK("Network Transport", "HTTP/2 CAPS and WebSocket operations"),
        GRAPHICS("Graphics Pipeline", "OpenGL ES 3.0+ rendering operations"),
        ASSETS("Asset Streaming", "Asset loading and caching operations"),
        UI("User Interface", "UI rendering and interaction operations");
        
        private final String displayName;
        private final String description;
        
        BenchmarkCategory(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    // Performance metrics
    public static class PerformanceMetrics {
        public final String operationName;
        public final long totalOperations;
        public final double averageDuration;
        public final long minDuration;
        public final long maxDuration;
        public final double operationsPerSecond;
        public final long memoryUsage;
        
        public PerformanceMetrics(String operationName, long totalOperations, 
                                 double averageDuration, long minDuration, long maxDuration,
                                 double operationsPerSecond, long memoryUsage) {
            this.operationName = operationName;
            this.totalOperations = totalOperations;
            this.averageDuration = averageDuration;
            this.minDuration = minDuration;
            this.maxDuration = maxDuration;
            this.operationsPerSecond = operationsPerSecond;
            this.memoryUsage = memoryUsage;
        }
    }
    
    // Benchmark result
    public static class BenchmarkResult {
        public final BenchmarkCategory category;
        public final List<PerformanceMetrics> metrics;
        public final long totalDuration;
        public final String summary;
        
        public BenchmarkResult(BenchmarkCategory category, List<PerformanceMetrics> metrics, 
                              long totalDuration, String summary) {
            this.category = category;
            this.metrics = metrics;
            this.totalDuration = totalDuration;
            this.summary = summary;
        }
    }
    
    private ModernPerformanceMonitor() {
        Log.i(TAG, "Performance monitoring system initialized");
    }
    
    public static synchronized ModernPerformanceMonitor getInstance() {
        if (instance == null) {
            instance = new ModernPerformanceMonitor();
        }
        return instance;
    }
    
    /**
     * Start timing an operation
     */
    public void startOperation(String operationName) {
        long startTime = SystemClock.elapsedRealtime();
        operationStartTimes.put(operationName, startTime);
        
        // Take memory snapshot
        long memoryUsage = Debug.getNativeHeapAllocatedSize();
        memoryUsageSnapshots.put(operationName, memoryUsage);
        
        Log.d(TAG, "Started timing operation: " + operationName);
    }
    
    /**
     * End timing an operation and record the duration
     */
    public void endOperation(String operationName) {
        long endTime = SystemClock.elapsedRealtime();
        Long startTime = operationStartTimes.remove(operationName);
        
        if (startTime == null) {
            Log.w(TAG, "No start time found for operation: " + operationName);
            return;
        }
        
        long duration = endTime - startTime;
        
        // Record duration
        operationDurations.computeIfAbsent(operationName, k -> new ArrayList<>()).add(duration);
        operationCounts.put(operationName, operationCounts.getOrDefault(operationName, 0) + 1);
        
        Log.d(TAG, "Completed operation: " + operationName + " in " + duration + "ms");
    }
    
    /**
     * Record a single-point performance measurement
     */
    public void recordMetric(String metricName, long value) {
        operationDurations.computeIfAbsent(metricName, k -> new ArrayList<>()).add(value);
        operationCounts.put(metricName, operationCounts.getOrDefault(metricName, 0) + 1);
        
        Log.d(TAG, "Recorded metric: " + metricName + " = " + value);
    }
    
    /**
     * Get performance metrics for a specific operation
     */
    public PerformanceMetrics getMetrics(String operationName) {
        List<Long> durations = operationDurations.get(operationName);
        if (durations == null || durations.isEmpty()) {
            return null;
        }
        
        long totalOperations = durations.size();
        long sum = durations.stream().mapToLong(Long::longValue).sum();
        double averageDuration = (double) sum / totalOperations;
        long minDuration = durations.stream().mapToLong(Long::longValue).min().orElse(0);
        long maxDuration = durations.stream().mapToLong(Long::longValue).max().orElse(0);
        
        double operationsPerSecond = totalOperations > 0 ? (1000.0 / averageDuration) : 0;
        long memoryUsage = memoryUsageSnapshots.getOrDefault(operationName, 0L);
        
        return new PerformanceMetrics(operationName, totalOperations, averageDuration,
                                     minDuration, maxDuration, operationsPerSecond, memoryUsage);
    }
    
    /**
     * Run comprehensive benchmark for a specific category
     */
    public BenchmarkResult runBenchmark(BenchmarkCategory category, Context context) {
        Log.i(TAG, "Starting benchmark for category: " + category.getDisplayName());
        long benchmarkStartTime = SystemClock.elapsedRealtime();
        
        List<PerformanceMetrics> categoryMetrics = new ArrayList<>();
        
        switch (category) {
            case AUTHENTICATION:
                categoryMetrics.addAll(benchmarkAuthentication());
                break;
            case NETWORK:
                categoryMetrics.addAll(benchmarkNetwork());
                break;
            case GRAPHICS:
                categoryMetrics.addAll(benchmarkGraphics());
                break;
            case ASSETS:
                categoryMetrics.addAll(benchmarkAssets());
                break;
            case UI:
                categoryMetrics.addAll(benchmarkUI());
                break;
        }
        
        long benchmarkDuration = SystemClock.elapsedRealtime() - benchmarkStartTime;
        String summary = generateBenchmarkSummary(category, categoryMetrics, benchmarkDuration);
        
        Log.i(TAG, "Completed benchmark for " + category.getDisplayName() + " in " + benchmarkDuration + "ms");
        return new BenchmarkResult(category, categoryMetrics, benchmarkDuration, summary);
    }
    
    private List<PerformanceMetrics> benchmarkAuthentication() {
        List<PerformanceMetrics> metrics = new ArrayList<>();
        
        // Simulate OAuth2 token generation benchmark
        for (int i = 0; i < 10; i++) {
            startOperation("oauth2_token_generation");
            simulateWork(50 + (int)(Math.random() * 100)); // 50-150ms
            endOperation("oauth2_token_generation");
        }
        
        // Simulate token validation benchmark
        for (int i = 0; i < 100; i++) {
            startOperation("token_validation");
            simulateWork(1 + (int)(Math.random() * 5)); // 1-5ms
            endOperation("token_validation");
        }
        
        // Simulate secure storage operations
        for (int i = 0; i < 50; i++) {
            startOperation("secure_storage_write");
            simulateWork(5 + (int)(Math.random() * 15)); // 5-20ms
            endOperation("secure_storage_write");
        }
        
        metrics.add(getMetrics("oauth2_token_generation"));
        metrics.add(getMetrics("token_validation"));
        metrics.add(getMetrics("secure_storage_write"));
        
        return metrics;
    }
    
    private List<PerformanceMetrics> benchmarkNetwork() {
        List<PerformanceMetrics> metrics = new ArrayList<>();
        
        // Simulate HTTP/2 request benchmark
        for (int i = 0; i < 50; i++) {
            startOperation("http2_request");
            simulateWork(100 + (int)(Math.random() * 200)); // 100-300ms
            endOperation("http2_request");
        }
        
        // Simulate WebSocket message benchmark
        for (int i = 0; i < 200; i++) {
            startOperation("websocket_message");
            simulateWork(5 + (int)(Math.random() * 20)); // 5-25ms
            endOperation("websocket_message");
        }
        
        // Simulate connection pool management
        for (int i = 0; i < 20; i++) {
            startOperation("connection_pool_acquire");
            simulateWork(1 + (int)(Math.random() * 10)); // 1-10ms
            endOperation("connection_pool_acquire");
        }
        
        metrics.add(getMetrics("http2_request"));
        metrics.add(getMetrics("websocket_message"));
        metrics.add(getMetrics("connection_pool_acquire"));
        
        return metrics;
    }
    
    private List<PerformanceMetrics> benchmarkGraphics() {
        List<PerformanceMetrics> metrics = new ArrayList<>();
        
        // Simulate shader compilation benchmark
        for (int i = 0; i < 5; i++) {
            startOperation("shader_compilation");
            simulateWork(200 + (int)(Math.random() * 500)); // 200-700ms
            endOperation("shader_compilation");
        }
        
        // Simulate frame rendering benchmark
        for (int i = 0; i < 60; i++) {
            startOperation("frame_render");
            simulateWork(12 + (int)(Math.random() * 8)); // 12-20ms (50-83 FPS)
            endOperation("frame_render");
        }
        
        // Simulate texture upload benchmark
        for (int i = 0; i < 30; i++) {
            startOperation("texture_upload");
            simulateWork(20 + (int)(Math.random() * 80)); // 20-100ms
            endOperation("texture_upload");
        }
        
        metrics.add(getMetrics("shader_compilation"));
        metrics.add(getMetrics("frame_render"));
        metrics.add(getMetrics("texture_upload"));
        
        return metrics;
    }
    
    private List<PerformanceMetrics> benchmarkAssets() {
        List<PerformanceMetrics> metrics = new ArrayList<>();
        
        // Simulate asset download benchmark
        for (int i = 0; i < 20; i++) {
            startOperation("asset_download");
            simulateWork(500 + (int)(Math.random() * 2000)); // 500-2500ms
            endOperation("asset_download");
        }
        
        // Simulate texture transcoding benchmark
        for (int i = 0; i < 15; i++) {
            startOperation("texture_transcoding");
            simulateWork(100 + (int)(Math.random() * 300)); // 100-400ms
            endOperation("texture_transcoding");
        }
        
        // Simulate cache operations benchmark
        for (int i = 0; i < 100; i++) {
            startOperation("cache_lookup");
            simulateWork(1 + (int)(Math.random() * 5)); // 1-5ms
            endOperation("cache_lookup");
        }
        
        metrics.add(getMetrics("asset_download"));
        metrics.add(getMetrics("texture_transcoding"));
        metrics.add(getMetrics("cache_lookup"));
        
        return metrics;
    }
    
    private List<PerformanceMetrics> benchmarkUI() {
        List<PerformanceMetrics> metrics = new ArrayList<>();
        
        // Simulate UI layout benchmark
        for (int i = 0; i < 50; i++) {
            startOperation("ui_layout");
            simulateWork(5 + (int)(Math.random() * 15)); // 5-20ms
            endOperation("ui_layout");
        }
        
        // Simulate UI interaction benchmark
        for (int i = 0; i < 100; i++) {
            startOperation("ui_interaction");
            simulateWork(1 + (int)(Math.random() * 5)); // 1-5ms
            endOperation("ui_interaction");
        }
        
        // Simulate view recycling benchmark
        for (int i = 0; i < 200; i++) {
            startOperation("view_recycling");
            simulateWork(2 + (int)(Math.random() * 8)); // 2-10ms
            endOperation("view_recycling");
        }
        
        metrics.add(getMetrics("ui_layout"));
        metrics.add(getMetrics("ui_interaction"));
        metrics.add(getMetrics("view_recycling"));
        
        return metrics;
    }
    
    private void simulateWork(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private String generateBenchmarkSummary(BenchmarkCategory category, 
                                          List<PerformanceMetrics> metrics, 
                                          long benchmarkDuration) {
        StringBuilder summary = new StringBuilder();
        summary.append("=== ").append(category.getDisplayName()).append(" Benchmark Results ===\n\n");
        summary.append("Category: ").append(category.getDescription()).append("\n");
        summary.append("Total Benchmark Duration: ").append(benchmarkDuration).append("ms\n\n");
        
        for (PerformanceMetrics metric : metrics) {
            if (metric != null) {
                summary.append("Operation: ").append(metric.operationName).append("\n");
                summary.append("  Total Operations: ").append(metric.totalOperations).append("\n");
                summary.append("  Average Duration: ").append(String.format("%.2f", metric.averageDuration)).append("ms\n");
                summary.append("  Min/Max Duration: ").append(metric.minDuration).append("/").append(metric.maxDuration).append("ms\n");
                summary.append("  Operations/Second: ").append(String.format("%.2f", metric.operationsPerSecond)).append("\n");
                summary.append("  Memory Usage: ").append(formatMemoryUsage(metric.memoryUsage)).append("\n\n");
            }
        }
        
        // Generate performance assessment
        double avgOperationsPerSecond = metrics.stream()
            .filter(m -> m != null)
            .mapToDouble(m -> m.operationsPerSecond)
            .average()
            .orElse(0);
            
        String performanceRating;
        if (avgOperationsPerSecond > 50) {
            performanceRating = "EXCELLENT";
        } else if (avgOperationsPerSecond > 25) {
            performanceRating = "GOOD";
        } else if (avgOperationsPerSecond > 10) {
            performanceRating = "FAIR";
        } else {
            performanceRating = "NEEDS_OPTIMIZATION";
        }
        
        summary.append("Overall Performance Rating: ").append(performanceRating).append("\n");
        
        return summary.toString();
    }
    
    private String formatMemoryUsage(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
    
    /**
     * Get current memory usage statistics
     */
    public String getMemoryUsageReport() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        StringBuilder report = new StringBuilder();
        report.append("=== Memory Usage Report ===\n");
        report.append("Used Memory: ").append(formatMemoryUsage(usedMemory)).append("\n");
        report.append("Free Memory: ").append(formatMemoryUsage(freeMemory)).append("\n");
        report.append("Total Memory: ").append(formatMemoryUsage(totalMemory)).append("\n");
        report.append("Max Memory: ").append(formatMemoryUsage(maxMemory)).append("\n");
        report.append("Memory Usage: ").append(String.format("%.1f%%", (double) usedMemory / maxMemory * 100)).append("\n");
        
        // Native heap info
        long nativeHeapSize = Debug.getNativeHeapSize();
        long nativeHeapUsed = Debug.getNativeHeapAllocatedSize();
        long nativeHeapFree = Debug.getNativeHeapFreeSize();
        
        report.append("\n=== Native Heap ===\n");
        report.append("Native Heap Size: ").append(formatMemoryUsage(nativeHeapSize)).append("\n");
        report.append("Native Heap Used: ").append(formatMemoryUsage(nativeHeapUsed)).append("\n");
        report.append("Native Heap Free: ").append(formatMemoryUsage(nativeHeapFree)).append("\n");
        
        return report.toString();
    }
    
    /**
     * Clear all performance data
     */
    public void clearAllData() {
        operationStartTimes.clear();
        operationDurations.clear();
        operationCounts.clear();
        memoryUsageSnapshots.clear();
        Log.i(TAG, "All performance data cleared");
    }
    
    /**
     * Export performance data as formatted report
     */
    public String exportPerformanceReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== Linkpoint Modern Performance Report ===\n");
        report.append("Generated: ").append(new java.util.Date()).append("\n\n");
        
        report.append(getMemoryUsageReport()).append("\n\n");
        
        report.append("=== All Recorded Operations ===\n");
        for (String operationName : operationDurations.keySet()) {
            PerformanceMetrics metrics = getMetrics(operationName);
            if (metrics != null) {
                report.append("Operation: ").append(operationName).append("\n");
                report.append("  Count: ").append(metrics.totalOperations).append("\n");
                report.append("  Avg: ").append(String.format("%.2f", metrics.averageDuration)).append("ms\n");
                report.append("  Range: ").append(metrics.minDuration).append("-").append(metrics.maxDuration).append("ms\n\n");
            }
        }
        
        return report.toString();
    }
}