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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.Hash;

final class HashCop extends ConsoleCop<Void>
{
	final static String CHECKED = "checked";
	final static String MEASURE = "measure";

	HashCop(final Args args)
	{
		super(TAB_HASH, "Hashes", args);
	}

	@Override
	protected HashCop newArgs(final Args args)
	{
		return new HashCop(args);
	}

	@Override
	final void writeBody(final Out out)
	{
		final HttpServletRequest request = out.request;
		final Model model = out.model;

		final ArrayList<Hash> hashes = new ArrayList<Hash>();
		for(final Type<?> type : model.getTypes())
			for(final Feature f : type.getDeclaredFeatures())
				if(f instanceof Hash)
					hashes.add((Hash)f);
		final ArrayList<Hash> checkedHashes;
		if(isPost(request))
		{
			checkedHashes = new ArrayList<Hash>();
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
				isPost(request) && request.getParameter(MEASURE)!=null);
	}
}
