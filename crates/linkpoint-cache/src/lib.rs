//! Cache policy shared by protocol and asset services.

#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub struct CachePolicy {
    pub memory_bytes: u64,
    pub disk_bytes: u64,
}
