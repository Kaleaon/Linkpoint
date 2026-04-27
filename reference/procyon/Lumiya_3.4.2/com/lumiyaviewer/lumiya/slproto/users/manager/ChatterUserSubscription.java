// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.dao.UserName;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.Subscription;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
class ChatterUserSubscription extends ChatterSubscription
{
    @Nonnull
    private final Subscription<UUID, Float> distanceSubscription;
    @Nonnull
    private final Subscription<UUID, UserName> nameSubscription;
    @Nonnull
    private final Subscription<UUID, Boolean> onlineStatusSubscription;
    
    ChatterUserSubscription(@Nonnull final SortedChatterList list, @Nonnull final ChatterID.ChatterIDUser chatterIDUser, @Nonnull final UserManager userManager) {
        super(list, chatterIDUser, userManager);
        this.nameSubscription = userManager.getUserNames().subscribe(chatterIDUser.getChatterUUID(), new _$Lambda$o86h7H3WuAxnvPtFprunJr0Jq8o(this));
        this.onlineStatusSubscription = userManager.getChatterList().getFriendManager().getOnlineStatus().subscribe(chatterIDUser.getChatterUUID(), new _$Lambda$o86h7H3WuAxnvPtFprunJr0Jq8o$1(this));
        this.distanceSubscription = userManager.getChatterList().getDistanceToUser().subscribe(chatterIDUser.getChatterUUID(), new _$Lambda$o86h7H3WuAxnvPtFprunJr0Jq8o$2(this));
    }
    
    private void onDistance(final Float n) {
        if (n != null) {
            final float floatValue = n;
            if (Float.compare(floatValue, this.displayData.distanceToUser) != 0) {
                this.setChatterDisplayData(this.displayData.withDistanceToUser(floatValue));
            }
        }
    }
    
    private void onOnlineStatus(final Boolean b) {
        if (b != null && this.displayData.isOnline != b) {
            this.setChatterDisplayData(this.displayData.withOnlineStatus(b));
        }
    }
    
    private void onUserName(final UserName userName) {
        String s;
        if (GlobalOptions.getInstance().isLegacyUserNames()) {
            s = userName.getUserName();
        }
        else {
            s = userName.getDisplayName();
        }
        if (!Objects.equal(s, this.displayData.displayName)) {
            this.setChatterDisplayData(this.displayData.withDisplayName(s));
        }
    }
    
    @Override
    public void unsubscribe() {
        this.nameSubscription.unsubscribe();
        this.onlineStatusSubscription.unsubscribe();
        this.distanceSubscription.unsubscribe();
        super.unsubscribe();
    }
}
