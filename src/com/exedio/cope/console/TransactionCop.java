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

import com.exedio.cope.ChangeEvent;
import com.exedio.cope.ChangeListener;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Transaction;
import com.exedio.cops.Pageable;
import com.exedio.cops.Pager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import javax.servlet.http.HttpServletRequest;

final class TransactionCop extends ConsoleCop<Void> implements Pageable
{
	static final String TAB = "transactions";

	private static final Pager.Config PAGER_CONFIG = new Pager.Config(10, 100, 1000, 10000);
	static final String ENABLE  = "txlistenerenable";
	static final String DISABLE = "txlistenerdisable";
	static final String CLEAR   = "txlistenerclear";

	private final Pager pager;

	private TransactionCop(final Args args, final Pager pager)
	{
		super(TAB, "Transactions", args);
		this.pager = pager;

		//noinspection ThisEscapedInObjectConstruction
		pager.addParameters(this);
	}

	TransactionCop(final Args args)
	{
		this(args, PAGER_CONFIG.newPager());
	}

	TransactionCop(final Args args, final HttpServletRequest request)
	{
		this(args, PAGER_CONFIG.newPager(request));
	}

	@Override
	protected TransactionCop newArgs(final Args args)
	{
		return new TransactionCop(args);
	}

	@Override
	public Pager getPager()
	{
		return pager;
	}

	@Override
	public TransactionCop toPage(final Pager pager)
	{
		return new TransactionCop(args, pager);
	}

	@Override
	void doPost(final HttpServletRequest request, final Model model)
	{
		{
			if(request.getParameter(ENABLE)!=null && !model.getChangeListeners().contains(listener))
			{
				model.addChangeListener(listener);
			}
			else if(request.getParameter(DISABLE)!=null)
			{
				model.removeChangeListener(listener);
			}
			else if(request.getParameter(CLEAR)!=null)
			{
				synchronized(events)
				{
					events.clear();
				}
			}
		}
	}

	@Override boolean requiresUnsafeInlineStyle() { return true; }

	@Override boolean requiresUnsafeInlineScript() { return true; }

	@Override
	void writeHead(final Out out)
	{
		Transaction_Jspm.writeHead(out);
		StackTrace_Jspm.writeHead(out);
	}

	@Override
	void writeBody(final Out out)
	{
		final Model model = app.model;

		Transaction_Jspm.writeStats(
				out,
				model.getNextTransactionId(),
				model.getLastTransactionStartDate());
		Transaction_Jspm.writeCounters(
				out,
				model.getTransactionCounters());
		writeOpenTransations(out, model);
		writeRecorded(out, model);
	}

	private static void writeOpenTransations(
			final Out out,
			final Model model)
	{
		final Collection<Transaction> openTransactionsList = model.getOpenTransactions();
		final Transaction[] openTransactions = openTransactionsList.toArray(EMPTY_TRANSACTION);
		Arrays.sort(openTransactions, Comparator.comparingLong(Transaction::getID));

		final Thread[] threads = new Thread[openTransactions.length];
		final long[] threadIds = new long[openTransactions.length];
		final String[] threadNames = new String[openTransactions.length];
		final int[] threadPriorities = new int[openTransactions.length];
		final Thread.State[] threadStates = new Thread.State[openTransactions.length];
		final StackTraceElement[][] stacktraces = new StackTraceElement[openTransactions.length][];
		for(int i = 0; i<openTransactions.length; i++)
		{
			final Thread thread = openTransactions[i].getBoundThread();
			if(thread!=null)
			{
				threads[i] = thread;
				threadIds[i] = thread.getId();
				threadNames[i] = thread.getName();
				threadPriorities[i] = thread.getPriority();
				threadStates[i] = thread.getState();
				stacktraces[i] = thread.getStackTrace();
			}
		}

		Transaction_Jspm.writeOpen(
				out,
				openTransactions,
				threads, threadIds, threadNames, threadPriorities, threadStates, stacktraces);
	}

	private static final Transaction[] EMPTY_TRANSACTION = {};

	private void writeRecorded(
			final Out out,
			final Model model)
	{
		final ChangeEvent[] events;
		synchronized(TransactionCop.events)
		{
			events = TransactionCop.events.toArray(EMPTY_EVENTS);
		}
		Transaction_Jspm.writeRecorded(
				out, this,
				model.getChangeListeners().contains(listener),
				pager.init(Arrays.asList(events)));
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass") // ok for jspm
	static void writeItems(final ChangeEvent event, final Out bf)
	{
		boolean first = true;
		for(final Item item : event.getItems())
		{
			if(first)
				first = false;
			else
				bf.write(", ");

			bf.write(item.getCopeID());
		}
	}

	static final ArrayList<ChangeEvent> events = new ArrayList<>();

	private static final ChangeListener listener = new ChangeListener()
	{
		@Override
		public void onChange(final ChangeEvent event)
		{
			synchronized(events)
			{
				if(events.size()<10000) // prevent indefinite accumulation TODO customize limit
					events.add(event);
			}
		}

		@Override
		public String toString()
		{
			return "Console ChangeListener for Committed Transactions";
		}
	};

	private static final ChangeEvent[] EMPTY_EVENTS = {};
}
