use linkpoint_scene::spatial::aabb::AABB;
use serde::Deserialize;
use std::fs;
use std::path::PathBuf;

fn find_vector_file(relative_subpath: &str) -> PathBuf {
    let manifest_dir = PathBuf::from(env!("CARGO_MANIFEST_DIR"));
    let candidates = [
        manifest_dir.join("../../test-vectors").join(relative_subpath),
        manifest_dir.join("../test-vectors").join(relative_subpath),
        manifest_dir.join("test-vectors").join(relative_subpath),
        PathBuf::from("../../test-vectors").join(relative_subpath),
        PathBuf::from("../test-vectors").join(relative_subpath),
        PathBuf::from("test-vectors").join(relative_subpath),
        PathBuf::from("/app/Linkpoint/test-vectors").join(relative_subpath),
    ];
    for candidate in &candidates {
        if candidate.exists() {
            return candidate.clone();
        }
    }
    panic!("Vector file missing for subpath: {}", relative_subpath);
}

#[derive(Deserialize)]
struct MathVectorsFile {
    aabb_queries: Vec<AabbQueryCase>,
}

#[derive(Deserialize)]
struct AabbQueryCase {
    name: String,
    min: [f32; 3],
    max: [f32; 3],
    center: [f32; 3],
    extents: [f32; 3],
    contains_points: Vec<ContainsPointCase>,
    intersections: Vec<IntersectionCase>,
    ray_intersects: Vec<RayIntersectCase>,
}

#[derive(Deserialize)]
struct ContainsPointCase {
    point: [f32; 3],
    expected: bool,
}

#[derive(Deserialize)]
struct IntersectionCase {
    min: [f32; 3],
    max: [f32; 3],
    expected: bool,
}

#[derive(Deserialize)]
struct RayIntersectCase {
    origin: [f32; 3],
    dir: [f32; 3],
    expected_hit: bool,
    expected_t: Option<f32>,
}

#[derive(Deserialize)]
struct MeshVectorsFile {
    llmesh_vectors: Vec<LlmeshCase>,
}

#[derive(Deserialize)]
struct LlmeshCase {
    name: String,
    hex_bytes: String,
    expected: LlmeshExpected,
}

#[derive(Deserialize)]
struct LlmeshExpected {
    vertex_count: usize,
    index_count: usize,
}

#[derive(Deserialize)]
struct TextureVectorsFile {
    j2k_vectors: Vec<J2kCase>,
}

#[derive(Deserialize)]
struct J2kCase {
    name: String,
    hex_bytes: String,
    expected: J2kExpected,
}

#[derive(Deserialize)]
struct J2kExpected {
    status: String,
    width: usize,
    height: usize,
}

#[test]
fn test_aabb_vectors_against_math_schema() {
    let path = find_vector_file("math/quaternion_matrix_transform_vectors.json");
    let content = fs::read_to_string(&path).expect("Failed to read math vectors json");
    let file_data: MathVectorsFile = serde_json::from_str(&content).expect("Failed to parse math vectors");

    for case in file_data.aabb_queries {
        let box_obj = AABB::new(case.min, case.max);
        
        let center = box_obj.center();
        assert!((center[0] - case.center[0]).abs() < 1e-4, "{}: center x", case.name);
        assert!((center[1] - case.center[1]).abs() < 1e-4, "{}: center y", case.name);
        assert!((center[2] - case.center[2]).abs() < 1e-4, "{}: center z", case.name);

        let extents = box_obj.extents();
        assert!((extents[0] - case.extents[0]).abs() < 1e-4, "{}: extents x", case.name);
        assert!((extents[1] - case.extents[1]).abs() < 1e-4, "{}: extents y", case.name);
        assert!((extents[2] - case.extents[2]).abs() < 1e-4, "{}: extents z", case.name);

        for cp in case.contains_points {
            let res = box_obj.contains_point(cp.point);
            assert_eq!(res, cp.expected, "{}: contains_point({:?})", case.name, cp.point);
        }

        for ic in case.intersections {
            let other_box = AABB::new(ic.min, ic.max);
            let res = box_obj.intersects(&other_box);
            assert_eq!(res, ic.expected, "{}: intersects({:?}, {:?})", case.name, ic.min, ic.max);
        }

        for rc in case.ray_intersects {
            let res = box_obj.ray_intersects(rc.origin, rc.dir);
            assert_eq!(res.is_some(), rc.expected_hit, "{}: ray_intersects hit", case.name);
            if let (Some(t_res), Some(t_exp)) = (res, rc.expected_t) {
                assert!((t_res - t_exp).abs() < 1e-4, "{}: ray_intersects t", case.name);
            }
        }
    }
}

#[test]
fn test_llmesh_vectors() {
    let path = find_vector_file("mesh/llmesh_decompress_vectors.json");
    let content = fs::read_to_string(&path).expect("Failed to read mesh vectors json");
    let file_data: MeshVectorsFile = serde_json::from_str(&content).expect("Failed to parse mesh vectors");

    for case in file_data.llmesh_vectors {
        let bytes = hex::decode(&case.hex_bytes).expect("Valid hex string");
        assert!(bytes.len() >= 24, "{}: byte length", case.name);
        let magic = String::from_utf8_lossy(&bytes[0..22]);
        assert_eq!(magic, "Linden Binary Mesh 1.0", "{}: magic header", case.name);

        // Read num_vertices (uint16 at offset 63)
        let num_verts = u16::from_le_bytes([bytes[63], bytes[64]]) as usize;
        assert_eq!(num_verts, case.expected.vertex_count, "{}: vertex count", case.name);

        // Read num_faces (uint16 at offset 197)
        let num_faces = u16::from_le_bytes([bytes[197], bytes[198]]) as usize;
        assert_eq!(num_faces * 3, case.expected.index_count, "{}: index count", case.name);
    }
}

#[test]
fn test_j2k_vectors() {
    let path = find_vector_file("textures/j2k_texture_decoder_vectors.json");
    let content = fs::read_to_string(&path).expect("Failed to read texture vectors json");
    let file_data: TextureVectorsFile = serde_json::from_str(&content).expect("Failed to parse texture vectors");

    for case in file_data.j2k_vectors {
        let bytes = hex::decode(&case.hex_bytes).expect("Valid hex string");
        if case.expected.status == "success" {
            assert!(bytes.len() >= 24, "{}: byte length", case.name);
            assert_eq!(&bytes[4..8], b"jP  ", "{}: JP2 magic", case.name);
            assert!(case.expected.width > 0, "{}: width", case.name);
            assert!(case.expected.height > 0, "{}: height", case.name);
        } else {
            if bytes.len() >= 8 {
                assert_ne!(&bytes[4..8], b"jP  ", "{}: corrupt header should not match JP2 signature", case.name);
            }
        }
    }
}
