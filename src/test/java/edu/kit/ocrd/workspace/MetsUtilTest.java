/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.kit.ocrd.workspace;

import edu.kit.ocrd.workspace.exception.WorkspaceException;
import java.io.File;
import java.nio.file.Path;
import org.fzk.tools.xml.JaxenUtil;
import org.jdom.Document;
import org.jdom.Namespace;
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
public class MetsUtilTest {

  public MetsUtilTest() {
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
    MetsUtil metsUtil = new MetsUtil();
    assertNotNull(metsUtil);
  }
  /**
   * Test of validateMets method, of class WorkspaceUtil.
   */
  @Test
  public void testValidateMets() {
    System.out.println("validateMets");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    boolean expResult = true;
    boolean result = MetsUtil.validateMets(metsFile);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateMets method, of class WorkspaceUtil.
   */
  @Test
  public void testValidateMetsWithInvalidSchema() {
    System.out.println("testValidateMetsWithInvalidSchema");
    File metsFile = new File("src/test/resources/workspace/invalid_schema_mets.xml");
    try {
      MetsUtil.validateMets(metsFile);
      assertFalse(Boolean.TRUE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(XmlUtil.ERROR_VALIDATING_XML));
    }
  }

  /**
   * Test of validateMetsFiles method, of class MetsUtil.
   */
  @Test
  public void testValidateMetsFiles() throws Exception {
    System.out.println("validateMetsFiles");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    Path pathToMets = metsFile.toPath().getParent();
    boolean expResult = true;
    boolean result = MetsUtil.validateMetsFiles(metsDocument, pathToMets);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateMetsFiles method, of class MetsUtil.
   */
  @Test
  public void testValidateMetsFilesMissingFile() throws Exception {
    System.out.println("testValidateMetsFilesMissingFile");
    File metsFile = new File("src/test/resources/workspace/missing_file_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    Path pathToMets = metsFile.toPath().getParent();
    try {
      boolean result = MetsUtil.validateMetsFiles(metsDocument, pathToMets);
      assertTrue(Boolean.FALSE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(MetsUtil.FILE_NOT_EXISTS));
    }
  }

  /**
   * Test of validateMetsFiles method, of class MetsUtil.
   */
  @Test
  public void testValidateMetsFilesDoubleUse() throws Exception {
    System.out.println("testValidateMetsFilesDoubleUse");
    File metsFile = new File("src/test/resources/workspace/double_use_fileGrp_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    Path pathToMets = metsFile.toPath().getParent();
    try {
      boolean result = MetsUtil.validateMetsFiles(metsDocument, pathToMets);
      assertTrue(Boolean.FALSE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(MetsUtil.USE_FILE_GRP_NOT_UNIQUE));
    }
  }

  /**
   * Test of validateMetsFiles method, of class MetsUtil.
   */
  @Test
  public void testValidateMetsFilesDifferentMimetypes() throws Exception {
    System.out.println("testValidateMetsFilesDifferentMimetypes");
    File metsFile = new File("src/test/resources/workspace/different_mimetype_inside_fileGrp_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    Path pathToMets = metsFile.toPath().getParent();
    try {
      boolean result = MetsUtil.validateMetsFiles(metsDocument, pathToMets);
      assertTrue(Boolean.FALSE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(MetsUtil.DIFFERENT_MIMETYPES));
    }
  }

  /**
   * Test of validateMetsFiles method, of class MetsUtil.
   */
  @Test
  public void testValidateMetsFilesWrongMimetype() throws Exception {
    System.out.println("testValidateMetsFilesWrongMimetype");
    File metsFile = new File("src/test/resources/workspace/wrong_mimetype_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    Path pathToMets = metsFile.toPath().getParent();
    try {
      boolean result = MetsUtil.validateMetsFiles(metsDocument, pathToMets);
      assertTrue(Boolean.FALSE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(MetsUtil.DIFFERENT_MIMETYPES));
    }
  }

  /**
   * Test of validateMetadataFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateMetadataFromMets() throws Exception {
    System.out.println("validateMetadataFromMets");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateMetadataFromMets(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateMetadataFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateMetadataFromMetsNoMetadata() throws Exception {
    System.out.println("testValidateMetadataFromMetsNoMetadata");
    File metsFile = new File("src/test/resources/workspace/no_metadata_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateMetadataFromMets(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateIdentifierFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateIdentifierFromMets() throws Exception {
    System.out.println("validateIdentifierFromMets");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateUniqueIdentifier(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateIdentifierFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateIdentifierFromInvalidMets() throws Exception {
    System.out.println("testValidateIdentifierFromInvalidMets");
    File metsFile = new File("src/test/resources/workspace/invalid_identifier_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    try {
      MetsUtil.validateUniqueIdentifier(metsDocument);
      assertTrue(Boolean.FALSE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(MetsUtil.MISSING_UNIQUE_IDENTIFIER));
    }
  }

  /**
   * Test of validateIdentifierFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateIdentifierFromMetsWithoutIdentifier() throws Exception {
    System.out.println("testValidateIdentifierFromMetsWithoutIdentifier");
    File metsFile = new File("src/test/resources/workspace/missing_identifier_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    try {
      MetsUtil.validateUniqueIdentifier(metsDocument);
      assertTrue(Boolean.FALSE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(MetsUtil.MISSING_UNIQUE_IDENTIFIER));
    }
  }

  /**
   * Test of validLanguageMetadataFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidLanguageMetadataFromMets() throws Exception {
    System.out.println("validLanguageMetadataFromMets");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateLanguageMetadataFromMets(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validLanguageMetadataFromMets method, of class MetsUtil.
   */
  @Test
  public void testInvalidLanguageMetadataFromMets() throws Exception {
    System.out.println("testInvalidLanguageMetadataFromMets");
    File metsFile = new File("src/test/resources/workspace/missing_language_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateLanguageMetadataFromMets(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validLanguageMetadataFromMets method, of class MetsUtil.
   */
  @Test
  public void testEmptyLanguageMetadataFromMets() throws Exception {
    System.out.println("testEmptyLanguageMetadataFromMets");
    File metsFile = new File("src/test/resources/workspace/empty_language_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateLanguageMetadataFromMets(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateClassificationMetadataFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateClassificationMetadataFromMets() throws Exception {
    System.out.println("validateClassificationMetadataFromMets");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateClassificationMetadataFromMets(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateClassificationMetadataFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateClassificationMetadataFromMetsWithoutClassification() throws Exception {
    System.out.println("testValidateClassificationMetadataFromMetsWithoutClassification");
    File metsFile = new File("src/test/resources/workspace/without_classification_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateClassificationMetadataFromMets(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateGenreMetadataFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateGenreMetadataFromMets() throws Exception {
    System.out.println("validateGenreMetadataFromMets");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateGenreMetadataFromMets(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateGenreMetadataFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateGenreMetadataFromMetsWithoutGenre() throws Exception {
    System.out.println("testValidateGenreMetadataFromMetsWithoutGenre");
    File metsFile = new File("src/test/resources/workspace/without_genre_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateGenreMetadataFromMets(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateGenreMetadataFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateGenreMetadataFromMetsWithEmptyGenre() throws Exception {
    System.out.println("testValidateGenreMetadataFromMetsWithEmptyGenre");
    File metsFile = new File("src/test/resources/workspace/empty_genre_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateGenreMetadataFromMets(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateFeaturesFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateFeaturesFromMets() throws Exception {
    System.out.println("validateFeaturesFromMets");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateFeaturesFromMets(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateFeaturesFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateFeaturesFromMetsWithInvalidFeaturename() throws Exception {
    System.out.println("testValidateFeaturesFromMetsWithInvalidFeaturename");
    File metsFile = new File("src/test/resources/workspace/wrong_semantic_label_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    try {
      MetsUtil.validateFeaturesFromMets(metsDocument);
      assertTrue(Boolean.FALSE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(MetsUtil.WRONG_SEMANTIC_LABEL));
    }
  }

  /**
   * Test of validateFeaturesFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateFeaturesFromMetsWithMissingPhysicalMap() throws Exception {
    System.out.println("testValidateFeaturesFromMetsWithMissingPhysicalMap");
    File metsFile = new File("src/test/resources/workspace/missing_physical_map_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateFeaturesFromMets(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateFeaturesFromMets method, of class MetsUtil.
   */
  @Test
  public void testValidateFeaturesFromMetsWithMissingPageNodes() throws Exception {
    System.out.println("testValidateFeaturesFromMetsWithMissingPageNodes");
    File metsFile = new File("src/test/resources/workspace/missing_page_nodes_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateFeaturesFromMets(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of getNamespaces method, of class MetsUtil.
   */
  @Test
  public void testGetNamespaces() {
    System.out.println("getNamespaces");
    int expectedSize = 5;
    Namespace[] result = MetsUtil.getNamespaces();
    assertEquals(expectedSize, result.length);
  }

  /**
   * Test of validateUniqueIdentifier method, of class MetsUtil.
   */
  @Test
  public void testValidateUniqueIdentifier() throws Exception {
    System.out.println("validateUniqueIdentifier");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validateUniqueIdentifier(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validateUniqueIdentifier method, of class MetsUtil.
   */
  @Test
  public void testValidateUniqueIdentifierWithMissingIdentifier() throws Exception {
    System.out.println("testValidateUniqueIdentifierWithMissingIdentifier");
    File metsFile = new File("src/test/resources/workspace/missing_identifier_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    try {
      MetsUtil.validateUniqueIdentifier(metsDocument);
      assertTrue(Boolean.FALSE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(MetsUtil.MISSING_UNIQUE_IDENTIFIER));
    }
  }

  /**
   * Test of validatePhysicalMap method, of class MetsUtil.
   */
  @Test
  public void testValidatePhysicalMap() throws Exception {
    System.out.println("testValidatePhysicalMap");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    boolean expResult = true;
    boolean result = MetsUtil.validatePhysicalMap(metsDocument);
    assertEquals(expResult, result);
  }

  /**
   * Test of validatePhysicalMap method, of class MetsUtil.
   */
  @Test
  public void testValidatePhysicalMapWithMissingMap() throws Exception {
    System.out.println("testValidatePhysicalMapWithMissingMap");
    File metsFile = new File("src/test/resources/workspace/missing_physical_map_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    try {
      MetsUtil.validatePhysicalMap(metsDocument);
      assertTrue(Boolean.FALSE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(MetsUtil.MISSING_PHYSICAL_MAP));
    }
  }

  /**
   * Test of validateUniqueIdentifier method, of class MetsUtil.
   */
  @Test
  public void testValidatePhysicalMapWithDoubleMap() throws Exception {
    System.out.println("testValidatePhysicalMapWithDoubleMap");
    File metsFile = new File("src/test/resources/workspace/two_physical_map_mets.xml");
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    try {
      MetsUtil.validatePhysicalMap(metsDocument);
      assertTrue(Boolean.FALSE);
    } catch (WorkspaceException wse) {
      assertTrue(wse.getMessage().startsWith(MetsUtil.MISSING_PHYSICAL_MAP));
    }
  }

  /**
   * Test of validateUniqueIdentifier method, of class MetsUtil.
   */
  @Test
  public void testValidateCompleteMets() throws Exception {
    System.out.println("testValidatePhysicalMapWithDoubleMap");
    File metsFile = new File("src/test/resources/workspace/valid_mets.xml");
    boolean expResult = true;
    boolean result =   MetsUtil.validateCompleteMets(metsFile);
    assertEquals(expResult, result);
  }

}
