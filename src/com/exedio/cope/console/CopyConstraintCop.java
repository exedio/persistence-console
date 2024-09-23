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

import com.exedio.cope.CopyConstraint;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.List;

final class CopyConstraintCop extends TestCop<CopyConstraint>
{
	static final String TAB = "copyconstraints";

	CopyConstraintCop(final Args args, final TestArgs testArgs)
	{
		super(TAB, "Copy Constraints", args, testArgs);
	}

	@Override
	protected CopyConstraintCop newArgs(final Args args)
	{
		return new CopyConstraintCop(args, testArgs);
	}

	@Override
	protected CopyConstraintCop newTestArgs(final TestArgs testArgs)
	{
		return new CopyConstraintCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"Verifies that data complies with copy constraints. " +
				"Copy constraints cannot be declared in any database.",
			HELP_IMPACT_FATAL
		};
	}

	@Override
	List<CopyConstraint> getItems()
	{
		final ArrayList<CopyConstraint> result = new ArrayList<>();

		for(final Type<?> type : app.model.getTypes())
			result.addAll(type.getDeclaredCopyConstraints());

		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Constraint", "Target"};
	}

	@Override
	void writeValue(final Out out, final CopyConstraint constraint, final int h)
	{
		switch(h)
		{
			case 0 -> out.write(constraint.toString());
			case 1 -> out.write(constraint.getTarget().getValueType().getID());
			default ->
				throw new RuntimeException(String.valueOf(h));
		}
	}

	@Override
	String getID(final CopyConstraint constraint)
	{
		return constraint.getID();
	}

	@Override
	CopyConstraint forID(final String id)
	{
		return (CopyConstraint)app.model.getFeature(id);
	}

	@Override
	long check(final CopyConstraint constraint)
	{
		try(TransactionTry tx = app.model.startTransactionTry("Console CopyConstraint " + id))
		{
			return tx.commit(
					constraint.check());
		}
	}

	@Override
	String getViolationSql(final CopyConstraint constraint)
	{
		return SchemaInfo.check(constraint);
	}
}
