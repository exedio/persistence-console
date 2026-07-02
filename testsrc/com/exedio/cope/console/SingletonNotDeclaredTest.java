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
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.pattern.Singleton;
import com.exedio.cope.util.UsageEntryPoint;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConnectRule.class)
public class SingletonNotDeclaredTest {

  @Test
  void test(final ConnectRule connect) {
    final var cop = new SingletonNotDeclaredCop(
      CopTest.args(MODEL),
      CopTest.testArgs
    );

    assertEquals(List.of(MyType.TYPE), cop.getItems()); // Lists all non-abstract, non-singleton types.
    connect.connect(MODEL);
    assertEquals(
      """
      SELECT COUNT(*) FROM "MyType" -- inspection fails if result is one (1)""",
      cop.getViolationSql(MyType.TYPE)
    );

    assertEquals(0, cop.check(MyType.TYPE)); // Does not fail if there are no items of that type in database at all.

    try (var tx = MODEL.startTransactionTry("first item")) {
      new MyType();
      tx.commit();
    }
    assertEquals(1, cop.check(MyType.TYPE)); // There is just one item of a type in database.

    try (var tx = MODEL.startTransactionTry("second item")) {
      new MyType();
      tx.commit();
    }
    assertEquals(0, cop.check(MyType.TYPE)); // OK
  }

  private abstract static class MyAbstractType extends Item {

    @java.io.Serial
    private static final long serialVersionUID = 1l;

    static final Type<?> TYPE = TypesBound.newTypeAbstract(
      MyAbstractType.class
    );

    MyAbstractType() {}

    private MyAbstractType(final ActivationParameters ap) {
      super(ap);
    }
  }

  private static final class MyType extends MyAbstractType {

    @java.io.Serial
    private static final long serialVersionUID = 1l;

    static final Type<?> TYPE = TypesBound.newType(MyType.class, MyType::new);

    MyType() {}

    private MyType(final ActivationParameters ap) {
      super(ap);
    }
  }

  private static final class MySingletonType extends Item {

    @UsageEntryPoint
    static final Singleton singleton = new Singleton();

    @java.io.Serial
    private static final long serialVersionUID = 1l;

    static final Type<?> TYPE = TypesBound.newType(
      MySingletonType.class,
      MySingletonType::new
    );

    private MySingletonType(final ActivationParameters ap) {
      super(ap);
    }
  }

  private static final Model MODEL = new Model(
    MyType.TYPE,
    MyAbstractType.TYPE,
    MySingletonType.TYPE
  );
}
