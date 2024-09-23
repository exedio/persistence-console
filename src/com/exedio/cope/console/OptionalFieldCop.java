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

import com.exedio.cope.Field;
import com.exedio.cope.FunctionField;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.List;

final class OptionalFieldCop extends TestCop<FunctionField<?>>
{
	static final String TAB = "optional";

	OptionalFieldCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Optional Fields", args, testArgs);
	}

	@Override
	protected OptionalFieldCop newArgs(final Args args)
	{
		return new OptionalFieldCop(args, testArgs);
	}

	@Override
	protected OptionalFieldCop newTestArgs(final TestArgs testArgs)
	{
		return new OptionalFieldCop(args, testArgs);
	}

	@Override
	List<FunctionField<?>> getItems()
	{
		final ArrayList<FunctionField<?>> result = new ArrayList<>();

		for(final Type<?> t : app.model.getTypes())
		{
			for(final Field<?> f : t.getDeclaredFields())
				if(f instanceof final FunctionField<?> ff &&
					!ff.isMandatory())
				{
					result.add(ff);
				}
		}

		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Field"};
	}

	@Override
	void writeValue(final Out out, final FunctionField<?> field, final int h)
	{
		//noinspection SwitchStatementWithTooFewBranches OK: all methods overriding writeValue have a switch
		switch(h)
		{
			case 0 -> out.write(field.toString());
			default ->
				throw new RuntimeException(String.valueOf(h));
		}
	}

	@Override
	String getID(final FunctionField<?> field)
	{
		return field.getID();
	}

	@Override
	FunctionField<?> forID(final String id)
	{
		return (FunctionField<?>)app.model.getFeature(id);
	}

	@Override
	long check(final FunctionField<?> field)
	{
		final Type<?> type = field.getType();
		try(TransactionTry tx = app.model.startTransactionTry("Console OptionalField " + id))
		{
			final boolean result =
				(type.newQuery(field.isNull()).total()==0) &&
				(type.newQuery().total()>0);
			tx.commit();
			return result ? 1 : 0;
		}
	}
}
