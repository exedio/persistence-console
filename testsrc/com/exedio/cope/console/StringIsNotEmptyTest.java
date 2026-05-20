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

import static com.exedio.cope.console.SchemaGetApiTest.assertOrphaned;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.util.Sources;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StringIsNotEmptyTest {

  @Test
  void test() {
    final var cop = new StringIsNotEmptyCop(
      CopTest.args(MODEL),
      CopTest.testArgs
    );

    assertEquals(List.of(MyType.empty), cop.getItems()); // Lists all string fields, that allow the empty string.

    assertEquals(0, cop.check(MyType.empty)); // Does not fail if there are no items at all (the table is empty).

    try (var tx = MODEL.startTransactionTry("non-empty item")) {
      new MyType("non-empty");
      tx.commit();
    }
    assertEquals(1, cop.check(MyType.empty)); // Fails on all string fields, where there is no item with value empty string.

    try (var tx = MODEL.startTransactionTry("empty item")) {
      new MyType("");
      tx.commit();
    }
    assertEquals(0, cop.check(MyType.empty));
  }

  private static final class MyType extends Item {

    @UsageEntryPoint
    static final StringField normal = new StringField().optional();

    @UsageEntryPoint
    static final StringField empty = new StringField().optional().lengthMin(0);

    MyType(final String empty) {
      super(SetValue.map(normal, "normal"), SetValue.map(MyType.empty, empty));
    }

    @java.io.Serial
    private static final long serialVersionUID = 1l;

    static final Type<?> TYPE = TypesBound.newType(MyType.class, MyType::new);

    private MyType(final ActivationParameters ap) {
      super(ap);
    }
  }

  private static final Model MODEL = new Model(MyType.TYPE);

  @BeforeAll
  static void connect() {
    final java.util.Properties props = new java.util.Properties();
    props.setProperty("connection.url", "jdbc:hsqldb:mem:copeconsoletest");
    props.setProperty("connection.username", "sa");
    props.setProperty("connection.password", "");
    MODEL.connect(
      assertOrphaned(ConnectProperties.create(Sources.view(props, "DESC")))
    );
  }

  @AfterAll
  static void disconnect() {
    MODEL.disconnect();
  }

  @BeforeEach
  void createSchema() {
    MODEL.createSchema();
  }

  @AfterEach
  void tearDownSchema() {
    MODEL.rollbackIfNotCommitted();
    MODEL.tearDownSchema();
  }
}
