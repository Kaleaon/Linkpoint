package com.google.common.io;

import java.io.IOException;

abstract class LineBuffer {
    private StringBuilder line = new StringBuilder();
    private boolean sawReturn;

    LineBuffer() {
    }

    private boolean finishLine(boolean z) throws IOException {
        handleLine(this.line.toString(), !this.sawReturn ? !z ? "" : "\n" : !z ? "\r" : "\r\n");
        this.line = new StringBuilder();
        this.sawReturn = false;
        return z;
    }

    /* access modifiers changed from: protected */
    public void add(char[] cArr, int i, int i2) throws IOException {
        int i3;
        int i4;
        if (this.sawReturn && i2 > 0) {
            i3 = !finishLine(cArr[i] == 10) ? i : i + 1;
        } else {
            i3 = i;
        }
        int i5 = i + i2;
        int i6 = i3;
        while (i6 < i5) {
            switch (cArr[i6]) {
                case 10:
                    this.line.append(cArr, i3, i6 - i3);
                    finishLine(true);
                    i3 = i6 + 1;
                    break;
                case 13:
                    this.line.append(cArr, i3, i6 - i3);
                    this.sawReturn = true;
                    if (i6 + 1 >= i5) {
                        i4 = i6;
                    } else {
                        i4 = !finishLine(cArr[i6 + 1] == 10) ? i6 : i6 + 1;
                    }
                    i6 = i4;
                    i3 = i4 + 1;
                    break;
            }
            i6++;
        }
        this.line.append(cArr, i3, (i + i2) - i3);
    }

    /* access modifiers changed from: protected */
    public void finish() throws IOException {
        if (this.sawReturn || this.line.length() > 0) {
            finishLine(false);
        }
    }

    /* access modifiers changed from: protected */
    public abstract void handleLine(String str, String str2) throws IOException;
}
