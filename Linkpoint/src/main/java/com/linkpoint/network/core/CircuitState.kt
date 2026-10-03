package com.linkpoint.network.core

/**
 * Circuit establishment state machine.
 * Based on the reference viewer's connection lifecycle.
 * 
 * This enum tracks the progression of circuit establishment with the server.
 * The sequence is:
 * 
 * DISCONNECTED → CONNECTING → USE_CIRCUIT_CODE_SENT → USE_CIRCUIT_CODE_ACKED 
 * → COMPLETE_AGENT_MOVEMENT_SENT → COMPLETE_AGENT_MOVEMENT_ACKED → CIRCUIT_READY
 * 
 * This state machine is critical for ensuring the proper protocol sequence
 * is followed to trigger the server to send scene data.
 */
enum class CircuitState {
    /** Not connected */
    DISCONNECTED,
    
    /** Establishing UDP connection */
    CONNECTING,
    
    /** UDP connected, UseCircuitCode sent */
    USE_CIRCUIT_CODE_SENT,
    
    /** UseCircuitCode acknowledged by server */
    USE_CIRCUIT_CODE_ACKED,
    
    /** CompleteAgentMovement sent */
    COMPLETE_AGENT_MOVEMENT_SENT,
    
    /** CompleteAgentMovement acknowledged by server */
    COMPLETE_AGENT_MOVEMENT_ACKED,
    
    /** Circuit ready, scene data flowing */
    CIRCUIT_READY,
    
    /** Error state */
    ERROR
}