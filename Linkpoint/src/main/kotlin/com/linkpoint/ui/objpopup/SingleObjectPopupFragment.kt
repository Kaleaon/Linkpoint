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
        public Unit onDismiss(View view) {
            SingleObjectPopupFragment.this.hideAndDismiss()
        }

        public Unit onDragStateChanged(Int i) {
        }
    }
    private val View.OnClickListener frameClickListener = $Lambda$gmgx9kG_frukRCwYiu6KI4GSv6k(this)

    @JvmStatic
    SingleObjectPopupFragment create(UUID uuid) {
        SingleObjectPopupFragment singleObjectPopupFragment = SingleObjectPopupFragment()
        singleObjectPopupFragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, (Bundle) null))
        return singleObjectPopupFragment
    }

    private SLChatEvent getEvent() {
        UserManager userManager = getUserManager()
        if (userManager != null) {
            return userManager.getObjectPopupsManager().getDisplayedObjectPopup()
        }
        return null
    }

    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(getArguments())
    }

    /* access modifiers changed from: private */
    public Unit hideAndDismiss() {
        FragmentActivity activity = getActivity()
        if (activity instanceof ConnectedActivity) {
            ((ConnectedActivity) activity).dismissSingleObjectPopup()
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_objpopup_SingleObjectPopupFragment_4170  reason: not valid java name */
    public /* synthetic */ Unit m694lambda$com_lumiyaviewer_lumiya_ui_objpopup_SingleObjectPopupFragment_4170(View view) {
        hideAndDismiss()
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        SLChatEvent sLChatEvent
        View inflate = layoutInflater.inflate(R.layout.object_popups_single_fragment_layout, viewGroup, false)
        UserManager userManager = getUserManager()
        if (userManager != null) {
            SLChatEvent displayedObjectPopup = userManager.getObjectPopupsManager().getDisplayedObjectPopup()
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
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) inflate.findViewById(R.id.single_object_popup_container)
            ChatEventViewHolder createViewHolder = SLChatEvent.createViewHolder(LayoutInflater.from(getContext()), sLChatEvent.getViewType().ordinal(), coordinatorLayout, (RecyclerView.Adapter) null)
            sLChatEvent.bindViewHolder(createViewHolder, userManager, (ChatEventTimestampUpdater) null)
            coordinatorLayout.addView(createViewHolder.itemView)
            ViewGroup.LayoutParams layoutParams = createViewHolder.itemView.getLayoutParams()
            if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
                SwipeDismissAdvancedBehavior swipeDismissAdvancedBehavior = SwipeDismissAdvancedBehavior()
                swipeDismissAdvancedBehavior.setSwipeDirection(7)
                swipeDismissAdvancedBehavior.setListener(this.dismissListener)
                ((CoordinatorLayout.LayoutParams) layoutParams).setBehavior(swipeDismissAdvancedBehavior)
            }
            if (z) {
                createViewHolder.itemView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_above))
            }
        }
        View findViewById = inflate.findViewById(R.id.touch_capture_view)
        if (findViewById != null) {
            findViewById.setOnClickListener(this.frameClickListener)
        }
        return inflate
    }

    public Unit onResume() {
        super.onResume()
        if (getEvent() == null) {
            hideAndDismiss()
        }
    }

    public Unit onStart() {
        super.onStart()
        if (getEvent() == null) {
            hideAndDismiss()
        }
    }
}
