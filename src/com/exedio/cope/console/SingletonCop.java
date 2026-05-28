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

import static com.exedio.cope.console.InspectionsCop.failWithOne;

import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.pattern.Singleton;
import java.util.List;

final class SingletonCop extends FeatureTestCop<Singleton>
{
	static final String TAB = "singleton";

	SingletonCop(final Args args, final TestArgs testArgs)
	{
		super(Singleton.class, TAB, "Singletons", args, testArgs);
	}

	@Override
	protected SingletonCop newArgs(final Args args)
	{
		return new SingletonCop(args, testArgs);
	}

	@Override
	protected SingletonCop newTestArgs(final TestArgs testArgs)
	{
		return new SingletonCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Verifies that Singletons are complete.",
			"This means, that the item exists.",

		};
	}

	@Override
	List<Column<Singleton>> columns()
	{
		return COLUMNS;
	}

	private static final List<Column<Singleton>> COLUMNS = List.of(
			column("Singleton", feature -> feature.getID())
	);

	@Override
	long check(final Singleton singleton)
	{
		try(var tx = startTransaction())
		{
			return failWithOne(
					tx.commit(getQuery(singleton).total()) != 1);
		}
	}

	private static Query<?> getQuery(final Singleton singleton)
	{
		return singleton.getType().newQuery();
	}

	@Override
	String getViolationSql(final Singleton singleton)
	{
		return SchemaInfo.total(getQuery(singleton)) + " -- inspection fails if result is less than 1";
	}
}
