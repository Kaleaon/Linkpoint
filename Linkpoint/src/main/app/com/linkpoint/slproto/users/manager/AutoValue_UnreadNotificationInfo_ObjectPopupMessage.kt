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

    Boolean equals(Any obj) {
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

    Int hashCode() {
        return ((this.objectName.hashCode() ^ 1000003) * 1000003) ^ this.message.hashCode()
    }

    String message() {
        return this.message
    }

    String objectName() {
        return this.objectName
    }

    String toString() {
        return "ObjectPopupMessage{objectName=" + this.objectName + ", " + "message=" + this.message + "}"
    }
}
