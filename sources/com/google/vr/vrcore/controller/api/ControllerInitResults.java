package com.google.vr.vrcore.controller.api;

public class ControllerInitResults {
    public static final int FAILED_CLIENT_OBSOLETE = 3;
    public static final int FAILED_NOT_AUTHORIZED = 2;
    public static final int FAILED_UNSUPPORTED = 1;
    public static final int SUCCESS = 0;

    public static final String toString(int i) {
        switch (i) {
            case 0:
                return "SUCCESS";
            case 1:
                return "FAILED_UNSUPPORTED";
            case 2:
                return "FAILED_NOT_AUTHORIZED";
            case 3:
                return "FAILED_CLIENT_OBSOLETE";
            default:
                return new StringBuilder(45).append("[UNKNOWN CONTROLLER INIT RESULT: ").append(i).append("]").toString();
        }
    }
}
