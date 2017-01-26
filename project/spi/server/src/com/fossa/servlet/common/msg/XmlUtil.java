/* $Header: /home/common/cvsarea/ibase/dia/src/common/msg/XmlUtil.java,v 1.3 2004/03/27 20:27:01 weaston Exp $ */
package com.fossa.servlet.common.msg;

import org.w3c.dom.Node;

/**
 * Miscellaneous utilities for XML manipulation
 */  
public class XmlUtil {

    /**
     * Given a node whose content consists of Text elements,
     * obtain the text represented by them.
     */
    public static String getTextFromNode(Node nd) {
        StringBuffer result = new StringBuffer();
        Node textNode = nd.getFirstChild();
        while (textNode != null) {
            int nodeType = textNode.getNodeType();
            if (nodeType == Node.TEXT_NODE) {
                result.append(textNode.getNodeValue());
            } else {
                assert nodeType == Node.COMMENT_NODE;
            }
            textNode = textNode.getNextSibling();
        }              
        return result.toString();
    }
}
