//! Octree spatial index with fast bounding-box range queries and worker re-balancing.

use super::aabb::AABB;
use super::topography::TopographyType;
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
pub struct SpatialEntity {
    pub id: String,
    pub bounds: AABB,
    pub position: [f32; 3],
    pub entity_type: String,
}

impl SpatialEntity {
    pub fn new(id: impl Into<String>, bounds: AABB, position: [f32; 3]) -> Self {
        Self {
            id: id.into(),
            bounds,
            position,
            entity_type: "object".to_string(),
        }
    }

    pub fn with_type(mut self, entity_type: impl Into<String>) -> Self {
        self.entity_type = entity_type.into();
        self
    }
}

#[derive(Debug, Clone)]
pub struct OctreeNode {
    pub bounds: AABB,
    pub depth: usize,
    pub max_depth: usize,
    pub max_capacity: usize,
    pub entities: Vec<SpatialEntity>,
    pub children: Option<Box<[OctreeNode; 8]>>,
}

impl OctreeNode {
    pub fn new(bounds: AABB, depth: usize, max_depth: usize, max_capacity: usize) -> Self {
        Self {
            bounds,
            depth,
            max_depth,
            max_capacity,
            entities: Vec::new(),
            children: None,
        }
    }

    pub fn is_leaf(&self) -> bool {
        self.children.is_none()
    }

    /// Splits node into 8 octants.
    pub fn subdivide(&mut self) {
        if self.children.is_some() {
            return;
        }

        let center = self.bounds.center();
        let min = [self.bounds.min[0], self.bounds.min[1], self.bounds.min[2]];
        let max = [self.bounds.max[0], self.bounds.max[1], self.bounds.max[2]];

        let child_depth = self.depth + 1;
        let md = self.max_depth;
        let mc = self.max_capacity;

        let child_bounds = [
            AABB::new(min, center),
            AABB::new([center[0], min[1], min[2]], [max[0], center[1], center[2]]),
            AABB::new([min[0], center[1], min[2]], [center[0], max[1], center[2]]),
            AABB::new([center[0], center[1], min[2]], [max[0], max[1], center[2]]),
            AABB::new([min[0], min[1], center[2]], [center[0], center[1], max[2]]),
            AABB::new([center[0], min[1], center[2]], [max[0], center[1], max[2]]),
            AABB::new([min[0], center[1], center[2]], [center[0], max[1], max[2]]),
            AABB::new(center, max),
        ];

        let children_nodes: Vec<OctreeNode> = child_bounds
            .into_iter()
            .map(|b| OctreeNode::new(b, child_depth, md, mc))
            .collect();

        let boxed_array: Box<[OctreeNode; 8]> = children_nodes
            .try_into()
            .expect("Failed converting 8 child nodes to fixed array");

        self.children = Some(boxed_array);

        // Redistribute existing entities into sub-nodes if they fit strictly inside
        let existing = std::mem::take(&mut self.entities);
        for entity in existing {
            self.insert(entity);
        }
    }

    pub fn insert(&mut self, entity: SpatialEntity) -> bool {
        if !self.bounds.intersects(&entity.bounds) {
            return false;
        }

        if let Some(ref mut children) = self.children {
            for child in children.iter_mut() {
                if child.bounds.min[0] <= entity.bounds.min[0]
                    && child.bounds.max[0] >= entity.bounds.max[0]
                    && child.bounds.min[1] <= entity.bounds.min[1]
                    && child.bounds.max[1] >= entity.bounds.max[1]
                    && child.bounds.min[2] <= entity.bounds.min[2]
                    && child.bounds.max[2] >= entity.bounds.max[2]
                {
                    return child.insert(entity);
                }
            }
        }

        if self.is_leaf() && self.entities.len() >= self.max_capacity && self.depth < self.max_depth
        {
            self.subdivide();
            return self.insert(entity);
        }

        self.entities.push(entity);
        true
    }

    pub fn remove(&mut self, id: &str) -> Option<SpatialEntity> {
        if let Some(pos) = self.entities.iter().position(|e| e.id == id) {
            return Some(self.entities.swap_remove(pos));
        }

        if let Some(ref mut children) = self.children {
            for child in children.iter_mut() {
                if let Some(removed) = child.remove(id) {
                    return Some(removed);
                }
            }
        }

        None
    }

    pub fn query_aabb<'a>(&'a self, query_bounds: &AABB, results: &mut Vec<&'a SpatialEntity>) {
        if !self.bounds.intersects(query_bounds) {
            return;
        }

        for entity in &self.entities {
            if entity.bounds.intersects(query_bounds) {
                results.push(entity);
            }
        }

        if let Some(ref children) = self.children {
            for child in children.iter() {
                child.query_aabb(query_bounds, results);
            }
        }
    }

    pub fn query_geodesic<'a>(
        &'a self,
        center: [f32; 2],
        max_distance: f32,
        topography: TopographyType,
        results: &mut Vec<&'a SpatialEntity>,
    ) {
        let node_center = self.bounds.center();
        let hx = (self.bounds.max[0] - self.bounds.min[0]) * 0.5;
        let hy = (self.bounds.max[1] - self.bounds.min[1]) * 0.5;
        let node_radius = (hx * hx + hy * hy).sqrt();

        let node_dist = topography.surface_geodesic_distance(center, [node_center[0], node_center[1]]);
        if node_dist > max_distance + node_radius {
            return;
        }

        for entity in &self.entities {
            let entity_center = [entity.position[0], entity.position[1]];
            let ehx = (entity.bounds.max[0] - entity.bounds.min[0]) * 0.5;
            let ehy = (entity.bounds.max[1] - entity.bounds.min[1]) * 0.5;
            let entity_radius = (ehx * ehx + ehy * ehy).sqrt();
            let dist = topography.surface_geodesic_distance(center, entity_center);
            if dist <= max_distance + entity_radius {
                results.push(entity);
            }
        }

        if let Some(ref children) = self.children {
            for child in children.iter() {
                child.query_geodesic(center, max_distance, topography, results);
            }
        }
    }

    pub fn query_horizon_culled<'a>(
        &'a self,
        camera_local: [f32; 3],
        query_bounds: &AABB,
        topography: TopographyType,
        results: &mut Vec<&'a SpatialEntity>,
    ) {
        if !self.bounds.intersects(query_bounds) {
            return;
        }

        let node_center = self.bounds.center();
        let hx = (self.bounds.max[0] - self.bounds.min[0]) * 0.5;
        let hy = (self.bounds.max[1] - self.bounds.min[1]) * 0.5;
        let hz = (self.bounds.max[2] - self.bounds.min[2]) * 0.5;
        let node_radius = (hx * hx + hy * hy + hz * hz).sqrt();
        if topography.is_beyond_horizon(camera_local, node_center, node_radius) {
            return;
        }

        for entity in &self.entities {
            if entity.bounds.intersects(query_bounds) {
                let ehx = (entity.bounds.max[0] - entity.bounds.min[0]) * 0.5;
                let ehy = (entity.bounds.max[1] - entity.bounds.min[1]) * 0.5;
                let ehz = (entity.bounds.max[2] - entity.bounds.min[2]) * 0.5;
                let entity_radius = (ehx * ehx + ehy * ehy + ehz * ehz).sqrt();
                if !topography.is_beyond_horizon(camera_local, entity.position, entity_radius) {
                    results.push(entity);
                }
            }
        }

        if let Some(ref children) = self.children {
            for child in children.iter() {
                child.query_horizon_culled(camera_local, query_bounds, topography, results);
            }
        }
    }

    pub fn max_depth(&self) -> usize {
        if let Some(ref children) = self.children {
            children.iter().map(|c| c.max_depth()).max().unwrap_or(self.depth)
        } else {
            self.depth
        }
    }

    pub fn query_ray<'a>(
        &'a self,
        origin: [f32; 3],
        dir: [f32; 3],
        results: &mut Vec<(&'a SpatialEntity, f32)>,
    ) {
        if self.bounds.ray_intersects(origin, dir).is_none() {
            return;
        }

        for entity in &self.entities {
            if let Some(t) = entity.bounds.ray_intersects(origin, dir) {
                results.push((entity, t));
            }
        }

        if let Some(ref children) = self.children {
            for child in children.iter() {
                child.query_ray(origin, dir, results);
            }
        }
    }

    pub fn rebalance(&mut self) -> usize {
        let mut total_entities = self.entities.len();

        if let Some(ref mut children) = self.children {
            for child in children.iter_mut() {
                total_entities += child.rebalance();
            }

            // Collapse children if total entities across all sub-nodes are small
            if total_entities <= self.max_capacity {
                let mut collapsed = Vec::new();
                self.gather_entities(&mut collapsed);
                self.children = None;
                self.entities = collapsed;
            }
        }

        total_entities
    }

    fn gather_entities(&mut self, acc: &mut Vec<SpatialEntity>) {
        acc.append(&mut self.entities);
        if let Some(ref mut children) = self.children {
            for child in children.iter_mut() {
                child.gather_entities(acc);
            }
        }
    }

    pub fn memory_usage_bytes(&self) -> usize {
        let node_bytes = std::mem::size_of::<Self>();
        let entity_bytes = self.entities.capacity() * std::mem::size_of::<SpatialEntity>();
        let children_bytes = match &self.children {
            Some(children) => children
                .iter()
                .map(|c| c.memory_usage_bytes())
                .sum::<usize>(),
            None => 0,
        };

        node_bytes + entity_bytes + children_bytes
    }
}

#[derive(Debug, Clone)]
pub struct Octree {
    pub root: OctreeNode,
    pub count: usize,
    pub topography: TopographyType,
}

impl Octree {
    pub fn new(bounds: AABB, max_depth: usize, max_capacity: usize) -> Self {
        Self {
            root: OctreeNode::new(bounds, 0, max_depth, max_capacity),
            count: 0,
            topography: TopographyType::Planar,
        }
    }

    pub fn with_topography(mut self, topography: TopographyType) -> Self {
        self.topography = topography;
        self
    }

    pub fn insert(&mut self, entity: SpatialEntity) -> bool {
        if self.root.insert(entity) {
            self.count += 1;
            true
        } else {
            false
        }
    }

    pub fn remove(&mut self, id: &str) -> Option<SpatialEntity> {
        let removed = self.root.remove(id);
        if removed.is_some() {
            self.count = self.count.saturating_sub(1);
        }
        removed
    }

    pub fn query_aabb(&self, query_bounds: &AABB) -> Vec<&SpatialEntity> {
        let mut results = Vec::new();
        self.root.query_aabb(query_bounds, &mut results);
        results
    }

    pub fn query_geodesic(&self, center: [f32; 2], max_distance: f32) -> Vec<&SpatialEntity> {
        let mut results = Vec::new();
        self.root.query_geodesic(center, max_distance, self.topography, &mut results);
        results
    }

    pub fn query_horizon_culled(
        &self,
        camera_local: [f32; 3],
        query_bounds: &AABB,
    ) -> Vec<&SpatialEntity> {
        let mut results = Vec::new();
        self.root
            .query_horizon_culled(camera_local, query_bounds, self.topography, &mut results);
        results
    }

    pub fn max_depth(&self) -> usize {
        self.root.max_depth()
    }

    pub fn query_ray(&self, origin: [f32; 3], dir: [f32; 3]) -> Vec<(&SpatialEntity, f32)> {
        let mut results = Vec::new();
        self.root.query_ray(origin, dir, &mut results);
        results.sort_by(|a, b| a.1.partial_cmp(&b.1).unwrap_or(std::cmp::Ordering::Equal));
        results
    }

    pub fn rebalance(&mut self) {
        self.root.rebalance();
    }

    pub fn memory_usage_bytes(&self) -> usize {
        self.root.memory_usage_bytes()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_octree_query_ray_remove_and_memory() {
        let bounds = AABB::new([0.0, 0.0, 0.0], [100.0, 100.0, 100.0]);
        let mut octree = Octree::new(bounds, 4, 8);

        let e1 = SpatialEntity::new(
            "item1",
            AABB::new([10.0, 10.0, 10.0], [20.0, 20.0, 20.0]),
            [15.0, 15.0, 15.0],
        )
        .with_type("avatar");
        assert_eq!(e1.entity_type, "avatar");

        octree.insert(e1.clone());
        assert_eq!(octree.count, 1);

        let ray_hits = octree.query_ray([0.0, 15.0, 15.0], [1.0, 0.0, 0.0]);
        assert_eq!(ray_hits.len(), 1);
        assert_eq!(ray_hits[0].0.id, "item1");

        octree.rebalance();
        assert!(octree.memory_usage_bytes() > 0);

        let removed = octree.remove("item1");
        assert!(removed.is_some());
        assert_eq!(octree.count, 0);

        let non_existent = octree.remove("item1");
        assert!(non_existent.is_none());
    }
}
