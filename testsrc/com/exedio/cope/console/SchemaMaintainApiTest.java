/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.console;

import static com.exedio.cope.console.ApiTest.writeJson;
import static com.exedio.cope.console.SchemaMaintainApi.maintain;
import static com.exedio.cope.junit.CopeAssert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.console.SchemaMaintainApi.Operation;
import com.exedio.cope.console.SchemaMaintainApi.Request;
import com.exedio.cope.util.Sources;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaMaintainApiTest {

  @Test
  void testCreate() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "elapsedNanos" : NNN
      }""",
      request(Operation.create)
    );
  }

  @Test
  void testTearDown() throws IOException, ApiTextException {
    assertEquals(
      """
      {
        "elapsedNanos" : NNN
      }""",
      request(Operation.tearDown)
    );
  }

  @Test
  void testDrop() throws IOException, ApiTextException {
    MODEL.createSchema();
    assertEquals(
      """
      {
        "elapsedNanos" : NNN
      }""",
      request(Operation.drop)
    );
  }

  @Test
  void testDelete() throws IOException, ApiTextException {
    MODEL.createSchema();
    assertEquals(
      """
      {
        "elapsedNanos" : NNN
      }""",
      request(Operation.delete)
    );
  }

  @Test
  void testNotConnectedException() {
    MODEL.disconnect();
    assertFails(
      () -> request(Operation.create),
      ApiTextException.class,
      "400 model not connected, use Model#connect for " +
      SchemaMaintainApiTest.class.getName() +
      "#MODEL"
    );
  }

  @Test
  void testSQLRuntimeException() {
    assertFails(
      () -> request(Operation.delete),
      ApiTextException.class,
      "400 user lacks privilege or object not found: MyType"
    );
  }

  private static String request(final Operation operation)
    throws IOException, ApiTextException {
    return writeJson(maintain(MODEL, new Request(operation))).replaceAll(
      "[0-9]+",
      "NNN"
    );
  }

  private static final class MyType extends Item {

    @UsageEntryPoint
    static final StringField myString = new StringField().toFinal();

    @java.io.Serial
    private static final long serialVersionUID = 1l;

    static final Type<?> TYPE = TypesBound.newType(MyType.class, MyType::new);

    private MyType(final ActivationParameters ap) {
      super(ap);
    }
  }

  private static final Model MODEL = new Model(MyType.TYPE);

  static {
    MODEL.enableSerialization(SchemaMaintainApiTest.class, "MODEL");
  }

  @BeforeAll
  static void connect() {
    final java.util.Properties props = new java.util.Properties();
    props.setProperty("connection.url", "jdbc:hsqldb:mem:copeconsoletest");
    props.setProperty("connection.username", "sa");
    props.setProperty("connection.password", "");
    MODEL.connect(ConnectProperties.create(Sources.view(props, "DESC")));
  }

  @BeforeEach
  void beforeEach() {
    if (!MODEL.isConnected()) {
      connect();
    }
  }

  @AfterAll
  static void disconnect() {
    MODEL.disconnect();
  }

  @AfterEach
  void tearDownSchema() {
    if (MODEL.isConnected()) {
      MODEL.rollbackIfNotCommitted();
      MODEL.tearDownSchema();
    }
  }
}
