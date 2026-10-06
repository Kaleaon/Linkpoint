use std::process::Command;

fn get_python_command() -> &'static str {
    for cmd in &["python3", "python", "py"] {
        if Command::new(cmd)
            .arg("--version")
            .output()
            .map(|o| o.status.success())
            .unwrap_or(false)
        {
            return cmd;
        }
    }
    "python3"
}

fn main() {
    println!("cargo:rerun-if-changed=../../schemas/protocol/message_template.msg");
    println!("cargo:rerun-if-changed=../../schemas/protocol/llsd");

    let py_cmd = get_python_command();

    let status = Command::new(py_cmd)
        .args([
            "tools/protocol_gen/cli.py",
            "generate",
            "--target",
            "rust,c",
        ])
        .current_dir("../../")
        .status();

    if let Ok(st) = status {
        if !st.success() {
            println!("cargo:warning=Protocol code generation failed");
        }
    } else {
        println!("cargo:warning=Failed to execute protocol code generator CLI");
    }
}
