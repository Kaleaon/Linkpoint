//! Portable Second Life/OpenSimulator wire protocol primitives.
//!
//! This crate deliberately contains no sockets or platform APIs. Callers own
//! HTTP/UDP transport and feed bytes into these deterministic codecs and state
//! machines, which keeps the protocol testable with sanitized fixtures.

pub mod capabilities;
pub mod endpoint;
pub mod llsd;
pub mod login;
pub mod packet;
pub mod reliable;

pub use capabilities::{CapabilitySet, Event, EventQueue};
pub use endpoint::{EndpointPolicy, EndpointPolicyError};
pub use login::{LoginOutcome, LoginParameters, LoginRedirect, LoginResponse, LoginResponseError};
pub use packet::{PacketFlags, PacketFrequency, PacketHeader, PacketParseError};
pub use reliable::{AckResult, ReliablePacket, ReliableUdp};
