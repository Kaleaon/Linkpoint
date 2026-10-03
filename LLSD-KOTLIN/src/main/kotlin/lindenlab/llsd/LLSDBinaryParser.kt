package lindenlab.llsd

import com.linkpoint.protocol.llsd.toLlsd
import java.io.IOException
import java.io.InputStream

class LLSDBinaryParser {
    @Throws(IOException::class, LLSDException::class)
    fun parse(binaryInput: InputStream): LLSD {
        val bytes = binaryInput.readBytes()
        val value = com.linkpoint.protocol.llsd.LLSDParser.parseBinary(bytes)
        return value.toLlsd()
    }
}
