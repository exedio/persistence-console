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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.MediaImageMagickFilter;

final class MediaTestableCop extends TestCop<MediaImageMagickFilter>
{
	MediaTestableCop(final Args args, final TestArgs testArgs)
	{
		super(TAB_MEDIA_TESTABLE, "Media Testables", args, testArgs);
	}

	@Override
	protected MediaTestableCop newArgs(final Args args)
	{
		return new MediaTestableCop(args, testArgs);
	}

	@Override
	protected MediaTestableCop newTestArgs(final TestArgs testArgs)
	{
		return new MediaTestableCop(args, testArgs);
	}

	@Override
	List<MediaImageMagickFilter> getItems(final Model model)
	{
		final ArrayList<MediaImageMagickFilter> result = new ArrayList<MediaImageMagickFilter>();

		for(final Type<?> type : model.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof MediaImageMagickFilter)
					result.add((MediaImageMagickFilter)feature);

		return result;
	}

	@Override
	String[] getHeadings()
	{
		return new String[]{"Type", "Name"};
	}

	@Override
	void writeValue(final Out out, final MediaImageMagickFilter testable, final int h)
	{
		switch(h)
		{
			case 0: out.write(testable.getType().getID()); break;
			case 1: out.write(testable.getName()); break;
			default:
				throw new RuntimeException(String.valueOf(h));
		};
	}

	@Override
	String getID(final MediaImageMagickFilter testable)
	{
		return testable.getID();
	}

	@Override
	MediaImageMagickFilter forID(final Model model, final String id)
	{
		return (MediaImageMagickFilter)model.getFeature(id);
	}

	@Override
	int check(final MediaImageMagickFilter testable)
	{
		try
		{
			testable.test();
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		return 0;
	}
}