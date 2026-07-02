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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.DoubleField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.console.annotations.SuppressIsNotNegative;
import com.exedio.cope.util.UsageEntryPoint;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConnectRule.class)
public class NumberIsNotNegativeDoubleTest {

  @Test
  void test(final ConnectRule connect) {
    final var cop = new NumberIsNotNegativeCop(
      CopTest.args(MODEL),
      CopTest.testArgs
    );

    assertEquals(List.of(MyType.negative), cop.getItems()); // Lists all number fields, that allow negative values.
    connect.connect(MODEL);
    assertEquals(
      """
      SELECT COUNT(*) FROM "MyType" WHERE "negative"<0.0 -- inspection fails if result is zero""",
      cop.getViolationSql(MyType.negative)
    );

    assertEquals(0, cop.check(MyType.negative)); // Does not fail if there are no values at all.

    try (var tx = MODEL.startTransactionTry("null item")) {
      new MyType((Double) null);
      tx.commit();
    }
    assertEquals(0, cop.check(MyType.negative)); // Does not fail if all values are null.

    try (var tx = MODEL.startTransactionTry("zero item")) {
      new MyType(0.0);
      tx.commit();
    }
    assertEquals(1, cop.check(MyType.negative)); // Fails on all number fields, where there is no item with a negative value.

    try (var tx = MODEL.startTransactionTry("positive item")) {
      new MyType(66.0);
      tx.commit();
    }
    assertEquals(1, cop.check(MyType.negative)); // still fails

    try (var tx = MODEL.startTransactionTry("negative item")) {
      new MyType(-0.1);
      tx.commit();
    }
    assertEquals(0, cop.check(MyType.negative)); // OK
  }

  private static final class MyType extends Item {

    static final DoubleField nonNegative = new DoubleField().min(0);

    static final DoubleField negative = new DoubleField().min(-0.1).optional();

    @UsageEntryPoint
    @SuppressIsNotNegative
    static final DoubleField negativeSuppressed = new DoubleField()
      .min(-0.1)
      .optional();

    MyType(final Double negative) {
      super(
        SetValue.map(nonNegative, 0.0),
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
}
