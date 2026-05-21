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
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.SchemaInfo.quoteName;
import static java.util.Locale.ENGLISH;

import com.exedio.cope.DateField;
import com.exedio.cope.DateField.Precision;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.dsmf.SQLRuntimeException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

final class DatePrecisionCop extends FeatureTestCop<DateField> {

  static final String TAB = "dateFieldPrecision";

  DatePrecisionCop(final Args args, final TestArgs testArgs) {
    super(DateField.class, TAB, "Date Has No Milliseconds", args, testArgs);
  }

  @Override
  String[] getHeadingHelp() {
    return new String[] {
      "Milliseconds are allowed, but do not appear in database.",
      "Fails on all date fields, where there is no item with non-zero milliseconds part of the value.",
      "Does not fail if all values are null.",
      "Lists all date fields, that allow milliseconds (getPrecision()==MILLI).",
      "This check currently works on MySQL only.",
    };
  }

  @Override
  protected DatePrecisionCop newArgs(final Args args) {
    return new DatePrecisionCop(args, testArgs);
  }

  @Override
  protected DatePrecisionCop newTestArgs(final TestArgs testArgs) {
    return new DatePrecisionCop(args, testArgs);
  }

  @Override
  boolean acceptsItem(final DateField field) {
    return field.getPrecision() == Precision.MILLI;
  }

  @Override
  List<Column<DateField>> columns() {
    return COLUMNS;
  }

  private static final List<Column<DateField>> COLUMNS = List.of(
    column("Field", DateField::toString),
    columnNonFilterable("Precision", field ->
      field.getPrecision().name().toLowerCase(ENGLISH)
    )
  );

  @Override
  long check(final DateField field) {
    final long all;
    final long even;
    try (
      Connection con = newConnection(app.model);
      Statement st = con.createStatement()
    ) {
      try (ResultSet rs = st.executeQuery(getSQL(field, false))) {
        rs.next();
        all = rs.getLong(1);
      }
      if (all == 0) return 0;

      try (ResultSet rs = st.executeQuery(getSQL(field, true))) {
        rs.next();
        even = rs.getLong(1);
      }
    } catch (final SQLException e) {
      throw new SQLRuntimeException(e, field.toString());
    }
    return (all == even) ? 1 : 0;
  }

  @Override
  String getViolationSql(final DateField field) {
    if (!field.getType().getModel().isConnected()) return "NOT YET CONNECTED";
    return getSQL(field, true);
  }

  private static String getSQL(final DateField field, final boolean even) {
    final Type<?> type = field.getType();
    final Model model = type.getModel();

    return (
      "SELECT COUNT(*) FROM " +
      quoteName(model, getTableName(type)) +
      " WHERE " +
      quoteName(model, getColumnName(field)) +
      " IS NOT NULL" +
      (even
        ? (" AND EXTRACT(MICROSECOND FROM " +
            quoteName(model, getColumnName(field)) +
            ")=0")
        : "")
    );
  }
}
