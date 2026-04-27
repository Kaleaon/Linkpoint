package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Optional;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
final class AutoValue_UnreadNotificationInfo_ObjectPopupNotification extends UnreadNotificationInfo.ObjectPopupNotification {
    private final int freshObjectPopupsCount;
    private final Optional<UnreadNotificationInfo.ObjectPopupMessage> lastObjectPopup;
    private final int objectPopupsCount;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_UnreadNotificationInfo_ObjectPopupNotification(int i, int i2, Optional<UnreadNotificationInfo.ObjectPopupMessage> optional) {
        this.freshObjectPopupsCount = i;
        this.objectPopupsCount = i2;
        if (optional == null) {
            throw new NullPointerException("Null lastObjectPopup");
        }
        this.lastObjectPopup = optional;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UnreadNotificationInfo.ObjectPopupNotification)) {
            return false;
        }
        UnreadNotificationInfo.ObjectPopupNotification objectPopupNotification = (UnreadNotificationInfo.ObjectPopupNotification) obj;
        if (this.freshObjectPopupsCount == objectPopupNotification.freshObjectPopupsCount() && this.objectPopupsCount == objectPopupNotification.objectPopupsCount()) {
            return this.lastObjectPopup.equals(objectPopupNotification.lastObjectPopup());
        }
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.ObjectPopupNotification
    public int freshObjectPopupsCount() {
        return this.freshObjectPopupsCount;
    }

    public int hashCode() {
        return ((((this.freshObjectPopupsCount ^ 1000003) * 1000003) ^ this.objectPopupsCount) * 1000003) ^ this.lastObjectPopup.hashCode();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.ObjectPopupNotification
    @Nonnull
    public Optional<UnreadNotificationInfo.ObjectPopupMessage> lastObjectPopup() {
        return this.lastObjectPopup;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.ObjectPopupNotification
    public int objectPopupsCount() {
        return this.objectPopupsCount;
    }

    public String toString() {
        return "ObjectPopupNotification{freshObjectPopupsCount=" + this.freshObjectPopupsCount + ", objectPopupsCount=" + this.objectPopupsCount + ", lastObjectPopup=" + this.lastObjectPopup + "}";
    }
}
