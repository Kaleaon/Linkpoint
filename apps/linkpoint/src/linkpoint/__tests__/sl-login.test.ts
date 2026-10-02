import { describe, expect, it } from 'vitest';
import { createRequire } from 'node:module';
import { existsSync, readFileSync } from 'node:fs';
import { join } from 'node:path';
const require = createRequire(import.meta.url);
const actions = require('../../../core/sl-actions.cjs');
const patcher = require('../../../scripts/patch-metaverse.cjs');

class LoginParameters { firstName = ''; lastName = ''; password = ''; start = ''; url = ''; token?: string; mfa_hash?: string; }
const lib = { LoginParameters };
const request = (extra: any = {}) => ({ username: 'jane doe', password: 'pw', loginUrl: 'https://login.agni.lindenlab.com/cgi-bin/login.cgi', ...extra });

describe('parseLoginName (Lumiya SLAuth.SendLoginRequest, from the original smali)', () => {
  it('splits on the first space, dot or underscore', () => {
    expect(actions.parseLoginName('Jane Doe')).toEqual({ firstName: 'Jane', lastName: 'Doe' });
    expect(actions.parseLoginName('jane.doe')).toEqual({ firstName: 'jane', lastName: 'doe' });
    expect(actions.parseLoginName('jane_doe')).toEqual({ firstName: 'jane', lastName: 'doe' });
    expect(actions.parseLoginName('a.b c')).toEqual({ firstName: 'a', lastName: 'b c' });
  });
  it('uses Resident when there is no last name', () => {
    expect(actions.parseLoginName('jane')).toEqual({ firstName: 'jane', lastName: 'Resident' });
    expect(actions.parseLoginName('  jane  ')).toEqual({ firstName: 'jane', lastName: 'Resident' });
    expect(actions.parseLoginName('jane.')).toEqual({ firstName: 'jane', lastName: 'Resident' });
  });
  it('rejects empty names and control or markup characters', () => {
    for (const bad of ['', '   ', null, undefined, '.doe', 'jane doe\u0007', 'jane <b>', 'ja"ne']) expect(() => actions.parseLoginName(bad), String(bad)).toThrow();
  });
});

describe('normalizeStart', () => {
  it('maps first/home to home, passes a valid uri, and falls back to last', () => {
    expect(actions.normalizeStart(undefined)).toBe('last');
    expect(actions.normalizeStart('last')).toBe('last');
    expect(actions.normalizeStart('first')).toBe('home');
    expect(actions.normalizeStart('home')).toBe('home');
    expect(actions.normalizeStart('whatever')).toBe('last');
    expect(actions.normalizeStart('uri:Ahern&10&20&30')).toBe('uri:Ahern&10&20&30');
    expect(actions.normalizeStart('uri:Da Boom')).toBe('uri:Da Boom&128&128&30');
  });
  it('refuses a uri that is malformed or tries to carry extra fields', () => {
    for (const bad of ['uri:Ahern&1&2', 'uri:Ahern&1&2&3&4', 'uri:../x&1&2&3', 'uri:Ahern&a&b&c', 'uri:&1&2&3', 'uri:Ahern<script>&1&2&3', `uri:${'A'.repeat(80)}&1&2&3`]) {
      expect(() => actions.normalizeStart(bad), bad).toThrow(/start location/);
    }
  });
});

describe('buildLoginParams', () => {
  it('builds validated parameters', () => {
    const p = actions.buildLoginParams(request({ start: 'first' }), lib);
    expect(p).toMatchObject({ firstName: 'jane', lastName: 'doe', password: 'pw', start: 'home', url: 'https://login.agni.lindenlab.com/cgi-bin/login.cgi' });
    expect(p.token).toBeUndefined();
    expect(p.mfa_hash).toBeUndefined();
  });
  it('carries the MFA code and device hash only when given, trimmed', () => {
    const p = actions.buildLoginParams(request({ mfaToken: ' 123 456 ', mfaHash: 'abc' }), lib);
    expect(p.token).toBe('123456');
    expect(p.mfa_hash).toBe('abc');
    expect(actions.buildLoginParams(request({ mfaToken: '9'.repeat(100) }), lib).token).toHaveLength(32);
  });
  it('rejects missing passwords and unusable login addresses', () => {
    expect(() => actions.buildLoginParams(request({ password: '' }), lib)).toThrow(/password/);
    expect(() => actions.buildLoginParams(request({ loginUrl: 'not a url' }), lib)).toThrow(/login address/);
    expect(() => actions.buildLoginParams(request({ loginUrl: 'file:///etc/passwd' }), lib)).toThrow(/http/);
    expect(() => actions.buildLoginParams(request({ loginUrl: 'javascript:alert(1)' }), lib)).toThrow();
  });
});

describe('describeLoginError / loginFailure', () => {
  const grid = (reason: string, message = 'grid text') => Object.assign(new Error(message), { reason });
  it('recognises an MFA challenge and a rejected code', () => {
    expect(actions.describeLoginError(grid('mfa_challenge'))).toMatchObject({ code: 'mfa_required', mfaRequired: true });
    expect(actions.describeLoginError(grid('mfa_failure'))).toMatchObject({ code: 'mfa_failed', mfaRequired: true });
  });
  it('maps the other reasons the grid gives', () => {
    expect(actions.describeLoginError(grid('key'))).toMatchObject({ code: 'bad_credentials', mfaRequired: false });
    expect(actions.describeLoginError(grid('presence')).code).toBe('already_logged_in');
    expect(actions.describeLoginError(grid('tos')).code).toBe('terms');
    expect(actions.describeLoginError(grid('update')).code).toBe('update_required');
  });
  it('falls back to the grid message, then to a generic one, for anything else', () => {
    expect(actions.describeLoginError(grid('something_new', 'Grid says no'))).toMatchObject({ code: 'login_failed', message: 'Grid says no', gridMessage: 'Grid says no' });
    expect(actions.describeLoginError(new Error('')).message).toBe('Login failed');
    expect(actions.describeLoginError(undefined).mfaRequired).toBe(false);
  });
  it('carries the details in the message so they survive Electron IPC', () => {
    const failure = actions.loginFailure(grid('mfa_challenge'));
    expect(failure.message.startsWith(actions.LOGIN_FAILURE_PREFIX)).toBe(true);
    expect(failure.details.mfaRequired).toBe(true);
  });
});

describe('viewer identity patch for node-metaverse (TPV_COMPLIANCE.md section 1)', () => {
  const sample = "const version = packageJson.version;\n  client.methodCall('login_to_simulator', [{ first: 'x', channel: 'libnmv', major }]);";
  it('replaces the library channel and version with this viewer\'s', () => {
    const out = patcher.patchLoginIdentity(sample, 'Linkpoint Viewer', '2.0.0');
    expect(out).toContain('channel: "Linkpoint Viewer"');
    expect(out).toContain('const version = "2.0.0";');
    expect(out).not.toContain('libnmv');
    expect(patcher.patchLoginIdentity(out, 'Linkpoint Viewer', '2.0.0')).toBe(out); // idempotent
  });
  it('reports a library whose shape changed instead of leaving it silently unpatched', () => {
    expect(patcher.patchLoginIdentity('totally different', 'Linkpoint Viewer', '2.0.0')).toBeNull();
  });
  it('reads the identity from its single source of truth', () => {
    expect(patcher.readViewerIdentity()).toEqual({ channel: 'Linkpoint Viewer', version: '2.0.0' });
  });
  it('is never "libnmv" or the official viewer name', () => {
    const { channel } = patcher.readViewerIdentity();
    expect(channel).not.toMatch(/libnmv|^Second Life/i);
  });
  // After `npm ci` the postinstall patch has run, so the shipped library must not say libnmv.
  const loginHandler = join(process.cwd(), 'node_modules/@caspertech/node-metaverse/dist/lib/LoginHandler.js');
  it.skipIf(!existsSync(loginHandler))('the installed library identifies as this viewer', () => {
    const text = readFileSync(loginHandler, 'utf8');
    expect(text).not.toContain("channel: 'libnmv'");
    expect(text).toContain(`channel: ${JSON.stringify(patcher.readViewerIdentity().channel)}`);
  });
});
