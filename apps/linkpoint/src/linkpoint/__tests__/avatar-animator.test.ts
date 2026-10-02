import { describe, expect, it } from 'vitest';
import { AvatarAnimator, bundledAnimationLoader } from '../avatar-animator';
import type { KeyframeAnimation } from '../avatar-animation';

const makeAnim = (over: Partial<KeyframeAnimation> = {}): KeyframeAnimation => ({
  priority: 1, length: 2, expression: '', inPoint: 0, outPoint: 2, loop: true, easeIn: 0, easeOut: 0.5, handPose: 0,
  joints: [{ name: 'mHead', priority: 1, rotations: [{ time: 0, value: [0.5, 0, 0, Math.sqrt(0.75)] }], positions: [] }],
  ...over,
});
const flush = () => new Promise((resolve) => setTimeout(resolve, 0));

describe('AvatarAnimator', () => {
  it('starts animations, blends them, and eases out when the simulator stops them', async () => {
    let t = 0;
    const animator = new AvatarAnimator(async () => makeAnim(), () => t);
    animator.setAnimations('avatar', [{ id: 'a1', seq: 1 }]);
    expect(animator.pose('avatar').size).toBe(0); // still loading
    await flush();
    t = 1;
    expect(animator.pose('avatar').get('mHead')!.rotation[0]).toBeCloseTo(0.5, 3);
    animator.setAnimations('avatar', []);
    t = 1.25; // halfway through the 0.5 s ease-out: still present
    expect(animator.pose('avatar').has('mHead')).toBe(true);
    t = 2;
    expect(animator.pose('avatar').size).toBe(0);
    expect(animator.isAnimating('avatar')).toBe(false);
  });

  it('restarts an animation when its sequence id changes but not when it is unchanged', async () => {
    let t = 0, loads = 0;
    const animator = new AvatarAnimator(async () => { loads++; return makeAnim({ loop: false, easeIn: 1 }); }, () => t);
    animator.setAnimations('a', [{ id: 'x', seq: 1 }]);
    await flush();
    t = 5;
    animator.setAnimations('a', [{ id: 'x', seq: 1 }]);
    await flush();
    expect(animator.isAnimating('a')).toBe(false); // unchanged: not restarted, one-shot finished
    animator.setAnimations('a', [{ id: 'x', seq: 2 }]);
    await flush();
    expect(animator.isAnimating('a')).toBe(true);
    expect(loads).toBe(1); // parsed once, cached
  });

  it('keeps independent subjects separate and ignores missing or null animations', async () => {
    const animator = new AvatarAnimator(async (id) => (id === 'good' ? makeAnim() : null), () => 1);
    animator.setAnimations('one', [{ id: 'good', seq: 1 }]);
    animator.setAnimations('two', [{ id: 'unknown', seq: 1 }, { id: '00000000-0000-0000-0000-000000000000', seq: 1 }]);
    await flush();
    expect(animator.pose('one').size).toBe(1);
    expect(animator.pose('two').size).toBe(0);
    animator.remove('one');
    expect(animator.pose('one').size).toBe(0);
  });

  it('loads bundled animations by UUID and rejects non-UUID ids', async () => {
    const calls: string[] = [];
    const loader = bundledAnimationLoader('/anims/', (async (url: string) => { calls.push(url); return { ok: false } as Response; }) as unknown as typeof fetch);
    expect(await loader('../etc/passwd')).toBeNull();
    expect(await loader('038fcec9-5ebd-8a8e-0e2e-6e71a0a1ac53')).toBeNull();
    expect(calls).toEqual(['/anims/038fcec9-5ebd-8a8e-0e2e-6e71a0a1ac53.base64']);
  });

  it('retries a failed download after the retry window but not before', async () => {
    let t = 0, calls = 0;
    const animator = new AvatarAnimator(async () => { calls++; return calls < 2 ? null : makeAnim(); }, () => t);
    animator.setAnimations('a', [{ id: 'custom', seq: 1 }]);
    await flush();
    t = 10;
    animator.setAnimations('a', [{ id: 'custom', seq: 2 }]);
    await flush();
    expect(calls).toBe(1); // still inside the window
    t = 31;
    animator.setAnimations('a', [{ id: 'custom', seq: 3 }]);
    await flush();
    expect(calls).toBe(2);
    expect(animator.pose('a').size).toBe(1);
  });
});
