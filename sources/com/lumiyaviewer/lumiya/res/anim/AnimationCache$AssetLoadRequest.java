// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.anim;

import android.content.res.AssetManager;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.render.avatar.AnimationData;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.res.anim:
//            AnimationCache

private static class assetName extends ResourceRequest
    implements Runnable
{

    private final String assetName;

    public void cancelRequest()
    {
        LoaderExecutor.getInstance().remove(this);
        super.cancelRequest();
    }

    public void execute()
    {
        LoaderExecutor.getInstance().execute(this);
    }

    public void run()
    {
        Object obj;
        Object obj1;
        obj1 = null;
        obj = LumiyaApp.getAssetManager();
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_220;
        }
        obj = ((AssetManager) (obj)).open((new StringBuilder()).append("anims/").append(assetName).toString());
        if (obj == null) goto _L2; else goto _L1
_L1:
        AnimationData animationdata = new AnimationData((UUID)getParams(), ((InputStream) (obj)));
        obj1 = animationdata;
        if (animationdata.getPriority() < 6)
        {
            break MISSING_BLOCK_LABEL_101;
        }
        Debug.Printf("Animation: priority %d loaded from asset %s", new Object[] {
            Integer.valueOf(animationdata.getPriority()), assetName
        });
        obj1 = animationdata;
_L8:
        Object obj2;
        obj2 = obj1;
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_113;
        }
        ((InputStream) (obj)).close();
        obj2 = obj1;
_L3:
        completeRequest(obj2);
        return;
        obj;
        Debug.Warning(((Throwable) (obj)));
        obj2 = obj1;
          goto _L3
        obj2;
        obj = null;
_L7:
        Debug.Warning(((Throwable) (obj2)));
        obj2 = obj;
        if (obj1 == null) goto _L3; else goto _L4
_L4:
        ((InputStream) (obj1)).close();
        obj2 = obj;
          goto _L3
        obj1;
        Debug.Warning(((Throwable) (obj1)));
        obj2 = obj;
          goto _L3
        obj1;
        obj = null;
_L6:
        if (obj != null)
        {
            try
            {
                ((InputStream) (obj)).close();
            }
            // Misplaced declaration of an exception variable
            catch (Object obj)
            {
                Debug.Warning(((Throwable) (obj)));
            }
        }
        throw obj1;
        obj1;
        continue; /* Loop/switch isn't completed */
        obj2;
        obj = obj1;
        obj1 = obj2;
        if (true) goto _L6; else goto _L5
_L5:
        obj2;
        animationdata = null;
        obj1 = obj;
        obj = animationdata;
          goto _L7
        obj2;
        obj1 = obj;
        obj = animationdata;
          goto _L7
_L2:
        obj1 = null;
          goto _L8
        obj2 = null;
          goto _L3
    }

    (UUID uuid, ResourceManager resourcemanager, String s)
    {
        super(uuid, resourcemanager);
        assetName = s;
    }
}
