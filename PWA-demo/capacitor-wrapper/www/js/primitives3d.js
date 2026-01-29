/**
 * Linkpoint PWA - 3D Primitive Shapes
 */

class Primitives3D {
  /**
   * Create cube mesh
   */
  static createCube(size = 1) {
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
  static createSphere(radius = 1, segments = 32, rings = 16) {
    const vertices = [];
    const normals = [];
    const texCoords = [];
    const indices = [];

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
  static createPlane(width = 1, height = 1, widthSegments = 1, heightSegments = 1) {
    const vertices = [];
    const normals = [];
    const texCoords = [];
    const indices = [];

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
  static createCylinder(radiusTop = 1, radiusBottom = 1, height = 1, segments = 32) {
    const vertices = [];
    const normals = [];
    const texCoords = [];
    const indices = [];

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
   * Create cone mesh
   */
  static createCone(radius = 1, height = 1, segments = 32) {
    return this.createCylinder(0, radius, height, segments);
  }

  /**
   * Create grid mesh
   */
  static createGrid(size = 256, divisions = 16) {
    const vertices = [];
    const normals = [];
    const texCoords = [];
    const indices = [];

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
  static calculateTangents(vertices, normals, texCoords, indices) {
    // Use Float32Array for better performance
    const vertexCount = vertices.length;
    const tangents = new Float32Array(vertexCount);

    // Cache lengths for loop bounds
    const indexCount = indices.length;

    // Variables for loop to avoid allocation
    let i0, i1, i2;
    let x1, y1, z1, x2, y2, z2;
    let s1, t1, s2, t2;
    let r;
    let tx, ty, tz;

    for (let i = 0; i < indexCount; i += 3) {
      i0 = indices[i] * 3;
      i1 = indices[i + 1] * 3;
      i2 = indices[i + 2] * 3;

      // Vertices
      // v1 - v0
      x1 = vertices[i1] - vertices[i0];
      y1 = vertices[i1 + 1] - vertices[i0 + 1];
      z1 = vertices[i1 + 2] - vertices[i0 + 2];

      // v2 - v0
      x2 = vertices[i2] - vertices[i0];
      y2 = vertices[i2 + 1] - vertices[i0 + 1];
      z2 = vertices[i2 + 2] - vertices[i0 + 2];

      // UVs
      // uv1 - uv0
      s1 = texCoords[indices[i + 1] * 2] - texCoords[indices[i] * 2];
      t1 = texCoords[indices[i + 1] * 2 + 1] - texCoords[indices[i] * 2 + 1];

      // uv2 - uv0
      s2 = texCoords[indices[i + 2] * 2] - texCoords[indices[i] * 2];
      t2 = texCoords[indices[i + 2] * 2 + 1] - texCoords[indices[i] * 2 + 1];

      r = 1.0 / (s1 * t2 - s2 * t1);

      // Tangent
      tx = (t2 * x1 - t1 * x2) * r;
      ty = (t2 * y1 - t1 * y2) * r;
      tz = (t2 * z1 - t1 * z2) * r;

      // Accumulate
      tangents[i0] += tx;
      tangents[i0 + 1] += ty;
      tangents[i0 + 2] += tz;

      tangents[i1] += tx;
      tangents[i1 + 1] += ty;
      tangents[i1 + 2] += tz;

      tangents[i2] += tx;
      tangents[i2 + 1] += ty;
      tangents[i2 + 2] += tz;
    }

    // Normalize tangents
    const lenTangents = tangents.length;
    for (let i = 0; i < lenTangents; i += 3) {
      const x = tangents[i];
      const y = tangents[i + 1];
      const z = tangents[i + 2];

      const sqLen = x * x + y * y + z * z;

      if (sqLen > 1e-12) {
        const invLen = 1.0 / Math.sqrt(sqLen);
        tangents[i] = x * invLen;
        tangents[i + 1] = y * invLen;
        tangents[i + 2] = z * invLen;
      }
    }

    return tangents;
  }
}

// Make available globally
window.Primitives3D = Primitives3D;
