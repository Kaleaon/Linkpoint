package com.lumiyaviewer.lumiya.slproto.handler

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SLMessageHandlerAnnotation

// Stub for the actual handler interface/class that messages expect
interface SLMessageHandler {
    fun HandleAcceptCallingCard(msg: com.lumiyaviewer.lumiya.slproto.messages.AcceptCallingCard)
    fun HandleAcceptFriendship(msg: com.lumiyaviewer.lumiya.slproto.messages.AcceptFriendship)
    fun HandleActivateGestures(msg: com.lumiyaviewer.lumiya.slproto.messages.ActivateGestures)
    fun HandleAbortXfer(msg: com.lumiyaviewer.lumiya.slproto.messages.AbortXfer)
    fun HandleAgentAlertMessage(msg: com.lumiyaviewer.lumiya.slproto.messages.AgentAlertMessage)
    fun HandleAgentAnimation(msg: com.lumiyaviewer.lumiya.slproto.messages.AgentAnimation)
    fun HandleAgentCachedTexture(msg: com.lumiyaviewer.lumiya.slproto.messages.AgentCachedTexture)
    fun HandleAgentCachedTextureResponse(msg: com.lumiyaviewer.lumiya.slproto.messages.AgentCachedTextureResponse)
    // Add other handlers as they are encountered/needed
}
