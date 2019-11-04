/*
 * Copyright 2018 Karlsruhe Institute of Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.kit.ocrd.workspace;

import edu.kit.ocrd.workspace.exception.WorkspaceException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.fzk.tools.xml.JaxenUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Utility handling METS document.
 */
public class XmlUtil {

  public static final String ERROR_VALIDATING_XML = "Error validating XML";
  public static final String UNKNOWN_NAMESPACE = "Unknown namespace for xml document: ";
  public static final String ERROR_PARSING_NAMESPACE = "Error parsing target namespace!";
  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtil.class);
  /**
   * Namespaces used inside METS documents.
   */
  public static Namespace[] xsdNamespace = {
    Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema")
  };
  /**
   * Namespaces used inside METS documents.
   */
  public static Namespace[] metsNamespaces = {
    Namespace.getNamespace("mets", "http://www.loc.gov/METS/"),
    Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3"),
    Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink"),
    Namespace.getNamespace("gt", "http://www.ocr-d.de/GT/")
  };
  /**
   * Namespaces used inside METS documents.
   */
  public static Namespace[] pageNamespaces = {
    Namespace.getNamespace("page", "http://schema.primaresearch.org/PAGE/gts/pagecontent/2017-07-15"),
    Namespace.getNamespace("page", "http://schema.primaresearch.org/PAGE/gts/pagecontent/2018-07-15"),
    Namespace.getNamespace("page", "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
  };
  /**
   * Set holding all available XSD files indexed by target namespace.
   */
  private static Map<String, Schema> xsdMap;
  /**
   * List of all registered xsd files.
   */
  private static final String[] xsdResources = {"xsd/mets.xsd", "xsd/page_2017.xsd", "xsd/page_2018.xsd", "xsd/page_2019.xsd"};

  static {
    // Initialize Map
    xsdMap = new HashMap();
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    for (String xsd : xsdResources) {
      InputStream resourceAsStream = XmlUtil.class.getClassLoader().getResourceAsStream(xsd);
      BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
      StringBuilder result = new StringBuilder();
      try {
        for (String line; (line = reader.readLine()) != null;) {
          result.append(line);
        }
        Document document = JaxenUtil.getDocument(result.toString());
        String targetNamespace = getTargetNamespace(document);
        System.out.println(targetNamespace);
        resourceAsStream = XmlUtil.class.getClassLoader().getResourceAsStream(xsd);
        Schema schema = factory.newSchema(new StreamSource(resourceAsStream));
        xsdMap.put(targetNamespace, schema);
      } catch (Exception ex) {
        // do nothing
      }
    }
  }

  /**
   * Get target namespace of xsd file.
   *
   * @param document xsd document
   * @return target namespace.
   */
  public static String getTargetNamespace(final Document document) {
    String attribute = null;
    try {
      attribute = JaxenUtil.getAttributeValue(document, "/xsd:schema/@targetNamespace", xsdNamespace);
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      String message = ERROR_PARSING_NAMESPACE;
      LOGGER.error(message, aioobe);
      throw new WorkspaceException(message, aioobe);
    }
    return attribute;
  }

  /**
   * Get namespace of xml file.
   *
   * @param document xml document
   * @return namespace.
   */
  public static String getNamespace(final Document document) {
    return document.getRootElement().getNamespace().getURI();
  }

  /**
   * Get value of attribute of given element.
   *
   * @param element Element
   * @param attribute Label of attribute.
   * @return Value or unknown if attribute is not set.
   */
  public static String getAttribute(final Element element, final String attribute) {
    String attributeValue = null;
    if (element.getAttribute(attribute) != null) {
      attributeValue = element.getAttribute(attribute).getValue();
    }
    return attributeValue;
  }

  /**
   * Validate xml file against one of registered xsd files.
   *
   * @param xmlFile xml file.
   * @return true or exception
   */
  public static boolean validateXml(final File xmlFile) {
    boolean valid = false;
    Schema schema = null;
    String namespace = null;
    Document document = getDocument(xmlFile);
    namespace = getNamespace(document);
    schema = xsdMap.get(namespace);
    if (schema != null) {
      try {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xmlFile));
        valid = true;
      } catch (IOException | SAXException ex) {
        String message = ERROR_VALIDATING_XML;
        LOGGER.error(message, ex);
        throw new WorkspaceException(message, ex);
      }
    } else {
      String message = UNKNOWN_NAMESPACE + namespace;
      LOGGER.error(message);
      throw new WorkspaceException(message);
    }
    return valid;
  }

  /**
   * Get instance of documnent for given file.
   *
   * @param xmlFile xml file.
   * @return Document or exception
   */
  public static Document getDocument(final File xmlFile) {
    Document document = null;
    try {
      document = JaxenUtil.getDocument(xmlFile);
    } catch (Exception ex) {
      String message = ERROR_VALIDATING_XML;
      LOGGER.error(message, ex);
      throw new WorkspaceException(message, ex);
    }
    return document;
  }
}
