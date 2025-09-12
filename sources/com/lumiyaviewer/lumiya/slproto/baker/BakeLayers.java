// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.baker;

import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.BakedTextureIndex;
import java.util.EnumMap;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.baker:
//            BakeLayer, SLAvatarGlobalColor, BakeLayerSet

public class BakeLayers
{

    public static Map layerSets;

    public BakeLayers()
    {
    }

    static 
    {
        layerSets = new EnumMap(com/lumiyaviewer/lumiya/slproto/avatar/BakedTextureIndex);
        Object obj = new BakeLayer("base", SLAvatarGlobalColor.hair_color, false, 0, false, false, true, AvatarTextureFaceIndex.TEX_HAIR, false, null, false, new int[0]);
        BakeLayer bakelayer = new BakeLayer("hair texture alpha layer", null, false, 0, false, true, false, AvatarTextureFaceIndex.TEX_HAIR, false, null, false, new int[0]);
        BakeLayer bakelayer1 = new BakeLayer("hair alpha", null, false, 0, false, true, false, AvatarTextureFaceIndex.TEX_HAIR_ALPHA, false, null, false, new int[0]);
        obj = new BakeLayerSet(BakedTextureIndex.BAKED_HAIR, 512, 512, false, new BakeLayer[] {
            obj
        }, new BakeLayer[] {
            bakelayer, bakelayer1
        });
        layerSets.put(BakedTextureIndex.BAKED_HAIR, obj);
        obj = new BakeLayer("head bump base", null, true, 0xff808080, true, false, false, null, false, null, false, new int[0]);
        bakelayer = new BakeLayer("head bump definition", null, false, 0, true, false, false, null, false, "bump_head_base.tga", false, new int[] {
            873
        });
        bakelayer1 = new BakeLayer("base", SLAvatarGlobalColor.skin_color, false, 0, false, false, false, null, false, "head_skingrain.tga", false, new int[0]);
        BakeLayer bakelayer2 = new BakeLayer("headcolor", null, false, 0, false, false, false, null, false, "head_color.tga", false, new int[0]);
        BakeLayer bakelayer3 = new BakeLayer("shadow", null, false, 0, false, false, false, null, false, "head_shading_alpha.tga", true, new int[] {
            158
        });
        BakeLayer bakelayer4 = new BakeLayer("highlight", null, false, 0, false, false, false, null, false, "head_highlights_alpha.tga", true, new int[] {
            159
        });
        BakeLayer bakelayer5 = new BakeLayer("rosyface", null, false, 0, false, false, false, null, false, "rosyface_alpha.tga", true, new int[] {
            116
        });
        BakeLayer bakelayer6 = new BakeLayer("lips", null, false, 0, false, false, false, null, false, "lips_mask.tga", true, new int[] {
            117
        });
        BakeLayer bakelayer7 = new BakeLayer("wrinkles_shading", null, true, 0x64000000, true, false, false, null, false, null, false, new int[] {
            118
        });
        BakeLayer bakelayer8 = new BakeLayer("freckles", null, true, 0x80142f78, false, false, false, null, false, null, false, new int[] {
            165
        });
        BakeLayer bakelayer9 = new BakeLayer("eyebrowsbump", null, false, 0, true, false, false, null, false, "head_hair.tga", false, new int[] {
            1000, 1002
        });
        BakeLayer bakelayer10 = new BakeLayer("eyebrows", SLAvatarGlobalColor.hair_color, false, 0, false, false, false, null, false, "head_hair.tga", false, new int[] {
            1001, 1003
        });
        BakeLayer bakelayer11 = new BakeLayer("lipstick", null, false, 0, false, false, false, null, false, null, false, new int[] {
            700, 701
        });
        BakeLayer bakelayer12 = new BakeLayer("lipgloss", null, true, 0xbeffffff, false, false, false, null, false, null, false, new int[] {
            702
        });
        BakeLayer bakelayer13 = new BakeLayer("blush", null, false, 0, false, false, false, null, false, null, false, new int[] {
            704, 705, 711
        });
        BakeLayer bakelayer14 = new BakeLayer("Outer Eye Shadow", null, false, 0, false, false, false, null, false, null, false, new int[] {
            708, 706, 707
        });
        BakeLayer bakelayer15 = new BakeLayer("Inner Eye Shadow", null, false, 0, false, false, false, null, false, null, false, new int[] {
            712, 713, 709
        });
        BakeLayer bakelayer16 = new BakeLayer("eyeliner", null, true, 0xc8000000, false, false, false, null, false, null, false, new int[] {
            703, 714
        });
        BakeLayer bakelayer17 = new BakeLayer("facialhair bump", null, false, 0, true, false, false, null, false, "head_hair.tga", false, new int[] {
            1004, 1006, 1008, 1010, 1012
        });
        BakeLayer bakelayer18 = new BakeLayer("facialhair", SLAvatarGlobalColor.hair_color, false, 0, false, false, false, null, false, "head_hair.tga", false, new int[] {
            1005, 1007, 1009, 1011, 751
        });
        BakeLayer bakelayer19 = new BakeLayer("head_bodypaint", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_HEAD_BODYPAINT, false, null, false, new int[0]);
        BakeLayer bakelayer20 = new BakeLayer("eyelash alpha", null, false, 0, false, true, false, null, false, "head_alpha.tga", true, new int[0]);
        BakeLayer bakelayer21 = new BakeLayer("head alpha", null, false, 0, false, true, false, AvatarTextureFaceIndex.TEX_HEAD_ALPHA, false, null, false, new int[0]);
        BakeLayer bakelayer22 = new BakeLayer("head_tattoo", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_HEAD_TATTOO, false, null, false, new int[] {
            1062, 1063, 1064
        });
        obj = new BakeLayerSet(BakedTextureIndex.BAKED_HEAD, 512, 512, true, new BakeLayer[] {
            obj, bakelayer, bakelayer1, bakelayer2, bakelayer3, bakelayer4, bakelayer5, bakelayer6, bakelayer7, bakelayer8, 
            bakelayer9, bakelayer10, bakelayer11, bakelayer12, bakelayer13, bakelayer14, bakelayer15, bakelayer16, bakelayer17, bakelayer18, 
            bakelayer19, bakelayer22
        }, new BakeLayer[] {
            bakelayer20, bakelayer21
        });
        layerSets.put(BakedTextureIndex.BAKED_HEAD, obj);
        obj = new BakeLayer("base_upperbody bump", null, true, 0xff808080, true, false, false, null, false, null, false, new int[0]);
        bakelayer = new BakeLayer("upperbody bump definition", null, false, 0, true, false, false, null, false, "bump_upperbody_base.tga", false, new int[] {
            874
        });
        bakelayer1 = new BakeLayer("base", SLAvatarGlobalColor.skin_color, false, 0, false, false, false, null, false, "body_skingrain.tga", false, new int[0]);
        bakelayer2 = new BakeLayer("nipples", null, false, 0, false, false, false, null, false, "upperbody_color.tga", false, new int[0]);
        bakelayer3 = new BakeLayer("shadow", null, false, 0, false, false, false, null, false, "upperbody_shading_alpha.tga", true, new int[] {
            125
        });
        bakelayer4 = new BakeLayer("highlight", null, false, 0, false, false, false, null, false, "upperbody_highlights_alpha.tga", true, new int[] {
            126
        });
        bakelayer5 = new BakeLayer("upper_bodypaint", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_UPPER_BODYPAINT, false, null, false, new int[0]);
        bakelayer6 = new BakeLayer("freckles upper", null, true, 0x80142f78, false, false, false, null, false, null, false, new int[] {
            776
        });
        bakelayer7 = new BakeLayer("upper_tattoo", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_UPPER_TATTOO, false, null, false, new int[] {
            1065, 1066, 1067
        });
        bakelayer8 = new BakeLayer("upper_undershirt bump", null, true, 0xff808080, true, false, false, AvatarTextureFaceIndex.TEX_UPPER_UNDERSHIRT, true, null, false, new int[] {
            1043, 1045, 1047, 1049
        });
        bakelayer9 = new BakeLayer("upper_undershirt", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_UPPER_UNDERSHIRT, false, null, false, new int[] {
            821, 822, 823, 1042, 1044, 1046, 1048
        });
        bakelayer10 = new BakeLayer("Nail Polish", null, false, 0, false, false, false, null, false, null, false, new int[] {
            710, 715
        });
        bakelayer11 = new BakeLayer("upper_gloves bump", null, true, 0xff808080, true, false, false, AvatarTextureFaceIndex.TEX_UPPER_GLOVES, true, null, false, new int[] {
            1059, 1061
        });
        bakelayer12 = new BakeLayer("upper_gloves", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_UPPER_GLOVES, false, null, false, new int[] {
            827, 829, 830, 1058, 1060
        });
        bakelayer13 = new BakeLayer("upper_clothes_shadow", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_UPPER_SHIRT, false, null, false, new int[] {
            899, 900, 901, 902, 903
        });
        bakelayer14 = new BakeLayer("upper_shirt base bump", null, true, 0xff808080, true, false, false, AvatarTextureFaceIndex.TEX_UPPER_SHIRT, true, null, false, new int[] {
            1029, 1030, 1031, 1032
        });
        bakelayer15 = new BakeLayer("upper_clothes bump", null, false, 0, true, false, false, AvatarTextureFaceIndex.TEX_UPPER_SHIRT, true, "bump_shirt_wrinkles.tga", false, new int[] {
            868, 1013, 1014, 1015, 1016
        });
        bakelayer16 = new BakeLayer("upper_clothes", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_UPPER_SHIRT, false, null, false, new int[] {
            803, 804, 805, 600, 601, 602, 778
        });
        bakelayer17 = new BakeLayer("upper_jacket base bump", null, true, 0xff808080, true, false, false, AvatarTextureFaceIndex.TEX_UPPER_JACKET, true, null, false, new int[] {
            1039, 1040, 1041, 1037, 1038
        });
        bakelayer18 = new BakeLayer("upper_jacket bump", null, false, 0, true, false, false, AvatarTextureFaceIndex.TEX_UPPER_JACKET, true, "bump_shirt_wrinkles.tga", false, new int[] {
            875, 1019, 1021, 1023, 1025, 1026
        });
        bakelayer19 = new BakeLayer("upper_jacket", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_UPPER_JACKET, false, null, false, new int[] {
            831, 832, 833, 1020, 1022, 1024, 620, 622
        });
        bakelayer20 = new BakeLayer("upper alpha", null, false, 0, false, true, false, AvatarTextureFaceIndex.TEX_UPPER_ALPHA, false, null, false, new int[0]);
        obj = new BakeLayerSet(BakedTextureIndex.BAKED_UPPER, 512, 512, true, new BakeLayer[] {
            obj, bakelayer, bakelayer1, bakelayer2, bakelayer3, bakelayer4, bakelayer5, bakelayer6, bakelayer7, bakelayer8, 
            bakelayer9, bakelayer10, bakelayer11, bakelayer12, bakelayer13, bakelayer14, bakelayer15, bakelayer16, bakelayer17, bakelayer18, 
            bakelayer19
        }, new BakeLayer[] {
            bakelayer20
        });
        layerSets.put(BakedTextureIndex.BAKED_UPPER, obj);
        obj = new BakeLayer("lower body bump base", null, true, 0xff808080, true, false, false, null, false, null, false, new int[0]);
        bakelayer = new BakeLayer("base_lowerbody bump", null, false, 0, true, false, false, null, false, "bump_lowerbody_base.tga", false, new int[] {
            878
        });
        bakelayer1 = new BakeLayer("base", SLAvatarGlobalColor.skin_color, false, 0, false, false, false, null, false, "body_skingrain.tga", false, new int[0]);
        bakelayer2 = new BakeLayer("shadow", null, false, 0, false, false, false, null, false, "lowerbody_shading_alpha.tga", true, new int[] {
            160
        });
        bakelayer3 = new BakeLayer("highlight", null, false, 0, false, false, false, null, false, "lowerbody_highlights_alpha.tga", true, new int[] {
            161
        });
        bakelayer4 = new BakeLayer("toenails", null, false, 0, false, false, false, null, false, "lowerbody_color.tga", false, new int[0]);
        bakelayer5 = new BakeLayer("lower_bodypaint", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_LOWER_BODYPAINT, false, null, false, new int[0]);
        bakelayer6 = new BakeLayer("freckles lower", null, true, 0x80142f78, false, false, false, null, false, null, false, new int[] {
            777
        });
        bakelayer7 = new BakeLayer("lower_tattoo", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_LOWER_TATTOO, false, null, false, new int[] {
            1068, 1069, 1070
        });
        bakelayer8 = new BakeLayer("lower_underpants bump", null, true, 0xff808080, true, false, false, AvatarTextureFaceIndex.TEX_LOWER_UNDERPANTS, true, null, false, new int[] {
            1055, 1057
        });
        bakelayer9 = new BakeLayer("lower_underpants", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_LOWER_UNDERPANTS, false, null, false, new int[] {
            824, 825, 826, 1054, 1056
        });
        bakelayer10 = new BakeLayer("lower_socks bump", null, true, 0xff808080, true, false, false, AvatarTextureFaceIndex.TEX_LOWER_SOCKS, true, null, false, new int[] {
            1051
        });
        bakelayer11 = new BakeLayer("lower_socks", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_LOWER_SOCKS, false, null, false, new int[] {
            818, 819, 820, 1050
        });
        bakelayer12 = new BakeLayer("lower_shoes bump", null, true, 0xff808080, true, false, false, AvatarTextureFaceIndex.TEX_LOWER_SHOES, true, null, false, new int[] {
            1053
        });
        bakelayer13 = new BakeLayer("lower_shoes", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_LOWER_SHOES, false, null, false, new int[] {
            812, 813, 817, 1052
        });
        bakelayer14 = new BakeLayer("lower_clothes_shadow", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_LOWER_PANTS, false, null, false, new int[] {
            913, 914, 915
        });
        bakelayer15 = new BakeLayer("lower_pants base bump", null, true, 0xff808080, true, false, false, AvatarTextureFaceIndex.TEX_LOWER_PANTS, true, null, false, new int[] {
            1035, 1036
        });
        bakelayer16 = new BakeLayer("lower_pants bump", null, false, 0, true, false, false, AvatarTextureFaceIndex.TEX_LOWER_PANTS, true, "bump_pants_wrinkles.tga", false, new int[] {
            869, 1017, 1018
        });
        bakelayer17 = new BakeLayer("lower_pants", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_LOWER_PANTS, false, null, false, new int[] {
            806, 807, 808, 614, 615
        });
        bakelayer18 = new BakeLayer("lower_jacket base bump", null, true, 0xff808080, true, false, false, AvatarTextureFaceIndex.TEX_LOWER_JACKET, true, null, false, new int[] {
            1033, 1034
        });
        bakelayer19 = new BakeLayer("lower_jacket bump", null, false, 0, true, false, false, AvatarTextureFaceIndex.TEX_LOWER_JACKET, true, "bump_pants_wrinkles.tga", false, new int[] {
            876, 1027, 1028
        });
        bakelayer20 = new BakeLayer("lower_jacket", null, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_LOWER_JACKET, false, null, false, new int[] {
            809, 810, 811, 621, 623
        });
        bakelayer21 = new BakeLayer("lower alpha", null, false, 0, false, true, false, AvatarTextureFaceIndex.TEX_LOWER_ALPHA, false, null, false, new int[0]);
        obj = new BakeLayerSet(BakedTextureIndex.BAKED_LOWER, 512, 512, true, new BakeLayer[] {
            obj, bakelayer, bakelayer1, bakelayer2, bakelayer3, bakelayer4, bakelayer5, bakelayer6, bakelayer7, bakelayer8, 
            bakelayer9, bakelayer10, bakelayer11, bakelayer12, bakelayer13, bakelayer14, bakelayer15, bakelayer16, bakelayer17, bakelayer18, 
            bakelayer19, bakelayer20
        }, new BakeLayer[] {
            bakelayer21
        });
        layerSets.put(BakedTextureIndex.BAKED_LOWER, obj);
        obj = new BakeLayer("whites", null, false, 0, false, false, false, null, false, "eyewhite.tga", false, new int[0]);
        bakelayer = new BakeLayer("iris", SLAvatarGlobalColor.eye_color, false, 0, false, false, false, AvatarTextureFaceIndex.TEX_EYES_IRIS, false, null, false, new int[0]);
        bakelayer1 = new BakeLayer("eyes alpha", null, false, 0, false, true, false, AvatarTextureFaceIndex.TEX_EYES_ALPHA, false, null, false, new int[0]);
        obj = new BakeLayerSet(BakedTextureIndex.BAKED_EYES, 128, 128, true, new BakeLayer[] {
            obj, bakelayer
        }, new BakeLayer[] {
            bakelayer1
        });
        layerSets.put(BakedTextureIndex.BAKED_EYES, obj);
        obj = new BakeLayer("skirt_fabric", null, false, 0, false, false, true, AvatarTextureFaceIndex.TEX_SKIRT, false, null, false, new int[] {
            921, 922, 923
        });
        bakelayer = new BakeLayer("skirt_fabric_alpha", null, false, 0, false, false, false, null, false, null, false, new int[] {
            858, 859, 860, 861, 862
        });
        obj = new BakeLayerSet(BakedTextureIndex.BAKED_SKIRT, 512, 512, false, new BakeLayer[] {
            obj, bakelayer
        }, new BakeLayer[0]);
        layerSets.put(BakedTextureIndex.BAKED_SKIRT, obj);
    }
}
