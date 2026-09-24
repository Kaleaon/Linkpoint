use crate::llsd::Value;
use std::collections::HashMap;

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct LoginParameters {
    pub first: String,
    pub last: String,
    pub password: String,
    pub start: String,
    pub channel: String,
    pub version: String,
    pub platform: String,
    pub options: Vec<String>,
}

impl LoginParameters {
    /// Build the legacy `login_to_simulator` XML-RPC request still used by SL
    /// and OpenSimulator. Values are XML-escaped, including user input.
    pub fn to_xml_rpc(&self) -> String {
        let mut members = vec![
            ("first", self.first.as_str()),
            ("last", self.last.as_str()),
            ("passwd", self.password.as_str()),
            ("start", self.start.as_str()),
            ("channel", self.channel.as_str()),
            ("version", self.version.as_str()),
            ("platform", self.platform.as_str()),
        ]
        .into_iter()
        .map(|(key, value)| {
            format!(
                "<member><name>{key}</name><value><string>{}</string></value></member>",
                escape(value)
            )
        })
        .collect::<String>();
        let options = self
            .options
            .iter()
            .map(|v| format!("<value><string>{}</string></value>", escape(v)))
            .collect::<String>();
        members.push_str(&format!("<member><name>options</name><value><array><data>{options}</data></array></value></member>"));
        format!(
            "<?xml version=\"1.0\"?><methodCall><methodName>login_to_simulator</methodName><params><param><value><struct>{members}</struct></value></param></params></methodCall>"
        )
    }
}

fn escape(value: &str) -> String {
    value
        .replace('&', "&amp;")
        .replace('<', "&lt;")
        .replace('>', "&gt;")
        .replace('"', "&quot;")
        .replace('\'', "&apos;")
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct LoginResponse {
    pub agent_id: String,
    pub session_id: String,
    pub secure_session_id: String,
    pub seed_capability: String,
    pub simulator_ip: String,
    pub simulator_port: u16,
    pub region_x: u32,
    pub region_y: u32,
}

#[derive(Debug, thiserror::Error, PartialEq, Eq)]
pub enum LoginResponseError {
    #[error("login was rejected: {0}")]
    Rejected(String),
    #[error("missing or invalid login field: {0}")]
    InvalidField(&'static str),
}

impl LoginResponse {
    pub fn from_llsd(value: &Value) -> Result<Self, LoginResponseError> {
        let map = value
            .as_map()
            .ok_or(LoginResponseError::InvalidField("root"))?;
        if !boolean(map, "login")? {
            return Err(LoginResponseError::Rejected(
                string(map, "message").unwrap_or("unknown error").into(),
            ));
        }
        Ok(Self {
            agent_id: string(map, "agent_id")?.into(),
            session_id: string(map, "session_id")?.into(),
            secure_session_id: string(map, "secure_session_id")?.into(),
            seed_capability: string(map, "seed_capability")?.into(),
            simulator_ip: string(map, "sim_ip")?.into(),
            simulator_port: integer(map, "sim_port")?
                .try_into()
                .map_err(|_| LoginResponseError::InvalidField("sim_port"))?,
            region_x: integer(map, "region_x")?
                .try_into()
                .map_err(|_| LoginResponseError::InvalidField("region_x"))?,
            region_y: integer(map, "region_y")?
                .try_into()
                .map_err(|_| LoginResponseError::InvalidField("region_y"))?,
        })
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
fn integer(map: &HashMap<String, Value>, key: &'static str) -> Result<i32, LoginResponseError> {
    map.get(key)
        .and_then(Value::as_integer)
        .copied()
        .ok_or(LoginResponseError::InvalidField(key))
}
fn boolean(map: &HashMap<String, Value>, key: &'static str) -> Result<bool, LoginResponseError> {
    match map.get(key) {
        Some(Value::Boolean(v)) => Ok(*v),
        Some(Value::String(v)) => Ok(v == "true"),
        _ => Err(LoginResponseError::InvalidField(key)),
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    #[test]
    fn request_is_xml_safe_and_contains_standard_method() {
        let xml = LoginParameters {
            first: "A<&".into(),
            last: "Resident".into(),
            password: "$1$hash".into(),
            start: "last".into(),
            channel: "Linkpoint".into(),
            version: "0.1".into(),
            platform: "Win".into(),
            options: vec!["inventory-root".into()],
        }
        .to_xml_rpc();
        assert!(xml.contains("<methodName>login_to_simulator</methodName>"));
        assert!(xml.contains("A&lt;&amp;"));
        assert!(!xml.contains("A<&"));
    }
}
