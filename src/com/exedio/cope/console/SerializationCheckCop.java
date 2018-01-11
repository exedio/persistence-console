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

import com.exedio.cope.Model;
import com.exedio.cope.misc.SerializationCheck;

final class SerializationCheckCop extends ConsoleCop<Void>
{
	static final String TAB = "serialization";

	SerializationCheckCop(final Args args)
	{
		super(TAB, "Serialization Check", args);
	}

	@Override
	protected SerializationCheckCop newArgs(final Args args)
	{
		return new SerializationCheckCop(args);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Checks classes of Items, Composites, and Blocks for fields that may obstruct serialization.",
			"Serialization is typically used for making an HttpSession survive a redeployment of the application. " +
				"The table lists all non-static and non-transient fields. " +
				"Fields with a non-Serializable type are shown in red.",
			"IMPACT: " +
				"A failure here means that serialization/deserialization may either fail and/or " +
				"render deserialized objects corrupt or useless."
		};
	}

	@Override
	ChecklistIcon getChecklistIcon(final Model model)
	{
		return
				SerializationCheck.check(model).isEmpty()
				? ChecklistIcon.ok
				: ChecklistIcon.error;
	}

	@Override
	void writeBody(final Out out)
	{
		SerializationCheck_Jspm.writeBody(
				out,
				SerializationCheck.check(out.model));
	}
}
