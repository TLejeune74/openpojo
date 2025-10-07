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

package com.openpojo.reflection.java.bytecode;

import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.reflection.java.bytecode.sample.*;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.openpojo.reflection.construct.InstanceFactory.getInstance;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class ByteCodeFactoryTest {

  @Test
  public void shouldNotBeAbleToCreateInstance() {
    assertThrows(ReflectionException.class, () ->getInstance(getPojoClass(ByteCodeFactory.class)));
  }

  @Test
  public void givenNullShouldReturnNull() {
    assertNull(getSubClass((Class<?>) null));
  }

  @Test
  public void givenAnInterfaceShouldReturnNull() {
    assertNull(getSubClass(AnInterface.class));
  }

  @Test
  public void givenAnEnumShouldReturnNull() {
    assertNull(getSubClass(AnEnum.class));
  }

  @Test
  public void givenPrimitiveShouldReturnNull() {
    assertNull(getSubClass(int.class));
    assertNull(getSubClass(char.class));
    assertNull(getSubClass(float.class));
    assertNull(getSubClass(long.class));
    assertNull(getSubClass(short.class));
    assertNull(getSubClass(byte.class));
    assertNull(getSubClass(boolean.class));
    assertNull(getSubClass(double.class));
  }

  @Test
  public void givenAnArrayShouldReturnNull() {
    assertNull(getSubClass(int[].class));
  }

  @Test
  public void givenAnEmptyAbstractClassShouldReturnImplementationClass() {
    Class<?> clazz = AnAbstractClassWithNoMethods.class;
    Class<?> subClassPojoClass = getSubClass(clazz);
    assertNotNull(subClassPojoClass);
    assertIsSubclass(clazz, subClassPojoClass);
  }

  @Test
  public void givenAFinalClassShouldReturnNull() {
    assertNull(getSubClass(AFinalClass.class));
  }

  @Test
  public void givenAnAbstractClassWithAnAbstractMethodShouldReturnAnInstance() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AnAbstractClassWithOneAbstraceMethod.class);
    assertEquals(2, pojoClass.getPojoMethods().size(), "Should have 1 constructor and 1 abstract method");

    Class<?> subclass = getSubClass(pojoClass.getClazz());
    assertNotNull(subclass);
    assertIsSubclass(pojoClass.getClazz(), subclass);

  }

  @Test
  public void givenAnAbstractClassWithConstructorShouldConstruct() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AnAbstractClassWithConstructor.class);
    Class<?> subClass = getSubClass(pojoClass.getClazz());

    assertIsConcreteAndConstructable(subClass);
  }

  @Test
  public void givenAnAbstractClassWithGenericConstructorShouldConstruct() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AnAbstractClassWithGenericConstructor.class);
    Class<?> subClass = getSubClass(pojoClass.getClazz());

    assertIsConcreteAndConstructable(subClass);
  }

  @Test
  public void givenTheSameClassShouldReturnSameGeneratedClass() {
    Class<?> clazz = AnAbstractClassWithNoMethods.class;

    Class<?> subClass1 = getSubClass(clazz);
    assertIsSubclass(clazz, subClass1);

    Class<?> subClass2 = getSubClass(clazz);
    assertIsSubclass(clazz, subClass2);

    assertEquals(subClass1, subClass2, "Should generate the same subclass");
  }

  @Test
  public void onlyValidConstructorsOverwritten() {
    Class<?> clazz = AnAbstractClassWithProtectedMethodBeforeConstructor.class;
    Class<?> subClass1 = getSubClass(clazz);

    assertNotNull(getInstance(PojoClassFactory.getPojoClass(subClass1)));
  }

  @Test
  public void endToEndTest() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(ACompleteAbstractClass.class);

    Validator pojoValidator = ValidatorBuilder.create()
        .with(new GetterMustExistRule())
        .with(new SetterMustExistRule())
        .with(new GetterTester())
        .with(new SetterTester())
        .build();

    pojoValidator.validate(pojoClass);
  }

  private void assertIsConcreteAndConstructable(Class<?> subClass) {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(subClass);
    assertTrue(pojoClass.isConcrete(), "Should be a concrete class");
    Object instance = RandomFactory.getRandomValue(subClass);
    assertNotNull(instance);
  }

  private void assertIsSubclass(Class<?> expected, Class<?> subClass) {
    assertEquals(expected, subClass.getSuperclass(), "Should have returned a sub-class");
  }

  private void assertNotNull(Object instance) {
      Assertions.assertNotNull(instance, "Should have returned an instance");
  }

  private Class<?> getSubClass(Class<?> clazz) {
    return ByteCodeFactory.getSubClass(clazz);
  }

  private PojoClass getPojoClass(Class<?> clazz) {
    return PojoClassFactory.getPojoClass(clazz);
  }
}
