package com.lumiyaviewer.lumiya.react

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

class UIThreadExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())
    
    override fun execute(command: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            command.run()
        } else {
            handler.post(command)
        }
    }
}
