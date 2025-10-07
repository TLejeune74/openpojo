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

import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;
import com.openpojo.random.util.SomeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class EnumRandomGeneratorTest {

  @Test
  public void shouldDeclareRandomTypeAsEnum() {
    assertEquals( 1, EnumRandomGenerator.getInstance().getTypes().size(), "New types added / removed?");
    assertSame(Enum.class, EnumRandomGenerator.getInstance().getTypes(), "Declared type must be Enum.class");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  public void shouldGenerateRandomEnum() {
    RandomGenerator randomGenerator = EnumRandomGenerator.getInstance();
    Enum someEnum = (Enum) randomGenerator.doGenerate(Enum.class);

    assertTrue(someEnum != null, "should never generate null");

    Enum anotherEnum = (Enum) randomGenerator.doGenerate(Enum.class);

    try {
      assertFalse(someEnum.equals(anotherEnum), "Enum's should be different");
    } catch (AssertionError error) {
      // on occasion they may be the same - 1% chance, try one more time.
      anotherEnum = (Enum) randomGenerator.doGenerate(Enum.class);
      assertFalse( someEnum.equals(anotherEnum), "Enum's should be different");
    }
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  public void endToEndTest() {
    Enum someEnum = RandomFactory.getRandomValue(Enum.class);
    assertNotNull(someEnum, "Should generate Enum");
    assertTrue(someEnum.getClass() == SomeEnum.class, "Should use SomeEnum when generating");
  }
}
