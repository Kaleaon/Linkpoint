package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

public enum MuteType {
    BY_NAME(2),
    AGENT(0),
    OBJECT(1),
    GROUP(3),
    EXTERNAL(4);
    
    private int viewOrder;

    private MuteType(int i) {
        this.viewOrder = i;
    }

    public int getViewOrder() {
        return this.viewOrder;
    }
}
