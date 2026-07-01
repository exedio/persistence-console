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

import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.SchemaInfo.quoteName;
import static com.exedio.cope.console.InspectionsCop.NO_FAILURE_ON_EMPTY;
import static com.exedio.cope.console.InspectionsCop.noFailureOnEmpty;

import com.exedio.cope.Feature;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.dsmf.SQLRuntimeException;
import java.sql.SQLException;
import java.util.List;

final class ItemIsNotCompleteCop extends FeatureTestCop<ItemField<?>> {

  static final String TAB = "itemNotComplete";

  ItemIsNotCompleteCop(final Args args, final TestArgs testArgs) {
    super(classWildcard(), TAB, "Item Is Not Complete", args, testArgs);
  }

  @Override
  String[] getHeadingHelp() {
    return new String[] {
      "Some subtypes of a value type of an item field do not appear in database.",
      "Fails on all item fields, where there is not at least one item for each subtype of the value type.",
      "Count of failures is the number of subtypes not found.",
      NO_FAILURE_ON_EMPTY,
      "Lists all item fields where the value type has subtypes.",
    };
  }

  @SuppressWarnings("unchecked")
  private static Class<ItemField<?>> classWildcard() {
    return (Class<ItemField<?>>) (Class<? extends Feature>) ItemField.class;
  }

  @Override
  protected ItemIsNotCompleteCop newArgs(final Args args) {
    return new ItemIsNotCompleteCop(args, testArgs);
  }

  @Override
  protected ItemIsNotCompleteCop newTestArgs(final TestArgs testArgs) {
    return new ItemIsNotCompleteCop(args, testArgs);
  }

  @Override
  boolean acceptsItem(final ItemField<?> field) {
    return expected(field).size() > 1;
  }

  @Override
  List<Column<ItemField<?>>> columns() {
    return COLUMNS;
  }

  private static final List<Column<ItemField<?>>> COLUMNS = List.of(
    column("Field", field -> field.toString()),
    columnNonFilterable("Subtypes", field -> expected(field).toString())
  );

  private static List<? extends Type<?>> expected(final ItemField<?> field) {
    return field.getValueType().getTypesOfInstances();
  }

  @Override
  long check(final ItemField<?> field) {
    try (var tx = startTransaction()) {
      if (noFailureOnEmpty(field)) return tx.commit(0);
      tx.commit();
    }

    final int actual;
    try (
      var con = newConnection(app.model);
      var stm = con.createStatement();
      var rst = stm.executeQuery(getQuery(field))
    ) {
      rst.next();
      actual = rst.getInt(1);
    } catch (final SQLException e) {
      throw new SQLRuntimeException(e, field.toString());
    }
    return Math.subtractExact(expected(field).size(), actual);
  }

  private static String getQuery(final ItemField<?> field) {
    final Type<?> type = field.getType();
    final Model model = type.getModel();
    final String columnQuoted = quoteName(model, getTypeColumnName(field));
    return (
      "SELECT COUNT(*) FROM ( SELECT DISTINCT " +
      columnQuoted +
      " FROM " +
      quoteName(model, getTableName(type)) +
      " WHERE " +
      columnQuoted +
      " IS NOT NULL )"
    );
  }

  @Override
  String getViolationSql(final ItemField<?> field) {
    return (
      getQuery(field) +
      " -- inspection fails if result is less than " +
      expected(field).size()
    );
  }
}
