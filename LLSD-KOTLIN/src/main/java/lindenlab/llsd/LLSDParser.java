/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 */

package lindenlab.llsd;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Base64;

/**
 * A parser for LLSD (Linden Lab Structured Data) in its traditional XML format.
 * <p>
 * This class is responsible for taking an {@link InputStream} containing an
 * LLSD XML document and converting it into a Java object representation, which is
 * then wrapped in an {@link LLSD} object. It uses the standard Java XML parsing
 * APIs (DOM) to process the document.
 * <p>
 * The parser correctly interprets the XML tags that correspond to LLSD data
 * types (e.g., {@code <integer>}, {@code <string>}, {@code <map>}, {@code <array>})
 * and converts their contents into the appropriate Java types.
 *
 * @see LLSD
 * @see <a href="http://wiki.secondlife.com/wiki/LLSD#XML_Serialization">LLSD XML Specification</a>
 */
public class LLSDParser {
    private static final String ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Document builder used to parse the replies
     */
    private final DocumentBuilder documentBuilder;

    /**
     * Initializes a new instance of the {@code LLSDParser}.
     * <p>
     * This constructor sets up the underlying XML document builder.
     *
     * @throws ParserConfigurationException if a JAXP parser cannot be created.
     */
    public      LLSDParser()
        throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        this.documentBuilder = factory.newDocumentBuilder();
    }

    /**
     * Helper method to filter a {@link NodeList}, returning only nodes that are
     * actual elements.
     *
     * @param nodes The list of nodes to filter.
     * @return A list containing only the {@code ELEMENT_NODE} instances from the input.
     */
    private List<Node> extractElements(final NodeList nodes) {
        final List<Node> trimmedNodes = new ArrayList<>();
        
        for (int nodeIdx = 0; nodeIdx < nodes.getLength(); nodeIdx++) {
            final Node node = nodes.item(nodeIdx);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                trimmedNodes.add(node);
            }
        }
        
        return trimmedNodes;
    }

    /**
     * Parses an LLSD document from an XML input stream.
     * <p>
     * This is the main entry point for parsing an XML-based LLSD document. It
     * reads the stream, builds a DOM tree, and then traverses the tree to
     * construct the corresponding LLSD object structure.
     *
     * @param xmlFile The input stream containing the XML LLSD data.
     * @return An {@link LLSD} object representing the parsed data.
     * @throws IOException   if an I/O error occurs while reading the stream.
     * @throws LLSDException if the document is not valid LLSD (e.g., incorrect
     *                       structure or invalid data formats).
     * @throws SAXException  if a general XML parsing error occurs.
     */
    public LLSD parse(final InputStream xmlFile)
        throws IOException, LLSDException, SAXException {
        final Document document = this.documentBuilder.parse(xmlFile);
        final List<Node> childNodesTrimmed;
        final Node llsdNode = document.getDocumentElement();
        final Object llsdContents;

        if (null == llsdNode) {
            throw new LLSDException("Outer-most tag for LLSD missing.");
        }

        if (!llsdNode.getNodeName().equalsIgnoreCase("llsd")) {
            throw new LLSDException("Outer-most tag for LLSD is \""
                + llsdNode.getNodeName() + "\" instead of \"llsd\".");
        }

        childNodesTrimmed = extractElements(llsdNode.getChildNodes());
        if (childNodesTrimmed.size() == 0) {
            // XXX: Warn?
            return new LLSD(null);
        }

        if (childNodesTrimmed.size() > 1) {
            throw new LLSDException("Expected only one subelement for element <llsd>.");
        }

        llsdContents = parseNode(childNodesTrimmed.get(0));

        return new LLSD(llsdContents);
    }

    private List<Object> parseArray(final NodeList nodeList)
        throws LLSDException {
        final List<Object> value = new ArrayList<>();

        for (int nodeIdx = 0; nodeIdx < nodeList.getLength(); nodeIdx++) {
            final Node node = nodeList.item(nodeIdx);
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                value.add(parseNode(node));
            }
        }

        return value;
    }

    private Boolean parseBoolean(final String elementContents)
        throws LLSDException {
        if (elementContents.equals("1") ||
            elementContents.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    private Date parseDate(final String elementContents)
        throws LLSDException {
        final Date value;

        if (elementContents.length() == 0) {
            return new Date(0);
        }

        try {
            // Create a new DateFormat instance for thread safety
            DateFormat dateFormat = new SimpleDateFormat(ISO8601_PATTERN);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            value = dateFormat.parse(elementContents);
        } catch(java.text.ParseException e) {
            throw new LLSDException("Unable to parse LLSD date value, received \""
                + elementContents + "\".", e);
        }

        return value;
    }

    private Integer parseInteger(final String elementContents)
        throws LLSDException {
        final Integer value;

        if (elementContents.length() == 0) {
            return 0;
        }

        try {
            // Parse integer value using modern method
            value = Integer.valueOf(elementContents);
        } catch(NumberFormatException e) {
            throw new LLSDException("Unable to parse LLSD integer value, received \""
                + elementContents + "\".", e);
        }

        return value;
    }

    private Map<String, Object> parseMap(final NodeList nodeList)
        throws LLSDException {
        final List<Node> trimmedNodes = extractElements(nodeList);
        final Map<String, Object> valueMap = new HashMap<>();

        if ((trimmedNodes.size() % 2) != 0) {
            throw new LLSDException("Unable to parse LLSD map as it has odd number of nodes: "
                + nodeList.toString());
        }

        for (int nodeIdx = 0; nodeIdx < trimmedNodes.size(); nodeIdx = nodeIdx + 2) {
            final NodeList keyChildren;
            final Node keyNode = trimmedNodes.get(nodeIdx);
            final Node valueNode = trimmedNodes.get(nodeIdx + 1);
            String key = null;
            final Object value;

            keyChildren = keyNode.getChildNodes();
            for (int keyNodeIdx = 0; keyNodeIdx < keyChildren.getLength(); keyNodeIdx++) {
                final Node textNode = keyChildren.item(keyNodeIdx);
                switch (textNode.getNodeType()) {
                case Node.TEXT_NODE:
                    key = textNode.getNodeValue();
                    break;
                default:
                    throw new LLSDException("Unexpected node \""
                        + textNode.getNodeName() + "\" found while parsing key for map.");
                }
            }

            value = parseNode(valueNode);
            assert null != value;

            valueMap.put(key, value);
        }

        return valueMap;
    }

    /**
     * Recursively parses a single DOM node and converts it into an LLSD value.
     * <p>
     * This method is the core of the parser's logic. It examines the node's name
     * to determine the LLSD data type and then calls the appropriate helper
     * method to parse its content.
     *
     * @param node The DOM node to parse.
     * @return A Java object representing the parsed LLSD value.
     * @throws LLSDException if the node is of an unknown or invalid type.
     */
    private Object parseNode(final Node node)
        throws LLSDException {
        boolean isUndefined = false;
        final NodeList childNodes;
        final String nodeName = node.getNodeName().toLowerCase();
        final StringBuilder nodeText;

        childNodes = node.getChildNodes();

        // Handle compound types (array and map)
        if (nodeName.equals("array")) {
            return parseArray(childNodes);
        } else if (nodeName.equals("map")) {
            return parseMap(childNodes);
        }

        nodeText = new StringBuilder();
        for (int nodeIdx = 0; nodeIdx < childNodes.getLength(); nodeIdx++) {
            final Node childNode = childNodes.item(nodeIdx);
            if (childNode == null) {
                continue; // Skip null nodes
            }

            switch (childNode.getNodeType()) {
            case Node.TEXT_NODE:
                String nodeValue = childNode.getNodeValue();
                if (nodeValue != null) {
                    nodeText.append(nodeValue);
                }
                break;
            case Node.ELEMENT_NODE:
                String elementName = childNode.getNodeName();
                if (elementName != null && elementName.equals("undef")) {
                    isUndefined = true;
                }
                break;
            default:
                break;
            }
        }

        // Handle binary after nodeText is built
        if (nodeName.equals("binary")) {
            if (isUndefined) {
                return LLSDUndefined.BINARY;
            }
            String base64Content = nodeText.toString().trim();
            if (base64Content.isEmpty()) {
                return new byte[0]; // Empty binary data
            }
            try {
                return Base64.getDecoder().decode(base64Content);
            } catch (IllegalArgumentException e) {
                throw new LLSDException("Invalid base64 binary data: " + base64Content, e);
            }
        }

        if (nodeName.equals("undef")) {
            return "";
        } else if (nodeName.equals("boolean")) {
            return isUndefined
                ? LLSDUndefined.BOOLEAN
                : parseBoolean(nodeText.toString());
        } else if (nodeName.equals("date")) {
            return isUndefined
                ? LLSDUndefined.DATE
                : parseDate(nodeText.toString());
        } else if (nodeName.equals("integer")) {
            return isUndefined
                ? LLSDUndefined.INTEGER
                : parseInteger(nodeText.toString());
        } else if (nodeName.equals("real")) {
            return isUndefined
                ? LLSDUndefined.REAL
                : parseReal(nodeText.toString());
        } else if (nodeName.equals("string")) {
            return isUndefined
                ? LLSDUndefined.STRING
                : parseString(nodeText.toString());
        } else if (nodeName.equals("uri")) {
            return isUndefined
                ? LLSDUndefined.URI
                : parseURI(nodeText.toString());
        } else if (nodeName.equals("uuid")) {
            return isUndefined
                ? LLSDUndefined.UUID
                : parseUUID(nodeText.toString());
        }

        throw new LLSDException("Encountered unexpected node \""
            + node.getNodeName() + "\".");
    }

    private Double parseReal(final String elementContents)
        throws LLSDException {
        final Double value;

        if (elementContents.length() == 0) {
            return 0.0;
        }

        if (elementContents.equals("nan")) {
            return Double.NaN;
        }

        try {
            // Parse double value using modern method
            value = Double.valueOf(elementContents);
        } catch(NumberFormatException e) {
            throw new LLSDException("Unable to parse LLSD real value, received \""
                + elementContents + "\".", e);
        }

        return value;
    }

    private String parseString(final String elementContents)
        throws LLSDException {
        return elementContents;
    }

    private URI parseURI(final String elementContents)
        throws LLSDException {
        final URI value;

        try {
            value = new URI(elementContents);
        } catch(java.net.URISyntaxException e) {
            throw new LLSDException("Unable to parse LLSD URI value, received \""
                + elementContents + "\".", e);
        }

        return value;
    }

    private UUID parseUUID(final String elementContents)
        throws LLSDException {
        final UUID value;

        if (elementContents.length() == 0) {
            return new UUID(0L, 0L);
        }

        try {
            value = UUID.fromString(elementContents);
        } catch(IllegalArgumentException e) {
            throw new LLSDException("Unable to parse LLSD UUID value, received \""
                + elementContents + "\".", e);
        }

        return value;
    }
}
