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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.quoteName;
import static com.exedio.cope.console.InspectionsCop.NO_FAILURE_ON_EMPTY;
import static com.exedio.cope.console.InspectionsCop.SQL_FAILS_IF_ZERO;
import static com.exedio.cope.console.InspectionsCop.noFailureOnEmpty;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import java.util.List;

final class BooleanIsNotCompleteCop extends FeatureTestCop<BooleanField> {

  static final String TAB = "booleanComplete";

  BooleanIsNotCompleteCop(final Args args, final TestArgs testArgs) {
    super(BooleanField.class, TAB, "Boolean Is Not Complete", args, testArgs);
  }

  @Override
  String[] getHeadingHelp() {
    return new String[] {
      "Either true or false does not appear in database.",
      "Fails on all boolean fields, where there is not at least one item for both true and false.",
      "Count of failures is one (1), if there is no item with value true.",
      "Count of failures is two (2), if there is no item with value false.",
      NO_FAILURE_ON_EMPTY,
      "Lists all boolean fields.",
    };
  }

  @Override
  protected BooleanIsNotCompleteCop newArgs(final Args args) {
    return new BooleanIsNotCompleteCop(args, testArgs);
  }

  @Override
  protected BooleanIsNotCompleteCop newTestArgs(final TestArgs testArgs) {
    return new BooleanIsNotCompleteCop(args, testArgs);
  }

  @Override
  List<Column<BooleanField>> columns() {
    return COLUMNS;
  }

  private static final List<Column<BooleanField>> COLUMNS = List.of(
    column("Field", field -> field.toString())
  );

  @Override
  long check(final BooleanField field) {
    try (var tx = startTransaction()) {
      if (noFailureOnEmpty(field)) return tx.commit(0);

      if (getQuery(field, true).total() == 0) return tx.commit(1);
      if (getQuery(field, false).total() == 0) return tx.commit(2);

      return tx.commit(0);
    }
  }

  private static Query<?> getQuery(
    final BooleanField field,
    final boolean value
  ) {
    return field.getType().newQuery(field.is(value));
  }

  @Override
  String getViolationSql(final BooleanField field) {
    return (
      SchemaInfo.total(getQuery(field, true)) +
      SQL_FAILS_IF_ZERO +
      " - for " +
      quoteName(field.getType().getModel(), getColumnName(field)) +
      "=0 as well"
    );
  }
}
