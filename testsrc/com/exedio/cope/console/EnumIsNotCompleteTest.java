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
import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConnectRule.class)
public class EnumIsNotCompleteTest {

  @Test
  void test(final ConnectRule connect) {
    final var cop = new EnumIsNotCompleteCop(
      CopTest.args(MODEL),
      CopTest.testArgs
    );

    assertEquals(List.of(MyType.field), cop.getItems()); // Lists all enum fields.
    connect.connect(MODEL);
    assertEquals(
      "SELECT COUNT(*) FROM ( SELECT DISTINCT \"field\" FROM \"MyType\" WHERE \"field\" IS NOT NULL ) -- inspection fails if result is less than 3",
      cop.getViolationSql(MyType.field)
    );

    assertEquals(0, cop.check(MyType.field)); // Does not fail if all values are null.

    try (var tx = MODEL.startTransactionTry("null item")) {
      new MyType((MyEnum) null);
      tx.commit();
    }
    assertEquals(0, cop.check(MyType.field)); // Does not fail if all values are null.

    try (var tx = MODEL.startTransactionTry("A item")) {
      new MyType(MyEnum.A);
      tx.commit();
    }
    assertEquals(2, cop.check(MyType.field)); // Fails on all enum fields, where there is not at least one item for each facet of the enum.

    try (var tx = MODEL.startTransactionTry("second A item")) {
      new MyType(MyEnum.A);
      tx.commit();
    }
    assertEquals(2, cop.check(MyType.field)); // duplicate, does not reduce failure count any further

    try (var tx = MODEL.startTransactionTry("B item")) {
      new MyType(MyEnum.B);
      tx.commit();
    }
    assertEquals(1, cop.check(MyType.field)); // C missing

    try (var tx = MODEL.startTransactionTry("C item")) {
      new MyType(MyEnum.C);
      tx.commit();
    }
    assertEquals(0, cop.check(MyType.field)); // OK
  }

  private enum MyEnum {
    A,
    B,
    C,
  }

  private static final class MyType extends Item {

    @UsageEntryPoint
    static final EnumField<MyEnum> field = EnumField.create(
      MyEnum.class
    ).optional();

    MyType(final MyEnum field) {
      super(SetValue.map(MyType.field, field));
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
