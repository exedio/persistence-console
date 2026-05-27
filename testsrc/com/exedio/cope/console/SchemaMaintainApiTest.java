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
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.console.SchemaMaintainApi.Operation;
import com.exedio.cope.console.SchemaMaintainApi.Request;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConnectRule.class)
public class SchemaMaintainApiTest {

  @Test
  void testCreate(final ConnectRule connect)
    throws IOException, ApiTextException {
    connect.connectWithoutCreate(MODEL);
    assertEquals(
      """
      {
        "elapsedNanos" : NNN
      }""",
      request(Operation.create)
    );
  }

  @Test
  void testTearDown(final ConnectRule connect)
    throws IOException, ApiTextException {
    connect.connect(MODEL);
    assertEquals(
      """
      {
        "elapsedNanos" : NNN
      }""",
      request(Operation.tearDown)
    );
  }

  @Test
  void testDrop(final ConnectRule connect)
    throws IOException, ApiTextException {
    connect.connect(MODEL);
    assertEquals(
      """
      {
        "elapsedNanos" : NNN
      }""",
      request(Operation.drop)
    );
  }

  @Test
  void testDelete(final ConnectRule connect)
    throws IOException, ApiTextException {
    connect.connect(MODEL);
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
    assertFails(
      () -> request(Operation.create),
      ApiTextException.class,
      "400 model not connected, use Model#connect for " +
        SchemaMaintainApiTest.class.getName() +
        "#MODEL"
    );
  }

  @Test
  void testSQLRuntimeException(final ConnectRule connect) {
    connect.connectWithoutCreate(MODEL);
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
}
