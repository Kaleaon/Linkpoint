package com.linkpoint.slproto.users.manager

import com.google.common.base.Optional
import com.linkpoint.slproto.users.manager.UnreadNotificationInfo
import javax.annotation.Nonnull

final class AutoValue_UnreadNotificationInfo_ObjectPopupNotification : UnreadNotificationInfo().ObjectPopupNotification {
    private val Int freshObjectPopupsCount
    private val Optional<UnreadNotificationInfo.ObjectPopupMessage> lastObjectPopup
    private val Int objectPopupsCount

    AutoValue_UnreadNotificationInfo_ObjectPopupNotification(Int i, Int i2, Optional<UnreadNotificationInfo.ObjectPopupMessage> optional) {
        this.freshObjectPopupsCount = i
        this.objectPopupsCount = i2
        if (optional == null) {
            throw NullPointerException("Null lastObjectPopup")
        }
        this.lastObjectPopup = optional
    }

     public override fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof UnreadNotificationInfo.ObjectPopupNotification)) {
            return false
        }
        UnreadNotificationInfo.ObjectPopupNotification objectPopupNotification = (UnreadNotificationInfo.ObjectPopupNotification) obj
        if (this.freshObjectPopupsCount == objectPopupNotification.freshObjectPopupsCount() && this.objectPopupsCount == objectPopupNotification.objectPopupsCount()) {
            return this.lastObjectPopup.equals(objectPopupNotification.lastObjectPopup())
        }
        return false
    }

     public fun freshObjectPopupsCount(): Int {
        return this.freshObjectPopupsCount
    }

     public override fun hashCode(): Int {
        return ((((this.freshObjectPopupsCount ^ 1000003) * 1000003) ^ this.objectPopupsCount) * 1000003) ^ this.lastObjectPopup.hashCode()
    }

    public Optional<UnreadNotificationInfo.ObjectPopupMessage> lastObjectPopup() {
        return this.lastObjectPopup
    }

     public fun objectPopupsCount(): Int {
        return this.objectPopupsCount
    }

     public override fun toString(): String {
        return "ObjectPopupNotification{freshObjectPopupsCount=" + this.freshObjectPopupsCount + ", " + "objectPopupsCount=" + this.objectPopupsCount + ", " + "lastObjectPopup=" + this.lastObjectPopup + "}"
    }
}
