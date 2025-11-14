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

import com.exedio.cope.ChangeListener;
import com.exedio.cope.ChangeListenerDispatcherInfo;
import com.exedio.cope.Model;
import com.exedio.cops.Pageable;
import com.exedio.cops.Pager;
import java.util.Arrays;
import java.util.HashSet;
import jakarta.servlet.http.HttpServletRequest;

final class ChangeListenerCop extends ConsoleCop<Void> implements Pageable
{
	static final String TAB = "changelistener";

	private static final Pager.Config PAGER_CONFIG = new Pager.Config(20, 100, 1000, 10000);
	static final String REMOVE_SELECTED = "removeSelected";
	static final String REMOVE_CHECKBOX = "rm";
	static final String REMOVE_ALL      = "removeAll";

	private final Pager pager;

	ChangeListenerCop(final Args args)
	{
		this(args, PAGER_CONFIG.newPager());
	}

	private ChangeListenerCop(final Args args, final Pager pager)
	{
		super(TAB, "Change Listeners", args);
		this.pager = pager;

		//noinspection ThisEscapedInObjectConstruction
		pager.addParameters(this);
	}

	ChangeListenerCop(final Args args, final HttpServletRequest request)
	{
		this(args, PAGER_CONFIG.newPager(request));
	}

	@Override
	protected ChangeListenerCop newArgs(final Args args)
	{
		return new ChangeListenerCop(args, pager);
	}

	@Override
	public Pager getPager()
	{
		return pager;
	}

	@Override
	public ChangeListenerCop toPage(final Pager pager)
	{
		return new ChangeListenerCop(args, pager);
	}

	@Override boolean requiresUnsafeInlineStyle() { return true; }

	@Override boolean requiresUnsafeInlineScript() { return true; }

	@Override
	void doPost(final HttpServletRequest request, final Model model)
	{
		{
			if(request.getParameter(REMOVE_SELECTED)!=null)
			{
				final String[] toDeleteArray = request.getParameterValues(REMOVE_CHECKBOX);

				if(toDeleteArray!=null)
				{
					final HashSet<String> toDelete = new HashSet<>(Arrays.asList(toDeleteArray));
					for(final ChangeListener listener : model.getChangeListeners())
					{
						if(toDelete.contains(toID(listener)))
							model.removeChangeListener(listener);
					}
				}
			}
			if(request.getParameter(REMOVE_ALL)!=null)
			{
				model.removeAllChangeListeners();
			}
		}
	}

	@Override
	void writeBody(final Out out)
	{
		final Model model = app.model;
		ChangeListener_Jspm.writeBody(this, out,
				model.getChangeListenersInfo(),
				pager.init(model.getChangeListeners()),
				model.getChangeListenersInfo(),
				dispatcherInfo(model));
	}

	String toID(final ChangeListener listener)
	{
		return listener.getClass().getName() + '@' + System.identityHashCode(listener);
	}

	private static ChangeListenerDispatcherInfo dispatcherInfo(final Model model)
	{
		try
		{
			return model.getChangeListenerDispatcherInfo();
		}
		catch(final Model.NotConnectedException ignored)
		{
			return null;
		}
	}
}
