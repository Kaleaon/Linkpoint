use std::collections::HashMap;

use crate::llsd::Value;

#[derive(Debug, thiserror::Error, PartialEq, Eq)]
pub enum CapabilityError {
    #[error("invalid capability response: {0}")]
    Invalid(&'static str),
}

/// Named URLs returned by a simulator seed capability.
#[derive(Debug, Clone, Default, PartialEq, Eq)]
pub struct CapabilitySet(HashMap<String, String>);

impl CapabilitySet {
    pub fn from_llsd(value: &Value) -> Result<Self, CapabilityError> {
        let map = value
            .as_map()
            .ok_or(CapabilityError::Invalid("expected map"))?;
        let mut capabilities = HashMap::with_capacity(map.len());
        for (name, value) in map {
            let uri = match value {
                Value::URI(value) | Value::String(value) => value,
                _ => {
                    return Err(CapabilityError::Invalid(
                        "capability URL must be a URI or string",
                    ));
                }
            };
            capabilities.insert(name.clone(), uri.clone());
        }
        Ok(Self(capabilities))
    }

    pub fn get(&self, name: &str) -> Option<&str> {
        self.0.get(name).map(String::as_str)
    }

    pub fn iter(&self) -> impl Iterator<Item = (&str, &str)> {
        self.0
            .iter()
            .map(|(name, uri)| (name.as_str(), uri.as_str()))
    }
}

#[derive(Debug, Clone, PartialEq)]
pub struct Event {
    pub message: String,
    pub body: Value,
}

#[derive(Debug, Clone, PartialEq)]
pub struct EventQueue {
    pub id: Value,
    pub events: Vec<Event>,
}

impl EventQueue {
    /// Parse an EventQueueGet response. The ID remains an LLSD value because
    /// deployed simulators have historically returned both integers and strings.
    pub fn from_llsd(value: &Value) -> Result<Self, CapabilityError> {
        let map = value
            .as_map()
            .ok_or(CapabilityError::Invalid("event queue must be a map"))?;
        let id = map
            .get("id")
            .cloned()
            .ok_or(CapabilityError::Invalid("missing event queue id"))?;
        let raw_events = map
            .get("events")
            .and_then(Value::as_array)
            .ok_or(CapabilityError::Invalid("missing events array"))?;
        let events = raw_events
            .iter()
            .map(|value| {
                let event = value
                    .as_map()
                    .ok_or(CapabilityError::Invalid("event must be a map"))?;
                let message = event
                    .get("message")
                    .and_then(Value::as_string)
                    .ok_or(CapabilityError::Invalid("event message must be a string"))?
                    .clone();
                let body = event
                    .get("body")
                    .cloned()
                    .ok_or(CapabilityError::Invalid("event body is missing"))?;
                Ok(Event { message, body })
            })
            .collect::<Result<_, _>>()?;
        Ok(Self { id, events })
    }

    /// Body for the next long poll. Passing `None` creates the initial request.
    pub fn request_body(previous_id: Option<Value>, done: bool) -> Value {
        let mut map = HashMap::from([("done".into(), Value::Boolean(done))]);
        if let Some(id) = previous_id {
            map.insert("ack".into(), id);
        }
        Value::Map(map)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn parses_seed_and_event_queue_shapes() {
        let caps = CapabilitySet::from_llsd(&Value::Map(HashMap::from([(
            "EventQueueGet".into(),
            Value::URI("https://sim.example/cap/event".into()),
        )])))
        .unwrap();
        assert_eq!(
            caps.get("EventQueueGet"),
            Some("https://sim.example/cap/event")
        );

        let queue = EventQueue::from_llsd(&Value::Map(HashMap::from([
            ("id".into(), Value::Integer(7)),
            (
                "events".into(),
                Value::Array(vec![Value::Map(HashMap::from([
                    ("message".into(), Value::String("ChatFromSimulator".into())),
                    ("body".into(), Value::Map(HashMap::new())),
                ]))]),
            ),
        ])))
        .unwrap();
        assert_eq!(queue.events[0].message, "ChatFromSimulator");
        assert!(
            matches!(EventQueue::request_body(Some(queue.id), false), Value::Map(map) if map.contains_key("ack"))
        );
    }
}
