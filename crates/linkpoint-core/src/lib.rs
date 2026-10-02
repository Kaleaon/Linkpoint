//! Platform-independent Linkpoint session boundary.

use serde::{Deserialize, Serialize};
use std::sync::mpsc::{self, Receiver, Sender};
use std::time::{Duration, Instant};

const MESSAGE_TTL: Duration = Duration::from_secs(60);
const TOKEN_TTL: Duration = Duration::from_secs(15 * 60);
const MAX_RECONNECT_ATTEMPTS: u32 = 5;

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
    offline_since: Option<Instant>,
    outbound_queue: Vec<(ViewerCommand, Instant)>,
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
            .field("queued_commands", &self.outbound_queue.len())
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
            offline_since: None,
            outbound_queue: Vec::new(),
        }
    }

    pub fn state(&self) -> SessionState {
        self.state
    }

    pub fn queued_commands_count(&self) -> usize {
        self.outbound_queue.len()
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
                if self.offline_since.is_none() {
                    self.offline_since = Some(Instant::now());
                }
                if self.state == SessionState::Connected {
                    self.state = SessionState::Suspended;
                }
                Ok(())
            }
            ViewerCommand::LifecycleResume => {
                if self.is_token_expired(Instant::now()) {
                    self.expire_token();
                    return Ok(());
                }
                if self.state == SessionState::Suspended {
                    self.reconnect()
                } else {
                    Ok(())
                }
            }
            ViewerCommand::NetworkChanged { online } => {
                self.online = online;
                if !online {
                    if self.offline_since.is_none() {
                        self.offline_since = Some(Instant::now());
                    }
                    if matches!(self.state, SessionState::Connected) {
                        self.state = SessionState::Reconnecting;
                        self.reconnect_attempt = 1;
                        self.emit(ViewerEvent::SessionReconnecting { attempt: 1 });
                        self.emit(ViewerEvent::SessionDisconnected {
                            reason: "network unavailable".into(),
                        });
                    }
                } else {
                    if self.is_token_expired(Instant::now()) {
                        self.expire_token();
                        return Ok(());
                    }
                    if self.state == SessionState::Reconnecting {
                        return self.reconnect();
                    }
                }
                Ok(())
            }
            ViewerCommand::ChatSend { ref body } => {
                if body.trim().is_empty() {
                    return Err(CoreError::InvalidRequest("chat body is empty".into()));
                }
                if self.state == SessionState::Connected {
                    self.transport.send_chat(body)
                } else {
                    self.enqueue_command(command, Instant::now());
                    Ok(())
                }
            }
            ViewerCommand::ObjectTouch {
                ref object_id,
                face,
            } => {
                if object_id.trim().is_empty() {
                    return Err(CoreError::InvalidRequest("object id is empty".into()));
                }
                if self.state == SessionState::Connected {
                    self.transport.touch_object(object_id, face)
                } else {
                    self.enqueue_command(command, Instant::now());
                    Ok(())
                }
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
                self.offline_since = None;
                self.state = SessionState::Connected;
                self.emit(ViewerEvent::SessionConnected(snapshot));
                self.flush_queue(Instant::now());
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
        let now = Instant::now();
        if !self.online {
            return Err(CoreError::Unavailable("network unavailable".into()));
        }
        if self.is_token_expired(now) {
            self.expire_token();
            return Err(CoreError::Unavailable("session token expired".into()));
        }
        let request = self.login.clone().ok_or(CoreError::Disconnected)?;
        self.reconnect_attempt += 1;
        if self.reconnect_attempt > MAX_RECONNECT_ATTEMPTS {
            self.state = SessionState::Disconnected;
            self.clear_secrets();
            self.outbound_queue.clear();
            self.emit(ViewerEvent::SessionError {
                code: "max-retries-exceeded".into(),
                message:
                    "Maximum reconnection attempts reached. Please retry or return to main menu."
                        .into(),
            });
            self.emit(ViewerEvent::SessionDisconnected {
                reason: "maximum reconnect attempts exceeded".into(),
            });
            return Err(CoreError::Unavailable(
                "max reconnect attempts exceeded".into(),
            ));
        }

        self.state = SessionState::Reconnecting;
        self.emit(ViewerEvent::SessionReconnecting {
            attempt: self.reconnect_attempt,
        });
        match self.transport.login(&request) {
            Ok(snapshot) => {
                self.session = Some(snapshot.clone());
                self.state = SessionState::Connected;
                self.reconnect_attempt = 0;
                self.offline_since = None;
                self.emit(ViewerEvent::SessionConnected(snapshot));
                self.flush_queue(now);
                Ok(())
            }
            Err(error) => {
                self.state = SessionState::Reconnecting;
                Err(error)
            }
        }
    }

    fn is_token_expired(&self, now: Instant) -> bool {
        if let Some(since) = self.offline_since {
            now.duration_since(since) > TOKEN_TTL
        } else {
            false
        }
    }

    fn expire_token(&mut self) {
        self.clear_secrets();
        self.outbound_queue.clear();
        self.state = SessionState::Disconnected;
        self.offline_since = None;
        self.reconnect_attempt = 0;
        self.emit(ViewerEvent::SessionError {
            code: "token-expired".into(),
            message:
                "Cached session token expired after 15 minutes offline. Please re-authenticate."
                    .into(),
        });
        self.emit(ViewerEvent::SessionDisconnected {
            reason: "session token expired".into(),
        });
    }

    fn enqueue_command(&mut self, command: ViewerCommand, now: Instant) {
        self.purge_expired_queue(now);
        self.outbound_queue.push((command, now));
    }

    fn purge_expired_queue(&mut self, now: Instant) {
        self.outbound_queue
            .retain(|(_, timestamp)| now.duration_since(*timestamp) <= MESSAGE_TTL);
    }

    fn flush_queue(&mut self, now: Instant) {
        self.purge_expired_queue(now);
        let queue = std::mem::take(&mut self.outbound_queue);
        for (cmd, _) in queue {
            if self.state != SessionState::Connected {
                break;
            }
            let _ = match cmd {
                ViewerCommand::ChatSend { ref body } => self.transport.send_chat(body),
                ViewerCommand::ObjectTouch {
                    ref object_id,
                    face,
                } => self.transport.touch_object(object_id, face),
                _ => Ok(()),
            };
        }
    }

    fn logout(&mut self, reason: &str) -> Result<(), CoreError> {
        let result = if self.state == SessionState::Disconnected {
            Ok(())
        } else {
            self.transport.logout()
        };
        self.clear_secrets();
        self.outbound_queue.clear();
        self.offline_since = None;
        self.state = SessionState::Disconnected;
        self.emit(ViewerEvent::SessionDisconnected {
            reason: reason.into(),
        });
        result
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

        // Queue outbound chat while offline
        session
            .execute(ViewerCommand::ChatSend {
                body: "Queued offline chat".into(),
            })
            .unwrap();
        assert_eq!(session.queued_commands_count(), 1);

        session
            .execute(ViewerCommand::NetworkChanged { online: true })
            .unwrap();
        assert_eq!(session.state(), SessionState::Connected);
        assert_eq!(session.queued_commands_count(), 0);
    }

    #[test]
    fn max_reconnect_attempts_fails_and_emits_error() {
        let mut session = Session::new(FakeTransport::default());
        session
            .execute(ViewerCommand::SessionLogin(request()))
            .unwrap();
        session.online = false;
        session
            .execute(ViewerCommand::NetworkChanged { online: false })
            .unwrap();
        session.online = true;

        // Transport fails during reconnects
        session.transport.fail_login = true;
        let events = session.subscribe();

        for _ in 0..4 {
            let _ = session.execute(ViewerCommand::SessionReconnect);
        }
        let _ = session.execute(ViewerCommand::SessionReconnect); // Attempt 5 fails and triggers max retries

        assert_eq!(session.state(), SessionState::Disconnected);
        let mut found_max = false;
        while let Ok(event) = events.try_recv() {
            if matches!(event, ViewerEvent::SessionError { ref code, .. } if code == "max-retries-exceeded")
            {
                found_max = true;
            }
        }
        assert!(found_max);
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
