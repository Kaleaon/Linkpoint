/**
 * Linkpoint PWA - 3D Primitive Shapes
 */

export class Primitives3D {
  /**
   * Create cube mesh
   */
  static createCube(size: number = 1) {
    const s = size / 2;

    const vertices = [
      // Front face
      -s, -s,  s,  s, -s,  s,  s,  s,  s, -s,  s,  s,
      // Back face
      -s, -s, -s, -s,  s, -s,  s,  s, -s,  s, -s, -s,
      // Top face
      -s,  s, -s, -s,  s,  s,  s,  s,  s,  s,  s, -s,
      // Bottom face
      -s, -s, -s,  s, -s, -s,  s, -s,  s, -s, -s,  s,
      // Right face
       s, -s, -s,  s,  s, -s,  s,  s,  s,  s, -s,  s,
      // Left face
      -s, -s, -s, -s, -s,  s, -s,  s,  s, -s,  s, -s
    ];

    const normals = [
      // Front
      0, 0, 1,  0, 0, 1,  0, 0, 1,  0, 0, 1,
      // Back
      0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1,
      // Top
      0, 1, 0,  0, 1, 0,  0, 1, 0,  0, 1, 0,
      // Bottom
      0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0,
      // Right
      1, 0, 0,  1, 0, 0,  1, 0, 0,  1, 0, 0,
      // Left
      -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0
    ];

    const texCoords = [
      // Front
      0, 0,  1, 0,  1, 1,  0, 1,
      // Back
      0, 0,  1, 0,  1, 1,  0, 1,
      // Top
      0, 0,  1, 0,  1, 1,  0, 1,
      // Bottom
      0, 0,  1, 0,  1, 1,  0, 1,
      // Right
      0, 0,  1, 0,  1, 1,  0, 1,
      // Left
      0, 0,  1, 0,  1, 1,  0, 1
    ];

    const indices = [
      0, 1, 2,  0, 2, 3,    // Front
      4, 5, 6,  4, 6, 7,    // Back
      8, 9, 10, 8, 10, 11,  // Top
      12, 13, 14, 12, 14, 15, // Bottom
      16, 17, 18, 16, 18, 19, // Right
      20, 21, 22, 20, 22, 23  // Left
    ];

    return { vertices, normals, texCoords, indices };
  }

  /**
   * Create sphere mesh
   */
  static createSphere(radius: number = 1, segments: number = 32, rings: number = 16) {
    const vertices: number[] = [];
    const normals: number[] = [];
    const texCoords: number[] = [];
    const indices: number[] = [];

    for (let ring = 0; ring <= rings; ring++) {
      const theta = ring * Math.PI / rings;
      const sinTheta = Math.sin(theta);
      const cosTheta = Math.cos(theta);

      for (let seg = 0; seg <= segments; seg++) {
        const phi = seg * 2 * Math.PI / segments;
        const sinPhi = Math.sin(phi);
        const cosPhi = Math.cos(phi);

        const x = cosPhi * sinTheta;
        const y = cosTheta;
        const z = sinPhi * sinTheta;

        vertices.push(radius * x, radius * y, radius * z);
        normals.push(x, y, z);
        texCoords.push(seg / segments, ring / rings);
      }
    }

    for (let ring = 0; ring < rings; ring++) {
      for (let seg = 0; seg < segments; seg++) {
        const first = ring * (segments + 1) + seg;
        const second = first + segments + 1;

        indices.push(first, second, first + 1);
        indices.push(second, second + 1, first + 1);
      }
    }

    return { vertices, normals, texCoords, indices };
  }

  /**
   * Create plane mesh
   */
  static createPlane(width: number = 1, height: number = 1, widthSegments: number = 1, heightSegments: number = 1) {
    const vertices: number[] = [];
    const normals: number[] = [];
    const texCoords: number[] = [];
    const indices: number[] = [];

    const w = width / 2;
    const h = height / 2;

    for (let iy = 0; iy <= heightSegments; iy++) {
      const y = iy * height / heightSegments - h;
      const v = iy / heightSegments;

      for (let ix = 0; ix <= widthSegments; ix++) {
        const x = ix * width / widthSegments - w;
        const u = ix / widthSegments;

        vertices.push(x, y, 0);
        normals.push(0, 0, 1);
        texCoords.push(u, v);
      }
    }

    for (let iy = 0; iy < heightSegments; iy++) {
      for (let ix = 0; ix < widthSegments; ix++) {
        const a = ix + (widthSegments + 1) * iy;
        const b = ix + (widthSegments + 1) * (iy + 1);
        const c = (ix + 1) + (widthSegments + 1) * (iy + 1);
        const d = (ix + 1) + (widthSegments + 1) * iy;

        indices.push(a, b, d);
        indices.push(b, c, d);
      }
    }

    return { vertices, normals, texCoords, indices };
  }

  /**
   * Create cylinder mesh
   */
  static createCylinder(radiusTop: number = 1, radiusBottom: number = 1, height: number = 1, segments: number = 32) {
    const vertices: number[] = [];
    const normals: number[] = [];
    const texCoords: number[] = [];
    const indices: number[] = [];

    const halfHeight = height / 2;

    // Generate vertices
    for (let y = 0; y <= 1; y++) {
      const radius = y === 0 ? radiusBottom : radiusTop;
      const posY = y * height - halfHeight;

      for (let seg = 0; seg <= segments; seg++) {
        const theta = seg * 2 * Math.PI / segments;
        const x = radius * Math.cos(theta);
        const z = radius * Math.sin(theta);

        vertices.push(x, posY, z);
        normals.push(Math.cos(theta), 0, Math.sin(theta));
        texCoords.push(seg / segments, y);
      }
    }

    // Generate indices
    for (let y = 0; y < 1; y++) {
      for (let seg = 0; seg < segments; seg++) {
        const first = y * (segments + 1) + seg;
        const second = first + segments + 1;

        indices.push(first, second, first + 1);
        indices.push(second, second + 1, first + 1);
      }
    }

    // Add caps
    const baseCenter = vertices.length / 3;
    vertices.push(0, -halfHeight, 0);
    normals.push(0, -1, 0);
    texCoords.push(0.5, 0.5);

    for (let seg = 0; seg <= segments; seg++) {
      const theta = seg * 2 * Math.PI / segments;
      vertices.push(radiusBottom * Math.cos(theta), -halfHeight, radiusBottom * Math.sin(theta));
      normals.push(0, -1, 0);
      texCoords.push(0.5 + 0.5 * Math.cos(theta), 0.5 + 0.5 * Math.sin(theta));
    }

    for (let seg = 0; seg < segments; seg++) {
      indices.push(baseCenter, baseCenter + seg + 2, baseCenter + seg + 1);
    }

    return { vertices, normals, texCoords, indices };
  }

  /**
   * Create grid mesh
   */
  static createGrid(size: number = 256, divisions: number = 16) {
    const vertices: number[] = [];
    const normals: number[] = [];
    const texCoords: number[] = [];
    const indices: number[] = [];

    const step = size / divisions;
    const halfSize = size / 2;

    for (let i = 0; i <= divisions; i++) {
      for (let j = 0; j <= divisions; j++) {
        const x = j * step - halfSize;
        const y = i * step - halfSize;

        vertices.push(x, y, 0);
        normals.push(0, 0, 1);
        texCoords.push(j / divisions, i / divisions);
      }
    }

    for (let i = 0; i < divisions; i++) {
      for (let j = 0; j < divisions; j++) {
        const a = i * (divisions + 1) + j;
        const b = a + divisions + 1;
        const c = a + 1;
        const d = b + 1;

        indices.push(a, b, c);
        indices.push(b, d, c);
      }
    }

    return { vertices, normals, texCoords, indices };
  }

  /**
   * Calculate tangents for normal mapping
   */
  static calculateTangents(vertices: number[], normals: number[], texCoords: number[], indices: number[]) {
    const tangents = new Array(vertices.length).fill(0);

    for (let i = 0; i < indices.length; i += 3) {
      const i0 = indices[i] * 3;
      const i1 = indices[i + 1] * 3;
      const i2 = indices[i + 2] * 3;

      const v0 = [vertices[i0], vertices[i0 + 1], vertices[i0 + 2]];
      const v1 = [vertices[i1], vertices[i1 + 1], vertices[i1 + 2]];
      const v2 = [vertices[i2], vertices[i2 + 1], vertices[i2 + 2]];

      const uv0 = [texCoords[indices[i] * 2], texCoords[indices[i] * 2 + 1]];
      const uv1 = [texCoords[indices[i + 1] * 2], texCoords[indices[i + 1] * 2 + 1]];
      const uv2 = [texCoords[indices[i + 2] * 2], texCoords[indices[i + 2] * 2 + 1]];

      const edge1 = [v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]];
      const edge2 = [v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]];

      const deltaUV1 = [uv1[0] - uv0[0], uv1[1] - uv0[1]];
      const deltaUV2 = [uv2[0] - uv0[0], uv2[1] - uv0[1]];

      const f = 1.0 / (deltaUV1[0] * deltaUV2[1] - deltaUV2[0] * deltaUV1[1]);

      const tangent = [
        f * (deltaUV2[1] * edge1[0] - deltaUV1[1] * edge2[0]),
        f * (deltaUV2[1] * edge1[1] - deltaUV1[1] * edge2[1]),
        f * (deltaUV2[1] * edge1[2] - deltaUV1[1] * edge2[2])
      ];

      tangents[i0] += tangent[0];
      tangents[i0 + 1] += tangent[1];
      tangents[i0 + 2] += tangent[2];
      tangents[i1] += tangent[0];
      tangents[i1 + 1] += tangent[1];
      tangents[i1 + 2] += tangent[2];
      tangents[i2] += tangent[0];
      tangents[i2 + 1] += tangent[1];
      tangents[i2 + 2] += tangent[2];
    }

    // Normalize tangents
    for (let i = 0; i < tangents.length; i += 3) {
      const len = Math.sqrt(tangents[i] ** 2 + tangents[i + 1] ** 2 + tangents[i + 2] ** 2);
      if (len > 0) {
        tangents[i] /= len;
        tangents[i + 1] /= len;
        tangents[i + 2] /= len;
      }
    }

    return tangents;
  }
}
