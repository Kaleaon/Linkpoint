import { describe, it, expect, vi, beforeEach } from 'vitest';
import { XMLRPCClient } from './xmlrpc-client';
import SparkMD5 from 'spark-md5';

describe('XMLRPCClient', () => {
  describe('buildLoginRequest', () => {
    beforeEach(() => {
      // Mock global navigator object since we are in Node environment
      global.navigator = { userAgent: 'Test User Agent' } as any;

      // Mock generateMAC, generateID0, generateViewerDigest for deterministic testing
      vi.spyOn(XMLRPCClient as any, 'generateMAC').mockReturnValue('00:11:22:33:44:55');
      vi.spyOn(XMLRPCClient as any, 'generateID0').mockReturnValue('test-id0-12345');
      vi.spyOn(XMLRPCClient as any, 'generateViewerDigest').mockReturnValue('test-digest-67890');
    });

    it('should build a valid XML-RPC login request with all parameters provided', () => {
      const params = {
        firstName: 'Test',
        lastName: 'User',
        passwordHash: 'abcdef123456',
        startLocation: 'home',
        channel: 'Custom Client',
        version: '2.0.0',
        macAddress: 'aa:bb:cc:dd:ee:ff',
        id0: 'custom-id0',
        viewerDigest: 'custom-digest'
      };

      const xml = XMLRPCClient.buildLoginRequest(params);

      // Verify structure
      expect(xml).toContain('<?xml version="1.0"?>');
      expect(xml).toContain('<methodCall>');
      expect(xml).toContain('<methodName>login_to_simulator</methodName>');

      // Verify string fields
      expect(xml).toContain('<name>first</name>\n<value><string>Test</string></value>');
      expect(xml).toContain('<name>last</name>\n<value><string>User</string></value>');
      expect(xml).toContain('<name>passwd</name>\n<value><string>$1$abcdef123456</string></value>');
      expect(xml).toContain('<name>start</name>\n<value><string>home</string></value>');
      expect(xml).toContain('<name>channel</name>\n<value><string>Custom Client</string></value>');
      expect(xml).toContain('<name>version</name>\n<value><string>2.0.0</string></value>');
      expect(xml).toContain('<name>mac</name>\n<value><string>' + SparkMD5.hash('aa:bb:cc:dd:ee:ff') + '</string></value>');
      expect(xml).toContain('<name>id0</name>\n<value><string>custom-id0</string></value>');
      expect(xml).toContain('<name>viewer_digest</name>\n<value><string>custom-digest</string></value>');
      expect(xml).toContain('<name>platform</name>\n<value><string>Web</string></value>');
      expect(xml).toContain('<name>platform_version</name>\n<value><string>Test User Agent</string></value>');

      // Verify boolean fields
      expect(xml).toContain('<name>agree_to_tos</name>\n<value><boolean>1</boolean></value>');
      expect(xml).toContain('<name>read_critical</name>\n<value><boolean>1</boolean></value>');

      // Verify some options
      expect(xml).toContain('<name>options</name>');
      expect(xml).toContain('<value><string>inventory-root</string></value>');
      expect(xml).toContain('<value><string>buddy-list</string></value>');
    });

    it('should use default values for missing optional parameters', () => {
      const params = {
        firstName: 'Default',
        lastName: 'Avatar',
        passwordHash: '098765fedcba'
      };

      const xml = XMLRPCClient.buildLoginRequest(params);

      // Verify provided values
      expect(xml).toContain('<name>first</name>\n<value><string>Default</string></value>');
      expect(xml).toContain('<name>last</name>\n<value><string>Avatar</string></value>');
      expect(xml).toContain('<name>passwd</name>\n<value><string>$1$098765fedcba</string></value>');

      // Verify default/generated values
      expect(xml).toContain('<name>start</name>\n<value><string>last</string></value>');
      expect(xml).toContain('<name>channel</name>\n<value><string>Linkpoint Viewer</string></value>');
      expect(xml).toContain('<name>version</name>\n<value><string>2.0.0</string></value>');
      expect(xml).toContain('<name>mac</name>\n<value><string>' + SparkMD5.hash('00:11:22:33:44:55') + '</string></value>');
      expect(xml).toContain('<name>id0</name>\n<value><string>test-id0-12345</string></value>');
      expect(xml).toContain('<name>viewer_digest</name>\n<value><string>test-digest-67890</string></value>');
    });

    it('should correctly escape XML special characters in parameters', () => {
      const params = {
        firstName: 'Test & Name',
        lastName: 'User <"Tag">',
        passwordHash: 'hash\'123',
      };

      const xml = XMLRPCClient.buildLoginRequest(params);

      // Verify escaped values
      expect(xml).toContain('<name>first</name>\n<value><string>Test &amp; Name</string></value>');
      expect(xml).toContain('<name>last</name>\n<value><string>User &lt;&quot;Tag&quot;&gt;</string></value>');
      expect(xml).toContain('<name>passwd</name>\n<value><string>$1$hash&apos;123</string></value>');
    });

    it('supports the next_method used by indeterminate grid login replies', () => {
      const xml = XMLRPCClient.buildLoginRequest({
        firstName: 'Redirected',
        lastName: 'Resident',
        passwordHash: 'hash',
      }, 'login_to_simulator_v2');
      expect(xml).toContain('<methodName>login_to_simulator_v2</methodName>');
    });
  });
});
