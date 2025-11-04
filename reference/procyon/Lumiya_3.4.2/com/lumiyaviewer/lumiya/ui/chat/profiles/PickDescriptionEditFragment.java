// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.react.Subscribable;
import javax.annotation.Nullable;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import android.os.Parcelable;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment;

public class PickDescriptionEditFragment extends TextFieldEditFragment
{
    private static final String AVATAR_PICK_KEY = "avatarPickKey";
    private final SubscriptionData<AvatarPickKey, PickInfoReply> pickInfo;
    
    public PickDescriptionEditFragment() {
        this.pickInfo = new SubscriptionData<AvatarPickKey, PickInfoReply>(UIThreadExecutor.getInstance(), new _$Lambda$Y7Ne2VWglUcvjFUgJydWWKVgIXM(this));
    }
    
    private AvatarPickKey getPickKey() {
        final Bundle arguments = this.getArguments();
        if (arguments != null && arguments.containsKey("avatarPickKey")) {
            return (AvatarPickKey)arguments.getParcelable("avatarPickKey");
        }
        return null;
    }
    
    public static Bundle makeSelection(final ChatterID chatterID, final AvatarPickKey avatarPickKey) {
        final Bundle selection = ChatterFragment.makeSelection(chatterID);
        selection.putParcelable("avatarPickKey", (Parcelable)avatarPickKey);
        return selection;
    }
    
    private void onPickInfoReply(final PickInfoReply pickInfoReply) {
        if (pickInfoReply != null) {
            this.setOriginalText(SLMessage.stringFromVariableUTF(pickInfoReply.Data_Field.Desc));
        }
    }
    
    @Override
    protected String getFieldHint(final Context context) {
        return this.getString(2131296883);
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        final AvatarPickKey pickKey = this.getPickKey();
        if (this.userManager != null && chatterID instanceof ChatterID.ChatterIDUser && pickKey != null) {
            this.pickInfo.subscribe(this.userManager.getAvatarPickInfos().getPool(), pickKey);
        }
        else {
            this.pickInfo.unsubscribe();
        }
    }
    
    @Override
    protected void saveEditedText(final SLAgentCircuit slAgentCircuit, final ChatterID chatterID, final String s) {
        final AvatarPickKey pickKey = this.getPickKey();
        final PickInfoReply pickInfoReply = this.pickInfo.getData();
        if (slAgentCircuit != null && pickKey != null && pickInfoReply != null) {
            slAgentCircuit.getModules().userProfiles.UpdatePickInfo(pickKey.pickID, pickInfoReply.Data_Field.CreatorID, pickInfoReply.Data_Field.ParcelID, SLMessage.stringFromVariableOEM(pickInfoReply.Data_Field.Name), s, pickInfoReply.Data_Field.SnapshotID, pickInfoReply.Data_Field.PosGlobal, pickInfoReply.Data_Field.SortOrder, pickInfoReply.Data_Field.Enabled);
        }
    }
}
