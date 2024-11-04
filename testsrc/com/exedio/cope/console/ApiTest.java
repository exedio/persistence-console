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

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.console.Api.ResponseStatusException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

final class ApiTest {

  static String writeJson(final Object json) throws IOException {
    final Response response = new Response();
    Api.writeJson(json, response);
    return response.getOut();
  }

  private static final class Response
    extends AssertionFailedHttpServletResponse {

    private final Out out = new Out();
    private boolean outReturned = false;

    @Override
    public void setContentType(final String type) {
      assertEquals(APPLICATION_JSON, type);
    }

    @Override
    public ServletOutputStream getOutputStream() {
      assertFalse(outReturned);
      outReturned = true;
      return out;
    }

    String getOut() {
      assertTrue(outReturned);
      return out.back.toString(US_ASCII);
    }

    private static final class Out extends AssertionFailedServletOutputStream {

      private final ByteArrayOutputStream back = new ByteArrayOutputStream();

      @Override
      public void write(final int b) {
        back.write(b);
      }
    }
  }

  static <T> T readJson(final Class<T> jsonClass, final String body)
    throws IOException, ResponseStatusException {
    final Request request = new Request(body);
    final T result = Api.readJsonPost(jsonClass, request);
    assertTrue(request.methodCalled);
    request.assertCalled();
    return result;
  }

  private static final class Request extends AssertionFailedHttpServletRequest {

    private final In body;
    private boolean methodCalled = false;
    private boolean contentTypeCalled = false;
    private boolean inputStreamCalled = false;

    void assertCalled() {
      assertAll(
        () -> assertTrue(methodCalled),
        () -> assertTrue(contentTypeCalled),
        () -> assertTrue(inputStreamCalled)
      );
    }

    Request(final String body) {
      this.body = new In(body);
    }

    @Override
    public String getMethod() {
      assertFalse(methodCalled);
      methodCalled = true;
      return "POST";
    }

    @Override
    public String getContentType() {
      assertFalse(contentTypeCalled);
      contentTypeCalled = true;
      return APPLICATION_JSON;
    }

    @Override
    public ServletInputStream getInputStream() {
      assertFalse(inputStreamCalled);
      inputStreamCalled = true;
      return body;
    }

    private static final class In extends AssertionFailedServletInputStream {

      private final ByteArrayInputStream body;

      private In(final String body) {
        this.body = new ByteArrayInputStream(body.getBytes(UTF_8));
      }

      @Override
      public int read() {
        return body.read();
      }
    }
  }

  private static final String APPLICATION_JSON =
    "application/json;charset=UTF-8";

  private ApiTest() {
    // prevent instantiation
  }
}
