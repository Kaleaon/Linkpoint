use std::process::Command;

fn main() {
    println!("cargo:rerun-if-changed=../../schemas/protocol/message_template.msg");
    println!("cargo:rerun-if-changed=../../schemas/protocol/llsd");

    let status = Command::new("python3")
        .args(&["tools/protocol_gen/cli.py", "generate", "--target", "rust,c"])
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
