package com.lumiyaviewer.lumiya.slproto.llsd

class LLSDValueTypeException : LLSDException {
    constructor() : super("Invalid value type")
    
    constructor(requestedType: String, node: LLSDNode) : super(
        "Invalid value type: requested $requestedType, actual ${node.javaClass.simpleName}"
    )
    
    companion object {
        private const val serialVersionUID = -1831477542961670453L
    }
}