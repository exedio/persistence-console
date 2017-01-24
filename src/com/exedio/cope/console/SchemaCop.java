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

import com.exedio.cope.Model;
import com.exedio.cope.misc.TimeUtil;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.StatementListener;
import com.exedio.dsmf.Table;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

final class SchemaCop extends ConsoleCop<Void>
{
	private static final String DETAILED = "dt";

	final boolean detailed;

	SchemaCop(final Args args)
	{
		this(args, false);
	}

	private SchemaCop(final Args args, final boolean detailed)
	{
		super(TAB_SCHEMA, "Schema", args);
		this.detailed = detailed;

		addParameter(DETAILED, detailed);
	}

	static final SchemaCop getSchemaCop(final Args args, final HttpServletRequest request)
	{
		return new SchemaCop(args, getBooleanParameter(request, DETAILED));
	}

	@Override
	protected SchemaCop newArgs(final Args args)
	{
		return new SchemaCop(args, detailed);
	}

	SchemaCop toToggleDetailed()
	{
		return new SchemaCop(args, !detailed);
	}

	@Override
	String getHeadingHelp()
	{
		return
				"Here you can create and drop the database schema - tables, sequences and constraints. " +
				"\"Tear down\" is similar to \"drop\" but ignores errors, " +
				"caused for instance by non-existing tables or sequences. " +
				"Section \"Details\" verifies, that tables, columns, constraints etc. do exist and " +
				"have correct types (columns), check clauses (check constraints) etc. " +
				HELP_IMPACT_FATAL;
	}

	static final String HELP_IMPACT_FATAL =
			"IMPACT: " +
			"Any failures here invalidate all contracts of the cope framework. " +
			"Your application may either fail with errors or silently destroy your data. " +
			"DANGER ZONE.";

	@Override
	void writeHead(final Out out)
	{
		Schema_Jspm.writeHead(out);
	}

	@Override
	final void writeBody(final Out out)
	{
		failIfNotConnected(out.model);
		SchemaSchema_Jspm.writeBody(this, out);
		SchemaConstraints_Jspm.writeBody(this, out);
		SchemaDetails_Jspm.writeBody(this, out);
	}

	private static final String strip(final String s, final String prefix)
	{
		return s.startsWith(prefix) ? s.substring(prefix.length()) : null;
	}

	private static final Sequence getSequence(final Schema schema, final String param)
	{
		final Sequence result = schema.getSequence(param);
		if(result==null)
			throw new RuntimeException(param);
		return result;
	}

	private static final Table getTable(final Schema schema, final String param)
	{
		final Table result = schema.getTable(param);
		if(result==null)
			throw new RuntimeException(param);
		return result;
	}

	private static final Column getColumn(final Schema schema, final String param)
	{
		final int pos = param.indexOf('#');
		if(pos<=0)
			throw new RuntimeException(param);

		final Table table = schema.getTable(param.substring(0, pos));
		if(table==null)
			throw new RuntimeException(param);

		final Column result = table.getColumn(param.substring(pos+1));
		if(result==null)
			throw new RuntimeException(param);

		return result;
	}

	private static final Constraint getConstraint(final Schema schema, final String param)
	{
		final int pos = param.indexOf('#');
		if(pos<=0)
			throw new RuntimeException(param);

		final Table table = schema.getTable(param.substring(0, pos));
		if(table==null)
			throw new RuntimeException(param);

		final Constraint result = table.getConstraint(param.substring(pos+1));
		if(result==null)
			throw new RuntimeException(param);

		return result;
	}

	static final String DROP_CONSTRAINT = "DROP_CONSTRAINT";
	static final String DROP_COLUMN     = "DROP_COLUMN";
	static final String DROP_TABLE      = "DROP_TABLE";
	static final String DROP_SEQUENCE   = "DROP_SEQUENCE";
	static final String RENAME_TABLE_PREFIX  = "RENAME_TABLE_";
	static final String MODIFY_COLUMN_PREFIX = "MODIFY_COLUMN__";
	static final String RENAME_COLUMN_PREFIX = "RENAME_COLUMN__";
	static final String CREATE_SEQUENCE   = "CREATE_SEQUENCE";
	static final String CREATE_TABLE      = "CREATE_TABLE";
	static final String CREATE_COLUMN     = "CREATE_COLUMN";
	static final String CREATE_CONSTRAINT = "CREATE_CONSTRAINT";

	static final String COLUMNS_CREATE_MISSING = "columns.create";
	static final String COLUMNS_DROP_UNUSED    = "columns.drop";
	static final String COLUMNS_MODIFY_MISMATCHING_TYPE = "columns.modify";

	static final String CATCH_CREATE = "catch.create";
	static final String CATCH_DROP   = "catch.drop";
	static final String CATCH_RESET  = "catch.reset";

	static final void writeApply(final Out out,
			final HttpServletRequest request, final Model model, final boolean dryRun)
	{
		final Schema schema = model.getVerifiedSchema();
		final StatementListener listener = new StatementListener()
		{
			long beforeExecuteTime = Long.MIN_VALUE;

			@Override
			public boolean beforeExecute(final String statement)
			{
				out.writeRaw("\n\t\t<div><span class=\"javaDecoration\">\"</span>"); // TODO do java decoration by javascript
				out.writeSQL(statement.replace("\"", "\\\""));
				out.writeRaw("<span class=\"javaDecoration\">\",</span>");
				if(dryRun)
				{
					out.writeRaw("</div>");
					return false;
				}
				else
				{
					out.flush();
					beforeExecuteTime = System.nanoTime();
					return true;
				}
			}

			@Override
			public void afterExecute(final String statement, final int rows)
			{
				final long time = TimeUtil.toMillies(System.nanoTime(), beforeExecuteTime);
				out.writeRaw(" <span class=\"javaDecoration\">// ");
				out.write(time);
				out.writeRaw("ms, ");
				out.write(rows);
				out.writeRaw(" rows</span></div>");
			}
		};

		for(final String p : getParameters(request, DROP_CONSTRAINT))
			getConstraint(schema, p).drop(listener);
		for(final String p : getParameters(request, DROP_COLUMN))
			getColumn    (schema, p).drop(listener);
		if(request.getParameter(COLUMNS_DROP_UNUSED)!=null)
			for(final Column c : getUnusedColumns(schema))
				c.drop(listener);
		if(request.getParameter(CATCH_DROP)!=null)
			for(final Column c : getCatchColumns(schema))
				if(c.exists())
					c.drop(listener);
		for(final String p : getParameters(request, DROP_TABLE))
			getTable     (schema, p).drop(listener);
		for(final String p : getParameters(request, DROP_SEQUENCE))
			getSequence  (schema, p).drop(listener);

		for(final Iterator<String> i = parameterNames(request); i.hasNext(); )
		{
			final String p = i.next();
			final String sourceName = strip(p, RENAME_TABLE_PREFIX);
			if(sourceName==null)
				continue;

			final String targetName = request.getParameter(p).trim();
			if(targetName.isEmpty())
				continue;

			final Table table = getTable(schema, sourceName);
			if(table.getName().equals(targetName))
				continue;

			table.renameTo(targetName, listener);
		}
		for(final Iterator<String> i = parameterNames(request); i.hasNext(); )
		{
			final String p = i.next();
			final String sourceName = strip(p, MODIFY_COLUMN_PREFIX);
			if(sourceName==null)
				continue;

			final String targetType = request.getParameter(p).trim();
			if(targetType.isEmpty())
				continue;

			getColumn(schema, sourceName).modify(targetType, listener);
		}
		if(request.getParameter(COLUMNS_MODIFY_MISMATCHING_TYPE)!=null)
			for(final Column c : getColumnsWithMismatchingType(schema))
				c.modify(c.getRequiredType(), listener);
		for(final Iterator<String> i = parameterNames(request); i.hasNext(); )
		{
			final String p = i.next();
			final String sourceName = strip(p, RENAME_COLUMN_PREFIX);
			if(sourceName==null)
				continue;

			final String targetName = request.getParameter(p).trim();
			if(targetName.isEmpty())
				continue;

			final Column column = getColumn(schema, sourceName);
			if(column.getName().equals(targetName))
				continue;

			column.renameTo(targetName, listener);
		}
		if(request.getParameter(CATCH_RESET)!=null)
			for(final Column c : getCatchColumns(schema))
				c.update("0", listener);

		for(final String p : getParameters(request, CREATE_SEQUENCE))
			getSequence  (schema, p).create(listener);
		for(final String p : getParameters(request, CREATE_TABLE))
			getTable     (schema, p).create(listener);
		if(request.getParameter(CATCH_CREATE)!=null)
			for(final Column c : getCatchColumns(schema))
				if(!c.exists())
					c.create(listener);
		if(request.getParameter(COLUMNS_CREATE_MISSING)!=null)
			for(final Column c : getMissingColumns(schema))
				c.create(listener);
		for(final String p : getParameters(request, CREATE_COLUMN))
			getColumn    (schema, p).create(listener);
		for(final String p : getParameters(request, CREATE_CONSTRAINT))
			getConstraint(schema, p).create(listener);
	}

	private static final String[] EMPTY_STRINGS = new String[]{};

	private static final String[] getParameters(final HttpServletRequest request, final String name)
	{
		final String[] result = request.getParameterMap().get(name);
		return result!=null ? result : EMPTY_STRINGS;
	}

	private static final ArrayList<Column> getUnusedColumns(final Schema schema)
	{
		final ArrayList<Column> result = new ArrayList<Column>();
		for(final Table table : schema.getTables())
			for(final Column column : table.getColumns())
				if(!column.required() && column.exists())
					result.add(column);
		return result;
	}

	private static final ArrayList<Column> getMissingColumns(final Schema schema)
	{
		final ArrayList<Column> result = new ArrayList<Column>();
		for(final Table table : schema.getTables())
			for(final Column column : table.getColumns())
				if(column.required() && !column.exists())
					result.add(column);
		return result;
	}

	private static final ArrayList<Column> getColumnsWithMismatchingType(final Schema schema)
	{
		final ArrayList<Column> result = new ArrayList<Column>();
		for(final Table table : schema.getTables())
			for(final Column column : table.getColumns())
				if(column.mismatchesType())
					result.add(column);
		return result;
	}

	private static final ArrayList<Column> getCatchColumns(final Schema schema)
	{
		final ArrayList<Column> result = new ArrayList<Column>();
		for(final Table table : schema.getTables())
			for(final Column column : table.getColumns())
				if("catch".equals(column.getName()))
					result.add(column);
		return result;
	}

	private static Iterator<String> parameterNames(final HttpServletRequest request)
	{
		@SuppressWarnings("unchecked") // OK: problem from servlet api
		final Map<String, ?> result = request.getParameterMap();
		return result.keySet().iterator();
	}
}
