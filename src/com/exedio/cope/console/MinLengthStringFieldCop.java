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
import com.exedio.cope.Query;
import com.exedio.cope.StringField;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.List;

final class MinLengthStringFieldCop extends TestCop<StringField>
{
	static final String TAB = "minlength";

	MinLengthStringFieldCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Min Length String Fields", args, testArgs);
	}

	@Override
	protected MinLengthStringFieldCop newArgs(final Args args)
	{
		return new MinLengthStringFieldCop(args, testArgs);
	}

	@Override
	protected MinLengthStringFieldCop newTestArgs(final TestArgs testArgs)
	{
		return new MinLengthStringFieldCop(args, testArgs);
	}

	@Override
	List<StringField> getItems()
	{
		final ArrayList<StringField> result = new ArrayList<>();

		for(final Type<?> t : app.model.getTypes())
		{
			for(final Field<?> f : t.getDeclaredFields())
				if(f instanceof StringField)
					result.add((StringField)f);
		}

		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Field", "Min Length"};
	}

	@Override
	int getNumberOfFilterableColumns()
	{
		return 1;
	}

	@Override
	void writeValue(final Out out, final StringField field, final int h)
	{
		switch(h)
		{
			case 0 -> out.write(field.toString());
			case 1 -> out.write(Format.formatAndHide(0, field.getMinimumLength()));
			default ->
				throw new RuntimeException(String.valueOf(h));
		}
	}

	@Override
	String getID(final StringField field)
	{
		return field.getID();
	}

	@Override
	StringField forID(final String id)
	{
		return (StringField)app.model.getFeature(id);
	}

	@Override
	long check(final StringField field)
	{
		final Query<Integer> q = new Query<>(field.length().min());

		try(TransactionTry tx = app.model.startTransactionTry("Console MinLengthStringField " + id))
		{
			final Integer result = q.searchSingleton();
			tx.commit();

			return
				(result!=null)
				? (result - field.getMinimumLength())
				: 0;
		}
	}
}
