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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

final class SavepointCop extends ConsoleCop<ArrayList<SavepointCop.Point>>
{
	SavepointCop(final Args args)
	{
		super(TAB_SAVEPOINT, "Savepoints", args);
	}

	@Override
	protected SavepointCop newArgs(final Args args)
	{
		return new SavepointCop(args);
	}

	@Override
	String getHeadingHelp()
	{
		return
				"You may get a current schema savepoint here. " +
				"All savepoints fetched here are listed in the table below. " +
				"A savepoint is an arbitrary string suitable for " +
				"rolling back the whole database to the state of the savepoint. " +
				"NOTE: " +
				"Your database may not be set up to support savepoints, " +
				"or may not support savepoints at all, " +
				"or the database user may not be allowed to fetch savepoints. " +
				"Savepoints are provided via Model#getSchemaSavepoint().";
	}

	@Override
	final ArrayList<SavepointCop.Point> initialStore()
	{
		return new ArrayList<>();
	}

	static final String SAVEPOINT = "savepoint";

	@Override
	void initialize(final HttpServletRequest request, final Model model)
	{
		super.initialize(request, model);

		if(isPost(request))
		{
			if(request.getParameter(SAVEPOINT)!=null)
			{
				final List<Point> list = store();
				try
				{
					list.add(new Point(model.getSchemaSavepoint()));
				}
				catch(final SQLException e)
				{
					list.add(new Point(e));
				}
			}
		}
	}

	@Override
	final void writeBody(final Out out)
	{
		Savepoint_Jspm.writeBody(
				out, this,
				store());
	}

	public static final class Point
	{
		private final long date;
		final String message;
		private final boolean success;

		public Point(final String result)
		{
			this.date = System.currentTimeMillis();
			this.message = result;
			this.success = true;
		}

		public Point(final SQLException exception)
		{
			this.date = System.currentTimeMillis();
			this.message = exception.getMessage();
			this.success = false;
		}

		Date getDate()
		{
			return new Date(date);
		}

		String getCssClass()
		{
			return success ? "text" : "notavailable";
		}
	}
}
