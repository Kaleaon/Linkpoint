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
    pub indices: Vec<u32>,
}

#[allow(dead_code)]
const DEFAULT_DETAIL: f32 = 3.0;
const MIN_DETAIL_FACES: f32 = 6.0;
const MIN_LOD: f32 = 0.5;
const TABLE_SCALE: [f32; 8] = [
    1.0,
    1.0,
    1.0,
    0.5,
    std::f32::consts::FRAC_1_SQRT_2,
    0.53,
    0.525,
    0.5,
];

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

const PATH_LINE: u8 = 0x10;
const PATH_CIRCLE: u8 = 0x20;
const PATH_CIRCLE2: u8 = 0x30;
const PATH_TEST: u8 = 0x40;

type P3 = [f32; 3];

fn lerp(a: f32, b: f32, t: f32) -> f32 {
    a + (b - a) * t
}

fn mix(a: P3, b: P3, f: f32) -> P3 {
    [
        lerp(a[0], b[0], f),
        lerp(a[1], b[1], f),
        lerp(a[2], b[2], f),
    ]
}

fn sub(a: P3, b: P3) -> P3 {
    [a[0] - b[0], a[1] - b[1], a[2] - b[2]]
}

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

fn rotate_axis(v: P3, axis: char, angle: f32) -> P3 {
    let c = angle.cos();
    let s = angle.sin();
    match axis {
        'z' => [v[0] * c - v[1] * s, v[0] * s + v[1] * c, v[2]],
        'x' => [v[0], v[1] * c - v[2] * s, v[1] * s + v[2] * c],
        _ => v,
    }
}

fn cull_degenerate_triangles(vertices: &[f32], indices: &[u32]) -> Vec<u32> {
    let num_verts = vertices.len() / 3;
    let mut clean = Vec::with_capacity(indices.len());
    for chunk in indices.chunks_exact(3) {
        let i0 = chunk[0] as usize;
        let i1 = chunk[1] as usize;
        let i2 = chunk[2] as usize;
        if i0 >= num_verts || i1 >= num_verts || i2 >= num_verts || i0 == i1 || i1 == i2 || i0 == i2
        {
            continue;
        }
        let a = [vertices[i0 * 3], vertices[i0 * 3 + 1], vertices[i0 * 3 + 2]];
        let b = [vertices[i1 * 3], vertices[i1 * 3 + 1], vertices[i1 * 3 + 2]];
        let c = [vertices[i2 * 3], vertices[i2 * 3 + 1], vertices[i2 * 3 + 2]];
        let e1 = sub(b, a);
        let e2 = sub(c, a);
        let n = cross(e1, e2);
        let len_sq = n[0] * n[0] + n[1] * n[1] + n[2] * n[2];
        if len_sq >= 1e-12 {
            clean.extend_from_slice(chunk);
        }
    }
    clean
}

#[derive(Debug, Clone)]
struct ProfileFace {
    index: usize,
    count: usize,
    #[allow(dead_code)]
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
                profile
                    .points
                    .push(mix(last, to, (1.0 / (split + 1) as f32) * (i + 1) as f32));
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

#[allow(clippy::too_many_arguments)]
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
                add_face(
                    &mut profile,
                    face_num * (split + 1),
                    split + 2,
                    1.0,
                    "side",
                    true,
                );
                face_num += 1;
            }
            scale_z(&mut profile, 4.0);
            if hollow > 0.0 {
                if hole_type == HOLE_TRIANGLE {
                    add_hole(&mut profile, p, true, 3.0, -0.375, hollow, 1.0, split);
                } else if hole_type == HOLE_CIRCLE {
                    add_hole(
                        &mut profile,
                        p,
                        false,
                        MIN_DETAIL_FACES * detail,
                        -0.375,
                        hollow,
                        1.0,
                        0,
                    );
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
                add_face(
                    &mut profile,
                    face_num * (split + 1),
                    split + 2,
                    1.0,
                    "side",
                    true,
                );
                face_num += 1;
            }
            if hollow > 0.0 {
                let triangle_hollow = hollow / 2.0;
                if hole_type == HOLE_CIRCLE {
                    add_hole(
                        &mut profile,
                        p,
                        false,
                        MIN_DETAIL_FACES * detail,
                        0.0,
                        triangle_hollow,
                        1.0,
                        0,
                    );
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
        let end_idx = if hollow > 0.0 { tot_out - 1 } else { tot - 2 };
        add_face(&mut profile, end_idx, 2, 0.5, "cut-end", true);
    }
    Some(profile)
}

#[derive(Debug, Clone)]
enum PathRotation {
    Z(f32),
    ZX(f32, f32),
    X(f32),
}

impl PathRotation {
    fn apply(&self, v: P3) -> P3 {
        match *self {
            PathRotation::Z(twist) => rotate_axis(v, 'z', twist),
            PathRotation::ZX(twist, ang) => {
                let r1 = rotate_axis(v, 'z', twist);
                rotate_axis(r1, 'x', ang)
            }
            PathRotation::X(ang) => rotate_axis(v, 'x', ang),
        }
    }
}

struct PathPoint {
    pos: P3,
    scale: [f32; 2],
    tex_t: f32,
    rotation: PathRotation,
}

struct Path {
    points: Vec<PathPoint>,
    open: bool,
}

fn path_begin_scale(p: &VolumeParams) -> [f32; 2] {
    [
        if p.path_scale_x > 1.0 {
            2.0 - p.path_scale_x
        } else {
            1.0
        },
        if p.path_scale_y > 1.0 {
            2.0 - p.path_scale_y
        } else {
            1.0
        },
    ]
}

fn path_end_scale(p: &VolumeParams) -> [f32; 2] {
    [
        if p.path_scale_x < 1.0 {
            p.path_scale_x
        } else {
            1.0
        },
        if p.path_scale_y < 1.0 {
            p.path_scale_y
        } else {
            1.0
        },
    ]
}

fn path_ngon(p: &VolumeParams, sides: f32) -> Path {
    let revolutions = p.path_revolutions;
    let skew = p.path_skew;
    let skew_mag = skew.abs();
    let hole_x = p.path_scale_x * (1.0 - skew_mag);
    let hole_y = p.path_scale_y;
    let mut taper_x_begin = 1.0;
    let mut taper_x_end = 1.0 - p.path_taper_x;
    let mut taper_y_begin = 1.0;
    let mut taper_y_end = 1.0 - p.path_taper_y;
    if taper_x_end > 1.0 {
        taper_x_begin = 2.0 - taper_x_end;
        taper_x_end = 1.0;
    }
    if taper_y_end > 1.0 {
        taper_y_begin = 2.0 - taper_y_end;
        taper_y_end = 1.0;
    }

    let sides_idx = sides.round() as usize;
    let mut radius_start = if sides_idx < 8 {
        TABLE_SCALE.get(sides_idx).cloned().unwrap_or(0.5)
    } else {
        0.5
    };
    radius_start *= 1.0 - hole_y;
    let mut radius_end = radius_start;
    if p.path_radius_offset < 0.0 {
        radius_start *= 1.0 + p.path_radius_offset;
    } else {
        radius_end *= 1.0 - p.path_radius_offset;
    }

    let open = p.path_end - p.path_begin < 0.99
        || skew_mag > 0.001
        || (taper_x_end - taper_x_begin).abs() > 0.001
        || (taper_y_end - taper_y_begin).abs() > 0.001
        || (radius_end - radius_start).abs() > 0.001;

    let twist_begin = p.path_twist_begin;
    let twist_end = p.path_twist;
    let mut points = Vec::new();

    let step = 1.0 / sides;
    let mut t = p.path_begin;

    let mut add_pt = |t_val: f32| {
        let ang = 2.0 * PI * revolutions * t_val;
        let r = lerp(radius_start, radius_end, t_val);
        let c = ang.cos() * r;
        let s = ang.sin() * r;
        let twist = lerp(twist_begin, twist_end, t_val) * 2.0 * PI - PI;
        points.push(PathPoint {
            pos: [
                lerp(0.0, p.path_shear_x, s) + lerp(-skew, skew, t_val) * 0.5,
                c + lerp(0.0, p.path_shear_y, s),
                s,
            ],
            scale: [
                hole_x * lerp(taper_x_begin, taper_x_end, t_val),
                hole_y * lerp(taper_y_begin, taper_y_end, t_val),
            ],
            tex_t: t_val,
            rotation: PathRotation::ZX(twist, ang),
        });
    };

    add_pt(t);
    t += step;
    t = (t * sides).trunc() / sides;
    while t < p.path_end {
        add_pt(t);
        t += step;
    }
    add_pt(p.path_end);

    Path { points, open }
}

fn generate_path(p: &VolumeParams, detail: f32, split: usize) -> Path {
    let mut path = Path {
        points: Vec::new(),
        open: true,
    };

    match p.path_curve & 0xf0 {
        PATH_CIRCLE => {
            let twist_mag = (p.path_twist_begin - p.path_twist).abs();
            let sides = (MIN_DETAIL_FACES * detail + twist_mag * 3.5 * (detail - 0.5)).floor()
                * p.path_revolutions;
            let sides = sides.floor();
            if sides > 0.0 {
                path = path_ngon(p, sides);
            }
        }
        PATH_CIRCLE2 => {
            let closed = p.path_end - p.path_begin >= 0.99 && p.path_scale_x >= 0.99;
            path = path_ngon(p, (MIN_DETAIL_FACES * detail).floor());
            if closed {
                path.open = false;
            }
            let mut toggle = 0.5f32;
            for pt in &mut path.points {
                pt.pos[0] = toggle;
                toggle = if toggle == 0.5 { -0.5 } else { 0.5 };
            }
        }
        PATH_TEST => {
            let np = 5;
            for i in 0..np {
                let t = i as f32 / (np - 1) as f32;
                let a = PI * p.path_twist * t;
                path.points.push(PathPoint {
                    pos: [
                        0.0,
                        lerp(0.0, -a.sin() * 0.5, t),
                        lerp(-0.5, a.cos() * 0.5, t),
                    ],
                    scale: [lerp(1.0, p.path_scale_x, t), lerp(1.0, p.path_scale_y, t)],
                    tex_t: t,
                    rotation: PathRotation::X(a),
                });
            }
        }
        _ => {
            let twist_mag = (p.path_twist_begin - p.path_twist).abs();
            let mut np = (twist_mag * 3.5 * (detail - 0.5)).floor() as usize + 2;
            if np < split + 2 {
                np = split + 2;
            }
            let start = path_begin_scale(p);
            let end = path_end_scale(p);
            for i in 0..np {
                let t = lerp(p.path_begin, p.path_end, i as f32 / (np - 1) as f32);
                let a = lerp(PI * p.path_twist_begin, PI * p.path_twist, t);
                path.points.push(PathPoint {
                    pos: [
                        lerp(0.0, p.path_shear_x, t),
                        lerp(0.0, p.path_shear_y, t),
                        t - 0.5,
                    ],
                    scale: [lerp(start[0], end[0], t), lerp(start[1], end[1], t)],
                    tex_t: t,
                    rotation: PathRotation::Z(a),
                });
            }
        }
    }

    if p.path_twist != p.path_twist_begin {
        path.open = true;
    }
    path
}

pub fn generate_volume(p: &VolumeParams, detail: f32) -> Vec<VolumeFace> {
    let detail = detail.max(MIN_LOD);
    let squareish = matches!(
        p.profile_curve & PROFILE_MASK,
        PROFILE_SQUARE | PROFILE_ISOTRI | PROFILE_EQUITRI | PROFILE_RIGHTTRI
    );
    let mut split = (detail * 0.66).floor() as usize;
    if (p.path_curve & 0xf0) == PATH_LINE
        && (p.path_scale_x != 1.0 || p.path_scale_y != 1.0)
        && squareish
    {
        split = 0;
    }

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

        for s in 0..size_s {
            let prof = profile.points[s];
            let r = pt
                .rotation
                .apply([prof[0] * pt.scale[0], prof[1] * pt.scale[1], 0.0]);

            mesh[t * size_s + s] = [r[0] + pt.pos[0], r[1] + pt.pos[1], r[2] + pt.pos[2]];
        }
    }

    let hollow = p.profile_hollow > 0.0;
    let mut faces = Vec::new();
    for (face_index, pf) in profile.faces.iter().enumerate() {
        let (vertices, normals, tex_coords, indices) = if pf.cap {
            build_cap(pf, &profile, &path, &mesh, size_s, hollow)
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
    hollow: bool,
) -> (Vec<f32>, Vec<f32>, Vec<f32>, Vec<u32>) {
    let top = pf.kind == "top";
    let size_t = path.points.len();
    if size_t < 2 || size_s == 0 {
        return (Vec::new(), Vec::new(), Vec::new(), Vec::new());
    }
    let offset = if top { (size_t - 1) * size_s } else { 0 };
    let count = profile.total.min(size_s);
    if count == 0 {
        return (Vec::new(), Vec::new(), Vec::new(), Vec::new());
    }

    let mut vertices = Vec::new();
    let mut tex_coords = Vec::new();
    let mut pos = Vec::new();

    for i in 0..count {
        if offset + i >= mesh.len() || i >= profile.points.len() {
            break;
        }
        let m = mesh[offset + i];
        let p = profile.points[i];
        pos.push(m);
        vertices.extend_from_slice(&m);
        tex_coords.push(p[0] + 0.5);
        tex_coords.push(if top { p[1] + 0.5 } else { 0.5 - p[1] });
    }

    let mut indices = Vec::new();
    if hollow {
        let mut i = 0usize;
        let mut j = count.saturating_sub(1);
        let mut flip = false;
        while j.saturating_sub(i) > 1 {
            if !flip {
                indices.push(i as u32);
                indices.push((i + 1) as u32);
                indices.push(j as u32);
                i += 1;
            } else {
                indices.push(j as u32);
                indices.push(i as u32);
                indices.push((j - 1) as u32);
                j -= 1;
            }
            flip = !flip;
        }
    } else {
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

            let mut mid_uv = [0.0f32, 0.0f32];
            for p in profile.points.iter().take(count) {
                mid_uv[0] += p[0];
                mid_uv[1] += p[1];
            }
            tex_coords.push(mid_uv[0] / n + 0.5);
            tex_coords.push(if top {
                mid_uv[1] / n + 0.5
            } else {
                0.5 - mid_uv[1] / n
            });

            let center = count as u32;
            for i in 0..count {
                indices.push(center);
                indices.push(i as u32);
                indices.push(((i + 1) % count.max(1)) as u32);
            }
        } else {
            let center = count.saturating_sub(1) as u32;
            for i in 0..(count.saturating_sub(2)) {
                indices.push(center);
                indices.push(i as u32);
                indices.push((i + 1) as u32);
            }
        }
    }

    if indices.is_empty() {
        return (Vec::new(), Vec::new(), Vec::new(), Vec::new());
    }

    let end_row = if top { size_t - 1 } else { 0 };
    let neighbour = if top {
        size_t.saturating_sub(2)
    } else {
        1.min(size_t - 1)
    };

    let mid = |row: usize| -> P3 {
        let mut c = [0.0f32; 3];
        if size_s == 0 {
            return c;
        }
        for s in 0..size_s {
            let idx = row * size_s + s;
            if idx < mesh.len() {
                let m = mesh[idx];
                c[0] += m[0];
                c[1] += m[1];
                c[2] += m[2];
            }
        }
        [
            c[0] / size_s as f32,
            c[1] / size_s as f32,
            c[2] / size_s as f32,
        ]
    };

    let want = sub(mid(end_row), mid(neighbour));
    let num_verts = vertices.len() / 3;
    let get_v = |idx: u32| -> P3 {
        let i = idx as usize;
        if i < num_verts {
            [vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2]]
        } else {
            [0.0, 0.0, 0.0]
        }
    };

    let mut n = [0.0f32; 3];
    for chunk in indices.chunks_exact(3) {
        let c = cross(
            sub(get_v(chunk[1]), get_v(chunk[0])),
            sub(get_v(chunk[2]), get_v(chunk[0])),
        );
        n[0] += c[0];
        n[1] += c[1];
        n[2] += c[2];
    }

    if n[0] * want[0] + n[1] * want[1] + n[2] * want[2] < 0.0 {
        for chunk in indices.chunks_exact_mut(3) {
            chunk.swap(1, 2);
        }
    }

    let flat = normalize(want);
    let mut normals = Vec::with_capacity(vertices.len());
    for _ in 0..(vertices.len() / 3) {
        normals.extend_from_slice(&flat);
    }

    let clean_indices = cull_degenerate_triangles(&vertices, &indices);
    (vertices, normals, tex_coords, clean_indices)
}

fn build_side(
    pf: &ProfileFace,
    profile: &Profile,
    path: &Path,
    mesh: &[P3],
    size_s: usize,
) -> (Vec<f32>, Vec<f32>, Vec<f32>, Vec<u32>) {
    let size_t = path.points.len();
    let is_end = pf.kind == "cut-begin" || pf.kind == "cut-end";
    let inner = pf.kind == "inner";
    let flat = pf.flat;
    let begin_s = pf.index;
    let num_s = pf.count;
    if num_s < 2 && !is_end {
        return (Vec::new(), Vec::new(), Vec::new(), Vec::new());
    }

    let dup = inner && flat && pf.count > 2;
    let num_cols = if dup { pf.count } else { num_s };

    let begin_s_tex = if !profile.points.is_empty() {
        let idx = begin_s.min(profile.points.len() - 1);
        profile.points[idx][2].floor()
    } else {
        0.0
    };

    let mut vertices = Vec::new();
    let mut tex_coords = Vec::new();
    let mut cols = 0;

    for t in 0..size_t {
        let tt = path.points[t].tex_t;
        let mut col_count = 0;
        for s in 0..num_cols {
            let index = begin_s + s;
            let ss = if is_end {
                if s > 0 { 1.0 } else { 0.0 }
            } else if index >= profile.points.len() {
                if flat { 1.0 - begin_s_tex } else { 1.0 }
            } else if flat {
                profile.points[index][2] - begin_s_tex
            } else {
                profile.points[index][2]
            };

            let source = if index >= size_s {
                t * size_s + (index - size_s)
            } else {
                t * size_s + index
            };
            if source >= mesh.len() {
                continue;
            }
            let m = mesh[source];
            vertices.extend_from_slice(&m);
            tex_coords.push(ss);
            tex_coords.push(tt);
            col_count += 1;

            if dup && s > 0 {
                vertices.extend_from_slice(&m);
                tex_coords.push(ss);
                tex_coords.push(tt);
                col_count += 1;
            }
        }
        if dup {
            let s = if profile.open {
                num_cols.saturating_sub(1)
            } else {
                0
            };
            let src_idx = t * size_s + begin_s + s;
            if src_idx < mesh.len() {
                let m = mesh[src_idx];
                vertices.extend_from_slice(&m);
                let prof_idx = begin_s + s;
                let ss = if prof_idx < profile.points.len() {
                    profile.points[prof_idx][2] - begin_s_tex
                } else {
                    0.0
                };
                tex_coords.push(ss);
                tex_coords.push(tt);
                col_count += 1;
            }
        }
        cols = col_count;
    }

    if cols < 2 {
        return (Vec::new(), Vec::new(), Vec::new(), Vec::new());
    }

    let mut indices = Vec::new();
    for t in 0..(size_t - 1) {
        for s in 0..(cols - 1) {
            let a = (s + cols * t) as u32;
            let b = (s + 1 + cols * (t + 1)) as u32;
            let c = (s + cols * (t + 1)) as u32;
            let d = (s + 1 + cols * t) as u32;
            indices.extend_from_slice(&[a, b, c, a, d, b]);
        }
    }

    let mut weld = Vec::new();
    if !flat && !profile.open && !is_end && num_s == profile.total {
        for t in 0..size_t {
            if cols > 0 {
                weld.push((t * cols, t * cols + cols - 1));
            }
        }
    }
    if !path.open && size_t > 2 {
        for s in 0..cols {
            weld.push((s, (size_t - 1) * cols + s));
        }
    }

    let normals = smooth_normals(&vertices, &indices, &weld);
    let clean_indices = cull_degenerate_triangles(&vertices, &indices);

    (vertices, normals, tex_coords, clean_indices)
}

fn smooth_normals(vertices: &[f32], indices: &[u32], weld: &[(usize, usize)]) -> Vec<f32> {
    let num_verts = vertices.len() / 3;
    let mut accum = vec![0.0f32; vertices.len()];

    for chunk in indices.chunks_exact(3) {
        let i0 = chunk[0] as usize;
        let i1 = chunk[1] as usize;
        let i2 = chunk[2] as usize;
        if i0 >= num_verts || i1 >= num_verts || i2 >= num_verts {
            continue;
        }
        let a = [vertices[i0 * 3], vertices[i0 * 3 + 1], vertices[i0 * 3 + 2]];
        let b = [vertices[i1 * 3], vertices[i1 * 3 + 1], vertices[i1 * 3 + 2]];
        let c = [vertices[i2 * 3], vertices[i2 * 3 + 1], vertices[i2 * 3 + 2]];
        let n = cross(sub(b, a), sub(c, a));
        for &k in &[i0, i1, i2] {
            accum[k * 3] += n[0];
            accum[k * 3 + 1] += n[1];
            accum[k * 3 + 2] += n[2];
        }
    }

    for &(a, b) in weld {
        if a < num_verts && b < num_verts {
            for k in 0..3 {
                let sum = accum[a * 3 + k] + accum[b * 3 + k];
                accum[a * 3 + k] = sum;
                accum[b * 3 + k] = sum;
            }
        }
    }

    let mut normals = vec![0.0f32; vertices.len()];
    for i in 0..num_verts {
        let n = normalize([accum[i * 3], accum[i * 3 + 1], accum[i * 3 + 2]]);
        normals[i * 3] = n[0];
        normals[i * 3 + 1] = n[1];
        normals[i * 3 + 2] = n[2];
    }

    normals
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

    #[test]
    fn test_volume_generator_hollow_cap() {
        let mut hollow_cube = VolumeParams::cube();
        hollow_cube.profile_hollow = 0.5;
        let faces = generate_volume(&hollow_cube, DEFAULT_DETAIL);
        assert!(!faces.is_empty());
        let top_face = faces.iter().find(|f| f.kind == "top").unwrap();
        assert!(!top_face.indices.is_empty());
        assert_eq!(top_face.indices.len() % 3, 0);
    }

    #[test]
    fn test_volume_generator_circular_path() {
        let torus = VolumeParams {
            path_curve: 0x20,
            profile_curve: 0x00,
            path_scale_y: 0.5,
            ..VolumeParams::default()
        };
        let faces = generate_volume(&torus, DEFAULT_DETAIL);
        assert!(!faces.is_empty());
        assert!(faces[0].vertices.len() > 100);
    }
}
