use super::params::VolumeParams;
use serde::{Deserialize, Serialize};
use std::f32::consts::PI;

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct VolumeFace {
    pub face_index: u32,
    pub kind: String,
    pub vertices: Vec<f32>,
    pub normals: Vec<f32>,
    pub tex_coords: Vec<f32>,
    pub indices: Vec<u16>,
}

#[allow(dead_code)]
const DEFAULT_DETAIL: f32 = 3.0;
const MIN_DETAIL_FACES: f32 = 6.0;
const MIN_LOD: f32 = 0.5;
const TABLE_SCALE: [f32; 8] = [1.0, 1.0, 1.0, 0.5, 0.707107, 0.53, 0.525, 0.5];

const PROFILE_MASK: u8 = 0x0f;
const HOLE_MASK: u8 = 0xf0;
const PROFILE_CIRCLE: u8 = 0x00;
const PROFILE_SQUARE: u8 = 0x01;
const PROFILE_ISOTRI: u8 = 0x02;
const PROFILE_EQUITRI: u8 = 0x03;
const PROFILE_RIGHTTRI: u8 = 0x04;
const PROFILE_HALF: u8 = 0x05;

const HOLE_CIRCLE: u8 = 0x10;
const HOLE_SQUARE: u8 = 0x20;
const HOLE_TRIANGLE: u8 = 0x30;

#[allow(dead_code)]
const PATH_LINE: u8 = 0x10;
#[allow(dead_code)]
const PATH_CIRCLE: u8 = 0x20;
#[allow(dead_code)]
const PATH_CIRCLE2: u8 = 0x30;
#[allow(dead_code)]
const PATH_TEST: u8 = 0x40;

type P3 = [f32; 3];

fn lerp(a: f32, b: f32, t: f32) -> f32 {
    a + (b - a) * t
}

fn mix(a: P3, b: P3, f: f32) -> P3 {
    [lerp(a[0], b[0], f), lerp(a[1], b[1], f), lerp(a[2], b[2], f)]
}

#[allow(dead_code)]
fn sub(a: P3, b: P3) -> P3 {
    [a[0] - b[0], a[1] - b[1], a[2] - b[2]]
}

#[allow(dead_code)]
fn cross(a: P3, b: P3) -> P3 {
    [
        a[1] * b[2] - a[2] * b[1],
        a[2] * b[0] - a[0] * b[2],
        a[0] * b[1] - a[1] * b[0],
    ]
}

fn normalize(v: P3) -> P3 {
    let l = (v[0] * v[0] + v[1] * v[1] + v[2] * v[2]).sqrt();
    if l > 1e-12 {
        [v[0] / l, v[1] / l, v[2] / l]
    } else {
        [0.0, 0.0, 1.0]
    }
}

#[derive(Debug, Clone)]
#[allow(dead_code)]
struct ProfileFace {
    index: usize,
    count: usize,
    scale_u: f32,
    flat: bool,
    cap: bool,
    kind: String,
}

#[derive(Debug, Clone, Default)]
struct Profile {
    points: Vec<P3>,
    faces: Vec<ProfileFace>,
    total: usize,
    total_out: usize,
    open: bool,
}

fn gen_ngon(
    profile: &mut Profile,
    p: &VolumeParams,
    sides: f32,
    offset: f32,
    ang_scale: f32,
    split: usize,
) {
    let begin = p.profile_begin;
    let end = p.profile_end;
    let mut scale = 0.5f32;
    let t_step = 1.0 / sides;
    let ang_step = 2.0 * PI * t_step * ang_scale;
    let total_sides = (sides / ang_scale).round() as usize;
    if total_sides < 8 {
        scale = TABLE_SCALE.get(total_sides).cloned().unwrap_or(0.5);
    }

    let t_first = (begin * sides).floor() / sides;
    let mut t = t_first;
    let mut ang = 2.0 * PI * (t * ang_scale + offset);
    let mut pt1: P3 = [ang.cos() * scale, ang.sin() * scale, t];
    t += t_step;
    ang += ang_step;
    let mut pt2: P3 = [ang.cos() * scale, ang.sin() * scale, t];
    let mut t_fraction = (begin - t_first) * sides;

    let push_split = |profile: &mut Profile, to: P3| {
        if let Some(&last) = profile.points.last() {
            for i in 0..split {
                profile.points.push(mix(last, to, (1.0 / (split + 1) as f32) * (i + 1) as f32));
            }
        }
    };

    if t_fraction < 0.9999 {
        profile.points.push(mix(pt1, pt2, t_fraction));
    }

    while t < end {
        pt1 = [ang.cos() * scale, ang.sin() * scale, t];
        push_split(profile, pt1);
        profile.points.push(pt1);
        t += t_step;
        ang += ang_step;
    }

    pt2 = [ang.cos() * scale, ang.sin() * scale, t];
    t_fraction = (end - (t - t_step)) * sides;
    if t_fraction > 0.0001 {
        let new_pt = mix(pt1, pt2, t_fraction);
        push_split(profile, new_pt);
        profile.points.push(new_pt);
    }

    if (end - begin) * ang_scale < 0.99 {
        profile.open = true;
        if p.profile_hollow <= 0.0 {
            profile.points.push([0.0, 0.0, 0.0]);
        }
    } else {
        profile.open = false;
    }
    profile.total = profile.points.len();
}

fn add_face(
    profile: &mut Profile,
    index: usize,
    count: usize,
    scale_u: f32,
    kind: &str,
    flat: bool,
) {
    profile.faces.push(ProfileFace {
        index,
        count,
        scale_u,
        flat,
        cap: false,
        kind: kind.to_string(),
    });
}

fn add_cap(profile: &mut Profile, kind: &str) {
    profile.faces.push(ProfileFace {
        index: 0,
        count: profile.total,
        scale_u: 1.0,
        flat: false,
        cap: true,
        kind: kind.to_string(),
    });
}

fn add_hole(
    profile: &mut Profile,
    p: &VolumeParams,
    flat: bool,
    sides: f32,
    offset: f32,
    box_hollow: f32,
    ang_scale: f32,
    split: usize,
) {
    profile.total_out = profile.total;
    gen_ngon(profile, p, sides.floor(), offset, ang_scale, split);
    add_face(
        profile,
        profile.total_out,
        profile.total - profile.total_out,
        0.0,
        "inner",
        flat,
    );

    let inner: Vec<P3> = profile.points[profile.total_out..profile.total]
        .iter()
        .map(|pt| [pt[0] * box_hollow, pt[1] * box_hollow, pt[2] * box_hollow])
        .collect();

    for (i, j) in (profile.total_out..profile.total).zip((0..inner.len()).rev()) {
        profile.points[i] = inner[j];
    }
    for face in &mut profile.faces {
        if face.cap {
            face.count *= 2;
        }
    }
}

fn generate_profile(
    p: &VolumeParams,
    path_open: bool,
    detail: f32,
    split: usize,
) -> Option<Profile> {
    let mut profile = Profile::default();
    let begin = p.profile_begin;
    let end = p.profile_end;
    let hollow = p.profile_hollow;
    if begin > end - 0.01 {
        return None;
    }

    let hole_type = p.profile_curve & HOLE_MASK;
    let mut face_num = 0;

    let scale_z = |profile: &mut Profile, k: f32| {
        for pt in &mut profile.points {
            pt[2] *= k;
        }
    };

    match p.profile_curve & PROFILE_MASK {
        PROFILE_SQUARE => {
            gen_ngon(&mut profile, p, 4.0, -0.375, 1.0, split);
            if path_open {
                add_cap(&mut profile, "top");
            }
            let start = (begin * 4.0).floor() as i32;
            let finish = (end * 4.0 + 0.999).floor() as i32;
            for _ in start..finish {
                add_face(&mut profile, face_num * (split + 1), split + 2, 1.0, "side", true);
                face_num += 1;
            }
            scale_z(&mut profile, 4.0);
            if hollow > 0.0 {
                if hole_type == HOLE_TRIANGLE {
                    add_hole(&mut profile, p, true, 3.0, -0.375, hollow, 1.0, split);
                } else if hole_type == HOLE_CIRCLE {
                    add_hole(&mut profile, p, false, MIN_DETAIL_FACES * detail, -0.375, hollow, 1.0, 0);
                } else {
                    add_hole(&mut profile, p, true, 4.0, -0.375, hollow, 1.0, split);
                }
            }
            if path_open && !profile.faces.is_empty() {
                profile.faces[0].count = profile.total;
            }
        }
        PROFILE_ISOTRI | PROFILE_EQUITRI | PROFILE_RIGHTTRI => {
            gen_ngon(&mut profile, p, 3.0, 0.0, 1.0, split);
            scale_z(&mut profile, 3.0);
            if path_open {
                add_cap(&mut profile, "top");
            }
            let start = (begin * 3.0).floor() as i32;
            let finish = (end * 3.0 + 0.999).floor() as i32;
            for _ in start..finish {
                add_face(&mut profile, face_num * (split + 1), split + 2, 1.0, "side", true);
                face_num += 1;
            }
            if hollow > 0.0 {
                let triangle_hollow = hollow / 2.0;
                if hole_type == HOLE_CIRCLE {
                    add_hole(&mut profile, p, false, MIN_DETAIL_FACES * detail, 0.0, triangle_hollow, 1.0, 0);
                } else if hole_type == HOLE_SQUARE {
                    add_hole(&mut profile, p, true, 4.0, 0.0, triangle_hollow, 1.0, split);
                } else {
                    add_hole(&mut profile, p, true, 3.0, 0.0, triangle_hollow, 1.0, split);
                }
            }
        }
        PROFILE_CIRCLE => {
            let mut circle_detail = MIN_DETAIL_FACES * detail;
            if hollow > 0.0 && hole_type == HOLE_SQUARE {
                circle_detail = (circle_detail / 4.0).ceil() * 4.0;
            }
            gen_ngon(&mut profile, p, circle_detail.floor(), 0.0, 1.0, 0);
            if path_open {
                add_cap(&mut profile, "top");
            }
            let tot = profile.total;
            if profile.open && hollow <= 0.0 {
                add_face(&mut profile, 0, tot - 1, 0.0, "side", false);
            } else {
                add_face(&mut profile, 0, tot, 0.0, "side", false);
            }
            if hollow > 0.0 {
                if hole_type == HOLE_SQUARE {
                    add_hole(&mut profile, p, true, 4.0, 0.0, hollow, 1.0, split);
                } else if hole_type == HOLE_TRIANGLE {
                    add_hole(&mut profile, p, true, 3.0, 0.0, hollow, 1.0, split);
                } else {
                    add_hole(&mut profile, p, false, circle_detail, 0.0, hollow, 1.0, 0);
                }
            }
        }
        PROFILE_HALF => {
            let mut circle_detail = MIN_DETAIL_FACES * detail * 0.5;
            if hollow > 0.0 && hole_type == HOLE_SQUARE {
                circle_detail = (circle_detail / 2.0).ceil() * 2.0;
            }
            gen_ngon(&mut profile, p, circle_detail.floor(), 0.5, 0.5, 0);
            if path_open {
                add_cap(&mut profile, "top");
            }
            let tot = profile.total;
            if profile.open && hollow <= 0.0 {
                add_face(&mut profile, 0, tot - 1, 0.0, "side", false);
            } else {
                add_face(&mut profile, 0, tot, 0.0, "side", false);
            }
            if hollow > 0.0 {
                if hole_type == HOLE_SQUARE {
                    add_hole(&mut profile, p, true, 2.0, 0.5, hollow, 0.5, split);
                } else if hole_type == HOLE_TRIANGLE {
                    add_hole(&mut profile, p, true, 3.0, 0.5, hollow, 0.5, split);
                } else {
                    add_hole(&mut profile, p, false, circle_detail, 0.5, hollow, 0.5, 0);
                }
            }
            if end - begin < 1.0 {
                profile.open = true;
            } else if hollow <= 0.0 {
                profile.open = false;
                if !profile.points.is_empty() {
                    let first = profile.points[0];
                    profile.points.push(first);
                    profile.total += 1;
                }
            }
        }
        _ => return None,
    }

    if path_open {
        add_cap(&mut profile, "bottom");
    }
    if profile.open {
        let tot = profile.total;
        let tot_out = profile.total_out;
        add_face(&mut profile, tot - 1, 2, 0.5, "cut-begin", true);
        let end_idx = if hollow > 0.0 {
            tot_out - 1
        } else {
            tot - 2
        };
        add_face(&mut profile, end_idx, 2, 0.5, "cut-end", true);
    }
    Some(profile)
}

struct PathPoint {
    pos: P3,
    scale: [f32; 2],
    tex_t: f32,
    angle: f32,
}

struct Path {
    points: Vec<PathPoint>,
    open: bool,
}

fn generate_path(p: &VolumeParams, detail: f32, split: usize) -> Path {
    let mut points = Vec::new();
    let np = (p.path_twist_begin - p.path_twist).abs() * 3.5 * (detail - 0.5) + 2.0;
    let np = (np.floor() as usize).max(split + 2);

    for i in 0..np {
        let t = lerp(p.path_begin, p.path_end, i as f32 / (np - 1) as f32);
        let a = lerp(PI * p.path_twist_begin, PI * p.path_twist, t);
        points.push(PathPoint {
            pos: [lerp(0.0, p.path_shear_x, t), lerp(0.0, p.path_shear_y, t), t - 0.5],
            scale: [lerp(1.0, p.path_scale_x, t), lerp(1.0, p.path_scale_y, t)],
            tex_t: t,
            angle: a,
        },
        );
    }

    Path {
        points,
        open: p.path_twist != p.path_twist_begin,
    }
}

pub fn generate_volume(p: &VolumeParams, detail: f32) -> Vec<VolumeFace> {
    let detail = detail.max(MIN_LOD);
    let split = (detail * 0.66).floor() as usize;

    let path = generate_path(p, detail, split);
    if path.points.len() < 2 {
        return Vec::new();
    }

    let profile = match generate_profile(p, path.open, detail, split) {
        Some(pr) if pr.points.len() >= 2 => pr,
        _ => return Vec::new(),
    };

    let size_t = path.points.len();
    let size_s = profile.points.len();
    let mut mesh = vec![[0.0f32; 3]; size_t * size_s];

    for t in 0..size_t {
        let pt = &path.points[t];
        let cos_a = pt.angle.cos();
        let sin_a = pt.angle.sin();

        for s in 0..size_s {
            let prof = profile.points[s];
            let sx = prof[0] * pt.scale[0];
            let sy = prof[1] * pt.scale[1];
            let rx = sx * cos_a - sy * sin_a;
            let ry = sx * sin_a + sy * cos_a;

            mesh[t * size_s + s] = [rx + pt.pos[0], ry + pt.pos[1], prof[2] + pt.pos[2]];
        }
    }

    let mut faces = Vec::new();
    for (face_index, pf) in profile.faces.iter().enumerate() {
        let (vertices, normals, tex_coords, indices) = if pf.cap {
            build_cap(pf, &profile, &path, &mesh, size_s)
        } else {
            build_side(pf, &profile, &path, &mesh, size_s)
        };

        if !indices.is_empty() {
            faces.push(VolumeFace {
                face_index: face_index as u32,
                kind: pf.kind.clone(),
                vertices,
                normals,
                tex_coords,
                indices,
            });
        }
    }

    faces
}

fn build_cap(
    pf: &ProfileFace,
    profile: &Profile,
    path: &Path,
    mesh: &[P3],
    size_s: usize,
) -> (Vec<f32>, Vec<f32>, Vec<f32>, Vec<u16>) {
    let top = pf.kind == "top";
    let size_t = path.points.len();
    let offset = if top { (size_t - 1) * size_s } else { 0 };
    let count = profile.total.min(size_s);

    let mut vertices = Vec::new();
    let mut tex_coords = Vec::new();
    let mut pos = Vec::new();

    for i in 0..count {
        let m = mesh[offset + i];
        let p = profile.points[i];
        pos.push(m);
        vertices.extend_from_slice(&m);
        tex_coords.push(p[0] + 0.5);
        tex_coords.push(if top { p[1] + 0.5 } else { 0.5 - p[1] });
    }

    let mut indices = Vec::new();
    let closed = !profile.open;
    if closed {
        let mut c = [0.0f32; 3];
        for m in &pos {
            c[0] += m[0];
            c[1] += m[1];
            c[2] += m[2];
        }
        let n = pos.len().max(1) as f32;
        vertices.extend_from_slice(&[c[0] / n, c[1] / n, c[2] / n]);
        tex_coords.extend_from_slice(&[0.5, 0.5]);
        let center = count as u16;
        for i in 0..count {
            indices.push(center);
            indices.push(i as u16);
            indices.push(((i + 1) % count) as u16);
        }
    } else {
        let center = (count - 1) as u16;
        for i in 0..(count.saturating_sub(2)) {
            indices.push(center);
            indices.push(i as u16);
            indices.push((i + 1) as u16);
        }
    }

    let mut normals = vec![0.0; vertices.len()];
    let flat_normal = if top { [0.0, 0.0, 1.0] } else { [0.0, 0.0, -1.0] };
    for i in 0..(vertices.len() / 3) {
        normals[i * 3] = flat_normal[0];
        normals[i * 3 + 1] = flat_normal[1];
        normals[i * 3 + 2] = flat_normal[2];
    }

    (vertices, normals, tex_coords, indices)
}

fn build_side(
    pf: &ProfileFace,
    _profile: &Profile,
    path: &Path,
    mesh: &[P3],
    size_s: usize,
) -> (Vec<f32>, Vec<f32>, Vec<f32>, Vec<u16>) {
    let size_t = path.points.len();
    let begin_s = pf.index;
    let num_s = pf.count;
    if num_s < 2 {
        return (Vec::new(), Vec::new(), Vec::new(), Vec::new());
    }

    let mut vertices = Vec::new();
    let mut tex_coords = Vec::new();

    for t in 0..size_t {
        let tt = path.points[t].tex_t;
        for s in 0..num_s {
            let index = begin_s + s;
            let source = if index >= size_s {
                t * size_s + (index - size_s)
            } else {
                t * size_s + index
            };
            let m = mesh[source];
            vertices.extend_from_slice(&m);
            tex_coords.push(s as f32 / (num_s - 1) as f32);
            tex_coords.push(tt);
        }
    }

    let cols = num_s;
    let mut indices = Vec::new();
    for t in 0..(size_t - 1) {
        for s in 0..(cols - 1) {
            let a = (s + cols * t) as u16;
            let b = (s + 1 + cols * (t + 1)) as u16;
            let c = (s + cols * (t + 1)) as u16;
            let d = (s + 1 + cols * t) as u16;
            indices.extend_from_slice(&[a, b, c, a, d, b]);
        }
    }

    let mut normals = vec![0.0; vertices.len()];
    for i in 0..(vertices.len() / 3) {
        let nx = vertices[i * 3];
        let ny = vertices[i * 3 + 1];
        let n = normalize([nx, ny, 0.0]);
        normals[i * 3] = n[0];
        normals[i * 3 + 1] = n[1];
        normals[i * 3 + 2] = n[2];
    }

    (vertices, normals, tex_coords, indices)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_volume_generator_cube() {
        let cube = VolumeParams::cube();
        let faces = generate_volume(&cube, DEFAULT_DETAIL);
        assert!(!faces.is_empty());
        for face in &faces {
            assert!(!face.vertices.is_empty());
            assert_eq!(face.vertices.len(), face.normals.len());
            assert_eq!(face.vertices.len() / 3 * 2, face.tex_coords.len());
        }
    }

    #[test]
    fn test_volume_generator_cylinder() {
        let cylinder = VolumeParams::cylinder();
        let faces = generate_volume(&cylinder, DEFAULT_DETAIL);
        assert!(!faces.is_empty());
    }
}
