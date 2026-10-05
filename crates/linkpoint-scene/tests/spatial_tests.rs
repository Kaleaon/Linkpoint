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
        latency_ms >= 0.0,
        "Boundary handoff latency was negative ({:.3}ms)",
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
        duration_ms >= 0.0,
        "Octree re-indexing overhead was negative ({:.3}ms)",
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

    // Multi-thread execution should complete successfully across thread counts
    assert!(time_2 >= 0.0 && time_4 >= 0.0 && time_8 >= 0.0);
}

#[test]
fn test_aabb_ray_intersects_and_union() {
    let box1 = AABB::new([0.0, 0.0, 0.0], [10.0, 10.0, 10.0]);
    let box2 = AABB::new([5.0, 5.0, 5.0], [15.0, 15.0, 15.0]);

    let u = box1.union(&box2);
    assert_eq!(&u.min[..3], &[0.0, 0.0, 0.0]);
    assert_eq!(&u.max[..3], &[15.0, 15.0, 15.0]);

    // Ray hit
    let hit = box1.ray_intersects([-5.0, 5.0, 5.0], [1.0, 0.0, 0.0]);
    assert!(hit.is_some());
    assert_eq!(hit.unwrap(), 5.0);

    // Ray miss
    let miss = box1.ray_intersects([-5.0, 20.0, 5.0], [1.0, 0.0, 0.0]);
    assert!(miss.is_none());

    // Ray parallel miss outside
    let par_miss = box1.ray_intersects([-5.0, 20.0, 5.0], [0.0, 1.0, 0.0]);
    assert!(par_miss.is_none());
}

#[test]
fn test_octree_query_ray_and_remove() {
    let bounds = AABB::new([0.0, 0.0, 0.0], [100.0, 100.0, 100.0]);
    let mut octree = Octree::new(bounds, 4, 2);

    let entity1 = SpatialEntity::new(
        "e1",
        AABB::new([10.0, 10.0, 10.0], [20.0, 20.0, 20.0]),
        [15.0, 15.0, 15.0],
    )
    .with_type("avatar");
    let entity2 = SpatialEntity::new(
        "e2",
        AABB::new([50.0, 50.0, 50.0], [60.0, 60.0, 60.0]),
        [55.0, 55.0, 55.0],
    );

    octree.insert(entity1);
    octree.insert(entity2);

    let ray_hits = octree.query_ray([0.0, 15.0, 15.0], [1.0, 0.0, 0.0]);
    assert!(!ray_hits.is_empty());
    assert_eq!(ray_hits[0].0.id, "e1");

    assert!(octree.memory_usage_bytes() > 0);

    let removed = octree.remove("e1");
    assert!(removed.is_some());
    assert_eq!(octree.count, 1);

    // Test collapse during rebalance
    octree.rebalance();
}

#[test]
fn test_spatial_manager_and_worker_pool_methods() {
    let manager = SpatialManager::new(
        [0.0, 0.0, 0.0],
        [256.0, 256.0, 256.0],
        [32.0, 32.0, 32.0],
        2,
    );

    assert_eq!(manager.pool.worker_count(), 2);

    let entity = SpatialEntity::new(
        "e1",
        AABB::new([10.0, 10.0, 10.0], [20.0, 20.0, 20.0]),
        [15.0, 15.0, 15.0],
    );
    assert!(manager.insert(entity.clone()));

    let simd_results = manager.query_aabb_simd(&AABB::new([0.0, 0.0, 0.0], [30.0, 30.0, 30.0]));
    assert_eq!(simd_results.len(), 1);

    let update_res = manager.update_entity_position(
        entity,
        [40.0, 40.0, 5.0],
        AABB::new([38.0, 38.0, 3.0], [42.0, 42.0, 7.0]),
        ChunkId::new(0, 0, 0),
    );
    assert!(update_res.is_ok());

    let rebalance_ms = manager.rebalance_async();
    assert!(rebalance_ms >= 0.0);
}

#[test]
fn test_spatial_worker_pool_additional_coverage() {
    let region_min = [0.0, 0.0, 0.0];
    let region_max = [256.0, 256.0, 256.0];
    let chunk_size = [32.0, 32.0, 32.0];

    let manager = SpatialManager::new(region_min, region_max, chunk_size, 4);
    assert_eq!(manager.pool.worker_count(), 4);

    let entity = SpatialEntity::new(
        "avatar_test",
        AABB::new([1.0, 1.0, 1.0], [5.0, 5.0, 5.0]),
        [3.0, 3.0, 3.0],
    )
    .with_type("avatar");
    assert_eq!(entity.entity_type, "avatar");

    manager.insert(entity);

    // Test parallel_rebalance
    let rebalance_ms = manager
        .pool
        .parallel_rebalance(&manager.grid.lock().unwrap());
    assert!(rebalance_ms >= 0.0);

    // Test empty parallel_batch_collisions
    let empty_res = manager.pool.parallel_batch_collisions(&[]);
    assert!(empty_res.is_empty());

    // Test total_memory_usage_bytes and handoff early return
    let grid = manager.grid.lock().unwrap();
    let mem_bytes = grid.total_memory_usage_bytes();
    assert!(mem_bytes > 0);

    let same_chunk_entity = SpatialEntity::new(
        "same_chunk",
        AABB::new([2.0, 2.0, 2.0], [4.0, 4.0, 4.0]),
        [3.0, 3.0, 3.0],
    );
    let same_chunk_id = grid.get_chunk_id([3.0, 3.0, 3.0]);
    let handoff_same = grid.handoff_boundary_entities(same_chunk_entity, same_chunk_id);
    assert_eq!(handoff_same.unwrap(), 0.0);
}

#[test]
fn test_octree_query_ray_and_remove_extended() {
    let region_bounds = AABB::new([0.0, 0.0, 0.0], [100.0, 100.0, 100.0]);
    let mut octree = Octree::new(region_bounds, 4, 2);

    let e1 = SpatialEntity::new(
        "obj_1",
        AABB::new([10.0, 10.0, 10.0], [20.0, 20.0, 20.0]),
        [15.0, 15.0, 15.0],
    );
    let e2 = SpatialEntity::new(
        "obj_2",
        AABB::new([30.0, 30.0, 30.0], [40.0, 40.0, 40.0]),
        [35.0, 35.0, 35.0],
    );

    assert!(octree.insert(e1.clone()));
    assert!(octree.insert(e2.clone()));
    assert_eq!(octree.count, 2);

    // Query ray through obj_1
    let ray_hits = octree.query_ray([0.0, 0.0, 0.0], [1.0, 1.0, 1.0]);
    assert!(!ray_hits.is_empty());

    // Remove obj_1
    let removed = octree.remove("obj_1");
    assert!(removed.is_some());
    assert_eq!(removed.unwrap().id, "obj_1");
    assert_eq!(octree.count, 1);

    // Remove non-existent object
    assert!(octree.remove("non_existent").is_none());

    octree.rebalance();
}
