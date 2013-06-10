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

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.cope.ThreadController;

final class ThreadCop extends ConsoleCop<Void>
{
	static final String RESTART = "restart";
	static final String ID_HASH = "idhs";

	ThreadCop(final Args args)
	{
		super(TAB_THREAD, "Threads", args);
	}

	@Override
	protected ThreadCop newArgs(final Args args)
	{
		return new ThreadCop(args);
	}

	@Override
	void initialize(final HttpServletRequest request, final Model model)
	{
		super.initialize(request, model);
		if(isPost(request))
		{
			if(request.getParameter(RESTART)!=null)
			{
				final int id = getIntParameter(request, ID_HASH, Integer.MIN_VALUE);
				for(final ThreadController thread : model.getThreadControllers())
				{
					if(System.identityHashCode(thread)==id) // cannot use getId(), because this is -1 if not active
						thread.restart();
				}
			}
		}
	}

	@Override
	void writeHead(final Out out)
	{
		StackTrace_Jspm.writeHead(out);
	}

	@Override
	final void writeBody(final Out out)
	{
		failIfNotConnected(out.model);
		Thread_Jspm.writeBody(out, out.model.getThreadControllers());
	}
}
