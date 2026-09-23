/**
 * Linkpoint PWA - Event Queue System (Features 5-8)
 *
 * Phase 2: Core Protocol Extensions - Priority 1
 * Roadmap: PWA-demo/ANDROID_PORT_ROADMAP.md (Lines 21-26)
 * Android Source: app/src/main/java/com/lumiyaviewer/lumiya/slproto/modules/
 *
 * Handles Second Life event queue polling and processing.
 */

import { SLProtocol } from '../sl-protocol-real';
import { corsHandler } from '../cors-handler';
import { LLSD } from '../llsd';

export class EventQueueManager {
  private protocol: SLProtocol;
  public queueUrl: string | null = null;
  public isPolling: boolean = false;
  private pollInterval: number = 1000;
  private handlers: Map<string, Function[]> = new Map();
  private eventBuffer: any[] = [];
  private ackId: number | null = null;
  private baseDelay: number = 1000;
  private maxDelay: number = 30000;
  private currentDelay: number = 1000;

  constructor(protocol: SLProtocol) {
    if (!protocol) throw new Error('Protocol instance is required');
    this.protocol = protocol;
  }

  /**
   * Feature 5: Event queue polling
   * Start polling the event queue with exponential backoff
   */
  async startPolling(seedCapability: string) {
    if (!seedCapability || typeof seedCapability !== 'string') {
      throw new Error('Valid seed capability URL required');
    }

    this.queueUrl = seedCapability;
    this.isPolling = true;
    this.currentDelay = this.baseDelay;
    this.ackId = null;
    console.log('[EventQueue] Started polling:', this.queueUrl);

    this.pollLoop();
    return Promise.resolve();
  }

  private async pollLoop() {
    if (!this.isPolling || !this.queueUrl) return;

    try {
      const body = this.ackId !== null
        ? LLSD.buildXML({ ack: this.ackId, done: false })
        : LLSD.buildXML({ done: false });

      const response = await corsHandler.makeRequest(this.queueUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/llsd+xml' },
        body
      });

      if (response && response.ok) {
        // Reset delay on success
        this.currentDelay = this.baseDelay;

        const text = await response.text();
        const data = LLSD.parseXML(text);

        if (data && data.events) {
          data.events.forEach((event: any) => this.enqueueEvent(event));
          if (data.id) this.ackId = data.id;
        }
      } else {
        // Exponential backoff
        this.currentDelay = Math.min(this.currentDelay * 2, this.maxDelay);
        console.warn(`[EventQueue] Polling error. Backing off to ${this.currentDelay}ms`);
      }
    } catch (error) {
      // Exponential backoff
      this.currentDelay = Math.min(this.currentDelay * 2, this.maxDelay);
      console.error(`[EventQueue] Polling exception:`, error);
      console.warn(`[EventQueue] Backing off to ${this.currentDelay}ms`);
    }

    if (this.isPolling) {
      setTimeout(() => this.pollLoop(), this.currentDelay);
    }
  }

  stopPolling() {
    this.isPolling = false;
    this.eventBuffer = [];
    console.log('[EventQueue] Stopped polling');
  }

  /**
   * Feature 7: Event handler registration
   * Register a handler for specific event types
   */
  registerHandler(eventName: string, handler: Function) {
    if (!eventName || typeof handler !== 'function') {
      throw new Error('Event name and handler function required');
    }

    if (!this.handlers.has(eventName)) {
      this.handlers.set(eventName, []);
    }

    this.handlers.get(eventName)!.push(handler);
    console.log(`[EventQueue] Registered handler for: ${eventName}`);
  }

  /**
   * Feature 6: Event deserialization
   * Deserialize and enqueue an event
   */
  enqueueEvent(eventData: any) {
    if (!eventData || typeof eventData !== 'object') {
      throw new Error('Valid event data object required');
    }

    this.eventBuffer.push(eventData);
    console.log('[EventQueue] Event enqueued:', eventData.message || 'unknown');
    this.processEvents();
  }

  /**
   * Feature 8: Capability-based event processing
   * Process events using registered handlers
   */
  processEvents() {
    while (this.eventBuffer.length > 0) {
      const event = this.eventBuffer.shift();
      const eventName = event?.message;

      if (eventName && this.handlers.has(eventName)) {
        const handlers = this.handlers.get(eventName)!;
        handlers.forEach(handler => {
          try {
            handler(event);
          } catch (error) {
            console.error(`[EventQueue] Handler error for ${eventName}:`, error);
          }
        });
      }
    }
  }

  getStats() {
    return {
      isPolling: this.isPolling,
      queueUrl: this.queueUrl,
      bufferedEvents: this.eventBuffer.length,
      handlerCount: this.handlers.size
    };
  }
}
