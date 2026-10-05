//! C-FFI exports for Dart and non-Rust clients.

use std::ffi::CString;
use std::os::raw::c_char;
use std::slice;

use crate::llsd::{Format, decode, encode, json_to_llsd, llsd_to_json};

/// Result buffer structure returned across the C-FFI boundary.
#[repr(C)]
pub struct LlsdResultBuffer {
    pub ptr: *mut u8,
    pub len: usize,
    pub cap: usize,
    pub status: i32,
    pub error_ptr: *mut c_char,
}

fn make_success_buffer(mut data: Vec<u8>) -> *mut LlsdResultBuffer {
    let ptr = data.as_mut_ptr();
    let len = data.len();
    let cap = data.capacity();
    std::mem::forget(data);

    let buf = Box::new(LlsdResultBuffer {
        ptr,
        len,
        cap,
        status: 0,
        error_ptr: std::ptr::null_mut(),
    });
    Box::into_raw(buf)
}

fn make_error_buffer(msg: &str) -> *mut LlsdResultBuffer {
    let c_str = CString::new(msg).unwrap_or_else(|_| CString::new("Error").unwrap());
    let buf = Box::new(LlsdResultBuffer {
        ptr: std::ptr::null_mut(),
        len: 0,
        cap: 0,
        status: -1,
        error_ptr: c_str.into_raw(),
    });
    Box::into_raw(buf)
}

/// Free memory allocated for a result buffer across the C-FFI boundary.
///
/// # Safety
///
/// `buffer_ptr` must either be null or point to a valid `LlsdResultBuffer` previously
/// returned by one of the `linkpoint_llsd_*` FFI functions.
#[unsafe(no_mangle)]
pub unsafe extern "C" fn linkpoint_free_buffer(buffer_ptr: *mut LlsdResultBuffer) {
    if buffer_ptr.is_null() {
        return;
    }
    unsafe {
        let buf = Box::from_raw(buffer_ptr);
        if !buf.ptr.is_null() && buf.cap > 0 {
            let _ = Vec::from_raw_parts(buf.ptr, buf.len, buf.cap);
        }
        if !buf.error_ptr.is_null() {
            let _ = CString::from_raw(buf.error_ptr);
        }
    }
}

/// Parse LLSD XML input into a JSON string result buffer.
///
/// # Safety
///
/// `input_ptr` must point to `input_len` contiguous, readable bytes or be null.
#[unsafe(no_mangle)]
pub unsafe extern "C" fn linkpoint_llsd_parse_xml(
    input_ptr: *const u8,
    input_len: usize,
) -> *mut LlsdResultBuffer {
    if input_ptr.is_null() {
        return make_error_buffer("Null input pointer");
    }
    let input = unsafe { slice::from_raw_parts(input_ptr, input_len) };
    match decode(input, 16 * 1024 * 1024) {
        Ok(llsd_val) => {
            let json_val = llsd_to_json(&llsd_val);
            match serde_json::to_vec(&json_val) {
                Ok(bytes) => make_success_buffer(bytes),
                Err(e) => make_error_buffer(&format!("JSON serialization error: {}", e)),
            }
        }
        Err(e) => make_error_buffer(&format!("XML parse error: {}", e)),
    }
}

/// Parse LLSD Binary input into a JSON string result buffer.
///
/// # Safety
///
/// `input_ptr` must point to `input_len` contiguous, readable bytes or be null.
#[unsafe(no_mangle)]
pub unsafe extern "C" fn linkpoint_llsd_parse_binary(
    input_ptr: *const u8,
    input_len: usize,
) -> *mut LlsdResultBuffer {
    if input_ptr.is_null() {
        return make_error_buffer("Null input pointer");
    }
    let input = unsafe { slice::from_raw_parts(input_ptr, input_len) };
    match decode(input, 16 * 1024 * 1024) {
        Ok(llsd_val) => {
            let json_val = llsd_to_json(&llsd_val);
            match serde_json::to_vec(&json_val) {
                Ok(bytes) => make_success_buffer(bytes),
                Err(e) => make_error_buffer(&format!("JSON serialization error: {}", e)),
            }
        }
        Err(e) => make_error_buffer(&format!("Binary parse error: {}", e)),
    }
}

/// Parse LLSD Notation input into a JSON string result buffer.
///
/// # Safety
///
/// `input_ptr` must point to `input_len` contiguous, readable bytes or be null.
#[unsafe(no_mangle)]
pub unsafe extern "C" fn linkpoint_llsd_parse_notation(
    input_ptr: *const u8,
    input_len: usize,
) -> *mut LlsdResultBuffer {
    if input_ptr.is_null() {
        return make_error_buffer("Null input pointer");
    }
    let input = unsafe { slice::from_raw_parts(input_ptr, input_len) };
    match decode(input, 16 * 1024 * 1024) {
        Ok(llsd_val) => {
            let json_val = llsd_to_json(&llsd_val);
            match serde_json::to_vec(&json_val) {
                Ok(bytes) => make_success_buffer(bytes),
                Err(e) => make_error_buffer(&format!("JSON serialization error: {}", e)),
            }
        }
        Err(e) => make_error_buffer(&format!("Notation parse error: {}", e)),
    }
}

/// Serialize JSON input into LLSD XML data stream buffer.
///
/// # Safety
///
/// `json_ptr` must point to `json_len` contiguous, readable bytes or be null.
#[unsafe(no_mangle)]
pub unsafe extern "C" fn linkpoint_llsd_serialize_xml(
    json_ptr: *const u8,
    json_len: usize,
) -> *mut LlsdResultBuffer {
    if json_ptr.is_null() {
        return make_error_buffer("Null JSON pointer");
    }
    let input = unsafe { slice::from_raw_parts(json_ptr, json_len) };
    match serde_json::from_slice::<serde_json::Value>(input) {
        Ok(json_val) => {
            let llsd_val = json_to_llsd(&json_val);
            match encode(&llsd_val, Format::Xml) {
                Ok(bytes) => make_success_buffer(bytes),
                Err(e) => make_error_buffer(&format!("XML encode error: {}", e)),
            }
        }
        Err(e) => make_error_buffer(&format!("JSON parse error: {}", e)),
    }
}

/// Serialize JSON input into LLSD Binary data stream buffer.
///
/// # Safety
///
/// `json_ptr` must point to `json_len` contiguous, readable bytes or be null.
#[unsafe(no_mangle)]
pub unsafe extern "C" fn linkpoint_llsd_serialize_binary(
    json_ptr: *const u8,
    json_len: usize,
) -> *mut LlsdResultBuffer {
    if json_ptr.is_null() {
        return make_error_buffer("Null JSON pointer");
    }
    let input = unsafe { slice::from_raw_parts(json_ptr, json_len) };
    match serde_json::from_slice::<serde_json::Value>(input) {
        Ok(json_val) => {
            let llsd_val = json_to_llsd(&json_val);
            match encode(&llsd_val, Format::Binary) {
                Ok(bytes) => make_success_buffer(bytes),
                Err(e) => make_error_buffer(&format!("Binary encode error: {}", e)),
            }
        }
        Err(e) => make_error_buffer(&format!("JSON parse error: {}", e)),
    }
}

/// Serialize JSON input into LLSD Notation data stream buffer.
///
/// # Safety
///
/// `json_ptr` must point to `json_len` contiguous, readable bytes or be null.
#[unsafe(no_mangle)]
pub unsafe extern "C" fn linkpoint_llsd_serialize_notation(
    json_ptr: *const u8,
    json_len: usize,
) -> *mut LlsdResultBuffer {
    if json_ptr.is_null() {
        return make_error_buffer("Null JSON pointer");
    }
    let input = unsafe { slice::from_raw_parts(json_ptr, json_len) };
    match serde_json::from_slice::<serde_json::Value>(input) {
        Ok(json_val) => {
            let llsd_val = json_to_llsd(&json_val);
            match encode(&llsd_val, Format::Notation) {
                Ok(bytes) => make_success_buffer(bytes),
                Err(e) => make_error_buffer(&format!("Notation encode error: {}", e)),
            }
        }
        Err(e) => make_error_buffer(&format!("JSON parse error: {}", e)),
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_ffi_roundtrip_and_memory_free() {
        unsafe {
            let xml_input = b"<llsd><map><key>test</key><integer>123</integer></map></llsd>";
            let res_buf = linkpoint_llsd_parse_xml(xml_input.as_ptr(), xml_input.len());
            assert!(!res_buf.is_null());
            assert_eq!((*res_buf).status, 0);
            assert!((*res_buf).len > 0);

            let res_slice = slice::from_raw_parts((*res_buf).ptr, (*res_buf).len);
            let json_str = std::str::from_utf8(res_slice).unwrap();
            assert!(json_str.contains("123"));

            linkpoint_free_buffer(res_buf);
        }
    }
}
