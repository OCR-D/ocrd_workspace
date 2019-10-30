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
package edu.kit.ocrd.workspace.entity;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test GroundTruthProperties.
 */
public class GroundTruthPropertiesTest {
  
  public GroundTruthPropertiesTest() {
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
   * Test of values method, of class GroundTruthProperties.
   */
  @Test
  public void testValues() {
    System.out.println("values");
    GroundTruthProperties[] result = GroundTruthProperties.values();
    assertEquals(352, result.length);
  }

  /**
   * Test of valueOf method, of class GroundTruthProperties.
   */
  @Test
  public void testValueOf() {
    System.out.println("valueOf");
    String arg0 = "AUTOMATION";
    GroundTruthProperties expResult = GroundTruthProperties.AUTOMATION;
    GroundTruthProperties result = GroundTruthProperties.valueOf(arg0);
    assertEquals(expResult, result);
  }

  /**
   * Test of toString method, of class GroundTruthProperties.
   */
  @Test
  public void testToString() {
    System.out.println("toString");
    GroundTruthProperties instance = GroundTruthProperties.PRODUCTION_RELATED;
    String expResult = "condition/production-related";
    String result = instance.toString();
    assertEquals(expResult, result);
  }

  /**
   * Test of get method, of class GroundTruthProperties.
   */
  @Test
  public void testGet() {
    System.out.println("get");
    String textRepresentation = "condition/acquisition/method-flaws/imaging/missing-content/thresholding";
    GroundTruthProperties expResult = GroundTruthProperties.THRESHOLDING;
    GroundTruthProperties result = GroundTruthProperties.get(textRepresentation);
    assertEquals(expResult, result);
  }
  
}
