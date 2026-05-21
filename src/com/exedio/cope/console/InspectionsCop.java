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

final class InspectionsCop extends ConsoleCop<Void> {

  static final String TAB = "inspections";

  InspectionsCop(final Args args) {
    super(TAB, "Inspections", args);
  }

  @Override
  protected InspectionsCop newArgs(final Args args) {
    return new InspectionsCop(args);
  }

  @Override
  String[] getHeadingHelp() {
    return new String[] {
      "Organizes inspections into a list. " +
      "All items listed here can be found somewhere else in the menu as well.",
    };
  }

  @Override
  void writeBody(final Out out) {
    final TestCop.TestArgs testArgs = new TestCop.TestArgs();
    Inspections_Jspm.writeBody(
      out,
      new ConsoleCop<?>[] {
        new TypeIsEmptyCop(args, testArgs),
        new IsNotNullCop(args, testArgs),
        new IsAlwaysNullCop(args, testArgs),
        new StringIsNotEmptyCop(args, testArgs),
        new NumberIsNotNegativeCop(args, testArgs),
        new EnumIsNotCompleteCop(args, testArgs),
        new DatePrecisionCop(args, testArgs),
      }
    );
  }
}
