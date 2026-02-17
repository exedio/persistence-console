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

import static java.lang.System.nanoTime;

import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

final class SchemaPatchApi {

  static Response patch(final Model model, final Request request)
    throws ApiTextException {
    try {
      //noinspection UnnecessarySemicolon
      try (
        Connection con = SchemaInfo.newConnection(model);
        Statement stmt = con.createStatement();
      ) {
        final long start = nanoTime();
        final int rows = stmt.executeUpdate(request.sql);
        return new Response(rows, nanoTime() - start);
      }
    } catch (final Model.NotConnectedException e) {
      throw ApiTextException.onException(e);
    } catch (final SQLException e) {
      throw ApiTextException.onException(e);
    }
  }

  record Request(@JsonProperty(required = true) String sql) {
    @JsonCreator
    Request {}
  }

  private record Response(int rows, long elapsedNanos) {}

  private SchemaPatchApi() {
    // prevent instantiation
  }
}
