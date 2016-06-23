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

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Model;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;

final class UnsupportedCheckConstraintCop extends TestCop<CheckConstraint>
{
	UnsupportedCheckConstraintCop(final Args args, final TestArgs testArgs)
	{
		super(TAB_UNSUPPORTED_CHECK_CONSTRAINTS, "Unsupported Check Constraints", args, testArgs);
	}

	@Override
	protected UnsupportedCheckConstraintCop newArgs(final Args args)
	{
		return new UnsupportedCheckConstraintCop(args, testArgs);
	}

	@Override
	protected UnsupportedCheckConstraintCop newTestArgs(final TestArgs testArgs)
	{
		return new UnsupportedCheckConstraintCop(args, testArgs);
	}

	@Override
	boolean toleratesNotConnected()
	{
		return false;
	}

	@Override
	List<CheckConstraint> getItems(final Model model)
	{
		final ArrayList<CheckConstraint> result = new ArrayList<CheckConstraint>();

		for(final Type<?> type : model.getTypes())
		{
			for(final CheckConstraint constraint : type.getDeclaredCheckConstraints())
				if(!constraint.isSupportedBySchemaIfSupportedByDialect())
					result.add(constraint);
		}

		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Constraint", "Condition"};
	}

	@Override
	void writeValue(final Out out, final CheckConstraint constraint, final int h)
	{
		switch(h)
		{
			case 0: out.write(constraint.toString()); break;
			case 1: out.write(constraint.getCondition().toString()); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		};
	}

	@Override
	String getID(final CheckConstraint constraint)
	{
		return constraint.getID();
	}

	@Override
	CheckConstraint forID(final Model model, final String id)
	{
		return (CheckConstraint)model.getFeature(id);
	}

	@Override
	@SuppressFBWarnings("NP_LOAD_OF_KNOWN_NULL_VALUE") // OK: caused by try-with-resources
	long check(final CheckConstraint constraint, final Model model)
	{
		try(TransactionTry tx = model.startTransactionTry("Console CheckConstraint " + id))
		{
			return tx.commit(
					constraint.check());
		}
	}
}
