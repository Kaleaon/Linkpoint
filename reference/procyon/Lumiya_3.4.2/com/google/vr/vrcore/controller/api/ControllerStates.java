// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.vrcore.controller.api;

public class ControllerStates
{
    public static final int CONNECTED = 3;
    public static final int CONNECTING = 2;
    public static final int DISCONNECTED = 0;
    public static final int SCANNING = 1;
    
    public static final String toString(final int i) {
        switch (i) {
            default: {
                return new StringBuilder(39).append("[UNKNOWN CONTROLLER STATE: ").append(i).append("]").toString();
            }
            case 0: {
                return "DISCONNECTED";
            }
            case 1: {
                return "SCANNING";
            }
            case 2: {
                return "CONNECTING";
            }
            case 3: {
                return "CONNECTED";
            }
        }
    }
}
