use std::env;
use std::path::PathBuf;
use std::process::Command;

fn get_python_command() -> &'static str {
    let cmds: &[&str] = if cfg!(windows) {
        &["python", "py", "python3"]
    } else {
        &["python3", "python"]
    };
    for &cmd in cmds {
        if Command::new(cmd)
            .arg("--version")
            .output()
            .map(|o| o.status.success())
            .unwrap_or(false)
        {
            return cmd;
        }
    }
    if cfg!(windows) {
        "python"
    } else {
        "python3"
    }
}

fn main() {
    println!("cargo:rerun-if-changed=../../schemas/protocol/message_template.msg");
    println!("cargo:rerun-if-changed=../../schemas/protocol/llsd");

    let manifest_dir = env::var("CARGO_MANIFEST_DIR")
        .map(PathBuf::from)
        .unwrap_or_else(|_| PathBuf::from("."));
    let repo_root = manifest_dir
        .parent()
        .and_then(|p| p.parent())
        .map(|p| p.to_path_buf())
        .unwrap_or_else(|| PathBuf::from("../../"));

    let cli_path = repo_root.join("tools/protocol_gen/cli.py");

    let py_cmd = get_python_command();

    let status = Command::new(py_cmd)
        .arg(&cli_path)
        .args(["generate", "--target", "rust,c"])
        .current_dir(&repo_root)
        .status();

    if let Ok(st) = status {
        if !st.success() {
            println!("cargo:warning=Protocol code generation failed");
        }
    } else {
        println!("cargo:warning=Failed to execute protocol code generator CLI");
    }
}
