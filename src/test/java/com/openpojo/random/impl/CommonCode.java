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

package com.openpojo.random.impl;

import com.openpojo.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class CommonCode {
  /**
   * Utility method to assist with testing all non-Primitive classes.
   *
   * @param type
   *     The class type to test.
   */
  @SuppressWarnings("ConstantConditions")
  public static void testDoGenerateForClass(final RandomGenerator randomGenerator, final Class<?> type) {
    assertTrue( type.isInstance(randomGenerator.doGenerate(type)), String.format("Failed to get the requested type=[%s] from [%s] received=[%s] instead", type,
            randomGenerator, randomGenerator.doGenerate(type)));
    Object object = type.cast(randomGenerator.doGenerate(type));

    assertNotNull(object, String.format("Request to registered type [%s] must return non-null value!!", type));

    Object anotherObject = randomGenerator.doGenerate(type);
    if (object.equals(anotherObject)) { // Just incase they are the same
      anotherObject = randomGenerator.doGenerate(type);
      // if Boolean, try for 20 times, since there is a 50% chance we land on the same value.
      if (object.equals(anotherObject) && type == Boolean.class) {
        for (int counter = 0; counter < 20; counter++) {
          anotherObject = randomGenerator.doGenerate(type);
          if (!object.equals(anotherObject)) {
            break;
          }
        }
      }
    }
    assertFalse(object.equals(anotherObject), String.format("[%s] generating the same values for type=[%s]", randomGenerator, type));
  }

  public static void testGetType(final RandomGenerator randomGenerator, final Class<?> type, final int expectedCount) {
    assertEquals(expectedCount, randomGenerator.getTypes().size(), String.format("[%s] added/removed Types?", randomGenerator));
    assertTrue(randomGenerator.getTypes().contains(type), String.format("[%s] doesn't report responsibility for [%s]?!!", randomGenerator.getClass(), type));
  }
}
