use super::matrix::Matrix4;
use super::quaternion::Quaternion;
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Copy, PartialEq, Serialize, Deserialize)]
pub struct DualQuaternion {
    pub real: Quaternion,
    pub dual: Quaternion,
}

impl Default for DualQuaternion {
    fn default() -> Self {
        Self::identity()
    }
}

impl DualQuaternion {
    pub const IDENTITY: Self = Self {
        real: Quaternion::IDENTITY,
        dual: Quaternion {
            x: 0.0,
            y: 0.0,
            z: 0.0,
            w: 0.0,
        },
    };

    pub fn new(real: Quaternion, dual: Quaternion) -> Self {
        Self { real, dual }
    }

    pub fn identity() -> Self {
        Self::IDENTITY
    }

    pub fn from_rotation_translation(rotation: Quaternion, translation: [f32; 3]) -> Self {
        let q0 = rotation.normalized();
        let t_x = translation[0];
        let t_y = translation[1];
        let t_z = translation[2];

        // q_e = 0.5 * (t_x, t_y, t_z, 0) * q0
        let qe_x = 0.5 * (t_x * q0.w + t_y * q0.z - t_z * q0.y);
        let qe_y = 0.5 * (t_y * q0.w + t_z * q0.x - t_x * q0.z);
        let qe_z = 0.5 * (t_z * q0.w + t_x * q0.y - t_y * q0.x);
        let qe_w = -0.5 * (t_x * q0.x + t_y * q0.y + t_z * q0.z);

        Self {
            real: q0,
            dual: Quaternion::new(qe_x, qe_y, qe_z, qe_w),
        }
    }

    pub fn from_matrix(matrix: &Matrix4) -> Self {
        let q0 = matrix_to_quaternion(matrix);
        let translation = matrix.get_translation();
        Self::from_rotation_translation(q0, translation)
    }

    pub fn to_matrix(&self) -> Matrix4 {
        let q0 = self.real.normalized();
        let translation = self.get_translation();
        Matrix4::from_quaternion_and_translation(&q0, translation)
    }

    pub fn get_translation(&self) -> [f32; 3] {
        let q0 = self.real;
        let qe = self.dual;
        // t = 2 * (w0 * v_e - w_e * v0 + v0 x v_e)
        let tx = 2.0 * (q0.w * qe.x - qe.w * q0.x + q0.y * qe.z - q0.z * qe.y);
        let ty = 2.0 * (q0.w * qe.y - qe.w * q0.y + q0.z * qe.x - q0.x * qe.z);
        let tz = 2.0 * (q0.w * qe.z - qe.w * q0.z + q0.x * qe.y - q0.y * qe.x);
        [tx, ty, tz]
    }

    pub fn magnitude(&self) -> f32 {
        self.real.magnitude()
    }

    pub fn normalize(&mut self) -> f32 {
        let mag = self.real.magnitude();
        if mag > 1e-6 {
            let inv_mag = 1.0 / mag;
            self.real.x *= inv_mag;
            self.real.y *= inv_mag;
            self.real.z *= inv_mag;
            self.real.w *= inv_mag;

            self.dual.x *= inv_mag;
            self.dual.y *= inv_mag;
            self.dual.z *= inv_mag;
            self.dual.w *= inv_mag;
        } else {
            *self = Self::identity();
        }
        mag
    }

    pub fn normalized(&self) -> Self {
        let mut dq = *self;
        dq.normalize();
        dq
    }

    pub fn conjugate(&mut self) {
        self.real.conjugate();
        self.dual.conjugate();
    }

    pub fn conjugated(&self) -> Self {
        Self {
            real: self.real.conjugated(),
            dual: self.dual.conjugated(),
        }
    }

    pub fn dot(&self, other: &Self) -> f32 {
        self.real.x * other.real.x
            + self.real.y * other.real.y
            + self.real.z * other.real.z
            + self.real.w * other.real.w
    }

    pub fn blend(dqs: &[DualQuaternion], weights: &[f32]) -> DualQuaternion {
        if dqs.is_empty() {
            return DualQuaternion::identity();
        }
        let ref_dq = &dqs[0];
        let mut sum_real = Quaternion::new(0.0, 0.0, 0.0, 0.0);
        let mut sum_dual = Quaternion::new(0.0, 0.0, 0.0, 0.0);

        for (dq, &w) in dqs.iter().zip(weights.iter()) {
            if w <= 0.0 {
                continue;
            }
            // Antipodal sign check: dot(q0, qi) < 0.0
            let sign = if ref_dq.dot(dq) < 0.0 { -1.0 } else { 1.0 };
            let sw = w * sign;

            sum_real.x += dq.real.x * sw;
            sum_real.y += dq.real.y * sw;
            sum_real.z += dq.real.z * sw;
            sum_real.w += dq.real.w * sw;

            sum_dual.x += dq.dual.x * sw;
            sum_dual.y += dq.dual.y * sw;
            sum_dual.z += dq.dual.z * sw;
            sum_dual.w += dq.dual.w * sw;
        }

        let mut blended = DualQuaternion::new(sum_real, sum_dual);
        blended.normalize();
        blended
    }

    pub fn transform_point(&self, point: [f32; 3]) -> [f32; 3] {
        let q0 = self.real.normalized();
        let r_xyz = [q0.x, q0.y, q0.z];
        let r_w = q0.w;

        // Rotation: rot_p = p + 2.0 * cross(r_xyz, cross(r_xyz, p) + r_w * p)
        let cx1 = r_xyz[1] * point[2] - r_xyz[2] * point[1] + r_w * point[0];
        let cy1 = r_xyz[2] * point[0] - r_xyz[0] * point[2] + r_w * point[1];
        let cz1 = r_xyz[0] * point[1] - r_xyz[1] * point[0] + r_w * point[2];

        let cx2 = r_xyz[1] * cz1 - r_xyz[2] * cy1;
        let cy2 = r_xyz[2] * cx1 - r_xyz[0] * cz1;
        let cz2 = r_xyz[0] * cy1 - r_xyz[1] * cx1;

        let rot_p = [
            point[0] + 2.0 * cx2,
            point[1] + 2.0 * cy2,
            point[2] + 2.0 * cz2,
        ];

        let trans = self.get_translation();
        [
            rot_p[0] + trans[0],
            rot_p[1] + trans[1],
            rot_p[2] + trans[2],
        ]
    }

    pub fn transform_vector(&self, vector: [f32; 3]) -> [f32; 3] {
        let q0 = self.real.normalized();
        let r_xyz = [q0.x, q0.y, q0.z];
        let r_w = q0.w;

        let cx1 = r_xyz[1] * vector[2] - r_xyz[2] * vector[1] + r_w * vector[0];
        let cy1 = r_xyz[2] * vector[0] - r_xyz[0] * vector[2] + r_w * vector[1];
        let cz1 = r_xyz[0] * vector[1] - r_xyz[1] * vector[0] + r_w * vector[2];

        let cx2 = r_xyz[1] * cz1 - r_xyz[2] * cy1;
        let cy2 = r_xyz[2] * cx1 - r_xyz[0] * cz1;
        let cz2 = r_xyz[0] * cy1 - r_xyz[1] * cx1;

        [
            vector[0] + 2.0 * cx2,
            vector[1] + 2.0 * cy2,
            vector[2] + 2.0 * cz2,
        ]
    }
}

fn matrix_to_quaternion(m: &Matrix4) -> Quaternion {
    let m00 = m.values[0];
    let m01 = m.values[4];
    let m02 = m.values[8];
    let m10 = m.values[1];
    let m11 = m.values[5];
    let m12 = m.values[9];
    let m20 = m.values[2];
    let m21 = m.values[6];
    let m22 = m.values[10];

    let trace = m00 + m11 + m22;
    if trace > 0.0 {
        let s = (trace + 1.0).sqrt() * 2.0;
        let w = 0.25 * s;
        let x = (m21 - m12) / s;
        let y = (m02 - m20) / s;
        let z = (m10 - m01) / s;
        Quaternion::new(x, y, z, w).normalized()
    } else if m00 > m11 && m00 > m22 {
        let s = (1.0 + m00 - m11 - m22).sqrt() * 2.0;
        let w = (m21 - m12) / s;
        let x = 0.25 * s;
        let y = (m01 + m10) / s;
        let z = (m02 + m20) / s;
        Quaternion::new(x, y, z, w).normalized()
    } else if m11 > m22 {
        let s = (1.0 + m11 - m00 - m22).sqrt() * 2.0;
        let w = (m02 - m20) / s;
        let x = (m01 + m10) / s;
        let y = 0.25 * s;
        let z = (m12 + m21) / s;
        Quaternion::new(x, y, z, w).normalized()
    } else {
        let s = (1.0 + m22 - m00 - m11).sqrt() * 2.0;
        let w = (m10 - m01) / s;
        let x = (m02 + m20) / s;
        let y = (m12 + m21) / s;
        let z = 0.25 * s;
        Quaternion::new(x, y, z, w).normalized()
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::f32::consts::PI;

    #[test]
    fn test_dual_quaternion_identity_and_matrix_roundtrip() {
        let dq = DualQuaternion::identity();
        assert_eq!(dq.real, Quaternion::IDENTITY);
        assert_eq!(dq.get_translation(), [0.0, 0.0, 0.0]);

        let mat = dq.to_matrix();
        assert!(mat.is_identity());

        let dq_from_mat = DualQuaternion::from_matrix(&mat);
        assert!((dq_from_mat.real.w - 1.0).abs() < 1e-5);
        assert_eq!(dq_from_mat.get_translation(), [0.0, 0.0, 0.0]);
    }

    #[test]
    fn test_dual_quaternion_transform_point_and_vector() {
        let q_rot = Quaternion::from_angle_axis(PI / 2.0, [0.0, 0.0, 1.0]); // 90 deg around Z
        let translation = [5.0, 10.0, -15.0];
        let dq = DualQuaternion::from_rotation_translation(q_rot, translation);

        let p = [1.0, 0.0, 0.0];
        let p_trans = dq.transform_point(p);
        // (1, 0, 0) rotated 90 deg around Z -> (0, 1, 0) + (5, 10, -15) = (5, 11, -15)
        assert!((p_trans[0] - 5.0).abs() < 1e-4);
        assert!((p_trans[1] - 11.0).abs() < 1e-4);
        assert!((p_trans[2] - (-15.0)).abs() < 1e-4);

        let v = [1.0, 0.0, 0.0];
        let v_trans = dq.transform_vector(v);
        // vector ignores translation
        assert!((v_trans[0] - 0.0).abs() < 1e-4);
        assert!((v_trans[1] - 1.0).abs() < 1e-4);
        assert!((v_trans[2] - 0.0).abs() < 1e-4);
    }

    #[test]
    fn test_volume_retention_at_acute_angles() {
        // Joint 0 at identity (0 deg rotation)
        let dq0 = DualQuaternion::identity();

        // Test 90 deg, 135 deg, and 180 deg rotations around Z axis
        let angles = [PI / 2.0, 3.0 * PI / 4.0, PI];

        for &angle in &angles {
            let q_rot = Quaternion::from_angle_axis(angle, [0.0, 0.0, 1.0]);
            let dq1 = DualQuaternion::from_rotation_translation(q_rot, [0.0, 0.0, 0.0]);

            // 50/50 blend between joint 0 and joint 1
            let blended = DualQuaternion::blend(&[dq0, dq1], &[0.5, 0.5]);

            // Point on skin mesh offset 1.0 unit from joint axis
            let p = [1.0, 0.0, 0.0];
            let p_blended = blended.transform_point(p);

            // Distance from joint axis (z-axis)
            let dist_from_axis = (p_blended[0] * p_blended[0] + p_blended[1] * p_blended[1]).sqrt();

            // Under DQS, distance from axis remains exactly 1.0 (no volume collapse!)
            assert!(
                (dist_from_axis - 1.0).abs() < 1e-4,
                "Volume collapsed at angle {}: dist={}",
                angle,
                dist_from_axis
            );
        }
    }

    #[test]
    fn test_antipodal_alignment_prevention() {
        let q0 = Quaternion::IDENTITY; // (0,0,0,1)
        let q1 = Quaternion::new(0.0, 0.0, 0.0, -1.0); // Antipodal orientation of same 360 deg rotation

        let dq0 = DualQuaternion::from_rotation_translation(q0, [0.0, 0.0, 0.0]);
        let dq1 = DualQuaternion::from_rotation_translation(q1, [0.0, 0.0, 0.0]);

        assert!(dq0.dot(&dq1) < 0.0);

        let blended = DualQuaternion::blend(&[dq0, dq1], &[0.5, 0.5]);
        let p = [1.0, 0.0, 0.0];
        let p_trans = blended.transform_point(p);

        // Should not flip or evaluate to zero vector
        assert!((p_trans[0] - 1.0).abs() < 1e-4);
        assert!((p_trans[1] - 0.0).abs() < 1e-4);
        assert!((p_trans[2] - 0.0).abs() < 1e-4);
    }
}
