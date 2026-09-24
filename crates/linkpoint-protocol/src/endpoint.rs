use std::collections::HashSet;
use url::Url;

/// Network policy applied before sending credentials or capability tokens.
#[derive(Debug, Clone)]
pub struct EndpointPolicy {
    login_hosts: HashSet<String>,
    allow_local_http: bool,
}

#[derive(Debug, thiserror::Error, PartialEq, Eq)]
pub enum EndpointPolicyError {
    #[error("invalid endpoint URL")]
    InvalidUrl,
    #[error("endpoint must use HTTPS")]
    InsecureTransport,
    #[error("endpoint has no host")]
    MissingHost,
    #[error("login host is not allowlisted: {0}")]
    HostNotAllowed(String),
    #[error("URL must not contain embedded credentials")]
    EmbeddedCredentials,
}

impl Default for EndpointPolicy {
    fn default() -> Self {
        Self::second_life()
    }
}

impl EndpointPolicy {
    pub fn second_life() -> Self {
        Self {
            login_hosts: HashSet::from([
                "login.agni.lindenlab.com".into(),
                "login.aditi.lindenlab.com".into(),
            ]),
            allow_local_http: false,
        }
    }

    pub fn allow_login_host(&mut self, host: impl Into<String>) {
        self.login_hosts.insert(host.into().to_ascii_lowercase());
    }

    pub fn allow_local_http(&mut self, allow: bool) {
        self.allow_local_http = allow;
    }

    pub fn validate_login(&self, endpoint: &str) -> Result<Url, EndpointPolicyError> {
        let url = self.validate_url(endpoint)?;
        let host = url
            .host_str()
            .ok_or(EndpointPolicyError::MissingHost)?
            .to_ascii_lowercase();
        if !self.login_hosts.contains(&host) {
            return Err(EndpointPolicyError::HostNotAllowed(host));
        }
        Ok(url)
    }

    /// Validate a seed/capability URL. Capability hosts are simulator-provided,
    /// so they are not constrained to the login allowlist, but TLS is required.
    pub fn validate_capability(&self, endpoint: &str) -> Result<Url, EndpointPolicyError> {
        self.validate_url(endpoint)
    }

    fn validate_url(&self, endpoint: &str) -> Result<Url, EndpointPolicyError> {
        let url = Url::parse(endpoint).map_err(|_| EndpointPolicyError::InvalidUrl)?;
        if !url.username().is_empty() || url.password().is_some() {
            return Err(EndpointPolicyError::EmbeddedCredentials);
        }
        let local = matches!(
            url.host_str(),
            Some("localhost" | "127.0.0.1" | "[::1]" | "::1")
        );
        if url.scheme() != "https" && !(self.allow_local_http && local && url.scheme() == "http") {
            return Err(EndpointPolicyError::InsecureTransport);
        }
        if url.host_str().is_none() {
            return Err(EndpointPolicyError::MissingHost);
        }
        Ok(url)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn defaults_to_production_and_beta_sl_grids() {
        let policy = EndpointPolicy::default();
        assert!(
            policy
                .validate_login("https://login.agni.lindenlab.com/cgi-bin/login.cgi")
                .is_ok()
        );
        assert!(
            policy
                .validate_login("https://login.aditi.lindenlab.com/cgi-bin/login.cgi")
                .is_ok()
        );
    }

    #[test]
    fn custom_grids_require_explicit_host_consent_and_tls() {
        let mut policy = EndpointPolicy::default();
        assert!(matches!(
            policy.validate_login("https://grid.example/login"),
            Err(EndpointPolicyError::HostNotAllowed(_))
        ));
        policy.allow_login_host("grid.example");
        assert!(policy.validate_login("https://grid.example/login").is_ok());
        assert_eq!(
            policy
                .validate_login("http://grid.example/login")
                .unwrap_err(),
            EndpointPolicyError::InsecureTransport
        );
    }

    #[test]
    fn local_http_is_an_explicit_development_exception() {
        let mut policy = EndpointPolicy::default();
        policy.allow_login_host("localhost");
        assert!(policy.validate_login("http://localhost:9000/").is_err());
        policy.allow_local_http(true);
        assert!(policy.validate_login("http://localhost:9000/").is_ok());
    }
}
