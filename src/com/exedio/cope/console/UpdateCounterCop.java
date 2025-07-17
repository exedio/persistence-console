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

import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.List;

final class UpdateCounterCop extends TestCop<Type<?>>
{
	static final String TAB = "updatecounters";

	UpdateCounterCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Update Counters", args, testArgs);
	}

	@Override
	protected UpdateCounterCop newArgs(final Args args)
	{
		return new UpdateCounterCop(args, testArgs);
	}

	@Override
	protected UpdateCounterCop newTestArgs(final TestArgs testArgs)
	{
		return new UpdateCounterCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Checks consistency of update counters between tables implementing type inheritance.",
			"IMPACT: " +
				"Any failures here causes errors when trying to load affected items into memory. " +
				"DANGER ZONE."
		};
	}

	@Override
	boolean requiresConnect()
	{
		return true;
	}

	@Override
	@SuppressWarnings("deprecation") // TODO: drop whole cop
	List<Type<?>> getItems()
	{
		final ArrayList<Type<?>> result = new ArrayList<>();

		for(final Type<?> t : app.model.getTypes())
			if(t.needsCheckUpdateCounter())
				result.add(t);

		return result;
	}

	@Override
	List<Column<Type<?>>> columns()
	{
		return COLUMNS;
	}

	@SuppressWarnings("Convert2MethodRef")
	private static final List<Column<Type<?>>> COLUMNS = List.of(
			column("Supertype", type -> type.getSupertype().getID()),
			column("Type",      type -> type.getID())
	);

	@Override
	String getID(final Type<?> type)
	{
		return type.getID();
	}

	@Override
	Type<?> forID(final String id)
	{
		return app.model.getType(id);
	}

	@Override
	@SuppressWarnings("deprecation") // TODO: drop whole cop
	long check(final Type<?> type)
	{
		try(TransactionTry tx = app.model.startTransactionTry("Console UpdateCounter " + id))
		{
			return tx.commit(
					type.checkUpdateCounterL());
		}
	}

	@Override
	@SuppressWarnings("deprecation") // TODO: drop whole cop
	String getViolationSql(final Type<?> type)
	{
		return SchemaInfo.checkUpdateCounter(type);
	}
}
