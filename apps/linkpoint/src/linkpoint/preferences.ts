/**
 * Linkpoint PWA - Preferences/Settings Manager
 */

import { Utils } from './utils';

export class PreferencesManager extends Utils.EventEmitter {
  public preferences: any;

  constructor() {
    super();
    this.preferences = this.getDefaultPreferences();
  }

  getDefaultPreferences() {
    return {
      graphics: { quality: 'medium', fov: 60 },
      interface: { theme: 'dark', showFPS: true, designStyle: 'glass', colorPalette: 'linkpoint-blue' },
      notifications: { enabled: true, soundEnabled: true },
      // Optional third-party features. Everything is off until the user turns it on in Settings.
      integrations: { google: false }
    };
  }

  init() {
    const defaults = this.getDefaultPreferences();
    const saved = Utils.storage.get('linkpoint_preferences');
    if (saved) {
      this.preferences = {
        ...defaults,
        ...saved,
        graphics: { ...defaults.graphics, ...(saved.graphics || {}) },
        interface: { ...defaults.interface, ...(saved.interface || {}) },
        notifications: { ...defaults.notifications, ...(saved.notifications || {}) },
        integrations: { ...defaults.integrations, ...(saved.integrations || {}) },
      };
      return;
    }
    this.preferences = defaults;
  }

  get(category: string, key: string) {
    return this.preferences[category]?.[key];
  }

  /** Whether the user has enabled Google sign-in for Contacts and Calendar. Off by default. */
  isGoogleEnabled(): boolean {
    return this.preferences.integrations?.google === true;
  }

  save() {
    Utils.storage.set('linkpoint_preferences', this.preferences);
  }

  set(category: string, key: string, value: any) {
    if (!this.preferences[category]) this.preferences[category] = {};
    this.preferences[category][key] = value;
    this.save();
    this.emit('preference_changed', { category, key, value });
  }
}
