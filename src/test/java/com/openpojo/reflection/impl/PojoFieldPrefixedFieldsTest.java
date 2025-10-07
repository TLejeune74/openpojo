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

package com.openpojo.reflection.impl;

import java.lang.reflect.Field;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.cache.PojoCache;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.reflection.impl.sample.classes.AClassWithFieldsNotPrefixed;
import com.openpojo.reflection.impl.sample.classes.AClassWithFieldsPrefixed;
import com.openpojo.reflection.utils.AttributeHelper;
import com.openpojo.validation.rule.Rule;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class PojoFieldPrefixedFieldsTest {

  @BeforeEach
  @AfterEach
  public void cleanup() {
    PojoCache.clear();
    AttributeHelper.clearRegistry();
  }

  @Test
  public void shouldHaveGettersAndSetters() {
    AttributeHelper.registerFieldPrefix("m");
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AClassWithFieldsPrefixed.class);
    for (PojoField pojoField : pojoClass.getPojoFields()) {
      assertTrue(pojoField.hasGetter() && pojoField.hasSetter(), String.format("Getters / Setters not found on field =[%s]", pojoField));
    }
  }

  @Test
  public void shouldNotHaveGettersAndSetters() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AClassWithFieldsPrefixed.class);
    for (PojoField pojoField : pojoClass.getPojoFields()) {
      assertFalse(pojoField.hasGetter() || pojoField.hasSetter(), String.format("Getters / Setters not found on field =[%s]", pojoField));
    }
  }

  @Test
  public void shouldFailAttributeName() throws NoSuchFieldException {
    assertThrows(ReflectionException.class, () -> {
        AttributeHelper.registerFieldPrefix("mName");
        Field mNameField = AClassWithFieldsPrefixed.class.getDeclaredField("mName");
        AttributeHelper.getAttributeName(mNameField);
    });
  }

  @Test
  public void unRegisteringAPrefix_reflectsOnMethodLookup() {
    AttributeHelper.registerFieldPrefix("m");
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AClassWithFieldsPrefixed.class);
    Rule getterRule = new GetterMustExistRule();
    getterRule.evaluate(pojoClass);

    AttributeHelper.unregisterFieldPrefix("m");
    PojoCache.clear();

    pojoClass = PojoClassFactory.getPojoClass(AClassWithFieldsPrefixed.class);
    boolean unregisterdSuccessfully = false;
    try {
      getterRule.evaluate(pojoClass);
    } catch (AssertionError ae) {
      unregisterdSuccessfully = true;
    }
    if (!unregisterdSuccessfully)
      fail("unregistering failed?!");
  }

  @Test
  public void unmatchedPrefixesHaveNoEffect() {
    AttributeHelper.registerFieldPrefix("n");
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AClassWithFieldsNotPrefixed.class);
    Rule getterRule = new GetterMustExistRule();
    getterRule.evaluate(pojoClass);
  }

  @Test
  public void registeringNullOrEmptyPrefixIsIgnored() {
    AttributeHelper.registerFieldPrefix(null);
    AttributeHelper.registerFieldPrefix("");
    PojoClass pojoClass = PojoClassFactory.getPojoClass(AClassWithFieldsNotPrefixed.class);
    Rule getterRule = new GetterMustExistRule();
    getterRule.evaluate(pojoClass);

    AttributeHelper.registerFieldPrefix("m");
    pojoClass = PojoClassFactory.getPojoClass(AClassWithFieldsPrefixed.class);
    getterRule.evaluate(pojoClass);
  }
}
