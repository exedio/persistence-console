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

import static com.exedio.cope.console.InspectionsCop.FAILS_WITH_ONE;
import static com.exedio.cope.console.InspectionsCop.NO_FAILURE_ON_EMPTY;
import static com.exedio.cope.console.InspectionsCop.failWithOne;
import static com.exedio.cope.console.InspectionsCop.noFailureOnEmpty;

import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.StringField;
import java.util.List;

final class StringIsNotEmptyCop extends FeatureTestCop<StringField>
{
	static final String TAB = "emptystring";

	StringIsNotEmptyCop(final Args args, final TestArgs testArgs)
	{
		super(StringField.class, TAB, "String Is Not Empty", args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
				"The empty string is allowed, but does not appear in database.",
				"Fails on all string fields, where there is no item with value empty string (\"\").",
				FAILS_WITH_ONE,
				NO_FAILURE_ON_EMPTY,
				"Lists all string fields, that allow the empty string (getMinimumLength()==0).",
		};
	}

	@Override
	protected StringIsNotEmptyCop newArgs(final Args args)
	{
		return new StringIsNotEmptyCop(args, testArgs);
	}

	@Override
	protected StringIsNotEmptyCop newTestArgs(final TestArgs testArgs)
	{
		return new StringIsNotEmptyCop(args, testArgs);
	}

	@Override
	boolean acceptsItem(final StringField feature)
	{
		return feature.getMinimumLength() < 1;
	}

	@Override
	List<Column<StringField>> columns()
	{
		return COLUMNS;
	}

	private static final List<Column<StringField>> COLUMNS = List.of(
			column("Field", StringField::toString)
	);

	@Override
	long check(final StringField field)
	{
		try(var tx = startTransaction())
		{
			final boolean result =
				(getQuery(field).total()==0) &&
				noFailureOnEmpty(field);
			tx.commit();
			return failWithOne(result);
		}
	}

	@Override
	String getViolationSql(final StringField field)
	{
		return SchemaInfo.total(getQuery(field));
	}

	private static Query<?> getQuery(final StringField field)
	{
		return field.getType().newQuery(field.length().equal(0));
	}
}
