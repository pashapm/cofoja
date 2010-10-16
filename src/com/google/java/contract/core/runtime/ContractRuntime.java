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
package com.google.java.contract.core.runtime;

import com.google.java.contract.SpecificationError;

/**
 * Utility methods to manipulate the {@link ContractContext} in the
 * current thread. Methods are modeled after methods of
 * ContractContext.
 *
 * @author nhat.minh.le@huoc.org (Nhat Minh Lê)
 */
public class ContractRuntime {
  public static boolean enter() {
    return ContractContext.getContractContext().enter();
  }

  public static void leave() {
    ContractContext.getContractContext().leave();
  }

  /**
   * Magically casts the first argument to the type of the second
   * argument.
   *
   * <p>This method is part of the old value type inference trick. It
   * is called at run time to cast the old value variable (first
   * argument) to the type of an unevaluated version of the old value
   * expression (second argument). The "unevaluation" is achieved
   * through a constant conditional (for example,
   * {@code true ? null : oldExpression}).
   */
  @SuppressWarnings("unchecked")
  public static <T> T magicCast(Object obj, T dummy) {
    return (T) obj;
  }

  static {
    if (ContractedChecker.startupContractedCheck) {
      try {
        ContractedChecker.getInstance().assertLoadedClassesContracted();
      } catch (SpecificationError e) {
        throw new RuntimeException(e);
      }
    }
  }
}
