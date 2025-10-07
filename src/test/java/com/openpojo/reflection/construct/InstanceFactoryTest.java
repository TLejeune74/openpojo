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

package com.openpojo.reflection.construct;

import java.util.List;

import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.construct.sampleclasses.*;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.validation.affirm.Affirm;
import org.junit.jupiter.api.Test;

import static com.openpojo.reflection.impl.PojoClassFactory.getPojoClass;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class InstanceFactoryTest {

  @Test
  @SuppressWarnings("RedundantArrayCreation")
  public void shouldCreateUsingDefaultConstructor() {
    final Class<?> clazz = ClassWithNoDeclaredConstructor.class;
    final Object obj1 = getInstance(clazz, (Object[]) null);
    Affirm.affirmNotNull(obj1, "Should have created an object");

    final Object obj2 = getInstance(clazz, new Object[] {});
    Affirm.affirmTrue(obj1 != obj2, "Should have created a different object");
  }

  @Test
  public void shouldCreateUsingSingleParameterConstructor() {
    final Class<?> clazz = ClassWithVariousDeclaredContructorsAndMethods.class;
    final String stringParam = RandomFactory.getRandomValue(String.class);
    final ClassWithVariousDeclaredContructorsAndMethods obj1 =
        (ClassWithVariousDeclaredContructorsAndMethods) getInstance(clazz, stringParam);
    Affirm.affirmNotNull(obj1, "Should have created using String constructor");
    Affirm.affirmEquals(stringParam, obj1.singleStringConstructor, "Incorrect constructor used");
  }

  @Test
  public void shouldCreateUsingNullParameterDoubleConstructor() {
    final Class<?> clazz = ClassWithVariousDeclaredContructorsAndMethods.class;
    final String stringParam = RandomFactory.getRandomValue(String.class);
    final ClassWithVariousDeclaredContructorsAndMethods obj =
        (ClassWithVariousDeclaredContructorsAndMethods) getInstance(clazz, stringParam, null);

    Affirm.affirmNotNull(obj, "Should have created using two parameter constructor");
    Affirm.affirmNull(obj.doubleIntegerConstructor, "Should have called using two parameter constructor");
    Affirm.affirmEquals(stringParam, obj.doubleStringConstructor, "Should have called using two parameter constructor");
  }

  @Test(expected = ReflectionException.class)
  public void shouldFailtoCreateParametersMissmatch() {
    final Class<?> clazz = ClassWithVariousDeclaredContructorsAndMethods.class;
    final String stringParam = RandomFactory.getRandomValue(String.class);
    getInstance(clazz, stringParam, stringParam);
    Affirm.fail("Should've failed to create");
  }

  @Test
  public void shouldFailtoCreateUsingDefault() {
      assertThrows(ReflectionException.class, ()-> {
          final Class<?> clazz = ClassWithNoDeclaredConstructor.class;
          final String stringParam = RandomFactory.getRandomValue(String.class);
          getInstance(clazz, stringParam);
          Affirm.fail("Should've failed to create");
      });
  }

  private Object getInstance(final Class<?> clazz, final Object... parameters) {
    final PojoClass pojoClass = getPojoClass(clazz);
    return InstanceFactory.getInstance(pojoClass, parameters);
  }

  @Test
  public void shouldConstructUsingMinimalParameterCount() {
    final PojoClass pojoClass = getPojoClass(ClassWithLessThanGreaterThanConstructors.class);
    final ClassWithLessThanGreaterThanConstructors instance =
        (ClassWithLessThanGreaterThanConstructors) InstanceFactory.getLeastCompleteInstance(pojoClass);
    Affirm.affirmEquals(1, instance.getParameterCountUsedForConstruction(), "Should've used constructor with single Parameter");
  }

  @Test
  public void shouldConstructUsingMaximumParameterCount() {
    final PojoClass pojoClass = getPojoClass(ClassWithLessThanGreaterThanConstructors.class);
    final ClassWithLessThanGreaterThanConstructors instance =
        (ClassWithLessThanGreaterThanConstructors) InstanceFactory.getMostCompleteInstance(pojoClass);
    Affirm.affirmEquals(3, instance.getParameterCountUsedForConstruction(), "Should've used constructor with single Parameter");
  }

  @Test
  public void shouldConstructUsingNativeParams() {
    InstanceFactory.getMostCompleteInstance(getPojoClass(ClassWithNativeTypesConstructor.class));
  }

  @Test
  public void shouldFailToConstruct() {
    assertThrows(ReflectionException.class, ()-> InstanceFactory.getInstance(getPojoClass(SomeEnum.class)));
  }

  @Test
  public void shouldFailToConstructUsingLeastCompleteInstance() {
      assertThrows(ReflectionException.class, ()-> InstanceFactory.getLeastCompleteInstance(getPojoClass(SomeEnum.class)));
  }

  @Test
  public void shouldFailToConstructUsingMostCompleteInstance() {
      assertThrows(ReflectionException.class, ()-> InstanceFactory.getMostCompleteInstance(getPojoClass(SomeEnum.class)));
  }

  @Test
  public void shouldConstructBasedOnDerivedClass() {
    final PojoClass aClassWithInterfaceBasedConstructor = getPojoClass(ClassWithInterfaceBasedConstructor.class);
    assertNotNull(InstanceFactory.getInstance(aClassWithInterfaceBasedConstructor, "SomeString"));
  }

  @Test
  public void shouldSkipSyntheticConstructor() {
    final PojoClass classWithStaticConstructorPojo = getPojoClass(ClassWithSyntheticConstructor.class);
    assertNotNull(InstanceFactory.getMostCompleteInstance(classWithStaticConstructorPojo));
  }

  @Test
  public void shouldConstructAClassWithGenericConstructor() {
    final PojoClass pojoClass = getPojoClass(AClassWithGenericConstructor.class);
    AClassWithGenericConstructor aClassWithGenericConstructor = (AClassWithGenericConstructor) InstanceFactory
        .getLeastCompleteInstance(pojoClass);
    assertNotNull(aClassWithGenericConstructor);

    List<AClassWithGenericConstructor.Child> children = aClassWithGenericConstructor.getMyChildren();

    assertTrue(children.size() > 0);

    for (AClassWithGenericConstructor.Child child : children) {
      assertNotNull(child);
      assertNotNull(child.getName());
    }
  }

  @Test
  public void shouldInitializeBusinessKeys() {
    final PojoClass pojoClass = getPojoClass(AClassWithOneBusinessKey.class);
    AClassWithOneBusinessKey classWithOneBusinessKey = (AClassWithOneBusinessKey) InstanceFactory.getInstance(pojoClass);
    assertNotNull(classWithOneBusinessKey.getName());
  }

  @Test
  public void shouldNotUpdateBusinessKeysIfTheyAreNotNull() {
    final PojoClass pojoClass = getPojoClass(AClassWithFinalBusinessKey.class);
    AClassWithFinalBusinessKey instance = (AClassWithFinalBusinessKey) InstanceFactory.getLeastCompleteInstance(pojoClass);
    assertEquals(instance.getFirstValueForName(), instance.getName(), "Name was modified post construction");
  }

  @Test
  public void shouldSetPrimitiveBusinessKeys() {
    final PojoClass pojoClass = getPojoClass(AClassWithPrimitiveBusinessKey.class);
    AClassWithPrimitiveBusinessKey instance = (AClassWithPrimitiveBusinessKey) InstanceFactory.getLeastCompleteInstance(pojoClass);
    if (instance.getSomeInt() == 0) // Random chance - try again
      instance = (AClassWithPrimitiveBusinessKey) InstanceFactory.getLeastCompleteInstance(pojoClass);
    assertTrue(instance.getSomeInt() != 0,"Primitive value unchanged for BusinessKey");

  }

  @Test
  public void shouldCreateObjectWithMultipleTypeVariableTypes() {
    final PojoClass pojoClass = getPojoClass(AClassWithMultipleTypeVariablesGenericConstructor.class);
    AClassWithMultipleTypeVariablesGenericConstructor instance =
        (AClassWithMultipleTypeVariablesGenericConstructor) InstanceFactory.getMostCompleteInstance(pojoClass);

    assertNotNull(instance);

    assertNotNull(instance.getMyV());
    assertEquals(instance.getMyV().getClass(), Object.class);
    assertNotNull(instance.getMyK());
    assertEquals(instance.getMyK().getClass(), Object.class);

    assertNotNull(instance.getMyT());
    assertTrue(CharSequence.class.isAssignableFrom(instance.getMyT().getClass()));

  }
}
