// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.dispnames.SLDisplayNameFetcher;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo;
import com.lumiyaviewer.lumiya.slproto.modules.groups.SLGroupManager;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.search.SLSearch;
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher;
import com.lumiyaviewer.lumiya.slproto.modules.texuploader.SLTextureUploader;
import com.lumiyaviewer.lumiya.slproto.modules.transfer.SLTransferManager;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXferManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLUserNameFetcher, SLMinimap, SLAvatarControl, SLDrawDistance, 
//            SLWorldMap, SLAvatarAppearance, SLTaskInventories, SLUserProfiles, 
//            SLModule

public class SLModules
{

    public final SLAvatarAppearance avatarAppearance;
    public final SLAvatarControl avatarControl;
    public final SLDisplayNameFetcher displayNameFetcher;
    public final SLDrawDistance drawDistance;
    public final SLFinancialInfo financialInfo;
    public final SLSearch gridSearch;
    public final SLGroupManager groupManager;
    public final SLInventory inventory;
    public final SLMinimap minimap;
    private final List modules = new ArrayList();
    public final SLMuteList muteList;
    public final RLVController rlvController;
    public final SLTaskInventories taskInventories;
    public final SLTextureFetcher textureFetcher;
    public final SLTextureUploader textureUploader;
    public final SLTransferManager transferManager;
    public final SLUserNameFetcher userNameFetcher;
    public final SLUserProfiles userProfiles;
    public final SLVoice voice;
    public final SLWorldMap worldMap;
    public final SLXferManager xferManager;

    public SLModules(SLAgentCircuit slagentcircuit, SLCaps slcaps, SLGridConnection slgridconnection)
    {
        Object obj = modules;
        Object obj1 = new SLUserNameFetcher(slagentcircuit, slcaps);
        userNameFetcher = ((SLUserNameFetcher) (obj1));
        ((List) (obj)).add(obj1);
        obj = modules;
        obj1 = new SLSearch(slagentcircuit);
        gridSearch = ((SLSearch) (obj1));
        ((List) (obj)).add(obj1);
        obj = modules;
        obj1 = new SLMinimap(slagentcircuit);
        minimap = ((SLMinimap) (obj1));
        ((List) (obj)).add(obj1);
        obj = modules;
        obj1 = new SLAvatarControl(slagentcircuit);
        avatarControl = ((SLAvatarControl) (obj1));
        ((List) (obj)).add(obj1);
        obj = modules;
        obj1 = new SLDrawDistance(slagentcircuit);
        drawDistance = ((SLDrawDistance) (obj1));
        ((List) (obj)).add(obj1);
        obj = modules;
        obj1 = new SLInventory(slagentcircuit, slcaps);
        inventory = ((SLInventory) (obj1));
        ((List) (obj)).add(obj1);
        obj = modules;
        obj1 = new SLWorldMap(slagentcircuit);
        worldMap = ((SLWorldMap) (obj1));
        ((List) (obj)).add(obj1);
        obj = modules;
        obj1 = new SLTransferManager(slagentcircuit);
        transferManager = ((SLTransferManager) (obj1));
        ((List) (obj)).add(obj1);
        obj = modules;
        slgridconnection = new SLTextureFetcher(slagentcircuit, slcaps, slgridconnection.authReply.agentAppearanceService);
        textureFetcher = slgridconnection;
        ((List) (obj)).add(slgridconnection);
        slgridconnection = modules;
        obj = new SLTextureUploader(slagentcircuit, slcaps);
        textureUploader = ((SLTextureUploader) (obj));
        slgridconnection.add(obj);
        slgridconnection = modules;
        obj = new SLAvatarAppearance(slagentcircuit, inventory, slcaps);
        avatarAppearance = ((SLAvatarAppearance) (obj));
        slgridconnection.add(obj);
        slgridconnection = modules;
        obj = new RLVController(slagentcircuit);
        rlvController = ((RLVController) (obj));
        slgridconnection.add(obj);
        slgridconnection = modules;
        obj = new SLXferManager(slagentcircuit);
        xferManager = ((SLXferManager) (obj));
        slgridconnection.add(obj);
        slgridconnection = modules;
        obj = new SLTaskInventories(slagentcircuit);
        taskInventories = ((SLTaskInventories) (obj));
        slgridconnection.add(obj);
        slgridconnection = modules;
        obj = new SLMuteList(slagentcircuit);
        muteList = ((SLMuteList) (obj));
        slgridconnection.add(obj);
        slgridconnection = modules;
        obj = new SLFinancialInfo(slagentcircuit);
        financialInfo = ((SLFinancialInfo) (obj));
        slgridconnection.add(obj);
        slgridconnection = modules;
        obj = new SLGroupManager(slagentcircuit);
        groupManager = ((SLGroupManager) (obj));
        slgridconnection.add(obj);
        slgridconnection = modules;
        obj = new SLUserProfiles(slagentcircuit, slcaps);
        userProfiles = ((SLUserProfiles) (obj));
        slgridconnection.add(obj);
        slgridconnection = modules;
        obj = new SLDisplayNameFetcher(slagentcircuit, slcaps);
        displayNameFetcher = ((SLDisplayNameFetcher) (obj));
        slgridconnection.add(obj);
        slgridconnection = modules;
        slagentcircuit = new SLVoice(slagentcircuit, slcaps);
        voice = slagentcircuit;
        slgridconnection.add(slagentcircuit);
    }

    public void HandleCircuitReady()
    {
        for (Iterator iterator = modules.iterator(); iterator.hasNext(); ((SLModule)iterator.next()).HandleCircuitReady()) { }
    }

    public void HandleCloseCircuit()
    {
        for (Iterator iterator = modules.iterator(); iterator.hasNext(); ((SLModule)iterator.next()).HandleCloseCircuit()) { }
    }

    public void HandleGlobalOptionsChange()
    {
        for (Iterator iterator = modules.iterator(); iterator.hasNext(); ((SLModule)iterator.next()).HandleGlobalOptionsChange()) { }
    }
}
