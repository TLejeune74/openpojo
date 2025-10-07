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

package com.openpojo.issues.issue14;

import com.openpojo.issues.issue14.sampleclasses.SampleClass;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class ClassMemberTest {
  private PojoClass pojoClass;
  private Validator pojoValidator;

  @BeforeEach
  public void setUp() {

    pojoClass = PojoClassFactory.getPojoClass(SampleClass.class);
    pojoValidator = ValidatorBuilder.create()
        // Add Testers to create a new instance on the private variable Class and trigger the problem.
        .with(new SetterTester())
        .with(new GetterTester())
        .build();
  }

  @Test
  public void ensureSampleClassDefinitionIsCorrect() {
    String fieldName = "someMemberClass";
    assertEquals( 1, pojoClass.getPojoFields().size(), String.format("Fields added/removed to [%s]?", pojoClass));

    boolean validated = false;
    for (PojoField pojoField : pojoClass.getPojoFields()) {
      if (pojoField.getName().equals(fieldName)) {
        assertEquals("Field type changed?", Class.class.getName(), pojoField.getType().getName());
        assertTrue( pojoField.hasGetter() && pojoField.hasSetter(), String.format("Getter/Setter removed from field[%s]",
                pojoField));
        validated = true;
      }
    }
    if (!validated) {
      fail(String.format("[%s] field not found on PojoClass [%s]", fieldName, pojoClass));
    }
  }

  @Test
  public void validatePojos() {
    pojoValidator.validate(pojoClass);
  }

}
