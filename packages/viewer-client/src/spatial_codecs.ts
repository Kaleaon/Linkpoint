export class Vector3U16 {
  static dequantize(
    u16Vec: [number, number, number],
    min: [number, number, number] = [-128.0, -128.0, -128.0],
    max: [number, number, number] = [128.0, 128.0, 128.0]
  ): [number, number, number] {
    return [
      this.dequantizeComponent(u16Vec[0], min[0], max[0]),
      this.dequantizeComponent(u16Vec[1], min[1], max[1]),
      this.dequantizeComponent(u16Vec[2], min[2], max[2]),
    ];
  }

  static quantize(
    vec: [number, number, number],
    min: [number, number, number] = [-128.0, -128.0, -128.0],
    max: [number, number, number] = [128.0, 128.0, 128.0]
  ): [number, number, number] {
    return [
      this.quantizeComponent(vec[0], min[0], max[0]),
      this.quantizeComponent(vec[1], min[1], max[1]),
      this.quantizeComponent(vec[2], min[2], max[2]),
    ];
  }

  private static dequantizeComponent(val: number, min: number, max: number): number {
    const norm = val / 65535.0;
    const range = max - min;
    const result = norm * range + min;
    if (Math.abs(result) < 0.01) {
      return 0.0;
    }
    return Math.round(result * 10000) / 10000;
  }

  private static quantizeComponent(val: number, min: number, max: number): number {
    const clamped = Math.max(min, Math.min(max, val));
    const norm = (clamped - min) / (max - min);
    return Math.round(norm * 65535);
  }
}

export class Vector3U8 {
  static dequantize(
    u8Vec: [number, number, number],
    min: [number, number, number] = [0.0, 0.0, 0.0],
    max: [number, number, number] = [255.0, 255.0, 255.0]
  ): [number, number, number] {
    return [
      this.dequantizeComponent(u8Vec[0], min[0], max[0]),
      this.dequantizeComponent(u8Vec[1], min[1], max[1]),
      this.dequantizeComponent(u8Vec[2], min[2], max[2]),
    ];
  }

  static quantize(
    vec: [number, number, number],
    min: [number, number, number] = [0.0, 0.0, 0.0],
    max: [number, number, number] = [255.0, 255.0, 255.0]
  ): [number, number, number] {
    return [
      this.quantizeComponent(vec[0], min[0], max[0]),
      this.quantizeComponent(vec[1], min[1], max[1]),
      this.quantizeComponent(vec[2], min[2], max[2]),
    ];
  }

  private static dequantizeComponent(val: number, min: number, max: number): number {
    const norm = val / 255.0;
    const range = max - min;
    const result = norm * range + min;
    return Math.round(result * 10000) / 10000;
  }

  private static quantizeComponent(val: number, min: number, max: number): number {
    const clamped = Math.max(min, Math.min(max, val));
    const norm = (clamped - min) / (max - min);
    return Math.round(norm * 255);
  }
}

export class PackedQuaternion {
  static unpack16(i16Vec: [number, number, number]): [number, number, number, number] {
    const x = i16Vec[0] / 32767.0;
    const y = i16Vec[1] / 32767.0;
    const z = i16Vec[2] / 32767.0;

    const sumSq = x * x + y * y + z * z;
    const w = sumSq < 1.0 ? Math.sqrt(1.0 - sumSq) : 0.0;

    return [
      Math.round(x * 10000) / 10000,
      Math.round(y * 10000) / 10000,
      Math.round(z * 10000) / 10000,
      Math.round(w * 10000) / 10000,
    ];
  }

  static pack16(quat: [number, number, number, number]): [number, number, number] {
    const [x, y, z, w] = quat;
    const s = w < 0 ? -1 : 1;

    return [
      Math.max(-32767, Math.min(32767, Math.round(x * s * 32767))),
      Math.max(-32767, Math.min(32767, Math.round(y * s * 32767))),
      Math.max(-32767, Math.min(32767, Math.round(z * s * 32767))),
    ];
  }
}
