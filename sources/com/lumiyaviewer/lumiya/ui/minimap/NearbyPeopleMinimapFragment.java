// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.minimap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NearbyPeopleMinimapFragment extends Fragment
{
    private class NearbyUserRecyclerAdapter extends android.support.v7.widget.RecyclerView.Adapter
    {

        private ImmutableList chatters;
        private final Context context;
        private final LayoutInflater layoutInflater;
        private long nextStableId;
        private int selectedPosition;
        private UUID selectedUUID;
        private final Map stableIds = new HashMap();
        final NearbyPeopleMinimapFragment this$0;
        private final UserManager userManager;

        public int getItemCount()
        {
            return chatters.size();
        }

        public long getItemId(int i)
        {
            if (i >= 0 && i < chatters.size())
            {
                Object obj = ((ChatterDisplayData)chatters.get(i)).chatterID.getOptionalChatterUUID();
                if (obj != null)
                {
                    obj = (Long)stableIds.get(obj);
                    if (obj != null)
                    {
                        return ((Long) (obj)).longValue();
                    }
                }
            }
            return -1L;
        }

        public volatile void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i)
        {
            onBindViewHolder((NearbyUserViewHolder)viewholder, i);
        }

        public void onBindViewHolder(NearbyUserViewHolder nearbyuserviewholder, int i)
        {
            boolean flag = false;
            if (i >= 0 && i < chatters.size())
            {
                Context context1 = context;
                LayoutInflater layoutinflater = layoutInflater;
                UserManager usermanager = userManager;
                ChatterDisplayData chatterdisplaydata = (ChatterDisplayData)chatters.get(i);
                if (i == selectedPosition)
                {
                    flag = true;
                }
                nearbyuserviewholder.bindToData(context1, layoutinflater, usermanager, chatterdisplaydata, flag);
            }
        }

        public volatile android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
        {
            return onCreateViewHolder(viewgroup, i);
        }

        public NearbyUserViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
        {
            viewgroup = layoutInflater.inflate(0x7f04005f, viewgroup, false);
            return new NearbyUserViewHolder(viewgroup);
        }

        public void setChatters(ImmutableList immutablelist)
        {
            if (immutablelist == null)
            {
                immutablelist = ImmutableList.of();
            }
            chatters = immutablelist;
            selectedPosition = -1;
            immutablelist = new HashSet();
            for (int i = 0; i < chatters.size(); i++)
            {
                UUID uuid = ((ChatterDisplayData)chatters.get(i)).chatterID.getOptionalChatterUUID();
                if (uuid == null)
                {
                    continue;
                }
                immutablelist.add(uuid);
                if (!stableIds.containsKey(uuid))
                {
                    stableIds.put(uuid, Long.valueOf(nextStableId));
                    nextStableId = nextStableId + 1L;
                }
                if (Objects.equal(uuid, selectedUUID))
                {
                    selectedPosition = i;
                }
            }

            stableIds.keySet().retainAll(immutablelist);
            notifyDataSetChanged();
        }

        public void setSelected(UUID uuid)
        {
            int i;
            selectedUUID = uuid;
            if (uuid == null)
            {
                break MISSING_BLOCK_LABEL_93;
            }
            i = 0;
_L3:
            UUID uuid1;
            if (i >= chatters.size())
            {
                break MISSING_BLOCK_LABEL_93;
            }
            uuid1 = ((ChatterDisplayData)chatters.get(i)).chatterID.getOptionalChatterUUID();
            if (uuid1 == null || !Objects.equal(uuid, uuid1)) goto _L2; else goto _L1
_L1:
            if (i != selectedPosition)
            {
                int j = selectedPosition;
                selectedPosition = i;
                notifyItemChanged(selectedPosition);
                notifyItemChanged(j);
            }
            return;
_L2:
            i++;
              goto _L3
            i = -1;
              goto _L1
        }

        NearbyUserRecyclerAdapter(Context context1, UserManager usermanager)
        {
            this$0 = NearbyPeopleMinimapFragment.this;
            super();
            nextStableId = 1L;
            chatters = ImmutableList.of();
            selectedPosition = -1;
            context = context1;
            userManager = usermanager;
            layoutInflater = LayoutInflater.from(context1);
            setHasStableIds(true);
        }
    }

    private class NearbyUserViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder
        implements android.view.View.OnClickListener
    {

        private final float cardSelectedElevation;
        private final CardView cardView;
        private ChatterDisplayData chatterDisplayData;
        private final View selectedLayout;
        final NearbyPeopleMinimapFragment this$0;
        private final FrameLayout userItemViewHolder;
        private final ChatterItemViewBuilder viewBuilder = new ChatterItemViewBuilder();

        public void bindToData(Context context, LayoutInflater layoutinflater, UserManager usermanager, ChatterDisplayData chatterdisplaydata, boolean flag)
        {
            viewBuilder.reset();
            chatterdisplaydata.buildView(context, viewBuilder, usermanager);
            context = userItemViewHolder.getChildAt(0);
            layoutinflater = viewBuilder.getView(layoutinflater, context, userItemViewHolder, true);
            if (layoutinflater != context)
            {
                if (context != null)
                {
                    userItemViewHolder.removeView(context);
                }
                userItemViewHolder.addView(layoutinflater);
            }
            if (flag)
            {
                cardView.setCardElevation(cardSelectedElevation);
                cardView.setCardBackgroundColor(NearbyPeopleMinimapFragment._2D_get0(NearbyPeopleMinimapFragment.this));
                selectedLayout.setVisibility(0);
            } else
            {
                cardView.setCardElevation(0.0F);
                cardView.setCardBackgroundColor(0);
                selectedLayout.setVisibility(8);
            }
            chatterDisplayData = chatterdisplaydata;
        }

        public void onClick(View view)
        {
            view.getId();
            JVM INSTR tableswitch 2131755487 2131755489: default 32
        //                       2131755487 33
        //                       2131755488 32
        //                       2131755489 79;
               goto _L1 _L2 _L1 _L3
_L1:
            return;
_L2:
            if ((view = getFragmentManager()) != null && ((view = view.findFragmentById(0x7f100286)) instanceof MinimapView.OnUserClickListener))
            {
                ((MinimapView.OnUserClickListener)view).onUserClick(chatterDisplayData.chatterID.getOptionalChatterUUID());
                return;
            }
            continue; /* Loop/switch isn't completed */
_L3:
            if (chatterDisplayData != null)
            {
                DetailsActivity.showDetails(getActivity(), ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(chatterDisplayData.chatterID));
                return;
            }
            if (true) goto _L1; else goto _L4
_L4:
        }

        public NearbyUserViewHolder(View view)
        {
            this$0 = NearbyPeopleMinimapFragment.this;
            super(view);
            chatterDisplayData = null;
            userItemViewHolder = (FrameLayout)view.findViewById(0x7f1001df);
            cardView = (CardView)view.findViewById(0x7f1001de);
            cardSelectedElevation = cardView.getCardElevation();
            selectedLayout = view.findViewById(0x7f1001e0);
            userItemViewHolder.setOnClickListener(this);
            view.findViewById(0x7f1001e1).setOnClickListener(this);
        }
    }


    private NearbyUserRecyclerAdapter adapter;
    private int cardSelectedColor;
    private final SubscriptionData chatterList = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda._cls0SrW7eOJ5Pm_SVTDQOmxGjUXtco(this));
    View emptyView;
    RecyclerView userListView;

    static int _2D_get0(NearbyPeopleMinimapFragment nearbypeopleminimapfragment)
    {
        return nearbypeopleminimapfragment.cardSelectedColor;
    }

    public NearbyPeopleMinimapFragment()
    {
        adapter = null;
        cardSelectedColor = 0;
    }

    static Fragment newInstance(UUID uuid)
    {
        NearbyPeopleMinimapFragment nearbypeopleminimapfragment = new NearbyPeopleMinimapFragment();
        nearbypeopleminimapfragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, null));
        return nearbypeopleminimapfragment;
    }

    private void onChatterList(ImmutableList immutablelist)
    {
        byte byte0 = 8;
        if (adapter != null)
        {
            adapter.setChatters(immutablelist);
        }
        if (getView() != null)
        {
            boolean flag = immutablelist.isEmpty();
            immutablelist = emptyView;
            int i;
            if (flag)
            {
                i = 0;
            } else
            {
                i = 8;
            }
            immutablelist.setVisibility(i);
            immutablelist = userListView;
            if (flag)
            {
                i = byte0;
            } else
            {
                i = 0;
            }
            immutablelist.setVisibility(i);
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_minimap_NearbyPeopleMinimapFragment_2D_mthref_2D_0(ImmutableList immutablelist)
    {
        onChatterList(immutablelist);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        viewgroup = layoutinflater.inflate(0x7f040060, viewgroup, false);
        ButterKnife.bind(this, viewgroup);
        bundle = new TypedValue();
        layoutinflater.getContext().getTheme().resolveAttribute(0x7f010002, bundle, true);
        cardSelectedColor = ((TypedValue) (bundle)).data;
        adapter = new NearbyUserRecyclerAdapter(getContext(), ActivityUtils.getUserManager(getArguments()));
        userListView.setAdapter(adapter);
        return viewgroup;
    }

    public void onStart()
    {
        super.onStart();
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        if (usermanager != null)
        {
            chatterList.subscribe(usermanager.getChatterList().getChatterList(), ChatterListType.Nearby);
            return;
        } else
        {
            chatterList.unsubscribe();
            return;
        }
    }

    public void onStop()
    {
        chatterList.unsubscribe();
        super.onStop();
    }

    public void setSelectedUser(UUID uuid)
    {
        if (adapter != null)
        {
            adapter.setSelected(uuid);
        }
    }
}
