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
package com.google.java.contract.core.agent;

import com.google.java.contract.Requires;
import com.google.java.contract.core.util.JavaUtils;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * A class adapter that transforms injected contract methods to remove
 * contract compilation artefacts:
 *
 * <ul>
 * <li>fix calls to {@code access$n} synthetic methods for inner/nested
 * classes.
 * </ul>
 *
 * @author nhat.minh.le@huoc.org (Nhat Minh Lê)
 */
class ContractFixingClassAdapter extends ClassAdapter {
  /**
   * A method adapter that amends calls to {@code access$n} synthetic
   * methods. These methods are generated for access to members from
   * inner/nested classes. Contract compilation may generate some of
   * these, which are renamed and injected into the original class
   * bytecode during instrumentation. Calls to these methods need to
   * be fixed to use the new names.
   */
  protected static class AccessMethodAdapter extends MethodAdapter {
    /**
     * Constructs a new AccessMethodAdapter.
     *
     * @param mv the MethodVisitor this adapter delegates to
     */
    @Requires("mv != null")
    public AccessMethodAdapter(MethodVisitor mv) {
      super(mv);
    }

    /**
     * Converts calls to {@code access$n} synthetic methods to the
     * equivalent injected methods.
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name,
                                String desc) {
      if (!name.startsWith("access$")) {
        mv.visitMethodInsn(opcode, owner, name, desc);
      } else {
        mv.visitMethodInsn(opcode, owner,
                           JavaUtils.SYNTHETIC_MEMBER_PREFIX + name, desc);
      }
    }
  }

  /**
   * Constructs a new ContractFixingClassAdapter.
   *
   * @param cv the ClassVisitor this adapter delegates to
   */
  @Requires("cv != null")
  public ContractFixingClassAdapter(ClassVisitor cv) {
    super(cv);
  }

  /**
   * Visits the specified method fixing method calls.
   */
  @Override
  public MethodVisitor visitMethod(int access, String name, String desc,
                                   String signature, String[] exceptions) {
    MethodVisitor mv = cv.visitMethod(access | Opcodes.ACC_SYNTHETIC,
                                      name, desc, signature, exceptions);
    return new AccessMethodAdapter(mv);
  }
}
