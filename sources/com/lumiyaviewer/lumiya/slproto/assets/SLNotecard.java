// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.assets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ReplacementSpan;
import android.view.View;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLSaleType;
import com.lumiyaviewer.lumiya.utils.SimpleStringParser;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class SLNotecard
{
    private static class AttachmentClickableSpan extends ClickableSpan
        implements InventoryEntrySpan
    {

        private OnAttachmentClickListener clickListener;
        private SLInventoryEntry entry;

        public SLInventoryEntry getEntry()
        {
            return entry;
        }

        public void onClick(View view)
        {
            if (clickListener != null)
            {
                clickListener.onAttachmentClick(entry);
            }
        }

        public AttachmentClickableSpan(SLInventoryEntry slinventoryentry, OnAttachmentClickListener onattachmentclicklistener)
        {
            entry = slinventoryentry;
            clickListener = onattachmentclicklistener;
        }
    }

    private static class AttachmentSpan extends ReplacementSpan
        implements InventoryEntrySpan
    {

        private SLInventoryEntry entry;
        private String linkText;

        public void draw(Canvas canvas, CharSequence charsequence, int i, int j, float f, int k, int l, 
                int i1, Paint paint)
        {
            if (i != j)
            {
                charsequence = new Paint(paint);
                charsequence.setUnderlineText(true);
                charsequence.setColor(Color.rgb(0, 50, 100));
                canvas.drawText(linkText, 0, linkText.length(), f, l, charsequence);
            }
        }

        public SLInventoryEntry getEntry()
        {
            return entry;
        }

        public int getSize(Paint paint, CharSequence charsequence, int i, int j, android.graphics.Paint.FontMetricsInt fontmetricsint)
        {
            if (fontmetricsint != null)
            {
                charsequence = paint.getFontMetricsInt();
                fontmetricsint.ascent = ((android.graphics.Paint.FontMetricsInt) (charsequence)).ascent;
                fontmetricsint.bottom = ((android.graphics.Paint.FontMetricsInt) (charsequence)).bottom;
                fontmetricsint.descent = ((android.graphics.Paint.FontMetricsInt) (charsequence)).descent;
                fontmetricsint.leading = ((android.graphics.Paint.FontMetricsInt) (charsequence)).leading;
                fontmetricsint.top = ((android.graphics.Paint.FontMetricsInt) (charsequence)).top;
            }
            if (i != j)
            {
                return (int)paint.measureText(linkText, 0, linkText.length());
            } else
            {
                return 0;
            }
        }

        public AttachmentSpan(SLInventoryEntry slinventoryentry)
        {
            entry = slinventoryentry;
            linkText = slinventoryentry.getReadableTextForLink();
        }
    }

    private static interface InventoryEntrySpan
    {

        public abstract SLInventoryEntry getEntry();
    }

    private static class NotecardAttachment
    {

        SLInventoryEntry entry;
        int extCharIndex;

        public NotecardAttachment(int i, SLInventoryEntry slinventoryentry)
        {
            extCharIndex = i;
            entry = slinventoryentry;
        }
    }

    public static interface OnAttachmentClickListener
    {

        public abstract void onAttachmentClick(SLInventoryEntry slinventoryentry);
    }


    private static final String DELIM_ANY = " \t\n";
    private static final String DELIM_EOL = "\n";
    private List attachments;
    private boolean isScript;
    private String notecardText;

    public SLNotecard(Spanned spanned, boolean flag)
    {
        StringBuilder stringbuilder;
        InventoryEntrySpan ainventoryentryspan[];
        int ai[];
        int ai1[];
        int j;
        int l;
        isScript = flag;
        stringbuilder = new StringBuilder();
        attachments = new ArrayList(0);
        ainventoryentryspan = (InventoryEntrySpan[])spanned.getSpans(0, spanned.length(), com/lumiyaviewer/lumiya/slproto/assets/SLNotecard$InventoryEntrySpan);
        ai = new int[ainventoryentryspan.length];
        ai1 = new int[ainventoryentryspan.length];
        for (int i = 0; i < ainventoryentryspan.length; i++)
        {
            ai[i] = spanned.getSpanStart(ainventoryentryspan[i]);
            ai1[i] = spanned.getSpanEnd(ainventoryentryspan[i]);
        }

        l = 0;
        j = 0;
_L5:
        if (j >= spanned.length()) goto _L2; else goto _L1
_L1:
        int k = 0;
_L6:
        if (k >= ainventoryentryspan.length)
        {
            break MISSING_BLOCK_LABEL_287;
        }
        if (ai[k] < j) goto _L4; else goto _L3
_L3:
        int i1 = k;
_L7:
        k = spanned.length();
        if (i1 != -1)
        {
            k = ai[i1];
        }
        stringbuilder.append(spanned.subSequence(j, k));
        if (i1 != -1)
        {
            attachments.add(new NotecardAttachment(l, ainventoryentryspan[i1].getEntry()));
            stringbuilder.append('\uDBC0');
            stringbuilder.append((char)(56320 + l));
            l++;
            j = ai1[i1];
        } else
        {
            j = k;
        }
          goto _L5
_L4:
        k++;
          goto _L6
_L2:
        notecardText = stringbuilder.toString();
        return;
        i1 = -1;
          goto _L7
    }

    public SLNotecard(boolean flag)
    {
        isScript = flag;
        attachments = new ArrayList(0);
        notecardText = "";
    }

    public SLNotecard(byte abyte0[], boolean flag)
        throws com.lumiyaviewer.lumiya.utils.SimpleStringParser.StringParsingException
    {
        abyte0 = SLMessage.stringFromVariableUTF(abyte0);
        isScript = flag;
        if (!flag) goto _L2; else goto _L1
_L1:
        notecardText = abyte0;
_L4:
        if (attachments == null)
        {
            attachments = new ArrayList(0);
        }
        return;
_L2:
        abyte0 = new SimpleStringParser(abyte0, " \t\n");
        abyte0.expectToken("Linden text version 2", "\n");
        abyte0.expectToken("{", "\n");
        do
        {
            String s = abyte0.nextToken(" \t\n");
            if (s.equals("}"))
            {
                continue; /* Loop/switch isn't completed */
            }
            if (s.equals("LLEmbeddedItems"))
            {
                abyte0.nextToken("\n");
                attachments = parseEmbeddedItems(abyte0);
            } else
            if (s.equals("Text"))
            {
                abyte0.expectToken("length", " \t\n");
                int i = abyte0.getIntToken("\n");
                abyte0.skipOneDelimiter("\n");
                notecardText = abyte0.getSubstring(i);
            } else
            {
                throw new com.lumiyaviewer.lumiya.utils.SimpleStringParser.StringParsingException((new StringBuilder()).append("Unknown tag type: ").append(s).toString());
            }
        } while (true);
        if (true) goto _L4; else goto _L3
_L3:
    }

    public static Spanned createSingleEditableAttachment(SLInventoryEntry slinventoryentry)
    {
        SpannableStringBuilder spannablestringbuilder = new SpannableStringBuilder();
        spannablestringbuilder.append("\u27F9");
        spannablestringbuilder.setSpan(new AttachmentSpan(slinventoryentry), 0, spannablestringbuilder.length(), 33);
        return spannablestringbuilder;
    }

    private SLInventoryEntry findAttachmentByCode(int i)
    {
label0:
        {
            if (attachments == null)
            {
                break label0;
            }
            Iterator iterator = attachments.iterator();
            NotecardAttachment notecardattachment;
            do
            {
                if (!iterator.hasNext())
                {
                    break label0;
                }
                notecardattachment = (NotecardAttachment)iterator.next();
            } while (notecardattachment.extCharIndex != i);
            return notecardattachment.entry;
        }
        return null;
    }

    private List parseEmbeddedItems(SimpleStringParser simplestringparser)
        throws com.lumiyaviewer.lumiya.utils.SimpleStringParser.StringParsingException
    {
        simplestringparser.expectToken("{", "\n");
        simplestringparser.expectToken("count", " \t\n");
        int j = simplestringparser.getIntToken("\n");
        ArrayList arraylist = new ArrayList(j);
        for (int i = 0; i < j; i++)
        {
            simplestringparser.expectToken("{", "\n");
            simplestringparser.expectToken("ext", " \t\n").expectToken("char", " \t\n").expectToken("index", " \t\n");
            int k = simplestringparser.getIntToken("\n");
            simplestringparser.expectToken("inv_item", " \t\n");
            simplestringparser.getIntToken("\n");
            arraylist.add(new NotecardAttachment(k, SLInventoryEntry.parseString(simplestringparser)));
            simplestringparser.expectToken("}", "\n");
        }

        simplestringparser.expectToken("}", "\n");
        return arraylist;
    }

    public byte[] toLindenText()
    {
        StringBuilder stringbuilder = new StringBuilder();
        if (isScript)
        {
            stringbuilder.append(notecardText);
        } else
        {
            stringbuilder.append("Linden text version 2\n{\n");
            stringbuilder.append("LLEmbeddedItems version 1\n{\n");
            stringbuilder.append("count ").append(attachments.size()).append("\n");
            for (Iterator iterator = attachments.iterator(); iterator.hasNext(); stringbuilder.append("}\n"))
            {
                NotecardAttachment notecardattachment = (NotecardAttachment)iterator.next();
                stringbuilder.append("{\n").append("ext char index ").append(notecardattachment.extCharIndex).append("\n");
                stringbuilder.append("\tinv_item\t0\n").append("\t{\n");
                stringbuilder.append("\t\t").append("item_id").append("\t").append(notecardattachment.entry.uuid.toString()).append("\n");
                if (notecardattachment.entry.parentUUID != null)
                {
                    stringbuilder.append("\t\t").append("parent_id").append("\t").append(notecardattachment.entry.parentUUID.toString()).append("\n");
                }
                stringbuilder.append("\t").append("permissions").append(" 0\n");
                stringbuilder.append("\t").append("{\n");
                stringbuilder.append("\t\t").append("base_mask").append("\t").append(String.format("%08x", new Object[] {
                    Integer.valueOf(notecardattachment.entry.baseMask)
                })).append("\n");
                stringbuilder.append("\t\t").append("owner_mask").append("\t").append(String.format("%08x", new Object[] {
                    Integer.valueOf(notecardattachment.entry.ownerMask)
                })).append("\n");
                stringbuilder.append("\t\t").append("group_mask").append("\t").append(String.format("%08x", new Object[] {
                    Integer.valueOf(notecardattachment.entry.groupMask)
                })).append("\n");
                stringbuilder.append("\t\t").append("everyone_mask").append("\t").append(String.format("%08x", new Object[] {
                    Integer.valueOf(notecardattachment.entry.everyoneMask)
                })).append("\n");
                stringbuilder.append("\t\t").append("next_owner_mask").append("\t").append(String.format("%08x", new Object[] {
                    Integer.valueOf(notecardattachment.entry.nextOwnerMask)
                })).append("\n");
                if (notecardattachment.entry.creatorUUID != null)
                {
                    stringbuilder.append("\t\t").append("creator_id").append("\t").append(notecardattachment.entry.creatorUUID.toString()).append("\n");
                }
                if (notecardattachment.entry.ownerUUID != null)
                {
                    stringbuilder.append("\t\t").append("owner_id").append("\t").append(notecardattachment.entry.ownerUUID.toString()).append("\n");
                }
                if (notecardattachment.entry.lastOwnerUUID != null)
                {
                    stringbuilder.append("\t\t").append("last_owner_id").append("\t").append(notecardattachment.entry.lastOwnerUUID.toString()).append("\n");
                }
                if (notecardattachment.entry.groupUUID != null)
                {
                    stringbuilder.append("\t\t").append("group_id").append("\t").append(notecardattachment.entry.groupUUID.toString()).append("\n");
                }
                stringbuilder.append("\t").append("}\n");
                if (notecardattachment.entry.assetUUID != null)
                {
                    stringbuilder.append("\t\t").append("asset_id").append("\t").append(notecardattachment.entry.assetUUID.toString()).append("\n");
                }
                stringbuilder.append("\t\t").append("type").append("\t").append(SLAssetType.getByType(notecardattachment.entry.assetType).getStringCode()).append("\n");
                stringbuilder.append("\t\t").append("inv_type").append("\t").append(SLInventoryType.getByType(notecardattachment.entry.invType).getStringCode()).append("\n");
                stringbuilder.append("\t\t").append("flags").append("\t").append(String.format("%08x", new Object[] {
                    Integer.valueOf(notecardattachment.entry.flags)
                })).append("\n");
                stringbuilder.append("\t").append("sale_info").append("\t0\n");
                stringbuilder.append("\t").append("{\n");
                stringbuilder.append("\t\t").append("sale_type").append("\t").append(SLSaleType.getByType(notecardattachment.entry.saleType).getStringCode()).append("\n");
                stringbuilder.append("\t\t").append("sale_price").append("\t").append(notecardattachment.entry.salePrice).append("\n");
                stringbuilder.append("\t").append("}\n");
                stringbuilder.append("\t\t").append("name").append("\t").append(notecardattachment.entry.name).append("|\n");
                stringbuilder.append("\t\t").append("desc").append("\t").append(notecardattachment.entry.description).append("|\n");
                stringbuilder.append("\t\t").append("creation_date").append("\t").append(notecardattachment.entry.creationDate).append("\n");
                stringbuilder.append("\t}\n");
            }

            stringbuilder.append("}\n");
            try
            {
                byte abyte0[] = notecardText.getBytes("UTF-8");
                stringbuilder.append("Text length ").append(abyte0.length).append("\n");
                stringbuilder.append(notecardText);
            }
            catch (UnsupportedEncodingException unsupportedencodingexception)
            {
                unsupportedencodingexception.printStackTrace();
            }
            stringbuilder.append("}\n");
        }
        return SLMessage.stringToVariableUTF(stringbuilder.toString());
    }

    public SpannableStringBuilder toSpannableString(boolean flag, OnAttachmentClickListener onattachmentclicklistener)
    {
        SpannableStringBuilder spannablestringbuilder;
        int i;
        i = 0;
        spannablestringbuilder = new SpannableStringBuilder();
        if (isScript)
        {
            spannablestringbuilder.append(notecardText);
            return spannablestringbuilder;
        }
          goto _L1
_L3:
        int j;
        Object obj = findAttachmentByCode(notecardText.charAt(j) - 56320);
        if (obj != null)
        {
            Object obj1;
            int k;
            if (flag)
            {
                obj1 = new AttachmentSpan(((SLInventoryEntry) (obj)));
                obj = "\u27F9";
            } else
            {
                obj1 = new AttachmentClickableSpan(((SLInventoryEntry) (obj)), onattachmentclicklistener);
                obj = ((SLInventoryEntry) (obj)).getReadableTextForLink();
            }
            i = spannablestringbuilder.length();
            spannablestringbuilder.append(((CharSequence) (obj)));
            spannablestringbuilder.setSpan(obj1, i, spannablestringbuilder.length(), 33);
        }
        i = j + 1;
_L1:
        if (i >= notecardText.length())
        {
            break; /* Loop/switch isn't completed */
        }
        k = notecardText.indexOf('\uDBC0', i);
        j = k;
        if (k < 0)
        {
            j = notecardText.length();
        }
        spannablestringbuilder.append(notecardText.substring(i, j));
        if (j >= notecardText.length())
        {
            break MISSING_BLOCK_LABEL_218;
        }
        j++;
        if (j < notecardText.length()) goto _L3; else goto _L2
_L2:
        return spannablestringbuilder;
        i = j;
          goto _L1
    }
}
