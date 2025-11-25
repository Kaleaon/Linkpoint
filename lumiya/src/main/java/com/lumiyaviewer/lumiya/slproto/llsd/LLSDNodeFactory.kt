package com.lumiyaviewer.lumiya.slproto.llsd

import com.lumiyaviewer.lumiya.slproto.llsd.types.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

object LLSDNodeFactory {

    private interface LLSDNodeConstructor {
        @Throws(LLSDXMLException::class, XmlPullParserException::class, IOException::class)
        fun createNodeFromXML(xmlPullParser: XmlPullParser): LLSDNode
    }

    private val createUndef = object : LLSDNodeConstructor {
        override fun createNodeFromXML(xmlPullParser: XmlPullParser): LLSDNode {
            xmlPullParser.nextTag()
            return LLSDUndefined()
        }
    }
    
    private val createBoolean = object : LLSDNodeConstructor {
        override fun createNodeFromXML(xmlPullParser: XmlPullParser): LLSDNode {
            return LLSDBoolean(xmlPullParser.nextText())
        }
    }
    
    private val createInt = object : LLSDNodeConstructor {
        override fun createNodeFromXML(xmlPullParser: XmlPullParser): LLSDNode {
            return LLSDInt(xmlPullParser.nextText())
        }
    }
    
    private val createDouble = object : LLSDNodeConstructor {
        override fun createNodeFromXML(xmlPullParser: XmlPullParser): LLSDNode {
            return LLSDDouble(xmlPullParser.nextText())
        }
    }
    
    private val createUUID = object : LLSDNodeConstructor {
        override fun createNodeFromXML(xmlPullParser: XmlPullParser): LLSDNode {
             return LLSDUUID(xmlPullParser.nextText())
        }
    }
    
    private val createString = object : LLSDNodeConstructor {
        override fun createNodeFromXML(xmlPullParser: XmlPullParser): LLSDNode {
            return LLSDString(xmlPullParser.nextText())
        }
    }
    
    private val createDate = object : LLSDNodeConstructor {
        override fun createNodeFromXML(xmlPullParser: XmlPullParser): LLSDNode {
            return LLSDDate(xmlPullParser.nextText())
        }
    }
    
    private val createURI = object : LLSDNodeConstructor {
        override fun createNodeFromXML(xmlPullParser: XmlPullParser): LLSDNode {
            return LLSDURI(xmlPullParser.nextText())
        }
    }
    
    private val createBinary = object : LLSDNodeConstructor {
        override fun createNodeFromXML(xmlPullParser: XmlPullParser): LLSDNode {
            return LLSDBinary(xmlPullParser.nextText())
        }
    }
    
    private val createArray = object : LLSDNodeConstructor {
        override fun createNodeFromXML(xmlPullParser: XmlPullParser): LLSDNode {
            val array = LLSDArray()
            while (xmlPullParser.nextTag() != XmlPullParser.END_TAG) {
                array.add(parseNode(xmlPullParser))
            }
            return array
        }
    }
    
    private val createMap = object : LLSDNodeConstructor {
        override fun createNodeFromXML(xmlPullParser: XmlPullParser): LLSDNode {
            val map = LLSDMap()
            while (xmlPullParser.nextTag() != XmlPullParser.END_TAG) {
                xmlPullParser.require(XmlPullParser.START_TAG, null, "key")
                val key = xmlPullParser.nextText()
                // nextText leaves us at END_TAG </key>
                xmlPullParser.nextTag() // Move to value START_TAG
                val value = parseNode(xmlPullParser)
                map[key] = value
            }
            return map
        }
    }

    private val tagMap = HashMap<String, LLSDNodeConstructor>().apply {
        put("undef", createUndef)
        put("boolean", createBoolean)
        put("integer", createInt)
        put("real", createDouble)
        put("uuid", createUUID)
        put("string", createString)
        put("date", createDate)
        put("uri", createURI)
        put("binary", createBinary)
        put("array", createArray)
        put("map", createMap)
    }

    @JvmStatic
    @Throws(XmlPullParserException::class, IOException::class, LLSDXMLException::class)
    fun parseNode(xmlPullParser: XmlPullParser): LLSDNode {
        val name = xmlPullParser.name
        val constructor = tagMap[name] ?: throw LLSDXMLException("Invalid tag name: $name")
        return constructor.createNodeFromXML(xmlPullParser)
    }
}
