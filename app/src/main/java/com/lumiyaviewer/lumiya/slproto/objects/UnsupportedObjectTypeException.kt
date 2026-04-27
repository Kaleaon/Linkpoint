package com.lumiyaviewer.lumiya.slproto.objects

class UnsupportedObjectTypeException(objectType: Byte) : Exception(
    "Unsupported object type: 0x%x".format(objectType),
)
