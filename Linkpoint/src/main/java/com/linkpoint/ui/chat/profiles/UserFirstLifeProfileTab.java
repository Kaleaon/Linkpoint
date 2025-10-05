package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import java.util.UUID;
import javax.annotation.Nullable;

public class UserFirstLifeProfileTab extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener {
    @BindView(2131755706)
    Button aboutEditButton;
    private final SubscriptionData<UUID, AvatarPropertiesReply> avatarProperties = new SubscriptionData<>(UIThreadExecutor.getInstance());
    @BindView(2131755698)
    Button changePicButton;
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.avatarProperties).withDataChangedListener(this);
    @BindView(2131755197)
    LoadingLayout loadingLayout;
    @BindView(2131755195)
    SwipeRefreshLayout swipeRefreshLayout;
    private Unbinder unbinder;
    @BindView(2131755702)
    ImageAssetView userPicView;
    @BindView(2131755705)
    TextView userProfileAboutText;
    @BindView(2131755703)
    TextView userProfilePaymentInfo;

    /* access modifiers changed from: protected */
    @OnClick({2131755706})
    public void onAboutEditClicked(View view) {
        if (this.chatterID != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserAboutTextEditFragment.class, UserAboutTextEditFragment.makeSelection(this.chatterID, true));
        }
    }

    /* access modifiers changed from: protected */
    @OnClick({2131755698})
    public void onChangePicClicked(View view) {
        AvatarPropertiesReply data = this.avatarProperties.getData();
        if (this.chatterID != null && data != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("oldProfileData", data);
            getContext().startActivity(InventoryActivity.makeSelectActionIntent(getContext(), this.chatterID.agentUUID, InventoryActivity.SelectAction.applyFirstLife, bundle, SLAssetType.AT_TEXTURE));
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.user_profile_tab_first, viewGroup, false);
        this.unbinder = ButterKnife.bind((Object) this, inflate);
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

    public void onLoadableDataChanged() {
        boolean z = true;
        if (getView() != null) {
            try {
                AvatarPropertiesReply avatarPropertiesReply = this.avatarProperties.get();
                boolean z2 = (avatarPropertiesReply.PropertiesData_Field.Flags & 8) != 0;
                if ((avatarPropertiesReply.PropertiesData_Field.Flags & 4) == 0) {
                    z = false;
                }
                this.userPicView.setAssetID(avatarPropertiesReply.PropertiesData_Field.FLImageID);
                this.userProfileAboutText.setText(SLMessage.stringFromVariableUTF(avatarPropertiesReply.PropertiesData_Field.FLAboutText));
                this.userProfilePaymentInfo.setText(z2 ? R.string.payment_info_used : z ? R.string.payment_info_identified : R.string.payment_info_none);
            } catch (SubscriptionData.DataNotReadyException e) {
                Debug.Warning(e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
        int i = 0;
        this.loadableMonitor.unsubscribeAll();
        if (this.userManager != null && (chatterID instanceof ChatterID.ChatterIDUser)) {
            UUID chatterUUID = ((ChatterID.ChatterIDUser) chatterID).getChatterUUID();
            this.avatarProperties.subscribe(this.userManager.getAvatarProperties().getPool(), chatterUUID);
            if (this.unbinder != null) {
                boolean equals = chatterUUID.equals(this.userManager.getUserID());
                this.aboutEditButton.setVisibility(equals ? 0 : 8);
                Button button = this.changePicButton;
                if (!equals) {
                    i = 8;
                }
                button.setVisibility(i);
            }
        }
    }
}
