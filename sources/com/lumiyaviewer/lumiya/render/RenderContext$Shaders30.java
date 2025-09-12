// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import com.lumiyaviewer.lumiya.render.shaders.BoundingBoxProgram;
import com.lumiyaviewer.lumiya.render.shaders.RiggedMeshProgram30;
import com.lumiyaviewer.lumiya.render.shaders.ShaderCompileException;
import com.lumiyaviewer.lumiya.render.shaders.ShaderPreprocessor;

// Referenced classes of package com.lumiyaviewer.lumiya.render:
//            RenderContext

private static class or
{

    final BoundingBoxProgram boundingBoxProgram;
    final RiggedMeshProgram30 riggedMeshProgram30;
    final RiggedMeshProgram30 riggedMeshProgramOpaque30;

    private or(ShaderPreprocessor shaderpreprocessor)
        throws ShaderCompileException
    {
        riggedMeshProgram30 = new RiggedMeshProgram30(false);
        riggedMeshProgramOpaque30 = new RiggedMeshProgram30(true);
        boundingBoxProgram = new BoundingBoxProgram();
        riggedMeshProgram30.Compile(shaderpreprocessor);
        riggedMeshProgramOpaque30.Compile(shaderpreprocessor);
        boundingBoxProgram.Compile(shaderpreprocessor);
    }

    or(ShaderPreprocessor shaderpreprocessor, or or)
    {
        this(shaderpreprocessor);
    }
}
