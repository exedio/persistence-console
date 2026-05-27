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
import static com.exedio.cope.console.InspectionsCop.NO_FAILURE_ON_EMPTY;
import static com.exedio.cope.console.InspectionsCop.SQL_FAILS_IF_ZERO;
import static com.exedio.cope.console.InspectionsCop.failWithOne;
import static com.exedio.cope.console.InspectionsCop.noFailureOnEmpty;
import static java.lang.String.valueOf;

import com.exedio.cope.Condition;
import com.exedio.cope.DoubleField;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.LongField;
import com.exedio.cope.NumberField;
import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import java.util.List;

final class NumberIsNotZeroCop extends FeatureTestCop<NumberField<?>> {

  static final String TAB = "numberNotZero";

  NumberIsNotZeroCop(final Args args, final TestArgs testArgs) {
    super(classWildcard(), TAB, "Number Is Not Zero", args, testArgs);
  }

  @Override
  String[] getHeadingHelp() {
    return new String[] {
      "Value zero is allowed, but does not appear in database.",
      "Fails on all number fields, where there is no item with a value of zero.",
      FAILS_WITH_ONE,
      NO_FAILURE_ON_EMPTY,
      "Lists all number fields, that allow zero (getMinimum() <= 0 <= getMaximum()).",
    };
  }

  @SuppressWarnings("unchecked")
  private static Class<NumberField<?>> classWildcard() {
    return (Class<NumberField<?>>) (Class<? extends Feature>) NumberField.class;
  }

  @Override
  protected NumberIsNotZeroCop newArgs(final Args args) {
    return new NumberIsNotZeroCop(args, testArgs);
  }

  @Override
  protected NumberIsNotZeroCop newTestArgs(final TestArgs testArgs) {
    return new NumberIsNotZeroCop(args, testArgs);
  }

  @Override
  boolean acceptsItem(final NumberField<?> field) {
    // TODO use NumberField#isMinimumLessOrEqualZero and #isMaximumGreaterOrEqualZero when available in cope
    if (field instanceof final IntegerField f) return (
      f.getMinimum() <= 0 && 0 <= f.getMaximum()
    );
    if (field instanceof final LongField f) return (
      f.getMinimum() <= 0 && 0 <= f.getMaximum()
    );
    if (field instanceof final DoubleField f) return (
      f.getMinimum() <= 0 && 0 <= f.getMaximum()
    );
    else throw new RuntimeException(field.toString());
  }

  @Override
  List<Column<NumberField<?>>> columns() {
    return COLUMNS;
  }

  private static final List<Column<NumberField<?>>> COLUMNS = List.of(
    column("Field", NumberField::toString),
    column("Type", field -> field.getValueClass().getSimpleName()),
    columnNonFilterable("Minimum", field -> {
      // TODO use some generic function when available in cope
      if (field instanceof final IntegerField f) return valueOf(f.getMinimum());
      if (field instanceof final LongField f) return valueOf(f.getMinimum());
      if (field instanceof final DoubleField f) return valueOf(f.getMinimum());
      else throw new RuntimeException(field.toString());
    }),
    columnNonFilterable("Maximum", field -> {
      // TODO use some generic function when available in cope
      if (field instanceof final IntegerField f) return valueOf(f.getMaximum());
      if (field instanceof final LongField f) return valueOf(f.getMaximum());
      if (field instanceof final DoubleField f) return valueOf(f.getMaximum());
      else throw new RuntimeException(field.toString());
    })
  );

  @Override
  long check(final NumberField<?> field) {
    try (var tx = startTransaction()) {
      if (!noFailureOnEmpty(field)) return tx.commit(0);

      final long total = getQuery(field).total();
      tx.commit();
      return failWithOne(total == 0);
    }
  }

  @Override
  String getViolationSql(final NumberField<?> field) {
    return SchemaInfo.total(getQuery(field)) + SQL_FAILS_IF_ZERO;
  }

  private static Query<?> getQuery(final NumberField<?> field) {
    final Condition condition;
    // TODO use NumberField#isZero when available in cope
    if (field instanceof final IntegerField f) condition = f.is(0);
    else if (field instanceof final LongField f) condition = f.is(0l);
    else if (field instanceof final DoubleField f) condition = f.is(0.0);
    else throw new RuntimeException(field.toString());

    return field.getType().newQuery(condition);
  }
}
