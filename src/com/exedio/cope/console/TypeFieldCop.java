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
import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.reflect.TypeField;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
	List<TypeField<?>> getItems()
	{
		final ArrayList<TypeField<?>> result = new ArrayList<>();

		for(final Type<?> type : app.model.getTypes())
		{
			for(final Feature feature : type.getDeclaredFeatures())
			{
				if(feature instanceof TypeField)
				{
					if (all || app.isStable((TypeField<?>)feature))
						result.add((TypeField<?>) feature);
				}
			}
		}
		return result;
	}

	@Override
	List<Column<TypeField<?>>> columns()
	{
		return COLUMNS;
	}

	private static final List<Column<TypeField<?>>> COLUMNS = List.of(
			column("Type", field -> field.getType().getID()),
			column("Name", field -> field.getName()),
			columnNonFilterable("Values", (out, field) -> writeValueLong(out, field.getValues().toString()))
	);

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
	TypeField<?> forID(final String id)
	{
		return (TypeField<?>)app.model.getFeature(id);
	}

	@Override
	long check(final TypeField<?> field)
	{
		final Query<?> query = getQuery(field);
		try(TransactionTry tx = app.model.startTransactionTry("Console TypeField " + id))
		{
			return tx.commit(
					query.total());
		}
	}

	@Override
	String getViolationSql(final TypeField<?> field)
	{
		return SchemaInfo.search(getQuery(field));
	}

	private static Query<?> getQuery(final TypeField<?> field)
	{
		return field.getType().newQuery(field.isInvalid());
	}
}
