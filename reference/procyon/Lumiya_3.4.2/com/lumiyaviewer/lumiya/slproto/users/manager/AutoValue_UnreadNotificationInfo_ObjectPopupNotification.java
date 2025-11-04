// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import javax.annotation.Nonnull;
import com.google.common.base.Optional;

final class AutoValue_UnreadNotificationInfo_ObjectPopupNotification extends ObjectPopupNotification
{
    private final int freshObjectPopupsCount;
    private final Optional<ObjectPopupMessage> lastObjectPopup;
    private final int objectPopupsCount;
    
    AutoValue_UnreadNotificationInfo_ObjectPopupNotification(final int freshObjectPopupsCount, final int objectPopupsCount, final Optional<ObjectPopupMessage> lastObjectPopup) {
        this.freshObjectPopupsCount = freshObjectPopupsCount;
        this.objectPopupsCount = objectPopupsCount;
        if (lastObjectPopup == null) {
            throw new NullPointerException("Null lastObjectPopup");
        }
        this.lastObjectPopup = lastObjectPopup;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (o == this) {
            return true;
        }
        if (o instanceof ObjectPopupNotification) {
            final ObjectPopupNotification objectPopupNotification = (ObjectPopupNotification)o;
            boolean equals = b;
            if (this.freshObjectPopupsCount == objectPopupNotification.freshObjectPopupsCount()) {
                equals = b;
                if (this.objectPopupsCount == objectPopupNotification.objectPopupsCount()) {
                    equals = this.lastObjectPopup.equals(objectPopupNotification.lastObjectPopup());
                }
            }
            return equals;
        }
        return false;
    }
    
    @Override
    public int freshObjectPopupsCount() {
        return this.freshObjectPopupsCount;
    }
    
    @Override
    public int hashCode() {
        return ((this.freshObjectPopupsCount ^ 0xF4243) * 1000003 ^ this.objectPopupsCount) * 1000003 ^ this.lastObjectPopup.hashCode();
    }
    
    @Nonnull
    @Override
    public Optional<ObjectPopupMessage> lastObjectPopup() {
        return this.lastObjectPopup;
    }
    
    @Override
    public int objectPopupsCount() {
        return this.objectPopupsCount;
    }
    
    @Override
    public String toString() {
        return "ObjectPopupNotification{freshObjectPopupsCount=" + this.freshObjectPopupsCount + ", " + "objectPopupsCount=" + this.objectPopupsCount + ", " + "lastObjectPopup=" + this.lastObjectPopup + "}";
    }
}
