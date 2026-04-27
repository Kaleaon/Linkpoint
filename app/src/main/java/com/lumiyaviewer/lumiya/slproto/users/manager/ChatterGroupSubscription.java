package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
class ChatterGroupSubscription extends ChatterSubscription {
    @Nonnull
    private final Subscription<UUID, GroupProfileReply> groupProfileSubscription;

    ChatterGroupSubscription(@Nonnull SortedChatterList sortedChatterList, ChatterID.ChatterIDGroup chatterIDGroup, @Nonnull UserManager userManager) {
        super(sortedChatterList, chatterIDGroup, userManager);
        this.groupProfileSubscription = userManager.getCachedGroupProfiles().getPool().subscribe(chatterIDGroup.getChatterUUID(), new $Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY(this));
    }

    /* access modifiers changed from: private */
    /* renamed from: onGroupProfile */
    public void m289com_lumiyaviewer_lumiya_slproto_users_manager_ChatterGroupSubscriptionmthref0(GroupProfileReply groupProfileReply) {
        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(groupProfileReply.GroupData_Field.Name);
        if (!Objects.equal(stringFromVariableOEM, this.displayData.displayName)) {
            setChatterDisplayData(this.displayData.withDisplayName(stringFromVariableOEM));
        }
    }

    public void unsubscribe() {
        this.groupProfileSubscription.unsubscribe();
        super.unsubscribe();
    }
}
