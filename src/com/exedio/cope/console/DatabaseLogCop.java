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
import com.exedio.cope.console.DatabaseLogListener.Builder;
import com.exedio.cope.misc.DatabaseListener;
import javax.servlet.http.HttpServletRequest;

final class DatabaseLogCop extends ConsoleCop<Void>
{
	static final String TAB = "dblogs";

	static final String ENABLE = "dblogenable";
	static final String LIMIT = "dbloglimit";
	static final String THRESHOLD = "dblogthreshold";
	static final String SQL = "dblogsql";
	static final String STACKTRACE = "dblogstacktrace";

	DatabaseLogCop(final Args args)
	{
		super(TAB, "Database Log", args);
	}

	@Override
	protected DatabaseLogCop newArgs(final Args args)
	{
		return new DatabaseLogCop(args);
	}

	@Override
	void doPost(final HttpServletRequest request, final Model model)
	{
		{
			final DatabaseLogListener listener;

			if(request.getParameter(ENABLE)!=null)
			{
				final Builder builder = new Builder(System.out);
				{
					final String p = request.getParameter(LIMIT).trim();
					if(!p.isEmpty())
						builder.logsLimit(Integer.parseInt(p));
				}
				{
					final String p = request.getParameter(THRESHOLD).trim();
					if(!p.isEmpty())
						builder.durationThreshold(Integer.parseInt(p));
				}
				{
					final String p = request.getParameter(SQL).trim();
					if(!p.isEmpty())
						builder.sqlFilter(p);
				}
				if(request.getParameter(STACKTRACE)!=null)
					builder.printStackTrace();

				listener = builder.build();
			}
			else
			{
				listener = null;
			}

			model.setDatabaseListener(listener);
		}
	}

	@Override
	void writeBody(final Out out)
	{
		final Model model = app.model;
		final DatabaseListener listener = model.getDatabaseListener();
		final boolean enabled = (listener instanceof DatabaseLogListener);
		DatabaseLog_Jspm.writeBody(this, out,
				listener!=null ? listener.getClass() : null,
				enabled,
				enabled ? ((DatabaseLogListener)listener).getDate()      : null,
				enabled ? ((DatabaseLogListener)listener).getLogsLimit() : Builder.LOGS_LIMIT_DEFAULT,
				enabled ? ((DatabaseLogListener)listener).getLogsLeft()  : 0,
				enabled ? ((DatabaseLogListener)listener).getThreshold() : 0,
				enabled ? ((DatabaseLogListener)listener).getSQL()       : null,
				enabled &&((DatabaseLogListener)listener).isPrintStackTraceEnabled());
	}
}
