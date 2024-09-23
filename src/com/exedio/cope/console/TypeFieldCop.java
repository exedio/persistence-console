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
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.reflect.TypeField;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

final class TypeFieldCop extends TestCop<TypeField<?>>
{
	static final String TAB = "type";

	private static final String ALL = "all";

	final boolean all;

	TypeFieldCop(final Args args, final TestArgs testArgs)
	{
		this(args, testArgs, false);
	}

	private TypeFieldCop(final Args args, final TestArgs testArgs, final boolean all)
	{
		super(TAB, "Type Fields", args, testArgs);
		this.all = all;
		addParameter(ALL, all);
	}

	static TypeFieldCop getTypeFieldCop(final Args args, final TestArgs testArgs, final HttpServletRequest request)
	{
		return new TypeFieldCop(args, testArgs, getBooleanParameter(request, ALL));
	}

	TypeFieldCop toToggleAll()
	{
		return new TypeFieldCop(args, testArgs, !all);
	}

	@Override
	protected TypeFieldCop newArgs(final Args args)
	{
		return new TypeFieldCop(args, testArgs, all);
	}

	@Override
	protected TypeFieldCop newTestArgs(final TestArgs testArgs)
	{
		return new TypeFieldCop(args, testArgs, all);
	}

	@Override
	String getNoItemsMessage()
	{
		return "There are no "+(all?"":"stable ")+"type fields in this model.";
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
				{
					if (all || args.app.isStable((TypeField<?>)feature))
						result.add((TypeField<?>) feature);
				}
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
			case 0 -> out.write(field.getType().getID());
			case 1 -> out.write(field.getName());
			case 2 -> writeValueLong(out, field.getValues().toString());
			default ->
				throw new RuntimeException(String.valueOf(h));
		}
	}

	@Override
	void writeIntro(final Out out)
	{
		TypeField_Jspm.writeIntro(this, out);
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
		final Query<?> query = getQuery(field);
		try(TransactionTry tx = model.startTransactionTry("Console TypeField " + id))
		{
			return tx.commit(
					query.total());
		}
	}

	@Override
	String getViolationSql(final TypeField<?> field, final Model model)
	{
		return SchemaInfo.search(getQuery(field));
	}

	private static Query<?> getQuery(final TypeField<?> field)
	{
		return field.getType().newQuery(field.isInvalid());
	}
}
