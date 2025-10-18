/**
 * Unit tests for EventQueue module
 * Tests event queue polling, handler registration, and event processing
 */

// Import the module
const { EventQueueManager } = require('../../js/phase2/eventqueue.js');

describe('EventQueueManager', () => {
  let eventQueue;
  let mockProtocol;

  beforeEach(() => {
    mockProtocol = {
      send: jest.fn(),
      receive: jest.fn()
    };
    eventQueue = new EventQueueManager(mockProtocol);
  });

  describe('Constructor', () => {
    test('should throw error if protocol is not provided', () => {
      expect(() => new EventQueueManager()).toThrow('Protocol instance is required');
    });

    test('should initialize with default values', () => {
      expect(eventQueue.queueUrl).toBeNull();
      expect(eventQueue.isPolling).toBe(false);
      expect(eventQueue.pollInterval).toBe(1000);
      expect(eventQueue.handlers.size).toBe(0);
      expect(eventQueue.eventBuffer).toEqual([]);
    });
  });

  describe('startPolling', () => {
    test('should throw error if seed capability is invalid', async () => {
      await expect(eventQueue.startPolling()).rejects.toThrow('Valid seed capability URL required');
      await expect(eventQueue.startPolling(123)).rejects.toThrow('Valid seed capability URL required');
    });

    test('should start polling with valid seed capability', async () => {
      const seedUrl = 'https://sim.example.com/cap/eventqueue';
      await eventQueue.startPolling(seedUrl);
      
      expect(eventQueue.queueUrl).toBe(seedUrl);
      expect(eventQueue.isPolling).toBe(true);
    });
  });

  describe('stopPolling', () => {
    test('should stop polling and clear buffer', async () => {
      await eventQueue.startPolling('https://example.com/queue');
      eventQueue.enqueueEvent({ message: 'test' });
      
      eventQueue.stopPolling();
      
      expect(eventQueue.isPolling).toBe(false);
      expect(eventQueue.eventBuffer).toEqual([]);
    });
  });

  describe('registerHandler', () => {
    test('should throw error if event name or handler is invalid', () => {
      expect(() => eventQueue.registerHandler()).toThrow('Event name and handler function required');
      expect(() => eventQueue.registerHandler('test')).toThrow('Event name and handler function required');
      expect(() => eventQueue.registerHandler('test', 'not a function')).toThrow('Event name and handler function required');
    });

    test('should register handler for event', () => {
      const handler = jest.fn();
      eventQueue.registerHandler('ChatFromSimulator', handler);
      
      expect(eventQueue.handlers.has('ChatFromSimulator')).toBe(true);
      expect(eventQueue.handlers.get('ChatFromSimulator')).toContain(handler);
    });

    test('should allow multiple handlers for same event', () => {
      const handler1 = jest.fn();
      const handler2 = jest.fn();
      
      eventQueue.registerHandler('ChatFromSimulator', handler1);
      eventQueue.registerHandler('ChatFromSimulator', handler2);
      
      const handlers = eventQueue.handlers.get('ChatFromSimulator');
      expect(handlers.length).toBe(2);
      expect(handlers).toContain(handler1);
      expect(handlers).toContain(handler2);
    });
  });

  describe('enqueueEvent', () => {
    test('should throw error if event data is invalid', () => {
      expect(() => eventQueue.enqueueEvent()).toThrow('Valid event data object required');
      expect(() => eventQueue.enqueueEvent('invalid')).toThrow('Valid event data object required');
    });

    test('should enqueue valid event', () => {
      const event = { message: 'ChatFromSimulator', body: { text: 'Hello' } };
      eventQueue.enqueueEvent(event);
      
      expect(eventQueue.eventBuffer.length).toBe(1);
      expect(eventQueue.eventBuffer[0]).toEqual(event);
    });

    test('should enqueue multiple events', () => {
      eventQueue.enqueueEvent({ message: 'Event1' });
      eventQueue.enqueueEvent({ message: 'Event2' });
      eventQueue.enqueueEvent({ message: 'Event3' });
      
      expect(eventQueue.eventBuffer.length).toBe(3);
    });
  });

  describe('dequeueEvent', () => {
    test('should return null if buffer is empty', () => {
      expect(eventQueue.dequeueEvent()).toBeNull();
    });

    test('should dequeue events in FIFO order', () => {
      const event1 = { message: 'Event1' };
      const event2 = { message: 'Event2' };
      
      eventQueue.enqueueEvent(event1);
      eventQueue.enqueueEvent(event2);
      
      expect(eventQueue.dequeueEvent()).toEqual(event1);
      expect(eventQueue.dequeueEvent()).toEqual(event2);
      expect(eventQueue.dequeueEvent()).toBeNull();
    });
  });

  describe('processEvents', () => {
    test('should process events with registered handlers', () => {
      const handler = jest.fn();
      const event = { message: 'ChatFromSimulator', body: { text: 'Hello' } };
      
      eventQueue.registerHandler('ChatFromSimulator', handler);
      eventQueue.enqueueEvent(event);
      eventQueue.processEvents();
      
      expect(handler).toHaveBeenCalledWith(event);
      expect(eventQueue.eventBuffer.length).toBe(0);
    });

    test('should handle events without registered handlers', () => {
      const event = { message: 'UnknownEvent' };
      
      eventQueue.enqueueEvent(event);
      eventQueue.processEvents();
      
      // Should not throw, just ignore
      expect(eventQueue.eventBuffer.length).toBe(0);
    });

    test('should call all registered handlers for an event', () => {
      const handler1 = jest.fn();
      const handler2 = jest.fn();
      const event = { message: 'ChatFromSimulator' };
      
      eventQueue.registerHandler('ChatFromSimulator', handler1);
      eventQueue.registerHandler('ChatFromSimulator', handler2);
      eventQueue.enqueueEvent(event);
      eventQueue.processEvents();
      
      expect(handler1).toHaveBeenCalledWith(event);
      expect(handler2).toHaveBeenCalledWith(event);
    });

    test('should continue processing if handler throws error', () => {
      const handler1 = jest.fn(() => { throw new Error('Handler error'); });
      const handler2 = jest.fn();
      const event = { message: 'TestEvent' };
      
      eventQueue.registerHandler('TestEvent', handler1);
      eventQueue.registerHandler('TestEvent', handler2);
      eventQueue.enqueueEvent(event);
      eventQueue.processEvents();
      
      expect(handler1).toHaveBeenCalled();
      expect(handler2).toHaveBeenCalled();
    });
  });

  describe('getStats', () => {
    test('should return correct statistics', async () => {
      await eventQueue.startPolling('https://example.com/queue');
      eventQueue.registerHandler('Event1', jest.fn());
      eventQueue.registerHandler('Event2', jest.fn());
      eventQueue.enqueueEvent({ message: 'test1' });
      eventQueue.enqueueEvent({ message: 'test2' });
      
      const stats = eventQueue.getStats();
      
      expect(stats.isPolling).toBe(true);
      expect(stats.queueUrl).toBe('https://example.com/queue');
      expect(stats.bufferedEvents).toBe(2);
      expect(stats.handlerCount).toBe(2);
    });
  });
});
