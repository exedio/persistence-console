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

import static com.exedio.cope.console.SchemaCop.HELP_IMPACT_FATAL;

import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.ItemField;
import com.exedio.cope.ItemFunction;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.This;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.List;

final class TypeColumnCop extends TestCop<ItemFunction<?>>
{
	static final String TAB = "typecolumns";

	TypeColumnCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Type Columns", args, testArgs);
	}

	@Override
	protected TypeColumnCop newArgs(final Args args)
	{
		return new TypeColumnCop(args, testArgs);
	}

	@Override
	protected TypeColumnCop newTestArgs(final TestArgs testArgs)
	{
		return new TypeColumnCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Checks consistency of type columns to the \"class\"-column of their target.",
			HELP_IMPACT_FATAL
		};
	}

	@Override
	boolean requiresConnect()
	{
		return true;
	}

	@Override
	List<ItemFunction<?>> getItems()
	{
		final ArrayList<ItemFunction<?>> result = new ArrayList<>();

		for(final Type<?> t : app.model.getTypes())
		{
			final This<?> tt = t.getThis();
			if(tt.needsCheckTypeColumn())
				result.add(tt);

			for(final Field<?> f : t.getDeclaredFields())
				if(f instanceof final ItemField<?> itf &&
					itf.needsCheckTypeColumn())
				{
					result.add(itf);
				}
		}

		return result;
	}

	@Override
	List<Column<ItemFunction<?>>> columns()
	{
		return COLUMNS;
	}

	@SuppressWarnings("Convert2MethodRef")
	private static final List<Column<ItemFunction<?>>> COLUMNS = List.of(
			column("Function", function -> function.toString()),
			column("Value",    function -> function.getValueType().getID())
	);

	@Override
	String getID(final ItemFunction<?> function)
	{
		return ((Feature)function).getID();
	}

	@Override
	ItemFunction<?> forID(final String id)
	{
		return (ItemFunction<?>)app.model.getFeature(id);
	}

	@Override
	long check(final ItemFunction<?> function)
	{
		try(TransactionTry tx = app.model.startTransactionTry("Console TypeColumn " + id))
		{
			return tx.commit(
					function.checkTypeColumnL());
		}
	}

	@Override
	String getViolationSql(final ItemFunction<?> function)
	{
		return SchemaInfo.checkTypeColumn(function);
	}
}
