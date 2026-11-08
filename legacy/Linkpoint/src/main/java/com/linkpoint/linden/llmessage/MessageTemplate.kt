package com.linkpoint.linden.llmessage

enum class Frequency(val byteSize: Int) {
    HIGH(1),
    MEDIUM(2),
    LOW(4),
    FIXED(4)
}

enum class Trust {
    NOTRUST,
    TRUST
}

enum class Encoding {
    UNENCODED,
    ZEROCODED
}

enum class Deprecation {
    NOT_DEPRECATED,
    UDP_DEPRECATED,
    UDP_BLACKLISTED,
    DEPRECATED
}

enum class VarType {
    U8, U16, U32,
    S8, S16, S32,
    F32, F64,
    LLUUID,
    BOOL,
    LLVECTOR3,
    LLVECTOR3D,
    LLVECTOR4,
    LLQUATERNION,
    IPADDR,
    IPPORT,
    FIXED,
    VARIABLE
}

class MessageVariable(
    val name: String,
    val type: VarType,
    val size: Int
)

class MessageBlock(
    val name: String,
    val type: BlockType,
    val number: Int = 1
) {
    enum class BlockType { NULL, SINGLE, MULTIPLE, VARIABLE, EOF }

    private val _variables: MutableList<MessageVariable> = mutableListOf()
    val variables: List<MessageVariable> get() = _variables

    var totalSize: Int = 0
        private set

    fun addVariable(varName: String, varType: VarType, varSize: Int) {
        _variables.add(MessageVariable(varName, varType, varSize))
        if (varType != VarType.VARIABLE && totalSize != -1) {
            totalSize += varSize
        } else {
            totalSize = -1
        }
    }

    fun getVariable(varName: String): MessageVariable? = _variables.firstOrNull { it.name == varName }
}

class MessageTemplate(
    val name: String,
    val frequency: Frequency,
    val messageNumber: UInt,
    val trust: Trust = Trust.NOTRUST,
    val encoding: Encoding = Encoding.ZEROCODED,
    val deprecation: Deprecation = Deprecation.NOT_DEPRECATED
) {
    private val _blocks: MutableList<MessageBlock> = mutableListOf()
    val blocks: List<MessageBlock> get() = _blocks

    var totalSize: Int = 0
        private set

    var receiveCount: UInt = 0u
    var receiveBytes: UInt = 0u
    var receiveInvalid: UInt = 0u
    var decodeTimeThisFrame: Float = 0f
    var totalDecoded: UInt = 0u
    var totalDecodeTime: Float = 0f
    var maxDecodeTimePerMsg: Float = 0f

    var banFromTrusted: Boolean = false
    var banFromUntrusted: Boolean = false

    fun addBlock(block: MessageBlock) {
        _blocks.add(block)
        if (totalSize != -1 && block.totalSize != -1 &&
            (block.type == MessageBlock.BlockType.SINGLE || block.type == MessageBlock.BlockType.MULTIPLE)
        ) {
            totalSize += block.number * block.totalSize
        } else {
            totalSize = -1
        }
    }

    fun getBlock(blockName: String): MessageBlock? = _blocks.firstOrNull { it.name == blockName }

    fun isUdpBanned(): Boolean = deprecation == Deprecation.UDP_BLACKLISTED

    fun isBanned(trustedSource: Boolean): Boolean = if (trustedSource) banFromTrusted else banFromUntrusted
}
