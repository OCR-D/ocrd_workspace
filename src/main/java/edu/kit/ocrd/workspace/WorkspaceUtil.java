/*
 * Copyright 2019 Karlsruhe Institute of Technology.
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
import java.io.File;
import java.io.IOException;
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
 * Utilities for workspace.
 * See documentation at: https://ocr-d.github.io/mets
 * - Mets has to be valid against mets.xsd
 * - Pixel density of images must be explicit and high enough
 * - Unique ID for the document processed
 * - File Group 
 *    - must have a mime-type
 *    - syntax (D := "OCR-D-" + PREFIX? + WORKFLOW_STEP + ("-" + PROCESSOR)?
PREFIX := ("" | "GT-")
WORKFLOW_STEP := ("IMG" | "SEG" | "OCR" | "COR")
PROCESSOR := [A-Z0-9\-]{3,}
* 
* File ID syntax
* FILEID := ID + "_" + [0-9]{4}
ID := "OCR-D-" + WORKFLOW_STEP + ("-" + PROCESSOR)?
WORKFLOW_STEP := ("IMG" | "SEG" | "OCR" | "COR")
PROCESSOR := [A-Z0-9\-]{3,}
* -Grouping files by page
* exactly one physical map
* mets:div[@TYPE="page"] for every page
* Media Type for PAGE XML MIMETYPE attribute set to application/vnd.prima.page+xml.
 */
public class WorkspaceUtil {

  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceUtil.class);


  /**
   * Validate workspace due to definition found at https://ocr-d.github.io/mets
   *
   * @param metsFile Mets file
   * @return valid or Exception if not.
   */
  public static boolean validateWorkspace(final File metsFile) {
    boolean valid = false;
    valid = MetsUtil.validateMets(metsFile);
    return valid;
  }
}
