package com.linkpoint.render.shaders

class ShaderCompileException : Exception() {
    private const val Long serialVersionUID = 1

    public ShaderCompileException(String str) {
        super(str)
    }
}
