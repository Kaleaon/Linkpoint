package com.linkpoint.slproto.assets

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ReplacementSpan
import android.view.View
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.inventory.SLAssetType
import com.linkpoint.slproto.inventory.SLInventoryEntry
import com.linkpoint.slproto.inventory.SLInventoryType
import com.linkpoint.slproto.inventory.SLSaleType
import com.linkpoint.utils.SimpleStringParser
import java.io.UnsupportedEncodingException

/**
 * Represents a Second Life notecard with text and embedded inventory items
 */
class SLNotecard {
    
    private val DELIM_ANY = " \t\n"
    private val DELIM_EOL = "\n"
    private var attachments: MutableList<NotecardAttachment>
    private val isScript: Boolean
    private var notecardText: String

    /**
     * Clickable span for notecard attachments
     */
    private inner class AttachmentClickableSpan(
        private val entry: SLInventoryEntry,
        private val clickListener: OnAttachmentClickListener?
    ) : ClickableSpan(), InventoryEntrySpan {
        
        override fun getEntry(): SLInventoryEntry = entry

        override fun onClick(view: View) {
            clickListener?.onAttachmentClick(entry)
        }
    }

    /**
     * Replacement span for displaying attachments in edit mode
     */
    private inner class AttachmentSpan(
        private val entry: SLInventoryEntry
    ) : ReplacementSpan(), InventoryEntrySpan {
        
        private val linkText: String = entry.getReadableTextForLink()

        override fun draw(
            canvas: Canvas,
            text: CharSequence,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
        ) {
            if (start != end) {
                val linkPaint = Paint().apply {
                    isUnderlineText = true
                    color = Color.rgb(0, 50, 100)
                }
                canvas.drawText(linkText, 0, linkText.length, x, y.toFloat(), linkPaint)
            }
        }

        override fun getEntry(): SLInventoryEntry = entry

        override fun getSize(
            paint: Paint,
            text: CharSequence,
            start: Int,
            end: Int,
            fm: Paint.FontMetricsInt?
        ): Int {
            fm?.let {
                val metrics = paint.fontMetricsInt
                it.ascent = metrics.ascent
                it.bottom = metrics.bottom
                it.descent = metrics.descent
                it.leading = metrics.leading
                it.top = metrics.top
            }
            return if (start != end) {
                paint.measureText(linkText, 0, linkText.length).toInt()
            } else {
                0
            }
        }
    }

    /**
     * Interface for inventory entry spans
     */
    private interface InventoryEntrySpan {
        fun getEntry(): SLInventoryEntry
    }

    /**
     * Notecard attachment information
     */
    private class NotecardAttachment(
        val extCharIndex: Int,
        val entry: SLInventoryEntry
    )

    /**
     * Listener for attachment clicks
     */
    interface OnAttachmentClickListener {
        fun onAttachmentClick(entry: SLInventoryEntry)
    }

    /**
     * Create notecard from spanned text (for editing)
     */
    constructor(spanned: Spanned, isScript: Boolean) {
        this.isScript = isScript
        val sb = StringBuilder()
        attachments = ArrayList()
        
        val spans = spanned.getSpans(0, spanned.length, InventoryEntrySpan::class.java)
        val starts = IntArray(spans.size) { i -> spanned.getSpanStart(spans[i]) }
        val ends = IntArray(spans.size) { i -> spanned.getSpanEnd(spans[i]) }
        
        var pos = 0
        var attachmentIndex = 0
        
        while (pos < spanned.length) {
            // Find next span start
            var nextSpanIndex = spans.indices.firstOrNull { starts[it] >= pos } ?: -1
            val nextStart = if (nextSpanIndex != -1) starts[nextSpanIndex] else spanned.length
            
            // Append text before span
            sb.append(spanned.subSequence(pos, nextStart))
            
            // Handle span if found
            if (nextSpanIndex != -1) {
                attachments.add(NotecardAttachment(attachmentIndex, spans[nextSpanIndex].getEntry()))
                sb.append(56256.toChar())
                sb.append((56320 + attachmentIndex).toChar())
                attachmentIndex++
                pos = ends[nextSpanIndex]
            } else {
                pos = nextStart
            }
        }
        
        notecardText = sb.toString()
    }

    /**
     * Create empty notecard
     */
    constructor(isScript: Boolean) {
        this.isScript = isScript
        attachments = ArrayList()
        notecardText = ""
    }

    /**
     * Create notecard from binary data
     */
    @Throws(SimpleStringParser.StringParsingException::class)
    constructor(data: ByteArray, isScript: Boolean) {
        val text = SLMessage.stringFromVariableUTF(data)
        this.isScript = isScript
        
        if (!isScript) {
            val parser = SimpleStringParser(text)
            parser.expectToken("Linden text version 2", DELIM_EOL)
            parser.expectToken("{", DELIM_EOL)
            
            while (true) {
                val token = parser.nextToken(DELIM_ANY)
                when (token) {
                    "}" -> break
                    "LLEmbeddedItems" -> {
                        parser.nextToken(DELIM_EOL)
                        attachments = parseEmbeddedItems(parser).toMutableList()
                    }
                    "Text" -> {
                        parser.expectToken("length", DELIM_ANY)
                        val length = parser.getIntToken(DELIM_EOL)
                        parser.skipOneDelimiter(DELIM_EOL)
                        notecardText = parser.getSubstring(length)
                    }
                    else -> throw SimpleStringParser.StringParsingException("Unknown tag type: $token")
                }
            }
            
            if (!::attachments.isInitialized) {
                attachments = ArrayList()
            }
        } else {
            notecardText = text
            attachments = ArrayList()
        }
    }

    /**
     * Create single editable attachment span
     */
    fun createSingleEditableAttachment(entry: SLInventoryEntry): Spanned {
        val builder = SpannableStringBuilder()
        builder.append("⟹")
        builder.setSpan(AttachmentSpan(entry), 0, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return builder
    }

    /**
     * Find attachment by surrogate pair code
     */
    private fun findAttachmentByCode(code: Int): SLInventoryEntry? {
        return attachments.firstOrNull { it.extCharIndex == code }?.entry
    }

    /**
     * Parse embedded items from notecard
     */
    @Throws(SimpleStringParser.StringParsingException::class)
    private fun parseEmbeddedItems(parser: SimpleStringParser): List<NotecardAttachment> {
        parser.expectToken("{", DELIM_EOL)
        parser.expectToken("count", DELIM_ANY)
        val count = parser.getIntToken(DELIM_EOL)
        
        val items = ArrayList<NotecardAttachment>()
        repeat(count) {
            parser.expectToken("{", DELIM_EOL)
            parser.expectToken("ext", DELIM_ANY)
                .expectToken("char", DELIM_ANY)
                .expectToken("index", DELIM_ANY)
            val charIndex = parser.getIntToken(DELIM_EOL)
            parser.expectToken("inv_item", DELIM_ANY)
            parser.getIntToken(DELIM_EOL)
            items.add(NotecardAttachment(charIndex, SLInventoryEntry.parseString(parser)))
            parser.expectToken("}", DELIM_EOL)
        }
        
        parser.expectToken("}", DELIM_EOL)
        return items
    }

    /**
     * Convert notecard to Linden text format
     */
    fun toLindenText(): ByteArray {
        val sb = StringBuilder()
        
        if (isScript) {
            sb.append(notecardText)
        } else {
            sb.append("Linden text version 2\n{\n")
            sb.append("LLEmbeddedItems version 1\n{\n")
            sb.append("count ${attachments.size}$DELIM_EOL")
            
            for (attachment in attachments) {
                val entry = attachment.entry
                sb.append("{\n")
                    .append("ext char index ${attachment.extCharIndex}$DELIM_EOL")
                    .append("\tinv_item\t0\n")
                    .append("\t{\n")
                    .append("\t\titem_id\t${entry.uuid}$DELIM_EOL")
                
                entry.parentUUID?.let {
                    sb.append("\t\tparent_id\t$it$DELIM_EOL")
                }
                
                sb.append("\tpermissions 0\n")
                    .append("\t{\n")
                    .append("\t\tbase_mask\t${String.format("%08x", entry.baseMask)}$DELIM_EOL")
                    .append("\t\towner_mask\t${String.format("%08x", entry.ownerMask)}$DELIM_EOL")
                    .append("\t\tgroup_mask\t${String.format("%08x", entry.groupMask)}$DELIM_EOL")
                    .append("\t\teveryone_mask\t${String.format("%08x", entry.everyoneMask)}$DELIM_EOL")
                    .append("\t\tnext_owner_mask\t${String.format("%08x", entry.nextOwnerMask)}$DELIM_EOL")
                
                entry.creatorUUID?.let { sb.append("\t\tcreator_id\t$it$DELIM_EOL") }
                entry.ownerUUID?.let { sb.append("\t\towner_id\t$it$DELIM_EOL") }
                entry.lastOwnerUUID?.let { sb.append("\t\tlast_owner_id\t$it$DELIM_EOL") }
                entry.groupUUID?.let { sb.append("\t\tgroup_id\t$it$DELIM_EOL") }
                
                sb.append("\t}\n")
                
                entry.assetUUID?.let { sb.append("\t\tasset_id\t$it$DELIM_EOL") }
                
                sb.append("\t\ttype\t${SLAssetType.getByType(entry.assetType).getStringCode()}$DELIM_EOL")
                    .append("\t\tinv_type\t${SLInventoryType.getByType(entry.invType).getStringCode()}$DELIM_EOL")
                    .append("\t\tflags\t${String.format("%08x", entry.flags)}$DELIM_EOL")
                    .append("\tsale_info\t0\n")
                    .append("\t{\n")
                    .append("\t\tsale_type\t${SLSaleType.getByType(entry.saleType).getStringCode()}$DELIM_EOL")
                    .append("\t\tsale_price\t${entry.salePrice}$DELIM_EOL")
                    .append("\t}\n")
                    .append("\t\tname\t${entry.name}|\n")
                    .append("\t\tdesc\t${entry.description}|\n")
                    .append("\t\tcreation_date\t${entry.creationDate}$DELIM_EOL")
                    .append("\t}\n")
                    .append("}\n")
            }
            
            sb.append("}\n")
            
            try {
                sb.append("Text length ${notecardText.toByteArray(Charsets.UTF_8).size}$DELIM_EOL")
                    .append(notecardText)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            
            sb.append("}\n")
        }
        
        return SLMessage.stringToVariableUTF(sb.toString())
    }

    /**
     * Convert notecard to spannable string for display
     */
    fun toSpannableString(
        editable: Boolean,
        clickListener: OnAttachmentClickListener?
    ): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        
        if (isScript) {
            builder.append(notecardText)
            return builder
        }
        
        var pos = 0
        while (pos < notecardText.length) {
            // Find next attachment marker (surrogate pair)
            val markerPos = notecardText.indexOf(56256.toChar(), pos)
            val nextMarkerPos = if (markerPos >= 0) markerPos else notecardText.length
            
            // Append text before marker
            builder.append(notecardText.substring(pos, nextMarkerPos))
            
            // Handle attachment if found
            if (markerPos >= 0 && markerPos + 1 < notecardText.length) {
                val attachmentCode = notecardText[markerPos + 1].code - 56320
                val entry = findAttachmentByCode(attachmentCode)
                
                entry?.let {
                    val spanStart = builder.length
                    val text: String
                    val span: Any
                    
                    if (editable) {
                        text = "⟹"
                        span = AttachmentSpan(it)
                    } else {
                        text = it.getReadableTextForLink()
                        span = AttachmentClickableSpan(it, clickListener)
                    }
                    
                    builder.append(text)
                    builder.setSpan(span, spanStart, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                
                pos = markerPos + 2
            } else {
                pos = nextMarkerPos
            }
        }
        
        return builder
    }
}
