// @vitest-environment jsdom
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { PreferencesManager } from '../preferences';

beforeEach(() => localStorage.clear());

describe('Google integration preference', () => {
  it('is off by default', () => {
    const prefs = new PreferencesManager();
    prefs.init();
    expect(prefs.isGoogleEnabled()).toBe(false);
  });

  it('turns on only when set, persists, and announces the change', () => {
    const prefs = new PreferencesManager();
    prefs.init();
    const changed = vi.fn();
    prefs.on('preference_changed', changed);
    prefs.set('integrations', 'google', true);
    expect(prefs.isGoogleEnabled()).toBe(true);
    expect(changed).toHaveBeenCalledWith({ category: 'integrations', key: 'google', value: true });

    const reloaded = new PreferencesManager();
    reloaded.init();
    expect(reloaded.isGoogleEnabled()).toBe(true);
  });

  it('stays off for preferences saved before the setting existed, and for any non-true value', () => {
    localStorage.setItem('linkpoint_preferences', JSON.stringify({ graphics: { quality: 'high' }, interface: {}, notifications: {} }));
    const prefs = new PreferencesManager();
    prefs.init();
    expect(prefs.isGoogleEnabled()).toBe(false);
    expect(prefs.get('graphics', 'quality')).toBe('high');
    prefs.set('integrations', 'google', 'yes' as any);
    expect(prefs.isGoogleEnabled()).toBe(false);
  });
});
