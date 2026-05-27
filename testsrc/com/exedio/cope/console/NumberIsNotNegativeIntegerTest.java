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
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.util.Sources;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NumberIsNotNegativeIntegerTest {

  @Test
  void test() {
    final var cop = new NumberIsNotNegativeCop(
      CopTest.args(MODEL),
      CopTest.testArgs
    );

    assertEquals(List.of(MyType.negative), cop.getItems()); // Lists all number fields, that allow negative values.
    assertEquals(
      "SELECT COUNT(*) FROM \"MyType\" WHERE \"negative\"<0 -- inspection fails if result is zero",
      cop.getViolationSql(MyType.negative)
    );

    assertEquals(0, cop.check(MyType.negative)); // Does not fail if there are no values at all.

    try (var tx = MODEL.startTransactionTry("null item")) {
      new MyType((Integer) null);
      tx.commit();
    }
    assertEquals(0, cop.check(MyType.negative)); // Does not fail if all values are null.

    try (var tx = MODEL.startTransactionTry("zero item")) {
      new MyType(0);
      tx.commit();
    }
    assertEquals(1, cop.check(MyType.negative)); // Fails on all number fields, where there is no item with a negative value.

    try (var tx = MODEL.startTransactionTry("positive item")) {
      new MyType(66);
      tx.commit();
    }
    assertEquals(1, cop.check(MyType.negative)); // still fails

    try (var tx = MODEL.startTransactionTry("negative item")) {
      new MyType(-1);
      tx.commit();
    }
    assertEquals(0, cop.check(MyType.negative)); // OK
  }

  private static final class MyType extends Item {

    @UsageEntryPoint
    static final IntegerField nonNegative = new IntegerField().min(0);

    @UsageEntryPoint
    static final IntegerField negative = new IntegerField().min(-1).optional();

    MyType(final Integer negative) {
      super(
        SetValue.map(nonNegative, 0),
        SetValue.map(MyType.negative, negative)
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
