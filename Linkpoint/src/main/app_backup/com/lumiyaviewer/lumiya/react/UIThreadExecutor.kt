package com.lumiyaviewer.lumiya.react
import java.util.concurrent.Executor
import android.os.Handler
import android.os.Looper

class UIThreadExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())
    
    companion object {
        private val instance = UIThreadExecutor()
        fun getInstance(): UIThreadExecutor = instance
    }
    
    override fun execute(command: Runnable) {
        handler.post(command)
    }
}
