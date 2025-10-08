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

package com.openpojo.reflection.java.load;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.construct.InstanceFactory;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.reflection.impl.PojoClassFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class ClassUtilTest {

  @Test
  public void shouldNotBeAbleToConstruct() {
      assertThrows(UnsupportedOperationException.class, () -> {
          try {
              PojoClass classUtilPojoClass = PojoClassFactory.getPojoClass(ClassUtil.class);
              assertEquals(1, classUtilPojoClass.getPojoConstructors().size());
              InstanceFactory.getLeastCompleteInstance(classUtilPojoClass);
          } catch (ReflectionException re) {
              Throwable cause = re.getCause();
              while (cause != null) {
                  if (cause instanceof UnsupportedOperationException)
                      throw cause;
                  cause = cause.getCause();
              }
          }
          fail("Should have not been able to construct");
      });
  }

  @Test
  public void shouldDetectTestClassLoaded() {
    assertTrue(ClassUtil.isClassLoaded(this.getClass().getName()));
  }

  @Test
  public void shouldDetectInvalidClassNotLoaded() {
    assertFalse(ClassUtil.isClassLoaded(this.getClass().getName() + "NonExistentClass"));
  }
}
