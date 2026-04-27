// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SimpleStringParser
{
    public static class StringParsingException extends Exception
    {

        public StringParsingException(String s)
        {
            super(s);
        }
    }


    private int curPos;
    private String spaceChars;
    private String string;

    public SimpleStringParser(String s, String s1)
    {
        string = s;
        spaceChars = s1;
        curPos = 0;
    }

    public boolean endOfString()
    {
        return curPos >= string.length();
    }

    public SimpleStringParser expectToken(String s, String s1)
        throws StringParsingException
    {
        s1 = nextToken(s1);
        if (!s1.equals(s))
        {
            throw new StringParsingException((new StringBuilder()).append("Expected '").append(s).append("', got '").append(s1).append("'").toString());
        } else
        {
            return this;
        }
    }

    public int getHexToken(String s)
        throws StringParsingException
    {
        Object obj = nextToken(s);
        int i;
        try
        {
            i = Integer.parseInt(((String) (obj)), 16);
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            obj = new StringParsingException((new StringBuilder()).append("Cannot parse expected integer: ").append(((String) (obj))).toString());
            ((StringParsingException) (obj)).initCause(s);
            throw obj;
        }
        return i;
    }

    public int getIntToken(String s)
        throws StringParsingException
    {
        Object obj = nextToken(s);
        int i;
        try
        {
            i = Integer.parseInt(((String) (obj)));
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            obj = new StringParsingException((new StringBuilder()).append("Cannot parse expected integer: ").append(((String) (obj))).toString());
            ((StringParsingException) (obj)).initCause(s);
            throw obj;
        }
        return i;
    }

    public String getPipeTerminatedString(String s)
        throws StringParsingException
    {
        String s1 = nextToken(s);
        int i = s1.lastIndexOf('|');
        s = s1;
        if (i >= 0)
        {
            s = s1.substring(0, i);
        }
        return s;
    }

    public String getSubstring(int i)
        throws StringParsingException
    {
        byte abyte0[] = SLMessage.stringToVariableUTF(string.substring(curPos));
        if (abyte0.length < i)
        {
            throw new StringParsingException((new StringBuilder()).append("End of string reached: wanted ").append(i).append(", still has ").append(abyte0.length).toString());
        } else
        {
            byte abyte1[] = new byte[i];
            System.arraycopy(abyte0, 0, abyte1, 0, i);
            String s = SLMessage.stringFromVariableUTF(abyte1);
            curPos = curPos + s.length();
            return s;
        }
    }

    public String nextToken(String s)
        throws StringParsingException
    {
        boolean flag;
        flag = false;
        if (curPos >= string.length())
        {
            throw new StringParsingException("End of string reached");
        }
          goto _L1
_L3:
        curPos = curPos + 1;
_L1:
        int i;
        i = ((flag) ? 1 : 0);
        if (curPos >= string.length())
        {
            break; /* Loop/switch isn't completed */
        }
        if (spaceChars.indexOf(string.charAt(curPos)) >= 0)
        {
            continue; /* Loop/switch isn't completed */
        }
        i = ((flag) ? 1 : 0);
        break; /* Loop/switch isn't completed */
        if (true) goto _L3; else goto _L2
_L2:
        do
        {
            if (curPos >= string.length() || s.indexOf(string.charAt(curPos)) >= 0)
            {
                return string.substring(curPos - i, curPos);
            }
            i++;
            curPos = curPos + 1;
        } while (true);
    }

    public void skipAllDelimiters(String s)
    {
        for (; curPos < string.length() && s.indexOf(string.charAt(curPos)) >= 0; curPos = curPos + 1) { }
    }

    public void skipOneDelimiter(String s)
    {
        if (curPos < string.length() && s.indexOf(string.charAt(curPos)) >= 0)
        {
            curPos = curPos + 1;
        }
    }
}
