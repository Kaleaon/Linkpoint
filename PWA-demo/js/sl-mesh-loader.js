/**
 * Linkpoint PWA - SL Mesh Loader
 * Based on Linkpoint MeshData.kt and MeshFace.kt
 */

class SLMeshLoader extends Utils.EventEmitter {
  constructor(graphics3d) {
    super();
    this.graphics = graphics3d;
    this.meshCache = new Map();
  }

  /**
   * Load mesh from LLSD data
   * Based on MeshData.kt loading logic
   */
  async loadMesh(meshId, llsdData) {
    if (this.meshCache.has(meshId)) {
      return this.meshCache.get(meshId);
    }

    try {
      // Parse LLSD mesh data
      const meshData = this.parseMeshLLSD(llsdData);
      
      // Select LOD (Level of Detail)
      const lod = this.selectLOD(meshData, 'medium'); // high, medium, low, lowest
      
      if (!lod) {
        throw new Error('No mesh LOD data found');
      }

      // Decompress mesh data if needed
      const decompressed = await this.decompressMeshData(lod);
      
      // Parse faces
      const faces = this.parseMeshFaces(decompressed);
      
      // Create WebGL meshes for each face
      const meshes = [];
      for (let i = 0; i < faces.length; i++) {
        const face = faces[i];
        const meshName = `${meshId}_face_${i}`;
        
        this.graphics.createMesh(
          meshName,
          face.vertices,
          face.indices,
          face.normals,
          face.texCoords,
          face.tangents
        );
        
        meshes.push(meshName);
      }

      const result = {
        id: meshId,
        faces: meshes,
        faceCount: faces.length,
        vertexCount: faces.reduce((sum, f) => sum + f.vertices.length / 3, 0),
        triangleCount: faces.reduce((sum, f) => sum + f.indices.length / 3, 0)
      };

      this.meshCache.set(meshId, result);
      this.emit('mesh_loaded', result);
      
      return result;

    } catch (error) {
      console.error('Mesh load error:', error);
      throw error;
    }
  }

  /**
   * Parse LLSD mesh data
   */
  parseMeshLLSD(llsdData) {
    // LLSD mesh format:
    // {
    //   'high_lod': { offset, size },
    //   'medium_lod': { offset, size },
    //   'low_lod': { offset, size },
    //   'lowest_lod': { offset, size },
    //   'physics_shape': { offset, size }
    // }
    return llsdData;
  }

  /**
   * Select appropriate LOD
   */
  selectLOD(meshData, preferredLOD = 'medium') {
    const lodPriority = ['high_lod', 'medium_lod', 'low_lod', 'lowest_lod'];
    const startIndex = lodPriority.indexOf(preferredLOD + '_lod');

    // Try preferred and fallbacks
    for (let i = startIndex >= 0 ? startIndex : 1; i < lodPriority.length; i++) {
      if (meshData[lodPriority[i]]) {
        return meshData[lodPriority[i]];
      }
    }

    // Try higher LODs if preferred not found
    for (let i = startIndex - 1; i >= 0; i--) {
      if (meshData[lodPriority[i]]) {
        return meshData[lodPriority[i]];
      }
    }

    return null;
  }

  /**
   * Decompress mesh data
   */
  async decompressMeshData(lodData) {
    // In browser, we'd need to fetch the actual data
    // For now, assume lodData contains the decompressed data
    return lodData;
  }

  /**
   * Parse mesh faces from decompressed data
   * Based on MeshFace.kt parsing logic
   */
  parseMeshFaces(data) {
    const faces = [];
    
    if (!data.faces || !Array.isArray(data.faces)) {
      console.warn('No faces in mesh data');
      return faces;
    }

    for (const faceData of data.faces) {
      try {
        const face = this.parseMeshFace(faceData);
        if (face) {
          faces.push(face);
        }
      } catch (error) {
        console.error('Failed to parse mesh face:', error);
      }
    }

    return faces;
  }

  /**
   * Parse individual mesh face
   */
  parseMeshFace(faceData) {
    // Check for NoGeometry flag
    if (faceData.NoGeometry || !faceData.Position || !faceData.TriangleList) {
      return null;
    }

    // Decode position data (compressed as u16)
    const positions = this.decodePositions(
      faceData.Position,
      faceData.PositionDomain || { Min: [-0.5, -0.5, -0.5], Max: [0.5, 0.5, 0.5] }
    );

    // Decode normals
    const normals = faceData.Normal 
      ? this.decodeNormals(faceData.Normal)
      : this.generateNormals(positions, faceData.TriangleList);

    // Decode texture coordinates
    const texCoords = faceData.TexCoord0
      ? this.decodeTexCoords(
          faceData.TexCoord0,
          faceData.TexCoord0Domain || { Min: [0, 0], Max: [1, 1] }
        )
      : this.generateDefaultTexCoords(positions.length / 3);

    // Decode triangle indices
    const indices = this.decodeIndices(faceData.TriangleList);

    // Calculate tangents for normal mapping
    const tangents = Primitives3D.calculateTangents(positions, normals, texCoords, indices);

    return {
      vertices: positions,
      normals,
      texCoords,
      indices,
      tangents
    };
  }

  /**
   * Decode compressed position data (u16 → float)
   */
  decodePositions(data, domain) {
    const positions = [];
    const min = domain.Min || [-0.5, -0.5, -0.5];
    const max = domain.Max || [0.5, 0.5, 0.5];
    const range = [max[0] - min[0], max[1] - min[1], max[2] - min[2]];

    // Data is array of u16 values
    for (let i = 0; i < data.length; i += 3) {
      const x = ((data[i] & 0xFFFF) / 65535.0) * range[0] + min[0];
      const y = ((data[i + 1] & 0xFFFF) / 65535.0) * range[1] + min[1];
      const z = ((data[i + 2] & 0xFFFF) / 65535.0) * range[2] + min[2];
      positions.push(x, y, z);
    }

    return positions;
  }

  /**
   * Decode compressed normal data (u16 → float, range -1 to 1)
   */
  decodeNormals(data) {
    const normals = [];

    for (let i = 0; i < data.length; i += 3) {
      const x = ((data[i] & 0xFFFF) / 65535.0) * 2.0 - 1.0;
      const y = ((data[i + 1] & 0xFFFF) / 65535.0) * 2.0 - 1.0;
      const z = ((data[i + 2] & 0xFFFF) / 65535.0) * 2.0 - 1.0;
      normals.push(x, y, z);
    }

    return normals;
  }

  /**
   * Decode texture coordinates
   */
  decodeTexCoords(data, domain) {
    const texCoords = [];
    const min = domain.Min || [0, 0];
    const max = domain.Max || [1, 1];
    const range = [max[0] - min[0], max[1] - min[1]];

    for (let i = 0; i < data.length; i += 2) {
      const u = ((data[i] & 0xFFFF) / 65535.0) * range[0] + min[0];
      const v = ((data[i + 1] & 0xFFFF) / 65535.0) * range[1] + min[1];
      texCoords.push(u, v);
    }

    return texCoords;
  }

  /**
   * Decode triangle indices
   */
  decodeIndices(data) {
    // Data is array of u16 indices
    return Array.from(data);
  }

  /**
   * Generate normals from geometry
   */
  generateNormals(positions, indices) {
    const normals = new Array(positions.length).fill(0);

    // Calculate face normals and accumulate
    for (let i = 0; i < indices.length; i += 3) {
      const i0 = indices[i] * 3;
      const i1 = indices[i + 1] * 3;
      const i2 = indices[i + 2] * 3;

      const v0 = [positions[i0], positions[i0 + 1], positions[i0 + 2]];
      const v1 = [positions[i1], positions[i1 + 1], positions[i1 + 2]];
      const v2 = [positions[i2], positions[i2 + 1], positions[i2 + 2]];

      const edge1 = [v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]];
      const edge2 = [v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]];

      const normal = [
        edge1[1] * edge2[2] - edge1[2] * edge2[1],
        edge1[2] * edge2[0] - edge1[0] * edge2[2],
        edge1[0] * edge2[1] - edge1[1] * edge2[0]
      ];

      normals[i0] += normal[0];
      normals[i0 + 1] += normal[1];
      normals[i0 + 2] += normal[2];
      normals[i1] += normal[0];
      normals[i1 + 1] += normal[1];
      normals[i1 + 2] += normal[2];
      normals[i2] += normal[0];
      normals[i2 + 1] += normal[1];
      normals[i2 + 2] += normal[2];
    }

    // Normalize
    for (let i = 0; i < normals.length; i += 3) {
      const len = Math.sqrt(normals[i] ** 2 + normals[i + 1] ** 2 + normals[i + 2] ** 2);
      if (len > 0) {
        normals[i] /= len;
        normals[i + 1] /= len;
        normals[i + 2] /= len;
      }
    }

    return normals;
  }

  /**
   * Generate default texture coordinates
   */
  generateDefaultTexCoords(vertexCount) {
    const texCoords = [];
    for (let i = 0; i < vertexCount; i++) {
      texCoords.push(0.5, 0.5); // Center UV
    }
    return texCoords;
  }

  /**
   * Clear cache
   */
  clearCache() {
    this.meshCache.clear();
    this.emit('cache_cleared');
  }

  /**
   * Get cache stats
   */
  getCacheStats() {
    return {
      meshCount: this.meshCache.size,
      meshes: Array.from(this.meshCache.keys())
    };
  }
}

// Make available globally
window.SLMeshLoader = SLMeshLoader;
