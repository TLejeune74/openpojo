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

package com.openpojo.validation.affirm;

import com.openpojo.business.BusinessIdentity;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.reflection.java.load.ClassUtil;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author oshoukry
 */
public class JUnitAssertAffirmation extends AbstractAffirmation implements Affirmation {
  static {
    if (!ClassUtil.isClassLoaded("org.junit.jupiter.api.Test"))
      throw ReflectionException.getInstance("org.junit.jupiter.api.Test class not found");
  }

  private JUnitAssertAffirmation() {
  }

  public void fail(final String message) {
    fail(message);
  }

  public void affirmTrue(final boolean condition, final String message) {
    assertTrue(condition, message);
  }

  public void affirmFalse(final boolean condition, final String message) {
    assertFalse(condition, message);
  }

  public void affirmNotNull(final Object object, final String message) {
    assertNotNull(object, message);
  }

  public void affirmNull(final Object object, final String message) {
    assertNull(object, message);
  }

  public void affirmEquals(final Object expected, final Object actual, final String message) {
    if (objectPointersAreTheSame(expected, actual))
      return;

    if (isArray(expected)) {
      affirmArrayEquals(expected, actual, message);
    } else {
      assertEquals(expected, actual, message);
    }
  }

  public void affirmSame(Object first, Object second, String message) {
    assertSame(first, second, message);
  }

  @Override
  public String toString() {
    return BusinessIdentity.toString(this);
  }
}
