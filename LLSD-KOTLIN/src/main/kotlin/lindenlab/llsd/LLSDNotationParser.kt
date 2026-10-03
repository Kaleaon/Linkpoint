package lindenlab.llsd

import com.linkpoint.protocol.llsd.toLlsd
import java.io.IOException
import java.io.InputStream

class LLSDNotationParser {
    @Throws(IOException::class, LLSDException::class)
    fun parse(notationInput: InputStream): LLSD {
        val bytes = notationInput.readBytes()
        val value = com.linkpoint.protocol.llsd.LLSDParser.parseNotation(bytes)
        return value.toLlsd()
    }
}
