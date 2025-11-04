// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import butterknife.OnClick;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import butterknife.ButterKnife;
import android.view.View;
import android.os.Bundle;
import javax.annotation.Nullable;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Intent;
import com.lumiyaviewer.lumiya.Debug;
import butterknife.Unbinder;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import android.widget.Button;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;

public class GroupNoticeFragment extends ChatterFragment
{
    private static final String ATTACHED_ENTRY_KEY = "attachedEntry";
    private static final int ITEM_FOR_ATTACH_REQUEST = 1;
    private SLInventoryEntry attachedEntry;
    @BindView(2131755383)
    Button groupNoticeAttachmentButton;
    @BindView(2131755382)
    TextView groupNoticeAttachmentText;
    @BindView(2131755384)
    EditText groupNoticeEditText;
    @BindView(2131755381)
    EditText groupNoticeSubject;
    private Unbinder unbinder;
    
    public GroupNoticeFragment() {
        this.attachedEntry = null;
        this.unbinder = null;
    }
    
    private void updateAttachedEntry() {
        Debug.Printf("GroupNotice: current attached entry %s", this.attachedEntry);
        if (this.unbinder != null) {
            if (this.attachedEntry == null) {
                this.groupNoticeAttachmentText.setText(2131296584);
                this.groupNoticeAttachmentButton.setText(2131296582);
            }
            else {
                this.groupNoticeAttachmentText.setText((CharSequence)this.attachedEntry.name);
                this.groupNoticeAttachmentButton.setText(2131296585);
            }
        }
    }
    
    @Override
    protected String decorateFragmentTitle(final String s) {
        return this.getString(2131296589, s);
    }
    
    @Override
    public void onActivityResult(final int n, final int n2, final Intent intent) {
        switch (n) {
            default: {
                super.onActivityResult(n, n2, intent);
                break;
            }
            case 1: {
                if (n2 == -1 && intent.hasExtra("selectedInventoryEntry")) {
                    this.attachedEntry = (SLInventoryEntry)intent.getParcelableExtra("selectedInventoryEntry");
                    Debug.Printf("GroupNotice: new attached entry %s", this.attachedEntry);
                    this.updateAttachedEntry();
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968645, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        if (bundle != null) {
            if (bundle.containsKey("attachedEntry")) {
                this.attachedEntry = (SLInventoryEntry)bundle.getParcelable("attachedEntry");
                Debug.Printf("GroupNotice: restored state attached entry %s", this.attachedEntry);
            }
            else {
                Debug.Printf("GroupNotice: restored state no entry", new Object[0]);
            }
        }
        this.updateAttachedEntry();
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
    
    @OnClick({ 2131755383 })
    public void onGroupNoticeAttachmentButton() {
        Debug.Printf("GroupNotice: current attached entry %s", this.attachedEntry);
        if (this.attachedEntry == null) {
            if (this.userManager != null) {
                this.startActivityForResult(InventoryActivity.makeSelectIntent(this.getContext(), this.userManager.getUserID()), 1);
            }
        }
        else {
            this.attachedEntry = null;
            this.updateAttachedEntry();
        }
    }
    
    @OnClick({ 2131755385 })
    public void onGroupNoticeSendButton() {
        if (this.userManager != null && this.chatterID instanceof ChatterID.ChatterIDGroup) {
            final SLAgentCircuit activeAgentCircuit = this.userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                activeAgentCircuit.getModules().groupManager.SendGroupNotice(((ChatterID.ChatterIDGroup)this.chatterID).getChatterUUID(), this.groupNoticeSubject.getText().toString(), this.groupNoticeEditText.getText().toString(), this.attachedEntry);
                final FragmentActivity activity = this.getActivity();
                if (activity instanceof DetailsActivity) {
                    ((DetailsActivity)activity).closeDetailsFragment(this);
                }
            }
        }
    }
    
    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        Debug.Printf("GroupNotice: saved state attached entry %s", this.attachedEntry);
        if (bundle != null) {
            bundle.putParcelable("attachedEntry", (Parcelable)this.attachedEntry);
        }
        super.onSaveInstanceState(bundle);
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
    }
}
