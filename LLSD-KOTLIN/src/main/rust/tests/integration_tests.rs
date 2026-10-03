/*!
 * LLSD Rust Implementation Tests
 * 
 * Comprehensive test suite covering all functionality
 * Copyright (C) 2024 Linden Lab
 */

#[cfg(test)]
mod tests {
    use llsd::*;
    use std::collections::HashMap;
    use uuid::uuid;
    use chrono::{DateTime, Utc, TimeZone};

    #[test]
    fn test_llsd_document_creation() {
        let doc = LLSDFactory::create(LLSDValue::String("test".to_string()));
        assert!(!doc.is_empty());
        assert_eq!(doc.get_type(), LLSDType::String);

        let empty_doc = LLSDDocument::empty();
        assert!(empty_doc.is_empty());
        assert_eq!(empty_doc.get_type(), LLSDType::Unknown);
    }

    #[test]
    fn test_llsd_value_conversions() {
        // Test From implementations
        let bool_val: LLSDValue = true.into();
        assert_eq!(bool_val, LLSDValue::Boolean(true));

        let int_val: LLSDValue = 42i32.into();
        assert_eq!(int_val, LLSDValue::Integer(42));

        let real_val: LLSDValue = 3.14f64.into();
        assert_eq!(real_val, LLSDValue::Real(3.14));

        let string_val: LLSDValue = "hello".into();
        assert_eq!(string_val, LLSDValue::String("hello".to_string()));

        let uuid_val: LLSDValue = uuid!("550e8400-e29b-41d4-a716-446655440000").into();
        assert!(matches!(uuid_val, LLSDValue::UUID(_)));
    }

    #[test]
    fn test_llsd_value_getters() {
        let bool_val = LLSDValue::Boolean(true);
        assert_eq!(bool_val.as_boolean(), Some(true));
        assert_eq!(bool_val.as_integer(), None);

        let int_val = LLSDValue::Integer(42);
        assert_eq!(int_val.as_integer(), Some(42));
        assert_eq!(int_val.as_real(), Some(42.0));

        let real_val = LLSDValue::Real(3.14);
        assert_eq!(real_val.as_real(), Some(3.14));

        let string_val = LLSDValue::String("test".to_string());
        assert_eq!(string_val.as_string(), Some("test"));

        let uuid = uuid!("550e8400-e29b-41d4-a716-446655440000");
        let uuid_val = LLSDValue::UUID(uuid);
        assert_eq!(uuid_val.as_uuid(), Some(uuid));
    }

    #[test]
    fn test_path_navigation() {
        let mut map = HashMap::new();
        map.insert("user".to_string(), LLSDValue::Map({
            let mut user_map = HashMap::new();
            user_map.insert("name".to_string(), LLSDValue::String("Alice".to_string()));
            user_map.insert("age".to_string(), LLSDValue::Integer(30));
            user_map
        }));

        let root = LLSDValue::Map(map);

        // Test getting nested values
        assert_eq!(
            root.get_path("user.name"),
            Some(&LLSDValue::String("Alice".to_string()))
        );
        assert_eq!(
            root.get_path("user.age"),
            Some(&LLSDValue::Integer(30))
        );
        assert_eq!(root.get_path("user.missing"), None);
        assert_eq!(root.get_path("missing.path"), None);

        // Test setting nested values
        let mut root_mut = root.clone();
        assert!(root_mut.set_path("user.name", LLSDValue::String("Bob".to_string())));
        assert_eq!(
            root_mut.get_path("user.name"),
            Some(&LLSDValue::String("Bob".to_string()))
        );
    }

    #[test]
    fn test_array_navigation() {
        let array = LLSDValue::Array(vec![
            LLSDValue::String("first".to_string()),
            LLSDValue::String("second".to_string()),
            LLSDValue::String("third".to_string()),
        ]);

        assert_eq!(
            array.get_path("0"),
            Some(&LLSDValue::String("first".to_string()))
        );
        assert_eq!(
            array.get_path("2"),
            Some(&LLSDValue::String("third".to_string()))
        );
        assert_eq!(array.get_path("5"), None);
    }

    #[test]
    fn test_utils_functions() {
        let test_data = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("string".to_string(), LLSDValue::String("hello".to_string()));
            map.insert("integer".to_string(), LLSDValue::Integer(42));
            map.insert("real".to_string(), LLSDValue::Real(3.14));
            map.insert("boolean".to_string(), LLSDValue::Boolean(true));
            map.insert("uuid".to_string(), LLSDValue::UUID(uuid!("550e8400-e29b-41d4-a716-446655440000")));
            map
        });

        // Test utility functions
        assert_eq!(LLSDUtils::get_string(&test_data, "string", "default"), "hello");
        assert_eq!(LLSDUtils::get_string(&test_data, "missing", "default"), "default");

        assert_eq!(LLSDUtils::get_integer(&test_data, "integer", 0), 42);
        assert_eq!(LLSDUtils::get_integer(&test_data, "missing", 0), 0);

        assert_eq!(LLSDUtils::get_real(&test_data, "real", 0.0), 3.14);
        assert_eq!(LLSDUtils::get_real(&test_data, "integer", 0.0), 42.0);

        assert_eq!(LLSDUtils::get_boolean(&test_data, "boolean", false), true);
        assert_eq!(LLSDUtils::get_boolean(&test_data, "missing", false), false);
    }

    #[test]
    fn test_floating_point_comparison() {
        let val1 = LLSDValue::Real(3.14159);
        let val2 = LLSDValue::Real(3.14160);
        let val3 = LLSDValue::Real(3.14159);

        assert!(!LLSDUtils::equals_with_tolerance(&val1, &val2, 1e-6));
        assert!(LLSDUtils::equals_with_tolerance(&val1, &val2, 1e-4));
        assert!(LLSDUtils::equals_with_tolerance(&val1, &val3, 1e-10));

        // Test mixed integer/real comparison
        let int_val = LLSDValue::Integer(3);
        let real_val = LLSDValue::Real(3.0);
        assert!(LLSDUtils::equals_with_tolerance(&int_val, &real_val, 1e-10));
    }

    #[test]
    fn test_map_operations() {
        let mut base_map = HashMap::new();
        base_map.insert("a".to_string(), LLSDValue::Integer(1));
        base_map.insert("b".to_string(), LLSDValue::Map({
            let mut inner = HashMap::new();
            inner.insert("x".to_string(), LLSDValue::String("old".to_string()));
            inner
        }));

        let mut overlay_map = HashMap::new();
        overlay_map.insert("b".to_string(), LLSDValue::Map({
            let mut inner = HashMap::new();
            inner.insert("y".to_string(), LLSDValue::String("new".to_string()));
            inner
        }));
        overlay_map.insert("c".to_string(), LLSDValue::Integer(3));

        LLSDUtils::merge_maps(&mut base_map, &overlay_map);

        // Check merge results
        assert_eq!(base_map["a"], LLSDValue::Integer(1));
        assert_eq!(base_map["c"], LLSDValue::Integer(3));

        if let LLSDValue::Map(inner_map) = &base_map["b"] {
            assert_eq!(inner_map["x"], LLSDValue::String("old".to_string()));
            assert_eq!(inner_map["y"], LLSDValue::String("new".to_string()));
        } else {
            panic!("Expected nested map");
        }
    }

    #[test]
    fn test_structure_analysis() {
        let complex_structure = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("level1".to_string(), LLSDValue::Map({
                let mut inner1 = HashMap::new();
                inner1.insert("level2".to_string(), LLSDValue::Array(vec![
                    LLSDValue::String("item1".to_string()),
                    LLSDValue::String("item2".to_string()),
                    LLSDValue::Map({
                        let mut inner2 = HashMap::new();
                        inner2.insert("level3".to_string(), LLSDValue::Integer(42));
                        inner2
                    }),
                ]));
                inner1
            }));
            map.insert("simple".to_string(), LLSDValue::String("value".to_string()));
            map
        });

        assert_eq!(LLSDUtils::max_depth(&complex_structure), 5);
        assert_eq!(LLSDUtils::count_elements(&complex_structure), 7);

        // Test constraint validation
        assert!(LLSDUtils::validate_constraints(&complex_structure, 5, 10).is_ok());
        assert!(LLSDUtils::validate_constraints(&complex_structure, 3, 10).is_err());
        assert!(LLSDUtils::validate_constraints(&complex_structure, 5, 5).is_err());
    }

    #[test]
    fn test_json_round_trip() {
        let original = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("string".to_string(), LLSDValue::String("hello world".to_string()));
            map.insert("integer".to_string(), LLSDValue::Integer(-123));
            map.insert("real".to_string(), LLSDValue::Real(3.14159));
            map.insert("boolean".to_string(), LLSDValue::Boolean(true));
            map.insert("array".to_string(), LLSDValue::Array(vec![
                LLSDValue::Integer(1),
                LLSDValue::Integer(2),
                LLSDValue::Integer(3),
            ]));
            map.insert("null".to_string(), LLSDValue::Undefined);
            map
        });

        let document = LLSDDocument::new(original.clone());

        // Serialize to JSON
        let json = LLSDFactory::serialize_json(&document, false).unwrap();

        // Parse back from JSON
        let parsed_document = LLSDFactory::parse_json(&json).unwrap();

        // Note: JSON has limited type system, so some precision may be lost
        assert_eq!(parsed_document.get_type(), document.get_type());
    }

    #[test]
    fn test_xml_round_trip() {
        let original = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("string".to_string(), LLSDValue::String("hello & <world>".to_string()));
            map.insert("integer".to_string(), LLSDValue::Integer(-123));
            map.insert("real".to_string(), LLSDValue::Real(3.14159));
            map.insert("boolean".to_string(), LLSDValue::Boolean(true));
            map.insert("uuid".to_string(), LLSDValue::UUID(uuid!("550e8400-e29b-41d4-a716-446655440000")));
            map.insert("date".to_string(), LLSDValue::Date(Utc.timestamp_opt(1609459200, 0).single().unwrap()));
            map.insert("binary".to_string(), LLSDValue::Binary(vec![0x48, 0x65, 0x6C, 0x6C, 0x6F]));
            map.insert("array".to_string(), LLSDValue::Array(vec![
                LLSDValue::Integer(1),
                LLSDValue::String("nested".to_string()),
            ]));
            map.insert("null".to_string(), LLSDValue::Undefined);
            map
        });

        let document = LLSDDocument::new(original.clone());

        // Serialize to XML
        let xml = LLSDFactory::serialize_xml(&document, true).unwrap();

        // Parse back from XML
        let parsed_document = LLSDFactory::parse_xml(&xml).unwrap();

        assert_eq!(*parsed_document.content(), original);
    }

    #[test]
    fn test_binary_round_trip() {
        let original = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("string".to_string(), LLSDValue::String("unicode: 你好世界".to_string()));
            map.insert("integer".to_string(), LLSDValue::Integer(i32::MIN));
            map.insert("real".to_string(), LLSDValue::Real(std::f64::consts::PI));
            map.insert("boolean".to_string(), LLSDValue::Boolean(false));
            map.insert("uuid".to_string(), LLSDValue::UUID(uuid!("550e8400-e29b-41d4-a716-446655440000")));
            map.insert("date".to_string(), LLSDValue::Date(Utc.timestamp_opt(1609459200, 500_000_000).single().unwrap()));
            map.insert("uri".to_string(), LLSDValue::URI("https://example.com/test?param=value".to_string()));
            map.insert("binary".to_string(), LLSDValue::Binary(vec![0x00, 0xFF, 0x42, 0xAB, 0xCD, 0xEF]));
            map.insert("empty_array".to_string(), LLSDValue::Array(Vec::new()));
            map.insert("empty_map".to_string(), LLSDValue::Map(HashMap::new()));
            map.insert("nested".to_string(), LLSDValue::Map({
                let mut nested = HashMap::new();
                nested.insert("array".to_string(), LLSDValue::Array(vec![
                    LLSDValue::Integer(1),
                    LLSDValue::Integer(2),
                    LLSDValue::Real(3.5),
                ]));
                nested
            }));
            map
        });

        let document = LLSDDocument::new(original.clone());

        // Serialize to binary
        let binary = LLSDFactory::serialize_binary(&document).unwrap();

        // Parse back from binary
        let parsed_document = LLSDFactory::parse_binary(&binary).unwrap();

        assert_eq!(*parsed_document.content(), original);
    }

    #[test]
    fn test_error_handling() {
        // Test invalid JSON
        assert!(LLSDFactory::parse_json("{invalid json}").is_err());

        // Test invalid XML
        assert!(LLSDFactory::parse_xml("<llsd><unclosed>").is_err());

        // Test invalid binary (wrong magic)
        assert!(LLSDFactory::parse_binary(&[0xFF, 0xFF, 0xFF, 0xFF]).is_err());

        // Test empty binary data
        assert!(LLSDFactory::parse_binary(&[]).is_err());
    }

    #[test]
    fn test_large_structures() {
        // Create a large array
        let large_array: Vec<LLSDValue> = (0..1000)
            .map(|i| LLSDValue::Integer(i))
            .collect();

        let doc = LLSDDocument::new(LLSDValue::Array(large_array.clone()));

        // Test binary serialization/parsing with large data
        let binary = LLSDFactory::serialize_binary(&doc).unwrap();
        let parsed = LLSDFactory::parse_binary(&binary).unwrap();

        if let LLSDValue::Array(parsed_array) = parsed.content() {
            assert_eq!(parsed_array.len(), 1000);
            assert_eq!(parsed_array[0], LLSDValue::Integer(0));
            assert_eq!(parsed_array[999], LLSDValue::Integer(999));
        } else {
            panic!("Expected array");
        }
    }

    #[test]
    fn test_debug_string_formatting() {
        let complex_data = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("name".to_string(), LLSDValue::String("Test".to_string()));
            map.insert("values".to_string(), LLSDValue::Array(vec![
                LLSDValue::Integer(1),
                LLSDValue::Boolean(true),
            ]));
            map.insert("binary".to_string(), LLSDValue::Binary(vec![1, 2, 3, 4, 5]));
            map
        });

        let debug_string = LLSDUtils::to_debug_string(&complex_data, 0);

        // Should contain structure information
        assert!(debug_string.contains("name"));
        assert!(debug_string.contains("Test"));
        assert!(debug_string.contains("values"));
        assert!(debug_string.contains("binary(5 bytes)"));
        assert!(debug_string.contains("{"));
        assert!(debug_string.contains("}"));
        assert!(debug_string.contains("["));
        assert!(debug_string.contains("]"));
    }

    // Additional integration tests would go here...
    // These would test cross-format compatibility, performance characteristics, etc.
}