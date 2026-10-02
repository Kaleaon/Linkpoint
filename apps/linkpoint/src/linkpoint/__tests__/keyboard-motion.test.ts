import { describe, expect, it } from 'vitest';
import { isMotionKey, isTypingTarget, resolveKeyMotion, RUN_SPEED_MULTIPLIER } from '../keyboard-motion';

const keys = (...codes: string[]) => new Set(codes);

describe('resolveKeyMotion', () => {
  it('maps WASD and arrows consistently', () => {
    expect(resolveKeyMotion(keys('KeyW'))).toMatchObject({ forward: 1, right: 0, turn: 0 });
    expect(resolveKeyMotion(keys('ArrowUp'))).toMatchObject({ forward: 1 });
    expect(resolveKeyMotion(keys('KeyS'))).toMatchObject({ forward: -1 });
    expect(resolveKeyMotion(keys('ArrowDown'))).toMatchObject({ forward: -1 });
    expect(resolveKeyMotion(keys('KeyD'))).toMatchObject({ right: 1, turn: 0 });
    expect(resolveKeyMotion(keys('KeyA'))).toMatchObject({ right: -1, turn: 0 });
  });

  it('turns with Left/Right instead of strafing', () => {
    expect(resolveKeyMotion(keys('ArrowRight'))).toMatchObject({ turn: 1, right: 0 });
    expect(resolveKeyMotion(keys('ArrowLeft'))).toMatchObject({ turn: -1, right: 0 });
  });

  it('supports several keys for vertical movement', () => {
    for (const code of ['KeyE', 'Space', 'PageUp']) expect(resolveKeyMotion(keys(code)).up).toBe(1);
    for (const code of ['KeyQ', 'KeyC', 'PageDown']) expect(resolveKeyMotion(keys(code)).up).toBe(-1);
  });

  it('cancels opposing keys and normalizes diagonals', () => {
    expect(resolveKeyMotion(keys('KeyW', 'KeyS'))).toMatchObject({ forward: 0 });
    expect(resolveKeyMotion(keys('KeyA', 'KeyD'))).toMatchObject({ right: 0 });
    const diagonal = resolveKeyMotion(keys('KeyW', 'KeyD'));
    expect(Math.hypot(diagonal.forward, diagonal.right)).toBeCloseTo(1, 6);
    // W plus Up arrow is the same direction held twice, not double speed.
    expect(resolveKeyMotion(keys('KeyW', 'ArrowUp')).forward).toBe(1);
  });

  it('runs faster with Shift', () => {
    expect(resolveKeyMotion(keys('KeyW'), true).forward).toBe(RUN_SPEED_MULTIPLIER);
    expect(resolveKeyMotion(keys('KeyW', 'KeyD'), true).forward).toBeCloseTo(RUN_SPEED_MULTIPLIER * Math.SQRT1_2, 6);
  });
});

describe('key filtering', () => {
  it('recognises only camera keys', () => {
    expect(isMotionKey('KeyW')).toBe(true);
    expect(isMotionKey('Space')).toBe(true);
    expect(isMotionKey('KeyR')).toBe(false);
    expect(isMotionKey('Enter')).toBe(false);
  });

  it('lets text entry win', () => {
    const el = (tagName: string, extra: object = {}) => ({ tagName, getAttribute: () => null, isContentEditable: false, ...extra }) as unknown as EventTarget;
    expect(isTypingTarget(el('INPUT'))).toBe(true);
    expect(isTypingTarget(el('TEXTAREA'))).toBe(true);
    expect(isTypingTarget(el('SELECT'))).toBe(true);
    expect(isTypingTarget(el('DIV', { isContentEditable: true }))).toBe(true);
    expect(isTypingTarget(el('DIV', { getAttribute: () => 'textbox' }))).toBe(true);
    expect(isTypingTarget(el('CANVAS'))).toBe(false);
    expect(isTypingTarget(null)).toBe(false);
  });
});
