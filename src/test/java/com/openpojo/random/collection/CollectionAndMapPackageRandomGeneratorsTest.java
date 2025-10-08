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

package com.openpojo.random.collection;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.LoggerFactory;
import com.openpojo.random.RandomGenerator;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.reflection.construct.InstanceFactory;
import com.openpojo.reflection.filters.FilterChain;
import com.openpojo.reflection.filters.FilterNestedClasses;
import com.openpojo.reflection.filters.FilterNonConcrete;
import com.openpojo.reflection.impl.PojoClassFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionAndMapPackageRandomGeneratorsTest {
  private static final List<PojoClass> collectionRandomGenerators = new LinkedList<>();
  private static final String[] packages = new String[] { "com.openpojo.random.collection", "com.openpojo.random.map" };
  private static final int EXPECTED_COUNT = 54;

  @BeforeAll
  public static void setup() {
    for (final String pkg : packages) {
      collectionRandomGenerators.addAll(PojoClassFactory.getPojoClassesRecursively(pkg,
          new FilterChain(
              new RandomGeneratorFilter(),
              new FilterNestedClasses(),
              new FilterNonConcrete())));
    }
    assertEquals( EXPECTED_COUNT, collectionRandomGenerators.size(),
            String.format("Invalid number of Collection/Map RandomGenerators added/removed? " +
                    "expected: " + "[%s], found: [%s] which were [%s] ", EXPECTED_COUNT, collectionRandomGenerators.size(),
            collectionRandomGenerators));
  }

  /**
   * This test will test every random generator against its declared types, for every type returned from getTypes.
   */
  @Test
  public void shouldReturnRandomInstanceForDeclaredType() {
    for (final PojoClass randomGeneratorPojoClass : collectionRandomGenerators) {
      final RandomGenerator randomGenerator = (RandomGenerator) InstanceFactory.getInstance(randomGeneratorPojoClass);
      final Collection<Class<?>> generatorTypes = randomGenerator.getTypes();
      for (final Class<?> type : generatorTypes) {
        LoggerFactory.getLogger(this.getClass()).debug("Generating Type [" + type + "]");
        Object firstInstance = randomGenerator.doGenerate(type);
        assertNotNull(firstInstance, String.format("[%s] returned null for type [%s]",randomGenerator.getClass(), type));

        assertTrue(type.isAssignableFrom(firstInstance.getClass()),
                String.format("[%s] returned incompatible type [%s] when requesting type [%s]",
                randomGenerator.getClass(), firstInstance.getClass(), type));

        int counter = 10;
        Object secondInstance = null;
        while (counter > 0) {
          firstInstance = randomGenerator.doGenerate(type);
          secondInstance = randomGenerator.doGenerate(type);
          if (!firstInstance.equals(secondInstance)) {
            break;
          }
          counter--;
        }

        assertFalse( firstInstance.equals(secondInstance),
                String.format("[%s] returned identical instances for type [%s]",randomGenerator.getClass(), type));

      }
    }
  }

  private static class RandomGeneratorFilter implements PojoClassFilter {
    public boolean include(final PojoClass pojoClass) {
      return pojoClass.extendz(RandomGenerator.class);
    }
  }
}
