package com.lumiyaviewer.lumiya.ui.chat.contacts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey
import com.lumiyaviewer.lumiya.react.UIThreadExecutor
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadMessageInfo
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo
import com.lumiyaviewer.lumiya.ui.common.DismissableAdapter
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo
import java.io.Closeable

class ActiveChatsListAdapter(
    private val context: Context,
    private val userManager: UserManager
) : BaseAdapter(), Closeable, DismissableAdapter {

    companion object {
        private const val VIEW_TYPE_ROW = 0
        private const val VIEW_TYPE_HEADER = 1
        private const val VIEW_TYPE_COUNT = 2
    }

    private val inflater = LayoutInflater.from(context)
    private val viewBuilder = ChatterItemViewBuilder()
    private val localChatItem = LocalChatItem(ChatterID.getLocalChatterID(userManager.userID))
    private val onlineFriendsHeader = OnlineFriendsHeaderRow()

    private var activeChatters: ImmutableList<out ChatterDisplayInfo> = ImmutableList.of()
    private var onlineFriends: ImmutableList<out ChatterDisplayInfo> = ImmutableList.of()
    private var currentLocationInfo: CurrentLocationInfo? = null

    private val activeChattersSubscription: Subscription<ChatterListType, ImmutableList<ChatterDisplayData>>
    private val onlineFriendsSubscription: Subscription<ChatterListType, ImmutableList<ChatterDisplayData>>
    private val currentLocationInfoSubscription: Subscription<SubscriptionSingleKey, CurrentLocationInfo>
    private val localChatUnreadCountSubscription: Subscription<ChatterID, UnreadMessageInfo>
    private val localVoiceChatSubscription: Subscription<ChatterID, VoiceChatInfo>

    init {
        activeChattersSubscription = userManager.chatterList.chatterList.subscribe(
            ChatterListType.Active,
            UIThreadExecutor.getInstance()
        ) { data ->
            activeChatters = data
            notifyDataSetChanged()
        }

        onlineFriendsSubscription = userManager.chatterList.chatterList.subscribe(
            ChatterListType.FriendsOnline,
            UIThreadExecutor.getInstance()
        ) { data ->
            onlineFriends = data
            onlineFriendsHeader.setAnyoneOnline(data.isNotEmpty())
            notifyDataSetChanged()
        }

        Debug.Printf("currentLocationInfo subscribing")
        currentLocationInfoSubscription = userManager.currentLocationInfo.subscribe(
            SubscriptionSingleDataPool.getSingleDataKey(),
            UIThreadExecutor.getInstance()
        ) { data ->
            currentLocationInfo = data
            Debug.Printf("currentLocationInfo updated: %s", data.toString())
            notifyDataSetChanged()
        }

        localChatUnreadCountSubscription = userManager.chatterList.activeChattersManager.unreadCounts.subscribe(
            localChatItem.getChatterID(userManager),
            UIThreadExecutor.getInstance()
        ) { data ->
            localChatItem.setUnreadInfo(data)
            notifyDataSetChanged()
        }

        localVoiceChatSubscription = userManager.voiceChatInfo.subscribe(
            localChatItem.getChatterID(userManager),
            UIThreadExecutor.getInstance()
        ) { data ->
            localChatItem.setVoiceChatInfo(data)
            notifyDataSetChanged()
        }
    }

    override fun getCount(): Int {
        return activeChatters.size + 1 + 1 + onlineFriends.size
    }

    override fun getItem(position: Int): Any? {
        val activeSize = activeChatters.size
        
        return when {
            position == 0 -> localChatItem
            position in 1..activeSize -> activeChatters[position - 1]
            position == activeSize + 1 -> onlineFriendsHeader
            position > activeSize + 1 -> onlineFriends[position - activeSize - 2]
            else -> null
        }
    }

    override fun getItemId(position: Int): Long = 0

    override fun getItemViewType(position: Int): Int {
        return if (position == activeChatters.size + 1) VIEW_TYPE_HEADER else VIEW_TYPE_ROW
    }

    override fun getViewTypeCount(): Int = VIEW_TYPE_COUNT

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val item = getItem(position)

        if (item == onlineFriendsHeader) {
            return onlineFriendsHeader.getView(inflater, convertView, parent)
        }

        if (item !is ChatterDisplayInfo) {
            return null
        }

        viewBuilder.reset()
        item.buildView(context, viewBuilder, userManager)
        val view = viewBuilder.getView(inflater, convertView, parent, true)
        view?.let {
            SwipeDismissListViewTouchListener.restoreViewState(it)
        }
        return view
    }

    override fun areAllItemsEnabled(): Boolean = false

    override fun isEnabled(position: Int): Boolean {
        return position != activeChatters.size + 1
    }

    override fun hasStableIds(): Boolean = false

    override fun isEmpty(): Boolean = false

    override fun close() {
        activeChattersSubscription.unsubscribe()
        onlineFriendsSubscription.unsubscribe()
        currentLocationInfoSubscription.unsubscribe()
        localChatUnreadCountSubscription.unsubscribe()
        localVoiceChatSubscription.unsubscribe()
    }

    override fun canDismiss(position: Int): Boolean {
        return position > 0 && position <= activeChatters.size
    }

    override fun onDismiss(position: Int) {
        val item = getItem(position)
        if (item is ChatterDisplayInfo) {
            val chatterID = item.getChatterID(userManager)
            chatterID?.let {
                userManager.chatterList.activeChattersManager.markChatterInactive(it, false)
            }
        }
    }

    // Inner class: LocalChatItem
    private inner class LocalChatItem(private val chatterID: ChatterID) : ChatterDisplayInfo {
        
        private var unreadMessageInfo: UnreadMessageInfo? = null
        private var voiceChatInfo: VoiceChatInfo? = null

        override fun buildView(
            context: Context,
            builder: ChatterItemViewBuilder,
            userManager: UserManager
        ) {
            val title = StringBuilder(context.getString(R.string.local_chat_item_title))
            
            currentLocationInfo?.let { locationInfo ->
                title.append(": ")
                val inRangeUsers = locationInfo.inChatRangeUsers()
                if (inRangeUsers != 0) {
                    title.append(context.getString(R.string.someone_in_chat_range, inRangeUsers))
                } else {
                    title.append(context.getString(R.string.no_one_in_chat_range))
                }
            }

            builder.setLabel(title.toString())
            builder.setUnreadCount(unreadMessageInfo?.unreadCount() ?: 0)
            builder.setThumbnailDefaultIcon(R.attr.IconLocalChat)
            builder.setThumbnailChatterID(chatterID, null)

            val lastMessage = unreadMessageInfo?.lastMessage()
            builder.setLastMessage(
                lastMessage?.getPlainTextMessage(context, userManager, false)?.toString()
            )

            val isVoiceActive = voiceChatInfo != null && 
                voiceChatInfo?.state != VoiceChatInfo.VoiceChatState.None
            builder.setVoiceActive(isVoiceActive)
        }

        override fun getChatterID(userManager: UserManager): ChatterID = chatterID

        override fun getDisplayName(): String? {
            return context.getString(R.string.local_chat_item_title)
        }

        fun setUnreadInfo(info: UnreadMessageInfo?) {
            unreadMessageInfo = info
        }

        fun setVoiceChatInfo(info: VoiceChatInfo?) {
            voiceChatInfo = info
        }
    }

    // Inner class: OnlineFriendsHeaderRow
    private class OnlineFriendsHeaderRow {
        
        private var isAnyoneOnline = false

        fun getView(inflater: LayoutInflater, convertView: View?, parent: ViewGroup): View {
            val layoutId = if (isAnyoneOnline) R.layout.list_header else R.layout.list_header_small
            val titleId = if (isAnyoneOnline) R.id.list_header_title else R.id.list_header_small_title

            val view = if (convertView?.id == titleId) {
                convertView
            } else {
                inflater.inflate(layoutId, parent, false)
            }

            val titleResId = if (isAnyoneOnline) {
                R.string.friends_online_caption
            } else {
                R.string.no_friends_online_caption
            }

            view.findViewById<TextView>(titleId)?.setText(titleResId)
            return view
        }

        fun setAnyoneOnline(online: Boolean) {
            isAnyoneOnline = online
        }
    }
}
