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

package com.exedio.cope.console.example;

import com.exedio.cops.Cop;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class DummyStrictRefererValidationFilter implements Filter
{
	@Override
	@SuppressFBWarnings("BC_UNCONFIRMED_CAST")
	public void doFilter(
			final ServletRequest request,
			final ServletResponse response,
			final FilterChain chain)
			throws IOException, ServletException
	{
		doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
	}

	private static void doFilter(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final FilterChain chain)
			throws IOException, ServletException
	{
		if(!Cop.isPost(request))
		{
			chain.doFilter(request, response);
			return;
		}

		final String referer = request.getHeader("Referer");
		// BEWARE:
		// This is just for tested how to cope with referer validation.
		// DO NOT DO IT IN PRODUCTION CODE, but compare referer against a configuration option.
		if(referer!=null && referer.startsWith(
				"http://" + request.getHeader("Host") + request.getContextPath() + request.getServletPath() + "/"))
		{
			chain.doFilter(request, response);
			return;
		}

		response.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Bad Request, potential CSRF");
	}

	@Override
	public void init(final FilterConfig config)
	{
		// empty
	}

	@Override
	public void destroy()
	{
		// empty
	}
}
