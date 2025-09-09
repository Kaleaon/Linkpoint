package com.google.vr.sdk.base.sensors.internal;

public class Vector3d {
    public double x;
    public double y;
    public double z;

    public Vector3d() {
    }

    public Vector3d(double d, double d2, double d3) {
        set(d, d2, d3);
    }

    public static void add(Vector3d vector3d, Vector3d vector3d2, Vector3d vector3d3) {
        Vector3d vector3d4 = vector3d3;
        vector3d4.set(vector3d2.x + vector3d.x, vector3d2.y + vector3d.y, vector3d2.z + vector3d.z);
    }

    public static void cross(Vector3d vector3d, Vector3d vector3d2, Vector3d vector3d3) {
        vector3d3.set((vector3d.y * vector3d2.z) - (vector3d.z * vector3d2.y), (vector3d.z * vector3d2.x) - (vector3d.x * vector3d2.z), (vector3d.x * vector3d2.y) - (vector3d.y * vector3d2.x));
    }

    public static double dot(Vector3d vector3d, Vector3d vector3d2) {
        return (vector3d.x * vector3d2.x) + (vector3d.y * vector3d2.y) + (vector3d.z * vector3d2.z);
    }

    public static int largestAbsComponent(Vector3d vector3d) {
        double abs = Math.abs(vector3d.x);
        double abs2 = Math.abs(vector3d.y);
        double abs3 = Math.abs(vector3d.z);
        return abs > abs2 ? abs > abs3 ? 0 : 2 : abs2 > abs3 ? 1 : 2;
    }

    public static void ortho(Vector3d vector3d, Vector3d vector3d2) {
        int largestAbsComponent = largestAbsComponent(vector3d) - 1;
        if (largestAbsComponent < 0) {
            largestAbsComponent = 2;
        }
        vector3d2.setZero();
        vector3d2.setComponent(largestAbsComponent, 1.0d);
        cross(vector3d, vector3d2, vector3d2);
        vector3d2.normalize();
    }

    public static void sub(Vector3d vector3d, Vector3d vector3d2, Vector3d vector3d3) {
        vector3d3.set(vector3d.x - vector3d2.x, vector3d.y - vector3d2.y, vector3d.z - vector3d2.z);
    }

    public double length() {
        return Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z));
    }

    public double maxNorm() {
        return Math.max(Math.abs(this.x), Math.max(Math.abs(this.y), Math.abs(this.z)));
    }

    public void normalize() {
        double length = length();
        if (length != 0.0d) {
            scale(1.0d / length);
        }
    }

    public boolean sameValues(Vector3d vector3d) {
        return this.x == vector3d.x && this.y == vector3d.y && this.z == vector3d.z;
    }

    public void scale(double d) {
        this.x *= d;
        this.y *= d;
        this.z *= d;
    }

    public void set(double d, double d2, double d3) {
        this.x = d;
        this.y = d2;
        this.z = d3;
    }

    public void set(Vector3d vector3d) {
        this.x = vector3d.x;
        this.y = vector3d.y;
        this.z = vector3d.z;
    }

    public void setComponent(int i, double d) {
        if (i == 0) {
            this.x = d;
        } else if (i != 1) {
            this.z = d;
        } else {
            this.y = d;
        }
    }

    public void setZero() {
        this.z = 0.0d;
        this.y = 0.0d;
        this.x = 0.0d;
    }

    public String toString() {
        return String.format("%+05f %+05f %+05f", new Object[]{Double.valueOf(this.x), Double.valueOf(this.y), Double.valueOf(this.z)});
    }
}
