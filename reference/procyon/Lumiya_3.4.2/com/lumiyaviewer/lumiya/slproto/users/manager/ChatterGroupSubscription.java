// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.Subscription;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
class ChatterGroupSubscription extends ChatterSubscription
{
    @Nonnull
    private final Subscription<UUID, GroupProfileReply> groupProfileSubscription;
    
    ChatterGroupSubscription(@Nonnull final SortedChatterList list, final ChatterID.ChatterIDGroup chatterIDGroup, @Nonnull final UserManager userManager) {
        super(list, chatterIDGroup, userManager);
        this.groupProfileSubscription = userManager.getCachedGroupProfiles().getPool().subscribe(chatterIDGroup.getChatterUUID(), new _$Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY(this));
    }
    
    private void onGroupProfile(final GroupProfileReply groupProfileReply) {
        final String stringFromVariableOEM = SLMessage.stringFromVariableOEM(groupProfileReply.GroupData_Field.Name);
        if (!Objects.equal(stringFromVariableOEM, this.displayData.displayName)) {
            this.setChatterDisplayData(this.displayData.withDisplayName(stringFromVariableOEM));
        }
    }
    
    @Override
    public void unsubscribe() {
        this.groupProfileSubscription.unsubscribe();
        super.unsubscribe();
    }
}
