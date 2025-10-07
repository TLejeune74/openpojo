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

package com.openpojo.random.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author oshoukry
 */
public class DefaultRandomGeneratorServiceTest {
  private DefaultRandomGeneratorService defaultRandomGeneratorService;

  @BeforeEach
  public void setup() {
    defaultRandomGeneratorService = new DefaultRandomGeneratorService();
  }

  @Test
  public void shouldReturnNameBasedOnClassName() {
    assertEquals(DefaultRandomGeneratorService.class.getName(),
        defaultRandomGeneratorService.getName(), "Name returned doesn't match class name");
  }

  @Test
  public void defaultRandomGeneratorMustNotBeInitialized() {
    assertNull( defaultRandomGeneratorService.getDefaultRandomGenerator(), String.format("defaultRandomGenerator must be initialized to null for [%s]",
            defaultRandomGeneratorService));
  }

  @Test
  public void shouldSetAndGetDefaultRandomGenerator() {
    final RandomGenerator randomGenerator = RandomFactory.getRandomValue(RandomGenerator.class);
    defaultRandomGeneratorService.setDefaultRandomGenerator(randomGenerator);

    assertEquals(randomGenerator,
        defaultRandomGeneratorService.getDefaultRandomGenerator(), "Setter & Getter must match passed in value");
  }

  @Test
  public void shouldGetTypeBasedOnRegisteredRandomGenerator() {
    final Class<?> type = DefaultRandomGeneratorServiceTest.class;

    assertNull(defaultRandomGeneratorService.getRandomGeneratorByType(type), "Should not have received a valid random generator for non registered type");

    final DummyRandomGenerator dummyRandomGenerator = new DummyRandomGenerator();
    dummyRandomGenerator.setTypes(new Class<?>[] { type });
    defaultRandomGeneratorService.registerRandomGenerator(dummyRandomGenerator);

    assertEquals(dummyRandomGenerator,
        defaultRandomGeneratorService.getRandomGeneratorByType(type), "Incorrect random generator returned");

  }

  @Test
  public void shouldGetRandomGeneratorBasedOnAssignability() {
    final Class<?> type = LinkedList.class;

    final DummyRandomGenerator dummyRandomGenerator = new DummyRandomGenerator();
    dummyRandomGenerator.setTypes(new Class<?>[] { type });
    defaultRandomGeneratorService.registerRandomGenerator(dummyRandomGenerator);

    defaultRandomGeneratorService.getRandomGeneratorByType(List.class).doGenerate(List.class);

    assertEquals( 1, dummyRandomGenerator.getCounter(), "Incorrect random generator returned (doGenerate should've incremented call count)");
  }

  @Test
  public void shouldRandomizeWhichRandomGeneratorReturnedBasedOnAssignability() {
    final Class<?> type = LinkedList.class;
    final Class<?> anotherType = ArrayList.class;

    final DummyRandomGenerator dummyRandomGenerator = new DummyRandomGenerator();
    dummyRandomGenerator.setTypes(new Class<?>[] { type });
    defaultRandomGeneratorService.registerRandomGenerator(dummyRandomGenerator);

    final DummyRandomGenerator anotherRandomGenerator = new DummyRandomGenerator();
    anotherRandomGenerator.setTypes(new Class<?>[] { anotherType });
    defaultRandomGeneratorService.registerRandomGenerator(anotherRandomGenerator);

    final List<DummyRandomGenerator> expectedRandomGenerators = new LinkedList<DummyRandomGenerator>();
    expectedRandomGenerators.add(dummyRandomGenerator);
    expectedRandomGenerators.add(anotherRandomGenerator);

    int tolerance = 20;

    while (tolerance > 0 && !expectedRandomGenerators.isEmpty()) {
      defaultRandomGeneratorService.getRandomGeneratorByType(List.class).doGenerate(List.class);
      for (int index = 0; index < expectedRandomGenerators.size(); index++) {
        final DummyRandomGenerator expectedDummyRandomGenerator = expectedRandomGenerators.get(index);
        if (expectedDummyRandomGenerator.getCounter() > 0) {
          expectedRandomGenerators.remove(expectedDummyRandomGenerator);
        }
      }
      tolerance--;
    }

    assertEquals( 0, expectedRandomGenerators.size(), "Failed to return all possible valid random generators based on assignability");

  }

  private class DummyRandomGenerator implements RandomGenerator {
    private Class<?>[] types;
    private int counter = 0;

    private void setTypes(final Class<?>[] types) {
      this.types = types;
    }

    public Collection<Class<?>> getTypes() {
      return Arrays.asList(types);
    }

    public Object doGenerate(final Class<?> type) {
      counter++;
      return null;
    }

    private int getCounter() {
      return counter;
    }
  }
}
