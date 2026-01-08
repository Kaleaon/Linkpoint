package com.lumiyaviewer.lumiya.render

class MatrixStack {
    fun glPushMatrix() {}
    fun glPopMatrix() {}
    fun glTranslatef(x: Float, y: Float, z: Float) {}
    fun glMultMatrixf(matrix: FloatArray, offset: Int) {}
    fun getMatrixData(): FloatArray = FloatArray(16)
    fun getMatrixDataOffset(): Int = 0
}
