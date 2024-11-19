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

import com.exedio.dsmf.Node;

enum ChecklistIcon
{
	empty    { @Override void write(final Out out) { Console_Jspm.writeOkGrey (out, true); }},
	ok       { @Override void write(final Out out) { Console_Jspm.writeOk     (out, true); }},
	warning  { @Override void write(final Out out) { Console_Jspm.writeWarning(out, true); }},
	error    { @Override void write(final Out out) { Console_Jspm.writeError  (out, true); }},
	unknown  { @Override void write(final Out out) { Console_Jspm.writeUnknown(out, true); }};

	abstract void write(Out out);

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	static ChecklistIcon forColor(final Node.Color color)
	{
		return switch(color)
		{
			case OK      -> ok;
			case WARNING -> warning;
			case ERROR   -> error;
		};
	}
}
