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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.cops.Cop;

abstract class TestAjaxCop<I> extends ConsoleCop<HashMap<String, TestAjaxCop.Info>>
{
	final static String ID = "testajax";
	final static String ITERATE = "iterate";

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
			this.id = request.getParameter(TestAjaxCop.ID);
			this.iterate = Cop.getBooleanParameter(request, TestAjaxCop.ITERATE);
		}

		void addParameters(final TestAjaxCop cop)
		{
			cop.addParameter(ID, id);
			cop.addParameter(ITERATE, iterate);
		}

		TestArgs toTest(final String id, final boolean iterate)
		{
			return new TestArgs(id, iterate);
		}
	}

	TestAjaxCop(final String tab, final String name, final Args args, final TestArgs testArgs)
	{
		super(tab, name, args);
		this.testArgs = testArgs;
		this.id = testArgs.id;
		this.iterate = testArgs.iterate;

		testArgs.addParameters(this);
	}

	final TestAjaxCop toTest(final String id, final boolean iterate)
	{
		return newTestArgs(testArgs.toTest(id, iterate));
	}

	abstract TestAjaxCop newTestArgs(TestArgs testArgs);

	@Override
	final void writeHead(final Out out)
	{
		TestAjax_Jspm.writeHead(out);
	}

	@Override
	final void writeBody(final Out out)
	{
		final Model model = out.model;

		final ConsoleServlet.Store<HashMap<String, Info>> testStore = getStore();
		TestAjax_Jspm.writeBody(
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
			final int result = check(item);
			info = new ResultInfo((System.nanoTime() - start) / 1000000, result);
		}
		catch(final Exception e)
		{
			info = new ExceptionInfo((System.nanoTime() - start) / 1000000, e);
		}
		catch(final AssertionError e)
		{
			info = new ExceptionInfo((System.nanoTime() - start) / 1000000, e);
		}

		final ConsoleServlet.Store<HashMap<String, Info>> testStore = getStore();
		HashMap<String, Info> infos = testStore!=null ? testStore.value : null;
		if(infos==null)
			infos = new HashMap<String, Info>();
		infos.put(id, info);
		putStore(infos);

		final List<I> items = getItems(out.model);
		final I nextItem = nextItem(items, id);

		final int headingsLength = getHeadings().length;
		out.writeRaw(
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
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
		out.writeRaw("\"><![CDATA[");
		TestAjax_Jspm.writeRow(out, this, headingsLength, item, id, info);
		out.writeRaw(
			"]]></update>" +
			"<update id=\"total\"><![CDATA[");
		writeTotal(out, headingsLength, items, infos);
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

	void writeTotal(
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

		TestAjax_Jspm.writeTotal(
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

		abstract int failures();
		abstract String getCellClass();
		abstract boolean isError();
		abstract void writeCellContent(Out out);
	}

	static final class ResultInfo extends Info
	{
		final int result;

		ResultInfo(final long elapsed, final int result)
		{
			super(elapsed);
			this.result = result;
		}

		@Override
		int failures()
		{
			return result;
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
		int failures()
		{
			return 1;
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

	abstract List<I> getItems(Model model);
	abstract String[] getHeadings();
	abstract void writeValue(Out out, I item, int h);
	abstract String getID(I item);
	abstract I forID(Model model, String id);
	abstract int check(I item);
}