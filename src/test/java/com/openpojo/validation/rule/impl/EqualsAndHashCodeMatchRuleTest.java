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

package com.openpojo.validation.rule.impl;

import java.util.List;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.rule.Rule;
import com.openpojo.validation.rule.impl.sampleclasses.AClassImplementingEqualsAndHashCode;
import com.openpojo.validation.rule.impl.sampleclasses.AClassImplementingEqualsOnly;
import com.openpojo.validation.rule.impl.sampleclasses.AClassImplementingHashcodeOnly;
import com.openpojo.validation.rule.impl.sampleclasses.AClassNotImplementingHashcodeOrEquals;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author oshoukry
 */
public class EqualsAndHashCodeMatchRuleTest {
  private Rule rule = new EqualsAndHashCodeMatchRule();

  @Test
  public void mustImplementRule() {
    PojoClass equalsAndHashcodeMatchRule = PojoClassFactory.getPojoClass(EqualsAndHashCodeMatchRule.class);
    List<PojoClass> interfaces = equalsAndHashcodeMatchRule.getInterfaces();
    assertTrue(interfaces.contains(PojoClassFactory.getPojoClass(Rule.class)), "Expected interface=[Rule] to be implemented, but was not");
  }

  @Test
  public void shouldPassIfNoEqualsOrHashcodeImplemented() {
    PojoClass aClassNotImplementingHashCodeOrEquals = PojoClassFactory.getPojoClass(AClassNotImplementingHashcodeOrEquals.class);
    List<PojoMethod> methods = aClassNotImplementingHashCodeOrEquals.getPojoMethods();


    assertEquals(1, methods.size());
    assertTrue(methods.get(0).isConstructor());

    rule.evaluate(aClassNotImplementingHashCodeOrEquals);
  }

  @Test
  public void shouldFailOnlyEqualsIsImplemented() {
    PojoClass aClassImplementingEqualsOnly = PojoClassFactory.getPojoClass(AClassImplementingEqualsOnly.class);
    List<PojoMethod> methods = aClassImplementingEqualsOnly.getPojoMethods();

    assertEquals(2, methods.size());
    boolean constructorFound = false;
    boolean equalsFound = false;

    for (PojoMethod method : methods) {
      if (method.isConstructor())
        constructorFound = true;
      if (method.getName().equals("equals")
          && method.getPojoParameters().size() == 1
          && method.getReturnType().equals(boolean.class))
        equalsFound = true;
    }

    assertTrue(constructorFound, "Constructor not found");
    assertTrue(equalsFound, "Equals not found");

    try {
      rule.evaluate(aClassImplementingEqualsOnly);
      fail("Should have failed validation but did not");
    } catch (AssertionError ae) {
      assertEquals(ae.getMessage(), "equals implemented but hashcode isn't in Pojo [" + aClassImplementingEqualsOnly + "]");
    }
  }

  @Test
  public void shouldFailOnlyHashcodeIsImplemented() {
    PojoClass aClassImplementingHashcodeOnly = PojoClassFactory.getPojoClass(AClassImplementingHashcodeOnly.class);
    List<PojoMethod> methods = aClassImplementingHashcodeOnly.getPojoMethods();

    assertEquals(2, methods.size());
    boolean constructorFound = false;
    boolean hashCode = false;
    for (PojoMethod method : methods) {
      if (method.isConstructor())
        constructorFound = true;
      if (!method.isConstructor() && method.getName().equals("hashCode")
          && method.getPojoParameters().size() == 0
          && method.getReturnType().equals(int.class))
        hashCode = true;
    }

    assertTrue(constructorFound, "Constructor not found");
    assertTrue(hashCode, "hashCode not found");

    try {
      rule.evaluate(aClassImplementingHashcodeOnly);
      fail("Should have failed validation but did not");
    } catch (AssertionError ae) {
      assertEquals(ae.getMessage(), "hashCode implemented but equals isn't in Pojo [" + aClassImplementingHashcodeOnly + "]");
    }
  }

  @Test
  public void shouldPassWhenEqualsAndHashCodeAreImplemented() {
    PojoClass aClassImplementingEqualsAndHashcode = PojoClassFactory.getPojoClass(AClassImplementingEqualsAndHashCode.class);
    List<PojoMethod> methods = aClassImplementingEqualsAndHashcode.getPojoMethods();

    assertEquals(3, methods.size());

    boolean constructorFound = false;
    boolean equalsFound = false;
    boolean hashCodeFound = false;

    for (PojoMethod method : methods) {
      if (method.isConstructor())
        constructorFound = true;
      if (method.getName().equals("hashCode")
          && method.getPojoParameters().size() == 0
          && method.getReturnType().equals(int.class))
        hashCodeFound = true;
      if (method.getName().equals("equals")
          && method.getPojoParameters().size() == 1
          && method.getReturnType().equals(boolean.class))
        equalsFound = true;
    }

    assertTrue(constructorFound, "Constructor not found");
    assertTrue(equalsFound, "Equals not found");
    assertTrue(hashCodeFound, "hashCode not found");

    rule.evaluate(aClassImplementingEqualsAndHashcode);
  }
}
