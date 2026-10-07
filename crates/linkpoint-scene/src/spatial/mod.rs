//! 3D Spatial Partitioning, SIMD AABB, Octrees, and Worker Pools.

pub mod aabb;
pub mod chunk;
pub mod octree;
pub mod worker;

pub use aabb::AABB;
pub use chunk::{ChunkGrid, ChunkId, SpatialChunk};
pub use octree::{Octree, SpatialEntity};
pub use worker::SpatialWorkerPool;

use std::sync::{Arc, RwLock};
use std::time::Instant;

/// Central coordinator for region spatial simulation, SIMD bounding box queries, and multi-threaded octrees.
pub struct SpatialManager {
    pub grid: Arc<RwLock<ChunkGrid>>,
    pub pool: SpatialWorkerPool,
}

impl SpatialManager {
    pub fn new(
        region_min: [f32; 3],
        region_max: [f32; 3],
        chunk_size: [f32; 3],
        worker_threads: usize,
    ) -> Self {
        let grid = Arc::new(RwLock::new(ChunkGrid::new(
            region_min, region_max, chunk_size,
        )));
        let pool = SpatialWorkerPool::new(worker_threads, Arc::clone(&grid));

        Self { grid, pool }
    }

    pub fn insert(&self, entity: SpatialEntity) -> bool {
        if let Ok(mut grid) = self.grid.write() {
            grid.insert_entity(entity)
        } else {
            false
        }
    }

    pub fn query_aabb_simd(&self, search_bounds: &AABB) -> Vec<SpatialEntity> {
        if let Ok(grid) = self.grid.read() {
            grid.query_aabb(search_bounds)
        } else {
            Vec::new()
        }
    }

    pub fn update_entity_position(
        &self,
        mut entity: SpatialEntity,
        new_position: [f32; 3],
        new_bounds: AABB,
        from_chunk: ChunkId,
    ) -> Result<f32, String> {
        entity.position = new_position;
        entity.bounds = new_bounds;

        if let Ok(grid) = self.grid.read() {
            grid.handoff_boundary_entities(entity, from_chunk)
        } else {
            Err("Failed to acquire chunk grid lock".to_string())
        }
    }

    pub fn rebalance_async(&self) -> f32 {
        let start = Instant::now();
        if let Ok(grid) = self.grid.read() {
            self.pool.parallel_rebalance(&grid);
        }
        start.elapsed().as_secs_f32() * 1000.0
    }

    pub fn memory_overhead_ratio(&self, estimated_region_allocation_bytes: usize) -> f64 {
        if estimated_region_allocation_bytes == 0 {
            return 0.0;
        }
        if let Ok(grid) = self.grid.read() {
            let usage = grid.total_memory_usage_bytes();
            (usage as f64 / estimated_region_allocation_bytes as f64) * 100.0
        } else {
            0.0
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_spatial_manager_operations() {
        let mgr = SpatialManager::new(
            [0.0, 0.0, 0.0],
            [256.0, 256.0, 256.0],
            [64.0, 64.0, 64.0],
            2,
        );

        let entity = SpatialEntity::new(
            "1",
            AABB::new([8.0, 8.0, 8.0], [12.0, 12.0, 12.0]),
            [10.0, 10.0, 10.0],
        );

        assert!(mgr.insert(entity.clone()));

        let search = AABB::new([0.0, 0.0, 0.0], [20.0, 20.0, 20.0]);
        let results = mgr.query_aabb_simd(&search);
        assert_eq!(results.len(), 1);
        assert_eq!(results[0].id, "1");

        let from_chunk = ChunkId { x: 0, y: 0, z: 0 };
        let update_res = mgr.update_entity_position(
            entity,
            [15.0, 15.0, 15.0],
            AABB::new([13.0, 13.0, 13.0], [17.0, 17.0, 17.0]),
            from_chunk,
        );
        assert!(update_res.is_ok());

        let _elapsed = mgr.rebalance_async();
        let overhead = mgr.memory_overhead_ratio(1024 * 1024);
        assert!(overhead >= 0.0);

        let zero_overhead = mgr.memory_overhead_ratio(0);
        assert_eq!(zero_overhead, 0.0);
    }
}
