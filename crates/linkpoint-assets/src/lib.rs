//! Asset scheduling and decoding boundary.

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub enum TextureChannel {
    Albedo,
    Normal,
    MetallicRoughness,
    Emissive,
    Occlusion,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub enum AssetKind {
    Texture,
    MaterialTexture(TextureChannel),
    Material,
    Mesh,
    Animation,
    Sound,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub enum GridKind {
    SecondLife,
    OpenSim,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub enum NetworkType {
    Unmetered,
    Metered,
}

#[derive(Debug, Clone, PartialEq)]
pub struct AssetRequest {
    pub id: String,
    pub kind: AssetKind,
    pub priority: u8,
    pub grid_uri: Option<String>,
    pub is_avatar_texture: bool,
    pub in_frustum: bool,
    pub distance: f32,
}

impl Eq for AssetRequest {}

impl AssetRequest {
    pub fn new(id: impl Into<String>, kind: AssetKind, priority: u8) -> Self {
        Self {
            id: id.into(),
            kind,
            priority,
            grid_uri: None,
            is_avatar_texture: false,
            in_frustum: true,
            distance: 0.0,
        }
    }

    pub fn with_avatar_texture(mut self, is_avatar_texture: bool) -> Self {
        self.is_avatar_texture = is_avatar_texture;
        self
    }

    pub fn with_frustum(mut self, in_frustum: bool) -> Self {
        self.in_frustum = in_frustum;
        self
    }

    pub fn with_distance(mut self, distance: f32) -> Self {
        self.distance = distance;
        self
    }

    pub fn with_grid_uri(mut self, grid_uri: Option<String>) -> Self {
        self.grid_uri = grid_uri;
        self
    }
}

#[derive(Debug, Clone)]
pub struct AssetScheduler {
    pending: Vec<AssetRequest>,
    grid_kind: GridKind,
    network_type: NetworkType,
    active_requests: usize,
    consecutive_errors: u32,
    dynamic_limit_offset: usize,
    min_concurrency: usize,
}

impl Default for AssetScheduler {
    fn default() -> Self {
        Self {
            pending: Vec::new(),
            grid_kind: GridKind::SecondLife,
            network_type: NetworkType::Unmetered,
            active_requests: 0,
            consecutive_errors: 0,
            dynamic_limit_offset: 0,
            min_concurrency: 1,
        }
    }
}

impl AssetScheduler {
    pub fn new(grid_kind: GridKind, network_type: NetworkType) -> Self {
        Self {
            grid_kind,
            network_type,
            ..Default::default()
        }
    }

    pub fn grid_kind(&self) -> GridKind {
        self.grid_kind
    }

    pub fn set_grid_kind(&mut self, grid_kind: GridKind) {
        self.grid_kind = grid_kind;
    }

    pub fn network_type(&self) -> NetworkType {
        self.network_type
    }

    pub fn set_network_type(&mut self, network_type: NetworkType) {
        self.network_type = network_type;
    }

    pub fn base_concurrency_limit(&self) -> usize {
        match (self.grid_kind, self.network_type) {
            (GridKind::SecondLife, NetworkType::Unmetered) => 64,
            (GridKind::SecondLife, NetworkType::Metered) => 16,
            (GridKind::OpenSim, NetworkType::Unmetered) => 10,
            (GridKind::OpenSim, NetworkType::Metered) => 4,
        }
    }

    pub fn effective_concurrency_limit(&self) -> usize {
        let base = self.base_concurrency_limit();
        if self.dynamic_limit_offset >= base {
            self.min_concurrency
        } else {
            (base - self.dynamic_limit_offset).max(self.min_concurrency)
        }
    }

    pub fn active_requests(&self) -> usize {
        self.active_requests
    }

    pub fn can_dispatch(&self) -> bool {
        self.active_requests < self.effective_concurrency_limit()
    }

    pub fn record_error(&mut self, status_code: u16) {
        if status_code == 503 || status_code == 504 || status_code == 408 {
            self.apply_backoff();
        }
    }

    pub fn record_timeout(&mut self) {
        self.apply_backoff();
    }

    pub fn record_response_time(&mut self, latency_ms: u64) {
        if latency_ms > 2000 {
            self.apply_backoff();
        } else if latency_ms < 500 {
            self.record_success();
        }
    }

    pub fn record_success(&mut self) {
        self.consecutive_errors = 0;
        if self.dynamic_limit_offset > 0 {
            self.dynamic_limit_offset = self.dynamic_limit_offset.saturating_sub(1);
        }
    }

    fn apply_backoff(&mut self) {
        self.consecutive_errors += 1;
        let current = self.effective_concurrency_limit();
        let next = (current / 2).max(self.min_concurrency);
        let base = self.base_concurrency_limit();
        self.dynamic_limit_offset = base.saturating_sub(next);
    }

    pub fn enqueue(&mut self, request: AssetRequest) {
        if let Some(existing) = self
            .pending
            .iter_mut()
            .find(|item| item.id == request.id && item.grid_uri == request.grid_uri)
        {
            if request.is_avatar_texture && !existing.is_avatar_texture {
                existing.is_avatar_texture = true;
            }
            if request.in_frustum && !existing.in_frustum {
                existing.in_frustum = true;
            }
            if request.priority > existing.priority {
                existing.priority = request.priority;
            }
            if request.distance < existing.distance {
                existing.distance = request.distance;
            }
        } else {
            self.pending.push(request);
        }
        self.sort_queue();
    }

    fn sort_queue(&mut self) {
        self.pending.sort_by(|a, b| {
            // 1. Avatar textures first (high priority avatar assets)
            if a.is_avatar_texture != b.is_avatar_texture {
                return b.is_avatar_texture.cmp(&a.is_avatar_texture);
            }
            // 2. Viewport frustum visible assets first
            if a.in_frustum != b.in_frustum {
                return b.in_frustum.cmp(&a.in_frustum);
            }
            // 3. Distance (closer first)
            let dist_diff = a.distance - b.distance;
            if dist_diff.abs() > 0.001 {
                return dist_diff
                    .partial_cmp(&0.0)
                    .unwrap_or(std::cmp::Ordering::Equal);
            }
            // 4. Priority (higher u8 priority first)
            b.priority.cmp(&a.priority)
        });
    }

    pub fn dispatch_next(&mut self) -> Option<AssetRequest> {
        if self.can_dispatch() && !self.pending.is_empty() {
            self.active_requests += 1;
            Some(self.pending.remove(0))
        } else {
            None
        }
    }

    pub fn complete_request(&mut self) {
        self.active_requests = self.active_requests.saturating_sub(1);
    }

    pub fn pop_next(&mut self) -> Option<AssetRequest> {
        (!self.pending.is_empty()).then(|| self.pending.remove(0))
    }

    pub fn len(&self) -> usize {
        self.pending.len()
    }

    pub fn is_empty(&self) -> bool {
        self.pending.is_empty()
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn deduplicates_and_prioritizes_assets() {
        let mut scheduler = AssetScheduler::default();
        scheduler.enqueue(AssetRequest::new("far", AssetKind::Texture, 1));
        scheduler.enqueue(AssetRequest::new("near", AssetKind::Mesh, 9));
        scheduler.enqueue(AssetRequest::new("near", AssetKind::Mesh, 9));
        assert_eq!(scheduler.len(), 2);
        assert_eq!(scheduler.pop_next().unwrap().id, "near");
    }

    #[test]
    fn schedules_multichannel_pbr_material_textures() {
        let mut scheduler = AssetScheduler::default();
        scheduler.enqueue(AssetRequest::new("mat_1", AssetKind::Material, 10));
        scheduler.enqueue(AssetRequest::new(
            "normal_map",
            AssetKind::MaterialTexture(TextureChannel::Normal),
            8,
        ));
        assert_eq!(scheduler.len(), 2);
        let req1 = scheduler.pop_next().unwrap();
        assert_eq!(req1.id, "mat_1");
        assert_eq!(req1.kind, AssetKind::Material);
        let req2 = scheduler.pop_next().unwrap();
        assert_eq!(req2.id, "normal_map");
        assert_eq!(
            req2.kind,
            AssetKind::MaterialTexture(TextureChannel::Normal)
        );
    }

    #[test]
    fn grid_and_network_concurrency_limits() {
        let mut scheduler = AssetScheduler::default();
        assert_eq!(scheduler.effective_concurrency_limit(), 64); // SL + Unmetered

        scheduler.set_network_type(NetworkType::Metered);
        assert_eq!(scheduler.effective_concurrency_limit(), 16); // SL + Metered

        scheduler.set_grid_kind(GridKind::OpenSim);
        assert_eq!(scheduler.effective_concurrency_limit(), 4); // OpenSim + Metered

        scheduler.set_network_type(NetworkType::Unmetered);
        assert_eq!(scheduler.effective_concurrency_limit(), 10); // OpenSim + Unmetered
    }

    #[test]
    fn dynamic_backoff_on_503_and_timeout_recovery() {
        let mut scheduler = AssetScheduler::new(GridKind::OpenSim, NetworkType::Unmetered);
        assert_eq!(scheduler.effective_concurrency_limit(), 10);

        scheduler.record_error(503);
        assert_eq!(scheduler.effective_concurrency_limit(), 5);

        scheduler.record_timeout();
        assert_eq!(scheduler.effective_concurrency_limit(), 2);

        scheduler.record_timeout();
        assert_eq!(scheduler.effective_concurrency_limit(), 1);

        // Success gradually recovers limit
        for _ in 0..10 {
            scheduler.record_success();
        }
        assert_eq!(scheduler.effective_concurrency_limit(), 10);
    }

    #[test]
    fn frustum_and_avatar_texture_prioritization() {
        let mut scheduler = AssetScheduler::default();

        let bg_asset = AssetRequest::new("bg", AssetKind::Texture, 5).with_frustum(false);
        let frustum_asset = AssetRequest::new("frustum", AssetKind::Texture, 2).with_frustum(true);
        let avatar_asset = AssetRequest::new("avatar_tex", AssetKind::Texture, 1)
            .with_avatar_texture(true)
            .with_frustum(false);

        scheduler.enqueue(bg_asset);
        scheduler.enqueue(frustum_asset);
        scheduler.enqueue(avatar_asset);

        assert_eq!(scheduler.pop_next().unwrap().id, "avatar_tex");
        assert_eq!(scheduler.pop_next().unwrap().id, "frustum");
        assert_eq!(scheduler.pop_next().unwrap().id, "bg");
    }
}
