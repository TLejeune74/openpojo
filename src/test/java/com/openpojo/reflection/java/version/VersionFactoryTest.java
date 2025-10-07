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

import com.openpojo.reflection.java.bytecode.asm.ASMDetector;
import com.openpojo.reflection.java.load.ClassUtil;
import org.junit.jupiter.api.Test;

import static com.openpojo.random.RandomFactory.getRandomValue;
import static com.openpojo.reflection.java.version.VersionFactory.getImplementationVersion;
import static com.openpojo.reflection.java.version.VersionFactory.getVersion;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class VersionFactoryTest {

  @Test
  public void canCreate() {
    final Version version = anyVersion();
    assertNotNull(version);
  }

  @Test
  public void versionInterfaceImplementsComparable() {
    assertSame(Comparable.class, anyVersion());
  }

  @Test
  public void compareTwoEmptyVersionsAsEqual() {
    Version left = getVersion("");
    Version right = getVersion("");
    assertEquals(0, left.compareTo(right));
  }

  @Test
  public void v4ShouldBeLessThanV5() {
    Version left = getVersion("4");
    Version right = getVersion("5");
      assertEquals(-1, left.compareTo(right));
      assertEquals(1, right.compareTo(left));
  }

  @Test
  public void v4_2ShouldBeLessThan4_3() {
    Version left = getVersion("4.2");
    Version right = getVersion("4.3");
      assertEquals(-1, left.compareTo(right));
      assertEquals(1, right.compareTo(left));
  }

  @Test
  public void v4_2_1ShouldBeLessThan4_2_1_1() {
    Version left = getVersion("4.2.1");
    Version right = getVersion("4.2.1.1");
      assertEquals(-1, left.compareTo(right));
      assertEquals(1, right.compareTo(left));
  }

  @Test
  public void sameLengthAndMatchingVersionsShouldBe0() {
    Version left = getVersion("4.2.1.1");
    Version right = getVersion("4.2.1.1");
      assertEquals(0, left.compareTo(right));
      assertEquals(0, right.compareTo(left));
  }

  @Test
  public void getVersionReturnsOriginalString() {
    final String expectedVersion = "4.2.1.1-SNAPSHOT";
    Version someVersion = getVersion(expectedVersion);
      assertEquals(expectedVersion, someVersion.getVersion());
  }

  @Test
  public void shouldGetEmptyStringForOpenPojoClasses() {
    final Version version = getImplementationVersion(this.getClass());
    assertNotNull(version.getVersion());
  }

  @Test
  public void shouldGetASMVersion() {
    final Version version = getImplementationVersion(ClassUtil.loadClass(ASMDetector.ASM_CLASS_NAME));
    assertThat(version.getVersion(), Matchers.anyOf(startsWith("5."), startsWith("6."), startsWith("7.")));
  }

  @Test
  public void shouldGetEmptyVersionOfClassIsNull() {
    Version version = getImplementationVersion(null);
    assertNotNull(version);
    assertNull(version.getVersion());
  }

  private Version anyVersion() {
    return getVersion(getRandomValue(String.class) + "." + getRandomValue(String.class));
  }
}