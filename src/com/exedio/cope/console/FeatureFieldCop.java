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
import com.exedio.cope.reflect.FeatureField;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

final class FeatureFieldCop extends TestCop<FeatureField<?>>
{
	static final String TAB = "feature";

	private static final String ALL = "all";

	final boolean all;

	FeatureFieldCop(final Args args, final TestArgs testArgs)
	{
		this(args, testArgs, false);
	}

	private FeatureFieldCop(final Args args, final TestArgs testArgs, final boolean all)
	{
		super(TAB, "Feature Fields", args, testArgs);
		this.all = all;
		addParameter(ALL, all);
	}

	static FeatureFieldCop getFeatureFieldCop(final Args args, final TestArgs testArgs, final HttpServletRequest request)
	{
		return new FeatureFieldCop(args, testArgs, getBooleanParameter(request, ALL));
	}

	FeatureFieldCop toToggleAll()
	{
		return new FeatureFieldCop(args, testArgs, !all);
	}

	@Override
	protected FeatureFieldCop newArgs(final Args args)
	{
		return new FeatureFieldCop(args, testArgs, all);
	}

	@Override
	protected FeatureFieldCop newTestArgs(final TestArgs testArgs)
	{
		return new FeatureFieldCop(args, testArgs, all);
	}

	@Override
	String getNoItemsMessage()
	{
		return "There are no "+(all?"":"stable ")+"feature fields in this model.";
	}

	@Override
	List<FeatureField<?>> getItems()
	{
		final ArrayList<FeatureField<?>> result = new ArrayList<>();

		for(final Type<?> type : app.model.getTypes())
		{
			for(final Feature feature : type.getDeclaredFeatures())
			{
				if(feature instanceof FeatureField)
				{
					if (all || app.isStable((FeatureField<?>)feature))
						result.add((FeatureField<?>) feature);
				}
			}
		}
		return result;
	}

	@Override
	List<Column<FeatureField<?>>> columns()
	{
		return COLUMNS;
	}

	private static final List<Column<FeatureField<?>>> COLUMNS = List.of(
			column("Type", field -> field.getType().getID()),
			column("Name", field -> field.getName()),
			columnNonFilterable("Values", (out, field) -> writeValueLong(out, field.getValues().toString()))
	);

	@Override
	void writeIntro(final Out out)
	{
		FeatureField_Jspm.writeIntro(this, out);
	}

	@Override
	String getID(final FeatureField<?> field)
	{
		return field.getID();
	}

	@Override
	FeatureField<?> forID(final String id)
	{
		return (FeatureField<?>)app.model.getFeature(id);
	}

	@Override
	long check(final FeatureField<?> field)
	{
		final Query<?> query = getQuery(field);
		try(TransactionTry tx = app.model.startTransactionTry("Console FeatureField " + id))
		{
			return tx.commit(
					query.total());
		}
	}

	@Override
	String getViolationSql(final FeatureField<?> field)
	{
		return SchemaInfo.search(getQuery(field));
	}

	private static Query<?> getQuery(final FeatureField<?> field)
	{
		return field.getType().newQuery(field.isInvalid());
	}
}
