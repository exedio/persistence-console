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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.pattern.MediaUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;

/**
 * A test subclass of MediaPath for unit-testing custom extentions of MediaPath.
 * @author Ralf Wiebicke
 */
public final class ANameServer extends MediaPath
{
	@Serial
	private static final long serialVersionUID = 1l;

	final StringField source;

	@UsageEntryPoint // OK: used by reflection
	ANameServer(final StringField source)
	{
		this.source = source;
	}

	@Override
	public boolean isMandatory()
	{
		return false;
	}

	@Override
	public String getContentType(final Item item)
	{
		return source.get(item)!=null ? "text/plain" : null;
	}

	private static final long EXPIRES_OFFSET = 1000 * 5; // 5 seconds

	private static final String RESPONSE_EXPIRES = "Expires";

	@Override
	public void doGetAndCommit(
			final HttpServletRequest request, final HttpServletResponse response,
			final Item item)
		throws IOException, NotFound
	{
		final String content = source.get(item);

		commit();

		//System.out.println("contentType="+contentType);
		if(content==null)
			throw notFoundIsNull();

		if(content.endsWith(" error"))
			throw new RuntimeException("test error in ANameServer");

		final long now = System.currentTimeMillis();
		response.setDateHeader(RESPONSE_EXPIRES, now+EXPIRES_OFFSET);

		final byte[] contentBytes = content.getBytes(UTF_8);
		//response.setHeader("Cache-Control", "public");

		System.out.println(request.getMethod()+' '+request.getProtocol()+" modified: "+contentBytes.length);

		MediaUtil.send("text/plain", contentBytes, response);
	}

	@Override
	public Condition isNull()
	{
		return source.isNull();
	}

	@Override
	public Condition isNotNull()
	{
		return source.isNotNull();
	}
}
