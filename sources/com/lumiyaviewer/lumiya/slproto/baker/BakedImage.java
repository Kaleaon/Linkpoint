// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.baker;

import android.graphics.Bitmap;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.baker:
//            BakeLayerSet, BakeLayer, BakeProcess

public class BakedImage
{

    private BakeLayerSet layerSet;
    private OpenJPEG resultImage;
    private UUID uploadedID;

    public BakedImage(BakeLayerSet bakelayerset)
    {
        layerSet = bakelayerset;
        resultImage = new OpenJPEG(bakelayerset.width, bakelayerset.height, 4, 4, 1, -1);
        resultImage.setComponent(4, (byte)-1);
    }

    public void Bake(BakeProcess bakeprocess)
    {
        boolean flag = false;
        BakeLayer abakelayer[] = layerSet.layers;
        int k = abakelayer.length;
        for (int i = 0; i < k; i++)
        {
            abakelayer[i].Bake(resultImage, bakeprocess);
        }

        if (layerSet.clear_alpha || layerSet.maskLayers.length > 0)
        {
            resultImage.setComponent(3, (byte)-1);
        }
        abakelayer = layerSet.maskLayers;
        k = abakelayer.length;
        for (int j = ((flag) ? 1 : 0); j < k; j++)
        {
            abakelayer[j].BakeAlpha(resultImage, bakeprocess);
        }

    }

    public void SaveToJPEG2K(File file)
        throws IOException
    {
        resultImage.SaveJPEG2K(file);
    }

    public Bitmap getAsBitmap()
    {
        return resultImage.getAsBitmap();
    }

    public OpenJPEG getBakedImage()
    {
        return resultImage;
    }

    public UUID getUploadedID()
    {
        return uploadedID;
    }

    public void setUploadedID(UUID uuid)
    {
        uploadedID = uuid;
    }
}
