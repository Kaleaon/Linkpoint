// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.baker;

import com.lumiyaviewer.lumiya.slproto.avatar.BakedTextureIndex;
import com.lumiyaviewer.lumiya.slproto.modules.texuploader.SLTextureUploadRequest;
import java.io.File;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.baker:
//            BakeProcess, BakedImage

private static class bakedIndex extends SLTextureUploadRequest
{

    final BakedImage bakedImage;
    final BakedTextureIndex bakedIndex;

    (BakedImage bakedimage, BakedTextureIndex bakedtextureindex, File file)
    {
        super(file, bakedtextureindex.ordinal());
        bakedImage = bakedimage;
        bakedIndex = bakedtextureindex;
    }
}
