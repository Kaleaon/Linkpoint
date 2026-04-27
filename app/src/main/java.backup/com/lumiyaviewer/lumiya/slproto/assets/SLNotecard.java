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
import java.util.List;

public class SLNotecard {
    private static final String DELIM_ANY = " \t\n";
    private static final String DELIM_EOL = "\n";
    private List<NotecardAttachment> attachments;
    private boolean isScript;
    private String notecardText;

    private static class AttachmentClickableSpan extends ClickableSpan implements InventoryEntrySpan {
        private OnAttachmentClickListener clickListener;
        private SLInventoryEntry entry;

        public AttachmentClickableSpan(SLInventoryEntry sLInventoryEntry, OnAttachmentClickListener onAttachmentClickListener) {
            this.entry = sLInventoryEntry;
            this.clickListener = onAttachmentClickListener;
        }

        public SLInventoryEntry getEntry() {
            return this.entry;
        }

        public void onClick(View view) {
            if (this.clickListener != null) {
                this.clickListener.onAttachmentClick(this.entry);
            }
        }
    }

    private static class AttachmentSpan extends ReplacementSpan implements InventoryEntrySpan {
        private SLInventoryEntry entry;
        private String linkText;

        public AttachmentSpan(SLInventoryEntry sLInventoryEntry) {
            this.entry = sLInventoryEntry;
            this.linkText = sLInventoryEntry.getReadableTextForLink();
        }

        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            if (i != i2) {
                Paint paint2 = new Paint(paint);
                paint2.setUnderlineText(true);
                paint2.setColor(Color.rgb(0, 50, 100));
                canvas.drawText(this.linkText, 0, this.linkText.length(), f, (float) i4, paint2);
            }
        }

        public SLInventoryEntry getEntry() {
            return this.entry;
        }

        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            if (fontMetricsInt != null) {
                Paint.FontMetricsInt fontMetricsInt2 = paint.getFontMetricsInt();
                fontMetricsInt.ascent = fontMetricsInt2.ascent;
                fontMetricsInt.bottom = fontMetricsInt2.bottom;
                fontMetricsInt.descent = fontMetricsInt2.descent;
                fontMetricsInt.leading = fontMetricsInt2.leading;
                fontMetricsInt.top = fontMetricsInt2.top;
            }
            if (i != i2) {
                return (int) paint.measureText(this.linkText, 0, this.linkText.length());
            }
            return 0;
        }
    }

    private interface InventoryEntrySpan {
        SLInventoryEntry getEntry();
    }

    private static class NotecardAttachment {
        SLInventoryEntry entry;
        int extCharIndex;

        public NotecardAttachment(int i, SLInventoryEntry sLInventoryEntry) {
            this.extCharIndex = i;
            this.entry = sLInventoryEntry;
        }
    }

    public interface OnAttachmentClickListener {
        void onAttachmentClick(SLInventoryEntry sLInventoryEntry);
    }

    public SLNotecard(Spanned spanned, boolean z) {
        this.isScript = z;
        StringBuilder sb = new StringBuilder();
        this.attachments = new ArrayList(0);
        InventoryEntrySpan[] inventoryEntrySpanArr = (InventoryEntrySpan[]) spanned.getSpans(0, spanned.length(), InventoryEntrySpan.class);
        int[] iArr = new int[inventoryEntrySpanArr.length];
        int[] iArr2 = new int[inventoryEntrySpanArr.length];
        for (int i = 0; i < inventoryEntrySpanArr.length; i++) {
            iArr[i] = spanned.getSpanStart(inventoryEntrySpanArr[i]);
            iArr2[i] = spanned.getSpanEnd(inventoryEntrySpanArr[i]);
        }
        int i2 = 0;
        int i3 = 0;
        while (i3 < spanned.length()) {
            int i4 = 0;
            while (true) {
                if (i4 >= inventoryEntrySpanArr.length) {
                    i4 = -1;
                    break;
                } else if (iArr[i4] >= i3) {
                    break;
                } else {
                    i4++;
                }
            }
            int length = i4 != -1 ? iArr[i4] : spanned.length();
            sb.append(spanned.subSequence(i3, length));
            if (i4 != -1) {
                this.attachments.add(new NotecardAttachment(i2, inventoryEntrySpanArr[i4].getEntry()));
                sb.append(56256);
                sb.append((char) (56320 + i2));
                i2++;
                i3 = iArr2[i4];
            } else {
                i3 = length;
            }
        }
        this.notecardText = sb.toString();
    }

    public SLNotecard(boolean z) {
        this.isScript = z;
        this.attachments = new ArrayList(0);
        this.notecardText = "";
    }

    public SLNotecard(byte[] bArr, boolean z) throws SimpleStringParser.StringParsingException {
        String stringFromVariableUTF = SLMessage.stringFromVariableUTF(bArr);
        this.isScript = z;
        if (!z) {
            SimpleStringParser simpleStringParser = new SimpleStringParser(stringFromVariableUTF, DELIM_ANY);
            simpleStringParser.expectToken("Linden text version 2", DELIM_EOL);
            simpleStringParser.expectToken("{", DELIM_EOL);
            while (true) {
                String nextToken = simpleStringParser.nextToken(DELIM_ANY);
                if (nextToken.equals("}")) {
                    break;
                } else if (nextToken.equals("LLEmbeddedItems")) {
                    simpleStringParser.nextToken(DELIM_EOL);
                    this.attachments = parseEmbeddedItems(simpleStringParser);
                } else if (nextToken.equals("Text")) {
                    simpleStringParser.expectToken("length", DELIM_ANY);
                    int intToken = simpleStringParser.getIntToken(DELIM_EOL);
                    simpleStringParser.skipOneDelimiter(DELIM_EOL);
                    this.notecardText = simpleStringParser.getSubstring(intToken);
                } else {
                    throw new SimpleStringParser.StringParsingException("Unknown tag type: " + nextToken);
                }
            }
        } else {
            this.notecardText = stringFromVariableUTF;
        }
        if (this.attachments == null) {
            this.attachments = new ArrayList(0);
        }
    }

    public static Spanned createSingleEditableAttachment(SLInventoryEntry sLInventoryEntry) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append("⟹");
        spannableStringBuilder.setSpan(new AttachmentSpan(sLInventoryEntry), 0, spannableStringBuilder.length(), 33);
        return spannableStringBuilder;
    }

    private SLInventoryEntry findAttachmentByCode(int i) {
        if (this.attachments != null) {
            for (NotecardAttachment notecardAttachment : this.attachments) {
                if (notecardAttachment.extCharIndex == i) {
                    return notecardAttachment.entry;
                }
            }
        }
        return null;
    }

    private List<NotecardAttachment> parseEmbeddedItems(SimpleStringParser simpleStringParser) throws SimpleStringParser.StringParsingException {
        simpleStringParser.expectToken("{", DELIM_EOL);
        simpleStringParser.expectToken("count", DELIM_ANY);
        int intToken = simpleStringParser.getIntToken(DELIM_EOL);
        ArrayList arrayList = new ArrayList(intToken);
        for (int i = 0; i < intToken; i++) {
            simpleStringParser.expectToken("{", DELIM_EOL);
            simpleStringParser.expectToken("ext", DELIM_ANY).expectToken("char", DELIM_ANY).expectToken("index", DELIM_ANY);
            int intToken2 = simpleStringParser.getIntToken(DELIM_EOL);
            simpleStringParser.expectToken("inv_item", DELIM_ANY);
            simpleStringParser.getIntToken(DELIM_EOL);
            arrayList.add(new NotecardAttachment(intToken2, SLInventoryEntry.parseString(simpleStringParser)));
            simpleStringParser.expectToken("}", DELIM_EOL);
        }
        simpleStringParser.expectToken("}", DELIM_EOL);
        return arrayList;
    }

    public byte[] toLindenText() {
        StringBuilder sb = new StringBuilder();
        if (this.isScript) {
            sb.append(this.notecardText);
        } else {
            sb.append("Linden text version 2\n{\n");
            sb.append("LLEmbeddedItems version 1\n{\n");
            sb.append("count ").append(this.attachments.size()).append(DELIM_EOL);
            for (NotecardAttachment notecardAttachment : this.attachments) {
                sb.append("{\n").append("ext char index ").append(notecardAttachment.extCharIndex).append(DELIM_EOL);
                sb.append("\tinv_item\t0\n").append("\t{\n");
                sb.append("\t\t").append("item_id").append("\t").append(notecardAttachment.entry.uuid.toString()).append(DELIM_EOL);
                if (notecardAttachment.entry.parentUUID != null) {
                    sb.append("\t\t").append("parent_id").append("\t").append(notecardAttachment.entry.parentUUID.toString()).append(DELIM_EOL);
                }
                sb.append("\t").append("permissions").append(" 0\n");
                sb.append("\t").append("{\n");
                sb.append("\t\t").append("base_mask").append("\t").append(String.format("%08x", new Object[]{Integer.valueOf(notecardAttachment.entry.baseMask)})).append(DELIM_EOL);
                sb.append("\t\t").append("owner_mask").append("\t").append(String.format("%08x", new Object[]{Integer.valueOf(notecardAttachment.entry.ownerMask)})).append(DELIM_EOL);
                sb.append("\t\t").append("group_mask").append("\t").append(String.format("%08x", new Object[]{Integer.valueOf(notecardAttachment.entry.groupMask)})).append(DELIM_EOL);
                sb.append("\t\t").append("everyone_mask").append("\t").append(String.format("%08x", new Object[]{Integer.valueOf(notecardAttachment.entry.everyoneMask)})).append(DELIM_EOL);
                sb.append("\t\t").append("next_owner_mask").append("\t").append(String.format("%08x", new Object[]{Integer.valueOf(notecardAttachment.entry.nextOwnerMask)})).append(DELIM_EOL);
                if (notecardAttachment.entry.creatorUUID != null) {
                    sb.append("\t\t").append("creator_id").append("\t").append(notecardAttachment.entry.creatorUUID.toString()).append(DELIM_EOL);
                }
                if (notecardAttachment.entry.ownerUUID != null) {
                    sb.append("\t\t").append("owner_id").append("\t").append(notecardAttachment.entry.ownerUUID.toString()).append(DELIM_EOL);
                }
                if (notecardAttachment.entry.lastOwnerUUID != null) {
                    sb.append("\t\t").append("last_owner_id").append("\t").append(notecardAttachment.entry.lastOwnerUUID.toString()).append(DELIM_EOL);
                }
                if (notecardAttachment.entry.groupUUID != null) {
                    sb.append("\t\t").append("group_id").append("\t").append(notecardAttachment.entry.groupUUID.toString()).append(DELIM_EOL);
                }
                sb.append("\t").append("}\n");
                if (notecardAttachment.entry.assetUUID != null) {
                    sb.append("\t\t").append("asset_id").append("\t").append(notecardAttachment.entry.assetUUID.toString()).append(DELIM_EOL);
                }
                sb.append("\t\t").append("type").append("\t").append(SLAssetType.getByType(notecardAttachment.entry.assetType).getStringCode()).append(DELIM_EOL);
                sb.append("\t\t").append("inv_type").append("\t").append(SLInventoryType.getByType(notecardAttachment.entry.invType).getStringCode()).append(DELIM_EOL);
                sb.append("\t\t").append("flags").append("\t").append(String.format("%08x", new Object[]{Integer.valueOf(notecardAttachment.entry.flags)})).append(DELIM_EOL);
                sb.append("\t").append("sale_info").append("\t0\n");
                sb.append("\t").append("{\n");
                sb.append("\t\t").append("sale_type").append("\t").append(SLSaleType.getByType(notecardAttachment.entry.saleType).getStringCode()).append(DELIM_EOL);
                sb.append("\t\t").append("sale_price").append("\t").append(notecardAttachment.entry.salePrice).append(DELIM_EOL);
                sb.append("\t").append("}\n");
                sb.append("\t\t").append("name").append("\t").append(notecardAttachment.entry.name).append("|\n");
                sb.append("\t\t").append("desc").append("\t").append(notecardAttachment.entry.description).append("|\n");
                sb.append("\t\t").append("creation_date").append("\t").append(notecardAttachment.entry.creationDate).append(DELIM_EOL);
                sb.append("\t}\n");
                sb.append("}\n");
            }
            sb.append("}\n");
            try {
                sb.append("Text length ").append(this.notecardText.getBytes("UTF-8").length).append(DELIM_EOL);
                sb.append(this.notecardText);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sb.append("}\n");
        }
        return SLMessage.stringToVariableUTF(sb.toString());
    }

    /* JADX WARNING: CFG modification limit reached, blocks count: 127 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.text.SpannableStringBuilder toSpannableString(boolean r7, com.lumiyaviewer.lumiya.slproto.assets.SLNotecard.OnAttachmentClickListener r8) {
        /*
            r6 = this;
            r0 = 0
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder
            r2.<init>()
            boolean r1 = r6.isScript
            if (r1 == 0) goto L_0x003c
            java.lang.String r0 = r6.notecardText
            r2.append(r0)
            return r2
        L_0x0010:
            java.lang.String r0 = r6.notecardText
            char r0 = r0.charAt(r3)
            r1 = 56320(0xdc00, float:7.8921E-41)
            int r0 = r0 - r1
            com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry r0 = r6.findAttachmentByCode(r0)
            if (r0 == 0) goto L_0x003a
            if (r7 == 0) goto L_0x0071
            com.lumiyaviewer.lumiya.slproto.assets.SLNotecard$AttachmentSpan r1 = new com.lumiyaviewer.lumiya.slproto.assets.SLNotecard$AttachmentSpan
            r1.<init>(r0)
            java.lang.String r0 = "⟹"
        L_0x002a:
            int r4 = r2.length()
            r2.append(r0)
            int r0 = r2.length()
            r5 = 33
            r2.setSpan(r1, r4, r0, r5)
        L_0x003a:
            int r0 = r3 + 1
        L_0x003c:
            java.lang.String r1 = r6.notecardText
            int r1 = r1.length()
            if (r0 >= r1) goto L_0x0070
            java.lang.String r1 = r6.notecardText
            r3 = 56256(0xdbc0, float:7.8831E-41)
            int r1 = r1.indexOf(r3, r0)
            if (r1 >= 0) goto L_0x0055
            java.lang.String r1 = r6.notecardText
            int r1 = r1.length()
        L_0x0055:
            java.lang.String r3 = r6.notecardText
            java.lang.String r0 = r3.substring(r0, r1)
            r2.append(r0)
            java.lang.String r0 = r6.notecardText
            int r0 = r0.length()
            if (r1 >= r0) goto L_0x007b
            int r3 = r1 + 1
            java.lang.String r0 = r6.notecardText
            int r0 = r0.length()
            if (r3 < r0) goto L_0x0010
        L_0x0070:
            return r2
        L_0x0071:
            com.lumiyaviewer.lumiya.slproto.assets.SLNotecard$AttachmentClickableSpan r1 = new com.lumiyaviewer.lumiya.slproto.assets.SLNotecard$AttachmentClickableSpan
            r1.<init>(r0, r8)
            java.lang.String r0 = r0.getReadableTextForLink()
            goto L_0x002a
        L_0x007b:
            r0 = r1
            goto L_0x003c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.assets.SLNotecard.toSpannableString(boolean, com.lumiyaviewer.lumiya.slproto.assets.SLNotecard$OnAttachmentClickListener):android.text.SpannableStringBuilder");
    }
}
