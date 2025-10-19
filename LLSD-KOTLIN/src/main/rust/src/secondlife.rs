/*!
 * Second Life LLSD Extensions - Rust Implementation
 * 
 * Based on Java implementation and Second Life viewer functionality
 * Copyright (C) 2024 Linden Lab
 */

use crate::types::LLSDValue;
use crate::error::{LLSDError, LLSDResult};
use std::collections::HashMap;
use uuid::Uuid;
use chrono::{DateTime, Utc};

/// Second Life specific LLSD utilities
pub struct SecondLifeLLSDUtils;

impl SecondLifeLLSDUtils {
    /// Create a Second Life compatible LLSD response structure
    pub fn create_sl_response(success: bool, message: &str, data: Option<LLSDValue>) -> HashMap<String, LLSDValue> {
        let mut response = HashMap::new();
        response.insert("success".to_string(), LLSDValue::Boolean(success));
        response.insert("message".to_string(), LLSDValue::String(message.to_string()));
        
        if let Some(data_value) = data {
            response.insert("data".to_string(), data_value);
        }
        
        response
    }

    /// Validate a UUID according to Second Life standards (non-null)
    pub fn is_valid_sl_uuid(uuid: &Uuid) -> bool {
        !uuid.is_nil()
    }

    /// Create agent appearance data structure
    pub fn create_agent_appearance(
        agent_id: Uuid,
        serial_number: u32,
        is_trial: bool,
        attachments: Vec<LLSDValue>,
        visual_params: Vec<u8>,
        texture_hashes: Vec<LLSDValue>,
    ) -> HashMap<String, LLSDValue> {
        let mut appearance = HashMap::new();
        
        appearance.insert("agent_id".to_string(), LLSDValue::UUID(agent_id));
        appearance.insert("serial_number".to_string(), LLSDValue::Integer(serial_number as i32));
        appearance.insert("is_trial_account".to_string(), LLSDValue::Boolean(is_trial));
        appearance.insert("attachments".to_string(), LLSDValue::Array(attachments));
        appearance.insert("visual_params".to_string(), LLSDValue::Binary(visual_params));
        appearance.insert("texture_hashes".to_string(), LLSDValue::Array(texture_hashes));
        appearance.insert("appearance_version".to_string(), LLSDValue::Integer(1));
        appearance.insert("cof_version".to_string(), LLSDValue::Integer(1));
        
        appearance
    }

    /// Create object properties data structure
    pub fn create_object_properties(
        object_id: Uuid,
        owner_id: Uuid,
        group_id: Uuid,
        name: &str,
        description: &str,
        permissions: HashMap<String, LLSDValue>,
    ) -> HashMap<String, LLSDValue> {
        let mut properties = HashMap::new();
        
        properties.insert("object_id".to_string(), LLSDValue::UUID(object_id));
        properties.insert("owner_id".to_string(), LLSDValue::UUID(owner_id));
        properties.insert("group_id".to_string(), LLSDValue::UUID(group_id));
        properties.insert("name".to_string(), LLSDValue::String(name.to_string()));
        properties.insert("description".to_string(), LLSDValue::String(description.to_string()));
        properties.insert("permissions".to_string(), LLSDValue::Map(permissions));
        
        // Sale info
        let mut sale_info = HashMap::new();
        sale_info.insert("sale_price".to_string(), LLSDValue::Integer(0));
        sale_info.insert("sale_type".to_string(), LLSDValue::Integer(0));
        properties.insert("sale_info".to_string(), LLSDValue::Map(sale_info));
        
        properties.insert("creation_date".to_string(), LLSDValue::Date(Utc::now()));
        
        properties
    }

    /// Create asset upload request
    pub fn create_asset_upload_request(
        asset_type: &str,
        name: &str,
        description: &str,
        data: Vec<u8>,
        expected_upload_cost: i32,
    ) -> HashMap<String, LLSDValue> {
        let mut request = HashMap::new();
        
        request.insert("asset_type".to_string(), LLSDValue::String(asset_type.to_string()));
        request.insert("name".to_string(), LLSDValue::String(name.to_string()));
        request.insert("description".to_string(), LLSDValue::String(description.to_string()));
        
        // Asset resources
        let mut asset_resources = HashMap::new();
        asset_resources.insert("asset_data".to_string(), LLSDValue::Binary(data));
        request.insert("asset_resources".to_string(), LLSDValue::Map(asset_resources));
        
        request.insert("folder_id".to_string(), LLSDValue::UUID(Uuid::new_v4()));
        request.insert("inventory_type".to_string(), LLSDValue::Integer(Self::asset_type_to_inventory_type(asset_type)));
        request.insert("expected_upload_cost".to_string(), LLSDValue::Integer(expected_upload_cost));
        
        // Permissions
        request.insert("everyone_mask".to_string(), LLSDValue::Integer(0x00000000));
        request.insert("group_mask".to_string(), LLSDValue::Integer(0x00000000));
        request.insert("next_owner_mask".to_string(), LLSDValue::Integer(0x00082000));
        
        request
    }

    /// Convert asset type to inventory type
    fn asset_type_to_inventory_type(asset_type: &str) -> i32 {
        match asset_type.to_lowercase().as_str() {
            "texture" => 0,
            "sound" => 1,
            "callingcard" => 2,
            "landmark" => 3,
            "script" => 10,
            "clothing" => 5,
            "object" => 6,
            "notecard" => 7,
            "category" => 8,
            "folder" => 8,
            "rootcategory" => 9,
            "lsltext" => 10,
            "lslbyte" => 11,
            "txtr_tga" => 12,
            "bodypart" => 13,
            "trash" => 14,
            "snapshot" => 15,
            "lostandfound" => 16,
            "attachment" => 17,
            "wearable" => 18,
            "animation" => 19,
            "gesture" => 20,
            "mesh" => 22,
            _ => -1,
        }
    }

    /// Create chat message structure
    pub fn create_chat_message(
        from_name: &str,
        source_type: i32,
        chat_type: i32,
        message: &str,
        position: Option<[f64; 3]>,
        owner_id: Option<Uuid>,
    ) -> HashMap<String, LLSDValue> {
        let mut chat = HashMap::new();
        
        chat.insert("from_name".to_string(), LLSDValue::String(from_name.to_string()));
        chat.insert("source_type".to_string(), LLSDValue::Integer(source_type));
        chat.insert("chat_type".to_string(), LLSDValue::Integer(chat_type));
        chat.insert("message".to_string(), LLSDValue::String(message.to_string()));
        
        let pos = position.unwrap_or([0.0, 0.0, 0.0]);
        chat.insert("position".to_string(), LLSDValue::Array(vec![
            LLSDValue::Real(pos[0]),
            LLSDValue::Real(pos[1]),
            LLSDValue::Real(pos[2]),
        ]));
        
        let owner = owner_id.unwrap_or(Uuid::nil());
        chat.insert("owner_id".to_string(), LLSDValue::UUID(owner));
        chat.insert("audible".to_string(), LLSDValue::Real(1.0));
        chat.insert("timestamp".to_string(), LLSDValue::Real(Utc::now().timestamp() as f64));
        
        chat
    }

    /// Create sim stats structure
    pub fn create_sim_stats(
        region_id: Uuid,
        time_dilation: f64,
        sim_fps: f64,
        physics_fps: f64,
        agent_updates: i32,
        root_agents: i32,
        child_agents: i32,
        total_prims: i32,
        active_prims: i32,
        active_scripts: i32,
    ) -> HashMap<String, LLSDValue> {
        let mut stats = HashMap::new();
        
        stats.insert("region_id".to_string(), LLSDValue::UUID(region_id));
        stats.insert("time_dilation".to_string(), LLSDValue::Real(time_dilation));
        stats.insert("sim_fps".to_string(), LLSDValue::Real(sim_fps));
        stats.insert("physics_fps".to_string(), LLSDValue::Real(physics_fps));
        stats.insert("agent_updates_per_second".to_string(), LLSDValue::Integer(agent_updates));
        stats.insert("root_agents".to_string(), LLSDValue::Integer(root_agents));
        stats.insert("child_agents".to_string(), LLSDValue::Integer(child_agents));
        stats.insert("total_prims".to_string(), LLSDValue::Integer(total_prims));
        stats.insert("active_prims".to_string(), LLSDValue::Integer(active_prims));
        stats.insert("active_scripts".to_string(), LLSDValue::Integer(active_scripts));
        stats.insert("timestamp".to_string(), LLSDValue::Real(Utc::now().timestamp() as f64));
        
        stats
    }
}

/// Validation rules for Second Life LLSD structures
#[derive(Debug, Clone, Default)]
pub struct SLValidationRules {
    pub requires_map: bool,
    pub requires_array: bool,
    pub required_fields: Vec<String>,
    pub field_types: HashMap<String, String>,
}

impl SLValidationRules {
    /// Create new validation rules
    pub fn new() -> Self {
        Self::default()
    }

    /// Require the root to be a map
    pub fn require_map(mut self) -> Self {
        self.requires_map = true;
        self
    }

    /// Require the root to be an array
    pub fn require_array(mut self) -> Self {
        self.requires_array = true;
        self
    }

    /// Require a specific field to be present
    pub fn require_field(mut self, name: &str, field_type: Option<&str>) -> Self {
        self.required_fields.push(name.to_string());
        if let Some(typ) = field_type {
            self.field_types.insert(name.to_string(), typ.to_string());
        }
        self
    }
}

/// Result of LLSD validation
#[derive(Debug, Clone, Default)]
pub struct ValidationResult {
    errors: Vec<String>,
    warnings: Vec<String>,
}

impl ValidationResult {
    /// Create a new validation result
    pub fn new() -> Self {
        Self::default()
    }

    /// Add an error
    pub fn add_error(&mut self, error: String) {
        self.errors.push(error);
    }

    /// Add a warning
    pub fn add_warning(&mut self, warning: String) {
        self.warnings.push(warning);
    }

    /// Check if validation passed (no errors)
    pub fn is_valid(&self) -> bool {
        self.errors.is_empty()
    }

    /// Get all errors
    pub fn errors(&self) -> &[String] {
        &self.errors
    }

    /// Get all warnings
    pub fn warnings(&self) -> &[String] {
        &self.warnings
    }

    /// Add errors from another result
    pub fn add_errors(&mut self, errors: &[String]) {
        self.errors.extend_from_slice(errors);
    }

    /// Add warnings from another result
    pub fn add_warnings(&mut self, warnings: &[String]) {
        self.warnings.extend_from_slice(warnings);
    }
}

/// Validate Second Life LLSD structure
pub fn validate_sl_structure(llsd_data: &LLSDValue, rules: &SLValidationRules) -> ValidationResult {
    let mut result = ValidationResult::new();

    // Check root type requirements
    if rules.requires_map && !matches!(llsd_data, LLSDValue::Map(_)) {
        result.add_error(format!(
            "Expected Map but got {:?}",
            llsd_data.get_type()
        ));
        return result;
    }

    if rules.requires_array && !matches!(llsd_data, LLSDValue::Array(_)) {
        result.add_error(format!(
            "Expected Array but got {:?}",
            llsd_data.get_type()
        ));
        return result;
    }

    // Validate map structure
    if let LLSDValue::Map(map) = llsd_data {
        // Check required fields
        for field in &rules.required_fields {
            if !map.contains_key(field) {
                result.add_error(format!("Missing required field: {}", field));
            }
        }

        // Check field types
        for (field, expected_type) in &rules.field_types {
            if let Some(value) = map.get(field) {
                let actual_type = match value {
                    LLSDValue::Undefined => "undefined",
                    LLSDValue::Boolean(_) => "boolean",
                    LLSDValue::Integer(_) => "integer",
                    LLSDValue::Real(_) => "real",
                    LLSDValue::String(_) => "string",
                    LLSDValue::UUID(_) => "uuid",
                    LLSDValue::Date(_) => "date",
                    LLSDValue::URI(_) => "uri",
                    LLSDValue::Binary(_) => "binary",
                    LLSDValue::Map(_) => "map",
                    LLSDValue::Array(_) => "array",
                };

                if actual_type != expected_type {
                    result.add_warning(format!(
                        "Field {} expected {} but got {}",
                        field, expected_type, actual_type
                    ));
                }
            }
        }
    }

    result
}

#[cfg(test)]
mod tests {
    use super::*;
    use uuid::uuid;

    #[test]
    fn test_create_sl_response() {
        let response = SecondLifeLLSDUtils::create_sl_response(
            true,
            "Operation successful",
            Some(LLSDValue::Integer(42))
        );

        assert_eq!(response["success"], LLSDValue::Boolean(true));
        assert_eq!(response["message"], LLSDValue::String("Operation successful".to_string()));
        assert_eq!(response["data"], LLSDValue::Integer(42));
    }

    #[test]
    fn test_uuid_validation() {
        let null_uuid = Uuid::nil();
        let valid_uuid = uuid!("550e8400-e29b-41d4-a716-446655440000");

        assert!(!SecondLifeLLSDUtils::is_valid_sl_uuid(&null_uuid));
        assert!(SecondLifeLLSDUtils::is_valid_sl_uuid(&valid_uuid));
    }

    #[test]
    fn test_create_agent_appearance() {
        let agent_id = uuid!("550e8400-e29b-41d4-a716-446655440000");
        let appearance = SecondLifeLLSDUtils::create_agent_appearance(
            agent_id,
            123,
            false,
            vec![LLSDValue::String("attachment1".to_string())],
            vec![1, 2, 3, 4],
            vec![LLSDValue::String("texture1".to_string())],
        );

        assert_eq!(appearance["agent_id"], LLSDValue::UUID(agent_id));
        assert_eq!(appearance["serial_number"], LLSDValue::Integer(123));
        assert_eq!(appearance["is_trial_account"], LLSDValue::Boolean(false));
        assert_eq!(appearance["appearance_version"], LLSDValue::Integer(1));
        assert_eq!(appearance["cof_version"], LLSDValue::Integer(1));
    }

    #[test]
    fn test_asset_type_conversion() {
        let upload_request = SecondLifeLLSDUtils::create_asset_upload_request(
            "texture",
            "Test Texture",
            "A test texture",
            vec![1, 2, 3, 4],
            10,
        );

        assert_eq!(upload_request["asset_type"], LLSDValue::String("texture".to_string()));
        assert_eq!(upload_request["inventory_type"], LLSDValue::Integer(0)); // texture = 0
        assert_eq!(upload_request["expected_upload_cost"], LLSDValue::Integer(10));
    }

    #[test]
    fn test_chat_message_creation() {
        let chat = SecondLifeLLSDUtils::create_chat_message(
            "TestUser",
            1,
            0,
            "Hello World",
            Some([128.0, 128.0, 25.0]),
            Some(uuid!("550e8400-e29b-41d4-a716-446655440000")),
        );

        assert_eq!(chat["from_name"], LLSDValue::String("TestUser".to_string()));
        assert_eq!(chat["source_type"], LLSDValue::Integer(1));
        assert_eq!(chat["chat_type"], LLSDValue::Integer(0));
        assert_eq!(chat["message"], LLSDValue::String("Hello World".to_string()));
        assert_eq!(chat["audible"], LLSDValue::Real(1.0));
        
        if let LLSDValue::Array(pos) = &chat["position"] {
            assert_eq!(pos.len(), 3);
            assert_eq!(pos[0], LLSDValue::Real(128.0));
            assert_eq!(pos[1], LLSDValue::Real(128.0));
            assert_eq!(pos[2], LLSDValue::Real(25.0));
        } else {
            panic!("Expected position array");
        }
    }

    #[test]
    fn test_validation_rules() {
        let rules = SLValidationRules::new()
            .require_map()
            .require_field("name", Some("string"))
            .require_field("age", Some("integer"));

        // Valid data
        let valid_data = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("name".to_string(), LLSDValue::String("Alice".to_string()));
            map.insert("age".to_string(), LLSDValue::Integer(30));
            map
        });

        let result = validate_sl_structure(&valid_data, &rules);
        assert!(result.is_valid());
        assert!(result.warnings().is_empty());

        // Invalid data - missing field
        let invalid_data = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("name".to_string(), LLSDValue::String("Bob".to_string()));
            // Missing 'age' field
            map
        });

        let result = validate_sl_structure(&invalid_data, &rules);
        assert!(!result.is_valid());
        assert!(result.errors().iter().any(|e| e.contains("age")));

        // Invalid data - wrong type
        let type_mismatch_data = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("name".to_string(), LLSDValue::String("Charlie".to_string()));
            map.insert("age".to_string(), LLSDValue::String("thirty".to_string())); // Wrong type
            map
        });

        let result = validate_sl_structure(&type_mismatch_data, &rules);
        assert!(result.is_valid()); // Still valid, just a warning
        assert!(!result.warnings().is_empty());
        assert!(result.warnings().iter().any(|w| w.contains("age") && w.contains("integer")));
    }

    #[test]
    fn test_sim_stats_creation() {
        let region_id = uuid!("550e8400-e29b-41d4-a716-446655440000");
        let stats = SecondLifeLLSDUtils::create_sim_stats(
            region_id,
            1.0,   // time_dilation
            45.0,  // sim_fps
            44.9,  // physics_fps
            50,    // agent_updates
            10,    // root_agents
            5,     // child_agents
            1000,  // total_prims
            800,   // active_prims
            50,    // active_scripts
        );

        assert_eq!(stats["region_id"], LLSDValue::UUID(region_id));
        assert_eq!(stats["time_dilation"], LLSDValue::Real(1.0));
        assert_eq!(stats["sim_fps"], LLSDValue::Real(45.0));
        assert_eq!(stats["physics_fps"], LLSDValue::Real(44.9));
        assert_eq!(stats["root_agents"], LLSDValue::Integer(10));
        assert_eq!(stats["child_agents"], LLSDValue::Integer(5));
        assert!(matches!(stats["timestamp"], LLSDValue::Real(_)));
    }
}