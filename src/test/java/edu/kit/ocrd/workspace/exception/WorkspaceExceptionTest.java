/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.kit.ocrd.workspace.exception;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test WorkspaceException
 */
public class WorkspaceExceptionTest {

  public WorkspaceExceptionTest() {
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

  @Test
  public void testConstructors() {
    String expectedMessage = "message";
    Throwable expectedThrowable = new Throwable();
    WorkspaceException wse = new WorkspaceException();
    assertTrue(Boolean.TRUE);
    wse = new WorkspaceException(expectedMessage);
    assertEquals(expectedMessage, wse.getMessage());
    wse = new WorkspaceException(expectedThrowable);
    assertEquals(expectedThrowable, wse.getCause());
    wse = new WorkspaceException(expectedMessage, expectedThrowable);
    assertEquals(expectedMessage, wse.getMessage());
    assertEquals(expectedThrowable, wse.getCause());
  }

}
