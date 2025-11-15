package com.lumiyaviewer.lumiya.slproto.caps

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Background poller for capability-based event queues.
 */
class SLCapEventQueue(
    private val capURL: String,
    private val eventHandler: ICapsEventHandler,
) : Runnable {

    data class CapsEvent(val eventType: CapsEventType, val eventBody: LLSDNode)

    enum class CapsEventType {
        AgentGroupDataUpdate,
        AvatarGroupsReply,
        ChatterBoxInvitation,
        ChatterBoxSessionStartReply,
        ParcelProperties,
        TeleportFailed,
        TeleportFinish,
        BulkUpdateInventory,
        EstablishAgentCommunication,
        UnknownCapsEvent,
    }

    interface ICapsEventHandler {
        fun onCapsEvent(event: CapsEvent)
    }

    private val xmlRequest = LLSDXMLRequest()
    private val nextQueue = CopyOnWriteArrayList<CapsEvent>()
    private val workerThread = Thread(this, "CapEventQueue-$capURL")

    @Volatile private var lastEventId: Int = 0
    @Volatile private var done = false
    @Volatile private var threadMustExit = false
    private val willExitGracefully = AtomicBoolean(false)

    init {
        workerThread.start()
    }

    override fun run() {
        Debug.Log("CapEventQueue: worker thread starting with capURL = $capURL")

        try {
            while (!threadMustExit) {
                try {
                    val payload = buildPollPayload()
                    val response = xmlRequest.PerformRequest(capURL, payload)

                    if (done) {
                        Debug.Log("CapEventQueue: done acknowledged, exiting thread")
                        break
                    }

                    processResponse(response)
                    dispatchPendingEvents()

                    if (!willExitGracefully.get() && !threadMustExit) {
                        Thread.sleep(POLL_DELAY_MS)
                    }
                } catch (ex: FileNotFoundException) {
                    Debug.Log("CapEventQueue: capability endpoint closed")
                    break
                } catch (ex: LLSDXMLException) {
                    Debug.Warning(ex)
                } catch (ex: IOException) {
                    Debug.Warning(ex)
                } catch (ex: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }
            }
        } finally {
            Debug.Log("CapEventQueue: worker thread exiting")
        }
    }

    private fun buildPollPayload(): LLSDMap {
        val ackNode = if (lastEventId != 0) LLSDInt(lastEventId) else LLSDUndefined()
        val doneNode = LLSDBoolean(done)
        return LLSDMap(
            arrayOf(
                LLSDMapEntry("ack", ackNode),
                LLSDMapEntry("done", doneNode),
            ),
        )
    }

    private fun processResponse(response: LLSDNode) {
        try {
            lastEventId = response.byKey("id").asInt()
        } catch (ex: LLSDException) {
            Debug.Warning(ex)
        }

        val eventsNode = try {
            response.byKey("events")
        } catch (ex: LLSDException) {
            return
        }

        for (index in 0 until eventsNode.getCount()) {
            val eventNode = eventsNode.byIndex(index)
            val name = eventNode.byKey("message").asString()
            val body = eventNode.byKey("body")
            val type = CapsEventType.values().find { it.name.equals(name, ignoreCase = true) }
                ?: CapsEventType.UnknownCapsEvent

            if (type == CapsEventType.TeleportFinish) {
                done = true
                willExitGracefully.set(true)
            }

            nextQueue.add(CapsEvent(type, body))
        }
    }

    private fun dispatchPendingEvents() {
        if (nextQueue.isEmpty()) return

        val iterator = nextQueue.iterator()
        while (iterator.hasNext() && !threadMustExit) {
            val event = iterator.next()
            iterator.remove()
            try {
                eventHandler.onCapsEvent(event)
            } catch (ex: Exception) {
                Debug.Warning(ex)
            }
        }
    }

    fun stopQueue() {
        if (threadMustExit) return
        threadMustExit = true
        xmlRequest.InterruptRequest()
        workerThread.interrupt()
    }

    companion object {
        private const val POLL_DELAY_MS = 2_500L
    }
}
