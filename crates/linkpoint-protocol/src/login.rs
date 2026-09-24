use crate::llsd::Value;
use std::collections::HashMap;

/// Parameters accepted by the SL/OpenSimulator `login_to_simulator` method.
/// Hardware identifiers are supplied by a platform adapter; this crate never
/// discovers or persists them itself.
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct LoginParameters {
    pub first: String,
    pub last: String,
    pub password: String,
    pub start: String,
    pub channel: String,
    pub version: String,
    pub platform: String,
    pub platform_string: String,
    pub platform_version: String,
    pub mac: String,
    pub id0: String,
    pub agree_to_tos: bool,
    pub read_critical: bool,
    pub viewer_digest: Option<String>,
    pub options: Vec<String>,
}

impl LoginParameters {
    pub fn to_xml_rpc(&self) -> Result<String, LoginResponseError> {
        let mut map = HashMap::from([
            ("first".into(), Value::String(self.first.clone())),
            ("last".into(), Value::String(self.last.clone())),
            ("passwd".into(), Value::String(self.password.clone())),
            ("start".into(), Value::String(self.start.clone())),
            ("channel".into(), Value::String(self.channel.clone())),
            ("version".into(), Value::String(self.version.clone())),
            ("platform".into(), Value::String(self.platform.clone())),
            (
                "platform_string".into(),
                Value::String(self.platform_string.clone()),
            ),
            (
                "platform_version".into(),
                Value::String(self.platform_version.clone()),
            ),
            ("mac".into(), Value::String(self.mac.clone())),
            ("id0".into(), Value::String(self.id0.clone())),
            ("agree_to_tos".into(), Value::Boolean(self.agree_to_tos)),
            ("read_critical".into(), Value::Boolean(self.read_critical)),
            (
                "options".into(),
                Value::Array(self.options.iter().cloned().map(Value::String).collect()),
            ),
        ]);
        if let Some(digest) = &self.viewer_digest {
            map.insert("viewer_digest".into(), Value::String(digest.clone()));
        }
        serde_llsd_benthic::ser::xml_rpc::to_string(&Value::Map(map), false, "login_to_simulator")
            .map_err(|error| LoginResponseError::InvalidXml(error.to_string()))
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct LoginResponse {
    pub agent_id: String,
    pub session_id: String,
    pub secure_session_id: Option<String>,
    pub seed_capability: Option<String>,
    pub simulator_ip: String,
    pub simulator_port: u16,
    pub circuit_code: u32,
    pub region_x: Option<u32>,
    pub region_y: Option<u32>,
    pub first_name: Option<String>,
    pub last_name: Option<String>,
    pub message: Option<String>,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct LoginRedirect {
    pub next_url: String,
    pub next_method: String,
    pub next_options: Vec<String>,
    pub next_duration: u32,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum LoginOutcome {
    Success(LoginResponse),
    Redirect(LoginRedirect),
    Failure {
        reason: Option<String>,
        message: String,
    },
}

#[derive(Debug, thiserror::Error, PartialEq, Eq)]
pub enum LoginResponseError {
    #[error("invalid XML-RPC login response: {0}")]
    InvalidXml(String),
    #[error("missing or invalid login field: {0}")]
    InvalidField(&'static str),
}

impl LoginOutcome {
    pub fn from_xml_rpc(xml: &str, limit: usize) -> Result<Self, LoginResponseError> {
        if xml.len() > limit {
            return Err(LoginResponseError::InvalidXml(format!(
                "response exceeds {limit} byte limit"
            )));
        }
        let value = serde_llsd_benthic::de::xml_rpc::from_str(xml)
            .map_err(|error| LoginResponseError::InvalidXml(error.to_string()))?;
        Self::from_llsd(&value)
    }

    pub fn from_llsd(value: &Value) -> Result<Self, LoginResponseError> {
        let map = value
            .as_map()
            .ok_or(LoginResponseError::InvalidField("root"))?;
        match login_status(map)? {
            "true" => Ok(Self::Success(LoginResponse {
                agent_id: string(map, "agent_id")?.into(),
                session_id: string(map, "session_id")?.into(),
                secure_session_id: optional_string(map, "secure_session_id"),
                seed_capability: optional_string(map, "seed_capability"),
                simulator_ip: string(map, "sim_ip")?.into(),
                simulator_port: unsigned(map, "sim_port")?
                    .try_into()
                    .map_err(|_| LoginResponseError::InvalidField("sim_port"))?,
                circuit_code: unsigned(map, "circuit_code")?,
                region_x: optional_unsigned(map, "region_x"),
                region_y: optional_unsigned(map, "region_y"),
                first_name: optional_string(map, "first_name"),
                last_name: optional_string(map, "last_name"),
                message: optional_string(map, "message"),
            })),
            "indeterminate" => Ok(Self::Redirect(LoginRedirect {
                next_url: string(map, "next_url")?.into(),
                next_method: optional_string(map, "next_method")
                    .unwrap_or_else(|| "login_to_simulator".into()),
                next_options: map
                    .get("next_options")
                    .and_then(Value::as_array)
                    .map(|items| {
                        items
                            .iter()
                            .filter_map(|item| item.as_string().cloned())
                            .collect()
                    })
                    .unwrap_or_default(),
                next_duration: optional_unsigned(map, "next_duration").unwrap_or_default(),
            })),
            "false" => Ok(Self::Failure {
                reason: optional_string(map, "reason"),
                message: optional_string(map, "message").unwrap_or_else(|| "Login failed".into()),
            }),
            _ => Err(LoginResponseError::InvalidField("login")),
        }
    }
}

fn login_status(map: &HashMap<String, Value>) -> Result<&str, LoginResponseError> {
    match map.get("login") {
        Some(Value::Boolean(true)) => Ok("true"),
        Some(Value::Boolean(false)) => Ok("false"),
        Some(Value::String(value)) => Ok(value.as_str()),
        _ => Err(LoginResponseError::InvalidField("login")),
    }
}
fn string<'a>(
    map: &'a HashMap<String, Value>,
    key: &'static str,
) -> Result<&'a str, LoginResponseError> {
    map.get(key)
        .and_then(Value::as_string)
        .map(String::as_str)
        .ok_or(LoginResponseError::InvalidField(key))
}
fn optional_string(map: &HashMap<String, Value>, key: &str) -> Option<String> {
    map.get(key).and_then(Value::as_string).cloned()
}
fn unsigned(map: &HashMap<String, Value>, key: &'static str) -> Result<u32, LoginResponseError> {
    map.get(key)
        .and_then(Value::as_integer)
        .and_then(|v| (*v).try_into().ok())
        .ok_or(LoginResponseError::InvalidField(key))
}
fn optional_unsigned(map: &HashMap<String, Value>, key: &str) -> Option<u32> {
    map.get(key)
        .and_then(Value::as_integer)
        .and_then(|v| (*v).try_into().ok())
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn request_uses_standard_serializer_and_full_viewer_metadata() {
        let xml = LoginParameters {
            first: "A<&".into(),
            last: "Resident".into(),
            password: "$1$hash".into(),
            start: "last".into(),
            channel: "Linkpoint".into(),
            version: "0.1".into(),
            platform: "lin".into(),
            platform_string: "Linux".into(),
            platform_version: "6".into(),
            options: vec!["inventory-root".into()],
            ..LoginParameters::default()
        }
        .to_xml_rpc()
        .unwrap();
        assert!(xml.contains("<methodName>login_to_simulator</methodName>"));
        assert!(xml.contains("A&lt;&amp;"));
        assert!(xml.contains("platform_string"));
    }

    #[test]
    fn parses_xml_rpc_redirect_without_treating_it_as_failure() {
        let xml = r#"<?xml version="1.0" encoding="utf-8"?><methodResponse><params><param><value><struct><member><name>login</name><value><string>indeterminate</string></value></member><member><name>next_url</name><value><string>https://login.example/next</string></value></member><member><name>next_duration</name><value><i4>3</i4></value></member></struct></value></param></params></methodResponse>"#;
        assert!(
            matches!(LoginOutcome::from_xml_rpc(xml, 4096).unwrap(), LoginOutcome::Redirect(redirect) if redirect.next_duration == 3)
        );
    }

    #[test]
    fn returns_structured_failure() {
        let value = Value::Map(HashMap::from([
            ("login".into(), Value::String("false".into())),
            ("reason".into(), Value::String("key".into())),
            (
                "message".into(),
                Value::String("credentials rejected".into()),
            ),
        ]));
        assert!(
            matches!(LoginOutcome::from_llsd(&value).unwrap(), LoginOutcome::Failure { reason: Some(reason), .. } if reason == "key")
        );
    }
}
