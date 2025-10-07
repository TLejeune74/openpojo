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

package com.openpojo.random.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author oshoukry
 */
public class ComparableDelayedTest {

  @Test
  public void delayShouldBeBetweenMinus3And1() {
    for (int count = 0; count < 100; count++) {
      long delay = new ComparableDelayed().getDelay(null);
      assertTrue(2 > delay, "Should be under 2");
      assertTrue(-4 < delay, "Should be over -3");
    }
  }

  @Test
  public void shouldWireCompareToOverHashCodeValue() {
    ComparableDelayed firstInstance = new ComparableDelayed();
    ComparableDelayedStub secondInstance = new ComparableDelayedStub();

    secondInstance.hashCode = firstInstance.hashCode() - 1;
    assertEquals( 1, firstInstance.compareTo(secondInstance), "CompareTo should return 1");

    secondInstance.hashCode = firstInstance.hashCode() + 1;
    assertEquals( -1, firstInstance.compareTo(secondInstance), "CompareTo should return -1");

    secondInstance.hashCode = firstInstance.hashCode();
    assertEquals( 0, firstInstance.compareTo(secondInstance),"CompareTo should return 0");
  }

  private class ComparableDelayedStub extends ComparableDelayed {
    private int hashCode;

    public int hashCode() {
      return hashCode;
    }
  }
}
