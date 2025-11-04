// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.net.Uri;
import android.content.Intent;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;

public final class SLVoiceUpgradeEvent extends SLChatYesNoEvent
{
    private final boolean isInstall;
    private final String upgradeURL;
    
    public SLVoiceUpgradeEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        boolean isInstall = false;
        super(chatMessage, uuid);
        this.upgradeURL = chatMessage.getItemName();
        if (chatMessage.getAssetType() != 0) {
            isInstall = true;
        }
        this.isInstall = isInstall;
    }
    
    public SLVoiceUpgradeEvent(@Nonnull final UUID uuid, final String s, final boolean isInstall, final String upgradeURL) {
        super(ChatMessageSourceUnknown.getInstance(), uuid, s);
        this.upgradeURL = upgradeURL;
        this.isInstall = isInstall;
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.VoiceUpgrade;
    }
    
    public String getNoButton(final Context context) {
        return context.getString(2131297158);
    }
    
    public String getNoMessage(final Context context) {
        String s;
        if (this.isInstall) {
            s = context.getString(2131297147);
        }
        else {
            s = context.getString(2131297157);
        }
        return s;
    }
    
    public String getQuestion(final Context context) {
        String s;
        if (this.isInstall) {
            s = context.getString(2131296605);
        }
        else {
            s = context.getString(2131297125);
        }
        return s;
    }
    
    @Override
    public String getText(final Context context, @Nonnull final UserManager userManager) {
        return this.text;
    }
    
    public String getYesButton(final Context context) {
        String s;
        if (this.isInstall) {
            s = context.getString(2131297148);
        }
        else {
            s = context.getString(2131297159);
        }
        return s;
    }
    
    public String getYesMessage(final Context context) {
        return "";
    }
    
    @Override
    public boolean isObjectPopup() {
        return false;
    }
    
    @Override
    protected void onNoAction(final Context context, final UserManager userManager) {
        super.onNoAction(context, userManager);
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }
    
    @Override
    public void onYesAction(final Context context, final UserManager userManager) {
        super.onYesAction(context, userManager);
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
        final Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse(this.upgradeURL));
        context.startActivity(intent);
    }
    
    @Override
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setItemName(this.upgradeURL);
        int i;
        if (this.isInstall) {
            i = 1;
        }
        else {
            i = 0;
        }
        chatMessage.setAssetType(i);
    }
}
