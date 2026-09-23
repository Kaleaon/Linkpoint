import { describe, expect, it } from 'vitest';
import { XMLRPCClient } from '../xmlrpc-client';
import { Utils } from '../utils';

describe('XMLRPCClient', () => {
  describe('generateMAC()', () => {
    it('should generate the same MAC address on subsequent calls because of persistence', () => {
      // Clear persistence just in case
      Utils.storage.remove('linkpoint_mac');

      const mac1 = XMLRPCClient.generateMAC();
      const mac2 = XMLRPCClient.generateMAC();

      expect(mac1).toBe(mac2);
    });
  });
});
