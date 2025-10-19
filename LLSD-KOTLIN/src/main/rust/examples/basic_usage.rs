/*!
 * LLSD Rust Usage Examples
 * 
 * Copyright (C) 2024 Linden Lab
 */

use llsd::*;
use std::collections::HashMap;
use uuid::Uuid;
use chrono::Utc;

fn main() -> Result<(), Box<dyn std::error::Error>> {
    println!("LLSD Rust Examples");
    println!("==================");

    basic_usage_example()?;
    format_conversion_example()?;
    
    #[cfg(feature = "secondlife")]
    second_life_example()?;
    
    #[cfg(feature = "firestorm")]
    firestorm_example()?;
    
    advanced_features_example()?;

    Ok(())
}

fn basic_usage_example() -> LLSDResult<()> {
    println!("\n1. Basic LLSD Usage");
    println!("------------------");

    // Create LLSD data programmatically
    let mut user_data = HashMap::new();
    user_data.insert("name".to_string(), LLSDValue::String("Alice Smith".to_string()));
    user_data.insert("age".to_string(), LLSDValue::Integer(30));
    user_data.insert("is_premium".to_string(), LLSDValue::Boolean(true));
    user_data.insert("user_id".to_string(), LLSDValue::UUID(Uuid::new_v4()));
    user_data.insert("join_date".to_string(), LLSDValue::Date(Utc::now()));
    user_data.insert("scores".to_string(), LLSDValue::Array(vec![
        LLSDValue::Integer(95),
        LLSDValue::Integer(87),
        LLSDValue::Real(92.5),
    ]));

    let document = LLSDDocument::new(LLSDValue::Map(user_data));

    // Access data safely
    let name = LLSDUtils::get_string(document.content(), "name", "Unknown");
    let age = LLSDUtils::get_integer(document.content(), "age", 0);
    let is_premium = LLSDUtils::get_boolean(document.content(), "is_premium", false);

    println!("User: {} (age: {}, premium: {})", name, age, is_premium);

    // Navigate nested structures
    if let Some(scores_array) = document.content().get_path("scores") {
        if let LLSDValue::Array(scores) = scores_array {
            println!("Test scores: {:?}", scores);
        }
    }

    Ok(())
}

fn format_conversion_example() -> LLSDResult<()> {
    println!("\n2. Format Conversion");
    println!("-------------------");

    // Create sample data
    let sample_data = create_sample_data();
    let document = LLSDDocument::new(sample_data);

    // Convert to JSON
    let json = LLSDFactory::serialize_json(&document, true)?;
    println!("JSON format:");
    println!("{}", &json[..200.min(json.len())]); // First 200 chars
    if json.len() > 200 { println!("..."); }

    // Convert to XML
    let xml = LLSDFactory::serialize_xml(&document, true)?;
    println!("\nXML format:");
    println!("{}", &xml[..300.min(xml.len())]); // First 300 chars
    if xml.len() > 300 { println!("..."); }

    // Convert to binary
    let binary = LLSDFactory::serialize_binary(&document)?;
    println!("\nBinary format: {} bytes", binary.len());
    
    // Round-trip test
    let parsed_json = LLSDFactory::parse_json(&json)?;
    let parsed_xml = LLSDFactory::parse_xml(&xml)?;
    let parsed_binary = LLSDFactory::parse_binary(&binary)?;

    println!("Round-trip successful: JSON={}, XML={}, Binary={}",
        parsed_json.get_type() == document.get_type(),
        *parsed_xml.content() == *document.content(),
        *parsed_binary.content() == *document.content()
    );

    Ok(())
}

#[cfg(feature = "secondlife")]
fn second_life_example() -> LLSDResult<()> {
    use llsd::secondlife::*;
    
    println!("\n3. Second Life Integration");
    println!("-------------------------");

    // Create agent appearance data
    let agent_id = Uuid::new_v4();
    let appearance = SecondLifeLLSDUtils::create_agent_appearance(
        agent_id,
        12345,
        false,
        vec![LLSDValue::String("hair_attachment".to_string())],
        vec![128, 255, 64, 192], // Visual params
        vec![LLSDValue::UUID(Uuid::new_v4())],
    );

    println!("Created agent appearance for: {}", agent_id);
    println!("Appearance data keys: {:?}", appearance.keys().collect::<Vec<_>>());

    // Create chat message
    let chat_message = SecondLifeLLSDUtils::create_chat_message(
        "TestUser",
        1, // Source type
        0, // Chat type
        "Hello, Second Life!",
        Some([128.0, 128.0, 25.0]),
        Some(Uuid::new_v4()),
    );

    println!("Chat message: {}", 
        chat_message.get("message").unwrap().as_string().unwrap_or(""));

    // Validation example
    let rules = SLValidationRules::new()
        .require_map()
        .require_field("message", Some("string"))
        .require_field("from_name", Some("string"));

    let validation_result = validate_sl_structure(
        &LLSDValue::Map(chat_message.clone()),
        &rules
    );

    println!("Validation passed: {}", validation_result.is_valid());
    if !validation_result.warnings().is_empty() {
        println!("Warnings: {:?}", validation_result.warnings());
    }

    Ok(())
}

#[cfg(feature = "firestorm")]
fn firestorm_example() -> LLSDResult<()> {
    use llsd::firestorm::*;
    
    println!("\n4. Firestorm Extensions");
    println!("----------------------");

    // RLV Command
    let rlv_command = RLVCommand::new("@sit", "ground", "=force", Uuid::new_v4());
    println!("RLV Command: {}", rlv_command.to_string());

    // Performance stats
    let perf_stats = FirestormLLSDUtils::create_performance_stats(
        60.0,   // fps
        1024.0, // bandwidth
        2048.0, // memory_usage
        16.67,  // render_time
        5.2,    // script_time
        150000, // triangles
    );

    println!("Performance stats created with {} triangles", 
        perf_stats.get("triangles").unwrap().as_integer().unwrap_or(0));

    // Cache demonstration
    let cache = FSLLSDCache::new(5000); // 5 second TTL
    cache.put("user_data", LLSDValue::String("cached_value".to_string()));
    
    if let Some(cached) = cache.get("user_data") {
        println!("Retrieved from cache: {}", cached.as_string().unwrap_or(""));
    }

    println!("Cache size: {}", cache.size());

    // Version compatibility
    let is_compatible = FirestormLLSDUtils::is_compatible_version("6.5.2", "6.0.0");
    println!("Version 6.5.2 compatible with 6.0.0: {}", is_compatible);

    Ok(())
}

fn advanced_features_example() -> LLSDResult<()> {
    println!("\n5. Advanced Features");
    println!("-------------------");

    let complex_data = create_complex_data();

    // Structure analysis
    let element_count = LLSDUtils::count_elements(&complex_data);
    let max_depth = LLSDUtils::max_depth(&complex_data);
    
    println!("Complex structure: {} elements, depth {}", element_count, max_depth);

    // Constraint validation
    match LLSDUtils::validate_constraints(&complex_data, 10, 1000) {
        Ok(_) => println!("Structure within constraints"),
        Err(e) => println!("Constraint violation: {}", e),
    }

    // Deep cloning
    let cloned_data = LLSDUtils::deep_clone(&complex_data);
    let are_equal = complex_data == cloned_data;
    println!("Deep clone successful: {}", are_equal);

    // Debug representation
    let debug_string = LLSDUtils::to_debug_string(&complex_data, 0);
    println!("Debug representation:");
    println!("{}", &debug_string[..200.min(debug_string.len())]);
    if debug_string.len() > 200 { println!("..."); }

    // Map operations
    let mut map1 = HashMap::new();
    map1.insert("a".to_string(), LLSDValue::Integer(1));
    map1.insert("nested".to_string(), LLSDValue::Map({
        let mut inner = HashMap::new();
        inner.insert("x".to_string(), LLSDValue::String("original".to_string()));
        inner
    }));

    let mut map2 = HashMap::new();
    map2.insert("b".to_string(), LLSDValue::Integer(2));
    map2.insert("nested".to_string(), LLSDValue::Map({
        let mut inner = HashMap::new();
        inner.insert("y".to_string(), LLSDValue::String("merged".to_string()));
        inner
    }));

    LLSDUtils::merge_maps(&mut map1, &map2);
    println!("Map merge completed, keys: {:?}", map1.keys().collect::<Vec<_>>());

    Ok(())
}

fn create_sample_data() -> LLSDValue {
    let mut data = HashMap::new();
    
    data.insert("application".to_string(), LLSDValue::String("LLSD Rust Example".to_string()));
    data.insert("version".to_string(), LLSDValue::String("1.0.0".to_string()));
    data.insert("timestamp".to_string(), LLSDValue::Date(Utc::now()));
    
    // User info
    let mut user = HashMap::new();
    user.insert("id".to_string(), LLSDValue::UUID(Uuid::new_v4()));
    user.insert("name".to_string(), LLSDValue::String("Demo User".to_string()));
    user.insert("level".to_string(), LLSDValue::Integer(42));
    user.insert("experience".to_string(), LLSDValue::Real(15750.5));
    user.insert("active".to_string(), LLSDValue::Boolean(true));
    
    data.insert("user".to_string(), LLSDValue::Map(user));
    
    // Settings array
    data.insert("settings".to_string(), LLSDValue::Array(vec![
        LLSDValue::String("auto_save".to_string()),
        LLSDValue::String("notifications".to_string()),
        LLSDValue::String("debug_mode".to_string()),
    ]));

    // Binary data
    data.insert("signature".to_string(), LLSDValue::Binary(
        b"LLSD_RUST_SIGNATURE_DATA".to_vec()
    ));
    
    LLSDValue::Map(data)
}

fn create_complex_data() -> LLSDValue {
    let mut root = HashMap::new();
    
    // Multi-level nesting
    for i in 0..5 {
        let level1_key = format!("branch_{}", i);
        let mut level1 = HashMap::new();
        
        for j in 0..3 {
            let level2_key = format!("node_{}", j);
            let level2_data = LLSDValue::Array(vec![
                LLSDValue::Integer(i * 10 + j),
                LLSDValue::String(format!("data_{}_{}", i, j)),
                LLSDValue::Boolean(i % 2 == 0),
            ]);
            level1.insert(level2_key, level2_data);
        }
        
        root.insert(level1_key, LLSDValue::Map(level1));
    }
    
    LLSDValue::Map(root)
}