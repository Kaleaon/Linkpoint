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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Future;
/* loaded from: classes.dex */
public class UserPicBitmapCache extends ResourceMemoryCache<UUID, Bitmap> {
    private static final int MAX_USERPIC_HEIGHT = 128;
    private static final int MAX_USERPIC_WIDTH = 128;
    private final UserManager userManager;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class UserPicBitmapRequest extends ResourceRequest<UUID, Bitmap> implements ResourceConsumer {
        private volatile File compressedFile;
        private final Runnable decompressRunnable;
        private volatile Future<?> decompressorFuture;
        private final Runnable loadRunnable;
        private volatile Future<?> loaderFuture;

        public UserPicBitmapRequest(UUID uuid, ResourceManager<UUID, Bitmap> resourceManager) {
            super(uuid, resourceManager);
            this.compressedFile = null;
            this.loadRunnable = new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.UserPicBitmapCache.UserPicBitmapRequest.1
                @Override // java.lang.Runnable
                public void run() {
                    byte[] userPic = UserPicBitmapCache.this.userManager.getUserPic((UUID) UserPicBitmapRequest.this.getParams());
                    Object[] objArr = new Object[2];
                    objArr[0] = UserPicBitmapRequest.this.getParams();
                    objArr[1] = userPic != null ? Integer.toString(userPic.length) : "null";
                    Debug.Printf("UserPic: bitmap ID %s: got bitmap data %s", objArr);
                    if (userPic != null) {
                        UserPicBitmapRequest.this.completeRequest(BitmapFactory.decodeByteArray(userPic, 0, userPic.length));
                    } else {
                        TextureCache.getInstance().getTextureCompressedCache().RequestResource(DrawableTextureParams.create((UUID) UserPicBitmapRequest.this.getParams(), TextureClass.Asset), (ResourceConsumer) UserPicBitmapRequest.this);
                    }
                }
            };
            this.decompressRunnable = new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.UserPicBitmapCache.UserPicBitmapRequest.2
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        Bitmap asBitmap = new OpenJPEG(UserPicBitmapRequest.this.compressedFile, 128, 128, false).getAsBitmap();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        asBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        Debug.Printf("UserPic: bitmap ID %s: storing bitmap data %d bytes", UserPicBitmapRequest.this.getParams(), Integer.valueOf(byteArray.length));
                        UserPicBitmapCache.this.userManager.setUserPic((UUID) UserPicBitmapRequest.this.getParams(), byteArray);
                        UserPicBitmapRequest.this.completeRequest(asBitmap);
                    } catch (IOException e) {
                        UserPicBitmapRequest.this.completeRequest(null);
                    }
                }
            };
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceConsumer
        public void OnResourceReady(Object obj, boolean z) {
            Object[] objArr = new Object[2];
            objArr[0] = getParams();
            objArr[1] = obj != null ? obj.toString() : "null";
            Debug.Printf("UserPic: bitmap ID %s: got resource %s", objArr);
            if (obj instanceof File) {
                this.compressedFile = (File) obj;
                this.decompressorFuture = TextureCache.getInstance().getDecompressorExecutor().submit(this.decompressRunnable);
            } else if (obj == null) {
                completeRequest(null);
            }
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void cancelRequest() {
            Debug.Printf("DecompressRequest: cancelled (%s)", getParams().toString());
            Future<?> future = this.decompressorFuture;
            if (future != null) {
                future.cancel(false);
            }
            Future<?> future2 = this.loaderFuture;
            if (future2 != null) {
                future2.cancel(false);
            }
            TextureCache.getInstance().getTextureCompressedCache().CancelRequest(this);
            super.cancelRequest();
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void execute() {
            Debug.Printf("UserPic: Requesting load for %s", getParams());
            this.loaderFuture = LoaderExecutor.getInstance().submit(this.loadRunnable);
        }
    }

    public UserPicBitmapCache(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override // com.lumiyaviewer.lumiya.res.ResourceManager
    protected /* bridge */ /* synthetic */ ResourceRequest CreateNewRequest(Object obj, ResourceManager resourceManager) {
        return CreateNewRequest((UUID) obj, (ResourceManager<UUID, Bitmap>) resourceManager);
    }

    protected ResourceRequest<UUID, Bitmap> CreateNewRequest(UUID uuid, ResourceManager<UUID, Bitmap> resourceManager) {
        return new UserPicBitmapRequest(uuid, resourceManager);
    }
}
