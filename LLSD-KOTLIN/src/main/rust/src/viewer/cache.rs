/*
 * Cache Manager - Rust implementation of comprehensive cache management
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Rust implementation Copyright (C) 2024
 */

use std::collections::HashMap;
use std::fs;
use std::path::{Path, PathBuf};
use std::sync::{Arc, RwLock};
use std::time::{Duration, SystemTime, UNIX_EPOCH};
use tokio::sync::Mutex;
use tokio::time::{interval, Instant};
use serde::{Deserialize, Serialize};

/// Comprehensive cache management system for Second Life viewer (Rust implementation).
/// 
/// Features:
/// - Safe concurrent operations with Rust's ownership system
/// - Async operations with Tokio for high performance
/// - Memory-safe cache management with zero-cost abstractions
/// - Type-safe configuration with enums and structs

pub const MAX_CACHE_SIZE: u64 = 200 * 1024 * 1024 * 1024; // 200GB
pub const DEFAULT_CACHE_SIZE: u64 = 10 * 1024 * 1024 * 1024; // 10GB

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash, Serialize, Deserialize)]
pub enum StorageLocation {
    Internal,
    External,
    SystemTemp,
    UserHome,
}

impl StorageLocation {
    pub fn display_name(&self) -> &'static str {
        match self {
            StorageLocation::Internal => "Internal Storage",
            StorageLocation::External => "External Storage", 
            StorageLocation::SystemTemp => "System Temp",
            StorageLocation::UserHome => "User Home",
        }
    }

    pub fn description(&self) -> &'static str {
        match self {
            StorageLocation::Internal => "Application data directory",
            StorageLocation::External => "User-specified external directory",
            StorageLocation::SystemTemp => "System temporary directory",
            StorageLocation::UserHome => "User home directory",
        }
    }
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash, Serialize, Deserialize)]
pub enum CacheType {
    Texture,
    Sound,
    Mesh,
    Animation,
    Clothing,
    Object,
    Inventory,
    Temporary,
}

impl CacheType {
    pub fn folder_name(&self) -> &'static str {
        match self {
            CacheType::Texture => "textures",
            CacheType::Sound => "sounds",
            CacheType::Mesh => "meshes",
            CacheType::Animation => "animations",
            CacheType::Clothing => "clothing",
            CacheType::Object => "objects",
            CacheType::Inventory => "inventory",
            CacheType::Temporary => "temp",
        }
    }

    pub fn description(&self) -> &'static str {
        match self {
            CacheType::Texture => "Texture cache",
            CacheType::Sound => "Audio cache",
            CacheType::Mesh => "Mesh cache",
            CacheType::Animation => "Animation cache",
            CacheType::Clothing => "Avatar clothing cache",
            CacheType::Object => "Object cache",
            CacheType::Inventory => "Inventory cache",
            CacheType::Temporary => "Temporary cache",
        }
    }

    pub fn all_types() -> &'static [CacheType] {
        &[
            CacheType::Texture,
            CacheType::Sound,
            CacheType::Mesh,
            CacheType::Animation,
            CacheType::Clothing,
            CacheType::Object,
            CacheType::Inventory,
            CacheType::Temporary,
        ]
    }
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct CacheEntry {
    pub key: String,
    pub cache_type: CacheType,
    pub size: u64,
    pub creation_time: u64,
    pub last_access_time: u64,
    pub access_count: u64,
}

impl CacheEntry {
    pub fn new(key: String, cache_type: CacheType, size: u64) -> Self {
        let now = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap()
            .as_millis() as u64;

        Self {
            key,
            cache_type,
            size,
            creation_time: now,
            last_access_time: now,
            access_count: 0,
        }
    }

    pub fn update_access_time(&mut self) {
        self.last_access_time = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap()
            .as_millis() as u64;
        self.access_count += 1;
    }

    pub fn age(&self) -> Duration {
        let now = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap()
            .as_millis() as u64;
        Duration::from_millis(now - self.creation_time)
    }

    pub fn time_since_last_access(&self) -> Duration {
        let now = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap()
            .as_millis() as u64;
        Duration::from_millis(now - self.last_access_time)
    }
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct CacheStatistics {
    pub total_size: u64,
    pub max_size: u64,
    pub total_hits: u64,
    pub total_misses: u64,
    pub total_writes: u64,
    pub total_cleanups: u64,
    pub type_sizes: HashMap<CacheType, u64>,
    pub type_limits: HashMap<CacheType, u64>,
    pub storage_location: StorageLocation,
    pub base_path: String,
}

impl CacheStatistics {
    pub fn usage_percent(&self) -> f64 {
        if self.max_size == 0 {
            0.0
        } else {
            (self.total_size as f64 / self.max_size as f64) * 100.0
        }
    }

    pub fn hit_ratio(&self) -> f64 {
        let total_requests = self.total_hits + self.total_misses;
        if total_requests == 0 {
            0.0
        } else {
            self.total_hits as f64 / total_requests as f64
        }
    }

    pub fn available_space(&self) -> u64 {
        self.max_size.saturating_sub(self.total_size)
    }

    pub fn total_requests(&self) -> u64 {
        self.total_hits + self.total_misses
    }

    pub fn type_size(&self, cache_type: CacheType) -> u64 {
        self.type_sizes.get(&cache_type).copied().unwrap_or(0)
    }

    pub fn type_limit(&self, cache_type: CacheType) -> u64 {
        self.type_limits.get(&cache_type).copied().unwrap_or(0)
    }

    pub fn type_usage_percent(&self, cache_type: CacheType) -> f64 {
        let limit = self.type_limit(cache_type);
        if limit == 0 {
            0.0
        } else {
            (self.type_size(cache_type) as f64 / limit as f64) * 100.0
        }
    }
}

impl std::fmt::Display for CacheStatistics {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        writeln!(f, "Cache Statistics:")?;
        writeln!(f, "  Total Size: {} / {} ({:.1}%)", 
                format_bytes(self.total_size), 
                format_bytes(self.max_size), 
                self.usage_percent())?;
        writeln!(f, "  Available: {}", format_bytes(self.available_space()))?;
        writeln!(f, "  Hit Ratio: {:.2}%", self.hit_ratio() * 100.0)?;
        writeln!(f, "  Requests: {} ({} hits, {} misses)", 
                self.total_requests(), self.total_hits, self.total_misses)?;
        writeln!(f, "  Writes: {}", self.total_writes)?;
        writeln!(f, "  Cleanups: {}", self.total_cleanups)?;
        writeln!(f, "  Storage: {}", self.storage_location.display_name())?;
        writeln!(f, "  Path: {}", self.base_path)?;
        writeln!(f)?;
        writeln!(f, "  Type Breakdown:")?;
        
        for cache_type in CacheType::all_types() {
            let size = self.type_size(*cache_type);
            let limit = self.type_limit(*cache_type);
            let percent = self.type_usage_percent(*cache_type);
            writeln!(f, "    {:?}: {} / {} ({:.1}%)",
                    cache_type,
                    format_bytes(size),
                    format_bytes(limit),
                    percent)?;
        }
        
        Ok(())
    }
}

pub struct CacheManager {
    storage_location: StorageLocation,
    max_cache_size: u64,
    base_cache_directory: PathBuf,
    cache_directories: HashMap<CacheType, PathBuf>,
    
    // Statistics with thread-safe access
    statistics: Arc<RwLock<CacheStatistics>>,
    
    // Cache index with async mutex for concurrent access
    cache_index: Arc<Mutex<HashMap<String, CacheEntry>>>,
    
    // Type limits and sizes
    type_limits: HashMap<CacheType, u64>,
    type_sizes: Arc<RwLock<HashMap<CacheType, u64>>>,
}

impl CacheManager {
    pub async fn new(
        storage_location: StorageLocation,
        max_cache_size: u64,
    ) -> Result<Self, Box<dyn std::error::Error + Send + Sync>> {
        let max_size = max_cache_size.min(MAX_CACHE_SIZE);
        let base_cache_directory = Self::get_base_cache_directory(storage_location)?;
        
        // Create cache directories
        fs::create_dir_all(&base_cache_directory)?;
        
        let mut cache_directories = HashMap::new();
        for cache_type in CacheType::all_types() {
            let type_dir = base_cache_directory.join(cache_type.folder_name());
            fs::create_dir_all(&type_dir)?;
            cache_directories.insert(*cache_type, type_dir);
        }

        // Initialize type limits
        let type_limits = Self::initialize_default_limits(max_size);
        
        // Initialize type sizes
        let mut type_sizes = HashMap::new();
        for cache_type in CacheType::all_types() {
            type_sizes.insert(*cache_type, 0);
        }

        let mut manager = Self {
            storage_location,
            max_cache_size: max_size,
            base_cache_directory: base_cache_directory.clone(),
            cache_directories,
            statistics: Arc::new(RwLock::new(CacheStatistics {
                total_size: 0,
                max_size,
                total_hits: 0,
                total_misses: 0,
                total_writes: 0,
                total_cleanups: 0,
                type_sizes: type_sizes.clone(),
                type_limits: type_limits.clone(),
                storage_location,
                base_path: base_cache_directory.to_string_lossy().to_string(),
            })),
            cache_index: Arc::new(Mutex::new(HashMap::new())),
            type_limits,
            type_sizes: Arc::new(RwLock::new(type_sizes)),
        };

        // Load existing cache index
        manager.load_cache_index().await?;
        
        // Start periodic cleanup
        manager.start_periodic_cleanup().await;

        log::info!(
            "Rust Cache manager initialized with {} storage, max size: {}",
            storage_location.display_name(),
            format_bytes(max_size)
        );

        Ok(manager)
    }

    fn get_base_cache_directory(location: StorageLocation) -> Result<PathBuf, Box<dyn std::error::Error + Send + Sync>> {
        let path = match location {
            StorageLocation::Internal => {
                std::env::current_dir()?.join("cache").join("secondlife")
            }
            StorageLocation::External => {
                dirs::home_dir()
                    .ok_or("Could not find home directory")?
                    .join("SLCache")
            }
            StorageLocation::SystemTemp => {
                std::env::temp_dir().join("secondlife-cache")
            }
            StorageLocation::UserHome => {
                dirs::home_dir()
                    .ok_or("Could not find home directory")?
                    .join(".secondlife")
                    .join("cache")
            }
        };
        
        Ok(path)
    }

    fn initialize_default_limits(max_size: u64) -> HashMap<CacheType, u64> {
        let mut limits = HashMap::new();
        
        // Distribute cache space across types (percentages)
        limits.insert(CacheType::Texture, max_size * 60 / 100);  // 60%
        limits.insert(CacheType::Sound, max_size * 15 / 100);    // 15%
        limits.insert(CacheType::Mesh, max_size * 15 / 100);     // 15%
        limits.insert(CacheType::Animation, max_size * 2 / 100); // 2%
        limits.insert(CacheType::Clothing, max_size * 2 / 100);  // 2%
        limits.insert(CacheType::Object, max_size * 3 / 100);    // 3%
        limits.insert(CacheType::Inventory, max_size * 2 / 100); // 2%
        limits.insert(CacheType::Temporary, max_size * 1 / 100); // 1%
        
        limits
    }

    /// Store data in cache with Rust's async/await
    pub async fn store(
        &self,
        cache_type: CacheType,
        key: String,
        data: Vec<u8>,
    ) -> Result<bool, Box<dyn std::error::Error + Send + Sync>> {
        let data_size = data.len() as u64;
        let type_limit = self.type_limits.get(&cache_type).copied().unwrap_or(0);

        if data_size > type_limit {
            log::warn!(
                "Data too large for cache type {:?}: {}",
                cache_type,
                format_bytes(data_size)
            );
            return Ok(false);
        }

        // Ensure space available
        self.ensure_space_available(cache_type, data_size).await?;

        // Create cache file path
        let cache_file = self.get_cache_file_path(cache_type, &key);
        if let Some(parent) = cache_file.parent() {
            fs::create_dir_all(parent)?;
        }

        // Write data to file
        fs::write(&cache_file, data)?;

        // Create cache entry
        let entry = CacheEntry::new(key.clone(), cache_type, data_size);

        // Update cache tracking
        {
            let mut index = self.cache_index.lock().await;
            index.insert(key.clone(), entry);
        }

        {
            let mut type_sizes = self.type_sizes.write().unwrap();
            *type_sizes.entry(cache_type).or_insert(0) += data_size;
        }

        {
            let mut stats = self.statistics.write().unwrap();
            stats.total_size += data_size;
            stats.total_writes += 1;
            if let Some(type_size) = stats.type_sizes.get_mut(&cache_type) {
                *type_size += data_size;
            }
        }

        log::debug!(
            "Cached {:?} item: {} ({})",
            cache_type,
            key,
            format_bytes(data_size)
        );

        Ok(true)
    }

    /// Retrieve data from cache with async operations
    pub async fn retrieve(
        &self,
        cache_type: CacheType,
        key: &str,
    ) -> Result<Option<Vec<u8>>, Box<dyn std::error::Error + Send + Sync>> {
        let cache_file = self.get_cache_file_path(cache_type, key);

        if !cache_file.exists() {
            let mut stats = self.statistics.write().unwrap();
            stats.total_misses += 1;
            return Ok(None);
        }

        // Update access time
        {
            let mut index = self.cache_index.lock().await;
            if let Some(entry) = index.get_mut(key) {
                entry.update_access_time();
            }
        }

        // Read data from file
        let data = fs::read(&cache_file)?;

        {
            let mut stats = self.statistics.write().unwrap();
            stats.total_hits += 1;
        }

        log::debug!(
            "Retrieved {:?} item: {} ({})",
            cache_type,
            key,
            format_bytes(data.len() as u64)
        );

        Ok(Some(data))
    }

    /// Check if item exists in cache
    pub async fn exists(&self, cache_type: CacheType, key: &str) -> bool {
        let cache_file = self.get_cache_file_path(cache_type, key);
        cache_file.exists()
    }

    /// Remove item from cache
    pub async fn remove(
        &self,
        cache_type: CacheType,
        key: &str,
    ) -> Result<bool, Box<dyn std::error::Error + Send + Sync>> {
        let cache_file = self.get_cache_file_path(cache_type, key);

        if !cache_file.exists() {
            return Ok(false);
        }

        let file_size = fs::metadata(&cache_file)?.len();
        fs::remove_file(&cache_file)?;

        // Update cache tracking
        {
            let mut index = self.cache_index.lock().await;
            index.remove(key);
        }

        {
            let mut type_sizes = self.type_sizes.write().unwrap();
            if let Some(type_size) = type_sizes.get_mut(&cache_type) {
                *type_size = type_size.saturating_sub(file_size);
            }
        }

        {
            let mut stats = self.statistics.write().unwrap();
            stats.total_size = stats.total_size.saturating_sub(file_size);
            if let Some(type_size) = stats.type_sizes.get_mut(&cache_type) {
                *type_size = type_size.saturating_sub(file_size);
            }
        }

        log::debug!(
            "Removed {:?} item: {} ({})",
            cache_type,
            key,
            format_bytes(file_size)
        );

        Ok(true)
    }

    /// Clear all cache for a specific type
    pub async fn clear_cache(
        &self,
        cache_type: CacheType,
    ) -> Result<(), Box<dyn std::error::Error + Send + Sync>> {
        let type_dir = self.cache_directories.get(&cache_type)
            .ok_or("Cache directory not found")?;

        if !type_dir.exists() {
            return Ok(());
        }

        let mut cleared_size = 0u64;
        let mut keys_to_remove = Vec::new();

        // Collect entries to remove
        {
            let index = self.cache_index.lock().await;
            for (key, entry) in index.iter() {
                if entry.cache_type == cache_type {
                    keys_to_remove.push(key.clone());
                    cleared_size += entry.size;
                }
            }
        }

        // Remove directory contents
        if type_dir.exists() {
            fs::remove_dir_all(type_dir)?;
            fs::create_dir_all(type_dir)?;
        }

        // Update cache tracking
        {
            let mut index = self.cache_index.lock().await;
            for key in &keys_to_remove {
                index.remove(key);
            }
        }

        {
            let mut type_sizes = self.type_sizes.write().unwrap();
            type_sizes.insert(cache_type, 0);
        }

        {
            let mut stats = self.statistics.write().unwrap();
            stats.total_size = stats.total_size.saturating_sub(cleared_size);
            stats.type_sizes.insert(cache_type, 0);
        }

        log::info!(
            "Cleared {:?} cache ({})",
            cache_type,
            format_bytes(cleared_size)
        );

        Ok(())
    }

    /// Clear all cache data
    pub async fn clear_all_cache(&self) -> Result<(), Box<dyn std::error::Error + Send + Sync>> {
        for cache_type in CacheType::all_types() {
            self.clear_cache(*cache_type).await?;
        }

        log::info!("Cleared all cache data");
        Ok(())
    }

    fn get_cache_file_path(&self, cache_type: CacheType, key: &str) -> PathBuf {
        let type_dir = self.cache_directories.get(&cache_type)
            .expect("Cache directory should exist");
        
        // Create subdirectories based on key hash for better file system performance
        let hash = format!("{:x}", md5::compute(key.as_bytes()));
        let sub_dir = &hash[0..2.min(hash.len())];
        
        type_dir.join(sub_dir).join(key)
    }

    async fn ensure_space_available(
        &self,
        cache_type: CacheType,
        required_space: u64,
    ) -> Result<(), Box<dyn std::error::Error + Send + Sync>> {
        let current_type_size = self.type_sizes.read().unwrap()
            .get(&cache_type).copied().unwrap_or(0);
        let type_limit = self.type_limits.get(&cache_type).copied().unwrap_or(0);

        if current_type_size + required_space > type_limit {
            self.cleanup_oldest_entries(cache_type, required_space).await?;
        }

        let total_size = self.statistics.read().unwrap().total_size;
        if total_size + required_space > self.max_cache_size {
            self.perform_global_cleanup(required_space).await?;
        }

        Ok(())
    }

    async fn cleanup_oldest_entries(
        &self,
        cache_type: CacheType,
        space_needed: u64,
    ) -> Result<(), Box<dyn std::error::Error + Send + Sync>> {
        let mut type_entries = Vec::new();

        // Collect entries of this type
        {
            let index = self.cache_index.lock().await;
            for entry in index.values() {
                if entry.cache_type == cache_type {
                    type_entries.push(entry.clone());
                }
            }
        }

        // Sort by last access time (oldest first)
        type_entries.sort_by_key(|entry| entry.last_access_time);

        let mut freed_space = 0u64;
        for entry in type_entries {
            if freed_space >= space_needed {
                break;
            }

            if self.remove(entry.cache_type, &entry.key).await? {
                freed_space += entry.size;
            }
        }

        if freed_space > 0 {
            log::info!(
                "Cleaned up {} from {:?} cache",
                format_bytes(freed_space),
                cache_type
            );
            
            let mut stats = self.statistics.write().unwrap();
            stats.total_cleanups += 1;
        }

        Ok(())
    }

    async fn perform_global_cleanup(
        &self,
        space_needed: u64,
    ) -> Result<(), Box<dyn std::error::Error + Send + Sync>> {
        log::info!(
            "Performing global cache cleanup, need {}",
            format_bytes(space_needed)
        );

        let mut all_entries = Vec::new();

        // Collect all entries
        {
            let index = self.cache_index.lock().await;
            all_entries.extend(index.values().cloned());
        }

        // Sort by last access time (oldest first)
        all_entries.sort_by_key(|entry| entry.last_access_time);

        let mut freed_space = 0u64;
        for entry in all_entries {
            if freed_space >= space_needed {
                break;
            }

            if self.remove(entry.cache_type, &entry.key).await? {
                freed_space += entry.size;
            }
        }

        Ok(())
    }

    async fn load_cache_index(&mut self) -> Result<(), Box<dyn std::error::Error + Send + Sync>> {
        let mut index = self.cache_index.lock().await;
        index.clear();

        let mut total_size = 0u64;
        let mut type_sizes = HashMap::new();

        for cache_type in CacheType::all_types() {
            let type_dir = self.cache_directories.get(cache_type)
                .ok_or("Cache directory not found")?;

            if !type_dir.exists() {
                continue;
            }

            let mut type_size = 0u64;

            // Walk directory tree and index files
            for entry in walkdir::WalkDir::new(type_dir) {
                let entry = entry?;
                let path = entry.path();

                if path.is_file() {
                    if let (Some(file_name), Ok(metadata)) = (path.file_name(), path.metadata()) {
                        let key = file_name.to_string_lossy().to_string();
                        let size = metadata.len();
                        
                        let cache_entry = CacheEntry::new(key.clone(), *cache_type, size);
                        index.insert(key, cache_entry);
                        
                        type_size += size;
                        total_size += size;
                    }
                }
            }

            type_sizes.insert(*cache_type, type_size);
        }

        // Update statistics
        {
            let mut stats = self.statistics.write().unwrap();
            stats.total_size = total_size;
            stats.type_sizes = type_sizes.clone();
        }

        {
            let mut sizes = self.type_sizes.write().unwrap();
            *sizes = type_sizes;
        }

        Ok(())
    }

    async fn start_periodic_cleanup(&self) {
        let statistics = Arc::clone(&self.statistics);
        let cache_index = Arc::clone(&self.cache_index);
        
        tokio::spawn(async move {
            let mut interval = interval(Duration::from_secs(5 * 60)); // 5 minutes
            
            loop {
                interval.tick().await;
                
                // Perform maintenance cleanup
                Self::perform_maintenance_cleanup(&statistics, &cache_index).await;
            }
        });
    }

    async fn perform_maintenance_cleanup(
        statistics: &Arc<RwLock<CacheStatistics>>,
        cache_index: &Arc<Mutex<HashMap<String, CacheEntry>>>,
    ) {
        log::debug!("Performing maintenance cleanup");

        // Remove expired temporary cache entries
        let now = SystemTime::now()
            .duration_since(UNIX_EPOCH)
            .unwrap()
            .as_millis() as u64;
        let temp_cache_expiry = 24 * 60 * 60 * 1000; // 24 hours

        let mut expired_keys = Vec::new();
        {
            let index = cache_index.lock().await;
            for (key, entry) in index.iter() {
                if entry.cache_type == CacheType::Temporary &&
                   now - entry.creation_time > temp_cache_expiry {
                    expired_keys.push(key.clone());
                }
            }
        }

        // Remove expired entries
        if !expired_keys.is_empty() {
            let mut index = cache_index.lock().await;
            for key in expired_keys {
                index.remove(&key);
            }
        }
    }

    // Configuration methods
    pub fn set_storage_location(&mut self, location: StorageLocation) -> Result<(), Box<dyn std::error::Error + Send + Sync>> {
        if location != self.storage_location {
            log::info!(
                "Changing storage location from {:?} to {:?}",
                self.storage_location,
                location
            );
            
            self.storage_location = location;
            self.base_cache_directory = Self::get_base_cache_directory(location)?;
            
            // Reinitialize cache directories
            for cache_type in CacheType::all_types() {
                let type_dir = self.base_cache_directory.join(cache_type.folder_name());
                fs::create_dir_all(&type_dir)?;
                self.cache_directories.insert(*cache_type, type_dir);
            }
        }
        
        Ok(())
    }

    pub fn set_max_cache_size(&mut self, max_size: u64) {
        let new_size = max_size.min(MAX_CACHE_SIZE);
        if new_size != self.max_cache_size {
            log::info!(
                "Changing max cache size from {} to {}",
                format_bytes(self.max_cache_size),
                format_bytes(new_size)
            );
            
            self.max_cache_size = new_size;
            self.type_limits = Self::initialize_default_limits(new_size);

            // Update statistics
            {
                let mut stats = self.statistics.write().unwrap();
                stats.max_size = new_size;
                stats.type_limits = self.type_limits.clone();
            }
        }
    }

    /// Get current cache statistics
    pub fn get_statistics(&self) -> CacheStatistics {
        self.statistics.read().unwrap().clone()
    }

    /// Get cache type sizes
    pub fn get_cache_type_sizes(&self) -> HashMap<CacheType, u64> {
        self.type_sizes.read().unwrap().clone()
    }

    /// Get cache hit ratio
    pub fn get_cache_hit_ratio(&self) -> f64 {
        let stats = self.statistics.read().unwrap();
        stats.hit_ratio()
    }

    /// Get available space
    pub fn get_available_space(&self) -> u64 {
        let stats = self.statistics.read().unwrap();
        stats.available_space()
    }

    /// Shutdown cache manager
    pub async fn shutdown(&self) {
        log::info!("Shutting down Rust cache manager");
        
        // Cache cleanup is automatic due to Rust's RAII
        
        log::info!("Rust cache manager shutdown complete");
    }
}

// Utility functions
pub fn format_bytes(bytes: u64) -> String {
    const UNITS: &[&str] = &["B", "KB", "MB", "GB", "TB"];
    const THRESHOLD: u64 = 1024;

    if bytes < THRESHOLD {
        return format!("{} B", bytes);
    }

    let mut size = bytes as f64;
    let mut unit_index = 0;

    while size >= THRESHOLD as f64 && unit_index < UNITS.len() - 1 {
        size /= THRESHOLD as f64;
        unit_index += 1;
    }

    format!("{:.1} {}", size, UNITS[unit_index])
}

// Add required dependencies to Cargo.toml:
// [dependencies]
// tokio = { version = "1", features = ["full"] }
// serde = { version = "1.0", features = ["derive"] }
// log = "0.4"
// dirs = "4.0"
// walkdir = "2"
// md5 = "0.7"