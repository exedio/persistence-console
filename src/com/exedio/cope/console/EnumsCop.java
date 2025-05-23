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

import com.exedio.cope.EnumField;
import com.exedio.cope.Field;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;

final class EnumsCop extends ConsoleCop<Void>
{
	static final String TAB = "enums";

	EnumsCop(final Args args)
	{
		super(TAB, "Enums", args);
	}

	@Override
	protected EnumsCop newArgs(final Args args)
	{
		return new EnumsCop(args);
	}

	EnumCop toClass(final Class<? extends Enum<?>> clazz)
	{
		return new EnumCop(args, clazz);
	}

	@Override
	void writeBody(final Out out)
	{
		final LinkedHashMap<Class<? extends Enum<?>>, ArrayList<EnumField<?>>> map =
			new LinkedHashMap<>();
		for(final Type<?> type : app.model.getTypes())
			for(final Field<?> field : type.getDeclaredFields())
				if(field instanceof final EnumField<?> f)
				{
					final Class<? extends Enum<?>> c = f.getValueClass();
					map.computeIfAbsent(c, k -> new ArrayList<>()).add(f);
				}
		Enums_Jspm.write(out, this, map, app.model.getChangeHookString());
	}
}
