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
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.pattern.MediaUtil;
import java.io.IOException;
import java.io.Serial;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class ConditionUnsupported extends MediaPath
{
	@Serial
	private static final long serialVersionUID = 1l;

	@Override
	public boolean isMandatory()
	{
		return true;
	}

	@Override
	public String getContentType(final Item item)
	{
		return "text/plain";
	}

	@Override
	public void doGetAndCommit(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
		throws IOException
	{
		commit();

		MediaUtil.send(
				"text/plain", StandardCharsets.US_ASCII.name(),
				"ConditionUnsupported", response);
	}

	// does not implement Condition methods isNull / isNotNull
}
