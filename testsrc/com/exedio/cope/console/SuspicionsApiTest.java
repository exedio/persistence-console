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
import static com.exedio.cope.console.SuspicionsApi.suspicions;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import java.io.IOException;
import java.io.Serial;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SuspicionsApiTest {

  @Test
  void testSuspicions() throws IOException {
    assertEquals(
      """
      [ {
        "type" : "MyType",
        "name" : "myName",
        "suspicions" : [ "First example suspicion", "Second example suspicion" ]
      }, {
        "type" : "MyType",
        "name" : "myOnly",
        "suspicions" : [ "Only example suspicion" ]
      } ]""",
      writeJson(suspicions(MODEL))
    );
  }

  private static final class MyType extends Item {

    @UsageEntryPoint
    static final MyFeature myName = new MyFeature(
      "First example suspicion",
      "Second example suspicion"
    );

    @UsageEntryPoint
    static final MyFeature myOnly = new MyFeature("Only example suspicion");

    @UsageEntryPoint
    static final MyFeature myNone = new MyFeature();

    @java.io.Serial
    private static final long serialVersionUID = 1l;

    static final Type<?> TYPE = TypesBound.newType(MyType.class, MyType::new);

    private MyType(final ActivationParameters ap) {
      super(ap);
    }
  }

  private static final class MyFeature extends Pattern {

    final List<String> suspicions;

    private MyFeature(final String... suspicions) {
      this.suspicions = List.of(suspicions);
    }

    @Override
    public Collection<String> getSuspicions() {
      return suspicions;
    }

    @Serial
    private static final long serialVersionUID = 1l;
  }

  private static final Model MODEL = new Model(MyType.TYPE);

  @Test
  void testSuspicionsEmpty() throws IOException {
    assertEquals("[ ]", writeJson(suspicions(MODEL_EMPTY)));
  }

  private static final class MyTypeEmpty extends Item {

    @java.io.Serial
    private static final long serialVersionUID = 1l;

    static final Type<?> TYPE = TypesBound.newType(
      MyTypeEmpty.class,
      MyTypeEmpty::new
    );

    private MyTypeEmpty(final ActivationParameters ap) {
      super(ap);
    }
  }

  private static final Model MODEL_EMPTY = new Model(MyTypeEmpty.TYPE);
}
