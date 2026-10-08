//! Portable Second Life/OpenSimulator wire protocol primitives.
//!
//! This crate deliberately contains no sockets or platform APIs. Callers own
//! HTTP/UDP transport and feed bytes into these deterministic codecs and state
//! machines, which keeps the protocol testable with sanitized fixtures.

pub mod capabilities;
pub mod circuit;
pub mod decoders;
pub mod endpoint;
pub mod ffi;
pub mod generated;
pub mod llsd;
pub mod login;
pub mod packet;
pub mod reliable;
pub mod spatial;
pub mod transport;
pub mod wasm;

pub use capabilities::{CapabilitySet, Event, EventQueue};
pub use circuit::{CircuitCodecError, USE_CIRCUIT_CODE_MESSAGE_ID, UseCircuitCode};
pub use decoders::{ObjectFlags, ObjectUpdateData, ObjectUpdateDecoder, RegionFlags, RegionHandshakeData, RegionHandshakeDecoder};
pub use endpoint::{EndpointPolicy, EndpointPolicyError};
pub use login::{LoginOutcome, LoginParameters, LoginRedirect, LoginResponse, LoginResponseError};
pub use packet::{PacketFlags, PacketFrequency, PacketHeader, PacketParseError};
pub use reliable::{AckResult, ReliablePacket, ReliableUdp};
pub use spatial::{PackedQuaternion, Vector3U16, Vector3U8};
pub use transport::{HttpResponse, HttpTransport, TransportError, TransportLimits, UdpCircuit};
