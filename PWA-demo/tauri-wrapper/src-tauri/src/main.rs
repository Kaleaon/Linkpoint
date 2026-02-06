// Prevents additional console window on Windows in release
#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

use actix_cors::Cors;
use actix_web::{web, App, HttpResponse, HttpServer};
use serde::{Deserialize, Serialize};
use std::sync::Mutex;
use tauri::Manager;

// Proxy state
struct ProxyState {
    port: u16,
}

// Capability request structure
#[derive(Deserialize)]
struct CapabilityRequest {
    url: String,
    body: Option<String>,
    method: Option<String>,
}

// SL Login proxy endpoint
async fn sl_login_proxy(body: String) -> HttpResponse {
    println!("[Proxy] SL Login request received");

    let client = reqwest::Client::new();
    match client
        .post("https://login.agni.lindenlab.com/cgi-bin/login.cgi")
        .header("Content-Type", "text/xml")
        .header("User-Agent", "Linkpoint-PWA-Tauri/1.0")
        .body(body)
        .send()
        .await
    {
        Ok(response) => {
            match response.text().await {
                Ok(text) => {
                    println!("[Proxy] SL Login response: {} bytes", text.len());
                    HttpResponse::Ok()
                        .content_type("text/xml")
                        .body(text)
                }
                Err(e) => {
                    eprintln!("[Proxy] Error reading response: {}", e);
                    HttpResponse::InternalServerError().body(e.to_string())
                }
            }
        }
        Err(e) => {
            eprintln!("[Proxy] Error sending request: {}", e);
            HttpResponse::InternalServerError().body(e.to_string())
        }
    }
}

// Generic capability proxy
async fn capability_proxy(req: web::Json<CapabilityRequest>) -> HttpResponse {
    println!("[Proxy] Capability request: {}", req.url);

    let client = reqwest::Client::new();
    let method = req.method.as_deref().unwrap_or("POST");

    let mut request_builder = match method {
        "GET" => client.get(&req.url),
        "POST" => client.post(&req.url),
        "PUT" => client.put(&req.url),
        "DELETE" => client.delete(&req.url),
        _ => client.post(&req.url),
    };

    request_builder = request_builder
        .header("Content-Type", "application/llsd+xml")
        .header("Accept", "application/llsd+xml");

    if let Some(body) = &req.body {
        request_builder = request_builder.body(body.clone());
    }

    match request_builder.send().await {
        Ok(response) => {
            match response.text().await {
                Ok(text) => HttpResponse::Ok().body(text),
                Err(e) => HttpResponse::InternalServerError().body(e.to_string()),
            }
        }
        Err(e) => HttpResponse::InternalServerError().body(e.to_string()),
    }
}

// Asset fetch proxy
async fn asset_proxy(
    path: web::Path<String>,
    query: web::Query<std::collections::HashMap<String, String>>,
) -> HttpResponse {
    let asset_id = path.into_inner();
    
    if let Some(server) = query.get("server") {
        println!("[Proxy] Asset fetch: {} from {}", asset_id, server);

        let url = format!("{}/{}", server, asset_id);
        let client = reqwest::Client::new();

        match client.get(&url).send().await {
            Ok(response) => {
                let content_type = response
                    .headers()
                    .get("content-type")
                    .and_then(|v| v.to_str().ok())
                    .unwrap_or("application/octet-stream")
                    .to_string();

                match response.bytes().await {
                    Ok(bytes) => {
                        HttpResponse::Ok()
                            .content_type(content_type)
                            .body(bytes)
                    }
                    Err(e) => HttpResponse::InternalServerError().body(e.to_string()),
                }
            }
            Err(e) => HttpResponse::InternalServerError().body(e.to_string()),
        }
    } else {
        HttpResponse::BadRequest().body("Missing server parameter")
    }
}

// Start proxy server in background
#[tokio::main]
async fn start_proxy_server(port: u16) -> std::io::Result<()> {
    println!("[Proxy] Starting server on 127.0.0.1:{}", port);

    HttpServer::new(|| {
        let cors = Cors::default()
            .allow_any_origin()
            .allow_any_method()
            .allow_any_header();

        App::new()
            .wrap(cors)
            .route("/sl-login", web::post().to(sl_login_proxy))
            .route("/sl-capability", web::post().to(capability_proxy))
            .route("/sl-asset/{asset_id}", web::get().to(asset_proxy))
    })
    .bind(("127.0.0.1", port))?
    .run()
    .await
}

// Tauri command to get proxy URL
#[tauri::command]
fn get_proxy_url(state: tauri::State<Mutex<ProxyState>>) -> String {
    let port = state.lock().unwrap().port;
    format!("http://127.0.0.1:{}", port)
}

fn main() {
    let proxy_port = 13338; // Different port from Electron

    // Start proxy server in separate thread
    std::thread::spawn(move || {
        if let Err(e) = start_proxy_server(proxy_port) {
            eprintln!("[Proxy] Failed to start server: {}", e);
        }
    });

    // Give server time to start
    std::thread::sleep(std::time::Duration::from_millis(500));

    tauri::Builder::default()
        .manage(Mutex::new(ProxyState { port: proxy_port }))
        .invoke_handler(tauri::generate_handler![get_proxy_url])
        .setup(|app| {
            let window = app.get_window("main").unwrap();
            
            // Inject proxy URL into window
            window.eval(&format!(
                "window.TAURI_PROXY_URL = 'http://127.0.0.1:{}'; window.IS_TAURI = true;",
                proxy_port
            ))?;

            Ok(())
        })
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
