/**
 * DualQuaternion math library for Dual Quaternion Skinning (DQS).
 * Real part represents rotation quaternion; dual part represents translation * rotation / 2.
 */

export class DualQuaternion {
  readonly real: Float32Array;
  readonly dual: Float32Array;

  constructor(real?: ArrayLike<number>, dual?: ArrayLike<number>) {
    this.real = new Float32Array(4);
    this.dual = new Float32Array(4);

    if (real && real.length >= 4) {
      this.real[0] = real[0];
      this.real[1] = real[1];
      this.real[2] = real[2];
      this.real[3] = real[3];
    } else {
      this.real[3] = 1.0; // identity rotation
    }

    if (dual && dual.length >= 4) {
      this.dual[0] = dual[0];
      this.dual[1] = dual[1];
      this.dual[2] = dual[2];
      this.dual[3] = dual[3];
    }
  }

  static identity(): DualQuaternion {
    return new DualQuaternion([0, 0, 0, 1], [0, 0, 0, 0]);
  }

  static fromRotationTranslation(rotation: ArrayLike<number>, translation: ArrayLike<number>): DualQuaternion {
    const rx = rotation[0], ry = rotation[1], rz = rotation[2], rw = rotation[3];
    const mag = Math.hypot(rx, ry, rz, rw);
    const invMag = mag > 1e-6 ? 1.0 / mag : 1.0;

    const q0x = rx * invMag;
    const q0y = ry * invMag;
    const q0z = rz * invMag;
    const q0w = rw * invMag;

    const tx = translation[0] || 0;
    const ty = translation[1] || 0;
    const tz = translation[2] || 0;

    // q_e = 0.5 * (tx, ty, tz, 0) * q0
    const qex = 0.5 * (tx * q0w + ty * q0z - tz * q0y);
    const qey = 0.5 * (ty * q0w + tz * q0x - tx * q0z);
    const qez = 0.5 * (tz * q0w + tx * q0y - ty * q0x);
    const qew = -0.5 * (tx * q0x + ty * q0y + tz * q0z);

    return new DualQuaternion([q0x, q0y, q0z, q0w], [qex, qey, qez, qew]);
  }

  static fromMatrix(matrix: ArrayLike<number>): DualQuaternion {
    if (!matrix || matrix.length < 16) {
      return DualQuaternion.identity();
    }
    const m00 = matrix[0], m01 = matrix[4], m02 = matrix[8];
    const m10 = matrix[1], m11 = matrix[5], m12 = matrix[9];
    const m20 = matrix[2], m21 = matrix[6], m22 = matrix[10];

    const trace = m00 + m11 + m22;
    let q0x = 0, q0y = 0, q0z = 0, q0w = 1;

    if (trace > 0.0) {
      const s = Math.sqrt(trace + 1.0) * 2.0;
      q0w = 0.25 * s;
      q0x = (m21 - m12) / s;
      q0y = (m02 - m20) / s;
      q0z = (m10 - m01) / s;
    } else if (m00 > m11 && m00 > m22) {
      const s = Math.sqrt(1.0 + m00 - m11 - m22) * 2.0;
      q0w = (m21 - m12) / s;
      q0x = 0.25 * s;
      q0y = (m01 + m10) / s;
      q0z = (m02 + m20) / s;
    } else if (m11 > m22) {
      const s = Math.sqrt(1.0 + m11 - m00 - m22) * 2.0;
      q0w = (m02 - m20) / s;
      q0x = (m01 + m10) / s;
      q0y = 0.25 * s;
      q0z = (m12 + m21) / s;
    } else {
      const s = Math.sqrt(1.0 + m22 - m00 - m11) * 2.0;
      q0w = (m10 - m01) / s;
      q0x = (m02 + m20) / s;
      q0y = (m12 + m21) / s;
      q0z = 0.25 * s;
    }

    const tx = matrix[12];
    const ty = matrix[13];
    const tz = matrix[14];

    return DualQuaternion.fromRotationTranslation([q0x, q0y, q0z, q0w], [tx, ty, tz]);
  }

  getTranslation(): [number, number, number] {
    const rx = this.real[0], ry = this.real[1], rz = this.real[2], rw = this.real[3];
    const dx = this.dual[0], dy = this.dual[1], dz = this.dual[2], dw = this.dual[3];

    const tx = 2.0 * (rw * dx - dw * rx + ry * dz - rz * dy);
    const ty = 2.0 * (rw * dy - dw * ry + rz * dx - rx * dz);
    const tz = 2.0 * (rw * dz - dw * rz + rx * dy - ry * dx);

    return [tx, ty, tz];
  }

  toMatrix(out?: Float32Array): Float32Array {
    const m = out || new Float32Array(16);
    const rx = this.real[0], ry = this.real[1], rz = this.real[2], rw = this.real[3];
    const [tx, ty, tz] = this.getTranslation();

    const xx = rx * rx, xy = rx * ry, xz = rx * rz, xw = rx * rw;
    const yy = ry * ry, yz = ry * rz, yw = ry * rw;
    const zz = rz * rz, zw = rz * rw;

    m[0] = 1 - 2 * (yy + zz);
    m[1] = 2 * (xy + zw);
    m[2] = 2 * (xz - yw);
    m[3] = 0;

    m[4] = 2 * (xy - zw);
    m[5] = 1 - 2 * (xx + zz);
    m[6] = 2 * (yz + xw);
    m[7] = 0;

    m[8] = 2 * (xz + yw);
    m[9] = 2 * (yz - xw);
    m[10] = 1 - 2 * (xx + yy);
    m[11] = 0;

    m[12] = tx;
    m[13] = ty;
    m[14] = tz;
    m[15] = 1;

    return m;
  }

  normalize(): DualQuaternion {
    const rx = this.real[0], ry = this.real[1], rz = this.real[2], rw = this.real[3];
    const mag = Math.hypot(rx, ry, rz, rw);

    if (mag > 1e-6) {
      const inv = 1.0 / mag;
      this.real[0] *= inv;
      this.real[1] *= inv;
      this.real[2] *= inv;
      this.real[3] *= inv;

      this.dual[0] *= inv;
      this.dual[1] *= inv;
      this.dual[2] *= inv;
      this.dual[3] *= inv;
    } else {
      this.real[0] = 0; this.real[1] = 0; this.real[2] = 0; this.real[3] = 1;
      this.dual[0] = 0; this.dual[1] = 0; this.dual[2] = 0; this.dual[3] = 0;
    }
    return this;
  }

  conjugate(): DualQuaternion {
    this.real[0] = -this.real[0];
    this.real[1] = -this.real[1];
    this.real[2] = -this.real[2];

    this.dual[0] = -this.dual[0];
    this.dual[1] = -this.dual[1];
    this.dual[2] = -this.dual[2];
    return this;
  }

  dot(other: DualQuaternion): number {
    return (
      this.real[0] * other.real[0] +
      this.real[1] * other.real[1] +
      this.real[2] * other.real[2] +
      this.real[3] * other.real[3]
    );
  }

  static blend(dqs: DualQuaternion[], weights: ArrayLike<number>): DualQuaternion {
    if (!dqs.length) return DualQuaternion.identity();
    const ref = dqs[0];

    let srx = 0, sry = 0, srz = 0, srw = 0;
    let sdx = 0, sdy = 0, sdz = 0, sdw = 0;

    for (let i = 0; i < dqs.length; i++) {
      const w = weights[i];
      if (!(w > 0)) continue;
      const dq = dqs[i];
      const sign = ref.dot(dq) < 0 ? -1 : 1;
      const sw = w * sign;

      srx += dq.real[0] * sw;
      sry += dq.real[1] * sw;
      srz += dq.real[2] * sw;
      srw += dq.real[3] * sw;

      sdx += dq.dual[0] * sw;
      sdy += dq.dual[1] * sw;
      sdz += dq.dual[2] * sw;
      sdw += dq.dual[3] * sw;
    }

    const blended = new DualQuaternion([srx, sry, srz, srw], [sdx, sdy, sdz, sdw]);
    blended.normalize();
    return blended;
  }

  transformPoint(p: ArrayLike<number>): [number, number, number] {
    const rx = this.real[0], ry = this.real[1], rz = this.real[2], rw = this.real[3];
    const px = p[0], py = p[1], pz = p[2];

    const cx1 = ry * pz - rz * py + rw * px;
    const cy1 = rz * px - rx * pz + rw * py;
    const cz1 = rx * py - ry * px + rw * pz;

    const cx2 = ry * cz1 - rz * cy1;
    const cy2 = rz * cx1 - rx * cz1;
    const cz2 = rx * cy1 - ry * cx1;

    const rotX = px + 2.0 * cx2;
    const rotY = py + 2.0 * cy2;
    const rotZ = pz + 2.0 * cz2;

    const [tx, ty, tz] = this.getTranslation();

    return [rotX + tx, rotY + ty, rotZ + tz];
  }

  transformVector(v: ArrayLike<number>): [number, number, number] {
    const rx = this.real[0], ry = this.real[1], rz = this.real[2], rw = this.real[3];
    const vx = v[0], vy = v[1], vz = v[2];

    const cx1 = ry * vz - rz * vy + rw * vx;
    const cy1 = rz * vx - rx * vz + rw * vy;
    const cz1 = rx * vy - ry * vx + rw * vz;

    const cx2 = ry * cz1 - rz * cy1;
    const cy2 = rz * cx1 - rx * cz1;
    const cz2 = rx * cy1 - ry * cx1;

    return [vx + 2.0 * cx2, vy + 2.0 * cy2, vz + 2.0 * cz2];
  }
}
