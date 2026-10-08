package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
/* JADX INFO: Access modifiers changed from: package-private */
@NotThreadSafe
/* loaded from: classes.dex */
public class ChatterUserSubscription extends ChatterSubscription {
    @Nonnull
    private final Subscription<UUID, Float> distanceSubscription;
    @Nonnull
    private final Subscription<UUID, UserName> nameSubscription;
    @Nonnull
    private final Subscription<UUID, Boolean> onlineStatusSubscription;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChatterUserSubscription(@Nonnull SortedChatterList sortedChatterList, @Nonnull ChatterID.ChatterIDUser chatterIDUser, @Nonnull UserManager userManager) {
        super(sortedChatterList, chatterIDUser, userManager);
        this.nameSubscription = userManager.getUserNames().subscribe(chatterIDUser.getChatterUUID(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$o86h7H3WuAxnvPtFprunJr0Jq8o
            private final /* synthetic */ void $m$0(Object obj) {
                ((ChatterUserSubscription) this).m310x593f85a7((UserName) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
        this.onlineStatusSubscription = userManager.getChatterList().getFriendManager().getOnlineStatus().subscribe(chatterIDUser.getChatterUUID(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$o86h7H3WuAxnvPtFprunJr0Jq8o.1
            private final /* synthetic */ void $m$0(Object obj) {
                ((ChatterUserSubscription) this).m311x593f85a8((Boolean) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
        this.distanceSubscription = userManager.getChatterList().getDistanceToUser().subscribe(chatterIDUser.getChatterUUID(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$o86h7H3WuAxnvPtFprunJr0Jq8o.2
            private final /* synthetic */ void $m$0(Object obj) {
                ((ChatterUserSubscription) this).m312x593f85a9((Float) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onDistance */
    public void m312x593f85a9(Float f) {
        if (f != null) {
            float floatValue = f.floatValue();
            if (Float.compare(floatValue, this.displayData.distanceToUser) != 0) {
                setChatterDisplayData(this.displayData.withDistanceToUser(floatValue));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onOnlineStatus */
    public void m311x593f85a8(Boolean bool) {
        if (bool == null || this.displayData.isOnline == bool.booleanValue()) {
            return;
        }
        setChatterDisplayData(this.displayData.withOnlineStatus(bool.booleanValue()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onUserName */
    public void m310x593f85a7(UserName userName) {
        String userName2 = GlobalOptions.getInstance().isLegacyUserNames() ? userName.getUserName() : userName.getDisplayName();
        if (Objects.equal(userName2, this.displayData.displayName)) {
            return;
        }
        setChatterDisplayData(this.displayData.withDisplayName(userName2));
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.ChatterSubscription
    public void unsubscribe() {
        this.nameSubscription.unsubscribe();
        this.onlineStatusSubscription.unsubscribe();
        this.distanceSubscription.unsubscribe();
        super.unsubscribe();
    }
}
