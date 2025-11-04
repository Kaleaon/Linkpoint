package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.ClipData;
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

/* loaded from: classes.dex */
public class UserMainProfileTab extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener {

    @BindView(R.id.about_edit_button)
    Button aboutEditButton;

    @BindView(R.id.change_pic_button)
    Button changePicButton;

    @BindView(R.id.loading_layout)
    LoadingLayout loadingLayout;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.text_profile_age)
    TextView textProfileAge;

    @BindView(R.id.text_profile_agent_key)
    TextView textProfileAgentKey;

    @BindView(R.id.text_profile_notes_text)
    TextView textProfileNotesText;

    @BindView(R.id.text_profile_online)
    TextView textProfileOnline;

    @BindView(R.id.text_profile_primary_name)
    TextView textProfilePrimaryName;

    @BindView(R.id.text_profile_secondary_name)
    TextView textProfileSecondaryName;
    private Unbinder unbinder;

    @BindView(R.id.user_partner_card_view)
    View userPartnerCardView;

    @BindView(R.id.user_pic_view)
    ImageAssetView userPicView;

    @BindView(R.id.user_profile_about_text)
    TextView userProfileAboutText;

    @BindView(R.id.user_profile_notes_caption)
    View userProfileNotesCaption;

    @BindView(R.id.user_profile_partner_name)
    TextView userProfilePartnerName;

    @BindView(R.id.user_profile_partner_pic)
    ChatterPicView userProfilePartnerPic;

    @BindView(R.id.user_web_profile_card_view)
    View userWebProfileCardView;

    @BindView(R.id.user_web_profile_link)
    TextView userWebProfileLink;
    private final SubscriptionData<UUID, AvatarPropertiesReply> avatarProperties = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, AvatarNotesReply> avatarNotes = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, Boolean> onlineStatus = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.avatarProperties, this.avatarNotes, this.onlineStatus).withDataChangedListener(this);
    private ChatterNameRetriever partnerNameRetriever = null;
    private final ChatterNameRetriever.OnChatterNameUpdated onPartnerNameReady = new ChatterNameRetriever.OnChatterNameUpdated() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$wqoLfTfjESd1OUBLJEQMKRim4S0
        private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
            ((UserMainProfileTab) this).m519x9d89034f(chatterNameRetriever);
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
        public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
            $m$0(chatterNameRetriever);
        }
    };

    private String getAge(AvatarPropertiesReply avatarPropertiesReply) {
        String trim = SLMessage.stringFromVariableOEM(avatarPropertiesReply.PropertiesData_Field.BornOn).trim();
        if (trim.equals("")) {
            return trim;
        }
        String format = String.format(getString(R.string.born_since), trim);
        try {
            return String.format(getString(R.string.age_days), Long.valueOf((new Date().getTime() - new SimpleDateFormat("MM/dd/yyyy").parse(trim).getTime()) / 86400000));
        } catch (ParseException e) {
            return format;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_UserMainProfileTab_9585, reason: not valid java name */
    public /* synthetic */ void m519x9d89034f(ChatterNameRetriever chatterNameRetriever) {
        if (getView() != null) {
            this.userProfilePartnerName.setText(chatterNameRetriever.getResolvedName());
            this.userProfilePartnerPic.setChatterID(chatterNameRetriever.chatterID, chatterNameRetriever.getResolvedName());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @OnClick({R.id.about_edit_button})
    public void onAboutEditClicked(View view) {
        if (this.chatterID != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserAboutTextEditFragment.class, UserAboutTextEditFragment.makeSelection(this.chatterID, false));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @OnClick({R.id.change_pic_button})
    public void onChangePicClicked(View view) {
        AvatarPropertiesReply data = this.avatarProperties.getData();
        if (this.chatterID == null || data == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable("oldProfileData", data);
        getContext().startActivity(InventoryActivity.makeSelectActionIntent(getContext(), this.chatterID.agentUUID, InventoryActivity.SelectAction.applyUserProfile, bundle, SLAssetType.AT_TEXTURE));
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment, com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        super.onChatterNameUpdated(chatterNameRetriever);
        View view = getView();
        if (this.chatterID == null || !Objects.equal(chatterNameRetriever.chatterID, this.chatterID) || view == null) {
            return;
        }
        this.textProfilePrimaryName.setText(chatterNameRetriever.getResolvedName());
        this.textProfileSecondaryName.setText(chatterNameRetriever.getResolvedSecondaryName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @OnClick({R.id.button_copy_agent_key})
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

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.user_profile_tab_main, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.userPicView.setAlignTop(true);
        this.userPicView.setVerticalFit(true);
        this.loadingLayout.setSwipeRefreshLayout(this.swipeRefreshLayout);
        this.loadableMonitor.setLoadingLayout(this.loadingLayout, getString(R.string.no_user_selected), getString(R.string.user_profile_fail));
        this.loadableMonitor.setSwipeRefreshLayout(this.swipeRefreshLayout);
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @OnClick({R.id.button_edit_notes})
    public void onEditNotesClicked(View view) {
        if (this.chatterID != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserNotesEditFragment.class, UserNotesEditFragment.makeSelection(this.chatterID));
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
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
                    this.userProfilePartnerPic.setChatterID(null, null);
                } else {
                    ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(this.chatterID.agentUUID, uuid);
                    this.userPartnerCardView.setVisibility(0);
                    this.partnerNameRetriever = new ChatterNameRetriever(userChatterID, this.onPartnerNameReady, UIThreadExecutor.getInstance());
                }
                String trim = SLMessage.stringFromVariableOEM(avatarPropertiesReply.PropertiesData_Field.ProfileURL).trim();
                if (trim.isEmpty()) {
                    this.userWebProfileCardView.setVisibility(8);
                } else {
                    this.userWebProfileLink.setText(trim);
                    Linkify.addLinks(this.userWebProfileLink, 15);
                    this.userWebProfileCardView.setVisibility(0);
                }
                String trim2 = SLMessage.stringFromVariableUTF(this.avatarNotes.get().Data_Field.Notes).trim();
                if (trim2.isEmpty()) {
                    this.textProfileNotesText.setText(R.string.user_notes_no_notes);
                    this.textProfileNotesText.setTypeface(null, 2);
                    this.userProfileNotesCaption.setVisibility(8);
                } else {
                    this.textProfileNotesText.setText(trim2);
                    this.textProfileNotesText.setTypeface(null, 0);
                    this.userProfileNotesCaption.setVisibility(0);
                }
                this.textProfileOnline.setText(getString(this.onlineStatus.get().booleanValue() ? R.string.profile_user_online : R.string.profile_user_offline));
            } catch (SubscriptionData.DataNotReadyException e) {
                Debug.Warning(e);
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        View view = getView();
        this.loadableMonitor.unsubscribeAll();
        if (this.partnerNameRetriever != null) {
            this.partnerNameRetriever.dispose();
            this.partnerNameRetriever = null;
        }
        if (this.userManager == null || !(chatterID instanceof ChatterID.ChatterIDUser)) {
            if (view != null) {
                this.textProfileAgentKey.setText("");
                this.aboutEditButton.setVisibility(8);
                this.changePicButton.setVisibility(8);
                return;
            }
            return;
        }
        UUID chatterUUID = ((ChatterID.ChatterIDUser) chatterID).getChatterUUID();
        this.avatarProperties.subscribe(this.userManager.getAvatarProperties().getPool(), chatterUUID);
        this.onlineStatus.subscribe(this.userManager.getChatterList().getFriendManager().getOnlineStatus(), chatterUUID);
        this.avatarNotes.subscribe(this.userManager.getAvatarNotes().getPool(), chatterUUID);
        if (view != null) {
            this.textProfileAgentKey.setText(chatterUUID.toString());
            boolean equals = chatterUUID.equals(this.userManager.getUserID());
            this.aboutEditButton.setVisibility(equals ? 0 : 8);
            this.changePicButton.setVisibility(equals ? 0 : 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @OnClick({R.id.user_profile_view_partner_button})
    public void onViewProfileClicked(View view) {
        if (this.chatterID != null) {
            try {
                UUID uuid = this.avatarProperties.get().PropertiesData_Field.PartnerID;
                if (uuid == null || !(!uuid.equals(UUIDPool.ZeroUUID)) || this.chatterID == null) {
                    return;
                }
                DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(this.chatterID.agentUUID, uuid)));
            } catch (SubscriptionData.DataNotReadyException e) {
                Debug.Warning(e);
            }
        }
    }
}
