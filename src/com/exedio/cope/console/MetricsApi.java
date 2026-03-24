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

import com.exedio.cope.Model;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

final class MetricsApi {

  @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
  static List<MyMeter> get(final Model model, final HttpServletRequest request)
    throws ApiTextException {
    final String prefixWithoutDot = requireParameter("prefix", request);
    {
      final String allowedPrefix = "com.exedio.cope";
      if (
        !prefixWithoutDot.startsWith(allowedPrefix)
      ) throw ApiTextException.notFound(
        "prefix must start with " + allowedPrefix
      );
    }
    final String prefix = prefixWithoutDot + '.';
    final String modelKey = "model";
    final String modelValue = model.toString();
    final ArrayList<Meter> resultMeters = new ArrayList<>();
    for (final Meter m : Metrics.globalRegistry.getMeters()) {
      final Meter.Id id = m.getId();
      final String name = id.getName();
      if (!name.startsWith(prefix)) continue;

      {
        final String modelActual = id.getTag(modelKey);
        if (modelActual != null && !modelActual.equals(modelValue)) continue;
      }

      resultMeters.add(m);
    }
    resultMeters.sort(Comparator.comparing(m -> m.getId().toString()));

    final ArrayList<MyMeter> result = new ArrayList<>();
    for (final Meter m : resultMeters) {
      final Meter.Id id = m.getId();
      final double count;
      if (m instanceof Counter) count = ((Counter) m).count();
      // TODO not covered by MetricsApiTest so far
      else if (m instanceof Timer) count = ((Timer) m).count();
      else continue;

      final TreeMap<String, String> tags = new TreeMap<>(); // repeatable order
      for (final var tag : id.getTags()) {
        final String key = tag.getKey();
        if (modelKey.equals(key)) continue;

        tags.put(key, tag.getValue());
      }
      result.add(new MyMeter(id.getName(), id.getDescription(), tags, count));
    }

    return result;
  }

  @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // OK: just json
  record MyMeter(
    String name,
    String description,
    Map<String, String> tags,
    double count
  ) {}

  private MetricsApi() {
    // prevent instantiation
  }
}
