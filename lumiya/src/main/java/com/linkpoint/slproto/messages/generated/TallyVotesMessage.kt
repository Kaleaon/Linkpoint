package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class TallyVotesMessage : SLMessage() {
    val groupMembersRequest: MutableList<GroupMembersRequestBlock> = mutableListOf()

    class GroupMembersRequestBlock

    override fun packPayload(buffer: ByteBuffer) {
        groupMembersRequest.forEach { entry ->
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        run {
            val count = unpackByte(buffer)
            groupMembersRequest.clear()
            repeat(count) {
                val entry = GroupMembersRequestBlock()
                groupMembersRequest += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF016D.toInt()

    override fun getMessageName(): String = "TallyVotes"
}
