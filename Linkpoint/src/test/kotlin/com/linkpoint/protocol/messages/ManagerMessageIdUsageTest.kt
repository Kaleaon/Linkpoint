package com.linkpoint.protocol.messages

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

class ManagerMessageIdUsageTest {

    @Test
    fun outboundManagerMessageIds_matchExpectedProtocolIntegers() {
        val expected = mapOf(
            // UserProfileManager
            "AVATAR_PROPERTIES_REQUEST" to (0xFFFF00A9).toInt(),
            "AVATAR_INTERESTS_REQUEST" to (0xFFFF00AE).toInt(),
            "AVATAR_NOTES_REQUEST" to (0xFFFF00B2).toInt(),
            "AVATAR_NOTES_UPDATE" to (0xFFFF00B1).toInt(),
            "AVATAR_PICKS_REQUEST" to (0xFFFF00B4).toInt(),

            // MuteManager
            "MUTE_LIST_REQUEST" to (0xFFFF0106).toInt(),
            "UPDATE_MUTE_LIST_ENTRY" to (0xFFFF0107).toInt(),
            "REMOVE_MUTE_LIST_ENTRY" to (0xFFFF0108).toInt(),

            // Transfer/Xfer managers
            "TRANSFER_REQUEST" to (0xFFFF0099).toInt(),
            "TRANSFER_ABORT" to (0xFFFF009B).toInt(),
            "CONFIRM_XFER_PACKET" to 19,
            "REQUEST_XFER" to (0xFFFF009C).toInt(),

            // Task inventory
            "REQUEST_TASK_INVENTORY" to (0xFFFF0121).toInt(),

            // Economy
            "MONEY_BALANCE_REQUEST" to (0xFFFF0139).toInt(),
            "ECONOMY_DATA_REQUEST" to (0xFFFF0018).toInt(),
            "MONEY_TRANSFER_REQUEST" to (0xFFFF0137).toInt(),

            // Script dialog
            "SCRIPT_DIALOG_REPLY" to (0xFFFF00BF).toInt(),
            "SCRIPT_ANSWER_YES" to (0xFFFF0084).toInt(),
        )

        expected.forEach { (name, value) ->
            val actual = MessageIds::class.java.getField(name).getInt(null)
            assertEquals("Unexpected integer for MessageIds.$name", value, actual)
        }
    }

    @Test
    fun targetedManagers_doNotContainRawProtocolHexLiterals() {
        val repoRoot = File(System.getProperty("user.dir"))
        val managerFiles = listOf(
            "src/main/java/com/linkpoint/users/UserProfileManager.kt",
            "src/main/java/com/linkpoint/users/MuteManager.kt",
            "src/main/java/com/linkpoint/protocol/transfer/TransferManager.kt",
            "src/main/java/com/linkpoint/protocol/transfer/XferManager.kt",
            "src/main/java/com/linkpoint/objects/inventory/TaskInventoryManager.kt",
            "src/main/java/com/linkpoint/economy/EconomyManager.kt",
            "src/main/java/com/linkpoint/chat/dialogs/ScriptDialogManager.kt",
        )

        val protocolLiteralPattern = Regex("0xFF(?:00|FF)[0-9A-Fa-f]{2,}")
        val violations = mutableListOf<String>()

        managerFiles.forEach { relativePath ->
            val file = File(repoRoot, relativePath)
            val text = file.readText()
            protocolLiteralPattern.findAll(text).forEach { match ->
                violations.add("$relativePath: `${match.value}`")
            }
        }

        if (violations.isNotEmpty()) {
            fail("Found raw protocol hex literals outside MessageIds.kt:\n${violations.joinToString("\n")}")
        }
    }
}
