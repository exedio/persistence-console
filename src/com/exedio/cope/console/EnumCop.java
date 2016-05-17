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

import javax.servlet.http.HttpServletRequest;

final class EnumCop extends ConsoleCop<Void>
{
	private static final String CLASS = "cs";

	final Class<? extends Enum<?>> clazz;

	EnumCop(final Args args, final Class<? extends Enum<?>> clazz)
	{
		super(TAB_ENUM, "Enum - " + clazz.getName(), args);
		this.clazz = clazz;
		addParameter(CLASS, clazz.getName());
	}

	static final EnumCop getEnumCop(final Args args, final HttpServletRequest request)
	{
		final String classString = request.getParameter(CLASS);
		final Class<? extends Enum<?>> clazz;
		try
		{
			clazz = cast(Class.forName(classString).asSubclass(Enum.class));
		}
		catch(final ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}

		return new EnumCop(args, clazz);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static final Class<? extends Enum<?>> cast(final Class<? extends Enum> clazz)
	{
		return (Class<? extends Enum<?>>)clazz;
	}

	@Override
	protected EnumCop newArgs(final Args args)
	{
		return new EnumCop(args, clazz);
	}

	@Override
	final void writeBody(final Out out)
	{
		Enum_Jspm.write(out, clazz);
	}
}
