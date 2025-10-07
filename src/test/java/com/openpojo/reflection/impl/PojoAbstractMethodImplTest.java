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

package com.openpojo.reflection.impl;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.impl.sample.classes.AnAbstractClassEmpty;
import com.openpojo.reflection.impl.sample.classes.AnAbstractClassWithOneAbstractMethod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class PojoAbstractMethodImplTest {

  @Test
  public void shouldFindNoAbstractMethods() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AnAbstractClassEmpty.class);
    for (PojoMethod pojoMethod : pojoClass.getPojoMethods()) {
      assertTrue(pojoMethod.isConstructor());
      assertFalse(pojoMethod.isAbstract());
    }

    assertEquals(1, pojoClass.getPojoMethods().size());
  }

  @Test
  public void shouldfindOneAbstractMethod() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AnAbstractClassWithOneAbstractMethod.class);
    for (PojoMethod pojoMethod : pojoClass.getPojoMethods()) {
      if (!pojoMethod.isConstructor())
        assertTrue(pojoMethod.isAbstract());
    }

    assertEquals(1 + 1 /* constructor */, pojoClass.getPojoMethods().size());
  }
}
