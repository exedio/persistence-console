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

import com.exedio.cope.SchemaInfo;

final class EnumCop<E extends Enum<E>> extends ConsoleCop<Void>
{
	private static final String CLASS = "cs";

	final Class<E> clazz;

	EnumCop(final Args args, final Class<E> clazz)
	{
		super(TAB_ENUM, "Enum - " + clazz.getName(), args);
		this.clazz = clazz;
		addParameter(CLASS, clazz.getName());
	}

	static final EnumCop<?> getEnumCop(final Args args, final HttpServletRequest request)
	{
		final String classString = request.getParameter(CLASS);
		final Class<? extends Enum> clazz;
		try
		{
			clazz = Class.forName(classString).asSubclass(Enum.class);
		}
		catch(final ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}

		return new EnumCop(args, clazz);
	}

	@Override
	protected EnumCop<E> newArgs(final Args args)
	{
		return new EnumCop<E>(args, clazz);
	}

	<X extends Enum<X>> EnumCop<X> toClass(final Class<X> clazz)
	{
		return new EnumCop<X>(args, clazz);
	}

	@Override
	final void writeBody(final Out out)
	{
		Enum_Jspm.write(out, clazz);
	}

	@SuppressWarnings("unchecked")
	static int getColumnValue(final Enum constant)
	{
		return SchemaInfo.getColumnValue(constant);
	}
}
