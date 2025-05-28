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

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Api {

  static void doRequest(
    final HttpServletRequest request,
    final String endpoint,
    final HttpServletResponse response,
    final Model model
  ) {
    try {
      doRequestInternal(request, endpoint, response, model);
    } catch (final RuntimeException | IOException e) {
      logger.error(e.getMessage(), e);
      response.setStatus(SC_INTERNAL_SERVER_ERROR);
    } catch (final ApiTextException e) {
      logger.error(e.getMessage(), e);
      e.respond(response);
    }
  }

  private static void doRequestInternal(
    final HttpServletRequest request,
    final String endpoint,
    final HttpServletResponse response,
    final Model model
  ) throws IOException, ApiTextException {
    switch (endpoint) {
      case "hashes" -> {
        requireGet(request);
        writeJson(HashCop.hashes(model), response);
      }
      case "doHash" -> writeJson(
        HashCop.doHash(
          model,
          readJsonPost(HashCop.DoHashRequest.class, request)
        ),
        response
      );
      case "suspicions" -> {
        requireGet(request);
        writeJson(SuspicionsCop.suspicions(model), response);
      }
      case "schema" -> {
        requireGet(request);
        writeJson(SchemaNewCop.schema(model), response);
      }
      default -> response.setStatus(SC_NOT_FOUND);
    }
  }

  static void writeJson(final Object json, final HttpServletResponse response)
    throws IOException {
    response.setContentType(APPLICATION_JSON);
    try (OutputStream out = response.getOutputStream()) {
      mapper.writeValue(out, json); // encodes in UTF8
    }
  }

  private static void requireGet(final HttpServletRequest request)
    throws ApiTextException {
    if (!"GET".equals(request.getMethod())) throw new ApiTextException(
      SC_METHOD_NOT_ALLOWED,
      "GET required"
    );
  }

  static <T> T readJsonPost(
    final Class<T> jsonClass,
    final HttpServletRequest request
  ) throws IOException, ApiTextException {
    if (!"POST".equals(request.getMethod())) throw new ApiTextException(
      SC_METHOD_NOT_ALLOWED,
      "POST required"
    );
    if (
      !APPLICATION_JSON.equals(request.getContentType())
    ) throw new ApiTextException(
      SC_UNSUPPORTED_MEDIA_TYPE,
      "Content-Type " + APPLICATION_JSON + " required"
    );

    final ObjectReader reader = mapper.readerFor(jsonClass);
    final Object result;
    try (InputStream in = request.getInputStream()) {
      result = reader.readValue(in);
    } catch (final MismatchedInputException e) {
      final JsonLocation location = e.getLocation();
      throw new ApiTextException(
        SC_BAD_REQUEST,
        e.getOriginalMessage() +
        " / " +
        "line: " +
        location.getLineNr() +
        ", column: " +
        location.getColumnNr() +
        " / " +
        e.getPathReference(),
        e
      );
    }
    return jsonClass.cast(result);
  }

  private static final String APPLICATION_JSON =
    "application/json;charset=UTF-8";

  @Nonnull
  static <F extends Feature> F resolveFeature(
    final Model model,
    final String typeId,
    final String name,
    final Class<F> featureClass
  ) throws ApiTextException {
    final Type<?> type = model.getType(typeId);
    if (type == null) throw new ApiTextException(
      SC_NOT_FOUND,
      "type not found within " + model
    );
    final Feature feature = type.getFeature(name);
    if (feature == null) throw new ApiTextException(
      SC_NOT_FOUND,
      "name not found within " + model
    );
    if (!(featureClass.isInstance(feature))) throw new ApiTextException(
      SC_NOT_FOUND,
      "name not a " + featureClass.getName() + " within " + model
    );
    return featureClass.cast(feature);
  }

  static final ObjectMapper mapper = createObjectMapper();

  private static ObjectMapper createObjectMapper() {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }

  private static final Logger logger = LoggerFactory.getLogger(Api.class);

  private Api() {
    // prevent instantiation
  }
}
