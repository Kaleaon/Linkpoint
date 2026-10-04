//! Worker thread pool for multi-core spatial chunk processing and octree rebalancing.

use super::aabb::AABB;
use super::chunk::ChunkGrid;
use super::octree::SpatialEntity;
use std::sync::mpsc::{Sender, channel};
use std::sync::{Arc, Mutex};
use std::thread::{self, JoinHandle};
use std::time::Instant;

pub enum Task {
    UpdateChunk {
        chunk_id: super::chunk::ChunkId,
    },
    CollisionBatchChunk {
        query_bounds_chunk: Vec<AABB>,
        reply: Sender<Vec<Vec<SpatialEntity>>>,
    },
    RebalanceOctree,
    Terminate,
}

pub struct SpatialWorkerPool {
    worker_count: usize,
    workers: Vec<Option<JoinHandle<()>>>,
    task_sender: Sender<Task>,
}

impl SpatialWorkerPool {
    pub fn new(worker_count: usize, grid: Arc<Mutex<ChunkGrid>>) -> Self {
        let (task_sender, task_receiver) = channel::<Task>();
        let receiver_arc = Arc::new(Mutex::new(task_receiver));

        let mut workers = Vec::with_capacity(worker_count);

        for _ in 0..worker_count {
            let receiver = Arc::clone(&receiver_arc);
            let grid_ref = Arc::clone(&grid);

            let handle = thread::spawn(move || {
                loop {
                    let task = {
                        let rx = receiver.lock().unwrap();
                        match rx.recv() {
                            Ok(t) => t,
                            Err(_) => break,
                        }
                    };

                    #[allow(clippy::collapsible_if)]
                    match task {
                        Task::UpdateChunk { chunk_id } => {
                            let grid = grid_ref.lock().unwrap();
                            if let Some(chunk_arc) = grid.chunks.get(&chunk_id) {
                                if let Ok(mut chunk) = chunk_arc.lock() {
                                    chunk.process_incoming_handoffs();
                                    chunk.octree.rebalance();
                                }
                            }
                        }
                        Task::CollisionBatchChunk {
                            query_bounds_chunk,
                            reply,
                        } => {
                            let grid = grid_ref.lock().unwrap();
                            let mut batch_results = Vec::with_capacity(query_bounds_chunk.len());
                            for query in &query_bounds_chunk {
                                batch_results.push(grid.query_aabb(query));
                            }
                            let _ = reply.send(batch_results);
                        }
                        Task::RebalanceOctree => {
                            let grid = grid_ref.lock().unwrap();
                            for chunk_arc in grid.chunks.values() {
                                if let Ok(mut chunk) = chunk_arc.lock() {
                                    chunk.octree.rebalance();
                                }
                            }
                        }
                        Task::Terminate => break,
                    }
                }
            });

            workers.push(Some(handle));
        }

        Self {
            worker_count,
            workers,
            task_sender,
        }
    }

    pub fn worker_count(&self) -> usize {
        self.worker_count
    }

    /// Dispatches parallel chunk rebalance tasks across worker threads.
    pub fn parallel_rebalance(&self, grid: &ChunkGrid) -> f32 {
        let start = Instant::now();
        for chunk_id in grid.chunks.keys() {
            let _ = self.task_sender.send(Task::UpdateChunk {
                chunk_id: *chunk_id,
            });
        }
        start.elapsed().as_secs_f32() * 1000.0
    }

    /// Evaluates batch collision queries across the worker pool using coarse task chunking.
    pub fn parallel_batch_collisions(&self, queries: &[AABB]) -> Vec<Vec<SpatialEntity>> {
        if queries.is_empty() {
            return Vec::new();
        }

        let chunk_size = queries.len().div_ceil(self.worker_count);
        let mut receivers = Vec::new();

        for chunk_slice in queries.chunks(chunk_size.max(1)) {
            let (tx, rx) = channel();
            let _ = self.task_sender.send(Task::CollisionBatchChunk {
                query_bounds_chunk: chunk_slice.to_vec(),
                reply: tx,
            });
            receivers.push(rx);
        }

        let mut results = Vec::with_capacity(queries.len());
        for rx in receivers {
            if let Ok(mut batch_res) = rx.recv() {
                results.append(&mut batch_res);
            }
        }
        results
    }
}

impl Drop for SpatialWorkerPool {
    fn drop(&mut self) {
        for _ in 0..self.worker_count {
            let _ = self.task_sender.send(Task::Terminate);
        }
        for worker in self.workers.iter_mut() {
            if let Some(handle) = worker.take() {
                let _ = handle.join();
            }
        }
    }
}
