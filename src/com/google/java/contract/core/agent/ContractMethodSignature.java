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
package com.google.java.contract.core.agent;

import com.google.java.contract.core.model.ContractKind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta data attached to a contract method. The metadata is generated
 * at compile-time by the contract compiler and provides information
 * to the runtime Java agent about the purpose of the contract
 * method. No two contract methods in a same class should have the
 * same signature.
 *
 * @author nhat.minh.le@huoc.org (Nhat Minh Lê)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface ContractMethodSignature {
  /**
   * The kind of contract method, which determines its role with
   * regard to the contracted class.
   */
  ContractKind kind();

  /**
   * The target of the contract method. Either the name of a method or
   * the empty string if the contract targets the whole class.
   */
  String target() default "";

  /**
   * The ID of the contract method. IDs are used to distinguish
   * between two contract methods with similar roles.
   */
  int id() default -1;

  /**
   * The line numbers where the original contract is located.
   */
  long[] lines() default {};
}
