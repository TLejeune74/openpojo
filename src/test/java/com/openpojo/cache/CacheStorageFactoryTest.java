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

package com.openpojo.cache;

import java.util.List;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.reflection.impl.PojoClassFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class CacheStorageFactoryTest {

  @Test
  public void shouldThrowExeptionIfConstructed() throws Throwable {
      assertThrows(UnsupportedOperationException.class, ()-> {
          PojoClass cacheStorageFactoryPojo = PojoClassFactory.getPojoClass(CacheStorageFactory.class);

          List<PojoMethod> pojoConstructors = cacheStorageFactoryPojo.getPojoConstructors();
          assertEquals( 1, pojoConstructors.size(), "Should have only one constructor");
          assertTrue( pojoConstructors.getFirst().isPrivate(), "Constructor must be private");

          try {
              pojoConstructors.getFirst().invoke(null, (Object[]) null);
          } catch (ReflectionException re) {
              throw re.getCause().getCause();
          }
      });

  }

  @Test
  public void shouldReturnTemporalCache() {
    CacheStorage<String> keyValuePairCache = CacheStorageFactory.getTemporalCacheStorage();
    String expectedKey = "SomeKey";
    String expectedValue = "SomeValue";
    keyValuePairCache.add(expectedKey, expectedValue);
    assertEquals(expectedValue, keyValuePairCache.get(expectedKey));
    System.gc();
    assertNull(keyValuePairCache.get(expectedKey));
  }

  @Test
  public void shouldReturnPersistentCache() {
    CacheStorage<String> keyValuePairCache = CacheStorageFactory.getPersistentCacheStorage();
    String expectedKey = "SomeKey";
    String expectedValue = "SomeValue";
    keyValuePairCache.add(expectedKey, expectedValue);
    assertEquals(expectedValue, keyValuePairCache.get(expectedKey));
    System.gc();
    assertEquals(expectedValue, keyValuePairCache.get(expectedKey));
  }
}
