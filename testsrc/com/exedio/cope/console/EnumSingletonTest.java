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
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.pattern.EnumSingleton;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConnectRule.class)
public class EnumSingletonTest {

  @Test
  void test(final ConnectRule connect) {
    final var cop = new EnumSingletonCop(CopTest.args(MODEL), CopTest.testArgs);

    assertEquals(List.of(MyType.singleton), cop.getItems());
    connect.connect(MODEL);
    assertEquals("""
      SELECT COUNT(*) FROM "MyType" -- inspection fails if result is less than 3""",
      cop.getViolationSql(MyType.singleton)
    );

    assertEquals(3, cop.check(MyType.singleton));

    try (var tx = MODEL.startTransactionTry("the A item")) {
      new MyType(MyEnum.A);
      tx.commit();
    }
    assertEquals(2, cop.check(MyType.singleton));

    try (var tx = MODEL.startTransactionTry("the B item")) {
      new MyType(MyEnum.B);
      tx.commit();
    }
    assertEquals(1, cop.check(MyType.singleton));

    try (var tx = MODEL.startTransactionTry("the C item")) {
      new MyType(MyEnum.C);
      tx.commit();
    }
    assertEquals(0, cop.check(MyType.singleton));
  }

  private enum MyEnum {
    A,
    B,
    C,
  }

  private static final class MyType extends Item {

    @UsageEntryPoint
    static final EnumSingleton<MyEnum> singleton = EnumSingleton.create(
      MyEnum.class
    );

    @UsageEntryPoint
    static final StringField otherFeature = new StringField().optional();

    MyType(final MyEnum singleton) {
      super(SetValue.map(MyType.singleton, singleton));
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
