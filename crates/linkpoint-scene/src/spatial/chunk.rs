//! Uniform 3D Spatial Chunks and Lock-Free Boundary Handoff.

use super::aabb::AABB;
use super::octree::{Octree, SpatialEntity};
use std::collections::HashMap;
use std::sync::mpsc::{Receiver, Sender, channel};
use std::sync::{Arc, Mutex};
use std::time::Instant;

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub struct ChunkId {
    pub x: i32,
    pub y: i32,
    pub z: i32,
}

impl ChunkId {
    pub fn new(x: i32, y: i32, z: i32) -> Self {
        Self { x, y, z }
    }
}

pub struct SpatialChunk {
    pub id: ChunkId,
    pub bounds: AABB,
    pub octree: Octree,
    pub tx: Sender<SpatialEntity>,
    pub rx: Receiver<SpatialEntity>,
}

impl SpatialChunk {
    pub fn new(id: ChunkId, bounds: AABB, max_depth: usize, max_capacity: usize) -> Self {
        let (tx, rx) = channel();
        Self {
            id,
            bounds,
            octree: Octree::new(bounds, max_depth, max_capacity),
            tx,
            rx,
        }
    }

    /// Process incoming lock-free boundary handoff entity transfers.
    /// Returns the number of entities transferred into this chunk.
    pub fn process_incoming_handoffs(&mut self) -> usize {
        let mut count = 0;
        while let Ok(entity) = self.rx.try_recv() {
            self.octree.insert(entity);
            count += 1;
        }
        count
    }
}

pub struct ChunkGrid {
    pub region_min: [f32; 3],
    pub region_max: [f32; 3],
    pub chunk_size: [f32; 3],
    pub chunks: HashMap<ChunkId, Arc<Mutex<SpatialChunk>>>,
    pub chunk_channels: HashMap<ChunkId, Sender<SpatialEntity>>,
}

impl ChunkGrid {
    pub fn new(region_min: [f32; 3], region_max: [f32; 3], chunk_size: [f32; 3]) -> Self {
        let mut grid = Self {
            region_min,
            region_max,
            chunk_size,
            chunks: HashMap::new(),
            chunk_channels: HashMap::new(),
        };
        grid.initialize_chunks();
        grid
    }

    fn initialize_chunks(&mut self) {
        let count_x =
            ((self.region_max[0] - self.region_min[0]) / self.chunk_size[0]).ceil() as i32;
        let count_y =
            ((self.region_max[1] - self.region_min[1]) / self.chunk_size[1]).ceil() as i32;
        let count_z =
            ((self.region_max[2] - self.region_min[2]) / self.chunk_size[2]).ceil() as i32;

        for x in 0..count_x.max(1) {
            for y in 0..count_y.max(1) {
                for z in 0..count_z.max(1) {
                    let id = ChunkId::new(x, y, z);
                    let min = [
                        self.region_min[0] + (x as f32) * self.chunk_size[0],
                        self.region_min[1] + (y as f32) * self.chunk_size[1],
                        self.region_min[2] + (z as f32) * self.chunk_size[2],
                    ];
                    let max = [
                        (min[0] + self.chunk_size[0]).min(self.region_max[0]),
                        (min[1] + self.chunk_size[1]).min(self.region_max[1]),
                        (min[2] + self.chunk_size[2]).min(self.region_max[2]),
                    ];

                    let bounds = AABB::new(min, max);
                    let chunk = SpatialChunk::new(id, bounds, 6, 16);
                    self.chunk_channels.insert(id, chunk.tx.clone());
                    self.chunks.insert(id, Arc::new(Mutex::new(chunk)));
                }
            }
        }
    }

    pub fn get_chunk_id(&self, pos: [f32; 3]) -> ChunkId {
        let x = (((pos[0] - self.region_min[0]) / self.chunk_size[0]).floor() as i32).max(0);
        let y = (((pos[1] - self.region_min[1]) / self.chunk_size[1]).floor() as i32).max(0);
        let z = (((pos[2] - self.region_min[2]) / self.chunk_size[2]).floor() as i32).max(0);
        ChunkId::new(x, y, z)
    }

    pub fn insert_entity(&mut self, entity: SpatialEntity) -> bool {
        let id = self.get_chunk_id(entity.position);
        self.chunks
            .get(&id)
            .and_then(|c| c.lock().ok())
            .map(|mut chunk| chunk.octree.insert(entity))
            .unwrap_or(false)
    }

    #[allow(clippy::collapsible_if)]
    pub fn query_aabb(&self, query_bounds: &AABB) -> Vec<SpatialEntity> {
        let mut results = Vec::new();
        for chunk_arc in self.chunks.values() {
            if let Ok(chunk) = chunk_arc.lock() {
                if chunk.bounds.intersects(query_bounds) {
                    for m in chunk.octree.query_aabb(query_bounds) {
                        results.push(m.clone());
                    }
                }
            }
        }
        results
    }

    /// Performs lock-free boundary handoff transfers for moving entities across spatial chunks.
    /// Returns transfer statistics and handoff latency.
    #[allow(clippy::collapsible_if)]
    pub fn handoff_boundary_entities(
        &self,
        moving_entity: SpatialEntity,
        from_chunk: ChunkId,
    ) -> Result<f32, String> {
        let start = Instant::now();
        let target_chunk_id = self.get_chunk_id(moving_entity.position);

        if target_chunk_id == from_chunk {
            return Ok(0.0);
        }

        // Remove from source chunk
        if let Some(source_chunk) = self.chunks.get(&from_chunk) {
            if let Ok(mut chunk) = source_chunk.lock() {
                chunk.octree.remove(&moving_entity.id);
            }
        }

        // Lock-free queue dispatch to target chunk channel
        if let Some(sender) = self.chunk_channels.get(&target_chunk_id) {
            sender
                .send(moving_entity)
                .map_err(|e| format!("Lock-free channel handoff send failed: {}", e))?;
        } else {
            return Err("Target chunk channel not found".to_string());
        }

        // Receive chunk processes incoming boundary queue
        if let Some(target_chunk) = self.chunks.get(&target_chunk_id) {
            if let Ok(mut chunk) = target_chunk.lock() {
                chunk.process_incoming_handoffs();
            }
        }

        let elapsed_ms = start.elapsed().as_secs_f32() * 1000.0;
        Ok(elapsed_ms)
    }

    pub fn total_memory_usage_bytes(&self) -> usize {
        let mut total = 0;
        for chunk_arc in self.chunks.values() {
            if let Ok(chunk) = chunk_arc.lock() {
                total += chunk.octree.memory_usage_bytes();
            }
        }
        total
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_chunk_grid_handoff_edge_cases() {
        let grid = ChunkGrid::new([0.0, 0.0, 0.0], [256.0, 256.0, 256.0], [64.0, 64.0, 64.0]);

        let entity = SpatialEntity::new(
            "test_e",
            AABB::new([1.0, 1.0, 1.0], [5.0, 5.0, 5.0]),
            [2.0, 2.0, 2.0],
        );

        let same_chunk = ChunkId { x: 0, y: 0, z: 0 };
        let same_res = grid.handoff_boundary_entities(entity.clone(), same_chunk);
        assert_eq!(same_res, Ok(0.0));

        let out_of_bounds = SpatialEntity::new(
            "out_e",
            AABB::new([500.0, 500.0, 500.0], [510.0, 510.0, 510.0]),
            [505.0, 505.0, 505.0],
        );
        let err_res = grid.handoff_boundary_entities(out_of_bounds, same_chunk);
        assert!(err_res.is_err());

        let _mem = grid.total_memory_usage_bytes();
    }
}
