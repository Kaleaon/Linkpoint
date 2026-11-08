package com.linkpoint.render.scene.commands

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RenderCommandStream {
    private val _commands = MutableSharedFlow<SceneRenderCommand>(
        replay = 0,
        extraBufferCapacity = 512
    )
    val commands: SharedFlow<SceneRenderCommand> = _commands.asSharedFlow()

    fun publish(command: SceneRenderCommand): Boolean = _commands.tryEmit(command)
}
