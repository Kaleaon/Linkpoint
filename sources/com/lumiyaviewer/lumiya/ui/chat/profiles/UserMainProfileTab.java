package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.ClipData;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.ClipboardManager;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;

public class UserMainProfileTab extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener {
    @BindView(2131755706)
    Button aboutEditButton;
    private final SubscriptionData<UUID, AvatarNotesReply> avatarNotes = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, AvatarPropertiesReply> avatarProperties = new SubscriptionData<>(UIThreadExecutor.getInstance());
    @BindView(2131755698)
    Button changePicButton;
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.avatarProperties, this.avatarNotes, this.onlineStatus).withDataChangedListener(this);
    @BindView(2131755197)
    LoadingLayout loadingLayout;
    private final ChatterNameRetriever.OnChatterNameUpdated onPartnerNameReady = new $Lambda$wqoLfTfjESd1OUBLJEQMKRim4S0(this);
    private final SubscriptionData<UUID, Boolean> onlineStatus = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private ChatterNameRetriever partnerNameRetriever = null;
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

    private String getAge(AvatarPropertiesReply avatarPropertiesReply) {
        String trim = SLMessage.stringFromVariableOEM(avatarPropertiesReply.PropertiesData_Field.BornOn).trim();
        if (trim.equals("")) {
            return trim;
        }
        String format = String.format(getString(R.string.born_since), new Object[]{trim});
        try {
            Date parse = new SimpleDateFormat("MM/dd/yyyy").parse(trim);
            return String.format(getString(R.string.age_days), new Object[]{Long.valueOf((new Date().getTime() - parse.getTime()) / 86400000)});
        } catch (ParseException e) {
            return format;
        }
    }

    /* synthetic */ void updatePartnerProfile(ChatterNameRetriever chatterNameRetriever) {
        if (getView() != null) {
            this.userProfilePartnerName.setText(chatterNameRetriever.getResolvedName());
            this.userProfilePartnerPic.setChatterID(chatterNameRetriever.chatterID, chatterNameRetriever.getResolvedName());
        }
    }

    /* access modifiers changed from: protected */
    @OnClick({2131755706})
    public void onAboutEditClicked(View view) {
        if (this.chatterID != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserAboutTextEditFragment.class, UserAboutTextEditFragment.makeSelection(this.chatterID, false));
        }
    }

    /* access modifiers changed from: protected */
    @OnClick({2131755698})
    public void onChangePicClicked(View view) {
        AvatarPropertiesReply data = this.avatarProperties.getData();
        if (this.chatterID != null && data != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("oldProfileData", data);
            getContext().startActivity(InventoryActivity.makeSelectActionIntent(getContext(), this.chatterID.agentUUID, InventoryActivity.SelectAction.applyUserProfile, bundle, SLAssetType.AT_TEXTURE));
        }
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        super.onChatterNameUpdated(chatterNameRetriever);
        View view = getView();
        if (this.chatterID != null && Objects.equal(chatterNameRetriever.chatterID, this.chatterID) && view != null) {
            this.textProfilePrimaryName.setText(chatterNameRetriever.getResolvedName());
            this.textProfileSecondaryName.setText(chatterNameRetriever.getResolvedSecondaryName());
        }
    }

    /* access modifiers changed from: protected */
    @OnClick({2131755720})
    public void onCopyAgentKeyClicked(View view) {
        if (this.chatterID instanceof ChatterID.ChatterIDUser) {
            String uuid = ((ChatterID.ChatterIDUser) this.chatterID).getChatterUUID().toString();
            if (Build.VERSION.SDK_INT < 11) {
                ((ClipboardManager) getActivity().getSystemService("clipboard")).setText(uuid);
            } else {
                ((android.content.ClipboardManager) getActivity().getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("Agent key", uuid));
            }
            Toast.makeText(getActivity(), "Agent key copied to clipboard", 0).show();
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.user_profile_tab_main, viewGroup, false);
        this.unbinder = ButterKnife.bind((Object) this, inflate);
        this.userPicView.setAlignTop(true);
        this.userPicView.setVerticalFit(true);
        this.loadingLayout.setSwipeRefreshLayout(this.swipeRefreshLayout);
        this.loadableMonitor.setLoadingLayout(this.loadingLayout, getString(R.string.no_user_selected), getString(R.string.user_profile_fail));
        this.loadableMonitor.setSwipeRefreshLayout(this.swipeRefreshLayout);
        return inflate;
    }

    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }

    /* access modifiers changed from: protected */
    @OnClick({2131755724})
    public void onEditNotesClicked(View view) {
        if (this.chatterID != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserNotesEditFragment.class, UserNotesEditFragment.makeSelection(this.chatterID));
        }
    }

    public void onLoadableDataChanged() {
        if (getView() != null) {
            try {
                AvatarPropertiesReply avatarPropertiesReply = this.avatarProperties.get();
                this.userPicView.setAssetID(avatarPropertiesReply.PropertiesData_Field.ImageID);
                this.userProfileAboutText.setText(SLMessage.stringFromVariableUTF(avatarPropertiesReply.PropertiesData_Field.AboutText));
                this.textProfileAge.setText(getAge(avatarPropertiesReply));
                if (this.partnerNameRetriever != null) {
                    this.partnerNameRetriever.dispose();
                    this.partnerNameRetriever = null;
                }
                UUID uuid = avatarPropertiesReply.PropertiesData_Field.PartnerID;
                if (uuid == null || !(!uuid.equals(UUIDPool.ZeroUUID)) || this.chatterID == null) {
                    this.userPartnerCardView.setVisibility(8);
                    this.userProfilePartnerPic.setChatterID((ChatterID) null, (String) null);
                } else {
                    ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(this.chatterID.agentUUID, uuid);
                    this.userPartnerCardView.setVisibility(0);
                    this.partnerNameRetriever = new ChatterNameRetriever(userChatterID, this.onPartnerNameReady, UIThreadExecutor.getInstance());
                }
                String trim = SLMessage.stringFromVariableOEM(avatarPropertiesReply.PropertiesData_Field.ProfileURL).trim();
                if (!trim.isEmpty()) {
                    this.userWebProfileLink.setText(trim);
                    Linkify.addLinks(this.userWebProfileLink, 15);
                    this.userWebProfileCardView.setVisibility(0);
                } else {
                    this.userWebProfileCardView.setVisibility(8);
                }
                String trim2 = SLMessage.stringFromVariableUTF(this.avatarNotes.get().Data_Field.Notes).trim();
                if (trim2.isEmpty()) {
                    this.textProfileNotesText.setText(R.string.user_notes_no_notes);
                    this.textProfileNotesText.setTypeface((Typeface) null, 2);
                    this.userProfileNotesCaption.setVisibility(8);
                } else {
                    this.textProfileNotesText.setText(trim2);
                    this.textProfileNotesText.setTypeface((Typeface) null, 0);
                    this.userProfileNotesCaption.setVisibility(0);
                }
                this.textProfileOnline.setText(getString(this.onlineStatus.get().booleanValue() ? R.string.profile_user_online : R.string.profile_user_offline));
            } catch (SubscriptionData.DataNotReadyException e) {
                Debug.Warning(e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
        int i = 0;
        View view = getView();
        this.loadableMonitor.unsubscribeAll();
        if (this.partnerNameRetriever != null) {
            this.partnerNameRetriever.dispose();
            this.partnerNameRetriever = null;
        }
        if (this.userManager != null && (chatterID instanceof ChatterID.ChatterIDUser)) {
            UUID chatterUUID = ((ChatterID.ChatterIDUser) chatterID).getChatterUUID();
            this.avatarProperties.subscribe(this.userManager.getAvatarProperties().getPool(), chatterUUID);
            this.onlineStatus.subscribe(this.userManager.getChatterList().getFriendManager().getOnlineStatus(), chatterUUID);
            this.avatarNotes.subscribe(this.userManager.getAvatarNotes().getPool(), chatterUUID);
            if (view != null) {
                this.textProfileAgentKey.setText(chatterUUID.toString());
                boolean equals = chatterUUID.equals(this.userManager.getUserID());
                this.aboutEditButton.setVisibility(equals ? 0 : 8);
                Button button = this.changePicButton;
                if (!equals) {
                    i = 8;
                }
                button.setVisibility(i);
            }
        } else if (view != null) {
            this.textProfileAgentKey.setText("");
            this.aboutEditButton.setVisibility(8);
            this.changePicButton.setVisibility(8);
        }
    }

    /* access modifiers changed from: protected */
    @OnClick({2131755715})
    public void onViewProfileClicked(View view) {
        if (this.chatterID != null) {
            try {
                UUID uuid = this.avatarProperties.get().PropertiesData_Field.PartnerID;
                if (uuid != null && (!uuid.equals(UUIDPool.ZeroUUID)) && this.chatterID != null) {
                    DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(this.chatterID.agentUUID, uuid)));
                }
            } catch (SubscriptionData.DataNotReadyException e) {
                Debug.Warning(e);
            }
        }
    }
}
