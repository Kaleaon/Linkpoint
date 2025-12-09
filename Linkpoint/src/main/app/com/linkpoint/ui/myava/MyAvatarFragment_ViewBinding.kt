package com.linkpoint.ui.myava
import java.util.*

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import android.view.View
import android.widget.ListView
import android.widget.TextView
import butterknife.Unbinder
import butterknife.internal.Utils
import com.linkpoint.R
import com.linkpoint.ui.chat.ChatterPicView

class MyAvatarFragment_ViewBinding : Unbinder {
    private MyAvatarFragment target

    @UiThread
    MyAvatarFragment_ViewBinding(MyAvatarFragment myAvatarFragment, View view) {
        this.target = myAvatarFragment
        myAvatarFragment.myAvatarPic = (Utils as ChatterPicView).findRequiredViewAsType(view, R.id.my_avatar_pic, "field 'myAvatarPic'", ChatterPicView.class)
        myAvatarFragment.myAvatarName = (Utils as TextView).findRequiredViewAsType(view, R.id.my_avatar_name, "field 'myAvatarName'", TextView.class)
        myAvatarFragment.myAvatarOptionsList = (Utils as ListView).findRequiredViewAsType(view, R.id.my_ava_options_list, "field 'myAvatarOptionsList'", ListView.class)
    }

    @CallSuper
    fun unbind(): Unit {
        MyAvatarFragment myAvatarFragment = this.target
        if (myAvatarFragment == null) {
            throw IllegalStateException("Bindings already cleared.")
        }
        this.target = null
        myAvatarFragment.myAvatarPic = null
        myAvatarFragment.myAvatarName = null
        myAvatarFragment.myAvatarOptionsList = null
    }
}
