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
  private processedCount: number = 0;

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

        const contentType = (response.headers?.get('content-type') || '').toLowerCase();
        let data: any;

        if (contentType.includes('application/llsd+binary')) {
          const buffer = new Uint8Array(await (response as any).arrayBuffer());
          data = LLSD.parseBinary(buffer);
        } else if (contentType.includes('application/llsd+notation') || contentType.includes('text/plain')) {
          const text = await response.text();
          data = LLSD.parseNotation(text);
        } else {
          const text = await response.text();
          data = LLSD.parseXML(text);
        }

        if (data && data.events) {
          this.enqueueEvents(data.events);
          if (data.id) this.ackId = data.id;
        }
      } else if (response && (response.status === 502 || response.status === 504)) {
        // Benign long-poll timeout (HTTP 502 / 504): reset delay immediately without backoff
        this.currentDelay = this.baseDelay;
        console.log(`[EventQueue] Benign long-poll timeout (${response.status}). Re-polling immediately.`);
      } else {
        // Exponential backoff for HTTP 500/503 or true network errors
        this.currentDelay = Math.min(this.currentDelay * 2, this.maxDelay);
        console.warn(`[EventQueue] Polling error. Backing off to ${this.currentDelay}ms`);
      }
    } catch (error: any) {
      if (error?.name === 'TimeoutError' || error?.message?.includes('timeout')) {
        // Read timeout during long poll: reset delay immediately
        this.currentDelay = this.baseDelay;
      } else {
        this.currentDelay = Math.min(this.currentDelay * 2, this.maxDelay);
        console.error(`[EventQueue] Polling exception:`, error);
        console.warn(`[EventQueue] Backing off to ${this.currentDelay}ms`);
      }
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

  unregisterHandler(eventName: string, handler: Function) {
    if (!eventName || !this.handlers.has(eventName)) return;
    const list = this.handlers.get(eventName)!.filter(h => h !== handler);
    if (list.length > 0) {
      this.handlers.set(eventName, list);
    } else {
      this.handlers.delete(eventName);
    }
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
    this.processEvents();
  }

  /**
   * Batch enqueue multiple events from an EventQueue poll response
   * and process them in a single coalesced pass.
   */
  enqueueEvents(events: any[]) {
    if (!Array.isArray(events) || events.length === 0) return;
    for (const event of events) {
      if (event && typeof event === 'object') {
        this.eventBuffer.push(event);
      }
    }
    this.processEvents();
  }

  /**
   * Feature 8: Capability-based event processing
   * Process events efficiently, batching avatar presence and coordinate updates
   * so they are dispatched cleanly to protocol and WorldViewer's scene graph.
   */
  processEvents() {
    if (this.eventBuffer.length === 0) return;

    const eventsToProcess = this.eventBuffer.splice(0, this.eventBuffer.length);
    const batchedAvatars: any[] = [];
    const regularEvents: any[] = [];

    // Triage events in the current batch
    for (const event of eventsToProcess) {
      this.processedCount++;
      const eventName = event?.message || event?.type || '';
      const eventBody = event?.body ?? event?.data ?? event;
      const lower = String(eventName).toLowerCase().replace(/[-_]/g, '');

      // Check for avatar presence / coordinate events
      if (lower === 'avatarpresence') {
        const items = Array.isArray(eventBody)
          ? eventBody
          : Array.isArray(eventBody?.AgentData)
          ? eventBody.AgentData
          : Array.isArray(eventBody?.agents)
          ? eventBody.agents
          : Array.isArray(eventBody?.avatars)
          ? eventBody.avatars
          : [eventBody];
        batchedAvatars.push(...items.filter(Boolean));
      } else {
        regularEvents.push({ eventName, eventBody, rawEvent: event });
      }

      // Dispatch to custom registered handlers
      if (eventName) {
        this.dispatchToHandlers(eventName, event);
      }
    }

    // 1. Dispatch batched avatar presence in a single consolidated pass to WorldViewer
    if (batchedAvatars.length > 0 && this.protocol && typeof this.protocol.emit === 'function') {
      // Coalesce updates by avatar ID to keep the latest coordinates/presence state
      const latestById = new Map<string, any>();
      for (const item of batchedAvatars) {
        const rawId = item.id || item.agentId || item.AgentID || item.agent_id || item.avatar_id || item.avatarId || item.uuid || '';
        const id = String(typeof rawId === 'object' && rawId?.toString ? rawId.toString() : rawId).trim();
        if (id) {
          latestById.set(id, item);
        } else {
          // If no specific ID, keep item in array
          latestById.set(Math.random().toString(), item);
        }
      }

      const consolidatedPayload = { AgentData: Array.from(latestById.values()) };
      this.protocol.emit('avatar_presence', consolidatedPayload);
    }

    // 2. Dispatch remaining simulator events
    for (const { eventName, eventBody } of regularEvents) {
      if (!eventName || !this.protocol || typeof this.protocol.emit !== 'function') continue;

      const lower = String(eventName).toLowerCase().replace(/[-_]/g, '');

      if (lower === 'coarselocationupdate') {
        this.protocol.emit('CoarseLocationUpdate', eventBody);
      } else if (lower === 'coarseavatarupdate') {
        this.protocol.emit('CoarseAvatarUpdate', eventBody);
      } else if (lower === 'agentmovementcomplete') {
        this.protocol.emit('AgentMovementComplete', eventBody);
      } else if (lower === 'objectupdate' || lower === 'improvedterseobjectupdate') {
        this.protocol.emit('ObjectUpdate', eventBody);
      } else {
        this.protocol.emit(eventName, eventBody);
      }
    }
  }

  private dispatchToHandlers(eventName: string, event: any) {
    const lowerNorm = String(eventName).toLowerCase().replace(/[-_]/g, '');
    const handledSet = new Set<Function>();

    for (const [registeredName, handlers] of this.handlers.entries()) {
      const regNorm = String(registeredName).toLowerCase().replace(/[-_]/g, '');
      if (regNorm === lowerNorm || registeredName === eventName) {
        handlers.forEach(handler => {
          if (handledSet.has(handler)) return;
          handledSet.add(handler);
          try {
            handler(event);
          } catch (error) {
            console.error(`[EventQueue] Handler error for ${registeredName}:`, error);
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
      handlerCount: this.handlers.size,
      processedEvents: this.processedCount
    };
  }
}
