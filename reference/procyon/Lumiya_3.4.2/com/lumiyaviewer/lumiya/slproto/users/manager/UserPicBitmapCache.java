// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import java.io.IOException;
import java.io.OutputStream;
import android.graphics.Bitmap$CompressFormat;
import java.io.ByteArrayOutputStream;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import android.graphics.BitmapFactory;
import com.lumiyaviewer.lumiya.Debug;
import java.util.concurrent.Future;
import java.io.File;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import android.graphics.Bitmap;
import java.util.UUID;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;

public class UserPicBitmapCache extends ResourceMemoryCache<UUID, Bitmap>
{
    private static final int MAX_USERPIC_HEIGHT = 128;
    private static final int MAX_USERPIC_WIDTH = 128;
    private final UserManager userManager;
    
    public UserPicBitmapCache(final UserManager userManager) {
        this.userManager = userManager;
    }
    
    @Override
    protected ResourceRequest<UUID, Bitmap> CreateNewRequest(final UUID uuid, final ResourceManager<UUID, Bitmap> resourceManager) {
        return new UserPicBitmapRequest(uuid, resourceManager);
    }
    
    private class UserPicBitmapRequest extends ResourceRequest<UUID, Bitmap> implements ResourceConsumer
    {
        private volatile File compressedFile;
        private final Runnable decompressRunnable;
        private volatile Future<?> decompressorFuture;
        private final Runnable loadRunnable;
        private volatile Future<?> loaderFuture;
        
        public UserPicBitmapRequest(final UUID uuid, final ResourceManager<UUID, Bitmap> resourceManager) {
            super(uuid, resourceManager);
            this.compressedFile = null;
            this.loadRunnable = new Runnable() {
                @Override
                public void run() {
                    final byte[] userPic = UserPicBitmapCache.this.userManager.getUserPic((UUID)ResourceRequest.this.getParams());
                    final Object -wrap0 = ResourceRequest.this.getParams();
                    String string;
                    if (userPic != null) {
                        string = Integer.toString(userPic.length);
                    }
                    else {
                        string = "null";
                    }
                    Debug.Printf("UserPic: bitmap ID %s: got bitmap data %s", -wrap0, string);
                    if (userPic != null) {
                        ((ResourceRequest<ResourceParams, Bitmap>)UserPicBitmapRequest.this).completeRequest(BitmapFactory.decodeByteArray(userPic, 0, userPic.length));
                    }
                    else {
                        TextureCache.getInstance().getTextureCompressedCache().RequestResource(DrawableTextureParams.create((UUID)ResourceRequest.this.getParams(), TextureClass.Asset), UserPicBitmapRequest.this);
                    }
                }
            };
            this.decompressRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        final Bitmap asBitmap = new OpenJPEG(UserPicBitmapRequest.this.compressedFile, 128, 128, false).getAsBitmap();
                        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        asBitmap.compress(Bitmap$CompressFormat.PNG, 100, (OutputStream)byteArrayOutputStream);
                        final byte[] byteArray = byteArrayOutputStream.toByteArray();
                        Debug.Printf("UserPic: bitmap ID %s: storing bitmap data %d bytes", ResourceRequest.this.getParams(), byteArray.length);
                        UserPicBitmapCache.this.userManager.setUserPic((UUID)ResourceRequest.this.getParams(), byteArray);
                        ((ResourceRequest<ResourceParams, Bitmap>)UserPicBitmapRequest.this).completeRequest(asBitmap);
                    }
                    catch (final IOException ex) {
                        ((ResourceRequest<ResourceParams, Bitmap>)UserPicBitmapRequest.this).completeRequest(null);
                    }
                }
            };
        }
        
        @Override
        public void OnResourceReady(final Object o, final boolean b) {
            final Object params = ((ResourceRequest<Object, ResourceType>)this).getParams();
            String string;
            if (o != null) {
                string = o.toString();
            }
            else {
                string = "null";
            }
            Debug.Printf("UserPic: bitmap ID %s: got resource %s", params, string);
            if (o instanceof File) {
                this.compressedFile = (File)o;
                this.decompressorFuture = TextureCache.getInstance().getDecompressorExecutor().submit(this.decompressRunnable);
            }
            else if (o == null) {
                ((ResourceRequest<ResourceParams, Bitmap>)this).completeRequest(null);
            }
        }
        
        @Override
        public void cancelRequest() {
            Debug.Printf("DecompressRequest: cancelled (%s)", ((ResourceRequest<UUID, ResourceType>)this).getParams().toString());
            final Future<?> decompressorFuture = this.decompressorFuture;
            if (decompressorFuture != null) {
                decompressorFuture.cancel(false);
            }
            final Future<?> loaderFuture = this.loaderFuture;
            if (loaderFuture != null) {
                loaderFuture.cancel(false);
            }
            TextureCache.getInstance().getTextureCompressedCache().CancelRequest(this);
            super.cancelRequest();
        }
        
        @Override
        public void execute() {
            Debug.Printf("UserPic: Requesting load for %s", ((ResourceRequest<Object, ResourceType>)this).getParams());
            this.loaderFuture = LoaderExecutor.getInstance().submit(this.loadRunnable);
        }
    }
}
