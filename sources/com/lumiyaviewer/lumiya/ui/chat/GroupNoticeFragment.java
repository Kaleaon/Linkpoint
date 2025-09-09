package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFragment;
import javax.annotation.Nullable;

public class GroupNoticeFragment extends ChatterFragment {
    private static final String ATTACHED_ENTRY_KEY = "attachedEntry";
    private static final int ITEM_FOR_ATTACH_REQUEST = 1;
    private SLInventoryEntry attachedEntry = null;
    @BindView(2131755383)
    Button groupNoticeAttachmentButton;
    @BindView(2131755382)
    TextView groupNoticeAttachmentText;
    @BindView(2131755384)
    EditText groupNoticeEditText;
    @BindView(2131755381)
    EditText groupNoticeSubject;
    private Unbinder unbinder = null;

    private void updateAttachedEntry() {
        Debug.Printf("GroupNotice: current attached entry %s", this.attachedEntry);
        if (this.unbinder == null) {
            return;
        }
        if (this.attachedEntry == null) {
            this.groupNoticeAttachmentText.setText(R.string.group_notice_no_attachment);
            this.groupNoticeAttachmentButton.setText(R.string.group_notice_attach);
            return;
        }
        this.groupNoticeAttachmentText.setText(this.attachedEntry.name);
        this.groupNoticeAttachmentButton.setText(R.string.group_notice_remove_attachment);
    }

    /* access modifiers changed from: protected */
    public String decorateFragmentTitle(String str) {
        return getString(R.string.group_notice_title_format, str);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        switch (i) {
            case 1:
                if (i2 == -1 && intent.hasExtra(InventoryFragment.SELECTED_INVENTORY_ENTRY)) {
                    this.attachedEntry = (SLInventoryEntry) intent.getParcelableExtra(InventoryFragment.SELECTED_INVENTORY_ENTRY);
                    Debug.Printf("GroupNotice: new attached entry %s", this.attachedEntry);
                    updateAttachedEntry();
                    return;
                }
                return;
            default:
                super.onActivityResult(i, i2, intent);
                return;
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.group_notice, viewGroup, false);
        this.unbinder = ButterKnife.bind((Object) this, inflate);
        if (bundle != null) {
            if (bundle.containsKey(ATTACHED_ENTRY_KEY)) {
                this.attachedEntry = (SLInventoryEntry) bundle.getParcelable(ATTACHED_ENTRY_KEY);
                Debug.Printf("GroupNotice: restored state attached entry %s", this.attachedEntry);
            } else {
                Debug.Printf("GroupNotice: restored state no entry", new Object[0]);
            }
        }
        updateAttachedEntry();
        return inflate;
    }

    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }

    @OnClick({2131755383})
    public void onGroupNoticeAttachmentButton() {
        Debug.Printf("GroupNotice: current attached entry %s", this.attachedEntry);
        if (this.attachedEntry != null) {
            this.attachedEntry = null;
            updateAttachedEntry();
        } else if (this.userManager != null) {
            startActivityForResult(InventoryActivity.makeSelectIntent(getContext(), this.userManager.getUserID()), 1);
        }
    }

    @OnClick({2131755385})
    public void onGroupNoticeSendButton() {
        SLAgentCircuit activeAgentCircuit;
        if (this.userManager != null && (this.chatterID instanceof ChatterID.ChatterIDGroup) && (activeAgentCircuit = this.userManager.getActiveAgentCircuit()) != null) {
            activeAgentCircuit.getModules().groupManager.SendGroupNotice(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), this.groupNoticeSubject.getText().toString(), this.groupNoticeEditText.getText().toString(), this.attachedEntry);
            FragmentActivity activity = getActivity();
            if (activity instanceof DetailsActivity) {
                ((DetailsActivity) activity).closeDetailsFragment(this);
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        Debug.Printf("GroupNotice: saved state attached entry %s", this.attachedEntry);
        if (bundle != null) {
            bundle.putParcelable(ATTACHED_ENTRY_KEY, this.attachedEntry);
        }
        super.onSaveInstanceState(bundle);
    }

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
    }
}
