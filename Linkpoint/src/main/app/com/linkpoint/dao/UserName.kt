package com.linkpoint.dao

import com.google.common.base.Strings
import java.util.UUID

data class UserName(
    var uuid: UUID,
    var userName: String? = null,
    var displayName: String? = null,
    var isBadUUID: Boolean = false,
) {
    fun isComplete(): Boolean {
        return if (isBadUUID) {
            true
        } else {
            !Strings.isNullOrEmpty(userName) && !Strings.isNullOrEmpty(displayName)
        }
    }

    fun mergeWith(other: UserName): Boolean {
        if (other.isBadUUID && !this.isBadUUID) {
            this.isBadUUID = true
            return true
        }

        if (this.isBadUUID) {
            return false
        }

        var changed = false

        if (!Strings.isNullOrEmpty(other.userName) && this.userName != other.userName) {
            this.userName = other.userName
            changed = true
        }

        if (!Strings.isNullOrEmpty(other.displayName) && this.displayName != other.displayName) {
            this.displayName = other.displayName
            changed = true
        }

        return changed
    }
}
