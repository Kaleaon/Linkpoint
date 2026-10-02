// @vitest-environment jsdom
import { describe, expect, it } from 'vitest';
import { MAX_SOURCE_BYTES, base64ToBlob, checkPhotoFile, fileToPhotoDataUrl, squareCrop } from '../contact-photo';

describe('squareCrop', () => {
  it('centres the square on the long side', () => {
    expect(squareCrop(600, 400)).toEqual({ sx: 100, sy: 0, size: 400 });
    expect(squareCrop(400, 600)).toEqual({ sx: 0, sy: 100, size: 400 });
    expect(squareCrop(256, 256)).toEqual({ sx: 0, sy: 0, size: 256 });
    expect(squareCrop(5, 4)).toEqual({ sx: 0, sy: 0, size: 4 });
  });

  it('rejects images with no usable size', () => {
    for (const [w, h] of [[0, 10], [10, 0], [-1, 5], [NaN, 5], [Infinity, 5]]) expect(() => squareCrop(w, h)).toThrow(/no size/);
  });
});

describe('checkPhotoFile', () => {
  it('accepts ordinary images and rejects everything else with a reason', () => {
    expect(checkPhotoFile({ type: 'image/png', size: 1000 })).toBeNull();
    expect(checkPhotoFile({ type: 'image/jpeg', size: MAX_SOURCE_BYTES })).toBeNull();
    expect(checkPhotoFile(null)).toMatch(/Choose an image/);
    expect(checkPhotoFile({ type: 'image/svg+xml', size: 10 })).toMatch(/JPEG, PNG/);
    expect(checkPhotoFile({ type: 'application/pdf', size: 10 })).toMatch(/JPEG, PNG/);
    expect(checkPhotoFile({ type: 'image/png', size: MAX_SOURCE_BYTES + 1 })).toMatch(/too large/);
    expect(checkPhotoFile({ type: 'image/png', size: 0 })).toMatch(/empty/);
  });
});

describe('fileToPhotoDataUrl', () => {
  it('refuses unsuitable files before decoding anything', async () => {
    await expect(fileToPhotoDataUrl(new Blob(['<svg/>'], { type: 'image/svg+xml' }))).rejects.toThrow(/JPEG, PNG/);
    await expect(fileToPhotoDataUrl(new Blob([], { type: 'image/png' }))).rejects.toThrow(/empty/);
  });
});

describe('base64ToBlob', () => {
  it('rebuilds the bytes and type', async () => {
    const blob = base64ToBlob(Buffer.from([1, 2, 3, 255]).toString('base64'), 'image/png');
    expect(blob.type).toBe('image/png');
    expect(blob.size).toBe(4);
    expect(new Uint8Array(await blob.arrayBuffer())).toEqual(new Uint8Array([1, 2, 3, 255]));
  });
});
