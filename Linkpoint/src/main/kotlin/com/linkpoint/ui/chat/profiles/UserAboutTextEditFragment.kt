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
    private const val IS_FIRST_LIFE_KEY: String = "isFirstLife"
    private AvatarPropertiesReply avatarProperties

     private fun isFirstLife(): Boolean {
        val arguments: Bundle = getArguments()
        if (arguments != null) {
            return arguments.getBoolean(IS_FIRST_LIFE_KEY)
        }
        return false
    }

    @JvmStatic
     fun makeSelection(chatterID: ChatterID, z: Boolean): Bundle {
        val makeSelection: Bundle = ChatterFragment.makeSelection(chatterID)
        makeSelection.putBoolean(IS_FIRST_LIFE_KEY, z)
        return makeSelection
    }

    /* access modifiers changed from: protected */
     public fun decorateFragmentTitle(str: String): String {
        return getString(R.string.edit_about_title, str)
    }

    /* access modifiers changed from: protected */
     public fun getFieldHint(context: Context): String {
        return getString(R.string.edit_about_hint)
    }

    /* access modifiers changed from: protected */
    fun onAvatarProperties(avatarPropertiesReply: AvatarPropertiesReply) {
        this.avatarProperties = avatarPropertiesReply
        setOriginalText(isFirstLife() ? SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.FLAboutText) : SLMessage.stringFromVariableUTF(avatarPropertiesReply.PropertiesData_Field.AboutText))
    }

    /* access modifiers changed from: protected */
    fun saveEditedText(sLAgentCircuit: SLAgentCircuit, chatterID: ChatterID, str: String) {
        val z: Boolean = true
        if (this.avatarProperties != null) {
            val stringFromVariableUTF: String = SLMessage.stringFromVariableUTF(this.avatarProperties.PropertiesData_Field.AboutText)
            val stringFromVariableOEM: String = SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.FLAboutText)
            if (isFirstLife()) {
                stringFromVariableOEM = str
            } else {
                stringFromVariableUTF = str
            }
            val sLUserProfiles: SLUserProfiles = sLAgentCircuit.getModules().userProfiles
            val uuid: UUID = this.avatarProperties.PropertiesData_Field.ImageID
            val uuid2: UUID = this.avatarProperties.PropertiesData_Field.FLImageID
            val z2: Boolean = (this.avatarProperties.PropertiesData_Field.Flags & 1) != 0
            if ((this.avatarProperties.PropertiesData_Field.Flags & 2) == 0) {
                z = false
            }
            sLUserProfiles.UpdateAvatarProperties(uuid, uuid2, stringFromVariableUTF, stringFromVariableOEM, z2, z, SLMessage.stringFromVariableOEM(this.avatarProperties.PropertiesData_Field.ProfileURL))
        }
    }
}
