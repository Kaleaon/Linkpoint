package com.lumiyaviewer.lumiya.slproto.events;

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;

public class SLObjectPayInfoEvent {
    public SLObjectInfo objectInfo;

    public SLObjectPayInfoEvent(SLObjectInfo sLObjectInfo) {
        this.objectInfo = sLObjectInfo;
    }
}
