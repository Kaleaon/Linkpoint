import CryptoJS from 'crypto-js';
/**
 * Linkpoint PWA - XML-RPC Client for Second Life Login
 * Based on Linkpoint Android implementation
 */

import { Utils } from './utils';
import { corsHandler } from './cors-handler';
import SparkMD5 from 'spark-md5';
import { VIEWER_CHANNEL, VIEWER_VERSION } from './viewer-identity';

export class XMLRPCClient {
  /**
   * Build XML-RPC login request
   */
  static buildLoginRequest(params: any, methodName: string = 'login_to_simulator'): string {
    // String fields (non-boolean)
    const stringFields: { name: string, value: string }[] = [];

    // Add all login parameters as strings
    stringFields.push({ name: 'first', value: params.firstName });
    stringFields.push({ name: 'last', value: params.lastName });
    stringFields.push({ name: 'passwd', value: `$1$${params.passwordHash}` });
    stringFields.push({ name: 'start', value: params.startLocation || 'last' });
    stringFields.push({ name: 'channel', value: params.channel || VIEWER_CHANNEL });
    stringFields.push({ name: 'version', value: params.version || VIEWER_VERSION });
    stringFields.push({ name: 'platform', value: 'Web' });
    stringFields.push({ name: 'platform_version', value: navigator.userAgent });
    // MD5 hash the MAC address before sending, per TPV_COMPLIANCE.md
    const rawMac = params.macAddress || this.generateMAC();
    let hashedMac = '';
    // We compute the hash synchronously if possible, or assume it's pre-hashed if it's 32 chars
    if (rawMac.length === 32 && !rawMac.includes(':')) {
       hashedMac = rawMac;
    } else {
       hashedMac = SparkMD5.hash(rawMac);
    }
    stringFields.push({ name: 'mac', value: hashedMac });
    stringFields.push({ name: 'id0', value: params.id0 || this.generateID0() });
    stringFields.push({ name: 'viewer_digest', value: params.viewerDigest || this.generateViewerDigest() });

    // Add options array
    const options = [
      'inventory-root',
      'inventory-skeleton',
      'inventory-lib-root',
      'inventory-lib-owner',
      'inventory-skel-lib',
      'initial-outfit',
      'gestures',
      'display_names',
      'event_categories',
      'event_notifications',
      'classified_categories',
      'adult_compliant',
      'buddy-list',
      'newuser-config',
      'ui-config',
      'advanced-mode',
      'max-agent-groups',
      'map-server-url',
      'voice-config',
      'tutorial_setting',
      'login-flags',
      'global-textures'
    ];

    // Build XML
    let xml = '<?xml version="1.0"?>\n';
    xml += '<methodCall>\n';
    xml += `<methodName>${this.escapeXml(methodName)}</methodName>\n`;
    xml += '<params>\n';
    xml += '<param>\n';
    xml += '<value><struct>\n';

    // Add all string fields
    for (const field of stringFields) {
      xml += '<member>\n';
      xml += `<name>${this.escapeXml(field.name)}</name>\n`;
      xml += `<value><string>${this.escapeXml(field.value)}</string></value>\n`;
      xml += '</member>\n';
    }

    // Add boolean fields - MUST use <boolean>1</boolean> format, NOT <string>true</string>
    xml += '<member>\n';
    xml += '<name>agree_to_tos</name>\n';
    xml += '<value><boolean>1</boolean></value>\n';
    xml += '</member>\n';

    xml += '<member>\n';
    xml += '<name>read_critical</name>\n';
    xml += '<value><boolean>1</boolean></value>\n';
    xml += '</member>\n';

    // Add options array
    xml += '<member>\n';
    xml += '<name>options</name>\n';
    xml += '<value><array><data>\n';
    for (const option of options) {
      xml += `<value><string>${option}</string></value>\n`;
    }
    xml += '</data></array></value>\n';
    xml += '</member>\n';

    xml += '</struct></value>\n';
    xml += '</param>\n';
    xml += '</params>\n';
    xml += '</methodCall>\n';

    return xml;
  }

  /**
   * Send XML-RPC request
   */
  static async sendRequest(url: string, xmlRequest: string): Promise<any> {
    try {
      console.log('[SL] Using CORSHandler for request');
      const response = await corsHandler.makeRequest(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'text/xml',
          'Accept': 'text/xml, application/xml'
        },
        body: xmlRequest
      });

      if (!response || !response.ok) {
        const errorText = response ? await response.text() : 'No response';
        console.error('[XMLRPCClient] HTTP error response:', errorText.substring(0, 500));
        throw new Error(`HTTP ${response ? response.status : 'Error'}: ${response ? response.statusText : 'Unknown'}`);
      }

      const responseText = await response.text();

      // Check if response looks like XML
      if (!responseText.trim().startsWith('<?xml') && !responseText.trim().startsWith('<methodResponse')) {
        console.error('[XMLRPCClient] Response is not XML:', responseText.substring(0, 500));
        throw new Error('Server returned an invalid response. This may be a CORS issue.');
      }

      return this.parseLoginResponse(responseText);

    } catch (error: any) {
      console.error('XML-RPC request failed:', error);
      throw error;
    }
  }

  /**
   * Parse XML-RPC login response
   */
  static parseLoginResponse(xmlText: string): any {
    const parser = new DOMParser();
    const doc = parser.parseFromString(xmlText, 'text/xml');

    const parseError = doc.querySelector('parsererror');
    if (parseError) {
      throw new Error('Invalid server response (XML parse error).');
    }

    const methodResponse = doc.querySelector('methodResponse');
    if (!methodResponse) {
      throw new Error('Invalid XML-RPC response');
    }

    // Check for fault
    const fault = methodResponse.querySelector('fault');
    if (fault) {
      const faultStruct = this.parseStruct(fault.querySelector('value struct') as Element);
      throw new Error(`Login failed: ${faultStruct.faultString || 'Unknown error'}`);
    }

    // Parse params
    const params = methodResponse.querySelector('params param value struct');
    if (!params) {
      throw new Error('No params in response');
    }

    return this.parseStruct(params);
  }

  /**
   * Parse XML struct into object
   */
  static parseStruct(structElement: Element): any {
    const result: Record<string, any> = {};

    if (!structElement) return result;

    const members = structElement.querySelectorAll('member');
    members.forEach(member => {
      const nameEl = member.querySelector('name');
      const valueEl = member.querySelector('value');

      if (nameEl && valueEl) {
        const name = nameEl.textContent?.trim() || '';
        const value = this.parseValue(valueEl);
        result[name] = value;
      }
    });

    return result;
  }

  /**
   * Parse XML value
   */
  static parseValue(valueElement: Element): any {
    const firstChild = valueElement.firstElementChild;

    if (!firstChild) {
      return valueElement.textContent?.trim() || '';
    }

    const tagName = firstChild.tagName.toLowerCase();
    const text = firstChild.textContent?.trim() || '';

    switch (tagName) {
      case 'string':
        return text;
      case 'int':
      case 'i4':
        return parseInt(text);
      case 'boolean':
        return text === '1' || text === 'true';
      case 'double':
        return parseFloat(text);
      case 'struct':
        return this.parseStruct(firstChild);
      case 'array':
        return this.parseArray(firstChild);
      default:
        return text;
    }
  }

  /**
   * Parse XML array
   */
  static parseArray(arrayElement: Element): any[] {
    const result: any[] = [];
    const data = arrayElement.querySelector('data');

    if (data) {
      const values = data.querySelectorAll(':scope > value');
      values.forEach(valueEl => {
        result.push(this.parseValue(valueEl));
      });
    }

    return result;
  }

  /**
   * Escape XML special characters
   */
  static escapeXml(str: string): string {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&apos;');
  }

  /**
   * Generate MAC address
   */
  static generateMAC(): string {
    let mac = Utils.storage.get('linkpoint_mac', null);
    if (!mac) {
      const bytes = new Uint8Array(6);
      globalThis.crypto.getRandomValues(bytes);
      mac = Array.from(bytes)
        .map(b => b.toString(16).padStart(2, '0').toUpperCase())
        .join(':');
      Utils.storage.set('linkpoint_mac', mac);
    }
    return mac;
  }

  /**
   * Generate ID0
   */
  static generateID0(): string {
    return Utils.generateUUID();
  }

  /**
   * Generate viewer digest
   */
  static generateViewerDigest(): string {
    return Utils.generateUUID();
  }

  /**
   * Calculate MD5 hash (pure JavaScript implementation)
   */
  static async md5(str: string): Promise<string> {
    return CryptoJS.MD5(str).toString();
  }

  /**
   * Hash password for Second Life (MD5)
   */
  static async hashPassword(password: string): Promise<string> {
    const hash = await this.md5(password);
    return hash.toLowerCase();
  }
}
