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
import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import java.util.List;

final class CustomQueryConstraintCop extends TestCop<Query<?>>
{
	static final String TAB = "customqueryconstraint";

	CustomQueryConstraintCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Custom Query Constraints", args, testArgs);
	}

	@Override
	protected CustomQueryConstraintCop newArgs(final Args args)
	{
		return new CustomQueryConstraintCop(args, testArgs);
	}

	@Override
	protected CustomQueryConstraintCop newTestArgs(final TestArgs testArgs)
	{
		return new CustomQueryConstraintCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Checks result of ConsoleServlet#getCustomQueryConstraints().",
		};
	}

	@Override
	List<Query<?>> getItems(final Model model)
	{
		return args.customQueries.get();
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Query"};
	}

	@Override
	void writeValue(final Out out, final Query<?> query, final int h)
	{
		//noinspection SwitchStatementWithTooFewBranches OK: all methods overriding writeValue have a switch
		switch(h)
		{
			case 0 -> out.write(query.toString());
			default ->
				throw new RuntimeException(String.valueOf(h));
		}
	}

	@Override
	String getID(final Query<?> query)
	{
		return query.toString();
	}

	@Override
	Query<?> forID(final Model model, final String id)
	{
		return args.customQueries.get().stream().
				filter(q -> id.equals(q.toString())).
				findFirst().
				orElse(null);
	}

	@Override
	long check(final Query<?> query, final Model model)
	{
		try(TransactionTry tx = model.startTransactionTry("Console CustomQueryConstraint " + query))
		{
			return tx.commit(query.total());
		}
	}

	@Override
	String getViolationSql(final Query<?> query, final Model model)
	{
		return SchemaInfo.total(query);
	}
}
