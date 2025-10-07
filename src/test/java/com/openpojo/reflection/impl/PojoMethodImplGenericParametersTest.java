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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.PojoParameter;
import com.openpojo.reflection.impl.sample.classes.AClassWithGenericParameterConstructor;
import com.openpojo.reflection.impl.sample.classes.AClassWithGenericParameterMethod;
import com.openpojo.reflection.impl.sample.classes.AClassWithNestedClass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class PojoMethodImplGenericParametersTest {

  @Test
  public void shouldGetConstructorWithGenericParameter() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AClassWithGenericParameterConstructor.class);
    List<PojoMethod> constructors = pojoClass.getPojoConstructors();
    assertEquals(1, constructors.size(), pojoClass.getName() + " should have only one generic parameterized constructor");

    shouldHaveOneParameterizedParameter(constructors, String.class);
  }

  private void shouldHaveOneParameterizedParameter(List<PojoMethod> methods, Class expectedParameter) {
    PojoMethod method = methods.get(0);
    Type types[] = method.getGenericParameterTypes();

    assertEquals(1, types.length, method.getName() + " should have only one parameter");

    Type type = types[0];

    assertTrue(type instanceof ParameterizedType, method.getName() + " parameter must be of type ParamaterizedType");

    ParameterizedType parameterizedType = (ParameterizedType) type;

    Type actualParameterTypes[] = parameterizedType.getActualTypeArguments();

    assertEquals(1, actualParameterTypes.length, method.getName() + " parameterizedType should have only one type binding");

    assertEquals(expectedParameter, parameterizedType.getActualTypeArguments()[0], parameterizedType.toString() + " must be parameterized with String");
  }

  @Test
  public void shouldGetMethodWithGenericParameter() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AClassWithGenericParameterMethod.class);
    List<PojoMethod> allMethodsAndConstructors = pojoClass.getPojoMethods();

    List<PojoMethod> methods = new LinkedList<PojoMethod>();
    for (PojoMethod method : allMethodsAndConstructors) {
      if (method.isConstructor())
        continue;
      methods.add(method);
    }

    assertEquals( 1, methods.size(), pojoClass.getName() + " should have only one generic parameterized method");

    shouldHaveOneParameterizedParameter(methods, Integer.class);

  }

  @Test
  public void shouldHaveOneParameterForInstanceNestedClassWhenNoneDeclared() {
    PojoClass pojoclass = PojoClassFactory.getPojoClass(AClassWithNestedClass.NestedClass.class);
    List<PojoMethod> pojoConstructors = pojoclass.getPojoConstructors();
    assertEquals( 1, pojoConstructors.size(), "Should have only one constructor");
    PojoMethod constructor = pojoConstructors.get(0);
    List<PojoParameter> pojoParameters = constructor.getPojoParameters();
    assertEquals(1, pojoParameters.size(), "Should have 1 parameter");
    assertFalse(pojoParameters.get(0).isParameterized(), "Should be nonParameterized parameter");
    assertEquals(constructor.getParameterTypes()[0], pojoclass.getEnclosingClass().getClazz(), "Should be enclosing type");
  }

  @Test
  public void shouldHaveTwoParametersWithWhenOneParameterDeclared() {
    PojoClass pojoclass = PojoClassFactory.getPojoClass(AClassWithNestedClass.NestedClassWithOneParamConstructor.class);
    List<PojoMethod> pojoConstructors = pojoclass.getPojoConstructors();
    assertEquals( 1, pojoConstructors.size(), "Should have only one constructor");
    PojoMethod constructor = pojoConstructors.get(0);
    List<PojoParameter> pojoParameters = constructor.getPojoParameters();
    assertEquals( 2, pojoParameters.size(), "Should have 2 parameter");
    assertFalse( pojoParameters.get(0).isParameterized(), "Should be nonParameterized parameter");
    assertEquals( constructor.getParameterTypes()[0], pojoclass.getEnclosingClass().getClazz(), "Should be enclosing type");
    assertEquals(constructor.getParameterTypes()[1], int.class, "Should be int type");
  }

  @Test
  public void shouldBeAbleToConstructNestedChildWithNoParameters() {
    PojoClass pojoclass = PojoClassFactory.getPojoClass(AClassWithNestedClass.NestedClass.class);
    Object instance = RandomFactory.getRandomValue(pojoclass.getClazz());
    assertEquals(pojoclass.getClazz(), instance.getClass(), "Should be same type");
  }

  @Test
  public void shouldBeAbletoConstructNestedChileWithOneParameter() {
    PojoClass pojoclass = PojoClassFactory.getPojoClass(AClassWithNestedClass.NestedClassWithOneParamConstructor.class);
    Object instance = RandomFactory.getRandomValue(pojoclass.getClazz());
    assertEquals( pojoclass.getClazz(), instance.getClass(), "Should be same type");
  }

  @Test
  public void shouldBeAbletoConstructNestedChileWithOneGenericParameter() {
    PojoClass pojoclass = PojoClassFactory.getPojoClass(AClassWithNestedClass.NestedClassWithOneGenericParamConstructor.class);
    Object instance = RandomFactory.getRandomValue(pojoclass.getClazz());
    assertEquals(pojoclass.getClazz(), instance.getClass(), "Should be same type");
  }
}
