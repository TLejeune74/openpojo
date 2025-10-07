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

package com.openpojo.validation;

import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.exception.ValidationException;
import com.openpojo.validation.rule.Rule;
import com.openpojo.validation.test.Tester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class ValidatorBuilderTest {

  @Test
  public void constructorMustBePrivate() {
    PojoClass validatorBuilderPojoClass = PojoClassFactory.getPojoClass(ValidatorBuilder.class);
    for (PojoMethod constructor : validatorBuilderPojoClass.getPojoConstructors())
      assertTrue(constructor.isPrivate());
  }

  @Test
  public void createReturnsValidatorBuilder() {
    Object validatorBuilder = ValidatorBuilder.create();
    assertNotNull(validatorBuilder);
    assertTrue(ValidatorBuilder.class.isAssignableFrom(validatorBuilder.getClass()));
  }

  @Test
  public void withRules_ignoresNullArray() {
    ValidatorBuilder validatorBuilder = ValidatorBuilder.create().with((Rule[]) null);
    assertEquals(0, validatorBuilder.getRules().size());
  }

  @Test
  public void withRules_ignoresNullArrayEntries() {
    ValidatorBuilder validatorBuilder = ValidatorBuilder.create().with(new Rule[] { null, null });
    assertEquals(0, validatorBuilder.getRules().size());
  }

  @Test
  public void withRules_persistRules() {
    Rule anyRule = RandomFactory.getRandomValue(Rule.class);
    ValidatorBuilder validatorBuilder = ValidatorBuilder.create().with(anyRule, null);
    assertEquals(1, validatorBuilder.getRules().size());
  }

  @Test
  public void withTesters_ignoresNullArray() {
    ValidatorBuilder validatorBuilder = ValidatorBuilder.create().with((Tester[]) null);
    assertEquals(0, validatorBuilder.getTesters().size());
  }

  @Test
  public void withTesters_ignoresNullArrayEntries() {
    ValidatorBuilder validatorBuilder = ValidatorBuilder.create().with(new Tester[] { null, null });
    assertEquals(0, validatorBuilder.getTesters().size());
  }

  @Test
  public void withTesters_persistRules() {
    Tester anyTester = RandomFactory.getRandomValue(Tester.class);
    ValidatorBuilder validatorBuilder = ValidatorBuilder.create().with(anyTester, null);
    assertEquals(1, validatorBuilder.getTesters().size());
  }

  @Test
  public void build_throwsExceptionIfNoRulesOrTestersAdded() {
    ValidatorBuilder validatorBuilder = ValidatorBuilder.create();
      assertThrows(ValidationException.class, () -> validatorBuilder.build());
  }

  @Test
  public void build_returnsValidator() {
    ValidatorBuilder validatorBuilder = ValidatorBuilder.create();
    validatorBuilder.with(RandomFactory.getRandomValue(Rule.class));

    Object validator = validatorBuilder.build();

    assertNotNull(validator);
    assertTrue(Validator.class.isAssignableFrom(validator.getClass()));
  }

}
