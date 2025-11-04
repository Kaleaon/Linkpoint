// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.modules.SLUserProfiles;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import android.content.Context;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;

public class UserAboutTextEditFragment extends ProfileTextFieldEditFragment
{
    private static final String IS_FIRST_LIFE_KEY = "isFirstLife";
    private AvatarPropertiesReply avatarProperties;
    
    private boolean isFirstLife() {
        final Bundle arguments = this.getArguments();
        return arguments != null && arguments.getBoolean("isFirstLife");
    }
    
    public static Bundle makeSelection(final ChatterID chatterID, final boolean b) {
        final Bundle selection = ChatterFragment.makeSelection(chatterID);
        selection.putBoolean("isFirstLife", b);
        return selection;
    }
    
    @Override
    protected String decorateFragmentTitle(final String s) {
        return this.getString(2131296517, s);
    }
    
    @Override
    protected String getFieldHint(final Context context) {
        return this.getString(2131296516);
    }
    
    @Override
    protected void onAvatarProperties(final AvatarPropertiesReply avatarProperties) {
        this.avatarProperties = avatarProperties;
        String originalText;
        if (this.isFirstLife()) {
            originalText = SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.FLAboutText);
        }
        else {
            originalText = SLMessage.stringFromVariableUTF(avatarProperties.PropertiesData_Field.AboutText);
        }
        this.setOriginalText(originalText);
    }
    
    @Override
    protected void saveEditedText(final SLAgentCircuit slAgentCircuit, final ChatterID chatterID, String s) {
        boolean b = true;
        if (this.avatarProperties != null) {
            String stringFromVariableUTF = SLMessage.stringFromVariableUTF(this.avatarProperties.PropertiesData_Field.AboutText);
            final String stringFromVariableOEM = SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.FLAboutText);
            if (!this.isFirstLife()) {
                stringFromVariableUTF = s;
                s = stringFromVariableOEM;
            }
            final SLUserProfiles userProfiles = slAgentCircuit.getModules().userProfiles;
            final UUID imageID = this.avatarProperties.PropertiesData_Field.ImageID;
            final UUID flImageID = this.avatarProperties.PropertiesData_Field.FLImageID;
            final boolean b2 = (this.avatarProperties.PropertiesData_Field.Flags & 0x1) != 0x0;
            if ((this.avatarProperties.PropertiesData_Field.Flags & 0x2) == 0x0) {
                b = false;
            }
            userProfiles.UpdateAvatarProperties(imageID, flImageID, stringFromVariableUTF, s, b2, b, SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.ProfileURL));
        }
    }
}
