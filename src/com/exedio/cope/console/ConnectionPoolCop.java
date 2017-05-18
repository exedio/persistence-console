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
import javax.servlet.http.HttpServletRequest;

final class ConnectionPoolCop extends ConsoleCop<Void>
{
	ConnectionPoolCop(final Args args)
	{
		super(TAB_CONNECTION_POOL, "Connection Pool", args);
	}

	@Override
	protected ConnectionPoolCop newArgs(final Args args)
	{
		return new ConnectionPoolCop(args);
	}

	static final String FLUSH = "connectionPool.flush";

	@Override
	void initialize(final HttpServletRequest request, final Model model)
	{
		super.initialize(request, model);

		if(isPost(request))
		{
			if(request.getParameter(FLUSH)!=null)
			{
				System.out.println("ConnectionPoolCop#flush");
				model.flushConnectionPool();
			}
		}
	}

	@Override
	void writeBody(final Out out)
	{
		ConnectionPool_Jspm.writeBody(out, this, out.model.getConnectionPoolInfo());
	}
}
