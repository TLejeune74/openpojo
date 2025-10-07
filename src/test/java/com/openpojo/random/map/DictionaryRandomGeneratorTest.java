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

package com.openpojo.random.map;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.openpojo.random.RandomFactory;
import com.openpojo.random.map.support.SimpleType1;
import com.openpojo.random.map.support.SimpleType2;
import com.openpojo.reflection.Parameterizable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author oshoukry
 */
public class DictionaryRandomGeneratorTest {

  @Test
  public void canGenerateDictionary() {
    assertNotNull(RandomFactory.getRandomValue(Dictionary.class), "Should not be null");
  }

  @Test
  public void dictionaryCreatedIsHashtable() {
    Object object = RandomFactory.getRandomValue(Dictionary.class);
    assertNotNull(object);
    assertEquals(Hashtable.class, object.getClass(), "Should be HashTable instance");
  }

  @Test
  public void shouldNotBeEmpty() {
    Hashtable hashtable = (Hashtable) RandomFactory.getRandomValue(Dictionary.class);
    assertNotNull(hashtable);
    assertTrue(hashtable.size() > 0, "Should not be empty");
  }

  @Test
  public void canGenerateGenerics() {
    Hashtable<?, ?> hashtable = (Hashtable) RandomFactory.getRandomValue(new Parameterizable() {
      public Class<?> getType() {
        return Dictionary.class;
      }

      public boolean isParameterized() {
        return true;
      }

      public List<Type> getParameterTypes() {
        List<Type> parameterTypes = new ArrayList<Type>(2);
        parameterTypes.add(SimpleType1.class);
        parameterTypes.add(SimpleType2.class);
        return parameterTypes;
      }
    });
    assertNotNull(hashtable, "Should not be null");
    assertTrue(!hashtable.isEmpty(), "Should not be empty");
    for (Map.Entry<?, ?> entry : hashtable.entrySet()) {
      assertNotNull(entry, "Should not be null entry");
      assertNotNull(entry.getKey(), "Should not be null entry.getKey()");
      assertEquals(SimpleType1.class, entry.getKey().getClass(), "Should be of type SimpleType1.class");
      assertNotNull( entry.getValue(), "Should not be null entry.getValue()");
      assertEquals(SimpleType2.class, entry.getValue().getClass(), "Should be of type SimpleType2.class");
    }
  }
}
