package com.linkpoint.slproto.chat.generic;
import java.util.*;

import javax.annotation.Nonnull;

public interface OnChatEventListener {
    void onChatEvent(@Nonnull SLChatEvent sLChatEvent);
}
