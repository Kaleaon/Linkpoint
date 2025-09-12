// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objpopup;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.ConnectedActivity;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissAdvancedBehavior;
import java.util.UUID;

public class SingleObjectPopupFragment extends Fragment
{

    private final com.lumiyaviewer.lumiya.ui.common.SwipeDismissAdvancedBehavior.OnDismissListener dismissListener = new com.lumiyaviewer.lumiya.ui.common.SwipeDismissAdvancedBehavior.OnDismissListener() {

        final SingleObjectPopupFragment this$0;

        public void onDismiss(View view)
        {
            SingleObjectPopupFragment._2D_wrap0(SingleObjectPopupFragment.this);
        }

        public void onDragStateChanged(int i)
        {
        }

            
            {
                this$0 = SingleObjectPopupFragment.this;
                super();
            }
    };
    private final android.view.View.OnClickListener frameClickListener = new _2D_.Lambda.gmgx9kG_frukRCwYiu6KI4GSv6k(this);

    static void _2D_wrap0(SingleObjectPopupFragment singleobjectpopupfragment)
    {
        singleobjectpopupfragment.hideAndDismiss();
    }

    public SingleObjectPopupFragment()
    {
    }

    public static SingleObjectPopupFragment create(UUID uuid)
    {
        SingleObjectPopupFragment singleobjectpopupfragment = new SingleObjectPopupFragment();
        singleobjectpopupfragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, null));
        return singleobjectpopupfragment;
    }

    private SLChatEvent getEvent()
    {
        UserManager usermanager = getUserManager();
        if (usermanager != null)
        {
            return usermanager.getObjectPopupsManager().getDisplayedObjectPopup();
        } else
        {
            return null;
        }
    }

    private UserManager getUserManager()
    {
        return ActivityUtils.getUserManager(getArguments());
    }

    private void hideAndDismiss()
    {
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity instanceof ConnectedActivity)
        {
            ((ConnectedActivity)fragmentactivity).dismissSingleObjectPopup();
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_objpopup_SingleObjectPopupFragment_4170(View view)
    {
        hideAndDismiss();
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        viewgroup = layoutinflater.inflate(0x7f04007b, viewgroup, false);
        bundle = getUserManager();
        boolean flag;
        if (bundle != null)
        {
            layoutinflater = bundle.getObjectPopupsManager().getDisplayedObjectPopup();
            ChatEventViewHolder chateventviewholder;
            CoordinatorLayout coordinatorlayout;
            if (layoutinflater != null)
            {
                flag = bundle.getObjectPopupsManager().mustAnimatePopup(layoutinflater);
            } else
            {
                flag = false;
            }
        } else
        {
            flag = false;
            layoutinflater = null;
        }
        if (layoutinflater != null) goto _L2; else goto _L1
_L1:
        hideAndDismiss();
_L4:
        layoutinflater = viewgroup.findViewById(0x7f100241);
        if (layoutinflater != null)
        {
            layoutinflater.setOnClickListener(frameClickListener);
        }
        return viewgroup;
_L2:
        coordinatorlayout = (CoordinatorLayout)viewgroup.findViewById(0x7f100240);
        chateventviewholder = SLChatEvent.createViewHolder(LayoutInflater.from(getContext()), layoutinflater.getViewType().ordinal(), coordinatorlayout, null);
        layoutinflater.bindViewHolder(chateventviewholder, bundle, null);
        coordinatorlayout.addView(chateventviewholder.itemView);
        layoutinflater = chateventviewholder.itemView.getLayoutParams();
        if (layoutinflater instanceof android.support.design.widget.CoordinatorLayout.LayoutParams)
        {
            bundle = new SwipeDismissAdvancedBehavior();
            bundle.setSwipeDirection(7);
            bundle.setListener(dismissListener);
            ((android.support.design.widget.CoordinatorLayout.LayoutParams)layoutinflater).setBehavior(bundle);
        }
        if (flag)
        {
            layoutinflater = AnimationUtils.loadAnimation(getContext(), 0x7f05000f);
            chateventviewholder.itemView.startAnimation(layoutinflater);
        }
        if (true) goto _L4; else goto _L3
_L3:
    }

    public void onResume()
    {
        super.onResume();
        if (getEvent() == null)
        {
            hideAndDismiss();
        }
    }

    public void onStart()
    {
        super.onStart();
        if (getEvent() == null)
        {
            hideAndDismiss();
        }
    }
}
