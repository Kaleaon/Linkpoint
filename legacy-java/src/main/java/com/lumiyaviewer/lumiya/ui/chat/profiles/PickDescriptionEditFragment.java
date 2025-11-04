package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class PickDescriptionEditFragment extends TextFieldEditFragment {
    private static final String AVATAR_PICK_KEY = "avatarPickKey";
    private final SubscriptionData<AvatarPickKey, PickInfoReply> pickInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$Y7Ne2VWglUcvjFUgJydWWKVgIXM
        private final /* synthetic */ void $m$0(Object obj) {
            ((PickDescriptionEditFragment) this).m516x9070e529((PickInfoReply) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });

    private AvatarPickKey getPickKey() {
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey(AVATAR_PICK_KEY)) {
            return null;
        }
        return (AvatarPickKey) arguments.getParcelable(AVATAR_PICK_KEY);
    }

    public static Bundle makeSelection(ChatterID chatterID, AvatarPickKey avatarPickKey) {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID);
        makeSelection.putParcelable(AVATAR_PICK_KEY, avatarPickKey);
        return makeSelection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onPickInfoReply, reason: merged with bridge method [inline-methods] */
    public void m516x9070e529(PickInfoReply pickInfoReply) {
        if (pickInfoReply != null) {
            setOriginalText(SLMessage.stringFromVariableUTF(pickInfoReply.Data_Field.Desc));
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment
    protected String getFieldHint(Context context) {
        return getString(R.string.pick_description_edit_hint);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        AvatarPickKey pickKey = getPickKey();
        if (this.userManager == null || !(chatterID instanceof ChatterID.ChatterIDUser) || pickKey == null) {
            this.pickInfo.unsubscribe();
        } else {
            this.pickInfo.subscribe(this.userManager.getAvatarPickInfos().getPool(), pickKey);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment
    protected void saveEditedText(SLAgentCircuit sLAgentCircuit, ChatterID chatterID, String str) {
        AvatarPickKey pickKey = getPickKey();
        PickInfoReply data = this.pickInfo.getData();
        if (sLAgentCircuit == null || pickKey == null || data == null) {
            return;
        }
        sLAgentCircuit.getModules().userProfiles.UpdatePickInfo(pickKey.pickID, data.Data_Field.CreatorID, data.Data_Field.ParcelID, SLMessage.stringFromVariableOEM(data.Data_Field.Name), str, data.Data_Field.SnapshotID, data.Data_Field.PosGlobal, data.Data_Field.SortOrder, data.Data_Field.Enabled);
    }
}
