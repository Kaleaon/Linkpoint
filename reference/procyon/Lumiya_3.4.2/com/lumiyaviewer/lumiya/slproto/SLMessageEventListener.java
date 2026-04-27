// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto;

public interface SLMessageEventListener
{
    void onMessageAcknowledged(final SLMessage p0);
    
    void onMessageTimeout(final SLMessage p0);
    
    public abstract static class SLMessageBaseEventListener implements SLMessageEventListener
    {
        @Override
        public void onMessageAcknowledged(final SLMessage slMessage) {
        }
        
        @Override
        public void onMessageTimeout(final SLMessage slMessage) {
        }
    }
}
