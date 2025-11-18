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

import static com.exedio.cope.console.Api.requireParameter;
import static com.exedio.cope.console.ApiTextException.requireFound;

import com.exedio.cope.Model;
import com.exedio.dsmf.StatementListener;
import com.exedio.dsmf.Table;
import com.exedio.dsmf.misc.DefaultStatementListener;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

final class SchemaAlterApi {

  static SqlResponse alterSchema(
    final Model model,
    final HttpServletRequest request
  ) throws ApiTextException {
    final String subject = requireParameter("subject", request);
    final String name = requireParameter("name", request);
    final String method = requireParameter("method", request);
    final String ADD = "add";
    final String DROP = "drop";
    final String RENAME = "rename";
    final String MODIFY = "modify";
    final String METHOD404 = "method not found";
    return switch (subject) {
      case "table" -> {
        final var node = getTable(model, name);
        yield switch (method) {
          case ADD -> apply(node::create);
          case DROP -> apply(node::drop);
          case RENAME -> {
            final String value = requireParameter("value", request);
            yield apply(l -> node.renameTo(value, l));
          }
          default -> throw ApiTextException.notFound(METHOD404);
        };
      }
      case "column" -> {
        final var node = requireFound(
          table(model, request).getColumn(name),
          "column",
          model
        );
        yield switch (method) {
          case ADD -> apply(node::create);
          case DROP -> apply(node::drop);
          case RENAME -> {
            final String value = requireParameter("value", request);
            yield apply(l -> node.renameTo(value, l));
          }
          case MODIFY -> apply(sl -> node.modify(node.getRequiredType(), sl));
          default -> throw ApiTextException.notFound(METHOD404);
        };
      }
      case "constraint" -> {
        final var node = requireFound(
          table(model, request).getConstraint(name),
          "constraint",
          model
        );
        yield switch (method) {
          case ADD -> apply(node::create);
          case DROP -> apply(node::drop);
          default -> throw ApiTextException.notFound(METHOD404);
        };
      }
      case "sequence" -> {
        final var node = requireFound(
          model.getVerifiedSchema().getSequence(name),
          "sequence",
          model
        );
        yield switch (method) {
          case ADD -> apply(node::create);
          case DROP -> apply(node::drop);
          default -> throw ApiTextException.notFound(METHOD404);
        };
      }
      default -> throw ApiTextException.notFound("subject not found");
    };
  }

  private static Table table(
    final Model model,
    final HttpServletRequest request
  ) throws ApiTextException {
    return getTable(model, requireParameter("table", request));
  }

  private static Table getTable(final Model model, final String name)
    throws ApiTextException {
    return requireFound(
      model.getVerifiedSchema().getTable(name),
      "table",
      model
    );
  }

  private static SqlResponse apply(final Consumer<StatementListener> listener) {
    final AtomicReference<String> sql = new AtomicReference<>();
    final DefaultStatementListener sl = new DefaultStatementListener() {
      @Override
      public boolean beforeExecute(final String statement) {
        sql.set(statement);
        return false;
      }
    };
    listener.accept(sl);
    return new SqlResponse(sql.get());
  }

  private record SqlResponse(String sql) {}

  private SchemaAlterApi() {
    // prevent instantiation
  }
}
