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
import com.exedio.cope.Field;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Model;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;

final class OptionalFieldCop extends TestCop<FunctionField<?>>
{
	OptionalFieldCop(final Args args, final TestArgs testArgs)
	{
		super(TAB_OPTIONAL_FIELDS, "Optional Fields", args, testArgs);
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
	List<FunctionField<?>> getItems(final Model model)
	{
		final ArrayList<FunctionField<?>> result = new ArrayList<FunctionField<?>>();

		for(final Type<?> t : model.getTypes())
		{
			for(final Field<?> f : t.getDeclaredFields())
				if(f instanceof FunctionField)
				{
					final FunctionField<?> ff = (FunctionField<?>)f;
					if(!ff.isMandatory())
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
		switch(h)
		{
			case 0: out.write(field.toString()); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		};
	}

	@Override
	String getID(final FunctionField<?> field)
	{
		return ((Feature)field).getID();
	}

	@Override
	FunctionField<?> forID(final Model model, final String id)
	{
		return (FunctionField<?>)model.getFeature(id);
	}

	@Override
	@SuppressFBWarnings({"NP_LOAD_OF_KNOWN_NULL_VALUE","RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE"}) // OK: caused by try-with-resources
	long check(final FunctionField<?> field, final Model model)
	{
		final Type<?> type = field.getType();
		try(TransactionTry tx = model.startTransactionTry("Console OptionalField " + id))
		{
			final boolean result =
				(type.newQuery(field.isNull()).total()==0) &&
				(type.newQuery().total()>0);
			tx.commit();
			return result ? 1 : 0;
		}
	}
}
