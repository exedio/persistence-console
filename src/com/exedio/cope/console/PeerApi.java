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

import static java.net.HttpURLConnection.HTTP_OK;
import static java.time.Duration.ofSeconds;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

final class PeerApi {

  static boolean forwarded(
    final ConsoleServlet servlet,
    final HttpServletRequest request,
    final HttpServletResponse response
  ) throws ApiTextException {
    final String peer = request.getHeader("X-Forward-To-Peer"); // MUST be consistent to api.ts#myFetch
    if (peer == null) return false;

    if (!servlet.getPeers().contains(peer)) throw ApiTextException.notFound(
      "peer not found"
    );

    final URI peerUri;
    try {
      peerUri = new URI(
        peer +
          request.getContextPath() +
          request.getServletPath() +
          request.getPathInfo() +
          '?' + // TODO handle missing query string
          request.getQueryString()
      );
    } catch (final URISyntaxException e) {
      throw ApiTextException.badRequest("URI", e);
    }
    final HttpRequest.Builder peerRequest = HttpRequest.newBuilder(peerUri);
    copyRequestHeader("Authorization", request, peerRequest);
    try {
      final HttpResponse<InputStream> peerResponse = httpClient.send(
        peerRequest.build(),
        info ->
          info.statusCode() == HTTP_OK
            ? HttpResponse.BodySubscribers.ofInputStream()
            : HttpResponse.BodySubscribers.replacing(null)
      );
      final int responseCode = peerResponse.statusCode();
      if (responseCode != HTTP_OK) throw ApiTextException.badRequest(
        "non-" + HTTP_OK + " from peer: " + responseCode,
        null
      );

      copyResponseHeader("Content-Type", peerResponse, response);
      try (var in = peerResponse.body(); var out = response.getOutputStream()) {
        in.transferTo(out);
      }
    } catch (final IOException e) {
      throw ApiTextException.badRequest("IOException", e);
    } catch (final InterruptedException e) {
      throw ApiTextException.badRequest("InterruptedException", e);
    }
    return true;
  }

  private static void copyRequestHeader(
    final String name,
    final HttpServletRequest from,
    final HttpRequest.Builder to
  ) {
    final String value = from.getHeader(name);
    if (value != null) to.setHeader(name, value);
  }

  private static void copyResponseHeader(
    final String name,
    final HttpResponse<?> from,
    final HttpServletResponse to
  ) {
    from
      .headers()
      .firstValue(name)
      .ifPresent(value -> to.setHeader(name, value));
  }

  private static final HttpClient httpClient = HttpClient.newBuilder() // TODO close it when HttpClient becomes AutoClosable in JDK 21
    .connectTimeout(ofSeconds(10)) // TODO allow customization
    .followRedirects(HttpClient.Redirect.NEVER)
    .proxy(HttpClient.Builder.NO_PROXY) // must not use any proxy for peers
    .build();

  private PeerApi() {
    // prevent instantiation
  }
}
