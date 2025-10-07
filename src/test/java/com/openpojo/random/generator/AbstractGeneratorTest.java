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

package com.openpojo.random.generator;

import java.util.Collection;
import java.util.List;

import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.java.load.ClassUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * @author oshoukry
 */
public abstract class AbstractGeneratorTest {

  protected abstract PojoClass getPojoClass();

  protected abstract String getTypeName();

  protected abstract RandomGenerator getRandomGenerator();

  @BeforeEach
  public void before() {
    assumeTrue(getTypeName() != null);
  }

  @Test
  public void singlePrivateConstructor() {
    List<PojoMethod> constructors = getPojoClass().getPojoConstructors();
    assertEquals(1, constructors.size(), "Should have only one constructor");
    assertTrue(constructors.get(0).isPrivate(), "Constructor should be private");
  }

  @Test
  public void canConstruct() {
    assertNotNull(getRandomGenerator(),"Should be able to construct");
  }

  private void assumeClassIsLoaded() {
    assumeTrue(ClassUtil.isClassLoaded(getTypeName()));
  }

  @Test
  public void whenGetTypesShouldReturnExpectedType() {
    assumeClassIsLoaded();

    Collection<Class<?>> types = getRandomGenerator().getTypes();
    assertEquals(1, types.size(), "Should only declare one type");
    assertEquals(getTypeName(), types.iterator().next().getName());
  }

  @Test
  public void whenDoGenerateReturnsDifferentInstances() {
    assumeClassIsLoaded();

    Object first = getRandomGenerator().doGenerate(null);
    assertNotNull(first, "First should not be null");

    Object second = getRandomGenerator().doGenerate(null);
    assertNotNull(second, "Second should not be null");

    Class<?> expectedClass = ClassUtil.loadClass(getTypeName());
    assertTrue(expectedClass.isAssignableFrom(first.getClass()), "Expected an instance assignable from [" + expectedClass + "] but was [" + first.getClass() + "]");
    assertTrue(expectedClass.isAssignableFrom(second.getClass()), "Expected an instance assignable from [" + expectedClass + "] but was [" + second.getClass() + "]");

    if (first.equals(second)) // by chance same object, try one more time.
      second = getRandomGenerator().doGenerate(null);

    assertNotEquals(first, second);
  }

  @Test
  public void end2end() {
    assumeClassIsLoaded();

    Class<?> type = ClassUtil.loadClass(getTypeName());
    Object instance = RandomFactory.getRandomValue(type);
    assertNotNull(instance, "Should not generated null");
    assertTrue(type.isAssignableFrom(instance.getClass()), "Should generate compatible type");
  }
}
