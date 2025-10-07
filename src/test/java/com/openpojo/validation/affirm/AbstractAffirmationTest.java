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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * @author oshoukry
 */
public abstract class AbstractAffirmationTest {

  public abstract Affirmation getAffirmation();

  @BeforeEach
  public void setup() {
    AffirmationFactory.getInstance().setActiveAffirmation(getAffirmation());
  }

  /**
   */
  @Test
  public void testFail() {
    try {
      Affirm.fail("Expected failure!!");
    } catch (AssertionError e) {
      return;
    }
      Affirm.fail("Affirm.fail(String) failed to fail :)!!");
  }

  @Test
  public void testFailWithNullMessage() {
    try {
      Affirm.fail(null);
    } catch (AssertionError e) {
      return;
    }
      Affirm.fail("Affirm.fail(null) failed to fail :)!!");
  }

  /**
   */
  @Test
  public void testAffirmTrue() {
    Affirm.affirmTrue(true, "Affirm.affirmTrue on true failed!!");
    try {
      Affirm.affirmTrue( false, "Affirm.affirmTrue on false passed!!");
    } catch (AssertionError e) {
      return;
    }
      Affirm.fail("Affirm.affirmTrue call on false passed!!");
  }

  /**
   */
  @Test
  public void testAffirmFalse() {
    Affirm.affirmFalse( false, "Affirm.affirmFalse on false failed!!");
    try {
      Affirm.affirmFalse(true, "Affirm.affirmTrue on true passed!!");
    } catch (AssertionError e) {
      return;
    }
      Affirm.fail("Affirm.affirmFalse call on true passed!!");
  }

  /**
   * Test method for {@link com.openpojo.vaylidation.affirm.Affirm#affirmNotNull(java.lang.Object, java.lang.String)}.
   */
  @Test
  public void testAffirmNotNull() {
    Affirm.affirmNotNull(new Object(), "Affirm.affirmNotNull on non-null failed!!");
    try {
      Affirm.affirmNotNull( null, "Affirm.affirmNotNull on null passed!!");
    } catch (AssertionError e) {
      return;
    }
      Affirm.fail("Affirm.affirmNotNull call on null passed!!");
  }

  /**
   */
  @Test
  public void testAffirmNull() {
    Affirm.affirmNull( null, "Affirm.affirmNull on null failed!!");
    try {
      Affirm.affirmNull( new Object(), "Affirm.affirmNull on non-null passed!!");
    } catch (AssertionError e) {
      return;
    }
      Affirm.fail("Affirm.affirmNull call on non-null passed!!");
  }

  /**
   * Test method for
   */
  @Test
  @SuppressWarnings("UnnecessaryBoxing")
  public void testAffirmEquals() {
    Integer five = Integer.valueOf(5);
    Integer anotherFive = Integer.valueOf(5);
    Integer six = Integer.valueOf(6);

    Affirm.affirmEquals( five, anotherFive, "Affirm.affirmEquals on equal objects failed");
    try {
      Affirm.affirmEquals( five, six, "Affirm.affirmEquals on non-equal objects should have failed.");
    } catch (AssertionError e) {
      return;
    }
      Affirm.fail("Affirm.affirmEquals call on non-equal objects passed!");
  }

  @Test
  public void testAffirmSame() {
    Object o = new Object();
    Affirm.affirmSame(o, o, "Affirm.affirmSame on same objects failed");
    try {
      Affirm.affirmSame( new Object(), new Object(), "Affirm.affirmSame on non-same objects should have failed");
    } catch (AssertionError e) {
      return;
    }
      Affirm.fail("Affirm.affirmSame call on non-same objects passed");
  }

  @Test
  public void testAffirmContains() {
    List<String> myList = new LinkedList<String>();
    myList.add("This");
    myList.add("is");
    myList.add("mylist");
    Affirm.affirmContains( "is", myList, "should find the word 'is' in my list");

    try {
      Affirm.affirmContains( "WHAT", myList, "should not find the word 'WHAT' in my list");
    } catch (AssertionError e) {
      return;
    }
      Affirm.fail("Affirm.affirmContains failed to detect that 'WHAT' is not part of the collection");
  }

  @Test
  public void testAffirmContainsWithNulls() {
    List<String> myList = new LinkedList<String>();
    myList.add("This");
    myList.add("is");
    myList.add("mylist");
    myList.add(null);

    Affirm.affirmContains(null, myList,"Should find a null in the list");
    myList.remove(null);

    try {
      Affirm.affirmContains( null, myList, "Should not find a null in the list");
    } catch (AssertionError e) {
      return;
    }
      Affirm.fail("Affirm.affirmContains failed to detect a missing null in the list");
  }

  @Test
  public void shouldFailWhenArraysNotEqualSizes() {
    try {
        byte[] expected = new byte[] { (byte) 0xaa, (byte) 0xbb, (byte) 0xcc };
        byte[] actual = new byte[] { (byte) 0xaa };
        Affirm.affirmEquals( expected, actual, "Should fail due to size");
    } catch (AssertionError e) {
        throw new RuntimeException(e);
    }
      Affirm.fail("Should fail due to size");
  }

  @Test
  public void whenArrayIsClonedEqualityShouldPass() {
    try {
        byte[] expected = new byte[]{(byte) 0xaa, (byte) 0xbb, (byte) 0xcc};
        byte[] actual = expected.clone();
        Affirm.affirmEquals(expected, actual, "Array clone should be equal");
    } catch (AssertionError exError) {}
      Affirm.fail("Array clone should pass");
  }

  @Test
  public void shouldFailWhenArrayItemMismatch() {
    try {
        byte[] expected = new byte[]{(byte) 0xaa, (byte) 0xbb, (byte) 0xcc};
        byte[] actual = new byte[]{(byte) 0xaa, (byte) 0xbb, (byte) 0xdd};
        Affirm.affirmEquals(expected, actual, "Should fail due to element mismatch");
    } catch (AssertionError e) {
        throw new RuntimeException(e);
    }
      Affirm.fail("Should fail due to element mismatch");
  }

  @Test
  public void shouldNotFailWhenArrayIsNull() {
    try {
        byte[] expected = new byte[] { (byte) 0xaa, (byte) 0xbb, (byte) 0xcc };
        Affirm.affirmEquals(expected, null, "Should not fail due to null");
    } catch (AssertionError exError) {}
      Affirm.fail("Should not fail due to null");
  }

}
