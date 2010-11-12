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

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.ChangeListener;
import com.exedio.cope.Model;

final class ChangeListenerCop extends ConsoleCop
{
	static final String REMOVE_SELECTED = "removeSelected";
	static final String REMOVE_CHECKBOX = "rm";

	ChangeListenerCop(final Args args)
	{
		super(TAB_CHANGE_LISTENER, "change", args);
	}

	@Override
	protected ChangeListenerCop newArgs(final Args args)
	{
		return new ChangeListenerCop(args);
	}

	@Override
	final void writeBody(final Out out)
	{
		final HttpServletRequest request = out.request;
		final Model model = out.model;

		if(isPost(request))
		{
			if(request.getParameter(REMOVE_SELECTED)!=null)
			{
				final String[] toDeleteArray = request.getParameterValues(REMOVE_CHECKBOX);

				if(toDeleteArray!=null)
				{
					final HashSet<String> toDelete = new HashSet<String>(Arrays.asList(toDeleteArray));
					for(final ChangeListener listener : model.getChangeListeners())
					{
						if(toDelete.contains(toID(listener)))
							model.removeChangeListener(listener);
					}
				}
			}
		}

		ChangeListener_Jspm.writeBody(this, out,
				model.getChangeListenersCleared(),
				model.getChangeListeners(),
				model.getChangeListenersCleared());
	}

	final String toID(final ChangeListener listener)
	{
		return listener.getClass().getName() + '@' + System.identityHashCode(listener);
	}
}
