package com.google.vr.vrcore.controller.api;

public class ControllerStates {
    public static final int CONNECTED = 3;
    public static final int CONNECTING = 2;
    public static final int DISCONNECTED = 0;
    public static final int SCANNING = 1;

    public static final String toString(int i) {
        switch (i) {
            case 0:
                return "DISCONNECTED";
            case 1:
                return "SCANNING";
            case 2:
                return "CONNECTING";
            case 3:
                return "CONNECTED";
            default:
                return new StringBuilder(39).append("[UNKNOWN CONTROLLER STATE: ").append(i).append("]").toString();
        }
    }
}
