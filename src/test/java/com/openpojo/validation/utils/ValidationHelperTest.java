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

package com.openpojo.validation.utils;

import java.util.List;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.reflection.java.bytecode.asm.ASMNotLoadedException;
import com.openpojo.utils.log.LogEvent;
import com.openpojo.utils.log.LogHelper;
import com.openpojo.utils.log.MockAppenderLog4J;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.impl.DefaultValidator;
import com.openpojo.validation.test.Tester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidationHelperTest {
  @Test
  public void testIsStaticFinal() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(StaticFinalData.class);
    List<PojoField> pojoFields = pojoClass.getPojoFields();
    assertEquals(4, pojoFields.size());
    for (PojoField fieldEntry : pojoFields) {
      if (fieldEntry.getName().equals("staticAndNotFinal")) {
        assertTrue(
            fieldEntry.isStatic()
                && !fieldEntry.isFinal()
                && !ValidationHelper.isStaticFinal(fieldEntry), "Static and not Final test failed!!");
      }
      if (fieldEntry.getName().equals("notStaticAndNotFinal")) {
        assertTrue(
            !fieldEntry.isStatic()
                && !fieldEntry.isFinal()
                && !ValidationHelper.isStaticFinal(fieldEntry), "Not static OR final test failed!!");
      }
      if (fieldEntry.getName().equals("STATIC_AND_FINAL")) {
        assertTrue(fieldEntry.isStatic() && fieldEntry.isFinal() && ValidationHelper.isStaticFinal(fieldEntry), "Static AND Final test failed!!!");
      }
      if (fieldEntry.getName().equals("finalAndNotStatic")) {
        assertTrue(
            !fieldEntry.isStatic()
                && fieldEntry.isFinal()
                && !ValidationHelper.isStaticFinal(fieldEntry),"Final and not Static test failed!!");
      }
    }
  }

  @Test
  public void shouldReportMissingASMProperly() {
    Validator validator = ValidatorBuilder.create()
        .with(new Tester() {
          public void run(PojoClass pojoClass) {
            throw ASMNotLoadedException.getInstance();
          }
        }).build();

    LogHelper.initialize(MockAppenderLog4J.class);
    validator.validate(PojoClassFactory.getPojoClass(this.getClass()));
    List<LogEvent> warnEvents = LogHelper.getWarnEvents(MockAppenderLog4J.class, DefaultValidator.class.getName());
    assertEquals(1, warnEvents.size());
    String expectedMessage = "ASM not loaded while attempting to execute behavioural tests on non-constructable class["
        + this.getClass() + "], either filter abstract classes or add asm to your classpath.";
    assertEquals(expectedMessage, warnEvents.get(0).getMessage());
  }

  private static class StaticFinalData {

    @SuppressWarnings("unused")
    private static int staticAndNotFinal = 0;

    @SuppressWarnings("unused")
    private int notStaticAndNotFinal;

    @SuppressWarnings("unused")
    private static final int STATIC_AND_FINAL = 0;

    @SuppressWarnings("unused")
    private final int finalAndNotStatic = 0;
  }
}
