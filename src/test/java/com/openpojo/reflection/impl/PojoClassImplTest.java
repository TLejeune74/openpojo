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
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import com.openpojo.business.BusinessIdentity;
import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.PojoPackage;
import com.openpojo.reflection.construct.InstanceFactory;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.reflection.impl.sample.annotation.AnotherAnnotation;
import com.openpojo.reflection.impl.sample.annotation.SomeAnnotation;
import com.openpojo.reflection.impl.sample.classes.*;
import com.openpojo.reflection.impl.sample.classes.AClassWithNestedClass.NestedClass;
import com.openpojo.reflection.java.Java;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * TODO: This test class needs to be re-worked, to focus on just the PojoClassImpl not across services, i.e.
 * PojoClassFactory, InstanceFactory, etc...
 */
public class PojoClassImplTest {
  private static final String SAMPLE_CLASSES_PKG = PojoClassImplTest.class.getPackage().getName() + ".sample.classes";

  @Test
  public void testIsInterfaceIsAbstractIsConcrete() {
    final String message = "Class type check failed on [%s], actual class returned [%s], PojoClass returned [%s]!!";
    for (final PojoClass pojoClass : PojoClassFactory.getPojoClassesRecursively(SAMPLE_CLASSES_PKG, null)) {
      final Class<?> actualClass = pojoClass.getClazz();
      assertTrue(pojoClass.isInterface() == actualClass.isInterface(), String.format(message, actualClass.getName() + ".isInterface()", actualClass.isInterface(),
              pojoClass.isInterface()));
      assertTrue(pojoClass.isAbstract() == (Modifier.isAbstract(actualClass.getModifiers())
              && !Modifier.isInterface(actualClass.getModifiers())), String.format(message, actualClass.getName() + ".isAbstract()",
              Modifier.isAbstract(actualClass.getModifiers())
                      && !Modifier.isInterface(actualClass.getModifiers()), pojoClass.isAbstract()));

      final boolean expectedValue = !(Modifier.isAbstract(actualClass.getModifiers())
          || actualClass.isInterface()
          || actualClass.isEnum());
      final boolean actualValue = pojoClass.isConcrete();
      assertTrue(actualValue == expectedValue, String.format(message, actualClass.getName() + ".isConcrete()", expectedValue, actualValue));
    }
  }

  @Test
  public void testIsFinalOnFinalClass() {
    final Class<?> aFinalClass = AFinalClass.class;
    final PojoClass pojoClass = getPojoClassImplForClass(aFinalClass);

    assertTrue( pojoClass.isFinal(), String.format("IsFinal on final=[%s] returned false for PojoClass implementation=[%s]!!", aFinalClass,
            pojoClass));
  }

  @Test
  public void testIsFinalOnNonFinalClass() {
    final Class<?> aNonFinalClass = ANonFinalClass.class;
    final PojoClass pojoClass = getPojoClassImplForClass(aNonFinalClass);
    assertFalse(pojoClass.isFinal(), String.format("IsFinal on non-final=[%s] returned true for PojoClass implementation=[%s]!!",
            aNonFinalClass, pojoClass));
  }

  @Test
  public void testIsSyntheticOnNonSyntheticClass() {
    PojoClass aClassWithSyntheticDoublePojoClass = getPojoClassImplForClass(AClassWithSythetics.class);
    assertFalse( aClassWithSyntheticDoublePojoClass.isSynthetic(), "Class isn't synthetic");
  }

  @Test
  public void testIsSyntheticOnSyntheticClass() {
    PojoClass syntheticPojoClass = getPojoClassImplForClass(AClassWithSythetics.class);
    assertEquals( 2, syntheticPojoClass.getPojoConstructors().size(), "Expected 2 constructors");

    PojoMethod constructor = null;

    for (PojoMethod constructorEntry : syntheticPojoClass.getPojoConstructors()) {
      if (constructorEntry.getParameterTypes().length > 0)
        constructor = constructorEntry;
    }

    assertNotNull(constructor);
    assertTrue(constructor.isSynthetic(), "Failed to find synthetic constructor");
    assertEquals(1, constructor.getParameterTypes().length, "Synthetic Constructor should have just one parameter");

    PojoClass aSyntheticClass = getPojoClassImplForClass(constructor.getParameterTypes()[0]);
    assertTrue( aSyntheticClass.isSynthetic(),"Parameter to synthetic constructor should be synthetic class");
  }

  @Test
  public void testGetPojoFieldsAnnotatedWith() {
    PojoClass pojoClass = getPojoClassImplForClass(AClassWithAnnotatedFields.class);
    assertEquals(4, pojoClass.getPojoFields().size(), "Expected 4 fields");

    List<PojoField> annotatedPojoFields = pojoClass.getPojoFieldsAnnotatedWith(SomeAnnotation.class);
    assertEquals( 2, annotatedPojoFields.size(), "Expected 2 annotated fields");


  }

  @Test
  @SuppressWarnings("PointlessArithmeticExpression")
  public void testGetPojoMethods() {
    PojoClass pojoClass = getPojoClassImplForClass(AClassWithSixMethods.class);
    assertEquals(6 + 1 /* constructor */, pojoClass.getPojoMethods().size(), String.format("Methods added/removed from class=[%s] found methods=[%s]", pojoClass.getName(),
            pojoClass.getPojoMethods()));

    pojoClass = getPojoClassImplForClass(AClassWithoutMethods.class);
    assertEquals( 0 + 1 /* constructor */, pojoClass.getPojoMethods().size(),String.format("Methods added/removed from class=[%s]", pojoClass.getName()));
  }

  @Test
  public void testGetPojoMethodsAnnotatedWith() {
    PojoClass pojoClass = getPojoClassImplForClass(AClassWithAnnotatedMethods.class);
    assertEquals( 4 + 1 /* constructor */, pojoClass.getPojoMethods().size(), "Expected 5 methods");

    List<PojoMethod> annotatedPojoFields = pojoClass.getPojoMethodsAnnotatedWith(SomeAnnotation.class);
    assertEquals( 2, annotatedPojoFields.size(),"Expected 2 annotated methods");

  }

  @Test
  public void testExtendz() {
    final Class<?> aClassExtendingAnInterfaceAndAbstract = AClassExtendingAnInterface.class;
    final Class<?> anInterface = AnInterfaceClass.class;

    final PojoClass pojoClass = getPojoClassImplForClass(aClassExtendingAnInterfaceAndAbstract);
    assertTrue( pojoClass.extendz(anInterface), String.format("Failed to validate Class=[%s] extending an interface=[%s]" + " for PojoClass " +
            "Implementation=[%s]", aClassExtendingAnInterfaceAndAbstract, anInterface, pojoClass));
  }

  @Test
  public void testGetAnnotations() {
    final Class<?> aClassWithAnnotations = AClassWithAnnotations.class;

    final PojoClass pojoClass = getPojoClassImplForClass(aClassWithAnnotations);
    assertEquals( 2, pojoClass.getAnnotations().size(), String.format("Annotations added/removed from Class=[%s]", aClassWithAnnotations));
  }

  @Test
  public void multipleAnnotationsShouldBeReturned() {
    final Class<?> aClassWithAnnotations = AClassWithAnnotations.class;

    final PojoClass pojoClass = getPojoClassImplForClass(aClassWithAnnotations);
    assertEquals(2, pojoClass.getAnnotations().size(), String.format("Annotations added/removed from Class=[%s]", aClassWithAnnotations));

    final List<Class<?>> expectedAnnotations = new LinkedList<Class<?>>();
    expectedAnnotations.add(SomeAnnotation.class);
    expectedAnnotations.add(AnotherAnnotation.class);
    for (final Annotation annotation : pojoClass.getAnnotations()) {
      assertTrue(expectedAnnotations.contains(annotation.annotationType()), String.format("Expected annotations [%s] not found, instead found [%s]", expectedAnnotations,
              annotation.annotationType()));
    }
  }

  @Test
  public void shouldFailToCreateInstanceOnInterface() {
    final PojoClass pojoClass = getPojoClassImplForClass(AnInterfaceClass.class);
    assertThrows(ReflectionException.class, () -> {InstanceFactory.getInstance(pojoClass);});
  }

  @Test
  public void shouldCreateInstanceOnAbstract() {
    final PojoClass pojoClass = getPojoClassImplForClass(AnAbstractClass.class);
    assertNotNull( InstanceFactory.getInstance(pojoClass), "Should have created instance");
  }

  @Test
  public void shouldFailToFindAppropriateConstructor() {
    final PojoClass pojoClass = getPojoClassImplForClass(MultiplePublicAndPrivateWithManyParamsConstructor.class);
      assertThrows(ReflectionException.class, () -> InstanceFactory.getInstance(pojoClass, (new Object[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 })));
  }

  @Test
  public void shouldFailToConstructBasedOnExcpetionalConstructorWithNoParam() {
    final PojoClass pojoClass = getPojoClassImplForClass(AClassWithExceptionalConstructors.class);
      assertThrows(ReflectionException.class, () -> InstanceFactory.getInstance(pojoClass));
  }

  @Test
  public void shouldFailToConstructBasedOnExcpetionalConstructorWithParam() {
    final PojoClass pojoClass = getPojoClassImplForClass(AClassWithExceptionalConstructors.class);
    assertThrows(ReflectionException.class, () -> InstanceFactory.getInstance(pojoClass, "OneStringParam"));
  }

  @Test
  public void shouldCreateInstanceUsingDeclaredPublicConstructor() {
    final PojoClass pojoClass = getPojoClassImplForClass(OnePublicNoParamConstructor.class);
    final Object instance = InstanceFactory.getInstance(pojoClass);
    assertNotNull( instance, String.format("Failed to create a new instance using publicly declared constructor for " +
            "class=[%s]", pojoClass));
  }

  @Test
  public void shouldCreateInstanceUsingDeclaredPrivateConstructor() {
    final PojoClass pojoClass = getPojoClassImplForClass(OnePrivateNoParamsConstructor.class);
    final Object instance = InstanceFactory.getInstance(pojoClass);
    assertNotNull( instance, String.format("Failed to create a new instance using privately declared constructor for " +
            "class=[%s]", pojoClass));
  }

  @Test
  public void shouldCreateInstanceUsingImplicitConstructor() {
    final PojoClass pojoClass = getPojoClassImplForClass(NoDeclaredConstructor.class);
    final Object instance = InstanceFactory.getInstance(pojoClass);

    assertNotNull( instance, String.format("Failed to create a new instance using compiler auto-generated constructor for " +
            "class=[%s]", pojoClass));
  }

  @Test
  public void shouldCreateInstanceOneParameterConstructor() {
    final PojoClass pojoClass = getPojoClassImplForClass(MultiplePublicAndPrivateWithManyParamsConstructor.class);
    final Object instance = InstanceFactory.getInstance(pojoClass, RandomFactory.getRandomValue(String.class));
    assertNotNull(instance, String.format("Failed to create a new instance using single parameter constructor for " +
        "class=[%s]", pojoClass));
  }

  @Test
  public void shouldCreateInstanceOneNullParameterConstructor() {
    final PojoClass pojoClass = getPojoClassImplForClass(MultiplePublicAndPrivateWithManyParamsConstructor.class);
    final Object instance = InstanceFactory.getInstance(pojoClass, (new Object[] { null }));
    assertNotNull(instance, String.format("Failed to create a new instance using single parameter constructor for " +
        "class=[%s]", pojoClass));
  }

  @Test
  public void shouldCreateInstanceMultipleParameterConstructor() {
    final PojoClass pojoClass = getPojoClassImplForClass(MultiplePublicAndPrivateWithManyParamsConstructor.class);
    final Object instance = InstanceFactory.getInstance(pojoClass, RandomFactory.getRandomValue(String.class), RandomFactory
        .getRandomValue(Integer.class));
    assertNotNull(instance, String.format("Failed to create a new instance using multiple parameter constructor for " +
        "class=[%s]", pojoClass));
  }

  @Test
  public void shouldCreateInstanceMultipleParameterPrivateConstructor() {
    final PojoClass pojoClass = getPojoClassImplForClass(MultiplePublicAndPrivateWithManyParamsConstructor.class);
    final Object instance = InstanceFactory.getInstance(pojoClass, RandomFactory.getRandomValue(String.class), RandomFactory
        .getRandomValue(Integer.class), RandomFactory.getRandomValue(Character.class));
    assertNotNull(instance, String.format("Failed to create a new instance using multiple parameter private constructor for " +
        "class=[%s]", pojoClass));
  }

  @Test
  public void testIsNestedClass() {
    final Class<?> nonNestedClass = AClassWithNestedClass.class;
    PojoClass pojoClass = getPojoClassImplForClass(nonNestedClass);
    assertFalse( pojoClass.isNestedClass(), String.format("Non-nested class=[%s] returned true for isNestedClass on PojoClass " +
            "implementation=[%s]", nonNestedClass, pojoClass));

    final Class<?> nestedClass = NestedClass.class;
    pojoClass = getPojoClassImplForClass(nestedClass);
    assertTrue(pojoClass.isNestedClass(), String.format("Nested class=[%s] returned false for isNestedClass on PojoClass implementation=[%s]",
            nestedClass, pojoClass));

    final Class<?> nestedStaticClass = AClassWithNestedClass.NestedStaticClass.class;
    pojoClass = getPojoClassImplForClass(nestedStaticClass);
    assertTrue( pojoClass.isNestedClass(), String.format("Nested class=[%s] returned false for isNestedClass on PojoClass implementation=[%s]",
            nestedClass, pojoClass));
  }

  @Test
  public void testCopy() {
    final AClassWithEquality first = new AClassWithEquality(RandomFactory.getRandomValue(String.class), RandomFactory
        .getRandomValue(Integer.class));

    final AClassWithEquality second = new AClassWithEquality();

      assertNotEquals(first, second, String.format("Class with data=[%s], evaluated equals to one without=[%s]!!", BusinessIdentity.toString
              (first), BusinessIdentity.toString(second)));

    final PojoClass pojoClass = getPojoClassImplForClass(first.getClass());
    pojoClass.copy(first, second);
      assertEquals(first, second, String.format("Class=[%s] copied to=[%s] and still equals returned false using PojoClass" + " " +
              "implementation=[%s]!!", BusinessIdentity.toString(first), BusinessIdentity.toString(second), pojoClass));
  }

  @Test
  public void shouldGetEmptyListForGetInterfaces() {
    final PojoClass pojoClass = getPojoClassImplForClass(AClassWithoutInterfaces.class);
    assertNotNull(pojoClass.getInterfaces(), String.format("Expected empty list no null for getInterfaces() on [%s]?", pojoClass));
    assertEquals( 0, pojoClass.getInterfaces().size(), String.format("Interfaces added to [%s]?", pojoClass));
  }

  @Test
  public void shouldGetInterfaces() {
    final PojoClass pojoClass = getPojoClassImplForClass(AClassWithInterfaces.class);
    assertEquals( 2, pojoClass.getInterfaces().size(), String.format("Interfaces added/removed from [%s]?", pojoClass));

    final List<Class<?>> expectedInterfaces = new LinkedList<Class<?>>();
    expectedInterfaces.add(FirstInterfaceForAClassWithInterfaces.class);
    expectedInterfaces.add(SecondInterfaceForAClassWithInterfaces.class);
    for (final PojoClass pojoInterface : pojoClass.getInterfaces()) {
      assertTrue(expectedInterfaces.contains(pojoInterface.getClazz()), String.format("Expected interfaces [%s] not found, instead found [%s]", expectedInterfaces,
              pojoInterface.getClazz()));
    }
  }

  @Test
  public void testGetClazz() {
    final Class<?> clazz = this.getClass();
    final PojoClass pojoClass = getPojoClassImplForClass(clazz);
    assertTrue(clazz.equals(pojoClass.getClazz()),String.format("PojoClass parsing for [%s] returned different class=[%s] in getClazz() call" + " for " +
            "PojoClass implementation=[%s]", clazz, pojoClass.getClazz(), pojoClass));
  }

  @Test
  public void testEqualityAndHashCodeBasedOnIdentityNotInstance() {
    final PojoClass first = getPojoClassImplForClass(this.getClass());
    final PojoClass second = getPojoClassImplForClass(this.getClass());
    assertEquals(first, second, "PojoClassImpl equals is instance based!! Should be business equality based.");
    assertEquals( first.hashCode(), second.hashCode(), "PojoClassImpl hashCode is instance based!! Should be business equality based.");
  }

  @Test
  @SuppressWarnings("ObjectEqualsNull")
  public void testEqualsReturnsFalseWhenOtherIsNull() {
    final PojoClass pojoClass = getPojoClassImplForClass(this.getClass());
    assertFalse( pojoClass.equals(null), "equals(null) should return false");
  }

  @Test
  public void testEqualsReturnsFalseWhenOtherIsDifferentClass() {
    final PojoClass pojoClass = getPojoClassImplForClass(this.getClass());
    assertFalse( pojoClass.equals(new Object()), "equals(differentClass) should return false");
  }

  @Test
  public void testIsArray() {
    final Object[] objectArray = new Object[] { new Object() };
    final PojoClass objectArrayPojoClass = getPojoClassImplForClass(objectArray.getClass());
    assertTrue(objectArrayPojoClass.isArray(), String.format("PojoClassImpl isArray() failed on array[%s]", objectArray));
    assertTrue(objectArrayPojoClass.isAbstract(), String.format("Array should return true on isAbstract for array [%s]! Did Java underlying " +
            "implementation change?", objectArray));
  }

  @Test
  public void testIsStatic() {
    PojoClass pojoClass = getPojoClassImplForClass(AClassWithTwoChildClassesOneStaticAndOneNot.APublicNonStaticClass.class);
    assertTrue(pojoClass.isNestedClass(), "Nested class not detected nested");
    assertFalse(pojoClass.isStatic(), "Nested non-static nested class detected as static");

    pojoClass = getPojoClassImplForClass(AClassWithTwoChildClassesOneStaticAndOneNot.AStaticClass.class);
    assertTrue(pojoClass.isNestedClass(), "Nested class not detected nested");
    assertTrue(pojoClass.isStatic(), "Nested static class not seen as static");
  }

  @Test
  public void testGetPath() {
    PojoClass pojoClass = getPojoClassImplForClass(this.getClass());

    String sourcePath = pojoClass.getSourcePath();
    assertTrue(sourcePath.startsWith("file://"), "Should start with file:// [" + sourcePath + "]");

    String thisClassEndingPath = this.getClass().getName().replace(Java.PACKAGE_DELIMITER, Java.PATH_DELIMITER) + Java.CLASS_EXTENSION;
    assertTrue(sourcePath.endsWith(thisClassEndingPath), "Should end with this class's package path [" + sourcePath + "]");
  }

  @Test
  public void testGetPackage() {
    PojoClass pojoClass = getPojoClassImplForClass(this.getClass());

    PojoPackage pojoPackage = pojoClass.getPackage();
    assertNotNull(pojoPackage, "Null package received");
    assertEquals(this.getClass().getPackage().getName(), pojoPackage.getName(), "Invalid package retrieved");
  }

  @Test
  public void isPublicClass() {
    PojoClass pojoclass = getClass(SAMPLE_CLASSES_PKG + ".AccessibilityClass$PublicClass");
    assertNotNull(pojoclass, "class not found");

    assertTrue( pojoclass.isPublic(), "isPublic() check on class=[" + pojoclass + "] returned false!!");
    assertFalse( pojoclass.isProtected(), "isProtected() check on class=[" + pojoclass + "] returned true!!");
    assertFalse( pojoclass.isPrivate(), "isPrivate() check on class=[" + pojoclass + "] returned true!!");
    assertFalse( pojoclass.isPackagePrivate(), "isPackagePrivate() check on class=[" + pojoclass + "] returned true!!");
  }

  @Test
  public void isProtectedClass() {
    PojoClass pojoclass = getClass(SAMPLE_CLASSES_PKG + ".AccessibilityClass$ProtectedClass");
    assertNotNull(pojoclass, "class not found");

    assertTrue( pojoclass.isProtected(), "isProtected() check on class=[" + pojoclass + "] returned false!!");
    assertFalse( pojoclass.isPrivate(), "isPrivate() check on class=[" + pojoclass + "] returned true!!");
    assertFalse(pojoclass.isPackagePrivate(), "isPackagePrivate() check on class=[" + pojoclass + "] returned true!!");
    assertFalse( pojoclass.isPublic(), "isPublic() check on class=[" + pojoclass + "] returned true!!");
  }

  @Test
  public void isPrivateClass() {
    PojoClass pojoclass = getClass(SAMPLE_CLASSES_PKG + ".AccessibilityClass$PrivateClass");
    assertNotNull(pojoclass,"class not found");

    assertTrue( pojoclass.isPrivate(), "isPrivate() check on class=[" + pojoclass + "] returned true!!");
    assertFalse(pojoclass.isPackagePrivate(), "isPackagePrivate() check on class=[" + pojoclass + "] returned true!!");
    assertFalse(pojoclass.isProtected(), "isProtected() check on class=[" + pojoclass + "] returned true!!");
    assertFalse( pojoclass.isPublic(), "isPublic() check on class=[" + pojoclass + "] returned true!!");
  }

  @Test
  public void isPackagePrivateClass() {
    PojoClass pojoclass = getClass(SAMPLE_CLASSES_PKG + ".AccessibilityClass$PackagePrivateClass");
    assertNotNull(pojoclass,"class not found");

    assertTrue( pojoclass.isPackagePrivate(), "isPackagePrivate() check on class=[" + pojoclass + "] returned false!!");
    assertFalse( pojoclass.isPrivate(), "isPrivate() check on class=[" + pojoclass + "] returned true!!");
    assertFalse( pojoclass.isProtected(),"isProtected() check on class=[" + pojoclass + "] returned true!!");
    assertFalse(pojoclass.isPublic(), "isPublic() check on class=[" + pojoclass + "] returned true!!");  }

  private PojoClass getClass(String name) {
    List<PojoClass> pojoClasses = PojoClassFactory.getPojoClasses(SAMPLE_CLASSES_PKG);
    for (PojoClass pojoClass : pojoClasses) {
      if (pojoClass.getName().equals(name))
        return pojoClass;
    }
    throw ReflectionException.getInstance("Request class not found! [" + name + "]");
  }

  @Test
  public void shouldReturnNullForNonEnclosedClass() {
    PojoClass aClassWithNested = PojoClassFactory.getPojoClass(AClassWithNestedClass.class);
    assertFalse(aClassWithNested.isNestedClass(), "Class should not be nested");
    assertNull( aClassWithNested.getEnclosingClass(), "Should not have any enclosing classes");
  }

  @Test
  public void canGetEnclosingClass() {
    PojoClass aClassWithNested = PojoClassFactory.getPojoClass(AClassWithNestedClass.class);
    PojoClass nestedClass = PojoClassFactory.getPojoClass(AClassWithNestedClass.NestedClass.class);
    assertTrue(nestedClass.isNestedClass(), "Class should be nested");

    PojoClass enclosingClass = nestedClass.getEnclosingClass();
    assertNotNull( enclosingClass, "Enclosing should not be null");
    assertTrue(enclosingClass.getClazz().equals(aClassWithNested.getClazz()), "Invalid enclosing class");
  }

  private static PojoClass getPojoClassImplForClass(final Class<?> clazz) {
    return PojoClassFactory.getPojoClass(clazz);
  }
}
