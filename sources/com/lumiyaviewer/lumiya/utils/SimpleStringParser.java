package com.lumiyaviewer.lumiya.utils;

import com.google.common.logging.nano.Vr;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SimpleStringParser {
    private int curPos = 0;
    private String spaceChars;
    private String string;

    public static class StringParsingException extends Exception {
        public StringParsingException(String str) {
            super(str);
        }
    }

    public SimpleStringParser(String str, String str2) {
        this.string = str;
        this.spaceChars = str2;
    }

    public boolean endOfString() {
        return this.curPos >= this.string.length();
    }

    public SimpleStringParser expectToken(String str, String str2) throws StringParsingException {
        String nextToken = nextToken(str2);
        if (nextToken.equals(str)) {
            return this;
        }
        throw new StringParsingException("Expected '" + str + "', got '" + nextToken + "'");
    }

    public int getHexToken(String str) throws StringParsingException {
        String nextToken = nextToken(str);
        try {
            return Integer.parseInt(nextToken, 16);
        } catch (NumberFormatException e) {
            StringParsingException stringParsingException = new StringParsingException("Cannot parse expected integer: " + nextToken);
            stringParsingException.initCause(e);
            throw stringParsingException;
        }
    }

    public int getIntToken(String str) throws StringParsingException {
        String nextToken = nextToken(str);
        try {
            return Integer.parseInt(nextToken);
        } catch (NumberFormatException e) {
            StringParsingException stringParsingException = new StringParsingException("Cannot parse expected integer: " + nextToken);
            stringParsingException.initCause(e);
            throw stringParsingException;
        }
    }

    public String getPipeTerminatedString(String str) throws StringParsingException {
        String nextToken = nextToken(str);
        int lastIndexOf = nextToken.lastIndexOf(Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_NOTIFY_FAILED);
        return lastIndexOf >= 0 ? nextToken.substring(0, lastIndexOf) : nextToken;
    }

    public String getSubstring(int i) throws StringParsingException {
        byte[] stringToVariableUTF = SLMessage.stringToVariableUTF(this.string.substring(this.curPos));
        if (stringToVariableUTF.length < i) {
            throw new StringParsingException("End of string reached: wanted " + i + ", still has " + stringToVariableUTF.length);
        }
        byte[] bArr = new byte[i];
        System.arraycopy(stringToVariableUTF, 0, bArr, 0, i);
        String stringFromVariableUTF = SLMessage.stringFromVariableUTF(bArr);
        this.curPos += stringFromVariableUTF.length();
        return stringFromVariableUTF;
    }

    public String nextToken(String str) throws StringParsingException {
        int i = 0;
        if (this.curPos >= this.string.length()) {
            throw new StringParsingException("End of string reached");
        }
        while (this.curPos < this.string.length() && this.spaceChars.indexOf(this.string.charAt(this.curPos)) >= 0) {
            this.curPos++;
        }
        while (this.curPos < this.string.length() && str.indexOf(this.string.charAt(this.curPos)) < 0) {
            i++;
            this.curPos++;
        }
        return this.string.substring(this.curPos - i, this.curPos);
    }

    public void skipAllDelimiters(String str) {
        while (this.curPos < this.string.length() && str.indexOf(this.string.charAt(this.curPos)) >= 0) {
            this.curPos++;
        }
    }

    public void skipOneDelimiter(String str) {
        if (this.curPos < this.string.length() && str.indexOf(this.string.charAt(this.curPos)) >= 0) {
            this.curPos++;
        }
    }
}
