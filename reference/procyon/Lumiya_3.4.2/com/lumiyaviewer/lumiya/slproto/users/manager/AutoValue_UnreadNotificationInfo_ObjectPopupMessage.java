// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

final class AutoValue_UnreadNotificationInfo_ObjectPopupMessage extends ObjectPopupMessage
{
    private final String message;
    private final String objectName;
    
    AutoValue_UnreadNotificationInfo_ObjectPopupMessage(final String objectName, final String message) {
        if (objectName == null) {
            throw new NullPointerException("Null objectName");
        }
        this.objectName = objectName;
        if (message == null) {
            throw new NullPointerException("Null message");
        }
        this.message = message;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = false;
        if (o == this) {
            return true;
        }
        if (o instanceof ObjectPopupMessage) {
            final ObjectPopupMessage objectPopupMessage = (ObjectPopupMessage)o;
            if (this.objectName.equals(objectPopupMessage.objectName())) {
                equals = this.message.equals(objectPopupMessage.message());
            }
            return equals;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (this.objectName.hashCode() ^ 0xF4243) * 1000003 ^ this.message.hashCode();
    }
    
    @Override
    public String message() {
        return this.message;
    }
    
    @Override
    public String objectName() {
        return this.objectName;
    }
    
    @Override
    public String toString() {
        return "ObjectPopupMessage{objectName=" + this.objectName + ", " + "message=" + this.message + "}";
    }
}
