package com.linkpoint.linden.llxml

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Color4
import com.linkpoint.linden.llmath.Vector3
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import org.xml.sax.InputSource

enum class NodeType {
    CONTAINER, UNKNOWN, BOOLEAN, INTEGER, FLOAT, STRING, UUID, NODEREF
}

enum class NodeEncoding {
    DEFAULT, DECIMAL, HEX
}

class XmlNode(
    var name: String,
    var isAttribute: Boolean = false
) {
    var value: String = ""
    var type: NodeType = NodeType.CONTAINER
    var encoding: NodeEncoding = NodeEncoding.DEFAULT
    var id: String = ""
    var versionMajor: Int = 0
    var versionMinor: Int = 0
    var length: Int = 0
    var precision: Int = 64
    var lineNumber: Int = -1

    var parent: XmlNode? = null
    val children: MutableList<XmlNode> = mutableListOf()
    val attributes: MutableMap<String, XmlNode> = mutableMapOf()

    private var prevSibling: XmlNode? = null
    private var nextSibling: XmlNode? = null

    fun isNull(): Boolean = name.isEmpty() && value.isEmpty()

    fun deepCopy(): XmlNode {
        val copy = XmlNode(name, isAttribute)
        copy.value = value
        copy.type = type
        copy.encoding = encoding
        copy.id = id
        copy.versionMajor = versionMajor
        copy.versionMinor = versionMinor
        copy.length = length
        copy.precision = precision
        copy.lineNumber = lineNumber
        for (attr in attributes.values) {
            val attrCopy = attr.deepCopy()
            attrCopy.parent = copy
            copy.attributes[attrCopy.name] = attrCopy
        }
        for (child in children) {
            val childCopy = child.deepCopy()
            copy.addChild(childCopy)
        }
        return copy
    }

    fun addChild(child: XmlNode) {
        child.parent = this
        if (children.isNotEmpty()) {
            val last = children.last()
            last.nextSibling = child
            child.prevSibling = last
        }
        children.add(child)
    }

    fun removeChild(child: XmlNode): Boolean {
        val removed = children.remove(child)
        if (removed) {
            child.parent = null
            val prev = child.prevSibling
            val next = child.nextSibling
            prev?.nextSibling = next
            next?.prevSibling = prev
            child.prevSibling = null
            child.nextSibling = null
        }
        return removed
    }

    fun reparent(newParent: XmlNode?) {
        parent?.removeChild(this)
        newParent?.addChild(this)
    }

    fun createChild(childName: String, asAttribute: Boolean): XmlNode {
        val child = XmlNode(childName, asAttribute)
        if (asAttribute) {
            child.parent = this
            attributes[childName] = child
        } else {
            addChild(child)
        }
        return child
    }

    fun hasAttribute(attrName: String): Boolean = attributes.containsKey(attrName)

    fun getAttribute(attrName: String): XmlNode? = attributes[attrName]

    fun setAttribute(attrName: String, attrValue: String) {
        val node = attributes.getOrPut(attrName) {
            val n = XmlNode(attrName, true)
            n.parent = this
            n
        }
        node.value = attrValue
        node.type = NodeType.STRING
    }

    fun getChild(childName: String): XmlNode? =
        children.firstOrNull { !it.isAttribute && it.name == childName }

    fun getChildren(childName: String): List<XmlNode> =
        children.filter { !it.isAttribute && it.name == childName }

    fun findName(targetName: String): List<XmlNode> {
        val results = mutableListOf<XmlNode>()
        if (name == targetName) results.add(this)
        for (child in children) results.addAll(child.findName(targetName))
        return results
    }

    fun findId(targetId: String): List<XmlNode> {
        val results = mutableListOf<XmlNode>()
        if (id == targetId) results.add(this)
        for (child in children) results.addAll(child.findId(targetId))
        return results
    }

    fun getFirstChild(): XmlNode? = children.firstOrNull { !it.isAttribute }

    fun getNextSibling(): XmlNode? {
        var n = nextSibling
        while (n != null && n.isAttribute) n = n.nextSibling
        return n
    }

    fun getRoot(): XmlNode {
        var node = this
        while (node.parent != null) node = node.parent!!
        return node
    }

    fun getChildCount(): Int = children.count { !it.isAttribute }

    fun getTextContents(): String {
        val sb = StringBuilder()
        for (child in children) {
            if (child.name == "#text" || child.name.isEmpty()) {
                sb.append(child.value)
            }
        }
        return if (sb.isEmpty()) value else sb.toString()
    }

    fun getSanitizedValue(): String =
        value.trim().replace(Regex("\\s+"), " ")

    fun deleteChildren(childName: String): Boolean {
        val toRemove = children.filter { it.name == childName }
        if (toRemove.isEmpty()) return false
        toRemove.forEach { removeChild(it) }
        return true
    }

    // Typed value getters
    fun getValueAsString(): String = value

    fun getValueAsInt(): Int = when (type) {
        NodeType.INTEGER -> value.trim().toIntOrNull() ?: 0
        NodeType.BOOLEAN -> if (value.trim().lowercase() == "true" || value.trim() == "1") 1 else 0
        NodeType.FLOAT -> value.trim().toDoubleOrNull()?.toInt() ?: 0
        else -> value.trim().toIntOrNull() ?: 0
    }

    fun getValueAsFloat(): Float = when (type) {
        NodeType.FLOAT, NodeType.INTEGER -> value.trim().toFloatOrNull() ?: 0f
        NodeType.BOOLEAN -> if (value.trim().lowercase() == "true" || value.trim() == "1") 1f else 0f
        else -> value.trim().toFloatOrNull() ?: 0f
    }

    fun getValueAsBool(): Boolean = when (type) {
        NodeType.BOOLEAN -> value.trim().lowercase() == "true" || value.trim() == "1"
        NodeType.INTEGER -> (value.trim().toIntOrNull() ?: 0) != 0
        NodeType.FLOAT -> (value.trim().toFloatOrNull() ?: 0f) != 0f
        else -> value.trim().lowercase() == "true" || value.trim() == "1"
    }

    fun getValueAsUUID(): LLUUID =
        LLUUID.fromString(value.trim()) ?: LLUUID.NULL

    fun getValueAsColor4(): Color4? {
        val values = parseFloatTuple(value, 4) ?: return null
        return Color4(values[0], values[1], values[2], values[3])
    }

    fun getValueAsVector3(): Vector3? {
        val values = parseFloatTuple(value, 3) ?: return null
        return Vector3(values[0], values[1], values[2])
    }

    // Typed attribute getters
    fun getAttributeBool(attrName: String): Boolean? {
        val v = attributes[attrName]?.value?.trim() ?: return null
        return v.lowercase() == "true" || v == "1"
    }

    fun getAttributeInt(attrName: String): Int? =
        attributes[attrName]?.value?.trim()?.toIntOrNull()

    fun getAttributeUInt(attrName: String): UInt? =
        attributes[attrName]?.value?.trim()?.toUIntOrNull()

    fun getAttributeFloat(attrName: String): Float? =
        attributes[attrName]?.value?.trim()?.toFloatOrNull()

    fun getAttributeDouble(attrName: String): Double? =
        attributes[attrName]?.value?.trim()?.toDoubleOrNull()

    fun getAttributeString(attrName: String): String? =
        attributes[attrName]?.value

    fun getAttributeUUID(attrName: String): LLUUID? {
        val v = attributes[attrName]?.value?.trim() ?: return null
        return LLUUID.fromString(v)
    }

    fun getAttributeColor4(attrName: String): Color4? {
        val v = attributes[attrName]?.value?.trim() ?: return null
        val values = parseFloatTuple(v, 4) ?: return null
        return Color4(values[0], values[1], values[2], values[3])
    }

    fun getAttributeVector3(attrName: String): Vector3? {
        val v = attributes[attrName]?.value?.trim() ?: return null
        val values = parseFloatTuple(v, 3) ?: return null
        return Vector3(values[0], values[1], values[2])
    }

    // Typed value setters
    fun setBoolValue(v: Boolean) { value = if (v) "true" else "false"; type = NodeType.BOOLEAN }
    fun setIntValue(v: Int) { value = v.toString(); type = NodeType.INTEGER }
    fun setUIntValue(v: UInt) { value = v.toString(); type = NodeType.INTEGER }
    fun setFloatValue(v: Float) { value = v.toString(); type = NodeType.FLOAT }
    fun setDoubleValue(v: Double) { value = v.toString(); type = NodeType.FLOAT }
    fun setStringValue(v: String) { value = v; type = NodeType.STRING }
    fun setUUIDValue(v: LLUUID) { value = v.toString(); type = NodeType.UUID }


    fun setAttributes(nodeType: NodeType, prec: Int, enc: NodeEncoding, len: Int) {
        type = nodeType; precision = prec; encoding = enc; length = len
    }

    fun toXmlString(indent: String = "", useTypeDecorations: Boolean = true): String {
        val sb = StringBuilder()
        sb.append("$indent<").append(escapeXmlAttr(name))
        if (useTypeDecorations && type != NodeType.CONTAINER && type != NodeType.UNKNOWN) {
            sb.append(" type=\"${type.name.lowercase()}\"")
        }
        if (id.isNotEmpty()) sb.append(" id=\"${escapeXmlAttr(id)}\"")
        for (attr in attributes.values) {
            sb.append(" ${escapeXmlAttr(attr.name)}=\"${escapeXmlAttr(attr.value)}\"")
        }
        if (children.isEmpty() && value.isEmpty()) {
            sb.append(" />")
        } else {
            sb.append(">")
            if (value.isNotEmpty()) sb.append(escapeXml(value))
            val childIndent = "$indent    "
            for (child in children) {
                if (!child.isAttribute) {
                    sb.append("\n").append(child.toXmlString(childIndent, useTypeDecorations))
                }
            }
            if (children.any { !it.isAttribute }) sb.append("\n$indent")
            sb.append("</${escapeXmlAttr(name)}>")
        }
        return sb.toString()
    }

    companion object {
        private val WHITESPACE_REGEX = Regex("\\s+")

        private fun parseFloatTuple(source: String, expected: Int): FloatArray? {
            val parts = source.trim().split(WHITESPACE_REGEX).filter { it.isNotEmpty() }
            if (parts.size < expected) return null
            return FloatArray(expected) { index ->
                parts[index].toFloatOrNull() ?: return null
            }
        }

        fun parse(xml: String): XmlNode? = runCatching {
            val factory = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = false }
            val builder = factory.newDocumentBuilder()
            val doc = builder.parse(InputSource(StringReader(xml)))
            doc.documentElement?.let { fromDomElement(it) }
        }.getOrNull()

        private fun fromDomElement(elem: Element): XmlNode {
            val node = XmlNode(elem.tagName)
            val attrs = elem.attributes
            for (i in 0 until attrs.length) {
                val attr = attrs.item(i)
                node.setAttribute(attr.nodeName, attr.nodeValue)
            }
            node.id = node.getAttributeString("id") ?: ""
            val typeStr = node.getAttributeString("type")
            node.type = when (typeStr?.lowercase()) {
                "boolean" -> NodeType.BOOLEAN
                "integer" -> NodeType.INTEGER
                "float" -> NodeType.FLOAT
                "string" -> NodeType.STRING
                "uuid" -> NodeType.UUID
                "noderef" -> NodeType.NODEREF
                null, "" -> NodeType.UNKNOWN
                else -> NodeType.UNKNOWN
            }
            val childNodes = elem.childNodes
            for (i in 0 until childNodes.length) {
                val child = childNodes.item(i)
                when (child.nodeType) {
                    Node.ELEMENT_NODE -> node.addChild(fromDomElement(child as Element))
                    Node.TEXT_NODE -> {
                        val text = child.nodeValue ?: ""
                        if (text.isNotBlank()) node.value += text
                    }
                    Node.CDATA_SECTION_NODE -> node.value += child.nodeValue ?: ""
                }
            }
            return node
        }

        fun escapeXml(s: String): String = s
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")

        private fun escapeXmlAttr(s: String): String = s
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace("\"", "&quot;")
    }
}
