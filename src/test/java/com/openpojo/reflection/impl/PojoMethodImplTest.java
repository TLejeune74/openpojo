/*
 * Copyright (c) 2010-2018 Osman Shoukry
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openpojo.reflection.impl;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.reflection.impl.sample.annotation.AnotherAnnotation;
import com.openpojo.reflection.impl.sample.annotation.SomeAnnotation;
import com.openpojo.reflection.impl.sample.classes.AClassWithAbstractGetter;
import com.openpojo.reflection.impl.sample.classes.AClassWithAbstractSetter;
import com.openpojo.reflection.impl.sample.classes.AClassWithSyntheticMethod;
import com.openpojo.reflection.impl.sample.classes.ClassWithSyntheticConstructor;
import com.openpojo.reflection.impl.sample.classes.PojoMethodClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class PojoMethodImplTest {
  private PojoClass pojoClass;
  private List<PojoMethod> pojoMethods;

  @BeforeEach
  public void setUp() throws Exception {
    pojoClass = PojoClassFactory.getPojoClass(PojoMethodClass.class);
    pojoMethods = pojoClass.getPojoMethods();
  }

  /**
   * Test method for {@link com.openpojo.reflection.impl.PojoMethodImpl#getAnnotation(java.lang.Class)}.
   */
  @Test
  public void testGetAnnotation() {
    for (PojoMethod pojoMethod : pojoMethods) {
      if (pojoMethod.getName().equals("methodWithAnnotation")) {
        assertNotNull(pojoMethod.getAnnotation(SomeAnnotation.class), "removed SomeAnnotation annotation from methodWithAnnotation?");
      }
      if (pojoMethod.getName().equals("methodWithoutAnnotation")) {
        assertNull(pojoMethod.getAnnotation(SomeAnnotation.class), "SomeAnnotation annotation added to methodWithoutAnnotation?");
      }
    }
  }

  @Test
  public void multipleAnnotationsShouldBeReturned() {
    for (PojoMethod pojoMethod : pojoMethods) {
      if (pojoMethod.getName().equals("methodWithMultipleAnnotations")) {
        assertEquals(2, pojoMethod.getAnnotations().size(), String.format("Annotations added/removed from method=[%s]", pojoMethod));
        List<Class<?>> expectedAnnotations = new LinkedList<Class<?>>();
        expectedAnnotations.add(SomeAnnotation.class);
        expectedAnnotations.add(AnotherAnnotation.class);
        for (Annotation annotation : pojoMethod.getAnnotations()) {
          assertTrue( expectedAnnotations.contains(annotation.annotationType()), String.format("Expected annotations [%s] not found, instead found [%s]",
                  expectedAnnotations, annotation.annotationType()));
        }
        return;
      }
    }
    fail(String.format("methodWithMultipleAnnotations renamed? expected in [%s]", pojoClass));
  }

  /**
   * Test method for {@link com.openpojo.reflection.impl.PojoMethodImpl#isFinal()}.
   */
  @Test
  public void testIsFinal() {
    for (PojoMethod pojoMethod : pojoMethods) {
      if (pojoMethod.getName().equals("finalMethod")) {
        assertTrue( pojoMethod.isFinal(), "Failed to check final");
        return;
      }
    }
    fail("finalMethod missing!!");
  }

  @Test
  public void testIsNonFinal() {
    for (PojoMethod pojoMethod : pojoMethods) {
      if (pojoMethod.getName().equals("nonFinalMethod")) {
          assertFalse(pojoMethod.isFinal(), "Failed to check non final");
        return;
      }
    }
    fail("nonFinalMethod missing!!");
  }

  @Test
  public void testIsPrivate() {
    String prefix = "privateMethod";
    PojoMethod pojoMethod = getPojoMethodStartingWith(prefix);

    assertNotNull(pojoMethod, "method not found [" + prefix + "]");
    assertTrue(pojoMethod.isPrivate(), "isPrivate() check on method=[" + pojoMethod + "] returned false!!");
    assertFalse( pojoMethod.isPackagePrivate(), "isPackagePrivate() check on method=[" + pojoMethod + "] returned true!!");
    assertFalse( pojoMethod.isProtected(), "isProtected() check on method=[" + pojoMethod + "] returned true!!");
    assertFalse( pojoMethod.isPublic(), "isPublic() check on method=[" + pojoMethod + "] returned true!!");
  }

  @Test
  public void isPackagePrivate() {
    String prefix = "packagePrivateMethod";
    PojoMethod pojoMethod = getPojoMethodStartingWith(prefix);

    assertNotNull( pojoMethod, "method not found [" + prefix + "]");
    assertTrue( pojoMethod.isPackagePrivate(), "isPackagePrivate() check on method=[" + pojoMethod + "] returned false!!");
    assertFalse( pojoMethod.isPrivate(), "isPrivate() check on method=[" + pojoMethod + "] returned true!!");
    assertFalse( pojoMethod.isProtected(), "isProtected() check on method=[" + pojoMethod + "] returned true!!");
    assertFalse( pojoMethod.isPublic(), "isPublic() check on method=[" + pojoMethod + "] returned true!!");
  }

  @Test
  public void testIsProtected() {
    String prefix = "protectedMethod";
    PojoMethod pojoMethod = getPojoMethodStartingWith(prefix);

    assertNotNull(pojoMethod,"method not found [" + prefix + "]");
    assertTrue(pojoMethod.isProtected(), "isProtected() check on method=[" + pojoMethod + "] returned false!!");
    assertFalse(pojoMethod.isPrivate(), "isPrivate() check on method=[" + pojoMethod + "] returned true!!");
    assertFalse( pojoMethod.isPackagePrivate(), "isPackagePrivate() check on method=[" + pojoMethod + "] returned true!!");
    assertFalse(pojoMethod.isPublic(), "isPublic() check on method=[" + pojoMethod + "] returned true!!");
  }

  @Test
  public void testIsPublic() {
    String prefix = "publicMethod";
    PojoMethod pojoMethod = getPojoMethodStartingWith(prefix);

    assertNotNull(pojoMethod,"method not found [" + prefix + "]");
    assertTrue( pojoMethod.isPublic(), "isPublic() check on method=[" + pojoMethod + "] returned false!!");
    assertFalse(pojoMethod.isPrivate(), "isPrivate() check on method=[" + pojoMethod + "] returned true!!");
    assertFalse( pojoMethod.isPackagePrivate(), "isPackagePrivate() check on method=[" + pojoMethod + "] returned true!!");
    assertFalse( pojoMethod.isProtected(), "isProtected() check on method=[" + pojoMethod + "] returned true!!");
  }

  private PojoMethod getPojoMethodStartingWith(String prefix) {
    for (PojoMethod pojoMethod : pojoMethods)
      if (pojoMethod.getName().startsWith(prefix))
        return pojoMethod;
    throw ReflectionException.getInstance("PojoMethod with prefix [" + prefix + "] not found");
  }

  @Test
  public void testIsStatic() {
    for (PojoMethod pojoMethod : pojoMethods) {
      if (pojoMethod.getName().equals("staticMethod")) {
        assertTrue( pojoMethod.isStatic(), "Failed to check static method");
        return;
      }
    }
    fail("staticMethod missing!!");
  }

  @Test
  public void testIsNotStatic() {
    for (PojoMethod pojoMethod : pojoMethods) {
      if (pojoMethod.getName().equals("nonStaticMethod")) {
          assertFalse(pojoMethod.isStatic(), "Failed to check non static method");
        return;
      }
    }
    fail("nonStaticMethod missing!!");
  }

  @Test
  public void testIsNotSynthetic() {
    for (PojoMethod pojoMethod : pojoMethods) {
      if (pojoMethod.getName().equals("isNotSyntheticMethod")) {
          assertFalse(pojoMethod.isSynthetic(), "Failed to check isNotSynthetic method");
        return;
      }
    }
    fail("isNotSyntheticMethod missing!!");
  }

  @Test
  public void testIsSynthetic() {
    PojoClass syntheticPojoClass = PojoClassFactory.getPojoClass(AClassWithSyntheticMethod.class);
    for (PojoMethod pojoMethod : syntheticPojoClass.getPojoMethods()) {
      if (!pojoMethod.getName().equals("doSomethingSneaky") && !pojoMethod.isConstructor()) {
          assertTrue(pojoMethod.isSynthetic(), "Failed to check synthetic method [" + pojoMethod + "]");
        return;
      }
    }
    fail("failed to find a synthetic method in class");
  }

  @Test
  public void classWithPrivateConstructorAndBuilder_hasSyntheticConstrutor() {
    assertNotNull(ClassWithSyntheticConstructor.Builder.getInstance());
    PojoClass pojoClass = PojoClassFactory.getPojoClass(ClassWithSyntheticConstructor.class);
    assertEquals(2, pojoClass.getPojoConstructors().size());
    for (PojoMethod constructor : pojoClass.getPojoMethods()) {
      if (constructor.getParameterTypes().length == 0)
        assertFalse(constructor.isSynthetic(), "Synthatic constructor found!! [" + constructor + "]");
      else
        assertTrue(constructor.isSynthetic(), "None synthatic constructor found!! [" + constructor + "]");
    }
  }

  @Test
  public void shouldNotIncludeAbstractGetterMethod() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AClassWithAbstractGetter.class);
    boolean hasAbstractGetterMethod = false;
    PojoField pojoField = pojoClass.getPojoFields().getFirst();

    String expectedGetterName = "get" + pojoField.getName().substring(0, 1).toUpperCase() + pojoField.getName().substring(1,
        pojoField.getName().length());
    for (PojoMethod pojoMethod : pojoClass.getPojoMethods()) {

      if (pojoMethod.getName().equals(expectedGetterName))
        hasAbstractGetterMethod = true;
    }
    assertTrue(hasAbstractGetterMethod);
    assertEquals(1, pojoClass.getPojoFields().size());
    assertFalse(pojoField.hasGetter());
  }

  @Test
  public void shouldNotIncludeAbstractSetterMethod() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AClassWithAbstractSetter.class);
    boolean hasAbstractSetterMethod = false;
    PojoField pojoField = pojoClass.getPojoFields().getFirst();

    String expectedSetterName = "set" + pojoField.getName().substring(0, 1).toUpperCase() + pojoField.getName().substring(1,
        pojoField.getName().length());
    for (PojoMethod pojoMethod : pojoClass.getPojoMethods()) {

      if (pojoMethod.getName().equals(expectedSetterName))
        hasAbstractSetterMethod = true;
    }
    assertTrue(hasAbstractSetterMethod);
    assertEquals(1, pojoClass.getPojoFields().size());
    assertFalse(pojoField.hasGetter());
  }
}
