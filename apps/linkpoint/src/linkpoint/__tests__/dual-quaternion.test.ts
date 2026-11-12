import { describe, expect, it } from 'vitest';
import { DualQuaternion } from '../dual-quaternion';

describe('DualQuaternion Math Library', () => {
  it('creates identity dual quaternion and converts to/from matrix', () => {
    const dq = DualQuaternion.identity();
    expect(dq.real[3]).toBe(1.0);
    expect(dq.getTranslation()).toEqual([0, 0, 0]);

    const m = dq.toMatrix();
    expect(Array.from(m)).toEqual([
      1, 0, 0, 0,
      0, 1, 0, 0,
      0, 0, 1, 0,
      0, 0, 0, 1,
    ]);

    const roundtrip = DualQuaternion.fromMatrix(m);
    expect(roundtrip.real[3]).toBeCloseTo(1.0);
    expect(roundtrip.getTranslation()).toEqual([0, 0, 0]);
  });

  it('transforms point and vector correctly with rotation and translation', () => {
    // 90 degree Z rotation
    const qRot = [0, 0, Math.SQRT1_2, Math.SQRT1_2];
    const trans = [5, 10, -15];
    const dq = DualQuaternion.fromRotationTranslation(qRot, trans);

    const pointTrans = dq.transformPoint([1, 0, 0]);
    expect(pointTrans[0]).toBeCloseTo(5.0, 4);
    expect(pointTrans[1]).toBeCloseTo(11.0, 4);
    expect(pointTrans[2]).toBeCloseTo(-15.0, 4);

    const vecTrans = dq.transformVector([1, 0, 0]);
    expect(vecTrans[0]).toBeCloseTo(0.0, 4);
    expect(vecTrans[1]).toBeCloseTo(1.0, 4);
    expect(vecTrans[2]).toBeCloseTo(0.0, 4);
  });

  it('maintains volume retention at 90, 135, and 180 degrees', () => {
    const dq0 = DualQuaternion.identity();
    const angles = [Math.PI / 2, (3 * Math.PI) / 4, Math.PI];

    for (const angle of angles) {
      const half = angle * 0.5;
      const qRot = [0, 0, Math.sin(half), Math.cos(half)];
      const dq1 = DualQuaternion.fromRotationTranslation(qRot, [0, 0, 0]);

      const blended = DualQuaternion.blend([dq0, dq1], [0.5, 0.5]);
      const point = [1, 0, 0];
      const pTrans = blended.transformPoint(point);

      const dist = Math.hypot(pTrans[0], pTrans[1]);
      expect(dist).toBeCloseTo(1.0, 4); // No volume collapse!
    }
  });

  it('handles antipodal sign alignment check correctly', () => {
    const dq0 = DualQuaternion.fromRotationTranslation([0, 0, 0, 1], [0, 0, 0]);
    const dq1 = DualQuaternion.fromRotationTranslation([0, 0, 0, -1], [0, 0, 0]); // antipodal

    expect(dq0.dot(dq1)).toBeLessThan(0);

    const blended = DualQuaternion.blend([dq0, dq1], [0.5, 0.5]);
    const pTrans = blended.transformPoint([1, 0, 0]);

    expect(pTrans[0]).toBeCloseTo(1.0, 4);
    expect(pTrans[1]).toBeCloseTo(0.0, 4);
    expect(pTrans[2]).toBeCloseTo(0.0, 4);
  });
});
