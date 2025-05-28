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

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import javax.annotation.Nonnull;

final class ApiTextException extends Exception {

  final int status;

  @Nonnull
  final String body;

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

  @Override
  public String getMessage() {
    return "" + status + ' ' + body;
  }

  @Serial
  private static final long serialVersionUID = 601159415713062976L;
}
