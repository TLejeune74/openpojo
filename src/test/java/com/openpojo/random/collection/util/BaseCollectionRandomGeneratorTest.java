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

package com.openpojo.random.collection.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.openpojo.random.ParameterizableRandomGenerator;
import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;
import com.openpojo.random.collection.support.ALeafChildClass;
import com.openpojo.random.exception.RandomGeneratorException;
import com.openpojo.random.util.SerializableComparableObject;
import com.openpojo.reflection.Parameterizable;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.impl.PojoClassFactory;
import org.junit.jupiter.api.Test;

/**
 * @author oshoukry
 */
public abstract class BaseCollectionRandomGeneratorTest {

  protected abstract ParameterizableRandomGenerator getInstance();

  protected abstract Class<? extends ParameterizableRandomGenerator> getGeneratorClass();

  protected abstract Class<? extends Collection> getExpectedTypeClass();

  protected abstract Class<? extends Collection> getGeneratedTypeClass();

  protected abstract Class<?> getGenericType();

  protected Class<?> getDefaultType() {
    return SerializableComparableObject.class;
  }

  protected boolean validateCollectionContents() {
    return true;
  }

  @Test
  public void constructorShouldBePrivate() {
    final Class<?> randomGeneratorClass = getGeneratorClass();
    PojoClass randomGeneratorPojo = PojoClassFactory.getPojoClass(randomGeneratorClass);

    List<PojoMethod> constructors = new ArrayList<PojoMethod>();

    for (PojoMethod constructor : randomGeneratorPojo.getPojoConstructors()) {
      if (!constructor.isSynthetic())
        constructors.add(constructor);
    }
    assertEquals(1,
        constructors.size(), "Should only have one constructor [" + randomGeneratorPojo.getPojoConstructors() + "]");

    PojoMethod constructor = constructors.get(0);

    Assert.assertTrue(constructor.isPrivate());
  }

  @Test
  public void shouldBeAbleToCreate() {
    final RandomGenerator instance = getInstance();
    Assert.assertNotNull(instance);
    Assert.assertEquals(getGeneratorClass(), instance.getClass());
  }

  @Test
  public void shouldOnlyReturnCollectionClassFromGetTypes() {
    Collection<Class<?>> types = getInstance().getTypes();
    Assert.assertNotNull(types, "Should not be null");
    Assert.assertEquals(1, types.size(), "Should only have one type");
    Assert.assertEquals(getExpectedTypeClass(), types.iterator().next(), "Should only be " + getExpectedTypeClass().getName());
  }

  @Test
  public void generatedTypeShouldBeAssignableToDeclaredType() {
    Class<?> declaredType = getInstance().getTypes().iterator().next();
    Object generatedInstance = getInstance().doGenerate(declaredType);
    Assert.assertTrue(declaredType.isAssignableFrom(generatedInstance.getClass()), "[" + declaredType.getName() + " is not assignable to " + generatedInstance.getClass().getName() + "]");
  }

  @Test
  public void shouldThrowExceptionForDoGenerateForOtherThanCollectionClass() {
      try {
          getInstance().doGenerate(ALeafChildClass.class);
      } catch (RuntimeException e) {
          throw new RuntimeException(e);
      }
      Assert.fail("RandomGeneratorException");
  }

  @Test
  public void shouldThrowExceptionForDoGenerateForParameterizedOtherThanCollectionClass() {
      try {
          getInstance().doGenerate(new Parameterizable() {
              public Class<?> getType() {
                  return ALeafChildClass.class;
              }

              public boolean isParameterized() {
                  throw new IllegalStateException("Unimplemented!!");
              }

              public List<Type> getParameterTypes() {
                  throw new IllegalStateException("Unimplemented!!");
              }
          });
      } catch (RuntimeException e) {}
      Assert.fail("RandomGeneratorException");
  }

  @Test
  public void shouldGenerateCorrectTypeCollectionForRequestedCollection() {
    Collection someObject = (Collection) getInstance().doGenerate(getExpectedTypeClass());
    Assert.assertNotNull(someObject, "Should not be null");
    Assert.assertEquals(getGeneratedTypeClass(), someObject.getClass(), "Should be a " + getGeneratedTypeClass().getName());
    if (validateCollectionContents())
      Assert.assertTrue(someObject.size() > 0, "Should not be Empty");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldGenerateParametrizableCorrectCollectionForRequest() {
    Collection<?> collectionOfType = (Collection) getInstance().doGenerate(getParameterizedType());

    Assert.assertNotNull(collectionOfType, "Should not be null");
    if (validateCollectionContents())
      Assert.assertTrue(collectionOfType.size() > 0, "Should not be empty");
    for (Object entry : collectionOfType) {
      Assert.assertNotNull(entry, "Should not be null");
      Assert.assertEquals(getGenericType(), entry.getClass(), "Entry should be " + getGenericType().getName());
    }
  }

  @Test
  public void endToEnd() {
    Collection<?> generatedCollection = RandomFactory.getRandomValue(getExpectedTypeClass());
    assertCollectionHasExpectedTypes(generatedCollection, getDefaultType());
  }

  protected void assertCollectionHasExpectedTypes(Collection<?> generatedCollection, Class<?> type) {
    Assert.assertNotNull(generatedCollection, "Should not be null");
    Assert.assertEquals(getGeneratedTypeClass(), generatedCollection.getClass());
    if (validateCollectionContents())
      Assert.assertTrue( generatedCollection.size() > 0, "Should not be empty");
    for (Object entry : generatedCollection) {
      Assert.assertNotNull(entry, "Should not be null");
      Assert.assertEquals(type, entry.getClass(), "Entry should be " + type.getName());
    }
  }

  @Test
  public void endToEndWithGenerics() {
    Collection<?> generatedCollection = (Collection) RandomFactory.getRandomValue(getParameterizedType());
    assertCollectionHasExpectedTypes(generatedCollection, getGenericType());
  }

  protected Parameterizable getParameterizedType() {
    return new Parameterizable() {
      private Type[] types = new Type[] { getGenericType() };

      public Class<?> getType() {
        return getExpectedTypeClass();
      }

      public boolean isParameterized() {
        return true;
      }

      public List<Type> getParameterTypes() {
        return Arrays.asList(types);
      }
    };
  }
}
