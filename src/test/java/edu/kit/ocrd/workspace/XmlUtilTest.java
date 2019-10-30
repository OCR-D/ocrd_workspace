/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.kit.ocrd.workspace;

import edu.kit.ocrd.workspace.exception.WorkspaceException;
import java.io.File;
import org.fzk.tools.xml.JaxenUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test XmlUtil class.
 */
public class XmlUtilTest {

  public XmlUtilTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  /**
   * Test of getTargetNamespace method, of class XMLUtil.
   */
  @Test
  public void testClass() throws Exception {
    System.out.println("testClass");
    XmlUtil xmlUtil = new XmlUtil();
    assertNotNull(xmlUtil);
  }

  /**
   * Test of getTargetNamespace method, of class XMLUtil.
   */
  @Test
  public void testGetTargetNamespace() throws Exception {
    System.out.println("getTargetNamespace");
    File xsdFile = new File("src/test/resources/xsd/mets.xsd");
    Document document = JaxenUtil.getDocument(xsdFile);
    String expResult = "http://www.loc.gov/METS/";
    String result = XmlUtil.getTargetNamespace(document);
    assertEquals(expResult, result);
  }

  /**
   * Test of getTargetNamespace method, of class XMLUtil.
   */
  @Test
  public void testGetTargetNamespaceInvalidSchema() throws Exception {
    System.out.println("getTargetNamespace");
    File xsdFile = new File("src/test/resources/workspace/mets.xml");
    Document document = JaxenUtil.getDocument(xsdFile);
    try {
      XmlUtil.getTargetNamespace(document);
      assertFalse(Boolean.TRUE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(XmlUtil.ERROR_PARSING_NAMESPACE));
    }
  }

  /**
   * Test of getNamespace method, of class XMLUtil.
   */
  @Test
  public void testGetNamespace() throws Exception {
    System.out.println("getNamespace");
    File xmlFile = new File("src/test/resources/workspace/mets.xml");
    Document document = JaxenUtil.getDocument(xmlFile);
    String expResult = "http://www.loc.gov/METS/";
    String result = XmlUtil.getNamespace(document);
    assertEquals(expResult, result);
    xmlFile = new File("src/test/resources/workspace/invalid_namespace.xml");
    document = JaxenUtil.getDocument(xmlFile);
    expResult = "http://www.loc.gov/METS/invalid";
    result = XmlUtil.getNamespace(document);
    assertEquals(expResult, result);
  }

  /**
   * Test of getAttribute method, of class XMLUtil.
   */
  @Test
  public void testGetAttribute() throws Exception {
    System.out.println("getAttribute");
    File xmlFile = new File("src/test/resources/workspace/mets.xml");
    Document document = JaxenUtil.getDocument(xmlFile);
    Element element = (Element)JaxenUtil.getNodes(document, "//mets:file", XmlUtil.metsNamespaces).get(0);
    String attribute = "MIMETYPE";
    String expResult = "image/jpeg";
    String result = XmlUtil.getAttribute(element, attribute);
    assertEquals(expResult, result);
     attribute = "ID";
     expResult = "DEFAULT_0001";
    result = XmlUtil.getAttribute(element, attribute);
    assertEquals(expResult, result);
     attribute = "LOCTYPE";
     expResult = null;
    result = XmlUtil.getAttribute(element, attribute);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateXml method, of class XMLUtil.
   */
  @Test
  public void testValidateXmlWithInvalidSchema() {
    System.out.println("testValidateMetsWithInvalidSchema");
    File metsFile = new File("src/test/resources/workspace/invalid_schema_mets.xml");
    try {
      XmlUtil.validateXml(metsFile);
      assertFalse(Boolean.TRUE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(XmlUtil.ERROR_VALIDATING_XML));
    }
  }

  /**
   * Test of validateXml method, of class XMLUtil.
   */
  @Test
  public void testValidateXmlWithInvalidNamespace() {
    System.out.println("testValidateXmlWithInvalidNamespace");
    File metsFile = new File("src/test/resources/workspace/invalid_namespace.xml");
    try {
      XmlUtil.validateXml(metsFile);
      assertFalse(Boolean.TRUE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(XmlUtil.UNKNOWN_NAMESPACE));
    }
  }

  /**
   * Test of validateXml method, of class XMLUtil.
   */
  @Test
  public void testValidateXmlWithInvalidXml() {
    System.out.println("testValidateXmlWithInvalidXml");
    File metsFile = new File("src/test/resources/workspace/invalid_xml_mets.xml");
    try {
      XmlUtil.validateXml(metsFile);
      assertFalse(Boolean.TRUE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(XmlUtil.ERROR_VALIDATING_XML));
    }
  }

  /**
   * Test of validateXml method, of class XMLUtil.
   */
  @Test
  public void testValidateXml() {
    System.out.println("validateXml");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    boolean expResult = true;
    boolean result = XmlUtil.validateXml(metsFile);
    assertEquals(expResult, result);
  }

}
