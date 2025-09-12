// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.avapicker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.astuetz.PagerSlidingTabStrip;
import com.google.common.base.Predicate;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterListSubscriptionAdapter;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public abstract class AvatarPickerFragment extends FragmentWithTitle
    implements android.widget.AdapterView.OnItemClickListener
{
    public class AvatarPickerPagerAdapter extends PagerAdapter
    {

        private final Context context;
        final AvatarPickerFragment this$0;

        public void destroyItem(ViewGroup viewgroup, int i, Object obj)
        {
            if (obj instanceof View)
            {
                if (obj instanceof ListView)
                {
                    ((ListView)obj).setAdapter(null);
                }
                viewgroup.removeView((View)obj);
            }
        }

        public int getCount()
        {
            return ContactListType.values().length;
        }

        public CharSequence getPageTitle(int i)
        {
            if (i >= 0 && i < ContactListType.values().length)
            {
                return ContactListType.values()[i].toString();
            } else
            {
                return null;
            }
        }

        public Object instantiateItem(ViewGroup viewgroup, int i)
        {
            if (i >= 0 && i < ContactListType.values().length)
            {
                ContactListType contactlisttype = ContactListType.values()[i];
                ListView listview = new ListView(context);
                listview.setOnItemClickListener(AvatarPickerFragment.this);
                listview.setAdapter(AvatarPickerFragment._2D_wrap0(AvatarPickerFragment.this, getContext(), ActivityUtils.getUserManager(getArguments()), contactlisttype));
                viewgroup.addView(listview);
                return listview;
            } else
            {
                return null;
            }
        }

        public boolean isViewFromObject(View view, Object obj)
        {
            return view == obj;
        }

        public AvatarPickerPagerAdapter(Context context1)
        {
            this$0 = AvatarPickerFragment.this;
            super();
            context = context1;
        }
    }

    private static final class ContactListType extends Enum
    {

        private static final ContactListType $VALUES[];
        public static final ContactListType Friends;
        public static final ContactListType Nearby;
        public static final ContactListType Recent;
        public final int drawableId;

        public static ContactListType valueOf(String s)
        {
            return (ContactListType)Enum.valueOf(com/lumiyaviewer/lumiya/ui/avapicker/AvatarPickerFragment$ContactListType, s);
        }

        public static ContactListType[] values()
        {
            return $VALUES;
        }

        static 
        {
            Recent = new ContactListType("Recent", 0, 0x7f02007d);
            Friends = new ContactListType("Friends", 1, 0x7f02007e);
            Nearby = new ContactListType("Nearby", 2, 0x7f020083);
            $VALUES = (new ContactListType[] {
                Recent, Friends, Nearby
            });
        }

        private ContactListType(String s, int i, int j)
        {
            super(s, i);
            drawableId = j;
        }
    }

    private static class UsersOnlyPredicate
        implements Predicate
    {

        public boolean apply(ChatterDisplayData chatterdisplaydata)
        {
            return chatterdisplaydata != null && (chatterdisplaydata.chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) && chatterdisplaydata.chatterID.isValidUUID();
        }

        public volatile boolean apply(Object obj)
        {
            return apply((ChatterDisplayData)obj);
        }

        private UsersOnlyPredicate()
        {
        }

        UsersOnlyPredicate(UsersOnlyPredicate usersonlypredicate)
        {
            this();
        }
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_avapicker_2D_AvatarPickerFragment$ContactListTypeSwitchesValues[];

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_avapicker_2D_AvatarPickerFragment$ContactListTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_avapicker_2D_AvatarPickerFragment$ContactListTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_avapicker_2D_AvatarPickerFragment$ContactListTypeSwitchesValues;
        }
        int ai[] = new int[ContactListType.values().length];
        try
        {
            ai[ContactListType.Friends.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[ContactListType.Nearby.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[ContactListType.Recent.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_avapicker_2D_AvatarPickerFragment$ContactListTypeSwitchesValues = ai;
        return ai;
    }

    static ListAdapter _2D_wrap0(AvatarPickerFragment avatarpickerfragment, Context context, UserManager usermanager, ContactListType contactlisttype)
    {
        return avatarpickerfragment.createListAdapter(context, usermanager, contactlisttype);
    }

    public AvatarPickerFragment()
    {
    }

    private ListAdapter createListAdapter(Context context, UserManager usermanager, ContactListType contactlisttype)
    {
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_avapicker_2D_AvatarPickerFragment$ContactListTypeSwitchesValues()[contactlisttype.ordinal()])
        {
        default:
            throw new IllegalArgumentException("Unknown contact list type");

        case 3: // '\003'
            return new ChatterListSubscriptionAdapter(context, usermanager, ChatterListType.Active, new UsersOnlyPredicate(null));

        case 1: // '\001'
            return new ChatterListSubscriptionAdapter(context, usermanager, ChatterListType.Friends);

        case 2: // '\002'
            return new ChatterListSubscriptionAdapter(context, usermanager, ChatterListType.Nearby);
        }
    }

    protected void createExtraView(LayoutInflater layoutinflater, FrameLayout framelayout)
    {
    }

    public abstract String getTitle();

    protected abstract void onAvatarSelected(ChatterID chatterid, String s);

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        viewgroup = layoutinflater.inflate(0x7f04001f, viewgroup, false);
        bundle = (ViewPager)viewgroup.findViewById(0x7f1000f3);
        bundle.setAdapter(new AvatarPickerPagerAdapter(layoutinflater.getContext()));
        ((PagerSlidingTabStrip)viewgroup.findViewById(0x7f1000f2)).setViewPager(bundle);
        createExtraView(layoutinflater, (FrameLayout)viewgroup.findViewById(0x7f1000f4));
        return viewgroup;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        adapterview = ((AdapterView) (adapterview.getItemAtPosition(i)));
        if (adapterview instanceof ChatterDisplayInfo)
        {
            view = ((ChatterDisplayInfo)adapterview).getChatterID(ActivityUtils.getUserManager(getArguments()));
            if (view != null)
            {
                onAvatarSelected(view, ((ChatterDisplayInfo)adapterview).getDisplayName());
            }
        }
    }
}
