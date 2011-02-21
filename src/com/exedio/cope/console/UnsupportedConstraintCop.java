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

import java.util.ArrayList;
import java.util.List;

import com.exedio.cope.Model;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

final class UnsupportedConstraintCop extends TestCop<Constraint>
{
	UnsupportedConstraintCop(final Args args, final TestArgs testArgs)
	{
		super(TAB_UNSUPPORTED_CONSTRAINTS, "Unsupported Constraints", args, testArgs);
	}

	@Override
	protected UnsupportedConstraintCop newArgs(final Args args)
	{
		return new UnsupportedConstraintCop(args, testArgs);
	}

	@Override
	protected UnsupportedConstraintCop newTestArgs(final TestArgs testArgs)
	{
		return new UnsupportedConstraintCop(args, testArgs);
	}

	@Override
	List<Constraint> getItems(final Model model)
	{
		final ArrayList<Constraint> constraints = new ArrayList<Constraint>();

		final Schema schema = model.getSchema();
		for(final Table t : schema.getTables())
		{
			for(final Constraint c : t.getConstraints())
				if(!c.isSupported())
					constraints.add(c);
		}

		return constraints;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Table", "Name", "Condition"};
	}

	@Override
	void writeValue(final Out out, final Constraint constraint, final int h)
	{
		switch(h)
		{
			case 0: out.write(constraint.getTable().getName()); break;
			case 1: out.write(constraint.getName()); break;
			case 2: out.write(constraint.getRequiredCondition()); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		};
	}

	@Override
	String getID(final Constraint constraint)
	{
		return constraint.getName();
	}

	@Override
	Constraint forID(final Model model, final String id)
	{
		final Schema schema = model.getSchema();
		for(final Table t : schema.getTables())
		{
			for(final Constraint c : t.getConstraints())
				if(id.equals(c.getName()))
					return c;
		}
		throw new RuntimeException(id);
	}

	@Override
	int check(final Constraint constraint)
	{
		return constraint.check();
	}
}
