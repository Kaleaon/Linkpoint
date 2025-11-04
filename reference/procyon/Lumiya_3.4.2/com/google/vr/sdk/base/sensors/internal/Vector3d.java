// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base.sensors.internal;

public class Vector3d
{
    public double x;
    public double y;
    public double z;
    
    public Vector3d() {
    }
    
    public Vector3d(final double n, final double n2, final double n3) {
        this.set(n, n2, n3);
    }
    
    public static void add(final Vector3d vector3d, final Vector3d vector3d2, final Vector3d vector3d3) {
        vector3d3.set(vector3d2.x + vector3d.x, vector3d2.y + vector3d.y, vector3d2.z + vector3d.z);
    }
    
    public static void cross(final Vector3d vector3d, final Vector3d vector3d2, final Vector3d vector3d3) {
        vector3d3.set(vector3d.y * vector3d2.z - vector3d.z * vector3d2.y, vector3d.z * vector3d2.x - vector3d.x * vector3d2.z, vector3d.x * vector3d2.y - vector3d.y * vector3d2.x);
    }
    
    public static double dot(final Vector3d vector3d, final Vector3d vector3d2) {
        return vector3d.x * vector3d2.x + vector3d.y * vector3d2.y + vector3d.z * vector3d2.z;
    }
    
    public static int largestAbsComponent(final Vector3d vector3d) {
        final double abs = Math.abs(vector3d.x);
        final double abs2 = Math.abs(vector3d.y);
        final double abs3 = Math.abs(vector3d.z);
        if (abs > abs2) {
            if (abs > abs3) {
                return 0;
            }
            return 2;
        }
        else {
            if (abs2 > abs3) {
                return 1;
            }
            return 2;
        }
    }
    
    public static void ortho(final Vector3d vector3d, final Vector3d vector3d2) {
        int n = largestAbsComponent(vector3d) - 1;
        if (n < 0) {
            n = 2;
        }
        vector3d2.setZero();
        vector3d2.setComponent(n, 1.0);
        cross(vector3d, vector3d2, vector3d2);
        vector3d2.normalize();
    }
    
    public static void sub(final Vector3d vector3d, final Vector3d vector3d2, final Vector3d vector3d3) {
        vector3d3.set(vector3d.x - vector3d2.x, vector3d.y - vector3d2.y, vector3d.z - vector3d2.z);
    }
    
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public double maxNorm() {
        return Math.max(Math.abs(this.x), Math.max(Math.abs(this.y), Math.abs(this.z)));
    }
    
    public void normalize() {
        final double length = this.length();
        if (length != 0.0) {
            this.scale(1.0 / length);
        }
    }
    
    public boolean sameValues(final Vector3d vector3d) {
        return this.x == vector3d.x && this.y == vector3d.y && this.z == vector3d.z;
    }
    
    public void scale(final double n) {
        this.x *= n;
        this.y *= n;
        this.z *= n;
    }
    
    public void set(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void set(final Vector3d vector3d) {
        this.x = vector3d.x;
        this.y = vector3d.y;
        this.z = vector3d.z;
    }
    
    public void setComponent(final int n, final double x) {
        if (n != 0) {
            if (n != 1) {
                this.z = x;
            }
            else {
                this.y = x;
            }
        }
        else {
            this.x = x;
        }
    }
    
    public void setZero() {
        this.z = 0.0;
        this.y = 0.0;
        this.x = 0.0;
    }
    
    @Override
    public String toString() {
        return String.format("%+05f %+05f %+05f", this.x, this.y, this.z);
    }
}
