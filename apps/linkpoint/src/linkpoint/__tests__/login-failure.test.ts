import { beforeEach, describe, expect, it } from 'vitest';
import { LoginFailure, failureFromResponseBody, toLoginFailure } from '../login-failure';
import { forgetMfaHash, getMfaHash, saveMfaHash } from '../mfa-store';

const details = { reason: 'mfa_challenge', code: 'mfa_required', mfaRequired: true, message: 'Enter the code' };

describe('login failures across process boundaries', () => {
  it('recovers details from an Electron IPC error message', () => {
    const ipc = new Error(`Error invoking remote method 'linkpoint:viewer-connect': Error: LOGIN_FAILURE:${JSON.stringify(details)}`);
    const failure = toLoginFailure(ipc) as LoginFailure;
    expect(failure).toBeInstanceOf(LoginFailure);
    expect(failure.details.mfaRequired).toBe(true);
    expect(failure.message).toBe('Enter the code');
  });
  it('leaves ordinary errors, garbage and existing failures alone', () => {
    const plain = new Error('network down');
    expect(toLoginFailure(plain)).toBe(plain);
    const broken = new Error('LOGIN_FAILURE:{not json');
    expect(toLoginFailure(broken)).toBe(broken);
    const existing = new LoginFailure(details);
    expect(toLoginFailure(existing)).toBe(existing);
    expect(toLoginFailure('text')).toBe('text');
    expect(toLoginFailure(undefined)).toBeUndefined();
  });
  it('builds a failure from a server error body only when it has a reason code', () => {
    expect(failureFromResponseBody({ error: 'x', code: 'bad_credentials', reason: 'key', mfaRequired: false })?.details.code).toBe('bad_credentials');
    expect(failureFromResponseBody({ error: 'plain failure' })).toBeNull();
    expect(failureFromResponseBody(null)).toBeNull();
  });
});

describe('remembered MFA device hashes', () => {
  beforeEach(() => localStorage.clear());
  it('are kept per grid and avatar, case-insensitively, and can be forgotten', () => {
    expect(getMfaHash('agni', 'Jane Doe')).toBe('');
    saveMfaHash('agni', 'Jane Doe', 'hash-1');
    saveMfaHash('aditi', 'Jane Doe', 'hash-2');
    expect(getMfaHash('AGNI', ' jane doe ')).toBe('hash-1');
    expect(getMfaHash('aditi', 'jane doe')).toBe('hash-2');
    expect(getMfaHash('agni', 'someone else')).toBe('');
    forgetMfaHash('agni', 'jane doe');
    expect(getMfaHash('agni', 'Jane Doe')).toBe('');
    expect(getMfaHash('aditi', 'Jane Doe')).toBe('hash-2');
  });
  it('ignores an empty hash', () => {
    saveMfaHash('agni', 'a', '');
    expect(getMfaHash('agni', 'a')).toBe('');
  });
});
