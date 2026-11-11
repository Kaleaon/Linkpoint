package android.support.v4.graphics;

import android.graphics.Path;
import android.support.annotation.RestrictTo;
import android.util.Log;
import com.google.common.logging.nano.Vr;
import java.util.ArrayList;
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class PathParser {
    private static final String LOGTAG = "PathParser";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ExtractFloatResult {
        int mEndPosition;
        boolean mEndWithNegOrDot;

        ExtractFloatResult() {
        }
    }

    /* loaded from: classes.dex */
    public static class PathDataNode {
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public float[] mParams;
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public char mType;

        PathDataNode(char c, float[] fArr) {
            this.mType = c;
            this.mParams = fArr;
        }

        PathDataNode(PathDataNode pathDataNode) {
            this.mType = pathDataNode.mType;
            this.mParams = PathParser.copyOfRange(pathDataNode.mParams, 0, pathDataNode.mParams.length);
        }

        private static void addCommand(Path path, float[] fArr, char c, char c2, float[] fArr2) {
            int i;
            float f;
            float f2;
            float f3;
            float f4;
            float f5;
            float f6;
            float f7;
            float f8;
            float f9;
            float f10;
            float f11;
            float f12;
            float f13 = fArr[0];
            float f14 = fArr[1];
            float f15 = fArr[2];
            float f16 = fArr[3];
            float f17 = fArr[4];
            float f18 = fArr[5];
            switch (c2) {
                case 'A':
                case 'a':
                    i = 7;
                    break;
                case 'B':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'I':
                case 'J':
                case 'K':
                case 'N':
                case 'O':
                case 'P':
                case 'R':
                case 'U':
                case 'W':
                case 'X':
                case 'Y':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                case 'b':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'i':
                case 'j':
                case 'k':
                case 'n':
                case 'o':
                case 'p':
                case 'r':
                case 'u':
                case Vr.VREvent.VrCore.ErrorCode.CONTROLLER_UNSUPPORTED /* 119 */:
                case Vr.VREvent.VrCore.ErrorCode.CONTROLLER_INFO_READ_ERROR /* 120 */:
                case Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_DISCOVERY_FAILED /* 121 */:
                default:
                    i = 2;
                    break;
                case 'C':
                case 'c':
                    i = 6;
                    break;
                case 'H':
                case 'V':
                case 'h':
                case 'v':
                    i = 1;
                    break;
                case 'L':
                case 'M':
                case 'T':
                case 'l':
                case 'm':
                case 't':
                    i = 2;
                    break;
                case 'Q':
                case 'S':
                case 'q':
                case 's':
                    i = 4;
                    break;
                case 'Z':
                case Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_SERVICE_NOT_FOUND /* 122 */:
                    path.close();
                    path.moveTo(f17, f18);
                    f16 = f18;
                    f15 = f17;
                    f14 = f18;
                    f13 = f17;
                    i = 2;
                    break;
            }
            int i2 = 0;
            float f19 = f18;
            float f20 = f17;
            float f21 = f14;
            float f22 = f13;
            while (i2 < fArr2.length) {
                switch (c2) {
                    case 'A':
                        drawArc(path, f22, f21, fArr2[i2 + 5], fArr2[i2 + 6], fArr2[i2 + 0], fArr2[i2 + 1], fArr2[i2 + 2], fArr2[i2 + 3] != 0.0f, fArr2[i2 + 4] != 0.0f);
                        float f23 = fArr2[i2 + 5];
                        float f24 = fArr2[i2 + 6];
                        f = f19;
                        f2 = f20;
                        f3 = f23;
                        f4 = f24;
                        f5 = f23;
                        f6 = f24;
                        break;
                    case 'B':
                    case 'D':
                    case 'E':
                    case 'F':
                    case 'G':
                    case 'I':
                    case 'J':
                    case 'K':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'R':
                    case 'U':
                    case 'W':
                    case 'X':
                    case 'Y':
                    case 'Z':
                    case '[':
                    case '\\':
                    case ']':
                    case '^':
                    case '_':
                    case '`':
                    case 'b':
                    case 'd':
                    case 'e':
                    case 'f':
                    case 'g':
                    case 'i':
                    case 'j':
                    case 'k':
                    case 'n':
                    case 'o':
                    case 'p':
                    case 'r':
                    case 'u':
                    default:
                        f = f19;
                        f2 = f20;
                        f3 = f15;
                        f4 = f21;
                        f5 = f22;
                        f6 = f16;
                        break;
                    case 'C':
                        path.cubicTo(fArr2[i2 + 0], fArr2[i2 + 1], fArr2[i2 + 2], fArr2[i2 + 3], fArr2[i2 + 4], fArr2[i2 + 5]);
                        float f25 = fArr2[i2 + 4];
                        float f26 = fArr2[i2 + 5];
                        f = f19;
                        f2 = f20;
                        f3 = fArr2[i2 + 2];
                        f4 = f26;
                        f5 = f25;
                        f6 = fArr2[i2 + 3];
                        break;
                    case 'H':
                        path.lineTo(fArr2[i2 + 0], f21);
                        f = f19;
                        f2 = f20;
                        f3 = f15;
                        f4 = f21;
                        f5 = fArr2[i2 + 0];
                        f6 = f16;
                        break;
                    case 'L':
                        path.lineTo(fArr2[i2 + 0], fArr2[i2 + 1]);
                        float f27 = fArr2[i2 + 0];
                        f = f19;
                        f2 = f20;
                        f3 = f15;
                        f4 = fArr2[i2 + 1];
                        f5 = f27;
                        f6 = f16;
                        break;
                    case 'M':
                        f2 = fArr2[i2 + 0];
                        f = fArr2[i2 + 1];
                        if (i2 > 0) {
                            path.lineTo(fArr2[i2 + 0], fArr2[i2 + 1]);
                            f3 = f15;
                            f4 = f;
                            f5 = f2;
                            f = f19;
                            f2 = f20;
                            f6 = f16;
                            break;
                        } else {
                            path.moveTo(fArr2[i2 + 0], fArr2[i2 + 1]);
                            f3 = f15;
                            f4 = f;
                            f5 = f2;
                            f6 = f16;
                            break;
                        }
                    case 'Q':
                        path.quadTo(fArr2[i2 + 0], fArr2[i2 + 1], fArr2[i2 + 2], fArr2[i2 + 3]);
                        float f28 = fArr2[i2 + 0];
                        float f29 = fArr2[i2 + 1];
                        float f30 = fArr2[i2 + 2];
                        f = f19;
                        f2 = f20;
                        f3 = f28;
                        f4 = fArr2[i2 + 3];
                        f5 = f30;
                        f6 = f29;
                        break;
                    case 'S':
                        if (c == 'c' || c == 's' || c == 'C' || c == 'S') {
                            f10 = (2.0f * f22) - f15;
                            f9 = (2.0f * f21) - f16;
                        } else {
                            f9 = f21;
                            f10 = f22;
                        }
                        path.cubicTo(f10, f9, fArr2[i2 + 0], fArr2[i2 + 1], fArr2[i2 + 2], fArr2[i2 + 3]);
                        float f31 = fArr2[i2 + 0];
                        float f32 = fArr2[i2 + 1];
                        float f33 = fArr2[i2 + 2];
                        f = f19;
                        f2 = f20;
                        f3 = f31;
                        f4 = fArr2[i2 + 3];
                        f5 = f33;
                        f6 = f32;
                        break;
                    case 'T':
                        if (c == 'q' || c == 't' || c == 'Q' || c == 'T') {
                            f22 = (2.0f * f22) - f15;
                            f21 = (2.0f * f21) - f16;
                        }
                        path.quadTo(f22, f21, fArr2[i2 + 0], fArr2[i2 + 1]);
                        float f34 = fArr2[i2 + 0];
                        f6 = f21;
                        f3 = f22;
                        f4 = fArr2[i2 + 1];
                        f5 = f34;
                        f = f19;
                        f2 = f20;
                        break;
                    case 'V':
                        path.lineTo(f22, fArr2[i2 + 0]);
                        f = f19;
                        f2 = f20;
                        f3 = f15;
                        f4 = fArr2[i2 + 0];
                        f5 = f22;
                        f6 = f16;
                        break;
                    case 'a':
                        drawArc(path, f22, f21, fArr2[i2 + 5] + f22, fArr2[i2 + 6] + f21, fArr2[i2 + 0], fArr2[i2 + 1], fArr2[i2 + 2], fArr2[i2 + 3] != 0.0f, fArr2[i2 + 4] != 0.0f);
                        float f35 = f22 + fArr2[i2 + 5];
                        float f36 = fArr2[i2 + 6] + f21;
                        f = f19;
                        f2 = f20;
                        f3 = f35;
                        f4 = f36;
                        f5 = f35;
                        f6 = f36;
                        break;
                    case 'c':
                        path.rCubicTo(fArr2[i2 + 0], fArr2[i2 + 1], fArr2[i2 + 2], fArr2[i2 + 3], fArr2[i2 + 4], fArr2[i2 + 5]);
                        float f37 = f22 + fArr2[i2 + 2];
                        float f38 = fArr2[i2 + 3] + f21;
                        float f39 = f22 + fArr2[i2 + 4];
                        f = f19;
                        f2 = f20;
                        f3 = f37;
                        f4 = f21 + fArr2[i2 + 5];
                        f5 = f39;
                        f6 = f38;
                        break;
                    case 'h':
                        path.rLineTo(fArr2[i2 + 0], 0.0f);
                        f = f19;
                        f2 = f20;
                        f3 = f15;
                        f4 = f21;
                        f5 = f22 + fArr2[i2 + 0];
                        f6 = f16;
                        break;
                    case 'l':
                        path.rLineTo(fArr2[i2 + 0], fArr2[i2 + 1]);
                        float f40 = f22 + fArr2[i2 + 0];
                        f = f19;
                        f2 = f20;
                        f3 = f15;
                        f4 = f21 + fArr2[i2 + 1];
                        f5 = f40;
                        f6 = f16;
                        break;
                    case 'm':
                        f2 = f22 + fArr2[i2 + 0];
                        f = fArr2[i2 + 1] + f21;
                        if (i2 > 0) {
                            path.rLineTo(fArr2[i2 + 0], fArr2[i2 + 1]);
                            f3 = f15;
                            f4 = f;
                            f5 = f2;
                            f = f19;
                            f2 = f20;
                            f6 = f16;
                            break;
                        } else {
                            path.rMoveTo(fArr2[i2 + 0], fArr2[i2 + 1]);
                            f3 = f15;
                            f4 = f;
                            f5 = f2;
                            f6 = f16;
                            break;
                        }
                    case 'q':
                        path.rQuadTo(fArr2[i2 + 0], fArr2[i2 + 1], fArr2[i2 + 2], fArr2[i2 + 3]);
                        float f41 = f22 + fArr2[i2 + 0];
                        float f42 = fArr2[i2 + 1] + f21;
                        float f43 = f22 + fArr2[i2 + 2];
                        f = f19;
                        f2 = f20;
                        f3 = f41;
                        f4 = f21 + fArr2[i2 + 3];
                        f5 = f43;
                        f6 = f42;
                        break;
                    case 's':
                        if (c == 'c' || c == 's' || c == 'C' || c == 'S') {
                            f12 = f22 - f15;
                            f11 = f21 - f16;
                        } else {
                            f11 = 0.0f;
                            f12 = 0.0f;
                        }
                        path.rCubicTo(f12, f11, fArr2[i2 + 0], fArr2[i2 + 1], fArr2[i2 + 2], fArr2[i2 + 3]);
                        float f44 = f22 + fArr2[i2 + 0];
                        float f45 = fArr2[i2 + 1] + f21;
                        float f46 = f22 + fArr2[i2 + 2];
                        f = f19;
                        f2 = f20;
                        f3 = f44;
                        f4 = f21 + fArr2[i2 + 3];
                        f5 = f46;
                        f6 = f45;
                        break;
                    case 't':
                        if (c == 'q' || c == 't' || c == 'Q' || c == 'T') {
                            f8 = f22 - f15;
                            f7 = f21 - f16;
                        } else {
                            f7 = 0.0f;
                            f8 = 0.0f;
                        }
                        path.rQuadTo(f8, f7, fArr2[i2 + 0], fArr2[i2 + 1]);
                        float f47 = f22 + f8;
                        float f48 = f22 + fArr2[i2 + 0];
                        f = f19;
                        f2 = f20;
                        f3 = f47;
                        f4 = f21 + fArr2[i2 + 1];
                        f5 = f48;
                        f6 = f7 + f21;
                        break;
                    case 'v':
                        path.rLineTo(0.0f, fArr2[i2 + 0]);
                        f = f19;
                        f2 = f20;
                        f3 = f15;
                        f4 = f21 + fArr2[i2 + 0];
                        f5 = f22;
                        f6 = f16;
                        break;
                }
                i2 += i;
                f19 = f;
                f20 = f2;
                f21 = f4;
                f22 = f5;
                c = c2;
                f16 = f6;
                f15 = f3;
            }
            fArr[0] = f22;
            fArr[1] = f21;
            fArr[2] = f15;
            fArr[3] = f16;
            fArr[4] = f20;
            fArr[5] = f19;
        }

        private static void arcToBezier(Path path, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9) {
            int ceil = (int) Math.ceil(Math.abs((4.0d * d9) / 3.141592653589793d));
            double cos = Math.cos(d7);
            double sin = Math.sin(d7);
            double cos2 = Math.cos(d8);
            double sin2 = Math.sin(d8);
            double d10 = (((-d3) * cos) * sin2) - ((d4 * sin) * cos2);
            double d11 = d9 / ceil;
            int i = 0;
            double d12 = (sin2 * (-d3) * sin) + (cos2 * d4 * cos);
            double d13 = d10;
            while (i < ceil) {
                double d14 = d8 + d11;
                double sin3 = Math.sin(d14);
                double cos3 = Math.cos(d14);
                double d15 = (((d3 * cos) * cos3) + d) - ((d4 * sin) * sin3);
                double d16 = (d4 * cos * sin3) + (d3 * sin * cos3) + d2;
                double d17 = (((-d3) * cos) * sin3) - ((d4 * sin) * cos3);
                double d18 = (cos3 * d4 * cos) + (sin3 * (-d3) * sin);
                double tan = Math.tan((d14 - d8) / 2.0d);
                double sqrt = ((Math.sqrt((tan * (3.0d * tan)) + 4.0d) - 1.0d) * Math.sin(d14 - d8)) / 3.0d;
                path.rLineTo(0.0f, 0.0f);
                path.cubicTo((float) ((d13 * sqrt) + d5), (float) (d6 + (d12 * sqrt)), (float) (d15 - (sqrt * d17)), (float) (d16 - (sqrt * d18)), (float) d15, (float) d16);
                i++;
                d13 = d17;
                d8 = d14;
                d6 = d16;
                d5 = d15;
                d12 = d18;
            }
        }

        private static void drawArc(Path path, float f, float f2, float f3, float f4, float f5, float f6, float f7, boolean z, boolean z2) {
            double d;
            double d2;
            double radians = Math.toRadians(f7);
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            double d3 = ((f * cos) + (f2 * sin)) / f5;
            double d4 = (((-f) * sin) + (f2 * cos)) / f6;
            double d5 = ((f3 * cos) + (f4 * sin)) / f5;
            double d6 = (((-f3) * sin) + (f4 * cos)) / f6;
            double d7 = d3 - d5;
            double d8 = d4 - d6;
            double d9 = (d3 + d5) / 2.0d;
            double d10 = (d4 + d6) / 2.0d;
            double d11 = (d7 * d7) + (d8 * d8);
            if (d11 == 0.0d) {
                Log.w(PathParser.LOGTAG, " Points are coincident");
                return;
            }
            double d12 = (1.0d / d11) - 0.25d;
            if (d12 < 0.0d) {
                Log.w(PathParser.LOGTAG, "Points are too far apart " + d11);
                float sqrt = (float) (Math.sqrt(d11) / 1.99999d);
                drawArc(path, f, f2, f3, f4, f5 * sqrt, f6 * sqrt, f7, z, z2);
                return;
            }
            double sqrt2 = Math.sqrt(d12);
            double d13 = d7 * sqrt2;
            double d14 = d8 * sqrt2;
            if (z != z2) {
                d = d14 + d9;
                d2 = d10 - d13;
            } else {
                d = d9 - d14;
                d2 = d13 + d10;
            }
            double atan2 = Math.atan2(d4 - d2, d3 - d);
            double atan22 = Math.atan2(d6 - d2, d5 - d) - atan2;
            if (z2 != (atan22 >= 0.0d)) {
                atan22 = atan22 > 0.0d ? atan22 - 6.283185307179586d : atan22 + 6.283185307179586d;
            }
            double d15 = f5 * d;
            double d16 = d2 * f6;
            arcToBezier(path, (d15 * cos) - (d16 * sin), (d15 * sin) + (d16 * cos), f5, f6, f, f2, radians, atan2, atan22);
        }

        public static void nodesToPath(PathDataNode[] pathDataNodeArr, Path path) {
            float[] fArr = new float[6];
            char c = 'm';
            for (int i = 0; i < pathDataNodeArr.length; i++) {
                addCommand(path, fArr, c, pathDataNodeArr[i].mType, pathDataNodeArr[i].mParams);
                c = pathDataNodeArr[i].mType;
            }
        }

        public void interpolatePathDataNode(PathDataNode pathDataNode, PathDataNode pathDataNode2, float f) {
            for (int i = 0; i < pathDataNode.mParams.length; i++) {
                this.mParams[i] = (pathDataNode.mParams[i] * (1.0f - f)) + (pathDataNode2.mParams[i] * f);
            }
        }
    }

    private static void addNode(ArrayList<PathDataNode> arrayList, char c, float[] fArr) {
        arrayList.add(new PathDataNode(c, fArr));
    }

    public static boolean canMorph(PathDataNode[] pathDataNodeArr, PathDataNode[] pathDataNodeArr2) {
        if (pathDataNodeArr == null || pathDataNodeArr2 == null || pathDataNodeArr.length != pathDataNodeArr2.length) {
            return false;
        }
        for (int i = 0; i < pathDataNodeArr.length; i++) {
            if (pathDataNodeArr[i].mType != pathDataNodeArr2[i].mType || pathDataNodeArr[i].mParams.length != pathDataNodeArr2[i].mParams.length) {
                return false;
            }
        }
        return true;
    }

    static float[] copyOfRange(float[] fArr, int i, int i2) {
        if (i <= i2) {
            int length = fArr.length;
            if (i >= 0 && i <= length) {
                int i3 = i2 - i;
                int min = Math.min(i3, length - i);
                float[] fArr2 = new float[i3];
                System.arraycopy(fArr, i, fArr2, 0, min);
                return fArr2;
            }
            throw new ArrayIndexOutOfBoundsException();
        }
        throw new IllegalArgumentException();
    }

    public static PathDataNode[] createNodesFromPathData(String str) {
        if (str != null) {
            ArrayList arrayList = new ArrayList();
            int i = 1;
            int i2 = 0;
            while (i < str.length()) {
                int nextStart = nextStart(str, i);
                String trim = str.substring(i2, nextStart).trim();
                if (trim.length() > 0) {
                    addNode(arrayList, trim.charAt(0), getFloats(trim));
                }
                i = nextStart + 1;
                i2 = nextStart;
            }
            if (i - i2 == 1 && i2 < str.length()) {
                addNode(arrayList, str.charAt(i2), new float[0]);
            }
            return (PathDataNode[]) arrayList.toArray(new PathDataNode[arrayList.size()]);
        }
        return null;
    }

    public static Path createPathFromPathData(String str) {
        Path path = new Path();
        PathDataNode[] createNodesFromPathData = createNodesFromPathData(str);
        if (createNodesFromPathData == null) {
            return null;
        }
        try {
            PathDataNode.nodesToPath(createNodesFromPathData, path);
            return path;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error in parsing " + str, e);
        }
    }

    public static PathDataNode[] deepCopyNodes(PathDataNode[] pathDataNodeArr) {
        if (pathDataNodeArr != null) {
            PathDataNode[] pathDataNodeArr2 = new PathDataNode[pathDataNodeArr.length];
            for (int i = 0; i < pathDataNodeArr.length; i++) {
                pathDataNodeArr2[i] = new PathDataNode(pathDataNodeArr[i]);
            }
            return pathDataNodeArr2;
        }
        return null;
    }

    private static void extract(String str, int i, ExtractFloatResult extractFloatResult) {
        extractFloatResult.mEndWithNegOrDot = false;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        for (int i2 = i; i2 < str.length(); i2++) {
            switch (str.charAt(i2)) {
                case ' ':
                case ',':
                    z = false;
                    z3 = true;
                    break;
                case '-':
                    if (i2 != i && !z) {
                        extractFloatResult.mEndWithNegOrDot = true;
                        z = false;
                        z3 = true;
                        break;
                    } else {
                        z = false;
                        break;
                    }
                case '.':
                    if (z2) {
                        extractFloatResult.mEndWithNegOrDot = true;
                        z = false;
                        z3 = true;
                        break;
                    } else {
                        z = false;
                        z2 = true;
                        break;
                    }
                case 'E':
                case 'e':
                    z = true;
                    break;
                default:
                    z = false;
                    break;
            }
            if (z3) {
                extractFloatResult.mEndPosition = i2;
            }
        }
        extractFloatResult.mEndPosition = i2;
    }

    private static float[] getFloats(String str) {
        if (str.charAt(0) == 'z' || str.charAt(0) == 'Z') {
            return new float[0];
        }
        try {
            float[] fArr = new float[str.length()];
            ExtractFloatResult extractFloatResult = new ExtractFloatResult();
            int length = str.length();
            int i = 1;
            int i2 = 0;
            while (i < length) {
                extract(str, i, extractFloatResult);
                int i3 = extractFloatResult.mEndPosition;
                if (i < i3) {
                    int i4 = i2 + 1;
                    fArr[i2] = Float.parseFloat(str.substring(i, i3));
                    i2 = i4;
                }
                i = !extractFloatResult.mEndWithNegOrDot ? i3 + 1 : i3;
            }
            return copyOfRange(fArr, 0, i2);
        } catch (NumberFormatException e) {
            throw new RuntimeException("error in parsing \"" + str + "\"", e);
        }
    }

    private static int nextStart(String str, int i) {
        while (i < str.length()) {
            char charAt = str.charAt(i);
            if (((charAt - 'A') * (charAt - 'Z') <= 0 || (charAt - 'a') * (charAt - 'z') <= 0) && charAt != 'e' && charAt != 'E') {
                return i;
            }
            i++;
        }
        return i;
    }

    public static void updateNodes(PathDataNode[] pathDataNodeArr, PathDataNode[] pathDataNodeArr2) {
        for (int i = 0; i < pathDataNodeArr2.length; i++) {
            pathDataNodeArr[i].mType = pathDataNodeArr2[i].mType;
            for (int i2 = 0; i2 < pathDataNodeArr2[i].mParams.length; i2++) {
                pathDataNodeArr[i].mParams[i2] = pathDataNodeArr2[i].mParams[i2];
            }
        }
    }
}
