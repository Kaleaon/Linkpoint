package com.linkpoint.ui.objpopup

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.linkpoint.R
import com.linkpoint.slproto.chat.generic.ChatEventViewHolder
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatEventTimestampUpdater
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.ConnectedActivity
import com.linkpoint.ui.common.SwipeDismissAdvancedBehavior
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class SingleObjectPopupFragment : Fragment() {
    private val SwipeDismissAdvancedBehavior.OnDismissListener dismissListener = SwipeDismissAdvancedBehavior.OnDismissListener() {
        fun onDismiss(view: View) {
            SingleObjectPopupFragment.this.hideAndDismiss()
        }

        fun onDragStateChanged(i: Int) {
        }
    }
    private val View.OnClickListener frameClickListener = $Lambda$gmgx9kG_frukRCwYiu6KI4GSv6k(this)

    @JvmStatic
     fun create(uuid: UUID): SingleObjectPopupFragment {
        val singleObjectPopupFragment: SingleObjectPopupFragment = SingleObjectPopupFragment()
        singleObjectPopupFragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, (Bundle) null))
        return singleObjectPopupFragment
    }

     private fun getEvent(): SLChatEvent {
        val userManager: UserManager = getUserManager()
        if (userManager != null) {
            return userManager.getObjectPopupsManager().getDisplayedObjectPopup()
        }
        return null
    }

     private fun getUserManager(): UserManager {
        return ActivityUtils.getUserManager(getArguments())
    }

    /* access modifiers changed from: private */
    fun hideAndDismiss() {
        val activity: FragmentActivity = getActivity()
        if (activity instanceof ConnectedActivity) {
            ((ConnectedActivity) activity).dismissSingleObjectPopup()
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objpopup_SingleObjectPopupFragment_4170  reason: not valid java name */
    public /* synthetic */ Unit m694lambda$com_lumiyaviewer_lumiya_ui_objpopup_SingleObjectPopupFragment_4170(View view) {
        hideAndDismiss()
    }

     public fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, bundle: Bundle): View {
        SLChatEvent sLChatEvent
        val inflate: View = layoutInflater.inflate(R.layout.object_popups_single_fragment_layout, viewGroup, false)
        val userManager: UserManager = getUserManager()
        if (userManager != null) {
            val displayedObjectPopup: SLChatEvent = userManager.getObjectPopupsManager().getDisplayedObjectPopup()
            if (displayedObjectPopup != null) {
                sLChatEvent = displayedObjectPopup
                z = userManager.getObjectPopupsManager().mustAnimatePopup(displayedObjectPopup)
            } else {
                sLChatEvent = displayedObjectPopup
                z = false
            }
        } else {
            z = false
            sLChatEvent = null
        }
        if (sLChatEvent == null) {
            hideAndDismiss()
        } else {
            val coordinatorLayout: CoordinatorLayout = (CoordinatorLayout) inflate.findViewById(R.id.single_object_popup_container)
            val createViewHolder: ChatEventViewHolder = SLChatEvent.createViewHolder(LayoutInflater.from(getContext()), sLChatEvent.getViewType().ordinal(), coordinatorLayout, (RecyclerView.Adapter) null)
            sLChatEvent.bindViewHolder(createViewHolder, userManager, (ChatEventTimestampUpdater) null)
            coordinatorLayout.addView(createViewHolder.itemView)
            ViewGroup.LayoutParams layoutParams = createViewHolder.itemView.getLayoutParams()
            if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
                val swipeDismissAdvancedBehavior: SwipeDismissAdvancedBehavior = SwipeDismissAdvancedBehavior()
                swipeDismissAdvancedBehavior.setSwipeDirection(7)
                swipeDismissAdvancedBehavior.setListener(this.dismissListener)
                ((CoordinatorLayout.LayoutParams) layoutParams).setBehavior(swipeDismissAdvancedBehavior)
            }
            if (z) {
                createViewHolder.itemView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_above))
            }
        }
        val findViewById: View = inflate.findViewById(R.id.touch_capture_view)
        if (findViewById != null) {
            findViewById.setOnClickListener(this.frameClickListener)
        }
        return inflate
    }

    fun onResume() {
        super.onResume()
        if (getEvent() == null) {
            hideAndDismiss()
        }
    }

    fun onStart() {
        super.onStart()
        if (getEvent() == null) {
            hideAndDismiss()
        }
    }
}
