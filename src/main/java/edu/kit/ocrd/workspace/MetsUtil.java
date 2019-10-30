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

import edu.kit.ocrd.workspace.entity.GroundTruthProperties;
import edu.kit.ocrd.workspace.exception.WorkspaceException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.tika.Tika;
import org.fzk.tools.xml.JaxenUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility handling METS document.
 */
public class MetsUtil {

  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(MetsUtil.class);
  /**
   * Namespaces used inside METS documents.
   */
  private static Namespace[] namespaces = {
    Namespace.getNamespace("mets", "http://www.loc.gov/METS/"),
    Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3"),
    Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink"),
    Namespace.getNamespace("gt", "http://www.ocr-d.de/GT/"),
    Namespace.getNamespace("page2017", "http://schema.primaresearch.org/PAGE/gts/pagecontent/2017-07-15")
  };
  /**
   * Title of document.
   */
  private static final String TITLE = "title";
  /**
   * Subtitle of document.
   */
  private static final String SUB_TITLE = "subtitle";
  /**
   * Author of document.
   */
  private static final String AUTHOR = "author";
  /**
   * License of document.
   */
  private static final String LICENSE = "license";
  /**
   * Language of document.
   */
  private static final String LANGUAGE = "language";
  /**
   * Year of document.
   */
  private static final String YEAR = "year";
  /**
   * Number of pages of document.
   */
  private static final String NUMBER_OF_IMAGES = "number_of_images";
  /**
   * Classifications of document.
   */
  private static final String CLASSIFICATION = "classification";
  /**
   * Genres of document.
   */
  private static final String GENRE = "genre";
  /**
   * Publisher of document.
   */
  private static final String PUBLISHER = "publisher";
  /**
   * Physical description of document.
   */
  private static final String PHYSICAL_DESCRIPTION = "physical_description";
  /**
   * Record identifier (PPN) of document.
   */
  private static final String PPN = "PPN";
  
  public static final String MISSING_UNIQUE_IDENTIFIER = "Missing unique identifier!";
  public static final String  MISSING_PHYSICAL_MAP = "Missing one or more than one physical map!";
  public static final String USE_FILE_GRP_NOT_UNIQUE = "USE of fileGrp is not unique: ";
  public static final String DIFFERENT_MIMETYPES = "Different mimetypes in filegrp with USE: ";
  public static final String WRONG_SEMANTIC_LABEL = "Wrong semantic label inside METS! - ";
  public static final String FILE_NOT_EXISTS = "File doesn't exist: ";
  public static final String WRONG_MIMETYPE = "Wrong mimetype for ID: ";
  public static final String MISSING_LANGUAGE = "Language field missing inside METS!";
  public static final String MISSING_GENRE = "Genre field missing inside METS!";
  public static final String   MISSING_CLASSIFICATION = "Classification field missing inside METS!";
  /**
   * Extract MetsFile instances from METS document. Tests: - unique name for USE
   * - same mimetype inside file grp but don't test - structure of USE (e.g.
   * OCR-D-GT-IMG-...) not a must
   *
   * @param metsDocument METS document.
   *
   * @return List with all found files.
   */
  public static boolean validateMetsFiles(Document metsDocument, Path pathToMets) {
    boolean valid = true;
    Set fileGrp = new HashSet();
    int noOfFileGrps = 0;
    String mimetypeOfGroup;
    StringBuffer message = new StringBuffer();
    String newLine = System.getProperties().getProperty("line.separator");
    Tika tika = new Tika();
    LOGGER.info("Validate files from METS document.");
    List nodes = JaxenUtil.getNodes(metsDocument, "//mets:fileGrp", namespaces);
    LOGGER.trace("Found {} fileGrp(s)", nodes.size());
    for (Object node : nodes) {
      Element fileGrpElement = (Element) node;
      String use = JaxenUtil.getAttributeValue(fileGrpElement, "./@USE");
      noOfFileGrps++;
      fileGrp.add(use);
      if (fileGrp.size() < noOfFileGrps) {
        message.append(USE_FILE_GRP_NOT_UNIQUE).append(use).append(newLine);
        valid = false;
      }
      List fileNodes = JaxenUtil.getNodes(fileGrpElement, "./mets:file", namespaces);
      LOGGER.trace("Found fileGrp with USE: {} containing {} file(s)", use, fileNodes.size());
      mimetypeOfGroup = null;
      for (Object node2 : fileNodes) {
        Element fileElement = (Element) node2;
        String id = JaxenUtil.getAttributeValue(fileElement, "./@ID");
        String pageId;
        try {
          pageId = JaxenUtil.getAttributeValue(metsDocument, "//mets:div[./mets:fptr/@FILEID='" + id + "']/@ID", namespaces);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
          // Try to find pageId using old style
          pageId = JaxenUtil.getAttributeValue(fileElement, "./@GROUPID");
        }
        String mimetype = JaxenUtil.getAttributeValue(fileElement, "./@MIMETYPE");
        if (mimetypeOfGroup != null) {
          if (!mimetype.equals(mimetypeOfGroup)) {
            message.append(DIFFERENT_MIMETYPES).append(use).append(newLine);
            valid = false;
          }
        } else {
          mimetypeOfGroup = mimetype;
        }
        String url = JaxenUtil.getAttributeValue(fileElement, "./mets:FLocat/@xlink:href", namespaces);
        LOGGER.trace("Found file with id: {}, pageId: {}, mimetype: {}, url: {}", id, pageId, mimetype, url);
        // validate entries
        Path metsFile;
        if (url.startsWith("http://")) {
          // ignore
        } else {
          metsFile = Paths.get(pathToMets.toString(), url);
          try {
            if (!metsFile.toFile().exists()) {
              message.append(FILE_NOT_EXISTS).append(url).append(newLine);
              valid = false;
              continue;
            }
            String tikaMimetype = tika.detect(metsFile);
            if (mimetype.equals("application/vnd.prima.page+xml")) {
              // Additional tests for page files
            } else if (!tikaMimetype.equals(mimetype)) {
              message.append(WRONG_MIMETYPE).append(id).append(newLine);
              valid = false;
            }
            if (tikaMimetype.startsWith("image")) {
              // TODO: Test for image resolution X/Y Resolution > 150
              // Not activated as most images have no valid values! 
//              try {
//                Parser parser = new AutoDetectParser();
//                BodyContentHandler handler = new BodyContentHandler();
//                Metadata metadata = new Metadata();
//                FileInputStream inputstream = new FileInputStream(metsFile.toFile());
//                ParseContext context = new ParseContext();
//
//                parser.parse(inputstream, handler, metadata, context);
//
//                if (Integer.parseInt(metadata.get("X Resolution")) < 150) {
//                  message.append("Image resolution to low for image ").append(url).append(newLine);
//                  valid = false;
//                }
//              } catch (SAXException ex) {
//                java.util.logging.Logger.getLogger(MetsUtil.class.getName()).log(Level.SEVERE, null, ex);
//              } catch (TikaException ex) {
//                java.util.logging.Logger.getLogger(MetsUtil.class.getName()).log(Level.SEVERE, null, ex);
//              }
            }
          } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new WorkspaceException(message.toString(), ex);
          }
        }
      }
    }
    if (!valid) {
      LOGGER.error(message.toString());
      throw new WorkspaceException(message.toString());
    }
    return valid;
  }

  /**
   * Extract all metadata from METS.
   *
   * @param metsDocument METS file.
   * @return MetsMetadata holding all metadata.
   *
   * @throws Exception An error occurred during parsing METS file.
   */
  public static boolean validateMetadataFromMets(final Document metsDocument) throws Exception {
    boolean valid = true;
    // define XPaths
    Map<String, String> metsMap = new HashMap<>();
    metsMap.put(TITLE, "/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/mods:mods/mods:titleInfo/mods:title[not(@type)]");
    metsMap.put(SUB_TITLE, "/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/mods:mods/mods:titleInfo/mods:subTitle[not(@type)]");
    metsMap.put(YEAR, "/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/mods:mods/mods:originInfo/mods:dateIssued");
    metsMap.put(LICENSE, "//mets:rightsMD/descendant::*");
    metsMap.put(AUTHOR, "//mods:name/mods:role/mods:roleTerm[text()='aut']/../../mods:displayForm");
    metsMap.put(NUMBER_OF_IMAGES, "/mets:mets/mets:fileSec/mets:fileGrp[@USE='OCR-D-IMG']/mets:file");
    metsMap.put(PUBLISHER, "//mods:publisher[not(@keydate = 'yes')]");
    metsMap.put(PHYSICAL_DESCRIPTION, "//mods:physicalDescription/mods:extent");
    metsMap.put(PPN, "//mods:mods/mods:recordInfo/mods:recordIdentifier[1]");
    Element root = metsDocument.getRootElement();
    String[] values = JaxenUtil.getValues(root, metsMap.get(TITLE), namespaces);
    if (values.length >= 1) {
      // validate title
    }
    values = JaxenUtil.getValues(root, metsMap.get(SUB_TITLE), namespaces);
    if (values.length >= 1) {
      // validate subTitle
    }
    values = JaxenUtil.getValues(root, metsMap.get(YEAR), namespaces);
    if (values.length >= 1) {
      // validate Year
    }
    values = JaxenUtil.getValues(root, metsMap.get(LICENSE), namespaces);
    if (values.length >= 1) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < values.length; i++) {
        if (values[i].trim().length() > 0) {
          if (builder.length() > 0) {
            builder.append(", ");
          }
          builder.append(values[i]);
        }
      }
      // validate License
    }
    values = JaxenUtil.getValues(root, metsMap.get(AUTHOR), namespaces);
    if (values.length >= 1) {
      // validate Author
    }
    values = JaxenUtil.getValues(root, metsMap.get(NUMBER_OF_IMAGES), namespaces);
    if (values.length >= 1) {
      // validate NoOfPages
    }

    values = JaxenUtil.getValues(root, metsMap.get(PUBLISHER), namespaces);
    if (values.length >= 1) {
      // validate Publisher
    }
    values = JaxenUtil.getValues(root, metsMap.get(PHYSICAL_DESCRIPTION), namespaces);
    if (values.length >= 1) {
      // validate PhysicalDescription 
    }
    values = JaxenUtil.getValues(root, metsMap.get(PPN), namespaces);
    if (values.length >= 1) {
      // validate Ppn
    }
    return valid;
  }

  /**
   * Extract all language metadata from METS.
   *
   * @param metsDocument METS file.
   * @return List of LanguageMetadata holding all language metadata.
   *
   * @throws Exception An error occurred during parsing METS file.
   */
  public static boolean validLanguageMetadataFromMets(final Document metsDocument) throws Exception {
    boolean valid = false;
    Map<String, String> metsMap = new HashMap<>();
    // define XPaths
    metsMap.put(LANGUAGE, "//mods:languageTerm");
    Element root = metsDocument.getRootElement();
    String[] values = JaxenUtil.getValues(root, metsMap.get(LANGUAGE), namespaces);
    if (values.length >= 1) {
      for (String language : values) {
        if (language.trim().length() > 1) {
          valid = true; // validate Language
        }
      }
    }
    if (!valid) {
      String message = MISSING_LANGUAGE;
      LOGGER.warn(message);
      //throw new WorkspaceException(message);
      //Not mandatory
      valid = true;
    }
    return valid;
  }

  /**
   * Extract all classification metadata from METS.
   *
   * @param metsDocument METS file.
   * @return List of ClassificationMetadata holding all classification metadata.
   *
   * @throws Exception An error occurred during parsing METS file.
   */
  public static boolean validateClassificationMetadataFromMets(final Document metsDocument) throws Exception {
    boolean valid = false;
    Map<String, String> metsMap = new HashMap<>();
    // define XPaths
    metsMap.put(CLASSIFICATION, "//mods:classification");
    Element root = metsDocument.getRootElement();
    String[] values = JaxenUtil.getValues(root, metsMap.get(CLASSIFICATION), namespaces);
    if ((values != null) && (values.length >= 1)) {
      for (String classification : values) {
        if (classification.trim().length() > 1) {
          valid = true; // validate Classification
        }
      }
    }
    if (!valid) {
      String message = MISSING_CLASSIFICATION;
      LOGGER.warn(message);
      //throw new WorkspaceException(message);
      //Not mandatory
      valid = true;
    }
    return valid;
  }

  /**
   * Extract all genre metadata from METS.
   *
   * @param metsDocument METS file.
   * @return List of ClassificationMetadata holding all genre metadata.
   *
   * @throws Exception An error occurred during parsing METS file.
   */
  public static boolean validateGenreMetadataFromMets(final Document metsDocument) throws Exception {
    boolean valid = false;
    Map<String, String> metsMap = new HashMap<>();
    // define XPaths
    metsMap.put(GENRE, "//mods:genre");
    Element root = metsDocument.getRootElement();
    String[] values = JaxenUtil.getValues(root, metsMap.get(GENRE), namespaces);
    if (values.length >= 1) {
      for (String genre : values) {
        if (genre.trim().length() > 1) {
          valid = true;   // validate Genre
        }
      }
    }
    if (!valid) {
      String message = MISSING_GENRE;
      LOGGER.warn(message);
      //throw new WorkspaceException(message);
      valid = true;
    }
    return valid;
  }

  /**
   * Extract all ground truth metadata from METS.
   *
   * @param metsDocument METS file.
   * @return List of PageMetadata holding all ground truth metadata.
   *
   * @throws Exception An error occurred during parsing METS file.
   */
  public static boolean validateFeaturesFromMets(final Document metsDocument) throws Exception {
    boolean valid = true;
    String invalidSemanticLabel = null;
    Element root = metsDocument.getRootElement();
    List physicalList = JaxenUtil.getNodes(root, "//mets:structMap[@TYPE='PHYSICAL']", namespaces);
    if (!physicalList.isEmpty()) {
      Element structMap = (Element) physicalList.get(0);
      List pageList = JaxenUtil.getNodes(structMap, "//mets:div[@TYPE='page']", namespaces);
      if (!pageList.isEmpty()) {
        for (Object pageObject : pageList) {
          // Determine order, id and dmdid. 
          Element pageNode = (Element) pageObject;
          String order = XmlUtil.getAttribute(pageNode, "ORDER");
          String id = XmlUtil.getAttribute(pageNode, "ID");
          String dmdId = XmlUtil.getAttribute(pageNode, "DMDID");
          String[] features = JaxenUtil.getValues(root, "//mets:dmdSec[@ID='" + dmdId + "']/mets:mdWrap[@OTHERMDTYPE='GT']/mets:xmlData/gt:gt/gt:state/@prop", namespaces);
          for (String feature : features) {
            // validate PageMetadata
            if (GroundTruthProperties.get(feature) == null) {
              invalidSemanticLabel = feature;
              valid = false;
            }
          }
        }
      }
    }
    if (!valid) {
      String message = WRONG_SEMANTIC_LABEL + invalidSemanticLabel;
      LOGGER.error(message);
      throw new WorkspaceException(message);
    }
    return valid;
  }

  /**
   * /**
   * Get all namespaces used inside METS document. (Do not contain namespaces
   * used only inside special section documents.)
   *
   * @return Namespaces used inside METS document.
   */
  public static Namespace[] getNamespaces() {
    return namespaces;
  }

  /**
   * At least one unique identifier has to be present.
   *
   * @param metsDocument Document of Mets file
   * @return valid or Exception if not.
   */
  public static boolean validateUniqueIdentifier(Document metsDocument) throws Exception {
    boolean valid = false;
    String[] values = JaxenUtil.getValues(metsDocument, "//mods:identifier[@type='purl' or @type='url' or @type='urn' or @type='handle' or @type='dtaid']", namespaces);
    if ((values != null) && (values.length > 0)) {
      valid = true;
    }
    if (!valid) {
      String message = MISSING_UNIQUE_IDENTIFIER;
      LOGGER.error(message);
      throw new WorkspaceException(message);
    }
    return valid;
  }

  /**
   * Exactly one physical map has to be present.
   *
   * @param metsDocument Document of Mets file
   * @return valid or Exception if not.
   */
  public static boolean validatePhysicalMap(Document metsDocument) throws Exception {
    boolean valid = false;
    String[] values = JaxenUtil.getValues(metsDocument, "//mets:div[@TYPE='physSequence']", namespaces);
    if ((values != null) && (values.length == 1)) {
      valid = true;
    }
    if (!valid) {
      String message = MISSING_PHYSICAL_MAP;
      LOGGER.error(message);
      throw new WorkspaceException(message);
    }
    return valid;
  }

  /**
   * Validate mets file against mets.xsd
   * (http://www.loc.gov/standards/mets/mets.xsd)
   *
   * @param metsFile Mets file
   * @return valid or Exception if not.
   */
  public static boolean validateMets(File metsFile) {
    boolean valid = false;
    valid = XmlUtil.validateXml(metsFile);
    return valid;
  }

  /**
   * Validate mets file against mets.xsd
   * (http://www.loc.gov/standards/mets/mets.xsd)
   *
   * @param metsFile Mets file
   * @return valid or Exception if not.
   */
  public static boolean validateCompleteMets(File metsFile) throws Exception {
    boolean valid = false;
    valid = XmlUtil.validateXml(metsFile);
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    
    valid |= MetsUtil.validateFeaturesFromMets(metsDocument);
    valid |= MetsUtil.validatePhysicalMap(metsDocument);
    valid |= MetsUtil.validateMetsFiles(metsDocument, metsFile.toPath().getParent());
    
    return valid;
  }
}
