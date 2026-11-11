package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
/* JADX INFO: Access modifiers changed from: package-private */
@NotThreadSafe
/* loaded from: classes.dex */
public class ChatterGroupSubscription extends ChatterSubscription {
    @Nonnull
    private final Subscription<UUID, GroupProfileReply> groupProfileSubscription;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChatterGroupSubscription(@Nonnull SortedChatterList sortedChatterList, ChatterID.ChatterIDGroup chatterIDGroup, @Nonnull UserManager userManager) {
        super(sortedChatterList, chatterIDGroup, userManager);
        this.groupProfileSubscription = userManager.getCachedGroupProfiles().getPool().subscribe(chatterIDGroup.getChatterUUID(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY
            private final /* synthetic */ void $m$0(Object obj) {
                ((ChatterGroupSubscription) this).m300xb0ce45f9((GroupProfileReply) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onGroupProfile */
    public void m300xb0ce45f9(GroupProfileReply groupProfileReply) {
        String stringFromVariableOEM = SLMessage.stringFromVariableOEM(groupProfileReply.GroupData_Field.Name);
        if (Objects.equal(stringFromVariableOEM, this.displayData.displayName)) {
            return;
        }
        setChatterDisplayData(this.displayData.withDisplayName(stringFromVariableOEM));
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.ChatterSubscription
    public void unsubscribe() {
        this.groupProfileSubscription.unsubscribe();
        super.unsubscribe();
    }
}
