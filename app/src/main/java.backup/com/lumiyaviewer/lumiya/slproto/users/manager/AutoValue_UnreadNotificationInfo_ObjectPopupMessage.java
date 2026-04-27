package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;

final class AutoValue_UnreadNotificationInfo_ObjectPopupMessage extends UnreadNotificationInfo.ObjectPopupMessage {
    private final String message;
    private final String objectName;

    AutoValue_UnreadNotificationInfo_ObjectPopupMessage(String str, String str2) {
        if (str == null) {
            throw new NullPointerException("Null objectName");
        }
        this.objectName = str;
        if (str2 == null) {
            throw new NullPointerException("Null message");
        }
        this.message = str2;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UnreadNotificationInfo.ObjectPopupMessage)) {
            return false;
        }
        UnreadNotificationInfo.ObjectPopupMessage objectPopupMessage = (UnreadNotificationInfo.ObjectPopupMessage) obj;
        if (this.objectName.equals(objectPopupMessage.objectName())) {
            return this.message.equals(objectPopupMessage.message());
        }
        return false;
    }

    public int hashCode() {
        return ((this.objectName.hashCode() ^ 1000003) * 1000003) ^ this.message.hashCode();
    }

    public String message() {
        return this.message;
    }

    public String objectName() {
        return this.objectName;
    }

    public String toString() {
        return "ObjectPopupMessage{objectName=" + this.objectName + ", " + "message=" + this.message + "}";
    }
}
