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

import com.exedio.cope.Item;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaFilter;
import com.exedio.cope.pattern.MediaTestable;
import com.exedio.cope.pattern.MediaType;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class AMediaTestable extends MediaFilter implements MediaTestable
{
	private static final long serialVersionUID = 1l;

	private final boolean fail;

	public AMediaTestable(final Media source, final boolean fail)
	{
		super(source);
		this.fail = fail;
	}

	@Override
	public Set<String> getSupportedSourceContentTypes()
	{
		return new HashSet<>(Arrays.asList(
				MediaType.JPEG,
				MediaType.PNG,
				MediaType.GIF));
	}

	@Override
	public String getContentType(final Item item)
	{
		return getSource().getContentType(item);
	}

	@Override
	public void doGetAndCommit(final HttpServletRequest request, final HttpServletResponse response, final Item item)
			throws IOException, NotFound
	{
		getSource().doGetAndCommit(request, response, item);
	}

	@Override
	public void test() throws Exception
	{
		if(fail)
			throw new IOException("test exception from " + getClass().getSimpleName());
	}
}
