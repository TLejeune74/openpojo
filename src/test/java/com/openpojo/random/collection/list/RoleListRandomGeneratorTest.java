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

package com.openpojo.random.collection.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.management.relation.RoleList;

import com.openpojo.random.RandomFactory;
import com.openpojo.random.collection.support.ALeafChildClass;
import com.openpojo.random.exception.RandomGeneratorException;
import com.openpojo.random.util.SomeRole;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.impl.PojoClassFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class RoleListRandomGeneratorTest {
  private final RoleListRandomGenerator randomGenerator = RoleListRandomGenerator.getInstance();
  private final Class<RoleList> expectedTypeClass = RoleList.class;

  @Test
  public void constructorShouldBePrivate() {
    PojoClass randomGeneratorPojo = PojoClassFactory.getPojoClass(randomGenerator.getClass());

    List<PojoMethod> constructors = new ArrayList<PojoMethod>();

    for (PojoMethod constructor : randomGeneratorPojo.getPojoConstructors()) {
      if (!constructor.isSynthetic())
        constructors.add(constructor);
    }
    assertEquals(1, constructors.size(), "Should only have one constructor [" + randomGeneratorPojo.getPojoConstructors() + "]");

    PojoMethod constructor = constructors.get(0);

    assertTrue(constructor.isPrivate());
  }

  @Test
  public void shouldBeAbleToCreate() {
    assertEquals(RoleListRandomGenerator.class, randomGenerator.getClass());
  }

  @Test
  public void shouldOnlyReturnCollectionClassFromGetTypes() {
    Collection<Class<?>> types = randomGenerator.getTypes();
    assertNotNull(types, "Should not be null");
    assertEquals(1, types.size(), "Should only have one type");
    assertEquals(expectedTypeClass, types.iterator().next(), "Should only be " + expectedTypeClass.getName());
  }

  @Test
  public void generatedTypeShouldBeAssignableToDeclaredType() {
    Class<?> declaredType = randomGenerator.getTypes().iterator().next();
    Object generatedInstance = randomGenerator.doGenerate(declaredType);
    assertTrue(declaredType.isAssignableFrom(generatedInstance.getClass()),"[" + declaredType.getName() + " is not assignable to " + generatedInstance.getClass().getName() +"]");
  }

  @Test
  public void shouldThrowExceptionForDoGenerateForOtherThanCollectionClass() {
    assertThrows(RandomGeneratorException.class, () -> {randomGenerator.doGenerate(ALeafChildClass.class);});
  }

  @Test
  public void shouldGenerateCorrectTypeCollectionForRequestedCollection() {
    Collection someObject = randomGenerator.doGenerate(expectedTypeClass);
    assertNotNull(someObject, "Should not be null");
    assertEquals(expectedTypeClass, someObject.getClass(), "Should be a " + expectedTypeClass.getName());
    assertTrue(someObject.size() > 0, "Should not be Empty");
  }

  @Test
  public void endToEnd() {
    Collection<?> generatedCollection = RandomFactory.getRandomValue(expectedTypeClass);
    assertCollectionHasExpectedTypes(generatedCollection, SomeRole.class);
  }

  protected void assertCollectionHasExpectedTypes(Collection<?> generatedCollection, Class<?> type) {
    assertNotNull(generatedCollection, "Should not be null");
    assertEquals(expectedTypeClass, generatedCollection.getClass());
    assertTrue(generatedCollection.size() > 0, "Should not be empty");
    for (Object entry : generatedCollection) {
      assertNotNull(entry, "Should not be null");
      assertEquals(type, entry.getClass(), "Entry should be " + type.getName());
    }
  }

}
