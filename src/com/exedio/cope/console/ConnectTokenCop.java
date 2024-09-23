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

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.misc.ConnectToken;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

final class ConnectTokenCop extends ConsoleCop<Void>
{
	static final String TAB = "connectToken";
	static final String ISSUE = "issue";
	static final String RETURN_SELECTED = "returnSelected";
	static final String RETURN_CHECKBOX = "rt";

	ConnectTokenCop(final Args args)
	{
		super(TAB, "Connect Tokens", args);
	}

	@Override
	protected ConnectTokenCop newArgs(final Args args)
	{
		return new ConnectTokenCop(args);
	}

	@Override boolean requiresUnsafeInlineScript() { return true; }

	@Override
	void writeHead(final Out out)
	{
		Properties_Jspm.writeHead(out);
	}

	@Override
	void writeBody(final Out out)
	{
		final HttpServletRequest request = out.request;
		final Model model = app.model;
		final ConnectProperties properties = ConnectToken.getProperties(model);

		if(isPost(request))
		{
			if(request.getParameter(ISSUE)!=null)
			{
				out.connect();
			}
			if(request.getParameter(RETURN_SELECTED)!=null)
			{
				final String[] ids = request.getParameterValues(RETURN_CHECKBOX);
				if(ids!=null)
				{
					final HashMap<Integer, ConnectToken> map = new HashMap<>();
					for(final ConnectToken token : ConnectToken.getTokens(model))
						map.put(token.getID(), token);

					for(final String id : ids)
						map.get(Integer.valueOf(id)).returnStrictly();
				}
			}
		}

		ConnectToken_Jspm.writeBody(
				out, this,
				properties,
				model);
	}
}
