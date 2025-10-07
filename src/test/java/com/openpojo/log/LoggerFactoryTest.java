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

package com.openpojo.log;

import com.openpojo.log.impl.Log4JLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author oshoukry
 */
public class LoggerFactoryTest {
  private Class<? extends Logger> defaultLoggerClass = Log4JLogger.class;

  private final String[] supportedLoggers = {
      "com.openpojo.log.impl.SLF4JLogger",
      "com.openpojo.log.impl.Log4JLogger",
      "com.openpojo.log.impl.JavaLogger" };

  @BeforeEach
  public final void setUp() {
    LoggerFactory.setActiveLogger(defaultLoggerClass);
  }

  @Test
  public final void shouldReturnDefaultLoggerClassByClass() {
    final Logger log = LoggerFactory.getLogger(LoggerFactoryTest.class);
    assertNotNull(log);
    assertEquals(defaultLoggerClass.getName(), log.getClass().getName());
  }

  @Test
  public final void shouldReturnDefaultLoggerClassByCategory() {
    final Logger log = LoggerFactory.getLogger("TestLogger");
    assertNotNull(log);
    assertEquals(defaultLoggerClass.getName(), log.getClass().getName());
  }

  @Test
  public final void shouldReturnDefaultCategoryByClass() {
    Logger log = LoggerFactory.getLogger((Class<?>) null);
    assertNotNull(log, "Null logger returned when requested with null class");
    log = LoggerFactory.getLogger((String) null);
    assertNotNull( log, "Null logger returned when requested with null category");
  }


  @Test
  public final void ensureSupportedLoggersAndOrder() {
    assertEquals( 3, LoggerFactory.SUPPORTED_LOGGERS.length, "Supported loggers added/removed?");

    String message = "Changed supported loggers order? expected position[%s] to be [%s]";

    for (int position = 0; position < supportedLoggers.length; position++) {
      assertEquals(String.format(message, position, supportedLoggers[position]), supportedLoggers[position],
          LoggerFactory.SUPPORTED_LOGGERS[position]);
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public final void shouldReturnSetLogger() throws ClassNotFoundException {
    for (String logger : supportedLoggers) {
      Class<Logger> loggerClass = (Class<Logger>) Class.forName(logger);
      LoggerFactory.setActiveLogger(loggerClass);
      assertTrue( LoggerFactory.getLogger((String) null).getClass().equals(loggerClass), String.format("Expected LoggerFactory to be set to [%s] but was [%s]", loggerClass,
              LoggerFactory.getLogger((String) null)));
    }
  }
}
