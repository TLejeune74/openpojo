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

package com.openpojo.reflection.adapt.impl;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.adapt.impl.sampleclasses.CloverInstrumentedClass;
import com.openpojo.reflection.coverage.impl.Clover3;
import com.openpojo.reflection.coverage.impl.Clover4;
import com.openpojo.reflection.coverage.impl.Cobertura;
import com.openpojo.reflection.coverage.impl.Jacoco;
import com.openpojo.reflection.coverage.service.PojoCoverageFilterService;
import com.openpojo.reflection.coverage.service.PojoCoverageFilterServiceFactory;
import com.openpojo.reflection.coverage.service.impl.DefaultPojoCoverageFilterService;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.registry.ServiceRegistrar;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author oshoukry
 */
public class CloverPojoClassAdapterTest {

  private PojoClass cloverInstrumentedPojoClass;
  private PojoClass cloverCleanedPojoClass;
  private static PojoCoverageFilterService originalPojoCoverageFilterService;
  private static PojoCoverageFilterService cloverPojoCoverageFilterService = PojoCoverageFilterServiceFactory
      .createPojoCoverageFilterServiceWith(Clover3.getInstance());

  @BeforeAll
  public static void initialSetup() {
    originalPojoCoverageFilterService = ServiceRegistrar.getInstance().getPojoCoverageFilterService();
  }

  @BeforeEach
  public void setup() {
    PojoCoverageFilterService allButClover3PojoCoverageFilterService = new DefaultPojoCoverageFilterService();

    allButClover3PojoCoverageFilterService.registerCoverageDetector(Cobertura.getInstance());
    allButClover3PojoCoverageFilterService.registerCoverageDetector(Jacoco.getInstance());

    ServiceRegistrar.getInstance().setPojoCoverageFilterService(allButClover3PojoCoverageFilterService);
    cloverInstrumentedPojoClass = PojoClassFactory.getPojoClass(CloverInstrumentedClass.class);
    ServiceRegistrar.getInstance().setPojoCoverageFilterService(cloverPojoCoverageFilterService);
    cloverCleanedPojoClass = CloverPojoClassAdapter.getInstance().adapt(cloverInstrumentedPojoClass);
  }

  @AfterAll
  public static void reInstateInitialSetup() {
    ServiceRegistrar.getInstance().setPojoCoverageFilterService(originalPojoCoverageFilterService);
  }

  @Test
  public void ensureCloverInstrumentedClassNotChanged() {
    int expectedFieldCount = 4;
    if (Clover4.getInstance().isLoaded())
      expectedFieldCount++;
    assertEquals(expectedFieldCount, cloverInstrumentedPojoClass.getPojoFields().size(), "Fields added/removed?");
    assertEquals( 3, cloverInstrumentedPojoClass.getPojoMethods().size(), "Methods added/removed?");
  }

  @Test
  public void shouldSkipFieldsStartingWith__CLR() {
    assertEquals( 2, cloverCleanedPojoClass.getPojoFields().size(), "Cobertura fields not filtered?");
  }

  @Test
  public void shouldNotSkipAnyMethods() {
    assertEquals( 3, cloverCleanedPojoClass.getPojoMethods().size(), "Cobertura methods not filtered?");
  }
}
