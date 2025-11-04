package com.lumiyaviewer.lumiya.slproto.users.manager;

/* loaded from: classes.dex */
final class AutoValue_MyAvatarState extends MyAvatarState {
    private final boolean hasHUDs;
    private final boolean isFlying;
    private final boolean isSitting;
    private final int sittingOn;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_MyAvatarState(boolean z, int i, boolean z2, boolean z3) {
        this.isSitting = z;
        this.sittingOn = i;
        this.isFlying = z2;
        this.hasHUDs = z3;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MyAvatarState)) {
            return false;
        }
        MyAvatarState myAvatarState = (MyAvatarState) obj;
        if (this.isSitting == myAvatarState.isSitting() && this.sittingOn == myAvatarState.sittingOn() && this.isFlying == myAvatarState.isFlying()) {
            return this.hasHUDs == myAvatarState.hasHUDs();
        }
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState
    public boolean hasHUDs() {
        return this.hasHUDs;
    }

    public int hashCode() {
        return (((this.isFlying ? 1231 : 1237) ^ (((((this.isSitting ? 1231 : 1237) ^ 1000003) * 1000003) ^ this.sittingOn) * 1000003)) * 1000003) ^ (this.hasHUDs ? 1231 : 1237);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState
    public boolean isFlying() {
        return this.isFlying;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState
    public boolean isSitting() {
        return this.isSitting;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState
    public int sittingOn() {
        return this.sittingOn;
    }

    public String toString() {
        return "MyAvatarState{isSitting=" + this.isSitting + ", sittingOn=" + this.sittingOn + ", isFlying=" + this.isFlying + ", hasHUDs=" + this.hasHUDs + "}";
    }
}
