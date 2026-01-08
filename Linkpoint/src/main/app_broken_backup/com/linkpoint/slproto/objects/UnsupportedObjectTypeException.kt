package com.linkpoint.slproto.objects

class UnsupportedObjectTypeException(objectType: Byte) : Exception(
    "Unsupported object type: 0x%x".format(objectType),
)
