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
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.StringField;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;

final class MinLengthStringFieldCop extends TestCop<StringField>
{
	MinLengthStringFieldCop(final Args args, final TestArgs testArgs)
	{
		super(TAB_MIN_LENGTH_STRING_FIELDS, "Min Length String Fields", args, testArgs);
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
	List<StringField> getItems(final Model model)
	{
		final ArrayList<StringField> result = new ArrayList<StringField>();

		for(final Type<?> t : model.getTypes())
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
	void writeValue(final Out out, final StringField field, final int h)
	{
		switch(h)
		{
			case 0: out.write(field.toString()); break;
			case 1: out.write(Format.formatAndHide(0, field.getMinimumLength())); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		};
	}

	@Override
	String getID(final StringField field)
	{
		return ((Feature)field).getID();
	}

	@Override
	StringField forID(final Model model, final String id)
	{
		return (StringField)model.getFeature(id);
	}

	@Override
	@SuppressFBWarnings({"NP_LOAD_OF_KNOWN_NULL_VALUE","RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE"}) // OK: caused by try-with-resources
	long check(final StringField field, final Model model)
	{
		final Query<Integer> q = new Query<Integer>(field.length().min());

		try(TransactionTry tx = model.startTransactionTry("Console MinLengthStringField " + id))
		{
			final Integer result = q.searchSingleton();
			tx.commit();

			return
				(result!=null)
				? (result.intValue() - field.getMinimumLength())
				: 0;
		}
	}
}
