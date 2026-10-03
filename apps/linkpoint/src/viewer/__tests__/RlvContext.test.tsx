// @vitest-environment jsdom
import { describe, it, expect, vi, afterEach } from 'vitest';
import { act, createElement } from 'react';
import { createRoot, type Root } from 'react-dom/client';
import {
  RlvProvider,
  useRlv,
  parseRlvCommandString,
  type RlvRestriction,
  RLV_REASONS,
  type RlvContextValue,
} from '../RlvContext';
import { MockViewerClient } from '@linkpoint/viewer-client';

(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true;

let currentContainer: { host: HTMLElement; root: Root; ctx: { current: RlvContextValue | null } } | null = null;

async function mountRlv(props: { initialEnabled?: boolean; client?: any; onQueryReply?: any } = {}) {
  const host = document.createElement('div');
  document.body.appendChild(host);
  const root = createRoot(host);
  const ctx = { current: null as RlvContextValue | null };
  const Probe = () => {
    ctx.current = useRlv();
    return null;
  };
  await act(async () => {
    root.render(createElement(RlvProvider, props, createElement(Probe)));
  });
  currentContainer = { host, root, ctx };
  return ctx;
}

afterEach(async () => {
  if (currentContainer) {
    await act(async () => currentContainer!.root.unmount());
    currentContainer.host.remove();
    currentContainer = null;
  }
});

describe('RLV Command Engine & Context', () => {
  describe('Command Parser (parseRlvCommandString)', () => {
    it('parses single command strings correctly', () => {
      const result = parseRlvCommandString('@detach=n');
      expect(result).toEqual([
        { name: 'detach', option: null, value: 'n' },
      ]);
    });

    it('parses compound command strings with pipe delimiter', () => {
      const result = parseRlvCommandString('@detach=n|@sendchat=n');
      expect(result).toEqual([
        { name: 'detach', option: null, value: 'n' },
        { name: 'sendchat', option: null, value: 'n' },
      ]);
    });

    it('parses compound command strings with comma delimiter', () => {
      const result = parseRlvCommandString('@detach=n,sendchat=n');
      expect(result).toEqual([
        { name: 'detach', option: null, value: 'n' },
        { name: 'sendchat', option: null, value: 'n' },
      ]);
    });

    it('parses command strings with options and values', () => {
      const result = parseRlvCommandString('@redirchat:0=add');
      expect(result).toEqual([
        { name: 'redirchat', option: '0', value: 'add' },
      ]);
    });

    it('handles query commands with channel numbers', () => {
      const result = parseRlvCommandString('@version=4711');
      expect(result).toEqual([
        { name: 'version', option: null, value: '4711' },
      ]);
    });

    it('handles empty or malformed strings gracefully', () => {
      expect(parseRlvCommandString('')).toEqual([]);
      expect(parseRlvCommandString('invalid_no_equals')).toEqual([]);
    });
  });

  describe('Multi-Object Reference Tracking & State Model', () => {
    it('keeps restriction active until ALL issuing objects clear it', async () => {
      const ctx = await mountRlv({ initialEnabled: true });

      const obj1 = 'object-uuid-1';
      const obj2 = 'object-uuid-2';

      // Object 1 sets @detach=n
      await act(async () => {
        ctx.current!.processCommand('@detach=n', obj1);
      });
      expect(ctx.current!.restricted('detach')).toBe(true);

      // Object 2 also sets @detach=n
      await act(async () => {
        ctx.current!.processCommand('@detach=n', obj2);
      });
      expect(ctx.current!.restricted('detach')).toBe(true);

      // Object 1 clears its restriction
      await act(async () => {
        ctx.current!.processCommand('@detach=y', obj1);
      });
      // Detach MUST remain restricted because Object 2 still holds it
      expect(ctx.current!.restricted('detach')).toBe(true);

      // Object 2 clears its restriction
      await act(async () => {
        ctx.current!.processCommand('@detach=y', obj2);
      });
      // Now restriction is cleared
      expect(ctx.current!.restricted('detach')).toBe(false);
    });

    it('clears restrictions when an object detaches (clearObjectRestrictions)', async () => {
      const ctx = await mountRlv({ initialEnabled: true });

      const obj1 = 'object-uuid-1';
      const obj2 = 'object-uuid-2';

      await act(async () => {
        ctx.current!.processCommand('@detach=n|sendchat=n', obj1);
      });
      await act(async () => {
        ctx.current!.processCommand('@detach=n', obj2);
      });

      expect(ctx.current!.restricted('detach')).toBe(true);
      expect(ctx.current!.restricted('sendchat')).toBe(true);

      // Clear all restrictions for Object 1 (simulate detaching item)
      await act(async () => {
        ctx.current!.clearObjectRestrictions(obj1);
      });

      // sendchat is cleared, but detach remains due to Object 2
      expect(ctx.current!.restricted('sendchat')).toBe(false);
      expect(ctx.current!.restricted('detach')).toBe(true);

      // Clear Object 2
      await act(async () => {
        ctx.current!.clearObjectRestrictions(obj2);
      });
      expect(ctx.current!.restricted('detach')).toBe(false);
    });
  });

  describe('27 Standard RLV Command Classes Support', () => {
    const all27Commands: { cmd: string; restriction: RlvRestriction }[] = [
      { cmd: '@detach=n', restriction: 'detach' },
      { cmd: '@showloc=n', restriction: 'showloc' },
      { cmd: '@shownames=n', restriction: 'shownames' },
      { cmd: '@sendchat=n', restriction: 'sendchat' },
      { cmd: '@recvchat=n', restriction: 'recvchat' },
      { cmd: '@sendim=n', restriction: 'sendim' },
      { cmd: '@recvim=n', restriction: 'recvim' },
      { cmd: '@tplm=n', restriction: 'tplm' },
      { cmd: '@tploc=n', restriction: 'tploc' },
      { cmd: '@sittp=n', restriction: 'sittp' },
      { cmd: '@tplure=n', restriction: 'tplure' },
      { cmd: '@tpto=n', restriction: 'tpto' },
      { cmd: '@accepttp=n', restriction: 'accepttp' },
      { cmd: '@showinv=n', restriction: 'showinv' },
      { cmd: '@showworldmap=n', restriction: 'showworldmap' },
      { cmd: '@showminimap=n', restriction: 'showminimap' },
      { cmd: '@viewnote=n', restriction: 'viewnote' },
      { cmd: '@edit=n', restriction: 'edit' },
      { cmd: '@rez=n', restriction: 'rez' },
      { cmd: '@unsit=n', restriction: 'unsit' },
      { cmd: '@sit=n', restriction: 'sit' },
      { cmd: '@remoutfit=n', restriction: 'remoutfit' },
      { cmd: '@addoutfit=n', restriction: 'addoutfit' },
      { cmd: '@sendchannel=n', restriction: 'sendchannel' },
      { cmd: '@redirchat=n', restriction: 'redirchat' },
    ];

    all27Commands.forEach(({ cmd, restriction }) => {
      it(`handles RLV restriction command class: ${cmd}`, async () => {
        const ctx = await mountRlv({ initialEnabled: true });

        await act(async () => {
          ctx.current!.processCommand(cmd, 'test-obj');
        });

        expect(ctx.current!.restricted(restriction)).toBe(true);
        expect(ctx.current!.reasonFor(restriction)).toBe(RLV_REASONS[restriction]);

        // Remove restriction
        const clearCmd = cmd.replace('=n', '=y');
        await act(async () => {
          ctx.current!.processCommand(clearCmd, 'test-obj');
        });

        expect(ctx.current!.restricted(restriction)).toBe(false);
      });
    });

    it('handles query commands and clear command classes', async () => {
      const onQueryReply = vi.fn();
      const mockClient = new MockViewerClient();

      const ctx = await mountRlv({ initialEnabled: true, client: mockClient, onQueryReply });

      // Version queries
      await act(async () => {
        ctx.current!.processCommand('@version=4711', 'obj1');
      });
      expect(onQueryReply).toHaveBeenCalledWith(4711, 'RestrainedLife viewer v2.8.0 (Linkpoint RLV v3.4.3)');

      await act(async () => {
        ctx.current!.processCommand('@versionnew=4712', 'obj1');
      });
      expect(onQueryReply).toHaveBeenCalledWith(4712, 'RestrainedLove viewer v2.8.0 (Linkpoint RLV v3.4.3)');

      await act(async () => {
        ctx.current!.processCommand('@versionnum=4713', 'obj1');
      });
      expect(onQueryReply).toHaveBeenCalledWith(4713, '3040300');

      // Set a restriction and test getstatus query
      await act(async () => {
        ctx.current!.processCommand('@detach=n', 'obj1');
      });
      await act(async () => {
        ctx.current!.processCommand('@getstatus=4714', 'obj1');
      });
      expect(onQueryReply).toHaveBeenCalledWith(4714, '/detach');

      // Test clear command
      await act(async () => {
        ctx.current!.processCommand('@clear=y', 'obj1');
      });
      expect(ctx.current!.restricted('detach')).toBe(false);
    });
  });

  describe('Master Switch & Pause Enforcement', () => {
    it('immediately pauses restriction enforcement when turned off', async () => {
      const ctx = await mountRlv({ initialEnabled: true });

      await act(async () => {
        ctx.current!.processCommand('@detach=n|sendchat=n', 'obj1');
      });

      expect(ctx.current!.restricted('detach')).toBe(true);
      expect(ctx.current!.restricted('sendchat')).toBe(true);

      // Disable RLV
      await act(async () => {
        ctx.current!.setEnabled(false);
      });

      // Enforcement is immediately paused across all controls
      expect(ctx.current!.restricted('detach')).toBe(false);
      expect(ctx.current!.restricted('sendchat')).toBe(false);
      expect(ctx.current!.reasonFor('detach')).toBeNull();

      // Re-enable RLV
      await act(async () => {
        ctx.current!.setEnabled(true);
      });

      // Restrictions resume
      expect(ctx.current!.restricted('detach')).toBe(true);
    });
  });

  describe('Unowned Object Authorization Constraint', () => {
    it('ignores restrictions from unowned objects (isOwner = false)', async () => {
      const ctx = await mountRlv({ initialEnabled: true });

      await act(async () => {
        ctx.current!.processCommand('@detach=n', 'unowned-obj', false);
      });

      expect(ctx.current!.restricted('detach')).toBe(false);
    });
  });

  describe('Performance Constraint (< 5ms per message)', () => {
    it('parses and processes messages well within 5ms', async () => {
      const ctx = await mountRlv({ initialEnabled: true });

      const compoundMsg = '@detach=n|sendchat=n|showloc=n|tploc=n|showinv=n|getstatus=1234';

      await act(async () => {
        const startTime = performance.now();
        for (let i = 0; i < 100; i++) {
          ctx.current!.processCommand(compoundMsg, `obj-${i}`);
        }
        const endTime = performance.now();

        const durationPerMessage = (endTime - startTime) / 100;
        expect(durationPerMessage).toBeLessThan(5); // Must be under 5 milliseconds
      });
    });
  });
});
