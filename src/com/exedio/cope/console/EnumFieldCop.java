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

import com.exedio.cope.EnumField;
import com.exedio.cope.Feature;
import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import java.util.List;

final class EnumFieldCop extends FeatureTestCop<EnumField<?>> {

  static final String TAB = "enumField";

  EnumFieldCop(final Args args, final TestArgs testArgs) {
    super(classWildcard(), TAB, "Enum Fields", args, testArgs);
  }

  @SuppressWarnings("unchecked")
  private static Class<EnumField<?>> classWildcard() {
    return (Class<EnumField<?>>) (Class<? extends Feature>) EnumField.class;
  }

  @Override
  protected EnumFieldCop newArgs(final Args args) {
    return new EnumFieldCop(args, testArgs);
  }

  @Override
  protected EnumFieldCop newTestArgs(final TestArgs testArgs) {
    return new EnumFieldCop(args, testArgs);
  }

  @Override
  List<Column<EnumField<?>>> columns() {
    return COLUMNS;
  }

  private static final List<Column<EnumField<?>>> COLUMNS = List.of(
    column("Field", field -> field.toString()),
    columnNonFilterable("Facets", field -> Format.format(expected(field)))
  );

  private static int expected(final EnumField<?> field) {
    return field.getValues().size();
  }

  @Override
  long check(final EnumField<?> field) {
    try (
      TransactionTry tx = app.model.startTransactionTry(
        "Console EnumFieldRedundancy " + id
      )
    ) {
      return Math.subtractExact(
        expected(field),
        tx.commit(getQuery(field).total())
      );
    }
  }

  private static Query<?> getQuery(final EnumField<?> field) {
    final Query<?> query = new Query<>(field);
    query.setDistinct(true);
    return query;
  }

  @Override
  String getViolationSql(final EnumField<?> field) {
    return SchemaInfo.total(getQuery(field)) + " < " + expected(field);
  }
}
