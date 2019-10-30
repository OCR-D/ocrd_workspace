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
package edu.kit.ocrd.workspace.exception;

/**
 * Invalid format of workspace.
 */
public class WorkspaceException extends RuntimeException {

  /**
   * Default constructor.
   */
  public WorkspaceException() {
    super();
  }

  /**
   * Constructor with given message and cause.
   *
   * @param message Message.
   * @param cause Cause.
   */
  public WorkspaceException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor with given message.
   *
   * @param message Message.
   */
  public WorkspaceException(String message) {
    super(message);
  }

  /**
   * Constructor with given message and cause.
   *
   * @param cause Cause.
   */
  public WorkspaceException(Throwable cause) {
    super(cause);
  }
}
