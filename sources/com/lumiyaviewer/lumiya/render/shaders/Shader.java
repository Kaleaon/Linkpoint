// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.shaders;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// Referenced classes of package com.lumiyaviewer.lumiya.render.shaders:
//            ShaderPreprocessor, ShaderCompileException

public final class Shader extends Enum
{

    private static final Shader $VALUES[];
    public static final Shader AvatarVertexShader;
    public static final Shader BoundingBoxFragmentShader;
    public static final Shader BoundingBoxVertexShader;
    public static final Shader ExtTextureFragmentShader;
    public static final Shader ExtTextureVertexShader;
    public static final Shader FXAAFragmentShader;
    public static final Shader FXAAVertexShader;
    public static final Shader FlexiVertexShader;
    public static final Shader PrimFragmentShader;
    public static final Shader PrimFragmentShader30;
    public static final Shader PrimOpaqueFragmentShader;
    public static final Shader PrimOpaqueFragmentShader30;
    public static final Shader PrimVertexShader;
    public static final Shader QuadFragmentShader;
    public static final Shader QuadVertexShader;
    public static final Shader RawFragmentShader;
    public static final Shader RawVertexShader;
    public static final Shader RiggedMeshVertexShader;
    public static final Shader RiggedMeshVertexShader30;
    public static final Shader SkyFragmentShader;
    public static final Shader SkyNoCloudsFragmentShader;
    public static final Shader SkyVertexShader;
    public static final Shader StarsFragmentShader;
    public static final Shader StarsVertexShader;
    public static final Shader WaterFragmentShader;
    public static final Shader WaterVertexShader;
    private final String fileName;
    private int handle;
    private final int type;

    private Shader(String s, int i, int j, String s1)
    {
        super(s, i);
        type = j;
        fileName = s1;
    }

    private String getShaderCode(ShaderPreprocessor shaderpreprocessor)
    {
        try
        {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(LumiyaApp.getAssetManager().open((new StringBuilder()).append("shaders/").append(fileName).toString())));
            shaderpreprocessor = shaderpreprocessor.processCode(bufferedreader);
            bufferedreader.close();
        }
        // Misplaced declaration of an exception variable
        catch (ShaderPreprocessor shaderpreprocessor)
        {
            return null;
        }
        return shaderpreprocessor;
    }

    public static Shader valueOf(String s)
    {
        return (Shader)Enum.valueOf(com/lumiyaviewer/lumiya/render/shaders/Shader, s);
    }

    public static Shader[] values()
    {
        return $VALUES;
    }

    public int Compile(ShaderPreprocessor shaderpreprocessor)
        throws ShaderCompileException
    {
        Debug.Printf("Shaders: Compiling shader '%s'...", new Object[] {
            fileName
        });
        shaderpreprocessor = getShaderCode(shaderpreprocessor);
        if (shaderpreprocessor == null)
        {
            handle = 0;
            throw new ShaderCompileException((new StringBuilder()).append("No shader code for ").append(fileName).toString());
        }
        handle = GLES20.glCreateShader(type);
        GLES20.glShaderSource(handle, shaderpreprocessor);
        GLES20.glCompileShader(handle);
        shaderpreprocessor = new int[1];
        GLES20.glGetShaderiv(handle, 35713, shaderpreprocessor, 0);
        if (shaderpreprocessor[0] != 1)
        {
            shaderpreprocessor = GLES20.glGetShaderInfoLog(handle);
            throw new ShaderCompileException(String.format("Shader (%s) compile error: '%s'", new Object[] {
                fileName, shaderpreprocessor
            }));
        } else
        {
            return handle;
        }
    }

    public int getHandle()
    {
        return handle;
    }

    static 
    {
        PrimFragmentShader = new Shader("PrimFragmentShader", 0, 35632, "prim.fsh");
        PrimFragmentShader30 = new Shader("PrimFragmentShader30", 1, 35632, "prim_30.fsh");
        PrimOpaqueFragmentShader = new Shader("PrimOpaqueFragmentShader", 2, 35632, "prim_opaque.fsh");
        PrimOpaqueFragmentShader30 = new Shader("PrimOpaqueFragmentShader30", 3, 35632, "prim_opaque_30.fsh");
        PrimVertexShader = new Shader("PrimVertexShader", 4, 35633, "prim.vsh");
        AvatarVertexShader = new Shader("AvatarVertexShader", 5, 35633, "avatar.vsh");
        FlexiVertexShader = new Shader("FlexiVertexShader", 6, 35633, "prim_flexible.vsh");
        RiggedMeshVertexShader = new Shader("RiggedMeshVertexShader", 7, 35633, "rigged_mesh.vsh");
        RiggedMeshVertexShader30 = new Shader("RiggedMeshVertexShader30", 8, 35633, "rigged_mesh_30.vsh");
        QuadVertexShader = new Shader("QuadVertexShader", 9, 35633, "quad.vsh");
        QuadFragmentShader = new Shader("QuadFragmentShader", 10, 35632, "quad.fsh");
        BoundingBoxVertexShader = new Shader("BoundingBoxVertexShader", 11, 35633, "bounding_box_30.vsh");
        BoundingBoxFragmentShader = new Shader("BoundingBoxFragmentShader", 12, 35632, "bounding_box_30.fsh");
        WaterVertexShader = new Shader("WaterVertexShader", 13, 35633, "water.vsh");
        WaterFragmentShader = new Shader("WaterFragmentShader", 14, 35632, "water.fsh");
        SkyVertexShader = new Shader("SkyVertexShader", 15, 35633, "sky.vsh");
        SkyFragmentShader = new Shader("SkyFragmentShader", 16, 35632, "sky.fsh");
        SkyNoCloudsFragmentShader = new Shader("SkyNoCloudsFragmentShader", 17, 35632, "sky_no_clouds.fsh");
        StarsVertexShader = new Shader("StarsVertexShader", 18, 35633, "stars.vsh");
        StarsFragmentShader = new Shader("StarsFragmentShader", 19, 35632, "stars.fsh");
        ExtTextureVertexShader = new Shader("ExtTextureVertexShader", 20, 35633, "external_texture.vsh");
        ExtTextureFragmentShader = new Shader("ExtTextureFragmentShader", 21, 35632, "external_texture.fsh");
        RawVertexShader = new Shader("RawVertexShader", 22, 35633, "raw.vsh");
        RawFragmentShader = new Shader("RawFragmentShader", 23, 35632, "raw.fsh");
        FXAAVertexShader = new Shader("FXAAVertexShader", 24, 35633, "fxaa.vsh");
        FXAAFragmentShader = new Shader("FXAAFragmentShader", 25, 35632, "fxaa.fsh");
        $VALUES = (new Shader[] {
            PrimFragmentShader, PrimFragmentShader30, PrimOpaqueFragmentShader, PrimOpaqueFragmentShader30, PrimVertexShader, AvatarVertexShader, FlexiVertexShader, RiggedMeshVertexShader, RiggedMeshVertexShader30, QuadVertexShader, 
            QuadFragmentShader, BoundingBoxVertexShader, BoundingBoxFragmentShader, WaterVertexShader, WaterFragmentShader, SkyVertexShader, SkyFragmentShader, SkyNoCloudsFragmentShader, StarsVertexShader, StarsFragmentShader, 
            ExtTextureVertexShader, ExtTextureFragmentShader, RawVertexShader, RawFragmentShader, FXAAVertexShader, FXAAFragmentShader
        });
    }
}
