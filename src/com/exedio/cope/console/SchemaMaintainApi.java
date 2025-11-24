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
import com.exedio.dsmf.SQLRuntimeException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class SchemaMaintainApi {

  static Response maintain(final Model model, final Request request)
    throws ApiTextException {
    final long start;
    try {
      start = System.nanoTime();
      switch (request.operation) {
        case create -> model.createSchema();
        case tearDown -> model.tearDownSchema();
        case drop -> model.dropSchema();
        case delete -> model.deleteSchema();
      }
      return new Response(System.nanoTime() - start);
    } catch (final Model.NotConnectedException e) {
      throw ApiTextException.onException(e);
    } catch (final SQLRuntimeException e) {
      throw ApiTextException.onException(e);
    }
  }

  enum Operation {
    create,
    tearDown,
    drop,
    delete,
  }

  record Request(@JsonProperty(required = true) Operation operation) {
    @JsonCreator
    Request {}
  }

  private record Response(long elapsedNanos) {}

  private SchemaMaintainApi() {
    // prevent instantiation
  }
}
