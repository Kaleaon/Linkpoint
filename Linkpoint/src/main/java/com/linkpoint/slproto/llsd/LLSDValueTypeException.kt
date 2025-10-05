package com.linkpoint.slproto.llsd

class LLSDValueTypeException : LLSDException {
    constructor() : super("Invalid value type")
    
    constructor(requestedType: String, node: LLSDNode) : super(
        "Invalid value type: requested $requestedType, actual ${node.javaClass.simpleName}"
    )
}