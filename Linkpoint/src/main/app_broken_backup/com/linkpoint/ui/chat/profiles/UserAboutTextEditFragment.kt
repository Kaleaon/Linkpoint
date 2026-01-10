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

    fun makeSelection(ChatterID chatterID, Boolean z): Bundle {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID)
        makeSelection.putBoolean(IS_FIRST_LIFE_KEY, z)
        return makeSelection
    }

    /* access modifiers changed from: protected */
    fun decorateFragmentTitle(String str): String {
        return getString(R.string.edit_about_title, str)
    }

    /* access modifiers changed from: protected */
    fun getFieldHint(Context context): String {
        return getString(R.string.edit_about_hint)
    }

    /* access modifiers changed from: protected */
    fun onAvatarProperties(AvatarPropertiesReply avatarPropertiesReply)  {
        this.avatarProperties = avatarPropertiesReply
        setOriginalText(isFirstLife() ? SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.FLAboutText) : SLMessage.stringFromVariableUTF(avatarPropertiesReply.PropertiesData_Field.AboutText))
    }

    /* access modifiers changed from: protected */
    fun saveEditedText(SLAgentCircuit sLAgentCircuit, ChatterID chatterID, String str)  {
        var z: Boolean = true
        if (this.avatarProperties != null) {
            var stringFromVariableUTF: String = SLMessage.stringFromVariableUTF(this.avatarProperties.PropertiesData_Field.AboutText)
            var stringFromVariableOEM: String = SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.FLAboutText)
            if (isFirstLife()) {
                stringFromVariableOEM = str
            } else {
                stringFromVariableUTF = str
            }
            SLUserProfiles sLUserProfiles = sLAgentCircuit.getModules().userProfiles
            UUID uuid = this.avatarProperties.PropertiesData_Field.ImageID
            UUID uuid2 = this.avatarProperties.PropertiesData_Field.FLImageID
            var z2: Boolean = (this.avatarProperties.PropertiesData_Field.Flags & 1) != 0
            if ((this.avatarProperties.PropertiesData_Field.Flags & 2) == 0) {
                z = false
            }
            sLUserProfiles.UpdateAvatarProperties(uuid, uuid2, stringFromVariableUTF, stringFromVariableOEM, z2, z, SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.ProfileURL))
        }
    }
}
