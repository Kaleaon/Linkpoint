use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Copy, PartialEq, Serialize, Deserialize)]
pub struct Quaternion {
    pub x: f32,
    pub y: f32,
    pub z: f32,
    pub w: f32,
}

impl Default for Quaternion {
    fn default() -> Self {
        Self::identity()
    }
}

impl Quaternion {
    pub const IDENTITY: Self = Self {
        x: 0.0,
        y: 0.0,
        z: 0.0,
        w: 1.0,
    };

    pub fn new(x: f32, y: f32, z: f32, w: f32) -> Self {
        Self { x, y, z, w }
    }

    pub fn identity() -> Self {
        Self::IDENTITY
    }

    pub fn is_identity(&self) -> bool {
        self.x == 0.0 && self.y == 0.0 && self.z == 0.0 && self.w == 1.0
    }

    pub fn is_finite(&self) -> bool {
        self.x.is_finite() && self.y.is_finite() && self.z.is_finite() && self.w.is_finite()
    }

    pub fn is_equal_eps(&self, other: &Self, epsilon: f32) -> bool {
        (self.x - other.x).abs() < epsilon
            && (self.y - other.y).abs() < epsilon
            && (self.z - other.z).abs() < epsilon
            && (self.w - other.w).abs() < epsilon
    }

    pub fn magnitude(&self) -> f32 {
        (self.x * self.x + self.y * self.y + self.z * self.z + self.w * self.w).sqrt()
    }

    pub fn normalize(&mut self) -> f32 {
        let mag = self.magnitude();
        if mag > 1e-6 {
            if (1.0 - mag).abs() > 1e-6 {
                let oom = 1.0 / mag;
                self.x *= oom;
                self.y *= oom;
                self.z *= oom;
                self.w *= oom;
            }
        } else {
            *self = Self::identity();
        }
        mag
    }

    pub fn normalized(&self) -> Self {
        let mut q = *self;
        q.normalize();
        q
    }

    pub fn conjugate(&mut self) {
        self.x = -self.x;
        self.y = -self.y;
        self.z = -self.z;
    }

    pub fn conjugated(&self) -> Self {
        Self::new(-self.x, -self.y, -self.z, self.w)
    }

    pub fn from_angle_axis(angle: f32, axis: [f32; 3]) -> Self {
        let mag = (axis[0] * axis[0] + axis[1] * axis[1] + axis[2] * axis[2]).sqrt();
        if mag > 1e-6 {
            let half = angle * 0.5;
            let c = half.cos();
            let s = half.sin() / mag;
            Self::new(axis[0] * s, axis[1] * s, axis[2] * s, c)
        } else {
            Self::identity()
        }
    }

    pub fn to_angle_axis(&self) -> (f32, [f32; 3]) {
        let v = (self.x * self.x + self.y * self.y + self.z * self.z).sqrt();
        if v > 1e-6 {
            let mut oom = 1.0 / v;
            let mut ww = self.w;
            if ww < 0.0 {
                ww = -ww;
                oom = -oom;
            }
            let angle = 2.0 * v.atan2(ww);
            (angle, [self.x * oom, self.y * oom, self.z * oom])
        } else {
            (0.0, [0.0, 0.0, 1.0])
        }
    }

    pub fn dot(&self, other: &Self) -> f32 {
        self.x * other.x + self.y * other.y + self.z * other.z + self.w * other.w
    }

    pub fn rotate_vector3(&self, v: [f32; 3]) -> [f32; 3] {
        let rw = -self.x * v[0] - self.y * v[1] - self.z * v[2];
        let rx = self.w * v[0] + self.y * v[2] - self.z * v[1];
        let ry = self.w * v[1] + self.z * v[0] - self.x * v[2];
        let rz = self.w * v[2] + self.x * v[1] - self.y * v[0];

        [
            -rw * self.x + rx * self.w - ry * self.z + rz * self.y,
            -rw * self.y + ry * self.w - rz * self.x + rx * self.z,
            -rw * self.z + rz * self.w - rx * self.y + ry * self.x,
        ]
    }

    pub fn rotate_vector4(&self, v: [f32; 4]) -> [f32; 4] {
        let r = self.rotate_vector3([v[0], v[1], v[2]]);
        [r[0], r[1], r[2], v[3]]
    }

    pub fn mul_quaternion(&self, b: &Self) -> Self {
        Self::new(
            b.w * self.x + b.x * self.w + b.y * self.z - b.z * self.y,
            b.w * self.y + b.y * self.w + b.z * self.x - b.x * self.z,
            b.w * self.z + b.z * self.w + b.x * self.y - b.y * self.x,
            b.w * self.w - b.x * self.x - b.y * self.y - b.z * self.z,
        )
    }

    pub fn lerp(t: f32, a: &Self, b: &Self) -> Self {
        let inv_t = 1.0 - t;
        let mut r = Self::new(
            t * b.x + inv_t * a.x,
            t * b.y + inv_t * a.y,
            t * b.z + inv_t * a.z,
            t * b.w + inv_t * a.w,
        );
        r.normalize();
        r
    }

    pub fn slerp(t: f32, a: &Self, b: &Self) -> Self {
        let mut cos_theta = a.dot(b);
        let b_flip = if cos_theta < 0.0 {
            cos_theta = -cos_theta;
            true
        } else {
            false
        };

        let (alpha, beta) = if 1.0 - cos_theta < 0.00001 {
            (t, 1.0 - t)
        } else {
            let theta = cos_theta.acos();
            let sin_theta = theta.sin();
            (
                (t * theta).sin() / sin_theta,
                ((1.0 - t) * theta).sin() / sin_theta,
            )
        };

        let b_factor = if b_flip { -beta } else { beta };
        let mut r = Self::new(
            b_factor * a.x + alpha * b.x,
            b_factor * a.y + alpha * b.y,
            b_factor * a.z + alpha * b.z,
            b_factor * a.w + alpha * b.w,
        );
        r.normalize();
        r
    }

    pub fn nlerp(t: f32, a: &Self, b: &Self) -> Self {
        if a.dot(b) < 0.0 {
            Self::slerp(t, a, b)
        } else {
            Self::lerp(t, a, b)
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_quaternion_identity() {
        let q = Quaternion::identity();
        assert!(q.is_identity());
        assert_eq!(q.magnitude(), 1.0);
    }

    #[test]
    fn test_quaternion_rotate() {
        let q = Quaternion::from_angle_axis(std::f32::consts::FRAC_PI_2, [0.0, 0.0, 1.0]);
        let v = [1.0, 0.0, 0.0];
        let rotated = q.rotate_vector3(v);
        assert!((rotated[0] - 0.0).abs() < 1e-4);
        assert!((rotated[1] - 1.0).abs() < 1e-4);
        assert!((rotated[2] - 0.0).abs() < 1e-4);
    }

    #[test]
    fn test_quaternion_slerp() {
        let q1 = Quaternion::identity();
        let q2 = Quaternion::from_angle_axis(std::f32::consts::FRAC_PI_2, [0.0, 0.0, 1.0]);
        let mid = Quaternion::slerp(0.5, &q1, &q2);
        let expected = Quaternion::from_angle_axis(std::f32::consts::FRAC_PI_4, [0.0, 0.0, 1.0]);
        assert!(mid.is_equal_eps(&expected, 1e-4));
    }
}
