package com.linkpoint.slproto.assets

class AssetFormatException : Exception {
    constructor() : super("Unsupported asset format")

    constructor(str: String) : super(str)

    constructor(str: String, th: Throwable) : super(str, th)

    companion object {
        private const val serialVersionUID = -8391424207465457690L
    }
}