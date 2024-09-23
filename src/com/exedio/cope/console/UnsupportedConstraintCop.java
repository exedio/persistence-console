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

import com.exedio.cope.SchemaInfo;
import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

final class UnsupportedConstraintCop extends TestCop<Constraint>
{
	static final String TAB = "unsupportedconstraints";
	static final String NAME = "Unsupported Constraints";

	static final String TABLE_FILTER = "table";

	final String tableFilter;

	UnsupportedConstraintCop(final Args args, final TestArgs testArgs)
	{
		this(args, testArgs, null);
	}

	UnsupportedConstraintCop(final Args args, final TestArgs testArgs, final String tableFilter)
	{
		super(TAB, NAME + (tableFilter==null?"":(" on '"+tableFilter+"'")), args, testArgs);
		if (tableFilter!=null && tableFilter.isEmpty())
			throw new IllegalArgumentException("tableFilter empty");
		this.tableFilter = tableFilter;
		addParameter(TABLE_FILTER, tableFilter);
	}

	static UnsupportedConstraintCop getUnsupportedConstraintCop(final Args args, final TestArgs testArgs, final HttpServletRequest request)
	{
		final String tableFilter = request.getParameter(TABLE_FILTER);
		return new UnsupportedConstraintCop(args, testArgs, tableFilter==null||tableFilter.isEmpty()?null:tableFilter);
	}

	@Override
	protected UnsupportedConstraintCop newArgs(final Args args)
	{
		return new UnsupportedConstraintCop(args, testArgs, tableFilter);
	}

	@Override
	protected UnsupportedConstraintCop newTestArgs(final TestArgs testArgs)
	{
		return new UnsupportedConstraintCop(args, testArgs, tableFilter);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"For databases not supporting check constraints (MySQL), " +
				"checks whether data complies with these unsupported check constraints.",
			HELP_IMPACT_FATAL,
			"NOTE: This screen and the screen \""+UnsupportedCheckConstraintByTableCop.NAME+"\" apply the same checks. " +
				"Here, each constraint is checked individually. " +
				"If you want to check all constraints of a table at once (much faster), " +
				"use \""+UnsupportedCheckConstraintByTableCop.NAME+"\"."
		};
	}

	@Override
	boolean requiresConnect()
	{
		return true;
	}

	@Override
	List<Constraint> getItems()
	{
		final ArrayList<Constraint> result = new ArrayList<>();

		final Schema schema = app.model.getSchema();
		for(final Table t : schema.getTables())
		{
			if (tableFilter==null || tableFilter.equals(t.getName()))
			{
				for(final Constraint c : t.getConstraints())
					if(! c.isSupported())
						result.add(c);
			}
		}

		return result;
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
			case 0 -> out.write(constraint.getTable().getName());
			case 1 -> out.write(constraint.getName());
			case 2 -> writeValueLong(out, constraint.getRequiredCondition());
			default ->
				throw new RuntimeException(String.valueOf(h));
		}
	}

	@Override
	String getID(final Constraint constraint)
	{
		return constraint.getName();
	}

	@Override
	Constraint forID(final String id)
	{
		final Schema schema = app.model.getSchema();
		for(final Table t : schema.getTables())
		{
			for(final Constraint c : t.getConstraints())
				if(id.equals(c.getName()))
					return c;
		}
		throw new RuntimeException(id);
	}

	@Override
	long check(final Constraint constraint)
	{
		return constraint.checkL();
	}

	@Override
	String getViolationSql(final Constraint constraint)
	{
		if (constraint instanceof CheckConstraint)
			return "SELECT * FROM " + SchemaInfo.quoteName(app.model, constraint.getTable().getName()) +
					 " WHERE NOT(" + constraint.getRequiredCondition() + ')';
		else
			return null;
	}
}
