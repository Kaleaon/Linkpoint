/*
 * Second Life Quaternion - Java implementation of quaternion mathematics for rotations
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.engine;

import java.util.Objects;

/**
 * Represents a quaternion, a mathematical construct used for representing 3D rotations.
 * <p>
 * This class provides a comprehensive set of methods for quaternion arithmetic,
 * including multiplication, normalization, and interpolation (slerp and lerp).
 * It also includes methods for converting between different rotation representations,
 * such as axis-angle, Euler angles, and rotation matrices.
 * <p>
 * Quaternions are used extensively in 3D graphics and physics to avoid issues
 * like gimbal lock that can occur with Euler angles.
 */
public class Quaternion {
    
    public static final Quaternion IDENTITY = new Quaternion(0.0, 0.0, 0.0, 1.0);
    
    private static final double EPSILON = 1e-6;
    
    public final double x;
    public final double y;
    public final double z;
    public final double w;
    
    /**
     * Constructs a new quaternion with the specified components.
     *
     * @param x The x-component (imaginary part).
     * @param y The y-component (imaginary part).
     * @param z The z-component (imaginary part).
     * @param w The w-component (real part).
     */
    public Quaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    /**
     * Constructs a new quaternion from a 4-element array in [x, y, z, w] order.
     *
     * @param components An array containing the x, y, z, and w components.
     * @throws IllegalArgumentException if the array does not have exactly 4 components.
     */
    public Quaternion(double[] components) {
        if (components.length != 4) {
            throw new IllegalArgumentException("Quaternion requires 4 components");
        }
        this.x = components[0];
        this.y = components[1];
        this.z = components[2];
        this.w = components[3];
    }
    
    /**
     * Constructs a new quaternion as a copy of another quaternion.
     *
     * @param other The quaternion to copy.
     */
    public Quaternion(Quaternion other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }
    
    /**
     * Creates a new quaternion representing a rotation around a specified axis.
     *
     * @param axis  The axis of rotation. This vector should be normalized.
     * @param angle The angle of rotation in radians.
     * @return A new {@code Quaternion} representing the rotation.
     */
    public static Quaternion fromAxisAngle(Vector3 axis, double angle) {
        Vector3 normalizedAxis = axis.normalize();
        double halfAngle = angle * 0.5;
        double sinHalf = Math.sin(halfAngle);
        
        return new Quaternion(
            normalizedAxis.x * sinHalf,
            normalizedAxis.y * sinHalf,
            normalizedAxis.z * sinHalf,
            Math.cos(halfAngle)
        );
    }
    
    /**
     * Creates a new quaternion from a set of Euler angles.
     * <p>
     * The rotation order is assumed to be ZYX (yaw, pitch, roll), which is common in many 3D systems.
     *
     * @param pitch The rotation around the x-axis in radians.
     * @param yaw   The rotation around the y-axis in radians.
     * @param roll  The rotation around the z-axis in radians.
     * @return A new {@code Quaternion} representing the combined rotation.
     */
    public static Quaternion fromEulerAngles(double pitch, double yaw, double roll) {
        double cp = Math.cos(pitch * 0.5);
        double sp = Math.sin(pitch * 0.5);
        double cy = Math.cos(yaw * 0.5);
        double sy = Math.sin(yaw * 0.5);
        double cr = Math.cos(roll * 0.5);
        double sr = Math.sin(roll * 0.5);
        
        return new Quaternion(
            sr * cp * cy - cr * sp * sy,
            cr * sp * cy + sr * cp * sy,
            cr * cp * sy - sr * sp * cy,
            cr * cp * cy + sr * sp * sy
        );
    }
    
    /**
     * Creates a new quaternion from a 3x3 rotation matrix.
     *
     * @param matrix A 3x3 array representing the rotation matrix.
     * @return A new {@code Quaternion} representing the same rotation.
     * @throws IllegalArgumentException if the matrix is not 3x3.
     */
    public static Quaternion fromMatrix3(double[][] matrix) {
        if (matrix.length != 3 || matrix[0].length != 3) {
            throw new IllegalArgumentException("Matrix must be 3x3");
        }
        
        double trace = matrix[0][0] + matrix[1][1] + matrix[2][2];
        
        if (trace > 0) {
            double s = Math.sqrt(trace + 1.0) * 2; // s = 4 * qw
            return new Quaternion(
                (matrix[2][1] - matrix[1][2]) / s,
                (matrix[0][2] - matrix[2][0]) / s,
                (matrix[1][0] - matrix[0][1]) / s,
                0.25 * s
            );
        } else if (matrix[0][0] > matrix[1][1] && matrix[0][0] > matrix[2][2]) {
            double s = Math.sqrt(1.0 + matrix[0][0] - matrix[1][1] - matrix[2][2]) * 2; // s = 4 * qx
            return new Quaternion(
                0.25 * s,
                (matrix[0][1] + matrix[1][0]) / s,
                (matrix[0][2] + matrix[2][0]) / s,
                (matrix[2][1] - matrix[1][2]) / s
            );
        } else if (matrix[1][1] > matrix[2][2]) {
            double s = Math.sqrt(1.0 + matrix[1][1] - matrix[0][0] - matrix[2][2]) * 2; // s = 4 * qy
            return new Quaternion(
                (matrix[0][1] + matrix[1][0]) / s,
                0.25 * s,
                (matrix[1][2] + matrix[2][1]) / s,
                (matrix[0][2] - matrix[2][0]) / s
            );
        } else {
            double s = Math.sqrt(1.0 + matrix[2][2] - matrix[0][0] - matrix[1][1]) * 2; // s = 4 * qz
            return new Quaternion(
                (matrix[0][2] + matrix[2][0]) / s,
                (matrix[1][2] + matrix[2][1]) / s,
                0.25 * s,
                (matrix[1][0] - matrix[0][1]) / s
            );
        }
    }
    
    /**
     * Multiplies this quaternion by another quaternion.
     * <p>
     * Quaternion multiplication is not commutative. The order {@code this * other}
     * represents applying the rotation of {@code other} followed by the rotation of {@code this}.
     *
     * @param other The quaternion to multiply by.
     * @return A new {@code Quaternion} representing the combined rotation.
     */
    public Quaternion multiply(Quaternion other) {
        return new Quaternion(
            w * other.x + x * other.w + y * other.z - z * other.y,
            w * other.y - x * other.z + y * other.w + z * other.x,
            w * other.z + x * other.y - y * other.x + z * other.w,
            w * other.w - x * other.x - y * other.y - z * other.z
        );
    }
    
    /**
     * Adds another quaternion to this one, component-wise.
     *
     * @param other The quaternion to add.
     * @return A new {@code Quaternion} representing the sum.
     */
    public Quaternion add(Quaternion other) {
        return new Quaternion(x + other.x, y + other.y, z + other.z, w + other.w);
    }
    
    /**
     * Subtracts another quaternion from this one, component-wise.
     *
     * @param other The quaternion to subtract.
     * @return A new {@code Quaternion} representing the difference.
     */
    public Quaternion subtract(Quaternion other) {
        return new Quaternion(x - other.x, y - other.y, z - other.z, w - other.w);
    }
    
    /**
     * Scales all components of this quaternion by a scalar value.
     *
     * @param scalar The scalar value to multiply by.
     * @return A new, scaled {@code Quaternion}.
     */
    public Quaternion scale(double scalar) {
        return new Quaternion(x * scalar, y * scalar, z * scalar, w * scalar);
    }
    
    /**
     * Calculates the squared norm (magnitude) of the quaternion.
     * <p>
     * This is computationally cheaper than {@link #norm()} as it avoids a square root.
     *
     * @return The squared norm.
     */
    public double normSquared() {
        return x * x + y * y + z * z + w * w;
    }
    
    /**
     * Calculates the norm (or magnitude or length) of the quaternion.
     *
     * @return The norm of the quaternion.
     */
    public double norm() {
        return Math.sqrt(normSquared());
    }
    
    /**
     * Normalizes the quaternion to have a magnitude of 1.
     * <p>
     * A normalized quaternion is also known as a unit quaternion and is required
     * for it to represent a pure rotation.
     *
     * @return A new, normalized {@code Quaternion}. Returns the identity quaternion
     *         if the norm is close to zero.
     */
    public Quaternion normalize() {
        double n = norm();
        if (n < EPSILON) {
            return IDENTITY;
        }
        return new Quaternion(x / n, y / n, z / n, w / n);
    }
    
    /**
     * Calculates the conjugate of this quaternion.
     * <p>
     * The conjugate has its vector part negated (x, y, z) and is used in
     * calculating the inverse and for vector rotation.
     *
     * @return A new {@code Quaternion} that is the conjugate of this one.
     */
    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }
    
    /**
     * Calculates the inverse of this quaternion.
     * <p>
     * For a unit quaternion, the inverse is equal to its conjugate.
     *
     * @return A new {@code Quaternion} that is the inverse of this one.
     */
    public Quaternion inverse() {
        double normSq = normSquared();
        if (normSq < EPSILON) {
            return IDENTITY;
        }
        return conjugate().scale(1.0 / normSq);
    }
    
    /**
     * Rotates a 3D vector by this quaternion.
     *
     * @param vector The vector to be rotated.
     * @return A new {@link Vector3} representing the rotated vector.
     */
    public Vector3 rotate(Vector3 vector) {
        // Convert vector to quaternion
        Quaternion vecQ = new Quaternion(vector.x, vector.y, vector.z, 0.0);
        
        // Perform rotation: q * v * q*
        Quaternion result = multiply(vecQ).multiply(conjugate());
        
        return new Vector3(result.x, result.y, result.z);
    }
    
    /**
     * Calculates the dot product of this quaternion with another.
     *
     * @param other The other quaternion.
     * @return The dot product.
     */
    public double dot(Quaternion other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }
    
    /**
     * Performs spherical linear interpolation (Slerp) between this quaternion
     * and a target quaternion.
     * <p>
     * Slerp provides a smooth interpolation along the shortest arc on a 4D sphere,
     * resulting in a constant-speed rotation.
     *
     * @param target The target quaternion to interpolate towards.
     * @param t      The interpolation factor, clamped to the range [0, 1].
     * @return A new {@code Quaternion} representing the interpolated rotation.
     */
    public Quaternion slerp(Quaternion target, double t) {
        t = Math.max(0.0, Math.min(1.0, t)); // Clamp t to [0,1]
        
        Quaternion from = normalize();
        Quaternion to = target.normalize();
        
        double dot = from.dot(to);
        
        // If quaternions are very close, use linear interpolation
        if (Math.abs(dot) > 1.0 - EPSILON) {
            return from.add(to.subtract(from).scale(t)).normalize();
        }
        
        // If dot product is negative, slerp won't take the shorter path
        if (dot < 0.0) {
            to = to.scale(-1.0);
            dot = -dot;
        }
        
        double theta = Math.acos(Math.abs(dot));
        double sinTheta = Math.sin(theta);
        
        double a = Math.sin((1.0 - t) * theta) / sinTheta;
        double b = Math.sin(t * theta) / sinTheta;
        
        return from.scale(a).add(to.scale(b));
    }
    
    /**
     * Performs normalized linear interpolation (Nlerp) between this quaternion
     * and a target quaternion.
     * <p>
     * Nlerp is computationally cheaper than Slerp but does not result in
     * constant-speed rotation. It is often a good approximation for small
     * rotational differences.
     *
     * @param target The target quaternion to interpolate towards.
     * @param t      The interpolation factor, clamped to the range [0, 1].
     * @return A new, normalized {@code Quaternion} representing the interpolated rotation.
     */
    public Quaternion lerp(Quaternion target, double t) {
        t = Math.max(0.0, Math.min(1.0, t)); // Clamp t to [0,1]
        
        Quaternion to = target;
        
        // Take shorter path
        if (dot(target) < 0.0) {
            to = target.scale(-1.0);
        }
        
        return add(to.subtract(this).scale(t)).normalize();
    }
    
    /**
     * Converts this quaternion into its axis-angle representation.
     *
     * @return An {@link AxisAngle} object containing the axis and the angle in radians.
     */
    public AxisAngle toAxisAngle() {
        Quaternion q = normalize();
        
        double sinHalfAngle = Math.sqrt(q.x * q.x + q.y * q.y + q.z * q.z);
        
        if (sinHalfAngle < EPSILON) {
            return new AxisAngle(Vector3.X_AXIS, 0.0);
        }
        
        Vector3 axis = new Vector3(q.x / sinHalfAngle, q.y / sinHalfAngle, q.z / sinHalfAngle);
        double angle = 2.0 * Math.atan2(sinHalfAngle, Math.abs(q.w));
        
        return new AxisAngle(axis, angle);
    }
    
    /**
     * Converts this quaternion into its Euler angle representation (pitch, yaw, roll).
     * <p>
     * The rotation order is ZYX (yaw, pitch, roll). Be aware of potential gimbal
     * lock issues when converting to Euler angles.
     *
     * @return An {@link EulerAngles} object containing the angles in radians.
     */
    public EulerAngles toEulerAngles() {
        Quaternion q = normalize();
        
        // Roll (x-axis rotation)
        double sinr_cosp = 2 * (q.w * q.x + q.y * q.z);
        double cosr_cosp = 1 - 2 * (q.x * q.x + q.y * q.y);
        double roll = Math.atan2(sinr_cosp, cosr_cosp);
        
        // Pitch (y-axis rotation)
        double sinp = 2 * (q.w * q.y - q.z * q.x);
        double pitch;
        if (Math.abs(sinp) >= 1) {
            pitch = Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
        } else {
            pitch = Math.asin(sinp);
        }
        
        // Yaw (z-axis rotation)
        double siny_cosp = 2 * (q.w * q.z + q.x * q.y);
        double cosy_cosp = 1 - 2 * (q.y * q.y + q.z * q.z);
        double yaw = Math.atan2(siny_cosp, cosy_cosp);
        
        return new EulerAngles(pitch, yaw, roll);
    }
    
    /**
     * Checks if this quaternion is normalized (i.e., has a magnitude of approximately 1).
     *
     * @return {@code true} if the quaternion is a unit quaternion, {@code false} otherwise.
     */
    public boolean isNormalized() {
        return Math.abs(norm() - 1.0) < EPSILON;
    }
    
    /**
     * Checks if this quaternion represents an identity rotation (i.e., no rotation).
     *
     * @return {@code true} if it is the identity quaternion, {@code false} otherwise.
     */
    public boolean isIdentity() {
        return Math.abs(x) < EPSILON && Math.abs(y) < EPSILON && 
               Math.abs(z) < EPSILON && Math.abs(Math.abs(w) - 1.0) < EPSILON;
    }
    
    /**
     * Converts this quaternion to a 4-element array of its components.
     *
     * @return A new array {@code [x, y, z, w]}.
     */
    public double[] toArray() {
        return new double[]{x, y, z, w};
    }
    
    /**
     * Converts this quaternion into an equivalent 3x3 rotation matrix.
     *
     * @return A new 3x3 array representing the rotation matrix.
     */
    public double[][] toMatrix3() {
        Quaternion q = normalize();
        
        double xx = q.x * q.x;
        double xy = q.x * q.y;
        double xz = q.x * q.z;
        double xw = q.x * q.w;
        double yy = q.y * q.y;
        double yz = q.y * q.z;
        double yw = q.y * q.w;
        double zz = q.z * q.z;
        double zw = q.z * q.w;
        
        return new double[][]{
            {1 - 2 * (yy + zz), 2 * (xy - zw), 2 * (xz + yw)},
            {2 * (xy + zw), 1 - 2 * (xx + zz), 2 * (yz - xw)},
            {2 * (xz - yw), 2 * (yz + xw), 1 - 2 * (xx + yy)}
        };
    }
    
    /**
     * Parses a quaternion from a string of comma-separated values.
     *
     * @param str The string to parse, in the format "x, y, z, w".
     * @return A new {@code Quaternion} parsed from the string.
     * @throws IllegalArgumentException if the string is not in the correct format.
     */
    public static Quaternion parse(String str) {
        if (str == null || str.trim().isEmpty()) {
            return IDENTITY;
        }
        
        String[] parts = str.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Quaternion string must have 4 components: " + str);
        }
        
        try {
            return new Quaternion(
                Double.parseDouble(parts[0].trim()),
                Double.parseDouble(parts[1].trim()),
                Double.parseDouble(parts[2].trim()),
                Double.parseDouble(parts[3].trim())
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Quaternion string: " + str, e);
        }
    }
    
    /**
     * A simple container class to hold an axis-angle representation of a rotation.
     */
    public static class AxisAngle {
        public final Vector3 axis;
        public final double angle;
        
        public AxisAngle(Vector3 axis, double angle) {
            this.axis = axis;
            this.angle = angle;
        }
    }
    
    /**
     * A simple container class to hold an Euler angle representation of a rotation.
     */
    public static class EulerAngles {
        /** Rotation around the x-axis. */
        public final double pitch;
        /** Rotation around the y-axis. */
        public final double yaw;
        /** Rotation around the z-axis. */
        public final double roll;
        
        public EulerAngles(double pitch, double yaw, double roll) {
            this.pitch = pitch;
            this.yaw = yaw;
            this.roll = roll;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Quaternion that = (Quaternion) obj;
        return Math.abs(that.x - x) < EPSILON &&
               Math.abs(that.y - y) < EPSILON &&
               Math.abs(that.z - z) < EPSILON &&
               Math.abs(that.w - w) < EPSILON;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(
            Math.round(x / EPSILON),
            Math.round(y / EPSILON),
            Math.round(z / EPSILON),
            Math.round(w / EPSILON)
        );
    }
    
    @Override
    public String toString() {
        return String.format("Quaternion(%.6f, %.6f, %.6f, %.6f)", x, y, z, w);
    }
    
    /**
     * Returns a compact string representation of the quaternion, formatted to two decimal places.
     *
     * @return A short string representation for debugging.
     */
    public String toShortString() {
        return String.format("(%.2f, %.2f, %.2f, %.2f)", x, y, z, w);
    }
}