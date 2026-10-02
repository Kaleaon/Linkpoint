/**
 * Turn an image the user picked (or a resident's public profile picture) into
 * the small square JPEG a contact stores. Resizing happens in the browser with
 * a canvas, so the picture never leaves the device unless the user later
 * chooses to sync that contact to Google.
 */

import { MAX_PHOTO_CHARS } from './contacts';

export const PHOTO_SIZE = 256;
/** Source images larger than this are refused before decoding. */
export const MAX_SOURCE_BYTES = 10 * 1024 * 1024;
const ACCEPTED_TYPES = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];

/** The centred square of a width x height image, as a source rectangle. */
export function squareCrop(width: number, height: number): { sx: number; sy: number; size: number } {
  if (!(width > 0) || !(height > 0) || !Number.isFinite(width) || !Number.isFinite(height)) throw new Error('That image has no size.');
  const size = Math.min(width, height);
  return { sx: Math.floor((width - size) / 2), sy: Math.floor((height - size) / 2), size };
}

/** Check a picked file before doing any work on it. Returns an error message, or null if it is acceptable. */
export function checkPhotoFile(file: { type: string; size: number } | null | undefined): string | null {
  if (!file) return 'Choose an image.';
  if (!ACCEPTED_TYPES.includes(file.type)) return 'Choose a JPEG, PNG, WebP or GIF image.';
  if (file.size > MAX_SOURCE_BYTES) return 'That image is too large (10 MB at most).';
  if (file.size === 0) return 'That image is empty.';
  return null;
}

async function decode(file: Blob): Promise<{ width: number; height: number; draw: (ctx: CanvasRenderingContext2D, sx: number, sy: number, size: number, target: number) => void; close: () => void }> {
  if (typeof createImageBitmap === 'function') {
    const bitmap = await createImageBitmap(file);
    return { width: bitmap.width, height: bitmap.height, draw: (ctx, sx, sy, size, target) => ctx.drawImage(bitmap, sx, sy, size, size, 0, 0, target, target), close: () => bitmap.close() };
  }
  const url = URL.createObjectURL(file);
  const image = new Image();
  await new Promise<void>((resolve, reject) => { image.onload = () => resolve(); image.onerror = () => reject(new Error('That image could not be read.')); image.src = url; });
  return { width: image.naturalWidth, height: image.naturalHeight, draw: (ctx, sx, sy, size, target) => ctx.drawImage(image, sx, sy, size, size, 0, 0, target, target), close: () => URL.revokeObjectURL(url) };
}

/** Resize `file` to a centred square JPEG data URL small enough to store. */
export async function fileToPhotoDataUrl(file: Blob, size = PHOTO_SIZE): Promise<string> {
  const problem = checkPhotoFile(file);
  if (problem) throw new Error(problem);
  let source;
  try {
    source = await decode(file);
  } catch {
    throw new Error('That image could not be read.');
  }
  try {
    const { sx, sy, size: side } = squareCrop(source.width, source.height);
    const canvas = document.createElement('canvas');
    canvas.width = size;
    canvas.height = size;
    const ctx = canvas.getContext('2d');
    if (!ctx) throw new Error('This browser cannot resize images.');
    ctx.fillStyle = '#ffffff'; // JPEG has no alpha: flatten transparent PNGs onto white, not black
    ctx.fillRect(0, 0, size, size);
    source.draw(ctx, sx, sy, side, size);
    for (const quality of [0.85, 0.7, 0.55, 0.4]) {
      const dataUrl = canvas.toDataURL('image/jpeg', quality);
      if (dataUrl.length <= MAX_PHOTO_CHARS) return dataUrl;
    }
    throw new Error('That image is too detailed to store; try a smaller one.');
  } finally {
    source.close();
  }
}

/** Decode base64 image bytes (as returned by the profile picture service) into a Blob. */
export function base64ToBlob(base64: string, contentType: string): Blob {
  const binary = atob(base64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
  return new Blob([bytes], { type: contentType });
}
