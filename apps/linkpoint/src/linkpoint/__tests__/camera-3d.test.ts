import { describe, expect, it } from 'vitest';
import { Camera3D } from '../camera-3d';

describe('Camera3D viewer controls', () => {
  it('moves the orbit focus instead of having movement overwritten by the orbit calculation', () => {
    const camera = new Camera3D();
    camera.setOrbitTarget(10, 20, 30);
    camera.setRotation(0, 0);
    camera.move(2, 3, 1);

    // Orbit camera sits at +Y of its target looking towards -Y: forward goes -Y, right goes -X.
    expect(camera.orbitTarget).toEqual([7, 18, 31]);
    expect(camera.position).not.toEqual(camera.orbitTarget);
  });

  it('provides rear, front, mouselook, and free camera setups', () => {
    const camera = new Camera3D();
    camera.setOrbitTarget(4, 5, 6);

    camera.setPreset('first-person');
    expect(camera.mode).toBe('first-person');
    expect(camera.position).toEqual([4, 5, 7.65]);

    camera.setPreset('front');
    expect(camera.mode).toBe('orbit');
    expect(camera.orbitDistance).toBe(7.5);
  });

  it('pans the orbit target without changing its distance', () => {
    const camera = new Camera3D();
    camera.setOrbitTarget(10, 10, 10);
    camera.setRotation(0, 0);
    camera.pan(2, -3);

    expect(camera.orbitTarget).toEqual([8, 10, 7]); // screen-right is -X for this view
    expect(camera.orbitDistance).toBe(10);
  });

  it('computes the view-projection matrix as projection * view (column-major)', () => {
    const camera = new Camera3D();
    camera.setOrbitTarget(10, 10, 10);
    camera.updateMatrices();
    const apply = (m: Float32Array, v: number[]) => [0, 1, 2, 3].map((r) => m[r] * v[0] + m[4 + r] * v[1] + m[8 + r] * v[2] + m[12 + r] * v[3]);
    const point = [12, 10, 10, 1];

    const expected = apply(camera.getProjectionMatrix(), apply(camera.getViewMatrix(), point));
    const actual = apply(camera.getViewProjectionMatrix(), point);
    actual.forEach((value, index) => expect(value).toBeCloseTo(expected[index], 3));
    // The orbit target projects to the centre of the screen.
    const centre = apply(camera.getViewProjectionMatrix(), [10, 10, 10, 1]);
    expect(centre[0] / centre[3]).toBeCloseTo(0, 4);
    expect(centre[1] / centre[3]).toBeCloseTo(0, 4);
  });

  describe('movement follows what is on screen', () => {
    const setups: Array<[string, (c: Camera3D, yaw: number, pitch: number) => void]> = [
      ['orbit', (c, yaw, pitch) => { c.mode = 'orbit'; c.setOrbitTarget(100, 100, 30); c.orbitDistance = 12; c.setRotation(pitch, yaw); }],
      ['first-person', (c, yaw, pitch) => { c.mode = 'first-person'; c.position = [100, 100, 30]; c.setRotation(pitch, yaw); }],
    ];
    const viewSpace = (c: Camera3D, p: number[]) => {
      const v = c.getViewMatrix();
      return [0, 1, 2].map((r) => v[r] * p[0] + v[4 + r] * p[1] + v[8 + r] * p[2] + v[12 + r]);
    };

    for (const [name, setup] of setups) {
      for (const yaw of [0, 1.1, 2.6, -2.2]) {
        for (const pitch of [0, -0.4]) {
          it(`${name} yaw ${yaw} pitch ${pitch}: forward approaches, right moves the world left, up rises`, () => {
            const camera = new Camera3D();
            setup(camera, yaw, pitch);
            const probe = [100 + 20 * Math.sin(yaw) * (name === 'orbit' ? -1 : 1), 100 + 20 * Math.cos(yaw) * (name === 'orbit' ? -1 : 1), 30];

            const before = viewSpace(camera, probe);
            camera.move(5, 0, 0);
            expect(-viewSpace(camera, probe)[2]).toBeLessThan(-before[2]); // closer along the view axis

            setup(camera, yaw, pitch);
            camera.move(0, 5, 0);
            expect(viewSpace(camera, probe)[0]).toBeLessThan(before[0] - 4.5); // we moved right, so it slides left on screen

            setup(camera, yaw, pitch);
            camera.move(0, 0, 5);
            expect(viewSpace(camera, probe)[1]).toBeLessThan(before[1]); // we rose, so it drops in view
          });
        }
      }
    }

    it('keeps the camera level when walking with the orbit camera looking down', () => {
      const camera = new Camera3D();
      camera.mode = 'orbit';
      camera.setOrbitTarget(0, 0, 10);
      camera.setRotation(0.9, 0.5);
      camera.move(10, 0, 0);
      expect(camera.orbitTarget[2]).toBe(10);
    });

    it('turn(+) turns the view to the right in both modes', () => {
      for (const mode of ['orbit', 'first-person']) {
        const camera = new Camera3D();
        camera.mode = mode;
        camera.position = [0, 0, 0];
        camera.setOrbitTarget(0, 0, 0);
        camera.setRotation(0, 0.3);
        const heading = () => {
          const v = camera.getViewMatrix(); // world +X axis in view space: x component positive means it is to our right
          return v[0];
        };
        const before = heading();
        camera.turn(0.2);
        // Turning right brings world +X towards the view's left-right axis from the left: its view-space x decreases.
        expect(heading()).toBeLessThan(before);
      }
    });
  });
});
