package com.lumiyaviewer.lumiya.ui.chat.profiles

import android.content.Context
import android.os.Bundle
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply
import com.lumiyaviewer.lumiya.slproto.modules.SLUserProfiles
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment
import java.util.UUID

class UserAboutTextEditFragment : ProfileTextFieldEditFragment {
    private val IS_FIRST_LIFE_KEY: String = "isFirstLife"
    private AvatarPropertiesReply avatarProperties

    private Boolean isFirstLife() {
        Bundle arguments = getArguments()
        if (arguments != null) {
            return arguments.getBoolean(IS_FIRST_LIFE_KEY)
        }
        return false
    }

    Bundle makeSelection(ChatterID chatterID, Boolean z) {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID)
        makeSelection.putBoolean(IS_FIRST_LIFE_KEY, z)
        return makeSelection
    }

    /* access modifiers changed from: protected */
    String decorateFragmentTitle(String str) {
        return getString(R.string.edit_about_title, str)
    }

    /* access modifiers changed from: protected */
    String getFieldHint(Context context) {
        return getString(R.string.edit_about_hint)
    }

    /* access modifiers changed from: protected */
    Unit onAvatarProperties(AvatarPropertiesReply avatarPropertiesReply) {
        this.avatarProperties = avatarPropertiesReply
        setOriginalText(isFirstLife() ? SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.FLAboutText) : SLMessage.stringFromVariableUTF(avatarPropertiesReply.PropertiesData_Field.AboutText))
    }

    /* access modifiers changed from: protected */
    Unit saveEditedText(SLAgentCircuit sLAgentCircuit, ChatterID chatterID, String str) {
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
