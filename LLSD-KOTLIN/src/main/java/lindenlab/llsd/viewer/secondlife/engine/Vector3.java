/*
 * Second Life Vector3 - Java implementation of 3D vector mathematics
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.engine;

import java.util.Objects;

/**
 * Represents an immutable three-dimensional vector with double-precision components.
 * <p>
 * This class provides a comprehensive set of methods for 3D vector mathematics,
 * essential for calculations in 3D graphics and physics, such as positions,
 * directions, and velocities.
 * <p>
 * All operations on a {@code Vector3} object produce a new {@code Vector3} object;
 * the original object is never modified.
 */
public class Vector3 {
    
    public static final Vector3 ZERO = new Vector3(0.0, 0.0, 0.0);
    public static final Vector3 ONE = new Vector3(1.0, 1.0, 1.0);
    public static final Vector3 X_AXIS = new Vector3(1.0, 0.0, 0.0);
    public static final Vector3 Y_AXIS = new Vector3(0.0, 1.0, 0.0);
    public static final Vector3 Z_AXIS = new Vector3(0.0, 0.0, 1.0);
    
    private static final double EPSILON = 1e-6;
    
    public final double x;
    public final double y;
    public final double z;
    
    /**
     * Constructs a new vector with the specified components.
     *
     * @param x The x-component.
     * @param y The y-component.
     * @param z The z-component.
     */
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Constructs a new vector from a 3-element array.
     *
     * @param components An array containing the x, y, and z components.
     * @throws IllegalArgumentException if the array does not have exactly 3 components.
     */
    public Vector3(double[] components) {
        if (components.length != 3) {
            throw new IllegalArgumentException("Vector3 requires 3 components");
        }
        this.x = components[0];
        this.y = components[1];
        this.z = components[2];
    }
    
    /**
     * Constructs a new vector as a copy of another vector.
     *
     * @param other The vector to copy.
     */
    public Vector3(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }
    
    /**
     * Adds another vector to this one, component-wise.
     *
     * @param other The vector to add.
     * @return A new {@code Vector3} representing the sum.
     */
    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }
    
    /**
     * Subtracts another vector from this one, component-wise.
     *
     * @param other The vector to subtract.
     * @return A new {@code Vector3} representing the difference.
     */
    public Vector3 subtract(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }
    
    /**
     * Multiplies this vector by a scalar value.
     *
     * @param scalar The scalar to multiply by.
     * @return A new, scaled {@code Vector3}.
     */
    public Vector3 multiply(double scalar) {
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }
    
    /**
     * Performs a component-wise multiplication (Hadamard product) with another vector.
     *
     * @param other The vector to multiply by.
     * @return A new {@code Vector3} where each component is the product of the
     *         corresponding components of the two vectors.
     */
    public Vector3 multiply(Vector3 other) {
        return new Vector3(x * other.x, y * other.y, z * other.z);
    }
    
    /**
     * Divides this vector by a scalar value.
     *
     * @param scalar The scalar to divide by.
     * @return A new, scaled {@code Vector3}.
     * @throws ArithmeticException if {@code scalar} is zero or near-zero.
     */
    public Vector3 divide(double scalar) {
        if (Math.abs(scalar) < EPSILON) {
            throw new ArithmeticException("Division by zero or near-zero value");
        }
        return new Vector3(x / scalar, y / scalar, z / scalar);
    }
    
    /**
     * Performs a component-wise division by another vector.
     *
     * @param other The vector to divide by.
     * @return A new {@code Vector3} where each component is the result of the division.
     * @throws ArithmeticException if any component of {@code other} is zero or near-zero.
     */
    public Vector3 divide(Vector3 other) {
        if (Math.abs(other.x) < EPSILON || Math.abs(other.y) < EPSILON || Math.abs(other.z) < EPSILON) {
            throw new ArithmeticException("Division by zero or near-zero component");
        }
        return new Vector3(x / other.x, y / other.y, z / other.z);
    }
    
    /**
     * Negates this vector by flipping the sign of each component.
     *
     * @return A new {@code Vector3} with all components negated.
     */
    public Vector3 negate() {
        return new Vector3(-x, -y, -z);
    }
    
    /**
     * Calculates the dot product of this vector with another.
     *
     * @param other The other vector.
     * @return The dot product.
     */
    public double dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }
    
    /**
     * Calculates the cross product of this vector with another.
     *
     * @param other The other vector.
     * @return A new {@code Vector3} that is perpendicular to both input vectors.
     */
    public Vector3 cross(Vector3 other) {
        return new Vector3(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }
    
    /**
     * Calculates the squared magnitude (length) of this vector.
     * <p>
     * This is computationally cheaper than {@link #magnitude()} as it avoids a square root.
     *
     * @return The squared magnitude.
     */
    public double magnitudeSquared() {
        return x * x + y * y + z * z;
    }
    
    /**
     * Calculates the magnitude (or length) of this vector.
     *
     * @return The magnitude.
     */
    public double magnitude() {
        return Math.sqrt(magnitudeSquared());
    }
    
    /**
     * Normalizes this vector to have a magnitude of 1.
     *
     * @return A new {@code Vector3} with the same direction but a magnitude of 1.
     *         Returns a zero vector if the original magnitude is close to zero.
     */
    public Vector3 normalize() {
        double mag = magnitude();
        if (mag < EPSILON) {
            return ZERO;
        }
        return divide(mag);
    }
    
    /**
     * Calculates the Euclidean distance between this vector and another.
     *
     * @param other The other vector.
     * @return The distance between the two vectors.
     */
    public double distance(Vector3 other) {
        return subtract(other).magnitude();
    }
    
    /**
     * Calculates the squared Euclidean distance between this vector and another.
     * <p>
     * This is computationally cheaper than {@link #distance(Vector3)}.
     *
     * @param other The other vector.
     * @return The squared distance.
     */
    public double distanceSquared(Vector3 other) {
        return subtract(other).magnitudeSquared();
    }
    
    /**
     * Performs a linear interpolation between this vector and a target vector.
     *
     * @param target The target vector to interpolate towards.
     * @param t      The interpolation factor, clamped to the range [0, 1].
     * @return A new {@code Vector3} representing the interpolated vector.
     */
    public Vector3 lerp(Vector3 target, double t) {
        t = Math.max(0.0, Math.min(1.0, t)); // Clamp t to [0,1]
        return add(target.subtract(this).multiply(t));
    }
    
    /**
     * Performs a spherical linear interpolation between this vector and a target vector.
     * <p>
     * This method interpolates along the arc between the two vectors, which is useful
     * for constant-speed rotation of direction vectors. Both vectors are normalized
     * before interpolation.
     *
     * @param target The target vector to interpolate towards.
     * @param t      The interpolation factor, clamped to the range [0, 1].
     * @return A new {@code Vector3} representing the interpolated vector.
     */
    public Vector3 slerp(Vector3 target, double t) {
        t = Math.max(0.0, Math.min(1.0, t)); // Clamp t to [0,1]
        
        Vector3 from = normalize();
        Vector3 to = target.normalize();
        
        double dot = from.dot(to);
        
        // If vectors are very close, use linear interpolation
        if (Math.abs(dot) > 1.0 - EPSILON) {
            return from.lerp(to, t);
        }
        
        double theta = Math.acos(Math.abs(dot));
        double sinTheta = Math.sin(theta);
        
        double a = Math.sin((1.0 - t) * theta) / sinTheta;
        double b = Math.sin(t * theta) / sinTheta;
        
        if (dot < 0.0) {
            to = to.negate();
        }
        
        return from.multiply(a).add(to.multiply(b));
    }
    
    /**
     * Checks if this vector is a zero vector (all components are close to zero).
     *
     * @return {@code true} if the vector is a zero vector, {@code false} otherwise.
     */
    public boolean isZero() {
        return Math.abs(x) < EPSILON && Math.abs(y) < EPSILON && Math.abs(z) < EPSILON;
    }
    
    /**
     * Checks if this vector is normalized (has a magnitude of approximately 1).
     *
     * @return {@code true} if the vector is a unit vector, {@code false} otherwise.
     */
    public boolean isNormalized() {
        return Math.abs(magnitude() - 1.0) < EPSILON;
    }
    
    /**
     * Projects this vector onto another vector.
     *
     * @param onto The vector to project onto.
     * @return A new {@code Vector3} representing the projection.
     */
    public Vector3 project(Vector3 onto) {
        double ontoMagSq = onto.magnitudeSquared();
        if (ontoMagSq < EPSILON) {
            return ZERO;
        }
        return onto.multiply(dot(onto) / ontoMagSq);
    }
    
    /**
     * Reflects this vector off a surface defined by a normal vector.
     *
     * @param normal The normal of the surface.
     * @return A new {@code Vector3} representing the reflected vector.
     */
    public Vector3 reflect(Vector3 normal) {
        return subtract(normal.multiply(2.0 * dot(normal)));
    }
    
    /**
     * Calculates the angle in radians between this vector and another.
     *
     * @param other The other vector.
     * @return The angle in radians.
     */
    public double angleTo(Vector3 other) {
        Vector3 a = normalize();
        Vector3 b = other.normalize();
        return Math.acos(Math.max(-1.0, Math.min(1.0, a.dot(b))));
    }
    
    /**
     * Converts this vector to a 3-element array of its components.
     *
     * @return A new array {@code [x, y, z]}.
     */
    public double[] toArray() {
        return new double[]{x, y, z};
    }
    
    /**
     * Gets a component of this vector by its index.
     *
     * @param index The index of the component (0 for x, 1 for y, 2 for z).
     * @return The value of the component.
     * @throws IndexOutOfBoundsException if the index is not 0, 1, or 2.
     */
    public double getComponent(int index) {
        switch (index) {
            case 0: return x;
            case 1: return y;
            case 2: return z;
            default: throw new IndexOutOfBoundsException("Vector3 index must be 0, 1, or 2");
        }
    }
    
    /**
     * Creates a new vector with the x-component replaced by a new value.
     *
     * @param newX The new x-component value.
     * @return A new {@code Vector3} with the updated component.
     */
    public Vector3 withX(double newX) {
        return new Vector3(newX, y, z);
    }
    
    /**
     * Creates a new vector with the y-component replaced by a new value.
     *
     * @param newY The new y-component value.
     * @return A new {@code Vector3} with the updated component.
     */
    public Vector3 withY(double newY) {
        return new Vector3(x, newY, z);
    }
    
    /**
     * Creates a new vector with the z-component replaced by a new value.
     *
     * @param newZ The new z-component value.
     * @return A new {@code Vector3} with the updated component.
     */
    public Vector3 withZ(double newZ) {
        return new Vector3(x, y, newZ);
    }
    
    /**
     * Creates a new vector containing the minimum components from two vectors.
     *
     * @param a The first vector.
     * @param b The second vector.
     * @return A new {@code Vector3} with components {@code (min(a.x, b.x), min(a.y, b.y), min(a.z, b.z))}.
     */
    public static Vector3 min(Vector3 a, Vector3 b) {
        return new Vector3(
            Math.min(a.x, b.x),
            Math.min(a.y, b.y),
            Math.min(a.z, b.z)
        );
    }
    
    /**
     * Creates a new vector containing the maximum components from two vectors.
     *
     * @param a The first vector.
     * @param b The second vector.
     * @return A new {@code Vector3} with components {@code (max(a.x, b.x), max(a.y, b.y), max(a.z, b.z))}.
     */
    public static Vector3 max(Vector3 a, Vector3 b) {
        return new Vector3(
            Math.max(a.x, b.x),
            Math.max(a.y, b.y),
            Math.max(a.z, b.z)
        );
    }
    
    /**
     * Parses a vector from a string of comma-separated values.
     *
     * @param str The string to parse, in the format "x, y, z".
     * @return A new {@code Vector3} parsed from the string.
     * @throws IllegalArgumentException if the string is not in the correct format.
     */
    public static Vector3 parse(String str) {
        if (str == null || str.trim().isEmpty()) {
            return ZERO;
        }
        
        String[] parts = str.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Vector3 string must have 3 components: " + str);
        }
        
        try {
            return new Vector3(
                Double.parseDouble(parts[0].trim()),
                Double.parseDouble(parts[1].trim()),
                Double.parseDouble(parts[2].trim())
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Vector3 string: " + str, e);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector3 vector3 = (Vector3) obj;
        return Math.abs(vector3.x - x) < EPSILON &&
               Math.abs(vector3.y - y) < EPSILON &&
               Math.abs(vector3.z - z) < EPSILON;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(
            Math.round(x / EPSILON),
            Math.round(y / EPSILON),
            Math.round(z / EPSILON)
        );
    }
    
    @Override
    public String toString() {
        return String.format("Vector3(%.6f, %.6f, %.6f)", x, y, z);
    }
    
    /**
     * Returns a compact string representation of the vector, formatted to two decimal places.
     *
     * @return A short string representation for debugging.
     */
    public String toShortString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }
}