// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.assets;

import android.graphics.Paint$FontMetricsInt;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.text.style.ClickableSpan;
import java.io.UnsupportedEncodingException;
import com.lumiyaviewer.lumiya.slproto.inventory.SLSaleType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import java.util.Iterator;
import android.text.SpannableStringBuilder;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.utils.SimpleStringParser;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.util.ArrayList;
import android.text.Spanned;
import java.util.List;

public class SLNotecard
{
    private static final String DELIM_ANY = " \t\n";
    private static final String DELIM_EOL = "\n";
    private List<NotecardAttachment> attachments;
    private boolean isScript;
    private String notecardText;
    
    public SLNotecard(final Spanned spanned, final boolean isScript) {
        this.isScript = isScript;
        final StringBuilder sb = new StringBuilder();
        this.attachments = new ArrayList<NotecardAttachment>(0);
        final InventoryEntrySpan[] array = (InventoryEntrySpan[])spanned.getSpans(0, spanned.length(), (Class)InventoryEntrySpan.class);
        final int[] array2 = new int[array.length];
        final int[] array3 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = spanned.getSpanStart((Object)array[i]);
            array3[i] = spanned.getSpanEnd((Object)array[i]);
        }
        int n = 0;
        int j = 0;
    Label_0118:
        while (j < spanned.length()) {
            int k = 0;
            while (true) {
                while (k < array.length) {
                    if (array2[k] >= j) {
                        final int n2 = k;
                        int length = spanned.length();
                        if (n2 != -1) {
                            length = array2[n2];
                        }
                        sb.append(spanned.subSequence(j, length));
                        if (n2 != -1) {
                            this.attachments.add(new NotecardAttachment(n, array[n2].getEntry()));
                            sb.append('\udbc0');
                            sb.append((char)(56320 + n));
                            ++n;
                            j = array3[n2];
                            continue Label_0118;
                        }
                        j = length;
                        continue Label_0118;
                    }
                    else {
                        ++k;
                    }
                }
                final int n2 = -1;
                continue;
            }
        }
        this.notecardText = sb.toString();
    }
    
    public SLNotecard(final boolean isScript) {
        this.isScript = isScript;
        this.attachments = new ArrayList<NotecardAttachment>(0);
        this.notecardText = "";
    }
    
    public SLNotecard(final byte[] array, final boolean isScript) throws SimpleStringParser.StringParsingException {
        final String stringFromVariableUTF = SLMessage.stringFromVariableUTF(array);
        this.isScript = isScript;
        Label_0023: {
            if (!isScript) {
                final SimpleStringParser simpleStringParser = new SimpleStringParser(stringFromVariableUTF, " \t\n");
                simpleStringParser.expectToken("Linden text version 2", "\n");
                simpleStringParser.expectToken("{", "\n");
                String nextToken;
                while (true) {
                    nextToken = simpleStringParser.nextToken(" \t\n");
                    if (nextToken.equals("}")) {
                        break Label_0023;
                    }
                    if (nextToken.equals("LLEmbeddedItems")) {
                        simpleStringParser.nextToken("\n");
                        this.attachments = this.parseEmbeddedItems(simpleStringParser);
                    }
                    else {
                        if (!nextToken.equals("Text")) {
                            break;
                        }
                        simpleStringParser.expectToken("length", " \t\n");
                        final int intToken = simpleStringParser.getIntToken("\n");
                        simpleStringParser.skipOneDelimiter("\n");
                        this.notecardText = simpleStringParser.getSubstring(intToken);
                    }
                }
                throw new SimpleStringParser.StringParsingException("Unknown tag type: " + nextToken);
            }
            this.notecardText = stringFromVariableUTF;
        }
        if (this.attachments == null) {
            this.attachments = new ArrayList<NotecardAttachment>(0);
        }
    }
    
    public static Spanned createSingleEditableAttachment(final SLInventoryEntry slInventoryEntry) {
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence)"\u27f9");
        spannableStringBuilder.setSpan((Object)new AttachmentSpan(slInventoryEntry), 0, spannableStringBuilder.length(), 33);
        return (Spanned)spannableStringBuilder;
    }
    
    private SLInventoryEntry findAttachmentByCode(final int n) {
        if (this.attachments != null) {
            for (final NotecardAttachment notecardAttachment : this.attachments) {
                if (notecardAttachment.extCharIndex == n) {
                    return notecardAttachment.entry;
                }
            }
        }
        return null;
    }
    
    private List<NotecardAttachment> parseEmbeddedItems(final SimpleStringParser simpleStringParser) throws SimpleStringParser.StringParsingException {
        simpleStringParser.expectToken("{", "\n");
        simpleStringParser.expectToken("count", " \t\n");
        final int intToken = simpleStringParser.getIntToken("\n");
        final ArrayList list = new ArrayList(intToken);
        for (int i = 0; i < intToken; ++i) {
            simpleStringParser.expectToken("{", "\n");
            simpleStringParser.expectToken("ext", " \t\n").expectToken("char", " \t\n").expectToken("index", " \t\n");
            final int intToken2 = simpleStringParser.getIntToken("\n");
            simpleStringParser.expectToken("inv_item", " \t\n");
            simpleStringParser.getIntToken("\n");
            list.add((Object)new NotecardAttachment(intToken2, SLInventoryEntry.parseString(simpleStringParser)));
            simpleStringParser.expectToken("}", "\n");
        }
        simpleStringParser.expectToken("}", "\n");
        return (List<NotecardAttachment>)list;
    }
    
    public byte[] toLindenText() {
        final StringBuilder sb = new StringBuilder();
        if (this.isScript) {
            sb.append(this.notecardText);
        }
        else {
            sb.append("Linden text version 2\n{\n");
            sb.append("LLEmbeddedItems version 1\n{\n");
            sb.append("count ").append(this.attachments.size()).append("\n");
            for (final NotecardAttachment notecardAttachment : this.attachments) {
                sb.append("{\n").append("ext char index ").append(notecardAttachment.extCharIndex).append("\n");
                sb.append("\tinv_item\t0\n").append("\t{\n");
                sb.append("\t\t").append("item_id").append("\t").append(notecardAttachment.entry.uuid.toString()).append("\n");
                if (notecardAttachment.entry.parentUUID != null) {
                    sb.append("\t\t").append("parent_id").append("\t").append(notecardAttachment.entry.parentUUID.toString()).append("\n");
                }
                sb.append("\t").append("permissions").append(" 0\n");
                sb.append("\t").append("{\n");
                sb.append("\t\t").append("base_mask").append("\t").append(String.format("%08x", notecardAttachment.entry.baseMask)).append("\n");
                sb.append("\t\t").append("owner_mask").append("\t").append(String.format("%08x", notecardAttachment.entry.ownerMask)).append("\n");
                sb.append("\t\t").append("group_mask").append("\t").append(String.format("%08x", notecardAttachment.entry.groupMask)).append("\n");
                sb.append("\t\t").append("everyone_mask").append("\t").append(String.format("%08x", notecardAttachment.entry.everyoneMask)).append("\n");
                sb.append("\t\t").append("next_owner_mask").append("\t").append(String.format("%08x", notecardAttachment.entry.nextOwnerMask)).append("\n");
                if (notecardAttachment.entry.creatorUUID != null) {
                    sb.append("\t\t").append("creator_id").append("\t").append(notecardAttachment.entry.creatorUUID.toString()).append("\n");
                }
                if (notecardAttachment.entry.ownerUUID != null) {
                    sb.append("\t\t").append("owner_id").append("\t").append(notecardAttachment.entry.ownerUUID.toString()).append("\n");
                }
                if (notecardAttachment.entry.lastOwnerUUID != null) {
                    sb.append("\t\t").append("last_owner_id").append("\t").append(notecardAttachment.entry.lastOwnerUUID.toString()).append("\n");
                }
                if (notecardAttachment.entry.groupUUID != null) {
                    sb.append("\t\t").append("group_id").append("\t").append(notecardAttachment.entry.groupUUID.toString()).append("\n");
                }
                sb.append("\t").append("}\n");
                if (notecardAttachment.entry.assetUUID != null) {
                    sb.append("\t\t").append("asset_id").append("\t").append(notecardAttachment.entry.assetUUID.toString()).append("\n");
                }
                sb.append("\t\t").append("type").append("\t").append(SLAssetType.getByType(notecardAttachment.entry.assetType).getStringCode()).append("\n");
                sb.append("\t\t").append("inv_type").append("\t").append(SLInventoryType.getByType(notecardAttachment.entry.invType).getStringCode()).append("\n");
                sb.append("\t\t").append("flags").append("\t").append(String.format("%08x", notecardAttachment.entry.flags)).append("\n");
                sb.append("\t").append("sale_info").append("\t0\n");
                sb.append("\t").append("{\n");
                sb.append("\t\t").append("sale_type").append("\t").append(SLSaleType.getByType(notecardAttachment.entry.saleType).getStringCode()).append("\n");
                sb.append("\t\t").append("sale_price").append("\t").append(notecardAttachment.entry.salePrice).append("\n");
                sb.append("\t").append("}\n");
                sb.append("\t\t").append("name").append("\t").append(notecardAttachment.entry.name).append("|\n");
                sb.append("\t\t").append("desc").append("\t").append(notecardAttachment.entry.description).append("|\n");
                sb.append("\t\t").append("creation_date").append("\t").append(notecardAttachment.entry.creationDate).append("\n");
                sb.append("\t}\n");
                sb.append("}\n");
            }
            sb.append("}\n");
            while (true) {
                try {
                    sb.append("Text length ").append(this.notecardText.getBytes("UTF-8").length).append("\n");
                    sb.append(this.notecardText);
                    sb.append("}\n");
                }
                catch (final UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                    continue;
                }
                break;
            }
        }
        return SLMessage.stringToVariableUTF(sb.toString());
    }
    
    public SpannableStringBuilder toSpannableString(final boolean b, final OnAttachmentClickListener onAttachmentClickListener) {
        int i = 0;
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (this.isScript) {
            spannableStringBuilder.append((CharSequence)this.notecardText);
            return spannableStringBuilder;
        }
        while (i < this.notecardText.length()) {
            int n;
            if ((n = this.notecardText.indexOf(56256, i)) < 0) {
                n = this.notecardText.length();
            }
            spannableStringBuilder.append((CharSequence)this.notecardText.substring(i, n));
            if (n < this.notecardText.length()) {
                if (++n >= this.notecardText.length()) {
                    break;
                }
                final SLInventoryEntry attachmentByCode = this.findAttachmentByCode(this.notecardText.charAt(n) - '\udc00');
                if (attachmentByCode != null) {
                    InventoryEntrySpan inventoryEntrySpan;
                    String readableTextForLink;
                    if (b) {
                        inventoryEntrySpan = new AttachmentSpan(attachmentByCode);
                        readableTextForLink = "\u27f9";
                    }
                    else {
                        inventoryEntrySpan = new AttachmentClickableSpan(attachmentByCode, onAttachmentClickListener);
                        readableTextForLink = attachmentByCode.getReadableTextForLink();
                    }
                    final int length = spannableStringBuilder.length();
                    spannableStringBuilder.append((CharSequence)readableTextForLink);
                    spannableStringBuilder.setSpan((Object)inventoryEntrySpan, length, spannableStringBuilder.length(), 33);
                }
                i = n + 1;
            }
            else {
                i = n;
            }
        }
        return spannableStringBuilder;
    }
    
    private static class AttachmentClickableSpan extends ClickableSpan implements InventoryEntrySpan
    {
        private OnAttachmentClickListener clickListener;
        private SLInventoryEntry entry;
        
        public AttachmentClickableSpan(final SLInventoryEntry entry, final OnAttachmentClickListener clickListener) {
            this.entry = entry;
            this.clickListener = clickListener;
        }
        
        public SLInventoryEntry getEntry() {
            return this.entry;
        }
        
        public void onClick(final View view) {
            if (this.clickListener != null) {
                this.clickListener.onAttachmentClick(this.entry);
            }
        }
    }
    
    private interface InventoryEntrySpan
    {
        SLInventoryEntry getEntry();
    }
    
    private static class AttachmentSpan extends ReplacementSpan implements InventoryEntrySpan
    {
        private SLInventoryEntry entry;
        private String linkText;
        
        public AttachmentSpan(final SLInventoryEntry entry) {
            this.entry = entry;
            this.linkText = entry.getReadableTextForLink();
        }
        
        public void draw(final Canvas canvas, final CharSequence charSequence, final int n, final int n2, final float n3, final int n4, final int n5, final int n6, final Paint paint) {
            if (n != n2) {
                final Paint paint2 = new Paint(paint);
                paint2.setUnderlineText(true);
                paint2.setColor(Color.rgb(0, 50, 100));
                canvas.drawText(this.linkText, 0, this.linkText.length(), n3, (float)n5, paint2);
            }
        }
        
        public SLInventoryEntry getEntry() {
            return this.entry;
        }
        
        public int getSize(final Paint paint, final CharSequence charSequence, final int n, final int n2, final Paint$FontMetricsInt paint$FontMetricsInt) {
            if (paint$FontMetricsInt != null) {
                final Paint$FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
                paint$FontMetricsInt.ascent = fontMetricsInt.ascent;
                paint$FontMetricsInt.bottom = fontMetricsInt.bottom;
                paint$FontMetricsInt.descent = fontMetricsInt.descent;
                paint$FontMetricsInt.leading = fontMetricsInt.leading;
                paint$FontMetricsInt.top = fontMetricsInt.top;
            }
            if (n != n2) {
                return (int)paint.measureText(this.linkText, 0, this.linkText.length());
            }
            return 0;
        }
    }
    
    private static class NotecardAttachment
    {
        SLInventoryEntry entry;
        int extCharIndex;
        
        public NotecardAttachment(final int extCharIndex, final SLInventoryEntry entry) {
            this.extCharIndex = extCharIndex;
            this.entry = entry;
        }
    }
    
    public interface OnAttachmentClickListener
    {
        void onAttachmentClick(final SLInventoryEntry p0);
    }
}
