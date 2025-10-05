package com.linkpoint.slproto.assets

class AssetFormatException : Exception {
    constructor() : super("Unsupported asset format")
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}