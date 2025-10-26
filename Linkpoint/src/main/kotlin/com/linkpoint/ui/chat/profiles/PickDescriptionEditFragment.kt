package com.linkpoint.ui.chat.profiles

import android.content.Context
import android.os.Bundle
import com.linkpoint.R
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.PickInfoReply
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.manager.AvatarPickKey
import com.linkpoint.ui.common.ChatterFragment
import com.linkpoint.ui.common.TextFieldEditFragment
import javax.annotation.Nullable

class PickDescriptionEditFragment : TextFieldEditFragment() {
    private const val AVATAR_PICK_KEY: String = "avatarPickKey"
    private val SubscriptionData<AvatarPickKey, PickInfoReply> pickInfo = SubscriptionData<>(UIThreadExecutor.getInstance(), $Lambda$Y7Ne2VWglUcvjFUgJydWWKVgIXM(this))

     private fun getPickKey(): AvatarPickKey {
        val arguments: Bundle = getArguments()
        if (arguments == null || !arguments.containsKey(AVATAR_PICK_KEY)) {
            return null
        }
        return (AvatarPickKey) arguments.getParcelable(AVATAR_PICK_KEY)
    }

    @JvmStatic
     fun makeSelection(chatterID: ChatterID, avatarPickKey: AvatarPickKey): Bundle {
        val makeSelection: Bundle = ChatterFragment.makeSelection(chatterID)
        makeSelection.putParcelable(AVATAR_PICK_KEY, avatarPickKey)
        return makeSelection
    }

    /* access modifiers changed from: private */
    /* renamed from: onPickInfoReply */
    fun m506com_lumiyaviewer_lumiya_ui_chat_profiles_PickDescriptionEditFragmentmthref0(pickInfoReply: PickInfoReply) {
        if (pickInfoReply != null) {
            setOriginalText(SLMessage.stringFromVariableUTF(pickInfoReply.Data_Field.Desc))
        }
    }

    /* access modifiers changed from: protected */
     public fun getFieldHint(context: Context): String {
        return getString(R.string.pick_description_edit_hint)
    }

    /* access modifiers changed from: protected */
    fun onShowUser(chatterID: ChatterID) {
        val pickKey: AvatarPickKey = getPickKey()
        if (this.userManager == null || !(chatterID instanceof ChatterID.ChatterIDUser) || pickKey == null) {
            this.pickInfo.unsubscribe()
        } else {
            this.pickInfo.subscribe(this.userManager.getAvatarPickInfos().getPool(), pickKey)
        }
    }

    /* access modifiers changed from: protected */
    fun saveEditedText(sLAgentCircuit: SLAgentCircuit, chatterID: ChatterID, str: String) {
        val pickKey: AvatarPickKey = getPickKey()
        val data: PickInfoReply = this.pickInfo.getData()
        if (sLAgentCircuit != null && pickKey != null && data != null) {
            sLAgentCircuit.getModules().userProfiles.UpdatePickInfo(pickKey.pickID, data.Data_Field.CreatorID, data.Data_Field.ParcelID, SLMessage.stringFromVariableOEM(data.Data_Field.Name), str, data.Data_Field.SnapshotID, data.Data_Field.PosGlobal, data.Data_Field.SortOrder, data.Data_Field.Enabled)
        }
    }
}
