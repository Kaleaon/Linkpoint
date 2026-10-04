use linkpoint_scene::decoder::j2k::{calculate_discard_level, generate_placeholder_rgba, parse_j2k_header};
use linkpoint_scene::math::{EulerAngles, Matrix4, Quaternion, AABB};
use linkpoint_scene::volume::{generate_volume, VolumeParams};
use linkpoint_scene::wasm::{
    wasm_generate_volume, wasm_parse_j2k_header, WasmMatrix4, WasmQuaternion,
};
use linkpoint_scene::{
    AlphaMode, ChunkGrid, ChunkId, EntityKind, Material, Octree, SceneDelta, SceneEntity,
    SimulatorObject, SpatialEntity, SpatialManager, SpatialWorkerPool, Transform,
};

#[test]
fn test_quaternion_comprehensive() {
    let mut q = Quaternion::default();
    assert!(q.is_identity());
    assert!(q.is_finite());
    assert!(q.is_equal_eps(&Quaternion::IDENTITY, 1e-5));
    assert_eq!(q.magnitude(), 1.0);

    q.x = 1.0;
    assert_eq!(q.magnitude(), 2.0f32.sqrt());
    let mag = q.normalize();
    assert!(mag > 0.0);
    assert!((q.magnitude() - 1.0).abs() < 1e-5);

    let mut q_zero = Quaternion::new(0.0, 0.0, 0.0, 0.0);
    assert_eq!(q_zero.normalize(), 0.0);
    assert!(q_zero.is_identity());

    let norm = q.normalized();
    assert!((norm.magnitude() - 1.0).abs() < 1e-5);

    q.conjugate();
    assert_eq!(q.x, -norm.x);
    let conj = q.conjugated();
    assert_eq!(conj.x, -q.x);

    let (angle, axis) = q.to_angle_axis();
    let q_rebuilt = Quaternion::from_angle_axis(angle, axis);
    assert!(q.is_equal_eps(&q_rebuilt, 1e-3) || q.is_equal_eps(&Quaternion::new(-q_rebuilt.x, -q_rebuilt.y, -q_rebuilt.z, -q_rebuilt.w), 1e-3));

    let zero_aa = Quaternion::from_angle_axis(0.0, [0.0, 0.0, 0.0]);
    assert_eq!(zero_aa.to_angle_axis(), (0.0, [0.0, 0.0, 1.0]));

    let q1 = Quaternion::IDENTITY;
    let q2 = Quaternion::from_angle_axis(std::f32::consts::FRAC_PI_2, [0.0, 0.0, 1.0]);
    assert_eq!(q1.dot(&q2), std::f32::consts::FRAC_PI_4.cos());

    let v3 = [1.0, 0.0, 0.0];
    let rot3 = q2.rotate_vector3(v3);
    assert!((rot3[0] - 0.0).abs() < 1e-4);
    assert!((rot3[1] - 1.0).abs() < 1e-4);

    let v4 = [1.0, 0.0, 0.0, 1.0];
    let rot4 = q2.rotate_vector4(v4);
    assert_eq!(rot4[3], 1.0);

    let mul = q1.mul_quaternion(&q2);
    assert!(mul.is_equal_eps(&q2, 1e-5));

    let lerped = Quaternion::lerp(0.5, &q1, &q2);
    assert!((lerped.magnitude() - 1.0).abs() < 1e-5);

    let nlerped = Quaternion::nlerp(0.5, &q1, &q2);
    assert!((nlerped.magnitude() - 1.0).abs() < 1e-5);

    let opp_q2 = Quaternion::new(-q2.x, -q2.y, -q2.z, -q2.w);
    let nlerped_opp = Quaternion::nlerp(0.5, &q1, &opp_q2);
    assert!((nlerped_opp.magnitude() - 1.0).abs() < 1e-5);
}

#[test]
fn test_matrix4_comprehensive() {
    let mut m = Matrix4::default();
    assert!(m.is_identity());
    assert_eq!(m.get(0, 0), 1.0);
    m.set(0, 0, 2.0);
    assert_eq!(m.get(0, 0), 2.0);
    m.set(0, 0, 1.0);

    let q = Quaternion::from_angle_axis(0.5, [0.0, 1.0, 0.0]);
    let mq = Matrix4::from_quaternion(&q);
    let mqt = Matrix4::from_quaternion_and_translation(&q, [10.0, 20.0, 30.0]);
    assert_eq!(mqt.get_translation(), [10.0, 20.0, 30.0]);

    let mut m_trans = mqt;
    m_trans.set_translation(5.0, 5.0, 5.0);
    assert_eq!(m_trans.get_translation(), [5.0, 5.0, 5.0]);

    let mut m_transpose = mq;
    m_transpose.transpose();
    assert_eq!(m_transpose, mq.transposed());

    assert!((mq.determinant() - 1.0).abs() < 1e-4);

    let mut inv_m = mqt;
    assert!(inv_m.inverse());
    let inv_opt = mqt.inversed();
    assert!(inv_opt.is_some());

    let singular = Matrix4::new([0.0; 16]);
    assert_eq!(singular.determinant(), 0.0);
    let mut singular_mut = singular;
    assert!(!singular_mut.inverse());
    assert!(singular.inversed().is_none());

    let combined = mqt.mul_matrix(&inv_opt.unwrap());
    assert!((combined.get(0, 0) - 1.0).abs() < 1e-3);

    let p3 = [1.0, 2.0, 3.0];
    let tp3 = Matrix4::IDENTITY.transform_vector3(p3);
    assert_eq!(tp3, p3);

    let p4 = [1.0, 2.0, 3.0, 1.0];
    let tp4 = Matrix4::IDENTITY.transform_vector4(p4);
    assert_eq!(tp4, p4);

    let rp3 = Matrix4::IDENTITY.rotate_vector3(p3);
    assert_eq!(rp3, p3);
}

#[test]
fn test_euler_comprehensive() {
    let e1 = EulerAngles::default();
    assert_eq!(e1.roll, 0.0);
    assert_eq!(e1.pitch, 0.0);
    assert_eq!(e1.yaw, 0.0);

    let e2 = EulerAngles::new(0.0, 0.0, 0.0);
    let q = e2.to_quaternion();
    let e3 = EulerAngles::from_quaternion(&q);
    assert!((e2.roll - e3.roll).abs() < 1e-3);

    let q_pitch_90 = Quaternion::from_angle_axis(std::f32::consts::FRAC_PI_2, [0.0, 1.0, 0.0]);
    let e_gimbal_pos = EulerAngles::from_quaternion(&q_pitch_90);
    assert!((e_gimbal_pos.pitch - std::f32::consts::FRAC_PI_2).abs() < 1e-3);

    let q_pitch_neg90 = Quaternion::from_angle_axis(-std::f32::consts::FRAC_PI_2, [0.0, 1.0, 0.0]);
    let e_gimbal_neg = EulerAngles::from_quaternion(&q_pitch_neg90);
    assert!((e_gimbal_neg.pitch + std::f32::consts::FRAC_PI_2).abs() < 1e-3);
}

#[test]
fn test_volume_params_and_generator_comprehensive() {
    let p_cube = VolumeParams::cube();
    let p_sphere = VolumeParams::sphere();
    let _p_cyl = VolumeParams::cylinder();
    assert_ne!(p_cube, p_sphere);

    for profile_curve in [0x00, 0x01, 0x02, 0x03, 0x04, 0x05] {
        for hole_type in [0x00, 0x10, 0x20, 0x30] {
            for hollow in [0.0f32, 0.2f32] {
                let params = VolumeParams {
                    profile_curve: profile_curve | hole_type,
                    profile_hollow: hollow,
                    profile_begin: 0.0,
                    profile_end: 1.0,
                    path_twist_begin: 0.0,
                    path_twist: 0.5,
                    path_shear_x: 0.1,
                    path_shear_y: 0.1,
                    path_scale_x: 0.8,
                    path_scale_y: 0.8,
                    ..VolumeParams::default()
                };
                let faces = generate_volume(&params, 3.0);
                assert!(!faces.is_empty());
            }
        }
    }

    let invalid_params = VolumeParams {
        profile_begin: 0.9,
        profile_end: 0.1,
        ..VolumeParams::default()
    };
    assert!(generate_volume(&invalid_params, 3.0).is_empty());
}

#[test]
fn test_decoder_j2k_comprehensive() {
    assert_eq!(calculate_discard_level(1024, 1024, 64), 4);
    assert_eq!(calculate_discard_level(64, 64, 64), 0);

    let placeholder = generate_placeholder_rgba(32, 32);
    assert_eq!(placeholder.len(), 32 * 32 * 4);

    let mut j2c_data = vec![
        0xFF, 0x4F, // SOC
        0xFF, 0x51, 0x00, 0x26, // SIZ marker
        0x00, 0x00, // RSIZ
        0x00, 0x00, 0x04, 0x00, // Xsiz = 1024
        0x00, 0x00, 0x04, 0x00, // Ysiz = 1024
        0x00, 0x00, 0x00, 0x00, // XOsiz = 0
        0x00, 0x00, 0x00, 0x00, // YOsiz = 0
        0x00, 0x00, 0x02, 0x00, // XTsiz
        0x00, 0x00, 0x02, 0x00, // YTsiz
        0x00, 0x00, 0x00, 0x00, // XTOsiz
        0x00, 0x00, 0x00, 0x00, // YTOsiz
        0x00, 0x04,             // Csiz = 4
    ];
    j2c_data.resize(50, 0);
    let j2c_header = parse_j2k_header(&j2c_data);
    assert!(j2c_header.is_some());
    let jh = j2c_header.unwrap();
    assert_eq!(jh.width, 1024);
    assert_eq!(jh.height, 1024);
}

#[test]
fn test_wasm_bindings_comprehensive() {
    let mut wq1 = WasmQuaternion::new(0.0, 0.0, 0.0, 1.0);
    let wq_id = WasmQuaternion::identity();
    assert!(wq1.normalize() > 0.0);
    let rot = wq1.rotate_vector3(1.0, 0.0, 0.0);
    assert_eq!(rot.len(), 3);

    let slerped = wq1.slerp(&wq_id, 0.5);
    let _ = slerped;

    let _wm1 = WasmMatrix4::identity();
    let wm2 = WasmMatrix4::from_quaternion(&wq1);
    let tp = wm2.transform_point(1.0, 2.0, 3.0);
    assert_eq!(tp.len(), 3);

    let params = VolumeParams::cube();
    let params_json = serde_json::to_string(&params).unwrap();
    let volume_json = wasm_generate_volume(&params_json, 3.0);
    assert!(volume_json.starts_with('['));

    let mut j2c_data = vec![
        0xFF, 0x4F, // SOC
        0xFF, 0x51, 0x00, 0x26, // SIZ marker
        0x00, 0x00, // RSIZ
        0x00, 0x00, 0x04, 0x00, // Xsiz = 1024
        0x00, 0x00, 0x04, 0x00, // Ysiz = 1024
        0x00, 0x00, 0x00, 0x00, // XOsiz = 0
        0x00, 0x00, 0x00, 0x00, // YOsiz = 0
        0x00, 0x00, 0x02, 0x00, // XTsiz
        0x00, 0x00, 0x02, 0x00, // YTsiz
        0x00, 0x00, 0x00, 0x00, // XTOsiz
        0x00, 0x00, 0x00, 0x00, // YTOsiz
        0x00, 0x04,             // Csiz = 4
    ];
    j2c_data.resize(50, 0);
    let wasm_header = wasm_parse_j2k_header(&j2c_data);
    assert!(wasm_header.is_some());
}

#[test]
fn test_spatial_and_lib_comprehensive() {
    let mut octree = Octree::new(AABB::new([-100.0; 3], [100.0; 3]), 4, 16);
    let entity = SpatialEntity::new("e1", AABB::new([0.0; 3], [10.0; 3]), [5.0; 3]);
    assert!(octree.insert(entity.clone()));
    let hits = octree.query_aabb(&AABB::new([-5.0; 3], [15.0; 3]));
    assert!(!hits.is_empty());

    let grid = ChunkGrid::new([0.0, 0.0, 0.0], [256.0, 256.0, 256.0], [32.0, 32.0, 32.0]);
    let cid = grid.get_chunk_id([10.0, 20.0, 30.0]);
    assert_eq!(cid, ChunkId::new(0, 0, 0));

    let mgr = SpatialManager::new(
        [0.0, 0.0, 0.0],
        [256.0, 256.0, 256.0],
        [32.0, 32.0, 32.0],
        4,
    );
    mgr.insert(entity.clone());
    let mgr_hits = mgr.query_aabb_simd(&AABB::new([-5.0; 3], [15.0; 3]));
    assert!(!mgr_hits.is_empty());

    let pool = SpatialWorkerPool::new(2, std::sync::Arc::new(std::sync::Mutex::new(grid)));
    let _ = pool;

    let mat = Material {
        base_color_texture: Some("tex1".into()),
        normal_texture: None,
        alpha_mode: AlphaMode::Opaque,
    };
    assert_eq!(mat.alpha_mode, AlphaMode::Opaque);

    let transform = Transform::default();
    assert_eq!(transform.position, [0.0; 3]);

    let scene_entity = SceneEntity {
        id: "se1".into(),
        parent_id: None,
        transform,
        kind: EntityKind::Primitive,
        materials: vec![mat],
    };
    assert_eq!(scene_entity.kind, EntityKind::Primitive);

    let delta = SceneDelta::Snapshot(vec![scene_entity.clone()]);
    match delta {
        SceneDelta::Snapshot(entities) => assert_eq!(entities.len(), 1),
        _ => panic!("Expected Snapshot"),
    }

    let sim_obj = SimulatorObject {
        id: "so1".into(),
        parent_id: None,
        position: [1.0, 2.0, 3.0],
        rotation: Some([0.0, 0.0, 0.0, 1.0]),
        scale: Some([1.0; 3]),
        mesh_asset: Some("mesh1".into()),
    };
    let converted: SceneEntity = sim_obj.into();
    assert_eq!(converted.kind, EntityKind::Mesh);
}
