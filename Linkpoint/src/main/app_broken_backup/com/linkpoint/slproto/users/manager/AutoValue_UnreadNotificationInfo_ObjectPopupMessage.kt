package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.users.manager.UnreadNotificationInfo

class AutoValue_UnreadNotificationInfo_ObjectPopupMessage : UnreadNotificationInfo.ObjectPopupMessage {
    private String message
    private String objectName

    AutoValue_UnreadNotificationInfo_ObjectPopupMessage(String str, String str2) {
        if (str == null) {
            throw NullPointerException("Null objectName")
        }
        this.objectName = str
        if (str2 == null) {
            throw NullPointerException("Null message")
        }
        this.message = str2
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof UnreadNotificationInfo.ObjectPopupMessage)) {
            return false
        }
        UnreadNotificationInfo.ObjectPopupMessage objectPopupMessage = (UnreadNotificationInfo.ObjectPopupMessage) obj
        if (this.objectName.equals(objectPopupMessage.objectName())) {
            return this.message.equals(objectPopupMessage.message())
        }
        return false
    }

    fun hashCode(): Int {
        return ((this.objectName.hashCode() ^ 1000003) * 1000003) ^ this.message.hashCode()
    }

    fun message(): String {
        return this.message
    }

    fun objectName(): String {
        return this.objectName
    }

    fun toString(): String {
        return "ObjectPopupMessage{objectName=" + this.objectName + ", " + "message=" + this.message + "}"
    }
}
