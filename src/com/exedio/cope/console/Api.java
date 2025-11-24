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

import static com.exedio.cope.console.ApiTextException.requireFound;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Api {

  static void doRequest(
    final ConsoleServlet servlet,
    final HttpServletRequest request,
    final String endpoint,
    final HttpServletResponse response,
    final Model model
  ) {
    try {
      doRequestInternal(servlet, request, endpoint, response, model);
    } catch (final RuntimeException | IOException e) {
      logger.error(e.getMessage(), e);
      response.setStatus(SC_INTERNAL_SERVER_ERROR);
    } catch (final ApiTextException e) {
      logger.error(e.getMessage(), e);
      e.respond(response);
    }
  }

  private static void doRequestInternal(
    final ConsoleServlet servlet,
    final HttpServletRequest request,
    final String endpoint,
    final HttpServletResponse response,
    final Model model
  ) throws IOException, ApiTextException {
    switch (endpoint) {
      case "connect" -> {
        requirePost(request);
        servlet.connect();
        writeJson(null, response);
      }
      case "hashes" -> {
        requireGet(request);
        writeJson(HashApi.hashes(model), response);
      }
      case "doHash" -> writeJson(
        HashApi.doHash(
          model,
          readJsonPost(HashApi.DoHashRequest.class, request)
        ),
        response
      );
      case "suspicions" -> {
        requireGet(request);
        writeJson(SuspicionsApi.suspicions(model), response);
      }
      case "schema" -> {
        requireGet(request);
        writeJson(SchemaAnalyzeApi.schema(model), response);
      }
      case "schema/alter" -> {
        requireGet(request);
        writeJson(SchemaAlterApi.alterSchema(model, request), response);
      }
      default -> throw ApiTextException.notFound("endpoint not found");
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
    ApiTextException.requireMethod("GET", request);
  }

  static <T> T readJsonPost(
    final Class<T> jsonClass,
    final HttpServletRequest request
  ) throws IOException, ApiTextException {
    requirePost(request);
    ApiTextException.requireContentType(APPLICATION_JSON, request);

    final ObjectReader reader = mapper.readerFor(jsonClass);
    final Object result;
    try (InputStream in = request.getInputStream()) {
      result = reader.readValue(in);
    } catch (final MismatchedInputException e) {
      final JsonLocation location = e.getLocation();
      throw ApiTextException.badRequest(
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

  private static void requirePost(final HttpServletRequest request)
    throws ApiTextException {
    ApiTextException.requireMethod("POST", request);
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
    final Type<?> type = requireFound(model.getType(typeId), "type", model);
    final Feature feature = requireFound(type.getFeature(name), "name", model);
    if (!(featureClass.isInstance(feature))) throw ApiTextException.notFound(
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

  static String requireParameter(
    final String name,
    final HttpServletRequest request
  ) throws ApiTextException {
    final String result = request.getParameter(name);
    if (result == null || result.isEmpty()) throw ApiTextException.notFound(
      "parameter " + name + " must be set"
    );
    return result;
  }

  private static final Logger logger = LoggerFactory.getLogger(Api.class);

  private Api() {
    // prevent instantiation
  }
}
