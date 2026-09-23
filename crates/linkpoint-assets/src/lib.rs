//! Asset scheduling and decoding boundary.

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub enum AssetKind {
    Texture,
    Mesh,
    Animation,
    Sound,
}
