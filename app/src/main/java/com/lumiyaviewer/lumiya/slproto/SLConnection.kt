package com.lumiyaviewer.lumiya.slproto

import java.nio.channels.Selector

data class SLConnection(
    var selector: Selector,
    var workingThread: Thread,
)
