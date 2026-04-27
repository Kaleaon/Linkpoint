// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.graphics;

import android.util.Log;
import android.graphics.Path;
import java.util.ArrayList;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class PathParser
{
    private static final String LOGTAG = "PathParser";
    
    private static void addNode(final ArrayList<PathDataNode> list, final char c, final float[] array) {
        list.add(new PathDataNode(c, array));
    }
    
    public static boolean canMorph(final PathDataNode[] array, final PathDataNode[] array2) {
        if (array == null || array2 == null) {
            return false;
        }
        if (array.length == array2.length) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i].mType != array2[i].mType || array[i].mParams.length != array2[i].mParams.length) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    static float[] copyOfRange(final float[] array, final int n, int a) {
        if (n > a) {
            throw new IllegalArgumentException();
        }
        final int length = array.length;
        if (n >= 0 && n <= length) {
            a -= n;
            final int min = Math.min(a, length - n);
            final float[] array2 = new float[a];
            System.arraycopy(array, n, array2, 0, min);
            return array2;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public static PathDataNode[] createNodesFromPathData(final String s) {
        if (s != null) {
            final ArrayList list = new ArrayList();
            int i = 1;
            int n = 0;
            while (i < s.length()) {
                final int nextStart = nextStart(s, i);
                final String trim = s.substring(n, nextStart).trim();
                if (trim.length() > 0) {
                    addNode(list, trim.charAt(0), getFloats(trim));
                }
                final int n2 = nextStart + 1;
                n = nextStart;
                i = n2;
            }
            if (i - n == 1 && n < s.length()) {
                addNode(list, s.charAt(n), new float[0]);
            }
            return list.toArray(new PathDataNode[list.size()]);
        }
        return null;
    }
    
    public static Path createPathFromPathData(final String str) {
        final Path path = new Path();
        final PathDataNode[] nodesFromPathData = createNodesFromPathData(str);
        if (nodesFromPathData == null) {
            return null;
        }
        try {
            PathDataNode.nodesToPath(nodesFromPathData, path);
            return path;
        }
        catch (final RuntimeException cause) {
            throw new RuntimeException("Error in parsing " + str, cause);
        }
    }
    
    public static PathDataNode[] deepCopyNodes(final PathDataNode[] array) {
        if (array != null) {
            final PathDataNode[] array2 = new PathDataNode[array.length];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = new PathDataNode(array[i]);
            }
            return array2;
        }
        return null;
    }
    
    private static void extract(final String s, final int n, final ExtractFloatResult extractFloatResult) {
        extractFloatResult.mEndWithNegOrDot = false;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int i;
        for (i = n; i < s.length(); ++i) {
            switch (s.charAt(i)) {
                default: {
                    n2 = 0;
                    break;
                }
                case ' ':
                case ',': {
                    n2 = 0;
                    n4 = 1;
                    break;
                }
                case '-': {
                    if (i != n && n2 == 0) {
                        extractFloatResult.mEndWithNegOrDot = true;
                        n2 = 0;
                        n4 = 1;
                        break;
                    }
                    n2 = 0;
                    break;
                }
                case '.': {
                    if (n3 != 0) {
                        extractFloatResult.mEndWithNegOrDot = true;
                        n2 = 0;
                        n4 = 1;
                        break;
                    }
                    n2 = 0;
                    n3 = 1;
                    break;
                }
                case 'E':
                case 'e': {
                    n2 = 1;
                    break;
                }
            }
            if (n4 != 0) {
                break;
            }
        }
        extractFloatResult.mEndPosition = i;
    }
    
    private static float[] getFloats(final String str) {
        if (str.charAt(0) == 'z' || str.charAt(0) == 'Z') {
            return new float[0];
        }
        try {
            final float[] array = new float[str.length()];
            final ExtractFloatResult extractFloatResult = new ExtractFloatResult();
            final int length = str.length();
            int i = 1;
            int n = 0;
            while (i < length) {
                extract(str, i, extractFloatResult);
                final int mEndPosition = extractFloatResult.mEndPosition;
                if (i < mEndPosition) {
                    array[n] = Float.parseFloat(str.substring(i, mEndPosition));
                    ++n;
                }
                if (!extractFloatResult.mEndWithNegOrDot) {
                    i = mEndPosition + 1;
                }
                else {
                    i = mEndPosition;
                }
            }
            return copyOfRange(array, 0, n);
        }
        catch (final NumberFormatException cause) {
            throw new RuntimeException("error in parsing \"" + str + "\"", cause);
        }
    }
    
    private static int nextStart(final String s, int i) {
        while (i < s.length()) {
            final char char1 = s.charAt(i);
            if ((char1 - 'A') * (char1 - 'Z') <= 0 || (char1 - 'a') * (char1 - 'z') <= 0) {
                if (char1 != 'e' && char1 != 'E') {
                    return i;
                }
            }
            ++i;
        }
        return i;
    }
    
    public static void updateNodes(final PathDataNode[] array, final PathDataNode[] array2) {
        for (int i = 0; i < array2.length; ++i) {
            array[i].mType = array2[i].mType;
            for (int j = 0; j < array2[i].mParams.length; ++j) {
                array[i].mParams[j] = array2[i].mParams[j];
            }
        }
    }
    
    private static class ExtractFloatResult
    {
        int mEndPosition;
        boolean mEndWithNegOrDot;
        
        ExtractFloatResult() {
        }
    }
    
    public static class PathDataNode
    {
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        public float[] mParams;
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        public char mType;
        
        PathDataNode(final char c, final float[] mParams) {
            this.mType = c;
            this.mParams = mParams;
        }
        
        PathDataNode(final PathDataNode pathDataNode) {
            this.mType = pathDataNode.mType;
            this.mParams = PathParser.copyOfRange(pathDataNode.mParams, 0, pathDataNode.mParams.length);
        }
        
        private static void addCommand(final Path path, final float[] array, char c, final char c2, final float[] array2) {
            float n = array[0];
            float n2 = array[1];
            float n3 = array[2];
            float n4 = array[3];
            float n5 = array[4];
            float n6 = array[5];
            int n7 = 0;
            switch (c2) {
                default: {
                    n7 = 2;
                    break;
                }
                case 'Z':
                case 'z': {
                    path.close();
                    path.moveTo(n5, n6);
                    n4 = n6;
                    n3 = n5;
                    n2 = n6;
                    n = n5;
                    n7 = 2;
                    break;
                }
                case 'L':
                case 'M':
                case 'T':
                case 'l':
                case 'm':
                case 't': {
                    n7 = 2;
                    break;
                }
                case 'H':
                case 'V':
                case 'h':
                case 'v': {
                    n7 = 1;
                    break;
                }
                case 'C':
                case 'c': {
                    n7 = 6;
                    break;
                }
                case 'Q':
                case 'S':
                case 'q':
                case 's': {
                    n7 = 4;
                    break;
                }
                case 'A':
                case 'a': {
                    n7 = 7;
                    break;
                }
            }
            float n9 = 0.0f;
            float n10 = 0.0f;
            float n11 = 0.0f;
            float n71;
            for (int i = 0; i < array2.length; i += n7, n71 = n10, c = c2, n4 = n9, n6 = n11, n5 = n71) {
                switch (c2) {
                    default: {
                        final float n8 = n6;
                        n9 = n4;
                        n10 = n5;
                        n11 = n8;
                        break;
                    }
                    case 'm': {
                        n += array2[i + 0];
                        n2 += array2[i + 1];
                        if (i <= 0) {
                            path.rMoveTo(array2[i + 0], array2[i + 1]);
                            final float n12 = n2;
                            final float n13 = n;
                            n9 = n4;
                            n11 = n2;
                            n10 = n;
                            n2 = n12;
                            n = n13;
                            break;
                        }
                        path.rLineTo(array2[i + 0], array2[i + 1]);
                        final float n14 = n5;
                        n11 = n6;
                        n9 = n4;
                        n10 = n14;
                        break;
                    }
                    case 'M': {
                        final float n15 = array2[i + 0];
                        final float n16 = array2[i + 1];
                        if (i <= 0) {
                            path.moveTo(array2[i + 0], array2[i + 1]);
                            final float n17 = n16;
                            final float n18 = n15;
                            n9 = n4;
                            n11 = n16;
                            n10 = n15;
                            n2 = n17;
                            n = n18;
                            break;
                        }
                        path.lineTo(array2[i + 0], array2[i + 1]);
                        final float n19 = n15;
                        final float n20 = n5;
                        n11 = n6;
                        n9 = n4;
                        n10 = n20;
                        n2 = n16;
                        n = n19;
                        break;
                    }
                    case 'l': {
                        path.rLineTo(array2[i + 0], array2[i + 1]);
                        final float n21 = array2[i + 0];
                        final float n22 = array2[i + 1];
                        final float n23 = n5;
                        n2 += n22;
                        n += n21;
                        n11 = n6;
                        n9 = n4;
                        n10 = n23;
                        break;
                    }
                    case 'L': {
                        path.lineTo(array2[i + 0], array2[i + 1]);
                        n = array2[i + 0];
                        final float n24 = array2[i + 1];
                        final float n25 = n5;
                        n11 = n6;
                        n9 = n4;
                        n10 = n25;
                        n2 = n24;
                        break;
                    }
                    case 'h': {
                        path.rLineTo(array2[i + 0], 0.0f);
                        final float n26 = array2[i + 0];
                        final float n27 = n5;
                        n += n26;
                        n11 = n6;
                        n9 = n4;
                        n10 = n27;
                        break;
                    }
                    case 'H': {
                        path.lineTo(array2[i + 0], n2);
                        n = array2[i + 0];
                        final float n28 = n5;
                        n11 = n6;
                        n9 = n4;
                        n10 = n28;
                        break;
                    }
                    case 'v': {
                        path.rLineTo(0.0f, array2[i + 0]);
                        final float n29 = array2[i + 0];
                        final float n30 = n5;
                        n2 += n29;
                        n11 = n6;
                        n9 = n4;
                        n10 = n30;
                        break;
                    }
                    case 'V': {
                        path.lineTo(n, array2[i + 0]);
                        n2 = array2[i + 0];
                        final float n31 = n5;
                        n11 = n6;
                        n9 = n4;
                        n10 = n31;
                        break;
                    }
                    case 'c': {
                        path.rCubicTo(array2[i + 0], array2[i + 1], array2[i + 2], array2[i + 3], array2[i + 4], array2[i + 5]);
                        final float n32 = array2[i + 2];
                        final float n33 = array2[i + 3];
                        final float n34 = array2[i + 4];
                        final float n35 = array2[i + 5];
                        n10 = n5;
                        n3 = n + n32;
                        final float n36 = n2 + n35;
                        n += n34;
                        final float n37 = n33 + n2;
                        n11 = n6;
                        n9 = n37;
                        n2 = n36;
                        break;
                    }
                    case 'C': {
                        path.cubicTo(array2[i + 0], array2[i + 1], array2[i + 2], array2[i + 3], array2[i + 4], array2[i + 5]);
                        n = array2[i + 4];
                        n2 = array2[i + 5];
                        n3 = array2[i + 2];
                        final float n38 = array2[i + 3];
                        n10 = n5;
                        n11 = n6;
                        n9 = n38;
                        break;
                    }
                    case 's': {
                        float n39;
                        float n40;
                        if (c != 'c' && c != 's' && c != 'C' && c != 'S') {
                            n39 = 0.0f;
                            n40 = 0.0f;
                        }
                        else {
                            final float n41 = n - n3;
                            n39 = n2 - n4;
                            n40 = n41;
                        }
                        path.rCubicTo(n40, n39, array2[i + 0], array2[i + 1], array2[i + 2], array2[i + 3]);
                        final float n42 = array2[i + 0];
                        final float n43 = array2[i + 1];
                        final float n44 = array2[i + 2];
                        final float n45 = array2[i + 3];
                        n10 = n5;
                        n3 = n + n42;
                        final float n46 = n2 + n45;
                        n += n44;
                        final float n47 = n43 + n2;
                        n11 = n6;
                        n9 = n47;
                        n2 = n46;
                        break;
                    }
                    case 'S': {
                        float n48;
                        if (c != 'c' && c != 's' && c != 'C' && c != 'S') {
                            n48 = n;
                        }
                        else {
                            final float n49 = 2.0f * n - n3;
                            n2 = 2.0f * n2 - n4;
                            n48 = n49;
                        }
                        path.cubicTo(n48, n2, array2[i + 0], array2[i + 1], array2[i + 2], array2[i + 3]);
                        n3 = array2[i + 0];
                        final float n50 = array2[i + 1];
                        n = array2[i + 2];
                        n2 = array2[i + 3];
                        n10 = n5;
                        n11 = n6;
                        n9 = n50;
                        break;
                    }
                    case 'q': {
                        path.rQuadTo(array2[i + 0], array2[i + 1], array2[i + 2], array2[i + 3]);
                        final float n51 = array2[i + 0];
                        final float n52 = array2[i + 1];
                        final float n53 = array2[i + 2];
                        final float n54 = array2[i + 3];
                        n10 = n5;
                        n3 = n + n51;
                        final float n55 = n2 + n54;
                        n += n53;
                        final float n56 = n52 + n2;
                        n11 = n6;
                        n9 = n56;
                        n2 = n55;
                        break;
                    }
                    case 'Q': {
                        path.quadTo(array2[i + 0], array2[i + 1], array2[i + 2], array2[i + 3]);
                        n3 = array2[i + 0];
                        final float n57 = array2[i + 1];
                        n = array2[i + 2];
                        n2 = array2[i + 3];
                        n10 = n5;
                        n11 = n6;
                        n9 = n57;
                        break;
                    }
                    case 't': {
                        float n58;
                        float n59;
                        if (c != 'q' && c != 't' && c != 'Q' && c != 'T') {
                            n58 = 0.0f;
                            n59 = 0.0f;
                        }
                        else {
                            n59 = n - n3;
                            n58 = n2 - n4;
                        }
                        path.rQuadTo(n59, n58, array2[i + 0], array2[i + 1]);
                        final float n60 = array2[i + 0];
                        final float n61 = array2[i + 1];
                        n10 = n5;
                        final float n62 = n + n59;
                        final float n63 = n2 + n61;
                        n += n60;
                        final float n64 = n58 + n2;
                        n11 = n6;
                        n9 = n64;
                        n3 = n62;
                        n2 = n63;
                        break;
                    }
                    case 'T': {
                        if (c == 'q' || c == 't' || c == 'Q' || c == 'T') {
                            n = 2.0f * n - n3;
                            n2 = 2.0f * n2 - n4;
                        }
                        path.quadTo(n, n2, array2[i + 0], array2[i + 1]);
                        final float n65 = array2[i + 0];
                        final float n66 = array2[i + 1];
                        final float n67 = n2;
                        n3 = n;
                        n2 = n66;
                        n = n65;
                        final float n68 = n5;
                        n11 = n6;
                        n9 = n67;
                        n10 = n68;
                        break;
                    }
                    case 'a': {
                        drawArc(path, n, n2, array2[i + 5] + n, array2[i + 6] + n2, array2[i + 0], array2[i + 1], array2[i + 2], array2[i + 3] != 0.0f, array2[i + 4] != 0.0f);
                        n += array2[i + 5];
                        final float n69 = array2[i + 6] + n2;
                        n10 = n5;
                        n3 = n;
                        n2 = n69;
                        n11 = n6;
                        n9 = n69;
                        break;
                    }
                    case 'A': {
                        drawArc(path, n, n2, array2[i + 5], array2[i + 6], array2[i + 0], array2[i + 1], array2[i + 2], array2[i + 3] != 0.0f, array2[i + 4] != 0.0f);
                        n = array2[i + 5];
                        final float n70 = array2[i + 6];
                        n10 = n5;
                        n3 = n;
                        n2 = n70;
                        n11 = n6;
                        n9 = n70;
                        break;
                    }
                }
            }
            array[0] = n;
            array[1] = n2;
            array[2] = n3;
            array[3] = n4;
            array[4] = n5;
            array[5] = n6;
        }
        
        private static void arcToBezier(final Path path, final double n, final double n2, final double n3, final double n4, double n5, double n6, double n7, double n8, double sin) {
            final int n9 = (int)Math.ceil(Math.abs(4.0 * sin / 3.141592653589793));
            final double cos = Math.cos(n7);
            final double sin2 = Math.sin(n7);
            final double cos2 = Math.cos(n8);
            final double sin3 = Math.sin(n8);
            final double n10 = -n3;
            n7 = -n3;
            final double n11 = sin / n9;
            int i = 0;
            n7 = sin3 * (n7 * sin2) + cos2 * (n4 * cos);
            final double n12 = n10 * cos * sin3 - n4 * sin2 * cos2;
            sin = n8;
            n8 = n12;
            while (i < n9) {
                final double n13 = sin + n11;
                final double sin4 = Math.sin(n13);
                final double cos3 = Math.cos(n13);
                final double n14 = n3 * cos * cos3 + n - n4 * sin2 * sin4;
                final double n15 = n4 * cos * sin4 + (n3 * sin2 * cos3 + n2);
                final double n16 = -n3 * cos * sin4 - n4 * sin2 * cos3;
                final double n17 = cos3 * (n4 * cos) + sin4 * (-n3 * sin2);
                final double tan = Math.tan((n13 - sin) / 2.0);
                sin = Math.sin(n13 - sin);
                sin = (Math.sqrt(tan * (3.0 * tan) + 4.0) - 1.0) * sin / 3.0;
                path.rLineTo(0.0f, 0.0f);
                path.cubicTo((float)(n8 * sin + n5), (float)(n6 + n7 * sin), (float)(n14 - sin * n16), (float)(n15 - sin * n17), (float)n14, (float)n15);
                ++i;
                n8 = n16;
                sin = n13;
                n6 = n15;
                n5 = n14;
                n7 = n17;
            }
        }
        
        private static void drawArc(final Path path, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final boolean b, final boolean b2) {
            final double radians = Math.toRadians(n7);
            final double cos = Math.cos(radians);
            final double sin = Math.sin(radians);
            final double n8 = (n * cos + n2 * sin) / n5;
            final double n9 = (-n * sin + n2 * cos) / n6;
            final double n10 = (n3 * cos + n4 * sin) / n5;
            final double n11 = (-n3 * sin + n4 * cos) / n6;
            final double n12 = n8 - n10;
            final double n13 = n9 - n11;
            final double n14 = (n8 + n10) / 2.0;
            final double n15 = (n9 + n11) / 2.0;
            final double n16 = n12 * n12 + n13 * n13;
            if (n16 == 0.0) {
                Log.w("PathParser", " Points are coincident");
                return;
            }
            final double a = 1.0 / n16 - 0.25;
            if (a < 0.0) {
                Log.w("PathParser", "Points are too far apart " + n16);
                final float n17 = (float)(Math.sqrt(n16) / 1.99999);
                drawArc(path, n, n2, n3, n4, n5 * n17, n6 * n17, n7, b, b2);
                return;
            }
            final double sqrt = Math.sqrt(a);
            final double n18 = n12 * sqrt;
            final double n19 = n13 * sqrt;
            double n20;
            double n21;
            if (b != b2) {
                n20 = n19 + n14;
                n21 = n15 - n18;
            }
            else {
                n20 = n14 - n19;
                n21 = n18 + n15;
            }
            final double atan2 = Math.atan2(n9 - n21, n8 - n20);
            double n22 = Math.atan2(n11 - n21, n10 - n20) - atan2;
            if (b2 != n22 >= 0.0) {
                if (n22 > 0.0) {
                    n22 -= 6.283185307179586;
                }
                else {
                    n22 += 6.283185307179586;
                }
            }
            final double n23 = n5 * n20;
            final double n24 = n21 * n6;
            arcToBezier(path, n23 * cos - n24 * sin, n23 * sin + n24 * cos, n5, n6, n, n2, radians, atan2, n22);
        }
        
        public static void nodesToPath(final PathDataNode[] array, final Path path) {
            final float[] array2 = new float[6];
            final char c = 'm';
            int i = 0;
            char c2 = c;
            while (i < array.length) {
                addCommand(path, array2, c2, array[i].mType, array[i].mParams);
                final char mType = array[i].mType;
                ++i;
                c2 = mType;
            }
        }
        
        public void interpolatePathDataNode(final PathDataNode pathDataNode, final PathDataNode pathDataNode2, final float n) {
            for (int i = 0; i < pathDataNode.mParams.length; ++i) {
                this.mParams[i] = pathDataNode.mParams[i] * (1.0f - n) + pathDataNode2.mParams[i] * n;
            }
        }
    }
}
