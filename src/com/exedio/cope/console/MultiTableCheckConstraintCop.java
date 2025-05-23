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

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.List;

final class MultiTableCheckConstraintCop extends TestCop<CheckConstraint>
{
	static final String TAB = "multitablecheckconstraints";

	MultiTableCheckConstraintCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Multi-Table Check Constraints", args, testArgs);
	}

	@Override
	protected MultiTableCheckConstraintCop newArgs(final Args args)
	{
		return new MultiTableCheckConstraintCop(args, testArgs);
	}

	@Override
	protected MultiTableCheckConstraintCop newTestArgs(final TestArgs testArgs)
	{
		return new MultiTableCheckConstraintCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Verifies that data complies with check constraints that cannot be declared in the database " +
				"because they span multiple tables (implementing a type hierarchy).",
			HELP_IMPACT_FATAL
		};
	}

	@Override
	boolean requiresConnect()
	{
		return true;
	}

	@Override
	List<CheckConstraint> getItems()
	{
		final ArrayList<CheckConstraint> result = new ArrayList<>();

		for(final Type<?> type : app.model.getTypes())
		{
			for(final CheckConstraint constraint : type.getDeclaredCheckConstraints())
				if(!constraint.isSupportedBySchemaIfSupportedByDialect())
					result.add(constraint);
		}

		return result;
	}

	@Override
	List<Column<CheckConstraint>> columns()
	{
		return COLUMNS;
	}

	private static final List<Column<CheckConstraint>> COLUMNS = List.of(
			column("Constraint", CheckConstraint::toString),
			column("Condition", (out,constraint) -> writeValueLong(out, constraint.getCondition().toString()))
	);

	@Override
	String getID(final CheckConstraint constraint)
	{
		return constraint.getID();
	}

	@Override
	CheckConstraint forID(final String id)
	{
		return (CheckConstraint)app.model.getFeature(id);
	}

	@Override
	long check(final CheckConstraint constraint)
	{
		try(TransactionTry tx = app.model.startTransactionTry("Console CheckConstraint " + id))
		{
			return tx.commit(
					constraint.check());
		}
	}

	@Override
	String getViolationSql(final CheckConstraint constraint)
	{
		return SchemaInfo.search(constraint.getType().newQuery(constraint.getCondition().not()));
	}
}
