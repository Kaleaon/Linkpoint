package com.lumiyaviewer.lumiya.slproto;

public interface SLMessageEventListener {

    public static abstract class SLMessageBaseEventListener implements SLMessageEventListener {
        public void onMessageAcknowledged(SLMessage sLMessage) {
        }

        public void onMessageTimeout(SLMessage sLMessage) {
        }
    }

    void onMessageAcknowledged(SLMessage sLMessage);

    void onMessageTimeout(SLMessage sLMessage);
}
