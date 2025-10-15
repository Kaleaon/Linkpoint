package com.lumiyaviewer.lumiya.slproto

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply
import java.io.IOException
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class SLThreadingCircuit : SLCircuit : Executor {
    private Int DEFAULT_IDLE_INTERVAL = 1000
    private val queue: BlockingQueue<Runnable> = LinkedBlockingQueue()
    private volatile Boolean workEnabled = true
    private val workingRunnable: Runnable = Runnable() {
        fun run(): Unit {
            Debug.Printf("SLThreadingCircuit: working thread started.", Any[0])
            while (SLThreadingCircuit.this.workEnabled) {
                try {
                    Runnable runnable = (Runnable) SLThreadingCircuit.this.queue.poll(1000, TimeUnit.MILLISECONDS)
                    runnable?.run()
                    } else {
                        SLThreadingCircuit.this.InvokeProcessIdle()
                    }
                } catch (InterruptedException e) {
                    // Thread was interrupted, restore interrupt status and exit
                    Thread.currentThread().interrupt()
                    break
                }
            }
            Debug.Printf("SLThreadingCircuit: working thread exiting.", Any[0])
        }
    }
    private val workingThread: Thread = Thread(this.workingRunnable, "SLCircuit")

    SLThreadingCircuit(SLGridConnection sLGridConnection, SLCircuitInfo sLCircuitInfo, SLAuthReply sLAuthReply, SLCircuit sLCircuit) throws IOException {
        super(sLGridConnection, sLCircuitInfo, sLAuthReply, sLCircuit)
        this.workingThread.start()
    }

    private fun stopThread(): Unit {
        this.workEnabled = false
        this.workingThread.interrupt()
    }

    fun HandleMessage(sLMessage: SLMessage): Unit {
        this.queue.offer(() -> super.HandleMessage(sLMessage))
    }

    fun ProcessCloseCircuit(): Unit {
        stopThread()
    }

    fun ProcessNetworkError(): Unit {
        stopThread()
    }

    fun ProcessTimeout(): Unit {
        stopThread()
    }

    fun execute(runnable: Runnable): Unit {
        this.queue.offer(runnable)
    }
}
