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

import java.util.List;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.reflection.filters.sampleclasses.SampleEnum;
import com.openpojo.reflection.impl.PojoClassFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author oshoukry
 */
public class FilterEnumTest extends IdentitiesAreEqual {
    private static final int EXPECTED_NON_FILTERED_COUNT = 6;
    private static final int EXPECTED_FILTERED_COUNT = 5;

  private final String sampleClassesPackage = this.getClass().getPackage().getName() + ".sampleclasses";
  private final PojoClassFilter enumFilter = new FilterEnum();
  private final List<PojoClass> filteredPojoClasses = PojoClassFactory.getPojoClasses(sampleClassesPackage, enumFilter);

  private final List<PojoClass> nonFilteredPojoClasses = PojoClassFactory.getPojoClasses(sampleClassesPackage);

  @Test
  public void shouldConfirmSampleClassCount() {
    assertEquals( EXPECTED_NON_FILTERED_COUNT, nonFilteredPojoClasses.size(), String.format("Classes added/removed from [%s]?", sampleClassesPackage));
    assertEquals(EXPECTED_FILTERED_COUNT, filteredPojoClasses.size(), String.format("Classes added/removed from [%s]?", sampleClassesPackage));
  }

  @Test
  public void shouldIncludeNonEnum() {
    for (PojoClass pojoClass : filteredPojoClasses) {
      assertFalse( pojoClass.isEnum(), String.format("[%s] should have been filtered out!!", pojoClass));
    }
  }

  @Test
  public final void shouldExcludeEnum() {
    assertFalse(enumFilter.include(PojoClassFactory.getPojoClass(SampleEnum.class)), String.format("[%s] didn't exclude enum!!", enumFilter));

  }

  @Test
  public void shouldBeIdentityEqual() {
    FilterEnum instanceOne = new FilterEnum();
    FilterEnum instanceTwo = new FilterEnum();
    checkEqualityAndHashCode(instanceOne, instanceTwo);
  }

}
