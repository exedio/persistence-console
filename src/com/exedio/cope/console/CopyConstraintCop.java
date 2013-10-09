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

import com.exedio.cope.CopyConstraint;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.List;

final class CopyConstraintCop extends TestCop<CopyConstraint>
{
	CopyConstraintCop(final Args args, final TestArgs testArgs)
	{
		super(TAB_COPY_CONSTRAINTS, "Copy Constraints", args, testArgs);
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
	List<CopyConstraint> getItems(final Model model)
	{
		final ArrayList<CopyConstraint> result = new ArrayList<CopyConstraint>();

		for(final Type<?> type : model.getTypes())
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
			case 0: out.write(constraint.toString()); break;
			case 1: out.write(constraint.getTarget().getValueType().getID()); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		};
	}

	@Override
	String getID(final CopyConstraint constraint)
	{
		return constraint.getID();
	}

	@Override
	CopyConstraint forID(final Model model, final String id)
	{
		return (CopyConstraint)model.getFeature(id);
	}

	@Override
	int check(final CopyConstraint constraint)
	{
		final Model model = constraint.getType().getModel();
		try
		{
			model.startTransaction("Console CopyConstraint " + id);
			final int result = constraint.check();
			model.commit();
			return result;
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
	}
}
