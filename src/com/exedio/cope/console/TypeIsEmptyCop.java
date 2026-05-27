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

import static com.exedio.cope.console.InspectionsCop.FAILS_WITH_ONE;
import static com.exedio.cope.console.InspectionsCop.failWithOne;

import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.List;

final class TypeIsEmptyCop extends TestCop<Type<?>> {

  static final String TAB = "typeIsEmpty";

  TypeIsEmptyCop(final Args args, final TestArgs testArgs) {
    super(TAB, "Type Is Empty", args, testArgs);
  }

  @Override
  String[] getHeadingHelp() {
    return new String[] {
      "There are no items of a type in database at all.",
      FAILS_WITH_ONE,
      "Lists all non-abstract types.",
    };
  }

  @Override
  protected TypeIsEmptyCop newArgs(final Args args) {
    return new TypeIsEmptyCop(args, testArgs);
  }

  @Override
  protected TypeIsEmptyCop newTestArgs(final TestArgs testArgs) {
    return new TypeIsEmptyCop(args, testArgs);
  }

  @Override
  List<Type<?>> getItems() {
    final ArrayList<Type<?>> result = new ArrayList<>();

    for (final Type<?> type : app.model.getTypes())
      if (!type.isAbstract()) result.add(type);

    return result;
  }

  @Override
  String getID(final Type<?> type) {
    return type.getID();
  }

  @Override
  Type<?> forID(final String id) {
    return app.model.getType(id);
  }

  @Override
  List<Column<Type<?>>> columns() {
    return COLUMNS;
  }

  private static final List<Column<Type<?>>> COLUMNS = List.of(
    column("Type", Type::toString)
  );

  @Override
  long check(final Type<?> type) {
    try (var tx = startTransaction()) {
      final boolean result = getQuery(type).total() == 0;
      tx.commit();
      return failWithOne(result);
    }
  }

  @Override
  String getViolationSql(final Type<?> type) {
    return SchemaInfo.total(getQuery(type));
  }

  private static Query<?> getQuery(final Type<?> type) {
    return type.newQuery();
  }
}
