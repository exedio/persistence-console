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
import com.exedio.cope.misc.DatabaseListener;
import com.exedio.cope.misc.DatabaseLogListener;
import javax.servlet.http.HttpServletRequest;

final class DatabaseLogCop extends ConsoleCop<Void>
{
	static final String ENABLE = "dblogenable";
	static final String THRESHOLD = "dblogthreshold";
	static final String SQL = "dblogsql";

	DatabaseLogCop(final Args args)
	{
		super(TAB_DATBASE_LOG, "Database Log", args);
	}

	@Override
	protected DatabaseLogCop newArgs(final Args args)
	{
		return new DatabaseLogCop(args);
	}

	@Override
	void initialize(final HttpServletRequest request, final Model model)
	{
		super.initialize(request, model);
		if(isPost(request))
		{
			final boolean enable = request.getParameter(ENABLE)!=null;
			final String threshold = request.getParameter(THRESHOLD).trim();
			final String sql = request.getParameter(SQL).trim();
			model.setDatabaseListener(
					enable
					? new DatabaseLogListener(
							!threshold.isEmpty() ? Integer.parseInt(threshold) : 0,
							!sql.isEmpty() ? sql : null,
							System.out)
					: null);
		}
	}

	@Override
	void writeBody(final Out out)
	{
		final Model model = out.model;
		final DatabaseListener listener = model.getDatabaseListener();
		final boolean enabled = (listener instanceof DatabaseLogListener);
		DatabaseLog_Jspm.writeBody(this, out,
				listener!=null ? listener.getClass() : null,
				enabled,
				enabled ? ((DatabaseLogListener)listener).getDate()      : null,
				enabled ? ((DatabaseLogListener)listener).getThreshold() : 0,
				enabled ? ((DatabaseLogListener)listener).getSQL()       : null);
	}
}
