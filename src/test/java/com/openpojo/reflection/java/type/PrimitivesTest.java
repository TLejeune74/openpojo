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

package com.openpojo.reflection.java.type;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class PrimitivesTest {

  @Test
  public void shouldHavePrivateConstructor() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(Primitives.class);
    assertEquals( 1, pojoClass.getPojoConstructors().size(), "Should only have one constructor");
    assertTrue( pojoClass.getPojoConstructors().getFirst().isPrivate(), "Constructor must be private");
  }

  @Test
  public void shouldReturnSameInstanceWhenGetInstance() {
    Primitives first = Primitives.getInstance();
    Primitives second = Primitives.getInstance();
    assertNotNull( first, "Should return an instance");
    assertNotNull(second, "Should return an instance");
    assertTrue( first == second, "Should have been the exact same instance");
  }

  @Test
  public void shouldReturnNullNotPrimitive() {
    Class<?> anyClass = Object.class;
    assertEquals( anyClass, Primitives.getInstance().autoBox(anyClass), "Should have been the same class");
  }

  @Test
  public void shouldConvertPrimitiveToWrappedClass() {
    checkPrimitiveCorrectlyWrapped(Boolean.TYPE, Boolean.class);
    checkPrimitiveCorrectlyWrapped(Byte.TYPE, Byte.class);
    checkPrimitiveCorrectlyWrapped(Character.TYPE, Character.class);
    checkPrimitiveCorrectlyWrapped(Double.TYPE, Double.class);
    checkPrimitiveCorrectlyWrapped(Float.TYPE, Float.class);
    checkPrimitiveCorrectlyWrapped(Integer.TYPE, Integer.class);
    checkPrimitiveCorrectlyWrapped(Long.TYPE, Long.class);
    checkPrimitiveCorrectlyWrapped(Short.TYPE, Short.class);
    checkPrimitiveCorrectlyWrapped(Void.TYPE, Void.class);

  }

  private void checkPrimitiveCorrectlyWrapped(Class<?> primitive, Class<?> expected) {
    Primitives instance = Primitives.getInstance();
    assertEquals(expected, instance.autoBox(primitive), "Should wrap primitive");
  }
}
