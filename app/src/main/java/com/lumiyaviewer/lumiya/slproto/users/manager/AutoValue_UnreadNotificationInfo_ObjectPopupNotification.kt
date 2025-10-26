package com.lumiyaviewer.lumiya.slproto.users.manager

import com.google.common.base.Optional
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo
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

    Boolean equals(Any obj) {
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

    Int freshObjectPopupsCount() {
        return this.freshObjectPopupsCount
    }

    Int hashCode() {
        return ((((this.freshObjectPopupsCount ^ 1000003) * 1000003) ^ this.objectPopupsCount) * 1000003) ^ this.lastObjectPopup.hashCode()
    }

    @NonNull
    Optional<UnreadNotificationInfo.ObjectPopupMessage> lastObjectPopup() {
        return this.lastObjectPopup
    }

    Int objectPopupsCount() {
        return this.objectPopupsCount
    }

    String toString() {
        return "ObjectPopupNotification{freshObjectPopupsCount=" + this.freshObjectPopupsCount + ", " + "objectPopupsCount=" + this.objectPopupsCount + ", " + "lastObjectPopup=" + this.lastObjectPopup + "}"
    }
}
