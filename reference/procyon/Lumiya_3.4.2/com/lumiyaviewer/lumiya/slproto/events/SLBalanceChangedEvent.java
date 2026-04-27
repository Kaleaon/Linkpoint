// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.events;

public class SLBalanceChangedEvent
{
    public final int newBalance;
    public final int oldBalance;
    public final boolean oldBalanceValid;
    
    public SLBalanceChangedEvent(final boolean oldBalanceValid, final int oldBalance, final int newBalance) {
        this.oldBalanceValid = oldBalanceValid;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
    }
}
