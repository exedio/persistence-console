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

import com.exedio.cope.Model;
import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

final class UnsupportedCheckConstraintByTableCop extends TestCop<Table>
{
	UnsupportedCheckConstraintByTableCop(final Args args, final TestArgs testArgs)
	{
		super(
				TAB_UNSUPPORTED_CHECK_CONSTRAINTS_BY_TABLE,
				"Unsupported Check Constraints By Table",
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
	boolean toleratesNotConnected()
	{
		return false;
	}

	@Override
	List<Table> getItems(final Model model)
	{
		final ArrayList<Table> result = new ArrayList<Table>();

		final Schema schema = model.getSchema();
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

	@Override
	String[] getHeadings()
	{
		return new String[]{"Table", "Condition"};
	}

	@Override
	void writeValue(final Out out, final Table table, final int h)
	{
		switch(h)
		{
			case 0: out.write(table.getName()); break;
			case 1:
				final StringBuilder bf = new StringBuilder();
				appendSQL(table, bf);
				writeValueLong(out, bf.toString());
				break;
			default:
				throw new RuntimeException(String.valueOf(h));
		};
	}

	private void appendSQL(final Table table, final StringBuilder bf)
	{
		boolean first = true;

		for(final Constraint constraint : table.getConstraints())
		{
			if(isRelevant(constraint))
			{
				if(first)
					first = false;
				else
					bf.append(" AND ");

				bf.append('(').
					append(constraint.getRequiredCondition()).
					append(')');
			}
		}
	}

	private static boolean isRelevant(final Constraint constraint)
	{
		return !constraint.isSupported() && constraint instanceof CheckConstraint;
	}

	@Override
	String getID(final Table table)
	{
		return table.getName();
	}

	@Override
	Table forID(final Model model, final String id)
	{
		final Schema schema = model.getSchema();
		for(final Table t : schema.getTables())
			if(id.equals(t.getName()))
				return t;

		throw new RuntimeException(id);
	}

	@Override
	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	long check(final Table table, final Model model)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("SELECT COUNT(*) FROM ").
			append(quoteName(model, table.getName())).
			append(" WHERE NOT(");
		appendSQL(table, bf);
		bf.append(')');

		try(
			Connection con = newConnection(model);
			Statement st = con.createStatement())
		{
			try(ResultSet rs = st.executeQuery(bf.toString()))
			{
				rs.next();
				return rs.getLong(1);
			}
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, bf.toString());
		}
	}
}