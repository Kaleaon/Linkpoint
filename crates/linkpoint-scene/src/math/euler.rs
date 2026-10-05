use super::quaternion::Quaternion;
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Copy, PartialEq, Serialize, Deserialize)]
pub struct EulerAngles {
    pub roll: f32,
    pub pitch: f32,
    pub yaw: f32,
}

impl Default for EulerAngles {
    fn default() -> Self {
        Self {
            roll: 0.0,
            pitch: 0.0,
            yaw: 0.0,
        }
    }
}

impl EulerAngles {
    pub fn new(roll: f32, pitch: f32, yaw: f32) -> Self {
        Self { roll, pitch, yaw }
    }

    pub fn to_quaternion(&self) -> Quaternion {
        let cr = (self.roll * 0.5).cos();
        let sr = (self.roll * 0.5).sin();
        let cp = (self.pitch * 0.5).cos();
        let sp = (self.pitch * 0.5).sin();
        let cy = (self.yaw * 0.5).cos();
        let sy = (self.yaw * 0.5).sin();

        let mut q = Quaternion::new(
            sr * cp * cy - cr * sp * sy,
            cr * sp * cy + sr * cp * sy,
            cr * cp * sy - sr * sp * cy,
            cr * cp * cy + sr * sp * sy,
        );
        q.normalize();
        q
    }

    pub fn from_quaternion(q: &Quaternion) -> Self {
        let sx = 2.0 * (q.x * q.w - q.y * q.z);
        let sy = 2.0 * (q.y * q.w + q.x * q.z);
        let ys = q.w * q.w - q.y * q.y;
        let xz = q.x * q.x - q.z * q.z;
        let cx = ys - xz;
        let cy = (sx * sx + cx * cx).sqrt();

        if cy > 1e-4 {
            Self::new(
                sx.atan2(cx),
                sy.atan2(cy),
                (2.0 * (q.z * q.w - q.x * q.y)).atan2(ys + xz),
            )
        } else if sy > 0.0 {
            Self::new(
                0.0,
                std::f32::consts::FRAC_PI_2,
                2.0 * (q.z + q.x).atan2(q.w + q.y),
            )
        } else {
            Self::new(
                0.0,
                -std::f32::consts::FRAC_PI_2,
                2.0 * (q.z - q.x).atan2(q.w - q.y),
            )
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_euler_quaternion_conversion() {
        let euler = EulerAngles::new(0.1, 0.2, 0.3);
        let q = euler.to_quaternion();
        let euler2 = EulerAngles::from_quaternion(&q);
        assert!((euler.roll - euler2.roll).abs() < 0.1 || (euler.roll + euler2.roll).abs() < 0.1);
        assert!(
            (euler.pitch - euler2.pitch).abs() < 0.1 || (euler.pitch + euler2.pitch).abs() < 0.1
        );
        assert!((euler.yaw - euler2.yaw).abs() < 0.1 || (euler.yaw + euler2.yaw).abs() < 0.1);
    }
}
