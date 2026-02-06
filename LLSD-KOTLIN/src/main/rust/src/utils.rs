/*!
 * LLSD Utilities - Rust Implementation
 * 
 * Based on Java implementation and Second Life viewer utilities
 * Copyright (C) 2024 Linden Lab
 */

use crate::types::{LLSDValue, LLSDType};
use crate::error::{LLSDError, LLSDResult};
use uuid::Uuid;
use std::collections::HashMap;
use chrono::{DateTime, Utc};

/// Utility functions for working with LLSD data
pub struct LLSDUtils;

impl LLSDUtils {
    /// Generate a random UUID
    pub fn generate_uuid() -> Uuid {
        Uuid::new_v4()
    }

    /// Validate if a string is a valid UUID format
    pub fn is_valid_uuid(s: &str) -> bool {
        Uuid::parse_str(s).is_ok()
    }

    /// Deep clone an LLSD value
    pub fn deep_clone(value: &LLSDValue) -> LLSDValue {
        value.clone() // Rust's Clone trait already does deep cloning
    }

    /// Get a nested value using dot notation path with a default value
    pub fn get_value<'a>(root: &'a LLSDValue, path: &str, default: &'a LLSDValue) -> &'a LLSDValue {
        root.get_path(path).unwrap_or(default)
    }

    /// Safely get a string value from a path
    pub fn get_string(root: &LLSDValue, path: &str, default: &str) -> String {
        match root.get_path(path) {
            Some(LLSDValue::String(s)) => s.clone(),
            Some(LLSDValue::URI(s)) => s.clone(),
            _ => default.to_string(),
        }
    }

    /// Safely get an integer value from a path
    pub fn get_integer(root: &LLSDValue, path: &str, default: i32) -> i32 {
        match root.get_path(path) {
            Some(LLSDValue::Integer(i)) => *i,
            Some(LLSDValue::Real(r)) => *r as i32,
            _ => default,
        }
    }

    /// Safely get a real value from a path
    pub fn get_real(root: &LLSDValue, path: &str, default: f64) -> f64 {
        match root.get_path(path) {
            Some(LLSDValue::Real(r)) => *r,
            Some(LLSDValue::Integer(i)) => *i as f64,
            _ => default,
        }
    }

    /// Safely get a boolean value from a path
    pub fn get_boolean(root: &LLSDValue, path: &str, default: bool) -> bool {
        match root.get_path(path) {
            Some(LLSDValue::Boolean(b)) => *b,
            _ => default,
        }
    }

    /// Safely get a UUID value from a path
    pub fn get_uuid(root: &LLSDValue, path: &str, default: Uuid) -> Uuid {
        match root.get_path(path) {
            Some(LLSDValue::UUID(u)) => *u,
            _ => default,
        }
    }

    /// Safely get a date value from a path
    pub fn get_date(root: &LLSDValue, path: &str, default: DateTime<Utc>) -> DateTime<Utc> {
        match root.get_path(path) {
            Some(LLSDValue::Date(d)) => *d,
            _ => default,
        }
    }

    /// Convert an LLSD value to a map if possible
    pub fn as_map(value: &LLSDValue) -> HashMap<String, LLSDValue> {
        match value {
            LLSDValue::Map(map) => map.clone(),
            _ => HashMap::new(),
        }
    }

    /// Convert an LLSD value to an array if possible
    pub fn as_array(value: &LLSDValue) -> Vec<LLSDValue> {
        match value {
            LLSDValue::Array(arr) => arr.clone(),
            _ => Vec::new(),
        }
    }

    /// Check if two LLSD values are equal with floating-point tolerance
    pub fn equals_with_tolerance(a: &LLSDValue, b: &LLSDValue, tolerance: f64) -> bool {
        match (a, b) {
            (LLSDValue::Real(a_val), LLSDValue::Real(b_val)) => {
                (a_val - b_val).abs() < tolerance
            }
            (LLSDValue::Integer(a_val), LLSDValue::Real(b_val)) => {
                ((*a_val as f64) - b_val).abs() < tolerance
            }
            (LLSDValue::Real(a_val), LLSDValue::Integer(b_val)) => {
                (a_val - (*b_val as f64)).abs() < tolerance
            }
            _ => a == b,
        }
    }

    /// Merge two LLSD maps recursively
    pub fn merge_maps(base: &mut HashMap<String, LLSDValue>, overlay: &HashMap<String, LLSDValue>) {
        for (key, value) in overlay {
            match (base.get_mut(key), value) {
                (Some(LLSDValue::Map(base_map)), LLSDValue::Map(overlay_map)) => {
                    Self::merge_maps(base_map, overlay_map);
                }
                _ => {
                    base.insert(key.clone(), value.clone());
                }
            }
        }
    }

    /// Filter an LLSD map by keeping only specified keys
    pub fn filter_map(map: &HashMap<String, LLSDValue>, keep_keys: &[&str]) -> HashMap<String, LLSDValue> {
        map.iter()
            .filter_map(|(k, v)| {
                if keep_keys.contains(&k.as_str()) {
                    Some((k.clone(), v.clone()))
                } else {
                    None
                }
            })
            .collect()
    }

    /// Remove null/undefined values from an LLSD map
    pub fn remove_nulls(map: &mut HashMap<String, LLSDValue>) {
        map.retain(|_, v| !v.is_undefined());
        
        // Recursively clean nested maps
        for (_, value) in map.iter_mut() {
            if let LLSDValue::Map(nested_map) = value {
                Self::remove_nulls(nested_map);
            }
        }
    }

    /// Convert LLSD value to a pretty-printed string representation
    pub fn to_debug_string(value: &LLSDValue, indent: usize) -> String {
        let indent_str = " ".repeat(indent);
        match value {
            LLSDValue::Undefined => "undefined".to_string(),
            LLSDValue::Boolean(b) => b.to_string(),
            LLSDValue::Integer(i) => i.to_string(),
            LLSDValue::Real(r) => r.to_string(),
            LLSDValue::String(s) => format!("\"{}\"", s),
            LLSDValue::UUID(u) => u.to_string(),
            LLSDValue::Date(d) => d.to_rfc3339(),
            LLSDValue::URI(u) => format!("uri(\"{}\")", u),
            LLSDValue::Binary(b) => format!("binary({} bytes)", b.len()),
            LLSDValue::Map(map) => {
                if map.is_empty() {
                    "{}".to_string()
                } else {
                    let mut result = "{\n".to_string();
                    for (key, value) in map {
                        result.push_str(&format!(
                            "{indent}  \"{}\": {},\n",
                            key,
                            Self::to_debug_string(value, indent + 2),
                            indent = indent_str
                        ));
                    }
                    result.push_str(&format!("{indent}}}", indent = indent_str));
                    result
                }
            }
            LLSDValue::Array(arr) => {
                if arr.is_empty() {
                    "[]".to_string()
                } else {
                    let mut result = "[\n".to_string();
                    for value in arr {
                        result.push_str(&format!(
                            "{indent}  {},\n",
                            Self::to_debug_string(value, indent + 2),
                            indent = indent_str
                        ));
                    }
                    result.push_str(&format!("{indent}]", indent = indent_str));
                    result
                }
            }
        }
    }

    /// Count the total number of elements in an LLSD structure
    pub fn count_elements(value: &LLSDValue) -> usize {
        match value {
            LLSDValue::Map(map) => {
                1 + map.values().map(|v| Self::count_elements(v)).sum::<usize>()
            }
            LLSDValue::Array(arr) => {
                1 + arr.iter().map(|v| Self::count_elements(v)).sum::<usize>()
            }
            _ => 1,
        }
    }

    /// Get the maximum depth of an LLSD structure
    pub fn max_depth(value: &LLSDValue) -> usize {
        match value {
            LLSDValue::Map(map) => {
                1 + map.values().map(|v| Self::max_depth(v)).max().unwrap_or(0)
            }
            LLSDValue::Array(arr) => {
                1 + arr.iter().map(|v| Self::max_depth(v)).max().unwrap_or(0)
            }
            _ => 1,
        }
    }

    /// Validate LLSD structure against constraints
    pub fn validate_constraints(value: &LLSDValue, max_depth: usize, max_elements: usize) -> LLSDResult<()> {
        if Self::max_depth(value) > max_depth {
            return Err(LLSDError::validation_error(format!(
                "Structure depth {} exceeds maximum {}",
                Self::max_depth(value),
                max_depth
            )));
        }

        if Self::count_elements(value) > max_elements {
            return Err(LLSDError::validation_error(format!(
                "Structure has {} elements, exceeds maximum {}",
                Self::count_elements(value),
                max_elements
            )));
        }

        Ok(())
    }
}