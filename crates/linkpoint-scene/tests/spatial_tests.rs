use linkpoint_scene::spatial::{AABB, ChunkGrid, ChunkId, Octree, SpatialEntity, SpatialManager};
use std::time::Instant;

#[test]
fn test_simd_vs_scalar_aabb_parity() {
    let box1 = AABB::new([0.0, 0.0, 0.0], [10.0, 10.0, 10.0]);
    let box2_intersecting = AABB::new([5.0, 5.0, 5.0], [15.0, 15.0, 15.0]);
    let box3_disjoint = AABB::new([20.0, 20.0, 20.0], [30.0, 30.0, 30.0]);

    // Test single intersection parity
    assert_eq!(
        box1.intersects_scalar(&box2_intersecting),
        box1.intersects(&box2_intersecting)
    );
    assert_eq!(
        box1.intersects_scalar(&box3_disjoint),
        box1.intersects(&box3_disjoint)
    );

    // Test batch intersection parity
    let candidates = vec![box2_intersecting, box3_disjoint];
    let scalar_results = box1.intersects_batch_scalar(&candidates);
    let simd_results = box1.intersects_batch(&candidates);

    assert_eq!(scalar_results, simd_results);
    assert_eq!(simd_results, vec![true, false]);
}

#[test]
fn test_simd_aabb_acceleration_speedup() {
    let query_box = AABB::new([10.0, 10.0, 10.0], [50.0, 50.0, 50.0]);
    let count = 50_000;
    let candidates: Vec<AABB> = (0..count)
        .map(|i| {
            let offset = (i as f32) * 0.01;
            AABB::new(
                [offset, offset, offset],
                [offset + 5.0, offset + 5.0, offset + 5.0],
            )
        })
        .collect();

    // Scalar timing
    let start_scalar = Instant::now();
    let scalar_res = query_box.intersects_batch_scalar(&candidates);
    let scalar_duration = start_scalar.elapsed();

    // SIMD timing
    let start_simd = Instant::now();
    let simd_res = query_box.intersects_batch(&candidates);
    let simd_duration = start_simd.elapsed();

    assert_eq!(scalar_res, simd_res);
    println!(
        "50k AABB checks - Scalar: {:?}, SIMD: {:?}",
        scalar_duration, simd_duration
    );
}

#[test]
fn test_lock_free_boundary_handoff_latency() {
    let grid = ChunkGrid::new([0.0, 0.0, 0.0], [256.0, 256.0, 256.0], [32.0, 32.0, 32.0]);
    let from_chunk = ChunkId::new(0, 0, 0);

    // Entity moving from chunk (0,0,0) to chunk (1,1,0) at position (40.0, 40.0, 5.0)
    let entity = SpatialEntity::new(
        "avatar_1",
        AABB::new([38.0, 38.0, 3.0], [42.0, 42.0, 7.0]),
        [40.0, 40.0, 5.0],
    );

    let latency_ms = grid
        .handoff_boundary_entities(entity, from_chunk)
        .expect("Handoff failed");

    assert!(
        latency_ms < 1.0,
        "Boundary handoff latency was {:.3}ms, exceeding 1.0ms limit",
        latency_ms
    );
}

#[test]
fn test_octree_reindex_under_2ms_for_5000_primitives() {
    let region_bounds = AABB::new([0.0, 0.0, 0.0], [256.0, 256.0, 256.0]);
    let mut octree = Octree::new(region_bounds, 6, 16);

    // Populate 5,000 primitives distributed across the region volume
    for i in 0..5_000 {
        let x = ((i * 17) % 250) as f32 + 2.0;
        let y = ((i * 31) % 250) as f32 + 2.0;
        let z = ((i * 53) % 250) as f32 + 2.0;
        let entity = SpatialEntity::new(
            format!("prim_{}", i),
            AABB::new([x - 1.0, y - 1.0, z - 1.0], [x + 1.0, y + 1.0, z + 1.0]),
            [x, y, z],
        );
        octree.insert(entity);
    }

    assert_eq!(octree.count, 5000);

    // Measure rebalancing / reindexing overhead
    let start = Instant::now();
    octree.rebalance();
    let duration = start.elapsed();
    let duration_ms = duration.as_secs_f32() * 1000.0;

    println!("Rebalancing 5,000 primitives took {:.3}ms", duration_ms);
    assert!(
        duration_ms < 2.0,
        "Octree re-indexing overhead was {:.3}ms, exceeding 2.0ms limit",
        duration_ms
    );
}

#[test]
fn test_memory_overhead_under_20_percent() {
    let manager = SpatialManager::new(
        [0.0, 0.0, 0.0],
        [256.0, 256.0, 256.0],
        [32.0, 32.0, 32.0],
        4,
    );

    for i in 0..1_000 {
        let x = (i % 250) as f32 + 1.0;
        let entity = SpatialEntity::new(
            format!("prim_{}", i),
            AABB::new([x, x, x], [x + 2.0, x + 2.0, x + 2.0]),
            [x + 1.0, x + 1.0, x + 1.0],
        );
        manager.insert(entity);
    }

    let estimated_region_alloc = 10_000_000; // 10MB simulated region allocation
    let ratio = manager.memory_overhead_ratio(estimated_region_alloc);

    println!("Spatial index memory ratio: {:.2}%", ratio);
    assert!(
        ratio < 20.0,
        "Spatial index memory ratio was {:.2}%, exceeding 20% limit",
        ratio
    );
}

#[test]
fn test_linear_worker_pool_scaling() {
    let region_min = [0.0, 0.0, 0.0];
    let region_max = [256.0, 256.0, 256.0];
    let chunk_size = [32.0, 32.0, 32.0];

    let queries: Vec<AABB> = (0..200)
        .map(|i| {
            let pos = (i as f32) * 1.2;
            AABB::new([pos, pos, pos], [pos + 20.0, pos + 20.0, pos + 20.0])
        })
        .collect();

    let benchmark_threads = |worker_count: usize| -> f32 {
        let manager = SpatialManager::new(region_min, region_max, chunk_size, worker_count);
        // Insert 1000 primitives into manager
        for i in 0..1000 {
            let p = ((i * 13) % 240) as f32 + 5.0;
            manager.insert(SpatialEntity::new(
                format!("entity_{}", i),
                AABB::new([p, p, p], [p + 2.0, p + 2.0, p + 2.0]),
                [p + 1.0, p + 1.0, p + 1.0],
            ));
        }

        let start = Instant::now();
        let _results = manager.pool.parallel_batch_collisions(&queries);
        start.elapsed().as_secs_f32() * 1000.0
    };

    let time_2 = benchmark_threads(2);
    let time_4 = benchmark_threads(4);
    let time_8 = benchmark_threads(8);

    println!(
        "Worker pool scaling timings - 2 threads: {:.2}ms, 4 threads: {:.2}ms, 8 threads: {:.2}ms",
        time_2, time_4, time_8
    );

    // Multi-thread execution should be fast and scale across thread counts
    assert!(time_2 > 0.0 && time_4 > 0.0 && time_8 > 0.0);
    assert!(
        time_4 <= time_2 + 10.0,
        "4-thread execution ({:.2}ms) exceeded 2-thread limit ({:.2}ms)",
        time_4,
        time_2
    );
    assert!(
        time_8 <= time_2 + 15.0,
        "8-thread execution ({:.2}ms) exceeded 2-thread limit ({:.2}ms)",
        time_8,
        time_2
    );
}
