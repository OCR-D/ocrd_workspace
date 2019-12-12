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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.tika.Tika;
import org.fzk.tools.xml.JaxenUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility handling/validating METS document. For validation see documentation
 * at: https://ocr-d.github.io/mets 
 * 
 * Mandatory: 
 * - Mets has to be valid against mets.xsd
 * - Unique ID for the document processed
 * - File Group
 *    - must have one mime-type
 *    - Grouping files by page
 * - There is exactly one physical map
 * - Each page mets:div[@TYPE="page"] for every page
 * - File
 *     - Media Type for PAGE XML MIMETYPE attribute set to application/vnd.prima.page+xml.
 *     - Check mimetype for images.
 * - Semantic labels: Check for correct syntax.
 *
 * Syntax for file groups and file IDs are only recommondations.
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
  protected static final String TITLE = "title";
  /**
   * Subtitle of document.
   */
  protected static final String SUB_TITLE = "subtitle";
  /**
   * Author of document.
   */
  protected static final String AUTHOR = "author";
  /**
   * License of document.
   */
  protected static final String LICENSE = "license";
  /**
   * Language of document.
   */
  protected static final String LANGUAGE = "language";
  /**
   * Year of document.
   */
  protected static final String YEAR = "year";
  /**
   * Number of pages of document.
   */
  protected static final String NUMBER_OF_IMAGES = "number_of_images";
  /**
   * Classifications of document.
   */
  protected static final String CLASSIFICATION = "classification";
  /**
   * Genres of document.
   */
  protected static final String GENRE = "genre";
  /**
   * Publisher of document.
   */
  protected static final String PUBLISHER = "publisher";
  /**
   * Physical description of document.
   */
  protected static final String PHYSICAL_DESCRIPTION = "physical_description";
  /**
   * Record identifier (PPN) of document.
   */
  protected static final String PPN = "PPN";
  
  /** 
   * File Group
   */
  protected static final String FILE_GROUPS = "filegrp";
  /**
   * Physical Map
   */
  protected static final String PHYSICAL_MAP = "physicalMap";
  /**
   * Page nodes
   */
  protected static final String PAGE_NODES = "pageNodes";
  /**
   * Unique identifier
   */
  protected static final String UNIQUE_IDENTIFIER = "uniqueIdentifier";
  /**
   * Physical sequence.
   */
  protected static final String PHYSICAL_SEQUENCE = "physicalSequence";
  /**
   * Map holding all relevant X-paths for METS.
   */
  protected static final Map<String, String> metsMap;

  static {
    Map<String, String> dummyMap = new HashMap<>();
    dummyMap.put(TITLE, "/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/mods:mods/mods:titleInfo/mods:title[not(@type)]");
    dummyMap.put(SUB_TITLE, "/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/mods:mods/mods:titleInfo/mods:subTitle[not(@type)]");
    dummyMap.put(YEAR, "/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/mods:mods/mods:originInfo/mods:dateIssued");
    dummyMap.put(LICENSE, "//mets:rightsMD/descendant::*");
    dummyMap.put(AUTHOR, "//mods:name/mods:role/mods:roleTerm[text()='aut']/../../mods:displayForm");
    dummyMap.put(NUMBER_OF_IMAGES, "/mets:mets/mets:fileSec/mets:fileGrp[@USE='OCR-D-IMG']/mets:file");
    dummyMap.put(PUBLISHER, "//mods:publisher[not(@keydate = 'yes')]");
    dummyMap.put(PHYSICAL_DESCRIPTION, "//mods:physicalDescription/mods:extent");
    dummyMap.put(PPN, "//mods:mods/mods:recordInfo/mods:recordIdentifier[1]");
    dummyMap.put(LANGUAGE, "//mods:languageTerm");
    dummyMap.put(CLASSIFICATION, "//mods:classification");
    dummyMap.put(GENRE, "//mods:genre");
     dummyMap.put(FILE_GROUPS, "//mets:fileGrp");
     dummyMap.put(PHYSICAL_MAP, "//mets:structMap[@TYPE='PHYSICAL']");
     dummyMap.put(PAGE_NODES, "//mets:div[@TYPE='page']");
     dummyMap.put(UNIQUE_IDENTIFIER, "//mods:identifier[@type='purl' or @type='url' or @type='urn' or @type='handle' or @type='dtaid']");
     dummyMap.put(PHYSICAL_SEQUENCE, "//mets:div[@TYPE='physSequence']");
     metsMap = Collections.unmodifiableMap(dummyMap);
  }
  /**
   * Error messages: Missing identifier!
   */
  public static final String MISSING_UNIQUE_IDENTIFIER = "Missing unique identifier!";
  /**
   * Error messages: Missing or more than one physical map.
   */
  public static final String MISSING_PHYSICAL_MAP = "Missing one or more than one physical map!";
  /**
   * Error messages: USE of file group has to be unique.
   */
  public static final String USE_FILE_GRP_NOT_UNIQUE = "USE of fileGrp is not unique: ";
  /**
   * Error messages: Different mimetypes inside file group.
   */
  public static final String DIFFERENT_MIMETYPES = "Different mimetypes in filegrp with USE: ";
  /**
   * Error messages: Wrong semantic label.
   */
  public static final String WRONG_SEMANTIC_LABEL = "Wrong semantic label inside METS! - ";
  /**
   * Error messages: Missing file.
   */
  public static final String FILE_NOT_EXISTS = "File doesn't exist: ";
  /**
   * Error messages: Wrong mimetype
   */
  public static final String WRONG_MIMETYPE = "Wrong mimetype for ID: ";
  /**
   * Error messages: Invalid XML
   */
  public static final String PARSING_ERROR = "Error parsing XML!";
  /**
   * Warn messages: Language missing! (optional)
   */
  public static final String MISSING_LANGUAGE = "Language field missing inside METS!";
  /**
   * Warn messages: Genre missing (optional)
   */
  public static final String MISSING_GENRE = "Genre field missing inside METS!";
  /**
   * Warn messages: Classification missing (optional)
   */
  public static final String MISSING_CLASSIFICATION = "Classification field missing inside METS!";

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
    Set<String> fileGrp = new HashSet<>();
    int noOfFileGrps = 0;
    String mimetypeOfGroup;
    StringBuffer message = new StringBuffer();
    String newLine = System.getProperties().getProperty("line.separator");
    Tika tika = new Tika();
    LOGGER.info("Validate files from METS document.");
    List nodes = JaxenUtil.getNodes(metsDocument, metsMap.get(FILE_GROUPS), namespaces);
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
  public static boolean validateLanguageMetadataFromMets(final Document metsDocument) throws Exception {
    boolean valid = false;
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
    Element root = metsDocument.getRootElement();
    String[] values = JaxenUtil.getValues(root, metsMap.get(CLASSIFICATION), namespaces);
    if (values.length >= 1) {
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
   * Validate all ground truth metadata from METS.
   *
   * @param metsDocument METS file.
   * @return true or Exception if not valid.
   */
  public static boolean validateFeaturesFromMets(final Document metsDocument) {
    boolean valid = true;
    String invalidSemanticLabel = null;
    Element root = metsDocument.getRootElement();
    List physicalList = JaxenUtil.getNodes(root, metsMap.get(PHYSICAL_MAP), namespaces);
    if (!physicalList.isEmpty()) {
      Element structMap = (Element) physicalList.get(0);
      List pageList = JaxenUtil.getNodes(structMap, metsMap.get(PAGE_NODES), namespaces);
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
  public static boolean validateUniqueIdentifier(Document metsDocument) {
    boolean valid = false;
    String[] values = JaxenUtil.getValues(metsDocument,metsMap.get(UNIQUE_IDENTIFIER) , namespaces);
    if (values.length > 0) {
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
  public static boolean validatePhysicalMap(Document metsDocument) {
    boolean valid = false;
    String[] values = JaxenUtil.getValues(metsDocument, metsMap.get(PHYSICAL_SEQUENCE), namespaces);
    if (values.length == 1) {
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
   * @throws java.lang.Exception Errors found!
   */
  public static boolean validateCompleteMets(File metsFile) {
    boolean valid;
    valid = MetsUtil.validateMets(metsFile);
    Document metsDocument = XmlUtil.getDocument(metsFile);

    valid |= MetsUtil.validateUniqueIdentifier(metsDocument);
    valid |= MetsUtil.validateFeaturesFromMets(metsDocument);
    valid |= MetsUtil.validatePhysicalMap(metsDocument);
    valid |= MetsUtil.validateMetsFiles(metsDocument, metsFile.toPath().getParent());

    return valid;
  }
}
