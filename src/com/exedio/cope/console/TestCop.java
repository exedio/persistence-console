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

import com.exedio.cope.Model;
import com.exedio.cope.console.Stores.Store;
import com.exedio.cope.misc.TimeUtil;
import com.exedio.cops.Cop;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

abstract class TestCop<I> extends ConsoleCop<HashMap<String, TestCop.Info>>
{
	static final String ID = "testajax";
	static final String ITERATE = "iterate";
	static final String SUCCESS_CLASS = "success";

	protected final TestArgs testArgs;
	protected final String id;
	protected final boolean iterate;

	protected static class TestArgs
	{
		protected final String id;
		protected final boolean iterate;

		TestArgs()
		{
			this.id = null;
			this.iterate = false;
		}

		private TestArgs(final String id, final boolean iterate)
		{
			this.id = id;
			this.iterate = iterate;
		}

		TestArgs(final HttpServletRequest request)
		{
			this.id = request.getParameter(TestCop.ID);
			this.iterate = Cop.getBooleanParameter(request, TestCop.ITERATE);
		}

		void addParameters(final TestCop<?> cop)
		{
			cop.addParameter(ID, id);
			cop.addParameter(ITERATE, iterate);
		}

		TestArgs toTest(final String id, final boolean iterate)
		{
			return new TestArgs(id, iterate);
		}
	}

	TestCop(final String tab, final String name, final Args args, final TestArgs testArgs)
	{
		super(tab, name, args);
		this.testArgs = testArgs;
		this.id = testArgs.id;
		this.iterate = testArgs.iterate;

		testArgs.addParameters(this);
	}

	final TestCop<I> toTest(final String id, final boolean iterate)
	{
		return newTestArgs(testArgs.toTest(id, iterate));
	}

	abstract TestCop<I> newTestArgs(TestArgs testArgs);

	@Override
	final void writeHead(final Out out)
	{
		Test_Jspm.writeHead(out);
	}

	@Override
	final void writeBody(final Out out)
	{
		final Model model = out.model;

		if(!toleratesNotConnected())
			failIfNotConnected(out.model);

		final Store<HashMap<String, Info>> testStore = getStore();
		Test_Jspm.writeBody(
				this, out,
				getHeadings(), getItems(model),
				testStore!=null ? testStore.value : new HashMap<String, Info>());
	}

	@Override
	boolean isAjax()
	{
		return id!=null;
	}

	@Override
	void writeAjax(final Out out)
	{
		if(id==null)
			throw new IllegalArgumentException();

		final I item = forID(out.model, id);
		if(item==null)
			throw new IllegalArgumentException(id);

		final long start = System.nanoTime();
		Info info;
		try
		{
			final long result = check(item, out.model);
			info = new ResultInfo(TimeUtil.toMillies(System.nanoTime(), start), result);
		}
		catch(final Exception e)
		{
			info = new ExceptionInfo(TimeUtil.toMillies(System.nanoTime(), start), e);
		}
		catch(final AssertionError e)
		{
			info = new ExceptionInfo(TimeUtil.toMillies(System.nanoTime(), start), e);
		}

		final Store<HashMap<String, Info>> testStore = getStore();
		HashMap<String, Info> infos = testStore!=null ? testStore.value : null;
		if(infos==null)
			infos = new HashMap<String, Info>();
		infos.put(id, info);
		putStore(infos);

		final List<I> items = getItems(out.model);
		final I nextItem = nextItem(items, id);

		final int headingsLength = getHeadings().length;
		out.writeRaw(
			"<?xml version=\"1.0\" encoding=\"" + UTF_8.name() + "\" standalone=\"yes\"?>" +
			"<response");
		if(nextItem!=null)
		{
			out.writeRaw(" iterate=\"");
			final String nextId = getID(nextItem);
			out.write(toTest(nextId, true));
			out.writeRaw('"');
		}
		out.writeRaw(">" +
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
		Test_Jspm.writeRow(out, this, headingsLength, item, id, info);
		out.writeRaw(
			"]]></update>" +
			"<update id=\"summary\"><![CDATA[");
		writeSummary(out, headingsLength, items, infos);
		out.writeRaw(
			"]]></update>" +
			"</response>");
	}

	private final I nextItem(final List<I> items, final String id)
	{
		if(!iterate)
			return null;

		for(final Iterator<I> i = items.iterator(); i.hasNext(); )
			if(id.equals(getID(i.next())))
				return i.hasNext() ? i.next() : null;

		return null;
	}

	void writeSummary(
			final Out out,
			final int headingsLength,
			final List<I> items,
			final HashMap<String, Info> infos)
	{
		int elapsed = 0;
		Info date = null;
		int failures = 0;
		boolean isError = false;

		for(final Info info : infos.values())
		{
			elapsed  += info.elapsed;
			date      = info.oldest(date);
			failures += info.failures();
			isError  |= info.isError();
		}

		Test_Jspm.writeSummary(
				out,
				this,
				headingsLength,
				getID(items.get(0)),
				items.size()==infos.size(),
				elapsed,
				date!=null ? date.getDate() : null,
				failures,
				(failures>0) ? "failure" : null,
				isError);
	}

	static abstract class Info
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

	boolean toleratesNotConnected()
	{
		return true;
	}

	abstract List<I> getItems(Model model);
	abstract String[] getHeadings();
	abstract void writeValue(Out out, I item, int h);
	abstract String getID(I item);
	abstract I forID(Model model, String id);
	abstract long check(I item, Model model);
}
