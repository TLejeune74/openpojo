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

package com.openpojo.reflection.java.packageloader.reader;

import java.net.URI;
import java.net.URL;
import java.util.Set;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.reflection.java.Java;
import com.openpojo.utils.samplejar.SampleJar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class JarFileReaderTest {

  @Test
  public void onlyPrivateConstructors() {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(JarFileReader.class);
    for (PojoMethod method : pojoClass.getPojoConstructors()) {
      assertTrue(method.isPrivate(), "Constructor must be private [" + method + "]");
    }
  }

  @Test
  public void canCreate() {
    JarFileReader jarFileReader = JarFileReader.getInstance((String)null);
    assertNotNull(jarFileReader);
  }

  @Test
  public void canCreateUsingInvalidJarFileUrl() {
    JarFileReader jarfileReader = JarFileReader.getInstance((URL) null);
    assertNotNull(jarfileReader);
    assertFalse(jarfileReader.isValid());
  }

  @Test
  public void canReadJarUsingURLOrFile() {
    JarFileReader urlJarFileReader = JarFileReader.getInstance(SampleJar.getJarURL());
    JarFileReader filepathJarFileReader = JarFileReader.getInstance(SampleJar.getJarFilePath());

    assertTrue(urlJarFileReader.isValid(), "Invalid URL JarFile [" + SampleJar.getJarURLPath() + "]");
    assertTrue(filepathJarFileReader.isValid(), "Invalid filepath JarFile [" + SampleJar.getJarFilePath() + "]");

    Set<String> urlClassNames = urlJarFileReader.getClassNames();
    Set<String> filepathClassNames = filepathJarFileReader.getClassNames();
    assertEquals(urlClassNames.size(), filepathClassNames.size());

    for (String urlEntry : urlClassNames) {
      assertTrue(filepathClassNames.contains(urlEntry), "Failed to find urlEntry [" + urlEntry + "]");
    }
  }

  @Test
  public void shouldReturnFalseWhenInvalidFile() {
    JarFileReader jarFileReader = JarFileReader.getInstance((String)null);
    assertFalse(jarFileReader.isValid());
  }

  @Test
  public void shouldReturnFalseIfFileIsNotJarFile() throws Exception {
    PojoClass pojoClass = PojoClassFactory.getPojoClass(this.getClass());
    String sourcePath = (new URI(pojoClass.getSourcePath())).getPath();
    JarFileReader jarFileReader = JarFileReader.getInstance(sourcePath);
    assertFalse(jarFileReader.isValid());
  }

  @Test
  public void isValidReturnsTrueWhenFileIsJar() throws Exception {
    JarFileReader jarFileReader = JarFileReader.getInstance(SampleJar.getJarFilePath());
    assertTrue(jarFileReader.isValid());
  }

  @Test
  public void canReadEntries() {
    String jarFile = getJarFile("rt.jar");
    assertNotNull(jarFile);
    JarFileReader jarFileReader = JarFileReader.getInstance(jarFile);
    assertNotNull(jarFileReader);
    assertTrue(jarFileReader.isValid(), "rt.jar should be valid");
    Set<String> classNames = jarFileReader.getClassNames();
    assertTrue(classNames.size() > 10000); // actual number is 20,651
  }

  private String getJarFile(String jarFileName) {
    String classPath = System.getProperty("java.class.path");
    classPath += Java.CLASSPATH_DELIMITER + System.getProperty("java.library.path");
    classPath += Java.CLASSPATH_DELIMITER + System.getProperty("java.ext.dirs");
    classPath += Java.CLASSPATH_DELIMITER + System.getProperty("sun.boot.class.path");
    String[] classPathParts = classPath.split(Java.CLASSPATH_DELIMITER);
    for (String entry : classPathParts) {
      if (entry.endsWith(Java.PATH_DELIMITER + jarFileName))
        return entry;
    }
    return null;
  }
}