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
import static com.exedio.cope.SchemaInfo.quoteName;

import com.exedio.cope.EnumField;
import com.exedio.cope.Field;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

final class EnumRenumber {

  static List<String> get(
    final Class<? extends Enum<?>> clazz,
    final Model model
  ) {
    final List<String> result = new ArrayList<>();
    for (final Type<?> type : model.getTypes())
      for (final Field<?> field : type.getDeclaredFields())
        if (
          field instanceof final EnumField<?> enumField &&
          enumField.getValueClass() == clazz
        ) {
          final String s = get(clazz, enumField, model);
          if (s != null) result.add(s);
        }

    return result.isEmpty() ? null : result;
  }

  static String get(
    final Class<? extends Enum<?>> clazz,
    final EnumField<?> field,
    final Model model
  ) {
    final StringBuilder sb = new StringBuilder();
    final LinkedHashMap<Integer, Integer> mapping = new LinkedHashMap<>();
    {
      int target = 10;
      for (final Enum<?> constant : clazz.getEnumConstants()) {
        final int source = SchemaInfo.getColumnValue(constant);
        if (source != target) if (
          mapping.put(source, target) != null
        ) throw new RuntimeException(constant.name());

        target += 10;
      }
    }
    if (mapping.isEmpty()) return null;

    final String column = quoteName(model, getColumnName(field));
    sb.append("UPDATE ")
      .append(quoteName(model, getTableName(field.getType())))
      .append(" SET ")
      .append(column)
      // https://dev.mysql.com/doc/refman/9.7/en/flow-control-functions.html#operator_case
      // https://www.postgresql.org/docs/current/functions-conditional.html#FUNCTIONS-CASE
      .append(" = CASE ")
      .append(column);

    for (final var e : mapping.entrySet()) {
      final int source = e.getKey();
      final int target = e.getValue();
      sb.append(" WHEN ").append(source).append(" THEN ").append(target);
    }

    sb
      .append(" ELSE ") // without ELSE result is null
      .append(column)
      .append(" END WHERE ")
      .append(column)
      .append(" IN (");

    boolean first = true;
    for (final var e : mapping.entrySet()) {
      if (first) first = false;
      else sb.append(',');

      final int source = e.getKey();
      sb.append(source);
    }
    sb.append(')');

    return sb.toString();
  }

  private EnumRenumber() {
    // prevent instantiation
  }
}
