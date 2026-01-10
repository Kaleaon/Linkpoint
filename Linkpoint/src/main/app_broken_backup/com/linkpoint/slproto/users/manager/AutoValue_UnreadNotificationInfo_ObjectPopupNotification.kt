package com.linkpoint.slproto.users.manager

import com.google.common.base.Optional
import com.linkpoint.slproto.users.manager.UnreadNotificationInfo
import androidx.annotation.NonNull

class AutoValue_UnreadNotificationInfo_ObjectPopupNotification : UnreadNotificationInfo.ObjectPopupNotification {
    private Int freshObjectPopupsCount
    private Optional<UnreadNotificationInfo.ObjectPopupMessage> lastObjectPopup
    private Int objectPopupsCount

    AutoValue_UnreadNotificationInfo_ObjectPopupNotification(Int i, Int i2, Optional<UnreadNotificationInfo.ObjectPopupMessage> optional) {
        this.freshObjectPopupsCount = i
        this.objectPopupsCount = i2
        if (optional == null) {
            throw NullPointerException("Null lastObjectPopup")
        }
        this.lastObjectPopup = optional
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj is UnreadNotificationInfo.ObjectPopupNotification)) {
            return false
        }
        UnreadNotificationInfo.ObjectPopupNotification objectPopupNotification = (UnreadNotificationInfo.ObjectPopupNotification) obj
        if (this.freshObjectPopupsCount == objectPopupNotification.freshObjectPopupsCount() && this.objectPopupsCount == objectPopupNotification.objectPopupsCount()) {
            return this.lastObjectPopup.equals(objectPopupNotification.lastObjectPopup())
        }
        return false
    }

    fun freshObjectPopupsCount(): Int {
        return this.freshObjectPopupsCount
    }

    fun hashCode(): Int {
        return ((((this.freshObjectPopupsCount ^ 1000003) * 1000003) ^ this.objectPopupsCount) * 1000003) ^ this.lastObjectPopup.hashCode()
    }

    @NonNull
    fun lastObjectPopup(): Optional<UnreadNotificationInfo.ObjectPopupMessage> {
        return this.lastObjectPopup
    }

    fun objectPopupsCount(): Int {
        return this.objectPopupsCount
    }

    fun toString(): String {
        return "ObjectPopupNotification{freshObjectPopupsCount=" + this.freshObjectPopupsCount + ", " + "objectPopupsCount=" + this.objectPopupsCount + ", " + "lastObjectPopup=" + this.lastObjectPopup + "}"
    }
}
