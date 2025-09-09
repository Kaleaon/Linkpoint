package com.lumiyaviewer.lumiya.slproto.users.manager;

final class AutoValue_MyAvatarState extends MyAvatarState {
    private final boolean hasHUDs;
    private final boolean isFlying;
    private final boolean isSitting;
    private final int sittingOn;

    AutoValue_MyAvatarState(boolean z, int i, boolean z2, boolean z3) {
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

    public boolean hasHUDs() {
        return this.hasHUDs;
    }

    public int hashCode() {
        int i = 1231;
        int i2 = ((this.isFlying ? 1231 : 1237) ^ (((((this.isSitting ? 1231 : 1237) ^ 1000003) * 1000003) ^ this.sittingOn) * 1000003)) * 1000003;
        if (!this.hasHUDs) {
            i = 1237;
        }
        return i2 ^ i;
    }

    public boolean isFlying() {
        return this.isFlying;
    }

    public boolean isSitting() {
        return this.isSitting;
    }

    public int sittingOn() {
        return this.sittingOn;
    }

    public String toString() {
        return "MyAvatarState{isSitting=" + this.isSitting + ", " + "sittingOn=" + this.sittingOn + ", " + "isFlying=" + this.isFlying + ", " + "hasHUDs=" + this.hasHUDs + "}";
    }
}
