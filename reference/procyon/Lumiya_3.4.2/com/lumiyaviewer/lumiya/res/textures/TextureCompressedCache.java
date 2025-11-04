// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.textures;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import java.util.concurrent.Future;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetchRequest;
import com.lumiyaviewer.lumiya.res.executors.Startable;
import com.lumiyaviewer.lumiya.res.executors.HTTPFetchExecutor;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.slproto.modules.texfetcher.SLTextureFetcher;
import com.lumiyaviewer.lumiya.res.executors.StartingExecutor;
import java.io.File;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.res.ResourceManager;

public class TextureCompressedCache extends ResourceManager<DrawableTextureParams, File>
{
    private final StartingExecutor downloadExecutor;
    private volatile SLTextureFetcher fetcher;
    private final Object lock;
    
    public TextureCompressedCache() {
        this.downloadExecutor = new StartingExecutor(GlobalOptions.getInstance().getMaxTextureDownloads());
        this.lock = new Object();
        this.fetcher = null;
    }
    
    @Override
    protected ResourceRequest<DrawableTextureParams, File> CreateNewRequest(final DrawableTextureParams drawableTextureParams, final ResourceManager<DrawableTextureParams, File> resourceManager) {
        return new TextureFetchRequest(drawableTextureParams, resourceManager, TextureCache.getInstance().getTextureCompressedFile(drawableTextureParams), this.fetcher);
    }
    
    @Override
    public void RequestResource(final DrawableTextureParams drawableTextureParams, final ResourceConsumer resourceConsumer) {
        while (true) {
            final File textureCompressedFileOld = TextureCache.getInstance().getTextureCompressedFileOld(drawableTextureParams);
            File textureCompressedFile = TextureCache.getInstance().getTextureCompressedFile(drawableTextureParams);
            while (true) {
                Label_0088: {
                    synchronized (this.lock) {
                        if (textureCompressedFileOld.exists()) {
                            textureCompressedFile = textureCompressedFileOld;
                        }
                        else if (!textureCompressedFile.exists()) {
                            break Label_0088;
                        }
                        monitorexit(this.lock);
                        if (textureCompressedFile != null) {
                            resourceConsumer.OnResourceReady(textureCompressedFile, false);
                            return;
                        }
                    }
                    final DrawableTextureParams drawableTextureParams2;
                    super.RequestResource(drawableTextureParams2, resourceConsumer);
                    return;
                }
                textureCompressedFile = null;
                continue;
            }
        }
    }
    
    public void setFetcher(final SLTextureFetcher fetcher) {
        this.fetcher = fetcher;
    }
    
    public void setMaxTextureDownloads(final int maximumPoolSize) {
        if (maximumPoolSize > 0) {
            this.downloadExecutor.setMaxConcurrentTasks(maximumPoolSize);
            HTTPFetchExecutor.getInstance().setCorePoolSize(maximumPoolSize);
            HTTPFetchExecutor.getInstance().setMaximumPoolSize(maximumPoolSize);
        }
    }
    
    private class TextureFetchRequest extends ResourceRequest<DrawableTextureParams, File> implements Startable, TextureFetchCompleteListener, Runnable, HasPriority
    {
        private static final int MAX_RETRIES = 2;
        private final File compressedFile;
        private volatile SLTextureFetchRequest fetchRequest;
        private volatile Future<?> fetchTask;
        private final SLTextureFetcher fetcher;
        
        private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues() {
            if (TextureFetchRequest.-com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues != null) {
                return TextureFetchRequest.-com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues;
            }
            int[] -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues = new int[TextureClass.values().length];
            while (true) {
                try {
                    -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Asset.ordinal()] = 3;
                    try {
                        -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Baked.ordinal()] = 1;
                        try {
                            -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Prim.ordinal()] = 4;
                            try {
                                -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Sculpt.ordinal()] = 2;
                                try {
                                    -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Terrain.ordinal()] = 5;
                                    return -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues = -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues;
                                }
                                catch (final NoSuchFieldError noSuchFieldError) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError2) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError3) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError4) {}
                }
                catch (final NoSuchFieldError noSuchFieldError5) {
                    continue;
                }
                break;
            }
        }
        
        public TextureFetchRequest(final DrawableTextureParams drawableTextureParams, final ResourceManager<DrawableTextureParams, File> resourceManager, final File compressedFile, final SLTextureFetcher fetcher) {
            super(drawableTextureParams, resourceManager);
            this.compressedFile = compressedFile;
            this.fetcher = fetcher;
        }
        
        @Override
        public void OnTextureFetchComplete(final SLTextureFetchRequest slTextureFetchRequest) {
            this.completeRequest(slTextureFetchRequest.outputFile);
        }
        
        @Override
        public void cancelRequest() {
            Debug.Printf("TextureFetchRequest: cancelled (%s)", ((ResourceRequest<DrawableTextureParams, ResourceType>)this).getParams().uuid().toString());
            synchronized (this) {
                final SLTextureFetchRequest fetchRequest = this.fetchRequest;
                final SLTextureFetcher fetcher = this.fetcher;
                final Future<?> fetchTask = this.fetchTask;
                monitorexit(this);
                if (fetcher != null && fetchRequest != null) {
                    fetcher.CancelFetch(fetchRequest);
                }
                if (fetchTask != null) {
                    fetchTask.cancel(true);
                }
                TextureCompressedCache.this.downloadExecutor.cancelRequest(this);
                super.cancelRequest();
            }
        }
        
        @Override
        public void completeRequest(final File file) {
            TextureCompressedCache.this.downloadExecutor.completeRequest(this);
            super.completeRequest(file);
        }
        
        @Override
        public void execute() {
            this.fetchTask = HTTPFetchExecutor.getInstance().submit(this);
        }
        
        @Override
        public int getPriority() {
            switch (-getcom-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues()[((ResourceRequest<DrawableTextureParams, ResourceType>)this).getParams().textureClass().ordinal()]) {
                default: {
                    return 2;
                }
                case 2: {
                    return 0;
                }
                case 1: {
                    return 1;
                }
            }
        }
        
        @Override
        public void run() {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache$TextureFetchRequest.fetcher:Lcom/lumiyaviewer/lumiya/slproto/modules/texfetcher/SLTextureFetcher;
            //     4: ifnonnull       13
            //     7: aload_0        
            //     8: aconst_null    
            //     9: invokevirtual   com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache$TextureFetchRequest.completeRequest:(Ljava/io/File;)V
            //    12: return         
            //    13: aload_0        
            //    14: getfield        com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache$TextureFetchRequest.fetcher:Lcom/lumiyaviewer/lumiya/slproto/modules/texfetcher/SLTextureFetcher;
            //    17: invokevirtual   com/lumiyaviewer/lumiya/slproto/modules/texfetcher/SLTextureFetcher.getCapURL:()Ljava/lang/String;
            //    20: astore_1       
            //    21: aload_0        
            //    22: getfield        com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache$TextureFetchRequest.fetcher:Lcom/lumiyaviewer/lumiya/slproto/modules/texfetcher/SLTextureFetcher;
            //    25: invokevirtual   com/lumiyaviewer/lumiya/slproto/modules/texfetcher/SLTextureFetcher.getAgentAppearanceService:()Ljava/lang/String;
            //    28: astore_2       
            //    29: aload_0        
            //    30: invokevirtual   com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache$TextureFetchRequest.getParams:()Ljava/lang/Object;
            //    33: checkcast       Lcom/lumiyaviewer/lumiya/render/tex/DrawableTextureParams;
            //    36: astore_3       
            //    37: aload_3        
            //    38: invokevirtual   com/lumiyaviewer/lumiya/render/tex/DrawableTextureParams.avatarUUID:()Ljava/util/UUID;
            //    41: astore          4
            //    43: aload_3        
            //    44: invokevirtual   com/lumiyaviewer/lumiya/render/tex/DrawableTextureParams.avatarFaceIndex:()Lcom/lumiyaviewer/lumiya/slproto/avatar/AvatarTextureFaceIndex;
            //    47: astore          5
            //    49: aload_2        
            //    50: ifnull          58
            //    53: aload           4
            //    55: ifnonnull       322
            //    58: new             Ljava/lang/StringBuilder;
            //    61: astore_2       
            //    62: aload_2        
            //    63: invokespecial   java/lang/StringBuilder.<init>:()V
            //    66: new             Ljava/net/URL;
            //    69: dup            
            //    70: aload_2        
            //    71: aload_1        
            //    72: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //    75: ldc             "/?texture_id="
            //    77: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //    80: aload_3        
            //    81: invokevirtual   com/lumiyaviewer/lumiya/render/tex/DrawableTextureParams.uuid:()Ljava/util/UUID;
            //    84: invokevirtual   java/util/UUID.toString:()Ljava/lang/String;
            //    87: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //    90: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //    93: invokespecial   java/net/URL.<init>:(Ljava/lang/String;)V
            //    96: astore_2       
            //    97: new             Ljava/lang/StringBuilder;
            //   100: dup            
            //   101: invokespecial   java/lang/StringBuilder.<init>:()V
            //   104: ldc             "TextureFetchRequest: Fetching texture "
            //   106: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   109: aload_3        
            //   110: invokevirtual   com/lumiyaviewer/lumiya/render/tex/DrawableTextureParams.uuid:()Ljava/util/UUID;
            //   113: invokevirtual   java/util/UUID.toString:()Ljava/lang/String;
            //   116: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   119: ldc             ", url = "
            //   121: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   124: aload_2        
            //   125: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
            //   128: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   131: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
            //   134: new             Ljava/io/File;
            //   137: dup            
            //   138: new             Ljava/lang/StringBuilder;
            //   141: dup            
            //   142: invokespecial   java/lang/StringBuilder.<init>:()V
            //   145: aload_0        
            //   146: getfield        com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache$TextureFetchRequest.compressedFile:Ljava/io/File;
            //   149: invokevirtual   java/io/File.getAbsolutePath:()Ljava/lang/String;
            //   152: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   155: ldc             ".part"
            //   157: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   160: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   163: invokespecial   java/io/File.<init>:(Ljava/lang/String;)V
            //   166: astore_3       
            //   167: aload_3        
            //   168: invokevirtual   java/io/File.getParentFile:()Ljava/io/File;
            //   171: astore_1       
            //   172: ldc             "TextureFetchRequest: tempOutputDir = %s, createResult = %b, exists = %b"
            //   174: iconst_3       
            //   175: anewarray       Ljava/lang/Object;
            //   178: dup            
            //   179: iconst_0       
            //   180: aload_1        
            //   181: aastore        
            //   182: dup            
            //   183: iconst_1       
            //   184: aload_1        
            //   185: invokevirtual   java/io/File.mkdirs:()Z
            //   188: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
            //   191: aastore        
            //   192: dup            
            //   193: iconst_2       
            //   194: aload_1        
            //   195: invokevirtual   java/io/File.exists:()Z
            //   198: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
            //   201: aastore        
            //   202: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
            //   205: iconst_0       
            //   206: istore          6
            //   208: iload           6
            //   210: iconst_2       
            //   211: if_icmpge       582
            //   214: ldc             "TextureFetchRequest: getting connection"
            //   216: iconst_0       
            //   217: anewarray       Ljava/lang/Object;
            //   220: invokestatic    com/lumiyaviewer/lumiya/Debug.Printf:(Ljava/lang/String;[Ljava/lang/Object;)V
            //   223: new             Lokhttp3/Request$Builder;
            //   226: astore_1       
            //   227: aload_1        
            //   228: invokespecial   okhttp3/Request$Builder.<init>:()V
            //   231: aload_1        
            //   232: aload_2        
            //   233: invokevirtual   okhttp3/Request$Builder.url:(Ljava/net/URL;)Lokhttp3/Request$Builder;
            //   236: ldc             "Accept"
            //   238: ldc             "image/x-j2c"
            //   240: invokevirtual   okhttp3/Request$Builder.header:(Ljava/lang/String;Ljava/lang/String;)Lokhttp3/Request$Builder;
            //   243: invokevirtual   okhttp3/Request$Builder.build:()Lokhttp3/Request;
            //   246: astore_1       
            //   247: invokestatic    com/lumiyaviewer/lumiya/slproto/https/SLHTTPSConnection.getOkHttpClient:()Lokhttp3/OkHttpClient;
            //   250: aload_1        
            //   251: invokevirtual   okhttp3/OkHttpClient.newCall:(Lokhttp3/Request;)Lokhttp3/Call;
            //   254: invokeinterface okhttp3/Call.execute:()Lokhttp3/Response;
            //   259: astore          4
            //   261: aload           4
            //   263: ifnonnull       444
            //   266: new             Ljava/io/IOException;
            //   269: astore_1       
            //   270: aload_1        
            //   271: ldc_w           "Null response"
            //   274: invokespecial   java/io/IOException.<init>:(Ljava/lang/String;)V
            //   277: aload_1        
            //   278: athrow         
            //   279: astore_1       
            //   280: iconst_0       
            //   281: istore          7
            //   283: aload_1        
            //   284: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
            //   287: iload           7
            //   289: ifeq            576
            //   292: aload_0        
            //   293: getfield        com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache$TextureFetchRequest.this$0:Lcom/lumiyaviewer/lumiya/res/textures/TextureCompressedCache;
            //   296: invokestatic    com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache.-get1:(Lcom/lumiyaviewer/lumiya/res/textures/TextureCompressedCache;)Ljava/lang/Object;
            //   299: astore_1       
            //   300: aload_1        
            //   301: monitorenter   
            //   302: aload_3        
            //   303: aload_0        
            //   304: getfield        com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache$TextureFetchRequest.compressedFile:Ljava/io/File;
            //   307: invokevirtual   java/io/File.renameTo:(Ljava/io/File;)Z
            //   310: pop            
            //   311: aload_1        
            //   312: monitorexit    
            //   313: aload_0        
            //   314: aload_0        
            //   315: getfield        com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache$TextureFetchRequest.compressedFile:Ljava/io/File;
            //   318: invokevirtual   com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache$TextureFetchRequest.completeRequest:(Ljava/io/File;)V
            //   321: return         
            //   322: aload           5
            //   324: ifnull          58
            //   327: aload_2        
            //   328: astore_1       
            //   329: aload_2        
            //   330: ldc_w           "/"
            //   333: invokevirtual   java/lang/String.endsWith:(Ljava/lang/String;)Z
            //   336: ifne            362
            //   339: new             Ljava/lang/StringBuilder;
            //   342: astore_1       
            //   343: aload_1        
            //   344: invokespecial   java/lang/StringBuilder.<init>:()V
            //   347: aload_1        
            //   348: aload_2        
            //   349: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   352: ldc_w           "/"
            //   355: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   358: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   361: astore_1       
            //   362: new             Ljava/lang/StringBuilder;
            //   365: astore_2       
            //   366: aload_2        
            //   367: invokespecial   java/lang/StringBuilder.<init>:()V
            //   370: new             Ljava/net/URL;
            //   373: dup            
            //   374: aload_2        
            //   375: aload_1        
            //   376: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   379: ldc_w           "texture/"
            //   382: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   385: aload           4
            //   387: invokevirtual   java/util/UUID.toString:()Ljava/lang/String;
            //   390: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   393: ldc_w           "/"
            //   396: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   399: aload           5
            //   401: invokevirtual   com/lumiyaviewer/lumiya/slproto/avatar/AvatarTextureFaceIndex.getBakedTextureName:()Ljava/lang/String;
            //   404: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   407: ldc_w           "/"
            //   410: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   413: aload_3        
            //   414: invokevirtual   com/lumiyaviewer/lumiya/render/tex/DrawableTextureParams.uuid:()Ljava/util/UUID;
            //   417: invokevirtual   java/util/UUID.toString:()Ljava/lang/String;
            //   420: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   423: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   426: invokespecial   java/net/URL.<init>:(Ljava/lang/String;)V
            //   429: astore_2       
            //   430: goto            97
            //   433: astore_1       
            //   434: aload_1        
            //   435: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
            //   438: aload_0        
            //   439: aconst_null    
            //   440: invokevirtual   com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache$TextureFetchRequest.completeRequest:(Ljava/io/File;)V
            //   443: return         
            //   444: aload           4
            //   446: invokevirtual   okhttp3/Response.isSuccessful:()Z
            //   449: ifne            509
            //   452: new             Ljava/io/IOException;
            //   455: astore          5
            //   457: new             Ljava/lang/StringBuilder;
            //   460: astore_1       
            //   461: aload_1        
            //   462: invokespecial   java/lang/StringBuilder.<init>:()V
            //   465: aload           5
            //   467: aload_1        
            //   468: ldc_w           "Response code "
            //   471: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   474: aload           4
            //   476: invokevirtual   okhttp3/Response.code:()I
            //   479: invokestatic    java/lang/Integer.toString:(I)Ljava/lang/String;
            //   482: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   485: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   488: invokespecial   java/io/IOException.<init>:(Ljava/lang/String;)V
            //   491: aload           5
            //   493: athrow         
            //   494: astore_1       
            //   495: iconst_0       
            //   496: istore          7
            //   498: aload           4
            //   500: invokevirtual   okhttp3/Response.close:()V
            //   503: aload_1        
            //   504: athrow         
            //   505: astore_1       
            //   506: goto            283
            //   509: aload           4
            //   511: invokevirtual   okhttp3/Response.body:()Lokhttp3/ResponseBody;
            //   514: invokevirtual   okhttp3/ResponseBody.byteStream:()Ljava/io/InputStream;
            //   517: astore          5
            //   519: new             Ljava/io/BufferedOutputStream;
            //   522: astore_1       
            //   523: new             Ljava/io/FileOutputStream;
            //   526: astore          8
            //   528: aload           8
            //   530: aload_3        
            //   531: invokespecial   java/io/FileOutputStream.<init>:(Ljava/io/File;)V
            //   534: aload_1        
            //   535: aload           8
            //   537: invokespecial   java/io/BufferedOutputStream.<init>:(Ljava/io/OutputStream;)V
            //   540: aload           5
            //   542: aload_1        
            //   543: invokestatic    com/google/common/io/ByteStreams.copy:(Ljava/io/InputStream;Ljava/io/OutputStream;)J
            //   546: pop2           
            //   547: aload_1        
            //   548: invokevirtual   java/io/BufferedOutputStream.close:()V
            //   551: aload           4
            //   553: invokevirtual   okhttp3/Response.close:()V
            //   556: iconst_1       
            //   557: istore          7
            //   559: goto            287
            //   562: astore          5
            //   564: aload_1        
            //   565: invokevirtual   java/io/BufferedOutputStream.close:()V
            //   568: aload           5
            //   570: athrow         
            //   571: astore_2       
            //   572: aload_1        
            //   573: monitorexit    
            //   574: aload_2        
            //   575: athrow         
            //   576: iinc            6, 1
            //   579: goto            208
            //   582: aload_0        
            //   583: getfield        com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache$TextureFetchRequest.fetchTask:Ljava/util/concurrent/Future;
            //   586: invokeinterface java/util/concurrent/Future.isCancelled:()Z
            //   591: ifne            611
            //   594: ldc_w           "TextureFetchRequest: HTTP fetch unsuccessful. Trying UDP."
            //   597: invokestatic    com/lumiyaviewer/lumiya/Debug.Log:(Ljava/lang/String;)V
            //   600: aload_0        
            //   601: getfield        com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache$TextureFetchRequest.this$0:Lcom/lumiyaviewer/lumiya/res/textures/TextureCompressedCache;
            //   604: invokestatic    com/lumiyaviewer/lumiya/res/textures/TextureCompressedCache.-get0:(Lcom/lumiyaviewer/lumiya/res/textures/TextureCompressedCache;)Lcom/lumiyaviewer/lumiya/res/executors/StartingExecutor;
            //   607: aload_0        
            //   608: invokevirtual   com/lumiyaviewer/lumiya/res/executors/StartingExecutor.queueRequest:(Lcom/lumiyaviewer/lumiya/res/executors/Startable;)V
            //   611: return         
            //   612: astore_1       
            //   613: iconst_1       
            //   614: istore          7
            //   616: goto            283
            //   619: astore_1       
            //   620: iconst_1       
            //   621: istore          7
            //   623: goto            498
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                            
            //  -----  -----  -----  -----  --------------------------------
            //  37     49     433    444    Ljava/net/MalformedURLException;
            //  58     97     433    444    Ljava/net/MalformedURLException;
            //  214    261    279    283    Ljava/io/IOException;
            //  266    279    279    283    Ljava/io/IOException;
            //  302    311    571    576    Any
            //  329    362    433    444    Ljava/net/MalformedURLException;
            //  362    430    433    444    Ljava/net/MalformedURLException;
            //  444    494    494    498    Any
            //  498    505    505    509    Ljava/io/IOException;
            //  509    540    494    498    Any
            //  540    547    562    571    Any
            //  547    551    619    626    Any
            //  551    556    612    619    Ljava/io/IOException;
            //  564    571    494    498    Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0322:
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        @Override
        public void start() {
            final SLTextureFetcher fetcher = this.fetcher;
            if (fetcher == null) {
                this.completeRequest(null);
                return;
            }
            final DrawableTextureParams drawableTextureParams = ((ResourceRequest<DrawableTextureParams, ResourceType>)this).getParams();
            synchronized (this) {
                final SLTextureFetchRequest fetchRequest = new SLTextureFetchRequest(drawableTextureParams.uuid(), 0, drawableTextureParams.textureClass(), drawableTextureParams.avatarFaceIndex(), drawableTextureParams.avatarUUID(), this.compressedFile);
                fetchRequest.setOnFetchComplete((SLTextureFetchRequest.TextureFetchCompleteListener)this);
                this.fetchRequest = fetchRequest;
                monitorexit(this);
                fetcher.BeginFetch(fetchRequest);
            }
        }
    }
}
