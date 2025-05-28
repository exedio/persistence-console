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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

final class ApiTextException extends Exception {

  @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
  static void requireMethod(
    final String method,
    final HttpServletRequest request
  ) throws ApiTextException {
    if (!method.equals(request.getMethod())) throw new ApiTextException(
      SC_METHOD_NOT_ALLOWED,
      method + "required"
    );
  }

  private final int status;

  @Nonnull
  private final String body;

  ApiTextException(final int status, @Nonnull final String body) {
    this.status = status;
    this.body = requireNonNull(body);
  }

  ApiTextException(
    final int status,
    @Nonnull final String body,
    @Nonnull final Throwable cause
  ) {
    super(cause);
    this.status = status;
    this.body = requireNonNull(body);
  }

  void respond(final HttpServletResponse response) {
    try {
      response.setStatus(status);
      response.setContentType("text/plain;charset=UTF-8");
      try (OutputStream out = response.getOutputStream()) {
        out.write(body.getBytes(UTF_8));
      }
    } catch (final IOException io) {
      throw new RuntimeException(io);
    }
  }

  @Override
  public String getMessage() {
    return "" + status + ' ' + body;
  }

  @Serial
  private static final long serialVersionUID = 601159415713062976L;
}
