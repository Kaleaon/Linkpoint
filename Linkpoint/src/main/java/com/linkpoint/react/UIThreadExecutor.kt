package com.linkpoint.react

import java.util.concurrent.Executor

class UIThreadExecutor : Executor {
    override fun execute(command: Runnable) {
        // Stub implementation
        command.run()
    }
}