// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objpopup;

import android.view.ViewGroup$LayoutParams;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import android.view.animation.AnimationUtils;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.CoordinatorLayout;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.ui.common.ConnectedActivity;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import javax.annotation.Nonnull;
import java.util.UUID;
import android.view.View;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissAdvancedBehavior;
import android.support.v4.app.Fragment;

public class SingleObjectPopupFragment extends Fragment
{
    private final SwipeDismissAdvancedBehavior.OnDismissListener dismissListener;
    private final View$OnClickListener frameClickListener;
    
    public SingleObjectPopupFragment() {
        this.frameClickListener = (View$OnClickListener)new _$Lambda$gmgx9kG_frukRCwYiu6KI4GSv6k(this);
        this.dismissListener = new SwipeDismissAdvancedBehavior.OnDismissListener() {
            @Override
            public void onDismiss(final View view) {
                SingleObjectPopupFragment.this.hideAndDismiss();
            }
            
            @Override
            public void onDragStateChanged(final int n) {
            }
        };
    }
    
    public static SingleObjectPopupFragment create(@Nonnull final UUID uuid) {
        final SingleObjectPopupFragment singleObjectPopupFragment = new SingleObjectPopupFragment();
        singleObjectPopupFragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, null));
        return singleObjectPopupFragment;
    }
    
    @Nullable
    private SLChatEvent getEvent() {
        final UserManager userManager = this.getUserManager();
        if (userManager != null) {
            return userManager.getObjectPopupsManager().getDisplayedObjectPopup();
        }
        return null;
    }
    
    @Nullable
    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(this.getArguments());
    }
    
    private void hideAndDismiss() {
        final FragmentActivity activity = this.getActivity();
        if (activity instanceof ConnectedActivity) {
            ((ConnectedActivity)activity).dismissSingleObjectPopup();
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968699, viewGroup, false);
        final UserManager userManager = this.getUserManager();
        SLChatEvent displayedObjectPopup;
        int n;
        if (userManager != null) {
            displayedObjectPopup = userManager.getObjectPopupsManager().getDisplayedObjectPopup();
            n = ((displayedObjectPopup != null && userManager.getObjectPopupsManager().mustAnimatePopup(displayedObjectPopup)) ? 1 : 0);
        }
        else {
            n = 0;
            displayedObjectPopup = null;
        }
        if (displayedObjectPopup == null) {
            this.hideAndDismiss();
        }
        else {
            final CoordinatorLayout coordinatorLayout = (CoordinatorLayout)inflate.findViewById(2131755584);
            final ChatEventViewHolder viewHolder = SLChatEvent.createViewHolder(LayoutInflater.from(this.getContext()), displayedObjectPopup.getViewType().ordinal(), coordinatorLayout, null);
            displayedObjectPopup.bindViewHolder(viewHolder, userManager, null);
            coordinatorLayout.addView(viewHolder.itemView);
            final ViewGroup$LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
            if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
                final SwipeDismissAdvancedBehavior behavior = new SwipeDismissAdvancedBehavior();
                behavior.setSwipeDirection(7);
                behavior.setListener(this.dismissListener);
                ((CoordinatorLayout.LayoutParams)layoutParams).setBehavior(behavior);
            }
            if (n != 0) {
                viewHolder.itemView.startAnimation(AnimationUtils.loadAnimation(this.getContext(), 2131034127));
            }
        }
        final View viewById = inflate.findViewById(2131755585);
        if (viewById != null) {
            viewById.setOnClickListener(this.frameClickListener);
        }
        return inflate;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (this.getEvent() == null) {
            this.hideAndDismiss();
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (this.getEvent() == null) {
            this.hideAndDismiss();
        }
    }
}
