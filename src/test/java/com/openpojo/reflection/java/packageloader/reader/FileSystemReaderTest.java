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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.exception.ReflectionException;
import com.openpojo.reflection.java.packageloader.impl.URLToFileSystemAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author oshoukry
 */
public class FileSystemReaderTest {
  private File file;

  @BeforeEach
  public void setup() {
    file = new File(System.getProperty("user.dir")
        + File.separator + ".tmp.testing.folder." + RandomFactory.getRandomValue(String.class));
    file.mkdir();
  }

  @AfterEach
  public void tearDown() {
    file.delete();
  }

  @Test
  public void shouldNotFailForEmptyPaths() throws MalformedURLException, URISyntaxException {
    URL url = new URI("file://" + file.getAbsolutePath()).toURL();
    FileSystemReader fileSystemReader = FileSystemReader.getInstance(url);
    assertNotNull(fileSystemReader);
    assertEquals(0, fileSystemReader.getSubPackagesOfPackage("").size());
      assertEquals(0,fileSystemReader.getTypesInPackage("").size());
  }

  @Test
  public void throwsProperException() throws MalformedURLException, URISyntaxException {
    String anyUrl = anyUrl();
    try {
      FileSystemReader.getInstance(new URI(anyUrl).toURL());
      fail("Expected exception not thrown!");
    } catch (ReflectionException | URISyntaxException re) {
      URLToFileSystemAdapter ufsa = new URLToFileSystemAdapter(new URI(anyUrl).toURL());
      assertEquals("Failed to retrieve entries in path: "
              + "[" + ufsa.getAsFile().getAbsolutePath() + "]"
              + " created from URI: [" + ufsa.getAsURI() + "].  Please report this issue @ http://openpojo.com"
          , re.getMessage());
    }
  }

  private String anyUrl() {
    return "http://any.domain." + RandomFactory.getRandomValue(String.class) + ".com";
  }
}