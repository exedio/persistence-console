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
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.EnumSingleton;
import com.exedio.cope.pattern.Singleton;
import java.util.ArrayList;
import java.util.List;

final class SingletonCop extends TestCop<SingletonCop.Line>
{
	static final String TAB = "singleton";

	SingletonCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "[Enum]Singletons", args, testArgs);
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
			"Verifies that [Enum]Singletons are complete.",
			"For Singleton this means, that the item exists.",
			"For EnumSingleton this means, that for each facet of the enum there is an item as well.",
		};
	}

	@Override
	List<Line> getItems()
	{
		final ArrayList<Line> result = new ArrayList<>();

		for(final Type<?> type : app.model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
			{
				final Line line = wrap(feature);
				if(line!=null)
					result.add(line);
			}

		return result;
	}

	private static Line wrap(final Feature feature)
	{
		if(feature instanceof final Singleton s)
			return new SingleLine(s);
		else if(feature instanceof final EnumSingleton<?> s)
			return new EnumLine(s);
		else
			return null;
	}

	@Override
	List<Column<Line>> columns()
	{
		return COLUMNS;
	}

	private static final List<Column<Line>> COLUMNS = List.of(
			column("[Enum]Singleton", x -> x.feature.getID()),
			column("Enum class", (out, singleton) -> singleton.writeEnumClass(out))
	);

	@Override
	String getID(final Line singleton)
	{
		return singleton.feature.getID();
	}

	@Override
	Line forID(final String id)
	{
		final Line feature = wrap(app.model.getFeature(id));
		if(feature==null)
			throw new ClassCastException(id);
		return feature;
	}

	@Override
	long check(final Line singleton)
	{
		try(TransactionTry tx = app.model.startTransactionTry("Console [Enum]Singleton " + id))
		{
			return Math.subtractExact(
					singleton.expected(),
					tx.commit(getQuery(singleton).total()));
		}
	}

	private static Query<?> getQuery(final Line singleton)
	{
		return singleton.feature.getType().newQuery();
	}

	@Override
	String getViolationSql(final Line singleton)
	{
		return SchemaInfo.total(getQuery(singleton)) + " < " + singleton.expected();
	}

	abstract static class Line
	{
		final Pattern feature;

		Line(final Pattern feature)
		{
			this.feature = feature;
		}

		abstract void writeEnumClass(Out out);

		abstract int expected();
	}

	private static final class SingleLine extends Line
	{
		SingleLine(final Singleton feature)
		{
			super(feature);
		}

		@Override
		void writeEnumClass(final Out out)
		{
			out.write("-");
		}

		@Override
		int expected()
		{
			return 1;
		}
	}

	private static final class EnumLine extends Line
	{
		private final EnumSingleton<?> feature;

		EnumLine(final EnumSingleton<?> feature)
		{
			super(feature);
			this.feature = feature;
		}

		@Override
		void writeEnumClass(final Out out)
		{
			out.write(feature.getOnce().getValueClass());
		}

		@Override
		int expected()
		{
			return feature.getOnce().getValueClass().getEnumConstants().length;
		}
	}
}
