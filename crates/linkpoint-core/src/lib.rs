//! Platform-independent Linkpoint session boundary.

use serde::{Deserialize, Serialize};
use std::sync::mpsc::{self, Receiver, Sender};

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
#[serde(rename_all = "camelCase")]
pub struct SessionSnapshot {
    pub agent_id: String,
    pub session_id: String,
    pub region_name: String,
}

#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct ChatMessage {
    pub id: String,
    pub from_id: String,
    pub from_name: String,
    pub body: String,
    pub timestamp: String,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct SceneEntity {
    pub id: String,
    pub parent_id: Option<String>,
    pub position: [f32; 3],
    pub rotation: [f32; 4],
    pub scale: [f32; 3],
}

#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
#[serde(tag = "type", content = "payload")]
pub enum ViewerCommand {
    #[serde(rename = "session.login")]
    SessionLogin(LoginRequest),
    #[serde(rename = "session.logout")]
    SessionLogout,
    #[serde(rename = "session.reconnect")]
    SessionReconnect,
    #[serde(rename = "lifecycle.suspend")]
    LifecycleSuspend,
    #[serde(rename = "lifecycle.resume")]
    LifecycleResume,
    #[serde(rename = "network.changed")]
    NetworkChanged { online: bool },
    #[serde(rename = "chat.send")]
    ChatSend { body: String },
    #[serde(rename = "object.touch")]
    ObjectTouch {
        #[serde(rename = "objectId")]
        object_id: String,
        face: Option<u8>,
    },
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(tag = "type", content = "payload")]
pub enum ViewerEvent {
    #[serde(rename = "session.connecting")]
    SessionConnecting,
    #[serde(rename = "session.connected")]
    SessionConnected(SessionSnapshot),
    #[serde(rename = "session.disconnected")]
    SessionDisconnected { reason: String },
    #[serde(rename = "session.reconnecting")]
    SessionReconnecting { attempt: u32 },
    #[serde(rename = "session.error")]
    SessionError { code: String, message: String },
    #[serde(rename = "chat.received")]
    ChatReceived(ChatMessage),
    #[serde(rename = "scene.snapshot")]
    SceneSnapshot(Vec<SceneEntity>),
    #[serde(rename = "scene.upsert")]
    SceneUpsert(SceneEntity),
    #[serde(rename = "scene.remove")]
    SceneRemove { id: String },
}

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum SessionState {
    Disconnected,
    Connecting,
    Connected,
    Reconnecting,
    Suspended,
}

/// Platform transport owned by the native adapter. Implementations may use
/// async internally, but this narrow boundary keeps the state machine fully
/// deterministic and independently testable.
pub trait SessionTransport {
    fn login(&mut self, request: &LoginRequest) -> Result<SessionSnapshot, CoreError>;
    fn logout(&mut self) -> Result<(), CoreError>;
    fn send_chat(&mut self, body: &str) -> Result<(), CoreError>;
    fn touch_object(&mut self, object_id: &str, face: Option<u8>) -> Result<(), CoreError>;
}

/// Owns credentials only for the lifetime of a live/reconnectable session.
/// Debug output is intentionally redacted by the manual implementation.
pub struct Session<T: SessionTransport> {
    state: SessionState,
    transport: T,
    login: Option<LoginRequest>,
    session: Option<SessionSnapshot>,
    subscribers: Vec<Sender<ViewerEvent>>,
    online: bool,
    reconnect_attempt: u32,
}

impl<T: SessionTransport> std::fmt::Debug for Session<T> {
    fn fmt(&self, formatter: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        formatter
            .debug_struct("Session")
            .field("state", &self.state)
            .field("has_credentials", &self.login.is_some())
            .field("session", &self.session)
            .field("online", &self.online)
            .field("reconnect_attempt", &self.reconnect_attempt)
            .finish_non_exhaustive()
    }
}

impl<T: SessionTransport> Session<T> {
    pub fn new(transport: T) -> Self {
        Self {
            state: SessionState::Disconnected,
            transport,
            login: None,
            session: None,
            subscribers: Vec::new(),
            online: true,
            reconnect_attempt: 0,
        }
    }

    pub fn state(&self) -> SessionState {
        self.state
    }

    pub fn subscribe(&mut self) -> Receiver<ViewerEvent> {
        let (sender, receiver) = mpsc::channel();
        self.subscribers.push(sender);
        receiver
    }

    pub fn execute(&mut self, command: ViewerCommand) -> Result<(), CoreError> {
        match command {
            ViewerCommand::SessionLogin(request) => self.login(request),
            ViewerCommand::SessionLogout => self.logout("logout requested"),
            ViewerCommand::SessionReconnect => self.reconnect(),
            ViewerCommand::LifecycleSuspend => {
                if self.state == SessionState::Connected {
                    self.state = SessionState::Suspended;
                }
                Ok(())
            }
            ViewerCommand::LifecycleResume => {
                if self.state == SessionState::Suspended {
                    self.reconnect()
                } else {
                    Ok(())
                }
            }
            ViewerCommand::NetworkChanged { online } => {
                self.online = online;
                if !online && matches!(self.state, SessionState::Connected) {
                    self.state = SessionState::Reconnecting;
                    self.emit(ViewerEvent::SessionDisconnected {
                        reason: "network unavailable".into(),
                    });
                } else if online && self.state == SessionState::Reconnecting {
                    return self.reconnect();
                }
                Ok(())
            }
            ViewerCommand::ChatSend { body } => {
                self.require_connected()?;
                if body.trim().is_empty() {
                    return Err(CoreError::InvalidRequest("chat body is empty".into()));
                }
                self.transport.send_chat(&body)
            }
            ViewerCommand::ObjectTouch { object_id, face } => {
                self.require_connected()?;
                if object_id.trim().is_empty() {
                    return Err(CoreError::InvalidRequest("object id is empty".into()));
                }
                self.transport.touch_object(&object_id, face)
            }
        }
    }

    fn login(&mut self, request: LoginRequest) -> Result<(), CoreError> {
        if request.username.trim().is_empty() || request.password.is_empty() {
            return Err(CoreError::InvalidRequest(
                "credentials are incomplete".into(),
            ));
        }
        if !self.online {
            return Err(CoreError::Unavailable("network unavailable".into()));
        }
        self.state = SessionState::Connecting;
        self.emit(ViewerEvent::SessionConnecting);
        match self.transport.login(&request) {
            Ok(snapshot) => {
                self.login = Some(request);
                self.session = Some(snapshot.clone());
                self.reconnect_attempt = 0;
                self.state = SessionState::Connected;
                self.emit(ViewerEvent::SessionConnected(snapshot));
                Ok(())
            }
            Err(error) => {
                self.clear_secrets();
                self.state = SessionState::Disconnected;
                self.emit(ViewerEvent::SessionError {
                    code: error.code().into(),
                    message: error.public_message(),
                });
                Err(error)
            }
        }
    }

    fn reconnect(&mut self) -> Result<(), CoreError> {
        if !self.online {
            return Err(CoreError::Unavailable("network unavailable".into()));
        }
        let request = self.login.clone().ok_or(CoreError::Disconnected)?;
        self.reconnect_attempt += 1;
        self.state = SessionState::Reconnecting;
        self.emit(ViewerEvent::SessionReconnecting {
            attempt: self.reconnect_attempt,
        });
        match self.transport.login(&request) {
            Ok(snapshot) => {
                self.session = Some(snapshot.clone());
                self.state = SessionState::Connected;
                self.emit(ViewerEvent::SessionConnected(snapshot));
                Ok(())
            }
            Err(error) => {
                self.state = SessionState::Reconnecting;
                Err(error)
            }
        }
    }

    fn logout(&mut self, reason: &str) -> Result<(), CoreError> {
        let result = if self.state == SessionState::Disconnected {
            Ok(())
        } else {
            self.transport.logout()
        };
        self.clear_secrets();
        self.state = SessionState::Disconnected;
        self.emit(ViewerEvent::SessionDisconnected {
            reason: reason.into(),
        });
        result
    }

    fn require_connected(&self) -> Result<(), CoreError> {
        (self.state == SessionState::Connected)
            .then_some(())
            .ok_or(CoreError::Disconnected)
    }

    fn clear_secrets(&mut self) {
        if let Some(request) = self.login.as_mut() {
            request.password.clear();
        }
        self.login = None;
        self.session = None;
    }

    fn emit(&mut self, event: ViewerEvent) {
        self.subscribers
            .retain(|subscriber| subscriber.send(event.clone()).is_ok());
    }
}

#[derive(Debug, Clone, thiserror::Error, PartialEq, Eq)]
pub enum CoreError {
    #[error("the command is unavailable while disconnected")]
    Disconnected,
    #[error("invalid request: {0}")]
    InvalidRequest(String),
    #[error("service unavailable: {0}")]
    Unavailable(String),
    #[error("authentication failed")]
    Authentication,
    #[error("transport failed: {0}")]
    Transport(String),
}

impl CoreError {
    pub fn code(&self) -> &'static str {
        match self {
            Self::Disconnected => "disconnected",
            Self::InvalidRequest(_) => "invalid-request",
            Self::Unavailable(_) => "unavailable",
            Self::Authentication => "authentication-failed",
            Self::Transport(_) => "transport-failed",
        }
    }

    /// Deliberately excludes transport internals and credential-bearing text.
    pub fn public_message(&self) -> String {
        match self {
            Self::Transport(_) => "The viewer could not reach the grid.".into(),
            _ => self.to_string(),
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[derive(Default)]
    struct FakeTransport {
        fail_login: bool,
        chats: Vec<String>,
        logouts: usize,
    }

    impl SessionTransport for FakeTransport {
        fn login(&mut self, _: &LoginRequest) -> Result<SessionSnapshot, CoreError> {
            if self.fail_login {
                Err(CoreError::Transport("secret upstream detail".into()))
            } else {
                Ok(SessionSnapshot {
                    agent_id: "agent".into(),
                    session_id: "session".into(),
                    region_name: "Test Region".into(),
                })
            }
        }
        fn logout(&mut self) -> Result<(), CoreError> {
            self.logouts += 1;
            Ok(())
        }
        fn send_chat(&mut self, body: &str) -> Result<(), CoreError> {
            self.chats.push(body.into());
            Ok(())
        }
        fn touch_object(&mut self, _: &str, _: Option<u8>) -> Result<(), CoreError> {
            Ok(())
        }
    }

    fn request() -> LoginRequest {
        LoginRequest {
            grid: GridKind::SecondLife,
            login_uri: "https://login.agni.lindenlab.com/cgi-bin/login.cgi".into(),
            username: "Test Resident".into(),
            password: "do-not-log".into(),
            start: Some("last".into()),
        }
    }

    #[test]
    fn contract_uses_the_frontend_command_discriminator() {
        let value = serde_json::to_value(ViewerCommand::ChatSend {
            body: "hello".into(),
        })
        .unwrap();
        assert_eq!(value["type"], "chat.send");
        assert_eq!(value["payload"]["body"], "hello");
    }

    #[test]
    fn login_chat_logout_publishes_a_safe_lifecycle() {
        let mut session = Session::new(FakeTransport::default());
        let events = session.subscribe();
        session
            .execute(ViewerCommand::SessionLogin(request()))
            .unwrap();
        session
            .execute(ViewerCommand::ChatSend {
                body: "hello".into(),
            })
            .unwrap();
        session.execute(ViewerCommand::SessionLogout).unwrap();
        assert!(matches!(
            events.recv().unwrap(),
            ViewerEvent::SessionConnecting
        ));
        assert!(matches!(
            events.recv().unwrap(),
            ViewerEvent::SessionConnected(_)
        ));
        assert!(matches!(
            events.recv().unwrap(),
            ViewerEvent::SessionDisconnected { .. }
        ));
        assert_eq!(session.state(), SessionState::Disconnected);
        assert!(!format!("{session:?}").contains("do-not-log"));
    }

    #[test]
    fn interrupted_connection_reconnects_when_network_returns() {
        let mut session = Session::new(FakeTransport::default());
        session
            .execute(ViewerCommand::SessionLogin(request()))
            .unwrap();
        session
            .execute(ViewerCommand::NetworkChanged { online: false })
            .unwrap();
        assert_eq!(session.state(), SessionState::Reconnecting);
        session
            .execute(ViewerCommand::NetworkChanged { online: true })
            .unwrap();
        assert_eq!(session.state(), SessionState::Connected);
    }

    #[test]
    fn transport_errors_are_redacted_for_frontend_events() {
        let mut session = Session::new(FakeTransport {
            fail_login: true,
            ..Default::default()
        });
        let events = session.subscribe();
        assert!(
            session
                .execute(ViewerCommand::SessionLogin(request()))
                .is_err()
        );
        let _ = events.recv().unwrap();
        let ViewerEvent::SessionError { message, .. } = events.recv().unwrap() else {
            panic!("expected error")
        };
        assert!(!message.contains("secret upstream detail"));
    }
}
