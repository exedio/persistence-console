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
import com.exedio.cope.console.ConsoleCop.Args;
import java.util.List;

final class SuspicionsCop {

  static final String TAB = "defaultToNow";

  static JsCop newCop(final Args args) {
    return new JsCop(
      TAB,
      "suspicions",
      "Suspicions",
      args,
      List.of(
        "Checks DateFields and DayFields whether their default constants are equal or close to time of model initialization.",
        "This means, that you probably should have used defaultToNow instead."
      ),
      SuspicionsCop::getChecklistIcon
    );
  }

  /**
   * Must be consistent to {@link SuspicionsApi#get(Model)}
   */
  static ChecklistIcon getChecklistIcon(final Model model) {
    for (final Type<?> t : model.getTypes())
      for (final Feature f : t.getDeclaredFeatures())
        if (!f.getSuspicions().isEmpty()) return ChecklistIcon.error;
    return ChecklistIcon.empty;
  }

  private SuspicionsCop() {
    // prevent instantiation
  }
}
