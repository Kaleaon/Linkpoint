package com.lumiyaviewer.lumiya.slproto.objects

class UnsupportedObjectTypeException(type: Byte) :
    Exception(String.format("Unsupported object type: 0x%x", type))
