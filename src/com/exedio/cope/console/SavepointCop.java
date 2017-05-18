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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
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
	String[] getHeadingHelp()
	{
		return new String[]
		{
			"You may get a current schema savepoint here. " +
				"All savepoints fetched here are listed in the table below.",
			"A savepoint is an arbitrary string suitable for " +
				"rolling back the whole database to the state of the savepoint.",
			"NOTE: " +
				"Your database may not be set up to support savepoints, " +
				"or may not support savepoints at all, " +
				"or the database user may not be allowed to fetch savepoints. " +
				"Savepoints are provided via Model#getSchemaSavepoint()."
		};
	}

	@Override
	ChecklistIcon getChecklistIcon(final Model model)
	{
		final ArrayList<Point> store = store();
		//noinspection SynchronizationOnLocalVariableOrMethodParameter OK: comes from global store
		synchronized(store)
		{
			return getChecklistIcon(store);
		}
	}

	static ChecklistIcon getChecklistIcon(final ArrayList<Point> store)
	{
		final int size = store.size();

		if(size<1)
			return ChecklistIcon.unknown;
		final Point latest = store.get(size-1);
		if(!latest.success)
			return ChecklistIcon.error;

		String lastSuccessMessage = null;
		for(final ListIterator<Point> iter = store.listIterator(store.size()); iter.hasPrevious(); )
		{
			final Point point = iter.previous();

			if(!point.success)
				return ChecklistIcon.unknown;

			final String message = point.message;
			if(lastSuccessMessage!=null && !lastSuccessMessage.equals(message))
				return ChecklistIcon.ok;

			lastSuccessMessage = message;
		}
		return ChecklistIcon.unknown;
	}

	@Override
	ArrayList<Point> initialStore()
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
				try
				{
					storeAdd(new Point(model.getSchemaSavepoint()));
				}
				catch(final SQLException e)
				{
					storeAdd(new Point(e));
				}
			}
		}
	}

	@Override
	void writeBody(final Out out)
	{
		Savepoint_Jspm.writeBody(
				out, this,
				storeGet());
	}

	static final class Point
	{
		private final long date;
		final String message;
		final boolean success;

		Point(final String result)
		{
			this.date = System.currentTimeMillis();
			this.message = result;
			this.success = true;
		}

		Point(final SQLException exception)
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

	private void storeAdd(final Point point)
	{
		final ArrayList<Point> store = store();
		//noinspection SynchronizationOnLocalVariableOrMethodParameter OK: comes from global store
		synchronized(store)
		{
			store.add(point);
		}
	}

	private List<Point> storeGet()
	{
		final ArrayList<Point> store = store();
		final ArrayList<Point> result;
		//noinspection SynchronizationOnLocalVariableOrMethodParameter OK: comes from global store
		synchronized(store)
		{
			result = new ArrayList<>(store);
		}
		return Collections.unmodifiableList(result);
	}
}
