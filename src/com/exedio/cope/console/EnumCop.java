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

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.EnumField;
import com.exedio.cope.Field;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.Type;

final class EnumCop extends ConsoleCop
{
	private static final String CLASS = "cs";

	final Class<? extends Enum> clazz;

	EnumCop(final Args args, final Class<? extends Enum> clazz)
	{
		super(TAB_ENUM, clazz==null ? "Enums" : ("Enums - " + clazz.getName()), args);
		this.clazz = clazz;

		if(clazz!=null)
			addParameter(CLASS, clazz.getName());
	}

	static final EnumCop getEnumCop(final Args args, final HttpServletRequest request)
	{
		final String classString = request.getParameter(CLASS);
		final Class<? extends Enum> clazz;
		if(classString!=null)
		{
			try
			{
				clazz = Class.forName(classString).asSubclass(Enum.class);
			}
			catch(final ClassNotFoundException e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			clazz = null;
		}

		return new EnumCop(args, clazz);
	}

	@Override
	protected EnumCop newArgs(final Args args)
	{
		return new EnumCop(args, clazz);
	}

	EnumCop toClass(final Class<? extends Enum> clazz)
	{
		return new EnumCop(args, clazz);
	}

	@Override
	final void writeBody(final Out out)
	{
		if(clazz==null)
		{
			final Model model = out.model;

			final LinkedHashMap<Class<? extends Enum>, ArrayList<EnumField<?>>> map =
				new LinkedHashMap<Class<? extends Enum>, ArrayList<EnumField<?>>>();
			for(final Type<?> type : model.getTypes())
				for(final Field field : type.getDeclaredFields())
					if(field instanceof EnumField)
					{
						final EnumField<?> f = (EnumField)field;
						final Class<? extends Enum> c = f.getValueClass();
						ArrayList<EnumField<?>> list = map.get(c);
						if(list==null)
						{
							list = new ArrayList<EnumField<?>>();
							map.put(c, list);
						}
						list.add(f);
					}
			Enum_Jspm.writeBodyList(out, this, map);
		}
		else
			Enum_Jspm.writeBodyClass(out, clazz);
	}

	@SuppressWarnings("unchecked")
	static int getColumnValue(final Enum constant)
	{
		return SchemaInfo.getColumnValue(constant);
	}
}
