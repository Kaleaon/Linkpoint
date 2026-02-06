/*!
 * Firestorm LLSD Extensions - Rust Implementation
 * 
 * Based on Java implementation and Firestorm viewer functionality
 * Copyright (C) 2024 Linden Lab
 */

use crate::types::LLSDValue;
use crate::utils::LLSDUtils;
#[cfg(feature = "secondlife")]
use crate::secondlife::{SLValidationRules, ValidationResult, validate_sl_structure};
use std::collections::HashMap;
use std::sync::{Arc, Mutex};
use std::time::{Duration, Instant};
use uuid::Uuid;
use chrono::{DateTime, Utc};

/// Firestorm specific LLSD utilities
pub struct FirestormLLSDUtils;

impl FirestormLLSDUtils {
    /// Create enhanced radar data structure
    pub fn create_radar_data(
        agent_id: Uuid,
        display_name: &str,
        user_name: &str,
        position: [f64; 3],
        distance: f64,
        is_typing: bool,
        attachments: Vec<LLSDValue>,
    ) -> HashMap<String, LLSDValue> {
        let mut radar_data = HashMap::new();
        
        radar_data.insert("agent_id".to_string(), LLSDValue::UUID(agent_id));
        radar_data.insert("display_name".to_string(), LLSDValue::String(display_name.to_string()));
        radar_data.insert("user_name".to_string(), LLSDValue::String(user_name.to_string()));
        radar_data.insert("position".to_string(), LLSDValue::Array(vec![
            LLSDValue::Real(position[0]),
            LLSDValue::Real(position[1]),
            LLSDValue::Real(position[2]),
        ]));
        radar_data.insert("distance".to_string(), LLSDValue::Real(distance));
        radar_data.insert("is_typing".to_string(), LLSDValue::Boolean(is_typing));
        radar_data.insert("attachments".to_string(), LLSDValue::Array(attachments));
        radar_data.insert("last_seen".to_string(), LLSDValue::Real(Utc::now().timestamp() as f64));
        radar_data.insert("radar_version".to_string(), LLSDValue::String("6.0.0".to_string()));
        
        radar_data
    }

    /// Create bridge communication message
    pub fn create_bridge_message(
        command: &str,
        parameters: HashMap<String, LLSDValue>,
        request_id: Uuid,
        priority: i32,
    ) -> HashMap<String, LLSDValue> {
        let mut message = HashMap::new();
        
        message.insert("command".to_string(), LLSDValue::String(command.to_string()));
        message.insert("parameters".to_string(), LLSDValue::Map(parameters));
        message.insert("request_id".to_string(), LLSDValue::UUID(request_id));
        message.insert("priority".to_string(), LLSDValue::Integer(priority));
        message.insert("bridge_version".to_string(), LLSDValue::String("6.0.0".to_string()));
        message.insert("timestamp".to_string(), LLSDValue::Real(Utc::now().timestamp() as f64));
        
        message
    }

    /// Create performance statistics structure
    pub fn create_performance_stats(
        fps: f64,
        bandwidth: f64,
        memory_usage: f64,
        render_time: f64,
        script_time: f64,
        triangles: i32,
    ) -> HashMap<String, LLSDValue> {
        let mut stats = HashMap::new();
        
        stats.insert("fps".to_string(), LLSDValue::Real(fps));
        stats.insert("bandwidth".to_string(), LLSDValue::Real(bandwidth));
        stats.insert("memory_usage".to_string(), LLSDValue::Real(memory_usage));
        stats.insert("render_time".to_string(), LLSDValue::Real(render_time));
        stats.insert("script_time".to_string(), LLSDValue::Real(script_time));
        stats.insert("triangles".to_string(), LLSDValue::Integer(triangles));
        stats.insert("firestorm_version".to_string(), LLSDValue::String("6.0.0".to_string()));
        stats.insert("timestamp".to_string(), LLSDValue::Real(Utc::now().timestamp() as f64));
        
        stats
    }

    /// Create enhanced particle system data
    #[allow(clippy::too_many_arguments)]
    pub fn create_enhanced_particle_system(
        source_id: Uuid,
        owner_key: Uuid,
        pattern: i32,
        max_age: f64,
        start_age: f64,
        inner_angle: f64,
        outer_angle: f64,
        burst_rate: f64,
        burst_part_count: i32,
        burst_speed_min: f64,
        burst_speed_max: f64,
        burst_radius: f64,
        accel: [f64; 3],
        texture_uuid: Uuid,
        target_uuid: Uuid,
        particle_flags: i32,
        start_color: [f64; 4],
        end_color: [f64; 4],
        start_scale: [f64; 2],
        end_scale: [f64; 2],
    ) -> HashMap<String, LLSDValue> {
        let mut particle_system = HashMap::new();
        
        particle_system.insert("source_id".to_string(), LLSDValue::UUID(source_id));
        particle_system.insert("owner_key".to_string(), LLSDValue::UUID(owner_key));
        particle_system.insert("pattern".to_string(), LLSDValue::Integer(pattern));
        particle_system.insert("max_age".to_string(), LLSDValue::Real(max_age));
        particle_system.insert("start_age".to_string(), LLSDValue::Real(start_age));
        particle_system.insert("inner_angle".to_string(), LLSDValue::Real(inner_angle));
        particle_system.insert("outer_angle".to_string(), LLSDValue::Real(outer_angle));
        particle_system.insert("burst_rate".to_string(), LLSDValue::Real(burst_rate));
        particle_system.insert("burst_part_count".to_string(), LLSDValue::Integer(burst_part_count));
        particle_system.insert("burst_speed_min".to_string(), LLSDValue::Real(burst_speed_min));
        particle_system.insert("burst_speed_max".to_string(), LLSDValue::Real(burst_speed_max));
        particle_system.insert("burst_radius".to_string(), LLSDValue::Real(burst_radius));
        
        particle_system.insert("accel".to_string(), LLSDValue::Array(vec![
            LLSDValue::Real(accel[0]),
            LLSDValue::Real(accel[1]),
            LLSDValue::Real(accel[2]),
        ]));
        
        particle_system.insert("texture_uuid".to_string(), LLSDValue::UUID(texture_uuid));
        particle_system.insert("target_uuid".to_string(), LLSDValue::UUID(target_uuid));
        particle_system.insert("particle_flags".to_string(), LLSDValue::Integer(particle_flags));
        
        particle_system.insert("start_color".to_string(), LLSDValue::Array(vec![
            LLSDValue::Real(start_color[0]),
            LLSDValue::Real(start_color[1]),
            LLSDValue::Real(start_color[2]),
            LLSDValue::Real(start_color[3]),
        ]));
        
        particle_system.insert("end_color".to_string(), LLSDValue::Array(vec![
            LLSDValue::Real(end_color[0]),
            LLSDValue::Real(end_color[1]),
            LLSDValue::Real(end_color[2]),
            LLSDValue::Real(end_color[3]),
        ]));
        
        particle_system.insert("start_scale".to_string(), LLSDValue::Array(vec![
            LLSDValue::Real(start_scale[0]),
            LLSDValue::Real(start_scale[1]),
        ]));
        
        particle_system.insert("end_scale".to_string(), LLSDValue::Array(vec![
            LLSDValue::Real(end_scale[0]),
            LLSDValue::Real(end_scale[1]),
        ]));
        
        particle_system.insert("firestorm_enhanced".to_string(), LLSDValue::Boolean(true));
        
        particle_system
    }

    /// Check if a Firestorm version is compatible with minimum required version
    pub fn is_compatible_version(version: &str, min_version: &str) -> bool {
        if version.is_empty() || min_version.is_empty() {
            return false;
        }

        let parse_version = |v: &str| -> Vec<u32> {
            v.split('.')
                .map(|part| {
                    part.chars()
                        .filter(|c| c.is_ascii_digit())
                        .collect::<String>()
                        .parse::<u32>()
                        .unwrap_or(0)
                })
                .collect()
        };

        let version_parts = parse_version(version);
        let min_parts = parse_version(min_version);
        let max_length = version_parts.len().max(min_parts.len());

        for i in 0..max_length {
            let v = version_parts.get(i).copied().unwrap_or(0);
            let m = min_parts.get(i).copied().unwrap_or(0);
            
            match v.cmp(&m) {
                std::cmp::Ordering::Greater => return true,
                std::cmp::Ordering::Less => return false,
                std::cmp::Ordering::Equal => continue,
            }
        }

        true // Equal versions are compatible
    }

    /// Deep copy LLSD structures (alias to LLSDUtils::deep_clone for API consistency)
    pub fn deep_copy(data: &LLSDValue) -> LLSDValue {
        LLSDUtils::deep_clone(data)
    }
}

/// RLV (Restrained Life Viewer) command structure
#[derive(Debug, Clone)]
pub struct RLVCommand {
    behaviour: String,
    option: String,
    param: String,
    source_id: Uuid,
}

impl RLVCommand {
    /// Create a new RLV command
    pub fn new(behaviour: &str, option: &str, param: &str, source_id: Uuid) -> Self {
        Self {
            behaviour: behaviour.to_string(),
            option: option.to_string(),
            param: param.to_string(),
            source_id,
        }
    }

    /// Convert RLV command to LLSD
    pub fn to_llsd(&self) -> HashMap<String, LLSDValue> {
        let mut llsd = HashMap::new();
        
        llsd.insert("behaviour".to_string(), LLSDValue::String(self.behaviour.clone()));
        llsd.insert("option".to_string(), LLSDValue::String(self.option.clone()));
        llsd.insert("param".to_string(), LLSDValue::String(self.param.clone()));
        llsd.insert("source_id".to_string(), LLSDValue::UUID(self.source_id));
        llsd.insert("timestamp".to_string(), LLSDValue::Real(Utc::now().timestamp() as f64));
        
        llsd
    }

    /// Convert RLV command to string representation
    pub fn to_string(&self) -> String {
        if self.option.is_empty() {
            format!("{}{}", self.behaviour, self.param)
        } else {
            format!("{}:{}{}", self.behaviour, self.option, self.param)
        }
    }
}

/// Firestorm-specific validation rules extending base SL rules
#[cfg(feature = "secondlife")]
#[derive(Debug, Clone)]
pub struct FSValidationRules {
    base_rules: SLValidationRules,
    requires_fs_version: bool,
    min_fs_version: String,
    requires_rlv: bool,
    requires_bridge: bool,
}

#[cfg(feature = "secondlife")]
impl FSValidationRules {
    /// Create new Firestorm validation rules
    pub fn new() -> Self {
        Self {
            base_rules: SLValidationRules::new(),
            requires_fs_version: false,
            min_fs_version: String::new(),
            requires_rlv: false,
            requires_bridge: false,
        }
    }

    /// Require the root to be a map
    pub fn require_map(mut self) -> Self {
        self.base_rules = self.base_rules.require_map();
        self
    }

    /// Require the root to be an array
    pub fn require_array(mut self) -> Self {
        self.base_rules = self.base_rules.require_array();
        self
    }

    /// Require a specific field to be present
    pub fn require_field(mut self, name: &str, field_type: Option<&str>) -> Self {
        self.base_rules = self.base_rules.require_field(name, field_type);
        self
    }

    /// Require Firestorm version
    pub fn require_fs_version(mut self, min_version: &str) -> Self {
        self.requires_fs_version = true;
        self.min_fs_version = min_version.to_string();
        self
    }

    /// Require RLV support
    pub fn require_rlv(mut self) -> Self {
        self.requires_rlv = true;
        self
    }

    /// Require bridge connection
    pub fn require_bridge(mut self) -> Self {
        self.requires_bridge = true;
        self
    }

    /// Get the base SL validation rules
    pub fn base_rules(&self) -> &SLValidationRules {
        &self.base_rules
    }

    /// Check if Firestorm version is required
    pub fn requires_fs_version(&self) -> bool {
        self.requires_fs_version
    }

    /// Get minimum Firestorm version
    pub fn min_fs_version(&self) -> &str {
        &self.min_fs_version
    }

    /// Check if RLV is required
    pub fn requires_rlv(&self) -> bool {
        self.requires_rlv
    }

    /// Check if bridge is required
    pub fn requires_bridge(&self) -> bool {
        self.requires_bridge
    }
}

#[cfg(feature = "secondlife")]
impl Default for FSValidationRules {
    fn default() -> Self {
        Self::new()
    }
}

/// Firestorm validation result extending base SL result
#[cfg(feature = "secondlife")]
pub type FSValidationResult = ValidationResult;

/// Validate Firestorm-specific LLSD structure
#[cfg(feature = "secondlife")]
pub fn validate_fs_structure(llsd_data: &LLSDValue, rules: &FSValidationRules) -> FSValidationResult {
    let mut result = validate_sl_structure(llsd_data, rules.base_rules());

    if !result.is_valid() {
        return result; // Don't continue if base validation failed
    }

    // Firestorm-specific validations
    if let LLSDValue::Map(map) = llsd_data {
        // Check Firestorm version if required
        if rules.requires_fs_version() {
            let version = map.get("firestorm_version")
                .or_else(|| map.get("viewer_version"))
                .or_else(|| map.get("ViewerVersion"));

            match version {
                Some(LLSDValue::String(v)) => {
                    if !FirestormLLSDUtils::is_compatible_version(v, rules.min_fs_version()) {
                        result.add_error(format!(
                            "Incompatible Firestorm version: {}, required: {}",
                            v, rules.min_fs_version()
                        ));
                    }
                }
                _ => {
                    result.add_error("Missing Firestorm version information".to_string());
                }
            }
        }

        // Check RLV support if required
        if rules.requires_rlv() {
            let rlv_enabled = map.get("rlv_enabled").or_else(|| map.get("RLVEnabled"));
            if !matches!(rlv_enabled, Some(LLSDValue::Boolean(true))) {
                result.add_warning("RLV support is required but not enabled".to_string());
            }
        }

        // Check bridge connection if required
        if rules.requires_bridge() {
            let bridge_connected = map.get("bridge_connected").or_else(|| map.get("BridgeConnected"));
            if !matches!(bridge_connected, Some(LLSDValue::Boolean(true))) {
                result.add_warning("Bridge connection is required but not established".to_string());
            }
        }
    }

    result
}

/// Thread-safe caching for performance
pub struct FSLLSDCache {
    cache: Arc<Mutex<HashMap<String, CacheEntry>>>,
    ttl: Duration,
}

#[derive(Debug, Clone)]
struct CacheEntry {
    data: LLSDValue,
    timestamp: Instant,
}

impl FSLLSDCache {
    /// Create a new cache with TTL in milliseconds
    pub fn new(ttl_ms: u64) -> Self {
        Self {
            cache: Arc::new(Mutex::new(HashMap::new())),
            ttl: Duration::from_millis(ttl_ms),
        }
    }

    /// Put data into the cache
    pub fn put(&self, key: &str, data: LLSDValue) {
        let entry = CacheEntry {
            data: LLSDUtils::deep_clone(&data),
            timestamp: Instant::now(),
        };

        if let Ok(mut cache) = self.cache.lock() {
            cache.insert(key.to_string(), entry);
        }
    }

    /// Get data from the cache
    pub fn get(&self, key: &str) -> Option<LLSDValue> {
        if let Ok(mut cache) = self.cache.lock() {
            if let Some(entry) = cache.get(key) {
                if entry.timestamp.elapsed() < self.ttl {
                    return Some(LLSDUtils::deep_clone(&entry.data));
                } else {
                    // Remove expired entry
                    cache.remove(key);
                }
            }
        }
        None
    }

    /// Clear all cached data
    pub fn clear(&self) {
        if let Ok(mut cache) = self.cache.lock() {
            cache.clear();
        }
    }

    /// Get the current cache size
    pub fn size(&self) -> usize {
        if let Ok(cache) = self.cache.lock() {
            cache.len()
        } else {
            0
        }
    }

    /// Remove expired entries
    pub fn cleanup(&self) {
        if let Ok(mut cache) = self.cache.lock() {
            let now = Instant::now();
            cache.retain(|_, entry| now.duration_since(entry.timestamp) < self.ttl);
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use uuid::uuid;
    use std::thread;

    #[test]
    fn test_create_radar_data() {
        let agent_id = uuid!("550e8400-e29b-41d4-a716-446655440000");
        let radar_data = FirestormLLSDUtils::create_radar_data(
            agent_id,
            "Test User",
            "testuser.resident",
            [128.0, 128.0, 25.0],
            15.5,
            false,
            vec![LLSDValue::String("attachment".to_string())],
        );

        assert_eq!(radar_data["agent_id"], LLSDValue::UUID(agent_id));
        assert_eq!(radar_data["display_name"], LLSDValue::String("Test User".to_string()));
        assert_eq!(radar_data["distance"], LLSDValue::Real(15.5));
        assert_eq!(radar_data["is_typing"], LLSDValue::Boolean(false));
        assert_eq!(radar_data["radar_version"], LLSDValue::String("6.0.0".to_string()));
    }

    #[test]
    fn test_create_bridge_message() {
        let request_id = Uuid::new_v4();
        let parameters = {
            let mut params = HashMap::new();
            params.insert("target".to_string(), LLSDValue::String("avatar".to_string()));
            params
        };

        let message = FirestormLLSDUtils::create_bridge_message(
            "get_avatar_data",
            parameters.clone(),
            request_id,
            2,
        );

        assert_eq!(message["command"], LLSDValue::String("get_avatar_data".to_string()));
        assert_eq!(message["request_id"], LLSDValue::UUID(request_id));
        assert_eq!(message["priority"], LLSDValue::Integer(2));
        assert_eq!(message["bridge_version"], LLSDValue::String("6.0.0".to_string()));
        
        if let LLSDValue::Map(params_map) = &message["parameters"] {
            assert_eq!(params_map["target"], LLSDValue::String("avatar".to_string()));
        } else {
            panic!("Expected parameters map");
        }
    }

    #[test]
    fn test_rlv_command() {
        let source_id = uuid!("550e8400-e29b-41d4-a716-446655440000");
        let command = RLVCommand::new("@sit", "ground", "=force", source_id);

        let llsd_data = command.to_llsd();
        assert_eq!(llsd_data["behaviour"], LLSDValue::String("@sit".to_string()));
        assert_eq!(llsd_data["option"], LLSDValue::String("ground".to_string()));
        assert_eq!(llsd_data["param"], LLSDValue::String("=force".to_string()));
        assert_eq!(llsd_data["source_id"], LLSDValue::UUID(source_id));

        assert_eq!(command.to_string(), "@sit:ground=force");
    }

    #[test]
    fn test_version_compatibility() {
        assert!(FirestormLLSDUtils::is_compatible_version("6.5.0", "6.0.0"));
        assert!(FirestormLLSDUtils::is_compatible_version("6.0.0", "6.0.0"));
        assert!(!FirestormLLSDUtils::is_compatible_version("5.9.0", "6.0.0"));
        assert!(!FirestormLLSDUtils::is_compatible_version("", "6.0.0"));
        assert!(!FirestormLLSDUtils::is_compatible_version("6.0.0", ""));

        // Test with build numbers
        assert!(FirestormLLSDUtils::is_compatible_version("6.0.1.123", "6.0.0"));
        assert!(FirestormLLSDUtils::is_compatible_version("6.0.0.456", "6.0.0.123"));
    }

    #[test]
    fn test_performance_stats() {
        let stats = FirestormLLSDUtils::create_performance_stats(
            60.0, 500.0, 1024.0, 16.67, 5.2, 150000
        );

        assert_eq!(stats["fps"], LLSDValue::Real(60.0));
        assert_eq!(stats["bandwidth"], LLSDValue::Real(500.0));
        assert_eq!(stats["triangles"], LLSDValue::Integer(150000));
        assert_eq!(stats["firestorm_version"], LLSDValue::String("6.0.0".to_string()));
    }

    #[test]
    fn test_cache_operations() {
        let cache = FSLLSDCache::new(100); // 100ms TTL
        let test_data = LLSDValue::String("test_value".to_string());

        // Test put and get
        cache.put("test_key", test_data.clone());
        assert_eq!(cache.get("test_key"), Some(test_data.clone()));
        assert_eq!(cache.size(), 1);

        // Test non-existent key
        assert_eq!(cache.get("non_existent"), None);

        // Test expiration
        thread::sleep(std::time::Duration::from_millis(150));
        assert_eq!(cache.get("test_key"), None);
        assert_eq!(cache.size(), 0); // Should be removed after access

        // Test clear
        cache.put("key1", LLSDValue::Integer(1));
        cache.put("key2", LLSDValue::Integer(2));
        assert_eq!(cache.size(), 2);
        cache.clear();
        assert_eq!(cache.size(), 0);
    }

    #[cfg(feature = "secondlife")]
    #[test]
    fn test_fs_validation() {
        let rules = FSValidationRules::new()
            .require_map()
            .require_fs_version("6.0.0")
            .require_field("command", Some("string"));

        // Valid data
        let valid_data = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("command".to_string(), LLSDValue::String("test".to_string()));
            map.insert("firestorm_version".to_string(), LLSDValue::String("6.0.0".to_string()));
            map
        });

        let result = validate_fs_structure(&valid_data, &rules);
        assert!(result.is_valid());

        // Invalid version
        let invalid_data = LLSDValue::Map({
            let mut map = HashMap::new();
            map.insert("command".to_string(), LLSDValue::String("test".to_string()));
            map.insert("firestorm_version".to_string(), LLSDValue::String("5.9.0".to_string()));
            map
        });

        let result = validate_fs_structure(&invalid_data, &rules);
        assert!(!result.is_valid());
        assert!(result.errors().iter().any(|e| e.contains("Incompatible")));
    }

    #[test]
    fn test_enhanced_particle_system() {
        let source_id = uuid!("550e8400-e29b-41d4-a716-446655440000");
        let owner_key = uuid!("550e8400-e29b-41d4-a716-446655440001");
        let texture_uuid = uuid!("550e8400-e29b-41d4-a716-446655440002");
        let target_uuid = uuid!("550e8400-e29b-41d4-a716-446655440003");

        let particle_system = FirestormLLSDUtils::create_enhanced_particle_system(
            source_id,
            owner_key,
            1,             // pattern
            10.0,          // max_age
            0.0,           // start_age
            0.1, 0.2,      // angles
            1.0, 10,       // burst_rate, burst_part_count
            1.0, 2.0, 5.0, // burst speeds and radius
            [0.0, 0.0, -9.8], // acceleration
            texture_uuid,
            target_uuid,
            0x01,          // flags
            [1.0, 1.0, 1.0, 1.0], // start_color
            [0.0, 0.0, 0.0, 0.0], // end_color
            [1.0, 1.0],    // start_scale
            [0.5, 0.5],    // end_scale
        );

        assert_eq!(particle_system["source_id"], LLSDValue::UUID(source_id));
        assert_eq!(particle_system["owner_key"], LLSDValue::UUID(owner_key));
        assert_eq!(particle_system["pattern"], LLSDValue::Integer(1));
        assert_eq!(particle_system["firestorm_enhanced"], LLSDValue::Boolean(true));

        // Check arrays
        if let LLSDValue::Array(accel) = &particle_system["accel"] {
            assert_eq!(accel.len(), 3);
            assert_eq!(accel[2], LLSDValue::Real(-9.8));
        } else {
            panic!("Expected acceleration array");
        }

        if let LLSDValue::Array(start_color) = &particle_system["start_color"] {
            assert_eq!(start_color.len(), 4);
            assert_eq!(start_color[0], LLSDValue::Real(1.0));
        } else {
            panic!("Expected start_color array");
        }
    }
}