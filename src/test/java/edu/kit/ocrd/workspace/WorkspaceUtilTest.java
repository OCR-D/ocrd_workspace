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
public class WorkspaceUtilTest {
  
  public WorkspaceUtilTest() {
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
   * Test Class.
   */
  @Test
  public void testClass() {
    System.out.println("testClass");
    WorkspaceUtil workspaceUtil = new WorkspaceUtil();
    assertNotNull(workspaceUtil);
  }

  /**
   * Test of validateWorkspace method, of class WorkspaceUtil.
   */
  @Test
  public void testValidateWorkspace() {
    System.out.println("validateWorkspace");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    boolean expResult = true;
    boolean result = WorkspaceUtil.validateWorkspace(metsFile);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateWorkspace method, of class WorkspaceUtil.
   */
  @Test
  public void testValidateWorkspaceMetsNotExists() {
    System.out.println("testValidateWorkspaceMetsNotExists");
    File metsFile = new File("src/test/resources/workspace/notExists_mets.xml");
    try {
      WorkspaceUtil.validateWorkspace(metsFile);
      assertTrue(Boolean.FALSE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(WorkspaceUtil.NO_METS_FILE));
    }
  }

  /**
   * Test of validateIdentifierFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateWorkspaceFromInvalidMets() throws Exception {
    System.out.println("testValidateWorkspaceFromInvalidMets");
    File metsFile = new File("src/test/resources/workspace/invalid_identifier_mets.xml");
    try {
      WorkspaceUtil.validateWorkspace(metsFile);
      assertTrue(Boolean.FALSE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(MetsUtil.MISSING_UNIQUE_IDENTIFIER));
    }
  }

  /**
   * Test of validateIdentifierFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateWorkspaceFromInvalidXml() throws Exception {
    System.out.println("testValidateWorkspaceFromInvalidXml");
    File metsFile = new File("src/test/resources/workspace/invalid_xml_mets.xml");
    try {
      WorkspaceUtil.validateWorkspace(metsFile);
      assertTrue(Boolean.FALSE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(XmlUtil.ERROR_VALIDATING_XML));
    }
  }
  
}
