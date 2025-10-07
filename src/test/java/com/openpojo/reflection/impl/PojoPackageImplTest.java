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

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoPackage;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.reflection.impl.sample.annotation.AnotherAnnotation;
import com.openpojo.reflection.impl.sample.annotation.SomeAnnotation;
import com.openpojo.registry.ServiceRegistrar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class PojoPackageImplTest {

  private static final int EXPECTED_CLASSES = 61;

  private String packageName;
  private String expectedToString;

  private PojoPackage pojoPackage;

  @BeforeEach
  public void setUp() {
    packageName = this.getClass().getPackage().getName() + ".sample.classes";
    expectedToString = "PojoPackageImpl [packageName=" + packageName + "]";
    pojoPackage = PojoPackageFactory.getPojoPackage(packageName);
  }

  @Test
  public void testGetPojoClasses() {
    int counter = 0;
    for (final PojoClass pojoClass : pojoPackage.getPojoClasses()) {
      if (ServiceRegistrar.getInstance().getPojoCoverageFilterService().include(pojoClass))
        counter++;
    }
    assertEquals(EXPECTED_CLASSES, counter,String.format("classes added/removed to package=[%s]?", packageName));
  }

  @Test
  public void shouldReturnEmptyListNoAnnotation() {
    final PojoPackage pojoPackage = PojoPackageFactory.getPojoPackage(this.getClass().getPackage().getName()
        + ".packagenoannotation");
    assertTrue(pojoPackage.getAnnotations() != null
        && pojoPackage.getAnnotations().size() == 0, String.format("Annotations added? expected none [%s]", pojoPackage));
  }

  @Test
  public void shouldReturnAnAnnotation() {
    final PojoPackage pojoPackage = PojoPackageFactory.getPojoPackage(this.getClass().getPackage().getName()
        + ".packagemanyannotations");
    final Class<? extends Annotation> expectedAnnotationClass = SomeAnnotation.class;
    assertNotNull(pojoPackage.getAnnotation(expectedAnnotationClass), String.format("[%s] removed from package [%s]?", expectedAnnotationClass, pojoPackage));
  }

  @Test
  public void shouldEnusreNoPackageInfoExists() {
    final String packageName = this.getClass().getPackage().getName() + ".packagenopackageinfo";
    final List<PojoClass> pojoClasses = PojoClassFactory.getPojoClasses(packageName);

    assertTrue(pojoClasses.size() > 0, "No classes in package?");
    for (final PojoClass pojoClass : pojoClasses) {
      assertFalse(pojoClass.getName().endsWith("package-info"),String.format("package-info added to package [%s]?", packageName));
    }

  }

  @Test
  public void shouldReturnNullAnnotationNoPackageInfo() {
    final PojoPackage pojoPackage = PojoPackageFactory.getPojoPackage(this.getClass().getPackage().getName()
        + ".packagenopackageinfo");
    assertNull(pojoPackage.getAnnotation(SomeAnnotation.class), String.format("package-info added to package [%s]?", pojoPackage));
  }

  @Test
  public void shouldReturnEmptyListAnnotationNoPackageInfo() {
    final PojoPackage pojoPackage = PojoPackageFactory.getPojoPackage(this.getClass().getPackage().getName()
        + ".packagenopackageinfo");
    assertEquals(
        0,
        pojoPackage.getAnnotations().size(), String.format("package-info with annotations added to package [%s]?", pojoPackage));
  }

  @Test
  public void shouldReturnAnnotationList() {
    final PojoPackage pojoPackage = PojoPackageFactory.getPojoPackage(this.getClass().getPackage().getName()
        + ".packagemanyannotations");
    assertEquals( 2, pojoPackage.getAnnotations().size(), String.format("Annotations added/removed? [%s]", pojoPackage));

    final List<Class<?>> expectedAnnotations = new LinkedList<Class<?>>();
    expectedAnnotations.add(SomeAnnotation.class);
    expectedAnnotations.add(AnotherAnnotation.class);
    for (final Annotation annotation : pojoPackage.getAnnotations()) {
      assertTrue(expectedAnnotations.contains(annotation.annotationType()), String.format("Expected annotations [%s] not found, instead found [%s]", expectedAnnotations,
              annotation.annotationType()));
    }
  }

  @Test
  public void shouldReturnPackageName() {
    final String packageName = this.getClass().getPackage().getName();

    final PojoPackage pojoPackage = PojoPackageFactory.getPojoPackage(packageName);
    assertEquals("Mismatch in packageName!!", packageName, pojoPackage.getName());

  }

  @Test
  public void shouldFailNoSuchPackage() {
    String randomPackageName = RandomFactory.getRandomValue(String.class);
    assertThrows(ReflectionException.class, () -> PojoPackageFactory.getPojoPackage(randomPackageName));
  }

  @Test
  public void shouldThrowIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new PojoPackageImpl(null));
  }

  @Test
  public void testtoString() {
    assertEquals(expectedToString, pojoPackage.toString(), "toString format changed?!");
  }
}
