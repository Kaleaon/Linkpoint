import { JpxImage } from 'jpeg2000';

export interface DecodeWorkerRequest {
  id: number;
  buffer: ArrayBuffer;
}

export interface DecodeWorkerResponse {
  id: number;
  width?: number;
  height?: number;
  rgbaBuffer?: ArrayBuffer;
  error?: string;
}

self.onmessage = (event: MessageEvent<DecodeWorkerRequest>) => {
  const { id, buffer } = event.data;
  try {
    const bytes = typeof Buffer !== 'undefined' ? Buffer.from(buffer) : new Uint8Array(buffer);
    const image = new JpxImage();
    image.parse(bytes as any);

    const width = image.width;
    const height = image.height;
    const rgba = new Uint8Array(width * height * 4);
    rgba.fill(255);

    for (const tile of image.tiles) {
      for (let y = 0; y < tile.height; y++) {
        for (let x = 0; x < tile.width; x++) {
          const source = (y * tile.width + x) * image.componentsCount;
          const target = ((tile.top + y) * width + tile.left + x) * 4;
          rgba[target] = tile.items[source];
          rgba[target + 1] = image.componentsCount > 1 ? tile.items[source + 1] : tile.items[source];
          rgba[target + 2] = image.componentsCount > 2 ? tile.items[source + 2] : tile.items[source];
          if (image.componentsCount > 3) rgba[target + 3] = tile.items[source + 3];
        }
      }
    }

    const response: DecodeWorkerResponse = {
      id,
      width,
      height,
      rgbaBuffer: rgba.buffer,
    };

    // Zero-copy transfer of RGBA ArrayBuffer back to main thread
    (self as any).postMessage(response, [rgba.buffer]);
  } catch (err: any) {
    const response: DecodeWorkerResponse = {
      id,
      error: String(err?.message || err),
    };
    (self as any).postMessage(response);
  }
};
