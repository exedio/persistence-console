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

public class IsNullTest {

  @Test
  void testIsNotNull() {
    final var cop = new IsNotNullCop(CopTest.args(MODEL), CopTest.testArgs);

    assertEquals(List.of(MyType.optional), cop.getItems()); // Lists all fields, that are optional.
    assertEquals(
      "SELECT COUNT(*) FROM \"MyType\" WHERE \"optional\" IS NULL -- inspection fails if result is zero",
      cop.getViolationSql(MyType.optional)
    );

    assertEquals(0, cop.check(MyType.optional)); // Does not fail if there are no items at all (the table is empty).

    try (var tx = MODEL.startTransactionTry("non-null item")) {
      new MyType("non-null");
      tx.commit();
    }
    assertEquals(1, cop.check(MyType.optional)); // Fails on all fields, where there is no item with value null.

    try (var tx = MODEL.startTransactionTry("second non-null item")) {
      new MyType("non-null");
      tx.commit();
    }
    assertEquals(1, cop.check(MyType.optional)); // another non-null item does not change anything

    try (var tx = MODEL.startTransactionTry("non-null item")) {
      new MyType((String) null);
      tx.commit();
    }
    assertEquals(0, cop.check(MyType.optional)); // OK
  }

  @Test
  void testIsAlwaysNull() {
    final var cop = new IsAlwaysNullCop(CopTest.args(MODEL), CopTest.testArgs);

    assertEquals(List.of(MyType.optional), cop.getItems()); // Lists all fields, that are optional.
    assertEquals(
      "SELECT COUNT(*) FROM \"MyType\" WHERE \"optional\" IS NOT NULL -- inspection fails if result is zero",
      cop.getViolationSql(MyType.optional)
    );

    assertEquals(0, cop.check(MyType.optional)); // Does not fail if there are no items at all (the table is empty).

    try (var tx = MODEL.startTransactionTry("null item")) {
      new MyType((String) null);
      tx.commit();
    }
    assertEquals(1, cop.check(MyType.optional)); // Fails on all fields, where there is no item with a non-null value.

    try (var tx = MODEL.startTransactionTry("second null item")) {
      new MyType((String) null);
      tx.commit();
    }
    assertEquals(1, cop.check(MyType.optional)); // another null item does not change anything

    try (var tx = MODEL.startTransactionTry("non-null item")) {
      new MyType("non-null");
      tx.commit();
    }
    assertEquals(0, cop.check(MyType.optional)); // OK
  }

  private static final class MyType extends Item {

    @UsageEntryPoint
    static final StringField mandatory = new StringField();

    @UsageEntryPoint
    static final StringField optional = new StringField().optional();

    MyType(final String optional) {
      super(
        SetValue.map(mandatory, "mandatory"),
        SetValue.map(MyType.optional, optional)
      );
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
