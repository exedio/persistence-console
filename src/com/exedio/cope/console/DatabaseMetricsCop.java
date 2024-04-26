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
import com.exedio.cope.SchemaInfo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

final class DatabaseMetricsCop extends ConsoleCop<ArrayList<DatabaseMetricsCop.Measurement>>
{
	static final String TAB = "dbmetrics";

	static final String SQL = "dbmetricssql";
	static final String LOOP = "dbmetricsloop";

	DatabaseMetricsCop(final Args args)
	{
		super(TAB, "Database Metrics", args);
	}

	@Override
	protected DatabaseMetricsCop newArgs(final Args args)
	{
		return new DatabaseMetricsCop(args);
	}

	@Override
	void initialize(final HttpServletRequest request, final Model model)
	{
		super.initialize(request, model);
		if(isPost(request))
		{
			final String sql = request.getParameter(SQL).trim();
			final int loop = Integer.parseInt(request.getParameter(LOOP).trim());

			try(Connection c = SchemaInfo.newConnection(model);
				 Statement st = c.createStatement())
			{
				final long start = System.nanoTime();

				long resultSize = 0;
				for(int i = 0; i<loop; i++)
					try(ResultSet rs = st.executeQuery(sql))
					{
						while(rs.next())
							resultSize++;
					}

				storeAdd(new Measurement(System.nanoTime() - start, loop, sql, resultSize));
			}
			catch(final SQLException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	void writeBody(final Out out)
	{
		final List<Measurement> history = storeGet();
		final Measurement next =
				history.isEmpty()
				? new Measurement(0, 1, "VALUES 42", 0)
				: history.get(history.size()-1);
		DatabaseMetrics_Jspm.writeBody(this, out, next, history);
	}

	static final class Measurement
	{
		private final long date;
		final int loop;
		final String sql;
		final long resultSize;
		final long elapsedNanos;

		Measurement(final long elapsedNanos, final int loop, final String sql, final long resultSize)
		{
			this.date = System.currentTimeMillis();
			this.loop = loop;
			this.sql = sql;
			this.resultSize = resultSize / loop;
			this.elapsedNanos = elapsedNanos / loop;
		}

		Date getDate()
		{
			return new Date(date);
		}
	}

	@Override
	ArrayList<Measurement> initialStore()
	{
		return new ArrayList<>();
	}

	private void storeAdd(final Measurement measurement)
	{
		final ArrayList<Measurement> store = store();
		synchronized(store)
		{
			store.add(measurement);
		}
	}

	private List<Measurement> storeGet()
	{
		final ArrayList<Measurement> store = store();
		final ArrayList<Measurement> result;
		synchronized(store)
		{
			result = new ArrayList<>(store);
		}
		return Collections.unmodifiableList(result);
	}
}
