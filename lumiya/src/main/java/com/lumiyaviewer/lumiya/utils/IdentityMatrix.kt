package com.lumiyaviewer.lumiya.utils

import android.opengl.Matrix

class IdentityMatrix {
    companion object {
        private val matrix = FloatArray(16)
        
        init {
            Matrix.setIdentityM(matrix, 0)
        }
        
        fun getMatrix(): FloatArray {
            return matrix
        }
    }
}
