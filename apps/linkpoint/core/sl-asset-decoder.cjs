const sharp = require('sharp');
const { JpxImage } = require('jpeg2000');
const { LLMesh, LLGLTFMaterial } = require('@caspertech/node-metaverse');

function number(value, axis) {
  if (typeof value?.[axis] === 'number') return value[axis];
  const getter = `get${axis.toUpperCase()}`;
  return typeof value?.[getter] === 'function' ? value[getter]() : 0;
}

async function decodeLLMesh(buffer) {
  const mesh = await LLMesh.from(buffer);
  return normalizeLLMesh(mesh);
}

function matrixValues(matrix) {
  if (!matrix) return null;
  if (Array.isArray(matrix)) return matrix.map(Number);
  if (typeof matrix.toArray === 'function') return matrix.toArray().map(Number);
  const values = matrix.values || matrix.elements || matrix.m;
  return Array.isArray(values) || ArrayBuffer.isView(values) ? Array.from(values, Number) : null;
}

function decodeWeights(weights, vertexCount) {
  const joints = [], jointWeights = [];
  for (let vertex = 0; vertex < vertexCount; vertex++) {
    const influences = Object.entries(weights?.[vertex] || {})
      .map(([joint, weight]) => [Number(joint), Number(weight) / 65535])
      .filter(([joint, weight]) => Number.isInteger(joint) && joint >= 0 && weight > 0)
      .sort((a, b) => b[1] - a[1]).slice(0, 4);
    const total = influences.reduce((sum, influence) => sum + influence[1], 0) || 1;
    for (let slot = 0; slot < 4; slot++) {
      joints.push(influences[slot]?.[0] || 0);
      jointWeights.push((influences[slot]?.[1] || 0) / total);
    }
  }
  return { joints, jointWeights };
}

/** Area-weighted smooth vertex normals for an indexed triangle list (counter-clockwise front faces). */
function computeNormals(vertices, indices) {
  const normals = new Array(vertices.length).fill(0);
  for (let i = 0; i + 2 < indices.length; i += 3) {
    const [a, b, c] = [indices[i] * 3, indices[i + 1] * 3, indices[i + 2] * 3];
    const e1 = [vertices[b] - vertices[a], vertices[b + 1] - vertices[a + 1], vertices[b + 2] - vertices[a + 2]];
    const e2 = [vertices[c] - vertices[a], vertices[c + 1] - vertices[a + 1], vertices[c + 2] - vertices[a + 2]];
    const n = [e1[1] * e2[2] - e1[2] * e2[1], e1[2] * e2[0] - e1[0] * e2[2], e1[0] * e2[1] - e1[1] * e2[0]];
    for (const o of [a, b, c]) { normals[o] += n[0]; normals[o + 1] += n[1]; normals[o + 2] += n[2]; }
  }
  for (let o = 0; o < normals.length; o += 3) {
    const length = Math.hypot(normals[o], normals[o + 1], normals[o + 2]);
    if (length > 1e-12) { normals[o] /= length; normals[o + 1] /= length; normals[o + 2] /= length; }
    else { normals[o] = 0; normals[o + 1] = 0; normals[o + 2] = 1; }
  }
  return normals;
}

function decodeSubmesh(submesh, materialIndex) {
  if (submesh.noGeometry || !submesh.position?.length || !submesh.triangleList?.length) return null;
  const vertexCount = submesh.position.length;
  const vertices = [], normals = [], texCoords = [];
  submesh.position.forEach((point, index) => {
    vertices.push(number(point, 'x'), number(point, 'y'), number(point, 'z'));
    const normal = submesh.normal?.[index];
    normals.push(normal ? number(normal, 'x') : 0, normal ? number(normal, 'y') : 0, normal ? number(normal, 'z') : 1);
    const uv = submesh.texCoord0?.[index];
    texCoords.push(uv ? number(uv, 'x') : 0, uv ? number(uv, 'y') : 0);
  });
  const indices = submesh.triangleList.map(Number);
  if (indices.some((index) => !Number.isInteger(index) || index < 0 || index >= vertexCount)) {
    throw new Error(`LLMesh material ${materialIndex} contains an invalid vertex index`);
  }
  if (!submesh.normal?.length) {
    const computed = computeNormals(vertices, indices);
    for (let i = 0; i < computed.length; i++) normals[i] = computed[i];
  }
  return { materialIndex, vertices, normals, texCoords, indices, ...decodeWeights(submesh.weights, vertexCount) };
}

function normalizeLLMesh(mesh) {
  const lods = {};
  for (const name of ['high_lod', 'medium_lod', 'low_lod', 'lowest_lod']) {
    const parts = (mesh.lodLevels?.[name] || []).map(decodeSubmesh).filter(Boolean);
    if (parts.length) lods[name] = parts;
  }
  const selectedLod = ['high_lod', 'medium_lod', 'low_lod', 'lowest_lod'].find((name) => lods[name]);
  if (!selectedLod) throw new Error('LLMesh contains no renderable LOD');
  const parts = lods[selectedLod];
  const skin = mesh.skin ? {
    jointNames: [...(mesh.skin.jointNames || [])],
    bindShapeMatrix: matrixValues(mesh.skin.bindShapeMatrix),
    inverseBindMatrices: (mesh.skin.inverseBindMatrix || []).map(matrixValues),
    alternateInverseBindMatrices: (mesh.skin.altInverseBindMatrix || []).map(matrixValues),
    // SL stores the pelvis offset as a single height; older shapes were matrices.
    pelvisOffset: typeof mesh.skin.pelvisOffset === 'number' ? mesh.skin.pelvisOffset : matrixValues(mesh.skin.pelvisOffset),
    lockScaleIfJointPosition: Boolean(mesh.skin.lockScaleIfJointPosition),
  } : null;
  const physics = mesh.physicsConvex ? {
    hullList: [...(mesh.physicsConvex.hullList || [])],
    positions: (mesh.physicsConvex.positions || []).map((point) => [number(point, 'x'), number(point, 'y'), number(point, 'z')]),
    boundingVertices: (mesh.physicsConvex.boundingVerts || []).map((point) => [number(point, 'x'), number(point, 'y'), number(point, 'z')]),
    domain: mesh.physicsConvex.domain ? {
      min: [number(mesh.physicsConvex.domain.min, 'x'), number(mesh.physicsConvex.domain.min, 'y'), number(mesh.physicsConvex.domain.min, 'z')],
      max: [number(mesh.physicsConvex.domain.max, 'x'), number(mesh.physicsConvex.domain.max, 'y'), number(mesh.physicsConvex.domain.max, 'z')],
    } : null,
  } : null;
  return {
    format: 'llmesh-v1', selectedLod, parts, lods, skin, physics,
    metadata: { version: mesh.version ?? null, creatorId: mesh.creatorID?.toString?.() || null, submodelId: mesh.submodel_id ?? null, cost: mesh.costData || null },
    // Keep the original shape for older web clients while no longer truncating
    // meshes at the WebGL unsigned-short boundary: each material is its own draw.
    ...parts[0],
  };
}

function materialTexture(data, info) {
  if (!info || !Number.isInteger(info.index)) return null;
  const source = data.textures?.[info.index]?.source;
  const uri = Number.isInteger(source) ? data.images?.[source]?.uri : null;
  const textureId = typeof uri === 'string' ? uri.replace(/^(?:asset|sl|uuid):(?:\/\/)?/i, '') : null;
  const transform = info.extensions?.KHR_texture_transform;
  return textureId ? {
    textureId,
    texCoord: transform?.texCoord ?? info.texCoord ?? 0,
    offset: transform?.offset || [0, 0], scale: transform?.scale || [1, 1], rotation: transform?.rotation || 0,
  } : null;
}

function decodeGLTFMaterial(buffer) {
  const envelope = new LLGLTFMaterial(buffer);
  return normalizeGLTFMaterial(envelope.data || {});
}

function normalizeGLTFMaterial(data) {
  const material = data.materials?.[0] || {};
  const metallic = material.pbrMetallicRoughness || {};
  return {
    format: 'gltf-pbr-v1', name: material.name || '',
    baseColor: metallic.baseColorFactor || [1, 1, 1, 1],
    metallic: metallic.metallicFactor ?? 1,
    roughness: metallic.roughnessFactor ?? 1,
    emissive: material.emissiveFactor || [0, 0, 0],
    alphaMode: material.alphaMode || 'OPAQUE', alphaCutoff: material.alphaCutoff ?? 0.5,
    doubleSided: Boolean(material.doubleSided),
    textures: {
      baseColor: materialTexture(data, metallic.baseColorTexture),
      metallicRoughness: materialTexture(data, metallic.metallicRoughnessTexture),
      normal: materialTexture(data, material.normalTexture),
      occlusion: materialTexture(data, material.occlusionTexture),
      emissive: materialTexture(data, material.emissiveTexture),
    },
  };
}

async function decodePixels(buffer) {
  try {
    const image = new JpxImage();
    image.parse(new Uint8Array(buffer));
    const rgba = Buffer.alloc(image.width * image.height * 4, 255);
    for (const tile of image.tiles) for (let y = 0; y < tile.height; y++) for (let x = 0; x < tile.width; x++) {
      const source = (y * tile.width + x) * image.componentsCount;
      const target = ((tile.top + y) * image.width + tile.left + x) * 4;
      rgba[target] = tile.items[source];
      rgba[target + 1] = image.componentsCount > 1 ? tile.items[source + 1] : tile.items[source];
      rgba[target + 2] = image.componentsCount > 2 ? tile.items[source + 2] : tile.items[source];
      if (image.componentsCount > 3) rgba[target + 3] = tile.items[source + 3];
    }
    return { data: rgba, width: image.width, height: image.height, channels: 4 };
  } catch {
    const result = await sharp(buffer, { failOn: 'error' }).ensureAlpha().raw().toBuffer({ resolveWithObject: true });
    return { data: result.data, width: result.info.width, height: result.info.height, channels: result.info.channels };
  }
}

/** Vertices per side of the sculpt grid; the viewer's highest detail level is 32 x 32. */
const SCULPT_GRID = 32;

/**
 * Geometry from a sculpt map, following the official viewer (LLVolume::sculptGenerateMapVertices):
 * each texel's R, G, B is a point's X, Y, Z in -0.5..0.5, rows run from the bottom of the image
 * upward (the viewer keeps decoded images bottom-up) and columns run around the shape.
 * Types: 1 sphere, 2 torus, 3 plane, 4 cylinder. 0x40 inverts the shape (faces point inward) and
 * 0x80 mirrors it across X. The viewer reverses the sampling direction for exactly one of the two
 * flags, so a mirror alone stays outward facing and the winding below never changes.
 */
async function decodeSculpt(buffer, sculptType = 1) {
  const decoded = await decodePixels(buffer);
  const baseType = sculptType & 0x07, mirror = Boolean(sculptType & 0x80), invert = Boolean(sculptType & 0x40);
  const reverse = invert ? !mirror : mirror;
  const width = decoded.width, height = decoded.height, channels = decoded.channels;
  const columns = Math.max(2, Math.min(SCULPT_GRID, width)), rows = Math.max(2, Math.min(SCULPT_GRID, height));
  const vertices = [], texCoords = [], indices = [];
  for (let y = 0; y < rows; y++) {
    const imageRow = height - 1 - Math.round((y * (height - 1)) / (rows - 1));
    for (let x = 0; x < columns; x++) {
      const column = Math.round(((reverse ? columns - 1 - x : x) * (width - 1)) / (columns - 1));
      const p = (imageRow * width + column) * channels;
      vertices.push((mirror ? -1 : 1) * (decoded.data[p] / 255 - 0.5), decoded.data[p + 1] / 255 - 0.5, decoded.data[p + 2] / 255 - 0.5);
      texCoords.push(x / (columns - 1), y / (rows - 1));
    }
  }
  // Sphere (1), torus (2) and cylinder (4) sculpts close around U; only a torus also closes around V; a plane (3) is open.
  const wrapsX = baseType !== 3, wrapsY = baseType === 2;
  // The last column repeats the first when the map closes (e.g. 0 and 360 degrees), so only add faces between distinct columns.
  const seamX = wrapsX && samePosition(vertices, 0, columns - 1, columns, rows);
  const quadColumns = seamX ? columns - 1 : (wrapsX ? columns : columns - 1);
  const quadRows = wrapsY ? rows : rows - 1;
  for (let y = 0; y < quadRows; y++) for (let x = 0; x < quadColumns; x++) {
    const nx = (x + 1) % columns, ny = (y + 1) % rows;
    const a = y * columns + x, b = y * columns + nx, c = ny * columns + x, d = ny * columns + nx;
    indices.push(a, b, c, b, d, c);
  }
  const normals = computeNormals(vertices, indices);
  const average = (list) => {
    const sum = [0, 0, 0];
    for (const v of list) for (let k = 0; k < 3; k++) sum[k] += normals[v * 3 + k];
    const length = Math.hypot(...sum);
    if (length < 1e-9) return;
    for (const v of list) for (let k = 0; k < 3; k++) normals[v * 3 + k] = sum[k] / length;
  };
  // Seams and poles share positions but not vertices, so average normals across them.
  if (seamX) for (let y = 0; y < rows; y++) average([y * columns, y * columns + columns - 1]);
  if (wrapsY) for (let x = 0; x < columns; x++) average([x, (rows - 1) * columns + x]);
  if (baseType === 1) for (const row of [0, rows - 1]) {
    // A sphere's top and bottom rows collapse to one point each.
    const first = row * columns, spread = Math.max(...[0, 1, 2].map((k) => {
      let low = Infinity, high = -Infinity;
      for (let x = 0; x < columns; x++) { const v = vertices[(first + x) * 3 + k]; low = Math.min(low, v); high = Math.max(high, v); }
      return high - low;
    }));
    if (spread < 0.02) average(Array.from({ length: columns }, (_, x) => first + x));
  }
  return { vertices, normals, texCoords, indices };
}

function samePosition(vertices, firstColumn, lastColumn, columns, rows) {
  for (let y = 0; y < rows; y++) {
    const a = (y * columns + firstColumn) * 3, b = (y * columns + lastColumn) * 3;
    if (Math.hypot(vertices[a] - vertices[b], vertices[a + 1] - vertices[b + 1], vertices[a + 2] - vertices[b + 2]) > 0.02) return false;
  }
  return true;
}

async function decodeJPEG2000(buffer) {
  const decoded = await decodePixels(buffer);
  return { width: decoded.width, height: decoded.height, rgba: decoded.data.toString('base64') };
}

module.exports = { computeNormals, decodeLLMesh, normalizeLLMesh, decodeGLTFMaterial, normalizeGLTFMaterial, decodeSculpt, decodeJPEG2000 };
