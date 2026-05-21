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

import static java.lang.String.valueOf;

import com.exedio.cope.Condition;
import com.exedio.cope.DoubleField;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.LongField;
import com.exedio.cope.NumberField;
import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.Type;
import java.util.List;

final class NumberIsNotNegativeCop extends FeatureTestCop<NumberField<?>> {

  static final String TAB = "numberNotNegative";

  NumberIsNotNegativeCop(final Args args, final TestArgs testArgs) {
    super(classWildcard(), TAB, "Number Is Not Negative", args, testArgs);
  }

  @Override
  String[] getHeadingHelp() {
    return new String[] {
      "Negative values are allowed, but do not appear in database.",
      "Fails on all number fields, where there is no item with a negative value.",
      "Does not fail if all values are null or there are no values at all.",
      "Lists all number fields, that allow negative values (getMinimum()<0).",
    };
  }

  @SuppressWarnings("unchecked")
  private static Class<NumberField<?>> classWildcard() {
    return (Class<NumberField<?>>) (Class<? extends Feature>) NumberField.class;
  }

  @Override
  protected NumberIsNotNegativeCop newArgs(final Args args) {
    return new NumberIsNotNegativeCop(args, testArgs);
  }

  @Override
  protected NumberIsNotNegativeCop newTestArgs(final TestArgs testArgs) {
    return new NumberIsNotNegativeCop(args, testArgs);
  }

  @Override
  boolean acceptsItem(final NumberField<?> field) {
    // TODO use NumberField#isMinimumLessZero when available in cope
    if (field instanceof final IntegerField f) return f.getMinimum() < 0;
    if (field instanceof final LongField f) return f.getMinimum() < 0;
    if (field instanceof final DoubleField f) return f.getMinimum() < 0;
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
    })
  );

  @Override
  long check(final NumberField<?> field) {
    final Type<?> type = field.getType();
    try (
      var tx = startTransaction()
    ) {
      if (type.newQuery(field.isNotNull()).total() == 0) return tx.commit(0);

      final long total = getQuery(field).total();
      tx.commit();
      return total == 0 ? 1 : 0;
    }
  }

  @Override
  String getViolationSql(final NumberField<?> field) {
    return SchemaInfo.total(getQuery(field));
  }

  private static Query<?> getQuery(final NumberField<?> field) {
    final Condition condition;
    // TODO use NumberField#lessZero when available in cope
    if (field instanceof final IntegerField f) condition = f.less(0);
    else if (field instanceof final LongField f) condition = f.less(0l);
    else if (field instanceof final DoubleField f) condition = f.less(0.0);
    else throw new RuntimeException(field.toString());

    return field.getType().newQuery(condition);
  }
}
