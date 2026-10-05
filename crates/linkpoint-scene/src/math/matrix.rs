use super::quaternion::Quaternion;
use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Copy, PartialEq, Serialize, Deserialize)]
pub struct Matrix4 {
    pub values: [f32; 16],
}

impl Default for Matrix4 {
    fn default() -> Self {
        Self::identity()
    }
}

impl Matrix4 {
    pub const IDENTITY: Self = Self {
        values: [
            1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0,
        ],
    };

    pub fn new(values: [f32; 16]) -> Self {
        Self { values }
    }

    pub fn identity() -> Self {
        Self::IDENTITY
    }

    pub fn is_identity(&self) -> bool {
        self.values == Self::IDENTITY.values
    }

    pub fn get(&self, row: usize, col: usize) -> f32 {
        self.values[row * 4 + col]
    }

    pub fn set(&mut self, row: usize, col: usize, v: f32) {
        self.values[row * 4 + col] = v;
    }

    pub fn from_quaternion(q: &Quaternion) -> Self {
        let xx = q.x * q.x;
        let xy = q.x * q.y;
        let xz = q.x * q.z;
        let xw = q.x * q.w;
        let yy = q.y * q.y;
        let yz = q.y * q.z;
        let yw = q.y * q.w;
        let zz = q.z * q.z;
        let zw = q.z * q.w;

        Self {
            values: [
                1.0 - 2.0 * (yy + zz),
                2.0 * (xy + zw),
                2.0 * (xz - yw),
                0.0,
                2.0 * (xy - zw),
                1.0 - 2.0 * (xx + zz),
                2.0 * (yz + xw),
                0.0,
                2.0 * (xz + yw),
                2.0 * (yz - xw),
                1.0 - 2.0 * (xx + yy),
                0.0,
                0.0,
                0.0,
                0.0,
                1.0,
            ],
        }
    }

    pub fn from_quaternion_and_translation(q: &Quaternion, pos: [f32; 3]) -> Self {
        let mut m = Self::from_quaternion(q);
        m.values[12] = pos[0];
        m.values[13] = pos[1];
        m.values[14] = pos[2];
        m
    }

    pub fn set_translation(&mut self, x: f32, y: f32, z: f32) {
        self.values[12] = x;
        self.values[13] = y;
        self.values[14] = z;
    }

    pub fn get_translation(&self) -> [f32; 3] {
        [self.values[12], self.values[13], self.values[14]]
    }

    pub fn transpose(&mut self) {
        let mut t = [0.0f32; 16];
        for i in 0..4 {
            for j in 0..4 {
                t[j * 4 + i] = self.values[i * 4 + j];
            }
        }
        self.values = t;
    }

    pub fn transposed(&self) -> Self {
        let mut m = *self;
        m.transpose();
        m
    }

    pub fn determinant(&self) -> f32 {
        let m = &self.values;
        m[3] * m[6] * m[9] * m[12] - m[2] * m[7] * m[9] * m[12] - m[3] * m[5] * m[10] * m[12]
            + m[1] * m[7] * m[10] * m[12]
            + m[2] * m[5] * m[11] * m[12]
            - m[1] * m[6] * m[11] * m[12]
            - m[3] * m[6] * m[8] * m[13]
            + m[2] * m[7] * m[8] * m[13]
            + m[3] * m[4] * m[10] * m[13]
            - m[0] * m[7] * m[10] * m[13]
            - m[2] * m[4] * m[11] * m[13]
            + m[0] * m[6] * m[11] * m[13]
            + m[3] * m[5] * m[8] * m[14]
            - m[1] * m[7] * m[8] * m[14]
            - m[3] * m[4] * m[9] * m[14]
            + m[0] * m[7] * m[9] * m[14]
            + m[1] * m[4] * m[11] * m[14]
            - m[0] * m[5] * m[11] * m[14]
            - m[2] * m[5] * m[8] * m[15]
            + m[1] * m[6] * m[8] * m[15]
            + m[2] * m[4] * m[9] * m[15]
            - m[0] * m[6] * m[9] * m[15]
            - m[1] * m[4] * m[10] * m[15]
            + m[0] * m[5] * m[10] * m[15]
    }

    pub fn inverse(&mut self) -> bool {
        let det = self.determinant();
        if det.abs() < 1e-8 {
            return false;
        }

        let mut t;
        t = self.values[1];
        self.values[1] = self.values[4];
        self.values[4] = t;
        t = self.values[2];
        self.values[2] = self.values[8];
        self.values[8] = t;
        t = self.values[6];
        self.values[6] = self.values[9];
        self.values[9] = t;

        for j in 0..3 {
            self.values[j * 4 + 3] = self.values[12] * self.values[j]
                + self.values[13] * self.values[4 + j]
                + self.values[14] * self.values[8 + j];
        }
        self.values[12] = -self.values[3];
        self.values[13] = -self.values[7];
        self.values[14] = -self.values[11];
        self.values[3] = 0.0;
        self.values[7] = 0.0;
        self.values[11] = 0.0;

        true
    }

    pub fn inversed(&self) -> Option<Self> {
        let mut m = *self;
        if m.inverse() { Some(m) } else { None }
    }

    pub fn mul_matrix(&self, b: &Self) -> Self {
        let mut r = [0.0f32; 16];
        for j in 0..4 {
            for i in 0..4 {
                r[j * 4 + i] = self.values[j * 4] * b.values[i]
                    + self.values[j * 4 + 1] * b.values[4 + i]
                    + self.values[j * 4 + 2] * b.values[8 + i]
                    + self.values[j * 4 + 3] * b.values[12 + i];
            }
        }
        Self { values: r }
    }

    pub fn transform_vector3(&self, a: [f32; 3]) -> [f32; 3] {
        [
            a[0] * self.values[0] + a[1] * self.values[4] + a[2] * self.values[8] + self.values[12],
            a[0] * self.values[1] + a[1] * self.values[5] + a[2] * self.values[9] + self.values[13],
            a[0] * self.values[2]
                + a[1] * self.values[6]
                + a[2] * self.values[10]
                + self.values[14],
        ]
    }

    pub fn transform_vector4(&self, a: [f32; 4]) -> [f32; 4] {
        [
            a[0] * self.values[0]
                + a[1] * self.values[4]
                + a[2] * self.values[8]
                + a[3] * self.values[12],
            a[0] * self.values[1]
                + a[1] * self.values[5]
                + a[2] * self.values[9]
                + a[3] * self.values[13],
            a[0] * self.values[2]
                + a[1] * self.values[6]
                + a[2] * self.values[10]
                + a[3] * self.values[14],
            a[0] * self.values[3]
                + a[1] * self.values[7]
                + a[2] * self.values[11]
                + a[3] * self.values[15],
        ]
    }

    pub fn rotate_vector3(&self, a: [f32; 3]) -> [f32; 3] {
        [
            a[0] * self.values[0] + a[1] * self.values[4] + a[2] * self.values[8],
            a[0] * self.values[1] + a[1] * self.values[5] + a[2] * self.values[9],
            a[0] * self.values[2] + a[1] * self.values[6] + a[2] * self.values[10],
        ]
    }

    pub fn to_quaternion(&self) -> Quaternion {
        let tr = self.values[0] + self.values[5] + self.values[10];
        let nxt = [1, 2, 0];
        let mut q = [0.0f32; 4];
        if tr > 0.0 {
            let mut s = (tr + 1.0).sqrt();
            q[3] = s * 0.5;
            s = 0.5 / s;
            q[0] = (self.values[6] - self.values[9]) * s;
            q[1] = (self.values[8] - self.values[2]) * s;
            q[2] = (self.values[1] - self.values[4]) * s;
        } else {
            let diag = [0, 5, 10];
            let mut i = 0;
            if self.values[5] > self.values[0] {
                i = 1;
            }
            if self.values[10] > self.values[diag[i]] {
                i = 2;
            }
            let j = nxt[i];
            let k = nxt[j];
            let mut s = ((self.values[diag[i]] - (self.values[diag[j]] + self.values[diag[k]]))
                + 1.0)
                .sqrt();
            q[i] = s * 0.5;
            if s != 0.0 {
                s = 0.5 / s;
            }
            q[3] = (self.values[j * 4 + k] - self.values[k * 4 + j]) * s;
            q[j] = (self.values[i * 4 + j] + self.values[j * 4 + i]) * s;
            q[k] = (self.values[i * 4 + k] + self.values[k * 4 + i]) * s;
        }
        let mut result = Quaternion::new(q[0], q[1], q[2], q[3]);
        result.normalize();
        result
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_matrix_identity() {
        let m = Matrix4::identity();
        assert!(m.is_identity());
        let v = [1.0, 2.0, 3.0];
        assert_eq!(m.transform_vector3(v), v);
    }

    #[test]
    fn test_matrix_quaternion_roundtrip() {
        let q = Quaternion::from_angle_axis(0.5, [0.0, 1.0, 0.0]);
        let m = Matrix4::from_quaternion(&q);
        let q2 = m.to_quaternion();
        let neg_q2 = Quaternion::new(-q2.x, -q2.y, -q2.z, -q2.w);
        assert!(q.is_equal_eps(&q2, 1e-3) || q.is_equal_eps(&neg_q2, 1e-3));
    }

    #[test]
    fn test_matrix_translation() {
        let mut m = Matrix4::identity();
        m.set_translation(10.0, 20.0, 30.0);
        let v = [1.0, 1.0, 1.0];
        let res = m.transform_vector3(v);
        assert_eq!(res, [11.0, 21.0, 31.0]);
    }
}
