package com.lumiyaviewer.lumiya.slproto.modules.rlv;

import java.util.UUID;

public interface RLVCommand {
    void Handle(RLVController rLVController, UUID uuid, RLVCommands rLVCommands, String str, String str2);
}
