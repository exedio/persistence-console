/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.util.Check.requireGreaterZero;
import static com.exedio.cope.util.Check.requireNonEmpty;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.misc.DatabaseListener;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class was copied from cope. It is also junit-tested there.
 */
final class DatabaseLogListener implements DatabaseListener
{
	@SuppressWarnings("UnusedReturnValue")
	static final class Builder
	{
		private final PrintStream out;
		public static final int LOGS_LIMIT_DEFAULT = 100;
		private int logsLimit = LOGS_LIMIT_DEFAULT;
		private int durationThreshold = 0;
		private String sqlFilter = null;
		private boolean printStackTrace = false;

		Builder(final PrintStream out)
		{
			this.out = requireNonNull(out, "out");
		}

		public Builder logsLimit(final int logsLimit)
		{
			this.logsLimit = requireGreaterZero(logsLimit, "logsLimit");
			return this;
		}

		public Builder durationThreshold(final int durationThreshold)
		{
			this.durationThreshold = requireGreaterZero(durationThreshold, "durationThreshold");
			return this;
		}

		public Builder sqlFilter(final String sqlFilter)
		{
			this.sqlFilter = requireNonEmpty(sqlFilter, "sqlFilter");
			return this;
		}

		public Builder printStackTrace()
		{
			this.printStackTrace = true;
			return this;
		}

		public DatabaseLogListener build()
		{
			return new DatabaseLogListener(logsLimit, durationThreshold, sqlFilter, printStackTrace, out);
		}
	}

	private final long date;
	private final int logsLimit;
	private int logsLeft;
	private final int threshold;
	private final String sql;
	private final boolean printStackTrace;
	private final PrintStream out;

	private DatabaseLogListener(
			final int logsLimit,
			final int threshold,
			final String sql,
			final boolean printStackTrace,
			final PrintStream out)
	{
		this.date = System.currentTimeMillis();
		this.logsLimit = logsLimit;
		this.logsLeft = logsLimit;
		this.threshold = threshold;
		this.sql = sql;
		this.printStackTrace = printStackTrace;
		this.out = out;
	}

	public Date getDate()
	{
		return new Date(date);
	}

	public int getLogsLimit()
	{
		return logsLimit;
	}

	public int getLogsLeft()
	{
		return logsLeft;
	}

	public int getThreshold()
	{
		return threshold;
	}

	public String getSQL()
	{
		return sql;
	}

	public boolean isPrintStackTraceEnabled()
	{
		return printStackTrace;
	}

	@Override
	public void onStatement(
			final String statement,
			final List<Object> parameters,
			final long durationPrepare,
			final long durationExecute,
			final long durationRead,
			final long durationClose)
	{
		if(logsLeft<=0)
			return;

		if(( (threshold==0) || ((durationPrepare+durationExecute+durationRead+durationClose)>=threshold) ) &&
			( (sql==null)    || (statement.contains(sql)) ))
		{
			logsLeft--;

			final StringBuilder sb = new StringBuilder(
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z (Z)", Locale.ENGLISH).format(new Date()));

			sb.append('|');
			sb.append(durationPrepare);
			sb.append('|');
			sb.append(durationExecute);
			sb.append('|');
			sb.append(durationRead);
			sb.append('|');
			sb.append(durationClose);
			sb.append('|');
			sb.append(statement);

			if(parameters!=null)
			{
				sb.append('|');
				sb.append(parameters);
			}

			out.println(sb);

			if(printStackTrace)
				new Exception("DatabaseLogListener").printStackTrace(out); // same as Thread.dumpStack(), but directed to out
		}
	}
}
