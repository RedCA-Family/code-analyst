package com.samsungsds.analyst.code.util;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

public class XmlElementUtil {
    public static int getElementCount(String xmlFilePath, String elementTagName) {
        try {
            // Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // The Document interface represents the entire HTML or XML document. Conceptually, it is the root of the document tree, and provides the primary access to the document's data.
            Document doc = factory.newDocumentBuilder().parse(xmlFilePath);

            // Returns a NodeList of all the Elements in document order with a given tag name and are contained in the document.
            NodeList nodes = doc.getElementsByTagName(elementTagName);

            return nodes.getLength();

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
