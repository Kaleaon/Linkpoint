package com.lumiyaviewer.lumiya.slproto.events;

public class SLBalanceChangedEvent {
    public final int newBalance;
    public final int oldBalance;
    public final boolean oldBalanceValid;

    public SLBalanceChangedEvent(boolean z, int i, int i2) {
        this.oldBalanceValid = z;
        this.oldBalance = i;
        this.newBalance = i2;
    }
}
