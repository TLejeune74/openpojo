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

package com.openpojo.issues.issue27;

import com.openpojo.issues.issue27.sample.ClassWithIntegerFieldAndPrimitiveIntParameterSetter;
import com.openpojo.issues.issue27.sample.ClassWithIntegerFieldAndPrimitiveIntReturnTypeGetter;
import com.openpojo.issues.issue27.sample.ClassWithPrimitiveIntFieldAndIntegerParameterSetter;
import com.openpojo.issues.issue27.sample.ClassWithPrimitiveIntFieldAndIntegerReturnTypeGetter;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author oshoukry
 */
public class issue27Test {

  @Test
  public void shouldFailValidationWithIntegerFieldAndPrimitiveIntParameterSetter() {
    final PojoClass pojoClass = PojoClassFactory.getPojoClass(ClassWithIntegerFieldAndPrimitiveIntParameterSetter.class);
    final Validator pojoValidator = ValidatorBuilder.create().with(new SetterMustExistRule()).build();
    assertThrows(AssertionError.class, () -> pojoValidator.validate(pojoClass));
  }

  @Test
  public void shouldFailValidationWithPrimitiveIntFieldAndIntegerParameterSetter() {
    final PojoClass pojoClass = PojoClassFactory.getPojoClass(ClassWithPrimitiveIntFieldAndIntegerParameterSetter.class);
    final Validator pojoValidator = ValidatorBuilder.create().with(new SetterMustExistRule()).build();
      assertThrows(AssertionError.class, () -> pojoValidator.validate(pojoClass));
  }

  @Test
  public void shouldFailValidationWithIntegerFieldAndPrimitiveIntReturnTypeGetter() {
    final PojoClass pojoClass = PojoClassFactory.getPojoClass(ClassWithIntegerFieldAndPrimitiveIntReturnTypeGetter.class);
    final Validator pojoValidator = ValidatorBuilder.create().with(new GetterMustExistRule()).build();
      assertThrows(AssertionError.class, () -> pojoValidator.validate(pojoClass));
  }

  @Test
  public void shouldFailValidationWithPrimitiveIntFieldAndIntegerReturnTypeGetter() {
    final PojoClass pojoClass = PojoClassFactory.getPojoClass(ClassWithPrimitiveIntFieldAndIntegerReturnTypeGetter.class);
    final Validator pojoValidator = ValidatorBuilder.create().with(new GetterMustExistRule()).build();
      assertThrows(AssertionError.class, () -> pojoValidator.validate(pojoClass));
  }

}
