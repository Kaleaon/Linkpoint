// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.Debug;
import android.graphics.Typeface;
import android.text.util.Linkify;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import butterknife.ButterKnife;
import javax.annotation.Nullable;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.Toast;
import android.content.ClipData;
import android.text.ClipboardManager;
import android.os.Build$VERSION;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import android.os.Parcelable;
import android.os.Bundle;
import butterknife.OnClick;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import android.view.View;
import butterknife.Unbinder;
import android.widget.TextView;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesReply;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import butterknife.BindView;
import android.widget.Button;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;

public class UserMainProfileTab extends ChatterReloadableFragment implements OnLoadableDataChangedListener
{
    @BindView(2131755706)
    Button aboutEditButton;
    private final SubscriptionData<UUID, AvatarNotesReply> avatarNotes;
    private final SubscriptionData<UUID, AvatarPropertiesReply> avatarProperties;
    @BindView(2131755698)
    Button changePicButton;
    private final LoadableMonitor loadableMonitor;
    @BindView(2131755197)
    LoadingLayout loadingLayout;
    private final OnChatterNameUpdated onPartnerNameReady;
    private final SubscriptionData<UUID, Boolean> onlineStatus;
    private ChatterNameRetriever partnerNameRetriever;
    @BindView(2131755195)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(2131755711)
    TextView textProfileAge;
    @BindView(2131755719)
    TextView textProfileAgentKey;
    @BindView(2131755723)
    TextView textProfileNotesText;
    @BindView(2131755710)
    TextView textProfileOnline;
    @BindView(2131755708)
    TextView textProfilePrimaryName;
    @BindView(2131755709)
    TextView textProfileSecondaryName;
    private Unbinder unbinder;
    @BindView(2131755712)
    View userPartnerCardView;
    @BindView(2131755702)
    ImageAssetView userPicView;
    @BindView(2131755705)
    TextView userProfileAboutText;
    @BindView(2131755722)
    View userProfileNotesCaption;
    @BindView(2131755713)
    TextView userProfilePartnerName;
    @BindView(2131755714)
    ChatterPicView userProfilePartnerPic;
    @BindView(2131755716)
    View userWebProfileCardView;
    @BindView(2131755717)
    TextView userWebProfileLink;
    
    public UserMainProfileTab() {
        this.avatarProperties = new SubscriptionData<UUID, AvatarPropertiesReply>(UIThreadExecutor.getInstance());
        this.avatarNotes = new SubscriptionData<UUID, AvatarNotesReply>(UIThreadExecutor.getInstance());
        this.onlineStatus = new SubscriptionData<UUID, Boolean>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.avatarProperties, this.avatarNotes, this.onlineStatus }).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
        this.partnerNameRetriever = null;
        this.onPartnerNameReady = new _$Lambda$wqoLfTfjESd1OUBLJEQMKRim4S0(this);
    }
    
    private String getAge(AvatarPropertiesReply avatarPropertiesReply) {
        final Object trim = SLMessage.stringFromVariableOEM(avatarPropertiesReply.PropertiesData_Field.BornOn).trim();
        if (((String)trim).equals("")) {
            return (String)trim;
        }
        avatarPropertiesReply = (AvatarPropertiesReply)String.format(this.getString(2131296390), trim);
        try {
            avatarPropertiesReply = (AvatarPropertiesReply)String.format(this.getString(2131296322), (new Date().getTime() - new SimpleDateFormat("MM/dd/yyyy").parse((String)trim).getTime()) / 86400000L);
            return (String)avatarPropertiesReply;
        }
        catch (final ParseException ex) {
            return (String)avatarPropertiesReply;
        }
        avatarPropertiesReply = (AvatarPropertiesReply)trim;
        return (String)avatarPropertiesReply;
    }
    
    @OnClick({ 2131755706 })
    protected void onAboutEditClicked(final View view) {
        if (this.chatterID != null) {
            DetailsActivity.showEmbeddedDetails(this.getActivity(), UserAboutTextEditFragment.class, UserAboutTextEditFragment.makeSelection(this.chatterID, false));
        }
    }
    
    @OnClick({ 2131755698 })
    protected void onChangePicClicked(final View view) {
        final AvatarPropertiesReply avatarPropertiesReply = this.avatarProperties.getData();
        if (this.chatterID != null && avatarPropertiesReply != null) {
            final Bundle bundle = new Bundle();
            bundle.putParcelable("oldProfileData", (Parcelable)avatarPropertiesReply);
            this.getContext().startActivity(InventoryActivity.makeSelectActionIntent(this.getContext(), this.chatterID.agentUUID, InventoryActivity.SelectAction.applyUserProfile, bundle, SLAssetType.AT_TEXTURE));
        }
    }
    
    @Override
    public void onChatterNameUpdated(final ChatterNameRetriever chatterNameRetriever) {
        super.onChatterNameUpdated(chatterNameRetriever);
        final View view = this.getView();
        if (this.chatterID != null && Objects.equal(chatterNameRetriever.chatterID, this.chatterID) && view != null) {
            this.textProfilePrimaryName.setText((CharSequence)chatterNameRetriever.getResolvedName());
            this.textProfileSecondaryName.setText((CharSequence)chatterNameRetriever.getResolvedSecondaryName());
        }
    }
    
    @OnClick({ 2131755720 })
    protected void onCopyAgentKeyClicked(final View view) {
        if (this.chatterID instanceof ChatterID.ChatterIDUser) {
            final String string = ((ChatterID.ChatterIDUser)this.chatterID).getChatterUUID().toString();
            if (Build$VERSION.SDK_INT < 11) {
                ((ClipboardManager)this.getActivity().getSystemService("clipboard")).setText((CharSequence)string);
            }
            else {
                ((android.content.ClipboardManager)this.getActivity().getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText((CharSequence)"Agent key", (CharSequence)string));
            }
            Toast.makeText((Context)this.getActivity(), (CharSequence)"Agent key copied to clipboard", 0).show();
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968760, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.userPicView.setAlignTop(true);
        this.userPicView.setVerticalFit(true);
        this.loadingLayout.setSwipeRefreshLayout(this.swipeRefreshLayout);
        this.loadableMonitor.setLoadingLayout(this.loadingLayout, this.getString(2131296753), this.getString(2131297140));
        this.loadableMonitor.setSwipeRefreshLayout(this.swipeRefreshLayout);
        return inflate;
    }
    
    @Override
    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }
    
    @OnClick({ 2131755724 })
    protected void onEditNotesClicked(final View view) {
        if (this.chatterID != null) {
            DetailsActivity.showEmbeddedDetails(this.getActivity(), UserNotesEditFragment.class, ChatterFragment.makeSelection(this.chatterID));
        }
    }
    
    @Override
    public void onLoadableDataChanged() {
        if (this.getView() == null) {
            return;
        }
        AvatarPropertiesReply avatarPropertiesReply;
        UUID partnerID;
        ChatterID.ChatterIDUser userChatterID;
        String trim;
        String trim2 = null;
        TextView textProfileOnline;
        int n;
        Label_0257_Outer:Label_0283_Outer:
        while (true) {
            while (true) {
            Label_0363:
                while (true) {
                Label_0335:
                    while (true) {
                        try {
                            avatarPropertiesReply = this.avatarProperties.get();
                            this.userPicView.setAssetID(avatarPropertiesReply.PropertiesData_Field.ImageID);
                            this.userProfileAboutText.setText((CharSequence)SLMessage.stringFromVariableUTF(avatarPropertiesReply.PropertiesData_Field.AboutText));
                            this.textProfileAge.setText((CharSequence)this.getAge(avatarPropertiesReply));
                            if (this.partnerNameRetriever != null) {
                                this.partnerNameRetriever.dispose();
                                this.partnerNameRetriever = null;
                            }
                            partnerID = avatarPropertiesReply.PropertiesData_Field.PartnerID;
                            if (partnerID != null && (partnerID.equals(UUIDPool.ZeroUUID) ^ true) && this.chatterID != null) {
                                userChatterID = ChatterID.getUserChatterID(this.chatterID.agentUUID, partnerID);
                                this.userPartnerCardView.setVisibility(0);
                                this.partnerNameRetriever = new ChatterNameRetriever(userChatterID, this.onPartnerNameReady, UIThreadExecutor.getInstance());
                            }
                            else {
                                this.userPartnerCardView.setVisibility(8);
                                this.userProfilePartnerPic.setChatterID(null, null);
                            }
                            trim = SLMessage.stringFromVariableOEM(avatarPropertiesReply.PropertiesData_Field.ProfileURL).trim();
                            if (!trim.isEmpty()) {
                                this.userWebProfileLink.setText((CharSequence)trim);
                                Linkify.addLinks(this.userWebProfileLink, 15);
                                this.userWebProfileCardView.setVisibility(0);
                                trim2 = SLMessage.stringFromVariableUTF(this.avatarNotes.get().Data_Field.Notes).trim();
                                if (!trim2.isEmpty()) {
                                    break Label_0335;
                                }
                                this.textProfileNotesText.setText(2131297131);
                                this.textProfileNotesText.setTypeface((Typeface)null, 2);
                                this.userProfileNotesCaption.setVisibility(8);
                                textProfileOnline = this.textProfileOnline;
                                if (this.onlineStatus.get()) {
                                    n = 2131296924;
                                    textProfileOnline.setText((CharSequence)this.getString(n));
                                    return;
                                }
                                break Label_0363;
                            }
                        }
                        catch (final SubscriptionData.DataNotReadyException ex) {
                            Debug.Warning(ex);
                            return;
                        }
                        this.userWebProfileCardView.setVisibility(8);
                        continue Label_0257_Outer;
                    }
                    this.textProfileNotesText.setText((CharSequence)trim2);
                    this.textProfileNotesText.setTypeface((Typeface)null, 0);
                    this.userProfileNotesCaption.setVisibility(0);
                    continue Label_0283_Outer;
                }
                n = 2131296923;
                continue;
            }
        }
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        final int n = 0;
        final View view = this.getView();
        this.loadableMonitor.unsubscribeAll();
        if (this.partnerNameRetriever != null) {
            this.partnerNameRetriever.dispose();
            this.partnerNameRetriever = null;
        }
        if (this.userManager != null && chatterID instanceof ChatterID.ChatterIDUser) {
            final UUID chatterUUID = ((ChatterID.ChatterIDUser)chatterID).getChatterUUID();
            this.avatarProperties.subscribe(this.userManager.getAvatarProperties().getPool(), chatterUUID);
            this.onlineStatus.subscribe(this.userManager.getChatterList().getFriendManager().getOnlineStatus(), chatterUUID);
            this.avatarNotes.subscribe(this.userManager.getAvatarNotes().getPool(), chatterUUID);
            if (view != null) {
                this.textProfileAgentKey.setText((CharSequence)chatterUUID.toString());
                final boolean equals = chatterUUID.equals(this.userManager.getUserID());
                final Button aboutEditButton = this.aboutEditButton;
                int visibility;
                if (equals) {
                    visibility = 0;
                }
                else {
                    visibility = 8;
                }
                aboutEditButton.setVisibility(visibility);
                final Button changePicButton = this.changePicButton;
                int visibility2;
                if (equals) {
                    visibility2 = n;
                }
                else {
                    visibility2 = 8;
                }
                changePicButton.setVisibility(visibility2);
            }
        }
        else if (view != null) {
            this.textProfileAgentKey.setText((CharSequence)"");
            this.aboutEditButton.setVisibility(8);
            this.changePicButton.setVisibility(8);
        }
    }
    
    @OnClick({ 2131755715 })
    protected void onViewProfileClicked(final View view) {
        if (this.chatterID == null) {
            return;
        }
        try {
            final UUID partnerID = this.avatarProperties.get().PropertiesData_Field.PartnerID;
            if (partnerID != null && (partnerID.equals(UUIDPool.ZeroUUID) ^ true) && this.chatterID != null) {
                DetailsActivity.showEmbeddedDetails(this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(this.chatterID.agentUUID, partnerID)));
            }
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
}
