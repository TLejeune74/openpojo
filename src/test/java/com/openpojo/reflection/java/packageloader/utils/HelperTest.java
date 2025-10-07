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

package com.openpojo.reflection.java.packageloader.utils;

import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.java.Java;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author oshoukry
 */
public class HelperTest {

  @Test
  public void whenChildSubPath_thenSubPath() {
    String somePackage = "com.openpojo.parent";
    String subPackage = "com.openpojo.parent.subpath";

    assertEquals(subPackage, Helper.getDirectSubPackageName(somePackage, subPackage));
  }

  @Test
  public void whenNonChildPath_thenNull() {
    String somePackage = "com.openpojo.parent";
    String subPackage = "com.openpojo.subpath";

    assertNull(Helper.getDirectSubPackageName(somePackage, subPackage));
  }

  @Test
  public void whenGrandChildPath_thenChildPath() {
    String somePackage = "com.openpojo.parent";
    String subPackage = "com.openpojo.parent.childpath";
    String grandChild = subPackage + ".grandChild";

    assertEquals(subPackage, Helper.getDirectSubPackageName(somePackage, grandChild));
  }

  @Test
  public void whenNonChildPathButNameOverlap_thenNull() {
    String somePackage = "com.openpojo.parent";
    String subPackage = "com.openpojo.parentalso.childpath";
    String grandChild = subPackage + ".grandChild";

    assertNull(Helper.getDirectSubPackageName(somePackage, grandChild));
  }

  @Test
  public void returnsFalseWhenEntryIsNullAndIsClass() {
    assertFalse(Helper.isClass(null), "Should return false for isClass and null");
  }

  @Test
  public void returnsFlaseIfNotEndsWithDotClass() {
    String someEntry = RandomFactory.getRandomValue(String.class);
    assertFalse(Helper.isClass(someEntry), "Should not return true for entry not ending with .class [" + someEntry + "]");
  }

  @Test
  public void whenEndsWithDotClassIsClassIsTrue() {
    String someEntry = RandomFactory.getRandomValue(String.class) + Java.CLASS_EXTENSION;
    assertTrue(Helper.isClass(someEntry), "Should return true for entry ending with .class [" + someEntry + "]");
  }

  @Test
  public void whenClassEndsWithDotClassAndGetFQClassName_ReturnsValidClassName() {
    String someEntry = RandomFactory.getRandomValue(String.class);
    assertEquals(someEntry, Helper.getFQClassName(someEntry + Java.CLASS_EXTENSION));
  }

  @Test
  public void whenGetFQClassNameReplacePathDelimiters() {
    char slash = Java.PATH_DELIMITER;
    String someEntry = "com" + slash + "package" + slash + "className" + Java.CLASS_EXTENSION;
    String expected = "com.package.className";
    assertEquals(expected, Helper.getFQClassName(someEntry));

  }
}