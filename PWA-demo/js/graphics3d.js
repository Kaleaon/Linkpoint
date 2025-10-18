/**
 * Linkpoint PWA - 3D Graphics Engine (WebGL)
 */

class Graphics3D extends Utils.EventEmitter {
  constructor(canvas) {
    super();
    this.canvas = canvas;
    this.gl = null;
    this.programs = new Map();
    this.meshes = new Map();
    this.textures = new Map();
    this.framebuffers = new Map();
    
    // Rendering state
    this.drawCalls = 0;
    this.triangles = 0;
    
    // Capabilities
    this.extensions = {};
    this.maxTextureSize = 0;
    this.maxVertexAttributes = 0;
  }

  /**
   * Initialize WebGL context
   */
  async init() {
    // Try WebGL2 first, fallback to WebGL1
    this.gl = this.canvas.getContext('webgl2', {
      alpha: false,
      depth: true,
      stencil: false,
      antialias: true,
      premultipliedAlpha: false,
      preserveDrawingBuffer: false,
      powerPreference: 'high-performance'
    });

    if (!this.gl) {
      this.gl = this.canvas.getContext('webgl', {
        alpha: false,
        depth: true,
        antialias: true,
        premultipliedAlpha: false
      });
    }

    if (!this.gl) {
      throw new Error('WebGL not supported');
    }

    console.log('WebGL version:', this.gl instanceof WebGL2RenderingContext ? '2.0' : '1.0');

    // Get capabilities
    this.maxTextureSize = this.gl.getParameter(this.gl.MAX_TEXTURE_SIZE);
    this.maxVertexAttributes = this.gl.getParameter(this.gl.MAX_VERTEX_ATTRIBS);

    // Get extensions
    this.extensions.anisotropic = this.gl.getExtension('EXT_texture_filter_anisotropic');
    this.extensions.depthTexture = this.gl.getExtension('WEBGL_depth_texture');
    this.extensions.floatTexture = this.gl.getExtension('OES_texture_float');
    this.extensions.vao = this.gl.getExtension('OES_vertex_array_object');

    // Initial GL state
    this.gl.enable(this.gl.DEPTH_TEST);
    this.gl.enable(this.gl.CULL_FACE);
    this.gl.cullFace(this.gl.BACK);
    this.gl.frontFace(this.gl.CCW);
    this.gl.depthFunc(this.gl.LEQUAL);
    // Lighter sky color for better visibility
    this.gl.clearColor(0.53, 0.81, 0.92, 1.0); // Light blue sky color

    // Create default shaders
    await this.createDefaultShaders();

    this.emit('initialized');
    return true;
  }

  /**
   * Create default shader programs
   */
  async createDefaultShaders() {
    // Basic shader
    this.createShaderProgram('basic', {
      vertex: `
        attribute vec3 aPosition;
        attribute vec3 aNormal;
        attribute vec2 aTexCoord;
        
        uniform mat4 uModelMatrix;
        uniform mat4 uViewMatrix;
        uniform mat4 uProjectionMatrix;
        uniform mat3 uNormalMatrix;
        
        varying vec3 vNormal;
        varying vec2 vTexCoord;
        varying vec3 vPosition;
        
        void main() {
          vec4 worldPos = uModelMatrix * vec4(aPosition, 1.0);
          vPosition = worldPos.xyz;
          vNormal = normalize(uNormalMatrix * aNormal);
          vTexCoord = aTexCoord;
          gl_Position = uProjectionMatrix * uViewMatrix * worldPos;
        }
      `,
      fragment: `
        precision mediump float;
        
        varying vec3 vNormal;
        varying vec2 vTexCoord;
        varying vec3 vPosition;
        
        uniform vec3 uLightPos;
        uniform vec3 uLightColor;
        uniform vec3 uAmbientColor;
        uniform vec4 uColor;
        uniform sampler2D uTexture;
        uniform bool uUseTexture;
        
        void main() {
          vec3 normal = normalize(vNormal);
          vec3 lightDir = normalize(uLightPos - vPosition);
          
          // Ambient
          vec3 ambient = uAmbientColor;
          
          // Diffuse
          float diff = max(dot(normal, lightDir), 0.0);
          vec3 diffuse = diff * uLightColor;
          
          // Final color
          vec4 baseColor = uUseTexture ? texture2D(uTexture, vTexCoord) : uColor;
          vec3 result = (ambient + diffuse) * baseColor.rgb;
          
          gl_FragColor = vec4(result, baseColor.a);
        }
      `
    });

    // PBR shader
    this.createShaderProgram('pbr', {
      vertex: `
        attribute vec3 aPosition;
        attribute vec3 aNormal;
        attribute vec2 aTexCoord;
        attribute vec3 aTangent;
        
        uniform mat4 uModelMatrix;
        uniform mat4 uViewMatrix;
        uniform mat4 uProjectionMatrix;
        uniform mat3 uNormalMatrix;
        
        varying vec3 vNormal;
        varying vec2 vTexCoord;
        varying vec3 vPosition;
        varying mat3 vTBN;
        
        void main() {
          vec4 worldPos = uModelMatrix * vec4(aPosition, 1.0);
          vPosition = worldPos.xyz;
          
          vec3 T = normalize(uNormalMatrix * aTangent);
          vec3 N = normalize(uNormalMatrix * aNormal);
          T = normalize(T - dot(T, N) * N);
          vec3 B = cross(N, T);
          vTBN = mat3(T, B, N);
          
          vNormal = N;
          vTexCoord = aTexCoord;
          gl_Position = uProjectionMatrix * uViewMatrix * worldPos;
        }
      `,
      fragment: `
        precision mediump float;
        
        varying vec3 vNormal;
        varying vec2 vTexCoord;
        varying vec3 vPosition;
        varying mat3 vTBN;
        
        uniform vec3 uCameraPos;
        uniform vec3 uLightPos;
        uniform vec3 uLightColor;
        
        uniform sampler2D uAlbedoMap;
        uniform sampler2D uNormalMap;
        uniform sampler2D uMetallicMap;
        uniform sampler2D uRoughnessMap;
        uniform sampler2D uAOMap;
        
        uniform vec3 uAlbedo;
        uniform float uMetallic;
        uniform float uRoughness;
        uniform float uAO;
        
        const float PI = 3.14159265359;
        
        float DistributionGGX(vec3 N, vec3 H, float roughness) {
          float a = roughness * roughness;
          float a2 = a * a;
          float NdotH = max(dot(N, H), 0.0);
          float NdotH2 = NdotH * NdotH;
          
          float num = a2;
          float denom = (NdotH2 * (a2 - 1.0) + 1.0);
          denom = PI * denom * denom;
          
          return num / denom;
        }
        
        float GeometrySchlickGGX(float NdotV, float roughness) {
          float r = (roughness + 1.0);
          float k = (r * r) / 8.0;
          
          float num = NdotV;
          float denom = NdotV * (1.0 - k) + k;
          
          return num / denom;
        }
        
        float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
          float NdotV = max(dot(N, V), 0.0);
          float NdotL = max(dot(N, L), 0.0);
          float ggx2 = GeometrySchlickGGX(NdotV, roughness);
          float ggx1 = GeometrySchlickGGX(NdotL, roughness);
          
          return ggx1 * ggx2;
        }
        
        vec3 fresnelSchlick(float cosTheta, vec3 F0) {
          return F0 + (1.0 - F0) * pow(1.0 - cosTheta, 5.0);
        }
        
        void main() {
          vec3 albedo = texture2D(uAlbedoMap, vTexCoord).rgb * uAlbedo;
          float metallic = texture2D(uMetallicMap, vTexCoord).r * uMetallic;
          float roughness = texture2D(uRoughnessMap, vTexCoord).r * uRoughness;
          float ao = texture2D(uAOMap, vTexCoord).r * uAO;
          
          vec3 N = normalize(vNormal);
          vec3 V = normalize(uCameraPos - vPosition);
          
          vec3 F0 = vec3(0.04);
          F0 = mix(F0, albedo, metallic);
          
          // Reflectance equation
          vec3 Lo = vec3(0.0);
          vec3 L = normalize(uLightPos - vPosition);
          vec3 H = normalize(V + L);
          float distance = length(uLightPos - vPosition);
          float attenuation = 1.0 / (distance * distance);
          vec3 radiance = uLightColor * attenuation;
          
          // Cook-Torrance BRDF
          float NDF = DistributionGGX(N, H, roughness);
          float G = GeometrySmith(N, V, L, roughness);
          vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);
          
          vec3 kS = F;
          vec3 kD = vec3(1.0) - kS;
          kD *= 1.0 - metallic;
          
          vec3 numerator = NDF * G * F;
          float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.001;
          vec3 specular = numerator / denominator;
          
          float NdotL = max(dot(N, L), 0.0);
          Lo += (kD * albedo / PI + specular) * radiance * NdotL;
          
          vec3 ambient = vec3(0.03) * albedo * ao;
          vec3 color = ambient + Lo;
          
          // HDR tonemapping
          color = color / (color + vec3(1.0));
          // Gamma correction
          color = pow(color, vec3(1.0/2.2));
          
          gl_FragColor = vec4(color, 1.0);
        }
      `
    });

    // Skybox shader
    this.createShaderProgram('skybox', {
      vertex: `
        attribute vec3 aPosition;
        
        uniform mat4 uViewMatrix;
        uniform mat4 uProjectionMatrix;
        
        varying vec3 vTexCoord;
        
        void main() {
          vTexCoord = aPosition;
          vec4 pos = uProjectionMatrix * mat4(mat3(uViewMatrix)) * vec4(aPosition, 1.0);
          gl_Position = pos.xyww;
        }
      `,
      fragment: `
        precision mediump float;
        
        varying vec3 vTexCoord;
        uniform samplerCube uSkybox;
        
        void main() {
          gl_FragColor = textureCube(uSkybox, vTexCoord);
        }
      `
    });
  }

  /**
   * Create shader program
   */
  createShaderProgram(name, source) {
    const gl = this.gl;

    const vertexShader = this.compileShader(gl.VERTEX_SHADER, source.vertex);
    const fragmentShader = this.compileShader(gl.FRAGMENT_SHADER, source.fragment);

    const program = gl.createProgram();
    gl.attachShader(program, vertexShader);
    gl.attachShader(program, fragmentShader);
    gl.linkProgram(program);

    if (!gl.getProgramParameter(program, gl.LINK_STATUS)) {
      console.error('Shader program link error:', gl.getProgramInfoLog(program));
      return null;
    }

    // Get attribute and uniform locations
    const numAttributes = gl.getProgramParameter(program, gl.ACTIVE_ATTRIBUTES);
    const attributes = {};
    for (let i = 0; i < numAttributes; i++) {
      const info = gl.getActiveAttrib(program, i);
      attributes[info.name] = gl.getAttribLocation(program, info.name);
    }

    const numUniforms = gl.getProgramParameter(program, gl.ACTIVE_UNIFORMS);
    const uniforms = {};
    for (let i = 0; i < numUniforms; i++) {
      const info = gl.getActiveUniform(program, i);
      uniforms[info.name] = gl.getUniformLocation(program, info.name);
    }

    this.programs.set(name, { program, attributes, uniforms });
    return program;
  }

  /**
   * Compile shader
   */
  compileShader(type, source) {
    const gl = this.gl;
    const shader = gl.createShader(type);
    gl.shaderSource(shader, source);
    gl.compileShader(shader);

    if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
      console.error('Shader compile error:', gl.getShaderInfoLog(shader));
      gl.deleteShader(shader);
      return null;
    }

    return shader;
  }

  /**
   * Create mesh
   */
  createMesh(name, vertices, indices, normals, texCoords, tangents) {
    const gl = this.gl;

    const mesh = {
      vao: null,
      buffers: {},
      indexCount: indices.length,
      vertexCount: vertices.length / 3
    };

    // Create VAO if supported
    if (this.extensions.vao) {
      mesh.vao = this.extensions.vao.createVertexArrayOES();
      this.extensions.vao.bindVertexArrayOES(mesh.vao);
    }

    // Vertex buffer
    mesh.buffers.position = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, mesh.buffers.position);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(vertices), gl.STATIC_DRAW);

    // Normal buffer
    if (normals) {
      mesh.buffers.normal = gl.createBuffer();
      gl.bindBuffer(gl.ARRAY_BUFFER, mesh.buffers.normal);
      gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(normals), gl.STATIC_DRAW);
    }

    // TexCoord buffer
    if (texCoords) {
      mesh.buffers.texCoord = gl.createBuffer();
      gl.bindBuffer(gl.ARRAY_BUFFER, mesh.buffers.texCoord);
      gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(texCoords), gl.STATIC_DRAW);
    }

    // Tangent buffer
    if (tangents) {
      mesh.buffers.tangent = gl.createBuffer();
      gl.bindBuffer(gl.ARRAY_BUFFER, mesh.buffers.tangent);
      gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(tangents), gl.STATIC_DRAW);
    }

    // Index buffer
    mesh.buffers.index = gl.createBuffer();
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, mesh.buffers.index);
    gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(indices), gl.STATIC_DRAW);

    if (this.extensions.vao) {
      this.extensions.vao.bindVertexArrayOES(null);
    }

    this.meshes.set(name, mesh);
    return mesh;
  }

  /**
   * Draw mesh
   */
  drawMesh(meshName, programName, uniforms) {
    const gl = this.gl;
    const mesh = this.meshes.get(meshName);
    const programInfo = this.programs.get(programName);

    if (!mesh || !programInfo) return;

    gl.useProgram(programInfo.program);

    // Bind VAO or manually bind attributes
    if (mesh.vao && this.extensions.vao) {
      this.extensions.vao.bindVertexArrayOES(mesh.vao);
    } else {
      this.bindMeshAttributes(mesh, programInfo.attributes);
    }

    // Set uniforms
    this.setUniforms(programInfo.uniforms, uniforms);

    // Draw
    gl.drawElements(gl.TRIANGLES, mesh.indexCount, gl.UNSIGNED_SHORT, 0);

    this.drawCalls++;
    this.triangles += mesh.indexCount / 3;
  }

  /**
   * Bind mesh attributes
   */
  bindMeshAttributes(mesh, attributes) {
    const gl = this.gl;

    if (attributes.aPosition !== undefined && mesh.buffers.position) {
      gl.bindBuffer(gl.ARRAY_BUFFER, mesh.buffers.position);
      gl.enableVertexAttribArray(attributes.aPosition);
      gl.vertexAttribPointer(attributes.aPosition, 3, gl.FLOAT, false, 0, 0);
    }

    if (attributes.aNormal !== undefined && mesh.buffers.normal) {
      gl.bindBuffer(gl.ARRAY_BUFFER, mesh.buffers.normal);
      gl.enableVertexAttribArray(attributes.aNormal);
      gl.vertexAttribPointer(attributes.aNormal, 3, gl.FLOAT, false, 0, 0);
    }

    if (attributes.aTexCoord !== undefined && mesh.buffers.texCoord) {
      gl.bindBuffer(gl.ARRAY_BUFFER, mesh.buffers.texCoord);
      gl.enableVertexAttribArray(attributes.aTexCoord);
      gl.vertexAttribPointer(attributes.aTexCoord, 2, gl.FLOAT, false, 0, 0);
    }

    if (attributes.aTangent !== undefined && mesh.buffers.tangent) {
      gl.bindBuffer(gl.ARRAY_BUFFER, mesh.buffers.tangent);
      gl.enableVertexAttribArray(attributes.aTangent);
      gl.vertexAttribPointer(attributes.aTangent, 3, gl.FLOAT, false, 0, 0);
    }

    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, mesh.buffers.index);
  }

  /**
   * Set uniforms
   */
  setUniforms(uniformLocations, values) {
    const gl = this.gl;

    for (const [name, location] of Object.entries(uniformLocations)) {
      if (location === null || values[name] === undefined) continue;

      const value = values[name];

      if (value.length === 16) {
        gl.uniformMatrix4fv(location, false, value);
      } else if (value.length === 9) {
        gl.uniformMatrix3fv(location, false, value);
      } else if (value.length === 4) {
        gl.uniform4fv(location, value);
      } else if (value.length === 3) {
        gl.uniform3fv(location, value);
      } else if (value.length === 2) {
        gl.uniform2fv(location, value);
      } else if (typeof value === 'number') {
        gl.uniform1f(location, value);
      } else if (typeof value === 'boolean') {
        gl.uniform1i(location, value ? 1 : 0);
      }
    }
  }

  /**
   * Clear buffers
   */
  clear(color = [0.05, 0.05, 0.1, 1.0]) {
    const gl = this.gl;
    gl.clearColor(...color);
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
    
    this.drawCalls = 0;
    this.triangles = 0;
  }

  /**
   * Resize viewport
   */
  resize(width, height) {
    this.canvas.width = width;
    this.canvas.height = height;
    this.gl.viewport(0, 0, width, height);
  }

  /**
   * Get rendering stats
   */
  getStats() {
    return {
      drawCalls: this.drawCalls,
      triangles: this.triangles,
      meshes: this.meshes.size,
      textures: this.textures.size,
      programs: this.programs.size
    };
  }

  /**
   * Cleanup
   */
  destroy() {
    // Delete all meshes
    this.meshes.forEach(mesh => {
      Object.values(mesh.buffers).forEach(buffer => {
        this.gl.deleteBuffer(buffer);
      });
      if (mesh.vao && this.extensions.vao) {
        this.extensions.vao.deleteVertexArrayOES(mesh.vao);
      }
    });

    // Delete all programs
    this.programs.forEach(({ program }) => {
      this.gl.deleteProgram(program);
    });

    this.meshes.clear();
    this.programs.clear();
  }
}

// Make available globally
window.Graphics3D = Graphics3D;
