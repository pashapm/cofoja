/*
 * Copyright 2010 Google Inc.
 * Copyright 2011 Nhat Minh Lê
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
package com.google.java.contract.core.apt;

import com.google.java.contract.Ensures;
import com.google.java.contract.Invariant;
import com.google.java.contract.Requires;
import com.google.java.contract.core.model.ClassName;
import com.google.java.contract.core.model.ContractAnnotationModel;
import com.google.java.contract.core.model.ElementKind;
import com.google.java.contract.core.model.ElementModel;
import com.google.java.contract.core.model.TypeName;
import com.google.java.contract.core.util.JavaUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.ElementScanner6;

/**
 * Abstract base class providing annotation processing facilities
 * used to build types.
 *
 * @author nhat.minh.le@huoc.org (Nhat Minh Lê)
 */
@Invariant("utils != null")
abstract class AbstractTypeBuilder
    extends ElementScanner6<Void, ElementModel> {
  protected FactoryUtils utils;

  @Requires("utils != null")
  protected AbstractTypeBuilder(FactoryUtils utils) {
    this.utils = utils;
  }

  /**
   * A global iterator to use to get contract annotation line number
   * information.
   */
  protected Iterator<Long> rootLineNumberIterator;

  /**
   * Visits an annotation and adds a corresponding node to the
   * specified Element.
   *
   * Despite the name, this method is not inherited through any
   * visitor interface. It is not intended for external calls.
   *
   * @param parent the target of the annotation
   * @param annotation the annotation
   * @param primary whether this is a primary contract annotation
   * @param owner the owner of this annotation
   * @param p the element to add the created annotation to
   *
   * @see ContractAnnotationModel
   */
  @Requires({
    "parent != null",
    "annotation != null",
    "owner != null",
    "p != null"
  })
  protected void visitAnnotation(
      Element parent, AnnotationMirror annotation,
      boolean primary, ClassName owner, ElementModel p) {
    String annotationName = annotation.getAnnotationType().toString();

    ElementKind kind;
    if (annotationName.equals("com.google.java.contract.Invariant")) {
      kind = ElementKind.INVARIANT;
    } else if (annotationName.equals("com.google.java.contract.Requires")) {
      kind = ElementKind.REQUIRES;
    } else if (annotationName.equals("com.google.java.contract.Ensures")) {
      kind = ElementKind.ENSURES;
    } else if (annotationName.equals("com.google.java.contract.ThrowEnsures")) {
      kind = ElementKind.THROW_ENSURES;
    } else {
      return;
    }

    boolean virtual;
    TypeName returnType;
    if (parent.getKind() != javax.lang.model.element.ElementKind.METHOD) {
      virtual =
          parent.getKind()
          != javax.lang.model.element.ElementKind.INTERFACE;
      returnType = null;
    } else {
      virtual =
          parent.getEnclosingElement().getKind()
          != javax.lang.model.element.ElementKind.INTERFACE;
      ExecutableElement method = (ExecutableElement) parent;
      returnType = utils.getTypeNameForType(
          utils.typeUtils.erasure(method.getReturnType()));
    }
    ContractAnnotationModel model =
        new ContractAnnotationModel(kind, primary, virtual,
                                    owner, returnType);
    List<Long> lineNumbers = null;
    if (rootLineNumberIterator == null) {
      lineNumbers = getLineNumbers(parent, annotation);
    }

    for (AnnotationValue annotationValue :
         annotation.getElementValues().values()) {
      @SuppressWarnings("unchecked")
      List<? extends AnnotationValue> values =
          (List<? extends AnnotationValue>) annotationValue.getValue();

      Iterator<? extends AnnotationValue> iterValue = values.iterator();
      Iterator<Long> iterLineNumber;
      if (rootLineNumberIterator != null) {
        iterLineNumber = rootLineNumberIterator;
      } else {
        iterLineNumber = lineNumbers.iterator();
      }
      while (iterValue.hasNext()) {
        String value = (String) iterValue.next().getValue();
        Long lineNumber =
            iterLineNumber.hasNext() ? iterLineNumber.next() : null;
        model.addValue(value, lineNumber);
      }

      AnnotationSourceInfo sourceInfo =
          new AnnotationSourceInfo(parent, annotation, annotationValue,
                                   model.getValues());
      model.setSourceInfo(sourceInfo);
    }

    p.addEnclosedElement(model);
  }

  /**
   * Returns the line numbers associated with {@code annotation} if
   * available.
   */
  @Requires({
    "parent != null",
    "annotation != null"
  })
  @Ensures("result != null")
  @SuppressWarnings("unchecked")
  protected List<Long> getLineNumbers(Element parent,
                                      AnnotationMirror annotation) {
    if (JavaUtils.classExists("com.sun.source.util.Trees")) {
      try {
        return (List<Long>) Class
            .forName("com.google.java.contract.core.apt.JavacUtils")
            .getMethod("getLineNumbers", ProcessingEnvironment.class,
                       Element.class, AnnotationMirror.class)
            .invoke(null, utils.processingEnv, parent, annotation);
      } catch (Exception e) {
        return Collections.emptyList();
      }
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * Returns the import statements in effect in the compilation unit
   * containing {@code element}.
   */
  @Requires("element != null")
  @Ensures("result != null")
  @SuppressWarnings("unchecked")
  protected Set<String> getImportNames(Element element) {
    if (JavaUtils.classExists("com.sun.source.util.Trees")) {
      try {
        return (Set<String>) Class
            .forName("com.google.java.contract.core.apt.JavacUtils")
            .getMethod("getImportNames", ProcessingEnvironment.class,
                       Element.class)
            .invoke(null, utils.processingEnv, element);
      } catch (Exception e) {
        return Collections.emptySet();
      }
    } else {
      return Collections.emptySet();
    }
  }

  /**
   * Scans a list of annotations and call
   * {@link #visitAnnotation(Element,AnnotationMirror,boolean,ClassName,ElementModel)}
   * on each one of them, in order.
   *
   * @see ContractAnnotationModel
   */
  @Requires({
    "parent != null",
    "owner != null",
    "p != null"
  })
  protected void scanAnnotations(Element parent,
      boolean primary, ClassName owner, ElementModel p) {
    for (AnnotationMirror ann : parent.getAnnotationMirrors()) {
      visitAnnotation(parent, ann, primary, owner, p);
    }
  }
}
