/*
Copyright 2018 Samsung SDS

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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
