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
import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.Dispatcher;
import java.util.ArrayList;
import java.util.List;

final class DispatcherFailureDeprecatedCop extends TestCop<Dispatcher>
{
	static final String TAB = "dispatcherfailuredeprecated";

	DispatcherFailureDeprecatedCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Dispatcher Deprecated Failures", args, testArgs);
	}

	@Override
	protected DispatcherFailureDeprecatedCop newArgs(final Args args)
	{
		return new DispatcherFailureDeprecatedCop(args, testArgs);
	}

	@Override
	protected DispatcherFailureDeprecatedCop newTestArgs(final TestArgs testArgs)
	{
		return new DispatcherFailureDeprecatedCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Checks usage of Dispatcher.Result#failure, which is deprecated.",
		};
	}

	@Override
	List<Dispatcher> getItems()
	{
		final ArrayList<Dispatcher> result = new ArrayList<>();

		for(final Type<?> type : app.model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof Dispatcher)
					result.add((Dispatcher)feature);

		return result;
	}

	@Override
	List<Column<Dispatcher>> columns()
	{
		return COLUMNS;
	}

	private static final List<Column<Dispatcher>> COLUMNS = List.of(
			column("Dispatcher", Dispatcher::getID)
	);

	@Override
	String getID(final Dispatcher dispatcher)
	{
		return dispatcher.getID();
	}

	@Override
	Dispatcher forID(final String id)
	{
		return (Dispatcher)app.model.getFeature(id);
	}

	@Override
	long check(final Dispatcher dispatcher)
	{
		try(TransactionTry tx = app.model.startTransactionTry("Console DispatcherFailureDeprecated " + id))
		{
			return tx.commit(getQuery(dispatcher).total());
		}
	}

	private static Query<?> getQuery(final Dispatcher dispatcher)
	{
		@SuppressWarnings("deprecation")
		final Dispatcher.Result deprecated = Dispatcher.Result.failure;
		return
				dispatcher.getRunType().newQuery(
						dispatcher.getRunResult().equal(deprecated));
	}

	@Override
	String getViolationSql(final Dispatcher dispatcher)
	{
		return SchemaInfo.total(getQuery(dispatcher));
	}
}
