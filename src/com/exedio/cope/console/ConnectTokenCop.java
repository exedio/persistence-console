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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.misc.ConnectToken;

final class ConnectTokenCop extends ConsoleCop
{
	static final String RETURN_SELECTED = "returnSelected";
	static final String RETURN_CHECKBOX = "rt";

	ConnectTokenCop(final Args args)
	{
		super(TAB_CONNECT_TOKEN, "Connect Tokens", args);
	}

	@Override
	protected ConnectTokenCop newArgs(final Args args)
	{
		return new ConnectTokenCop(args);
	}

	@Override
	void writeHead(final Out out)
	{
		Properties_Jspm.writeHead(out);
	}

	@Override
	final void writeBody(final Out out)
	{
		final HttpServletRequest request = out.request;
		final Model model = out.model;

		if(isPost(request))
		{
			if(request.getParameter(RETURN_SELECTED)!=null)
			{
				final String[] ids = request.getParameterValues(RETURN_CHECKBOX);
				if(ids!=null)
				{
					final HashMap<Integer, ConnectToken> map = new HashMap<Integer, ConnectToken>();
					for(final ConnectToken token : ConnectToken.getTokens(model))
						map.put(token.getID(), token);

					for(final String id : ids)
						map.get(Integer.valueOf(id)).returnIt();
				}
			}
		}

		// TODO use ConnectToken.getProperties
		final ConnectProperties props = model.getConnectProperties();

		ConnectToken_Jspm.writeBody(
				out, this,
				props,
				model);
	}
}