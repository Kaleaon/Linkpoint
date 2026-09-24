#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

use linkpoint_core::{
    CoreError, LoginRequest, Session, SessionSnapshot, SessionTransport, ViewerCommand,
};
use std::sync::Mutex;
use tauri::{Emitter, Manager};

/// Until the HTTP/UDP adapter is selected, fail closed rather than accepting
/// credentials in a partially configured native build.
struct NativeTransport;
impl SessionTransport for NativeTransport {
    fn login(&mut self, _: &LoginRequest) -> Result<SessionSnapshot, CoreError> {
        Err(CoreError::Unavailable(
            "native transport is not configured".into(),
        ))
    }
    fn logout(&mut self) -> Result<(), CoreError> {
        Ok(())
    }
    fn send_chat(&mut self, _: &str) -> Result<(), CoreError> {
        Err(CoreError::Disconnected)
    }
    fn touch_object(&mut self, _: &str, _: Option<u8>) -> Result<(), CoreError> {
        Err(CoreError::Disconnected)
    }
}

type ViewerState = Mutex<Session<NativeTransport>>;

#[tauri::command]
fn viewer_execute(
    command: ViewerCommand,
    state: tauri::State<'_, ViewerState>,
) -> Result<(), String> {
    state
        .lock()
        .map_err(|_| "viewer state unavailable".to_string())?
        .execute(command)
        .map_err(|error| error.public_message())
}

fn main() {
    tauri::Builder::default()
        .setup(|app| {
            let mut session = Session::new(NativeTransport);
            let events = session.subscribe();
            app.manage(Mutex::new(session));
            let handle = app.handle().clone();
            std::thread::spawn(move || {
                for event in events {
                    let _ = handle.emit("viewer-event", event);
                }
            });
            Ok(())
        })
        .invoke_handler(tauri::generate_handler![viewer_execute])
        .run(tauri::generate_context!())
        .expect("run Linkpoint desktop");
}
