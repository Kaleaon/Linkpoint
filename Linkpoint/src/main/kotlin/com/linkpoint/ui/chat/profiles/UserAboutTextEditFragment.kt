package com.linkpoint.ui.chat.profiles

import android.content.Context
import android.os.Bundle
import com.linkpoint.R
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.AvatarPropertiesReply
import com.linkpoint.slproto.modules.SLUserProfiles
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.ui.common.ChatterFragment
import java.util.UUID

class UserAboutTextEditFragment : ProfileTextFieldEditFragment() {
    private const val String IS_FIRST_LIFE_KEY = "isFirstLife"
    private AvatarPropertiesReply avatarProperties

    private Boolean isFirstLife() {
        Bundle arguments = getArguments()
        if (arguments != null) {
            return arguments.getBoolean(IS_FIRST_LIFE_KEY)
        }
        return false
    }

    @JvmStatic
    Bundle makeSelection(ChatterID chatterID, Boolean z) {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID)
        makeSelection.putBoolean(IS_FIRST_LIFE_KEY, z)
        return makeSelection
    }

    /* access modifiers changed from: protected */
    public String decorateFragmentTitle(String str) {
        return getString(R.string.edit_about_title, str)
    }

    /* access modifiers changed from: protected */
    public String getFieldHint(Context context) {
        return getString(R.string.edit_about_hint)
    }

    /* access modifiers changed from: protected */
    public Unit onAvatarProperties(AvatarPropertiesReply avatarPropertiesReply) {
        this.avatarProperties = avatarPropertiesReply
        setOriginalText(isFirstLife() ? SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.FLAboutText) : SLMessage.stringFromVariableUTF(avatarPropertiesReply.PropertiesData_Field.AboutText))
    }

    /* access modifiers changed from: protected */
    public Unit saveEditedText(SLAgentCircuit sLAgentCircuit, ChatterID chatterID, String str) {
        Boolean z = true
        if (this.avatarProperties != null) {
            String stringFromVariableUTF = SLMessage.stringFromVariableUTF(this.avatarProperties.PropertiesData_Field.AboutText)
            String stringFromVariableOEM = SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.FLAboutText)
            if (isFirstLife()) {
                stringFromVariableOEM = str
            } else {
                stringFromVariableUTF = str
            }
            SLUserProfiles sLUserProfiles = sLAgentCircuit.getModules().userProfiles
            UUID uuid = this.avatarProperties.PropertiesData_Field.ImageID
            UUID uuid2 = this.avatarProperties.PropertiesData_Field.FLImageID
            Boolean z2 = (this.avatarProperties.PropertiesData_Field.Flags & 1) != 0
            if ((this.avatarProperties.PropertiesData_Field.Flags & 2) == 0) {
                z = false
            }
            sLUserProfiles.UpdateAvatarProperties(uuid, uuid2, stringFromVariableUTF, stringFromVariableOEM, z2, z, SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.ProfileURL))
        }
    }
}
