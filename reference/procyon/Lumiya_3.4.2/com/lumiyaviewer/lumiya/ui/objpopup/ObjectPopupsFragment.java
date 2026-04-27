// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objpopup;

import com.lumiyaviewer.lumiya.ui.chat.ChatLayoutManager;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import javax.annotation.Nullable;
import android.os.Bundle;
import javax.annotation.Nonnull;
import java.util.UUID;
import android.view.View;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import android.support.v7.widget.RecyclerView;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v4.app.Fragment;

public class ObjectPopupsFragment extends Fragment
{
    private static final String AGENT_UUID_KEY = "agentUUID";
    private final ItemTouchHelper.Callback itemTouchCallback;
    
    public ObjectPopupsFragment() {
        this.itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, 12) {
            @Override
            public boolean onMove(final RecyclerView recyclerView, final ViewHolder viewHolder, final ViewHolder viewHolder2) {
                return false;
            }
            
            @Override
            public void onSwiped(final ViewHolder viewHolder, int adapterPosition) {
                final UserManager -wrap0 = ObjectPopupsFragment.this.getUserManager();
                final View view = ObjectPopupsFragment.this.getView();
                if (view != null && -wrap0 != null) {
                    final RecyclerView recyclerView = (RecyclerView)view.findViewById(2131755583);
                    if (recyclerView != null) {
                        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
                        if (adapter != null) {
                            adapterPosition = viewHolder.getAdapterPosition();
                            if (adapter instanceof ObjectPopupsAdapter) {
                                -wrap0.getObjectPopupsManager().cancelObjectPopup(((ObjectPopupsAdapter)adapter).getObject(adapterPosition));
                            }
                        }
                    }
                }
            }
        };
    }
    
    public static ObjectPopupsFragment create(@Nonnull final UUID uuid) {
        final ObjectPopupsFragment objectPopupsFragment = new ObjectPopupsFragment();
        final Bundle arguments = new Bundle();
        arguments.putString("agentUUID", uuid.toString());
        objectPopupsFragment.setArguments(arguments);
        return objectPopupsFragment;
    }
    
    @Nullable
    private UserManager getUserManager() {
        final Bundle arguments = this.getArguments();
        if (arguments != null && arguments.containsKey("agentUUID")) {
            return UserManager.getUserManager(UUID.fromString(arguments.getString("agentUUID")));
        }
        return null;
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968698, viewGroup, false);
        final RecyclerView recyclerView = (RecyclerView)inflate.findViewById(2131755583);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager((RecyclerView.LayoutManager)new ChatLayoutManager(layoutInflater.getContext(), 1, false));
        new ItemTouchHelper(this.itemTouchCallback).attachToRecyclerView(recyclerView);
        return inflate;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        final UserManager userManager = this.getUserManager();
        final View view = this.getView();
        if (userManager != null && view != null) {
            final RecyclerView recyclerView = (RecyclerView)view.findViewById(2131755583);
            if (recyclerView != null) {
                recyclerView.setAdapter((RecyclerView.Adapter)new ObjectPopupsAdapter(this.getContext(), userManager.getObjectPopupsManager().getObjectPopups(), userManager));
            }
        }
    }
    
    @Override
    public void onStop() {
        final View view = this.getView();
        if (view != null) {
            final RecyclerView recyclerView = (RecyclerView)view.findViewById(2131755583);
            if (recyclerView != null) {
                recyclerView.setAdapter(null);
            }
        }
        super.onStop();
    }
}
