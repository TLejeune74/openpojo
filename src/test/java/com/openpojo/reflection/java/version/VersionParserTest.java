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

package com.openpojo.reflection.java.version;

import java.util.List;

import org.junit.jupiter.api.Test;

import static com.openpojo.reflection.java.version.VersionParser.getVersionParts;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author oshoukry
 */
public class VersionParserTest {

  @Test
  public void shouldReturnEmptyListForVersionNull() {
    assertEquals(0, getVersionParts(null).size());
  }

  @Test
  public void shouldReturnEmptyListForVersionPartsWhenString() {
      assertEquals(0, getVersionParts("SomeString").size());
  }

  @Test
  public void shouldReturnMajorWhenOnlyMajorPresent() {
    Integer major = 5;

    final List<Integer> versionParts = getVersionParts("" + major);
      assertEquals(1, versionParts.size());
      assertEquals(major, versionParts.get(0));
  }

  @Test
  public void shouldGetMajorAndMinor() {
    Integer major = 6;
    Integer minor = 2;

    final List<Integer> versionParts = getVersionParts("" + major + "." + minor);

      assertEquals(2,versionParts.size());
      assertEquals(major,versionParts.get(0));
      assertEquals(minor,versionParts.get(1));
  }

  @Test
  public void shouldSkipOverStrings() {
    Integer major = 7;
    Integer minor = 5;

    final List<Integer> versionParts = getVersionParts("" + major + "." + minor + "-BETA");
      assertEquals(2,versionParts.size());
      assertEquals(major, versionParts.get(0));
      assertEquals(minor, versionParts.get(1));
  }

  @Test
  public void shouldSkipDotsThatAreBetweenStrings() {
    Integer major = 9;
    Integer minor = 3;

    final List<Integer> versionParts = getVersionParts("" + major + ".BETA." + minor);
      assertEquals(2, versionParts.size());
      assertEquals(major, versionParts.get(0));
      assertEquals(minor, versionParts.get(1));
  }
}
