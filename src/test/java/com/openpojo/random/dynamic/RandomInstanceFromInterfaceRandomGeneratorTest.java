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

package com.openpojo.random.dynamic;

import java.util.List;

import com.openpojo.random.dynamic.sampleclasses.AConcreteClass;
import com.openpojo.random.dynamic.sampleclasses.ASimpleInterface;
import com.openpojo.random.dynamic.sampleclasses.AnAbstractClass;
import com.openpojo.random.dynamic.sampleclasses.AnInterfaceWithGenericMethodReturnType;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.reflection.impl.PojoClassFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RandomInstanceFromInterfaceRandomGeneratorTest {

  RandomInstanceFromInterfaceRandomGenerator proxyGenerator;
  ASimpleInterface aSimpleInterface;

  @BeforeEach
  public void setup() {
    proxyGenerator = RandomInstanceFromInterfaceRandomGenerator.getInstance();
    aSimpleInterface = proxyGenerator.doGenerate(ASimpleInterface.class);
  }

  @Test
  public void shouldReturnAProxy() {
    assertNotNull(aSimpleInterface, "Interface instance not generated");
  }

  @Test
  public void shouldReturnANewInstanceEveryTime() {
      assertNotEquals(aSimpleInterface, proxyGenerator.doGenerate(ASimpleInterface.class), "Same instance returned or faulty equality implementation on proxy");
  }

  @Test
  public void shouldReturnFalseWithNullEquals() {
    assertFalse(aSimpleInterface.equals(null), "Should be false");
  }

  @Test
  public void shouldReturnRandomNonNullValuesForInterfaceMethods() {
    final ASimpleInterface aSimpleInterface = proxyGenerator.doGenerate(ASimpleInterface.class);

    assertNotNull( aSimpleInterface.getName(), "Generated proxy getName() returned null");

    final String name = aSimpleInterface.getName();
    final String otherName = aSimpleInterface.getName();
    if (name.equals(otherName)) { // Just in case they are the same by chance.
        assertNotEquals(name, aSimpleInterface.getName(), String.format("RandomProxyFactory=[%s] returned a non-Random Pojo Proxy",
                RandomInstanceFromInterfaceRandomGenerator.getInstance()));
    }
  }

  @Test
  public void shouldImplementAccuratetoStringAndhashCode() {
    final String toString = aSimpleInterface.toString();
    assertNotNull(toString, "toString() on proxy returned null");
    assertTrue(toString.contains("$Proxy") && toString.contains("@"),
            String.format("toString returned [%s] expected it to begin with [%s] and contain [@]", toString, "$Proxy"));

    assertTrue(toString.endsWith(String.valueOf(aSimpleInterface.hashCode())), "toString() doesn't end with hashCode()");

    final ASimpleInterface anotherSimpleInterface = proxyGenerator.doGenerate(ASimpleInterface.class);
    assertTrue(
        aSimpleInterface.hashCode() != anotherSimpleInterface.hashCode(), "Generated Proxy hashCode() should not return equal values across instances");
  }

  @Test
  public void shouldAllowInvokingVoidReturnMethods() {
    // Just ensuring it doesn't throw some exception/etc.
    aSimpleInterface.doSomethingUseful();
  }

  @Test
  public void shouldReturnProperGenericsValue() {
    final AnInterfaceWithGenericMethodReturnType anInterfaceWithGenericMethodReturnType = proxyGenerator.doGenerate
        (AnInterfaceWithGenericMethodReturnType.class);

    List<AConcreteClass> theList = anInterfaceWithGenericMethodReturnType.aListOfAConcreteClass();

    assertNotNull(theList, "Should not be null");
      assertFalse(theList.isEmpty(), "Should not be empty");
    for (Object entry : theList)
      assertEquals(AConcreteClass.class, entry.getClass(), "Should be of correct type");

    int[] anIntArray = anInterfaceWithGenericMethodReturnType.anIntArray();
    assertNotNull( anIntArray, "Should not be null");
    assertTrue( anIntArray.length > 0, "Should not be empty");
    for (int entry : anIntArray)
        assertNotEquals(entry, entry + 1, "should not be equal");

    String aString = anInterfaceWithGenericMethodReturnType.aString();
    assertNotNull(aString, "Should not be null");
      assertFalse(aString.isEmpty(), "Should not be empty");

    boolean voidMethodInvoked = false;
    PojoClass pojoClass = PojoClassFactory.getPojoClass(anInterfaceWithGenericMethodReturnType.getClass());
    for (PojoMethod pojoMethod : pojoClass.getPojoMethods()) {
      if (pojoMethod.getName().equals("aVoid")) {
        assertNull(pojoMethod.invoke(anInterfaceWithGenericMethodReturnType), "Should be null");
        voidMethodInvoked = true;
      }
    }
    assertTrue( voidMethodInvoked, "Void method not found!!");
  }

  @Test
  public void shouldFailAbstractClass() {
      assertThrows(ReflectionException.class, () -> proxyGenerator.doGenerate(AnAbstractClass.class));
  }

  @Test
  public void shouldFailConcreteClass() {
      assertThrows(ReflectionException.class, () -> proxyGenerator.doGenerate(AConcreteClass.class));
  }

}
