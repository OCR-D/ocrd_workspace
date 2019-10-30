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
import java.io.File;
import org.fzk.tools.xml.JaxenUtil;
import org.jdom.Document;
import org.jdom.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility handling PAGE document.
 *
 */
public class PageUtil {
  public static final String WRONG_IMAGE_URL = "Wrong imgage URL inside PAGE: ";
  /**
   * Logger.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(PageUtil.class);

  /**
   * Validate image urls.
   *
   * @param pageFile PAGE file
   * @param metsFile Mets file
   * @return valid or Exception if not.
   */
  public static boolean validateImageUrl(final File pageFile, final File metsFile) throws Exception {
    boolean valid = false;
    String message = WRONG_IMAGE_URL + pageFile.getPath();
    Document pageDocument = JaxenUtil.getDocument(pageFile);
    String pageNamespace = XmlUtil.getNamespace(pageDocument);
    Namespace[] namespaces = {Namespace.getNamespace("page", pageNamespace)};
    Document metsDocument = JaxenUtil.getDocument(metsFile);
    String[] values = JaxenUtil.getValues(pageDocument, "page:Page/@imageFilename", namespaces);
    if ((values != null) && (values.length == 1)) {
      String imageUrl = values[0];
      if (imageUrl.startsWith("file://")) {
        imageUrl = imageUrl.substring(7);
      }
      String[] attributesValues = JaxenUtil.getAttributesValues(metsDocument, "//mets:FLocat/@xlink:href", MetsUtil.getNamespaces());
      for (String value : attributesValues) {
        if (value.equals(imageUrl)) {
          valid = true;
          break;
        }
      }
    }
    if (!valid) {
      LOGGER.error(message);
      throw new WorkspaceException(message);
    }
    return valid;
  }

  /**
   * Validate page file against page.xsd
   * (https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd)
   *
   * @param pageFile PAGE file
   * @return valid or Exception if not.
   */
  public static boolean validatePage(final File pageFile) {
    boolean valid = false;
    valid = XmlUtil.validateXml(pageFile);
    return valid;
  }
}
