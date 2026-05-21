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

import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import java.util.List;

final class IsAlwaysNullCop extends FeatureTestCop<FunctionField<?>> {

  static final String TAB = "alwaysNull";

  IsAlwaysNullCop(final Args args, final TestArgs testArgs) {
    super(classWildcard(), TAB, "Is Always Null", args, testArgs);
  }

  @Override
  String[] getHeadingHelp() {
    return new String[] {
      "Value null only appears in database.",
      "Fails on all fields, where there is no item with a non-null value.",
      "Does not fail if there are no items at all (the table is empty).",
      "Lists all fields, that are optional.",
    };
  }

  @SuppressWarnings("unchecked")
  private static Class<FunctionField<?>> classWildcard() {
    return (Class<FunctionField<?>>) (Class<
      ? extends Feature
    >) FunctionField.class;
  }

  @Override
  protected IsAlwaysNullCop newArgs(final Args args) {
    return new IsAlwaysNullCop(args, testArgs);
  }

  @Override
  protected IsAlwaysNullCop newTestArgs(final TestArgs testArgs) {
    return new IsAlwaysNullCop(args, testArgs);
  }

  @Override
  boolean acceptsItem(final FunctionField<?> field) {
    return !field.isMandatory();
  }

  @Override
  List<Column<FunctionField<?>>> columns() {
    return COLUMNS;
  }

  private static final List<Column<FunctionField<?>>> COLUMNS = List.of(
    column("Field", FunctionField::toString)
  );

  @Override
  long check(final FunctionField<?> field) {
    final Type<?> type = field.getType();
    try (
      TransactionTry tx = app.model.startTransactionTry(
        "Console IsAlwaysNullCop " + id
      )
    ) {
      final boolean result =
        (type.newQuery(field.isNotNull()).total() == 0) &&
        (type.newQuery().total() > 0);
      tx.commit();
      return result ? 1 : 0;
    }
  }
}
