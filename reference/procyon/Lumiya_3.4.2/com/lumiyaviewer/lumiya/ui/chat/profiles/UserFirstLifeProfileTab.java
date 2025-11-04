// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import butterknife.ButterKnife;
import javax.annotation.Nullable;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import android.os.Parcelable;
import android.os.Bundle;
import butterknife.OnClick;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.view.View;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import butterknife.Unbinder;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import butterknife.BindView;
import android.widget.Button;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;

public class UserFirstLifeProfileTab extends ChatterReloadableFragment implements OnLoadableDataChangedListener
{
    @BindView(2131755706)
    Button aboutEditButton;
    private final SubscriptionData<UUID, AvatarPropertiesReply> avatarProperties;
    @BindView(2131755698)
    Button changePicButton;
    private final LoadableMonitor loadableMonitor;
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
    
    public UserFirstLifeProfileTab() {
        this.avatarProperties = new SubscriptionData<UUID, AvatarPropertiesReply>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.avatarProperties }).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
    }
    
    @OnClick({ 2131755706 })
    protected void onAboutEditClicked(final View view) {
        if (this.chatterID != null) {
            DetailsActivity.showEmbeddedDetails(this.getActivity(), UserAboutTextEditFragment.class, UserAboutTextEditFragment.makeSelection(this.chatterID, true));
        }
    }
    
    @OnClick({ 2131755698 })
    protected void onChangePicClicked(final View view) {
        final AvatarPropertiesReply avatarPropertiesReply = this.avatarProperties.getData();
        if (this.chatterID != null && avatarPropertiesReply != null) {
            final Bundle bundle = new Bundle();
            bundle.putParcelable("oldProfileData", (Parcelable)avatarPropertiesReply);
            this.getContext().startActivity(InventoryActivity.makeSelectActionIntent(this.getContext(), this.chatterID.agentUUID, InventoryActivity.SelectAction.applyFirstLife, bundle, SLAssetType.AT_TEXTURE));
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968758, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
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
    
    @Override
    public void onLoadableDataChanged() {
        boolean b = true;
        if (this.getView() == null) {
            return;
        }
        try {
            final AvatarPropertiesReply avatarPropertiesReply = this.avatarProperties.get();
            int n;
            if ((avatarPropertiesReply.PropertiesData_Field.Flags & 0x8) != 0x0) {
                n = 1;
            }
            else {
                n = 0;
            }
            if ((avatarPropertiesReply.PropertiesData_Field.Flags & 0x4) == 0x0) {
                b = false;
            }
            this.userPicView.setAssetID(avatarPropertiesReply.PropertiesData_Field.FLImageID);
            this.userProfileAboutText.setText((CharSequence)SLMessage.stringFromVariableUTF(avatarPropertiesReply.PropertiesData_Field.FLAboutText));
            final TextView userProfilePaymentInfo = this.userProfilePaymentInfo;
            int text;
            if (n != 0) {
                text = 2131296875;
            }
            else if (b) {
                text = 2131296872;
            }
            else {
                text = 2131296873;
            }
            userProfilePaymentInfo.setText(text);
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        final int n = 0;
        this.loadableMonitor.unsubscribeAll();
        if (this.userManager != null && chatterID instanceof ChatterID.ChatterIDUser) {
            final UUID chatterUUID = ((ChatterID.ChatterIDUser)chatterID).getChatterUUID();
            this.avatarProperties.subscribe(this.userManager.getAvatarProperties().getPool(), chatterUUID);
            if (this.unbinder != null) {
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
    }
}
