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

package com.openpojo.business.utils;

import java.util.List;

import com.openpojo.business.annotation.BusinessKey;
import com.openpojo.business.cache.BusinessKeyField;
import com.openpojo.business.cache.BusinessKeyFieldCache;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.reflection.impl.PojoClassFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class BusinessPojoHelperTest {

  @Test
  public void shouldThrowExeptionIfConstructed() {
    PojoClass businessPojoHelper = PojoClassFactory.getPojoClass(BusinessPojoHelper.class);

    List<PojoMethod> pojoConstructors = businessPojoHelper.getPojoConstructors();
    assertEquals( 1, pojoConstructors.size(), "Should have only one constructor");
    assertTrue(pojoConstructors.getFirst().isPrivate(), "Constructor must be private");

    // old UnsupportedOperationException.class
    assertThrows(ReflectionException.class, () -> businessPojoHelper.getPojoConstructors().getFirst().invoke(null, (Object[]) null));
  }

  @Test
  public void whenGetBusinessKeyFields_FieldsAreCached() {
    List<BusinessKeyField> businessFields = BusinessPojoHelper.getBusinessKeyFields(DummyBusinessPojo.class);

    PojoClass pojoClass = PojoClassFactory.getPojoClass(BusinessPojoHelper.class);

    BusinessKeyFieldCache businessPojoHelperCache = null;
    for (PojoField field : pojoClass.getPojoFields()) {
      if (field.getType() == BusinessKeyFieldCache.class) {
        businessPojoHelperCache = (BusinessKeyFieldCache) field.get(null);
      }
    }

    assert businessPojoHelperCache != null;
    businessPojoHelperCache.add("SomePojo", businessFields);
    assertEquals(businessFields, businessPojoHelperCache.get(DummyBusinessPojo.class.getName()));
  }

  private static class DummyBusinessPojo {
    @BusinessKey
    private String name;
  }
}
