import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { Utils } from './utils';

describe('Utils', () => {
  describe('generateUUID', () => {
    it('should generate a string of length 36', () => {
      const uuid = Utils.generateUUID();
      expect(uuid.length).toBe(36);
    });

    it('should match the standard UUID v4 format', () => {
      const uuid = Utils.generateUUID();
      const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
      expect(uuid).toMatch(uuidRegex);
    });

    it('should generate unique UUIDs', () => {
      const ITERATIONS = 1000;
      const uuids = new Set<string>();

      for (let i = 0; i < ITERATIONS; i++) {
        uuids.add(Utils.generateUUID());
      }

      expect(uuids.size).toBe(ITERATIONS);
    });
  });

  describe('debounce', () => {
    beforeEach(() => {
      vi.useFakeTimers();
    });

    afterEach(() => {
      vi.useRealTimers();
    });

    it('should only call the function once after the wait time', () => {
      const func = vi.fn();
      const debouncedFunc = Utils.debounce(func, 100);

      debouncedFunc();
      debouncedFunc();
      debouncedFunc();

      expect(func).not.toHaveBeenCalled();

      vi.advanceTimersByTime(50);
      expect(func).not.toHaveBeenCalled();

      vi.advanceTimersByTime(50);
      expect(func).toHaveBeenCalledTimes(1);
    });

    it('should call the function with the latest arguments', () => {
      const func = vi.fn();
      const debouncedFunc = Utils.debounce(func, 100);

      debouncedFunc(1);
      debouncedFunc(2);
      debouncedFunc(3);

      vi.advanceTimersByTime(100);

      expect(func).toHaveBeenCalledTimes(1);
      expect(func).toHaveBeenCalledWith(3);
    });

    it('should be able to handle multiple calls spaced out more than the wait time', () => {
      const func = vi.fn();
      const debouncedFunc = Utils.debounce(func, 100);

      debouncedFunc('call 1');
      vi.advanceTimersByTime(100);
      expect(func).toHaveBeenCalledTimes(1);
      expect(func).toHaveBeenCalledWith('call 1');

      debouncedFunc('call 2');
      vi.advanceTimersByTime(100);
      expect(func).toHaveBeenCalledTimes(2);
      expect(func).toHaveBeenCalledWith('call 2');
    });

    it('should pass multiple arguments correctly', () => {
      const func = vi.fn();
      const debouncedFunc = Utils.debounce(func, 100);

      debouncedFunc('arg1', 'arg2', { key: 'value' });
      vi.advanceTimersByTime(100);

      expect(func).toHaveBeenCalledTimes(1);
      expect(func).toHaveBeenCalledWith('arg1', 'arg2', { key: 'value' });
    });
  });

  describe('clamp', () => {
    it('should return the value when it is within the bounds', () => {
      expect(Utils.clamp(5, 0, 10)).toBe(5);
      expect(Utils.clamp(0, -5, 5)).toBe(0);
      expect(Utils.clamp(-3, -10, 0)).toBe(-3);
    });

    it('should return the minimum value when the value is below the bounds', () => {
      expect(Utils.clamp(-5, 0, 10)).toBe(0);
      expect(Utils.clamp(-10, -5, 5)).toBe(-5);
    });

    it('should return the maximum value when the value is above the bounds', () => {
      expect(Utils.clamp(15, 0, 10)).toBe(10);
      expect(Utils.clamp(10, -5, 5)).toBe(5);
    });

    it('should handle edge cases where value equals min or max', () => {
      expect(Utils.clamp(0, 0, 10)).toBe(0);
      expect(Utils.clamp(10, 0, 10)).toBe(10);
    });

    it('should work correctly with floating point numbers', () => {
      expect(Utils.clamp(3.5, 1.1, 5.5)).toBe(3.5);
      expect(Utils.clamp(0.5, 1.1, 5.5)).toBe(1.1);
      expect(Utils.clamp(6.5, 1.1, 5.5)).toBe(5.5);
    });
  });

  describe('parseQueryString', () => {
    it('parses basic query parameters', () => {
      expect(Utils.parseQueryString('https://example.com?a=1&b=2')).toEqual({ a: '1', b: '2' });
    });

    it('handles empty query string', () => {
      expect(Utils.parseQueryString('https://example.com')).toEqual({});
      expect(Utils.parseQueryString('https://example.com?')).toEqual({});
    });

    it('handles URL-encoded values', () => {
      expect(Utils.parseQueryString('https://example.com?message=Hello%20World&symbol=%24')).toEqual({ message: 'Hello World', symbol: '$' });
    });

    it('handles parameters without values', () => {
      expect(Utils.parseQueryString('https://example.com?flag=&another')).toEqual({ flag: '', another: '' });
    });

    it('handles duplicate parameters by overwriting with the last value', () => {
      expect(Utils.parseQueryString('https://example.com?a=1&a=2')).toEqual({ a: '2' });
    });

    it('handles string starting with ? instead of full URL', () => {
      expect(Utils.parseQueryString('?a=1&b=2')).toEqual({ a: '1', b: '2' });
    });
  });

  describe('formatFileSize', () => {
    it('should correctly format 0 bytes', () => {
      expect(Utils.formatFileSize(0)).toBe('0 Bytes');
    });

    it('should correctly format negative bytes', () => {
      expect(Utils.formatFileSize(-1024)).toBe('-1 KB');
      expect(Utils.formatFileSize(-1500)).toBe('-1.46 KB');
    });

    it('should correctly format byte values under 1 KB', () => {
      expect(Utils.formatFileSize(1)).toBe('1 Bytes');
      expect(Utils.formatFileSize(512)).toBe('512 Bytes');
      expect(Utils.formatFileSize(1023)).toBe('1023 Bytes');
    });

    it('should correctly format exactly 1 KB', () => {
      expect(Utils.formatFileSize(1024)).toBe('1 KB');
    });

    it('should correctly format decimal values for KB', () => {
      expect(Utils.formatFileSize(1536)).toBe('1.5 KB');
      expect(Utils.formatFileSize(2000)).toBe('1.95 KB');
    });

    it('should correctly format MB values', () => {
      expect(Utils.formatFileSize(1048576)).toBe('1 MB');
      expect(Utils.formatFileSize(2621440)).toBe('2.5 MB');
    });

    it('should correctly format GB values', () => {
      expect(Utils.formatFileSize(1073741824)).toBe('1 GB');
      expect(Utils.formatFileSize(1073741824 * 5.25)).toBe('5.25 GB');
    });

    it('should correctly format values exceeding GB (TB, PB, etc.)', () => {
      const tb = Math.pow(1024, 4);
      expect(Utils.formatFileSize(tb)).toBe('1 TB');

      const pb = Math.pow(1024, 5);
      expect(Utils.formatFileSize(pb * 3.14)).toBe('3.14 PB');

      const yb = Math.pow(1024, 8);
      expect(Utils.formatFileSize(yb * 99)).toBe('99 YB');
    });

    it('should cap out at YB without throwing undefined index errors for extremely large values', () => {
      const huge = Math.pow(1024, 10);
      expect(Utils.formatFileSize(huge)).toBe('1048576 YB');
    });
  });

  describe('EventEmitter', () => {
    it('should register and emit events', () => {
      const emitter = new Utils.EventEmitter();
      const listener = vi.fn();

      emitter.on('test-event', listener);
      emitter.emit('test-event', 'arg1', 42);

      expect(listener).toHaveBeenCalledTimes(1);
      expect(listener).toHaveBeenCalledWith('arg1', 42);
    });

    it('should unregister events with off()', () => {
      const emitter = new Utils.EventEmitter();
      const listener = vi.fn();

      emitter.on('test-event', listener);
      emitter.off('test-event', listener);
      emitter.emit('test-event');

      expect(listener).not.toHaveBeenCalled();
    });

    it('should only trigger once when registered with once()', () => {
      const emitter = new Utils.EventEmitter();
      const listener = vi.fn();

      emitter.once('test-event', listener);

      emitter.emit('test-event', 'first');
      emitter.emit('test-event', 'second');

      expect(listener).toHaveBeenCalledTimes(1);
      expect(listener).toHaveBeenCalledWith('first');
    });

    it('should handle multiple listeners for the same event', () => {
      const emitter = new Utils.EventEmitter();
      const listener1 = vi.fn();
      const listener2 = vi.fn();

      emitter.on('test-event', listener1);
      emitter.on('test-event', listener2);

      emitter.emit('test-event');

      expect(listener1).toHaveBeenCalledTimes(1);
      expect(listener2).toHaveBeenCalledTimes(1);
    });

    it('should safely handle emitting events with no listeners', () => {
      const emitter = new Utils.EventEmitter();
      expect(() => emitter.emit('non-existent')).not.toThrow();
    });

    it('should safely handle off() with no listeners', () => {
      const emitter = new Utils.EventEmitter();
      const listener = vi.fn();
      expect(() => emitter.off('non-existent', listener)).not.toThrow();
    });

    it('should safely handle off() for an existing event but wrong listener', () => {
      const emitter = new Utils.EventEmitter();
      const listener1 = vi.fn();
      const listenerToRemove = vi.fn();

      emitter.on('test-event', listener1);
      emitter.off('test-event', listenerToRemove);

      emitter.emit('test-event');

      expect(listener1).toHaveBeenCalledTimes(1);
    });
  });
});
