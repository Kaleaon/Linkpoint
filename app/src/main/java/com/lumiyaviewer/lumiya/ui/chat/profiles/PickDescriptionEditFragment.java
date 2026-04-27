package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.R;
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

public class PickDescriptionEditFragment extends TextFieldEditFragment {
    private static final String AVATAR_PICK_KEY = "avatarPickKey";
    private final SubscriptionData<AvatarPickKey, PickInfoReply> pickInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new $Lambda$Y7Ne2VWglUcvjFUgJydWWKVgIXM(this));

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

    /* access modifiers changed from: private */
    /* renamed from: onPickInfoReply */
    public void m506com_lumiyaviewer_lumiya_ui_chat_profiles_PickDescriptionEditFragmentmthref0(PickInfoReply pickInfoReply) {
        if (pickInfoReply != null) {
            setOriginalText(SLMessage.stringFromVariableUTF(pickInfoReply.Data_Field.Desc));
        }
    }

    /* access modifiers changed from: protected */
    public String getFieldHint(Context context) {
        return getString(R.string.pick_description_edit_hint);
    }

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
        AvatarPickKey pickKey = getPickKey();
        if (this.userManager == null || !(chatterID instanceof ChatterID.ChatterIDUser) || pickKey == null) {
            this.pickInfo.unsubscribe();
        } else {
            this.pickInfo.subscribe(this.userManager.getAvatarPickInfos().getPool(), pickKey);
        }
    }

    /* access modifiers changed from: protected */
    public void saveEditedText(SLAgentCircuit sLAgentCircuit, ChatterID chatterID, String str) {
        AvatarPickKey pickKey = getPickKey();
        PickInfoReply data = this.pickInfo.getData();
        if (sLAgentCircuit != null && pickKey != null && data != null) {
            sLAgentCircuit.getModules().userProfiles.UpdatePickInfo(pickKey.pickID, data.Data_Field.CreatorID, data.Data_Field.ParcelID, SLMessage.stringFromVariableOEM(data.Data_Field.Name), str, data.Data_Field.SnapshotID, data.Data_Field.PosGlobal, data.Data_Field.SortOrder, data.Data_Field.Enabled);
        }
    }
}
