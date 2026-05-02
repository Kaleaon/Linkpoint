package com.linkpoint.protocol.messages.ids

object ObjectMessages {
    const val OBJECT_UPDATE = 12
    const val OBJECT_UPDATE_COMPRESSED = 13
    const val OBJECT_UPDATE_CACHED = 14
    const val IMPROVED_TERSE_OBJECT_UPDATE = 15
    const val KILL_OBJECT = 16
    const val OBJECT_SELECT = (0xFFFF006E).toInt()
    const val MULTIPLE_OBJECT_UPDATE = 65282
    const val REZ_OBJECT = (0xFFFF0125).toInt()
    const val DEREZ_OBJECT = (0xFFFF0123).toInt()
    const val OBJECT_DELETE = (0xFFFF0059).toInt()
    const val OBJECT_LINK = (0xFFFF0073).toInt()
    const val OBJECT_DELINK = (0xFFFF0074).toInt()
    const val OBJECT_NAME = (0xFFFF006B).toInt()
    const val OBJECT_DESCRIPTION = (0xFFFF006C).toInt()
    const val OBJECT_GRAB = (0xFFFF0075).toInt()
    const val OBJECT_DEGRAB = (0xFFFF0077).toInt()
    const val OBJECT_PROPERTIES = 65289
    const val REQUEST_MULTIPLE_OBJECTS = 65283
}
