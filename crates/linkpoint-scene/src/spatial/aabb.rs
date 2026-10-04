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

        #[cfg(not(target_arch = "x86_64"))]
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

        #[cfg(not(target_arch = "x86_64"))]
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
