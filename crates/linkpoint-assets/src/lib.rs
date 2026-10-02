//! Asset scheduling and decoding boundary.

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub enum AssetKind {
    Texture,
    Mesh,
    Animation,
    Sound,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct AssetRequest {
    pub id: String,
    pub kind: AssetKind,
    pub priority: u8,
    pub grid_uri: Option<String>,
}

#[derive(Debug, Default)]
pub struct AssetScheduler {
    pending: Vec<AssetRequest>,
}
impl AssetScheduler {
    pub fn enqueue(&mut self, request: AssetRequest) {
        if !self
            .pending
            .iter()
            .any(|item| item.id == request.id && item.grid_uri == request.grid_uri)
        {
            self.pending.push(request);
            self.pending
                .sort_by_key(|item| std::cmp::Reverse(item.priority));
        }
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
        scheduler.enqueue(AssetRequest {
            id: "far".into(),
            kind: AssetKind::Texture,
            priority: 1,
            grid_uri: None,
        });
        scheduler.enqueue(AssetRequest {
            id: "near".into(),
            kind: AssetKind::Mesh,
            priority: 9,
            grid_uri: None,
        });
        scheduler.enqueue(AssetRequest {
            id: "near".into(),
            kind: AssetKind::Mesh,
            priority: 9,
            grid_uri: None,
        });
        assert_eq!(scheduler.len(), 2);
        assert_eq!(scheduler.pop_next().unwrap().id, "near");
    }
}
