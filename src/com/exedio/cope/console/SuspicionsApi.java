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

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class SuspicionsApi {

  /**
   * Must be consistent to {@link SuspicionsCop#getChecklistIcon()}.
   */
  @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
  static List<SuspicionsResponse> suspicions(final Model model) {
    ArrayList<SuspicionsResponse> result = null;
    for (final Type<?> t : model.getTypes())
      for (final Feature f : t.getDeclaredFeatures()) {
        final Collection<String> suspicions = f.getSuspicions();
        if (!suspicions.isEmpty()) {
          if (result == null) result = new ArrayList<>();

          result.add(
            new SuspicionsResponse(
              t.getID(),
              f.getName(),
              List.copyOf(suspicions)
            )
          );
        }
      }
    return result != null ? result : List.of();
  }

  private record SuspicionsResponse(
    String type,
    String name,
    List<String> suspicions
  ) {}

  private SuspicionsApi() {
    // prevent instantiation
  }
}
