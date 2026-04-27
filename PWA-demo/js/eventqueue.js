/**
 * Linkpoint PWA - Event Queue System (Features 5-8)
 * Ported from Android slproto/events/
 * Handles Second Life event queue polling and processing
 */

class EventQueue {
  constructor(protocol) {
    this.protocol = protocol;
    this.queueUrl = null;
    this.isPolling = false;
    this.pollInterval = 1000; // 1 second default
    this.maxPollInterval = 30000; // 30 seconds max
    this.currentPollInterval = this.pollInterval;
    this.pollTimer = null;
    this.ackId = null;
    this.done = false;
    this.handlers = new Map();
    this.abortController = null;
  }
  
  /**
   * Feature 5: Event queue polling
   * Start polling the event queue
   */
  start(seedCapability) {
    if (this.isPolling) {
      return;
    }
    
    this.queueUrl = seedCapability;
    this.isPolling = true;
    this.done = false;
    this.currentPollInterval = this.pollInterval;
    
    console.log('Event queue started:', this.queueUrl);
    this.poll();
  }
  
  /**
   * Stop polling
   */
  stop() {
    this.isPolling = false;
    this.done = true;
    
    if (this.pollTimer) {
      clearTimeout(this.pollTimer);
      this.pollTimer = null;
    }
    
    if (this.abortController) {
      this.abortController.abort();
      this.abortController = null;
    }
    
    console.log('Event queue stopped');
  }
  
  /**
   * Poll the event queue
   */
  async poll() {
    if (!this.isPolling || this.done) {
      return;
    }
    
    try {
      this.abortController = new AbortController();
      const timeout = setTimeout(() => this.abortController.abort(), 60000); // 60s timeout
      
      const requestBody = this.ackId ? { ack: this.ackId, done: false } : { done: false };
      
      const response = await fetch(this.queueUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/llsd+xml'
        },
        body: this.serializeLLSD(requestBody),
        signal: this.abortController.signal
      });
      
      clearTimeout(timeout);
      
      if (!response.ok) {
        throw new Error(`Event queue request failed: ${response.status}`);
      }
      
      const data = await response.json();
      this.processEvents(data);
      
      // Reset poll interval on success
      this.currentPollInterval = this.pollInterval;
      
    } catch (error) {
      if (error.name === 'AbortError') {
        console.log('Event queue poll timeout, retrying...');
      } else {
        console.error('Event queue error:', error);
        // Exponential backoff on error
        this.currentPollInterval = Math.min(
          this.currentPollInterval * 2,
          this.maxPollInterval
        );
      }
    } finally {
      // Schedule next poll
      if (this.isPolling && !this.done) {
        this.pollTimer = setTimeout(() => this.poll(), this.currentPollInterval);
      }
    }
  }
  
  /**
   * Feature 6: Event deserialization
   * Process events from the queue response
   */
  processEvents(data) {
    if (!data || !data.events) {
      return;
    }
    
    // Update ack ID for next request
    if (data.id !== undefined) {
      this.ackId = data.id;
    }
    
    // Process each event
    for (const event of data.events) {
      this.processEvent(event);
    }
  }
  
  /**
   * Process a single event
   */
  processEvent(event) {
    if (!event || !event.message) {
      return;
    }
    
    const eventName = event.message;
    const eventBody = event.body || {};
    
    console.log('Event received:', eventName, eventBody);
    
    // Call registered handlers
    const handlers = this.handlers.get(eventName) || [];
    for (const handler of handlers) {
      try {
        handler(eventBody);
      } catch (error) {
        console.error(`Error in event handler for ${eventName}:`, error);
      }
    }
    
    // Emit to protocol if available
    if (this.protocol && this.protocol.emit) {
      this.protocol.emit(eventName, eventBody);
    }
  }
  
  /**
   * Feature 7: Event handler registration
   * Register a handler for a specific event type
   */
  on(eventName, handler) {
    if (!this.handlers.has(eventName)) {
      this.handlers.set(eventName, []);
    }
    this.handlers.get(eventName).push(handler);
  }
  
  /**
   * Unregister a handler
   */
  off(eventName, handler) {
    if (!this.handlers.has(eventName)) {
      return;
    }
    
    const handlers = this.handlers.get(eventName);
    const index = handlers.indexOf(handler);
    if (index !== -1) {
      handlers.splice(index, 1);
    }
  }
  
  /**
   * Feature 8: Capability-based event processing
   * Set up event queue from seed capability
   */
  async setupFromSeed(seedCapUrl) {
    try {
      const response = await fetch(seedCapUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/llsd+xml'
        },
        body: this.serializeLLSD({ capabilities: [] })
      });
      
      if (!response.ok) {
        throw new Error('Failed to get capabilities from seed');
      }
      
      const caps = await response.json();
      
      // Extract event queue URL
      if (caps.EventQueueGet) {
        this.start(caps.EventQueueGet);
      }
      
      return caps;
    } catch (error) {
      console.error('Failed to setup event queue from seed:', error);
      throw error;
    }
  }
  
  /**
   * Simple LLSD serialization
   */
  serializeLLSD(obj) {
    let xml = '<?xml version="1.0" encoding="UTF-8"?>\n<llsd>\n';
    xml += this.serializeValue(obj);
    xml += '</llsd>';
    return xml;
  }
  
  serializeValue(val) {
    if (val === null || val === undefined) {
      return '<undef />\n';
    }
    
    if (typeof val === 'boolean') {
      return `<boolean>${val ? 1 : 0}</boolean>\n`;
    }
    
    if (typeof val === 'number') {
      return Number.isInteger(val) ? 
        `<integer>${val}</integer>\n` : 
        `<real>${val}</real>\n`;
    }
    
    if (typeof val === 'string') {
      return `<string>${this.escapeXml(val)}</string>\n`;
    }
    
    if (Array.isArray(val)) {
      let xml = '<array>\n';
      for (const item of val) {
        xml += this.serializeValue(item);
      }
      xml += '</array>\n';
      return xml;
    }
    
    if (typeof val === 'object') {
      let xml = '<map>\n';
      for (const [key, value] of Object.entries(val)) {
        xml += `<key>${this.escapeXml(key)}</key>\n`;
        xml += this.serializeValue(value);
      }
      xml += '</map>\n';
      return xml;
    }
    
    return '<undef />\n';
  }
  
  escapeXml(str) {
    return str.replace(/&/g, '&amp;')
              .replace(/</g, '&lt;')
              .replace(/>/g, '&gt;')
              .replace(/"/g, '&quot;')
              .replace(/'/g, '&apos;');
  }
}

// Export
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { EventQueue };
}
