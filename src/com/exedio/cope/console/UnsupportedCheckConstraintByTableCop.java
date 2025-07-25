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

import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.SchemaInfo.quoteName;
import static com.exedio.cope.console.SchemaCop.HELP_IMPACT_FATAL;

import com.exedio.cope.Model;
import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class UnsupportedCheckConstraintByTableCop extends TestCop<Table>
{
	private static final Logger logger = LoggerFactory.getLogger(UnsupportedCheckConstraintByTableCop.class);

	static final String TAB = "unsupportedcheckconstraintsbytable";
	static final String NAME = "Unsupported Check Constraints By Table";

	UnsupportedCheckConstraintByTableCop(final Args args, final TestArgs testArgs)
	{
		super(
				TAB,
				NAME,
				args, testArgs);
	}

	@Override
	protected UnsupportedCheckConstraintByTableCop newArgs(final Args args)
	{
		return new UnsupportedCheckConstraintByTableCop(args, testArgs);
	}

	@Override
	protected UnsupportedCheckConstraintByTableCop newTestArgs(final TestArgs testArgs)
	{
		return new UnsupportedCheckConstraintByTableCop(args, testArgs);
	}

	@Override
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"For databases not supporting check constraints (MySQL), " +
				"checks whether data complies with these unsupported check constraints.",
			HELP_IMPACT_FATAL,
			"NOTE: This screen and the screen \""+UnsupportedConstraintCop.NAME+"\" apply the same checks. " +
				"Here, all constraints of a table are checked at once. " +
				"If you want to check each constraint individually (typically if you found an error), " +
				"use \""+UnsupportedConstraintCop.NAME+"\"."
		};
	}

	@Override
	boolean requiresConnect()
	{
		return true;
	}

	@Override
	List<Table> getItems()
	{
		final ArrayList<Table> result = new ArrayList<>();

		final Schema schema = app.model.getSchema();
		for(final Table table : schema.getTables())
		{
			for(final Constraint constraint : table.getConstraints())
			{
				if(isRelevant(constraint))
				{
					result.add(table);
					break;
				}
			}
		}

		return result;
	}

	UnsupportedConstraintCop toTable(final Table table)
	{
		return new UnsupportedConstraintCop(args, testArgs, table.getName());
	}

	@Override
	List<Column<Table>> columns()
	{
		return List.of(
			column("Table", (out, table) -> UnsupportedCheckConstraintByTable_Jspm.writeTableValue(this, out, table)),
			columnNonFilterable("Condition", (out, table) ->
			{
				final StringBuilder sb = new StringBuilder();
				appendSQL(table, sb);
				writeValueLong(out, sb.toString());
			})
		);
	}

	private static void appendSQL(final Table table, final StringBuilder sb)
	{
		boolean first = true;

		for(final Constraint constraint : table.getConstraints())
		{
			if(isRelevant(constraint))
			{
				if(first)
					first = false;
				else
					sb.append(" AND ");

				sb.append('(').
					append(constraint.getRequiredCondition()).
					append(')');
			}
		}
	}

	private static boolean isRelevant(final Constraint constraint)
	{
		if(constraint.isSupported())
			return false;
		if(constraint instanceof CheckConstraint)
		{
			return true;
		}
		else
		{
			logger.warn(
					"found unsupported Constraint that is not a CheckConstraint -> {} and {} don't show the same constraints",
					UnsupportedConstraintCop.class.getSimpleName(),
					UnsupportedCheckConstraintByTableCop.class.getSimpleName());
			return false;
		}
	}

	@Override
	String getID(final Table table)
	{
		return table.getName();
	}

	@Override
	Table forID(final String id)
	{
		final Schema schema = app.model.getSchema();
		for(final Table t : schema.getTables())
			if(id.equals(t.getName()))
				return t;

		throw new RuntimeException(id);
	}

	@Override
	long check(final Table table)
	{
		final Model model = app.model;
		final StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(*) FROM ").
			append(quoteName(model, table.getName())).
			append(" WHERE NOT(");
		appendSQL(table, sb);
		sb.append(')');

		try(
			Connection con = newConnection(model);
			Statement st = con.createStatement())
		{
			try(ResultSet rs = st.executeQuery(sb.toString()))
			{
				rs.next();
				return rs.getLong(1);
			}
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, sb.toString());
		}
	}
}
