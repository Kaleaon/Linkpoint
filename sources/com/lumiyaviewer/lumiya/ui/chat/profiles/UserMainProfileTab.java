// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.ClipboardManager;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.FriendManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserAboutTextEditFragment, UserNotesEditFragment, UserProfileFragment

public class UserMainProfileTab extends ChatterReloadableFragment
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
{

    Button aboutEditButton;
    private final SubscriptionData avatarNotes = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData avatarProperties = new SubscriptionData(UIThreadExecutor.getInstance());
    Button changePicButton;
    private final LoadableMonitor loadableMonitor;
    LoadingLayout loadingLayout;
    private final com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated onPartnerNameReady = new _2D_.Lambda.wqoLfTfjESd1OUBLJEQMKRim4S0(this);
    private final SubscriptionData onlineStatus = new SubscriptionData(UIThreadExecutor.getInstance());
    private ChatterNameRetriever partnerNameRetriever;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView textProfileAge;
    TextView textProfileAgentKey;
    TextView textProfileNotesText;
    TextView textProfileOnline;
    TextView textProfilePrimaryName;
    TextView textProfileSecondaryName;
    private Unbinder unbinder;
    View userPartnerCardView;
    ImageAssetView userPicView;
    TextView userProfileAboutText;
    View userProfileNotesCaption;
    TextView userProfilePartnerName;
    ChatterPicView userProfilePartnerPic;
    View userWebProfileCardView;
    TextView userWebProfileLink;

    public UserMainProfileTab()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            avatarProperties, avatarNotes, onlineStatus
        })).withDataChangedListener(this);
        partnerNameRetriever = null;
    }

    private String getAge(AvatarPropertiesReply avatarpropertiesreply)
    {
        Object obj = SLMessage.stringFromVariableOEM(avatarpropertiesreply.PropertiesData_Field.BornOn).trim();
        if (!((String) (obj)).equals(""))
        {
            avatarpropertiesreply = String.format(getString(0x7f090086), new Object[] {
                obj
            });
            try
            {
                obj = (new SimpleDateFormat("MM/dd/yyyy")).parse(((String) (obj)));
                obj = String.format(getString(0x7f090042), new Object[] {
                    Long.valueOf(((new Date()).getTime() - ((Date) (obj)).getTime()) / 0x5265c00L)
                });
            }
            // Misplaced declaration of an exception variable
            catch (Object obj)
            {
                return avatarpropertiesreply;
            }
            return ((String) (obj));
        } else
        {
            return ((String) (obj));
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserMainProfileTab_9585(ChatterNameRetriever chatternameretriever)
    {
        if (getView() != null)
        {
            userProfilePartnerName.setText(chatternameretriever.getResolvedName());
            userProfilePartnerPic.setChatterID(chatternameretriever.chatterID, chatternameretriever.getResolvedName());
        }
    }

    protected void onAboutEditClicked(View view)
    {
        if (chatterID != null)
        {
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserAboutTextEditFragment, UserAboutTextEditFragment.makeSelection(chatterID, false));
        }
    }

    protected void onChangePicClicked(View view)
    {
        view = (AvatarPropertiesReply)avatarProperties.getData();
        if (chatterID != null && view != null)
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable("oldProfileData", view);
            view = InventoryActivity.makeSelectActionIntent(getContext(), chatterID.agentUUID, com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity.SelectAction.applyUserProfile, bundle, SLAssetType.AT_TEXTURE);
            getContext().startActivity(view);
        }
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatternameretriever)
    {
        super.onChatterNameUpdated(chatternameretriever);
        View view = getView();
        if (chatterID != null && Objects.equal(chatternameretriever.chatterID, chatterID) && view != null)
        {
            textProfilePrimaryName.setText(chatternameretriever.getResolvedName());
            textProfileSecondaryName.setText(chatternameretriever.getResolvedSecondaryName());
        }
    }

    protected void onCopyAgentKeyClicked(View view)
    {
        if (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)
        {
            view = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterID).getChatterUUID().toString();
            if (android.os.Build.VERSION.SDK_INT < 11)
            {
                ((ClipboardManager)getActivity().getSystemService("clipboard")).setText(view);
            } else
            {
                ((android.content.ClipboardManager)getActivity().getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("Agent key", view));
            }
            Toast.makeText(getActivity(), "Agent key copied to clipboard", 0).show();
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        layoutinflater = layoutinflater.inflate(0x7f0400b8, viewgroup, false);
        unbinder = ButterKnife.bind(this, layoutinflater);
        userPicView.setAlignTop(true);
        userPicView.setVerticalFit(true);
        loadingLayout.setSwipeRefreshLayout(swipeRefreshLayout);
        loadableMonitor.setLoadingLayout(loadingLayout, getString(0x7f0901f1), getString(0x7f090374));
        loadableMonitor.setSwipeRefreshLayout(swipeRefreshLayout);
        return layoutinflater;
    }

    public void onDestroyView()
    {
        if (unbinder != null)
        {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroyView();
    }

    protected void onEditNotesClicked(View view)
    {
        if (chatterID != null)
        {
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserNotesEditFragment, UserNotesEditFragment.makeSelection(chatterID));
        }
    }

    public void onLoadableDataChanged()
    {
        if (getView() == null) goto _L2; else goto _L1
_L1:
        Object obj;
        Object obj1;
        obj = (AvatarPropertiesReply)avatarProperties.get();
        userPicView.setAssetID(((AvatarPropertiesReply) (obj)).PropertiesData_Field.ImageID);
        userProfileAboutText.setText(SLMessage.stringFromVariableUTF(((AvatarPropertiesReply) (obj)).PropertiesData_Field.AboutText));
        textProfileAge.setText(getAge(((AvatarPropertiesReply) (obj))));
        if (partnerNameRetriever != null)
        {
            partnerNameRetriever.dispose();
            partnerNameRetriever = null;
        }
        obj1 = ((AvatarPropertiesReply) (obj)).PropertiesData_Field.PartnerID;
        if (obj1 == null) goto _L4; else goto _L3
_L3:
        if (!(((UUID) (obj1)).equals(UUIDPool.ZeroUUID) ^ true) || chatterID == null) goto _L4; else goto _L5
_L5:
        obj1 = ChatterID.getUserChatterID(chatterID.agentUUID, ((UUID) (obj1)));
        userPartnerCardView.setVisibility(0);
        partnerNameRetriever = new ChatterNameRetriever(((ChatterID) (obj1)), onPartnerNameReady, UIThreadExecutor.getInstance());
_L14:
        obj = SLMessage.stringFromVariableOEM(((AvatarPropertiesReply) (obj)).PropertiesData_Field.ProfileURL).trim();
        if (((String) (obj)).isEmpty()) goto _L7; else goto _L6
_L6:
        userWebProfileLink.setText(((CharSequence) (obj)));
        Linkify.addLinks(userWebProfileLink, 15);
        userWebProfileCardView.setVisibility(0);
_L11:
        obj = SLMessage.stringFromVariableUTF(((AvatarNotesReply)avatarNotes.get()).Data_Field.Notes).trim();
        if (!((String) (obj)).isEmpty()) goto _L9; else goto _L8
_L8:
        textProfileNotesText.setText(0x7f09036b);
        textProfileNotesText.setTypeface(null, 2);
        userProfileNotesCaption.setVisibility(8);
_L12:
        obj = textProfileOnline;
          goto _L10
_L4:
        userPartnerCardView.setVisibility(8);
        userProfilePartnerPic.setChatterID(null, null);
        continue; /* Loop/switch isn't completed */
_L10:
        int i;
        if (((Boolean)onlineStatus.get()).booleanValue())
        {
            i = 0x7f09029c;
        } else
        {
            i = 0x7f09029b;
        }
        try
        {
            ((TextView) (obj)).setText(getString(i));
            return;
        }
        // Misplaced declaration of an exception variable
        catch (Object obj)
        {
            Debug.Warning(((Throwable) (obj)));
        }
        return;
_L7:
        userWebProfileCardView.setVisibility(8);
          goto _L11
_L9:
        textProfileNotesText.setText(((CharSequence) (obj)));
        textProfileNotesText.setTypeface(null, 0);
        userProfileNotesCaption.setVisibility(0);
          goto _L12
_L2:
        return;
        if (true) goto _L14; else goto _L13
_L13:
    }

    protected void onShowUser(ChatterID chatterid)
    {
        boolean flag = false;
        View view = getView();
        loadableMonitor.unsubscribeAll();
        if (partnerNameRetriever != null)
        {
            partnerNameRetriever.dispose();
            partnerNameRetriever = null;
        }
        if (userManager != null && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser))
        {
            chatterid = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID();
            avatarProperties.subscribe(userManager.getAvatarProperties().getPool(), chatterid);
            onlineStatus.subscribe(userManager.getChatterList().getFriendManager().getOnlineStatus(), chatterid);
            avatarNotes.subscribe(userManager.getAvatarNotes().getPool(), chatterid);
            if (view != null)
            {
                textProfileAgentKey.setText(chatterid.toString());
                boolean flag1 = chatterid.equals(userManager.getUserID());
                chatterid = aboutEditButton;
                int i;
                if (flag1)
                {
                    i = 0;
                } else
                {
                    i = 8;
                }
                chatterid.setVisibility(i);
                chatterid = changePicButton;
                if (flag1)
                {
                    i = ((flag) ? 1 : 0);
                } else
                {
                    i = 8;
                }
                chatterid.setVisibility(i);
            }
        } else
        if (view != null)
        {
            textProfileAgentKey.setText("");
            aboutEditButton.setVisibility(8);
            changePicButton.setVisibility(8);
            return;
        }
    }

    protected void onViewProfileClicked(View view)
    {
        if (chatterID == null)
        {
            break MISSING_BLOCK_LABEL_74;
        }
        try
        {
            view = ((AvatarPropertiesReply)avatarProperties.get()).PropertiesData_Field.PartnerID;
        }
        // Misplaced declaration of an exception variable
        catch (View view)
        {
            Debug.Warning(view);
            return;
        }
        if (view == null)
        {
            break MISSING_BLOCK_LABEL_74;
        }
        if (view.equals(UUIDPool.ZeroUUID) ^ true && chatterID != null)
        {
            view = ChatterID.getUserChatterID(chatterID.agentUUID, view);
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(view));
        }
    }
}
