package com.linkpoint.linden.llxml

import com.linkpoint.linden.llcommon.LLSD
import com.linkpoint.linden.llmath.Color3
import com.linkpoint.linden.llmath.Color4
import com.linkpoint.linden.llmath.Quaternion
import com.linkpoint.linden.llmath.Rect
import com.linkpoint.linden.llmath.Vector3
import com.linkpoint.linden.llmath.Vector3d

enum class ControlType(val typeName: String) {
    U32("U32"),
    S32("S32"),
    F32("F32"),
    BOOLEAN("Boolean"),
    STRING("String"),
    VEC3("Vector3"),
    VEC3D("Vector3D"),
    QUAT("Quaternion"),
    RECT("Rect"),
    COL4("Color4"),
    COL3("Color3"),
    LLSD("LLSD");

    companion object {
        fun fromString(s: String): ControlType? = values().firstOrNull { it.typeName == s }
    }
}

enum class SanityType {
    NONE, EQUALS, NOT_EQUALS, LESS_THAN, GREATER_THAN,
    LESS_THAN_EQUALS, GREATER_THAN_EQUALS, BETWEEN, NOT_BETWEEN;

    companion object {
        fun fromString(s: String): SanityType = when (s) {
            "Equals"             -> EQUALS
            "NotEquals"          -> NOT_EQUALS
            "LessThan"           -> LESS_THAN
            "GreaterThan"        -> GREATER_THAN
            "LessThanEquals"     -> LESS_THAN_EQUALS
            "GreaterThanEquals"  -> GREATER_THAN_EQUALS
            "Between"            -> BETWEEN
            "NotBetween"         -> NOT_BETWEEN
            else                 -> NONE
        }
    }
}

enum class PersistMode { NO, NONDFT, ALWAYS }

class ControlVariable(
    val name: String,
    val type: ControlType,
    defaultValue: LLSD,
    var comment: String,
    val sanityType: SanityType = SanityType.NONE,
    val sanityValues: List<LLSD> = listOf(LLSD.Undefined, LLSD.Undefined),
    val sanityComment: String = "",
    var persist: PersistMode = PersistMode.NONDFT,
    var canBackup: Boolean = true,
    var hideFromSettingsEditor: Boolean = false
) {
    // mValues stack: [0] = default, [1] = saved value (optional), [2+] = unsaved overrides
    private val values: MutableList<LLSD> = mutableListOf(defaultValue)

    private val commitListeners: MutableList<(ControlVariable, LLSD, LLSD) -> Unit> = mutableListOf()
    private val validateListeners: MutableList<(ControlVariable, LLSD) -> Boolean> = mutableListOf()
    private val sanityListeners: MutableList<(ControlVariable, Boolean) -> Unit> = mutableListOf()

    fun get(): LLSD = values.last()
    fun getValue(): LLSD = values.last()
    fun getDefault(): LLSD = values.first()

    fun getSaveValue(): LLSD = if (values.size > 1) values[1] else values[0]

    fun isDefault(): Boolean = values.size == 1
    fun isPersisted(): Boolean = persist != PersistMode.NO
    fun isBackupable(): Boolean = canBackup

    fun shouldSave(nondefaultOnly: Boolean): Boolean {
        if (persist == PersistMode.NO) return false
        if (persist == PersistMode.ALWAYS) return true
        if (!nondefaultOnly) return true
        if (isDefault()) return false
        return !llsdCompare(getSaveValue(), getDefault())
    }

    fun set(value: LLSD) = setValue(value)

    fun setValue(newValue: LLSD, savedValue: Boolean = true) {
        val valid = validateListeners.all { it(this, newValue) }
        if (!valid) return

        val storable = normalizeValue(newValue)
        val original = getValue()
        val changed = !llsdCompare(original, storable)

        if (savedValue) {
            resetToDefault(false)
            if (!llsdCompare(values.last(), storable)) {
                values.add(storable)
            }
        } else {
            if (!llsdCompare(values.last(), storable)) {
                while (values.size > 2) values.removeAt(values.lastIndex)
                if (values.size < 2) values.add(values[0])
                values.add(storable)
            }
        }

        if (changed) {
            firePropertyChanged(original)
            val sane = isSane()
            sanityListeners.forEach { it(this, sane) }
        }
    }

    fun setDefaultValue(value: LLSD) {
        val comparable = normalizeValue(value)
        val original = getValue()
        val changed = !llsdCompare(original, comparable)
        resetToDefault(false)
        values[0] = comparable
        if (changed) {
            val sane = isSane()
            sanityListeners.forEach { it(this, sane) }
            firePropertyChanged(original)
        }
    }

    fun resetToDefault(fireSignal: Boolean = false) {
        val original = values.last()
        while (values.size > 1) values.removeAt(values.lastIndex)
        if (fireSignal) firePropertyChanged(original)
    }

    fun isSane(): Boolean {
        if (sanityType == SanityType.NONE) return true
        if (values.size < 2 || values[1].isUndefined()) return true
        val v = values[1]
        val s0 = sanityValues.getOrElse(0) { LLSD.Undefined }
        val s1 = sanityValues.getOrElse(1) { LLSD.Undefined }
        return when (sanityType) {
            SanityType.NONE                 -> true
            SanityType.EQUALS               -> llsdCompare(v, s0)
            SanityType.NOT_EQUALS           -> !llsdCompare(v, s0)
            SanityType.LESS_THAN            -> v.asReal() < s0.asReal()
            SanityType.GREATER_THAN         -> v.asReal() > s0.asReal()
            SanityType.LESS_THAN_EQUALS     -> v.asReal() <= s0.asReal()
            SanityType.GREATER_THAN_EQUALS  -> v.asReal() >= s0.asReal()
            SanityType.BETWEEN              -> v.asReal() >= s0.asReal() && v.asReal() <= s1.asReal()
            SanityType.NOT_BETWEEN          -> v.asReal() <= s0.asReal() || v.asReal() >= s1.asReal()
        }
    }

    fun addCommitListener(listener: (ControlVariable, LLSD, LLSD) -> Unit) {
        commitListeners.add(listener)
    }

    fun removeCommitListener(listener: (ControlVariable, LLSD, LLSD) -> Unit) {
        commitListeners.remove(listener)
    }

    fun addValidateListener(listener: (ControlVariable, LLSD) -> Boolean) {
        validateListeners.add(listener)
    }

    fun removeValidateListener(listener: (ControlVariable, LLSD) -> Boolean) {
        validateListeners.remove(listener)
    }

    fun addSanityListener(listener: (ControlVariable, Boolean) -> Unit) {
        sanityListeners.add(listener)
    }

    private fun firePropertyChanged(previous: LLSD) {
        commitListeners.forEach { it(this, values.last(), previous) }
    }

    // Normalizes string-encoded booleans and LLSD for type-consistent comparison.
    private fun normalizeValue(value: LLSD): LLSD {
        if (type == ControlType.BOOLEAN && value is LLSD.LLSDString) {
            val s = value.value.lowercase()
            return LLSD.of(s == "true" || s == "1")
        }
        return value
    }

    private fun llsdCompare(a: LLSD, b: LLSD): Boolean = when (type) {
        ControlType.U32,
        ControlType.S32      -> a.asInt() == b.asInt()
        ControlType.BOOLEAN  -> a.asBoolean() == b.asBoolean()
        ControlType.F32      -> a.asReal() == b.asReal()
        ControlType.STRING   -> a.asString() == b.asString()
        else                 -> a == b
    }
}

class ControlGroup(val name: String) {
    private val nameTable: MutableMap<String, ControlVariable> = mutableMapOf()

    fun getControl(n: String): ControlVariable? = nameTable[n]

    fun controlExists(n: String): Boolean = nameTable.containsKey(n)

    fun declareControl(
        n: String,
        type: ControlType,
        defaultValue: LLSD,
        comment: String,
        sanityType: SanityType = SanityType.NONE,
        sanityValues: LLSD = LLSD.emptyArray(),
        sanityComment: String = "",
        persist: PersistMode = PersistMode.NONDFT,
        canBackup: Boolean = true,
        hideFromSettingsEditor: Boolean = false
    ): ControlVariable {
        val existing = nameTable[n]
        if (existing != null) {
            if (persist != PersistMode.NO && existing.type == type) {
                if (existing.getDefault() != defaultValue) {
                    val cur = existing.getValue()
                    existing.setDefaultValue(defaultValue)
                    existing.setValue(cur)
                }
            }
            return existing
        }
        val sv = listOf(sanityValues[0], sanityValues[1])
        val ctrl = ControlVariable(
            n, type, defaultValue, comment,
            sanityType, sv, sanityComment,
            persist, canBackup, hideFromSettingsEditor
        )
        nameTable[n] = ctrl
        return ctrl
    }

    fun declareU32(n: String, initial: UInt, comment: String, persist: PersistMode = PersistMode.NONDFT): ControlVariable =
        declareControl(n, ControlType.U32, LLSD.of(initial.toInt()), comment, persist = persist)

    fun declareS32(n: String, initial: Int, comment: String, persist: PersistMode = PersistMode.NONDFT): ControlVariable =
        declareControl(n, ControlType.S32, LLSD.of(initial), comment, persist = persist)

    fun declareF32(n: String, initial: Float, comment: String, persist: PersistMode = PersistMode.NONDFT): ControlVariable =
        declareControl(n, ControlType.F32, LLSD.of(initial.toDouble()), comment, persist = persist)

    fun declareBool(n: String, initial: Boolean, comment: String, persist: PersistMode = PersistMode.NONDFT): ControlVariable =
        declareControl(n, ControlType.BOOLEAN, LLSD.of(initial), comment, persist = persist)

    fun declareString(n: String, initial: String, comment: String, persist: PersistMode = PersistMode.NONDFT): ControlVariable =
        declareControl(n, ControlType.STRING, LLSD.of(initial), comment, persist = persist)

    fun declareVec3(n: String, initial: Vector3, comment: String, persist: PersistMode = PersistMode.NONDFT): ControlVariable =
        declareControl(n, ControlType.VEC3, vec3ToLlsd(initial), comment, persist = persist)

    fun declareVec3d(n: String, initial: Vector3d, comment: String, persist: PersistMode = PersistMode.NONDFT): ControlVariable =
        declareControl(n, ControlType.VEC3D, vec3dToLlsd(initial), comment, persist = persist)

    fun declareQuat(n: String, initial: Quaternion, comment: String, persist: PersistMode = PersistMode.NONDFT): ControlVariable =
        declareControl(n, ControlType.QUAT, quatToLlsd(initial), comment, persist = persist)

    fun declareRect(n: String, initial: Rect, comment: String, persist: PersistMode = PersistMode.NONDFT): ControlVariable =
        declareControl(n, ControlType.RECT, rectToLlsd(initial), comment, persist = persist)

    fun declareColor4(n: String, initial: Color4, comment: String, persist: PersistMode = PersistMode.NONDFT): ControlVariable =
        declareControl(n, ControlType.COL4, color4ToLlsd(initial), comment, persist = persist)

    fun declareColor3(n: String, initial: Color3, comment: String, persist: PersistMode = PersistMode.NONDFT): ControlVariable =
        declareControl(n, ControlType.COL3, color3ToLlsd(initial), comment, persist = persist)

    fun declareLLSD(n: String, initial: LLSD, comment: String, persist: PersistMode = PersistMode.NONDFT): ControlVariable =
        declareControl(n, ControlType.LLSD, initial, comment, persist = persist)

    // Typed getters
    fun getBool(n: String): Boolean = nameTable[n]?.get()?.asBoolean() ?: false
    fun getS32(n: String): Int = nameTable[n]?.get()?.asInt() ?: 0
    fun getU32(n: String): UInt = (nameTable[n]?.get()?.asInt() ?: 0).toUInt()
    fun getF32(n: String): Float = nameTable[n]?.get()?.asReal()?.toFloat() ?: 0f
    fun getString(n: String): String = nameTable[n]?.get()?.asString() ?: ""
    fun getLLSD(n: String): LLSD = nameTable[n]?.get() ?: LLSD.Undefined

    fun getVector3(n: String): Vector3 = llsdToVec3(nameTable[n]?.get() ?: LLSD.Undefined)
    fun getVector3d(n: String): Vector3d = llsdToVec3d(nameTable[n]?.get() ?: LLSD.Undefined)
    fun getQuaternion(n: String): Quaternion = llsdToQuat(nameTable[n]?.get() ?: LLSD.Undefined)
    fun getRect(n: String): Rect = llsdToRect(nameTable[n]?.get() ?: LLSD.Undefined)
    fun getColor4(n: String): Color4 = llsdToColor4(nameTable[n]?.get() ?: LLSD.Undefined)
    fun getColor3(n: String): Color3 = llsdToColor3(nameTable[n]?.get() ?: LLSD.Undefined)

    // Typed setters
    fun setBool(n: String, v: Boolean) = nameTable[n]?.set(LLSD.of(v))
    fun setS32(n: String, v: Int) = nameTable[n]?.set(LLSD.of(v))
    fun setU32(n: String, v: UInt) = nameTable[n]?.set(LLSD.of(v.toInt()))
    fun setF32(n: String, v: Float) = nameTable[n]?.set(LLSD.of(v.toDouble()))
    fun setString(n: String, v: String) = nameTable[n]?.set(LLSD.of(v))
    fun setVector3(n: String, v: Vector3) = nameTable[n]?.set(vec3ToLlsd(v))
    fun setVector3d(n: String, v: Vector3d) = nameTable[n]?.set(vec3dToLlsd(v))
    fun setQuaternion(n: String, v: Quaternion) = nameTable[n]?.set(quatToLlsd(v))
    fun setRect(n: String, v: Rect) = nameTable[n]?.set(rectToLlsd(v))
    fun setColor4(n: String, v: Color4) = nameTable[n]?.set(color4ToLlsd(v))
    fun setLLSD(n: String, v: LLSD) = nameTable[n]?.set(v)

    fun setUntypedValue(n: String, v: LLSD) = nameTable[n]?.setValue(v)

    fun resetToDefaults() = nameTable.values.forEach { it.resetToDefault(true) }

    fun cleanup() = nameTable.clear()

    fun applyToAll(action: (String, ControlVariable) -> Unit) =
        nameTable.forEach { (k, v) -> action(k, v) }

    // Load settings from an LLSD-XML XmlNode tree (modern format).
    // Each child element represents one control: name -> { Type, Comment, Value, Persist, Backup }.
    fun loadFromXml(node: XmlNode, setDefaultValues: Boolean = false, saveValues: Boolean = true): Int {
        var count = 0
        for (child in node.children) {
            if (child.isAttribute) continue
            val ctrlName = child.name
            val typeStr = child.getAttributeString("Type") ?: child.getChild("Type")?.value ?: continue
            val ctrlType = ControlType.fromString(typeStr) ?: continue
            val valueNode = child.getChild("Value") ?: continue
            val commentStr = child.getChild("Comment")?.value ?: ""
            val persistInt = child.getChild("Persist")?.value?.trim()?.toIntOrNull() ?: 1
            val persist = if (persistInt != 0) PersistMode.NONDFT else PersistMode.NO
            val canBackup = child.getChild("Backup")?.value?.trim()?.lowercase() != "false"
            val hide = child.getChild("HideFromEditor")?.value?.trim()?.toIntOrNull() != 0

            val llsdValue = xmlNodeToLlsd(valueNode, ctrlType)

            val existing = nameTable[ctrlName]
            if (existing != null) {
                if (setDefaultValues) {
                    if (existing.type == ctrlType) {
                        existing.setDefaultValue(llsdValue)
                        existing.persist = persist
                        existing.comment = commentStr
                        existing.hideFromSettingsEditor = hide
                    }
                } else if (existing.isPersisted()) {
                    existing.setValue(llsdValue, saveValues)
                }
            } else {
                val ctrl = ControlVariable(
                    ctrlName, ctrlType, llsdValue, commentStr,
                    persist = persist, canBackup = canBackup,
                    hideFromSettingsEditor = hide
                )
                nameTable[ctrlName] = ctrl
            }
            count++
        }
        return count
    }

    // Serialize all persistent controls to an XmlNode tree.
    fun saveToXml(nondefaultOnly: Boolean = false): XmlNode {
        val root = XmlNode("llsd")
        for ((ctrlName, ctrl) in nameTable.entries.sortedBy { it.key }) {
            if (!ctrl.shouldSave(nondefaultOnly)) continue
            val entry = XmlNode(ctrlName)
            entry.addChild(textNode("Type", ctrl.type.typeName))
            entry.addChild(textNode("Comment", ctrl.comment))
            entry.addChild(textNode("Persist", if (ctrl.persist != PersistMode.NO) "1" else "0"))
            entry.addChild(textNode("Backup", if (ctrl.canBackup) "true" else "false"))
            entry.addChild(llsdToXmlNode("Value", ctrl.getSaveValue(), ctrl.type))
            root.addChild(entry)
        }
        return root
    }

    companion object {
        fun typeStringToEnum(s: String): ControlType? = ControlType.fromString(s)
        fun typeEnumToString(t: ControlType): String = t.typeName

        // LLSD <-> math type conversions (array form, matching LL conventions)
        fun vec3ToLlsd(v: Vector3): LLSD =
            LLSD.ofArray(listOf(LLSD.of(v.x.toDouble()), LLSD.of(v.y.toDouble()), LLSD.of(v.z.toDouble())))

        fun vec3dToLlsd(v: Vector3d): LLSD =
            LLSD.ofArray(listOf(LLSD.of(v.x), LLSD.of(v.y), LLSD.of(v.z)))

        fun quatToLlsd(q: Quaternion): LLSD =
            LLSD.ofArray(listOf(LLSD.of(q.x.toDouble()), LLSD.of(q.y.toDouble()), LLSD.of(q.z.toDouble()), LLSD.of(q.w.toDouble())))

        fun rectToLlsd(r: Rect): LLSD =
            LLSD.ofArray(listOf(LLSD.of(r.left), LLSD.of(r.bottom), LLSD.of(r.right), LLSD.of(r.top)))

        fun color4ToLlsd(c: Color4): LLSD =
            LLSD.ofArray(listOf(LLSD.of(c.r.toDouble()), LLSD.of(c.g.toDouble()), LLSD.of(c.b.toDouble()), LLSD.of(c.a.toDouble())))

        fun color3ToLlsd(c: Color3): LLSD =
            LLSD.ofArray(listOf(LLSD.of(c.r.toDouble()), LLSD.of(c.g.toDouble()), LLSD.of(c.b.toDouble())))

        fun llsdToVec3(sd: LLSD): Vector3 {
            if (sd !is LLSD.LLSDArray || sd.value.size < 3) return Vector3()
            return Vector3(sd.value[0].asReal().toFloat(), sd.value[1].asReal().toFloat(), sd.value[2].asReal().toFloat())
        }

        fun llsdToVec3d(sd: LLSD): Vector3d {
            if (sd !is LLSD.LLSDArray || sd.value.size < 3) return Vector3d()
            return Vector3d(sd.value[0].asReal(), sd.value[1].asReal(), sd.value[2].asReal())
        }

        fun llsdToQuat(sd: LLSD): Quaternion {
            if (sd !is LLSD.LLSDArray || sd.value.size < 4) return Quaternion()
            return Quaternion(
                sd.value[0].asReal().toFloat(), sd.value[1].asReal().toFloat(),
                sd.value[2].asReal().toFloat(), sd.value[3].asReal().toFloat()
            )
        }

        fun llsdToRect(sd: LLSD): Rect {
            if (sd !is LLSD.LLSDArray || sd.value.size < 4) return Rect()
            return Rect(sd.value[0].asInt(), sd.value[3].asInt(), sd.value[2].asInt(), sd.value[1].asInt())
        }

        fun llsdToColor4(sd: LLSD): Color4 {
            if (sd !is LLSD.LLSDArray || sd.value.size < 4) return Color4()
            return Color4(
                sd.value[0].asReal().toFloat(), sd.value[1].asReal().toFloat(),
                sd.value[2].asReal().toFloat(), sd.value[3].asReal().toFloat()
            )
        }

        fun llsdToColor3(sd: LLSD): Color3 {
            if (sd !is LLSD.LLSDArray || sd.value.size < 3) return Color3()
            return Color3(sd.value[0].asReal().toFloat(), sd.value[1].asReal().toFloat(), sd.value[2].asReal().toFloat())
        }

        private fun textNode(name: String, text: String): XmlNode {
            val n = XmlNode(name)
            n.value = text
            return n
        }

        private fun parseTupleValues(node: XmlNode, expectedCount: Int): List<Double>? {
            val textParts = node.value.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
            if (textParts.size >= expectedCount) {
                val parsed = textParts.take(expectedCount).map { it.toDoubleOrNull() ?: return null }
                return parsed
            }

            val childValues = node.children
                .asSequence()
                .filter { !it.isAttribute }
                .mapNotNull { it.value.trim().toDoubleOrNull() }
                .take(expectedCount)
                .toList()

            return if (childValues.size >= expectedCount) childValues else null
        }

        private fun xmlNodeToLlsd(node: XmlNode, type: ControlType): LLSD = when (type) {
            ControlType.BOOLEAN -> LLSD.of(node.value.trim().lowercase() == "true" || node.value.trim() == "1")
            ControlType.S32     -> LLSD.of(node.value.trim().toIntOrNull() ?: 0)
            ControlType.U32     -> LLSD.of((node.value.trim().toUIntOrNull() ?: 0u).toInt())
            ControlType.F32     -> LLSD.of(node.value.trim().toDoubleOrNull() ?: 0.0)
            ControlType.STRING  -> LLSD.of(node.value)
            ControlType.VEC3    -> {
                val p = parseTupleValues(node, 3)
                if (p != null) vec3ToLlsd(Vector3(p[0].toFloat(), p[1].toFloat(), p[2].toFloat()))
                else LLSD.Undefined
            }
            ControlType.VEC3D   -> {
                val p = parseTupleValues(node, 3)
                if (p != null) vec3dToLlsd(Vector3d(p[0], p[1], p[2]))
                else LLSD.Undefined
            }
            ControlType.QUAT    -> {
                val p = parseTupleValues(node, 4)
                if (p != null) quatToLlsd(Quaternion(p[0].toFloat(), p[1].toFloat(), p[2].toFloat(), p[3].toFloat()))
                else LLSD.Undefined
            }
            ControlType.RECT    -> {
                val p = parseTupleValues(node, 4)
                if (p != null) rectToLlsd(Rect(p[0].toInt(), p[3].toInt(), p[2].toInt(), p[1].toInt()))
                else LLSD.Undefined
            }
            ControlType.COL4    -> {
                val p = parseTupleValues(node, 4)
                if (p != null) color4ToLlsd(Color4(p[0].toFloat(), p[1].toFloat(), p[2].toFloat(), p[3].toFloat()))
                else LLSD.Undefined
            }
            ControlType.COL3    -> {
                val p = parseTupleValues(node, 3)
                if (p != null) color3ToLlsd(Color3(p[0].toFloat(), p[1].toFloat(), p[2].toFloat()))
                else LLSD.Undefined
            }
            ControlType.LLSD    -> LLSD.of(node.value)
        }

        private fun llsdToXmlNode(nodeName: String, sd: LLSD, type: ControlType): XmlNode {
            val node = XmlNode(nodeName)
            node.value = when (type) {
                ControlType.BOOLEAN -> if (sd.asBoolean()) "true" else "false"
                ControlType.S32, ControlType.U32 -> sd.asInt().toString()
                ControlType.F32     -> sd.asReal().toString()
                ControlType.STRING  -> sd.asString()
                ControlType.VEC3    -> llsdToVec3(sd).let { "${it.x} ${it.y} ${it.z}" }
                ControlType.VEC3D   -> llsdToVec3d(sd).let { "${it.x} ${it.y} ${it.z}" }
                ControlType.QUAT    -> llsdToQuat(sd).let { "${it.x} ${it.y} ${it.z} ${it.w}" }
                ControlType.RECT    -> llsdToRect(sd).let { "${it.left} ${it.bottom} ${it.right} ${it.top}" }
                ControlType.COL4    -> llsdToColor4(sd).let { "${it.r} ${it.g} ${it.b} ${it.a}" }
                ControlType.COL3    -> llsdToColor3(sd).let { "${it.r} ${it.g} ${it.b}" }
                ControlType.LLSD    -> sd.asString()
            }
            return node
        }
    }
}
