// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { act } from 'react';
import Profile, { sanitizeBioText } from '../../screens/Profile.jsx';
import { app } from '../app';
import { slBridge } from '../sl-bridge';
import { METHODS } from '../../../core/viewer-api.cjs';
import { mountScreen, unmount, buttonByText, type Mounted } from './ui-helpers';

let mounted: Mounted | null = null;

describe('Profile tabbed view and bridge RPC', () => {
  beforeEach(() => {
    localStorage.clear();
    app.auth.user = {
      id: 'agent-123',
      fullName: 'Test Resident',
      grid: 'agni',
      bio: 'Sample Second Life bio text',
    };
    slBridge.connected = false;
  });

  afterEach(async () => {
    await unmount(mounted);
    mounted = null;
    vi.restoreAllMocks();
  });

  describe('Bio Sanitization Security Guardrail', () => {
    it('sanitizes unsafe script, iframe, and HTML tags from bio text', () => {
      const malicious = '<script>alert("XSS")</script>Hello <b>World</b><iframe src="evil.com"></iframe>';
      const clean = sanitizeBioText(malicious);
      expect(clean).toBe('Hello World');
      expect(clean).not.toContain('<script>');
      expect(clean).not.toContain('<iframe>');
    });

    it('handles empty or non-string bio gracefully', () => {
      expect(sanitizeBioText(null as any)).toBe('');
      expect(sanitizeBioText(undefined as any)).toBe('');
      expect(sanitizeBioText('')).toBe('');
    });
  });

  describe('RPC Allowed List Methods', () => {
    it('includes profile RPC methods in viewer-api METHODS set', () => {
      expect(METHODS.has('getAvatarProfile')).toBe(true);
      expect(METHODS.has('getAvatarPicks')).toBe(true);
      expect(METHODS.has('getAvatarGroups')).toBe(true);
    });
  });

  describe('Bridge Caching Performance Guardrail', () => {
    it('caches profile RPC responses in memory during connected session', async () => {
      slBridge.connected = true;
      const callSpy = vi.spyOn(slBridge, 'call').mockResolvedValue({
        agentId: 'agent-123',
        displayName: 'Cached Resident',
        aboutText: 'Cached bio',
      });

      const p1 = await slBridge.getAvatarProfile('agent-123');
      const p2 = await slBridge.getAvatarProfile('agent-123');

      expect(p1.displayName).toBe('Cached Resident');
      expect(p2.displayName).toBe('Cached Resident');
      // Should call slBridge.call only once due to in-memory caching
      expect(callSpy).toHaveBeenCalledTimes(1);
    });
  });

  describe('Profile UI Tabs & Rendering', () => {
    it('renders About tab with user profile details when offline/fallback', async () => {
      mounted = await mountScreen(Profile);
      expect(mounted.host.textContent).toContain('Test Resident');
      expect(mounted.host.textContent).toContain('Sample Second Life bio text');
      expect(mounted.host.textContent).toContain('agni');
      expect(mounted.host.textContent).toContain('agent-123');
    });

    it('renders empty states for missing bio sections and empty Picks/Groups', async () => {
      app.auth.user = {
        id: 'agent-empty',
        fullName: 'Empty Resident',
        grid: 'agni',
        bio: '',
      };

      mounted = await mountScreen(Profile);
      expect(mounted.host.textContent).toContain('No Biography');

      // Switch to First Life tab
      const firstLifeBtn = buttonByText(mounted.host, /^First Life$/);
      expect(firstLifeBtn).toBeTruthy();
      await act(async () => {
        firstLifeBtn!.click();
      });
      expect(mounted.host.textContent).toContain('No First Life Details');

      // Switch to Picks tab
      const picksBtn = buttonByText(mounted.host, /^Picks$/);
      expect(picksBtn).toBeTruthy();
      await act(async () => {
        picksBtn!.click();
      });
      expect(mounted.host.textContent).toContain('No Picks Found');

      // Switch to Groups tab
      const groupsBtn = buttonByText(mounted.host, /^Groups$/);
      expect(groupsBtn).toBeTruthy();
      await act(async () => {
        groupsBtn!.click();
      });
      expect(mounted.host.textContent).toContain('No Groups Found');
    });

    it('renders Picks with working direct Teleport action', async () => {
      slBridge.connected = true;
      vi.spyOn(slBridge, 'getAvatarProfile').mockResolvedValue({
        agentId: 'agent-123',
        displayName: 'Explorer Resident',
        aboutText: 'Loves exploring',
      });
      vi.spyOn(slBridge, 'getAvatarPicks').mockResolvedValue([
        {
          id: 'pick-1',
          name: 'Favorite Beach Parcel',
          description: 'A serene beach parcel for sunset relaxing',
          simName: 'Arah',
          parcelName: 'Sunset Cove',
          destination: 'Arah/128/128/25',
        },
      ]);
      vi.spyOn(slBridge, 'getAvatarGroups').mockResolvedValue([]);
      const teleportSpy = vi.spyOn(slBridge, 'teleport').mockResolvedValue({
        requested: { region: 'Arah', x: 128, y: 128, z: 25 },
        message: 'Teleporting',
      });

      mounted = await mountScreen(Profile);

      // Navigate to Picks tab
      const picksBtn = buttonByText(mounted.host, /^Picks$/);
      await act(async () => {
        picksBtn!.click();
      });

      expect(mounted.host.textContent).toContain('Favorite Beach Parcel');
      expect(mounted.host.textContent).toContain('Arah (Sunset Cove)');

      const tpBtn = buttonByText(mounted.host, /^Teleport$/);
      expect(tpBtn).toBeTruthy();

      await act(async () => {
        tpBtn!.click();
      });

      expect(teleportSpy).toHaveBeenCalledWith({ destination: 'Arah/128/128/25' });
    });

    it('renders Groups tab with group titles and insignia icons', async () => {
      slBridge.connected = true;
      vi.spyOn(slBridge, 'getAvatarProfile').mockResolvedValue({
        agentId: 'agent-123',
        displayName: 'Group Member Resident',
      });
      vi.spyOn(slBridge, 'getAvatarPicks').mockResolvedValue([]);
      vi.spyOn(slBridge, 'getAvatarGroups').mockResolvedValue([
        {
          id: 'grp-1',
          name: 'Second Life Builders',
          title: 'Master Architect',
          insignia: 'insignia-uuid-1',
        },
      ]);

      mounted = await mountScreen(Profile);

      // Navigate to Groups tab
      const groupsBtn = buttonByText(mounted.host, /^Groups$/);
      await act(async () => {
        groupsBtn!.click();
      });

      expect(mounted.host.textContent).toContain('Second Life Builders');
      expect(mounted.host.textContent).toContain('Master Architect');
    });
  });
});
