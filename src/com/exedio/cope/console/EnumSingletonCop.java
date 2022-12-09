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
import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.EnumSingleton;
import java.util.ArrayList;
import java.util.List;

final class EnumSingletonCop extends TestCop<EnumSingleton<?>>
{
	static final String TAB = "enumsingleton";

	EnumSingletonCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Enum Singletons", args, testArgs);
	}

	@Override
	protected EnumSingletonCop newArgs(final Args args)
	{
		return new EnumSingletonCop(args, testArgs);
	}

	@Override
	protected EnumSingletonCop newTestArgs(final TestArgs testArgs)
	{
		return new EnumSingletonCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Verifies that EnumSingletons are complete, " +
				"that is for each facet of the enum there is an item as well.",
		};
	}

	@Override
	List<EnumSingleton<?>> getItems(final Model model)
	{
		final ArrayList<EnumSingleton<?>> result = new ArrayList<>();

		for(final Type<?> type : model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof EnumSingleton)
					result.add((EnumSingleton<?>)feature);

		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"EnumSingleton", "Enum class"};
	}

	@Override
	void writeValue(final Out out, final EnumSingleton<?> singleton, final int h)
	{
		switch(h)
		{
			case 0: out.write(singleton.getID()); break;
			case 1: out.write(singleton.getOnce().getValueClass()); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		}
	}

	@Override
	String getID(final EnumSingleton<?> singleton)
	{
		return singleton.getID();
	}

	@Override
	EnumSingleton<?> forID(final Model model, final String id)
	{
		return (EnumSingleton<?>)model.getFeature(id);
	}

	@Override
	long check(final EnumSingleton<?> singleton, final Model model)
	{
		try(TransactionTry tx = model.startTransactionTry("Console EnumSingleton " + id))
		{
			return Math.subtractExact(
					expected(singleton),
					tx.commit(getQuery(singleton).total()));
		}
	}

	private static int expected(final EnumSingleton<?> singleton)
	{
		return singleton.getOnce().getValueClass().getEnumConstants().length;
	}

	private static Query<?> getQuery(final EnumSingleton<?> singleton)
	{
		return singleton.getType().newQuery();
	}

	@Override
	String getViolationSql(final EnumSingleton<?> singleton, final Model model)
	{
		return SchemaInfo.total(getQuery(singleton)) + " < " + expected(singleton);
	}
}
