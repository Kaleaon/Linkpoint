package com.linkpoint.slproto

import com.linkpoint.Debug
import com.linkpoint.slproto.auth.SLAuthReply
import java.io.IOException
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class SLThreadingCircuit : SLCircuit(), Executor {
    private const val DEFAULT_IDLE_INTERVAL: Int = 1000
    private val BlockingQueue<Runnable> queue = LinkedBlockingQueue()
    private volatile Boolean workEnabled = true
    private val Runnable workingRunnable = Runnable() {
        override fun run() {
            Debug.Printf("SLThreadingCircuit: working thread started.", Object[0])
            while (SLThreadingCircuit.this.workEnabled) {
                try {
                    val runnable: Runnable = (Runnable) SLThreadingCircuit.this.queue.poll(1000, TimeUnit.MILLISECONDS)
                    if (runnable != null) {
                        runnable.run()
                    } else {
                        SLThreadingCircuit.this.InvokeProcessIdle()
                    }
                } catch (InterruptedException e) {
                    // Thread was interrupted, restore interrupt status and exit
                    Thread.currentThread().interrupt()
                    break
                }
            }
            Debug.Printf("SLThreadingCircuit: working thread exiting.", Object[0])
        }
    }
    private val Thread workingThread = Thread(this.workingRunnable, "SLCircuit")

    public SLThreadingCircuit(SLGridConnection sLGridConnection, SLCircuitInfo sLCircuitInfo, SLAuthReply sLAuthReply, SLCircuit sLCircuit) throws IOException {
        super(sLGridConnection, sLCircuitInfo, sLAuthReply, sLCircuit)
        this.workingThread.start()
    }

     private fun stopThread() {
        this.workEnabled = false
        this.workingThread.interrupt()
    }

    fun HandleMessage(sLMessage: SLMessage) {
        this.queue.offer(() -> super.HandleMessage(sLMessage))
    }

    fun ProcessCloseCircuit() {
        stopThread()
    }

    fun ProcessNetworkError() {
        stopThread()
    }

    fun ProcessTimeout() {
        stopThread()
    }

    fun execute(runnable: Runnable) {
        this.queue.offer(runnable)
    }
}
