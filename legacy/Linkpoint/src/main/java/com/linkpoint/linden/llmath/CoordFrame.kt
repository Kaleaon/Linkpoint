package com.linkpoint.linden.llmath

import kotlin.math.cos
import kotlin.math.sin

open class CoordFrame {
    var origin = Vector3(0f, 0f, 0f)
    var xAxis  = Vector3(1f, 0f, 0f)
    var yAxis  = Vector3(0f, 1f, 0f)
    var zAxis  = Vector3(0f, 0f, 1f)

    constructor()

    constructor(origin: Vector3) {
        this.origin.set(origin)
    }

    constructor(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3) {
        this.xAxis.set(xAxis); this.yAxis.set(yAxis); this.zAxis.set(zAxis)
    }

    constructor(origin: Vector3, xAxis: Vector3, yAxis: Vector3, zAxis: Vector3) {
        this.origin.set(origin)
        this.xAxis.set(xAxis); this.yAxis.set(yAxis); this.zAxis.set(zAxis)
    }

    constructor(origin: Vector3, direction: Vector3) {
        this.origin.set(origin)
        lookDir(direction)
    }

    constructor(origin: Vector3, rotation: Matrix3) {
        this.origin.set(origin)
        setAxes(rotation)
    }

    constructor(origin: Vector3, q: Quaternion) {
        this.origin.set(origin)
        setAxes(q)
    }

    constructor(q: Quaternion) {
        setAxes(q)
    }

    constructor(mat: Matrix4) {
        origin.set(mat.getTranslation())
        xAxis.set(mat[0, 0], mat[0, 1], mat[0, 2])
        yAxis.set(mat[1, 0], mat[1, 1], mat[1, 2])
        zAxis.set(mat[2, 0], mat[2, 1], mat[2, 2])
    }

    fun isFinite(): Boolean =
        origin.isFinite() && xAxis.isFinite() && yAxis.isFinite() && zAxis.isFinite()

    fun reset() {
        origin.set(0f, 0f, 0f)
        resetAxes()
    }

    fun resetAxes() {
        xAxis.set(1f, 0f, 0f)
        yAxis.set(0f, 1f, 0f)
        zAxis.set(0f, 0f, 1f)
    }

    fun setOrigin(x: Float, y: Float, z: Float) { origin.set(x, y, z) }
    fun setOrigin(frame: CoordFrame)             { origin.set(frame.origin) }

    var originX: Float get() = origin.x; set(v) { origin.x = v }
    var originY: Float get() = origin.y; set(v) { origin.y = v }
    var originZ: Float get() = origin.z; set(v) { origin.z = v }

    fun setAxes(x: Vector3, y: Vector3, z: Vector3) {
        xAxis.set(x); yAxis.set(y); zAxis.set(z)
    }

    fun setAxes(m: Matrix3) {
        xAxis.set(m[0, 0], m[0, 1], m[0, 2])
        yAxis.set(m[1, 0], m[1, 1], m[1, 2])
        zAxis.set(m[2, 0], m[2, 1], m[2, 2])
    }

    fun setAxes(q: Quaternion) {
        setAxes(Matrix3(q))
    }

    fun setAxes(frame: CoordFrame) {
        xAxis.set(frame.xAxis); yAxis.set(frame.yAxis); zAxis.set(frame.zAxis)
    }

    fun translate(x: Float, y: Float, z: Float) {
        origin.x += x; origin.y += y; origin.z += z
    }

    fun translate(v: Vector3) { origin = origin + v }

    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        val q = Quaternion().also { it.setAngleAxis(angle, x, y, z) }
        rotate(q)
    }

    fun rotate(angle: Float, axis: Vector3) {
        val q = Quaternion().also { it.setAngleAxis(angle, axis) }
        rotate(q)
    }

    fun rotate(q: Quaternion) {
        rotate(Matrix3(q))
    }

    fun rotate(m: Matrix3) {
        xAxis = m * xAxis
        yAxis = m * yAxis
        orthonormalize()
    }

    fun orthonormalize() {
        xAxis.normalize()
        val dot = xAxis * yAxis
        yAxis = yAxis - xAxis * dot
        yAxis.normalize()
        zAxis = xAxis % yAxis
    }

    fun roll(angle: Float)  { rotate2(yAxis, zAxis, angle) }
    fun pitch(angle: Float) { rotate2(zAxis, xAxis, angle) }
    fun yaw(angle: Float)   { rotate2(xAxis, yAxis, angle) }

    fun getAtAxis(): Vector3   = xAxis
    fun getLeftAxis(): Vector3 = yAxis
    fun getUpAxis(): Vector3   = zAxis

    fun getQuaternion(): Quaternion {
        val m = Matrix3()
        m[0, 0] = xAxis.x; m[0, 1] = xAxis.y; m[0, 2] = xAxis.z
        m[1, 0] = yAxis.x; m[1, 1] = yAxis.y; m[1, 2] = yAxis.z
        m[2, 0] = zAxis.x; m[2, 1] = zAxis.y; m[2, 2] = zAxis.z
        return m.toQuaternion()
    }

    fun getMatrixToLocal(mat: Matrix4) {
        // Transpose of rotation goes into columns
        mat[0, 0] = xAxis.x; mat[1, 0] = xAxis.y; mat[2, 0] = xAxis.z
        mat[0, 1] = yAxis.x; mat[1, 1] = yAxis.y; mat[2, 1] = yAxis.z
        mat[0, 2] = zAxis.x; mat[1, 2] = zAxis.y; mat[2, 2] = zAxis.z
        mat[3, 0] = -(origin.x * mat[0, 0] + origin.y * mat[1, 0] + origin.z * mat[2, 0])
        mat[3, 1] = -(origin.x * mat[0, 1] + origin.y * mat[1, 1] + origin.z * mat[2, 1])
        mat[3, 2] = -(origin.x * mat[0, 2] + origin.y * mat[1, 2] + origin.z * mat[2, 2])
    }

    fun getRotMatrixToParent(mat: Matrix4) {
        val ny = -yAxis
        mat[0, 0] = ny.x;    mat[0, 1] = ny.y;    mat[0, 2] = ny.z
        mat[1, 0] = zAxis.x; mat[1, 1] = zAxis.y; mat[1, 2] = zAxis.z
        val nx = -xAxis
        mat[2, 0] = nx.x;    mat[2, 1] = nx.y;    mat[2, 2] = nx.z
    }

    fun rotateToLocal(v: Vector3): Vector3 =
        Vector3(xAxis * v, yAxis * v, zAxis * v)

    fun rotateToLocal(v: Vector4): Vector4 {
        val lx = xAxis.x * v.x + xAxis.y * v.y + xAxis.z * v.z
        val ly = yAxis.x * v.x + yAxis.y * v.y + yAxis.z * v.z
        val lz = zAxis.x * v.x + zAxis.y * v.y + zAxis.z * v.z
        return Vector4(lx, ly, lz, v.w)
    }

    fun rotateToAbsolute(v: Vector3): Vector3 {
        val ax = xAxis.x * v.x + yAxis.x * v.y + zAxis.x * v.z
        val ay = xAxis.y * v.x + yAxis.y * v.y + zAxis.y * v.z
        val az = xAxis.z * v.x + yAxis.z * v.y + zAxis.z * v.z
        return Vector3(ax, ay, az)
    }

    fun rotateToAbsolute(v: Vector4): Vector4 {
        val ax = xAxis.x * v.x + yAxis.x * v.y + zAxis.x * v.z
        val ay = xAxis.y * v.x + yAxis.y * v.y + zAxis.y * v.z
        val az = xAxis.z * v.x + yAxis.z * v.y + zAxis.z * v.z
        return Vector4(ax, ay, az, v.w)
    }

    fun transformToLocal(v: Vector3): Vector3   = rotateToLocal(v - origin)
    fun transformToLocal(v: Vector4): Vector4 {
        val shifted = Vector4(v.x - origin.x, v.y - origin.y, v.z - origin.z, v.w)
        return rotateToLocal(shifted)
    }

    fun transformToAbsolute(v: Vector3): Vector3 = rotateToAbsolute(v) + origin
    fun transformToAbsolute(v: Vector4): Vector4 {
        val r = rotateToAbsolute(v)
        return Vector4(r.x + origin.x, r.y + origin.y, r.z + origin.z, r.w)
    }

    fun getOpenGLTranslation(): FloatArray {
        val m = FloatArray(16)
        m[0] = 1f; m[5] = 1f; m[10] = 1f; m[15] = 1f
        m[12] = -origin.x; m[13] = -origin.y; m[14] = -origin.z
        return m
    }

    fun getOpenGLRotation(): FloatArray {
        val m = FloatArray(16)
        m[0] = xAxis.x;  m[4] = xAxis.y;  m[8]  = xAxis.z
        m[1] = yAxis.x;  m[5] = yAxis.y;  m[9]  = yAxis.z
        m[2] = zAxis.x;  m[6] = zAxis.y;  m[10] = zAxis.z
        m[15] = 1f
        return m
    }

    fun getOpenGLTransform(): FloatArray {
        val m = FloatArray(16)
        m[0] = xAxis.x;  m[4] = xAxis.y;  m[8]  = xAxis.z;  m[12] = -(origin * xAxis)
        m[1] = yAxis.x;  m[5] = yAxis.y;  m[9]  = yAxis.z;  m[13] = -(origin * yAxis)
        m[2] = zAxis.x;  m[6] = zAxis.y;  m[10] = zAxis.z;  m[14] = -(origin * zAxis)
        m[15] = 1f
        return m
    }

    fun lookDir(at: Vector3, up: Vector3 = Vector3(0f, 0f, 1f)) {
        var left = up % at
        if (left.isNull()) {
            val tweaked = Vector3(at.x + 0.01f, at.y, at.z).also { it.normalize() }
            left = up % tweaked
        }
        left.normalize()
        val upVec = at % left
        if (at.isFinite() && left.isFinite() && upVec.isFinite()) {
            setAxes(at, left, upVec)
        }
    }

    fun lookAt(origin: Vector3, poi: Vector3, up: Vector3 = Vector3(0f, 0f, 1f)) {
        this.origin.set(origin)
        val at = (poi - origin).also { it.normalize() }
        lookDir(at, up)
    }

    override fun toString(): String =
        "{ origin=$origin x=$xAxis y=$yAxis z=$zAxis }"

    private fun rotate2(source: Vector3, target: Vector3, angle: Float) {
        val sx = source.x; val sy = source.y; val sz = source.z
        val tx = target.x; val ty = target.y; val tz = target.z
        val c = cos(angle); val s = sin(angle)
        source.set(sx * c + tx * s, sy * c + ty * s, sz * c + tz * s)
        target.set(tx * c - sx * s, ty * c - sy * s, tz * c - sz * s)
    }
}
