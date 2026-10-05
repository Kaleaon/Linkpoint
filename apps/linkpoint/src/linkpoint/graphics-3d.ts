/**
 * Linkpoint PWA - 3D Graphics Engine (WebGL)
 */

import { Utils } from './utils';
import { clampJointIndices, maxSkinJoints, skinnedVertexShader } from './skinning';
import { TERRAIN_FRAGMENT_SHADER, TERRAIN_VERTEX_SHADER } from './terrain';
import {
  SKY_VERTEX_SHADER, SKY_FRAGMENT_SHADER, STARS_VERTEX_SHADER, STARS_FRAGMENT_SHADER,
  WATER_VERTEX_SHADER, WATER_FRAGMENT_SHADER,
} from './sky';

/** Attribute slots shared by every program so a mesh's VAO is valid for any of them. */
const ATTRIBUTE_SLOTS: Record<string, number> = { aPosition: 0, aNormal: 1, aTexCoord: 2, aTangent: 3, aJoints: 4, aWeights: 5 };

export interface DrawOptions {
  mode?: 'triangles' | 'points';
  /** Defaults to true. Ignored (forced off) for blended alpha mode 2. */
  depthWrite?: boolean;
  blend?: boolean;
  cullFace?: boolean;
}

const BASIC_FRAGMENT_SHADER = `
        #ifdef GL_FRAGMENT_PRECISION_HIGH
        precision highp float;
        #else
        precision mediump float;
        #endif
        
        varying vec3 vNormal;
        varying vec2 vTexCoord;
        varying vec3 vPosition;
        
        uniform vec3 uLightPos;
        uniform vec3 uLightColor;
        uniform vec3 uAmbientColor;
        uniform vec4 uColor;
        uniform sampler2D uTexture;
        uniform bool uUseTexture;
        uniform vec4 uTexTransform;
        uniform float uTexRotation;
        uniform bool uFullBright;
        uniform vec3 uCameraPos;
        uniform float uMetallic;
        uniform float uRoughness;
        uniform vec3 uEmissive;
        uniform sampler2D uMetallicRoughnessTexture;
        uniform sampler2D uNormalTexture;
        uniform sampler2D uEmissiveTexture;
        uniform bool uUseMetallicRoughnessTexture;
        uniform bool uUseNormalTexture;
        uniform bool uUseEmissiveTexture;
        uniform float uAlphaCutoff;
        uniform int uAlphaMode;
        
        void main() {
          // SL applies repeats first, then rotates around the texture centre,
          // then applies the face offset. Rotating before repeat distorted
          // non-square/repeated textures and made textured meshes look squeezed.
          vec2 centered = vTexCoord * uTexTransform.xy - vec2(0.5);
          float texSin = sin(uTexRotation);
          float texCos = cos(uTexRotation);
          vec2 rotated = mat2(texCos, -texSin, texSin, texCos) * centered + vec2(0.5);
          vec2 transformedUV = rotated + uTexTransform.zw;

          vec3 normal = normalize(vNormal);
          if (uUseNormalTexture) normal = normalize(normal + (texture2D(uNormalTexture, transformedUV).xyz * 2.0 - 1.0));
          vec3 lightDir = normalize(uLightPos - vPosition);
          
          // Ambient
          vec3 ambient = uAmbientColor;
          
          // Diffuse
          float diff = max(dot(normal, lightDir), 0.0);
          vec3 diffuse = diff * uLightColor;
          
          // Final color
          vec4 baseColor = uUseTexture ? texture2D(uTexture, transformedUV) * uColor : uColor;
          if (uAlphaMode == 1 && baseColor.a < uAlphaCutoff) discard;
          vec3 orm = uUseMetallicRoughnessTexture ? texture2D(uMetallicRoughnessTexture, transformedUV).rgb : vec3(1.0);
          float metallic = clamp(uMetallic * orm.b, 0.0, 1.0);
          float roughness = clamp(uRoughness * orm.g, 0.04, 1.0);
          vec3 viewDir = normalize(uCameraPos - vPosition);
          vec3 halfDir = normalize(lightDir + viewDir);
          float specPower = mix(128.0, 2.0, roughness);
          float specular = pow(max(dot(normal, halfDir), 0.0), specPower);
          vec3 f0 = mix(vec3(0.04), baseColor.rgb, metallic);
          // The combined light is clamped to 1 (as the viewer does), then gamma-encoded: the viewer
          // lights in linear space and converts to sRGB at the end, which for sRGB surface colours
          // is the same as scaling them by light^(1/2.2).
          vec3 diffusePbr = baseColor.rgb * (1.0 - metallic) * pow(min(ambient + diffuse, vec3(1.0)), vec3(1.0 / 2.2));
          vec3 emission = uEmissive * (uUseEmissiveTexture ? texture2D(uEmissiveTexture, transformedUV).rgb : vec3(1.0));
          vec3 result = uFullBright ? baseColor.rgb : diffusePbr + f0 * specular + emission;
          
          gl_FragColor = vec4(result, baseColor.a);
        }
`;

export class Graphics3D extends Utils.EventEmitter {
  public canvas: HTMLCanvasElement;
  public gl: WebGLRenderingContext | WebGL2RenderingContext | null = null;
  private programs: Map<string, any> = new Map();
  /** Joint matrices the skinned program can hold (limited by the GPU's vertex uniform vectors). */
  maxJoints = 0;
  private meshes: Map<string, any> = new Map();
  private textures: Map<string, any> = new Map();
  /** Whether each texture has any transparent pixels, so it can be drawn blended. */
  private textureAlpha: Map<string, boolean> = new Map();
  private renderTargets: Map<string, { framebuffer: WebGLFramebuffer; depth: WebGLRenderbuffer; width: number; height: number }> = new Map();
  private clearColor: [number, number, number, number] = [0.53, 0.81, 0.92, 1];
  
  // Rendering state
  public drawCalls: number = 0;
  public triangles: number = 0;
  
  // Capabilities
  public extensions: any = {};
  public maxTextureSize: number = 0;
  public maxVertexAttributes: number = 0;

  constructor(canvas: HTMLCanvasElement) {
    super();
    this.canvas = canvas;
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
    }) as WebGL2RenderingContext;

    if (!this.gl) {
      this.gl = this.canvas.getContext('webgl', {
        alpha: false,
        depth: true,
        antialias: true,
        premultipliedAlpha: false
      }) as WebGLRenderingContext;
    }

    if (!this.gl) {
      throw new Error('WebGL not supported');
    }

    const gl = this.gl;
    console.log('WebGL version:', typeof WebGL2RenderingContext !== 'undefined' && gl instanceof WebGL2RenderingContext ? '2.0' : '1.0');

    // Get capabilities
    this.maxTextureSize = gl.getParameter(gl.MAX_TEXTURE_SIZE);
    this.maxVertexAttributes = gl.getParameter(gl.MAX_VERTEX_ATTRIBS);

    // Get extensions
    this.extensions.anisotropic = gl.getExtension('EXT_texture_filter_anisotropic');
    this.extensions.depthTexture = gl.getExtension('WEBGL_depth_texture');
    this.extensions.floatTexture = gl.getExtension('OES_texture_float');
    this.extensions.vao = gl.getExtension('OES_vertex_array_object');
    this.extensions.uintIndices = typeof WebGL2RenderingContext !== 'undefined' && gl instanceof WebGL2RenderingContext
      ? true
      : gl.getExtension('OES_element_index_uint');

    // Initial GL state
    gl.enable(gl.DEPTH_TEST);
    gl.enable(gl.CULL_FACE);
    gl.cullFace(gl.BACK);
    gl.frontFace(gl.CCW);
    gl.depthFunc(gl.LEQUAL);
    // Lighter sky color for better visibility
    gl.clearColor(...this.clearColor);

    // Create default shaders
    await this.createDefaultShaders();
    // The view can be torn down while this awaits; destroy() releases the context.
    if (this.gl !== gl) throw new Error('Graphics3D was destroyed while initializing');

    // Always bind deterministic fallback textures. Without these, a material
    // whose asset is still streaming can accidentally sample the texture left
    // behind by the previous draw call.
    this.createTexture('__white', 1, 1, new Uint8Array([255, 255, 255, 255]));
    this.createTexture('__normal', 1, 1, new Uint8Array([128, 128, 255, 255]));

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
      fragment: BASIC_FRAGMENT_SHADER
    });

    // Skinned (rigged mesh / avatar) variant: same lighting, joint-driven vertices.
    this.maxJoints = maxSkinJoints(Number(this.gl!.getParameter(this.gl!.MAX_VERTEX_UNIFORM_VECTORS)) || 128);
    this.createShaderProgram('skinned', { vertex: skinnedVertexShader(this.maxJoints), fragment: BASIC_FRAGMENT_SHADER });
    this.createShaderProgram('terrain', { vertex: TERRAIN_VERTEX_SHADER, fragment: TERRAIN_FRAGMENT_SHADER });

    // Environment programs (see sky.ts).
    this.createShaderProgram('sky', { vertex: SKY_VERTEX_SHADER, fragment: SKY_FRAGMENT_SHADER });
    this.createShaderProgram('stars', { vertex: STARS_VERTEX_SHADER, fragment: STARS_FRAGMENT_SHADER });
    this.createShaderProgram('water', { vertex: WATER_VERTEX_SHADER, fragment: WATER_FRAGMENT_SHADER });
  }

  /**
   * Create shader program
   */
  createShaderProgram(name: string, source: { vertex: string, fragment: string }) {
    const gl = this.gl!;

    const vertexShader = this.compileShader(gl.VERTEX_SHADER, source.vertex);
    const fragmentShader = this.compileShader(gl.FRAGMENT_SHADER, source.fragment);

    if (!vertexShader || !fragmentShader) return null;

    const program = gl.createProgram()!;
    gl.attachShader(program, vertexShader);
    gl.attachShader(program, fragmentShader);
    // Pin attribute slots before linking. Otherwise each program may be given a
    // different slot for the same name, and a mesh VAO captured against the
    // 'basic' program would feed the wrong data to every other program.
    for (const [name, slot] of Object.entries(ATTRIBUTE_SLOTS)) gl.bindAttribLocation(program, slot, name);
    gl.linkProgram(program);

    if (!gl.getProgramParameter(program, gl.LINK_STATUS)) {
      console.error('Shader program link error:', gl.getProgramInfoLog(program));
      return null;
    }

    // Get attribute and uniform locations
    const numAttributes = gl.getProgramParameter(program, gl.ACTIVE_ATTRIBUTES);
    const attributes: Record<string, number> = {};
    for (let i = 0; i < numAttributes; i++) {
      const info = gl.getActiveAttrib(program, i)!;
      attributes[info.name] = gl.getAttribLocation(program, info.name);
    }

    const numUniforms = gl.getProgramParameter(program, gl.ACTIVE_UNIFORMS);
    const uniforms: Record<string, WebGLUniformLocation | null> = {};
    const arrayUniforms: Record<string, { type: number; size: number }> = {};
    for (let i = 0; i < numUniforms; i++) {
      const info = gl.getActiveUniform(program, i)!;
      uniforms[info.name] = gl.getUniformLocation(program, info.name);
      // Arrays are reported as "name[0]"; expose them under the bare name too.
      if (info.size > 1 && info.name.endsWith('[0]')) {
        const bare = info.name.slice(0, -3);
        uniforms[bare] = uniforms[info.name];
        arrayUniforms[bare] = { type: info.type, size: info.size };
      }
    }

    this.programs.set(name, { program, attributes, uniforms, arrayUniforms });
    return program;
  }

  /**
   * Compile shader
   */
  private compileShader(type: number, source: string) {
    const gl = this.gl!;
    const shader = gl.createShader(type)!;
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
  createMesh(name: string, vertices: number[], indices: number[], normals?: number[], texCoords?: number[], tangents?: number[], skin?: { joints: number[]; weights: number[] }) {
    const gl = this.gl!;
    const previous = this.meshes.get(name);
    if (previous) this.deleteMesh(previous);
    let maxIndex = 0;
    for (const index of indices) {
      if (!Number.isInteger(index) || index < 0 || index >= vertices.length / 3) {
        throw new Error(`Mesh ${name} contains an invalid vertex index`);
      }
      if (index > maxIndex) maxIndex = index;
    }
    if (maxIndex > 65535 && !this.extensions.uintIndices) {
      throw new Error(`Mesh ${name} needs 32-bit indices, which this WebGL context does not support`);
    }
    const indexType = maxIndex > 65535 ? gl.UNSIGNED_INT : gl.UNSIGNED_SHORT;

    // Local-space bounds drive frustum culling and picking.
    const min = [Infinity, Infinity, Infinity];
    const max = [-Infinity, -Infinity, -Infinity];
    for (let i = 0; i + 2 < vertices.length; i += 3) {
      for (let axis = 0; axis < 3; axis++) {
        const value = vertices[i + axis];
        if (value < min[axis]) min[axis] = value;
        if (value > max[axis]) max[axis] = value;
      }
    }
    const bounds = vertices.length >= 3 && min.every(Number.isFinite) && max.every(Number.isFinite) ? { min, max } : null;

    const mesh: any = {
      vao: null,
      buffers: {},
      indexCount: indices.length,
      indexType,
      vertexCount: vertices.length / 3,
      bounds
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

    // Skin influences: four joint indices and weights per vertex
    if (skin && skin.joints.length === (vertices.length / 3) * 4 && skin.weights.length === skin.joints.length) {
      mesh.buffers.joints = gl.createBuffer();
      gl.bindBuffer(gl.ARRAY_BUFFER, mesh.buffers.joints);
      gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(clampJointIndices(skin.joints, Math.max(this.maxJoints, 1))), gl.STATIC_DRAW);
      mesh.buffers.weights = gl.createBuffer();
      gl.bindBuffer(gl.ARRAY_BUFFER, mesh.buffers.weights);
      gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(skin.weights), gl.STATIC_DRAW);
      mesh.skinned = true;
    }

    // Index buffer
    mesh.buffers.index = gl.createBuffer();
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, mesh.buffers.index);
    gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, indexType === gl.UNSIGNED_INT ? new Uint32Array(indices) : new Uint16Array(indices), gl.STATIC_DRAW);

    if (this.extensions.vao) {
      // OES VAOs capture attribute and element-buffer bindings. Previously the
      // VAO was created but never populated, so WebGL 1 implementations with
      // OES_vertex_array_object drew empty meshes.
      // Slots are pinned identically in every program, so record against the fixed
      // slot map: this also captures joint/weight attributes that 'basic' never declares.
      this.bindMeshAttributes(mesh, ATTRIBUTE_SLOTS);
      this.extensions.vao.bindVertexArrayOES(null);
    }

    this.meshes.set(name, mesh);
    this.meshes.set(name.toLowerCase(), mesh);
    return mesh;
  }

  /** True when the mesh carries joint indices and weights. */
  isSkinnedMesh(name: string): boolean {
    return Boolean((this.meshes.get(name) || this.meshes.get(name.toLowerCase()))?.skinned);
  }

  /** Local-space bounding box of a mesh, or null if unknown. */
  getMeshBounds(name: string): { min: number[]; max: number[] } | null {
    return (this.meshes.get(name) || this.meshes.get(name.toLowerCase()))?.bounds ?? null;
  }

  /**
   * Draw mesh
   */
  drawMesh(meshName: string, programName: string, uniforms: any, options: DrawOptions = {}) {
    const gl = this.gl!;
    const mesh = this.meshes.get(meshName) || this.meshes.get(meshName.toLowerCase());
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
    // Sampler uniforms the program declares, bound to consecutive texture units.
    const bindings = [
      ['uTextureName', 'uTexture'], ['uMetallicRoughnessTextureName', 'uMetallicRoughnessTexture'],
      ['uNormalTextureName', 'uNormalTexture'], ['uEmissiveTextureName', 'uEmissiveTexture'],
      ['uCompositionName', 'uComposition'], ['uDetail0Name', 'uDetail0'], ['uDetail1Name', 'uDetail1'],
      ['uDetail2Name', 'uDetail2'], ['uDetail3Name', 'uDetail3'],
    ];
    let unit = 0;
    for (const [valueName, uniformName] of bindings) {
      const sampler = programInfo.uniforms[uniformName];
      if (!sampler) continue;
      const fallback = uniformName === 'uNormalTexture' ? '__normal' : '__white';
      const val = uniforms[valueName];
      const texture = this.textures.get(val) || (val && this.textures.get(String(val).toLowerCase())) || this.textures.get(fallback);
      if (!texture) continue;
      gl.activeTexture(gl.TEXTURE0 + unit);
      gl.bindTexture(gl.TEXTURE_2D, texture);
      gl.uniform1i(sampler, unit);
      unit++;
    }
    this.setUniforms(programInfo.uniforms, uniforms, programInfo.arrayUniforms);

    const blend = uniforms.uAlphaMode === 2 || options.blend === true;
    const noDepthWrite = uniforms.uAlphaMode === 2 || options.depthWrite === false;
    const doubleSided = Boolean(uniforms.uDoubleSided) || options.cullFace === false;
    const points = options.mode === 'points';
    if (blend) {
      gl.enable(gl.BLEND);
      gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);
    }
    if (noDepthWrite) gl.depthMask(false);
    if (doubleSided) gl.disable(gl.CULL_FACE);

    // Draw
    gl.drawElements(points ? gl.POINTS : gl.TRIANGLES, mesh.indexCount, mesh.indexType, 0);
    if (noDepthWrite) gl.depthMask(true);
    if (blend) gl.disable(gl.BLEND);
    if (doubleSided) gl.enable(gl.CULL_FACE);

    this.drawCalls++;
    if (!points) this.triangles += mesh.indexCount / 3;
  }

  createTexture(name: string, width: number, height: number, rgba: Uint8Array) {
    const gl = this.gl!;
    const existing = this.textures.get(name) || this.textures.get(name.toLowerCase());
    if (existing) gl.deleteTexture(existing);
    const texture = gl.createTexture();
    gl.bindTexture(gl.TEXTURE_2D, texture);
    gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, 1);
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, width, height, 0, gl.RGBA, gl.UNSIGNED_BYTE, rgba);
    const powerOfTwo = (value: number) => value > 0 && (value & (value - 1)) === 0;
    const webgl2 = typeof WebGL2RenderingContext !== 'undefined' && gl instanceof WebGL2RenderingContext;
    // WebGL 2 permits repeat wrapping and mipmaps for NPOT textures. The old
    // WebGL-1-only check clamped many SL textures, so repeats sampled a stretched
    // edge instead of the actual surface image.
    const canMipmap = webgl2 || (powerOfTwo(width) && powerOfTwo(height));
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, canMipmap ? gl.LINEAR_MIPMAP_LINEAR : gl.LINEAR);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, canMipmap ? gl.REPEAT : gl.CLAMP_TO_EDGE);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, canMipmap ? gl.REPEAT : gl.CLAMP_TO_EDGE);
    if (canMipmap) gl.generateMipmap(gl.TEXTURE_2D);
    const anisotropic = this.extensions.anisotropic;
    if (anisotropic) {
      const maximum = gl.getParameter(anisotropic.MAX_TEXTURE_MAX_ANISOTROPY_EXT) || 1;
      gl.texParameterf(gl.TEXTURE_2D, anisotropic.TEXTURE_MAX_ANISOTROPY_EXT, Math.min(8, maximum));
    }
    this.textures.set(name, texture);
    this.textures.set(name.toLowerCase(), texture);
    let hasAlpha = false;
    for (let i = 3; i < rgba.length; i += 4) { if (rgba[i] < 250) { hasAlpha = true; break; } }
    this.textureAlpha.set(name, hasAlpha);
    this.textureAlpha.set(name.toLowerCase(), hasAlpha);
    return name;
  }

  hasTexture(name: string | undefined | null): boolean {
    return Boolean(name && (this.textures.has(name) || this.textures.has(String(name).toLowerCase())));
  }

  /** True when the named texture has transparent pixels. Unknown textures are opaque. */
  textureHasAlpha(name: string | undefined | null): boolean {
    return Boolean(name && (this.textureAlpha.get(name) || this.textureAlpha.get(String(name).toLowerCase())));
  }

  /** Clear only the depth buffer, so a later pass (the HUD) draws over everything already rendered. */
  clearDepth() {
    const gl = this.gl;
    if (!gl) return;
    gl.depthMask(true);
    gl.clear(gl.DEPTH_BUFFER_BIT);
  }

  createRenderTarget(name: string, width = 256, height = 256) {
    if (this.renderTargets.has(name)) return name;
    const gl = this.gl!;
    const texture = gl.createTexture()!;
    gl.bindTexture(gl.TEXTURE_2D, texture);
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, width, height, 0, gl.RGBA, gl.UNSIGNED_BYTE, null);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
    const depth = gl.createRenderbuffer()!;
    gl.bindRenderbuffer(gl.RENDERBUFFER, depth);
    gl.renderbufferStorage(gl.RENDERBUFFER, gl.DEPTH_COMPONENT16, width, height);
    const framebuffer = gl.createFramebuffer()!;
    gl.bindFramebuffer(gl.FRAMEBUFFER, framebuffer);
    gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.TEXTURE_2D, texture, 0);
    gl.framebufferRenderbuffer(gl.FRAMEBUFFER, gl.DEPTH_ATTACHMENT, gl.RENDERBUFFER, depth);
    if (gl.checkFramebufferStatus(gl.FRAMEBUFFER) !== gl.FRAMEBUFFER_COMPLETE) throw new Error('Unable to create reflection render target');
    gl.bindFramebuffer(gl.FRAMEBUFFER, null);
    this.textures.set(name, texture);
    this.renderTargets.set(name, { framebuffer, depth, width, height });
    return name;
  }

  beginRenderTarget(name: string) {
    const target = this.renderTargets.get(name);
    if (!target) return false;
    const gl = this.gl!;
    gl.bindFramebuffer(gl.FRAMEBUFFER, target.framebuffer);
    gl.viewport(0, 0, target.width, target.height);
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
    return true;
  }

  endRenderTarget() {
    const gl = this.gl!;
    gl.bindFramebuffer(gl.FRAMEBUFFER, null);
    gl.viewport(0, 0, this.canvas.width, this.canvas.height);
  }

  /**
   * Bind mesh attributes
   */
  private bindMeshAttributes(mesh: any, attributes: any) {
    const gl = this.gl!;

    // Attributes the mesh does not supply must be switched off. A previous
    // mesh's enabled array would otherwise be read with this mesh's draw count,
    // which WebGL rejects (or renders as garbage) when the buffer is smaller.
    const bind = (location: number | undefined, buffer: WebGLBuffer | undefined, size: number) => {
      if (location === undefined || location < 0) return;
      if (!buffer) { gl.disableVertexAttribArray(location); return; }
      gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
      gl.enableVertexAttribArray(location);
      gl.vertexAttribPointer(location, size, gl.FLOAT, false, 0, 0);
    };
    bind(attributes.aPosition, mesh.buffers.position, 3);
    bind(attributes.aNormal, mesh.buffers.normal, 3);
    bind(attributes.aTexCoord, mesh.buffers.texCoord, 2);
    bind(attributes.aTangent, mesh.buffers.tangent, 3);
    bind(attributes.aJoints, mesh.buffers.joints, 4);
    bind(attributes.aWeights, mesh.buffers.weights, 4);

    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, mesh.buffers.index);
  }

  /**
   * Set uniforms
   */
  private setUniforms(uniformLocations: any, values: any, arrayUniforms: Record<string, { type: number; size: number }> = {}) {
    const gl = this.gl!;

    for (const [name, location] of Object.entries(uniformLocations)) {
      if (location === null || values[name] === undefined) continue;

      const value = values[name];

      // Array uniforms are typed by the program, not guessed from the JS length
      // (a float[4] would otherwise be uploaded as a vec4).
      const array = arrayUniforms[name];
      if (array) {
        const flat = value as ArrayLike<number>;
        const upload = array.type === gl.FLOAT ? gl.uniform1fv
          : array.type === gl.FLOAT_VEC2 ? gl.uniform2fv
          : array.type === gl.FLOAT_VEC3 ? gl.uniform3fv
          : array.type === gl.FLOAT_VEC4 ? gl.uniform4fv : null;
        if (upload && flat.length > 0) upload.call(gl, location as WebGLUniformLocation, flat as Float32List);
        continue;
      }

      if (value instanceof Float32Array || Array.isArray(value)) {
        if (value.length === 16) {
          gl.uniformMatrix4fv(location as WebGLUniformLocation, false, value);
        } else if (value.length === 9) {
          gl.uniformMatrix3fv(location as WebGLUniformLocation, false, value);
        } else if (value.length === 4) {
          gl.uniform4fv(location as WebGLUniformLocation, value);
        } else if (value.length === 3) {
          gl.uniform3fv(location as WebGLUniformLocation, value);
        } else if (value.length === 2) {
          gl.uniform2fv(location as WebGLUniformLocation, value);
        }
      } else if (typeof value === 'number') {
        if (name === 'uAlphaMode') gl.uniform1i(location as WebGLUniformLocation, value);
        else gl.uniform1f(location as WebGLUniformLocation, value);
      } else if (typeof value === 'boolean') {
        gl.uniform1i(location as WebGLUniformLocation, value ? 1 : 0);
      }
    }
  }

  /**
   * Clear buffers
   */
  setClearColor(color: number[]) {
    this.clearColor = [
      Math.max(0, Math.min(1, Number(color[0]) || 0)),
      Math.max(0, Math.min(1, Number(color[1]) || 0)),
      Math.max(0, Math.min(1, Number(color[2]) || 0)),
      Math.max(0, Math.min(1, Number(color[3]) || 0)),
    ];
    this.gl?.clearColor(...this.clearColor);
  }

  clear(color?: number[]) {
    const gl = this.gl!;
    if (color) this.setClearColor(color);
    gl.clearColor(...this.clearColor);
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
    
    this.drawCalls = 0;
    this.triangles = 0;
  }

  /**
   * Resize viewport
   */
  resize(width: number, height: number) {
    if (this.canvas.width === width && this.canvas.height === height) return;
    this.canvas.width = width;
    this.canvas.height = height;
    if (this.gl) {
      this.gl.viewport(0, 0, width, height);
    }
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
    const gl = this.gl;
    if (!gl) return;

    // Delete all meshes
    this.meshes.forEach(mesh => this.deleteMesh(mesh));

    // Delete all programs
    this.programs.forEach(({ program }) => {
      gl.deleteProgram(program);
    });

    this.meshes.clear();
    this.programs.clear();
    this.textures.forEach(texture => gl.deleteTexture(texture));
    this.textures.clear();
    this.textureAlpha.clear();
    this.renderTargets.forEach(target => {
      gl.deleteFramebuffer(target.framebuffer);
      gl.deleteRenderbuffer(target.depth);
    });
    this.renderTargets.clear();
    this.gl = null;
  }

  private deleteMesh(mesh: any) {
    const gl = this.gl;
    if (!gl) return;
    Object.values(mesh.buffers || {}).forEach((buffer: any) => gl.deleteBuffer(buffer));
    if (mesh.vao && this.extensions.vao) this.extensions.vao.deleteVertexArrayOES(mesh.vao);
  }
}
