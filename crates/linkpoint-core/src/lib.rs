//! Platform-independent Linkpoint session boundary.

use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
#[serde(rename_all = "kebab-case")]
pub enum GridKind {
    SecondLife,
    Opensim,
}

#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct LoginRequest {
    pub grid: GridKind,
    pub login_uri: String,
    pub username: String,
    pub password: String,
    pub start: Option<String>,
}

#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
#[serde(tag = "type", content = "payload")]
pub enum ViewerCommand {
    #[serde(rename = "session.login")]
    SessionLogin(LoginRequest),
    #[serde(rename = "session.logout")]
    SessionLogout,
    #[serde(rename = "chat.send")]
    ChatSend { body: String },
    #[serde(rename = "object.touch")]
    ObjectTouch {
        #[serde(rename = "objectId")]
        object_id: String,
        face: Option<u8>,
    },
}

#[derive(Debug, thiserror::Error, PartialEq, Eq)]
pub enum CoreError {
    #[error("the command is unavailable while disconnected")]
    Disconnected,
    #[error("invalid request: {0}")]
    InvalidRequest(String),
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn contract_uses_the_frontend_command_discriminator() {
        let command = ViewerCommand::ChatSend {
            body: "hello".into(),
        };
        let value = serde_json::to_value(command).expect("command should serialize");
        assert_eq!(value["type"], "chat.send");
        assert_eq!(value["payload"]["body"], "hello");
    }
}
