package com.google.vr.sdk.controller;

import android.graphics.PointF;

public class Controller {
    private static final int DURATION_REQUIRED_TO_RECENTER_NS = 600000000;
    static final int MIN_VRCORE_API_WITH_RECENTERING = 8;
    private static final float RECENTER_PITCH_BOUNDS_RADIANS = ((float) Math.toRadians(30.0d));
    public boolean appButtonState;
    public boolean clickButtonState;
    private EventListener controllerEventListener = new EventListener();
    private final Orientation controllerPoseInSensorSpace = new Orientation();
    boolean enableRecenterShim;
    private long homeButtonDownTimestamp;
    public boolean homeButtonState;
    public boolean isTouching;
    private final ControllerManager manager;
    public final Orientation orientation = new Orientation();
    public final float[] position = new float[3];
    private Orientation startFromSensorTransformation = new Orientation();
    public long timestamp = 0;
    public final PointF touch = new PointF();
    public boolean volumeDownButtonState;
    public boolean volumeUpButtonState;

    public static class ConnectionStates {
        public static final int CONNECTED = 3;
        public static final int CONNECTING = 2;
        public static final int DISCONNECTED = 0;
        public static final int SCANNING = 1;

        private ConnectionStates() {
        }

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

    public static class EventListener {
        public void onConnectionStateChanged(int i) {
        }

        public void onUpdate() {
        }
    }

    Controller(ControllerManager controllerManager) {
        this.manager = controllerManager;
    }

    private void setStartFromSensorTransformation() {
        float[] eulerAngles = this.controllerPoseInSensorSpace.toEulerAngles(new float[3]);
        this.startFromSensorTransformation = new Orientation(0.0f, (float) Math.sin((double) ((-eulerAngles[1]) / 2.0f)), 0.0f, (float) Math.cos((double) (eulerAngles[1] / 2.0f)));
    }

    /* access modifiers changed from: package-private */
    public void notifyConnectionStateChange(int i) {
        this.controllerEventListener.onConnectionStateChanged(i);
    }

    /* access modifiers changed from: package-private */
    public void notifyUpdate() {
        this.controllerEventListener.onUpdate();
    }

    public void setEventListener(EventListener eventListener) {
        this.controllerEventListener = eventListener;
    }

    /* access modifiers changed from: package-private */
    public boolean setHomeButtonState(boolean z) {
        this.homeButtonState = z;
        if (!this.enableRecenterShim) {
            return false;
        }
        if (!this.homeButtonState) {
            if (!(this.timestamp - this.homeButtonDownTimestamp <= 600000000) && Math.abs(this.controllerPoseInSensorSpace.toEulerAngles(new float[3])[0]) < RECENTER_PITCH_BOUNDS_RADIANS) {
                setStartFromSensorTransformation();
                return true;
            }
        } else {
            this.homeButtonDownTimestamp = this.timestamp;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setOrientationInSensorSpace(float f, float f2, float f3, float f4) {
        this.controllerPoseInSensorSpace.set(f, f2, f3, f4);
        this.orientation.set(this.startFromSensorTransformation);
        this.orientation.multiply(this.controllerPoseInSensorSpace);
    }

    /* access modifiers changed from: package-private */
    public void setPublicState(Controller controller) {
        this.timestamp = controller.timestamp;
        this.orientation.w = controller.orientation.w;
        this.orientation.x = controller.orientation.x;
        this.orientation.y = controller.orientation.y;
        this.orientation.z = controller.orientation.z;
        this.isTouching = controller.isTouching;
        this.touch.x = controller.touch.x;
        this.touch.y = controller.touch.y;
        System.arraycopy(controller.position, 0, this.position, 0, this.position.length);
        this.appButtonState = controller.appButtonState;
        this.homeButtonState = controller.homeButtonState;
        this.clickButtonState = controller.clickButtonState;
        this.volumeDownButtonState = controller.volumeDownButtonState;
        this.volumeUpButtonState = controller.volumeUpButtonState;
    }

    public void update() {
        this.manager.updateController(this);
    }
}
