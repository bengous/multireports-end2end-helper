package com.bengous.e2e.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.StringWriter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Prettifier {
    // https://www.baeldung.com/java-pretty-print-xml#pretty-printing-xml-with-the-dom4j-library
    public static String prettyPrintXml(String xmlString) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setIndentSize(4);
            format.setSuppressDeclaration(true);
            format.setEncoding("UTF-8");

            Document document = DocumentHelper.parseText(xmlString);
            StringWriter sw = new StringWriter();
            XMLWriter writer = new XMLWriter(sw, format);
            writer.write(document);
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlString, e);
        }
    }
}
