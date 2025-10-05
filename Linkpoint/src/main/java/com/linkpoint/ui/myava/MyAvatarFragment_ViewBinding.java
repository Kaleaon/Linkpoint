package com.lumiyaviewer.lumiya.ui.myava;
import java.util.*;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;

public class MyAvatarFragment_ViewBinding implements Unbinder {
    private MyAvatarFragment target;

    @UiThread
    public MyAvatarFragment_ViewBinding(MyAvatarFragment myAvatarFragment, View view) {
        this.target = myAvatarFragment;
        myAvatarFragment.myAvatarPic = (ChatterPicView) Utils.findRequiredViewAsType(view, R.id.my_avatar_pic, "field 'myAvatarPic'", ChatterPicView.class);
        myAvatarFragment.myAvatarName = (TextView) Utils.findRequiredViewAsType(view, R.id.my_avatar_name, "field 'myAvatarName'", TextView.class);
        myAvatarFragment.myAvatarOptionsList = (ListView) Utils.findRequiredViewAsType(view, R.id.my_ava_options_list, "field 'myAvatarOptionsList'", ListView.class);
    }

    @CallSuper
    public void unbind() {
        MyAvatarFragment myAvatarFragment = this.target;
        if (myAvatarFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        myAvatarFragment.myAvatarPic = null;
        myAvatarFragment.myAvatarName = null;
        myAvatarFragment.myAvatarOptionsList = null;
    }
}
