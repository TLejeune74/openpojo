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

package com.openpojo.random.map.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author oshoukry
 */
public class MapHelperTest {
  @Test
  public void shouldReturnWithoutGenerationIfTypeOrKeyAreNull() {
    Map emptyMap = new HashMap();
    Map actual = MapHelper.buildMap(emptyMap, null, null);
    assertEquals(0, actual.size());
    assertSame(emptyMap, actual);

    actual = MapHelper.buildMap(emptyMap, this.getClass(), null);
      assertEquals(0, actual.size());
    assertSame(emptyMap, actual);

    actual = MapHelper.buildMap(emptyMap, null, this.getClass());
      assertEquals(0, actual.size());
      assertSame(emptyMap, actual);

  }

  @Test
  public void shouldReturnNullIfMapIsNull() {
    assertNull(MapHelper.buildMap(null, null, null));
      assertNull(MapHelper.buildMap(null, this.getClass(), null));
      assertNull(MapHelper.buildMap(null, null, this.getClass()));
      assertNull(MapHelper.buildMap(null, this.getClass(), this.getClass()));
  }

}
