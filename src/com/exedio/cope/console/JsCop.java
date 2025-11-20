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

import static com.exedio.cope.console.Console_Jspm.writeJsComponentMountPoint;

import com.exedio.cope.Model;
import java.util.List;
import java.util.function.Function;

final class JsCop extends ConsoleCop<Void> {

  private final String tab;
  private final String id;
  private final List<String> help;
  private final Function<Model, ChecklistIcon> checklistIcon;

  JsCop(
    final String tab,
    final String id,
    final String name,
    final Args args,
    final List<String> help,
    final Function<Model, ChecklistIcon> checklistIcon
  ) {
    super(tab, name, args);
    this.tab = tab;
    this.id = id;
    //noinspection AssignmentOrReturnOfFieldWithMutableType
    this.help = help;
    this.checklistIcon = checklistIcon;
  }

  @Override
  protected JsCop newArgs(final Args args) {
    return new JsCop(tab, id, name, args, help, checklistIcon);
  }

  @Override
  String[] getHeadingHelp() {
    return help.toArray(EMPTY_STRING_ARRAY);
  }

  @Override
  boolean hasJsComponent() {
    return true;
  }

  @Override
  ChecklistIcon getChecklistIcon() {
    return checklistIcon != null
      ? checklistIcon.apply(app.model)
      : super.getChecklistIcon();
  }

  @Override
  void writeBody(final Out out) {
    writeJsComponentMountPoint(out, id);
  }
}
