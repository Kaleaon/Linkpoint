package com.lumiyaviewer.lumiya.ui.objpopup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatLayoutManager;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class ObjectPopupsFragment extends Fragment {
    private static final String AGENT_UUID_KEY = "agentUUID";
    private final ItemTouchHelper.Callback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, 12) { // from class: com.lumiyaviewer.lumiya.ui.objpopup.ObjectPopupsFragment.1
        @Override // android.support.v7.widget.helper.ItemTouchHelper.Callback
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            return false;
        }

        @Override // android.support.v7.widget.helper.ItemTouchHelper.Callback
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
            RecyclerView recyclerView;
            RecyclerView.Adapter adapter;
            UserManager userManager = ObjectPopupsFragment.this.getUserManager();
            View view = ObjectPopupsFragment.this.getView();
            if (view == null || userManager == null || (recyclerView = (RecyclerView) view.findViewById(R.id.objectPopupsList)) == null || (adapter = recyclerView.getAdapter()) == null) {
                return;
            }
            int adapterPosition = viewHolder.getAdapterPosition();
            if (adapter instanceof ObjectPopupsAdapter) {
                userManager.getObjectPopupsManager().cancelObjectPopup(((ObjectPopupsAdapter) adapter).getObject(adapterPosition));
            }
        }
    };

    public static ObjectPopupsFragment create(@Nonnull UUID uuid) {
        ObjectPopupsFragment objectPopupsFragment = new ObjectPopupsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AGENT_UUID_KEY, uuid.toString());
        objectPopupsFragment.setArguments(bundle);
        return objectPopupsFragment;
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Nullable
    public UserManager getUserManager() {
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey(AGENT_UUID_KEY)) {
            return null;
        }
        return UserManager.getUserManager(UUID.fromString(arguments.getString(AGENT_UUID_KEY)));
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.object_popups_fragment_layout, viewGroup, false);
        RecyclerView recyclerView = (RecyclerView) inflate.findViewById(R.id.objectPopupsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new ChatLayoutManager(layoutInflater.getContext(), 1, false));
        new ItemTouchHelper(this.itemTouchCallback).attachToRecyclerView(recyclerView);
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        RecyclerView recyclerView;
        super.onStart();
        UserManager userManager = getUserManager();
        View view = getView();
        if (userManager == null || view == null || (recyclerView = (RecyclerView) view.findViewById(R.id.objectPopupsList)) == null) {
            return;
        }
        recyclerView.setAdapter(new ObjectPopupsAdapter(getContext(), userManager.getObjectPopupsManager().getObjectPopups(), userManager));
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        RecyclerView recyclerView;
        View view = getView();
        if (view != null && (recyclerView = (RecyclerView) view.findViewById(R.id.objectPopupsList)) != null) {
            recyclerView.setAdapter(null);
        }
        super.onStop();
    }
}
