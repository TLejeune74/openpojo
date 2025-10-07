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

package com.openpojo.validation.test.impl;

import java.util.List;

import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.utils.log.LogEvent;
import com.openpojo.utils.log.SpyAppender;
import com.openpojo.validation.CommonCode;
import com.openpojo.validation.test.Tester;
import com.openpojo.validation.test.impl.sampleclasses.SerializableTest_NonSerializableClass;
import com.openpojo.validation.test.impl.sampleclasses.SerializableTest_SerializableChildForSerializableParentWithNonSerializableFieldClass;
import com.openpojo.validation.test.impl.sampleclasses.SerializableTest_SerializableWithNonSerializableField;
import com.openpojo.validation.test.impl.sampleclasses.SerializableTest_SerializableWithTransientNonSerializableField;
import com.openpojo.validation.test.impl.sampleclasses.SerializationTest_SimpleSerializable;
import com.openpojo.validation.test.impl.sampleclasses.SerializerTest_SerializableThatThrowsExceptionOnReadObject;
import com.openpojo.validation.test.impl.sampleclasses.SerializerTest_SerializableThatThrowsExceptionOnWriteObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * @author oshoukry
 */
public class SerializableTesterTest {

  private SpyAppender spyAppender;
  private Class<? extends Tester> testerClass;
  private SerializableTester serializableTester;

  @BeforeEach
  public void setUp() throws Exception {
    testerClass = SerializableTester.class;
    serializableTester = new SerializableTester();

    spyAppender = new SpyAppender();
    spyAppender.startCaptureForLogger(testerClass);
  }

  @AfterEach
  public void tearDown() {
    spyAppender.stopCaptureForLogger(testerClass);
  }

  @Test
  public void shouldSkipOverNonSerializableClass() {
    final Class<?> nonSerializableClassClass = SerializableTest_NonSerializableClass.class;
    CommonCode.shouldPassTesterValidation(serializableTester, nonSerializableClassClass);

    final List<LogEvent> eventsForLogger = spyAppender.getEventsForLogger(testerClass);

    assertEquals(1, eventsForLogger.size());
      assertEquals("Class [" + nonSerializableClassClass + "] is not serializable, skipping validation", eventsForLogger.getFirst().message().toString());
  }

  @Test
  public void shouldPassSerializationTest() {
    SerializationTest_SimpleSerializable simpleSerializable = new SerializationTest_SimpleSerializable();
    simpleSerializable.setName(RandomFactory.getRandomValue(String.class));

    PojoClass pojoClass = PojoClassFactory.getPojoClass(SerializationTest_SimpleSerializable.class);
    serializableTester.run(pojoClass);

  }

  @Test
  public void shouldFailNonSerializableObject() {
      Class clazz = SerializableTest_SerializableWithNonSerializableField.class;
      PojoClass pojoClass = PojoClassFactory.getPojoClass(clazz);

      Throwable exception = assertThrows(AssertionError.class, () -> serializableTester.run(pojoClass));
    assertEquals("Class ["
                    + clazz.getName()
                    + "] has non-serializable field type ["
                    + pojoClass.getPojoFields().getFirst()
                    + "]", exception.getMessage());
  }

  @Test
  public void shouldNotFailIfNonSerializableIsTransient() {
    Class clazz = SerializableTest_SerializableWithTransientNonSerializableField.class;
    PojoClass pojoClass = PojoClassFactory.getPojoClass(clazz);

    serializableTester.run(pojoClass);
  }

  @Test
  public void shouldFailOnObjectWrite() {
    Class clazz = SerializerTest_SerializableThatThrowsExceptionOnWriteObject.class;
    PojoClass pojoClass = PojoClassFactory.getPojoClass(clazz);

      Throwable exception = assertThrows(AssertionError.class, () -> serializableTester.run(pojoClass));
    assertEquals("Failed to run " + serializableTester.getClass().getName()
        + " - Got exception [java.lang.RuntimeException: java.io.IOException: Can't write object]", exception.getMessage());
  }

  @Test
  public void shouldFailOnReadObject() {
    Class clazz = SerializerTest_SerializableThatThrowsExceptionOnReadObject.class;
    PojoClass pojoClass = PojoClassFactory.getPojoClass(clazz);

      Throwable exception = assertThrows(AssertionError.class, () -> serializableTester.run(pojoClass));
      assertEquals("Failed to run " + serializableTester.getClass().getName()
        + " - Got exception [java.lang.RuntimeException: java.io.IOException: Can't read object]", exception.getMessage());
  }

  @Test
  public void shouldFailSerializingSerializableChildOfSerializableParentWithNonSerializableField() {
    Class clazz = SerializableTest_SerializableChildForSerializableParentWithNonSerializableFieldClass.class;
    PojoClass pojoClass = PojoClassFactory.getPojoClass(clazz);
      Throwable exception = assertThrows(AssertionError.class, () -> serializableTester.run(pojoClass));
      assertEquals("Class [" + clazz.getName() + "] has non-serializable field type ["
        + SerializableTest_NonSerializableClass.class.getName() + "] which is inherited from a super class", exception.getMessage());
  }

}