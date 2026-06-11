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

import com.exedio.cope.Model;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serial;
import java.util.List;
import java.util.Map;

public final class CopTest {

  private static ConsoleServlet servlet() {
    return new ConsoleServlet() {
      @Override
      public List<String> getMediaURLPrefixes(
        final HttpServletRequest request
      ) {
        return List.of("mupx");
      }

      @Serial
      private static final long serialVersionUID = 1L;
    };
  }

  static ConsoleCop.Args args(final Model model) {
    final var servlet = servlet();
    return new ConsoleCop.Args(
      new App(model, servlet),
      new ParameterRequest(Map.of("ar", "0", "dp", "m", "mup", "mupx")),
      servlet
    );
  }

  static final TestCop.TestArgs testArgs = new TestCop.TestArgs();

  private CopTest() {
    // prevent instantiation
  }
}
