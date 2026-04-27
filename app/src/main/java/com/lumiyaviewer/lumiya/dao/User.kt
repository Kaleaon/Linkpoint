package com.lumiyaviewer.lumiya.dao

import android.content.Context
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder
import java.util.UUID

data class User(
    var id: Long? = null,
    var uuid: UUID? = null,
    var userName: String? = null,
    var displayName: String? = null,
    var badUUID: Boolean = false,
    var isFriend: Boolean = false,
    var rightsGiven: Int = 0,
    var rightsHas: Int = 0,
) : ChatterDisplayInfo {
    override fun buildView(
        context: Context,
        chatterItemViewBuilder: ChatterItemViewBuilder,
        userManager: UserManager,
    ) {
        chatterItemViewBuilder.setLabel(displayName)
        chatterItemViewBuilder.setThumbnailChatterID(getChatterID(userManager), displayName)
    }

    fun getChatterID(userManager: UserManager): ChatterID {
        return ChatterID.getUserChatterID(userManager.userID, uuid)
    }

    fun nameNeedsFetching(): Boolean {
        return if (userName == null || displayName == null) {
            !badUUID
        } else {
            false
        }
    }
}
