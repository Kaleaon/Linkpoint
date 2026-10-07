//! Local manifold topography projections, surface geodesic metrics, and network protocol serialization.

use std::f32::consts::PI;
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Copy, PartialEq, Default, Serialize, Deserialize)]
pub enum TopographyType {
    #[default]
    Planar,
    Ringworld { radius: f32, width: f32 },
    Spherical { radius: f32 },
}

impl TopographyType {
    /// Map local manifold position (x_l, y_l, z_l) to standard Cartesian coordinates (x_c, y_c, z_c).
    pub fn project_to_cartesian(&self, local_pos: [f32; 3]) -> [f32; 3] {
        match *self {
            TopographyType::Planar => local_pos,
            TopographyType::Ringworld { radius, .. } => {
                let theta = local_pos[0] / radius;
                let r = radius - local_pos[2];
                let x = r * theta.sin();
                let y = local_pos[1];
                let z = radius - r * theta.cos();
                [x, y, z]
            }
            TopographyType::Spherical { radius } => {
                let lambda = local_pos[0] / radius;
                let phi = local_pos[1] / radius;
                let r = radius + local_pos[2];

                let x = r * phi.cos() * lambda.sin();
                let y = r * phi.sin();
                let z = r * phi.cos() * lambda.cos() - radius;
                [x, y, z]
            }
        }
    }

    /// Map standard Cartesian coordinates (x_c, y_c, z_c) to local manifold position (x_l, y_l, z_l).
    pub fn project_from_cartesian(&self, cartesian_pos: [f32; 3]) -> [f32; 3] {
        match *self {
            TopographyType::Planar => cartesian_pos,
            TopographyType::Ringworld { radius, .. } => {
                let local_y = cartesian_pos[1];
                let dx = cartesian_pos[0];
                let dz = radius - cartesian_pos[2];
                let r = (dx * dx + dz * dz).sqrt();
                let local_z = radius - r;
                let mut theta = dx.atan2(dz);
                if theta < 0.0 {
                    theta += 2.0 * PI;
                }
                let local_x = theta * radius;
                [local_x, local_y, local_z]
            }
            TopographyType::Spherical { radius } => {
                let dz = cartesian_pos[2] + radius;
                let r = (cartesian_pos[0] * cartesian_pos[0]
                    + cartesian_pos[1] * cartesian_pos[1]
                    + dz * dz)
                    .sqrt();
                let local_z = r - radius;
                let phi = (cartesian_pos[1] / r).clamp(-1.0, 1.0).asin();
                let lambda = cartesian_pos[0].atan2(dz);

                let local_x = lambda * radius;
                let local_y = phi * radius;
                [local_x, local_y, local_z]
            }
        }
    }

    /// Calculate native surface geodesic distance along the manifold floor.
    pub fn surface_geodesic_distance(&self, p1: [f32; 2], p2: [f32; 2]) -> f32 {
        match *self {
            TopographyType::Planar => {
                let dx = p2[0] - p1[0];
                let dy = p2[1] - p1[1];
                (dx * dx + dy * dy).sqrt()
            }
            TopographyType::Ringworld { radius, .. } => {
                let circumference = 2.0 * PI * radius;
                let dx_raw = (p2[0] - p1[0]).abs();
                let dx = dx_raw.min(circumference - dx_raw);
                let dy = p2[1] - p1[1];
                (dx * dx + dy * dy).sqrt()
            }
            TopographyType::Spherical { radius } => {
                let lambda1 = p1[0] / radius;
                let phi1 = (p1[1] / radius).clamp(-PI / 2.0, PI / 2.0);
                let lambda2 = p2[0] / radius;
                let phi2 = (p2[1] / radius).clamp(-PI / 2.0, PI / 2.0);

                let cos_sigma = (phi1.sin() * phi2.sin()
                    + phi1.cos() * phi2.cos() * (lambda2 - lambda1).cos())
                    .clamp(-1.0, 1.0);
                let delta_sigma = cos_sigma.acos();
                delta_sigma * radius
            }
        }
    }

    /// Evaluate horizon culling on non-planar surfaces.
    pub fn is_beyond_horizon(
        &self,
        camera_local: [f32; 3],
        target_local: [f32; 3],
        target_radius: f32,
    ) -> bool {
        match *self {
            TopographyType::Planar | TopographyType::Ringworld { .. } => false,
            TopographyType::Spherical { radius } => {
                let cam_alt = camera_local[2].max(0.0);
                let cos_horizon = (radius / (radius + cam_alt)).clamp(0.0, 1.0);
                let sigma_horizon = cos_horizon.acos();

                let dist_geo = self.surface_geodesic_distance(
                    [camera_local[0], camera_local[1]],
                    [target_local[0], target_local[1]],
                );
                let delta_sigma = dist_geo / radius;
                let angular_radius = if radius > 0.0 { target_radius / radius } else { 0.0 };

                delta_sigma > (sigma_horizon + angular_radius)
            }
        }
    }

    /// Wrap or clamp local boundary coordinates according to topography rules.
    pub fn wrap_or_clamp_boundary(&self, local_pos: [f32; 3]) -> [f32; 3] {
        match *self {
            TopographyType::Planar => local_pos,
            TopographyType::Ringworld { radius, .. } => {
                let circumference = 2.0 * PI * radius;
                let wrapped_x = ((local_pos[0] % circumference) + circumference) % circumference;
                [wrapped_x, local_pos[1], local_pos[2]]
            }
            TopographyType::Spherical { radius } => {
                let circumference = 2.0 * PI * radius;
                let max_lat = circumference * 0.25; // PI/2 * radius
                let wrapped_x = ((local_pos[0] % circumference) + circumference) % circumference;
                let clamped_y = local_pos[1].clamp(-max_lat, max_lat);
                [wrapped_x, clamped_y, local_pos[2]]
            }
        }
    }
}

/// Boundary network serializer mapping local manifold positions to Cartesian protocol packets.
pub struct TopographyNetworkSerializer;

impl TopographyNetworkSerializer {
    /// Converts local manifold position to Cartesian protocol packet array.
    pub fn to_cartesian_protocol_packet(
        local_pos: [f32; 3],
        topography: TopographyType,
    ) -> [f32; 3] {
        topography.project_to_cartesian(local_pos)
    }

    /// Converts Cartesian protocol packet array to local manifold position.
    pub fn from_cartesian_protocol_packet(
        cartesian_pos: [f32; 3],
        topography: TopographyType,
    ) -> [f32; 3] {
        topography.project_from_cartesian(cartesian_pos)
    }
}
