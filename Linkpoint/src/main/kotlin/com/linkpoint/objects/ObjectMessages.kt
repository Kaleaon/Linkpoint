package com.linkpoint.objects

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

/**
 * KillObject - Remove objects from world
 */
class KillObject(
    val localIDs: List<Int>
) : SLMessage() {
    
    override fun getMessageName() = "KillObject"
    
    override fun encode(buffer: ByteBuffer) {
        // Not typically sent from client
    }
    
    override fun decode(buffer: ByteBuffer) {
        // Decoded by MessageDecoder
    }
    
    /**
     * Handle this message
     */
    override fun handleMessage(circuit: com.linkpoint.slproto.SLCircuitNew) {
        // Remove these objects from ObjectManager
        // circuit.objectManager.removeObjects(localIDs)
        com.linkpoint.Debug.Log("KillObject: ${localIDs.size} objects")
    }
}
