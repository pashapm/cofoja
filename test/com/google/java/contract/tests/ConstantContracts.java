/*
 * Copyright 2007 Johannes Rieken
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
package com.google.java.contract.tests;

import com.google.java.contract.Ensures;
import com.google.java.contract.Requires;

/**
 * This class exposes some constant contracts.
 *
 * @author nhat.minh.le@huoc.org (Nhat Minh Lê)
 */
class ConstantContracts {
  @Requires("false || false")
  public void preFailure() {
  }

  @Ensures("false")
  public void postFailure() {
  }

  @Requires("true")
  @Ensures("false")
  public void postFailure1() {
  }

  @Requires("true")
  @Ensures({ "false && false || false", "true", "true && false" })
  public void postFailure2() {
  }

  @Requires("true")
  public void preSuccess() {
  }

  @Ensures("true")
  public void postSuccess() {
  }

  @Requires("true")
  @Ensures("true")
  public void postSuccess1() {
  }

  @Requires("true")
  @Ensures("true")
  public void postSuccess2() {
  }

  @Requires({ "true", "true" })
  @Ensures("true")
  public void postSuccess3() {
  }
}
