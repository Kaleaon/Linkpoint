package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;

/* renamed from: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$6hJe-KPqqQcY7xiCxogddm78oYc  reason: invalid class name */
final /* synthetic */ class $Lambda$6hJeKPqqQcY7xiCxogddm78oYc implements Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private final /* synthetic */ Object f269$f0;

    private final /* synthetic */ void $m$0(Object obj) {
        ((ProfileTextFieldEditFragment) this.f269$f0).m507com_lumiyaviewer_lumiya_ui_chat_profiles_ProfileTextFieldEditFragmentmthref0((AvatarPropertiesReply) obj);
    }

    public /* synthetic */ $Lambda$6hJeKPqqQcY7xiCxogddm78oYc(Object obj) {
        this.f269$f0 = obj;
    }

    public final void onData(Object obj) {
        $m$0(obj);
    }
}
