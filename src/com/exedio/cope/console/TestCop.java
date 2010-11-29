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

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;

abstract class TestCop<I> extends ConsoleCop<HashMap<Integer, TestCop.Info>>
{
	final static String TEST = "TEST";

	TestCop(final String tab, final String name, final Args args)
	{
		super(tab, name, args);
	}

	@Override
	final void writeHead(final Out out)
	{
		Test_Jspm.writeHead(out);
	}

	@Override
	final void writeBody(final Out out)
	{
		final HttpServletRequest request = out.request;
		final Model model = out.model;

		final List<I> items = getItems(model);

		if(isPost(request) && request.getParameter(TEST)!=null)
		{
			try
			{
				model.startTransaction();
				Test_Jspm.writeBody(this, out, getHeadings(), items, true);
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}
		else
			Test_Jspm.writeBody(this, out, getHeadings(), items, false);
	}

	static abstract class Info
	{
		final long elapsed;

		Info(final long elapsed)
		{
			this.elapsed  = elapsed;
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
		final Exception exception;

		ExceptionInfo(final long elapsed, final Exception exception)
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
	abstract int check(I item);
}
