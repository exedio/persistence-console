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
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.reflect.TypeField;
import java.util.ArrayList;
import java.util.List;

final class TypeFieldCop extends TestCop<TypeField<?>>
{
	static final String TAB = "type";

	TypeFieldCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Type Fields", args, testArgs);
	}

	@Override
	protected TypeFieldCop newArgs(final Args args)
	{
		return new TypeFieldCop(args, testArgs);
	}

	@Override
	protected TypeFieldCop newTestArgs(final TestArgs testArgs)
	{
		return new TypeFieldCop(args, testArgs);
	}

	@Override
	List<TypeField<?>> getItems(final Model model)
	{
		final ArrayList<TypeField<?>> result = new ArrayList<>();

		for(final Type<?> type : model.getTypes())
		{
			for(final Feature feature : type.getDeclaredFeatures())
			{
				if(feature instanceof TypeField)
					result.add((TypeField<?>)feature);
			}
		}
		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Type", "Name", "Values"};
	}

	@Override
	void writeValue(final Out out, final TypeField<?> field, final int h)
	{
		switch(h)
		{
			case 0: out.write(field.getType().getID()); break;
			case 1: out.write(field.getName()); break;
			case 2: writeValueLong(out, field.getValues().toString()); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		}
	}

	@Override
	String getID(final TypeField<?> field)
	{
		return field.getID();
	}

	@Override
	TypeField<?> forID(final Model model, final String id)
	{
		return (TypeField<?>)model.getFeature(id);
	}

	@Override
	long check(final TypeField<?> field, final Model model)
	{
		final Query<?> query = field.getType().newQuery(field.isInvalid());
		try(TransactionTry tx = model.startTransactionTry("Console TypeField " + id))
		{
			return tx.commit(
					query.total());
		}
	}
}
