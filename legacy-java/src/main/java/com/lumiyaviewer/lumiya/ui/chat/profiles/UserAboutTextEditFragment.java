package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;

/* loaded from: classes.dex */
public class UserAboutTextEditFragment extends ProfileTextFieldEditFragment {
    private static final String IS_FIRST_LIFE_KEY = "isFirstLife";
    private AvatarPropertiesReply avatarProperties;

    private boolean isFirstLife() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getBoolean(IS_FIRST_LIFE_KEY);
        }
        return false;
    }

    public static Bundle makeSelection(ChatterID chatterID, boolean z) {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID);
        makeSelection.putBoolean(IS_FIRST_LIFE_KEY, z);
        return makeSelection;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected String decorateFragmentTitle(String str) {
        return getString(R.string.edit_about_title, str);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment
    protected String getFieldHint(Context context) {
        return getString(R.string.edit_about_hint);
    }

    @Override // com.lumiyaviewer.lumiya.ui.chat.profiles.ProfileTextFieldEditFragment
    /* renamed from: onAvatarProperties */
    protected void m517x7aa22308(AvatarPropertiesReply avatarPropertiesReply) {
        this.avatarProperties = avatarPropertiesReply;
        setOriginalText(isFirstLife() ? SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.FLAboutText) : SLMessage.stringFromVariableUTF(avatarPropertiesReply.PropertiesData_Field.AboutText));
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment
    protected void saveEditedText(SLAgentCircuit sLAgentCircuit, ChatterID chatterID, String str) {
        if (this.avatarProperties != null) {
            String stringFromVariableUTF = SLMessage.stringFromVariableUTF(this.avatarProperties.PropertiesData_Field.AboutText);
            String stringFromVariableOEM = SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.FLAboutText);
            if (isFirstLife()) {
                stringFromVariableOEM = str;
            } else {
                stringFromVariableUTF = str;
            }
            sLAgentCircuit.getModules().userProfiles.UpdateAvatarProperties(this.avatarProperties.PropertiesData_Field.ImageID, this.avatarProperties.PropertiesData_Field.FLImageID, stringFromVariableUTF, stringFromVariableOEM, (this.avatarProperties.PropertiesData_Field.Flags & 1) != 0, (this.avatarProperties.PropertiesData_Field.Flags & 2) != 0, SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.ProfileURL));
        }
    }
}
