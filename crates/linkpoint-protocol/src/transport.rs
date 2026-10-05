//! Bounded transport primitives. Authentication policy remains in the caller;
//! these types ensure socket timeouts and response limits cannot be omitted.

use std::{
    io,
    net::{SocketAddr, UdpSocket},
    time::Duration,
};

#[derive(Debug, Clone, Copy)]
pub struct TransportLimits {
    pub connect_timeout: Duration,
    pub request_timeout: Duration,
    pub max_response_bytes: usize,
    pub max_datagram_bytes: usize,
}

impl Default for TransportLimits {
    fn default() -> Self {
        Self {
            connect_timeout: Duration::from_secs(10),
            request_timeout: Duration::from_secs(30),
            max_response_bytes: 4 * 1024 * 1024,
            max_datagram_bytes: 64 * 1024,
        }
    }
}

pub trait HttpTransport: Send {
    fn post(
        &mut self,
        url: &str,
        content_type: &str,
        body: &[u8],
        limits: TransportLimits,
    ) -> Result<HttpResponse, TransportError>;
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct HttpResponse {
    pub status: u16,
    pub body: Vec<u8>,
}

impl HttpResponse {
    pub fn bounded(
        status: u16,
        body: Vec<u8>,
        limits: TransportLimits,
    ) -> Result<Self, TransportError> {
        if body.len() > limits.max_response_bytes {
            return Err(TransportError::ResponseTooLarge);
        }
        Ok(Self { status, body })
    }
}

pub struct UdpCircuit {
    socket: UdpSocket,
    peer: SocketAddr,
    limits: TransportLimits,
}

impl UdpCircuit {
    pub fn connect(
        bind: SocketAddr,
        peer: SocketAddr,
        limits: TransportLimits,
    ) -> Result<Self, TransportError> {
        let socket = UdpSocket::bind(bind)?;
        socket.connect(peer)?;
        socket.set_read_timeout(Some(limits.request_timeout))?;
        socket.set_write_timeout(Some(limits.request_timeout))?;
        Ok(Self {
            socket,
            peer,
            limits,
        })
    }
    pub fn peer(&self) -> SocketAddr {
        self.peer
    }
    pub fn send(&self, packet: &[u8]) -> Result<usize, TransportError> {
        if packet.len() > self.limits.max_datagram_bytes {
            return Err(TransportError::DatagramTooLarge);
        }
        Ok(self.socket.send(packet)?)
    }
    pub fn receive(&self) -> Result<Vec<u8>, TransportError> {
        let mut bytes = vec![0; self.limits.max_datagram_bytes];
        let size = self.socket.recv(&mut bytes)?;
        bytes.truncate(size);
        Ok(bytes)
    }
}

#[derive(Debug, thiserror::Error)]
pub enum TransportError {
    #[error("transport I/O failed: {0}")]
    Io(#[from] io::Error),
    #[error("response exceeded configured limit")]
    ResponseTooLarge,
    #[error("datagram exceeded configured limit")]
    DatagramTooLarge,
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn rejects_oversized_http_response() {
        let limits = TransportLimits {
            max_response_bytes: 2,
            ..Default::default()
        };
        assert!(matches!(
            HttpResponse::bounded(200, vec![1, 2, 3], limits),
            Err(TransportError::ResponseTooLarge)
        ));
    }

    #[test]
    fn accepts_valid_http_response() {
        let limits = TransportLimits::default();
        let resp = HttpResponse::bounded(200, vec![1, 2, 3], limits).unwrap();
        assert_eq!(resp.status, 200);
        assert_eq!(resp.body, vec![1, 2, 3]);
    }

    #[test]
    fn udp_circuit_send_receive_and_limits() {
        let server_socket = UdpSocket::bind("127.0.0.1:0").unwrap();
        let server_addr = server_socket.local_addr().unwrap();

        let limits = TransportLimits {
            max_datagram_bytes: 10,
            ..Default::default()
        };

        let circuit =
            UdpCircuit::connect("127.0.0.1:0".parse().unwrap(), server_addr, limits).unwrap();
        assert_eq!(circuit.peer(), server_addr);

        // Test oversized datagram rejection
        let oversized = vec![0u8; 15];
        assert!(matches!(
            circuit.send(&oversized),
            Err(TransportError::DatagramTooLarge)
        ));

        // Test valid send and receive
        let payload = b"hello";
        let sent = circuit.send(payload).unwrap();
        assert_eq!(sent, 5);

        let mut buf = [0u8; 32];
        let (len, src) = server_socket.recv_from(&mut buf).unwrap();
        assert_eq!(&buf[..len], payload);

        // Send back from server to circuit
        server_socket.send_to(b"world", src).unwrap();
        let received = circuit.receive().unwrap();
        assert_eq!(received, b"world");
    }
}
