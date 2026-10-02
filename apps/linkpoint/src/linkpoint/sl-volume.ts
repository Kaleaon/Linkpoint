/**
 * Second Life prim ("volume") tessellation: a profile swept along a path.
 *
 * This is a TypeScript implementation of the algorithm the official viewer uses
 * (LLProfile / LLPath / LLVolume in indra/llmath/llvolume.cpp), so cuts, hollows,
 * twists, tapers, shears, skews, radius offsets and revolutions produce the same
 * shapes, and faces come out in the same order as texture-entry face numbers
 * (top cap, sides, inner wall, bottom cap, cut ends).
 *
 * Shapes are unit sized (about +-0.5 on each axis) and are scaled by the object.
 */

export interface VolumeParams {
  pathCurve: number;
  profileCurve: number;
  pathBegin: number;
  pathEnd: number;
  /** Path scale as the viewer stores it (1 = no taper at the far end). */
  pathScaleX: number;
  pathScaleY: number;
  pathShearX: number;
  pathShearY: number;
  pathTwist: number;
  pathTwistBegin: number;
  pathRadiusOffset: number;
  pathTaperX: number;
  pathTaperY: number;
  pathRevolutions: number;
  pathSkew: number;
  profileBegin: number;
  profileEnd: number;
  profileHollow: number;
}

export interface VolumeFace {
  /** Index in the object's texture entry. */
  faceIndex: number;
  kind: 'top' | 'bottom' | 'side' | 'inner' | 'cut-begin' | 'cut-end';
  vertices: number[];
  normals: number[];
  texCoords: number[];
  indices: number[];
}

export const DEFAULT_DETAIL = 3;

const MIN_DETAIL_FACES = 6;
const MIN_LOD = 0.5;
const TABLE_SCALE = [1, 1, 1, 0.5, 0.707107, 0.53, 0.525, 0.5];

const PROFILE_MASK = 0x0f;
const HOLE_MASK = 0xf0;
const PROFILE_CIRCLE = 0x00, PROFILE_SQUARE = 0x01, PROFILE_ISOTRI = 0x02, PROFILE_EQUITRI = 0x03, PROFILE_RIGHTTRI = 0x04, PROFILE_HALF = 0x05;
const HOLE_SAME = 0x00, HOLE_CIRCLE = 0x10, HOLE_SQUARE = 0x20, HOLE_TRIANGLE = 0x30;
const PATH_LINE = 0x10, PATH_CIRCLE = 0x20, PATH_CIRCLE2 = 0x30, PATH_TEST = 0x40;

type P3 = [number, number, number];
const lerp = (a: number, b: number, t: number) => a + (b - a) * t;

interface ProfileFace { index: number; count: number; scaleU: number; flat: boolean; cap: boolean; kind: VolumeFace['kind'] }
interface Profile { points: P3[]; faces: ProfileFace[]; total: number; totalOut: number; open: boolean }

function genNGon(profile: Profile, p: VolumeParams, sides: number, offset = 0, angScale = 1, split = 0) {
  const begin = p.profileBegin, end = p.profileEnd;
  let scale = 0.5;
  const tStep = 1 / sides;
  const angStep = 2 * Math.PI * tStep * angScale;
  const totalSides = Math.round(sides / angScale);
  if (totalSides < 8) scale = TABLE_SCALE[totalSides] ?? 0.5;

  const tFirst = Math.floor(begin * sides) / sides;
  let t = tFirst;
  let ang = 2 * Math.PI * (t * angScale + offset);
  let pt1: P3 = [Math.cos(ang) * scale, Math.sin(ang) * scale, t];
  t += tStep;
  ang += angStep;
  let pt2: P3 = [Math.cos(ang) * scale, Math.sin(ang) * scale, t];
  let tFraction = (begin - tFirst) * sides;
  const mix = (a: P3, b: P3, f: number): P3 => [lerp(a[0], b[0], f), lerp(a[1], b[1], f), lerp(a[2], b[2], f)];
  const pushSplit = (to: P3) => {
    const last = profile.points[profile.points.length - 1];
    for (let i = 0; i < split && profile.points.length > 0; i++) profile.points.push(mix(last, to, (1 / (split + 1)) * (i + 1)));
  };

  if (tFraction < 0.9999) profile.points.push(mix(pt1, pt2, tFraction));

  while (t < end) {
    pt1 = [Math.cos(ang) * scale, Math.sin(ang) * scale, t];
    pushSplit(pt1);
    profile.points.push(pt1);
    t += tStep;
    ang += angStep;
  }

  pt2 = [Math.cos(ang) * scale, Math.sin(ang) * scale, t];
  tFraction = (end - (t - tStep)) * sides;
  if (tFraction > 0.0001) {
    const newPt = mix(pt1, pt2, tFraction);
    pushSplit(newPt);
    profile.points.push(newPt);
  }

  if ((end - begin) * angScale < 0.99) {
    profile.open = true;
    if (p.profileHollow <= 0) profile.points.push([0, 0, 0]);
  } else {
    profile.open = false;
  }
  profile.total = profile.points.length;
}

function addFace(profile: Profile, index: number, count: number, scaleU: number, kind: VolumeFace['kind'], flat: boolean) {
  const face: ProfileFace = { index, count, scaleU, flat, cap: false, kind };
  profile.faces.push(face);
  return face;
}
const addCap = (profile: Profile, kind: 'top' | 'bottom') => {
  profile.faces.push({ index: 0, count: profile.total, scaleU: 1, flat: false, cap: true, kind });
};

function addHole(profile: Profile, p: VolumeParams, flat: boolean, sides: number, offset: number, boxHollow: number, angScale: number, split = 0) {
  profile.totalOut = profile.total;
  genNGon(profile, p, Math.floor(sides), offset, angScale, split);
  addFace(profile, profile.totalOut, profile.total - profile.totalOut, 0, 'inner', flat);
  const inner = profile.points.slice(profile.totalOut, profile.total).map((pt): P3 => [pt[0] * boxHollow, pt[1] * boxHollow, pt[2] * boxHollow]);
  for (let i = profile.totalOut, j = inner.length - 1; i < profile.total; i++, j--) profile.points[i] = inner[j];
  for (const face of profile.faces) if (face.cap) face.count *= 2;
}

function generateProfile(p: VolumeParams, pathOpen: boolean, detail: number, split: number): Profile | null {
  const profile: Profile = { points: [], faces: [], total: 0, totalOut: 0, open: false };
  const { profileBegin: begin, profileEnd: end, profileHollow: hollow } = p;
  if (begin > end - 0.01) return null;
  const holeType = p.profileCurve & HOLE_MASK;
  let faceNum = 0;
  const scaleZ = (k: number) => { for (const pt of profile.points) pt[2] *= k; };

  switch (p.profileCurve & PROFILE_MASK) {
    case PROFILE_SQUARE: {
      genNGon(profile, p, 4, -0.375, 1, split);
      if (pathOpen) addCap(profile, 'top');
      for (let i = Math.floor(begin * 4); i < Math.floor(end * 4 + 0.999); i++) addFace(profile, faceNum++ * (split + 1), split + 2, 1, 'side', true);
      scaleZ(4);
      if (hollow) {
        if (holeType === HOLE_TRIANGLE) addHole(profile, p, true, 3, -0.375, hollow, 1, split);
        else if (holeType === HOLE_CIRCLE) addHole(profile, p, false, MIN_DETAIL_FACES * detail, -0.375, hollow, 1);
        else addHole(profile, p, true, 4, -0.375, hollow, 1, split);
      }
      if (pathOpen) profile.faces[0].count = profile.total;
      break;
    }
    case PROFILE_ISOTRI:
    case PROFILE_EQUITRI:
    case PROFILE_RIGHTTRI: {
      genNGon(profile, p, 3, 0, 1, split);
      scaleZ(3);
      if (pathOpen) addCap(profile, 'top');
      for (let i = Math.floor(begin * 3); i < Math.floor(end * 3 + 0.999); i++) addFace(profile, faceNum++ * (split + 1), split + 2, 1, 'side', true);
      if (hollow) {
        const triangleHollow = hollow / 2;
        if (holeType === HOLE_CIRCLE) addHole(profile, p, false, MIN_DETAIL_FACES * detail, 0, triangleHollow, 1);
        else if (holeType === HOLE_SQUARE) addHole(profile, p, true, 4, 0, triangleHollow, 1, split);
        else addHole(profile, p, true, 3, 0, triangleHollow, 1, split);
      }
      break;
    }
    case PROFILE_CIRCLE: {
      let circleDetail = MIN_DETAIL_FACES * detail;
      if (hollow && holeType === HOLE_SQUARE) circleDetail = Math.ceil(circleDetail / 4) * 4;
      genNGon(profile, p, Math.floor(circleDetail));
      if (pathOpen) addCap(profile, 'top');
      if (profile.open && !hollow) addFace(profile, 0, profile.total - 1, 0, 'side', false);
      else addFace(profile, 0, profile.total, 0, 'side', false);
      if (hollow) {
        if (holeType === HOLE_SQUARE) addHole(profile, p, true, 4, 0, hollow, 1, split);
        else if (holeType === HOLE_TRIANGLE) addHole(profile, p, true, 3, 0, hollow, 1, split);
        else addHole(profile, p, false, circleDetail, 0, hollow, 1);
      }
      break;
    }
    case PROFILE_HALF: {
      let circleDetail = MIN_DETAIL_FACES * detail * 0.5;
      if (hollow && holeType === HOLE_SQUARE) circleDetail = Math.ceil(circleDetail / 2) * 2;
      genNGon(profile, p, Math.floor(circleDetail), 0.5, 0.5);
      if (pathOpen) addCap(profile, 'top');
      if (profile.open && !hollow) addFace(profile, 0, profile.total - 1, 0, 'side', false);
      else addFace(profile, 0, profile.total, 0, 'side', false);
      if (hollow) {
        if (holeType === HOLE_SQUARE) addHole(profile, p, true, 2, 0.5, hollow, 0.5, split);
        else if (holeType === HOLE_TRIANGLE) addHole(profile, p, true, 3, 0.5, hollow, 0.5, split);
        else addHole(profile, p, false, circleDetail, 0.5, hollow, 0.5);
      }
      // Special case for the openness of a sphere.
      if (end - begin < 1) profile.open = true;
      else if (!hollow) {
        profile.open = false;
        profile.points.push([...profile.points[0]] as P3);
        profile.total++;
      }
      break;
    }
    default:
      return null;
  }

  if (pathOpen) addCap(profile, 'bottom');
  if (profile.open) {
    addFace(profile, profile.total - 1, 2, 0.5, 'cut-begin', true);
    addFace(profile, hollow ? profile.totalOut - 1 : profile.total - 2, 2, 0.5, 'cut-end', true);
  }
  return profile;
}

// ---- path -------------------------------------------------------------------

interface PathPoint { pos: P3; scale: [number, number]; texT: number; /** rotation applied to the scaled profile point */ rotate: (v: P3) => P3 }
interface Path { points: PathPoint[]; open: boolean }

const rotateAxis = (v: P3, axis: 'x' | 'z', angle: number): P3 => {
  const c = Math.cos(angle), s = Math.sin(angle);
  return axis === 'z' ? [v[0] * c - v[1] * s, v[0] * s + v[1] * c, v[2]] : [v[0], v[1] * c - v[2] * s, v[1] * s + v[2] * c];
};

function pathBeginScale(p: VolumeParams): [number, number] {
  return [p.pathScaleX > 1 ? 2 - p.pathScaleX : 1, p.pathScaleY > 1 ? 2 - p.pathScaleY : 1];
}
function pathEndScale(p: VolumeParams): [number, number] {
  return [p.pathScaleX < 1 ? p.pathScaleX : 1, p.pathScaleY < 1 ? p.pathScaleY : 1];
}

function pathNGon(p: VolumeParams, sides: number, endScale = 1, twistScale = 1): Path {
  const revolutions = p.pathRevolutions;
  const skew = p.pathSkew, skewMag = Math.abs(skew);
  const holeX = p.pathScaleX * (1 - skewMag), holeY = p.pathScaleY;
  let taperXBegin = 1, taperXEnd = 1 - p.pathTaperX, taperYBegin = 1, taperYEnd = 1 - p.pathTaperY;
  if (taperXEnd > 1) { taperXBegin = 2 - taperXEnd; taperXEnd = 1; }
  if (taperYEnd > 1) { taperYBegin = 2 - taperYEnd; taperYEnd = 1; }

  let radiusStart = sides < 8 ? TABLE_SCALE[sides] : 0.5;
  radiusStart *= 1 - holeY;
  let radiusEnd = radiusStart;
  if (p.pathRadiusOffset < 0) radiusStart *= 1 + p.pathRadiusOffset;
  else radiusEnd *= 1 - p.pathRadiusOffset;

  const open = p.pathEnd * endScale - p.pathBegin < 1
    || skewMag > 0.001
    || Math.abs(taperXEnd - taperXBegin) > 0.001
    || Math.abs(taperYEnd - taperYBegin) > 0.001
    || Math.abs(radiusEnd - radiusStart) > 0.001;

  const twistBegin = p.pathTwistBegin * twistScale, twistEnd = p.pathTwist * twistScale;
  const points: PathPoint[] = [];
  const add = (t: number) => {
    const ang = 2 * Math.PI * revolutions * t;
    const r = lerp(radiusStart, radiusEnd, t);
    const c = Math.cos(ang) * r, s = Math.sin(ang) * r;
    const twist = lerp(twistBegin, twistEnd, t) * 2 * Math.PI - Math.PI;
    points.push({
      pos: [lerp(0, p.pathShearX, s) + lerp(-skew, skew, t) * 0.5, c + lerp(0, p.pathShearY, s), s],
      scale: [holeX * lerp(taperXBegin, taperXEnd, t), holeY * lerp(taperYBegin, taperYEnd, t)],
      texT: t,
      // twist about z first, then revolve about the x axis
      rotate: (v) => rotateAxis(rotateAxis(v, 'z', twist), 'x', ang),
    });
  };

  const step = 1 / sides;
  let t = p.pathBegin;
  add(t);
  t += step;
  t = Math.trunc(t * sides) / sides; // snap so a cut does not move most sample points
  while (t < p.pathEnd) { add(t); t += step; }
  add(p.pathEnd);
  return { points, open };
}

function generatePath(p: VolumeParams, detail: number, split: number): Path {
  let path: Path = { points: [], open: true };
  switch (p.pathCurve & 0xf0) {
    case PATH_CIRCLE: {
      const twistMag = Math.abs(p.pathTwistBegin - p.pathTwist);
      const sides = Math.floor(Math.floor(MIN_DETAIL_FACES * detail + twistMag * 3.5 * (detail - 0.5)) * p.pathRevolutions);
      if (sides > 0) path = pathNGon(p, sides);
      break;
    }
    case PATH_CIRCLE2: {
      const closed = p.pathEnd - p.pathBegin >= 0.99 && p.pathScaleX >= 0.99;
      path = pathNGon(p, Math.floor(MIN_DETAIL_FACES * detail));
      if (closed) path.open = false;
      let toggle = 0.5;
      for (const pt of path.points) { pt.pos[0] = toggle; toggle = toggle === 0.5 ? -0.5 : 0.5; }
      break;
    }
    case PATH_TEST: {
      const np = 5;
      for (let i = 0; i < np; i++) {
        const t = i / (np - 1);
        const a = Math.PI * p.pathTwist * t;
        path.points.push({
          pos: [0, lerp(0, -Math.sin(a) * 0.5, t), lerp(-0.5, Math.cos(a) * 0.5, t)],
          scale: [lerp(1, p.pathScaleX, t), lerp(1, p.pathScaleY, t)],
          texT: t,
          rotate: (v) => rotateAxis(v, 'x', a),
        });
      }
      break;
    }
    default: {
      let np = Math.floor(Math.abs(p.pathTwistBegin - p.pathTwist) * 3.5 * (detail - 0.5)) + 2;
      if (np < split + 2) np = split + 2;
      const start = pathBeginScale(p), end = pathEndScale(p);
      for (let i = 0; i < np; i++) {
        const t = lerp(p.pathBegin, p.pathEnd, i / (np - 1));
        const a = lerp(Math.PI * p.pathTwistBegin, Math.PI * p.pathTwist, t);
        path.points.push({
          pos: [lerp(0, p.pathShearX, t), lerp(0, p.pathShearY, t), t - 0.5],
          scale: [lerp(start[0], end[0], t), lerp(start[1], end[1], t)],
          texT: t,
          rotate: (v) => rotateAxis(v, 'z', a),
        });
      }
    }
  }
  if (p.pathTwist !== p.pathTwistBegin) path.open = true;
  return path;
}

// ---- faces ---------------------------------------------------------------------

const sub = (a: P3, b: P3): P3 => [a[0] - b[0], a[1] - b[1], a[2] - b[2]];
const cross = (a: P3, b: P3): P3 => [a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]];
const normalize = (v: P3): P3 => { const l = Math.hypot(v[0], v[1], v[2]); return l > 1e-12 ? [v[0] / l, v[1] / l, v[2] / l] : [0, 0, 1]; };

/** Smooth vertex normals from triangle normals (area weighted). */
function smoothNormals(vertices: number[], indices: number[], weld?: Array<[number, number]>): number[] {
  const normals = new Array<number>(vertices.length).fill(0);
  const vertex = (i: number): P3 => [vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2]];
  for (let i = 0; i + 2 < indices.length; i += 3) {
    const a = vertex(indices[i]), b = vertex(indices[i + 1]), c = vertex(indices[i + 2]);
    const n = cross(sub(b, a), sub(c, a));
    for (const k of [indices[i], indices[i + 1], indices[i + 2]]) { normals[k * 3] += n[0]; normals[k * 3 + 1] += n[1]; normals[k * 3 + 2] += n[2]; }
  }
  // Seams (closed profile / closed path) share one normal.
  for (const [a, b] of weld || []) {
    for (let k = 0; k < 3; k++) { const s = normals[a * 3 + k] + normals[b * 3 + k]; normals[a * 3 + k] = s; normals[b * 3 + k] = s; }
  }
  for (let i = 0; i < normals.length; i += 3) {
    const n = normalize([normals[i], normals[i + 1], normals[i + 2]]);
    normals[i] = n[0]; normals[i + 1] = n[1]; normals[i + 2] = n[2];
  }
  return normals;
}

export function generateVolume(params: VolumeParams, detail = DEFAULT_DETAIL): VolumeFace[] {
  detail = Math.max(detail, MIN_LOD);
  let split = Math.floor(detail * 0.66);
  const squareish = [PROFILE_SQUARE, PROFILE_ISOTRI, PROFILE_EQUITRI, PROFILE_RIGHTTRI].includes(params.profileCurve & PROFILE_MASK);
  if ((params.pathCurve & 0xf0) === PATH_LINE && (params.pathScaleX !== 1 || params.pathScaleY !== 1) && squareish) split = 0;

  const path = generatePath(params, detail, split);
  if (path.points.length < 2) return [];
  const profile = generateProfile(params, path.open, detail, split);
  if (!profile || profile.points.length < 2) return [];

  const sizeT = path.points.length, sizeS = profile.points.length;
  // mesh[t * sizeS + s]: every profile point carried along every path point.
  const mesh: P3[] = new Array(sizeT * sizeS);
  for (let t = 0; t < sizeT; t++) {
    const pt = path.points[t];
    for (let s = 0; s < sizeS; s++) {
      const profilePoint = profile.points[s];
      const r = pt.rotate([profilePoint[0] * pt.scale[0], profilePoint[1] * pt.scale[1], 0]);
      mesh[t * sizeS + s] = [r[0] + pt.pos[0], r[1] + pt.pos[1], r[2] + pt.pos[2]];
    }
  }

  const faces: VolumeFace[] = [];
  const hollow = params.profileHollow > 0;
  profile.faces.forEach((pf, faceIndex) => {
    const face = pf.cap ? buildCap(pf, profile, path, mesh, sizeS, hollow) : buildSide(pf, profile, path, mesh, sizeS, hollow);
    if (face) faces.push({ ...face, faceIndex });
  });
  return faces;
}

function buildCap(pf: ProfileFace, profile: Profile, path: Path, mesh: P3[], sizeS: number, hollow: boolean): Omit<VolumeFace, 'faceIndex'> | null {
  const top = pf.kind === 'top';
  const sizeT = path.points.length;
  const offset = top ? (sizeT - 1) * sizeS : 0;
  const count = Math.min(profile.total, sizeS);
  const vertices: number[] = [], texCoords: number[] = [];
  const pos: P3[] = [];
  for (let i = 0; i < count; i++) {
    const m = mesh[offset + i], p = profile.points[i];
    pos.push(m);
    vertices.push(m[0], m[1], m[2]);
    texCoords.push(p[0] + 0.5, top ? p[1] + 0.5 : 0.5 - p[1]);
  }
  const indices: number[] = [];
  if (hollow) {
    // zipper between the outer ring (start) and the reversed inner ring (end)
    let i = 0, j = count - 1, flip = false;
    while (j - i > 1) {
      if (!flip) { indices.push(i, i + 1, j); i++; } else { indices.push(j, i, j - 1); j--; }
      flip = !flip;
    }
  } else {
    const closed = !profile.open;
    let center: number;
    if (closed) {
      const c: P3 = [0, 0, 0];
      for (const m of pos) { c[0] += m[0]; c[1] += m[1]; c[2] += m[2]; }
      const n = Math.max(pos.length, 1);
      vertices.push(c[0] / n, c[1] / n, c[2] / n);
      const first = profile.points[0], mid = [0, 0];
      for (const p of profile.points.slice(0, count)) { mid[0] += p[0]; mid[1] += p[1]; }
      texCoords.push(mid[0] / n + 0.5, top ? mid[1] / n + 0.5 : 0.5 - mid[1] / n);
      void first;
      center = count;
      for (let i = 0; i < count; i++) indices.push(center, i, (i + 1) % count);
    } else {
      center = count - 1; // an open solid profile ends with its centre point
      for (let i = 0; i + 1 < count - 1; i++) indices.push(center, i, i + 1);
    }
  }
  if (!indices.length) return null;
  // Flat cap: orient it away from the solid.
  const endRow = top ? sizeT - 1 : 0;
  const neighbour = top ? sizeT - 2 : 1;
  const mid = (row: number): P3 => { const c: P3 = [0, 0, 0]; for (let s = 0; s < sizeS; s++) { const m = mesh[row * sizeS + s]; c[0] += m[0]; c[1] += m[1]; c[2] += m[2]; } return [c[0] / sizeS, c[1] / sizeS, c[2] / sizeS]; };
  // The cap faces away from the solid: along the path at the top end, against it at the bottom end.
  const want: P3 = sub(mid(endRow), mid(neighbour));
  const v = (i: number): P3 => [vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2]];
  const n: P3 = [0, 0, 0];
  for (let k = 0; k + 2 < indices.length; k += 3) {
    const c = cross(sub(v(indices[k + 1]), v(indices[k])), sub(v(indices[k + 2]), v(indices[k])));
    n[0] += c[0]; n[1] += c[1]; n[2] += c[2];
  }
  if (n[0] * want[0] + n[1] * want[1] + n[2] * want[2] < 0) {
    for (let k = 0; k + 2 < indices.length; k += 3) [indices[k + 1], indices[k + 2]] = [indices[k + 2], indices[k + 1]];
  }
  const flat = normalize(want);
  const normals: number[] = [];
  for (let i = 0; i < vertices.length / 3; i++) normals.push(flat[0], flat[1], flat[2]);
  return { kind: pf.kind, vertices, normals, texCoords, indices };
}

function buildSide(pf: ProfileFace, profile: Profile, path: Path, mesh: P3[], sizeS: number, hollow: boolean): Omit<VolumeFace, 'faceIndex'> | null {
  const sizeT = path.points.length;
  const isEnd = pf.kind === 'cut-begin' || pf.kind === 'cut-end';
  const inner = pf.kind === 'inner';
  const flat = pf.flat;
  const beginS = pf.index;
  let numS = pf.count;
  if (numS < 2 && !isEnd) return null;
  if (inner && flat && numS > 2) numS *= 2;
  const beginStex = Math.floor(profile.points[Math.min(beginS, profile.points.length - 1)][2]);
  const dup = inner && flat && pf.count > 2;
  const numCols = dup ? pf.count : numS; // number of source profile points per row

  const vertices: number[] = [], texCoords: number[] = [];
  let cols = 0;
  for (let t = 0; t < sizeT; t++) {
    const tt = path.points[t].texT;
    let colCount = 0;
    for (let s = 0; s < numCols; s++) {
      const index = beginS + s;
      let ss: number;
      if (isEnd) ss = s ? 1 : 0;
      else if (index >= profile.points.length) ss = flat ? 1 - beginStex : 1;
      else ss = flat ? profile.points[index][2] - beginStex : profile.points[index][2];
      // wrapping: the point after the last profile point is the first one of the same row
      const source = index >= sizeS ? t * sizeS + (index - sizeS) : t * sizeS + index;
      const m = mesh[source];
      vertices.push(m[0], m[1], m[2]);
      texCoords.push(ss, tt);
      colCount++;
      if (dup && s > 0) { vertices.push(m[0], m[1], m[2]); texCoords.push(ss, tt); colCount++; }
    }
    if (dup) {
      const s = profile.open ? numCols - 1 : 0;
      const m = mesh[t * sizeS + beginS + s];
      vertices.push(m[0], m[1], m[2]);
      texCoords.push(profile.points[beginS + s][2] - beginStex, tt);
      colCount++;
    }
    cols = colCount;
  }
  if (cols < 2) return null;
  const indices: number[] = [];
  for (let t = 0; t < sizeT - 1; t++) {
    for (let s = 0; s < cols - 1; s++) {
      indices.push(s + cols * t, s + 1 + cols * (t + 1), s + cols * (t + 1), s + cols * t, s + 1 + cols * t, s + 1 + cols * (t + 1));
    }
  }
  // seams: a closed round profile / closed path share normals across the join
  const weld: Array<[number, number]> = [];
  if (!flat && !profile.open && !isEnd && numS === profile.total) for (let t = 0; t < sizeT; t++) weld.push([t * cols, t * cols + cols - 1]);
  if (!path.open && sizeT > 2) for (let s = 0; s < cols; s++) weld.push([s, (sizeT - 1) * cols + s]);
  const normals = smoothNormals(vertices, indices, weld);
  void hollow;
  return { kind: pf.kind, vertices, normals, texCoords, indices };
}

/** Parameters from the simulator's unpacked ObjectUpdate shape fields (already in the viewer's units). */
export function volumeParamsFrom(shape: Record<string, any> | null | undefined): VolumeParams | null {
  if (!shape || shape.pathCurve === undefined || shape.profileCurve === undefined) return null;
  const n = (value: unknown, fallback: number) => (Number.isFinite(Number(value)) && value !== null && value !== undefined ? Number(value) : fallback);
  return {
    pathCurve: n(shape.pathCurve, PATH_LINE), profileCurve: n(shape.profileCurve, PROFILE_SQUARE),
    pathBegin: n(shape.pathBegin, 0), pathEnd: n(shape.pathEnd, 1),
    pathScaleX: n(shape.pathScaleX, 1), pathScaleY: n(shape.pathScaleY, 1),
    pathShearX: n(shape.pathShearX, 0), pathShearY: n(shape.pathShearY, 0),
    pathTwist: n(shape.pathTwist, 0), pathTwistBegin: n(shape.pathTwistBegin, 0),
    pathRadiusOffset: n(shape.pathRadiusOffset, 0),
    pathTaperX: n(shape.pathTaperX, 0), pathTaperY: n(shape.pathTaperY, 0),
    pathRevolutions: n(shape.pathRevolutions, 1), pathSkew: n(shape.pathSkew, 0),
    profileBegin: n(shape.profileBegin, 0), profileEnd: n(shape.profileEnd, 1), profileHollow: n(shape.profileHollow, 0),
  };
}

/** Stable key for caching generated geometry. */
export function volumeKey(p: VolumeParams, detail = DEFAULT_DETAIL): string {
  return `${detail}|${Object.values(p).map((v) => Number(v).toFixed(5)).join(',')}`;
}
