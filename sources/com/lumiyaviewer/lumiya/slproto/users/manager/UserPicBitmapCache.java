// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.res.textures.TextureCompressedCache;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserManager

public class UserPicBitmapCache extends ResourceMemoryCache
{
    private class UserPicBitmapRequest extends ResourceRequest
        implements ResourceConsumer
    {

        private volatile File compressedFile;
        private final Runnable decompressRunnable = new _cls2();
        private volatile Future decompressorFuture;
        private final Runnable loadRunnable = new _cls1();
        private volatile Future loaderFuture;
        final UserPicBitmapCache this$0;

        static File _2D_get0(UserPicBitmapRequest userpicbitmaprequest)
        {
            return userpicbitmaprequest.compressedFile;
        }

        static Object _2D_wrap0(UserPicBitmapRequest userpicbitmaprequest)
        {
            return userpicbitmaprequest.getParams();
        }

        public void OnResourceReady(Object obj, boolean flag)
        {
            Object obj1 = getParams();
            String s;
            if (obj != null)
            {
                s = obj.toString();
            } else
            {
                s = "null";
            }
            Debug.Printf("UserPic: bitmap ID %s: got resource %s", new Object[] {
                obj1, s
            });
            if (obj instanceof File)
            {
                compressedFile = (File)obj;
                decompressorFuture = TextureCache.getInstance().getDecompressorExecutor().submit(decompressRunnable);
            } else
            if (obj == null)
            {
                completeRequest(null);
                return;
            }
        }

        public void cancelRequest()
        {
            Debug.Printf("DecompressRequest: cancelled (%s)", new Object[] {
                ((UUID)getParams()).toString()
            });
            Future future = decompressorFuture;
            if (future != null)
            {
                future.cancel(false);
            }
            future = loaderFuture;
            if (future != null)
            {
                future.cancel(false);
            }
            TextureCache.getInstance().getTextureCompressedCache().CancelRequest(this);
            super.cancelRequest();
        }

        public void execute()
        {
            Debug.Printf("UserPic: Requesting load for %s", new Object[] {
                getParams()
            });
            loaderFuture = LoaderExecutor.getInstance().submit(loadRunnable);
        }

        public UserPicBitmapRequest(UUID uuid, ResourceManager resourcemanager)
        {
            this$0 = UserPicBitmapCache.this;
            super(uuid, resourcemanager);
            compressedFile = null;
        }
    }


    private static final int MAX_USERPIC_HEIGHT = 128;
    private static final int MAX_USERPIC_WIDTH = 128;
    private final UserManager userManager;

    static UserManager _2D_get0(UserPicBitmapCache userpicbitmapcache)
    {
        return userpicbitmapcache.userManager;
    }

    public UserPicBitmapCache(UserManager usermanager)
    {
        userManager = usermanager;
    }

    protected volatile ResourceRequest CreateNewRequest(Object obj, ResourceManager resourcemanager)
    {
        return CreateNewRequest((UUID)obj, resourcemanager);
    }

    protected ResourceRequest CreateNewRequest(UUID uuid, ResourceManager resourcemanager)
    {
        return new UserPicBitmapRequest(uuid, resourcemanager);
    }

    // Unreferenced inner class com/lumiyaviewer/lumiya/slproto/users/manager/UserPicBitmapCache$UserPicBitmapRequest$1

/* anonymous class */
    class UserPicBitmapRequest._cls1
        implements Runnable
    {

        final UserPicBitmapRequest this$1;

        public void run()
        {
            byte abyte0[] = UserPicBitmapCache._2D_get0(_fld0).getUserPic((UUID)UserPicBitmapRequest._2D_wrap0(UserPicBitmapRequest.this));
            Object obj1 = UserPicBitmapRequest._2D_wrap0(UserPicBitmapRequest.this);
            Object obj;
            if (abyte0 != null)
            {
                obj = Integer.toString(abyte0.length);
            } else
            {
                obj = "null";
            }
            Debug.Printf("UserPic: bitmap ID %s: got bitmap data %s", new Object[] {
                obj1, obj
            });
            if (abyte0 != null)
            {
                obj = BitmapFactory.decodeByteArray(abyte0, 0, abyte0.length);
                completeRequest(obj);
                return;
            } else
            {
                TextureCache.getInstance().getTextureCompressedCache().RequestResource(DrawableTextureParams.create((UUID)UserPicBitmapRequest._2D_wrap0(UserPicBitmapRequest.this), TextureClass.Asset), UserPicBitmapRequest.this);
                return;
            }
        }

            
            {
                this$1 = UserPicBitmapRequest.this;
                super();
            }
    }


    // Unreferenced inner class com/lumiyaviewer/lumiya/slproto/users/manager/UserPicBitmapCache$UserPicBitmapRequest$2

/* anonymous class */
    class UserPicBitmapRequest._cls2
        implements Runnable
    {

        final UserPicBitmapRequest this$1;

        public void run()
        {
            try
            {
                Bitmap bitmap = (new OpenJPEG(UserPicBitmapRequest._2D_get0(UserPicBitmapRequest.this), 128, 128, false)).getAsBitmap();
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, bytearrayoutputstream);
                byte abyte0[] = bytearrayoutputstream.toByteArray();
                Debug.Printf("UserPic: bitmap ID %s: storing bitmap data %d bytes", new Object[] {
                    UserPicBitmapRequest._2D_wrap0(UserPicBitmapRequest.this), Integer.valueOf(abyte0.length)
                });
                UserPicBitmapCache._2D_get0(_fld0).setUserPic((UUID)UserPicBitmapRequest._2D_wrap0(UserPicBitmapRequest.this), abyte0);
                completeRequest(bitmap);
                return;
            }
            catch (IOException ioexception)
            {
                completeRequest(null);
            }
        }

            
            {
                this$1 = UserPicBitmapRequest.this;
                super();
            }
    }

}
