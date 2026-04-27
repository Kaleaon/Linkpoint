// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.groups.SLGroupManager;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;

public class GroupNoticeFragment extends ChatterFragment
{

    private static final String ATTACHED_ENTRY_KEY = "attachedEntry";
    private static final int ITEM_FOR_ATTACH_REQUEST = 1;
    private SLInventoryEntry attachedEntry;
    Button groupNoticeAttachmentButton;
    TextView groupNoticeAttachmentText;
    EditText groupNoticeEditText;
    EditText groupNoticeSubject;
    private Unbinder unbinder;

    public GroupNoticeFragment()
    {
        attachedEntry = null;
        unbinder = null;
    }

    private void updateAttachedEntry()
    {
label0:
        {
            Debug.Printf("GroupNotice: current attached entry %s", new Object[] {
                attachedEntry
            });
            if (unbinder != null)
            {
                if (attachedEntry != null)
                {
                    break label0;
                }
                groupNoticeAttachmentText.setText(0x7f090148);
                groupNoticeAttachmentButton.setText(0x7f090146);
            }
            return;
        }
        groupNoticeAttachmentText.setText(attachedEntry.name);
        groupNoticeAttachmentButton.setText(0x7f090149);
    }

    protected String decorateFragmentTitle(String s)
    {
        return getString(0x7f09014d, new Object[] {
            s
        });
    }

    public void onActivityResult(int i, int j, Intent intent)
    {
        i;
        JVM INSTR tableswitch 1 1: default 20
    //                   1 28;
           goto _L1 _L2
_L1:
        super.onActivityResult(i, j, intent);
_L4:
        return;
_L2:
        if (j == -1 && intent.hasExtra("selectedInventoryEntry"))
        {
            attachedEntry = (SLInventoryEntry)intent.getParcelableExtra("selectedInventoryEntry");
            Debug.Printf("GroupNotice: new attached entry %s", new Object[] {
                attachedEntry
            });
            updateAttachedEntry();
            return;
        }
        if (true) goto _L4; else goto _L3
_L3:
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        layoutinflater = layoutinflater.inflate(0x7f040045, viewgroup, false);
        unbinder = ButterKnife.bind(this, layoutinflater);
        if (bundle != null)
        {
            if (bundle.containsKey("attachedEntry"))
            {
                attachedEntry = (SLInventoryEntry)bundle.getParcelable("attachedEntry");
                Debug.Printf("GroupNotice: restored state attached entry %s", new Object[] {
                    attachedEntry
                });
            } else
            {
                Debug.Printf("GroupNotice: restored state no entry", new Object[0]);
            }
        }
        updateAttachedEntry();
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

    public void onGroupNoticeAttachmentButton()
    {
        Debug.Printf("GroupNotice: current attached entry %s", new Object[] {
            attachedEntry
        });
        if (attachedEntry == null)
        {
            if (userManager != null)
            {
                startActivityForResult(InventoryActivity.makeSelectIntent(getContext(), userManager.getUserID()), 1);
            }
            return;
        } else
        {
            attachedEntry = null;
            updateAttachedEntry();
            return;
        }
    }

    public void onGroupNoticeSendButton()
    {
        if (userManager != null && (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup))
        {
            Object obj = userManager.getActiveAgentCircuit();
            if (obj != null)
            {
                ((SLAgentCircuit) (obj)).getModules().groupManager.SendGroupNotice(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterID).getChatterUUID(), groupNoticeSubject.getText().toString(), groupNoticeEditText.getText().toString(), attachedEntry);
                obj = getActivity();
                if (obj instanceof DetailsActivity)
                {
                    ((DetailsActivity)obj).closeDetailsFragment(this);
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        Debug.Printf("GroupNotice: saved state attached entry %s", new Object[] {
            attachedEntry
        });
        if (bundle != null)
        {
            bundle.putParcelable("attachedEntry", attachedEntry);
        }
        super.onSaveInstanceState(bundle);
    }

    protected void onShowUser(ChatterID chatterid)
    {
    }
}
