// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            GroupMemberDao

public static class 
{

    public static final Property AgentPowers;
    public static final Property Contribution;
    public static final Property GroupID = new Property(0, java/util/UUID, "groupID", false, "GROUP_ID");
    public static final Property IsOwner;
    public static final Property OnlineStatus = new Property(4, java/lang/String, "onlineStatus", false, "ONLINE_STATUS");
    public static final Property RequestID = new Property(1, java/util/UUID, "requestID", false, "REQUEST_ID");
    public static final Property Title = new Property(6, java/lang/String, "title", false, "TITLE");
    public static final Property UserID = new Property(2, java/util/UUID, "userID", false, "USER_ID");

    static 
    {
        Contribution = new Property(3, Integer.TYPE, "contribution", false, "CONTRIBUTION");
        AgentPowers = new Property(5, Long.TYPE, "agentPowers", false, "AGENT_POWERS");
        IsOwner = new Property(7, Boolean.TYPE, "isOwner", false, "IS_OWNER");
    }

    public ()
    {
    }
}
