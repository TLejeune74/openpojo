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

package com.openpojo.reflection.java.packageloader.env;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.impl.PojoClassFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class JavaClassPathClassLoaderTest {

  private Integer minExpectedTotalClasses;
  private JavaClassPathClassLoader javaClassPathClassLoader;
  private int minJavaUtilConcurrentAtomicCount;
  private int minJavaLangClasses;
  private int minPackageCountUnderJava;

  private static final String JAVA_VERSION = System.getProperty("java.version");

  @BeforeEach
  public void setup() {
    javaClassPathClassLoader = JavaClassPathClassLoader.getInstance();
    if (JAVA_VERSION.startsWith("1.8")) {
      minExpectedTotalClasses = 20000;
      minJavaUtilConcurrentAtomicCount = 30;
      minJavaLangClasses = 400;
      minPackageCountUnderJava = 13;
    } else {
      minExpectedTotalClasses = 16000;
      minJavaUtilConcurrentAtomicCount = 17;
      minJavaLangClasses = 230;
      minPackageCountUnderJava = 10;
    }
  }

  @Test
  public void onlyPrivateConstructors() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(JavaClassPathClassLoader.class);
    for (PojoMethod constructor : pojoClass.getPojoConstructors())
      assertTrue(constructor.isPrivate());
  }

  @Test
  public void shouldNotThrowExceptionOnInvalidClassPathProperty() {
    JavaClassPathClassLoader instance = JavaClassPathClassLoader.getInstance("InvalidClassPathPropertyName");
    assertNotNull(instance);
    assertEquals(0, instance.getClassNames().size());
  }

  @Test
  public void canGetInstance() {
    JavaClassPathClassLoader instance = JavaClassPathClassLoader.getInstance();
    assertNotNull(instance);
  }

  @Test
  public void whenPackageNameIsNullReturnEmptyClassSet() {
    Set<Type> classes = javaClassPathClassLoader.getTypesInPackage(null);
      assertNotNull(classes);
    assertEquals(0, classes.size());
  }

  @Test
  public void defaultClassPathVars() {
    String[] expectedClassPathKeys = { "java.library.path", "java.class.path", "java.ext.dirs", "sun.boot.class.path" };

    Set<String> classPathKeys = javaClassPathClassLoader.getClassPathKeys();

      assertNotNull(classPathKeys);
    assertEquals(expectedClassPathKeys.length, classPathKeys.size());
    assertThat(classPathKeys, containsInAnyOrder(expectedClassPathKeys));
  }

  @Test
  public void canGetAllClassNamesInBootClassPath() {
    Set<String> classNames = javaClassPathClassLoader.getClassNames();
      assertNotNull(classNames);
    assertTrue(classNames.size() > minExpectedTotalClasses);
  }

  @Test
  public void canLoadAllClassesInJavaUtilConcurrentAtomic() {
    String concurrentPackageName = AtomicInteger.class.getPackage().getName();
    Set<Type> classesInPackage = javaClassPathClassLoader.getTypesInPackage(concurrentPackageName);
    assertTrue(classesInPackage.size() > minJavaUtilConcurrentAtomicCount);
  }

  @Test
  public void canGetPackageNamesUnderGivenPackageName() {
    Set<String> subPackages = javaClassPathClassLoader.getSubPackagesFor("java");
    assertNotNull(subPackages);
    assertTrue(subPackages.size() > minPackageCountUnderJava);
  }

  @Test
  public void willReturnTrueForJavaPackageExists() {
      assertTrue(javaClassPathClassLoader.hasPackage("java"));
      assertTrue(javaClassPathClassLoader.hasPackage("javax"));
      assertTrue(javaClassPathClassLoader.hasPackage("com.sun"));
    assertFalse(javaClassPathClassLoader.hasPackage("com.openpojo"));
  }

  @Test
  public void end2end_shouldLoadAllClassesInJavaLang() {
    List<PojoClass> types = PojoClassFactory.getPojoClassesRecursively("java.lang", null);
    checkListOfPojoClassesContains(types, java.lang.Class.class);
    checkListOfPojoClassesContains(types, java.lang.CharSequence.class);
    checkListOfPojoClassesContains(types, java.lang.Runnable.class);
    checkListOfPojoClassesContains(types, java.lang.Throwable.class);
    checkListOfPojoClassesContains(types, java.lang.Double.class);
    checkListOfPojoClassesContains(types, java.lang.Float.class);
    checkListOfPojoClassesContains(types, java.lang.Object.class);
    checkListOfPojoClassesContains(types, java.lang.Error.class);

    assertTrue(types.size() > minJavaLangClasses);
  }

  private void checkListOfPojoClassesContains(List<PojoClass> types, Class<?> expectedClass) {
    assertTrue(types.contains(PojoClassFactory.getPojoClass(expectedClass)), "Expected type [" + expectedClass.getName() + "] not found");
  }

  @Test
  public void end2endLoadAllClassesInTheVM() {
    List<PojoClass> types = PojoClassFactory.getPojoClassesRecursively("", null);
    assertTrue(types.contains(PojoClassFactory.getPojoClass(this.getClass())));
    final String reason = "Loaded " + types.size() + " classes instead of expected " + minExpectedTotalClasses;
      assertTrue(types.size() > minExpectedTotalClasses, reason);
    checkListOfPojoClassesContains(types, java.rmi.registry.LocateRegistry.class);
  }

}