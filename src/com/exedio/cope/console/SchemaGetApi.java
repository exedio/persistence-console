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

import com.exedio.cope.Model;
import com.exedio.dsmf.Node;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

final class SchemaGetApi {

  @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
  static Schema get(final Model model) throws ApiTextException {
    try {
      return new Schema(model.getVerifiedSchema());
    } catch (final Model.NotConnectedException e) {
      throw ApiTextException.onException(e);
    }
  }

  record Schema(
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just json
    List<Table> tables,
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just json
    List<Sequence> sequences
  ) {
    Schema(final com.exedio.dsmf.Schema s) {
      this(
        map(s.getTables(), Table::convert),
        map(s.getSequences(), Sequence::convert)
      );
    }
  }

  record Table(
    String name,
    Existence existence,
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just json
    List<String> additionalErrors,
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just json
    List<Column> columns,
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just json
    List<Constraint> constraints
  ) {
    static Table convert(final com.exedio.dsmf.Table t) {
      final Existence existence = Existence.forNode(t);
      return new Table(
        t.getName(),
        existence,
        emptyToNull(t.getAdditionalErrors()),
        map(t.getColumns(), c -> Column.convert(existence, c)),
        Constraint.convert(existence, t.getTableConstraints())
      );
    }
  }

  record Column(
    String name,
    String type,
    Existence existence,
    Boolean toleratesInsertIfUnused,
    String mismatchingType,
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just json
    List<String> additionalErrors,
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just json
    List<Constraint> constraints
  ) {
    static Column convert(
      final Existence tableExistence,
      final com.exedio.dsmf.Column c
    ) {
      final Existence existence = filterContainer(
        tableExistence,
        Existence.forNode(c)
      );
      return new Column(
        c.getName(),
        c.getType(),
        existence,
        existence == Existence.unused && c.toleratesInsertIfUnused()
          ? Boolean.TRUE
          : null,
        c.getMismatchingType(),
        emptyToNull(c.getAdditionalErrors()),
        Constraint.convert(tableExistence, c.getConstraints())
      );
    }
  }

  private record Constraint(
    String name,
    String type,
    String clause,
    Existence existence,
    String mismatchingClause,
    String mismatchingClauseRaw,
    List<String> additionalErrors
  ) {
    private static Constraint convert(
      final Existence tableExistence,
      final com.exedio.dsmf.Constraint c
    ) {
      final String clause = c.getMismatchingCondition();
      final String clauseRaw = c.getMismatchingConditionRaw();
      return new Constraint(
        c.getName(),
        c.getType().name(),
        c.getCondition(),
        filterContainer(tableExistence, Existence.forNode(c)),
        clause,
        Objects.equals(clause, clauseRaw) ? null : clauseRaw,
        emptyToNull(c.getAdditionalErrors())
      );
    }

    static List<Constraint> convert(
      final Existence tableExistence,
      final Collection<com.exedio.dsmf.Constraint> list
    ) {
      return map(
        list,
        c -> !c.required() || c.isSupported(),
        c -> convert(tableExistence, c)
      );
    }
  }

  private static <T, R> List<R> map(
    final Collection<T> c,
    final Function<T, R> mapper
  ) {
    return map(c, i -> true, mapper);
  }

  private static <T, R> List<R> map(
    final Collection<T> c,
    final Predicate<? super T> predicate,
    final Function<T, R> mapper
  ) {
    final List<R> result = c.stream().filter(predicate).map(mapper).toList();
    return emptyToNull(result);
  }

  private static Existence filterContainer(
    final Existence container,
    final Existence own
  ) {
    return (container == own) ? null : own;
  }

  private record Sequence(
    String name,
    com.exedio.dsmf.Sequence.Type type,
    long start,
    Existence existence,
    com.exedio.dsmf.Sequence.Type mismatchingType,
    Long mismatchingStart,
    List<String> additionalErrors
  ) {
    static Sequence convert(final com.exedio.dsmf.Sequence s) {
      return new Sequence(
        s.getName(),
        s.getType(),
        s.getStartL(),
        Existence.forNode(s),
        s.getMismatchingType(),
        s.getMismatchingStart(),
        emptyToNull(s.getAdditionalErrors())
      );
    }
  }

  private enum Existence {
    missing,
    unused;

    static Existence forNode(final Node n) {
      // prettier-ignore
      if (n.required())
        if (n.exists())
          return null;
        else
          return missing;
      else
        if (n.exists())
          return unused;
        else
          throw new RuntimeException(n.toString());
    }
  }

  private static <E> List<E> emptyToNull(final List<E> l) {
    return l.isEmpty() ? null : l;
  }

  private SchemaGetApi() {
    // prevent instantiation
  }
}
