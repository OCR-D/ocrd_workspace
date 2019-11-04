/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.kit.ocrd.workspace;

import edu.kit.ocrd.workspace.exception.WorkspaceException;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hartmann-v
 */
public class PageUtilTest {
  
  public PageUtilTest() {
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
   * Test Class
   */
  @Test
  public void testClass() throws Exception {
    System.out.println("testClass");
    PageUtil pageUtil = new PageUtil();
    assertNotNull(pageUtil);
  }

  /**
   * Test of validateImageUrl method, of class PageUtil.
   */
  @Test
  public void testValidateImageUrl() throws Exception {
    System.out.println("validateImageUrl");
    File pageFile = new File("src/test/resources/page/valid_page.xml");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    boolean expResult = true;
    boolean result = PageUtil.validateImageUrl(pageFile, metsFile);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateImageUrl method, of class PageUtil.
   */
  @Test
  public void testValidateImageUrlFileUrl() throws Exception {
    System.out.println("testValidateImageUrlFileUrl");
    File pageFile = new File("src/test/resources/workspace/OCR-D-GT-SEG-PAGE/OCR-D-GT-SEG-PAGE_0001_fileUrl.xml");
    File metsFile = new File("src/test/resources/workspace/page_fileUrl_mets.xml");
    boolean expResult = true;
    boolean result = PageUtil.validateImageUrl(pageFile, metsFile);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateImageUrl method, of class PageUtil.
   */
  @Test
  public void testValidateImageUrlAbsoluteUrl() throws Exception {
    System.out.println("testValidateImageUrlAbsoluteUrl");
    File pageFile = new File("src/test/resources/workspace/OCR-D-GT-SEG-PAGE/OCR-D-GT-SEG-PAGE_0001_absoluteUrl.xml");
    File metsFile = new File("src/test/resources/workspace/page_absoluteUrl_mets.xml");
    try {
      PageUtil.validateImageUrl(pageFile, metsFile);
      assertFalse(Boolean.TRUE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(PageUtil.WRONG_IMAGE_URL));
    }
  }

  /**
   * Test of validateImageUrl method, of class PageUtil.
   */
  @Test
  public void testValidateImageUrlWrongUrl() throws Exception {
    System.out.println("testValidateImageUrlWrongUrl");
    File pageFile = new File("src/test/resources/workspace/OCR-D-GT-SEG-PAGE/OCR-D-GT-SEG-PAGE_0001_wrongUrl.xml");
    File metsFile = new File("src/test/resources/workspace/page_wrongUrl_mets.xml");
    try {
      PageUtil.validateImageUrl(pageFile, metsFile);
      assertFalse(Boolean.TRUE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(PageUtil.WRONG_IMAGE_URL));
    }
  }

  /**
   * Test of validateImageUrl method, of class PageUtil.
   */
  @Test
  public void testValidateImageUrlMissingImageFilename() throws Exception {
    System.out.println("testValidateImageUrlMissingImageFilename");
    File pageFile = new File("src/test/resources/page/double_page_page.xml");
    File metsFile = new File("src/test/resources/workspace/page_wrongUrl_mets.xml");
    try {
      PageUtil.validateImageUrl(pageFile, metsFile);
      assertFalse(Boolean.TRUE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(PageUtil.WRONG_IMAGE_URL));
    }
  }

  /**
   * Test of validatePage method, of class PageUtil.
   */
  @Test
  public void testValidatePage() {
    System.out.println("validatePage");
    File pageFile = new File("src/test/resources/page/valid_page.xml");
    boolean expResult = true;
    boolean result = PageUtil.validatePage(pageFile);
    assertEquals(expResult, result);
  }

  /**
   * Test of validatePage method, of class PageUtil.
   */
  @Test
  public void testValidatePageDoublePage() {
    System.out.println("testValidatePageDoublePage");
    File pageFile = new File("src/test/resources/page/double_page_page.xml");
    try {
      PageUtil.validatePage(pageFile);
      assertFalse(Boolean.TRUE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(XmlUtil.ERROR_VALIDATING_XML));
    }
  }

  /**
   * Test of validatePage method, of class PageUtil.
   */
  @Test
  public void testValidatePageWrongNamespace() {
    System.out.println("testValidatePageWrongNamespace");
    File pageFile = new File("src/test/resources/page/wrong_namespace_page.xml");
    try {
      PageUtil.validatePage(pageFile);
      assertFalse(Boolean.TRUE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(XmlUtil.UNKNOWN_NAMESPACE));
    }
  }
}
