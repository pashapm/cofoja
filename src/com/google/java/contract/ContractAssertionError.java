/*
 * Copyright 2010 Google Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */
package com.google.java.contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for contract assertion errors.
 *
 * @author nhat.minh.le@huoc.org (Nhat Minh Lê)
 */
public abstract class ContractAssertionError extends AssertionError {
  /**
   * Constructs a new ContractAssertionError.
   *
   * @param msg the error message.
   */
  public ContractAssertionError(String msg) {
    super(msg);
    cleanStackTrace();
  }

  /**
   * Constructs a new ContractAssertionError.
   *
   * @param msg the error message.
   * @param cause a previous contract error
   */
  public ContractAssertionError(String msg, ContractAssertionError cause) {
    super(msg);
    initCause(cause);
    cleanStackTrace();
  }

  /**
   * Remove wrapper call, leaving only the contract helper.
   */
  private void cleanStackTrace() {
    StackTraceElement[] realTrace = getStackTrace();
    StackTraceElement[] trace = new StackTraceElement[realTrace.length - 1];
    trace[0] = realTrace[0];
    System.arraycopy(realTrace, 2, trace, 1, realTrace.length - 2);
    setStackTrace(trace);
  }

  public List<String> getMessages() {
    ArrayList<String> list = new ArrayList<String>();
    Throwable error = this;
    do {
      list.add(error.getMessage());
      error = error.getCause();
    } while (error != null);
    return list;
  }
}
