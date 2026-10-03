# LLSD Rust Implementation

A complete Rust implementation of LLSD (Linden Lab Structured Data) format with Second Life and Firestorm viewer extensions.

## Features

- **Complete LLSD Support**: All LLSD data types including undefined, boolean, integer, real, string, UUID, date, URI, binary, array, and map
- **Multiple Formats**: XML, JSON, and binary serialization/parsing
- **Second Life Integration**: Specialized utilities for Second Life viewer protocols
- **Firestorm Extensions**: Enhanced features including RLV support, radar, bridge communication, and performance monitoring
- **Type Safety**: Full Rust type safety with proper error handling
- **Performance**: Optimized for speed with comprehensive benchmarks
- **Memory Safety**: No unsafe code, leveraging Rust's memory safety guarantees

## Installation

Add this to your `Cargo.toml`:

```toml
[dependencies]
llsd = "1.0.0"

# Enable optional features
[features]
secondlife = []
firestorm = ["secondlife"]
```

## Quick Start

```rust
use llsd::*;
use std::collections::HashMap;
use uuid::Uuid;

fn main() -> LLSDResult<()> {
    // Create LLSD data
    let mut data = HashMap::new();
    data.insert("name".to_string(), LLSDValue::String("Alice".to_string()));
    data.insert("age".to_string(), LLSDValue::Integer(30));
    data.insert("id".to_string(), LLSDValue::UUID(Uuid::new_v4()));
    
    let document = LLSDDocument::new(LLSDValue::Map(data));
    
    // Serialize to different formats
    let json = LLSDFactory::serialize_json(&document, true)?;
    let xml = LLSDFactory::serialize_xml(&document, true)?;
    let binary = LLSDFactory::serialize_binary(&document)?;
    
    // Parse back from any format
    let from_json = LLSDFactory::parse_json(&json)?;
    let from_xml = LLSDFactory::parse_xml(&xml)?;
    let from_binary = LLSDFactory::parse_binary(&binary)?;
    
    // Safe data access
    let name = LLSDUtils::get_string(document.content(), "name", "Unknown");
    let age = LLSDUtils::get_integer(document.content(), "age", 0);
    
    println!("User: {} (age: {})", name, age);
    
    Ok(())
}
```

## Data Types

LLSD supports these native types:

```rust
pub enum LLSDValue {
    Undefined,                      // null/undefined
    Boolean(bool),                  // true/false
    Integer(i32),                   // 32-bit signed integer
    Real(f64),                      // 64-bit floating point
    String(String),                 // UTF-8 string
    UUID(Uuid),                     // RFC 4122 UUID
    Date(DateTime<Utc>),            // UTC timestamp
    URI(String),                    // URI string
    Binary(Vec<u8>),               // binary data
    Map(HashMap<String, LLSDValue>), // key-value map
    Array(Vec<LLSDValue>),          // ordered array
}
```

## Working with Data

### Creating LLSD Values

```rust
// From primitive types
let bool_val: LLSDValue = true.into();
let int_val: LLSDValue = 42.into();
let string_val: LLSDValue = "hello".into();
let uuid_val: LLSDValue = Uuid::new_v4().into();

// Complex structures
let mut map = HashMap::new();
map.insert("users".to_string(), LLSDValue::Array(vec![
    LLSDValue::String("Alice".to_string()),
    LLSDValue::String("Bob".to_string()),
]));
let document = LLSDDocument::new(LLSDValue::Map(map));
```

### Accessing Data Safely

```rust
// Direct type checking
match document.content() {
    LLSDValue::Map(map) => {
        if let Some(LLSDValue::String(name)) = map.get("name") {
            println!("Name: {}", name);
        }
    }
    _ => {}
}

// Utility functions with defaults
let name = LLSDUtils::get_string(document.content(), "name", "Unknown");
let age = LLSDUtils::get_integer(document.content(), "age", 0);
let active = LLSDUtils::get_boolean(document.content(), "active", false);

// Path-based navigation
let nested_value = document.content().get_path("user.profile.name");
```

## Format Support

### JSON

```rust
let json_parser = LLSDJsonParser::new()
    .with_strict_uuid_parsing(true)
    .with_type_preservation(true);

let document = json_parser.parse(r#"{"name": "Alice", "age": 30}"#)?;

let json_serializer = LLSDJsonSerializer::new()
    .with_pretty_print(true);

let json_string = json_serializer.serialize(&document)?;
```

### XML

```rust
let xml_parser = LLSDXmlParser::new()
    .with_validation(true);

let document = xml_parser.parse(r#"
    <?xml version="1.0"?>
    <llsd>
        <map>
            <key>name</key><string>Alice</string>
            <key>age</key><integer>30</integer>
        </map>
    </llsd>
"#)?;

let xml_serializer = LLSDXmlSerializer::new()
    .with_pretty_print(true)
    .with_indent_size(2);

let xml_string = xml_serializer.serialize(&document)?;
```

### Binary

```rust
let binary_parser = LLSDBinaryParser::new()
    .with_max_depth(1000)
    .with_max_elements(100000);

let document = binary_parser.parse(&binary_data)?;

let binary_serializer = LLSDBinarySerializer::new();
let binary_data = binary_serializer.serialize(&document)?;
```

## Second Life Integration

```rust
use llsd::secondlife::*;

// Agent appearance
let appearance = SecondLifeLLSDUtils::create_agent_appearance(
    agent_id,
    serial_number,
    is_trial,
    attachments,
    visual_params,
    texture_hashes,
);

// Chat messages
let chat = SecondLifeLLSDUtils::create_chat_message(
    "Username",
    1,      // source_type
    0,      // chat_type
    "Hello!",
    Some([128.0, 128.0, 25.0]), // position
    Some(owner_id),
);

// Validation
let rules = SLValidationRules::new()
    .require_map()
    .require_field("message", Some("string"));

let result = validate_sl_structure(&llsd_data, &rules);
```

## Firestorm Extensions

```rust
use llsd::firestorm::*;

// RLV Commands
let rlv_cmd = RLVCommand::new("@sit", "ground", "=force", source_id);
let llsd_data = rlv_cmd.to_llsd();

// Performance monitoring
let stats = FirestormLLSDUtils::create_performance_stats(
    fps, bandwidth, memory_usage, render_time, script_time, triangles
);

// Caching
let cache = FSLLSDCache::new(300000); // 5 minute TTL
cache.put("key", expensive_data);
let cached_data = cache.get("key");

// Version compatibility
let compatible = FirestormLLSDUtils::is_compatible_version("6.5.0", "6.0.0");
```

## Utilities

```rust
// Deep cloning
let cloned = LLSDUtils::deep_clone(&original_data);

// Structure analysis
let element_count = LLSDUtils::count_elements(&data);
let max_depth = LLSDUtils::max_depth(&data);

// Constraint validation
LLSDUtils::validate_constraints(&data, max_depth, max_elements)?;

// Map operations
LLSDUtils::merge_maps(&mut base_map, &overlay_map);
let filtered = LLSDUtils::filter_map(&map, &["key1", "key2"]);

// Debug formatting
let debug_string = LLSDUtils::to_debug_string(&data, 0);
```

## Performance

The Rust implementation is optimized for performance:

- Zero-copy parsing where possible
- Efficient memory management
- SIMD optimizations for binary parsing
- Comprehensive benchmarks included

Run benchmarks:

```bash
cargo bench
```

## Error Handling

Comprehensive error types with context:

```rust
match LLSDFactory::parse_json(invalid_json) {
    Ok(document) => { /* success */ },
    Err(LLSDError::JsonError(e)) => println!("JSON error: {}", e),
    Err(LLSDError::ValidationError { message }) => println!("Validation failed: {}", message),
    Err(e) => println!("Other error: {}", e),
}
```

## Testing

Run the complete test suite:

```bash
# Basic tests
cargo test

# With all features
cargo test --all-features

# Integration tests
cargo test --test integration_tests

# Documentation tests
cargo test --doc
```

## Examples

See the `examples/` directory for comprehensive usage examples:

```bash
# Basic usage
cargo run --example basic_usage

# With Second Life features
cargo run --example basic_usage --features secondlife

# With all features
cargo run --example basic_usage --features "secondlife,firestorm"
```

## License

This project maintains compatibility with the original LLSD licensing terms.

## Contributing

Contributions welcome! Please ensure:

- All tests pass: `cargo test --all-features`
- Code is formatted: `cargo fmt`
- No clippy warnings: `cargo clippy --all-features`
- Documentation is updated as needed