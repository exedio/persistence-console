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

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.Hash;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

final class HashCop extends ConsoleCop<Void>
{
	static final String TAB = "hash";

	static final String CHECKED = "checked";
	static final String COMPUTE = "compute";
	static final String COMPUTE_PLAINTEXT = "compute.plaintext";
	static final String MEASURE = "measure";

	HashCop(final Args args)
	{
		super(TAB, "Hashes", args);
	}

	@Override
	protected HashCop newArgs(final Args args)
	{
		return new HashCop(args);
	}

	@Override
	void writeBody(final Out out)
	{
		final HttpServletRequest request = out.request;
		final Model model = app.model;

		final ArrayList<Hash> hashes = new ArrayList<>();
		for(final Type<?> type : model.getTypes())
			for(final Feature f : type.getDeclaredFeatures())
				if(f instanceof Hash)
					hashes.add((Hash)f);
		final boolean post = isPost(request);
		final ArrayList<Hash> checkedHashes;
		if(post)
		{
			checkedHashes = new ArrayList<>();
			final String[] values = request.getParameterValues(CHECKED);
			if(values!=null)
				for(final String value : values)
					checkedHashes.add((Hash)model.getFeature(value));
		}
		else
		{
			checkedHashes = hashes;
		}
		Hash_Jspm.writeBody(
				out, this,
				hashes, checkedHashes,
				post && request.getParameter(COMPUTE)!=null,
				post ?  request.getParameter(COMPUTE_PLAINTEXT) : null,
				post && request.getParameter(MEASURE)!=null);
	}
}
