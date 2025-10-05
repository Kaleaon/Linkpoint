package com.linkpoint.slproto.users.manager

import com.google.common.base.Objects
import com.linkpoint.react.Subscription
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.GroupProfileReply
import com.linkpoint.slproto.users.ChatterID
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class ChatterGroupSubscription : ChatterSubscription() {
    private val Subscription<UUID, GroupProfileReply> groupProfileSubscription

    ChatterGroupSubscription(SortedChatterList sortedChatterList, ChatterID.ChatterIDGroup chatterIDGroup, UserManager userManager) {
        super(sortedChatterList, chatterIDGroup, userManager)
        this.groupProfileSubscription = userManager.getCachedGroupProfiles().getPool().subscribe(chatterIDGroup.getChatterUUID(), $Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY(this))
    }

    /* access modifiers changed from: private */
    /* renamed from: onGroupProfile */
    public Unit m289com_lumiyaviewer_lumiya_slproto_users_manager_ChatterGroupSubscriptionmthref0(GroupProfileReply groupProfileReply) {
        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(groupProfileReply.GroupData_Field.Name)
        if (!Objects.equal(stringFromVariableOEM, this.displayData.displayName)) {
            setChatterDisplayData(this.displayData.withDisplayName(stringFromVariableOEM))
        }
    }

    public Unit unsubscribe() {
        this.groupProfileSubscription.unsubscribe()
        super.unsubscribe()
    }
}
