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

import com.openpojo.business.annotation.BusinessKey;
import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.construct.InstanceFactory;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.reflection.impl.sample.classes.AClassWithFields;
import com.openpojo.reflection.impl.sample.annotation.SomeAnnotation;
import com.openpojo.reflection.impl.sample.classes.AClassWithSyntheticField;
import com.openpojo.reflection.impl.sample.classes.AClassWithVariousAnnotatedFields;
import com.openpojo.reflection.impl.sample.classes.ClassWithGenericTypes;
import com.openpojo.reflection.impl.sample.classes.PojoFieldImplClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class PojoFieldImplTest {
  private PojoClass pojoClass = PojoClassFactory.getPojoClass(PojoFieldImplClass.class);
  private Object pojoClassInstance;

  /**
   * @throws java.lang.Exception
   */
  @BeforeEach
  public void setUp() throws Exception {
    pojoClassInstance = InstanceFactory.getInstance(pojoClass);
  }

  /**
   * Test method for: {@link com.openpojo.reflection.impl.PojoFieldImpl#get(java.lang.Object)}.
   * {@link com.openpojo.reflection.impl.PojoFieldImpl#set(java.lang.Object, java.lang.Object)}.
   */
  @Test
  public void testSetAndGet() {
    for (PojoField pojoField : pojoClass.getPojoFields()) {
      if (!pojoField.isFinal() && !pojoField.isPrimitive()) {
        assertNull(pojoField.get(pojoClassInstance), String.format("Field=[%s] should have null default value", pojoField));
        Object randomValue = RandomFactory.getRandomValue(pojoField.getType());
        pojoField.set(pojoClassInstance, randomValue);
        assertEquals(randomValue, pojoField.get(pojoClassInstance),
                String.format("PojoField.get() result=[%s] different from what was set=[%s] for " +
                        "PojoFieldImpl=[%s]", pojoField.get(pojoClassInstance), randomValue, pojoField));
      }
    }
  }

  @Test
  public void canAccessGetter() {
    boolean found = false;
    for (PojoField pojoField : pojoClass.getPojoFields()) {
      if (pojoField.hasGetter()) {
        PojoMethod getter = pojoField.getGetter();
        assertNotNull( getter, "Getter can't be retrieved");
        Object randomInstance = RandomFactory.getRandomValue(pojoField.getType());
        pojoField.set(pojoClassInstance, randomInstance);
        assertSame(randomInstance, getter.invoke(pojoClassInstance),"Expected same object in and out");
        found = true;
      }
    }
    assertTrue(found, "No getters were found!");
  }

  @Test
  public void canAccessSetter() {
    boolean found = false;
    for (PojoField pojoField : pojoClass.getPojoFields()) {
      if (pojoField.hasSetter()) {
        PojoMethod setter = pojoField.getSetter();
        assertNotNull(setter, "Setter can't be retrieved");
        Object randomInstance = RandomFactory.getRandomValue(pojoField.getType());
        setter.invoke(pojoClassInstance, randomInstance);
        assertSame( randomInstance, pojoField.get(pojoClassInstance), "Expected same object in and out");
        found = true;
      }
    }
    assertTrue(found,"No Setters were found!");
  }

  private PojoField getPrivateStringField() {
    for (PojoField pojoField : pojoClass.getPojoFields()) {
      if (pojoField.getName().equals("privateString")) {
        return pojoField;
      }
    }
    fail("Field with name 'privateString' removed from class" + pojoClass.getName());
    return null;
  }

  @Test
  public void shouldFailSet() {
    PojoField pojoField = getPrivateStringField();
    assert pojoField != null;
      assertThrows(ReflectionException.class, () -> pojoField.set(null, RandomFactory.getRandomValue(pojoField.getType())));
  }

  @Test
  public void shouldFailGet() {
    PojoField pojoField = getPrivateStringField();
    assert pojoField != null;
      assertThrows(ReflectionException.class, () -> pojoField.get(null));
  }

  @Test
  public void shouldFailSetter() {
    PojoField pojoField = getPrivateStringField();
    assert pojoField != null;
      assertThrows(ReflectionException.class, () -> pojoField.invokeSetter(null, RandomFactory.getRandomValue(pojoField.getType())));
  }

  @Test
  public void shouldFailGetter() {
    PojoField pojoField = getPrivateStringField();
    assert pojoField != null;
      assertThrows(ReflectionException.class, () -> pojoField.invokeGetter(null));
  }

  @Test
  public void shouldGetParameterizedType() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(ClassWithGenericTypes.class);
    assertEquals( 4, pojoClass.getPojoFields().size(),"Fields added/removed?!");

    int affirmChecks = 0;
    for (PojoField pojoField : pojoClass.getPojoFields()) {
      if (pojoField.getName().equals("parameterizedChildren")) {
        assertTrue(pojoField.isParameterized(), "Not Generic?!");
        assertTrue( pojoField.getParameterTypes().contains(ClassWithGenericTypes.class), "Wrong Parameterization!?");
        affirmChecks++;
      }

      if (pojoField.getName().equals("nonparameterizedList") || pojoField.getName().equals("nonParameterizedString")) {
        assertFalse(pojoField.isParameterized(), "Turned generic?!");
        assertEquals( 0, pojoField.getParameterTypes().size(), "Returned non-empty list for nonParameterized type!? [" + pojoField.getParameterTypes() + "]");
        affirmChecks++;
      }

      if (pojoField.getName().equals("parameterizedMap")) {
        assertEquals( 2, pojoField.getParameterTypes().size(), "MultipTypeGeneric failed!!");
        assertTrue(pojoField.getParameterTypes().contains(String.class), String.format("Type not found [%s]", String.class));
        assertTrue(pojoField.getParameterTypes().contains(Integer.class), String.format("Type not found [%s]", Integer.class));
        affirmChecks++;
      }
    }
    assertEquals(4, affirmChecks, "Fields added/removed/renamed? expected 4 checks!!");
  }

  @Test
  public void annotationlessShouldNotReturnNull() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AClassWithVariousAnnotatedFields.class);
    List<PojoField> allFields = pojoClass.getPojoFields();

    for (PojoField pojoField : allFields) {
      if (pojoField.getName().equals("nonAnnotatedField")) {
        assertNotNull( pojoField.getAnnotations(), "getAnnotations should not return null.");
        return;
      }
    }
    fail(String.format("nonAnnotatedField renamed? expected in [%s]", pojoClass));
  }

  @Test
  public void multipleAnnotationsShouldBeReturned() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AClassWithVariousAnnotatedFields.class);
    List<PojoField> allFields = pojoClass.getPojoFields();

    for (PojoField pojoField : allFields) {
      if (pojoField.getName().equals("multipleAnnotationField")) {
        assertEquals( 2, pojoField.getAnnotations().size(), String.format("Annotations added/removed from field=[%s]", pojoField));
        List<Class<?>> expectedAnnotations = new LinkedList<Class<?>>();
        expectedAnnotations.add(SomeAnnotation.class);
        expectedAnnotations.add(BusinessKey.class);
        for (Annotation annotation : pojoField.getAnnotations()) {
          assertTrue(expectedAnnotations.contains(annotation.annotationType()), String.format("Expected annotations [%s] not found, instead found [%s]", expectedAnnotations,
                  annotation.annotationType()));
        }
        return;
      }
    }
    fail(String.format("multipleAnnotationField renamed? expected in [%s]", pojoClass));
  }

  /**
   * Test method for {@link com.openpojo.reflection.impl.PojoFieldImpl#isPrimitive()}.
   */
  @Test
  public void testIsPrimitive() {
    for (PojoField pojoField : pojoClass.getPojoFields()) {
      if (pojoField.getName().startsWith("primitive")) {
        assertTrue(pojoField.isPrimitive(), String.format("isPrimitive() check on primitive field=[%s] returned false!!", pojoField));
      }
    }
  }

  /**
   * Test method for {@link com.openpojo.reflection.impl.PojoFieldImpl#isStatic()}.
   */
  @Test
  public void testIsStatic() {
    for (PojoField pojoField : pojoClass.getPojoFields()) {
      if (pojoField.getName().startsWith("static")) {
        assertTrue(pojoField.isStatic(), String.format("isStatic() check on field=[%s] returned false!!", pojoField));
      }
    }
  }

  @Test
  public void testIsTransient() {
    for (PojoField pojoField : pojoClass.getPojoFields()) {
      if (pojoField.getName().equals("transientString")) {
        assertTrue(pojoField.isTransient(), String.format("isTransient() check on field=[%s] returned false!!", pojoField));
      }
    }

  }

  @Test
  public void testIsVolatile() {
    for (PojoField pojoField : pojoClass.getPojoFields()) {
      if (pojoField.getName().equals("volatileString")) {
        assertTrue( pojoField.isVolatile(), String.format("isVolatile() check on field=[%s] returned false!!", pojoField));
      }
    }

  }

  @Test
  public void testIsPrivate() {
    String prefix = "private";
    PojoField pojoField = getFieldStartingWith(prefix);
    assertNotNull(pojoField, "Field not found [" + prefix + "]");
    assertTrue(pojoField.isPrivate(), "isPrivate() check on field=[" + pojoField + "] returned false!!");
    assertFalse( pojoField.isPublic(), "isPublic() check on field=[" + pojoField + "] returned true!!");
    assertFalse(pojoField.isProtected(), "isProtected() check on field=[" + pojoField + "] returned true!!");
    assertFalse(pojoField.isPackagePrivate(), "isPackagePrivate() check on field=[" + pojoField + "] returned true!!");
  }

  @Test
  public void isPackagePrivate() {
    String prefix = "packagePrivate";
    PojoField pojoField = getFieldStartingWith(prefix);
    assertNotNull(pojoField, "Field not found [" + prefix + "]");
    assertTrue(pojoField.isPackagePrivate(), "isPackagePrivate() check on field=[" + pojoField + "] returned false!!");
    assertFalse( pojoField.isPrivate(), "isPrivate() check on field=[" + pojoField + "] returned true!!");
    assertFalse( pojoField.isPublic(), "isPublic() check on field=[" + pojoField + "] returned true!!");
    assertFalse(pojoField.isProtected(), "isProtected() check on field=[" + pojoField + "] returned true!!");
  }

  @Test
  public void testIsProtected() {
    String prefix = "protected";
    PojoField pojoField = getFieldStartingWith(prefix);
    assertNotNull( pojoField, "Field not found [" + prefix + "]");
    assertTrue( pojoField.isProtected(), "isProtected() check on field=[" + pojoField + "] returned false!!");
    assertFalse( pojoField.isPrivate(), "isPrivate() check on field=[" + pojoField + "] returned true!!");
    assertFalse(pojoField.isPackagePrivate(), "isPackagePrivate() check on field=[" + pojoField + "] returned true!!");
    assertFalse( pojoField.isPublic(), "isPublic() check on field=[" + pojoField + "] returned true!!");
  }

  @Test
  public void testIsPublic() {
    String prefix = "public";
    PojoField pojoField = getFieldStartingWith(prefix);
    assertNotNull(pojoField, "Field not found [" + prefix + "]");
    assertTrue( pojoField.isPublic(), "isPublic() check on field=[" + pojoField + "] returned false!!");
    assertFalse( pojoField.isPrivate(), "isPrivate() check on field=[" + pojoField + "] returned true!!");
    assertFalse(pojoField.isPackagePrivate(), "isPackagePrivate() check on field=[" + pojoField + "] returned true!!");
    assertFalse(pojoField.isProtected(), "isProtected() check on field=[" + pojoField + "] returned true!!");
  }

  private PojoField getFieldStartingWith(String prefix) {
    for (PojoField pojoField : pojoClass.getPojoFields())
      if (pojoField.getName().startsWith(prefix))
        return pojoField;
    throw ReflectionException.getInstance("Request field with prefix [" + prefix + "} not found.");
  }

  @Test
  public void testIsSynthetic() {
    PojoClass classWithSyntheticField = PojoClassFactory.getPojoClass(AClassWithSyntheticField.SyntheticFieldContainer.class);
    assertEquals( 1, classWithSyntheticField
        .getPojoFields().size(), "Failed to find field in class[" + classWithSyntheticField + "]");
    PojoField pojoField = classWithSyntheticField.getPojoFields().get(0);
    assertTrue(pojoField.isSynthetic(), "Failed to check isSynthetic + [" + pojoField + "]");
  }

  @Test
  public void canGetEnclosingClass() {
    PojoClass pojoClassWithFields = PojoClassFactory.getPojoClass(AClassWithFields.class);
      assertFalse(pojoClassWithFields.getPojoFields().isEmpty(), "Class should have some fields");
    for (PojoField field : pojoClassWithFields.getPojoFields()) {
      assertEquals(pojoClassWithFields, field.getDeclaringPojoClass(), "Failed to get PojoClass from field ["+ field + "]");
    }
  }
}
