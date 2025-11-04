// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SimpleStringParser
{
    private int curPos;
    private String spaceChars;
    private String string;
    
    public SimpleStringParser(final String string, final String spaceChars) {
        this.string = string;
        this.spaceChars = spaceChars;
        this.curPos = 0;
    }
    
    public boolean endOfString() {
        return this.curPos >= this.string.length();
    }
    
    public SimpleStringParser expectToken(final String s, String nextToken) throws StringParsingException {
        nextToken = this.nextToken(nextToken);
        if (!nextToken.equals(s)) {
            throw new StringParsingException("Expected '" + s + "', got '" + nextToken + "'");
        }
        return this;
    }
    
    public int getHexToken(final String s) throws StringParsingException {
        final String nextToken = this.nextToken(s);
        try {
            return Integer.parseInt(nextToken, 16);
        }
        catch (final NumberFormatException cause) {
            final StringParsingException ex = new StringParsingException("Cannot parse expected integer: " + nextToken);
            ex.initCause(cause);
            throw ex;
        }
    }
    
    public int getIntToken(final String s) throws StringParsingException {
        final String nextToken = this.nextToken(s);
        try {
            return Integer.parseInt(nextToken);
        }
        catch (final NumberFormatException cause) {
            final StringParsingException ex = new StringParsingException("Cannot parse expected integer: " + nextToken);
            ex.initCause(cause);
            throw ex;
        }
    }
    
    public String getPipeTerminatedString(String substring) throws StringParsingException {
        final String nextToken = this.nextToken(substring);
        final int lastIndex = nextToken.lastIndexOf(124);
        substring = nextToken;
        if (lastIndex >= 0) {
            substring = nextToken.substring(0, lastIndex);
        }
        return substring;
    }
    
    public String getSubstring(final int i) throws StringParsingException {
        final byte[] stringToVariableUTF = SLMessage.stringToVariableUTF(this.string.substring(this.curPos));
        if (stringToVariableUTF.length < i) {
            throw new StringParsingException("End of string reached: wanted " + i + ", still has " + stringToVariableUTF.length);
        }
        final byte[] array = new byte[i];
        System.arraycopy(stringToVariableUTF, 0, array, 0, i);
        final String stringFromVariableUTF = SLMessage.stringFromVariableUTF(array);
        this.curPos += stringFromVariableUTF.length();
        return stringFromVariableUTF;
    }
    
    public String nextToken(final String s) throws StringParsingException {
        final int n = 0;
        if (this.curPos >= this.string.length()) {
            throw new StringParsingException("End of string reached");
        }
        while (true) {
            Label_0036: {
                break Label_0036;
                do {
                    ++this.curPos;
                    int n2 = n;
                    if (this.curPos < this.string.length()) {
                        continue;
                    }
                    while (this.curPos < this.string.length() && s.indexOf(this.string.charAt(this.curPos)) < 0) {
                        ++n2;
                        ++this.curPos;
                    }
                    return this.string.substring(this.curPos - n2, this.curPos);
                } while (this.spaceChars.indexOf(this.string.charAt(this.curPos)) >= 0);
            }
            int n2 = n;
            continue;
        }
    }
    
    public void skipAllDelimiters(final String s) {
        while (this.curPos < this.string.length() && s.indexOf(this.string.charAt(this.curPos)) >= 0) {
            ++this.curPos;
        }
    }
    
    public void skipOneDelimiter(final String s) {
        if (this.curPos < this.string.length() && s.indexOf(this.string.charAt(this.curPos)) >= 0) {
            ++this.curPos;
        }
    }
    
    public static class StringParsingException extends Exception
    {
        public StringParsingException(final String message) {
            super(message);
        }
    }
}
