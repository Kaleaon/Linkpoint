//! SIMD-accelerated Axis-Aligned Bounding Box (AABB) intersection and spatial queries.

use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Copy, PartialEq, Serialize, Deserialize)]
#[repr(C, align(16))]
pub struct AABB {
    pub min: [f32; 4],
    pub max: [f32; 4],
}

impl Default for AABB {
    fn default() -> Self {
        Self {
            min: [0.0; 4],
            max: [0.0; 4],
        }
    }
}

impl AABB {
    #[inline]
    pub fn new(min: [f32; 3], max: [f32; 3]) -> Self {
        Self {
            min: [
                min[0].min(max[0]),
                min[1].min(max[1]),
                min[2].min(max[2]),
                0.0,
            ],
            max: [
                min[0].max(max[0]),
                min[1].max(max[1]),
                min[2].max(max[2]),
                0.0,
            ],
        }
    }

    #[inline]
    pub fn from_center_extents(center: [f32; 3], extents: [f32; 3]) -> Self {
        let half = [extents[0] * 0.5, extents[1] * 0.5, extents[2] * 0.5];
        Self::new(
            [
                center[0] - half[0],
                center[1] - half[1],
                center[2] - half[2],
            ],
            [
                center[0] + half[0],
                center[1] + half[1],
                center[2] + half[2],
            ],
        )
    }

    #[inline]
    pub fn center(&self) -> [f32; 3] {
        [
            (self.min[0] + self.max[0]) * 0.5,
            (self.min[1] + self.max[1]) * 0.5,
            (self.min[2] + self.max[2]) * 0.5,
        ]
    }

    #[inline]
    pub fn extents(&self) -> [f32; 3] {
        [
            self.max[0] - self.min[0],
            self.max[1] - self.min[1],
            self.max[2] - self.min[2],
        ]
    }

    #[inline]
    pub fn contains_point(&self, point: [f32; 3]) -> bool {
        point[0] >= self.min[0]
            && point[0] <= self.max[0]
            && point[1] >= self.min[1]
            && point[1] <= self.max[1]
            && point[2] >= self.min[2]
            && point[2] <= self.max[2]
    }

    /// Scalar AABB-AABB intersection calculation.
    #[inline]
    pub fn intersects_scalar(&self, other: &AABB) -> bool {
        self.min[0] <= other.max[0]
            && self.max[0] >= other.min[0]
            && self.min[1] <= other.max[1]
            && self.max[1] >= other.min[1]
            && self.min[2] <= other.max[2]
            && self.max[2] >= other.min[2]
    }

    /// SIMD-accelerated AABB intersection query with scalar fallback.
    #[inline]
    pub fn intersects(&self, other: &AABB) -> bool {
        #[cfg(target_arch = "x86_64")]
        {
            unsafe { self.intersects_simd_sse2(other) }
        }

        #[cfg(target_arch = "aarch64")]
        {
            unsafe { self.intersects_simd_neon(other) }
        }

        #[cfg(not(any(target_arch = "x86_64", target_arch = "aarch64")))]
        {
            self.intersects_scalar(other)
        }
    }

    #[cfg(target_arch = "x86_64")]
    #[inline]
    unsafe fn intersects_simd_sse2(&self, other: &AABB) -> bool {
        use std::arch::x86_64::*;

        unsafe {
            let v_min_a = _mm_loadu_ps(self.min.as_ptr());
            let v_max_a = _mm_loadu_ps(self.max.as_ptr());
            let v_min_b = _mm_loadu_ps(other.min.as_ptr());
            let v_max_b = _mm_loadu_ps(other.max.as_ptr());

            let cond1 = _mm_cmple_ps(v_min_a, v_max_b);
            let cond2 = _mm_cmpge_ps(v_max_a, v_min_b);
            let combined = _mm_and_ps(cond1, cond2);

            let mask = _mm_movemask_ps(combined);
            (mask & 0x07) == 0x07
        }
    }

    #[cfg(target_arch = "aarch64")]
    #[inline]
    unsafe fn intersects_simd_neon(&self, other: &AABB) -> bool {
        use std::arch::aarch64::*;

        unsafe {
            let v_min_a = vld1q_f32(self.min.as_ptr());
            let v_max_a = vld1q_f32(self.max.as_ptr());
            let v_min_b = vld1q_f32(other.min.as_ptr());
            let v_max_b = vld1q_f32(other.max.as_ptr());

            let cond1 = vcleq_f32(v_min_a, v_max_b);
            let cond2 = vcgeq_f32(v_max_a, v_min_b);
            let combined = vandq_u32(cond1, cond2);

            let lane0 = vgetq_lane_u32::<0>(combined);
            let lane1 = vgetq_lane_u32::<1>(combined);
            let lane2 = vgetq_lane_u32::<2>(combined);

            lane0 != 0 && lane1 != 0 && lane2 != 0
        }
    }

    /// Scalar batch intersection testing against multiple candidates.
    pub fn intersects_batch_scalar(&self, candidates: &[AABB]) -> Vec<bool> {
        candidates
            .iter()
            .map(|c| self.intersects_scalar(c))
            .collect()
    }

    /// SIMD-vectorized batch intersection testing against multiple candidates.
    pub fn intersects_batch(&self, candidates: &[AABB]) -> Vec<bool> {
        #[cfg(target_arch = "x86_64")]
        {
            unsafe { self.intersects_batch_simd_sse2(candidates) }
        }

        #[cfg(target_arch = "aarch64")]
        {
            unsafe { self.intersects_batch_simd_neon(candidates) }
        }

        #[cfg(not(any(target_arch = "x86_64", target_arch = "aarch64")))]
        {
            self.intersects_batch_scalar(candidates)
        }
    }

    #[cfg(target_arch = "x86_64")]
    unsafe fn intersects_batch_simd_sse2(&self, candidates: &[AABB]) -> Vec<bool> {
        use std::arch::x86_64::*;

        let mut results = Vec::with_capacity(candidates.len());

        unsafe {
            let v_min_a = _mm_loadu_ps(self.min.as_ptr());
            let v_max_a = _mm_loadu_ps(self.max.as_ptr());

            for candidate in candidates {
                let v_min_b = _mm_loadu_ps(candidate.min.as_ptr());
                let v_max_b = _mm_loadu_ps(candidate.max.as_ptr());

                let cond1 = _mm_cmple_ps(v_min_a, v_max_b);
                let cond2 = _mm_cmpge_ps(v_max_a, v_min_b);
                let combined = _mm_and_ps(cond1, cond2);

                let mask = _mm_movemask_ps(combined);
                results.push((mask & 0x07) == 0x07);
            }
        }

        results
    }

    #[cfg(target_arch = "aarch64")]
    unsafe fn intersects_batch_simd_neon(&self, candidates: &[AABB]) -> Vec<bool> {
        use std::arch::aarch64::*;

        let mut results = Vec::with_capacity(candidates.len());

        unsafe {
            let v_min_a = vld1q_f32(self.min.as_ptr());
            let v_max_a = vld1q_f32(self.max.as_ptr());

            for candidate in candidates {
                let v_min_b = vld1q_f32(candidate.min.as_ptr());
                let v_max_b = vld1q_f32(candidate.max.as_ptr());

                let cond1 = vcleq_f32(v_min_a, v_max_b);
                let cond2 = vcgeq_f32(v_max_a, v_min_b);
                let combined = vandq_u32(cond1, cond2);

                let lane0 = vgetq_lane_u32::<0>(combined);
                let lane1 = vgetq_lane_u32::<1>(combined);
                let lane2 = vgetq_lane_u32::<2>(combined);

                results.push(lane0 != 0 && lane1 != 0 && lane2 != 0);
            }
        }

        results
    }

    /// Ray intersection test returning hit distance t along origin + t * dir if intersected.
    pub fn ray_intersects(&self, origin: [f32; 3], dir: [f32; 3]) -> Option<f32> {
        let mut t_min = f32::NEG_INFINITY;
        let mut t_max = f32::INFINITY;

        for i in 0..3 {
            if dir[i].abs() < 1e-8 {
                if origin[i] < self.min[i] || origin[i] > self.max[i] {
                    return None;
                }
            } else {
                let inv_d = 1.0 / dir[i];
                let mut t0 = (self.min[i] - origin[i]) * inv_d;
                let mut t1 = (self.max[i] - origin[i]) * inv_d;
                if inv_d < 0.0 {
                    std::mem::swap(&mut t0, &mut t1);
                }
                t_min = t_min.max(t0);
                t_max = t_max.min(t1);
                if t_max < t_min {
                    return None;
                }
            }
        }

        if t_max < 0.0 {
            None
        } else {
            Some(if t_min >= 0.0 { t_min } else { t_max })
        }
    }

    /// Computes the union bounding box that encompasses both `self` and `other`.
    pub fn union(&self, other: &AABB) -> AABB {
        AABB::new(
            [
                self.min[0].min(other.min[0]),
                self.min[1].min(other.min[1]),
                self.min[2].min(other.min[2]),
            ],
            [
                self.max[0].max(other.max[0]),
                self.max[1].max(other.max[1]),
                self.max[2].max(other.max[2]),
            ],
        )
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_aabb_basics() {
        let default_aabb = AABB::default();
        assert_eq!(default_aabb.center(), [0.0, 0.0, 0.0]);
        assert_eq!(default_aabb.extents(), [0.0, 0.0, 0.0]);

        let box1 = AABB::from_center_extents([10.0, 20.0, 30.0], [2.0, 4.0, 6.0]);
        assert_eq!(box1.center(), [10.0, 20.0, 30.0]);
        assert_eq!(box1.extents(), [2.0, 4.0, 6.0]);

        assert!(box1.contains_point([10.0, 20.0, 30.0]));
        assert!(!box1.contains_point([0.0, 0.0, 0.0]));

        let box2 = AABB::new([8.0, 18.0, 28.0], [12.0, 22.0, 32.0]);
        assert!(box1.intersects_scalar(&box2));
        assert!(box1.intersects(&box2));

        let batch = box1.intersects_batch(&[box2]);
        assert_eq!(batch, vec![true]);

        let union_box = box1.union(&AABB::new([0.0, 0.0, 0.0], [1.0, 1.0, 1.0]));
        assert!(union_box.contains_point([0.5, 0.5, 0.5]));
        assert!(union_box.contains_point([10.0, 20.0, 30.0]));

        let ray_hit = box1.ray_intersects([10.0, 20.0, 0.0], [0.0, 0.0, 1.0]);
        assert!(ray_hit.is_some());
        let ray_miss = box1.ray_intersects([0.0, 0.0, 0.0], [0.0, -1.0, 0.0]);
        assert!(ray_miss.is_none());
    }

    #[test]
    fn test_aabb_default_and_constructors() {
        let def = AABB::default();
        assert_eq!(def.min, [0.0; 4]);
        assert_eq!(def.max, [0.0; 4]);

        let aabb = AABB::from_center_extents([10.0, 20.0, 30.0], [4.0, 6.0, 8.0]);
        assert_eq!(aabb.min, [8.0, 17.0, 26.0, 0.0]);
        assert_eq!(aabb.max, [12.0, 23.0, 34.0, 0.0]);
        assert_eq!(aabb.center(), [10.0, 20.0, 30.0]);
        assert_eq!(aabb.extents(), [4.0, 6.0, 8.0]);
    }

    #[test]
    fn test_aabb_contains_and_union() {
        let aabb1 = AABB::new([0.0, 0.0, 0.0], [10.0, 10.0, 10.0]);
        assert!(aabb1.contains_point([5.0, 5.0, 5.0]));
        assert!(!aabb1.contains_point([15.0, 5.0, 5.0]));

        let aabb2 = AABB::new([5.0, 5.0, 5.0], [15.0, 15.0, 15.0]);
        let u = aabb1.union(&aabb2);
        assert_eq!(u.min, [0.0, 0.0, 0.0, 0.0]);
        assert_eq!(u.max, [15.0, 15.0, 15.0, 0.0]);
    }

    #[test]
    fn test_aabb_intersects_batch_and_scalar() {
        let box1 = AABB::new([0.0, 0.0, 0.0], [10.0, 10.0, 10.0]);
        let candidates = vec![
            AABB::new([5.0, 5.0, 5.0], [12.0, 12.0, 12.0]),
            AABB::new([20.0, 20.0, 20.0], [25.0, 25.0, 25.0]),
        ];
        let scalar_res = box1.intersects_batch_scalar(&candidates);
        assert_eq!(scalar_res, vec![true, false]);

        let batch_res = box1.intersects_batch(&candidates);
        assert_eq!(batch_res, vec![true, false]);
    }

    #[test]
    fn test_aabb_ray_intersects() {
        let box1 = AABB::new([0.0, 0.0, 0.0], [10.0, 10.0, 10.0]);

        // Hit ray from outside
        let hit = box1.ray_intersects([-5.0, 5.0, 5.0], [1.0, 0.0, 0.0]);
        assert!(hit.is_some());
        assert!((hit.unwrap() - 5.0).abs() < 1e-4);

        // Hit ray starting inside
        let hit_inside = box1.ray_intersects([5.0, 5.0, 5.0], [1.0, 0.0, 0.0]);
        assert!(hit_inside.is_some());

        // Miss ray
        let miss = box1.ray_intersects([-5.0, 20.0, 5.0], [1.0, 0.0, 0.0]);
        assert!(miss.is_none());

        // Parallel ray outside
        let parallel_outside = box1.ray_intersects([-5.0, 20.0, 5.0], [0.0, 1.0, 0.0]);
        assert!(parallel_outside.is_none());

        // Ray pointing away
        let away = box1.ray_intersects([-5.0, 5.0, 5.0], [-1.0, 0.0, 0.0]);
        assert!(away.is_none());
    }
}
