package com.linkpoint.slproto.events;

import com.linkpoint.slproto.objects.SLObjectInfo;

public class SLObjectPayInfoEvent {
    public SLObjectInfo objectInfo;

    public SLObjectPayInfoEvent(SLObjectInfo sLObjectInfo) {
        this.objectInfo = sLObjectInfo;
    }
}
