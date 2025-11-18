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

import static com.exedio.cope.console.Console_Jspm.writeJsComponentMountPoint;

import com.exedio.cope.Model;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Node;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

final class SchemaNewCop extends ConsoleCop<Void> {

  static final String TAB = "schemaNew";

  SchemaNewCop(final Args args) {
    super(TAB, "Schema", args);
  }

  @Override
  protected SchemaNewCop newArgs(final Args args) {
    return new SchemaNewCop(args);
  }

  @Override
  boolean hasJsComponent() {
    return true;
  }

  @Override
  void writeBody(final Out out) {
    writeJsComponentMountPoint(out, "schema");
  }

  @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
  static SchemaResponse schema(final Model model) throws ApiTextException {
    try {
      return new SchemaResponse(model.getVerifiedSchema());
    } catch (final Model.NotConnectedException e) {
      throw ApiTextException.badRequest(e.getMessage(), e); // TODO should be SC_FORBIDDEN
    }
  }

  record SchemaResponse(
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just json
    List<TableResponse> tables,
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just json
    List<SequenceResponse> sequences
  ) {
    SchemaResponse(final Schema s) {
      this(
        map(s.getTables(), TableResponse::convert),
        map(s.getSequences(), SequenceResponse::convert)
      );
    }
  }

  record TableResponse(
    String name,
    TableError error,
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just json
    List<ColumnResponse> columns,
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just json
    List<ConstraintResponse> constraints
  ) {
    static TableResponse convert(final Table t) {
      final TableError error = TableError.convert(t);
      final Existence existence = error != null ? error.existence : null;
      return new TableResponse(
        t.getName(),
        error,
        map(t.getColumns(), c -> new ColumnResponse(existence, c)),
        ConstraintResponse.convert(existence, t.getTableConstraints())
      );
    }
  }

  private record TableError(
    Existence existence,
    List<String> remainder
  ) {
    static TableError convert(final Table t) {
      final Existence existence = Existence.forNode(t);
      final List<String> remainder = emptyToNull(t.getAdditionalErrors());
      return existence != null || remainder != null
        ? new TableError(existence, remainder)
        : null;
    }
  }

  record ColumnResponse(
    String name,
    String type,
    ColumnError error,
    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just json
    List<ConstraintResponse> constraints
  ) {
    ColumnResponse(final Existence tableExistence, final Column c) {
      this(
        c.getName(),
        c.getType(),
        ColumnError.convert(tableExistence, c),
        ConstraintResponse.convert(tableExistence, c.getConstraints())
      );
    }
  }

  private record ColumnError(
    Existence existence,
    Boolean toleratesInsertIfUnused,
    String type,
    List<String> remainder
  ) {
    static ColumnError convert(final Existence tableExistence, final Column c) {
      final Existence existence = filterContainer(
        tableExistence,
        Existence.forNode(c)
      );
      final String type = c.getMismatchingType();
      final boolean toleratesInsertIfUnused =
        existence == Existence.unused && c.toleratesInsertIfUnused();
      final List<String> remainder = emptyToNull(c.getAdditionalErrors());
      return existence != null || type != null || remainder != null
        ? new ColumnError(
          existence,
          toleratesInsertIfUnused ? Boolean.TRUE : null,
          type,
          remainder
        )
        : null;
    }
  }

  private record ConstraintResponse(
    String name,
    String type,
    String clause,
    ConstraintError error
  ) {
    private static ConstraintResponse convert(
      final Existence tableExistence,
      final Constraint c
    ) {
      return new ConstraintResponse(
        c.getName(),
        c.getType().name(),
        c.getCondition(),
        ConstraintError.convert(tableExistence, c)
      );
    }

    static List<ConstraintResponse> convert(
      final Existence tableExistence,
      final Collection<Constraint> list
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

  private record ConstraintError(
    Existence existence,
    String clause,
    String clauseRaw,
    List<String> remainder
  ) {
    static ConstraintError convert(
      final Existence tableExistence,
      final Constraint c
    ) {
      final Existence existence = filterContainer(
        tableExistence,
        Existence.forNode(c)
      );
      final String clause = c.getMismatchingCondition();
      final String clauseRaw = c.getMismatchingConditionRaw();
      final List<String> remainder = emptyToNull(c.getAdditionalErrors());
      return existence != null || clause != null || remainder != null
        ? new ConstraintError(
          existence,
          clause,
          Objects.equals(clause, clauseRaw) ? null : clauseRaw,
          remainder
        )
        : null;
    }
  }

  private static Existence filterContainer(
    final Existence container,
    final Existence own
  ) {
    return (container == own) ? null : own;
  }

  private record SequenceResponse(
    String name,
    String type,
    long start,
    SequenceError error
  ) {
    static SequenceResponse convert(final Sequence s) {
      return new SequenceResponse(
        s.getName(),
        s.getType().name(),
        s.getStartL(),
        SequenceError.convert(s)
      );
    }
  }

  private record SequenceError(
    Existence existence,
    Sequence.Type type,
    Long start,
    List<String> remainder
  ) {
    static SequenceError convert(final Sequence s) {
      final Existence existence = Existence.forNode(s);
      final Sequence.Type type = s.getMismatchingType();
      final Long start = s.getMismatchingStart();
      final List<String> remainder = emptyToNull(s.getAdditionalErrors());
      return (
          existence != null ||
          type != null ||
          start != null ||
          remainder != null
        )
        ? new SequenceError(existence, type, start, remainder)
        : null;
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
}
