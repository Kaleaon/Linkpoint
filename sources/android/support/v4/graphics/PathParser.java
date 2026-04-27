package android.support.v4.graphics;

import android.graphics.Path;
import android.support.annotation.RestrictTo;
import android.util.Log;
import java.util.ArrayList;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class PathParser {
    private static final String LOGTAG = "PathParser";

    private static class ExtractFloatResult {
        int mEndPosition;
        boolean mEndWithNegOrDot;

        ExtractFloatResult() {
        }
    }

    public static class PathDataNode {
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public float[] mParams;
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public char mType;

        PathDataNode(char c, float[] fArr) {
            this.mType = (char) c;
            this.mParams = fArr;
        }

        PathDataNode(PathDataNode pathDataNode) {
            this.mType = (char) pathDataNode.mType;
            this.mParams = PathParser.copyOfRange(pathDataNode.mParams, 0, pathDataNode.mParams.length);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v0, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v0, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v0, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v0, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v8, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v13, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v16, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v26, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v37, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v41, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v42, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v29, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v25, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v43, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v44, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v23, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v27, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v45, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v46, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v47, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v48, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v41, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v49, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v44, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v50, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v45, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v51, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v107, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v52, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v46, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v53, resolved type: int} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static void addCommand(android.graphics.Path r19, float[] r20, char r21, char r22, float[] r23) {
            /*
                r9 = 2
                r3 = 0
                r8 = r20[r3]
                r3 = 1
                r7 = r20[r3]
                r3 = 2
                r5 = r20[r3]
                r3 = 3
                r3 = r20[r3]
                r4 = 4
                r6 = r20[r4]
                r4 = 5
                r4 = r20[r4]
                switch(r22) {
                    case 65: goto L_0x0052;
                    case 66: goto L_0x0016;
                    case 67: goto L_0x004c;
                    case 68: goto L_0x0016;
                    case 69: goto L_0x0016;
                    case 70: goto L_0x0016;
                    case 71: goto L_0x0016;
                    case 72: goto L_0x0049;
                    case 73: goto L_0x0016;
                    case 74: goto L_0x0016;
                    case 75: goto L_0x0016;
                    case 76: goto L_0x0046;
                    case 77: goto L_0x0046;
                    case 78: goto L_0x0016;
                    case 79: goto L_0x0016;
                    case 80: goto L_0x0016;
                    case 81: goto L_0x004f;
                    case 82: goto L_0x0016;
                    case 83: goto L_0x004f;
                    case 84: goto L_0x0046;
                    case 85: goto L_0x0016;
                    case 86: goto L_0x0049;
                    case 87: goto L_0x0016;
                    case 88: goto L_0x0016;
                    case 89: goto L_0x0016;
                    case 90: goto L_0x0038;
                    case 91: goto L_0x0016;
                    case 92: goto L_0x0016;
                    case 93: goto L_0x0016;
                    case 94: goto L_0x0016;
                    case 95: goto L_0x0016;
                    case 96: goto L_0x0016;
                    case 97: goto L_0x0052;
                    case 98: goto L_0x0016;
                    case 99: goto L_0x004c;
                    case 100: goto L_0x0016;
                    case 101: goto L_0x0016;
                    case 102: goto L_0x0016;
                    case 103: goto L_0x0016;
                    case 104: goto L_0x0049;
                    case 105: goto L_0x0016;
                    case 106: goto L_0x0016;
                    case 107: goto L_0x0016;
                    case 108: goto L_0x0046;
                    case 109: goto L_0x0046;
                    case 110: goto L_0x0016;
                    case 111: goto L_0x0016;
                    case 112: goto L_0x0016;
                    case 113: goto L_0x004f;
                    case 114: goto L_0x0016;
                    case 115: goto L_0x004f;
                    case 116: goto L_0x0046;
                    case 117: goto L_0x0016;
                    case 118: goto L_0x0049;
                    case 119: goto L_0x0016;
                    case 120: goto L_0x0016;
                    case 121: goto L_0x0016;
                    case 122: goto L_0x0038;
                    default: goto L_0x0016;
                }
            L_0x0016:
                r13 = r9
            L_0x0017:
                r9 = 0
                r14 = r9
                r15 = r4
                r16 = r6
                r17 = r7
                r18 = r8
            L_0x0020:
                r0 = r23
                int r4 = r0.length
                if (r14 < r4) goto L_0x0055
                r4 = 0
                r20[r4] = r18
                r4 = 1
                r20[r4] = r17
                r4 = 2
                r20[r4] = r5
                r4 = 3
                r20[r4] = r3
                r3 = 4
                r20[r3] = r16
                r3 = 5
                r20[r3] = r15
                return
            L_0x0038:
                r19.close()
                r0 = r19
                r0.moveTo(r6, r4)
                r3 = r4
                r5 = r6
                r7 = r4
                r8 = r6
                r13 = r9
                goto L_0x0017
            L_0x0046:
                r9 = 2
                r13 = r9
                goto L_0x0017
            L_0x0049:
                r9 = 1
                r13 = r9
                goto L_0x0017
            L_0x004c:
                r9 = 6
                r13 = r9
                goto L_0x0017
            L_0x004f:
                r9 = 4
                r13 = r9
                goto L_0x0017
            L_0x0052:
                r9 = 7
                r13 = r9
                goto L_0x0017
            L_0x0055:
                switch(r22) {
                    case 65: goto L_0x03f4;
                    case 66: goto L_0x0058;
                    case 67: goto L_0x01c4;
                    case 68: goto L_0x0058;
                    case 69: goto L_0x0058;
                    case 70: goto L_0x0058;
                    case 71: goto L_0x0058;
                    case 72: goto L_0x0135;
                    case 73: goto L_0x0058;
                    case 74: goto L_0x0058;
                    case 75: goto L_0x0058;
                    case 76: goto L_0x00fa;
                    case 77: goto L_0x00a5;
                    case 78: goto L_0x0058;
                    case 79: goto L_0x0058;
                    case 80: goto L_0x0058;
                    case 81: goto L_0x02e4;
                    case 82: goto L_0x0058;
                    case 83: goto L_0x0255;
                    case 84: goto L_0x035d;
                    case 85: goto L_0x0058;
                    case 86: goto L_0x016a;
                    case 87: goto L_0x0058;
                    case 88: goto L_0x0058;
                    case 89: goto L_0x0058;
                    case 90: goto L_0x0058;
                    case 91: goto L_0x0058;
                    case 92: goto L_0x0058;
                    case 93: goto L_0x0058;
                    case 94: goto L_0x0058;
                    case 95: goto L_0x0058;
                    case 96: goto L_0x0058;
                    case 97: goto L_0x03a6;
                    case 98: goto L_0x0058;
                    case 99: goto L_0x0184;
                    case 100: goto L_0x0058;
                    case 101: goto L_0x0058;
                    case 102: goto L_0x0058;
                    case 103: goto L_0x0058;
                    case 104: goto L_0x011a;
                    case 105: goto L_0x0058;
                    case 106: goto L_0x0058;
                    case 107: goto L_0x0058;
                    case 108: goto L_0x00d6;
                    case 109: goto L_0x0070;
                    case 110: goto L_0x0058;
                    case 111: goto L_0x0058;
                    case 112: goto L_0x0058;
                    case 113: goto L_0x02ac;
                    case 114: goto L_0x0058;
                    case 115: goto L_0x01fc;
                    case 116: goto L_0x0314;
                    case 117: goto L_0x0058;
                    case 118: goto L_0x014f;
                    default: goto L_0x0058;
                }
            L_0x0058:
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
            L_0x0061:
                int r3 = r14 + r13
                r14 = r3
                r15 = r4
                r16 = r6
                r17 = r8
                r18 = r9
                r21 = r22
                r3 = r5
                r5 = r7
                goto L_0x0020
            L_0x0070:
                int r4 = r14 + 0
                r4 = r23[r4]
                float r6 = r18 + r4
                int r4 = r14 + 1
                r4 = r23[r4]
                float r4 = r4 + r17
                if (r14 > 0) goto L_0x0090
                int r7 = r14 + 0
                r7 = r23[r7]
                int r8 = r14 + 1
                r8 = r23[r8]
                r0 = r19
                r0.rMoveTo(r7, r8)
                r7 = r5
                r8 = r4
                r9 = r6
                r5 = r3
                goto L_0x0061
            L_0x0090:
                int r7 = r14 + 0
                r7 = r23[r7]
                int r8 = r14 + 1
                r8 = r23[r8]
                r0 = r19
                r0.rLineTo(r7, r8)
                r7 = r5
                r8 = r4
                r9 = r6
                r4 = r15
                r6 = r16
                r5 = r3
                goto L_0x0061
            L_0x00a5:
                int r4 = r14 + 0
                r6 = r23[r4]
                int r4 = r14 + 1
                r4 = r23[r4]
                if (r14 > 0) goto L_0x00c1
                int r7 = r14 + 0
                r7 = r23[r7]
                int r8 = r14 + 1
                r8 = r23[r8]
                r0 = r19
                r0.moveTo(r7, r8)
                r7 = r5
                r8 = r4
                r9 = r6
                r5 = r3
                goto L_0x0061
            L_0x00c1:
                int r7 = r14 + 0
                r7 = r23[r7]
                int r8 = r14 + 1
                r8 = r23[r8]
                r0 = r19
                r0.lineTo(r7, r8)
                r7 = r5
                r8 = r4
                r9 = r6
                r4 = r15
                r6 = r16
                r5 = r3
                goto L_0x0061
            L_0x00d6:
                int r4 = r14 + 0
                r4 = r23[r4]
                int r6 = r14 + 1
                r6 = r23[r6]
                r0 = r19
                r0.rLineTo(r4, r6)
                int r4 = r14 + 0
                r4 = r23[r4]
                float r18 = r18 + r4
                int r4 = r14 + 1
                r4 = r23[r4]
                float r17 = r17 + r4
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
                goto L_0x0061
            L_0x00fa:
                int r4 = r14 + 0
                r4 = r23[r4]
                int r6 = r14 + 1
                r6 = r23[r6]
                r0 = r19
                r0.lineTo(r4, r6)
                int r4 = r14 + 0
                r18 = r23[r4]
                int r4 = r14 + 1
                r17 = r23[r4]
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
                goto L_0x0061
            L_0x011a:
                int r4 = r14 + 0
                r4 = r23[r4]
                r6 = 0
                r0 = r19
                r0.rLineTo(r4, r6)
                int r4 = r14 + 0
                r4 = r23[r4]
                float r18 = r18 + r4
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
                goto L_0x0061
            L_0x0135:
                int r4 = r14 + 0
                r4 = r23[r4]
                r0 = r19
                r1 = r17
                r0.lineTo(r4, r1)
                int r4 = r14 + 0
                r18 = r23[r4]
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
                goto L_0x0061
            L_0x014f:
                int r4 = r14 + 0
                r4 = r23[r4]
                r6 = 0
                r0 = r19
                r0.rLineTo(r6, r4)
                int r4 = r14 + 0
                r4 = r23[r4]
                float r17 = r17 + r4
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
                goto L_0x0061
            L_0x016a:
                int r4 = r14 + 0
                r4 = r23[r4]
                r0 = r19
                r1 = r18
                r0.lineTo(r1, r4)
                int r4 = r14 + 0
                r17 = r23[r4]
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
                goto L_0x0061
            L_0x0184:
                int r3 = r14 + 0
                r4 = r23[r3]
                int r3 = r14 + 1
                r5 = r23[r3]
                int r3 = r14 + 2
                r6 = r23[r3]
                int r3 = r14 + 3
                r7 = r23[r3]
                int r3 = r14 + 4
                r8 = r23[r3]
                int r3 = r14 + 5
                r9 = r23[r3]
                r3 = r19
                r3.rCubicTo(r4, r5, r6, r7, r8, r9)
                int r3 = r14 + 2
                r3 = r23[r3]
                float r5 = r18 + r3
                int r3 = r14 + 3
                r3 = r23[r3]
                float r3 = r3 + r17
                int r4 = r14 + 4
                r4 = r23[r4]
                float r18 = r18 + r4
                int r4 = r14 + 5
                r4 = r23[r4]
                float r17 = r17 + r4
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
                goto L_0x0061
            L_0x01c4:
                int r3 = r14 + 0
                r4 = r23[r3]
                int r3 = r14 + 1
                r5 = r23[r3]
                int r3 = r14 + 2
                r6 = r23[r3]
                int r3 = r14 + 3
                r7 = r23[r3]
                int r3 = r14 + 4
                r8 = r23[r3]
                int r3 = r14 + 5
                r9 = r23[r3]
                r3 = r19
                r3.cubicTo(r4, r5, r6, r7, r8, r9)
                int r3 = r14 + 4
                r18 = r23[r3]
                int r3 = r14 + 5
                r17 = r23[r3]
                int r3 = r14 + 2
                r5 = r23[r3]
                int r3 = r14 + 3
                r3 = r23[r3]
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
                goto L_0x0061
            L_0x01fc:
                r6 = 0
                r4 = 0
                r7 = 99
                r0 = r21
                if (r0 != r7) goto L_0x0240
            L_0x0204:
                float r4 = r18 - r5
                float r5 = r17 - r3
            L_0x0208:
                int r3 = r14 + 0
                r6 = r23[r3]
                int r3 = r14 + 1
                r7 = r23[r3]
                int r3 = r14 + 2
                r8 = r23[r3]
                int r3 = r14 + 3
                r9 = r23[r3]
                r3 = r19
                r3.rCubicTo(r4, r5, r6, r7, r8, r9)
                int r3 = r14 + 0
                r3 = r23[r3]
                float r5 = r18 + r3
                int r3 = r14 + 1
                r3 = r23[r3]
                float r3 = r3 + r17
                int r4 = r14 + 2
                r4 = r23[r4]
                float r18 = r18 + r4
                int r4 = r14 + 3
                r4 = r23[r4]
                float r17 = r17 + r4
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
                goto L_0x0061
            L_0x0240:
                r7 = 115(0x73, float:1.61E-43)
                r0 = r21
                if (r0 == r7) goto L_0x0204
                r7 = 67
                r0 = r21
                if (r0 == r7) goto L_0x0204
                r7 = 83
                r0 = r21
                if (r0 == r7) goto L_0x0204
                r5 = r4
                r4 = r6
                goto L_0x0208
            L_0x0255:
                r4 = 99
                r0 = r21
                if (r0 != r4) goto L_0x0295
            L_0x025b:
                r4 = 1073741824(0x40000000, float:2.0)
                float r4 = r4 * r18
                float r4 = r4 - r5
                r5 = 1073741824(0x40000000, float:2.0)
                float r5 = r5 * r17
                float r5 = r5 - r3
            L_0x0265:
                int r3 = r14 + 0
                r6 = r23[r3]
                int r3 = r14 + 1
                r7 = r23[r3]
                int r3 = r14 + 2
                r8 = r23[r3]
                int r3 = r14 + 3
                r9 = r23[r3]
                r3 = r19
                r3.cubicTo(r4, r5, r6, r7, r8, r9)
                int r3 = r14 + 0
                r5 = r23[r3]
                int r3 = r14 + 1
                r3 = r23[r3]
                int r4 = r14 + 2
                r18 = r23[r4]
                int r4 = r14 + 3
                r17 = r23[r4]
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
                goto L_0x0061
            L_0x0295:
                r4 = 115(0x73, float:1.61E-43)
                r0 = r21
                if (r0 == r4) goto L_0x025b
                r4 = 67
                r0 = r21
                if (r0 == r4) goto L_0x025b
                r4 = 83
                r0 = r21
                if (r0 == r4) goto L_0x025b
                r5 = r17
                r4 = r18
                goto L_0x0265
            L_0x02ac:
                int r3 = r14 + 0
                r3 = r23[r3]
                int r4 = r14 + 1
                r4 = r23[r4]
                int r5 = r14 + 2
                r5 = r23[r5]
                int r6 = r14 + 3
                r6 = r23[r6]
                r0 = r19
                r0.rQuadTo(r3, r4, r5, r6)
                int r3 = r14 + 0
                r3 = r23[r3]
                float r5 = r18 + r3
                int r3 = r14 + 1
                r3 = r23[r3]
                float r3 = r3 + r17
                int r4 = r14 + 2
                r4 = r23[r4]
                float r18 = r18 + r4
                int r4 = r14 + 3
                r4 = r23[r4]
                float r17 = r17 + r4
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
                goto L_0x0061
            L_0x02e4:
                int r3 = r14 + 0
                r3 = r23[r3]
                int r4 = r14 + 1
                r4 = r23[r4]
                int r5 = r14 + 2
                r5 = r23[r5]
                int r6 = r14 + 3
                r6 = r23[r6]
                r0 = r19
                r0.quadTo(r3, r4, r5, r6)
                int r3 = r14 + 0
                r5 = r23[r3]
                int r3 = r14 + 1
                r3 = r23[r3]
                int r4 = r14 + 2
                r18 = r23[r4]
                int r4 = r14 + 3
                r17 = r23[r4]
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
                goto L_0x0061
            L_0x0314:
                r6 = 0
                r4 = 0
                r7 = 113(0x71, float:1.58E-43)
                r0 = r21
                if (r0 != r7) goto L_0x0348
            L_0x031c:
                float r4 = r18 - r5
                float r3 = r17 - r3
            L_0x0320:
                int r5 = r14 + 0
                r5 = r23[r5]
                int r6 = r14 + 1
                r6 = r23[r6]
                r0 = r19
                r0.rQuadTo(r4, r3, r5, r6)
                float r5 = r18 + r4
                float r3 = r3 + r17
                int r4 = r14 + 0
                r4 = r23[r4]
                float r18 = r18 + r4
                int r4 = r14 + 1
                r4 = r23[r4]
                float r17 = r17 + r4
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r17
                r9 = r18
                r5 = r3
                goto L_0x0061
            L_0x0348:
                r7 = 116(0x74, float:1.63E-43)
                r0 = r21
                if (r0 == r7) goto L_0x031c
                r7 = 81
                r0 = r21
                if (r0 == r7) goto L_0x031c
                r7 = 84
                r0 = r21
                if (r0 == r7) goto L_0x031c
                r3 = r4
                r4 = r6
                goto L_0x0320
            L_0x035d:
                r4 = 113(0x71, float:1.58E-43)
                r0 = r21
                if (r0 != r4) goto L_0x0393
            L_0x0363:
                r4 = 1073741824(0x40000000, float:2.0)
                float r4 = r4 * r18
                float r18 = r4 - r5
                r4 = 1073741824(0x40000000, float:2.0)
                float r4 = r4 * r17
                float r17 = r4 - r3
            L_0x036f:
                int r3 = r14 + 0
                r3 = r23[r3]
                int r4 = r14 + 1
                r4 = r23[r4]
                r0 = r19
                r1 = r18
                r2 = r17
                r0.quadTo(r1, r2, r3, r4)
                int r3 = r14 + 0
                r6 = r23[r3]
                int r3 = r14 + 1
                r4 = r23[r3]
                r5 = r17
                r7 = r18
                r8 = r4
                r9 = r6
                r4 = r15
                r6 = r16
                goto L_0x0061
            L_0x0393:
                r4 = 116(0x74, float:1.63E-43)
                r0 = r21
                if (r0 == r4) goto L_0x0363
                r4 = 81
                r0 = r21
                if (r0 == r4) goto L_0x0363
                r4 = 84
                r0 = r21
                if (r0 == r4) goto L_0x0363
                goto L_0x036f
            L_0x03a6:
                int r3 = r14 + 5
                r3 = r23[r3]
                float r6 = r3 + r18
                int r3 = r14 + 6
                r3 = r23[r3]
                float r7 = r3 + r17
                int r3 = r14 + 0
                r8 = r23[r3]
                int r3 = r14 + 1
                r9 = r23[r3]
                int r3 = r14 + 2
                r10 = r23[r3]
                int r3 = r14 + 3
                r3 = r23[r3]
                r4 = 0
                int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r3 == 0) goto L_0x03f0
                r11 = 1
            L_0x03c8:
                int r3 = r14 + 4
                r3 = r23[r3]
                r4 = 0
                int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r3 == 0) goto L_0x03f2
                r12 = 1
            L_0x03d2:
                r3 = r19
                r4 = r18
                r5 = r17
                drawArc(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
                int r3 = r14 + 5
                r3 = r23[r3]
                float r5 = r18 + r3
                int r3 = r14 + 6
                r3 = r23[r3]
                float r3 = r3 + r17
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r3
                r9 = r5
                r5 = r3
                goto L_0x0061
            L_0x03f0:
                r11 = 0
                goto L_0x03c8
            L_0x03f2:
                r12 = 0
                goto L_0x03d2
            L_0x03f4:
                int r3 = r14 + 5
                r6 = r23[r3]
                int r3 = r14 + 6
                r7 = r23[r3]
                int r3 = r14 + 0
                r8 = r23[r3]
                int r3 = r14 + 1
                r9 = r23[r3]
                int r3 = r14 + 2
                r10 = r23[r3]
                int r3 = r14 + 3
                r3 = r23[r3]
                r4 = 0
                int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r3 == 0) goto L_0x0436
                r11 = 1
            L_0x0412:
                int r3 = r14 + 4
                r3 = r23[r3]
                r4 = 0
                int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r3 == 0) goto L_0x0438
                r12 = 1
            L_0x041c:
                r3 = r19
                r4 = r18
                r5 = r17
                drawArc(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
                int r3 = r14 + 5
                r5 = r23[r3]
                int r3 = r14 + 6
                r3 = r23[r3]
                r4 = r15
                r6 = r16
                r7 = r5
                r8 = r3
                r9 = r5
                r5 = r3
                goto L_0x0061
            L_0x0436:
                r11 = 0
                goto L_0x0412
            L_0x0438:
                r12 = 0
                goto L_0x041c
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.PathParser.PathDataNode.addCommand(android.graphics.Path, float[], char, char, float[]):void");
        }

        private static void arcToBezier(Path path, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9) {
            int ceil = (int) Math.ceil(Math.abs((4.0d * d9) / 3.141592653589793d));
            double cos = Math.cos(d7);
            double sin = Math.sin(d7);
            double cos2 = Math.cos(d8);
            double sin2 = Math.sin(d8);
            double d10 = (((-d3) * cos) * sin2) - ((d4 * sin) * cos2);
            double d11 = d9 / ((double) ceil);
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
            double radians = Math.toRadians((double) f7);
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            double d3 = ((((double) f) * cos) + (((double) f2) * sin)) / ((double) f5);
            double d4 = ((((double) (-f)) * sin) + (((double) f2) * cos)) / ((double) f6);
            double d5 = ((((double) f3) * cos) + (((double) f4) * sin)) / ((double) f5);
            double d6 = ((((double) (-f3)) * sin) + (((double) f4) * cos)) / ((double) f6);
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
            double d15 = ((double) f5) * d;
            double d16 = d2 * ((double) f6);
            arcToBezier(path, (d15 * cos) - (d16 * sin), (d15 * sin) + (d16 * cos), (double) f5, (double) f6, (double) f, (double) f2, radians, atan2, atan22);
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
        if (str == null) {
            return null;
        }
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
        if (pathDataNodeArr == null) {
            return null;
        }
        PathDataNode[] pathDataNodeArr2 = new PathDataNode[pathDataNodeArr.length];
        for (int i = 0; i < pathDataNodeArr.length; i++) {
            pathDataNodeArr2[i] = new PathDataNode(pathDataNodeArr[i]);
        }
        return pathDataNodeArr2;
    }

    private static void extract(String str, int i, ExtractFloatResult extractFloatResult) {
        extractFloatResult.mEndWithNegOrDot = false;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        int i2 = i;
        while (i2 < str.length()) {
            switch (str.charAt(i2)) {
                case ' ':
                case ',':
                    z = false;
                    z3 = true;
                    break;
                case '-':
                    if (i2 == i || z) {
                        z = false;
                        break;
                    } else {
                        extractFloatResult.mEndWithNegOrDot = true;
                        z = false;
                        z3 = true;
                        break;
                    }
                case '.':
                    if (!z2) {
                        z = false;
                        z2 = true;
                        break;
                    } else {
                        extractFloatResult.mEndWithNegOrDot = true;
                        z = false;
                        z3 = true;
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
            if (!z3) {
                i2++;
            } else {
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
            pathDataNodeArr[i].mType = (char) pathDataNodeArr2[i].mType;
            for (int i2 = 0; i2 < pathDataNodeArr2[i].mParams.length; i2++) {
                pathDataNodeArr[i].mParams[i2] = pathDataNodeArr2[i].mParams[i2];
            }
        }
    }
}
