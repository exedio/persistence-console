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

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serial;
import javax.annotation.Nonnull;

final class ApiTextException extends Exception {

  static <T> T requireFound(final T obj, final String name, final Model model)
    throws ApiTextException {
    if (obj == null) throw notFound(name + " not found within " + model);
    return obj;
  }

  @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
  static void requireMethod(
    final String method,
    final HttpServletRequest request
  ) throws ApiTextException {
    if (!method.equals(request.getMethod())) throw new ApiTextException(
      SC_METHOD_NOT_ALLOWED,
      method + " required"
    );
  }

  @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
  static void requireContentType(
    final String contentType,
    final HttpServletRequest request
  ) throws ApiTextException {
    if (
      !contentType.equals(request.getContentType())
    ) throw new ApiTextException(
      SC_UNSUPPORTED_MEDIA_TYPE,
      "Content-Type " + contentType + " required"
    );
  }

  static ApiTextException notFound(final String body) {
    return new ApiTextException(SC_NOT_FOUND, body);
  }

  static ApiTextException badRequest(final String body, final Exception e) {
    return new ApiTextException(SC_BAD_REQUEST, body, e);
  }

  private final int status;

  @Nonnull
  private final String body;

  private ApiTextException(final int status, @Nonnull final String body) {
    this.status = status;
    this.body = requireNonNull(body);
    switch (status) {
      case SC_METHOD_NOT_ALLOWED, SC_NOT_FOUND, SC_UNSUPPORTED_MEDIA_TYPE:
        break;
      default:
        throw new RuntimeException("status not allowed: " + status);
    }
  }

  private ApiTextException(
    final int status,
    @Nonnull final String body,
    @Nonnull final Throwable cause
  ) {
    super(cause);
    this.status = status;
    this.body = requireNonNull(body);
    //noinspection SwitchStatementWithTooFewBranches
    switch (status) {
      case SC_BAD_REQUEST:
        break;
      default:
        throw new RuntimeException("status not allowed: " + status);
    }
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
