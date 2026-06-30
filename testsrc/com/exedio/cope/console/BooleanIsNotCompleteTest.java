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
import com.exedio.cope.BooleanField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConnectRule.class)
public class BooleanIsNotCompleteTest {

  @Test
  void test(final ConnectRule connect) {
    final var cop = new BooleanIsNotCompleteCop(
      CopTest.args(MODEL),
      CopTest.testArgs
    );

    assertEquals(List.of(MyType.field), cop.getItems()); // Lists all boolean fields.
    connect.connect(MODEL);
    assertEquals(
      """
      SELECT COUNT(*) FROM "MyType" WHERE "field"=1 -- inspection fails if result is zero - for "field"=0 as well""",
      cop.getViolationSql(MyType.field)
    );

    assertEquals(0, cop.check(MyType.field)); // Does not fail if all values are null.

    try (var tx = MODEL.startTransactionTry("null item")) {
      new MyType((Boolean) null);
      tx.commit();
    }
    assertEquals(0, cop.check(MyType.field)); // Does not fail if all values are null.

    final MyType trueItem;
    try (var tx = MODEL.startTransactionTry("true item")) {
      trueItem = new MyType(true);
      tx.commit();
    }
    assertEquals(2, cop.check(MyType.field)); // Fails on all boolean fields, where there is not at least one item for false.

    try (var tx = MODEL.startTransactionTry("false item")) {
      new MyType(false);
      tx.commit();
    }
    assertEquals(0, cop.check(MyType.field)); // OK

    try (var tx = MODEL.startTransactionTry("delete true item")) {
      trueItem.deleteCopeItem();
      tx.commit();
    }
    assertEquals(1, cop.check(MyType.field)); // Fails on all boolean fields, where there is not at least one item for true.
  }

  private static final class MyType extends Item {

    @UsageEntryPoint
    static final BooleanField field = new BooleanField().optional();

    MyType(final Boolean field) {
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
