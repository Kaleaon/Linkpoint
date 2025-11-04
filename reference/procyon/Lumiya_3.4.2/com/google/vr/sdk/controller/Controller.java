// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.controller;

import android.graphics.PointF;

public class Controller
{
    private static final int DURATION_REQUIRED_TO_RECENTER_NS = 600000000;
    static final int MIN_VRCORE_API_WITH_RECENTERING = 8;
    private static final float RECENTER_PITCH_BOUNDS_RADIANS;
    public boolean appButtonState;
    public boolean clickButtonState;
    private EventListener controllerEventListener;
    private final Orientation controllerPoseInSensorSpace;
    boolean enableRecenterShim;
    private long homeButtonDownTimestamp;
    public boolean homeButtonState;
    public boolean isTouching;
    private final ControllerManager manager;
    public final Orientation orientation;
    public final float[] position;
    private Orientation startFromSensorTransformation;
    public long timestamp;
    public final PointF touch;
    public boolean volumeDownButtonState;
    public boolean volumeUpButtonState;
    
    static {
        RECENTER_PITCH_BOUNDS_RADIANS = (float)Math.toRadians(30.0);
    }
    
    Controller(final ControllerManager manager) {
        this.timestamp = 0L;
        this.orientation = new Orientation();
        this.position = new float[3];
        this.touch = new PointF();
        this.controllerEventListener = new EventListener();
        this.controllerPoseInSensorSpace = new Orientation();
        this.startFromSensorTransformation = new Orientation();
        this.manager = manager;
    }
    
    private void setStartFromSensorTransformation() {
        final float[] eulerAngles = this.controllerPoseInSensorSpace.toEulerAngles(new float[3]);
        this.startFromSensorTransformation = new Orientation(0.0f, (float)Math.sin(-eulerAngles[1] / 2.0f), 0.0f, (float)Math.cos(eulerAngles[1] / 2.0f));
    }
    
    void notifyConnectionStateChange(final int n) {
        this.controllerEventListener.onConnectionStateChanged(n);
    }
    
    void notifyUpdate() {
        this.controllerEventListener.onUpdate();
    }
    
    public void setEventListener(final EventListener controllerEventListener) {
        this.controllerEventListener = controllerEventListener;
    }
    
    boolean setHomeButtonState(final boolean homeButtonState) {
        this.homeButtonState = homeButtonState;
        if (this.enableRecenterShim) {
            if (!this.homeButtonState) {
                boolean b;
                if (this.timestamp - this.homeButtonDownTimestamp <= 600000000L) {
                    b = true;
                }
                else {
                    b = false;
                }
                if (!b && Math.abs(this.controllerPoseInSensorSpace.toEulerAngles(new float[3])[0]) < Controller.RECENTER_PITCH_BOUNDS_RADIANS) {
                    this.setStartFromSensorTransformation();
                    return true;
                }
            }
            else {
                this.homeButtonDownTimestamp = this.timestamp;
            }
            return false;
        }
        return false;
    }
    
    void setOrientationInSensorSpace(final float n, final float n2, final float n3, final float n4) {
        this.controllerPoseInSensorSpace.set(n, n2, n3, n4);
        this.orientation.set(this.startFromSensorTransformation);
        this.orientation.multiply(this.controllerPoseInSensorSpace);
    }
    
    void setPublicState(final Controller controller) {
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
    
    public static class ConnectionStates
    {
        public static final int CONNECTED = 3;
        public static final int CONNECTING = 2;
        public static final int DISCONNECTED = 0;
        public static final int SCANNING = 1;
        
        private ConnectionStates() {
        }
        
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
    
    public static class EventListener
    {
        public void onConnectionStateChanged(final int n) {
        }
        
        public void onUpdate() {
        }
    }
}
