package com.linkpoint.linden.llxml

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Color4
import com.linkpoint.linden.llmath.Color3
import com.linkpoint.linden.llmath.Vector3
import com.linkpoint.linden.llmath.Vector3d
import com.linkpoint.linden.llmath.Quaternion

// String-table-based XML tree for efficient attribute lookups.
// The "canonical name" concept from LLStdStringTable is replaced by
// a companion-object intern table that deduplicates attribute name strings.

class XmlTree {
    var root: XmlTreeNode? = null
        private set

    fun cleanup() {
        root = null
        nodeNames.clear()
    }

    fun parseFile(path: String, keepContents: Boolean = true): Boolean {
        cleanup()
        val xml = runCatching { java.io.File(path).readText(Charsets.UTF_8) }.getOrElse { return false }
        return parseString(xml, keepContents)
    }

    fun parseString(xml: String, keepContents: Boolean = true): Boolean {
        cleanup()
        val parsed = runCatching {
            XmlNode.parse(xml)
        }.getOrElse { return false } ?: return false
        root = XmlTreeNode.fromXmlNode(parsed, null, this, keepContents)
        return true
    }

    fun dump() { root?.dump("    ") }

    // Per-instance node-name intern table (mirrors LLStdStringTable mNodeNames).
    internal val nodeNames: MutableSet<String> = mutableSetOf()
    internal fun internNodeName(name: String): String {
        nodeNames.add(name)
        return nodeNames.first { it == name }
    }

    companion object {
        // Global attribute-key intern table (mirrors LLXmlTree::sAttributeKeys).
        private val attributeKeys: MutableSet<String> = mutableSetOf()

        fun addAttributeString(name: String): String {
            attributeKeys.add(name)
            return attributeKeys.first { it == name }
        }

        internal fun internAttr(name: String): String = addAttributeString(name)
    }
}

class XmlTreeNode internal constructor(
    val name: String,
    val parent: XmlTreeNode?,
    private val tree: XmlTree,
    contents: String = ""
) {
    private val attributes: MutableMap<String, String> = mutableMapOf()
    private val children: MutableList<XmlTreeNode> = mutableListOf()
    private var childrenIter: Int = 0
    private var namedChildIter: List<XmlTreeNode> = emptyList()
    private var namedChildIdx: Int = 0

    var contents: String = contents
        private set

    fun hasName(n: String): Boolean = name == n

    fun getTextContents(): String {
        val sb = StringBuilder()
        for (line in contents.split('\n')) {
            val trimmed = line.trim()
            if (trimmed.isNotEmpty()) {
                sb.append(trimmed).append('\n')
            }
        }
        return sb.toString()
    }

    fun hasAttribute(n: String): Boolean {
        val canonical = XmlTree.internAttr(n)
        return attributes.containsKey(canonical)
    }

    fun getChildCount(): Int = children.size

    fun getFirstChild(): XmlTreeNode? {
        childrenIter = 0
        return getNextChild()
    }

    fun getNextChild(): XmlTreeNode? {
        if (childrenIter >= children.size) return null
        return children[childrenIter++]
    }

    fun getChildByName(n: String): XmlTreeNode? {
        namedChildIter = children.filter { it.name == n }
        namedChildIdx = 0
        return getNextNamedChild()
    }

    fun getNextNamedChild(): XmlTreeNode? {
        if (namedChildIdx >= namedChildIter.size) return null
        return namedChildIter[namedChildIdx++]
    }

    internal fun addAttribute(attrName: String, attrValue: String) {
        val canonical = XmlTree.internAttr(attrName)
        attributes[canonical] = attrValue
    }

    internal fun appendContents(str: String) {
        contents += str
    }

    internal fun addChild(child: XmlTreeNode) {
        children.add(child)
    }

    fun dump(prefix: String) {
        val attrsStr = attributes.entries.joinToString(" ") { "${it.key}=${it.value}" }
        println("$prefix$name${if (contents.isNotEmpty()) " contents=\"$contents\"" else ""}${if (attrsStr.isNotEmpty()) " $attrsStr" else ""}")
    }

    // ---- Typed attribute getters ----

    fun getFastAttributeString(canonical: String, out: StringBuilder): Boolean {
        val v = attributes[canonical] ?: return false
        out.append(v)
        return true
    }

    private fun attrValue(n: String): String? = attributes[XmlTree.internAttr(n)]

    fun getAttributeString(name: String, out: StringBuilder): Boolean {
        val v = attrValue(name) ?: return false
        out.append(v); return true
    }

    fun getAttributeString(name: String): String? = attrValue(name)

    fun getAttributeBOOL(name: String): Boolean? {
        val v = attrValue(name) ?: return null
        return v.lowercase() == "true" || v == "1"
    }

    fun getAttributeU8(name: String): UByte? = attrValue(name)?.trim()?.toUByteOrNull()
    fun getAttributeS8(name: String): Byte? = attrValue(name)?.trim()?.toByteOrNull()
    fun getAttributeU16(name: String): UShort? = attrValue(name)?.trim()?.toUShortOrNull()
    fun getAttributeS16(name: String): Short? = attrValue(name)?.trim()?.toShortOrNull()
    fun getAttributeU32(name: String): UInt? = attrValue(name)?.trim()?.toUIntOrNull()
    fun getAttributeS32(name: String): Int? = attrValue(name)?.trim()?.toIntOrNull()
    fun getAttributeF32(name: String): Float? = attrValue(name)?.trim()?.toFloatOrNull()
    fun getAttributeF64(name: String): Double? = attrValue(name)?.trim()?.toDoubleOrNull()

    fun getAttributeUUID(name: String): LLUUID? {
        val v = attrValue(name)?.trim() ?: return null
        return LLUUID.fromString(v)
    }

    fun getAttributeColor4(name: String): Color4? {
        val v = attrValue(name)?.trim() ?: return null
        val p = v.split(Regex("\\s+"))
        if (p.size < 4) return null
        return runCatching { Color4(p[0].toFloat(), p[1].toFloat(), p[2].toFloat(), p[3].toFloat()) }.getOrNull()
    }

    fun getAttributeColor(name: String): Color4? = getAttributeColor4(name)

    fun getAttributeColor4U(name: String): Color4? {
        val v = attrValue(name)?.trim() ?: return null
        val p = v.split(Regex("\\s+"))
        if (p.size < 4) return null
        return runCatching {
            Color4(
                p[0].toInt() / 255f,
                p[1].toInt() / 255f,
                p[2].toInt() / 255f,
                p[3].toInt() / 255f
            )
        }.getOrNull()
    }

    fun getAttributeVector3(name: String): Vector3? {
        val v = attrValue(name)?.trim() ?: return null
        val p = v.split(Regex("\\s+"))
        if (p.size < 3) return null
        return runCatching { Vector3(p[0].toFloat(), p[1].toFloat(), p[2].toFloat()) }.getOrNull()
    }

    fun getAttributeVector3d(name: String): Vector3d? {
        val v = attrValue(name)?.trim() ?: return null
        val p = v.split(Regex("\\s+"))
        if (p.size < 3) return null
        return runCatching { Vector3d(p[0].toDouble(), p[1].toDouble(), p[2].toDouble()) }.getOrNull()
    }

    fun getAttributeQuat(name: String): Quaternion? {
        val v = attrValue(name)?.trim() ?: return null
        val p = v.split(Regex("\\s+"))
        if (p.size < 4) return null
        return runCatching { Quaternion(p[0].toFloat(), p[1].toFloat(), p[2].toFloat(), p[3].toFloat()) }.getOrNull()
    }

    companion object {
        internal fun fromXmlNode(
            node: XmlNode,
            parent: XmlTreeNode?,
            tree: XmlTree,
            keepContents: Boolean
        ): XmlTreeNode {
            val treeNode = XmlTreeNode(tree.internNodeName(node.name), parent, tree)
            for ((k, v) in node.attributes) {
                if (k != "type") treeNode.addAttribute(k, v.value)
            }
            if (keepContents && node.value.isNotEmpty()) {
                treeNode.appendContents(node.value)
            }
            for (child in node.children) {
                if (!child.isAttribute) {
                    val childNode = fromXmlNode(child, treeNode, tree, keepContents)
                    treeNode.addChild(childNode)
                }
            }
            return treeNode
        }
    }
}
