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
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;

final class UpdateCounterCop extends TestCop<Type<?>>
{
	UpdateCounterCop(final Args args, final TestArgs testArgs)
	{
		super(TAB_UPDATE_COUNTERS, "Update Counters", args, testArgs);
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
	boolean toleratesNotConnected()
	{
		return false;
	}

	@Override
	List<Type<?>> getItems(final Model model)
	{
		final ArrayList<Type<?>> result = new ArrayList<>();

		for(final Type<?> t : model.getTypes())
			if(t.needsCheckUpdateCounter())
				result.add(t);

		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Supertype", "Type"};
	}

	@Override
	void writeValue(final Out out, final Type<?> type, final int h)
	{
		switch(h)
		{
			case 1: out.write(type.getSupertype().getID()); break;
			case 0: out.write(type.getID()); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		}
	}

	@Override
	String getID(final Type<?> type)
	{
		return type.getID();
	}

	@Override
	Type<?> forID(final Model model, final String id)
	{
		return model.getType(id);
	}

	@Override
	@SuppressFBWarnings("NP_LOAD_OF_KNOWN_NULL_VALUE") // OK: caused by try-with-resources
	long check(final Type<?> type, final Model model)
	{
		try(TransactionTry tx = model.startTransactionTry("Console UpdateCounter " + id))
		{
			return tx.commit(
					type.checkUpdateCounterL());
		}
	}
}
