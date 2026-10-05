use std::{collections::HashMap, fmt::Debug, hash::Hash, str::FromStr};

use chrono::{DateTime, TimeZone, Utc};
use uuid::Uuid;

use crate::LLSDValue;

pub trait FromLLSDValue: Sized {
    fn from_llsd(value: &LLSDValue) -> Option<Self>;
}

pub fn get<T: FromLLSDValue + Default>(key: &str, map: &HashMap<String, LLSDValue>) -> T {
    get_opt::<T>(key, map).unwrap_or_default()
}

pub fn get_opt<T: FromLLSDValue>(key: &str, map: &HashMap<String, LLSDValue>) -> Option<T> {
    map.get(key).and_then(|v| T::from_llsd(v))
}

pub fn get_vec<T: FromLLSDValue>(key: &str, map: &HashMap<String, LLSDValue>) -> Option<Vec<T>> {
    map.get(key).and_then(|v| {
        if let LLSDValue::Array(arr) = v {
            Some(
                arr.iter()
                    .filter_map(|item| T::from_llsd(item))
                    .collect::<Vec<_>>(),
            )
        } else {
            None
        }
    })
}

pub fn get_nested_vec<T: FromLLSDValue>(
    key: &str,
    map: &HashMap<String, LLSDValue>,
) -> Option<Vec<T>> {
    map.get(key).and_then(|v| {
        if let LLSDValue::Array(arr) = v {
            let mut result = Vec::new();
            for inner in arr {
                if let LLSDValue::Array(inner_arr) = inner {
                    for item in inner_arr {
                        if let Some(parsed) = T::from_llsd(item) {
                            result.push(parsed);
                        }
                    }
                }
            }
            Some(result)
        } else {
            None
        }
    })
}
impl FromLLSDValue for String {
    fn from_llsd(value: &LLSDValue) -> Option<Self> {
        if let LLSDValue::String(s) = value {
            Some(s.clone())
        } else {
            None
        }
    }
}

impl FromLLSDValue for DateTime<Utc> {
    fn from_llsd(value: &LLSDValue) -> Option<Self> {
        if let LLSDValue::Date(s) = value {
            match Utc.timestamp_opt(*s, 0) {
                chrono::LocalResult::Single(dt) => Some(dt),
                _ => None, // None if ambiguous or invalid
            }
        } else {
            None
        }
    }
}
impl FromLLSDValue for Uuid {
    fn from_llsd(value: &LLSDValue) -> Option<Self> {
        if let LLSDValue::UUID(u) = value {
            Some(*u)
        } else {
            None
        }
    }
}

impl FromLLSDValue for u16 {
    fn from_llsd(value: &LLSDValue) -> Option<Self> {
        if let LLSDValue::Integer(i) = value {
            Some(*i as u16)
        } else {
            None
        }
    }
}

impl FromLLSDValue for bool {
    fn from_llsd(value: &LLSDValue) -> Option<Self> {
        if let LLSDValue::Boolean(b) = value {
            Some(*b)
        } else {
            None
        }
    }
}
impl FromLLSDValue for i32 {
    fn from_llsd(value: &LLSDValue) -> Option<Self> {
        if let LLSDValue::Integer(i) = value {
            Some(*i) // convert i32 stored in LLSDValue to i32
        } else {
            None
        }
    }
}

impl FromLLSDValue for u32 {
    fn from_llsd(value: &LLSDValue) -> Option<Self> {
        if let LLSDValue::Integer(i) = value {
            Some(*i as u32) // convert i32 stored in LLSDValue to i32
        } else {
            None
        }
    }
}

impl FromLLSDValue for i64 {
    fn from_llsd(value: &LLSDValue) -> Option<Self> {
        if let LLSDValue::Integer(i) = value {
            Some(*i as i64) // convert i32 stored in LLSDValue to i32
        } else {
            None
        }
    }
}

impl FromLLSDValue for f64 {
    fn from_llsd(value: &LLSDValue) -> Option<Self> {
        match value {
            LLSDValue::Real(f) => Some(*f),
            LLSDValue::Integer(i) => Some(*i as f64),
            _ => None,
        }
    }
}

impl FromLLSDValue for f32 {
    fn from_llsd(value: &LLSDValue) -> Option<Self> {
        match value {
            LLSDValue::Real(f) => Some(*f as f32),
            LLSDValue::Integer(i) => Some(*i as f32),
            _ => None,
        }
    }
}

impl<K, V> FromLLSDValue for HashMap<K, V>
where
    K: FromStr + Eq + Hash + Debug,
    V: FromLLSDValue,
{
    fn from_llsd(value: &LLSDValue) -> Option<Self> {
        if let LLSDValue::Map(map) = value {
            let mut result = HashMap::new();
            for (k, v) in map {
                if let Ok(parsed_key) = K::from_str(k) {
                    if let Some(parsed_val) = V::from_llsd(v) {
                        result.insert(parsed_key, parsed_val);
                    }
                }
            }
            Some(result)
        } else {
            None
        }
    }
}
