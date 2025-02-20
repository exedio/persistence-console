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
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  static SchemaResponse schema(final Model model) {
    return new SchemaResponse(model.getVerifiedSchema());
  }

  record SchemaResponse(
    List<TableResponse> tables,
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
    List<ColumnResponse> columns,
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

  record TableError(Existence existence, String[] remainder) {
    static TableError convert(final Table t) {
      Existence existence = null;
      String remainder = t.getError();
      if (remainder != null) {
        switch (remainder) {
          case MISSING -> {
            existence = Existence.missing;
            remainder = null;
          }
          case UNUSED -> {
            existence = Existence.unused;
            remainder = null;
          }
        }
      }
      return existence != null || remainder != null
        ? new TableError(existence, splitRemainder(remainder))
        : null;
    }
  }

  record ColumnResponse(
    String name,
    String type,
    ColumnError error,
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

  record ColumnError(Existence existence, String type, String[] remainder) {
    static ColumnError convert(final Existence tableExistence, final Column c) {
      Existence existence = null;
      String type = null;
      String remainder = c.getError();
      if (remainder != null) {
        switch (remainder) {
          case MISSING -> {
            existence = Existence.missing;
            remainder = null;
          }
          case UNUSED -> {
            existence = Existence.unused;
            remainder = null;
          }
          default -> {
            final Matcher mType = UNEXPECTED_TYPE.matcher(remainder);
            if (mType.matches()) {
              type = mType.group(1);
              remainder = null;
            }
          }
        }
      }
      existence = filterContainer(tableExistence, existence);
      return existence != null || type != null || remainder != null
        ? new ColumnError(existence, type, splitRemainder(remainder))
        : null;
    }
    private static final Pattern UNEXPECTED_TYPE = Pattern.compile(
      "^unexpected type >(.*)<" // Should be replaced by explicit API
    );
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
        c.getRequiredCondition(),
        ConstraintError.convert(tableExistence, c)
      );
    }
    static List<ConstraintResponse> convert(
      final Existence tableExistence,
      final Collection<Constraint> list
    ) {
      return map(
        list,
        c -> c.getRequiredCondition() == null || c.isSupported(),
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
    return result.isEmpty() ? null : result;
  }

  record ConstraintError(
    Existence existence,
    String clause,
    String[] remainder
  ) {
    static ConstraintError convert(
      final Existence tableExistence,
      final Constraint c
    ) {
      Existence existence = null;
      String clause = null;
      String remainder = c.getError();
      if (remainder != null) {
        switch (remainder) {
          case MISSING -> {
            existence = Existence.missing;
            remainder = null;
          }
          case UNUSED -> {
            existence = Existence.unused;
            remainder = null;
          }
          default -> {
            final Matcher mType = UNEXPECTED_CLAUSE.matcher(remainder);
            if (mType.matches()) {
              clause = mType.group(1);
              remainder = null;
            }
          }
        }
      }
      existence = filterContainer(tableExistence, existence);
      return existence != null || clause != null || remainder != null
        ? new ConstraintError(existence, clause, splitRemainder(remainder))
        : null;
    }
    private static final Pattern UNEXPECTED_CLAUSE = Pattern.compile(
      "^unexpected condition >>>(.*)<<<.*$" // must allow more characters for (originally >>>...<<<), should be replaced by explicit API
    );
  }

  private static Existence filterContainer(
    final Existence container,
    final Existence own
  ) {
    return (container == own) ? null : own;
  }

  record SequenceResponse(
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

  record SequenceError(
    Existence existence,
    String type,
    Long start,
    String[] remainder
  ) {
    static SequenceError convert(final Sequence s) {
      Existence existence = null;
      String type = null;
      Long start = null;
      String remainder = s.getError();
      if (remainder != null) {
        switch (remainder) {
          case MISSING -> {
            existence = Existence.missing;
            remainder = null;
          }
          case UNUSED -> {
            existence = Existence.unused;
            remainder = null;
          }
          default -> {
            {
              final Matcher mType = UNEXPECTED_TYPE.matcher(remainder);
              if (mType.matches()) {
                type = mType.group(1);
                remainder = null;
              } else {
                final Matcher mStart = UNEXPECTED_START.matcher(remainder);
                if (mStart.matches()) {
                  start = Long.parseLong(mStart.group(1));
                  remainder = null;
                }
              }
            }
          }
        }
      }

      return (
          existence != null ||
          type != null ||
          start != null ||
          remainder != null
        )
        ? new SequenceError(existence, type, start, splitRemainder(remainder))
        : null;
    }

    private static final Pattern UNEXPECTED_TYPE = Pattern.compile(
      "^unexpected type (\\w*)$" // Should be replaced by explicit API
    );
    private static final Pattern UNEXPECTED_START = Pattern.compile(
      "^unexpected start (\\d*)$" // Should be replaced by explicit API
    );
  }

  enum Existence {
    missing,
    unused,
  }

  private static final String MISSING = "missing"; // Should be replaced by explicit API
  private static final String UNUSED = "unused"; // Should be replaced by explicit API

  private static String[] splitRemainder(final String s) {
    return s != null ? s.split(", ") : null;
  }
}
