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

package com.openpojo.issues.issue85;

import java.lang.reflect.Array;

import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.reflection.utils.ObjectToString;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author oshoukry
 */
public class IssueTest {
  private PojoClass pojoClass;

  @BeforeEach
  public void setup() {
    pojoClass = PojoClassFactory.getPojoClass(AClassWithPrimitiveArrays.class);
  }

  @Test
  public void ensureClassIsDefinedCorrectly() {
    Validator validator = ValidatorBuilder.create()
        .with(new GetterMustExistRule())
        .with(new SetterMustExistRule())
        .with(new AllPimitivesAsArraysDeclaredRule())
        .with(new GetterTester())
        .with(new SetterTester())
        .build();
    validator.validate(pojoClass);
  }

  @Test
  public void shouldBeAbleToString() {
    for (int i = 0; i < 100 ; i++) {
      AClassWithPrimitiveArrays primitiveArrays = RandomFactory.getRandomValue(AClassWithPrimitiveArrays.class);
      for (PojoField field : pojoClass.getPojoFields()) {
        assertTrue( field.isArray(), "Expected field to be array but wasn't [" + field + "]");
        Object contents = field.get(primitiveArrays);
        assertTrue(contents != null, "Expected field to not be null but was [" + field + "]");
        assertTrue( Array.getLength(contents) > 0, "Expected array to not be empty [" + field + "]");
        String expected = field.getName() + "=" + ObjectToString.toString(contents);
        assertTrue(pojoClass.toString(primitiveArrays).contains(expected), "Expected [" + expected + "] to be included in [" + pojoClass.toString(primitiveArrays) + "]");
      }
    }
  }
}
