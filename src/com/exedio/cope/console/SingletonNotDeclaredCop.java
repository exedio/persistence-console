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
import com.exedio.cope.pattern.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 * Maybe I want to merge this inspection into {@link TypeIsEmptyCop}
 * with a different "Count of failures" for distinction.
 */
final class SingletonNotDeclaredCop extends TestCop<Type<?>> {

  static final String TAB = "singletonNotDeclared";

  SingletonNotDeclaredCop(final Args args, final TestArgs testArgs) {
    super(TAB, "Singleton Is Not Declared", args, testArgs);
  }

  @Override
  String[] getHeadingHelp() {
    return new String[] {
      "There is just one item of a type in database, but the type is no singleton.",
      FAILS_WITH_ONE,
      "Does not fail if there are no items of that type in database at all.",
      "Lists all non-abstract, non-singleton types.",
    };
  }

  @Override
  protected SingletonNotDeclaredCop newArgs(final ConsoleCop.Args args) {
    return new SingletonNotDeclaredCop(args, testArgs);
  }

  @Override
  protected SingletonNotDeclaredCop newTestArgs(
    final TestCop.TestArgs testArgs
  ) {
    return new SingletonNotDeclaredCop(args, testArgs);
  }

  @Override
  List<Type<?>> getItems() {
    final ArrayList<Type<?>> result = new ArrayList<>();

    typeLoop: for (final Type<?> type : app.model.getTypes())
      if (!type.isAbstract()) {
        for (final var f : type.getFeatures())
          if (f instanceof Singleton) continue typeLoop;

        result.add(type);
      }

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
  List<TestCop.Column<Type<?>>> columns() {
    return COLUMNS;
  }

  private static final List<TestCop.Column<Type<?>>> COLUMNS = List.of(
    column("Type", Type::toString)
  );

  @Override
  long check(final Type<?> type) {
    try (var tx = startTransaction()) {
      final long total = getQuery(type).total();
      tx.commit();
      return failWithOne(total == 1); // do not fail on zero, because TypeIsEmptyCop fails on zero already
    }
  }

  @Override
  String getViolationSql(final Type<?> type) {
    return (
      SchemaInfo.total(getQuery(type)) +
      " -- inspection fails if result is one (1)"
    );
  }

  private static Query<?> getQuery(final Type<?> type) {
    return type.newQuery();
  }
}
