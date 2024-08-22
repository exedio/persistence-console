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

import static com.exedio.cope.console.Format.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Model;
import com.exedio.cope.misc.TimeUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;

abstract class TestCop<I> extends ConsoleCop<TestCop.Store>
{
	static final String SUCCESS_CLASS = "success";
	@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // ok for jspm
	static final String NOT_YET_TESTED_CLASS = "notYetTested";

	protected final TestArgs testArgs;
	protected final String id;

	protected static class TestArgs
	{
		private static final String ID = "testajax";

		protected final String id;

		TestArgs()
		{
			this.id = null;
		}

		private TestArgs(final String id)
		{
			this.id = id;
		}

		TestArgs(final HttpServletRequest request)
		{
			this.id = request.getParameter(ID);
		}

		void addParameters(final TestCop<?> cop)
		{
			cop.addParameter(ID, id);
		}

		TestArgs toTest(final String id)
		{
			return new TestArgs(id);
		}
	}

	TestCop(final String tab, final String name, final Args args, final TestArgs testArgs)
	{
		super(tab, name, args);
		this.testArgs = testArgs;
		this.id = testArgs.id;

		//noinspection ThisEscapedInObjectConstruction
		testArgs.addParameters(this);
	}

	final TestCop<I> toTest(final String id)
	{
		return newTestArgs(testArgs.toTest(id));
	}

	@Override
	final Store initialStore()
	{
		return new Store();
	}

	abstract TestCop<I> newTestArgs(TestArgs testArgs);

	@Override
	final ChecklistIcon getChecklistIcon()
	{
		if(requiresConnect() && !app.model.isConnected())
			return ChecklistIcon.unknown;

		final List<I> items = getItems();
		if(items.isEmpty())
			return ChecklistIcon.empty;

		return store().getChecklistIcon(getItemIDs(items));
	}

	@Override boolean requiresUnsafeInlineStyle() { return true; }

	@Override boolean requiresUnsafeInlineScript() { return true; }

	@Override
	final void writeHead(final Out out)
	{
		Test_Jspm.writeHead(out);
	}

	@Override
	final void writeBody(final Out out)
	{
		if(requiresConnect())
			failIfNotConnected();

		writeIntro(out);

		Test_Jspm.writeBody(
				this, out,
				columns(), getItems(),
				store());
	}

	void writeIntro(final Out out)
	{
	}

	@Override
	final boolean isAjax()
	{
		return id!=null;
	}

	@Override
	final void writeAjax(final Out out)
	{
		if(id==null)
			throw new IllegalArgumentException();

		final I item = forID(id);
		if(item==null)
			throw new IllegalArgumentException(id);

		final long start = System.nanoTime();
		Info info;
		try
		{
			final long result = check(item);
			info = new ResultInfo(TimeUtil.toMillies(System.nanoTime(), start), result);
		}
		catch(final Exception | AssertionError e)
		{
			info = new ExceptionInfo(TimeUtil.toMillies(System.nanoTime(), start), e);
		}

		final Store store = store();
		store.put(id, info);

		final List<Column<I>> columns = columns();
		final List<I> items = getItems();

		final int headingsLength = columns.size();
		out.writeRaw(
			"<?xml version=\"1.0\" encoding=\"" + UTF_8.name() + "\" standalone=\"yes\"?>" +
			"<response>" +
			"<update id=\"");
		out.write(id);
		out.writeRaw("\"");
		final String rowClass = info.getRowClass();
		{
			out.writeRaw(" className=\"");
			out.write(rowClass);
			out.writeRaw('"');
		}
		out.writeRaw("><![CDATA[");
		Test_Jspm.writeRow(out, this, columns, headingsLength, item, id, info);
		out.writeRaw(
			"]]></update>" +
			"<update id=\"summary\"><![CDATA[");
		writeSummary(out, headingsLength, items, store);
		out.writeRaw(
			"]]></update>" +
			"</response>");
	}

	final List<String> getItemIDs(final List<I> items)
	{
		return items.stream().map(this::getID).collect(Collectors.toList());
	}

	final void writeSummary(
			final Out out,
			final int headingsLength,
			final List<I> items,
			final Store store)
	{
		final Store.Summary summary = store.summarize(getItemIDs(items));
		Test_Jspm.writeSummary(
				out,
				headingsLength,
				summary,
				(summary.failures>0) ? "failure" : null);
	}

	abstract static class Info
	{
		private final long date = System.currentTimeMillis();
		final long elapsed;

		Info(final long elapsed)
		{
			this.elapsed  = elapsed;
		}

		Date getDate()
		{
			return new Date(date);
		}

		Info oldest(final Info other)
		{
			if(other==null)
				return this;

			return date<=other.date ? this : other;
		}

		abstract long failures();
		abstract String getRowClass();
		abstract String getCellClass();
		abstract boolean isError();
		abstract void writeCellContent(Out out);
	}

	static final class ResultInfo extends Info
	{
		final long result;

		ResultInfo(final long elapsed, final long result)
		{
			super(elapsed);
			this.result = result;
		}

		@Override
		long failures()
		{
			return result;
		}

		@Override
		String getRowClass()
		{
			return (result>0) ? null : SUCCESS_CLASS;
		}

		@Override
		String getCellClass()
		{
			return (result>0) ? "failure" : null;
		}

		@Override
		boolean isError()
		{
			return result>0;
		}

		@Override
		void writeCellContent(final Out out)
		{
			out.write(format(result));
		}
	}

	static final class ExceptionInfo extends Info
	{
		final Throwable exception;

		ExceptionInfo(final long elapsed, final Throwable exception)
		{
			super(elapsed);
			this.exception = exception;
		}

		@Override
		long failures()
		{
			return 1;
		}

		@Override
		String getRowClass()
		{
			return null;
		}

		@Override
		String getCellClass()
		{
			return "notavailable";
		}

		@Override
		boolean isError()
		{
			return true;
		}

		@Override
		void writeCellContent(final Out out)
		{
			out.writeStackTrace(exception);
		}
	}

	static final class Store
	{
		private final HashMap<String, Info> infos = new HashMap<>();

		Info get(final String id)
		{
			synchronized(infos)
			{
				return infos.get(id);
			}
		}

		void put(final String id, final Info info)
		{
			synchronized(infos)
			{
				infos.put(id, info);
			}
		}

		ChecklistIcon getChecklistIcon(final List<String> itemIDs)
		{
			synchronized(infos)
			{
				boolean complete = true;
				for (final String id: itemIDs)
				{
					final Info info = infos.get(id);
					if(info == null)
						complete = false;
					else if(info.isError())
						return ChecklistIcon.error;
				}
				if(!complete)
					return ChecklistIcon.unknown;
			}
			return ChecklistIcon.ok;
		}

		Summary summarize(final List<String> summarizedItemsIds)
		{
			boolean complete;
			long elapsed = 0;
			Info date = null;
			long failures = 0;
			boolean isError = false;

			synchronized(infos)
			{
				complete = true;

				for(final String id : summarizedItemsIds)
				{
					final Info info = infos.get(id);
					if (info==null)
					{
						complete = false;
					}
					else
					{
						elapsed += info.elapsed;
						date = info.oldest(date);
						failures += info.failures();
						isError = isError || info.isError();
					}
				}
			}

			return new Summary(complete, elapsed, date, failures, isError);
		}

		static final class Summary
		{
			final boolean complete;
			final long elapsed;
			private final Info date;
			final long failures;
			final boolean isError;

			Summary(
					final boolean complete,
					final long elapsed,
					final Info date,
					final long failures,
					final boolean isError)
			{
				this.complete = complete;
				this.elapsed = elapsed;
				this.date = date;
				this.failures = failures;
				this.isError = isError;
			}

			Date date()
			{
				return date!=null ? date.getDate() : null;
			}
		}
	}

	boolean requiresConnect()
	{
		return false;
	}

	static final void writeValueLong(final Out out, final String s)
	{
		if(s.length()<=80)
		{
			out.write(s);
			return;
		}

		out.writeStatic("<div class=\"long\">");
		out.write(s.replaceAll(",", ", ")); // allow word wrap
		out.writeStatic("</div>");
	}

	String getNoItemsMessage()
	{
		return "There are no "+name+" in this model.";
	}

	final String getViolationSqlIfConnected(final I item)
	{
		try
		{
			return getViolationSql(item);
		}
		catch(final Model.NotConnectedException e)
		{
			return null;
		}
	}

	/**
	 * @return SQL query that finds violations, or null to indicate that violation SQL is not supported
	 */
	String getViolationSql(final I item)
	{
		return null;
	}

	abstract List<I> getItems();
	abstract List<Column<I>> columns();

	static final <I> Column<I> column(final String heading, final Function<I,String> cell)
	{
		return new Column<>(heading, toConsumer(cell), true);
	}

	// TODO
	// I'd rather want to have a modifier method Column#nonFilterable,
	// but this causes compiler errors I don't understand.
	static final <I> Column<I> columnNonFilterable(final String heading, final Function<I,String> cell)
	{
		return new Column<>(heading, toConsumer(cell), false);
	}

	private static <I> BiConsumer<Out, I> toConsumer(final Function<I,String> cell)
	{
		return (out, field) -> out.write(cell.apply(field));
	}

	static final <I> Column<I> column(final String heading, final BiConsumer<Out, I> cell)
	{
		return new Column<>(heading, cell, true);
	}

	static final <I> Column<I> columnNonFilterable(final String heading, final BiConsumer<Out, I> cell)
	{
		return new Column<>(heading, cell, false);
	}

	protected static final class Column<I>
	{
		final String heading;
		final BiConsumer<Out, I> cell;
		final boolean filterable;

		Column(final String heading, final BiConsumer<Out, I> cell, final boolean filterable)
		{
			this.heading = requireNonNull(heading);
			this.cell = requireNonNull(cell);
			this.filterable = filterable;
		}
	}

	abstract String getID(I item);
	abstract I forID(String id);
	abstract long check(I item);
}
