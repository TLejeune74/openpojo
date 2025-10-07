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

package com.openpojo.reflection.filters;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author oshoukry
 */
public class FilterNonConcreteTest extends IdentitiesAreEqual {

  @Test
  public void testInclude() {
    PojoClassFilter pojoClassFilter = new FilterNonConcrete();
    PojoClass stubPojoClass = PojoStubFactory.getStubPojoClass(false);

    assertTrue(stubPojoClass.isConcrete() == pojoClassFilter.include(stubPojoClass), String.format("Filter[%s] was supposed to filter OUT non concrete class", pojoClassFilter));

    stubPojoClass = PojoStubFactory.getStubPojoClass(true);
    assertTrue(stubPojoClass.isConcrete() == pojoClassFilter.include(stubPojoClass), String.format("Filter[%s] was supposed to filter IN concrete class", pojoClassFilter));

    final StubPojoClassFilter stubPojoClassFilter = new StubPojoClassFilter();
    pojoClassFilter = new FilterChain(new FilterNonConcrete(), stubPojoClassFilter);

    stubPojoClass = PojoStubFactory.getStubPojoClass(true);
    pojoClassFilter.include(stubPojoClass);
    assertTrue( stubPojoClassFilter.includeCalled, String.format("Filter [%s] didn't invoke next in filter chain", pojoClassFilter));
  }

  private static class StubPojoClassFilter implements PojoClassFilter {
    private boolean includeCalled = false;

    public boolean include(final PojoClass pojoClass) {
      includeCalled = true;
      return true;
    }
  }

  @Test
  public void shouldBeIdentityEqual() {
    FilterNonConcrete instanceOne = new FilterNonConcrete();
    FilterNonConcrete instanceTwo = new FilterNonConcrete();

    checkEqualityAndHashCode(instanceOne, instanceTwo);
  }

  private static class PojoStubFactory {

    public static PojoClass getStubPojoClass(boolean isConcrete) {
      return (PojoClass) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
          new Class<?>[] { PojoClass.class }, new StubInvocationHandler(isConcrete));
    }
  }

  private static class StubInvocationHandler implements InvocationHandler {
    private boolean isConcrete;

    public StubInvocationHandler(boolean isConcrete) {
      this.isConcrete = isConcrete;
    }

    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

      if (method.getName().equals("isConcrete"))
        return isConcrete;

      throw new RuntimeException("UnImplemented!!");
    }
  }
}
