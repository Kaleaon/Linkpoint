package com.lumiyaviewer.lumiya.slproto.chat.generic;
import java.util.*;

import javax.annotation.Nonnull;

public interface OnChatEventListener {
    void onChatEvent(@Nonnull SLChatEvent sLChatEvent);
}
